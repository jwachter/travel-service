/**
 * Copyright 2010 Johannes Wachter, Marcus Körner, Johannes Potschies, Jeffrey Groneberg, Sergej Jakimcuk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package travelservice.webservice.rest

// Lift modules.
import _root_.net.liftweb.http._
import _root_.net.liftweb.common._

//
// Base trait for simple implementation of REST apis.
//
trait RESTResource {
	//
	// Setup what urls match the service
	//
	val dispatch : LiftRules.DispatchPF
  
	//
	// Define the handle function for the different request types
	//
	val get : (Req, String) => Box[LiftResponse] = (r, c) => Full(MethodNotAllowedResponse())
    val put : (Req, String) => Box[LiftResponse] = (r, c) => Full(MethodNotAllowedResponse())
    val post : (Req, String) => Box[LiftResponse] = (r, c) => Full(MethodNotAllowedResponse())
    val delete : (Req, String) => Box[LiftResponse] = (r, c) => Full(MethodNotAllowedResponse())

    //
    // The content types supported by this Resource
    //
	val supportedContentTypes:List[String]
 
	//
	// Determines the content type (XML or JSON) via several checks and preconditions.
	//
	private final def determineContentType(req:Req):Box[String]={
	  req match {
		// GetRequest: Context overrules Accept Header (if present)
	    case r@Req(_,contentType,GetRequest) => decide(contentType, "Accept")
	    case r@Req(_,contentType,DeleteRequest) => decide(contentType, "Accept")
	    case r@Req(_,contentType,_) => decide(contentType, "Content-Type")
	    case _ => Full("xml")  
	  }   
	}
 
    //
    // Final decider what the current content type is.
    //
	private final def decide(contentType:String, header:String):Box[String]={
	  contentType match {
	      case "json" => Full("json")
	      case "xml" => Full("xml")
	      case _ => S.getRequestHeader(header) match {
	        case Full("application/json") => Full("json")
	        case Full("application/x-json") => Full("json")
	        case Full("text/json") => Full("json")
	        case Full("application/xml") => Full("xml")
	        case Full("text/xml") => Full("xml")
	        case _ => Full("xml")
	      }
	    }
	}
 
    //
    // Basic request handling calls the right method implementation and handles
    // some errors early without the need to implement them again.
    //
	final def process(req:Req) : Box[LiftResponse] = {
		try {
			val requestType = req.requestType
			val contentType = determineContentType(req)
			if(contentType.open_! == "xml"){
				// Dispatch dependent on request type
				requestType match {
				case GetRequest => get(req, contentType.open_!)
				case PostRequest => post(req, contentType.open_!)
				case PutRequest => put(req, contentType.open_!)
				case DeleteRequest => delete(req, contentType.open_!)
				case _ => Full(MethodNotAllowedResponse())
				}
			} else {
				Full(UnsupportedMediaTypeResponse())
		    }
		} catch {
			case e:NoSuchElementException => e.printStackTrace; Full(MethodNotAllowedResponse())
			case e:Exception => e.printStackTrace; Full(InternalServerErrorResponse())
		}
	}
}
