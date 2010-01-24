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

// Import needed Lift modules.
import _root_.net.liftweb.mapper._
import _root_.net.liftweb.json.JsonAST._

// Import needed JDK classes.
import _root_.java.util.Date

//
// Companion object to Airport that provides utility methods for finding an Airport directly
// by IATA code or City instance. If you search by City a List of Airports is returned because it's
// possible that one City has multiple Airports (e.g. NYC, ...)
//
object Airport extends Airport with LongKeyedMetaMapper[Airport] {
  override def dbTableName = "airports"
  
  //
  // Find an Airport by the code (unique). Code is the IATA Code of the Airport.
  //
  def findByCode(code:String) = Airport.find(By(Airport.code, code))
  
  //
  // Find all airports in one City.
  //
  def findByCity(city:City) = Airport.findAll(By(Airport._city, city))
}

//
// Data mapper class that holds the information about an Airport. Like City this class mirrors the attributes of the
// Public interface class except that it is bound to a database. It's possible to convert this classes instances into
// instances of the public interface
//
class Airport extends LongKeyedMapper[Airport] with IdPK{
	//
    // Define a shortcut to the companion object.
    //
	def getSingleton = Airport
  
	//
    // The IATA Code of the Airport.
    //
	object code extends MappedString(this, 3)
 
	//
    // The common name of the Airport.
    //
	object name extends MappedString(this, 200)
 
	//
    // The foreign key to the city it is in.
    //
	object _city extends MappedLongForeignKey(this, City){
	  // Override column name as _city would be invalid.
	  override def dbColumnName = "ref_city"
	}
	
	//
    // Define a shortcut to the real referenced City.
    //
	lazy val city = _city.obj.openOr(null)
 
	//
    // Holds the latitude of the Airport.
    //
	object lat extends MappedDouble(this)
 
	//
    // Holds the longitude of the Airport.
    //
	object long extends MappedDouble(this)
 
	//
    // Find all Flight instances departing from this Airport.
    //
	def findFlights = Flight.findAll(By(Flight._origin, this))
 
	//
    // Find all Flights departing from this Airport in a specific timeframe.
    //
	def findFlightsInDateRange(from:Date, to:Date) = Flight.findAll(By(Flight._origin, this), By_>(Flight.departure, from), By_<(Flight.departure, to)) 
	
	//
    // Transform to XML.
    //
	def toXML = <airport>
	<code>{this.code.is}</code>
    <name>{this.name.is}</name>
    <location>
    	<city>{this.city.toString}</city>
        <latitude>{this.lat.is}</latitude>
        <longitude>{this.long.is}</longitude>
    </location>
 </airport>
 
	//
    // Transform to JSON.
    //
    def toJSON = JObject(List(JField("code",JString(this.code.is)),JField("name",JString(this.name.is)),JField("city", JString(this.city.toString)), JField("latitude",JString(this.lat.toString)),JField("longitude",JString(this.long.toString))))
  
	//
    // Transform to public API type.
    //
    def toAirport = new specification.Airport(Symbol(this.code.is), this.name.is, this.city.toCity)
 
	//
    // Transform to a readable String.
    //
	override def toString = "[" + this.code.is + "] "+this.name.is+" - "+this.city.toString
}

//
// Shortcut class for defining foreign key relationships from other mapper classes to this type.
//
private[model] class MappedAirport[T <: Mapper[T]](mapper: T) extends MappedLongForeignKey(mapper, Airport)
