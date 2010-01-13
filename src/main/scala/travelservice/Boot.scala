/**
 * Copyright 2010 Johannes Wachter
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
package travelservice

import _root_.net.liftweb.util._
import _root_.net.liftweb.http._
import _root_.net.liftweb.mapper._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import _root_.org.joda.time._
import Helpers._

import travelservice.model._

/**
  * A class that's instantiated early and run.  It allows the application
  * to modify lift's environment
  */
class Boot extends Bootable{
  def boot {
    // where to search snippet
    LiftRules.addToPackages("travelservice.webservice")
    LiftRules.addToPackages("travelservice.webclient")

    // Build SiteMap
    val entries = Menu(Loc("Home", List("index"), "Home")) :: Nil
    LiftRules.setSiteMap(SiteMap(entries:_*))
    
    
    // Prepare the database connection when JDBC is available
    if (!DB.jndiJdbcConnAvailable_?)
      DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)

    // Create or update the Schema information for the model classes
    
    // Prevent bugs while database schema is not stable, start from blank!
    // Onky uncomment when experiencing problems during development
    // Schemifier.destroyTables_!!(Log.infoF _, Airport, Flight)
      
    Schemifier.destroyTables_!!(Log.infoF _, Airport, Flight)
    
    Schemifier.schemify(true, Log.infoF _, Airport, Flight)
    
    initialize()
  }
  
  def initialize() {
    // Setup some airports    
    val FRA = Airport.create.code("FRA").name("Frankfurt International Airport").city("Frankfurt").country("Germany").lat(50.026422).long(8.543125)
    FRA.save
    val TXL = Airport.create.code("TXL").name("Tegel International Airport").city("Berlin").country("Germany").lat(52.559686).long(13.287711)
    TXL.save
    val CDG = Airport.create.code("CDG").name("Charles de Gaulle International Airport").city("Paris").country("France").lat(49.012778).long(2.55)
    CDG.save
    val ZRH = Airport.create.code("ZRH").name("Zurich International Airport").city("Zurich").country("Switzerland").lat(47.464722).long(8.549167)
    ZRH.save
    val YYZ = Airport.create.code("YYZ").name("Toronto Pearson International Airport").city("Missisauga, Toronto").country("Canada").lat(43.677222).long(-79.630556)
    YYZ.save
    val YVR = Airport.create.code("YVR").name("Vancouver International Airport").city("Richmond, Vancouver").country("Canada").lat(50.026422).long(8.543125)
    YVR.save
    val NRT = Airport.create.code("NRT").name("Narita International Airport").city("Narita, Tokyo").country("Japan").lat(35.764722).long(140.386389)
    NRT.save
    val JFK = Airport.create.code("JFK").name("John F. Kennedy International Airport").city("New York City").country("USA").lat(40.63975).long(-73.778925)
    JFK.save
    val YUL = Airport.create.code("YUL").name("Montreal-Pierre Elliott Trudeau International Airport").city("Dorval, Montreal").country("Canada").lat(45.470556).long(-73.740833)
    YUL.save
    
    // Setup some Flight times
    
    // FROM FRA
    val TIME_FRA_TXL_1 = new DateTime(2010,1,1,8,0,0,0) // 1h
    val TIME_FRA_TXL_2 = new DateTime(2010,1,1,15,10,0,0) // 1h
    val TIME_FRA_TXL_3 = new DateTime(2010,1,1,18,50,0,0) // 1h
    val TIME_FRA_JFK_1 = new DateTime(2010,1,1,10,50,0,0) // 8.75h
    val TIME_FRA_JFK_2 = new DateTime(2010,1,1,16,50,0,0) // 8.25h
    val TIME_FRA_YUL = new DateTime(2010,1,1,11,10,0,0) // 8.2h
    val TIME_FRA_CDG_1 = new DateTime(2010,1,1,12,00,0,0) // 1.1h
    val TIME_FRA_CDG_2 = new DateTime(2010,1,1,16,10,0,0) // 1.1h
    val TIME_FRA_CDG_3 = new DateTime(2010,1,1,20,35,0,0) // 1.1h
    val TIME_FRA_YYZ_1 = new DateTime(2010,1,1,10,0,0,0) // 8.5h
    val TIME_FRA_YYZ_2 = new DateTime(2010,1,1,17,0,0,0) // 8.5h
    val TIME_FRA_NRT = new DateTime(2010,1,1,13,35,0,0) // 11h
    val TIME_FRA_YVR = new DateTime(2010,1,1,12,30,0,0) // 10.5h
    val TIME_FRA_ZRH_1 = new DateTime(2010,1,1,12,30,0,0) // 10.5h
    val TIME_FRA_ZRH_2 = new DateTime(2010,1,1,12,30,0,0) // 10.5h
    
    // FROM TXL
    val TIME_TXL_FRA_1 = new DateTime(2010,1,1,7,0,0,0) // 1.25h
    val TIME_TXL_FRA_2 = new DateTime(2010,1,1,13,45,0,0) // 1.25h
    val TIME_TXL_CDG = new DateTime(2010,1,1,17,25,0,0) // 1.75h
    val TIME_TXL_ZRH = new DateTime(2010,1,1,14,55,0,0) // 1.3h
    
    // FROM CDG
    val TIME_CDG_TXL = new DateTime(2010,1,1,19,50,0,0) // 1.5h
    val TIME_CDG_FRA_1 = new DateTime(2010,1,1,10,40,0,0) // 1.3h
    val TIME_CDG_FRA_2 = new DateTime(2010,1,1,15,20,0,0) // 1.3h
    val TIME_CDG_YUL = new DateTime(2010,1,1,13,40,0,0) // 7.4h
    val TIME_CDG_YYZ = new DateTime(2010,1,1,11,20,0,0) // 8.4h
    
    // FROM ZRH
    val TIME_ZRH_TXL = new DateTime(2010,1,1,7,25,0,0) // 1.5h
    val TIME_ZRH_FRA = new DateTime(2010,1,1,18,50,0,0) // 1h
    val TIME_ZRH_YYZ = new DateTime(2010,1,1,13,10,0,0) // 9.1h
    
    // FROM YYZ
    val TIME_YYZ_YUL_1 = new DateTime(2010,1,1,13,00,0,0) // 1.1h
    val TIME_YYZ_YUL_2 = new DateTime(2010,1,1,14,00,0,0) // 1.1h
    val TIME_YYZ_CDG = new DateTime(2010,1,1,20,15,0,0) // 7.5h
    val TIME_YYZ_YVR = new DateTime(2010,1,1,22,45,0,0) // 5.1h
    
    // FROM YVR
    val TIME_YVR_FRA = new DateTime(2010,1,1,15,35,0,0) // 10.1h
    val TIME_YVR_YUL = new DateTime(2010,1,1,8,45,0,0) // 4.75h
    val TIME_YVR_YYZ = new DateTime(2010,1,1,11,30,0,0) // 4.4h
    
    // FROM NRT
    val TIME_NRT_FRA = new DateTime(2010,1,1,10,20,0,0) // 11.75h
    
    // FROM JFK
    val TIME_JFK_FRA = new DateTime(2010,1,1,21,35,0,0) // 7.4h

    // FROM YUL
    val TIME_YUL_FRA = new DateTime(2010,1,1,18,40,0,0) // 7.4h
    val TIME_YUL_CDG = new DateTime(2010,1,1,20,0,0,0) // 6.75h
    val TIME_YUL_YYZ = new DateTime(2010,1,1,16,0,0,0) // 1.4h
    val TIME_YUL_YVR = new DateTime(2010,1,1,19,55,0,0) // 5.5h
    
    LiftRules.early append { _ setCharacterEncoding "UTF-8" }

    
    for(i <- (1 to 365)){
      // FROM FRA
      Flight.create.from(FRA).to(TXL).time(TIME_FRA_TXL_1.plusDays(i).toDate).length(1).save
      Flight.create.from(FRA).to(TXL).time(TIME_FRA_TXL_2.plusDays(i).toDate).length(1).save
      Flight.create.from(FRA).to(TXL).time(TIME_FRA_TXL_3.plusDays(i).toDate).length(1).save
      Flight.create.from(FRA).to(JFK).time(TIME_FRA_JFK_1.plusDays(i).toDate).length(8.75).save
      Flight.create.from(FRA).to(JFK).time(TIME_FRA_JFK_2.plusDays(i).toDate).length(8.25).save
      Flight.create.from(FRA).to(YUL).time(TIME_FRA_YUL.plusDays(i).toDate).length(8.2).save
      Flight.create.from(FRA).to(CDG).time(TIME_FRA_CDG_1.plusDays(i).toDate).length(1.1).save
      Flight.create.from(FRA).to(CDG).time(TIME_FRA_CDG_2.plusDays(i).toDate).length(1.1).save
      Flight.create.from(FRA).to(CDG).time(TIME_FRA_CDG_3.plusDays(i).toDate).length(1.1).save
      Flight.create.from(FRA).to(YYZ).time(TIME_FRA_YYZ_1.plusDays(i).toDate).length(8.5).save
      Flight.create.from(FRA).to(YYZ).time(TIME_FRA_YYZ_2.plusDays(i).toDate).length(8.5).save
      Flight.create.from(FRA).to(NRT).time(TIME_FRA_NRT.plusDays(i).toDate).length(11).save
      Flight.create.from(FRA).to(YVR).time(TIME_FRA_YVR.plusDays(i).toDate).length(10.5).save
      Flight.create.from(FRA).to(ZRH).time(TIME_FRA_ZRH_1.plusDays(i).toDate).length(1).save
      Flight.create.from(FRA).to(ZRH).time(TIME_FRA_ZRH_2.plusDays(i).toDate).length(1).save
      
      // FROM TXL
      Flight.create.from(TXL).to(FRA).time(TIME_TXL_FRA_1.plusDays(i).toDate).length(1.25).save
      Flight.create.from(TXL).to(FRA).time(TIME_TXL_FRA_2.plusDays(i).toDate).length(1.25).save
      Flight.create.from(TXL).to(CDG).time(TIME_TXL_CDG.plusDays(i).toDate).length(1.75).save
      Flight.create.from(TXL).to(ZRH).time(TIME_TXL_ZRH.plusDays(i).toDate).length(1.3).save
    
      // FROM CDG
      Flight.create.from(CDG).to(TXL).time(TIME_CDG_TXL.plusDays(i).toDate).length(1.5).save
      Flight.create.from(CDG).to(TXL).time(TIME_CDG_FRA_1.plusDays(i).toDate).length(1.3).save
      Flight.create.from(CDG).to(TXL).time(TIME_CDG_FRA_2.plusDays(i).toDate).length(1.3).save
      Flight.create.from(CDG).to(TXL).time(TIME_CDG_YUL.plusDays(i).toDate).length(7.4).save
      Flight.create.from(CDG).to(TXL).time(TIME_CDG_YYZ.plusDays(i).toDate).length(8.4).save
    
      // FROM ZRH
      Flight.create.from(ZRH).to(TXL).time(TIME_ZRH_TXL.plusDays(i).toDate).length(1.5).save
      Flight.create.from(ZRH).to(FRA).time(TIME_ZRH_FRA.plusDays(i).toDate).length(1).save
      Flight.create.from(ZRH).to(YYZ).time(TIME_ZRH_YYZ.plusDays(i).toDate).length(9.1).save
    
	  // FROM YYZ
	  Flight.create.from(YYZ).to(YUL).time(TIME_YYZ_YUL_1.plusDays(i).toDate).length(1.1).save
	  Flight.create.from(YYZ).to(YUL).time(TIME_YYZ_YUL_2.plusDays(i).toDate).length(1.1).save
	  Flight.create.from(YYZ).to(CDG).time(TIME_YYZ_CDG.plusDays(i).toDate).length(7.5).save
	  Flight.create.from(YYZ).to(YVR).time(TIME_YYZ_YVR.plusDays(i).toDate).length(5.1).save
    
	  // FROM YVR
	  Flight.create.from(YVR).to(FRA).time(TIME_YVR_FRA.plusDays(i).toDate).length(10.1).save
	  Flight.create.from(YVR).to(YUL).time(TIME_YVR_YUL.plusDays(i).toDate).length(4.75).save
	  Flight.create.from(YVR).to(YYZ).time(TIME_YVR_YYZ.plusDays(i).toDate).length(4.4).save
    
	  // FROM NRT
	  Flight.create.from(NRT).to(FRA).time(TIME_NRT_FRA.plusDays(i).toDate).length(11.75).save
    
	  // FROM JFK
	  Flight.create.from(JFK).to(FRA).time(TIME_JFK_FRA.plusDays(i).toDate).length(7.4).save

	  // FROM YUL
	  Flight.create.from(YUL).to(FRA).time(TIME_YUL_FRA.plusDays(i).toDate).length(7.4).save
	  Flight.create.from(YUL).to(CDG).time(TIME_YUL_CDG.plusDays(i).toDate).length(6.75).save
	  Flight.create.from(YUL).to(YYZ).time(TIME_YUL_YYZ.plusDays(i).toDate).length(1.4).save
	  Flight.create.from(YUL).to(YVR).time(TIME_YUL_YVR.plusDays(i).toDate).length(5.5).save

    }
  }
}

