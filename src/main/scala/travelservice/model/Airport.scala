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

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.json.JsonAST._
import _root_.java.util.Date

/*
 * Companion object to the Airport mapper. Provides utility methods for looking up an Airport by Code or by city
 */
object Airport extends Airport with LongKeyedMetaMapper[Airport] {
  override def dbTableName = "airports"
  
  // Find an Airport by the code (unique)
  def findByCode(code:String) = Airport.find(By(Airport.code, code))
  
  // Find all airports in one city
  def findByCity(city:City) = Airport.findAll(By(Airport._city, city))
}

/*
 * Mapper class for an Airport. Provides also utility methods for getting all flights from the current Airport and
 * Flights in a specific date range from the current airport.
*/
class Airport extends LongKeyedMapper[Airport] with IdPK{
	def getSingleton = Airport
  
	// Holds the IATA code of the Airport
	object code extends MappedString(this, 3)
 
	// Holds the name of the Airport
	object name extends MappedString(this, 200)
 
	// Holds the foreign key to the city where the Airport is located. Should only be used when initializing new db entries.
	object _city extends MappedLongForeignKey(this, City){
	  // Override column name as _city would be invalid.
	  override def dbColumnName = "ref_city"
	}
	
	// Holds the proper City instance or null if none is defined (shouldn't happen)
	def city = _city.obj.openOr(null)
 
	// Geolocation of the Airport.
	object lat extends MappedDouble(this)
	object long extends MappedDouble(this)
 
	// Find all flights departing from this Airport
	def findFlights = Flight.findAll(By(Flight._origin, this))
 
	// FInd all flights departing from this airport in the specified range
	def findFlightsInDateRange(from:Date, to:Date) = Flight.findAll(By(Flight._origin, this), By_>(Flight.departure, from), By_<(Flight.departure, to)) 
	
	// Transform to XML
	def toXML = <airport>
	<code>{this.code.is}</code>
    <name>{this.name.is}</name>
    <location>
    	<city>{this.city.toString}</city>
        <latitude>{this.lat.is}</latitude>
        <longitude>{this.long.is}</longitude>
    </location>
 </airport>
 
 // Transform to JSON
 def toJSON = JObject(List(JField("code",JString(this.code.is)),JField("name",JString(this.name.is)),JField("city", JString(this.city.toString)), JField("latitude",JString(this.lat.toString)),JField("longitude",JString(this.long.toString))))
  
 // Transform to API type
 def toAirport = new world.Airport(Symbol(this.code.is), this.name.is, this.city.toCity)
 
 // Transform to String
 override def toString = "[" + this.code.is + "] "+this.name.is+" - "+this.city.toString
}

// Shortcut for specifing foreign keys to this mapper
private[model] class MappedAirport[T <: Mapper[T]](mapper: T) extends MappedLongForeignKey(mapper, Airport)
