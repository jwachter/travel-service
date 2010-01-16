package travelservice.webservice.rest

import _root_.net.liftweb.json._
import _root_.net.liftweb.http._

object JSONResponse {
  def apply(text: String): JSONResponse = JSONResponse(text, Nil, 200)
 
  def apply(text: String, code: Int): JSONResponse = JSONResponse(text, Nil, code)
}
 
case class JSONResponse(renderedJson: String, headers: List[(String, String)], code: Int) extends LiftResponse {
  def toResponse = {
    val bytes = renderedJson.getBytes("UTF-8")
    InMemoryResponse(bytes, ("Content-Length", bytes.length.toString) :: ("Content-Type", "application/json; charset=utf-8") :: headers, Nil, code)
  }
}
