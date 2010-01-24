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
		
		<div class="result">
	    <table width="100%">
        <tr style="background-color:#eee" id={it.id}>
          	<td><strong>{it.origin.code.name} to { it.destination.code.name }</strong></td>
            <td><a href={link}>Book this Trip</a></td>
            <td>{it.departureDate}</td>
        </tr>
        <tr>
            <td>Departure: ({it.departureDate})</td>
            <td>Duration: {it.duration}</td>
            <td>Price: {it.price}</td>
        </tr>
        <tr>
            <td colspan="3">
                { for (segment <- it.segments) yield segmentToXHTML(segment) }
            </td>
        </tr>
        <tr>
        <td colspan="3">
        &nbsp;
        </td>
        </tr>
        </table>
        </div>
    }
 
	//
	// Transform a Segment into XHTML
	//
	def segmentToXHTML(s : Segment):NodeSeq={
        <div style="width:80%;border:1px solid #bbb">
            <div><strong>{ s.origin.code.name } to { s.destination.code.name }</strong> ({ s.departureDate })</div> 
            <table width="100%" style="border:1px dotted #ddd">
                { for (hop <- s.hops) yield hopToXHTML(hop) }
            </table>
        </div>
	}
	
	//
	// Transform a Hop into XHTML
	//
	def hopToXHTML(h:Hop):NodeSeq = {
	    <tr>
            <td>[{h.flightNumber}]</td>
            <td>{ h.origin.code.name }</td>
            <td>{ h.destination.code.name }</td>
            <td>({ h.departureDate }) in {h.duration}h</td>
        </tr>
	}
}
