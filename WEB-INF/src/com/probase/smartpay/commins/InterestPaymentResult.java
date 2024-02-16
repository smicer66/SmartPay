package com.probase.smartpay.commins;

public class InterestPaymentResult {

	private String result;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getOfficeCode() {
		return officeCode;
	}
	public void setOfficeCode(String officeCode) {
		this.officeCode = officeCode;
	}
	public String getReceiptSerial() {
		return receiptSerial;
	}
	public void setReceiptSerial(String receiptSerial) {
		this.receiptSerial = receiptSerial;
	}
	public String getReceiptNumber() {
		return receiptNumber;
	}
	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}
	public String getReceiptDate() {
		return receiptDate;
	}
	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}
	private String errorCode;
	private String officeCode;
	private String receiptSerial;
	private String receiptNumber;
	private String receiptDate;
	
	
}
