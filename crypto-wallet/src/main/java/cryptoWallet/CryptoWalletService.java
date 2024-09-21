package cryptoWallet;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CryptoWalletService {

	@Autowired
	CryptoWalletRepository repo;
	
	public CryptoWalletModel findByEmail(String email) {
		return repo.findByEmail(email);
	}
	
	public CryptoWalletModel update(CryptoWalletModel wallet) {
		return repo.save(wallet);
	}
	
	public CryptoWalletModel updateOne(String email, String update, BigDecimal quantity) {
		
		CryptoWalletModel wallet = findByEmail(email);
		
		switch (update.toLowerCase()) {
		case "btc":
			wallet.setBtc(wallet.getBtc().add(quantity));
			break;
		case "bnb":
			wallet.setBnb(wallet.getBnb().add(quantity));
			break;
		case "eth":
			wallet.setEth(wallet.getEth().add(quantity));
			break;
		default:
			break;
		}
		
		return repo.save(wallet);
	}
}
