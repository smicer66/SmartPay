package com.probase.smartpay.commins;

public class InterestPayment {

	private String declarantCode; 
	private String tpinNo; 
	private String bankCode; 
	private String portCode; 
	private String transactionCode; 
	private String assessmentYear; 
	private String assessmentSerial; 
	private String registrationNumber; 
	private String referenceText; 
	private String amountToBePaid; 
	private String assessmentNumber; 
	private String meanOfPayment; 
	private String checkReference; 
	private String paymentDate;
	private boolean interestTrue;
	
	public String getDeclarantCode() {
		return declarantCode;
	}
	public void setDeclarantCode(String declarantCode) {
		this.declarantCode = declarantCode;
	}
	public String getTpinNo() {
		return tpinNo;
	}
	public void setTpinNo(String tpinNo) {
		this.tpinNo = tpinNo;
	}
	public String getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}
	public String getCheckReference() {
		return checkReference;
	}
	public void setCheckReference(String checkReference) {
		this.checkReference = checkReference;
	}
	public String getMeanOfPayment() {
		return meanOfPayment;
	}
	public void setMeanOfPayment(String meanOfPayment) {
		this.meanOfPayment = meanOfPayment;
	}
	public String getAssessmentNumber() {
		return assessmentNumber;
	}
	public void setAssessmentNumber(String assessmentNumber) {
		this.assessmentNumber = assessmentNumber;
	}
	public String getAmountToBePaid() {
		return amountToBePaid;
	}
	public void setAmountToBePaid(String amountToBePaid) {
		this.amountToBePaid = amountToBePaid;
	}
	public String getReferenceText() {
		return referenceText;
	}
	public void setReferenceText(String referenceText) {
		this.referenceText = referenceText;
	}
	public String getRegistrationNumber() {
		return registrationNumber;
	}
	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}
	public String getAssessmentSerial() {
		return assessmentSerial;
	}
	public void setAssessmentSerial(String assessmentSerial) {
		this.assessmentSerial = assessmentSerial;
	}
	public String getAssessmentYear() {
		return assessmentYear;
	}
	public void setAssessmentYear(String assessmentYear) {
		this.assessmentYear = assessmentYear;
	}
	public String getTransactionCode() {
		return transactionCode;
	}
	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}
	public String getPortCode() {
		return portCode;
	}
	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public boolean isInterestTrue() {
		return interestTrue;
	}
	public void setInterestTrue(boolean interestTrue) {
		this.interestTrue = interestTrue;
	}
}
