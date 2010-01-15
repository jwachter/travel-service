package lufthansa {

    import airline._
    import _root_.java.util.Date
    import _root_.net.liftweb.common._
    import _root_.net.liftweb.mapper._
    import _root_.net.liftweb.json.JsonAST._
    import _root_.org.joda.time._
    import travelservice.model._

    class Lufthansa extends Airline {

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

        //def searchOneway(origin: Place, destination: Place, date: Date): Seq[Itinerary]
        override def searchOneway(origin: world.Airport, destination:world.Airport, date: Date): Seq[Itinerary] = {

            def getFlightsTo (flights : List[Flight], visited: List[Airport], destination: Airport, maxHops: Int, maxReachDestinationDate : Date) : List[List[Flight]] = {

                def getFlightsStartingWith (flight: Flight, visited: List[Airport], destination: Airport) : List[List[Flight]] = {

                    flight.destination match {

                        // reached target airport
                        case dest if (dest == destination) => List(flight) :: Nil

                            // try recursive find
                        case _ if (visited.contains (flight.destination) == false) => {

                                // match for max hops
                                visited.length match {

                                    case hops if (hops - 1 < maxHops) => {

                                            // search only for flights after landing ... in future we should also support some transit duration
                                            val newStartDate = new DateTime(flight.departure).plusHours (flight.duration)

                                            val flightsNew : List [Flight] = Flight.findInRangeAndOrigin( newStartDate.toDate, maxReachDestinationDate, flight.destination)

                                            getFlightsTo (flightsNew, flight.destination :: visited, destination, maxHops, maxReachDestinationDate) map ( flights => flight :: flights )
                                        }

                                        // did not reach destination Airport with given hops
                                    case _ => Nil
                                }
                            }

                        case _ => Nil
                    }
                }

                // find list of flights to the destination airport (segments) for all flights starting at the origin airport
                flights match {
                    case hd :: tl => getFlightsStartingWith (hd, visited, destination) ::: getFlightsTo (tl, visited, destination, maxHops, maxReachDestinationDate)
                    case _ => Nil
                }
            }

            val _origin : Airport = Airport.findByCode (origin.code.toString).openOr(null)
            val _destination : Airport = Airport.findByCode (destination.code.toString).openOr (null)

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

        //def searchRoundtrip(origin: Place, destination: Place, departureDate: Date, returnDate: Date): Seq[Itinerary]
        override def searchRoundtrip(origin: world.Airport, destination: world.Airport, departureDate: Date, returnDate: Date): Seq[Itinerary] = {
            Nil
        }

        //override def searchMultisegment(trips: Seq[(Airport, Airport, Date)]): Seq[Itinerary]
        override def searchMultisegment(trips: Seq[(world.Airport, world.Airport, Date)]): Seq[Itinerary] = {

            /*
             // for multisegment itinerary
             def toItinerary (flightSeqs : List[List[Flight]]) : Itinerary = {

             var segments = List [Segment] ()

             for (flightseq <- flightSeqs){
             val hops = for (flight <- flightseq) yield flight.toHop
             // calculate sum of durations
             val singleDurations : List [Int] = flightseq.map (flight => flight.duration)
             val sumOfDurations = singleDurations.foldLeft(0)((a,b) => a+b)

             val seg = new Segment (origin, destination, hops.head.departureDate, sumOfDurations, hops)

             segments = seg :: segments
             }

             val segmentDurations : List [Int] = segments.map (seg => seg.duration)
             val segmentSumOfDurations = segmentDurations.foldLeft(0)((a,b) => a+b)

             new Itinerary ("newIt", segments.head.departureDate, segmentSumOfDurations, origin, destination, segments, 100)
             }
             */


            Nil
        }

    }
}