package travelservice.webservice.resources
// Import Lift modules.
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.mapper._

// Import Scala classes.
import _root_.scala.xml._

// Import Joda Time.
import _root_.org.joda.time._
import _root_.org.joda.time.format._

// Import JDK classes.
import _root_.java.util.Date

// Import application classes.
import travelservice.webservice.rest._
import travelservice.model._
import travelservice.helper._
import travelservice.session._
import travelservice.helper._


object BookResource extends RESTResource{
	//
	// Setup the routes that are used.
	//
	override val dispatch : LiftRules.DispatchPF = {
	
	  // Retrieve the Ticket
	  case r@Req("api" :: "book" :: id :: Nil, _, PostRequest) => () => process(r)
	  
	  // All other requests are not allowed on this Resource.
	  case r@Req("api" :: "book" :: _, _, _) => () => Full(MethodNotAllowedResponse())
	}
  
 	//
 	// Only XML at the moment.
    //
    override val supportedContentTypes = List("xml")
	  //
	  // React to PUT which indicates payment confirmation by a PaymentGateway
	  //
	  override val post = (r:Req, ct:String) => {
	  r match {
	     case  Req("api" :: "book" :: id :: Nil, _, PostRequest) => {
			  val it = ItineraryHolder.is
			  it match {
			    case Full(t) if t.filter(e => e.id == id) != Nil => try {
			      val xml : NodeSeq= r.xml.open_!
			      val travelers = xml \\ "travelers" \ "traveler"
			      
			      val tdata : Seq[(String, String)] = travelers.map(e => ((e  \\ "firstname").text, (e  \\ "lastname").text))

			      val tls = tdata.map(x => Traveler.create.firstName(x._1).lastName(x._2))
         
			      tls.foreach(_.save)
                                   
			      val ticket = createTicket(t.filter(e => e.id == id).toList.head, tls.toList)
         
			      Full(XmlResponse(ticket.toXML))
			    } catch {
			      case e:Exception => Full(InternalServerErrorResponse())
			    }
			    case _ => Full(NotFoundResponse())
			  } 
	     } 
	     case _ => Full(BadResponse())
	  }
   
	}
   	//
	// Create the Ticket instance.
	//
	private def createTicket(it:specification.Itinerary, t:List[Traveler]):Ticket={
	  // Create Ticket.
	  val ticket = Ticket.create.uid(it.id).price(it.price * t.size)
	  val success = ticket.save
   
	  // Associate Travelers with Ticket.
	  t.foreach(e => {
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
