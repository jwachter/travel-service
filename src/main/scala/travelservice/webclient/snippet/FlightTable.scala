package travelservice.webclient.snippet

import _root_.java.text.SimpleDateFormat
import _root_.java.util.Date
import _root_.net.liftweb.http._
import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util.Helpers._
import _root_.org.joda.time._ 
import _root_.scala.xml._

import travelservice.model._
import travelservice.helper._
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

    its.flatMap(i => ItineraryHelper.itineraryToXHTML(i))
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

    its.flatMap(i => ItineraryHelper.itineraryToXHTML(i))
  }
  
  def multisegment( html : NodeSeq ) =
  {
    
	  val origins = S.request.open_!.params.get( "origins" ).get.head.split( separator ).toList.reverse
	  val destinations = S.request.open_!.params.get( "destinations" ).get.head.split( separator ).toList.reverse
	  val departures = S.request.open_!.params.get( "departures" ).get.head.split( separator ).toList.reverse
	  
	  val param = origins.zip( destinations ).zip( departures ).map( ( e ) => ( e._1._1, e._1._2, e._2 ) )
	  
   
	  val real : Seq[(world.Airport, world.Airport, Date)] = param.map(e => (Airport.findByCode(e._1).open_!.toAirport, Airport.findByCode(e._2).open_!.toAirport, df.parse(e._3)))
   
	  println(real)
	  
    val its = lufthansa.searchMultisegment(real)
    
    its.flatMap(i => ItineraryHelper.itineraryToXHTML(i))
  }
}
