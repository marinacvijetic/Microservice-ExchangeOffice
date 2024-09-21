package currency.dto;

import java.math.BigDecimal;

public class CurrencyDto {

	private String from;
	private String to;
	private BigDecimal conversionMultiple;
	private String environment;
	
	private BigDecimal conversionTotal;
	private Double quantity;
	
	public CurrencyDto() {
		
	}
	
	public CurrencyDto(String from, String to, BigDecimal conversionMultiple, String enviroment,
			BigDecimal conversionTotal, Double quantity) {
		this.from = from;
		this.to = to;
		this.conversionMultiple = conversionMultiple;
		this.environment = enviroment;
		this.conversionTotal = conversionTotal;
		this.quantity = quantity;
		
	}
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public BigDecimal getConversionMultiple() {
		return conversionMultiple;
	}

	public void setConversionMultiple(BigDecimal conversionMultiple) {
		this.conversionMultiple = conversionMultiple;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String enviroment) {
		this.environment = enviroment;
	}

	public BigDecimal getConversionTotal() {
		return conversionTotal;
	}

	public void setConversionTotal(BigDecimal conversionTotal) {
		this.conversionTotal = conversionTotal;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
}
