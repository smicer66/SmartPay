package com.probase.smartpay.commins;

public class TaxAssessment {

	private String registrationNumber;
	private String assessmentYear;
	private String port;
	private String declarantCode;
	private String TPIN;
	private boolean interest;
	private String dateRegistered;
	private Double amount;
	
	public String getRegistrationNumber() {
		return registrationNumber;
	}
	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}
	public String getAssessmentYear() {
		return assessmentYear;
	}
	public void setAssessmentYear(String assessmentYear) {
		this.assessmentYear = assessmentYear;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getTPIN() {
		return TPIN;
	}
	public void setTPIN(String tPIN) {
		TPIN = tPIN;
	}
	public String getDeclarantCode() {
		return declarantCode;
	}
	public void setDeclarantCode(String declarantCode) {
		this.declarantCode = declarantCode;
	}
	public boolean isInterest() {
		return interest;
	}
	public void setInterest(boolean interest) {
		this.interest = interest;
	}
	public String getDateRegistered() {
		return dateRegistered;
	}
	public void setDateRegistered(String dateRegistered) {
		this.dateRegistered = dateRegistered;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
}
