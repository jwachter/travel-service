package travelservice.webclient.snippet

import _root_.java.text.SimpleDateFormat
import _root_.java.util.Date
import _root_.net.liftweb.http._
import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util.Helpers._
import _root_.org.joda.time._ 
import _root_.scala.xml._

import travelservice.model._
import lufthansa._

class FlightTable
{
  val df = new SimpleDateFormat( "yyyy/mm/dd" )
  val lufthansa = new Lufthansa()
   
  def oneway( html : NodeSeq ) =
  {
    val from = S.request.open_!.params.get( "from" ).get.head
    val to = S.request.open_!.params.get( "to" ).get.head
    val departure = df.parse( S.request.open_!.params.get( "departure" ).get.head )
    
    println( "DEBUG: " + from + " " + to + " " + departure )
    
    //val ocity = City.find( By( City.name, from ) ).open_!    
    //val oap = Airport.find( By( Airport._city, ocity) ).open_!
    //val dcity = City.find( By( City.name, to ) ).open_!
    //val dap = Airport.find( By( Airport._city, dcity) ).open_!
    
    val oap = Airport.findByCode( from ).open_!
    val dap = Airport.findByCode( to ).open_!
    
    val its = lufthansa.searchOneway( oap.toAirport, dap.toAirport, departure )
    //val flights = Flight.findByDaysAndOrigin( departure, 3, ap )
    its.flatMap(
      i => bind(
        "flight",
        html,
        //"id" -> Text( i.id ),
        "departure" -> Text( i.departureDate.toString ),
        "origin" -> Text( i.origin.toString ),
        "arrival" -> Text( ( new DateTime( departure ).plusMinutes( i.duration ) ).toString ),
        "destination" -> Text( i.destination.toString ),
        "price" -> Text( i.price.toString)
      )
	)
  }
  
  def roundtrip( html : NodeSeq ) =
  {
	  Nil
  }
  
  def multisegment( html : NodeSeq ) =
  {
	  Nil
  }
}
