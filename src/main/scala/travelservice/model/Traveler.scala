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

// Import application modules.
import travelservice.helper.DateTimeHelpers._

//
// Companion object for mapping purposes.
//
object Traveler extends Traveler with LongKeyedMetaMapper[Traveler]{
  override def dbTableName = "travelers"
}

//
// A Traveler. Is used to associate people with Tickets. It's possible that multiple
// Travelers inside the DB represent the same person becuase it's unique to a Ticket. (TODO change this behaviour with accounts)
//
class Traveler extends LongKeyedMapper[Traveler] with IdPK{
  def getSingleton = Traveler
 
  //
  // The first name.
  //
  object firstName extends MappedString(this, 255)

  //
  // The middle name.
  //
  object middleName extends MappedString(this, 255)

  //
  // The last name.
  //
  object lastName extends MappedString(this, 255)

  //
  // The gender.
  //
  object gender extends MappedString(this, 10)

  //
  // The birthday.
  //
  object birthday extends MappedDateTime(this)

  //
  // The travel document type, e.g. passport.
  //
  object travelDocType extends MappedString(this , 100)

  //
  // The travel document number.
  //
  object travelDocNumber extends MappedString(this , 100)

  //
  // The phone number.
  //
  object phone extends MappedString(this , 100)

  //
  // The email address.
  //
  object email extends MappedString(this , 255)
 
  //
  // Transform to a simple HTML List Item.
  //
  def toXHTMLListItem = {
    <li>
    	{firstName.is + middleName.is + lastName.is}
    </li>
  }

  //
  //  Transform to XML
  //
  def toXML = {
    <traveler>
      <firstName>{ this.firstName.is }</firstName>
      <middleName>{ this.middleName.is }</middleName>
      <lastName>{ this.lastName.is }</lastName>
      <birthday>{ iso( this.birthday.is ) }</birthday>
      <travelDocType>{ this.travelDocType.is }</travelDocType>
      <travelDocNumber>{ this.travelDocNumber.is }</travelDocNumber>
    </traveler>
  }
}
