package lufthansa {

    import airline._
    import _root_.java.util.Date
    import _root_.net.liftweb.common._
    import _root_.net.liftweb.mapper._
    import _root_.net.liftweb.json.JsonAST._
    import _root_.org.joda.time._
    import travelservice.model._

    // Implement our version of the Airline spec
    class Lufthansa extends Airline {
    	
    	// transfrom our search result to the interface types 
        private def toSingleSegmentItineraries (flightSeqs : List[List[Flight]], origin: world.Airport, destination: world.Airport) : List[Itinerary] = {
            flightSeqs.map ( flightseq => {

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
        }

        // Search one way flights between two different Airports on a specific date.
        override def searchOneway(origin: world.Airport, destination:world.Airport, date: Date): Seq[Itinerary] = {

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
                                            val flightsNew : List [Flight] = Flight.findInRangeAndOrigin( newStartDate.toDate, maxReachDestinationDate, flight.destination)

                                            // Find all connections to our target. Add the current airport to visited airports to avvoid circles. Add current flight in front of the rest of the journey generated
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
                    // Walk through the algorithm
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

            // list of visited airports
            val visited = _origin :: Nil

            // get all flights to the destination airport
            val possibleFlightLines : List[List[Flight]] = getFlightsTo (flights, visited, _destination, maxHops, new DateTime (date).plusDays (departureDayRange).toDate)

            // return as itineraries
            toSingleSegmentItineraries (possibleFlightLines, origin, destination)
        }

        // Get all Itineraries that let you travel from one Airport to another and back again on two specific dates
        // TODO implement using searchOneway
        override def searchRoundtrip(origin: world.Airport, destination: world.Airport, departureDate: Date, returnDate: Date): Seq[Itinerary] = {
            
//            // find Itineraries in both directions
//            val to = searchOneway(origin, destination, departureDate)
//            val back = searchOneway(destination, origin, returnDate)
//
//            // Combine to and back Itineraries
//            for(p1 <- to){
//
//            }
      
            // this is just a special case of searchMultisegment
            searchMultisegment (List((origin, destination, departureDate), (destination, origin, returnDate)))


            //Nil
        }

        /*




         An itinerary is a series of segments of flights. It is the final entity passengers get and pay for.

         For a one-way direct flight, it contains a single segment, which contains a single hop.
         For a one-way connect flight, it  contains a single segment, which contains multiple hops.
         For a round-trip flight, it contains two segments.
         For a multisegment flight, it contains at least two segments.

         For example, a multisegment itinerary would be:
         Segment 1: from Chengdu via Beijing, Amsterdam to Frankfurt, then stay half a year
         Hop 1.1: Chengdu -> Beijing
         Hop 1.2: Beijing -> Amsterdam
         Hop 1.3: Amsterdam -> Frankfurt

         Segment 2: from Frankfurt via Toronto to St. John's, then stay half a year
         Hop 2.1: Frankfurt -> Toronto
         Hop 2.2: Toronto -> St. John's

         Segment 3: from St. John's via Toronto, Vancouver, Beijing to Chengdu
         Hop 3.1: St. John's -> Toronto
         Hop 3.2: Toronto -> Vancouver
         Hop 3.3: Vancouver -> Beijing
         Hop 3.4: Beijing -> Chengdu

         //val id: String    // globally unique identifier for a particular intinerary; useful for later booking
         //val departureDate: Date // departureDate of the first hop
         //val duration: Int // time interval in minutes; = sum of duration for all segments
         //val origin: Airport       // origin airport
         //val destination: Airport  // ultimate destination airport
         //val segments: Seq[Segment]
         //val price: Int    // amount of ticket fare + tax (e.g. dollars)



         val hops = for (flight <- flightseq) yield flight.toHop

         // calc sum of durations
         val singleDurations : List [Int] = flightseq.map (flight => flight.duration)
         val sumOfDurations = singleDurations.foldLeft(0)((a,b) => a+b)

         // create segment for itinerary
         val seg = new Segment (origin, destination, hops.head.departureDate, sumOfDurations, hops)

         new Itinerary ("newIt", seg.departureDate, seg.duration, origin, destination, seg :: Nil, 100)

         */

         private def combineItinerariesIntoOne (itineraries : List[Itinerary]) : Itinerary = {

                def collectSegments (lst:List[Itinerary]) : List[Segment] = lst match {
                    case Nil => Nil
                    case hd :: tl => hd.segments.toList ::: collectSegments (tl)
                }

                val allSegments : List[Segment] = collectSegments (itineraries)
                val departureDate = allSegments.head.departureDate

                val origin = allSegments.head.origin
                val destination = allSegments.last.destination

                val allDurations : List[Int] = allSegments.map (seg => seg.duration)
                val sumOfSegmentDurations = allDurations.foldLeft (0)((a,b) => a+b)

                new Itinerary ("newIt", departureDate, sumOfSegmentDurations, origin, destination, allSegments, 100)
            }

         // Get all Itineraries that let you travel between a sequence of airport pairs on specific dates
         // DISCLAIMER: using searchOneway for this implementation might not be the best speed we can achieve
         // still the most time consuming thing will the combinatorial explosion if there are lots of itineraries found
         // for the single trips
         override def searchMultisegment(trips: Seq[(world.Airport, world.Airport, Date)]): Seq[Itinerary] = {

                def combineItineraries (flights : List[List[Itinerary]]) : List [List[Any]] = flights map (
                    its => flights match {
                        case Nil => Nil
                        case hd :: tl => its flatMap ( it => List(it) ::: combineItineraries (tl))
                    }
                )



                val allFlights : Seq[Seq[Itinerary]] = trips map (trip => searchOneway (trip._1, trip._2, trip._3))
                val allFlightsList = (allFlights map (s1 => s1.toList)).toList

                val allFlightsCombined : List[List[Itinerary]] = combineItineraries (allFlightsList).asInstanceOf[List[List[Itinerary]]]

                allFlightsCombined.map (it => combineItinerariesIntoOne (it))
            }

         }
         }