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
package travelservice.webservice.resources

// Import Lift modules.
import _root_.net.liftweb.mapper._
import _root_.net.liftweb.common._
import _root_.net.liftweb.json._
import _root_.net.liftweb.json.JsonAST._
import _root_.net.liftweb.http._

// Import joda time.
import _root_.org.joda.time._

// Import application classes.
import travelservice.webservice.rest._
import travelservice.model._

//
// The Ticket resource provides a restful interface for retrieving the value of a Ticket or a complete representation of it.
//
object TicketResource extends RESTResource{
	//
	// Setup the routes that are used.
	//
	override val dispatch : LiftRules.DispatchPF = {
	
	  // Retrieve the Ticket
	  case r@Req("api" :: "ticket" :: id :: Nil, _, GetRequest) => () => process(r)
	
	  // Get value of the ticket
	  case r@Req("api" :: "ticket" :: id :: "payment" :: Nil, _, GetRequest) => () => process(r)
	  
	  // Set payment succesful flag from PaymentGateway via service
	  case r@Req("api" :: "ticket" :: id :: "payment" :: Nil, _, PutRequest) => () => process(r)
	  
	  // All other requests are not allowed on this Resource.
	  case r@Req("api" :: "ticket" :: _, _, _) => () => Full(MethodNotAllowedResponse())
	}
  
 	//
 	// Only XML at the moment.
    //
    override val supportedContentTypes = List("xml")
 
    //
    // Override the default GET handling of the RESTResource trait.
    //
	override val get = (r:Req, ct:String) => {
	  r match {
		 case Req("api" :: "ticket" :: id :: Nil, _, GetRequest) => {
			  val ticket = Ticket.find(By(Ticket.uid, id))
			  ticket match {
			    case Full(t) => Full(XmlResponse(t.toXML))
			    case _ => Full(NotFoundResponse())
			  }
		 }
	     case  Req("api" :: "ticket" :: id ::"payment" :: Nil, _, GetRequest) => {
			  val ticket = Ticket.find(By(Ticket.uid, id))
			  ticket match {
			    case Full(t) => Full(XmlResponse(<price id={id}>{t.price}</price>))
			    case _ => Full(NotFoundResponse())
			  } 
	     } 
	     case _ => Full(BadResponse())
	  }
	  }
 
	  //
	  // React to PUT which indicates payment confirmation by a PaymentGateway
	  //
	  override val put = (r:Req, ct:String) => {
	  r match {
	     case  Req("api" :: "ticket" :: id ::"payment" :: Nil, _, PutRequest) => {
			  val ticket = Ticket.find(By(Ticket.uid, id))
			  ticket match {
			    case Full(t) => try {
			      t.paymentStatus(true);
			      Full(OkResponse())
			    } catch {
			      case e:Exception => Full(InternalServerErrorResponse())
			    }
			    case _ => Full(NotFoundResponse())
			  } 
	     } 
	     case _ => Full(BadResponse())
	  }
   
	}
}
