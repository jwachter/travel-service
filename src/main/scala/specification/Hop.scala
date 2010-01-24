package specification {

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
}