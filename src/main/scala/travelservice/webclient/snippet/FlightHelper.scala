package travelservice.webclient.snippet

import _root_.net.liftweb.mapper._
import travelservice.model._
import lufthansa._
import world._
import java.util.Date
import _root_.org.joda.time._

class FlightHelper {
    def showCount = <b>Airports: {travelservice.model.Airport.findAll.size} Flights:{Flight.findAll.size}</b>

    def getContent =         
        {
            val lufthansa = new Lufthansa()
            val its = lufthansa.searchOneway (FRA, YVR, new DateMidnight().toDate)

            its map (it => it.toXML) //<li><b>Flight</b>{it.origin} to {it.destination} in {it.duration} hours segments: {it.segments} price is {it.price}</li>

            //<li><b>Flight</b> </li> )
        }
                     


    def showTest = {
        
        <ul>{getContent}</ul>
    } 
}