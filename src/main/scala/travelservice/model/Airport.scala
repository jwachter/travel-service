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

object Airport extends Airport with LongKeyedMetaMapper[Airport] {
  override def dbTableName = "airports"
  
  def findByCode(code:String) = Airport.find(By(Airport.code, code))
  
  def findByCity(city:City) = Airport.find(By(Airport._city, city))
}

class Airport extends LongKeyedMapper[Airport] with IdPK{
	def getSingleton = Airport
  
	object code extends MappedString(this, 3)
	object name extends MappedString(this, 200)
	object _city extends MappedLongForeignKey(this, City){
	  override def dbColumnName = "ref_city"
	}
	def city = _city.obj.openOr(null)
	object lat extends MappedDouble(this)
	object long extends MappedDouble(this)
 
	def findFlights = Flight.findAll(By(Flight._origin, this))
	def findFlightsInDateRange(from:Date, to:Date) = Flight.findAll(By(Flight._origin, this), By_>(Flight.departure, from), By_<(Flight.departure, to)) 
 
	def toXML = <airport>
	<code>{this.code.is}</code>
    <name>{this.name.is}</name>
    <location>
    	<city>{this.city.toString}</city>
        <latitude>{this.lat.is}</latitude>
        <longitude>{this.long.is}</longitude>
    </location>
 </airport>
 
 def toJSON = JObject(List(JField("code",JString(this.code.is)),JField("name",JString(this.name.is)),JField("city", JString(this.city.toString)), JField("latitude",JString(this.lat.toString)),JField("longitude",JString(this.long.toString))))
  
 // To external Airport object
 def toAirport = new world.Airport(Symbol(this.code.is), this.name.is, this.city.toCity)
 
 override def toString = "[" + this.code.is + "] "+this.name.is+" - "+this.city.toString
}

private[model] class MappedAirport[T <: Mapper[T]](mapper: T) extends MappedLongForeignKey(mapper, Airport)
