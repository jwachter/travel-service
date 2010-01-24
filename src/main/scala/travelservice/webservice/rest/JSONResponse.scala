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
import _root_.net.liftweb.json._
import _root_.net.liftweb.http._

//
// Simple class for providing JSON Responses.
//
object JSONResponse {
  //
  // Create with message and OK Status
  //
  def apply(text: String): JSONResponse = JSONResponse(text, Nil, 200)
 
  //
  // Create with message and response code.
  //
  def apply(text: String, code: Int): JSONResponse = JSONResponse(text, Nil, code)
}

//
// LiftResponse for JSON content.
//
case class JSONResponse(renderedJson: String, headers: List[(String, String)], code: Int) extends LiftResponse {
  def toResponse = {
    val bytes = renderedJson.getBytes("UTF-8")
    InMemoryResponse(bytes, ("Content-Length", bytes.length.toString) :: ("Content-Type", "application/json; charset=utf-8") :: headers, Nil, code)
  }
}
