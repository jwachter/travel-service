package travelservice.model

import world._

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.json._
import _root_.net.liftweb.json.JsonAST._

object City extends City with LongKeyedMetaMapper[City] {
	override def dbTableName = "cities"
}

class City extends LongKeyedMapper[City] with IdPK {
  def getSingleton = City
  
  object name extends MappedString(this, 100)
  object country extends MappedString(this, 100)
  
  def toXML = <city>{ name.is + ", " + country.is }</city>
    
  def toJSON = JObject(List(JField("name",JString(this.name.is)),JField("country",JString(this.country.is))))
  
  // Convert to exposed City object
  def toCity = new world.City(this.name.is, this.country.is)
  
  override def toString = this.name.is + ", " + this.country.is

}
