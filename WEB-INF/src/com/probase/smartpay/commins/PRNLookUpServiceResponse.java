package com.probase.smartpay.commins;

public class PRNLookUpServiceResponse {

	private String amountToBePaid;
	private String errorCode;
	private String errorDescription;
	private String paymentRegDate;
	private String paymentExpDate;
	private String paymentRegNo;
	private String taxPayerName;
	private String tpin;
	private String status;
	
	public String getAmountToBePaid() {
		return amountToBePaid;
	}
	public void setAmountToBePaid(String amountToBePaid) {
		this.amountToBePaid = amountToBePaid;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getPaymentRegDate() {
		return paymentRegDate;
	}
	public void setPaymentRegDate(String paymentRegDate) {
		this.paymentRegDate = paymentRegDate;
	}
	public String getPaymentRegNo() {
		return paymentRegNo;
	}
	public void setPaymentRegNo(String paymentRegNo) {
		this.paymentRegNo = paymentRegNo;
	}
	public String getTaxPayerName() {
		return taxPayerName;
	}
	public void setTaxPayerName(String taxPayerName) {
		this.taxPayerName = taxPayerName;
	}
	public String getTpin() {
		return tpin;
	}
	public void setTpin(String tpin) {
		this.tpin = tpin;
	}
	public String getErrorDescription() {
		return errorDescription;
	}
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPaymentExpDate() {
		return paymentExpDate;
	}
	public void setPaymentExpDate(String paymentExpDate) {
		this.paymentExpDate = paymentExpDate;
	}
}
