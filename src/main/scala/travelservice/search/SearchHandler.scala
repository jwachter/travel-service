package travelservice.search

import travelservice.model._
import _root_.java.util.Date
import _root_.net.liftweb.mapper.{By, By_<,By_>}
import _root_.net.liftweb.util._
import _root_.org.joda.time._

object SearchHandler {
  def searchAllConnections(origin:Airport, destination:Airport, departure:Date):Seq[Itinerary]={
    def findConnections(start:List[Flight], visitedAirports:List[Airport], destination:Airport):Seq[Flight] = {
      val foo : Flight = start.head
      val bar : Flight = foo.followingFlight
      start match {
        case hd::Nil => Nil
        case hd::tl => Nil
        case _ => Nil
      }
    }
    
    // Flights departing in the date time window from our origin
    
    assert(departure after TimeHelpers.now)
    
    val startConnections = Flight.findAll(By(Flight.origin, origin), By_>(Flight.time, TimeHelpers.now), By_>(Flight.time, departure), By_<(Flight.time, new DateTime(departure).plusDays(3).toDate))
    
    findConnections(startConnections, List(origin), destination)   
    
    Nil
  }
}
