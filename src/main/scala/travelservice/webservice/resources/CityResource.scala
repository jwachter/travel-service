package travelservice.webservice.resources

import _root_.net.liftweb.common._
import _root_.net.liftweb.http._

import travelservice.webservice.rest._

object CityResource extends RESTResource{
	
	override val dispatch : LiftRules.DispatchPF = {
	  case r@Req("api" :: "city" :: _, _, GetRequest) => () => process(r)
	  case r@Req("api" :: "city" :: _, _, PostRequest) => () => process(r)
	  case r@Req("api" :: "city" :: _, _, PutRequest) => () => process(r)
	  case r@Req("api" :: "city" :: _, _, DeleteRequest) => () => process(r)
	}
 
 	override val supportedContentTypes = List("xml","json")
  
}
