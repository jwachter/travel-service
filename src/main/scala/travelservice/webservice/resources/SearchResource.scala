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
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._

// Import Scala classes.
import _root_.scala.xml._

// Import Joda Time.
import _root_.org.joda.time._
import _root_.org.joda.time.format._

// Import JDK classes.
import _root_.java.util.Date

// Import application classes.
import travelservice.webservice.rest._
import model._

//
// This object provides the implementation of a restful resource to handle search service requests.
//
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
  
  	  private def airportOrCity(id1:String, id2:String):(specification.Place, specification.Place) = {
	    val oap = Airport.findByCode( id1 )
	    val dap = Airport.findByCode( id2 )
     
	
	    var its = Nil;
	    
	    (oap, dap) match {
	    	case (Full(orig), Full(dest)) => (orig.toAirport, dest.toAirport) 
	      case (Empty, Full(dest)) => (City.findByName(id1).open_!.toCity, dest.toAirport) 
	      case (Full(orig), Empty) => (orig.toAirport, City.findByName(id2).open_!.toCity)
	      case (_, _) => ( City.findByName(id1).open_!.toCity, City.findByName(id2).open_!.toCity)
	    }		  
	  }
}
