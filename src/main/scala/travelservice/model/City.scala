package travelservice.model

import contract.world._

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.json._
import _root_.net.liftweb.json.JsonAST._

object DBCity extends DBCity with LongKeyedMetaMapper[DBCity] {
  
}

class DBCity extends LongKeyedMapper[DBCity] with IdPK {
  def getSingleton = DBCity
  
  object name extends MappedString(this, 100)
  object country extends MappedString(this, 100)
  
  def toXML = <city>{ name.is + ", " + country.is }</city>
    
  def toJSON = JObject(Nil)
  
  def toCity = new City(this.name.is, this.country.is)

}
