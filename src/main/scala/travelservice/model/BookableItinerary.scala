package travelservice.model

import _root_.net.liftweb.mapper._

object BookableItinerary extends BookableItinerary with LongKeyedMetaMapper[BookableItinerary] {
	override def dbTableName = "bookable_itinerary"
}

class BookableItinerary extends LongKeyedMapper[BookableItinerary] with IdPK{
	def getSingleton = BookableItinerary
}
