package com.probase.smartpay.workflow;

import java.io.IOException;
import java.net.MalformedURLException;
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
import smartpay.entity.WorkFlow;
import smartpay.entity.WorkFlowAssessment;
import smartpay.entity.enumerations.CompanyStatusConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.service.SwpService;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.probase.smartpay.commins.BalanceInquiry;
import com.probase.smartpay.commins.ComminsApplicationState;
import com.probase.smartpay.commins.Util;
import com.probase.smartpay.commins.ProbaseConstants.CORE_VIEW;
import com.probase.smartpay.commins.TaxAssessment;
import com.probase.smartpay.commins.TaxBreakDownResponse;
import com.sf.primepay.smartpay13.ServiceLocator;

public class WorkFlowPortletState {

	



	private static Logger log = Logger.getLogger(WorkFlowPortletState.class);
	private static WorkFlowPortletUtil workFlowPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private String remoteIPAddress;
	private static ComminsApplicationState cas;
	private ArrayList<RoleType> portalUserRoleType;
	

	private String successMessage;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	private Collection<WorkFlowAssessment> workFlowAssessmentList;
	private String selectedWorkFlow;
	private Collection<Assessment> assessmentListing;
	
	private Collection<Assessment> allAssessmentListing;
	private BalanceInquiry balanceInquiry;
	private WorkFlow selectedSearchedWorkFlow;
	
	private CORE_VIEW coreCurrentTab;
	private String token;
	private String selectedAssessmentsClicked;
	private String selectedClientTPID;
	private Settings primaryFeeSetting;
	private Settings notifyCorporateFirmEmail;
	private Settings settingsZRAAccount;
	private Settings settingsZRASortCode;
	
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
	private Settings taxBodyWSURL;
	private String reason;
	
	
	/*****************NAVIGATION*****************/
	private VIEW_TABS currentTab;
	private Collection<HashMap> allList;
	private String settingsCountry;
	private String settingsSource;
	private Collection<WorkFlow> workFlowList;
	
    
    public static enum VIEW_TABS{
    	FIND_ASSESSMENT_BY_TOKEN, WORK_FLOW_LISTINGS
    }
    
    public static enum WORK_FLOW_VIEW
    {
    	FIND_ASSESSMENT_BY_TOKEN, WORK_FLOW_LISTINGS
    }
    
    
	public static enum NAVIGATE{
		NAVIGATE_ACTIONS
	}
	
	
	public static enum WORK_FLOW_ACTION{
		FIND_ASSESSMENT_BY_TOKEN, HANDLE_WORKFLOW_LISTINGS, HANDLE_WORKFLOW_FOR_ONE_ASSESSMENT
	}
	
	
	/****core section starts here****/
	public static WorkFlowPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		WorkFlowPortletState portletState = null;
		Logger.getLogger(WorkFlowPortletState.class).info("------getInstance");
		try {
			
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (WorkFlowPortletState) session.getAttribute(WorkFlowPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new WorkFlowPortletState();
					WorkFlowPortletUtil util = new WorkFlowPortletUtil();
					portletState.setWorkFlowPortletUtil(util);
					session.setAttribute(WorkFlowPortletState.class.getName(), portletState);
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
	
	
	public void setWorkFlowPortletUtil(WorkFlowPortletUtil util) {
		// TODO Auto-generated method stub
		this.workFlowPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, WorkFlowPortletState portletState) {
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
				WorkFlowPortletUtil util = WorkFlowPortletUtil.getInstance();
				portletState.setWorkFlowPortletUtil(util);
				
				ArrayList<RoleType> pur = portletState.getWorkFlowPortletUtil().
						getRoleTypeByPortalUser(portletState.getPortalUser());	
				portletState.setPortalUserRoleType(pur);
				//loadWorkFlowsForCompany(portletState);
				loadWorkFlowsForPortalUser(portletState);
				loadWorkFlowListForPortalUser(portletState);
				loadSettings(portletState);
				loadBalance(portletState);
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

	private static void loadBalance(WorkFlowPortletState portletState) {
		// TODO Auto-generated method stub
		BalanceInquiry bi = null;
		try {
			bi = Util.getBalanceInquiry(portletState.getApplicationName().getValue(), 
					"SMARTPAY:ZM:REVENUE:BALANCE:REQUEST", "TYPE", portletState.getPortalUser().getCompany().getAccountNumber(), 
					"ZMW");
			portletState.setBalanceInquiry(bi);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private static void loadSettings(
			WorkFlowPortletState portletState) {
		// TODO Auto-generated method stub
		Settings primaryFeeSetting = portletState.getWorkFlowPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_PRIMARY_FEE_DESCRIPTION);
		portletState.setPrimaryFeeSetting(primaryFeeSetting);
		Settings notifyCorporateFirmEmail = portletState.getWorkFlowPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_EMAIL);
		portletState.setNotifyCorporateFirmEmail(notifyCorporateFirmEmail);
		Settings notifyCorporateFirmSms = portletState.getWorkFlowPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_SMS);
		portletState.setNotifyCorporateFirmSms(notifyCorporateFirmSms);
		Settings notifyCorporateIndividualEmail = portletState.getWorkFlowPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_EMAIL);
		portletState.setNotifyCorporateIndividualEmail(notifyCorporateIndividualEmail);
		Settings notifyCorporateIndividualSMS = portletState.getWorkFlowPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_SMS);
		portletState.setNotifyCorporateIndividualSMS(notifyCorporateIndividualSMS);
		Settings etaxPaymentNotifyEmail = portletState.getWorkFlowPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_EMAIL);
		portletState.setEtaxPaymentNotifyEmail(etaxPaymentNotifyEmail);
		Settings etaxPaymentNotifySMS = portletState.getWorkFlowPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_SMS);
		portletState.setEtaxPaymentNotifySMS(etaxPaymentNotifySMS);
		Settings systemUrl = portletState.getWorkFlowPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_URL);
		portletState.setSystemUrl(systemUrl);
		
		Settings twostep = portletState.getWorkFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_TWO_STEP_LOGIN);
		portletState.setTwoStepLogin(twostep);
		Settings appr = portletState.getWorkFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_APPROVAL_PROCESS);
		portletState.setApprovalProcess(appr);
		Settings usname = portletState.getWorkFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_USERNAME);
		portletState.setSendingEmailUsername(usname);
		Settings semail = portletState.getWorkFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_EMAIL);
		portletState.setSendingEmail(semail);
		Settings pswd = portletState.getWorkFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_PASSWORD);
		portletState.setSendingEmailPassword(pswd);
		Settings port = portletState.getWorkFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_PORT);
		portletState.setSendingEmailPort(port);
		Settings spc = portletState.getWorkFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_COUNTRY);
		portletState.setPlatformCountry(spc);
		Settings spb = portletState.getWorkFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATORM_BANK);
		portletState.setPlatformBank(spb);
		Settings zacc = portletState.getWorkFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_NUMBER);
		portletState.setSettingsZRAAccount(zacc);
		Settings zacs = portletState.getWorkFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_SORT_CODE);
		portletState.setSettingsZRASortCode(zacs);
		
		
		
		Settings app = portletState.getWorkFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_APPLICATION_NAME);
		portletState.setApplicationName(app);
		Settings mapp = portletState.getWorkFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_MOBILE_APPLICATION_NAME);
		portletState.setMobileApplicationName(mapp);
		Settings proxH = portletState.getWorkFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_HOST);
		portletState.setProxyHost(proxH);
		Settings proxP = portletState.getWorkFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_PORT);
		portletState.setProxyPort(proxP);
		Settings proxU = portletState.getWorkFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_USERNAME);
		portletState.setProxyUsername(proxU);
		Settings proxPwd = portletState.getWorkFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_PASSWORD);
		portletState.setProxyPassword(proxPwd);
		Settings bank = portletState.getWorkFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_BANK_NAME);
		portletState. setBankName(bank);
		Settings currency = portletState.getWorkFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_CURRENCY);
		portletState.setCurrency(currency);
		Settings bpwsu = portletState.getWorkFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_BANK_PAYMENT_WS_URL);
		portletState.setBankPaymentWebServiceUrl(bpwsu);
		Settings zwsu = portletState.getWorkFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_TAX_BODY_WS_URL);
		portletState.setTaxBodyWSURL(zwsu);
	}

	


	private static void loadWorkFlowsForPortalUser(
			WorkFlowPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setWorkFlowAssessmentList(portletState.getWorkFlowPortletUtil().getWorkFlowsByReceipientId(
				portletState.getPortalUser().getId()));
	}
	
	
	private static void loadWorkFlowListForPortalUser(
			WorkFlowPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setWorkFlowList(portletState.getWorkFlowPortletUtil().getWorkFlowListByReceipientId(
				portletState.getPortalUser().getId()));
	}
	
	
	private static void loadWorkFlowsForCompany(
			WorkFlowPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setWorkFlowAssessmentList(portletState.getWorkFlowPortletUtil().getWorkFlowsByCompany(portletState.getPortalUser().getCompany()));
	}


	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			WorkFlowPortletState portletState) {

		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			WorkFlowPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			WorkFlowPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			WorkFlowPortletState.log.debug("Error including error message", e);
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


	public WorkFlowPortletUtil getWorkFlowPortletUtil() {
		// TODO Auto-generated method stub
		return this.workFlowPortletUtil;
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

	


	public void setTaxBreakDownList(List<HashMap> allList2) {
		// TODO Auto-generated method stub
		this.allList = allList2;
	}
	
	public Collection<HashMap> getTaxBreakDownList() {
		// TODO Auto-generated method stub
		return this.allList;
	}


	

	public void reinitializeForTaxBreakDown(
			WorkFlowPortletState portletState) {
		// TODO Auto-generated method stub
		
	}



	public CORE_VIEW getCoreCurrentTab() {
		return coreCurrentTab;
	}


	public void setCoreCurrentTab(CORE_VIEW coreCurrentTab) {
		this.coreCurrentTab = coreCurrentTab;
	}


	public String getToken() {
		return token;
	}


	public void setToken(String token) {
		this.token = token;
	}


	public Collection<WorkFlowAssessment> getWorkFlowAssessmentList() {
		return workFlowAssessmentList;
	}


	public void setWorkFlowAssessmentList(Collection<WorkFlowAssessment> workFlowAssessmentList) {
		this.workFlowAssessmentList = workFlowAssessmentList;
	}


	public String getSelectedWorkFlow() {
		return selectedWorkFlow;
	}


	public void setSelectedWorkFlow(String selectedWorkFlow) {
		this.selectedWorkFlow = selectedWorkFlow;
	}


	public String getSettingsSource() {
		return settingsSource;
	}


	public void setSettingsSource(String settingsSource) {
		this.settingsSource = settingsSource;
	}


	public String getSettingsCountry() {
		return settingsCountry;
	}


	public void setSettingsCountry(String settingsCountry) {
		this.settingsCountry = settingsCountry;
	}


	public Collection<Assessment> getAssessmentListing() {
		return assessmentListing;
	}


	public void setAssessmentListing(Collection<Assessment> assessmentListing) {
		this.assessmentListing = assessmentListing;
	}


	public Collection<Assessment> getAllAssessmentListing() {
		return allAssessmentListing;
	}


	public void setAllAssessmentListing(Collection<Assessment> allAssessmentListing) {
		this.allAssessmentListing = allAssessmentListing;
	}


	public BalanceInquiry getBalanceInquiry() {
		return balanceInquiry;
	}


	public void setBalanceInquiry(BalanceInquiry balanceInquiry) {
		this.balanceInquiry = balanceInquiry;
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


	


	public String getSelectedAssessmentsClicked() {
		return selectedAssessmentsClicked;
	}


	public void setSelectedAssessmentsClicked(String selectedAssessmentsClicked) {
		this.selectedAssessmentsClicked = selectedAssessmentsClicked;
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


	public WorkFlow getSelectedSearchedWorkFlow() {
		return selectedSearchedWorkFlow;
	}


	public void setSelectedSearchedWorkFlow(WorkFlow selectedSearchedWorkFlow) {
		this.selectedSearchedWorkFlow = selectedSearchedWorkFlow;
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
		this.currency = currency;
	}


	public Settings getProxyUsername() {
		return proxyUsername;
	}


	public void setProxyUsername(Settings proxyUsername) {
		this.proxyUsername = proxyUsername;
	}


	public Settings getProxyPassword() {
		return proxyPassword;
	}


	public void setProxyPassword(Settings proxyPassword) {
		this.proxyPassword = proxyPassword;
	}


	public Settings getBankPaymentWebServiceUrl() {
		return bankPaymentWebServiceUrl;
	}


	public void setBankPaymentWebServiceUrl(Settings bankPaymentWebServiceUrl) {
		this.bankPaymentWebServiceUrl = bankPaymentWebServiceUrl;
	}


	public Collection<WorkFlow> getWorkFlowList() {
		return workFlowList;
	}


	public void setWorkFlowList(Collection<WorkFlow> workFlowList) {
		this.workFlowList = workFlowList;
	}


	public Settings getTaxBodyWSURL() {
		return taxBodyWSURL;
	}


	public void setTaxBodyWSURL(Settings taxBodyWSURL) {
		this.taxBodyWSURL = taxBodyWSURL;
	}


	public String getReason() {
		return reason;
	}


	public void setReason(String reason) {
		this.reason = reason;
	}


	public static ComminsApplicationState getCas() {
		return cas;
	}


	public static void setCas(ComminsApplicationState cas) {
		WorkFlowPortletState.cas = cas;
	}
}
