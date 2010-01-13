package travelservice.webservice.resources

import _root_.net.liftweb.http._
import _root_.net.liftweb.common._

import travelservice.webservice.rest._

object FlightsResource extends RESTResource{
  
	def supportedContentTypes = List("json","xml")
 
	/*val handlers = Map(
		(GetRequest, (req:Req,contentType:String)=>Empty)
	)*/
 
	def initialize() {
	  // Nothing special to initialize here
	}
 
	def register() {
	  
	}
}
