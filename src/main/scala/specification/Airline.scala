package specification { 

import _root_.java.util.Date

trait Airline {
    def searchOneway(origin: specification.Place, destination: specification.Place, date: Date): Seq[specification.Itinerary] = {
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


    def searchRoundtrip(origin: specification.Place, destination:specification.Place, departureDate: Date, returnDate: Date): Seq[specification.Itinerary] = {
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


    def searchMultisegment(trips: Seq[(specification.Place,specification.Place, Date)]): Seq[specification.Itinerary] = {
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

   // TODO investigate!!!
	  /*def book(itinerary: specification.Itinerary, travelers: Seq[specification.Traveler]): (specification.Ticket, specification.PaymentGateway) = {
	    // pre-condition
	    //assert( itinerary is still available )    // don't worry about this if your implementation of airline does not check availablity
	    //assert( travelers.length > 0 )            // require at least one traveler
	
	    // internal processing to generate a ticket
	    val ticket  = null
	
	    // choose a particular payment gateway
	    val paymentGateway  = null
	
	    // post-condition
	    //assert( ticket.itinerary == itinerary )
	    //assert( ticket.travelers == travelers )
	
	    (ticket, paymentGateway)
	  }*/
}

}