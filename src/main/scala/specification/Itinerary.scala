package specification

import _root_.java.util.Date

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