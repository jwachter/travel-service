package specification {

trait Billable {
  val id: String    // globally unique identifier
  val price: Int    // to make it simple, you can assume the total price = price of the itinerary x number of travelers
  val payee: String // which airline to be paid?
}

}