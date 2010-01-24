package specification

import _root_.java.util.Date

abstract class Payable

class CreditCard(val cardNumber : String, val holderName : String, val expireDate : Date, val securityCode : String, val issuer : String) extends Payable

case class Visa(override val cardNumber : String, override val holderName : String, override val expireDate : Date, override val securityCode : String, override val issuer : String) extends CreditCard(cardNumber, holderName, expireDate, securityCode, issuer)
case class MasterCard(override val cardNumber : String, override val holderName : String, override val expireDate : Date, override val securityCode : String, override val issuer : String) extends CreditCard(cardNumber, holderName, expireDate, securityCode, issuer)
case class AmericanExpress(override val cardNumber : String, override val holderName : String, override val expireDate : Date, override val securityCode : String, override val issuer : String) extends CreditCard(cardNumber, holderName, expireDate, securityCode, issuer)

class DebitCard(val cardNumber:String, val holderName:String, val expireDate: Date, val pin:String) extends Payable

case class ConvenienceCard(override val cardNumber:String, override val holderName:String, override val expireDate: Date, override val pin:String) extends DebitCard(cardNumber, holderName, expireDate, pin)
case class ElectronicCash(override val cardNumber:String, override val holderName:String, override val expireDate: Date, override val pin:String)  extends DebitCard(cardNumber, holderName, expireDate, pin)
case class UnionPayCard(override val cardNumber:String, override val holderName:String, override val expireDate: Date, override val pin:String)    extends DebitCard(cardNumber, holderName, expireDate, pin)