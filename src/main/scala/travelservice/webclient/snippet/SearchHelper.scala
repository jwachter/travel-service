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
import _root_.scala.xml.NodeSeq._

import travelservice.model._

/**
 * Author:  Johannes Potschies, jpotschies@googlemail.com
 * Date:    2010/01/21
 * Project: FPUS - Airline Reservation System
 *
 * <i>SearchHelper</i> is used to handle user input for multisegment flights. In
 * comparison to the other two forms, the input form for multisegment flights
 * consists of Ajax elements, which allow the user to enter several segments.
 * After every input, the new entry is stored in lists for origin, destination
 * and departure of the flight. Therefore, <i>var</i>s had to be used: The
 * sequential input left no other choice.
 *
 * When the user submits the form, parameters are sent via GET.
 *
 * Todo:
 *  - JavaScript Datepicker doesn't seem to work. When integrating it in this
 *    form, parameters won't be passed to their helper <i>var</i>s.
 */
class SearchHelper {
  // parse the date input
  val df = new SimpleDateFormat( "yyyy/mm/dd" )
  // the separator for the parameter list elements when passing the query via
  // GET
  val separator = ";"

  // helper vars to clarify the code below
	var from      = ""
  var to        = ""
  var departure = ""

  // the lists containing the parameters which are provided sequentially by the
  // user
  var origins      : List[String] = Nil
  var destinations : List[String] = Nil
  var departures   : List[String] = Nil

  /**
   * The actual snippet function which is called from index.html. The snippet
   * contains two nested functions which are passed to the button generators to
   * be called when the buttons are clicked.
   *
   * The function uses the binding mechanism to read from the form and to fill
   * the segment table below with information dynamically.
   */
	def searchMultisegment( html : NodeSeq ) : NodeSeq =
	{
    /**
     * Finally add the parameters one last time to the lists and redirect to the
     * result page. Append the parameters (separated by <i>separator</i>) to the
     * url.
     */
	  def addParamsAndSend()
	  {
      storeToLists()
	    S.redirectTo(
	      "multiSegmentResult.html?origins="
	    + origins.mkString( separator )
	    + "&destinations=" + destinations.mkString( separator )
	    + "&departures=" + departures.mkString( separator )
	    )
	  }

    /**
     * Prepend the parameters which were entered to the appropriate list.
     * Recursively recall the snippet method which will lead to another call of
     * the binding function. Like this, the segment table is updated with the
     * currently entered flight parameters.
     */
    def storeToLists() : JsCmd =
    {
    		origins      = origins ::: List( from )
    		destinations = destinations ::: List( to )
    		departures   = departures ::: List( departure )
    		SetHtml( "multisegment", searchMultisegment( html ) )
    }

    // Use Ajax-textfields, but use Noop as returning function as we only need
    // to assign the parameters.
    // Create segment table by using a HTML list. There is no other way to get
    // the segment entries one below the other. Use flatMap to combine all
    // contained strings with XML tags and immediately create a NodeSeq from
    // the list containing the new XML elements.
    // The datepicker attribute is currently commented out. There seems to be a
    // bug in the Lift framework which leads to misbehaviour, when the attribute
    // is activated.
    bind(
	    "query",
	    html,
	    "from"          -> SHtml.ajaxText( from, ( s ) => { from = s; Noop; }  ),
	    "to"            -> SHtml.ajaxText( to, ( t ) => { to = t; Noop; }  ),
	    "departure"     -> SHtml.ajaxText( departure, ( d ) => { departure = d; Noop; }/*, ("class", "datepicker")*/ ),
	    "addSeg"        -> SHtml.ajaxButton( "add segment", () => storeToLists() ),
	    "sendQuery"     -> submit( "search", () => addParamsAndSend() ),
	    "segFrom"       -> <ul type="none">{fromSeq( origins.flatMap( ( o ) => <li>{ o }</li> ) )}</ul>,
	    "segTo"         -> <ul type="none">{fromSeq( destinations.flatMap( ( d ) => <li>{ d }</li> ) )}</ul>,
	    "segDepartures" -> <ul type="none">{fromSeq( departures.flatMap( ( d ) => <li>{ d }</li> ) )}</ul>
	  )
	}
}
