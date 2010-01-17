package travelservice.helper

import airline.{Itinerary, Segment, Hop}
import _root_.scala.xml._

object ItineraryHelper {
	def itineraryToXHTML(it:Itinerary):NodeSeq = {
        <tr id={it.id}>
          	<td>
            <strong>{ it.origin.code.name }</strong>-<strong>{ it.destination.code.name }</strong>
            <p> Departure: ({it.departureDate}) - Duration: {it.duration}</p>
            <p>{it.price}</p>
            <ol>
                { for (segment <- it.segments) yield segmentToXHTML(segment) }
            </ol>
            </td>
        </tr>
    }
 
	def segmentToXHTML(s : Segment):NodeSeq={
        <li>
            <p><strong>{ s.origin.code.name }</strong> - 
            <strong>{ s.destination.code.name }</strong>
            <em>({ s.departureDate })</em>
            </p>
            <ol>
                { for (hop <- s.hops) yield hopToXHTML(hop) }
            </ol>
        </li>
	}
 
	def hopToXHTML(h:Hop):NodeSeq = {
	    <li>
            {h.flightNumber} from { h.origin.code.name } to { h.destination.code.name } @ ({ h.departureDate }) in {h.duration}h
        </li>
	}
}
