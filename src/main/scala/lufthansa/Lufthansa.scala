package lufthansa {

    import airline._
    import _root_.java.util.Date
    import _root_.net.liftweb.common._
    import _root_.net.liftweb.mapper._
    import _root_.net.liftweb.json.JsonAST._
    import _root_.org.joda.time._
    import travelservice.model._

    import scala.actors.Futures._
    import scala.actors.Actor._
    import scala.actors._

    // Implement our version of the Airline spec
    class Lufthansa extends Airline {

        abstract trait Mapper {
            def map (trips: Seq[(world.Place, world.Place, Date)]) : Seq[Seq[Itinerary]]
        }

        abstract trait Combiner {
            def combine (its: List[List[Itinerary]]) : List[Itinerary]
        }

        object SequentialMapper extends Mapper {
            def map (trips: Seq[(world.Place, world.Place, Date)]) = trips map (trip => searchOneway (trip._1, trip._2, trip._3))
        }

        object SequentialCombiner extends Combiner {
            def combine (its: List[List[Itinerary]]) : List[Itinerary] = its.map (it => combineItinerariesIntoOne (it))
        }

        // The ParallelMapper object will distribute each request for a trip into an individual EventThread based on scalas Actors
        // The Response of the SearchTrip message will be a list with all the itineraries the searchOneeway method found
        object ParallelMapper extends Mapper {
            case class SearchTrip (val trip : (world.Place, world.Place, Date))
            case class SearchTripResponse (val flights:Seq[Itinerary])

            // first map all single trips in a MultiSegment search into several actors awaiting a Future response containing SearchTripResponse
            // Then map a second time to the flights
            def map (trips: Seq[(world.Place, world.Place, Date)]) = trips.map ( singleTrip => {
                    // create anonym actor to process SearchTrip in parallel
                    actor {
                        react {
                            case SearchTrip(trip) => reply (SearchTripResponse(searchOneway (trip._1, trip._2, trip._3)));
                        }
                    } !! SearchTrip (singleTrip)

                }).map (future => {
                    val ft = future ().asInstanceOf[SearchTripResponse]
                    ft.flights
                }
            )
        }

        // The ParallelCombiner object uses scalas Actors to distribute the combination of itineraries
        // among multiple EventThreads. After the distribution the it will wait for the Futures to finish
        // and combine the results of the CombineResponses
        object ParallelCombiner extends Combiner {
            case class Combine (val itineraries:List[List[Itinerary]])
            case class CombineResponse (val it:List[Itinerary])
            class ParallelChunk (val lst: List[List[Itinerary]])

            def combine (its: List[List[Itinerary]]) : List[Itinerary] = {

                def sliceList (lst : List[List[Itinerary]], sliceSize : Int) : List[ParallelChunk] = lst match {
                    case Nil => Nil
                    case _ => {
                            val (slice, rest) = lst.splitAt (sliceSize)
                            new ParallelChunk (slice) :: sliceList (rest, sliceSize)
                        }
                }

                // divide the list in four equal parts (not too much overhead when distributing)
                val sliceSize = (its.length / 4) + 1
                val slicedList = sliceList (its, sliceSize)

                // collect the futures in a list
                val lstFutures : List[Future[Any]]= slicedList.map ( x =>
                    actor {
                        react {
                            case Combine (combine) => reply (CombineResponse(combine.map (it => combineItinerariesIntoOne (it))));
                        }
                    } !! Combine (x.lst)
                )
                // wait for all futures to finish processing and flatMap the resulting lists
                lstFutures.flatMap (future => {
                        val ft = future ().asInstanceOf[CombineResponse]
                        ft.it
                    }
                )
            }
        }

        object StrategyDecision {
            def getMapper (tripCount: Int) = if (tripCount < 2) SequentialMapper else ParallelMapper

            def getCombiner (count:Int) = if (count < 2000000) SequentialCombiner else ParallelCombiner
        }
    	
    	// transfrom our search result to the interface types 
        def toSingleSegmentItineraries (flightSeqs : List[List[Flight]], origin: world.Airport, destination: world.Airport) : List[Itinerary] = {
            val temp = flightSeqs.map ( flightseq => {

                    // create List of hops
                    val hops = for (flight <- flightseq) yield flight.toHop

                    // calc sum of durations
                    val singleDurations : List [Int] = flightseq.map (flight => flight.duration)
                    val sumOfDurations = singleDurations.foldLeft(0)((a,b) => a+b)

                    // create segment for itinerary
                    val seg = new Segment (origin, destination, hops.head.departureDate, sumOfDurations, hops)

                    new Itinerary ("newIt", seg.departureDate, seg.duration, origin, destination, seg :: Nil, 100)
                }
            )
            // sort by duration time
            temp.sort((e1,e2) => e1.duration < e2.duration)
        }

        override def searchOneway (origin: world.Place, destination: world.Place, date:Date) : Seq[Itinerary] = {

            def getAirports (place: world.Place) = place match {
                case a : world.Airport => List(a)
                case c : world.City =>
                    val city = City.findByName(c.name)
                    for (ap <- Airport.findByCity(city.open_!)) yield ap.toAirport
            }

            // convert origin city or airport into list of possible origin airports
            val origins = getAirports (origin)

            // convert destination city or airport into list of possible destination airports
            val destinations = getAirports (destination)

            // combine all origin and destination airports with the departure date into a list of routes to check
            val routesToCheck = origins.flatMap(
                x => destinations.map (
                    y => (x, y, date)
                )
            )

            // combine all searchOneWay_ results for each (origin, destination, date) tripe
            routesToCheck flatMap ( route => searchOneway_ (route._1, route._2, route._3))
        }

        // Search one way flights between two different Airports on a specific date.
        def searchOneway_(origin: world.Airport, destination: world.Airport, date: Date): Seq[Itinerary] = {

            def getFlightsTo (flights : List[Flight], visited: List[Airport], destination: Airport, maxHops: Int, maxReachDestinationDate : Date) : List[List[Flight]] = {
            	// Get all flights that are possible after having taken the given flight en route to the destination whereas some Airports
            	// are already visited and shouldn't be considered anymore.
                def getFlightsStartingWith (flight: Flight, visited: List[Airport], destination: Airport) : List[List[Flight]] = {
                    // Check the reached destination by the current flight
                    flight.destination match {

                        // reached target airport, return the flights
                        case dest if (dest == destination) => List(flight) :: Nil

                            // try recursive find
                        case _ if (visited.contains (flight.destination) == false) => {

                                // match for max hops
                                visited.length match {
                                    // Are we inside our range?
                                    case hops if (hops - 1 < maxHops) => {

                                            // search only for flights after landing ... in future we should also support some transit duration
                                            val dep : Date = flight.departure
                                            val newStartDate = new DateTime(dep).plusHours (flight.duration)

                                            // Find all flights departing on the right time window
                                            val flightsNew = Flight.findInRangeAndOrigin( newStartDate.toDate, maxReachDestinationDate, flight.destination)

                                            // Find all connections to our target. Add the current airport to visited airports to avoid circles. Add current flight in front of the rest of the journey generated
                                            getFlightsTo (flightsNew, flight.destination :: visited, destination, maxHops, maxReachDestinationDate) map ( flights => flight :: flights )
                                        }

                                        // did not reach destination Airport with given hops
                                    case _ => Nil
                                }
                            }
                            // We didn't find a satisfying flight
                        case _ => Nil
                    }
                }

                // find list of flights to the destination airport (segments) for all flights starting at the origin airport
                flights match {
                    case hd :: tl => getFlightsStartingWith (hd, visited, destination) ::: getFlightsTo (tl, visited, destination, maxHops, maxReachDestinationDate)
                    case _ => Nil
                }
            }
            
            // Load from input interface classes and look if we have the Airports
            val _origin : Airport = Airport.findByCode (origin.code.name).openOr(null)
            val _destination : Airport = Airport.findByCode (destination.code.name).openOr (null)

            assert (_origin != null)
            assert (_destination != null)

            // include up to startday + 2 days in results
            val departureDayRange = 2
            // max airport hops
            val maxHops = 5

            // all flights starting at origin during the specified timerange
            val flights : List [Flight] = Flight.findByDaysAndOrigin(date, departureDayRange, _origin)

            // get all flights to the destination airport
            val possibleFlightLines : List[List[Flight]] = getFlightsTo (flights, List(_origin), _destination, maxHops, new DateTime (date).plusDays (departureDayRange).toDate)

            // return as itineraries
            toSingleSegmentItineraries (possibleFlightLines, origin, destination)
        }

        // Get all Itineraries that let you travel from one Airport to another and back again on two specific dates
        override def searchRoundtrip(origin: world.Place, destination: world.Place, departureDate: Date, returnDate: Date): Seq[Itinerary] = {
            
            // this is just a special case of searchMultisegment
            searchMultisegment (List((origin, destination, departureDate), (destination, origin, returnDate)))
        }

        def combineItinerariesIntoOne (itineraries : List[Itinerary]) : Itinerary = {

            assert (itineraries != Nil)

            val allSegments = itineraries.flatMap( x => x.segments)
            val departureDate = allSegments.head.departureDate

            val origin = allSegments.head.origin
            val destination = allSegments.last.destination

            val allDurations : List[Int] = allSegments.map (seg => seg.duration)
            val sumOfSegmentDurations = allDurations.foldLeft (0)((a,b) => a+b)

            new Itinerary ("newIt", departureDate, sumOfSegmentDurations, origin, destination, allSegments, 100)
        }


        // Get all Itineraries that let you travel between a sequence of airport pairs on specific dates       
        override def searchMultisegment(trips: Seq[(world.Place, world.Place, Date)]): Seq[Itinerary] = {

            def combineItineraries (flights : List[List[Itinerary]]) : List[List[Itinerary]] = flights match {
                case hd :: tl => {
                        val lstTail = combineItineraries (tl)

                        hd flatMap (head => lstTail map (tail => head :: tail))

                    }
                case Nil => List(Nil)
            }

            val allFlights = StrategyDecision.getMapper(trips.length).map(trips)
            val allFlightsCombined = combineItineraries (allFlights.asInstanceOf[List[List[Itinerary]]])

            StrategyDecision.getCombiner(allFlightsCombined.length).combine(allFlightsCombined).take(50)
        }

    }
}
