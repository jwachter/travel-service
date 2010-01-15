package travelservice.webclient.snippet

import _root_.java.text.SimpleDateFormat
import _root_.java.util.Date
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.SHtml._
import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util.Helpers._
import _root_.scala.xml._
import travelservice.model._

class SearchHelper {
	def searchOneWay( html : NodeSeq ) =
	{
	  var from = ""
	  var to = ""
	  var time = ""
	 	  
	  bind(
	    "query",
	    html,
	    "from" -> SHtml.text( "", S.setHeader( "origin", _ ) ),
	    "to" -> SHtml.text( "", S.setHeader( "destination", _ ) ),
	    "departure" -> SHtml.text( "", S.setHeader( "departure", _ ) ) % ( "class" -> "datepicker" ),
	    "submit" -> submit( "search", () => {println( S.request.open_!.headers ); S.redirectTo( "oneWayResult.html" ) })
	  )
	}
	
	def searchRoundtrip( html : NodeSeq ) =
	{
	  var from = ""
	  var to = ""
	  var departure = ""
	  var returnDate = ""
	 	  
	  bind(
	    "query",
	    html,
	    "from" -> SHtml.text( "", from = _ ),
	    "to" -> SHtml.text( "", to = _ ),
	    "departure" -> SHtml.text( "", departure = _ ) % ( "class" -> "datepicker" ),
	    "returnDate" -> SHtml.text( "", returnDate = _ ) % ( "class" -> "datepicker" ),
	    "submit" -> submit( "search", () => S.redirectTo( "roundtripResult.html" ) )
	  )
	}
	
	def searchMultiSegment( html : NodeSeq ) =
	{
      var from = ""
	  var to = ""
	  var departure = ""
	  var returnDate = ""
	 	  
	  bind(
	    "query",
	    html,
	    "from" -> SHtml.text( "", S.setHeader( "origin", _ ) ),
	    "to" -> SHtml.text( "", S.setHeader( "destination", _ ) ),
	    "departure" -> SHtml.text( "", S.setHeader( "departure", _ ) ) % ( "class" -> "datepicker" ),
	    "submit" -> submit( "search", () => S.redirectTo( "multiSegmentResult.html" ) )
	  )
    }
}
