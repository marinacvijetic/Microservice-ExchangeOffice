package tradeService.dto;

import java.math.BigDecimal;

public class CryptoWalletDto {
	private long id;

	private BigDecimal btc;

	private BigDecimal eth;

	private BigDecimal bnb;

	private String email;

	public Long getId() {
		return id;
	}

	public BigDecimal getBtc() {
		return btc;
	}

	public BigDecimal getEth() {
		return eth;
	}

	public BigDecimal getBnb() {
		return bnb;
	}

	public String getEmail() {
		return email;
	}
}
