package lufthansa {

import airline._
import world._
import _root_.java.util.Date

trait Lufthansa extends Airline {
  def searchOneway(origin: Place, destination: Place, date: Date): Seq[Itinerary]

  def searchRoundtrip(origin: Place, destination: Place, departureDate: Date, returnDate: Date): Seq[Itinerary]

  override def searchMultisegment(trips: Seq[(Airport, Airport, Date)]): Seq[Itinerary]

}
}