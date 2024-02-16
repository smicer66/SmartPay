package com.probase.smartpay.commins;

public class DeclarationPaymentResult {

	private String result;
	private String errorCode;
	private String errorDescription;
	private String receiptSerial;
	private String receiptNumber;
	private String receiptDate;
	
	
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
	public String getErrorDescription() {
		return errorDescription;
	}
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
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
}
