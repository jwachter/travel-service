package travelservice.webservice.resources

import _root_.net.liftweb.common._
import _root_.net.liftweb.json._
import _root_.net.liftweb.json.JsonAST._
import _root_.net.liftweb.http._
import _root_.org.joda.time.{DateTime, DateMidnight}
import _root_.java.util.Date

import travelservice.model._
import travelservice.webservice.rest._

object FlightResource extends RESTResource{
	
	override val dispatch : LiftRules.DispatchPF = {
	  case r@Req("api" :: "flight" :: _, _, GetRequest) => () => process(r)
	  case r@Req("api" :: "flight" :: _, _, PostRequest) => () => process(r)
	  case r@Req("api" :: "flight" :: _, _, PutRequest) => () => process(r)
	  case r@Req("api" :: "flight" :: _, _, DeleteRequest) => () => process(r)
	}
 
 	override val supportedContentTypes = List("xml","json")

 	override val get = (r:Req, ct:String) => {
 	  r match {
 	    case req@Req("api" :: "flight" :: airportcode :: year :: month :: day :: hour :: Nil, _, _) => {
 	      val airport = Airport.findByCode(airportcode)
 	      airport match {
 	        case Full(airport) => {
	 	      val from : DateTime = new DateTime(year.toInt, month.toInt, day.toInt, hour.toInt, 0, 0, 0); 
	 	      val result = airport.findFlightsInDateRange(from.toDate, new DateMidnight(from.plusDays(1)).toDate)
	 	      ct match {
	 	    	case "json" => Full(JSONResponse(Printer.compact(JsonAST.render(JArray(result.map(it => it.toJSON))))))
	 	    	case "xml" => Full(XmlResponse(<flights>{result.map(it => it.toXML)}</flights>))
	 	      }
	        }
 	        case _ => Full(NotFoundResponse())
 	      } 
 	    }
 	    case req@Req("api" :: "flight" :: airportcode :: year :: month :: day :: Nil, _, _) => {
 	      val airport = Airport.findByCode(airportcode)
 	      airport match {
 	        case Full(airport) => {
	 	      val from : DateTime = new DateTime(year.toInt, month.toInt, day.toInt, 0, 0, 0, 0); 
	 	      val result = airport.findFlightsInDateRange(from.toDate, new DateMidnight(from.plusDays(1)).toDate)
	 	      ct match {
	 	    	case "json" => Full(JSONResponse(Printer.compact(JsonAST.render(JArray(result.map(it => it.toJSON))))))
	 	    	case "xml" => Full(XmlResponse(<flights>{result.map(it => it.toXML)}</flights>))
	 	      }
	        }
 	        case _ => Full(NotFoundResponse())
 	      }
 	    }
 	    case req@Req("api" :: "flight" :: airportcode :: year :: month :: Nil, _, _) => {
 	      val airport = Airport.findByCode(airportcode)
 	      println("Phase 1" + airport.toString)
 	      airport match {
 	        case Full(airport) => {
 	        	println("Phase 2a")
 	        	println(year)
 	        	println(month)
	 	      val from = new DateTime(year.toInt, month.toInt, 1, 0, 0, 0, 0); 
 	          println("Phase 2a")
	 	      val result = airport.findFlightsInDateRange(from.toDate, new DateMidnight(from.plusMonths(1)).toDate)
	 	      println("Phase 2b")
	 	      ct match {
	 	    	case "json" => println("Phase 3a");Full(JSONResponse(Printer.compact(JsonAST.render(JArray(result.map(it => it.toJSON))))))
	 	    	case "xml" => println("Phase 3b");Full(XmlResponse(<flights>{result.map(it => it.toXML)}</flights>))
	 	      }
	        }
 	        case _ => Full(NotFoundResponse())
 	      }
 	    }
 	    case req@Req("api" :: "flight" :: airportcode :: year :: Nil, _, _) => {
 	      val airport = Airport.findByCode(airportcode)
 	      airport match {
 	        case Full(airport) => {
	 	      val from : DateTime = new DateTime(year.toInt, 1, 1, 0, 0, 0, 0); 
	 	      val result = airport.findFlightsInDateRange(from.toDate, new DateMidnight(from.plusYears(1)).toDate)
	 	      ct match {
	 	    	case "json" => Full(JSONResponse(Printer.compact(JsonAST.render(JArray(result.map(it => it.toJSON))))))
	 	    	case "xml" => Full(XmlResponse(<flights>{result.map(it => it.toXML)}</flights>))
	 	      }
	        }
 	        case _ => Full(NotFoundResponse())
 	      }
 	    }
 	    case req@Req("api" :: "flight" :: airportcode :: Nil, _, _) => {
 	      val airport = Airport.findByCode(airportcode)
 	      airport match {
 	        case Full(airport) => {
	 	      val result = airport.findFlights
	 	      ct match {
	 	    	case "json" => Full(JSONResponse(Printer.compact(JsonAST.render(JArray(result.map(it => it.toJSON))))))
	 	    	case "xml" => Full(XmlResponse(<flights>{result.map(it => it.toXML)}</flights>))
	 	      }
	        }
 	        case _ => Full(NotFoundResponse())
 	      }
 	    }
 	    case req@Req("api" :: "flight" :: Nil, _, _) => Full(NotFoundResponse())
      case _ => Full(NotFoundResponse()) 
 	  }
    }
}
