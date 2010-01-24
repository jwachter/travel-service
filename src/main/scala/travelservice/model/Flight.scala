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

// Import application modules.
import travelservice.helper.DateTimeHelpers._

// Import public interface types.
import specification._

// Import needed Lift modules.
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.mapper._
import _root_.net.liftweb.json.JsonAST._

// Import Joda Time for DateTime handling.
import _root_.org.joda.time._
import _root_.org.joda.time.format._

// Import needed JDK classes.
import _root_.java.util.Date

//
// Companion object for Flight that provides several additonal methods for finding Flights in different time ranges. Also
// Flights in a time range departing from a specific Airport.
//
object Flight extends Flight with LongKeyedMetaMapper[Flight] {
  override def dbTableName = "flights"

  //
  // Find all Flights from a Date + several Hours.
  //
  def findByHours(earliest:Date, hours:Int) = findInRange(earliest, new DateTime(earliest).plusHours(hours).toDate)
  
  //
  // Find all Flights from a Date + several Days.
  //
  def findByDays(earliest:Date, days:Int) = findInRange(earliest, new DateTime(earliest).plusDays(days).toDate)
  
  //
  // Find all Flights from a Date + several Days and departing a specific Airport.
  //
  def findByDaysAndOrigin(earliest:Date, days:Int, origin:Airport) = findInRangeAndOrigin(earliest, new DateTime(earliest).plusDays(days).toDate, origin)
  
  //
  // Find all Flights from a Date + several Weeks.
  //
  def findByWeeks(earliest:Date, weeks:Int) = findInRange(earliest, new DateTime(earliest).plusWeeks(weeks).toDate)
  
  //
  // Find all Flights from a Date + several Months.
  //
  def findByMonths(earliest:Date, months:Int) = findInRange(earliest, new DateTime(earliest).plusMonths(months).toDate)
  
  //
  // Find all Flights between two Dates.
  //
  def findInRange(from:Date, to:Date) = Flight.findAll(By_>(Flight.departure, from), By_<(Flight.departure, to))
  
  // 
  // Find Flights in a date range with a specified starting airport.
  //
  def findInRangeAndOrigin(from:Date, to:Date, origin:Airport) = Flight.findAll(By(Flight._origin, origin), By_>(Flight.departure, from), By_<(Flight.departure, to))
}

//
// Mapper class that specifies the data model for Flights in our Airline system. Mirrors mostly the API type specification.Hop, but with
// the extension that it is database bound. For convenience it can be converted to the API type.
//
class Flight extends LongKeyedMapper[Flight] with IdPK{
  
	//
    // Define a shortcut to the companion.
    //
	def getSingleton = Flight
 
	// A flight number, e.g. FRA123
	object number extends MappedString(this, 32)
 
    //
    // The origin as a foreign key. Should be only used when initializing a new database entry. Otherwise 
	// origin should be used as this holds the real Airport instance or null if no one is specified (shouldn't happen as this would be illegal)
    //
	object _origin extends MappedLongForeignKey(this, Airport){
	  // Rename column name because _origin would be illegal.
	  override def dbColumnName = "ref_origin"
	}
    
    //
	// Directly load the connected object of type Airport.
    //
	def origin = _origin.obj.openOr(null)
    
    //
	// The destination as a foreign key. Should be only used when initializing a new database entry. Otherwise 
	// origin should be used as this holds the real Airport instance or null if no one is specified (shouldn't happen as this would be illegal).
    //
	object _destination extends MappedLongForeignKey(this, Airport){
	  // rename column name because _destination would be illegal 
	  override def dbColumnName = "ref_destination"
	}
    
    //
	// Directly load the connected object of type Airport.
    //
	def destination = _destination.obj.openOr(null)
    
    //
	// DateTime when this flight starts.
    //
	object departure extends MappedDateTime(this)
  
    //
	// How long does the flight take (in hours).
    //
	object duration extends MappedInt(this)
 
    // 
    // How much does this Flight cost?
    //
	object price extends MappedInt(this)
 
    //
	// Transforms the Object into XML.
	//
	def toXML = 
		<flight>
		 <number>{this.number.is}</number>
		 <origin>{this.origin.code.is}</origin>
		 <destination>{this.destination.code.is}</destination>
		 <departure>{iso(this.departure.is)}</departure>
		 <duration>{this.duration.is}</duration>
		</flight>

		//
		// Transforms the Object into XML.
		//
		def toXHTMLTable = 
			<tr>
		<td>{this.number.is}</td>
		<td>{this.origin.code.is}</td>
		<td>{this.destination.code.is}</td>
		<td>{iso(this.departure.is)}</td>
		<td>{this.duration.is}</td>
		</tr>
 
    //
    // Transforms the Object into a JSON representation.
	// 
	def toJSON = JObject(List(JField("number", JString(this.number.is)),JField("origin", JString(this.origin.code.is)),JField("destination", JString(this.destination.code.is)),JField("departure", JString(iso(this.departure.is))),JField("duration", JString(this.duration.is.toString))))
 
    //
    // Transform into a very simple list item for HTML.
    //
	def toXHTML = <li>{this.toString}</li>
 
    //
	// Transforms the Object into the interface type.
    //
	def toHop = new Hop(this.number.is, this.origin.toAirport, this.destination.toAirport, this.departure.is, this.duration.is)
 
    //
    // Do a simple String representation.
	//
	override def toString = "["+this.number.is+"] "+this.origin.code.is + " to " + this.destination.code.is+" ("+iso(this.departure.is)+" taking "+this.duration.is.toString+"h)"
}