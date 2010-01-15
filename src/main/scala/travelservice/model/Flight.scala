package travelservice.model

import airline._

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.mapper._
import _root_.net.liftweb.json.JsonAST._
import _root_.org.joda.time._
import _root_.java.util.Date

object Flight extends Flight with LongKeyedMetaMapper[Flight] {
  override def dbTableName = "flights"
  
  def findByHours(earliest:Date, hours:Int) = findInRange(earliest, new DateTime(earliest).plusHours(hours).toDate)
  def findByDays(earliest:Date, days:Int) = findInRange(earliest, new DateTime(earliest).plusDays(days).toDate)
  def findByDaysAndOrigin(earliest:Date, days:Int, origin:Airport) = findInRangeAndOrigin(earliest, new DateTime(earliest).plusDays(days).toDate, origin)


  def findByWeeks(earliest:Date, weeks:Int) = findInRange(earliest, new DateTime(earliest).plusWeeks(weeks).toDate)
  def findByMonths(earliest:Date, months:Int) = findInRange(earliest, new DateTime(earliest).plusMonths(months).toDate)
  def findInRange(from:Date, to:Date) = Flight.findAll(By_>(Flight.departure, from), By_<(Flight.departure, to))

  def findInRangeAndOrigin(from:Date, to:Date, origin:Airport) = Flight.findAll(By(Flight._origin, origin), By_>(Flight.departure, from), By_<(Flight.departure, to))
}

class Flight extends LongKeyedMapper[Flight] with IdPK{
	def getSingleton = Flight
 
	object number extends MappedString(this, 32)
	object _origin extends MappedLongForeignKey(this, Airport){
	  override def dbColumnName = "ref_origin"
	}
	def origin = _origin.obj.openOr(null)
	object _destination extends MappedLongForeignKey(this, Airport){
	  override def dbColumnName = "ref_destination"
	}
	def destination = _destination.obj.openOr(null)
	object departure extends MappedDateTime(this)
	object duration extends MappedInt(this)
 
	def toXML = 
		<flight>
		 <number>{this.number.is}</number>
		 <origin>{this.origin.code.is}</origin>
		 <destination>{this.destination.code.is}</destination>
		 // TODO format the date
		 <departure>{this.departure.is}</departure>
		 <duration>{this.duration.is}</duration>
		</flight>
 
	// TODO format the date
	def toJSON = JObject(List(JField("number", JString(this.number.is)),JField("origin", JString(this.origin.code.is)),JField("destination", JString(this.destination.code.is)),JField("departure", JString(this.departure.is.toString)),JField("duration", JString(this.duration.is.toString))))
 
	def toHop = new Hop(this.number.is, this.origin.toAirport, this.destination.toAirport, this.departure.is, this.duration.is)
 
	// TODO format date
	override def toString = "["+this.number.is+"] "+this.origin.code.is + " to " + this.destination.code.is+" ("+this.departure.is+" taking "+this.duration.is.toString+")"
}