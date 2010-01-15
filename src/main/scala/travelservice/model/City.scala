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

import world._

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.json._
import _root_.net.liftweb.json.JsonAST._

/*
 * Companion object for the City mapper, just defines the table name
 */
object City extends City with LongKeyedMetaMapper[City] {
	override def dbTableName = "cities"
}

/*
 * Represents a City in the database. A City is described by Name and Country
 */
class City extends LongKeyedMapper[City] with IdPK {
  // Define companion
  def getSingleton = City
  
  // The city name
  object name extends MappedString(this, 100)
  // The city's country
  object country extends MappedString(this, 100)
  
  // Transform object to XML
  def toXML = <city>{ name.is + ", " + country.is }</city>
  
  // Transform object to JSON
  def toJSON = JObject(List(JField("name",JString(this.name.is)),JField("country",JString(this.country.is))))
  
  // Transform city to interface type
  def toCity = new world.City(this.name.is, this.country.is)
  
  // Transform object to readable string
  override def toString = this.name.is + ", " + this.country.is

}
