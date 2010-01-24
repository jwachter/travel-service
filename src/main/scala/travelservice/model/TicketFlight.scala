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

// Import needed Lift modules
import _root_.net.liftweb.mapper._

//
// Companion object for mapping purposes.
//
object TicketFlight extends TicketFlight with LongKeyedMetaMapper[TicketFlight]{
  override def dbTableName = "ref_ticket_flight"
}

//
// M:N Relation between Tickets and Flights
//
class TicketFlight extends LongKeyedMapper[TicketFlight] with IdPK{
	def getSingleton = TicketFlight
 
	//
	// To enable persistence of Itinerary: This is the position of this Flight inside the Itinerary and the specific Segment
    //
	object positionNumber extends MappedInt(this)
 
    //
	// The Flight model instance associated with the Ticket
    //
	object _flight extends MappedLongForeignKey(this, Flight){
	  override def dbColumnName = "ref_flight"
	}
 
    //
	// When called the actual Flight is fetched
    //
	lazy val flight = Flight.find(By(Flight.id, _flight.is))
 
	//
	// The Ticket model instance that is associated with the Flight
    //
	object _ticket extends MappedLongForeignKey(this, Ticket){
	  override def dbColumnName = "ref_ticket"
	}
 
    //
	// When called the actual Ticket is fetched
    //
	lazy val ticket = Ticket.find(By(Ticket.id, _ticket.is))
}