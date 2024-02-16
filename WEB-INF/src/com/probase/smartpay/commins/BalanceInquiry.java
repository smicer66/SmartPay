package com.probase.smartpay.commins;

public class BalanceInquiry {
	

    private String resMessageId;
    private String resTimeStamp;
    private String resTrackingId;
    private String resSourceSystem;
    private String resMessageType;
    private String accountNumber;
    private String currency;
    private Double availableBalance;
    private String type;
    private String status;
    
	public String getResMessageId() {
		return resMessageId;
	}
	public void setResMessageId(String resMessageId) {
		this.resMessageId = resMessageId;
	}
	public String getReqTimeStamp() {
		return resTimeStamp;
	}
	public void setResTimeStamp(String resTimeStamp) {
		this.resTimeStamp = resTimeStamp;
	}
	public String getResTrackingId() {
		return resTrackingId;
	}
	public void setResTrackingId(String resTrackingId) {
		this.resTrackingId = resTrackingId;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Double getAvailableBalance() {
		return availableBalance;
	}
	public void setAvailableBalance(Double d) {
		this.availableBalance = d;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getResSourceSystem() {
		return resSourceSystem;
	}
	public void setResSourceSystem(String resSourceSystem) {
		this.resSourceSystem = resSourceSystem;
	}
	public String getResMessageType() {
		return resMessageType;
	}
	public void setResMessageType(String resMessageType) {
		this.resMessageType = resMessageType;
	}

}
