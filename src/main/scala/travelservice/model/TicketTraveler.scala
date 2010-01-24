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
object TicketTraveler extends TicketTraveler with LongKeyedMetaMapper[TicketTraveler]{
  override def dbTableName = "ref_ticket_traveler"
}

//
// M:N Relation between Tickets and Travelers.
//
class TicketTraveler extends LongKeyedMapper[TicketTraveler] with IdPK{
  def getSingleton = TicketTraveler
  
  //
  // The reference to a Ticket.
  //
  object _ticket extends MappedLongForeignKey(this, Ticket){
    override def dbColumnName = "ref_ticket"
  }
  
  //
  // The referenced Ticket.
  //
  lazy val ticket = Ticket.find(By(Ticket.id, _ticket.is))
  
  //
  // The reference to a Traveler.
  //
  object _traveler extends MappedLongForeignKey(this, Traveler){
    override def dbColumnName = "ref_traveler"
  }
  
  //
  // The referenced Traveler.
  //
  lazy val traveler = Traveler.find(By(Traveler.id, _traveler.is))
  
}
