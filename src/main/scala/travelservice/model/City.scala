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

// Import public interface contracts.
import specification._

// Import Lift needed framework modules. 
import _root_.net.liftweb.mapper._
import _root_.net.liftweb.json._
import _root_.net.liftweb.json.JsonAST._

//
// Companion object to the City data class. Besides the default creation and query methods of LongKeyedMetaMapper this object
// provides an utility methods for finding Cities directly by their name without constructing a query.
//
object City extends City with LongKeyedMetaMapper[City] {
    override def dbTableName = "cities"

    def findByName(name:String) = City.find(By(City.name, name))
}

//
// Represents a City in the local data model of the Lift application. The attributes of a City are the same as of
// specification.City except that this City implementation is persisted into a database and supports also JSON representation.
// It's also possible to transform a City into a specification.City to satisfy public interface contracts.
//
class City extends LongKeyedMapper[City] with IdPK {
    //
    // Define a shortcut to the companion object to this class.
    //
    def getSingleton = City
  
    //
    // Holds the name of the City.
    //
    object name extends MappedString(this, 100)

    //
    // Holds the country the City is located in.
    //
    object country extends MappedString(this, 100)
  
    //
    // Allows a XML representation of the city.
    //
    def toXML = <city>{ name.is + ", " + country.is }</city>
  
    //
    // Allows a JSON representation using Lift 1.1-M8 JSON features 
    //
    def toJSON = JObject(List(JField("name",JString(this.name.is)),JField("country",JString(this.country.is))))
  
    //
    // Transforms this City into the public interface type.
    //
    def toCity = new specification.City(this.name.is, this.country.is)
  
    //
    // Provides a readable String representation.
    //
    override def toString = this.name.is + ", " + this.country.is
}
