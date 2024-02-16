package com.probase.smartpay.reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;

import org.apache.log4j.Logger;

import smartpay.entity.ApprovalFlowTransit;
import smartpay.entity.Assessment;
import smartpay.entity.AuthorizePanelCombination;
import smartpay.entity.Company;
import smartpay.entity.CompanyCRUDRights;
import smartpay.entity.FeeDescription;
import smartpay.entity.PaymentHistory;
import smartpay.entity.PortalUser;
import smartpay.entity.PortalUserCRUDRights;
import smartpay.entity.Ports;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.TaxType;
import smartpay.entity.WorkFlow;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.service.SwpService;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.probase.smartpay.admin.feedescriptionmanagement.FeeDescriptionPortletState.FEE_DESCRIPTION_APPROVAL_TYPE;
import com.probase.smartpay.admin.mandatepanelmanagement.MandatePanelPortletState;
import com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortletState;
import com.probase.smartpay.commins.ComminsApplicationState;
import com.probase.smartpay.commins.Util;
import com.probase.smartpay.commins.ProbaseConstants.CORE_VIEW;
import com.sf.primepay.smartpay13.ServiceLocator;

public class ReportPortletState {

	private static Logger log = Logger.getLogger(ReportPortletState.class);
	private static ReportPortletUtil reportPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private String remoteIPAddress;
	private ArrayList<RoleType> portalUserRoleType;
	private String successMessage;
	private String errorMessage;
	private String filName;
	private ArrayList<String> errorList = new ArrayList<String>();  
	private static ComminsApplicationState cas;

	private Collection<Ports> portsList;
	
	
	/*************SETTINGS************************/
	private Settings primaryFeeSetting;
	private Settings notifyCorporateFirmEmail;
	private Settings notifyCorporateFirmSms;
	private Settings notifyCorporateIndividualEmail;
	private Settings notifyCorporateIndividualSMS;
	private Settings etaxPaymentNotifyEmail;
	private Settings etaxPaymentNotifySMS;
	private Settings systemUrl;
	private Settings platformCountry;
	private Settings platformBank;
	private Settings taxCompanyAccount;
	private Settings taxCompanySortCode;
	private Settings sendingEmail;
	private Settings sendingEmailPassword;
	private Settings sendingEmailPort;
	private Settings sendingEmailUsername;
	

	private PortalUserCRUDRights portalUserCRUDRights;
	private CompanyCRUDRights companyCRUDRights;
	private Collection<PaymentHistory> payList;
	Collection<TaxType> taxTypeListing;
	
	public Collection<TaxType> getTaxTypeListing() {
		return taxTypeListing;
	}


	public void setTaxTypeListing(Collection<TaxType> taxTypeListing) {
		this.taxTypeListing = taxTypeListing;
	}


	private Settings approvalDirect;
	private Settings twoStepLogin;
	private Settings approvalProcess;
	
	
	/*****************NAVIGATION*****************/
	private CORE_VIEW coreCurrentTab;
	
	
	
	
	
	
	private String startDate;
	private String endDate;
	private String sourceAccount;
	private String receipientAccount;
	private String sourceSortCode;
	private String tpin;
	private String paymentType;
	private String paymentStatus;
	private String amountLowerLimit;
	private String amountUpperLimit;
	private String assessmentRegNo;
	private String assessmentYear;
	private String portofEntry;
	private String companyRegNo;
	private String showTxnRefNo;
	private String showRectNo;
	private String showSrcAcctNo;
	private String showRecAcctNo;
	private String showSrcSortCode;
	private String showPayStatus;
	private String showPayType;
	private String showTxnAmt;
	private String showAssPaid;
	private String showPayeeTpin;
	private String showPayeeComp;
	private String showPayeeName;
	private String showDatePaid;
	private String showClientPaidFor;
	private String reportEmailSend;
	private String declarantTpin;
	private String assessmentTpin;
	private String selectedReportType;
	private String defaultColumnShow;
	
	public String getReceipientAccount() {
		return receipientAccount;
	}


	public void setReceipientAccount(String receipientAccount) {
		this.receipientAccount = receipientAccount;
	}


	public String getSourceSortCode() {
		return sourceSortCode;
	}


	public void setSourceSortCode(String sourceSortCode) {
		this.sourceSortCode = sourceSortCode;
	}


	public String getTpin() {
		return tpin;
	}


	public void setTpin(String tpin) {
		this.tpin = tpin;
	}


	public String getPortofEntry() {
		return portofEntry;
	}


	public void setPortofEntry(String portofEntry) {
		this.portofEntry = portofEntry;
	}


	public String getShowTxnRefNo() {
		return showTxnRefNo;
	}


	public void setShowTxnRefNo(String showTxnRefNo) {
		this.showTxnRefNo = showTxnRefNo;
	}


	public String getShowRectNo() {
		return showRectNo;
	}


	public void setShowRectNo(String showRectNo) {
		this.showRectNo = showRectNo;
	}


	public String getShowSrcAcctNo() {
		return showSrcAcctNo;
	}


	public void setShowSrcAcctNo(String showSrcAcctNo) {
		this.showSrcAcctNo = showSrcAcctNo;
	}


	public String getShowRecAcctNo() {
		return showRecAcctNo;
	}


	public void setShowRecAcctNo(String showRecAcctNo) {
		this.showRecAcctNo = showRecAcctNo;
	}


	public String getShowSrcSortCode() {
		return showSrcSortCode;
	}


	public void setShowSrcSortCode(String showSrcSortCode) {
		this.showSrcSortCode = showSrcSortCode;
	}


	public String getShowPayStatus() {
		return showPayStatus;
	}


	public void setShowPayStatus(String showPayStatus) {
		this.showPayStatus = showPayStatus;
	}


	public String getShowPayType() {
		return showPayType;
	}


	public void setShowPayType(String showPayType) {
		this.showPayType = showPayType;
	}


	public String getShowTxnAmt() {
		return showTxnAmt;
	}


	public void setShowTxnAmt(String showTxnAmt) {
		this.showTxnAmt = showTxnAmt;
	}


	public String getShowAssPaid() {
		return showAssPaid;
	}


	public void setShowAssPaid(String showAssPaid) {
		this.showAssPaid = showAssPaid;
	}


	public String getShowPayeeTpin() {
		return showPayeeTpin;
	}


	public void setShowPayeeTpin(String showPayeeTpin) {
		this.showPayeeTpin = showPayeeTpin;
	}


	public String getShowPayeeComp() {
		return showPayeeComp;
	}


	public void setShowPayeeComp(String showPayeeComp) {
		this.showPayeeComp = showPayeeComp;
	}


	public String getShowPayeeName() {
		return showPayeeName;
	}


	public void setShowPayeeName(String showPayeeName) {
		this.showPayeeName = showPayeeName;
	}


	public String getShowDatePaid() {
		return showDatePaid;
	}


	public void setShowDatePaid(String showDatePaid) {
		this.showDatePaid = showDatePaid;
	}


	public String getReportEmailSend() {
		return reportEmailSend;
	}


	public void setReportEmailSend(String reportEmailSend) {
		this.reportEmailSend = reportEmailSend;
	}



	
	public String getPaymentType() {
		return paymentType;
	}


	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}


	public String getPaymentStatus() {
		return paymentStatus;
	}


	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}


	public String getAmountLowerLimit() {
		return amountLowerLimit;
	}


	public void setAmountLowerLimit(String amountLowerLimit) {
		this.amountLowerLimit = amountLowerLimit;
	}


	public String getAmountUpperLimit() {
		return amountUpperLimit;
	}


	public void setAmountUpperLimit(String amountUpperLimit) {
		this.amountUpperLimit = amountUpperLimit;
	}


	public String getAssessmentYear() {
		return assessmentYear;
	}


	public void setAssessmentYear(String assessmentYear) {
		this.assessmentYear = assessmentYear;
	}


	public String getCompanyRegNo() {
		return companyRegNo;
	}


	public void setCompanyRegNo(String companyRegNo) {
		this.companyRegNo = companyRegNo;
	}


    
    
    
	public static enum NAVIGATE{
		NAVIGATE_ACTIONS
	}
	
	
	
	public static enum REPORTING_ACTIONS{
		CREATE_A_REPORT_STEP_ONE,
		CREATE_A_PAYMENT_REPORT_STEP_TWO, 
		CREATE_AN_ASSESSMENT_REPORT_STEP_TWO, 
		LOGIN_STEP_TWO, 
		CREATE_A_REPORT_STEP_TWO
	}
	
	/****core section starts here****/
	public static ReportPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		ReportPortletState portletState = null;
		Logger.getLogger(ReportPortletState.class).info("------getInstance");
		try {
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (ReportPortletState) session.getAttribute(ReportPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new ReportPortletState();
					ReportPortletUtil util = new ReportPortletUtil();
					portletState.setReportPortletUtil(util);
					session.setAttribute(ReportPortletState.class.getName(), portletState);
					defaultInit(request, portletState);
					portletState.setCas(ComminsApplicationState.getInstance(request, response));
	            }
				
				ServiceLocator serviceLocator = ServiceLocator.getInstance();
				SwpService swpService = serviceLocator.getSwpService();
			}
			
			//initSettings(portletState, swpService);
			// init settings
			return portletState;
		} catch (Exception e) {
			return null;
		}


	}
	
	
	public void setReportPortletUtil(ReportPortletUtil util) {
		// TODO Auto-generated method stub
		this.reportPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, ReportPortletState portletState) {
		// TODO Auto-generated method stub
		com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
		log.info("------set default init");
		try
		{
		
			if (request.getRemoteUser() != null
					&& !request.getRemoteUser().equals("")) {
				portletState.setCurrentRemoteUserId(request.getRemoteUser());
				log.info(">>>Remote user durin default init: " + portletState.getCurrentRemoteUserId());
				log.info("request.getRemoteUser() =" + request.getRemoteUser());
				log.info("request.getRemoteUser() =" + swpCustomService);
				Long orbitaId = Long.parseLong(request.getRemoteUser());
				portletState.setPortalUser((PortalUser) swpCustomService
						.getPortalUserByOrbitaId(Long.toString(orbitaId)));
				portletState.setRemoteIPAddress(PortalUtil.getHttpServletRequest(request).getRemoteAddr());
				ReportPortletUtil util = ReportPortletUtil.getInstance();
				portletState.setReportPortletUtil(util);
				
				ArrayList<RoleType> pur = portletState.getReportPortletUtil().
						getRoleTypeByPortalUser(portletState.getPortalUser());	
				portletState.setPortalUserRoleType(pur);
				//loadWorkFlowsForCompany(portletState);
				loadSettings(portletState);
				loadAccessLevels(portletState);
				loadTaxTypes(portletState);
			}else{
				log.info(">>>Remote user durin default init2: " + portletState.getCurrentRemoteUserId());
			}
		}
		catch(Exception ex)
		{
			log.error("", ex);
		} finally {
			
			
		}
	}
	
	
	private static void loadAccessLevels(
			ReportPortletState portletState) {
		// TODO Auto-generated method stub
		CompanyCRUDRights ccr = portletState.getReportPortletUtil().getCompanyCRUDRightsByPortalUser(portletState.getPortalUser());
		PortalUserCRUDRights pucr = portletState.getReportPortletUtil().getPortalUserCRUDRightsByPortalUser(portletState.getPortalUser());
		portletState.setCompanyCRUDRights(ccr);
		portletState.setPortalUserCRUDRights(pucr);
	}
	
	private static void loadTaxTypes(
			ReportPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setTaxTypeListing(portletState.getReportPortletUtil().getAllTaxTypes());
	}

	public static boolean loadSettings(ReportPortletState portletState) {
		// TODO Auto-generated method stub
		Settings primaryFeeSetting = portletState.getReportPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_PRIMARY_FEE_DESCRIPTION);
		portletState.setPrimaryFeeSetting(primaryFeeSetting);
		Settings notifyCorporateFirmEmail = portletState.getReportPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_EMAIL);
		portletState.setNotifyCorporateFirmEmail(notifyCorporateFirmEmail);
		Settings notifyCorporateFirmSms = portletState.getReportPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_SMS);
		portletState.setNotifyCorporateFirmSms(notifyCorporateFirmSms);
		Settings notifyCorporateIndividualEmail = portletState.getReportPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_EMAIL);
		portletState.setNotifyCorporateIndividualEmail(notifyCorporateIndividualEmail);
		Settings notifyCorporateIndividualSMS = portletState.getReportPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_SMS);
		portletState.setNotifyCorporateIndividualSMS(notifyCorporateIndividualSMS);
		Settings etaxPaymentNotifyEmail = portletState.getReportPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_EMAIL);
		portletState.setEtaxPaymentNotifyEmail(etaxPaymentNotifyEmail);
		Settings etaxPaymentNotifySMS = portletState.getReportPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_SMS);
		portletState.setEtaxPaymentNotifySMS(etaxPaymentNotifySMS);
		Settings systemUrl = portletState.getReportPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_URL);
		portletState.setSystemUrl(systemUrl);
		
		Settings twostep = portletState.getReportPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_TWO_STEP_LOGIN);
		portletState.setTwoStepLogin(twostep);
		Settings appr = portletState.getReportPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_APPROVAL_PROCESS);
		portletState.setApprovalProcess(appr);
		Settings usname = portletState.getReportPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_USERNAME);
		portletState.setSendingEmailUsername(usname);
		Settings semail = portletState.getReportPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_EMAIL);
		portletState.setSendingEmail(semail);
		Settings pswd = portletState.getReportPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_PASSWORD);
		portletState.setSendingEmailPassword(pswd);
		Settings port = portletState.getReportPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_PORT);
		portletState.setSendingEmailPort(port);
		Settings spc = portletState.getReportPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_COUNTRY);
		portletState.setPlatformCountry(spc);
		Settings spb = portletState.getReportPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATORM_BANK);
		portletState.setPlatformBank(spb);
		
		Settings zacc = portletState.getReportPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_NUMBER);
		portletState.setTaxCompanyAccount(zacc);
		Settings zacs = portletState.getReportPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_SORT_CODE);
		portletState.setTaxCompanySortCode(zacs);
		
		return false;
	}


	public void setApprovalDirect(Settings approvalDirect) {
		// TODO Auto-generated method stub
		this.approvalDirect = approvalDirect;
	}
	
	public Settings getApprovalDirect() {
		// TODO Auto-generated method stub
		return this.approvalDirect;
	}


	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			ReportPortletState portletState) {

		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			ReportPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			ReportPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			ReportPortletState.log.debug("Error including error message", e);
		}
	}
	
	public void setSuccessMessage(String successMessage)
	{
		this.successMessage=successMessage;
	}
	
	
	public String getErrorMessage()
	{
		return this.errorMessage;
	}
	
	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
		
	}

	private void setCurrentRemoteUserId(String remoteUser) {
		// TODO Auto-generated method stub
		this.remoteUser = remoteUser;
	}
	
	private String getCurrentRemoteUserId() {
		// TODO Auto-generated method stub
		return this.remoteUser;
	}


	private void setPortalUser(PortalUser portalUser) {
		// TODO Auto-generated method stub
		this.portalUser = portalUser;
	}
	
	public PortalUser getPortalUser() {
		// TODO Auto-generated method stub
		return this.portalUser;
	}


	public ReportPortletUtil getReportPortletUtil() {
		// TODO Auto-generated method stub
		return this.reportPortletUtil;
	}


	public ArrayList<RoleType> getPortalUserRoleType() {
		return portalUserRoleType;
	}


	public void setPortalUserRoleType(ArrayList<RoleType> portalUserRoleType) {
		this.portalUserRoleType = portalUserRoleType;
	}
	
	

	/****company creation section starts here****/
	

	public ArrayList<String> getErrorList() {
		return errorList;
	}


	public void setErrorList(ArrayList<String> errorList) {
		this.errorList = errorList;
	}
	
	

	public String getRemoteIPAddress() {
		return remoteIPAddress;
	}

	public void setRemoteIPAddress(String remoteIPAddress) {
		this.remoteIPAddress = remoteIPAddress;
	}

	
	

	public void reinitializeForTaxBreakDown(
			ReportPortletState portletState) {
		// TODO Auto-generated method stub
		
	}



	public CORE_VIEW getCoreCurrentTab() {
		return coreCurrentTab;
	}


	public void setCoreCurrentTab(CORE_VIEW coreCurrentTab) {
		this.coreCurrentTab = coreCurrentTab;
	}


	public Settings getApprovalProcess() {
		return approvalProcess;
	}


	public void setApprovalProcess(Settings approvalProcess) {
		this.approvalProcess = approvalProcess;
	}


	public Settings getTwoStepLogin() {
		return twoStepLogin;
	}


	public void setTwoStepLogin(Settings twoStepLogin) {
		this.twoStepLogin = twoStepLogin;
	}


	public Settings getPrimaryFeeSetting() {
		return primaryFeeSetting;
	}


	public void setPrimaryFeeSetting(Settings primaryFeeSetting) {
		this.primaryFeeSetting = primaryFeeSetting;
	}


	public Settings getNotifyCorporateFirmEmail() {
		return notifyCorporateFirmEmail;
	}


	public void setNotifyCorporateFirmEmail(Settings notifyCorporateFirmEmail) {
		this.notifyCorporateFirmEmail = notifyCorporateFirmEmail;
	}


	public Settings getNotifyCorporateFirmSms() {
		return notifyCorporateFirmSms;
	}


	public void setNotifyCorporateFirmSms(Settings notifyCorporateFirmSms) {
		this.notifyCorporateFirmSms = notifyCorporateFirmSms;
	}


	public Settings getNotifyCorporateIndividualEmail() {
		return notifyCorporateIndividualEmail;
	}


	public void setNotifyCorporateIndividualEmail(
			Settings notifyCorporateIndividualEmail) {
		this.notifyCorporateIndividualEmail = notifyCorporateIndividualEmail;
	}


	public Settings getNotifyCorporateIndividualSMS() {
		return notifyCorporateIndividualSMS;
	}


	public void setNotifyCorporateIndividualSMS(
			Settings notifyCorporateIndividualSMS) {
		this.notifyCorporateIndividualSMS = notifyCorporateIndividualSMS;
	}


	public Settings getEtaxPaymentNotifyEmail() {
		return etaxPaymentNotifyEmail;
	}


	public void setEtaxPaymentNotifyEmail(Settings etaxPaymentNotifyEmail) {
		this.etaxPaymentNotifyEmail = etaxPaymentNotifyEmail;
	}


	public Settings getEtaxPaymentNotifySMS() {
		return etaxPaymentNotifySMS;
	}


	public void setEtaxPaymentNotifySMS(Settings etaxPaymentNotifySMS) {
		this.etaxPaymentNotifySMS = etaxPaymentNotifySMS;
	}


	public Settings getSystemUrl() {
		return systemUrl;
	}


	public void setSystemUrl(Settings systemUrl) {
		this.systemUrl = systemUrl;
	}


	public PortalUserCRUDRights getPortalUserCRUDRights() {
		return portalUserCRUDRights;
	}


	public void setPortalUserCRUDRights(PortalUserCRUDRights portalUserCRUDRights) {
		this.portalUserCRUDRights = portalUserCRUDRights;
	}


	public CompanyCRUDRights getCompanyCRUDRights() {
		return companyCRUDRights;
	}


	public void setCompanyCRUDRights(CompanyCRUDRights companyCRUDRights) {
		this.companyCRUDRights = companyCRUDRights;
	}


	public Settings getPlatformCountry() {
		return platformCountry;
	}


	public void setPlatformCountry(Settings platformCountry) {
		this.platformCountry = platformCountry;
	}


	public Settings getTaxCompanyAccount() {
		return taxCompanyAccount;
	}


	public void setTaxCompanyAccount(Settings taxCompanyAccount) {
		this.taxCompanyAccount = taxCompanyAccount;
	}


	public Settings getTaxCompanySortCode() {
		return taxCompanySortCode;
	}


	public void setTaxCompanySortCode(Settings taxCompanySortCode) {
		this.taxCompanySortCode = taxCompanySortCode;
	}


	public Settings getPlatformBank() {
		return platformBank;
	}


	public void setPlatformBank(Settings platformBank) {
		this.platformBank = platformBank;
	}


	public Settings getSendingEmail() {
		return sendingEmail;
	}


	public void setSendingEmail(Settings sendingEmail) {
		this.sendingEmail = sendingEmail;
	}


	public Settings getSendingEmailPassword() {
		return sendingEmailPassword;
	}


	public void setSendingEmailPassword(Settings sendingEmailPassword) {
		this.sendingEmailPassword = sendingEmailPassword;
	}


	public Settings getSendingEmailPort() {
		return sendingEmailPort;
	}


	public void setSendingEmailPort(Settings sendingEmailPort) {
		this.sendingEmailPort = sendingEmailPort;
	}


	public Settings getSendingEmailUsername() {
		return sendingEmailUsername;
	}


	public void setSendingEmailUsername(Settings sendingEmailUsername) {
		this.sendingEmailUsername = sendingEmailUsername;
	}


	public Collection<Ports> getPortsList() {
		return portsList;
	}


	public void setPortsList(Collection<Ports> portsList) {
		this.portsList = portsList;
	}


	public String getStartDate() {
		return startDate;
	}


	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}


	public String getSourceAccount() {
		return sourceAccount;
	}


	public void setSourceAccount(String sourceAccount) {
		this.sourceAccount = sourceAccount;
	}


	public String getAssessmentRegNo() {
		return assessmentRegNo;
	}


	public void setAssessmentRegNo(String assessmentRegNo) {
		this.assessmentRegNo = assessmentRegNo;
	}


	public String getEndDate() {
		return endDate;
	}


	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}


	public String getDeclarantTpin() {
		return declarantTpin;
	}


	public void setDeclarantTpin(String declarantTpin) {
		this.declarantTpin = declarantTpin;
	}


	public String getAssessmentTpin() {
		return assessmentTpin;
	}


	public void setAssessmentTpin(String assessmentTpin) {
		this.assessmentTpin = assessmentTpin;
	}


	public Collection<PaymentHistory> getPayList() {
		return payList;
	}


	public void setPayList(Collection<PaymentHistory> payList) {
		this.payList = payList;
	}


	public String getSelectedReportType() {
		return selectedReportType;
	}


	public void setSelectedReportType(String selectedReportType) {
		this.selectedReportType = selectedReportType;
	}


	public String getDefaultColumnShow() {
		return defaultColumnShow;
	}


	public void setDefaultColumnShow(String defaultColumnShow) {
		this.defaultColumnShow = defaultColumnShow;
	}


	public String getShowClientPaidFor() {
		return showClientPaidFor;
	}


	public void setShowClientPaidFor(String showClientPaidFor) {
		this.showClientPaidFor = showClientPaidFor;
	}


	public String getFilName() {
		return filName;
	}


	public void setFilName(String filName) {
		this.filName = filName;
	}


	public static ComminsApplicationState getCas() {
		return cas;
	}


	public static void setCas(ComminsApplicationState cas) {
		ReportPortletState.cas = cas;
	}


	
}
