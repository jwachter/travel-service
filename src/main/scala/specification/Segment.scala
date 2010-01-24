package specification {
  
  import _root_.java.util.Date

class Segment(val origin : Airport, val destination : Airport, val departureDate : Date, val duration : Int, val hops : Seq[Hop]) {
    /*
     A segment is a direct or a connect flight from an origin airport to a destination airport. It can contain one or more
     hops to make the complete journal. A passenger is expected to finish all hops in the segment continously without leaving
     intermeidate airports (although they can if transfer time permits and the passenger has valid visa to enter the country).

     e.g. a flight from Frankfurt to St. John's, with connect at Toronto.
     */
    //val origin: Airport // ultimate origin (e.g. Frankfurt)
    //val destination: Airport  // ultimate destination (e.g. St. John's, not Toronto)
    //val departureDate: Date   // departure date of the first hop, e.g. 2010-02-07 14:35
    //val duration: Int   // time interval in minutes; inlcuding waiting time at trasfer airports
    //val hops: Seq[Hop]  // the detailed hops, e.g. first hop: Frankfurt -> Toronto; second hop: Toronto -> St. John's.

    def toXML =
    <segment>
        <origin>{ origin.code.name }</origin>
        <destination>{ destination.code.name }</destination>
        <departureDate>{ departureDate }</departureDate>
        <hops>
            { for (hop <- hops) yield hop.toXML }
        </hops>
    </segment>
}
}