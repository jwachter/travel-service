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

// Import needed JDK classes.
import _root_.java.security.MessageDigest

//
// Provides general methods that enable UID generation and related operations.
//
object IdHelper {
	 
    //
	// Calculates the MD5 sum of a string and does ASCII encoding.
	// From: http://code-redefined.blogspot.com/2009/05/md5-sum-in-scala.html
    //
	def md5(base : String) : String = {
	  val md5 = MessageDigest.getInstance("MD5")
	  md5.reset()
	  md5.update(base.getBytes)

	  md5.digest().map(0xFF & _).map { "%02x".format(_) }.foldLeft(""){_ + _}
	}
}
