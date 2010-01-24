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
package travelservice.helper

// Import needed Lift modules.
import _root_.net.liftweb.common._

// Import data model.
import travelservice.model._

//
// This helper object provides the possiblity to give it two Strings and it determines which type of place
// is represented by each of them. Useful to allow a search with IATA Codes and City names. IATA Codes are checked first and
// overrule City names.
//
object AirportCityHelper {
  
	 //
	 // Fetches the Place instance representing each of the names. Can be either City or Airport.
	 //
	 def getPlaces(from:String, to:String):Box[(specification.Place, specification.Place)] = {
	    // Try to find Airports
		try {
		  	val oap = Airport.findByCode( from )
		    val dap = Airport.findByCode( to )
		    
		    // See if we found Airports. If Empty is indicated this means it can only be a City. (or wrong data. TODO catch that case!)
		    ( oap, dap ) match {
		      case ( Full( orig ), Full( dest)) => Full(( orig.toAirport, dest.toAirport ))
		      case ( Empty, Full( dest ) ) =>  Full(( City.findByName( from ).open_!.toCity, dest.toAirport ))
		      case ( Full( orig), Empty ) => Full( ( orig.toAirport, City.findByName( to ).open_!.toCity ))
		      case ( _, _ ) =>  Full(( City.findByName( from ).open_!.toCity, City.findByName( to ).open_!.toCity ))
		    }	
		} catch {
  			case e:Exception => Empty
		}
	 }
}
