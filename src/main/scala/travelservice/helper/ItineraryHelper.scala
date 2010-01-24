/**
 * Copyright 2010 Johannes Wachter, Marcus Körner, Johannes Potschies, Jeffrey Groneberg, Sergej Jakimcuk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package travelservice.helper

// Import specification.
import specification.{Itinerary, Segment, Hop}

// Import needed scala classes.
import _root_.scala.xml._

//
// Helper that implements presentation layer related methods for working with
// Itineraries and there content.
//
object ItineraryHelper {
	
	//
	// True if it contains one segment that has only one hop.
    //
	def isDirectFlight(it:Itinerary) = false
	
	//
	// True if it contains inbound and outbound as two segments.
    //
	def isRoundtrip(it:Itinerary) = false
 
	//
	// True if it contains more than one segment
	//
	def isMultiSegment(it:Itinerary) = false
 
	//
	// True if it contains only one Segment
	//
	def isSingleSegment(it:Itinerary) = false
	
	//
	// Transform an Itinerary to the default XHTML representation defined for our application.
	//
	def itineraryToXHTML(it:Itinerary):NodeSeq = {
		val link = "/book/booking.html?id="+it.id	  
	  
        <tr id={it.id}>
          	<td>
            <strong>{ it.origin.code.name }</strong>-<strong>{ it.destination.code.name }</strong> <a href={link}>Book</a>
            <p> Departure: ({it.departureDate}) - Duration: {it.duration}</p>
            <p>{it.price}</p>
            <ol>
                { for (segment <- it.segments) yield segmentToXHTML(segment) }
            </ol>
            </td>
        </tr>
    }
 
	//
	// Transform a Segment into XHTML
	//
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
	
	//
	// Transform a Hop into XHTML
	//
	def hopToXHTML(h:Hop):NodeSeq = {
	    <li>
            {h.flightNumber} from { h.origin.code.name } to { h.destination.code.name } @ ({ h.departureDate }) in {h.duration}h
        </li>
	}
}
