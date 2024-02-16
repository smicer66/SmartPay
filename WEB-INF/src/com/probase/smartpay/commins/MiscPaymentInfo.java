package com.probase.smartpay.commins;

public class MiscPaymentInfo {
	private String bankCode;
	private String officeCode;
	private String declarantCode;
	private String transactionCode;
	private String transactionDescription;
	private String companyCode;
	private MiscTransactionToBePaid miscTransactionToBePaid;
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getOfficeCode() {
		return officeCode;
	}
	public void setOfficeCode(String officeCode) {
		this.officeCode = officeCode;
	}
	public String getDeclarantCode() {
		return declarantCode;
	}
	public void setDeclarantCode(String declarantCode) {
		this.declarantCode = declarantCode;
	}
	public String getTransactionCode() {
		return transactionCode;
	}
	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}
	public String getTransactionDescription() {
		return transactionDescription;
	}
	public void setTransactionDescription(String transactionDescription) {
		this.transactionDescription = transactionDescription;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public MiscTransactionToBePaid getMiscTransactionToBePaid() {
		return miscTransactionToBePaid;
	}
	public void setMiscTransactionToBePaid(
			MiscTransactionToBePaid miscTransactionToBePaid) {
		this.miscTransactionToBePaid = miscTransactionToBePaid;
	}
}
