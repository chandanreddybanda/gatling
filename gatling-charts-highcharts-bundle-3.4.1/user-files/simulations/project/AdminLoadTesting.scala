
package project

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class AdminLoadTesting extends Simulation{
	val sessionHeaders = Map("Authorization" -> "Bearer ${authToken}",
                           "Content-Type" -> "application/json",
				"Connection"-> "keep-alive")

	val httpProtocol = http
    				.baseUrl("http://54.160.202.148:8765/api")
    				.doNotTrackHeader("1")
				.acceptLanguageHeader("en-US,en;q=0.5")
				.header("Accept", "*/*")
    				.acceptEncodingHeader("gzip, deflate,br")
    				.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
		
	val scn = scenario("customer-load-testing")
		.exec(
			http("Login")
			.post("/auth/login")
			.header("Content-Type", "application/json")                       
			.header("Connection", "keep-alive")
			.body(StringBody("""{
									"email": "rishabnahata07@gmail.com",
									"password": "U2FsdGVkX1+ktWG8SMjdt1kdHk7JDc0kkiP+XTwWJYM="
							}"""))
			.asJson
			.check(jsonPath("$..token").exists.saveAs("authToken"))//${authtoken}
		)
		.exec(
			http("Get Transaction By State And Date")
			.post("/admin/byState")
			.headers(sessionHeaders)
			.body(StringBody("""{
								    "status": "PENDING",
									"startDate": "2020/09/10",
									"endDate": "2020/10/30"
							}"""))
			.asJson
		)
        .exec(
			http("Get Transaction by Amount")
			.post("/admin/byAmount")
			.headers(sessionHeaders)
			.body(StringBody("""{
								    "from" : "2020/09/10",
    								"to": "2020/10/30",
									"min" : "0",
									"max" : "11111111"
							}"""))
			.asJson
		)
		.exec(
		  	http("Get Customer")
			.get("/customer/fetchCustomer")
			.headers(sessionHeaders)
		)
	//setUp(scn.inject(rampUsers(500) during (10 seconds)).protocols(httpProtocol))
	setUp(scn.inject(atOnceUsers(500))).protocols(httpProtocol)
}