package travelservice.webservice.resources

import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.scala.xml._
import _root_.org.joda.time._
import _root_.org.joda.time.format._
import _root_.java.util.Date

import travelservice.webservice.rest._

import model._

object SearchResource /*extends RESTResource*/{
	
	val df  = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm")
  
	/*override*/ val dispatch : LiftRules.DispatchPF = {
	  case r@Req("api" :: "search" :: _, _, GetRequest) => () => process(r)
	  case r@Req("api" :: "search" :: _, _, PostRequest) => () => process(r)
	  case r@Req("api" :: "search" :: _, _, PutRequest) => () => process(r)
	  case r@Req("api" :: "search" :: _, _, DeleteRequest) => () => process(r)
	}
 
 	//override val supportedContentTypes = List("xml","json")
  
 	//override val post = (r:Req, ct:String) => {
    def process(req:Req)={
 	  val contents = req.xml.open_!
    
 	  val searchType = (contents \ "@type").text
 	  
 	  searchType match {
 	    case "oneway" => handleOneWay(contents)
 	    case "roundtrip" => handleRoundTrip(contents) 
 	    //case "multisegment" => handleMultiSegment(contents)
 	  }
 	}
  
 	private def handleOneWay(contents:NodeSeq):Box[LiftResponse]={
 		val origin = (contents \ "origin").text
 		val destination = (contents \ "destination").text
 		val departureDate = df.parseDateTime((contents \ "departureDate").text).toDate
 		val found = airportOrCity(origin, destination)
 		
 		val res = new lufthansa.Lufthansa().searchOneway(found._1, found._2, departureDate)
   
 		Full(XmlResponse(<itineraries>{res.map(e => e.toXML)}</itineraries>))
 	}
  
 	private def handleRoundTrip(contents:NodeSeq):Box[LiftResponse]={
 		val origin = (contents \ "origin").text
 		val destination = (contents \ "destination").text
 		val departureDate = df.parseDateTime((contents \ "departureDate").text).toDate
 		val returnDate = df.parseDateTime((contents \ "returnDate").text).toDate
 		
 		val found = airportOrCity(origin, destination)
   
 		val res = new lufthansa.Lufthansa().searchRoundtrip(found._1, found._2, departureDate, returnDate)
   
 		Full(XmlResponse(<itineraries>{res.map(e => e.toXML)}</itineraries>))
 	}
  
  	  private def airportOrCity(id1:String, id2:String):(world.Place, world.Place) = {
  	    println(id1, id2)
	    val oap = Airport.findByCode( id1 )
	    val dap = Airport.findByCode( id2 )
     
	    println(oap, dap)
	
	    var its = Nil;
	    
	    (oap, dap) match {
	    	case (Full(orig), Full(dest)) => (orig.toAirport, dest.toAirport) 
	      case (Empty, Full(dest)) => (City.find(id1).open_!.toCity, dest.toAirport) 
	      case (Full(orig), Empty) => (orig.toAirport, City.find(id2).open_!.toCity)
	      case (_, _) => ( City.find(id1).open_!.toCity, City.find(id2).open_!.toCity)
	    }		  
	  }
}
