package com.probase.smartpay.commins;

public class FundsTransferResponse {

	private String accountNumber;
	private Boolean status;
	private Boolean resTimeStamp;
	private String resMessageId;
	private String errorCode;
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public Boolean getResTimeStamp() {
		return resTimeStamp;
	}
	public void setResTimeStamp(Boolean resTimeStamp) {
		this.resTimeStamp = resTimeStamp;
	}
	public String getResMessageId() {
		return resMessageId;
	}
	public void setResMessageId(String resMessageId) {
		this.resMessageId = resMessageId;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	

}
