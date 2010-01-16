package travelservice.webclient.snippet

import _root_.java.text.SimpleDateFormat
import _root_.java.util.Date
import _root_.net.liftweb.http._
import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util.Helpers._ 
import _root_.scala.xml._

import travelservice.model._

class FlightTable
{
   val df = new SimpleDateFormat( "yyyy/mm/dd" )
   
  def oneWay( html : NodeSeq ) =
  {
    val from = S.request.open_!.params.get( "from" ).get.head
    val to = S.request.open_!.params.get( "to" ).get.head
    val departure = df.parse( S.request.open_!.params.get( "departure" ).get.head )
    
    println( "DEBUG: " + from + " " + to + " " + departure )
    
    val city = City.find(By(City.name, from)).open_!
    
    val ap = Airport.find( By( Airport._city, city) ).open_!
    
    val flights = Flight.findByDaysAndOrigin( departure, 3, ap )
    
    //bind(
      //"flight",
      //html,
      //"departure" -> Text( "bla1" ),
      //"origin" -> Text( "bla2" ),
      //"arrival" -> Text( "bla3" ),
      //"destination" -> Text( "bla4" )
    //)
  
    <ul>{flights.map(e => e.toXHTML)}</ul>
  }
  
  def roundtrip( html : NodeSeq ) =
  {
	  Nil
  }
  
  def multiSegment( html : NodeSeq ) =
  {
	  Nil
  }
}
