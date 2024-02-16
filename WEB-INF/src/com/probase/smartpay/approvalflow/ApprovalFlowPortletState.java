package com.probase.smartpay.approvalflow;

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
import smartpay.entity.PortalUser;
import smartpay.entity.PortalUserCRUDRights;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.WorkFlow;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.service.SwpService;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.probase.smartpay.admin.feedescriptionmanagement.FeeDescriptionPortletState.FEE_DESCRIPTION_APPROVAL_TYPE;
import com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortletState;
import com.probase.smartpay.commins.ComminsApplicationState;
import com.probase.smartpay.commins.Util;
import com.probase.smartpay.commins.ProbaseConstants.CORE_VIEW;
import com.sf.primepay.smartpay13.ServiceLocator;

public class ApprovalFlowPortletState {

	private static Logger log = Logger.getLogger(ApprovalFlowPortletState.class);
	private static ApprovalFlowPortletUtil approvalFlowPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private String remoteIPAddress;
	private ArrayList<RoleType> portalUserRoleType;
	private String successMessage;
	private String errorMessage;
	private static ComminsApplicationState cas;
	private ArrayList<String> errorList = new ArrayList<String>();  

	private CORE_VIEW coreCurrentTab;
	
	
	private FEE_DESCRIPTION_APPROVAL_TYPE feeDescriptionApprovalType;
	
	/*************SETTINGS************************/
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
	private Settings settingsZraAccount;
	private Settings settingsZraAccountSortCode;
	
	
	
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
	private Settings taxBodyWSUrl;
	

	private PortalUserCRUDRights portalUserCRUDRights;
	private CompanyCRUDRights companyCRUDRights;
	
	
	/*****************NAVIGATION*****************/
	private VIEW_TABS currentTab;
	private String selectedApprovalItemType;
	private String selectedApprovalItem;
	private Collection<ApprovalFlowTransit> allApprovalFlowTransitListing; 
	private String workItemTypeDescription;
	
	private Collection<ApprovalFlowTransit> authorizePanelCombinationRequestListing;
	private Collection<ApprovalFlowTransit> companyRequestListing;
	private Collection<ApprovalFlowTransit> portalUserRequestListing;
	private Collection<ApprovalFlowTransit> settingsRequestListing;
	private Collection<ApprovalFlowTransit> feeDescriptionRequestListing;
	private Settings approvalDirect;
	
	
    
    public static enum VIEW_TABS{
    	VNEW_COMPANY, VUPDATE_COMPANY, VBLOCK_COMPANY, VUNBLOCK_COMPANY, VDELETE_COMPANY, 
    	VNEW_USER, VUPDATE_USER, VBLOCK_USER, VUNBLOCK_USER, VDELETE_USER,
    	VMAP_PANEL, VUNMAP_PANEL,
    	VMAP_FEE, VUNMAP_FEE, VNEW_FEE, VDELETE_FEE, VUPDATE_FEE,
    	VUPDATE_SETTINGS, 
    	VNEW_TAXTYPE, VUPDATE_TAXTYPE, VACT_TAXTYPE_SUSPEND, VACT_TAXTYPE_REACTIVATE
    }
    
    
    
	public static enum NAVIGATE{
		NAVIGATE_ACTIONS
	}
	
	
	
	public static enum APPROVAL_FLOW_ACTIONS{
		LOGIN_STEP_TWO, SELECT_APPROVAL_ENTITY, HANDLE_APPROVAL_LISTINGS
	}
	
	
	public static enum APPROVAL_TAB_ACTION{
		HANDLE_CREATE_NEW_USER, HANDLE_UPDATE_NEW_USER, HANDLE_BLOCK_NEW_USER, 
		HANDLE_UNBLOCK_NEW_USER, HANDLE_DELETE_NEW_USER, HANDLE_CREATE_NEW_COMPANY, 
		HANDLE_UPDATE_COMPANY, HANDLE_BLOCK_COMPANY, HANDLE_UNBLOCK_COMPANY, 
		HANDLE_DELETE_COMPANY, HANDLE_MAP_PANEL, HANDLE_UNMAP_PANEL, HANDLE_CREATE_FEE, 
		HANDLE_UPDATE_FEE, HANDLE_DELETE_FEE, HANDLE_MAP_FEE, 
		HANDLE_UNMAP_FEE, HANDLE_UPDATE_SETTINGS, 
		HANDLE_NEW_TAX_TYPE, HANDLE_TAX_TYPE_LIST_REACTIVATE, HANDLE_TAX_TYPE_LIST_SUSPEND, HANDLE_TAX_TYPE_LIST_UPDATE
	}
	
	/****core section starts here****/
	public static ApprovalFlowPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		ApprovalFlowPortletState portletState = null;
		Logger.getLogger(ApprovalFlowPortletState.class).info("------getInstance");
		try {
			
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (ApprovalFlowPortletState) session.getAttribute(ApprovalFlowPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new ApprovalFlowPortletState();
					ApprovalFlowPortletUtil util = new ApprovalFlowPortletUtil();
					portletState.setApprovalFlowPortletUtil(util);
					session.setAttribute(ApprovalFlowPortletState.class.getName(), portletState);
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
	
	
	public void setApprovalFlowPortletUtil(ApprovalFlowPortletUtil util) {
		// TODO Auto-generated method stub
		this.approvalFlowPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, ApprovalFlowPortletState portletState) {
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
				ApprovalFlowPortletUtil util = ApprovalFlowPortletUtil.getInstance();
				portletState.setApprovalFlowPortletUtil(util);
				
				ArrayList<RoleType> pur = portletState.getApprovalFlowPortletUtil().
						getRoleTypeByPortalUser(portletState.getPortalUser());	
				portletState.setPortalUserRoleType(pur);
				//loadWorkFlowsForCompany(portletState);
				loadSettings(portletState);
				loadAccessLevels(portletState);
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
			ApprovalFlowPortletState portletState) {
		// TODO Auto-generated method stub
		PortalUserCRUDRights pucr = portletState.getApprovalFlowPortletUtil().getPortalUserCRUDRightsByPortalUser(portletState.getPortalUser());
		portletState.setPortalUserCRUDRights(pucr);
		CompanyCRUDRights ccr = portletState.getApprovalFlowPortletUtil().getAllCompanyCRUDRightsRightsByPortalUser(portletState.getPortalUser().getId());
		portletState.setCompanyCRUDRights(ccr);
	}

	public static void loadSettings(
			ApprovalFlowPortletState portletState) {
		// TODO Auto-generated method stub
		Settings primaryFeeSetting = portletState.getApprovalFlowPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_PRIMARY_FEE_DESCRIPTION);
		portletState.setPrimaryFeeSetting(primaryFeeSetting);
		Settings notifyCorporateFirmEmail = portletState.getApprovalFlowPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_EMAIL);
		portletState.setNotifyCorporateFirmEmail(notifyCorporateFirmEmail);
		Settings notifyCorporateFirmSms = portletState.getApprovalFlowPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_SMS);
		portletState.setNotifyCorporateFirmSms(notifyCorporateFirmSms);
		Settings notifyCorporateIndividualEmail = portletState.getApprovalFlowPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_EMAIL);
		portletState.setNotifyCorporateIndividualEmail(notifyCorporateIndividualEmail);
		Settings notifyCorporateIndividualSMS = portletState.getApprovalFlowPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_SMS);
		portletState.setNotifyCorporateIndividualSMS(notifyCorporateIndividualSMS);
		Settings etaxPaymentNotifyEmail = portletState.getApprovalFlowPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_EMAIL);
		portletState.setEtaxPaymentNotifyEmail(etaxPaymentNotifyEmail);
		Settings etaxPaymentNotifySMS = portletState.getApprovalFlowPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_SMS);
		portletState.setEtaxPaymentNotifySMS(etaxPaymentNotifySMS);
		Settings systemUrl = portletState.getApprovalFlowPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_URL);
		portletState.setSystemUrl(systemUrl);
		Settings approvalDirect = portletState.getApprovalFlowPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_APPROVAL_DIRECT_TO_ONE_PORTAL_USER);
		portletState.setApprovalDirect(approvalDirect);
		
		Settings twostep = portletState.getApprovalFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_TWO_STEP_LOGIN);
		portletState.setTwoStepLogin(twostep);
		Settings appr = portletState.getApprovalFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_APPROVAL_PROCESS);
		portletState.setApprovalProcess(appr);
		Settings usname = portletState.getApprovalFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_USERNAME);
		portletState.setSendingEmailUsername(usname);
		Settings semail = portletState.getApprovalFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_EMAIL);
		portletState.setSendingEmail(semail);
		Settings pswd = portletState.getApprovalFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_PASSWORD);
		portletState.setSendingEmailPassword(pswd);
		Settings port = portletState.getApprovalFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_PORT);
		portletState.setSendingEmailPort(port);
		Settings spc = portletState.getApprovalFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_COUNTRY);
		portletState.setPlatformCountry(spc);
		Settings spb = portletState.getApprovalFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATORM_BANK);
		portletState.setPlatformBank(spb);
		Settings zacc = portletState.getApprovalFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_NUMBER);
		portletState.setSettingsZraAccount(zacc);
		Settings zacs = portletState.getApprovalFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_SORT_CODE);
		portletState.setSettingsZraAccountSortCode(zacs);
		
		
		
		Settings app = portletState.getApprovalFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_APPLICATION_NAME);
		portletState.setApplicationName(app);
		Settings mapp = portletState.getApprovalFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_MOBILE_APPLICATION_NAME);
		portletState.setMobileApplicationName(mapp);
		Settings proxH = portletState.getApprovalFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_HOST);
		portletState.setProxyHost(proxH);
		Settings proxP = portletState.getApprovalFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_PORT);
		portletState.setProxyPort(proxP);
		Settings proxU = portletState.getApprovalFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_USERNAME);
		portletState.setProxyUsername(proxU);
		Settings proxPwd = portletState.getApprovalFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_PASSWORD);
		portletState.setProxyPassword(proxPwd);
		Settings bank = portletState.getApprovalFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_BANK_NAME);
		portletState. setBankName(bank);
		Settings currency = portletState.getApprovalFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_CURRENCY);
		portletState.setCurrency(currency);
		Settings bpwsu = portletState.getApprovalFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_BANK_PAYMENT_WS_URL);
		portletState.setBankPaymentWebServiceUrl(bpwsu);
		Settings zwsu = portletState.getApprovalFlowPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_TAX_BODY_WS_URL);
		portletState.setTaxBodyWSUrl(zwsu);
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
			ApprovalFlowPortletState portletState) {

		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			ApprovalFlowPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			ApprovalFlowPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			ApprovalFlowPortletState.log.debug("Error including error message", e);
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


	public ApprovalFlowPortletUtil getApprovalFlowPortletUtil() {
		// TODO Auto-generated method stub
		return this.approvalFlowPortletUtil;
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

	
	

	public void reinitializeForTaxBreakDown(
			ApprovalFlowPortletState portletState) {
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


	public String getSelectedApprovalItemType() {
		return selectedApprovalItemType;
	}


	public void setSelectedApprovalItemType(String selectedApprovalItemType) {
		this.selectedApprovalItemType = selectedApprovalItemType;
	}


	public Collection<ApprovalFlowTransit> getAllApprovalFlowTransitListing() {
		return allApprovalFlowTransitListing;
	}


	public void setAllApprovalFlowTransitListing(
			Collection<ApprovalFlowTransit> allApprovalFlowTransitListing) {
		this.allApprovalFlowTransitListing = allApprovalFlowTransitListing;
	}


	public String getSelectedApprovalItem() {
		return selectedApprovalItem;
	}


	public void setSelectedApprovalItem(String selectedApprovalItem) {
		this.selectedApprovalItem = selectedApprovalItem;
	}


	public String getWorkItemTypeDescription() {
		return workItemTypeDescription;
	}


	public void setWorkItemTypeDescription(String workItemTypeDescription) {
		this.workItemTypeDescription = workItemTypeDescription;
	}

	

	public FEE_DESCRIPTION_APPROVAL_TYPE getFeeDescriptionApprovalType() {
		return feeDescriptionApprovalType;
	}


	public void setFeeDescriptionApprovalType(FEE_DESCRIPTION_APPROVAL_TYPE feeDescriptionApprovalType) {
		this.feeDescriptionApprovalType = feeDescriptionApprovalType;
	}


	public Collection<ApprovalFlowTransit> getAuthorizePanelCombinationRequestListing() {
		return authorizePanelCombinationRequestListing;
	}


	public void setAuthorizePanelCombinationRequestListing(
			Collection<ApprovalFlowTransit> authorizePanelCombinationRequestListing) {
		this.authorizePanelCombinationRequestListing = authorizePanelCombinationRequestListing;
	}


	public Collection<ApprovalFlowTransit> getCompanyRequestListing() {
		return companyRequestListing;
	}


	public void setCompanyRequestListing(Collection<ApprovalFlowTransit> companyRequestListing) {
		this.companyRequestListing = companyRequestListing;
	}


	public Collection<ApprovalFlowTransit> getPortalUserRequestListing() {
		return portalUserRequestListing;
	}


	public void setPortalUserRequestListing(Collection<ApprovalFlowTransit> portalUserRequestListing) {
		this.portalUserRequestListing = portalUserRequestListing;
	}


	public Collection<ApprovalFlowTransit> getSettingsRequestListing() {
		return settingsRequestListing;
	}


	public void setSettingsRequestListing(Collection<ApprovalFlowTransit> settingsRequestListing) {
		this.settingsRequestListing = settingsRequestListing;
	}


	public Collection<ApprovalFlowTransit> getFeeDescriptionRequestListing() {
		return feeDescriptionRequestListing;
	}


	public void setFeeDescriptionRequestListing(
			Collection<ApprovalFlowTransit> feeDescriptionRequestListing) {
		this.feeDescriptionRequestListing = feeDescriptionRequestListing;
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


	public Settings getSettingsZraAccount() {
		return settingsZraAccount;
	}


	public void setSettingsZraAccount(Settings settingsZraAccount) {
		this.settingsZraAccount = settingsZraAccount;
	}


	public Settings getSettingsZraAccountSortCode() {
		return settingsZraAccountSortCode;
	}


	public void setSettingsZraAccountSortCode(Settings settingsZraAccountSortCode) {
		this.settingsZraAccountSortCode = settingsZraAccountSortCode;
	}


	public Settings getTaxBodyWSUrl() {
		return taxBodyWSUrl;
	}


	public void setTaxBodyWSUrl(Settings taxBodyWSUrl) {
		this.taxBodyWSUrl = taxBodyWSUrl;
	}


	public static ComminsApplicationState getCas() {
		return cas;
	}


	public static void setCas(ComminsApplicationState cas) {
		ApprovalFlowPortletState.cas = cas;
	}

	
}
