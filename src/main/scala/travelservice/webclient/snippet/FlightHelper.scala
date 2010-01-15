
package travelservice.webclient.snippet

import _root_.net.liftweb.mapper._
import travelservice.model._
import lufthansa._
import world._
import java.util.Date
import _root_.org.joda.time._

// Helper Snippet to test some behaviour.
class FlightHelper {
	// Show how many flights we have overall
    def showCount = <b>Airports: {travelservice.model.Airport.findAll.size} Flights:{Flight.findAll.size}</b>

    def getContent =         
        {	
    		// Init our Airline
            val lufthansa = new Lufthansa()
            
            // Search all connections between to airports. From today (midnight)
            val its = lufthansa.searchOneway (FRA, YVR, new DateMidnight().toDate)
            
            // output the xml representations of all found routes
            its map (it => it.toXML) //<li><b>Flight</b>{it.origin} to {it.destination} in {it.duration} hours segments: {it.segments} price is {it.price}</li>

            //<li><b>Flight</b> </li> )
        }
                     

    // Test
    def showTest = {
        
        <ul>{getContent}</ul>
    } 
}