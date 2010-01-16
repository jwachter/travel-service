/**
 * Copyright 2010 Johannes Wachter
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

import airline._

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.mapper._
import _root_.net.liftweb.json.JsonAST._
import _root_.org.joda.time._
import _root_.java.util.Date

/*
 * Companion to the Flight mapper class. Maps the database table to 'flights' and provides utility functions to
 * query for often used result sets.
 */
object Flight extends Flight with LongKeyedMetaMapper[Flight] {
  override def dbTableName = "flights"

  // Utility functions to query flights in date ranges. Shortcuts for querying ranges from a start date + some offset
  def findByHours(earliest:Date, hours:Int) = findInRange(earliest, new DateTime(earliest).plusHours(hours).toDate)
  def findByDays(earliest:Date, days:Int) = findInRange(earliest, new DateTime(earliest).plusDays(days).toDate)
  def findByDaysAndOrigin(earliest:Date, days:Int, origin:Airport) = findInRangeAndOrigin(earliest, new DateTime(earliest).plusDays(days).toDate, origin)
  def findByWeeks(earliest:Date, weeks:Int) = findInRange(earliest, new DateTime(earliest).plusWeeks(weeks).toDate)
  def findByMonths(earliest:Date, months:Int) = findInRange(earliest, new DateTime(earliest).plusMonths(months).toDate)
  def findInRange(from:Date, to:Date) = Flight.findAll(By_>(Flight.departure, from), By_<(Flight.departure, to))
  
  // Find Flights in a date range with a specified starting airport.
  def findInRangeAndOrigin(from:Date, to:Date, origin:Airport) = Flight.findAll(By(Flight._origin, origin), By_>(Flight.departure, from), By_<(Flight.departure, to))
}

/*
 * Database bound mapper class describing a flight.
 */
class Flight extends LongKeyedMapper[Flight] with IdPK{
	// Define the companion object
	def getSingleton = Flight
 
	// A flight number, e.g. FRA123
	object number extends MappedString(this, 32)
 
	// The origin as a foreign key. Should be only used when initializing a new database entry. Otherwise 
	// origin should be used as this holds the real Airport instance or null if no one is specified (shouldn't happen as this would be illegal)
	object _origin extends MappedLongForeignKey(this, Airport){
	  // rename column name because _origin would be illegal
	  override def dbColumnName = "ref_origin"
	}
	// directly load the connected object of type Airport
	def origin = _origin.obj.openOr(null)
	// The destination as a foreign key. Should be only used when initializing a new database entry. Otherwise 
	// origin should be used as this holds the real Airport instance or null if no one is specified (shouldn't happen as this would be illegal)
	object _destination extends MappedLongForeignKey(this, Airport){
	  // rename column name because _destination would be illegal 
	  override def dbColumnName = "ref_destination"
	}
	// directly load the connected object of type Airport
	def destination = _destination.obj.openOr(null)
	// Time when this flight starts
	object departure extends MappedDateTime(this)
	// How long does the flight take (in hours)
	object duration extends MappedInt(this)
 
	// Transforms the Object into XML
	// TODO format the date
	def toXML = 
		<flight>
		 <number>{this.number.is}</number>
		 <origin>{this.origin.code.is}</origin>
		 <destination>{this.destination.code.is}</destination>
		 <departure>{this.departure.is}</departure>
		 <duration>{this.duration.is}</duration>
		</flight>
 
    // Transforms the Object into a JSON representation
	// TODO format the date
	def toJSON = JObject(List(JField("number", JString(this.number.is)),JField("origin", JString(this.origin.code.is)),JField("destination", JString(this.destination.code.is)),JField("departure", JString(this.departure.is.toString)),JField("duration", JString(this.duration.is.toString))))
 
	def toXHTML = <li>{this.toString}</li>
 
	// Transforms the Object into the interface type
	def toHop = new Hop(this.number.is, this.origin.toAirport, this.destination.toAirport, this.departure.is, this.duration.is)
 
    // Do a simple String representation
	// TODO format date
	override def toString = "["+this.number.is+"] "+this.origin.code.is + " to " + this.destination.code.is+" ("+this.departure.is+" taking "+this.duration.is.toString+"h)"
}