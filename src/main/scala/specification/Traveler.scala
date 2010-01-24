package specification

import _root_.java.util.Date

class Traveler(
  val firstName: String,
  val middleName: String,
  val lastName: String,
  val gender: String,
  val birthday: Date,          // should be in YYYY-MM-DD format when exported to XML
  val travelDocType: String,   // passport, driver license, EU citizen card, etc
  val travelDocNumber: String,
  val phone: String,
  val email: String){

  def toXML = 
    <traveler>
      <firstName>{ firstName }</firstName>
      <middleName>{ middleName }</middleName>
      <lastName>{ lastName }</lastName>
      <gender>{ gender } </gender>
      <birthday>{ birthday }</birthday>
      <travelDocType>{ travelDocType }</travelDocType>
      <travelDocNumber>{ travelDocNumber }</travelDocNumber>
      <phone>{ phone }</phone>
      <email>{ email }</email>
    </traveler>
}