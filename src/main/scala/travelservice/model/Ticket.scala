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
package travelservice.model

// Import needed Lift modules.
import _root_.net.liftweb.mapper._

// Import application modules.
import travelservice.helper.DateTimeHelpers._

//
// Companion object for mapping purposes.
//
object Ticket extends Ticket with LongKeyedMetaMapper[Ticket]{
	override def dbIndexes = UniqueIndex(IndexField(Ticket.uid)) :: Nil
	override def dbTableName = "tickets"
}

//
// Describes a bookable entity for a Traveler or a group of Travelers. 
// Mirrors the Interface specification for Ticket but doesn't directly use it.
//
class Ticket extends LongKeyedMapper[Ticket] with IdPK{
	//
	// Define shortcut to the companion object.
	//
	def getSingleton = Ticket
 
    //
    // Holds a unique identifier for this Ticket based upon the itinerary and the Travelers + the current time.
    //
	object uid extends MappedString(this, 255){
	  override def dbIndexed_? = false
	}
	
	//
	// Holds a unique identifier for the itinerary this ticket is locked to.
	//
	object itid extends MappedString(this, 255)
 
	//
    // Holds the price of this Ticket.
    //
	object price extends MappedInt(this)
 
	//
	// Holds all Flights associated with this Ticket.
	//
	object flights extends HasManyThrough(this, Flight, TicketFlight, TicketFlight._ticket, TicketFlight._flight)

	//
	// Holds all the Travelers that fly with this Ticket.
	//
	object travelers extends HasManyThrough(this, Traveler, TicketTraveler, TicketTraveler._ticket, TicketTraveler._traveler)
 
	//
	// Payment status should be set via service by payment gateway.
	//
	object paymentStatus extends MappedBoolean(this)
 
	//
	// Transform to XML
	//
	def toXML = {
    	val tfs : List[TicketFlight] = TicketFlight.findAll(By(TicketFlight._ticket, this))
    	
    	// Get all segment numbers and sort them without duplicates
    	val segNums = tfs.map(_.segmentNumber.is).removeDuplicates.sort((e1,e2)=>e1 < e2)
    
    	// group all flights by there segment
    	val segments : List[List[TicketFlight]] = segNums.map(x => tfs.filter(_.segmentNumber.is == x))
     
    	// sort the flights themselves
    	val sorted = segments.map(l => l.sort((e1,e2)=>e1.positionNumber.is < e2.positionNumber.is))
     
    	// retreive the real Flight objects for each TicketFlight
    	val groupedFlights : List[List[Flight]] = sorted.map(l => l.map(tf => tf.flight.open_!))
     
    	val durationPre : List[Flight]= groupedFlights.flatten
     
    	val duration = durationPre.map(e => e.duration.is).foldLeft(0)(_+_)
     
    	<ticket id={uid.is}>
    		<itinerary id ={itid.is}> 
            <origin>{ groupedFlights.head.head.origin.code.is }</origin>
            <destination>{ groupedFlights.last.last.destination.code.is }</destination>
            <departureDate>{ groupedFlights.head.head.departure.is.toString }</departureDate>
            <price>{ price }</price>
            <duration>{ duration }</duration>
	    	<segments>
	    	{groupedFlights.map(e => <segment>{e.map(f => f.toXML)}</segment>)}
	    	</segments>
	    	</itinerary>
	    	<travelers>
	    		{travelers.get.map(t => t.toXML)}
	    	</travelers>
    	</ticket>
	}
}
