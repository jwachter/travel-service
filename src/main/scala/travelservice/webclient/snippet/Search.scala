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

// Import JDK classes.
import _root_.java.util.Date

// Import Joda Time.
import _root_.org.joda.time._
import _root_.org.joda.time.format._

// Import Lift modules.
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.js.{JsCmd}
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.SHtml._
import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util.Helpers._

// Import Scala classes.
import _root_.scala.xml._
import _root_.scala.xml.NodeSeq

// Import application classes.
import travelservice.model._
import travelservice.helper._
import travelservice.session._

// Import airline service.
import lufthansa._

//
// Snippet that handles everything that is related to the search mechanism.
//
class Search {
  
  //
  // Private instance of our service.
  //
  private val lufthansa = new Lufthansa()
  
  //
  // Which search types are supported?
  //
  private val supportedTypes = List("oneway","roundtrip","multisegment")

  //
  // Formatter/Parser for Dates.
  //
  private val dateFormat = DateTimeFormat.forPattern("yyyy/MM/dd")

  	//
  	// Method that kicks of the search.
  	//
	def performSearch(xhtml:NodeSeq):NodeSeq = {
	  val searchType = S.param("type")
	  // Determine search Type. If it is supported do the search.
	  searchType match {
	    case Full(st) if !st.isEmpty && supportedTypes.contains(st) => search(st)
	    case _ => <p>The requested search type isn't supported by our airline!</p>
	  }
	}
 
  	//
  	// Methods that handles the search execution.
    //
  	private def search(mode:String):NodeSeq={
  	  // Use specific method for different types.
  	  mode match {
  	    case "oneway" => handleOneWaySearch()
  	    case "roundtrip" => handleRoundTripSearch()
  	    case "multisegment" => handleMultiSegmentSearch()
      }
  	}
   
   	//
   	// Do a one way search.
    //
   	private def handleOneWaySearch()={
   	  val origin = S.param("from")
      val destination = S.param("to") 
      val departure = S.param("departure")
      
      (origin, destination, departure) match {
        case (Full(orig), Full(dest), Full(dept)) if !dept.isEmpty && !orig.isEmpty && !dest.isEmpty => {
          try {
        	  val date : Date = dateFormat.parseDateTime(dept).toDate
        	  val places = AirportCityHelper.getPlaces(orig, dest)
        	  places match {
        	    case Full((p1, p2)) => {
        	      val its = lufthansa.searchOneway(p1, p2, date)
        	      ItineraryHolder.set(Full(its))
        	   
        	      <div>{its.take(100).map(e => ItineraryHelper.itineraryToXHTML(e))}</div>
                }
        	    case _ => <p>An error occured while processing your search.</p>
        	  }
          } catch {
            case e:Exception => S.error("An error occured while searching. Please try again!"); <p>No results!</p>
          }
        }
        case _ => S.error("You didn't specify all needed parameters."); <p>No results!</p>
      }
   	}

  //
  // Do a round trip search.
  //
  private def handleRoundTripSearch()={
   	  val origin = S.param("from")
      val destination = S.param("to") 
      val departure = S.param("departure")
      val returnDate = S.param("returnDate")
      
      (origin, destination, departure, returnDate) match {
        case (Full(orig), Full(dest), Full(dept), Full(ret)) if !dept.isEmpty && !orig.isEmpty && !dest.isEmpty && !ret.isEmpty => {
          try {
        	  val start : Date = dateFormat.parseDateTime(dept).toDate
        	  val back : Date = dateFormat.parseDateTime(ret).toDate
        	  val places = AirportCityHelper.getPlaces(orig, dest)
        	  places match {
        	    case Full((p1, p2)) => {
        	      val its = lufthansa.searchRoundtrip(p1, p2, start, back)
        	      ItineraryHolder.set(Full(its))
               
        	      <div>{its.take(100).map(e => ItineraryHelper.itineraryToXHTML(e))}</div>
                }
        	    case _ => <p>An error occured while processing your search.</p>
        	  }
          } catch {
            case e:Exception => S.error("An error occured while searching. Please try again!"); <p>No results!</p>
          }
        }
        case _ => S.error("You didn't specify all needed parameters."); <p>No results!</p>
      }
  }
  
  //
  // Do a multisegment search.
  //
  private def handleMultiSegmentSearch()={
   	  val origins = S.param("originSequence")
      val destinations = S.param("destinationSequence") 
      val departures = S.param("departureSequence")
      
      (origins, destinations, departures) match {
        case (Full(orig), Full(dest), Full(dept)) if !dept.isEmpty && !orig.isEmpty && !dest.isEmpty => {
          try {
        	  // Split incoming csv.
        	  val _ors = orig.split(";").toList
        	  val _dsts = dest.split(";").toList
        	  val _dpts = dept.split(";").toList
           
        	  	  val param = _ors.zip( _dsts ).map( ( e ) => AirportCityHelper.getPlaces( e._1, e._2 ).open_! ).zip( _dpts ).map( ( e ) => ( e._1._1, e._1._2, e._2 ) )
   
				  val real : Seq[( specification.Place, specification.Place, Date )] = param.map(e => ( e._1, e._2, DateTimeHelpers.fromDefault(e._3).toDate))
			   
				  val its = lufthansa.searchMultisegment( real )

				  its match {
				      case seq if seq.size > 0 => ItineraryHolder.set(Full(its))
				      case _ => ItineraryHolder.set(Empty)
				    }
				
				    <div>{its.take(100).flatMap( i => ItineraryHelper.itineraryToXHTML( i ) )}</div>
          } catch {
            case e:Exception => S.error("An error occured while searching. Please try again!"); <p>No results!</p>
          }
        }
        case _ => S.error("You didn't specify all needed parameters."); <p>No results!</p>
      }
  }
}
