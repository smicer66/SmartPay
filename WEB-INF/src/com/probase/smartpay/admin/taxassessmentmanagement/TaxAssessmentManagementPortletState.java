package com.probase.smartpay.admin.taxassessmentmanagement;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;

import org.apache.log4j.Logger;

import smartpay.entity.Assessment;
import smartpay.entity.AuthorizePanel;
import smartpay.entity.AuthorizePanelCombination;
import smartpay.entity.BankBranches;
import smartpay.entity.Company;
import smartpay.entity.FeeDescription;
import smartpay.entity.FinancialAmountRestriction;
import smartpay.entity.PortalUser;
import smartpay.entity.Ports;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.TpinInfo;
import smartpay.entity.enumerations.CompanyStatusConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.service.SwpService;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.probase.smartpay.commins.BalanceInquiry;
import com.probase.smartpay.commins.ComminsApplicationState;
import com.probase.smartpay.commins.InterestToBePaid;
import com.probase.smartpay.commins.Util;
import com.probase.smartpay.commins.ProbaseConstants.CORE_VIEW;
import com.probase.smartpay.commins.TaxAssessment;
import com.probase.smartpay.commins.TaxBreakDownResponse;
import com.sf.primepay.smartpay13.ServiceLocator;

public class TaxAssessmentManagementPortletState {

	private static Logger log = Logger.getLogger(TaxAssessmentManagementPortletState.class);
	private static TaxAssessmentManagementPortletUtil taxAssessmentManagementPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private String remoteIPAddress;
	private ArrayList<RoleType> portalUserRoleType;
	private static ComminsApplicationState cas;
	

	private String successMessage;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	
	private Collection<Assessment> allAssessmentListing;
	private Collection<Ports> allPortListing;
	private Collection<Ports> allAgentClients;
	private Collection<Assessment> allWorkFlowAssessmentsRegNo;
//	private String settingsSource;
//	private String settingsCountry;
	private String selectedPortId;
	private String selectedYear;
	private CORE_VIEW coreCurrentTab;
	private String selectedAssessmentsClicked;
	private String selectedClientTPID;
	
	
	
	
	private Settings settingsZRAAccount;
	private Settings settingsZRASortCode;
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
	private Settings sendingEmail;
	private Settings sendingEmailPassword;
	private Settings sendingEmailPort;
	private Settings sendingEmailUsername;
	private Settings approvalProcess;
	private Settings twoStepLogin;
	
	
	private Settings applicationName;
	private Settings mobileApplicationName;
	private Settings proxyUsername;
	private Settings proxyPassword;
	private Settings proxyHost;
	private Settings proxyPort;
	private Settings emailFooter;
	private Settings sameLevelLoginCombination;
	private Settings bankName;
	private Settings currency;
	private Settings bankPaymentWebServiceUrl;
	private Settings taxBodyWebServiceUrl;
	
	
	
	
	/*****Response from Probase Service****/
	private ArrayList<String> amountToBePaid;
	private ArrayList<String> assessmentNumber;
	private ArrayList<String> assessmentStatus;
	private ArrayList<String> assessmentYear;
	private ArrayList<String> interestAvailable;
	private ArrayList<String> portOfEntry;
	private ArrayList<String> registrationDate;
	private ArrayList<String> registrationNumber;
	private ArrayList<String> registrationSerial;
	private BalanceInquiry balanceInquiry;
	
	
	
	
	private String clientTPIN;
	private String country;
	private String reasoncode;
	private String reasonDescription;
	private String source;
	private String sourceID;
	private String timestamp;
	private String tpin_declarantCode;
	private String type;
	private String platformFlag;
	private String regSerial;
	private boolean demoConnection;
	
	/*****************NAVIGATION*****************/
	private VIEW_TABS currentTab;
	private List<HashMap> allList;
	private Collection<InterestToBePaid> allInterestToBePaid;
	
    
    public static enum VIEW_TABS{
    	TAX_ASSESSMENTS_LISTING_SIMPLE, TAX_ASSESSMENTS_LISTING_ADV, VIEW_A_TAX_ASSESSMENT
    }
    
    public static enum TAX_ASSESSMENT_VIEW
    {
    	TAX_ASSESSMENTS_LISTING_SIMPLE, TAX_ASSESSMENTS_LISTING_ADV, VIEW_A_TAX_ASSESSMENT
    }
    
    
	public static enum NAVIGATE{
		NAVIGATE_ACTIONS
	}
	
	
	public static enum TAX_ASSESSMENT_ACTION{
		VIEW_A_TAX_ASSESSMENT_SIMPLE, VIEW_A_TAX_ASSESSMENT_ADV, HANDLE_ASSESSMENT_LISTING
	}
	
	
	
	/****core section starts here****/
	public static TaxAssessmentManagementPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		TaxAssessmentManagementPortletState portletState = null;
		Logger.getLogger(TaxAssessmentManagementPortletState.class).info("------getInstance");
		try {
			
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (TaxAssessmentManagementPortletState) session.getAttribute(TaxAssessmentManagementPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new TaxAssessmentManagementPortletState();
					TaxAssessmentManagementPortletUtil util = new TaxAssessmentManagementPortletUtil();
					portletState.setTaxAssessmentManagementPortletUtil(util);
					session.setAttribute(TaxAssessmentManagementPortletState.class.getName(), portletState);
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
	
	
	private void setTaxAssessmentManagementPortletUtil(TaxAssessmentManagementPortletUtil util) {
		// TODO Auto-generated method stub
		this.taxAssessmentManagementPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, TaxAssessmentManagementPortletState portletState) {
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
				TaxAssessmentManagementPortletUtil util = TaxAssessmentManagementPortletUtil.getInstance();
				portletState.setTaxAssessmentManagementPortletUtil(util);
				
				ArrayList<RoleType> pur = portletState.getTaxAssessmentManagementPortletUtil().
						getRoleTypeByPortalUser(portletState.getPortalUser());	
				portletState.setPortalUserRoleType(pur);
				loadAssessmentIdsByCompany(portletState, portletState.getPortalUser().getCompany());
				loadSettings(portletState);
				loadPorts(portletState);
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

	

	public static void loadWorkFlowAssessmentIds(
			TaxAssessmentManagementPortletState portletState) {
		// TODO Auto-generated method stub
		Collection<Assessment> intColl = portletState.getTaxAssessmentManagementPortletUtil().getWorkFlowsByCompany(portletState.getPortalUser().getCompany());
		portletState.setAllWorkFlowAssessments(intColl);
		
	}
	
	
	public static void loadAssessmentIdsByCompany(
			TaxAssessmentManagementPortletState portletState, Company company) {
		// TODO Auto-generated method stub
		log.info("Load Assessments Saved by Company");
		Collection<Assessment> intColl = portletState.getTaxAssessmentManagementPortletUtil().getAssessmentsByCompany(company);
		portletState.setAllWorkFlowAssessments(intColl);
		
	}


	private static void loadPorts(
			TaxAssessmentManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setAllPortListing(portletState.getTaxAssessmentManagementPortletUtil().getAllPortListing());
	}


	private static void loadSettings(
			TaxAssessmentManagementPortletState portletState) {
		// TODO Auto-generated method stub
		Settings primaryFeeSetting = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PRIMARY_FEE_DESCRIPTION);
		portletState.setPrimaryFeeSetting(primaryFeeSetting);
		Settings notifyCorporateFirmEmail = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_EMAIL);
		portletState.setNotifyCorporateFirmEmail(notifyCorporateFirmEmail);
		Settings notifyCorporateFirmSms = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_SMS);
		portletState.setNotifyCorporateFirmSms(notifyCorporateFirmSms);
		Settings notifyCorporateIndividualEmail = portletState.getTaxAssessmentManagementPortletUtil()
				.getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_EMAIL);
		portletState.setNotifyCorporateIndividualEmail(notifyCorporateIndividualEmail);
		Settings notifyCorporateIndividualSMS = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_SMS);
		portletState.setNotifyCorporateIndividualSMS(notifyCorporateIndividualSMS);
		Settings etaxPaymentNotifyEmail = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_EMAIL);
		portletState.setEtaxPaymentNotifyEmail(etaxPaymentNotifyEmail);
		Settings etaxPaymentNotifySMS = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_SMS);
		portletState.setEtaxPaymentNotifySMS(etaxPaymentNotifySMS);
		Settings systemUrl = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_URL);
		portletState.setSystemUrl(systemUrl);
		
		Settings twostep = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_TWO_STEP_LOGIN);
		portletState.setTwoStepLogin(twostep);
		Settings appr = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_APPROVAL_PROCESS);
		portletState.setApprovalProcess(appr);
		Settings usname = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_USERNAME);
		portletState.setSendingEmailUsername(usname);
		Settings semail = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_EMAIL);
		portletState.setSendingEmail(semail);
		Settings pswd = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_PASSWORD);
		portletState.setSendingEmailPassword(pswd);
		Settings port = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_PORT);
		portletState.setSendingEmailPort(port);
		Settings spc = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_COUNTRY);
		portletState.setPlatformCountry(spc);
		Settings spb = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATORM_BANK);
		portletState.setPlatformBank(spb);
		Settings zacc = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_NUMBER);
		portletState.setSettingsZRAAccount(zacc);
		Settings zacs = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_SORT_CODE);
		portletState.setSettingsZRASortCode(zacs);
		
		
		
		Settings app = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_APPLICATION_NAME);
		portletState.setApplicationName(app);
		Settings mapp = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_MOBILE_APPLICATION_NAME);
		portletState.setMobileApplicationName(mapp);
		Settings proxH = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_HOST);
		portletState.setProxyHost(proxH);
		Settings proxP = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_PORT);
		portletState.setProxyPort(proxP);
		Settings proxU = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_USERNAME);
		portletState.setProxyUsername(proxU);
		Settings proxPwd = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_PASSWORD);
		portletState.setProxyPassword(proxPwd);
		Settings bank = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_BANK_NAME);
		portletState. setBankName(bank);
		Settings currency = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_CURRENCY);
		portletState.setCurrency(currency);
		Settings bpwsu = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_BANK_PAYMENT_WS_URL);
		portletState.setBankPaymentWebServiceUrl(bpwsu);
		Settings zwsu = portletState.getTaxAssessmentManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_TAX_BODY_WS_URL);
		portletState.setTaxBodyWebServiceUrl(zwsu);
	}


	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			TaxAssessmentManagementPortletState portletState) {

		
			portletState.setErrorMessage(errorMessage);
	
			try {
				SessionErrors.add(aReq, "errorMessage");
			} catch (Exception e) {
				TaxAssessmentManagementPortletState.log.debug("Error including error message", e);
			}
		
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			TaxAssessmentManagementPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			TaxAssessmentManagementPortletState.log.debug("Error including error message", e);
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


	public TaxAssessmentManagementPortletUtil getTaxAssessmentManagementPortletUtil() {
		// TODO Auto-generated method stub
		return this.taxAssessmentManagementPortletUtil;
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
	
	
	

	


	public VIEW_TABS getCurrentTab() {
		return currentTab;
	}



	public void setCurrentTab(VIEW_TABS currentTab) {
		this.currentTab = currentTab;
	}

	public String getRemoteIPAddress() {
		return remoteIPAddress;
	}

	public void setRemoteIPAddress(String remoteIPAddress) {
		this.remoteIPAddress = remoteIPAddress;
	}

	public Collection<Assessment> getAllAssessmentListing() {
		return allAssessmentListing;
	}

	public void setAllAssessmentListing(Collection<Assessment> allAssessmentListing) {
		this.allAssessmentListing = allAssessmentListing;
	}


//	public String getSettingsSource() {
//		return settingsSource;
//	}
//
//
//	public void setSettingsSource(String settingsSource) {
//		this.settingsSource = settingsSource;
//	}
//
//
//	public String getSettingsCountry() {
//		return settingsCountry;
//	}
//
//
//	public void setSettingsCountry(String settingsCountry) {
//		this.settingsCountry = settingsCountry;
//	}


	public ArrayList<String> getAmountToBePaid() {
		return amountToBePaid;
	}


	public void setAmountToBePaid(ArrayList<String> amountToBePaid) {
		this.amountToBePaid = amountToBePaid;
	}


	public ArrayList<String> getAssessmentNumber() {
		return assessmentNumber;
	}


	public void setAssessmentNumber(ArrayList<String> assessmentNumber) {
		this.assessmentNumber = assessmentNumber;
	}


	public ArrayList<String> getAssessmentStatus() {
		return assessmentStatus;
	}


	public void setAssessmentStatus(ArrayList<String> assessmentStatus) {
		this.assessmentStatus = assessmentStatus;
	}


	public ArrayList<String> getAssessmentYear() {
		return assessmentYear;
	}


	public void setAssessmentYear(ArrayList<String> assessmentYear) {
		this.assessmentYear = assessmentYear;
	}


	public ArrayList<String> getInterestAvailable() {
		return interestAvailable;
	}


	public void setInterestAvailable(ArrayList<String> interestAvailable) {
		this.interestAvailable = interestAvailable;
	}


	public ArrayList<String> getPortOfEntry() {
		return portOfEntry;
	}


	public void setPortOfEntry(ArrayList<String> portOfEntry) {
		this.portOfEntry = portOfEntry;
	}


	public ArrayList<String> getRegistrationDate() {
		return registrationDate;
	}


	public void setRegistrationDate(ArrayList<String> registrationDate) {
		this.registrationDate = registrationDate;
	}


	public ArrayList<String> getRegistrationNumber() {
		return registrationNumber;
	}


	public void setRegistrationNumber(ArrayList<String> registrationNumber) {
		this.registrationNumber = registrationNumber;
	}


	public ArrayList<String> getRegistrationSerial() {
		return registrationSerial;
	}


	public void setRegistrationSerial(ArrayList<String> registrationSerial) {
		this.registrationSerial = registrationSerial;
	}


	public Collection<Ports> getAllPortListing() {
		return allPortListing;
	}


	public void setAllPortListing(Collection<Ports> allPortListing) {
		this.allPortListing = allPortListing;
	}


	public Collection<Ports> getAllAgentClients() {
		return allAgentClients;
	}


	public void setAllAgentClients(Collection<Ports> allAgentClients) {
		this.allAgentClients = allAgentClients;
	}


	public String getSelectedPortId() {
		return selectedPortId;
	}


	public void setSelectedPortId(String selectedPortId) {
		this.selectedPortId = selectedPortId;
	}


	public String getSelectedYear() {
		return selectedYear;
	}


	public void setSelectedYear(String selectedYear) {
		this.selectedYear = selectedYear;
	}


	public String getClientTPIN() {
		return clientTPIN;
	}


	public void setClientTPIN(String clientTPIN) {
		this.clientTPIN = clientTPIN;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}


	public String getReasoncode() {
		return reasoncode;
	}


	public void setReasoncode(String reasoncode) {
		this.reasoncode = reasoncode;
	}


	public String getReasonDescription() {
		return reasonDescription;
	}


	public void setReasonDescription(String reasonDescription) {
		this.reasonDescription = reasonDescription;
	}


	public String getSource() {
		return source;
	}


	public void setSource(String source) {
		this.source = source;
	}


	public String getSourceID() {
		return sourceID;
	}


	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}


	public String getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}


	public String getTpin_declarantCode() {
		return tpin_declarantCode;
	}


	public void setTpin_declarantCode(String tpin_declarantCode) {
		this.tpin_declarantCode = tpin_declarantCode;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public void setTaxBreakDownList(List<HashMap> allList) {
		// TODO Auto-generated method stub
		this.allList = allList;
	}
	
	public List<HashMap> getTaxBreakDownList() {
		// TODO Auto-generated method stub
		return this.allList;
	}


	public String getPlatformFlag() {
		return platformFlag;
	}


	public void setPlatformFlag(String platformFlag) {
		this.platformFlag = platformFlag;
	}


	public String getRegSerial() {
		return regSerial;
	}


	public void setRegSerial(String regSerial) {
		this.regSerial = regSerial;
	}


	public void reinitializeForTaxBreakDown(
			TaxAssessmentManagementPortletState portletState) {
		// TODO Auto-generated method stub
		
	}


	public BalanceInquiry getBalanceInquiry() {
		return balanceInquiry;
	}


	public void setBalanceInquiry(BalanceInquiry balanceInquiry) {
		this.balanceInquiry = balanceInquiry;
	}


	public CORE_VIEW getCoreCurrentTab() {
		return coreCurrentTab;
	}


	public void setCoreCurrentTab(CORE_VIEW coreCurrentTab) {
		this.coreCurrentTab = coreCurrentTab;
	}


	public Collection<Assessment> getAllWorkFlowAssessments() {
		return allWorkFlowAssessmentsRegNo;
	}


	public void setAllWorkFlowAssessments(Collection<Assessment> allWorkFlowAssessmentsRegNo) {
		this.allWorkFlowAssessmentsRegNo = allWorkFlowAssessmentsRegNo;
	}


	public Settings getSettingsZRAAccount() {
		return settingsZRAAccount;
	}


	public void setSettingsZRAAccount(Settings settingsZRAAccount) {
		this.settingsZRAAccount = settingsZRAAccount;
	}


	public Settings getSettingsZRASortCode() {
		return settingsZRASortCode;
	}


	public void setSettingsZRASortCode(Settings settingsZRASortCode) {
		this.settingsZRASortCode = settingsZRASortCode;
	}


	public String getSelectedAssessmentsClicked() {
		return selectedAssessmentsClicked;
	}


	public void setSelectedAssessmentsClicked(String selectedAssessmentsClicked) {
		this.selectedAssessmentsClicked = selectedAssessmentsClicked;
	}


	public String getSelectedClientTPID() {
		return selectedClientTPID;
	}


	public void setSelectedClientTPID(String selectedClientTPID) {
		this.selectedClientTPID = selectedClientTPID;
	}


	public void setAllInterestToBePaid(
			Collection<InterestToBePaid> interestListing) {
		// TODO Auto-generated method stub
		this.allInterestToBePaid = interestListing;
	}
	
	public Collection<InterestToBePaid> getAllInterestToBePaid()
	{
		return this.allInterestToBePaid;
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




	public Settings getPlatformCountry() {
		return platformCountry;
	}




	public void setPlatformCountry(Settings platformCountry) {
		this.platformCountry = platformCountry;
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




	public Settings getSendingEmailUsername() {
		return sendingEmailUsername;
	}




	public void setSendingEmailUsername(Settings sendingEmailUsername) {
		this.sendingEmailUsername = sendingEmailUsername;
	}




	public Settings getSendingEmailPort() {
		return sendingEmailPort;
	}




	public void setSendingEmailPort(Settings sendingEmailPort) {
		this.sendingEmailPort = sendingEmailPort;
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


	public Settings getApplicationName() {
		return applicationName;
	}


	public void setApplicationName(Settings applicationName) {
		this.applicationName = applicationName;
	}


	public Settings getMobileApplicationName() {
		return mobileApplicationName;
	}


	public void setMobileApplicationName(Settings mobileApplicationName) {
		this.mobileApplicationName = mobileApplicationName;
	}


	public Settings getProxyHost() {
		return proxyHost;
	}


	public void setProxyHost(Settings proxyHost) {
		this.proxyHost = proxyHost;
	}


	public Settings getProxyPort() {
		return proxyPort;
	}


	public void setProxyPort(Settings proxyPort) {
		this.proxyPort = proxyPort;
	}


	public Settings getEmailFooter() {
		return emailFooter;
	}


	public void setEmailFooter(Settings emailFooter) {
		this.emailFooter = emailFooter;
	}


	public Settings getSameLevelLoginCombination() {
		return sameLevelLoginCombination;
	}


	public void setSameLevelLoginCombination(Settings sameLevelLoginCombination) {
		this.sameLevelLoginCombination = sameLevelLoginCombination;
	}


	public Settings getBankName() {
		return bankName;
	}


	public void setBankName(Settings bankName) {
		this.bankName = bankName;
	}


	public Settings getCurrency() {
		return currency;
	}


	public void setCurrency(Settings currency) {
		currency = currency;
	}


	public Settings getProxyUsername() {
		return proxyUsername;
	}


	public void setProxyUsername(Settings proxyUsername) {
		this.proxyUsername = proxyUsername;
	}


	public Settings getBankPaymentWebServiceUrl() {
		return bankPaymentWebServiceUrl;
	}


	public void setBankPaymentWebServiceUrl(Settings bankPaymentWebServiceUrl) {
		this.bankPaymentWebServiceUrl = bankPaymentWebServiceUrl;
	}


	public Settings getProxyPassword() {
		return proxyPassword;
	}


	public void setProxyPassword(Settings proxyPassword) {
		this.proxyPassword = proxyPassword;
	}


	public Settings getTaxBodyWebServiceUrl() {
		return taxBodyWebServiceUrl;
	}


	public void setTaxBodyWebServiceUrl(Settings taxBodyWebServiceUrl) {
		this.taxBodyWebServiceUrl = taxBodyWebServiceUrl;
	}


	public static ComminsApplicationState getCas() {
		return cas;
	}


	public static void setCas(ComminsApplicationState cas) {
		TaxAssessmentManagementPortletState.cas = cas;
	}
}
