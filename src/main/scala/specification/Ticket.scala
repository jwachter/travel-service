package specification {

class Ticket(override val id:String, override val price:Int, override val payee:String, val itinerary:specification.Itinerary, val travelers:Seq[specification.Traveler]) extends Billable {

  def toXML = 
    <ticket id={ id }>
      { itinerary.toXML }
      <travelers>
        { for (traveler <- travelers) yield traveler.toXML }
      </travelers>
    </ticket>
}
}