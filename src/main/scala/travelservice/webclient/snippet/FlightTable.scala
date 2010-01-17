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
  val separator = ';'
  val df = new SimpleDateFormat( "yyyy/mm/dd" )
  val lufthansa = new Lufthansa()
   
  def oneway( html : NodeSeq ) =
  {
    val from = S.request.open_!.params.get( "from" ).get.head
    val to = S.request.open_!.params.get( "to" ).get.head
    val departure = df.parse( S.request.open_!.params.get( "departure" ).get.head )

    val oap = Airport.findByCode( from ).open_!
    val dap = Airport.findByCode( to ).open_!
    
    val its = lufthansa.searchOneway( oap.toAirport, dap.toAirport, departure )

    its.flatMap(
      i => bind(
        "flight",
        html,
        //"id" -> Text( i.id ),
        "departure" -> Text( i.departureDate.toString ),
        "origin" -> Text( i.origin.toString ),
        "arrival" -> Text( ( new DateTime( i.departureDate ).plusMinutes( i.duration ) ).toString ),
        "destination" -> Text( i.destination.toString ),
        "price" -> Text( i.price.toString)
      )
	)
  }
  
  def roundtrip( html : NodeSeq ) =
  {
    val from = S.request.open_!.params.get( "from" ).get.head
    val to = S.request.open_!.params.get( "to" ).get.head
    val departure = df.parse( S.request.open_!.params.get( "departure" ).get.head )
    val returnDate = df.parse( S.request.open_!.params.get( "returnDate" ).get.head )

    val oap = Airport.findByCode( from ).open_!
    val dap = Airport.findByCode( to ).open_!
    
    val its = lufthansa.searchRoundtrip( oap.toAirport, dap.toAirport, departure, returnDate )

    its.flatMap(
      i => bind(
        "flight",
        html,
        //"id" -> Text( i.id ),
        "departure" -> Text( i.departureDate.toString ),
        "origin" -> Text( i.origin.toString ),
        "returnDate" -> Text( ( new DateTime( i.departureDate ).plusMinutes( i.duration ) ).toString ),
        "destination" -> Text( i.destination.toString ),
        "price" -> Text( i.price.toString)
      )
	  )
  }
  
  def multisegment( html : NodeSeq ) =
  {
    //println( S.request.open_!.headers.toString )
    
	  val origins = S.request.open_!.params.get( "origins" ).get.head.split( separator ).toList
	  val destinations = S.request.open_!.params.get( "destinations" ).get.head.split( separator ).toList
	  val departures = S.request.open_!.params.get( "departures" ).get.head.split( separator ).toList
	  
	  val param = origins.zip( destinations ).zip( departures ).map( ( e ) => ( e._1._1, e._1._2, e._2 ) )
	  
    val its = lufthansa.searchMultisegment( param )
  }
}
