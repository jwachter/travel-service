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
package travelservice.webclient.snippet

// Import Lift modules.
import _root_.net.liftweb.util._
import _root_.net.liftweb.util.Helpers._
import _root_.net.liftweb.http._
import _root_.net.liftweb.mapper._
import _root_.net.liftweb.common._

// Import Scala classes.
import _root_.scala.xml.NodeSeq

// Import Joda Time
import _root_.org.joda.time._
import _root_.org.joda.time.format._

// Import JDK classes.
import _root_.java.util.Date

// Import application classes.
import travelservice.model._
import travelservice.session._
import travelservice.helper._


//
// This snippet class is used to handle and oversee the complete booking procedure. The different
// snippet methods are called from the according html templates.
//
class Book {
	//
	// Session sensitive holder for the Itinerary ID to book.
	//
	object IDHolder extends SessionVar[Box[String]](Empty)
 
	//
	// Session sensitive holder for the Itinerary to book.
	//
	object BookableItinerary extends SessionVar[Box[specification.Itinerary]](Empty)
 
	//
	// Session sensitive holder for the Traveler list to assign to the ticket.
	//
	object TravelerHolder extends SessionVar[List[Traveler]](Nil)

	//
	// Session sensitive holder for the Ticket to book.
	//
	object TicketHolder extends SessionVar[Ticket](null)
 
	//
	// Internal date formatting instance.
	//
	private val dateFormat = DateTimeFormat.forPattern("yyyy/MM/dd")
  
	//
	// Starting point of the booking process. Checks if an itinerary ID is set, and if not redirects to start.
	//
	// Checks if the Itinerary exists otherwise redirects.
	//
	def ticket(xhtml:NodeSeq):NodeSeq = {
	    // ID of the Itininerary to book (is in stored in the session)
	    val id = S.param("id")
     
	    // Check if id present
	    id match {
	      case Full(ident) if !ident.isEmpty => IDHolder.set(Full(ident));
	      case _ => S.error("You tried to book a flight without specifying the Itinerary ID.");S.redirectTo("/index.html")  
	    }
     
	    // Get the Itinerary from Cache
	    val container = ItineraryHolder.is
     
	    // Check if referenced object with id exists. Otherwise back to start. If it exists, continue with next step.
	    container match {
	      case Full(its) => {
	        val itList = its.filter(i => i.id == IDHolder.is.open_!)
	        if(itList.size == 1){
	          val it = itList.toList.head
	          BookableItinerary.set(Full(it))
	          S.redirectTo("/book/travelers.html")
	        } else {
	          S.error("You tried to book a ticket with not existing flights.");S.redirectTo("/index.html")
	        }
	      }
	      case _ => S.error("You tried to book a ticket with not existing flights.");S.redirectTo("/index.html")
	    }
	}
 
	//
	// Second step of booking: Define the Travelers that are booked onto the Ticket.
	// This step can repeat several times until alle Travelers are added and the User decides to proceed
	// to the next step.
	//
	def travelers(xhtml:NodeSeq):NodeSeq = {
	  // Only consider parameters if Request is a post request.
	  if(S.request.open_!.post_?){
		  // Retrieve Traveler attributes with certain default values and fallbacks.
		  // TODO proper check for some required fields.
		  val firstName = S.param("firstName") match {
		    case Full(n) if !n.isEmpty=> n
		    case _ => "" 
		  }
		  val middleName = S.param("middleName") match {
		  case Full(n) if !n.isEmpty => n
		  case _ => "" 
		  }
		  val lastName = S.param("lastName") match {
		  case Full(n) if !n.isEmpty => n
		  case _ => "" 
		  }
		  val gender = S.param("gender") match {
		  case Full(n) if !n.isEmpty => n
		  case _ => "male" 
		  }
		  val birthday = S.param("birthday") match {
		  case Full(n) if !n.isEmpty => try { dateFormat.parseDateTime(n).toDate } catch { case e:Exception => new DateMidnight(1980,1,1).toDate }
		  case _ => new DateMidnight(1980,1,1).toDate 
		  }
		  val travelDocType = S.param("travelDocType") match {
		  case Full(n) if !n.isEmpty => n
		  case _ => "none" 
		  }	  
		  val travelDocNumber = S.param("travelDocNumber") match {
		  case Full(n) if !n.isEmpty => n
		  case _ => "none" 
		  }	  
		  val phone = S.param("phone") match {
		  case Full(n) if !n.isEmpty => n
		  case _ => "none" 
		  }	  
		  val email = S.param("email") match {
		  case Full(n) if !n.isEmpty => n
		  case _ => "none" 
		  }
	   
		  // Construct the Traveler instance.
		  val traveler = Traveler.create.firstName(firstName).middleName(middleName).lastName(lastName).gender(gender).birthday(birthday).travelDocType(travelDocType).travelDocNumber(travelDocNumber).phone(phone).email(email)
		  traveler.save
	   
		  // Add the Traveler to the List of participants.
		  TravelerHolder.set(traveler :: TravelerHolder.is)
	  }
	  
	  // Return a simple list of the current participants.
	  <tr>{TravelerHolder.is.map(t => t.toXHTMLListItem)}</tr>
	}
	
	//
	// Final step on airline side. When the User proceeds from the travellers step to this, he can than pay through EuroPay.
	//
	def checkout(xhtml:NodeSeq):NodeSeq = {
		// Check if we have an Itinerary to book and at least 1 Traveler. Else back to adding travelers step.
	 	if(TravelerHolder.is.size > 0 && !BookableItinerary.is.isEmpty){
	 	  // create the Ticket
	 	  val ticket = createTicket()
     
	 	  
	 	  // Link to the payment service.
	 	  val link = "http://localhost:9090/pay.html?payee=lufthansa&item="+BookableItinerary.is.open_!.id
     
	      // Return link.
	      val result = <p>You need to pay {BookableItinerary.is.open_!.price.toString}. Do it with EuroPay</p><br /><a href={link}>Pay with EuroPay</a>

       // Clear cache
	 	  IDHolder.set(Empty)
	 	  ItineraryHolder.set(Empty)
	 	  TravelerHolder.set(Nil)
	 	  TicketHolder.set(null)
	 	  BookableItinerary.set(Empty)
     
	 	  result
	 	} else {
	 	  // Return to adding travelers.
	 	  S.error("You need at least one Traveler");S.redirectTo("/book/travelers.html")
	 	}
	}
 
	//
	// Create the Ticket instance.
	//
	private def createTicket():Ticket={
	  // Fetch cached Itinerary.
	  val it = BookableItinerary.is.open_!
	  
	  // Fetch cached travellers.
	  val travelers = TravelerHolder.is
   
	  // Create Ticket.
	  val ticket = Ticket.create.uid(it.id).price(it.price * travelers.size)
	  val success = ticket.save
   
	  // Associate Travelers with Ticket.
	  travelers.foreach(e => {
	    TicketTraveler.create._ticket(ticket)._traveler(e).save
	  })
   
	  // Associate Flights with Ticket.
	  var sn = 0;
	  it.segments.foreach(s => {
		var pn = 0;
	    s.hops.foreach(h => {
	    	val f = Flight.find(By(Flight.number, h.flightNumber))
	    	if(!f.isEmpty){
	    	    pn += 1
	    		TicketFlight.create._flight(f.open_!)._ticket(ticket).segmentNumber(sn).positionNumber(pn).save
	    	}
	    })
	    sn += 1
	  })
   
	  // Return the Ticket
	  ticket
    }
 
}
