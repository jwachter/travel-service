import airline._

trait Lufthansa extends Airline {
  def searchOneway(origin: Place, destination: Place, date: Date): Seq[Itinerary] { // relax input type
    // pre-condition
    now = new Date
    assert( now before date )   // date must be in the future

    // searching ...
    itineraries = ...

    // post-condition
    for (itinerary <- itineraries) {
      assert( itinerary.origin == origin )
      assert( itinerary.destination == destination )
      assert( itinerary.departureDate within date +/- 3days )
      assert( itinerary.duration < 48hours )
      assert( itinerary.segments(0).hops.length < 5 ) // less than 5 connections
      // Hops and segments are different things. See definition of Itinerary for detail. 
    }
  }

  def searchRoundtrip(origin: Place, destination: Place, departureDate: Date, returnDate: Date): Seq[Itinerary] { // relax input type
    // pre-condition
    now = new Date
    assert( now before departureDate )        // date in the future
    assert( departureDate before returnDate )

    // searching ...
    itineraries = ...

    // post-condition
    for (itinerary <- itineraries) {
      assert( itinerary.segments.length == 2 )
      // Hops and segments are different things. See definition of Itinerary for detail. 

      departureSegment  = itinerary.segments(0)
      assert( departureSegement.origin == origin )
      assert( departureSegement.destination == destination )
      assert( departureSegement.departureDate within departureDate +/- 3days)

      returnSegment     = itinerary.segments(1)
      assert( returnSegement.origin == destination )
      assert( returnSegement.destination == origin )
      assert( returnSegement.departureDate within returnDate +/- 3days)
      
      assert( returnDate - departureDate < 3months) // restrict post-condition
    }
  }


  def searchMultisegment(trips: Seq[(Place, Place, Date)]): Seq[Itinerary] {  // relax input type
    // pre-condition
    assert( trips.length > 1 ) // otherwise use searchOneway instead

    var lastDate  = new Date  // now
    var (_, lastDestination, _) = trips(0)

    for ( (origin, destination, date) <-  trips) {
      assert( lastDestination == origin )       // fly from the same airport since last landing
      assert( date after lastDate )             // must be able to connect in time
      assert( date before lastDate + 30days)    // allow 30 days of stay    (relax pre-condition)

      lastDate = date
      lastDestination = destination
    }

    // searching
    itineraries = ...

    // post-condition
    assert( itinerary.segments.length == trips.length )
    var lastDate = new Date // now
    for ((segment, trip) <- itinerary.segments.toList zip trips.toList) {
      val (origin, destination, date) = trip
      assert( segment.origin == origin )
      assert( segment.destination == destination )
      assert( segment.departureDate within date +/- 12hours )   // restrict post-condition
      assert( segment.departureDate after lastDate )    // must be able to connect in time
      lastDate == segment.departureDate
    }
  }


  def book(itinerary: Itinerary, travelers: Seq[Traveler]): (Ticket, EuroPay) = {
    // pre-condition
    assert( itinerary is still available )    // don't worry about this if your implementation of airline does not check availablity
    assert( travelers.length > 0 )            // require at least one traveler

    // internal processing to generate a ticket
    val ticket  = ...

    // Lufthansa always use EuroPay to collect payments
    val euroPay = ...

    // post-condition
    assert( ticket.itinerary == itinerary )
    assert( ticket.travelers == travelers )

    (ticket, euroPay)
  }
}
