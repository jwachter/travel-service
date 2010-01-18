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
  
	var from      = ""
  var to        = ""
  var departure = ""
  
  var origins      : List[String] = Nil
  var destinations : List[String] = Nil
  var departures   : List[String] = Nil
  
	def searchMultisegment( html : NodeSeq ) : NodeSeq =
	{
	  def addParamsAndSend()
	  {
      storeToLists()
	    S.redirectTo(
	      "multiSegmentResult.html?origins="
	    + origins.mkString( separator )
	    + "&destinations=" + destinations.mkString( separator )
	    + "&departures=" + departures.mkString( separator)
	    )
	  }
	  	  
    def storeToLists() : JsCmd =
    {
    		origins      = from      :: origins
    		destinations = to        :: destinations
    		departures   = departure :: departures
    		SetHtml( "multisegment", searchMultisegment( html ) )
    }
    
    def updateList( html : NodeSeq) : NodeSeq =
    {
      println( origins.mkString( "\n" ) )
      println( destinations.mkString( "\n" ) )
      println( departures.mkString( "\n" ) )
      
      bind(
        "query",
        html,
	      "segFrom"       -> <b>Test</b>,//Text( origins.mkString( "\n" ) ),
	      "segTo"         -> <b>Test</b>,//Text( destinations.mkString( "\n" ) ),
  	    "segDepartures" -> <b>Test</b>//Text( departures.mkString( "\n" ) )
      )
    }
    
//    def reDraw() = SetHtml( "results", updateResultTable() )
    
	  bind(
	    "query",
	    html,
	    "from"          -> SHtml.ajaxText( from, ( s ) => { from = s; Noop; }  ),
	    "to"            -> SHtml.ajaxText( to, ( t ) => { to = t; Noop; }  ),
	    "departure"     -> SHtml.ajaxText( departure, ( d ) => { departure = d; Noop; }/*, ("class", "datepicker")*/ ),
	    "addSeg"        -> SHtml.ajaxButton( "add segment", () => storeToLists() ),
	    "sendQuery"     -> submit( "search", () => addParamsAndSend() ),
	    "segFrom"       -> Text( origins.mkString( "\n" ) ),
	    "segTo"         -> Text( destinations.mkString( "\n" ) ),
	    "segDepartures" -> Text( departures.mkString( "\n" ) )
	  )
	}
}
