//baseUrl("http://54.82.202.101:8765/api")

package project

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class CustomerLoadTesting extends Simulation{
	val sessionHeaders = Map("Authorization" -> "Bearer ${authToken}",
                           "Content-Type" -> "application/json",
				"Connection"-> "keep-alive")

	val httpProtocol = http
    				.baseUrl("http://20.50.2.214:8765/api")
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
									"email": "testing@customer.com",
									"password": "uUe4dCaV1KuerHwIEhSJIA=="
							}"""))
			.asJson
			.check(jsonPath("$..token").exists.saveAs("authToken"))
		)
		.exec(
		  	http("Credit cards")
			.get("/customer/creditcards")
			.headers(sessionHeaders)
		)
		.exec(
		  	http("Customer information")
			.get("/customer/fetchCustomer")
			.headers(sessionHeaders)
		)
		.exec(
		  	http("Credit card expenses")
			.get("/customer/creditcards/expenses/4539915351062246")
			.headers(sessionHeaders)
		)
		.exec(
		  	http("Customer port")
			.get("/customer/port")
			.headers(sessionHeaders)
		)
	//setUp(scn.inject(rampUsers(500) during (10 seconds)).protocols(httpProtocol))
	setUp(scn.inject(atOnceUsers(500))).protocols(httpProtocol)
}
