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
package travelservice

// Lift modules.
import _root_.net.liftweb.util._
import _root_.net.liftweb.http._
import _root_.net.liftweb.common._
import _root_.net.liftweb.mapper._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._

// Import Joda Time.
import _root_.org.joda.time._
import _root_.org.joda.time.format._

// Import application classes.
import travelservice.model._
import travelservice.webservice.resources._

//
// Boot class that sets up our Application.
//
class Boot extends Bootable{
  //
  // Main initializer method
  //
  def boot {
    // Make sure UTF-8 is used
    LiftRules.early append { _ setCharacterEncoding "UTF-8" }
    
    // Which packages are treated as sources for views and snippets.
    LiftRules.addToPackages("travelservice.webclient") 
    
    // Prepare the database connection when JDBC is available
    if (!DB.jndiJdbcConnAvailable_?)
      DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)

    // Prepend the Service classes.
    LiftRules.dispatch.prepend(SearchResource.dispatch)
    LiftRules.dispatch.prepend(TicketResource.dispatch)

    // Create or update the Schema information for the model classes
    // Remove comments for 1 run to initialize the database
    
    // Prevent bugs while database schema is not stable, start from blank!
    // Note: Use H2 Database for development to speedup startup and 
      
    Schemifier.destroyTables_!!(Log.infoF _, Airport, City, Flight, Ticket, TicketFlight, TicketTraveler, Traveler)
    
    Schemifier.schemify(true, Log.infoF _, Airport, City, Flight, Ticket, TicketFlight, TicketTraveler, Traveler)
    
    initialize()
  }
  
  //
  // Initialize our database.
  //
  def initialize() {
    
    // Initialize cities
    val Frankfurt = City.create.name("Frankfurt").country("Germany")
    Frankfurt.save
    val Berlin = City.create.name("Berlin").country("Germany")
    Berlin.save
    val Zurich = City.create.name("Zurich").country("Switzerland")
    Zurich.save
    val Toronto = City.create.name("Toronto").country("Canada")
    Toronto.save
    val Vancouver = City.create.name("Vancouver").country("Canada")
    Vancouver.save
    val Montreal = City.create.name("Montreal").country("Canada")
    Montreal.save
    val Tokyo = City.create.name("Tokyo").country("Japan")
    Tokyo.save
    val NewYork = City.create.name("New York City").country("USA")
    NewYork.save
    val Paris = City.create.name("Paris").country("France")
    Paris.save
    
    // Setup some airports    
    val FRA = Airport.create.code("FRA").name("Frankfurt International Airport")._city(Frankfurt).lat(50.026422).long(8.543125)
    FRA.save
    val TXL = Airport.create.code("TXL").name("Tegel International Airport")._city(Berlin).lat(52.559686).long(13.287711)
    TXL.save
    val CDG = Airport.create.code("CDG").name("Charles de Gaulle International Airport")._city(Paris).lat(49.012778).long(2.55)
    CDG.save
    val ZRH = Airport.create.code("ZRH").name("Zurich International Airport")._city(Zurich).lat(47.464722).long(8.549167)
    ZRH.save
    val YYZ = Airport.create.code("YYZ").name("Toronto Pearson International Airport")._city(Toronto).lat(43.677222).long(-79.630556)
    YYZ.save
    val YVR = Airport.create.code("YVR").name("Vancouver International Airport")._city(Vancouver).lat(50.026422).long(8.543125)
    YVR.save
    val YUL = Airport.create.code("YUL").name("Montreal-Pierre Elliott Trudeau International Airport")._city(Montreal).lat(45.470556).long(-73.740833)
    YUL.save
    val NRT = Airport.create.code("NRT").name("Narita International Airport")._city(Tokyo).lat(35.764722).long(140.386389)
    NRT.save
    val JFK = Airport.create.code("JFK").name("John F. Kennedy International Airport")._city(NewYork).lat(40.63975).long(-73.778925)
    JFK.save
    
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

    def randomPrice : Int = ((Math.random+0.1)*600).toInt
    
    // Generate Unique Flight Numbers
    var fn = 1
    
    for(i <- (1 to 365)){
      // FROM FRA
      Flight.create.number("LH"+fn)._origin(FRA)._destination(TXL).departure(TIME_FRA_TXL_1.plusDays(i).toDate).duration(1).price(randomPrice).save
      fn = fn + 1
      Flight.create.number("LH"+fn)._origin(FRA)._destination(TXL).departure(TIME_FRA_TXL_2.plusDays(i).toDate).duration(1).price(randomPrice).save
      fn = fn + 1
      Flight.create.number("LH"+fn)._origin(FRA)._destination(TXL).departure(TIME_FRA_TXL_3.plusDays(i).toDate).duration(1).price(randomPrice).save
      fn = fn + 1
      Flight.create.number("LH"+fn)._origin(FRA)._destination(JFK).departure(TIME_FRA_JFK_1.plusDays(i).toDate).duration(9).price(randomPrice).save
      fn = fn + 1
      Flight.create.number("LH"+fn)._origin(FRA)._destination(JFK).departure(TIME_FRA_JFK_2.plusDays(i).toDate).duration(9).price(randomPrice).save
      fn = fn + 1
      Flight.create.number("LH"+fn)._origin(FRA)._destination(YUL).departure(TIME_FRA_YUL.plusDays(i).toDate).duration(8).price(randomPrice).save
      fn = fn + 1
      Flight.create.number("LH"+fn)._origin(FRA)._destination(CDG).departure(TIME_FRA_CDG_1.plusDays(i).toDate).duration(1).price(randomPrice).save
      fn = fn + 1
      Flight.create.number("LH"+fn)._origin(FRA)._destination(CDG).departure(TIME_FRA_CDG_2.plusDays(i).toDate).duration(1).price(randomPrice).save
      fn = fn + 1
      Flight.create.number("LH"+fn)._origin(FRA)._destination(CDG).departure(TIME_FRA_CDG_3.plusDays(i).toDate).duration(1).price(randomPrice).save
      fn = fn + 1
      Flight.create.number("LH"+fn)._origin(FRA)._destination(YYZ).departure(TIME_FRA_YYZ_1.plusDays(i).toDate).duration(9).price(randomPrice).save
      fn = fn + 1
      Flight.create.number("LH"+fn)._origin(FRA)._destination(YYZ).departure(TIME_FRA_YYZ_2.plusDays(i).toDate).duration(9).price(randomPrice).save
      fn = fn + 1
      Flight.create.number("LH"+fn)._origin(FRA)._destination(NRT).departure(TIME_FRA_NRT.plusDays(i).toDate).duration(11).price(randomPrice).save
      fn = fn + 1
      Flight.create.number("LH"+fn)._origin(FRA)._destination(YVR).departure(TIME_FRA_YVR.plusDays(i).toDate).duration(11).price(randomPrice).save
      fn = fn + 1
      Flight.create.number("LH"+fn)._origin(FRA)._destination(ZRH).departure(TIME_FRA_ZRH_1.plusDays(i).toDate).duration(1).price(randomPrice).save
      fn = fn + 1
      Flight.create.number("LH"+fn)._origin(FRA)._destination(ZRH).departure(TIME_FRA_ZRH_2.plusDays(i).toDate).duration(1).price(randomPrice).save
      fn = fn + 1
      
      // origin TXL
      Flight.create.number("LH"+fn)._origin(TXL)._destination(FRA).departure(TIME_TXL_FRA_1.plusDays(i).toDate).duration(1).price(randomPrice).save
      fn = fn + 1
      Flight.create.number("LH"+fn)._origin(TXL)._destination(FRA).departure(TIME_TXL_FRA_2.plusDays(i).toDate).duration(1).price(randomPrice).save
      fn = fn + 1
      Flight.create.number("LH"+fn)._origin(TXL)._destination(CDG).departure(TIME_TXL_CDG.plusDays(i).toDate).duration(2).price(randomPrice).save
      fn = fn + 1
      Flight.create.number("LH"+fn)._origin(TXL)._destination(ZRH).departure(TIME_TXL_ZRH.plusDays(i).toDate).duration(1).price(randomPrice).save
      fn = fn + 1
    
      // origin CDG
      Flight.create.number("LH"+fn)._origin(CDG)._destination(TXL).departure(TIME_CDG_TXL.plusDays(i).toDate).duration(2).price(randomPrice).save
      fn = fn + 1
      Flight.create.number("LH"+fn)._origin(CDG)._destination(FRA).departure(TIME_CDG_FRA_1.plusDays(i).toDate).duration(1).price(randomPrice).save
      fn = fn + 1
      Flight.create.number("LH"+fn)._origin(CDG)._destination(FRA).departure(TIME_CDG_FRA_2.plusDays(i).toDate).duration(1).price(randomPrice).save
      fn = fn + 1
      Flight.create.number("LH"+fn)._origin(CDG)._destination(YUL).departure(TIME_CDG_YUL.plusDays(i).toDate).duration(7).price(randomPrice).save
      fn = fn + 1
      Flight.create.number("LH"+fn)._origin(CDG)._destination(YYZ).departure(TIME_CDG_YYZ.plusDays(i).toDate).duration(8).price(randomPrice).save
      fn = fn + 1
    
      // origin ZRH
      Flight.create.number("LH"+fn)._origin(ZRH)._destination(TXL).departure(TIME_ZRH_TXL.plusDays(i).toDate).duration(1).price(randomPrice).save
      fn = fn + 1
      Flight.create.number("LH"+fn)._origin(ZRH)._destination(FRA).departure(TIME_ZRH_FRA.plusDays(i).toDate).duration(1).price(randomPrice).save
      fn = fn + 1
      Flight.create.number("LH"+fn)._origin(ZRH)._destination(YYZ).departure(TIME_ZRH_YYZ.plusDays(i).toDate).duration(9).price(randomPrice).save
      fn = fn + 1
    
	  // origin YYZ
	  Flight.create.number("LH"+fn)._origin(YYZ)._destination(YUL).departure(TIME_YYZ_YUL_1.plusDays(i).toDate).duration(1).price(randomPrice).save
	  fn = fn + 1
	  Flight.create.number("LH"+fn)._origin(YYZ)._destination(YUL).departure(TIME_YYZ_YUL_2.plusDays(i).toDate).duration(1).price(randomPrice).save
	  fn = fn + 1
	  Flight.create.number("LH"+fn)._origin(YYZ)._destination(CDG).departure(TIME_YYZ_CDG.plusDays(i).toDate).duration(8).price(randomPrice).save
	  fn = fn + 1
	  Flight.create.number("LH"+fn)._origin(YYZ)._destination(YVR).departure(TIME_YYZ_YVR.plusDays(i).toDate).duration(5).price(randomPrice).save
	  fn = fn + 1
    
	  // origin YVR
	  Flight.create.number("LH"+fn)._origin(YVR)._destination(FRA).departure(TIME_YVR_FRA.plusDays(i).toDate).duration(10).price(randomPrice).save
	  fn = fn + 1
	  Flight.create.number("LH"+fn)._origin(YVR)._destination(YUL).departure(TIME_YVR_YUL.plusDays(i).toDate).duration(5).price(randomPrice).save
	  fn = fn + 1
	  Flight.create.number("LH"+fn)._origin(YVR)._destination(YYZ).departure(TIME_YVR_YYZ.plusDays(i).toDate).duration(4).price(randomPrice).save
	  fn = fn + 1
    
	  // origin NRT
	  Flight.create.number("LH"+fn)._origin(NRT)._destination(FRA).departure(TIME_NRT_FRA.plusDays(i).toDate).duration(12).price(randomPrice).save
	  fn = fn + 1
    
	  // origin JFK
	  Flight.create.number("LH"+fn)._origin(JFK)._destination(FRA).departure(TIME_JFK_FRA.plusDays(i).toDate).duration(7).price(randomPrice).save
	  fn = fn + 1

	  // origin YUL
	  Flight.create.number("LH"+fn)._origin(YUL)._destination(FRA).departure(TIME_YUL_FRA.plusDays(i).toDate).duration(7).price(randomPrice).save
	  fn = fn + 1
	  Flight.create.number("LH"+fn)._origin(YUL)._destination(CDG).departure(TIME_YUL_CDG.plusDays(i).toDate).duration(7).price(randomPrice).save
	  fn = fn + 1
	  Flight.create.number("LH"+fn)._origin(YUL)._destination(YYZ).departure(TIME_YUL_YYZ.plusDays(i).toDate).duration(1).price(randomPrice).save
	  fn = fn + 1
	  Flight.create.number("LH"+fn)._origin(YUL)._destination(YVR).departure(TIME_YUL_YVR.plusDays(i).toDate).duration(6).price(randomPrice).save
	  fn = fn + 1
    }
  }
}

