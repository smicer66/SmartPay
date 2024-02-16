package com.probase.smartpay.commins;

import java.util.Collection;

import smartpay.entity.Assessment;

public class TaxBreakDownResponse {
	
	private Collection<TaxDetails> taxDetailListing;
	private String productName;
	private String productCode;
	public Collection<TaxDetails> getTaxDetailListing() {
		return taxDetailListing;
	}
	public void setTaxDetailListing(Collection<TaxDetails> taxDetailListing) {
		this.taxDetailListing = taxDetailListing;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
}
