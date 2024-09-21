package bankAccount;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bankAccount.dto.BankAccountDto;
import bankAccount.dto.UpdateBankAccountDto;

@RestController
@RequestMapping("/bank-account")
public class BankAccountController {

	@Autowired
	private BankAccountRepository repo;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@GetMapping
	public ResponseEntity<?> getUserAccount(@RequestHeader("Authorization") String auth){
		String pair = new String(Base64.decodeBase64(auth.substring(6)));
		String email = pair.split(":")[0];
		
		BankAccountModel userAccount = repo.findByEmail(email);
		
		if(userAccount == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The bank account for given email address does not exist.");
		}else {
			BankAccountDto userAccountDto = new BankAccountDto();
			
			userAccountDto.setEmail(userAccount.getEmail());
			userAccountDto.setChf(userAccount.getChf());
			userAccountDto.setEur(userAccount.getEur());
			userAccountDto.setRsd(userAccount.getRsd());
			userAccountDto.setGbp(userAccount.getGbp());
			userAccountDto.setUsd(userAccount.getUsd());
			
			return ResponseEntity.ok(userAccountDto);
		}
	}
	
	@GetMapping("/user/{email}")
	public BankAccountModel getBankAccount(@PathVariable String email) {
		return repo.findByEmail(email);
	}
	
	@PutMapping("/{email}/update/{update}/quantity/{quantity}")
	public BankAccountModel updateOne(@PathVariable String email, @PathVariable String update, @PathVariable BigDecimal quantity) {
		
		BankAccountModel bankAccount = repo.findByEmail(email);
		
		switch(update.toUpperCase()) {
			case "USD":
				bankAccount.setUsd(bankAccount.getUsd().add(quantity));
				break;
			case "GBP":
				bankAccount.setGbp(bankAccount.getGbp().add(quantity));
				break;
			case "CHF":
				bankAccount.setChf(bankAccount.getChf().add(quantity));
				break;
			case "EUR":
				bankAccount.setEur(bankAccount.getEur().add(quantity));
				break;
			case "RSD":
				bankAccount.setRsd(bankAccount.getRsd().add(quantity));
				break;
		}
		
		return repo.save(bankAccount);
	}
	
	@PutMapping("/changeUserEmail/{email}")
	public ResponseEntity<?> updateUserEmail(@RequestBody String newEmail, @PathVariable("email") String oldEmail){
		BankAccountModel userAccount = repo.findByEmail(oldEmail);
		
		if(userAccount == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The bank account for given email address does not exist.");
		}else {
			Long userId = userAccount.getId();
			
			userAccount.setEmail(newEmail);
			
			jdbcTemplate.update("UPDATE bank_account_model SET email = ? WHERE id = ?", userAccount.getEmail(), userId);
		
			return ResponseEntity.ok().build();
		}
	}
	
	@PutMapping("/editAccount/{email}")
	public ResponseEntity<?> updateUserBankAccount(@RequestBody UpdateBankAccountDto userAccountDto, @PathVariable("email") String email){
		BankAccountModel userAccount = repo.findByEmail(email);
		
		if(userAccount == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The bank account for given email address does not exist.");
		}else {
			userAccount.setRsd(userAccountDto.getRsd());
			userAccount.setEur(userAccountDto.getEur());
			userAccount.setUsd(userAccountDto.getUsd());
			userAccount.setGbp(userAccountDto.getGbp());
			userAccount.setChf(userAccountDto.getChf());
			
			jdbcTemplate.update("UPDATE bank_account_model SET usd = ?, gbp = ?, chf = ?, eur = ?, rsd = ? WHERE email = ?",
					userAccount.getUsd(),
					userAccount.getGbp(),
					userAccount.getChf(),
					userAccount.getEur(),
					userAccount.getRsd(),
					email
				);
			
			Map<String, Object> responseBody = new HashMap<>();
			responseBody.put("message",	"The account balance has been updated successfully.");
			responseBody.put("bank account", mapperBankAccountUpdateDto(userAccount));
		
			return ResponseEntity.ok().body(responseBody);
		}
	}
	
	@PutMapping
	public ResponseEntity<?> updateUserAccount(@RequestBody BankAccountDto userAccountDto){
		BankAccountModel userAccount = repo.findByEmail(userAccountDto.getEmail());
		
		if(userAccount == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The bank account for given email address does not exist.");
			
		}else {
			userAccount.setRsd(userAccountDto.getRsd());
			userAccount.setEur(userAccountDto.getEur());
			userAccount.setUsd(userAccountDto.getUsd());
			userAccount.setGbp(userAccountDto.getGbp());
			userAccount.setChf(userAccountDto.getChf());
			
			jdbcTemplate.update("UPDATE bank_account_model SET usd = ?, gbp = ?, chf = ?, eur = ?, rsd = ? WHERE email = ?", 
			userAccount.getUsd(),
			userAccount.getGbp(),
			userAccount.getChf(),
			userAccount.getEur(),
			userAccount.getRsd(),
			userAccount.getEmail()
			);
			
			return ResponseEntity.ok(mapperBankAccountDto(userAccount));
			
		}
	}
	
	@PostMapping("/addAccount")
	public ResponseEntity<?> addUserBankAccount(@RequestBody String email) {
		BankAccountModel userAccount = repo.findByEmail(email);
		
		if(userAccount != null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The bank account for this user already exist.");
		}else {
			Long bankAccountId = ThreadLocalRandom.current().nextLong(3, 101);
			
			BankAccountModel newUserAccount = new BankAccountModel();
			
			newUserAccount.setId(bankAccountId);
			newUserAccount.setChf(new BigDecimal(0));
			newUserAccount.setRsd(new BigDecimal(0));
			newUserAccount.setEur(new BigDecimal(0));
			newUserAccount.setUsd(new BigDecimal(0));
			newUserAccount.setGbp(new BigDecimal(0));
			newUserAccount.setEmail(email);
			
			repo.save(newUserAccount);
			
			return ResponseEntity.ok().build();
		}
	}
	
	
	@DeleteMapping("/removeUser/{email}")
	public ResponseEntity<?> removeUserBankAccount(@PathVariable("email") String email){
		BankAccountModel userAccount = repo.findByEmail(email);
		
		if(userAccount == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The bank account for given email address does not exist.");
		}else {
			Long userId = userAccount.getId();
			
			repo.deleteById(userId);
			
			return ResponseEntity.ok().build();
		}
	}
	
	
	
	
	
	private BankAccountDto mapperBankAccountDto(BankAccountModel entity) {
		return new BankAccountDto(
			entity.getEmail(),
			entity.getRsd(),
			entity.getEur(),
			entity.getUsd(),
			entity.getGbp(),
			entity.getChf()
		);
	}
	
	private UpdateBankAccountDto mapperBankAccountUpdateDto(BankAccountModel entity) {
		return new UpdateBankAccountDto(
			entity.getRsd(),
			entity.getEur(),
			entity.getUsd(),
			entity.getGbp(),
			entity.getChf()
		);
	}

	
}
