package com.probase.smartpay.commins;

public class TaxDetails {
	private Double amountToBePaid;
	private String taxCode;
	
	public TaxDetails()
	{
		
	}
	
	public Double getAmountToBePaid() {
		return this.amountToBePaid;
	}
	public void setAmountToBePaid(Double amountToBePaid) {
		this.amountToBePaid = amountToBePaid;
	}
	public String getTaxCode() {
		return this.taxCode;
	}
	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}
	
}
