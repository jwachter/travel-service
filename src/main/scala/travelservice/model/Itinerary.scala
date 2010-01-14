package travelservice.model

import _root_.java.util.Date

class Itinerary(val id:String, val departure:Date, val duration:Double, val origin:Airport, val destination:Airport, val segments:Seq[Segment], val price:Double) {
  def toXML = 
    <itinerary id={ id }>
      <origin>{ origin.airportCode }</origin>
      <destination>{ destination.airportCode }</destination>
      <departureDate>{ departure }</departureDate>
      <duration>{ duration }</duration>
      <price>{ price }</price>

      <segments>
        { for (segment <- segments) yield segment.toXML }
      </segments>
    </itinerary>
}