package tradeService;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TradeService {
	@Id
	private long id;
	
	@Column
	private String exchangeFrom;
	
	@Column 
	private String exchangeTo;
	
	@Column(precision = 20, scale = 5)
	private BigDecimal multiplier;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getExchangeFrom() {
		return exchangeFrom;
	}

	public void setExchangeFrom(String exchangeFrom) {
		this.exchangeFrom = exchangeFrom;
	}

	public String getExchangeTo() {
		return exchangeTo;
	}

	public void setExchangeTo(String exchangeTo) {
		this.exchangeTo = exchangeTo;
	}

	public BigDecimal getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(BigDecimal multiplier) {
		this.multiplier = multiplier;
	}

	public TradeService(Long id, String exchangeFrom, String exchangeTo, BigDecimal multiplier) {
		super();
		this.id = id;
		this.exchangeFrom = exchangeFrom;
		this.exchangeTo = exchangeTo;
		this.multiplier = multiplier;
	}

	public TradeService() {
		super();
	}
}
