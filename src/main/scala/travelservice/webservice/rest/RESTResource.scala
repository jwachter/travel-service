package travelservice.webservice.rest

import _root_.net.liftweb.http._
import _root_.net.liftweb.common._

trait RESTResource {
	def initialize()
 
	def register()
 
	val handlers : Map[RequestType, (Req, String) => Box[LiftResponse]]

	def supportedContentTypes():List[String]
 
	private final def determineContentType(req:Req):Box[String]={
	  Empty
	}
 
	final def create(req:Req) : Box[LiftResponse] = {
		try {
			val handler = handlers(req.requestType)
			val contentType = determineContentType(req)
			if(supportedContentTypes.contains(contentType.open_!))
				Full(UnsupportedMediaTypeResponse())
			else
				contentType match {
				  case Full(contentType) => handler(req, contentType)
				  case _ => Full(NotAcceptableResponse())
				}
		} catch {
			case e:NoSuchElementException => Full(MethodNotAllowedResponse())
			case e:Exception => Full(InternalServerErrorResponse())
		}
	}
}
