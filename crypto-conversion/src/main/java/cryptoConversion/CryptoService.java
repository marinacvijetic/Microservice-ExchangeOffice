package cryptoConversion;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import cryptoConversion.dto.CryptoDto;
import cryptoConversion.dto.CryptoWalletDto;
import cryptoConversion.dto.ResponseDto;

@Service
public class CryptoService {

	@Autowired
	private CryptoExchangeProxy cryptoExchangeProxy;
	
	@Autowired
	private CryptoWalletProxy cryptoWalletProxy;
	
	public ResponseDto<String, CryptoWalletDto> getConversion(String from, String to, BigDecimal quantity, String auth){
		CryptoDto exchangeRate = cryptoExchangeProxy.getExchange(from, to);
		
		if(exchangeRate == null) {
			return new ResponseDto<String, CryptoWalletDto>("Something went wrong.", null);
		}
		
		CryptoWalletDto userAccount = cryptoWalletProxy.getUserAccount(auth);
		
		if(!checkBalance(userAccount, from.toUpperCase(), quantity)) {
			return new ResponseDto<String, CryptoWalletDto>("Insufficient amount of funds.", null);
		}
		
		userAccount = updateBalance(userAccount, exchangeRate, quantity);
		
		ResponseEntity<?> response = cryptoWalletProxy.updateUserAccount(userAccount);

		if(response.getStatusCode() == HttpStatus.OK) {
			return new ResponseDto<String, CryptoWalletDto>("Conversion and transfer of the amount of "
					+ quantity + " " + from + " to " + quantity.multiply(exchangeRate.getCryptoMultiple())
					+ " " + to, userAccount);
		}else {
			return new ResponseDto<String, CryptoWalletDto>("Failed to update crypto wallet.", null);
		}
	}
	
	
	
	private boolean isBalanceGreaterThan(BigDecimal balance, BigDecimal quantity) {
		return balance.compareTo(quantity) >= 0;
	}

	private boolean checkBalance(CryptoWalletDto userAccount, String from, BigDecimal quantity) {
		switch (from) {
		case "ETH":
			return isBalanceGreaterThan(userAccount.getETH(), quantity);
		case "BTC":
			return isBalanceGreaterThan(userAccount.getBTC(), quantity);
		case "BNB":
			return isBalanceGreaterThan(userAccount.getBNB(), quantity);
		default:
			return false;
		}
	}

	private CryptoWalletDto updateBalance(CryptoWalletDto userAccount, CryptoDto exchangeRate, BigDecimal quantity) {
		BigDecimal amount = quantity.multiply(exchangeRate.getCryptoMultiple());
		;

		switch (exchangeRate.getFrom()) {
		case "ETH":

			switch (exchangeRate.getTo()) {
			case "BTC":
				userAccount.setETH(userAccount.getETH().subtract(quantity));
				userAccount.setBTC(userAccount.getBTC().add(amount));

				break;
			case "BNB":
				userAccount.setETH(userAccount.getETH().subtract(quantity));
				userAccount.setBNB(userAccount.getBNB().add(amount));

				break;
			}

			break;
		case "BNB":

			switch (exchangeRate.getTo()) {
			case "ETH":
				userAccount.setBNB(userAccount.getBNB().subtract(quantity));
				userAccount.setETH(userAccount.getETH().add(amount));

				break;
			case "BTC":
				userAccount.setBNB(userAccount.getBNB().subtract(quantity));
				userAccount.setBTC(userAccount.getBTC().add(amount));

				break;
			}

			break;
		case "BTC":

			switch (exchangeRate.getTo()) {
			case "ETH":
				userAccount.setBTC(userAccount.getBTC().subtract(quantity));
				userAccount.setETH(userAccount.getETH().add(amount));

				break;
			case "BNB":
				userAccount.setBTC(userAccount.getBTC().subtract(quantity));
				userAccount.setBNB(userAccount.getBNB().add(amount));

				break;
			}

			break;
		}

		return userAccount;
	}
}
