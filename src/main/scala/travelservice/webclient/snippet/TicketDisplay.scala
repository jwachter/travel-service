package travelservice.webclient.snippet

import _root_.scala.xml.NodeSeq
import _root_.net.liftweb.http._
import _root_.net.liftweb.util._
import _root_.net.liftweb.mapper._
import _root_.net.liftweb.common._
import Helpers._

import travelservice.model._

class TicketDisplay {
	def show(xhtml:NodeSeq):NodeSeq={
	  
	  S.param("id") match {
	    case Full(ident) => {
	    	val ticket = Ticket.find(By(Ticket.uid, ident))
	    	ticket match {
	    	  case Full(t) => {
	    		  bind("ticket", xhtml, "main" -> t.uid, "travelers"->t.travelers.get.map(e => e.toXHTML), "flights" -> t.flights.get.map(e => e.toXHTMLTable), "price" -> t.price, "paymentStatus" -> t.paymentStatus)	    	    
	    	  }
	    	  case _ => S.error("Ticket not found.");S.redirectTo("/index.html")
	    	}
	    }
	    case _ => S.error("No ID specified.");S.redirectTo("/index.html") 
	  }
	  
	}
}
