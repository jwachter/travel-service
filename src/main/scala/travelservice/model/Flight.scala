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

object Flight extends Flight with LongKeyedMetaMapper[Flight]

class Flight extends LongKeyedMapper[Flight] with IdPK{
	 def getSingleton = Flight
  
	 object followingFlight extends MappedLongForeignKey(this, Flight)
	 object origin extends MappedLongForeignKey(this, Airport)//MappedAirport(this)
	 object destination extends MappedLongForeignKey(this, Airport)//MappedAirport(this)
	 object time extends MappedDateTime(this)
	 object length extends MappedDouble(this)
  
	 def toXML = <flight>
	 <from>{origin.name}</from>
	 <to>{destination.name}</to>
	 <duration>{time}</duration>
	 <length>{length}</length>
  </flight>
}

private[model] class MappedFlight[T <: Mapper[T]](mapper: T) extends MappedLongForeignKey(mapper, Flight)