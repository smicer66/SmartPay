package com.probase.smartpay.commins;

public class InterestToBePaid {
	private String errorCode;
	private String tpin;
	private String declarantCode;
	private String port;
	private String registrationSerial;
	private String registrationNumber;
	private String registrationYear;
	private Double amountToBePaid;
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getTpin() {
		return tpin;
	}
	public void setTpin(String tpin) {
		this.tpin = tpin;
	}
	public String getDeclarantCode() {
		return declarantCode;
	}
	public void setDeclarantCode(String declarantCode) {
		this.declarantCode = declarantCode;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getRegistrationSerial() {
		return registrationSerial;
	}
	public void setRegistrationSerial(String registrationSerial) {
		this.registrationSerial = registrationSerial;
	}
	public String getRegistrationNumber() {
		return registrationNumber;
	}
	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}
	public String getRegistrationYear() {
		return registrationYear;
	}
	public void setRegistrationYear(String registrationYear) {
		this.registrationYear = registrationYear;
	}
	public Double getAmountToBePaid() {
		return amountToBePaid;
	}
	public void setAmountToBePaid(Double amountToBePaid) {
		this.amountToBePaid = amountToBePaid;
	}
}
