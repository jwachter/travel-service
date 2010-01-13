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

// Cities

// in Germany
object Frankfurt extends City("Frankfurt", "Germany")
object Berlin    extends City("Berlin", "Germany")
object Munich    extends City("Munich", "Germany")

// in Canada
object Toronto   extends City("Toronto", "Canada")
object Vancouver extends City("Vancouver", "Canada")
object Calgary   extends City("Calgary", "Canada")
object StJohns   extends City("St. John's", "Canada")

// in China
object Beijing   extends City("Beijing", "China")
object Shanghai  extends City("Shanghai", "China")
object Chengdu   extends City("Chengdu", "China")
object HongKong  extends City("Hong Kong", "China")

// in Italy
object Milan     extends City("Milan", "Italy")
object Rome      extends City("Rome", "Italy")
object Venice    extends City("Venice", "Italy")

// Airports

// in Germany
object FRA extends Airport('FRA, "Frankfurt International Airport", Frankfurt)
object HHN extends Airport('HHN, "Frankfurt Hahn Airport", Frankfurt)
object MUC extends Airport('MUC, "Franz Josef Strauss International Airport", Munich)
object OIN extends Airport('OIN, "Tegel International Airport", Berlin)
object SXF extends Airport('SXF, "Berlin-Schönefeld International Airport", Berlin)

// in Canada
object YYZ extends Airport('YYZ, "Toronto Pearson International Airport", Toronto)
object YZD extends Airport('YZD, "Toronto Downsview Airport", Toronto)
object YYT extends Airport('YYT, "St. John's International Airport", StJohns)
object YVR extends Airport('YVR, "Vancouver International Airport", Vancouver)
object YYC extends Airport('YYC, "Calgary International Airport", Calgary)

// in China
object PEK extends Airport('PEK, "Beijing Capital International Airport", Beijing)
object PVG extends Airport('PVG, "Shanghai Pudong International Airport", Shanghai)
object CTU extends Airport('CTU, "Chengdu Shuangliu International Airport", Chengdu)
object HKG extends Airport('HKG, "Hong Kong International Airport", HongKong)

// in Italy
object MXP extends Airport('MXP, "Milan Malpensa International Airport", Milan)
object LIN extends Airport('LIN, "Milan Linate Airport", Milan)
object CIA extends Airport('CIA, "Rome Ciampino Airport", Rome)
object VCE extends Airport('VCE, "Marco Polo International Airport", Venice)
