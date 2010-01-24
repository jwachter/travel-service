package lufthansa {

    import _root_.java.util.Date
    import _root_.net.liftweb.common._
    import _root_.net.liftweb.mapper._
    import _root_.net.liftweb.json.JsonAST._
    import _root_.org.joda.time._
    import travelservice.model._
    import travelservice.helper.IdHelper

    // Implement our version of the Airline spec
    class Lufthansa extends specification.Airline {
      
    	private def generateUniqueId(segs:Seq[specification.Segment]):String={
            val combinedFlightNumbers = (for(seg <- segs) yield (for(hop <- seg.hops) yield hop.flightNumber).mkString("")).mkString("")
            val combinedFlightDates = (for(seg <- segs) yield (for(hop <- seg.hops) yield hop.departureDate).mkString("")).mkString("")
            
            IdHelper.md5(combinedFlightNumbers+combinedFlightDates)
    	}
    	
    	// transfrom our search result to the interface types 
        private def toSingleSegmentItineraries (flightSeqs : List[List[Flight]], origin: specification.Airport, destination: specification.Airport) : List[specification.Itinerary] = {
            val temp = flightSeqs.map ( flightseq => {

                    // create List of hops
                    val hops = for (flight <- flightseq) yield flight.toHop

                    // calc sum of durations
                    val singleDurations : List [Int] = flightseq.map (flight => flight.duration)
                    val sumOfDurations = singleDurations.foldLeft(0)((a,b) => a+b)

                    // create segment for itinerary
                    val seg = new specification.Segment (origin, destination, hops.head.departureDate, sumOfDurations, hops)
                    
                    val uid = generateUniqueId(List(seg))

                    new specification.Itinerary (uid, seg.departureDate, seg.duration, origin, destination, seg :: Nil, 100)
                }
            )
            // sort by duration time
            temp.sort((e1,e2) => e1.duration < e2.duration)
        }

        override def searchOneway (origin: specification.Place, destination: specification.Place, date:Date) : Seq[specification.Itinerary] = {

            def getAirports (place: specification.Place) = place match {
                case a : specification.Airport => List(a)
                case c : specification.City =>
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
        def searchOneway_(origin: specification.Airport, destination: specification.Airport, date: Date): Seq[specification.Itinerary] = {

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
        // TODO implement using searchOneway
        override def searchRoundtrip(origin: specification.Place, destination: specification.Place, departureDate: Date, returnDate: Date): Seq[specification.Itinerary] = {
            
            // this is just a special case of searchMultisegment
            searchMultisegment (List((origin, destination, departureDate), (destination, origin, returnDate)))
        }

        private def combineItinerariesIntoOne (itineraries : List[specification.Itinerary]) : specification.Itinerary = {

            assert (itineraries != Nil)

            val allSegments = itineraries.flatMap( x => x.segments)
            val departureDate = allSegments.head.departureDate

            val origin = allSegments.head.origin
            val destination = allSegments.last.destination

            val allDurations : List[Int] = allSegments.map (seg => seg.duration)
            val sumOfSegmentDurations = allDurations.foldLeft (0)((a,b) => a+b)
            
            val uid = generateUniqueId(allSegments)
            
            new specification.Itinerary (uid, departureDate, sumOfSegmentDurations, origin, destination, allSegments, 100)
        }

        // Get all Itineraries that let you travel between a sequence of airport pairs on specific dates
        // DISCLAIMER: using searchOneway for this implementation might not be the best speed we can achieve
        // still the most time consuming thing will be the combinatorial explosion if there are lots of itineraries found
        // for the single trips
        //TODO: Check if the single destination/departure airport map between the segments
        override def searchMultisegment(trips: Seq[(specification.Place, specification.Place, Date)]): Seq[specification.Itinerary] = {
          
            def combineItineraries (flights : List[List[specification.Itinerary]]) : List[List[specification.Itinerary]] = flights match {

                case hd :: tl => {
                        val lstTail = combineItineraries (tl)

                        hd flatMap (head => lstTail map (tail => head :: tail))

                    }
                case Nil => List(Nil)
            }

            val allFlights : Seq[Seq[specification.Itinerary]] = trips map (trip => searchOneway (trip._1, trip._2, trip._3))
            
            
            val allFlightsCombined = combineItineraries (allFlights.asInstanceOf[List[List[specification.Itinerary]]])
            
            allFlightsCombined.map (it => combineItinerariesIntoOne (it))
        }

         override def book(itinerary: specification.Itinerary, travelers: Seq[specification.Traveler]): (specification.Ticket, specification.EuroPay) = {
	    // pre-condition
	    /*assert( itinerary is still available )    // don't worry about this if your implementation of airline does not check availablity
	    assert( travelers.length > 0 )            // require at least one traveler
	
	    // internal processing to generate a ticket
	    //val ticket  = ...
	
	    // Lufthansa always use EuroPay to collect payments
	    //val euroPay = ...
	
	    // post-condition
	    assert( ticket.itinerary == itinerary )
	    assert( ticket.travelers == travelers )
	*/
    (null, null)
  }
        
    }
}