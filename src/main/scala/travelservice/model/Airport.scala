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

object Airport extends Airport with LongKeyedMetaMapper[Airport] {
  override def dbTableName = "airports"
}

class Airport extends LongKeyedMapper[Airport] with IdPK{
	def getSingleton = Airport
  
	object code extends MappedString(this, 3)
	object name extends MappedString(this, 200)
	object city extends MappedString(this, 100)
	object country extends MappedString(this, 100)
	object lat extends MappedDouble(this)
	object long extends MappedDouble(this)
 
	def asXml = <airport>
	<code>{this.code.is}</code>
    <name>{this.name.is}</name>
    <location>
    	<city>{this.city.is}</city>
    	<country>{this.country.is}</country>
        <latitude>{this.lat.is}</latitude>
        <longitude>{this.long.is}</longitude>
    </location>
 </airport>
}

private[model] class MappedAirport[T <: Mapper[T]](mapper: T) extends MappedLongForeignKey(mapper, Airport)
