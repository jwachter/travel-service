package travelservice.webclient.snippet

import _root_.java.text.SimpleDateFormat
import _root_.java.util.Date
import _root_.net.liftweb.http._
import _root_.net.liftweb.common._
import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util.Helpers._
import _root_.org.joda.time._ 
import _root_.scala.xml._

import travelservice.model._
import travelservice.helper._
import lufthansa._
import _root_.org.joda.time._
import _root_.org.joda.time.format._

class FlightTable
{
  val separator = ';'
  val df = DateTimeFormat.forPattern("yyyy/MM/dd");
  val lufthansa = new Lufthansa()
   
  def oneway( html : NodeSeq ) =
  {
    val from = S.request.open_!.params.get( "from" ).get.head
    val to = S.request.open_!.params.get( "to" ).get.head
    val departure = df.parseDateTime( S.request.open_!.params.get( "departure" ).get.head )

    val oap = Airport.findByCode( from )
    val dap = Airport.findByCode( to )
    
    var its = (oap, dap) match {
	    case (Full(orig), Full(dest)) => lufthansa.searchOneway(orig.toAirport, dest.toAirport, departure.toDate)      
      case (Empty, Full(dest)) => lufthansa.searchOneway( City.findByName(from).open_!.toCity, dest.toAirport, departure.toDate) 
      case (Full(orig), Empty) => lufthansa.searchOneway( orig.toAirport, City.findByName(to).open_!.toCity, departure.toDate)
      case (_, _) => lufthansa.searchOneway( City.findByName(from).open_!.toCity, City.findByName(to).open_!.toCity, departure.toDate)
    }

    its.flatMap(i => ItineraryHelper.itineraryToXHTML(i))
  }
  
  def roundtrip( html : NodeSeq ) =
  {
    val from = S.request.open_!.params.get( "from" ).get.head
    val to = S.request.open_!.params.get( "to" ).get.head
    val departure = df.parseDateTime( S.request.open_!.params.get( "departure" ).get.head ).toDate
    val returnDate = df.parseDateTime( S.request.open_!.params.get( "returnDate" ).get.head ).toDate

 
    val oap = Airport.findByCode( from )
    val dap = Airport.findByCode( to )

    var its = (oap, dap) match {
	  case (Full(orig), Full(dest)) => lufthansa.searchRoundtrip(orig.toAirport, dest.toAirport, departure, returnDate)      
      case (Empty, Full(dest)) => lufthansa.searchRoundtrip( City.findByName(from).open_!.toCity, dest.toAirport, departure, returnDate ) 
      case (Full(orig), Empty) => lufthansa.searchRoundtrip( orig.toAirport, City.findByName(to).open_!.toCity, departure, returnDate )
      case (_, _) => lufthansa.searchRoundtrip( City.findByName(from).open_!.toCity, City.findByName(to).open_!.toCity, departure, returnDate )
    }

    its.flatMap(i => ItineraryHelper.itineraryToXHTML(i))
  }
  
  def multisegment( html : NodeSeq ) =
  {
	  def airportOrCity(id1:String, id2:String):(world.Place, world.Place) = {
	    val oap = Airport.findByCode( id1 )
	    val dap = Airport.findByCode( id2 )
	
	    var its = Nil;
	    
	    (oap, dap) match {
	      case (Full(orig), Full(dest)) => (orig.toAirport, dest.toAirport)
	      case (Empty, Full(dest)) => (City.find(id1).open_!.toCity, dest.toAirport) 
	      case (Full(orig), Empty) => (orig.toAirport, City.find(id2).open_!.toCity)
	      case (_, _) => ( City.find(id1).open_!.toCity, City.find(id2).open_!.toCity)
	    }		  
	  }
   
	  val origins = S.request.open_!.params.get( "origins" ).get.head.split( separator ).toList.reverse
   
	  val destinations = S.request.open_!.params.get( "destinations" ).get.head.split( separator ).toList.reverse
   
	  val departures = S.request.open_!.params.get( "departures" ).get.head.split( separator ).toList.reverse
   
	  val param = origins.zip( destinations ).zip( departures ).map( ( e ) => ( e._1._1, e._1._2, e._2 ) )
   
	  val real : Seq[(world.Airport, world.Airport, Date)] = param.map(e => (Airport.findByCode(e._1).open_!.toAirport, Airport.findByCode(e._2).open_!.toAirport, df.parseDateTime(e._3).toDate))
   
    val its = lufthansa.searchMultisegment(real)
    
    its.flatMap(i => ItineraryHelper.itineraryToXHTML(i))
  }
}
