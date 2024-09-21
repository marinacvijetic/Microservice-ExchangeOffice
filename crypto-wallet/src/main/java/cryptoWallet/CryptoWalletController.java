package cryptoWallet;

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
import org.springframework.web.bind.annotation.RestController;

import cryptoWallet.dto.CryptoWalletDto;
import cryptoWallet.dto.UpdateCryptoWalletDto;

@RestController
public class CryptoWalletController {

	@Autowired
	private CryptoWalletRepository repo;
	
	@Autowired
	private CryptoWalletService service;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@GetMapping("/crypto-wallet")
	public ResponseEntity<?> getUserAccount(@RequestHeader("Authorization") String auth){
		String pair = new String(Base64.decodeBase64(auth.substring(6)));
		String email = pair.split(":")[0];
		
		CryptoWalletModel wallet = repo.findByEmail(email);
		
		if(wallet == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The crypto wallet for given email address does not exist.");
		}else {
			CryptoWalletDto cryptoWalletDto = new CryptoWalletDto();
			
			cryptoWalletDto.setEmail(wallet.getEmail());
			cryptoWalletDto.setETH(wallet.getEth());
			cryptoWalletDto.setBTC(wallet.getBtc());
			cryptoWalletDto.setBNB(wallet.getBnb());
			
			return ResponseEntity.ok(cryptoWalletDto);
		}
	}
	
	@GetMapping("/crypto-wallet/{email}")
	public CryptoWalletModel getWallet(@PathVariable String email) {
		return service.findByEmail(email);
	}
	
	@PutMapping("/crypto-wallet/{email}/update/{update}/quantity/{quantity}")
	public CryptoWalletModel updateOne(@PathVariable String email, @PathVariable String update, @PathVariable BigDecimal quantity) {
		return service.updateOne(email, update, quantity);
	}
	
	@PutMapping("/crypto-wallet/changeUserEmail/{email}")
	public ResponseEntity<?> updateUserEmail(@RequestBody String newEmail, @PathVariable("email") String oldEmail){
		CryptoWalletModel wallet = repo.findByEmail(oldEmail);
		
		if(wallet == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The crypto wallet for given email address does not exist.");
			
		}else {
			Long userId = wallet.getId();
			
			wallet.setEmail(newEmail);
			
			jdbcTemplate.update("UPDATE crypto_wallet_model SET email = ? WHERE id = ?",
					wallet.getEmail(), userId);
			
			return ResponseEntity.ok().build();
		}
	}
	
	@PutMapping("/crypto-wallet")
	public ResponseEntity<?> updateUserAccount(@RequestBody CryptoWalletDto userAccountDto){
		
		CryptoWalletModel userAccount = repo.findByEmail(userAccountDto.getEmail());
		
		if(userAccount == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The crypto wallet for given email address does not exist.");
		}else {
			userAccount.setBnb(userAccountDto.getBNB());
			userAccount.setEth(userAccountDto.getETH());
			userAccount.setBtc(userAccountDto.getBTC());
			
			jdbcTemplate.update("UPDATE crypto_wallet_model SET bnb = ?, btc = ?, eth = ? WHERE email = ?",
					userAccount.getBnb(), userAccount.getBtc(), userAccount.getEth(), userAccount.getEmail());
			
			return ResponseEntity.ok(mapperCryptoWalletDto(userAccount));
		}
	
	}
	
	@PutMapping("/crypto-wallet/editWallet/{email}")
	public ResponseEntity<?> updateUserBankAccount(@RequestBody UpdateCryptoWalletDto cryptoWalletDto, @PathVariable("email") String email){
		CryptoWalletModel wallet = repo.findByEmail(email);
		
		if(wallet == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The crypto wallet for given email address does not exist.");
		}else {
			wallet.setBnb(cryptoWalletDto.getBNB());
			wallet.setBtc(cryptoWalletDto.getBTC());
			wallet.setEth(cryptoWalletDto.getETH());
			
			jdbcTemplate.update("UPDATE crypto_wallet_model SET bnb = ?, btc = ?, eth = ? WHERE email = ?", 
					wallet.getBnb(), wallet.getBtc(), wallet.getEth(), email);
		
			Map<String, Object> responseBody = new HashMap<>();
			responseBody.put("message", "The account balance has been updated successfully.");
			responseBody.put("crypto-wallet", mapperCryptoWalletUpdateDto(wallet));
			
			return ResponseEntity.ok().body(responseBody);
		}
	}
	
	@PostMapping("/crypto-wallet/addAccount")
	public ResponseEntity<?> addUserCryptoWallet(@RequestBody String email){
		CryptoWalletModel wallet = repo.findByEmail(email);
		
		if(wallet != null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The crypto wallet for given email address already exists.");
		}else {
			Long userAccountId = ThreadLocalRandom.current().nextLong(3, 101);
			
			CryptoWalletModel newUserAccount = new CryptoWalletModel();
			
			newUserAccount.setId(userAccountId);
			newUserAccount.setBnb(new BigDecimal(0));
			newUserAccount.setEth(new BigDecimal(0));
			newUserAccount.setBtc(new BigDecimal(0));
			newUserAccount.setEmail(email);
			
			repo.save(newUserAccount);
			
			return ResponseEntity.ok().build();
		
		}
	}
	
	@DeleteMapping("/crypto-wallet/removeUser/{email}")
	public ResponseEntity<?> removeUserCryptoWallet(@PathVariable("email") String email){
		CryptoWalletModel wallet = repo.findByEmail(email);
		
		if(wallet == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The crypto wallet for given email address does not exist.");
		}else {
			Long userId = wallet.getId();
			
			repo.deleteById(userId);
			
			return ResponseEntity.ok().build();
		}
	}
	
	
	private UpdateCryptoWalletDto mapperCryptoWalletUpdateDto(CryptoWalletModel entity) {
		return new UpdateCryptoWalletDto(entity.getBnb(), entity.getBtc(), entity.getEth());
	}

	private CryptoWalletDto mapperCryptoWalletDto(CryptoWalletModel entity) {
		return new CryptoWalletDto(entity.getEmail(), entity.getBnb(), entity.getEth(), entity.getBtc());
	}
	
	
	
	
}
