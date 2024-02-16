package com.probase.smartpay.commins;

import java.util.ArrayList;

public class EndOfDayDomTResponse {

	
	private ArrayList<PmtNotifyErrReport> pmtNotifyErrReport;

	public ArrayList<PmtNotifyErrReport> getPmtNotifyErrReport() {
		return pmtNotifyErrReport;
	}

	public void setPmtNotifyErrReport(ArrayList<PmtNotifyErrReport> pmtNotifyErrReport) {
		this.pmtNotifyErrReport = pmtNotifyErrReport;
	} 
}




class PmtNotifyErrReport {

	
	private String errorCode;
	private String errorMessage;
	private String amountPaid;
	private String bankBranchCode;
	private String datePaid;
	private String paymentRegTransNo;
	private String status;
	private String taxPayerName;
	private String tin;
	private String transactionId;
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getAmountPaid() {
		return amountPaid;
	}
	public void setAmountPaid(String amountPaid) {
		this.amountPaid = amountPaid;
	}
	public String getBankBranchCode() {
		return bankBranchCode;
	}
	public void setBankBranchCode(String bankBranchCode) {
		this.bankBranchCode = bankBranchCode;
	}
	public String getDatePaid() {
		return datePaid;
	}
	public void setDatePaid(String datePaid) {
		this.datePaid = datePaid;
	}
	public String getPaymentRegTransNo() {
		return paymentRegTransNo;
	}
	public void setPaymentRegTransNo(String paymentRegTransNo) {
		this.paymentRegTransNo = paymentRegTransNo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTaxPayerName() {
		return taxPayerName;
	}
	public void setTaxPayerName(String taxPayerName) {
		this.taxPayerName = taxPayerName;
	}
	public String getTin() {
		return tin;
	}
	public void setTin(String tin) {
		this.tin = tin;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
}
