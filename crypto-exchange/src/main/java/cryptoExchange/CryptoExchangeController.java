package cryptoExchange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@RestController
public class CryptoExchangeController {

	@Autowired
	private CryptoExchangeRepository repo;
	
	@Autowired
	private Environment enviroment;
	
	@GetMapping("/crypto-exchange/from/{from}/to/{to}")
	@RateLimiter(name = "default")
	public ResponseEntity<?> getExchange(@PathVariable String from, String to){
		String port = enviroment.getProperty("local.server.port");
		CryptoExchangeModel crypto = repo.findByFromAndToIgnoreCase(from, to);
		
		if(crypto != null) {
			crypto.setEnvironment(port);
			return ResponseEntity.ok(crypto);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Requested crypto exchange could not be found.");
		}

	}
	
	
	
	@ExceptionHandler(RequestNotPermitted.class)
	public ResponseEntity<String> rateLimiterExceptionHandler(RequestNotPermitted ex){
		return ResponseEntity.status(503).body("Currency exchange service can only serve up to 2 requests every 30 seconds.");
	}
}
