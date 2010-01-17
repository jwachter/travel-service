package travelservice.webclient.snippet

import _root_.net.liftweb.mapper._
import travelservice.model._

import world._
import java.util.Date
import org.joda.time._


class FlightHelper {
    def showCount = <b>{Flight.findAll.size}</b>

    def testXML = <div>
        {
            val lf = new lufthansa.Lufthansa ()
            val today = new Date
            val tomorrow = new DateTime (today).plusDays (1).toDate

            for (it <- lf.searchRoundtrip(world.FRA, world.YVR, today, tomorrow)){
                it.toXML
            }

        }

                  </div>

}
