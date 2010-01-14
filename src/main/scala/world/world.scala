package world

abstract class Place

case class City(name: String, country: String) extends Place {
  def toXML = <city>{ name + ", " + country }</city>
}

case class Airport(code: Symbol, name: String, city: City) extends Place {
  def toXML =
    <airport>
      <code type="IATA">{ code.name }</code>
      <name>{ name }</name>
      { city.toXML }
    </airport>
    // note: must convert Symbol to string by invoking its .name method to write in XML
    //       otherwise the string output in XML will contain the quote mark of symbols
}