package airline {

    import world.{Airport, City, Place}
    import java.util.Date

    class Hop(val flightNumber : String, val origin : Airport, val destination : Airport, val departureDate : Date, val duration : Int) {
        /*
         A hop is a direct jump (no intermediate stops) from an origin airport to a destination airport and a passenger
         is required to get off an airjet at the destination before a connection or finish the complete trip.

         e.g. a direct flight from Frankfurt to Toronto
         */

        //val flightNumber: String  // e.g. LH46, AC99
        //val origin: Airport
        //val destination: Airport
        //val departureDate: Date   // time of departure of the flight. When output, use YYYY-MM-DD HH:MM format
        // e.g. 2010-01-08 15:35
        //val duration: Int         // duration of flight in minutes

        def toXML = // provided for use in your RESTful API
        <hop>
            <flightNumber>{ flightNumber }</flightNumber>
            <origin>{ origin.code.name }</origin>
            <destination>{ destination.code.name }</destination>
            <departureDate>{ departureDate }</departureDate>
            <duration>{ duration }</duration>
        </hop>
    }

    class Segment(val origin : Airport, val destination : Airport, val departureDate : Date, val duration : Int, val hops : Seq[Hop]) {
        /*
         A segment is a direct or a connect flight from an origin airport to a destination airport. It can contain one or more
         hops to make the complete journal. A passenger is expected to finish all hops in the segment continously without leaving
         intermeidate airports (although they can if transfer time permits and the passenger has valid visa to enter the country).

         e.g. a flight from Frankfurt to St. John's, with connect at Toronto.
         */
        //val origin: Airport // ultimate origin (e.g. Frankfurt)
        //val destination: Airport  // ultimate destination (e.g. St. John's, not Toronto)
        //val departureDate: Date   // departure date of the first hop, e.g. 2010-02-07 14:35
        //val duration: Int   // time interval in minutes; inlcuding waiting time at trasfer airports
        //val hops: Seq[Hop]  // the detailed hops, e.g. first hop: Frankfurt -> Toronto; second hop: Toronto -> St. John's.

        def toXML =
        <segment>
            <origin>{ origin.code.name }</origin>
            <destination>{ destination.code.name }</destination>
            <departureDate>{ departureDate }</departureDate>
            <hops>
                { for (hop <- hops) yield hop.toXML }
            </hops>
        </segment>
    }

    class Itinerary(val id: String, val departureDate: Date, val duration: Int, val origin: Airport, val destination: Airport, val segments: Seq[Segment], val price: Int) {
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
         */
        //val id: String    // globally unique identifier for a particular intinerary; useful for later booking
        //val departureDate: Date // departureDate of the first hop
        //val duration: Int // time interval in minutes; = sum of duration for all segments
        //val origin: Airport       // origin airport
        //val destination: Airport  // ultimate destination airport
        //val segments: Seq[Segment]
        //val price: Int    // amount of ticket fare + tax (e.g. dollars)

        override def toString = "Itinerary [" + id + "] from " + origin + " to " + destination + " departure on: " + departureDate + " segments: " + segments.length + " duration: " + duration

        def toXML =
        <itinerary id={ id }>
            <origin>{ origin.code.name }</origin>
            <destination>{ destination.code.name }</destination>
            <departureDate>{ departureDate }</departureDate>
            <duration>{ duration }</duration>
            <price>{ price }</price>

            <segments>
                { for (segment <- segments) yield segment.toXML }
            </segments>
        </itinerary>
    }


    trait Airline {
        def searchOneway(origin: Place, destination: Place, date: Date): Seq[Itinerary] = {
            /*
             // pre-condition
             val now = new Date
             assert now before date   // date must be in the future

             // searching ...
             //itineraries = ...

             // post-condition
             for (itinerary <- itineraries) {
             assert( itinerary.origin == origin )
             assert( itinerary.destination == destination )
             assert( itinerary.departureDate within date +/- 3days )
             assert( itinerary.duration < 48hours )
             assert( itinerary.segments(0).hops.length < 5 ) // less than 5 connections
             }*/
            Nil
        }


        def searchRoundtrip(origin: Place, destination:Place, departureDate: Date, returnDate: Date): Seq[Itinerary] = {
            /*
             // pre-condition
             now = new Date
             assert( now before departureDate )        // date in the future
             assert( departureDate before returnDate)

             // searching ...
             itineraries = ...

             // post-condition
             for (itinerary <- itineraries) {
             assert( itinerary.segments.length == 2 )
             // Hops and segments are different things. See definition of Itinerary for detail.

             departureSegment  = itinerary.segments(0)
             assert( departureSegment.origin == origin )
             assert( departureSegment.destination == destination )
             assert( departureSegment.departureDate within departureDate +/- 3days)

             returnSegment     = itinerary.segments(1)
             assert( returnSegment.origin == destination )
             assert( returnSegment.destination == origin )
             assert( returnSegment.departureDate within returnDate +/- 3days)

             assert( departureSegment.departurDate before returnSegment.departureDate )
             assert( returnDate - departureDate < 1year )  // allow one year of stay
             }*/
            Nil
        }


        def searchMultisegment(trips: Seq[(Place,Place, Date)]): Seq[Itinerary] = {
            /*
             // pre-condition
             assert( trips.length > 1 ) // otherwise use searchOneway instead

             var lastDate  = new Date  // now
             var (_, lastDestination, _) = trips(0)

             for ( (origin, destination, date) <- trips ) {
             assert( lastDestination == origin )       // fly from the same airport since last landing
             assert( date after lastDate )             // must be able to connect in time
             assert( date before lastDate + 7days )    // allow one week of stay

             lastDate = date
             lastDestination = destination
             }

             // searching
             itineraries = ...

             // post-condition
             assert( itinerary.segments.length == trips.length )

             for ((segment, trip) <- itinerary.segments.toList zip trips.toList) {
             val (origin, destination, date) = trip
             assert( segment.origin == origin )
             assert( segment.destination == destination )
             assert( segment.departureDate within date +/- 24hours )
             }
             */
            Nil
        }


        //def book(itinerary: Itinerary, travelers: Seq[Traveler], creditCard: CreditCard)
        // TODO: need more information about Credit Card processing services. Think about it later.
    }

}