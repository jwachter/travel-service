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

/**
 * Author:  Johannes Potschies, jpotschies@googlemail.com
 * Date:    2010/01/21
 * Project: FPUS - Airline Reservation System
 *
 * <i>FlightTable</i> receives the parameters which were entered by the user
 * from the forms in index.html or from <i>SearchHelper</i>. Each function here
 * "opens" the incoming request and extracts the parameters.
 */
class FlightTable
{
  
  object ItineraryHolder extends SessionVar[Seq[specification.Itinerary]](Nil)
  
  // parameter lists will enter this class as a string which is separated by
  // this symbol.
  val separator = ';'
  val df = DateTimeFormat.forPattern( "yyyy/MM/dd" );
  // the Lufthansa object which contains the search algorithms.
  val lufthansa = new Lufthansa()

  /**
   * A single flight with possible connection flight is searched by using this
   * snippet. It uses the incoming parameters to create a list of results. These
   * results are furnished with some XML code and returned to the view, which
   * can now display it.
   */
  def oneway( html : NodeSeq ) =
  {
    // open request and extract parameters
    val from = S.request.open_!.params.get( "from" ).get.head
    val to = S.request.open_!.params.get( "to" ).get.head
    val departure = df.parseDateTime( S.request.open_!.params.get( "departure" ).get.head )

    // try to find an airport by the given parameters and interpret them as
    // airport codes at first.
    val oap = Airport.findByCode( from )
    val dap = Airport.findByCode( to )

    // depending on the current values of oap and dap, continue:
    // - if we have two proper airports now, call lufthansa.searchOneway( ... )
    //   and pass these airports
    // - if the origin parameter was no airport code, it has to be a city name
    //   --> try to find a city with this name and then call
    //       lufthansa.searchOneway( ... ) and provide the city name and the
    //       airport code
    // - proceed in the same way with the departure parameter
    // - if both parameters are not an airport code, try to find two city names.
    // The return value will be a list of possible itinaries.
    var its = ( oap, dap ) match {
	    case ( Full( orig ), Full( dest ) ) => lufthansa.searchOneway( orig.toAirport, dest.toAirport, departure.toDate )
      case ( Empty, Full( dest ) ) => lufthansa.searchOneway( City.findByName( from ).open_!.toCity, dest.toAirport, departure.toDate )
      case ( Full( orig ), Empty ) => lufthansa.searchOneway( orig.toAirport, City.findByName( to ).open_!.toCity, departure.toDate )
      case ( _, _ ) => lufthansa.searchOneway( City.findByName( from ).open_!.toCity, City.findByName( to ).open_!.toCity, departure.toDate )
    }
    
    its match {
      case seq if seq.size > 0 => ItineraryHolder.set(its)
      case _ => ItineraryHolder.set(Nil)
    }
    
    // convert the itinaries into XHTML code to display a result on the webpage
    its.flatMap( i => ItineraryHelper.itineraryToXHTML( i ) )
  }

  /**
   * Searching for a roundtrip flight is almost identical to the one way flight.
   * Unfortunately it is not possible to combine these two snippets, because
   * roundtrip needs one more attribute which it has to handle.
   */
  def roundtrip( html : NodeSeq ) =
  {
    // open request and get parameters
    val from = S.request.open_!.params.get( "from" ).get.head
    val to = S.request.open_!.params.get( "to" ).get.head
    val departure = df.parseDateTime( S.request.open_!.params.get( "departure" ).get.head ).toDate
    val returnDate = df.parseDateTime( S.request.open_!.params.get( "returnDate" ).get.head ).toDate

    // try to find an airport
    val oap = Airport.findByCode( from )
    val dap = Airport.findByCode( to )

    // continue according to the values of oap and dap
    var its = ( oap, dap ) match
    {
      case ( Full( orig ), Full( dest ) ) => lufthansa.searchRoundtrip( orig.toAirport, dest.toAirport, departure, returnDate )
      case ( Empty, Full( dest ) ) => lufthansa.searchRoundtrip( City.findByName( from ).open_!.toCity, dest.toAirport, departure, returnDate )
      case ( Full( orig ), Empty) => lufthansa.searchRoundtrip( orig.toAirport, City.findByName(to).open_!.toCity, departure, returnDate )
      case ( _, _ ) => lufthansa.searchRoundtrip( City.findByName( from ).open_!.toCity, City.findByName( to ).open_!.toCity, departure, returnDate )
    }

    its match {
      case seq if seq.size > 0 => ItineraryHolder.set(its)
      case _ => ItineraryHolder.set(Nil)
    }
    
    // return the whole list of intinaries as XHTML code.
    its.flatMap(i => ItineraryHelper.itineraryToXHTML(i))
  }

  /**
   * multisegment get
   */
  def multisegment( html : NodeSeq ) =
  {
	  def airportOrCity( id1:String, id2:String ):( specification.Place, specification.Place ) = {
	    val oap = Airport.findByCode( id1 )
	    val dap = Airport.findByCode( id2 )
	
	    var its = Nil;
	    
	    ( oap, dap ) match {
	      case ( Full( orig ), Full( dest)) => ( orig.toAirport, dest.toAirport )
	      case ( Empty, Full( dest ) ) => ( City.findByName( id1 ).open_!.toCity, dest.toAirport )
	      case ( Full( orig), Empty ) => ( orig.toAirport, City.findByName( id2 ).open_!.toCity )
	      case ( e1, e2 ) => ( City.findByName( id1 ).open_!.toCity, City.findByName( id2 ).open_!.toCity )
	    }		  
	  }
   
	  val origins = S.request.open_!.params.get( "origins" ).get.head.split( separator ).toList
   
	  val destinations = S.request.open_!.params.get( "destinations" ).get.head.split( separator ).toList
   
	  val departures = S.request.open_!.params.get( "departures" ).get.head.split( separator ).toList
   
	  val param = origins.zip( destinations ).map( ( e ) => airportOrCity( e._1, e._2 ) ).zip( departures ).map( ( e ) => ( e._1._1, e._1._2, e._2 ) )
   
	  val real : Seq[( specification.Place, specification.Place, Date )] = param.map(e => ( e._1, e._2, df.parseDateTime( e._3 ).toDate ) )
   
   
    val its = lufthansa.searchMultisegment( real )
    
    its match {
      case seq if seq.size > 0 => ItineraryHolder.set(its)
      case _ => ItineraryHolder.set(Nil)
    }

    <p>{its.flatMap( i => ItineraryHelper.itineraryToXHTML( i ) )}</p><a href="search.html">Check cache</a>
  }
  
  def checkCache(xhtml:NodeSeq)={
    val id = S.param("id").open_!
    
    <p><strong>{ItineraryHolder.is.size}</strong></p><p>{ItineraryHolder.is.filter(e => e.id == id).map(e => ItineraryHelper.itineraryToXHTML(e))}</p>
  }
}
