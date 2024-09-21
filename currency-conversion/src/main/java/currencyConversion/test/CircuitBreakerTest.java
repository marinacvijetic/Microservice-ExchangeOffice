package currencyConversion.test;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class CircuitBreakerTest {

	/*@GetMapping("/try-circuit-breaker")
	public String callSampleApi() {
		String response = "";
		for(int i=1; i<=10000; i++) {
			response = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/sample/api", String.class).getBody();
		}
		return response;
	}*/
}
