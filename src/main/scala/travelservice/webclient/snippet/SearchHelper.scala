package travelservice.webclient.snippet

import _root_.java.text.SimpleDateFormat
import _root_.java.util.Date
import _root_.net.liftweb.http._
import net.liftweb.http.js.{JsCmd}
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.SHtml._
import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util.Helpers._
import _root_.scala.xml._

import travelservice.model._

class SearchHelper {
  val df = new SimpleDateFormat( "yyyy/mm/dd" )
  val separator = ";"
  
	def searchMultisegment( html : NodeSeq ) =
	{
	  var from      = ""
	  var to        = ""
	  var departure = ""
	  
	  var origins      : List[String] = Nil
	  var destinations : List[String] = Nil
	  var departures   : List[String] = Nil
	  	  
	  def addParamsAndSend()
	  {
	    //println( "DEBUG:\n" + origins + "\n" + destinations + "\n" + departures )
	    S.setHeader( "origins", from )
	    S.setHeader( "destinations", to )
	    S.setHeader( "departures", departure )
	    S.redirectTo( "multiSegmentResult.html" )
	  }
	  	  
    def storeToLists() : JsCmd =
    {
      //origins      = from      :: origins
      //destinations = to        :: destinations
      //departures   = departure :: departures
      println( "origins: " + from )
      S.setHeader( "origins", from )
	    S.setHeader( "destinations", to )
	    S.setHeader( "departures", departure )
      Noop
    }
	 	  
	  bind(
	    "query",
	    html,
	    "from"      -> SHtml.ajaxText( from, ( s ) => { from = s; Noop; }  ),
	    "to"        -> SHtml.ajaxText( to, ( t ) => { to = t; Noop; }  ),
	    "departure" -> SHtml.ajaxText( departure, ( d ) => { departure = d; Noop; } ),
	    "addSeg"    -> SHtml.ajaxButton( "add segment", () => storeToLists() ),
	    "sendQuery" -> submit( "search", () => addParamsAndSend() )
	  )
	}
}
