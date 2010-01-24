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

//
// Companion object for mapping purposes.
//
object Ticket extends Ticket with LongKeyedMetaMapper[Ticket]{
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
    // Holds a unique identifier for this Ticket based upon the itinerary and the Travelers.
    //
	object uid extends MappedString(this, 255)
 
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
	  val tfs = TicketFlight.findAll(By(TicketFlight._ticket, this), OrderBy(TicketFlight.segmentNumber, Ascending), OrderBy(TicketFlight.positionNumber, Ascending))
	  
	  println(tfs)
	  
	  <ticket>test</ticket>
    }
}