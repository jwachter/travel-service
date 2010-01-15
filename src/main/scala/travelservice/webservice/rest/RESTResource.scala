package travelservice.webservice.rest

import _root_.net.liftweb.http._
import _root_.net.liftweb.common._

/*
 * Trait for simplifying the implementation of RESTful Resources
*/
trait RESTResource {
	// Initialize the resource
	def initialize()
 
	// Register urls, ...
	def register()
 
	// Define the handle function for the different request types
	def get : (Req, String) => Box[LiftResponse]
    def put : (Req, String) => Box[LiftResponse]
    def post : (Req, String) => Box[LiftResponse]
    def delete : (Req, String) => Box[LiftResponse]

    // The content types supported by this Resource
	def supportedContentTypes():List[String]
 
	private final def determineContentType(req:Req):Box[String]={
	  Empty
	}
 
	final def create(req:Req) : Box[LiftResponse] = {
		try {
			//val handler = handlers(req.requestType)
			val contentType = determineContentType(req)
			if(supportedContentTypes.contains(contentType.open_!))
				Full(UnsupportedMediaTypeResponse())
			else
				contentType match {
				  //case Full(contentType) => handler(req, contentType)
				  case _ => Full(NotAcceptableResponse())
				}
		} catch {
			case e:NoSuchElementException => Full(MethodNotAllowedResponse())
			case e:Exception => Full(InternalServerErrorResponse())
		}
	}
}
