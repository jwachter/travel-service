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
import travelservice.model._
import travelservice.helper._

//
// This object provides the implementation of a restful resource to handle search service requests.
//
object SearchResource extends RESTResource{
	
	val df  = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm")
  
	override val dispatch : LiftRules.DispatchPF = {
	  case r@Req("api" :: "search" :: _, _, PostRequest) => () => process(r)
	  case r@Req("api" :: "search" :: _, _, _) => () => process(r)
	}
 
 	override val supportedContentTypes = List("xml")
  
 	override val post = (r:Req, ct:String) => {
 	  r match {
 	    case r@Req("api" :: "search" :: _, _, PostRequest) => {
 	      if(!r.xml.isEmpty){
 	    	handleSearch(r.xml.open_!)
 	      } else {
 	        Full(BadResponse())
 	      }
 	    }
      case _ => Full(BadResponse())
 	  }
 	}
  
 	private def handleSearch(xml:NodeSeq):Box[LiftResponse]={
 	  val searchType = (xml \ "@type").text
 	  
 	  searchType match {
 	    case "oneway" => handleOneWay(xml)
 	    case "roundtrip" => handleRoundTrip(xml) 
 	    case "multisegment" => handleMultiSegment(xml)
 	  }
 	}  
  
 	private def handleOneWay(contents:NodeSeq):Box[LiftResponse]={
 		val origin = (contents \\ "origin").text
 		val destination = (contents \\ "destination").text
 		val departureDate = df.parseDateTime((contents \\ "departureDate").text).toDate
   
 		val found = AirportCityHelper.getPlaces(origin, destination).open_!
 		
 		val res = new lufthansa.Lufthansa().searchOneway(found._1, found._2, departureDate)
   
 		Full(XmlResponse(<itineraries>{res.map(e => e.toXML)}</itineraries>))
 	}
  
 	private def handleRoundTrip(contents:NodeSeq):Box[LiftResponse]={
 		val origin = (contents \\ "origin").text
 		val destination = (contents \\ "destination").text
 		val departureDate = df.parseDateTime((contents \\ "departureDate").text).toDate
 		val returnDate = df.parseDateTime((contents \\ "returnDate").text).toDate
 		
 		val found = AirportCityHelper.getPlaces(origin, destination).open_!
   
 		val res = new lufthansa.Lufthansa().searchRoundtrip(found._1, found._2, departureDate, returnDate)
   
 		Full(XmlResponse(<itineraries>{res.map(e => e.toXML)}</itineraries>))
 	}
  
 	private def handleMultiSegment(xml:NodeSeq)={
 	  val segments = xml \\ "segments" \ "segment"
    
 	  val steps = segments.map(s => ((s \\ "origin").text,(s \\ "destination").text,(s \\ "departureDate").text))
    
 	  var searchSegments : List[(specification.Place, specification.Place, Date)]= Nil
 	  for(step <- steps){
 	    val places = AirportCityHelper.getPlaces(step._1, step._2).open_!
 	    val date = df.parseDateTime(step._3).toDate
 	    
 	    searchSegments = (places._1, places._2, date) :: searchSegments
 	  }
 		val res = new lufthansa.Lufthansa().searchMultisegment(searchSegments.reverse)
   
 		Full(XmlResponse(<itineraries>{res.map(e => e.toXML)}</itineraries>))
 	}     
}
