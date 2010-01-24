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
package travelservice.helper

// Import Joda Time for Time handling.
import _root_.org.joda.time._
import _root_.org.joda.time.format._

// Import JDK classes.
import _root_.java.util.Date

//
// This object provides lots of helpful constants and helper methods for working with
// time periods.
//
object DateTimeHelpers {

  //
  // Different formatters for DateTimes.
  //
  private val DEFAULT_FORMAT = DateTimeFormat.forPattern("yyyy/MM/dd")
  private val ISO_FORMAT = ISODateTimeFormat.dateTime()
  private val PRETTY_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd '-' HH:mm") 
  
  //
  // Formatting helper methods using the defined formats.
  //
  def pretty(date:Date):String = pretty(new DateTime(date))
  def pretty(date:DateTime):String = date.toString(PRETTY_FORMAT)
  def iso(date:Date):String = iso(new DateTime(date))
  def iso(date:DateTime):String = date.toString(ISO_FORMAT)
  def default(date:Date):String = default(new DateTime(date))
  def default(date:DateTime):String = date.toString(DEFAULT_FORMAT)

  //
  // Parser helpers that can parse the different formats.
  //
  def fromPretty(date:String):DateTime = PRETTY_FORMAT.parseDateTime(date)
  def fromISO(date:String):DateTime = ISO_FORMAT.parseDateTime(date)
  def fromDefault(date:String):DateTime = DEFAULT_FORMAT.parseDateTime(date)
  
  //
  // Object that provides time period constants and methdos for constructing and working with them.
  //
  object Periods {
    def Hours(n:Int) = new Period(n, PeriodType.hours)
    lazy val Hour = Hours(1)
    lazy val Hours_12 = Hours(12)    
    lazy val Hours_24 = Hours(24) 
    lazy val Hours_48 = Hours(48) 
    lazy val Hours_72 = Hours(72)
    def Days(n:Int) = new Period(n, PeriodType.days)
    lazy val Day = Days(1)   
    lazy val Days_2 = Days(2)    
    lazy val Days_3 = Days(3)    
    lazy val Days_4 = Days(4)    
    lazy val Days_5 = Days(5)    
    lazy val Days_6 = Days(6)    
    lazy val Days_7 = Days(7) 
    def Weeks(n:Int) = new Period(n, PeriodType.weeks)
    lazy val Week = Weeks(1)    
    lazy val Weeks_2 = Weeks(2)    
    lazy val Weeks_3 = Weeks(3)    
    lazy val Weeks_4 = Weeks(4)
    def Months(n:Int) = new Period(n, PeriodType.months)
    lazy val Month = Months(1)
    def Years(n:Int) = new Period(n, PeriodType.years)
    
    //
    // Returns a range of a Date +/- the period.
    //
    def +/-(date:DateTime, period:Period):(DateTime, DateTime) = (date.minus(period), date.plus(period))
    
    //
    // Checks if a Date is inside a given range.
    //
    def inRange(date:DateTime, range:(DateTime, DateTime)):Boolean = range._1.isBefore(date) && date.isBefore(range._2) 
  }
  
}
