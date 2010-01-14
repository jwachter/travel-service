package travelservice.model

import java.util.Date

class Segment(val origin:Airport, val destination:Airport, val departure:Date, val duration:Double, val flights:Seq[Flight]) {
  def toXML = 
    <segment>
      <origin>{ origin.airportCode }</origin>
      <destination>{ destination.airportCode }</destination>
      <departureDate>{ departure}</departureDate>
      <hops>
        { for (flight <- flights) yield flight.toXML }
      </hops>
    </segment>
}