package travelservice.webservice.resources

import _root_.net.liftweb.common._
import _root_.net.liftweb.http._

import travelservice.webservice.rest._

object AirportResource extends RESTResource{
	
	override val dispatch : LiftRules.DispatchPF = {
	  case r@Req("api" :: "airport" :: _, _, GetRequest) => () => process(r)
	  case r@Req("api" :: "airport" :: _, _, PostRequest) => () => process(r)
	  case r@Req("api" :: "airport" :: _, _, PutRequest) => () => process(r)
	  case r@Req("api" :: "airport" :: _, _, DeleteRequest) => () => process(r)
	}
 
 	override val supportedContentTypes = List("xml","json")
  
}
