package com.probase.smartpay.admin.settingsmanagement;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;

import org.apache.log4j.Logger;

import smartpay.entity.AuthorizePanel;
import smartpay.entity.AuthorizePanelCombination;
import smartpay.entity.BankBranches;
import smartpay.entity.Company;
import smartpay.entity.FeeDescription;
import smartpay.entity.FinancialAmountRestriction;
import smartpay.entity.PaymentBreakDownHistory;
import smartpay.entity.PortalUser;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.enumerations.CompanyStatusConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.service.SwpService;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.probase.smartpay.commins.CronScheduler;
import com.probase.smartpay.commins.Util;
import com.sf.primepay.smartpay13.ServiceLocator;

public class SettingsManagementPortletState {

	private static Logger log = Logger.getLogger(SettingsManagementPortletState.class);
	private static SettingsManagementPortletUtil settingsManagementPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private ArrayList<RoleType> portalUserRoleType;
	private CronScheduler cronScheduler;
	

	private String successMessage;
	private String remoteIPAddress;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	private Collection<FeeDescription> allFeeDescription;
	private Collection<PaymentBreakDownHistory> paymentBreakdownHistoryListing;
	
	
	
	
	/****settings*****/
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
	private Settings settingsZRAAccount;
	private Settings settingsZRASortCode;
	private Settings sendingEmail;
	private Settings sendingEmailPassword;
	private Settings sendingEmailPort;
	private Settings sendingEmailUsername;
	private Settings approvalProcess;
	private Settings twoStepLogin;
	private Settings EODSetting;
	private Settings paySplitterSetting;
	
	private Settings applicationName;
	private Settings mobileApplicationName;
	private Settings proxyHost;
	private Settings proxyPort;
	private Settings emailFooter;
	private Settings sameLevelLoginCombination;
	private Settings bankPaymentWebServiceUrl;
	private Settings zraWebServiceUrl;
	private Settings bankName;
	private Settings proxyUsername;
	private Settings proxyPassword;
	private Settings currency;
	
	

	/*****************NAVIGATION*****************/
	private VIEW_TABS currentTab;
	
	
	
	/****settings*********/
	private String selectedFeeDescription;
	private String selectedEmailNotificationForCorporateFirm;
	private String selectedMobileNotificationForCorporateFirm;
	private String selectedEmailNotificationForCorporateIndivididuals;
	private String selectedMobileNotificationForCorporateIndivididuals;
	private String selectedEtaxPaymentEmailNotify;
	private String selectedEtaxPaymentSmsNotify;
	private String selectedSystemUrl;
	private String selectedPlatformCountry;
	private String selectedPlatformBank;
	private String selectedTaxCompanyAccount;
	private String selectedTaxCompanySortCode;
	private String selectedSendingEmail;
	private String selectedSendingEmailPassword;
	private String selectedSendingEmailPort;
	private String selectedSendingEmailUsername;
	private String selectedApprovalProcess;
	private String selectedTwoStepLogin;
	
	private String selectedApplicationName;
	private String selectedMobileApplicationName;
	private String selectedProxyHost;
	private String selectedProxyPort;
	private String selectedProxyUsername;
	private String selectedProxyPassword;
	private String selectedBankName;
	private String selectedcurrency;
	private String selectedBankPaymentWebServiceUrl;
	private String selectedZraWebServiceUrl;
	
	
	
	/****enum section****/
    
    
    
    public static enum VIEW_TABS{
    	MANAGE_SETTINGS, VIEW_SETTINGS, MANAGE_JOBS, VIEW_PAYMENT_BREAKDOWN
    }
    
    
    
    public static enum SETTINGS_VIEW
    {
    	MANAGE_SETTINGS, VIEW_SETTINGS, MANAGE_JOBS, VIEW_PAYMENT_BREAKDOWN
    }
    
	
	public static enum NAVIGATE{
		NAVIGATE_ACTIONS
	}
	
	
	public static enum SETTINGS{
		UPDATE_SETTINGS, GO_TO_UPDATE_SETTINGS_INTERFACE, MANAGE_JOBS_PAY_SPLIT, MANAGE_JOBS_EOD, MANAGE_ONE_PAYMENT_BREAKDOWN_HISTORY
	}
	
	/****core section starts here****/
	public static SettingsManagementPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		SettingsManagementPortletState portletState = null;
		Logger.getLogger(SettingsManagementPortletState.class).info("------getInstance");
		try {
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (SettingsManagementPortletState) session.getAttribute(SettingsManagementPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new SettingsManagementPortletState();
					SettingsManagementPortletUtil util = new SettingsManagementPortletUtil();
					portletState.setSettingsManagementPortletUtil(util);
					session.setAttribute(SettingsManagementPortletState.class.getName(), portletState);
					defaultInit(request, portletState);
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
	
	
	private void setSettingsManagementPortletUtil(SettingsManagementPortletUtil util) {
		// TODO Auto-generated method stub
		this.settingsManagementPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, SettingsManagementPortletState portletState) {
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
				SettingsManagementPortletUtil util = SettingsManagementPortletUtil.getInstance();
				portletState.setSettingsManagementPortletUtil(util);
				
				ArrayList<RoleType> pur = portletState.getSettingsManagementPortletUtil().
						getRoleTypeByPortalUser(portletState.getPortalUser());	
				portletState.setPortalUserRoleType(pur);
				
				loadSettings(portletState);
				portletState.setAllFeeDescription(portletState.getSettingsManagementPortletUtil().getFeeDescriptionListing(true));
				
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


	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			SettingsManagementPortletState portletState) {

		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			SettingsManagementPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			SettingsManagementPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			SettingsManagementPortletState.log.debug("Error including error message", e);
		}
	}
	
	public void setSuccessMessage(String successMessage)
	{
		this.successMessage=successMessage;
	}
	
	public String getRemoteIPAddress() {
		return remoteIPAddress;
	}

	public void setRemoteIPAddress(String remoteIPAddress) {
		this.remoteIPAddress = remoteIPAddress;
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


	public SettingsManagementPortletUtil getSettingsManagementPortletUtil() {
		// TODO Auto-generated method stub
		return this.settingsManagementPortletUtil;
	}


	public ArrayList<RoleType> getPortalUserRoleType() {
		return portalUserRoleType;
	}


	public void setPortalUserRoleType(ArrayList<RoleType> portalUserRoleType) {
		this.portalUserRoleType = portalUserRoleType;
	}
	
	

	public ArrayList<String> getErrorList() {
		return errorList;
	}


	public void setErrorList(ArrayList<String> errorList) {
		this.errorList = errorList;
	}
	
	
	


	public String getSelectedFeeDescription() {
		return selectedFeeDescription;
	}



	public void setSelectedFeeDescription(String selectedFeeDescription) {
		this.selectedFeeDescription = selectedFeeDescription;
	}



	public String getSelectedEmailNotificationForCorporateFirm() {
		return selectedEmailNotificationForCorporateFirm;
	}



	public void setSelectedEmailNotificationForCorporateFirm(
			String selectedEmailNotificationForCorporateFirm) {
		this.selectedEmailNotificationForCorporateFirm = selectedEmailNotificationForCorporateFirm;
	}



	public String getSelectedMobileNotificationForCorporateFirm() {
		return selectedMobileNotificationForCorporateFirm;
	}



	public void setSelectedMobileNotificationForCorporateFirm(
			String selectedMobileNotificationForCorporateFirm) {
		this.selectedMobileNotificationForCorporateFirm = selectedMobileNotificationForCorporateFirm;
	}



	public String getSelectedEmailNotificationForCorporateIndivididuals() {
		return selectedEmailNotificationForCorporateIndivididuals;
	}



	public void setSelectedEmailNotificationForCorporateIndivididuals(
			String selectedEmailNotificationForCorporateIndivididuals) {
		this.selectedEmailNotificationForCorporateIndivididuals = selectedEmailNotificationForCorporateIndivididuals;
	}



	public String getSelectedMobileNotificationForCorporateIndivididuals() {
		return selectedMobileNotificationForCorporateIndivididuals;
	}



	public void setSelectedMobileNotificationForCorporateIndivididuals(
			String selectedMobileNotificationForCorporateIndivididuals) {
		this.selectedMobileNotificationForCorporateIndivididuals = selectedMobileNotificationForCorporateIndivididuals;
	}



	public String getSelectedEtaxPaymentEmailNotify() {
		return selectedEtaxPaymentEmailNotify;
	}



	public void setSelectedEtaxPaymentEmailNotify(
			String selectedEtaxPaymentEmailNotify) {
		this.selectedEtaxPaymentEmailNotify = selectedEtaxPaymentEmailNotify;
	}



	public String getSelectedEtaxPaymentSmsNotify() {
		return selectedEtaxPaymentSmsNotify;
	}



	public void setSelectedEtaxPaymentSmsNotify(
			String selectedEtaxPaymentSmsNotify) {
		this.selectedEtaxPaymentSmsNotify = selectedEtaxPaymentSmsNotify;
	}



	public void reinitializeForSettings(SettingsManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setSelectedFeeDescription(null);
		portletState.setSelectedEmailNotificationForCorporateFirm(null);
		portletState.setSelectedMobileNotificationForCorporateFirm(null);
		portletState.setSelectedEmailNotificationForCorporateIndivididuals(null);
		portletState.setSelectedMobileNotificationForCorporateIndivididuals(null);
		portletState.setSelectedEtaxPaymentEmailNotify(null);
		portletState.setSelectedEtaxPaymentSmsNotify(null);
	}








	public VIEW_TABS getCurrentTab() {
		return currentTab;
	}



	public void setCurrentTab(VIEW_TABS currentTab) {
		this.currentTab = currentTab;
	}



	public static boolean loadSettings(SettingsManagementPortletState portletState) {
		// TODO Auto-generated method stub
		Settings primaryFeeSetting = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PRIMARY_FEE_DESCRIPTION);
		portletState.setPrimaryFeeSetting(primaryFeeSetting);
		Settings notifyCorporateFirmEmail = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_EMAIL);
		portletState.setNotifyCorporateFirmEmail(notifyCorporateFirmEmail);
		Settings notifyCorporateFirmSms = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_SMS);
		portletState.setNotifyCorporateFirmSms(notifyCorporateFirmSms);
		Settings notifyCorporateIndividualEmail = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_EMAIL);
		portletState.setNotifyCorporateIndividualEmail(notifyCorporateIndividualEmail);
		Settings notifyCorporateIndividualSMS = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_SMS);
		portletState.setNotifyCorporateIndividualSMS(notifyCorporateIndividualSMS);
		Settings etaxPaymentNotifyEmail = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_EMAIL);
		portletState.setEtaxPaymentNotifyEmail(etaxPaymentNotifyEmail);
		Settings etaxPaymentNotifySMS = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_SMS);
		portletState.setEtaxPaymentNotifySMS(etaxPaymentNotifySMS);
		Settings systemUrl = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_URL);
		portletState.setSystemUrl(systemUrl);
		
		Settings twostep = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_TWO_STEP_LOGIN);
		portletState.setTwoStepLogin(twostep);
		Settings appr = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_APPROVAL_PROCESS);
		portletState.setApprovalProcess(appr);
		Settings usname = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_USERNAME);
		portletState.setSendingEmailUsername(usname);
		Settings semail = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_EMAIL);
		portletState.setSendingEmail(semail);
		Settings pswd = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_PASSWORD);
		portletState.setSendingEmailPassword(pswd);
		Settings port = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_PORT);
		portletState.setSendingEmailPort(port);
		Settings spc = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_COUNTRY);
		portletState.setPlatformCountry(spc);
		Settings spb = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATORM_BANK);
		portletState.setPlatformBank(spb);
		Settings zacc = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_NUMBER);
		portletState.setSettingsZRAAccount(zacc);
		Settings zacs = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_SORT_CODE);
		portletState.setSettingsZRASortCode(zacs);
		
		
		
		Settings app = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_APPLICATION_NAME);
		portletState.setApplicationName(app);
		Settings mapp = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_MOBILE_APPLICATION_NAME);
		portletState.setMobileApplicationName(mapp);
		Settings proxH = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_HOST);
		portletState.setProxyHost(proxH);
		Settings proxP = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_PORT);
		portletState.setProxyPort(proxP);
		Settings proxU = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_USERNAME);
		portletState.setProxyUsername(proxU);
		Settings proxPwd = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_PASSWORD);
		portletState.setProxyPassword(proxPwd);
		Settings bank = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_BANK_NAME);
		portletState. setBankName(bank);
		Settings currency = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_CURRENCY);
		portletState.setCurrency(currency);
		Settings bpwsu = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_BANK_PAYMENT_WS_URL);
		portletState.setBankPaymentWebServiceUrl(bpwsu);
		Settings zwsu = portletState.getSettingsManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_TAX_BODY_WS_URL);
		portletState.setZraWebServiceUrl(zwsu);
		Settings eODSetting = portletState.getSettingsManagementPortletUtil().
				getSettingByNameString("EOD_RUN");
		portletState.setEODSetting(eODSetting);
		Settings paySplitterSetting = portletState.getSettingsManagementPortletUtil().
				getSettingByNameString("PAYMENT_SPLITTER_RUN");
		portletState.setPaySplitterSetting(paySplitterSetting);
		
		return false;
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


	public Collection<FeeDescription> getAllFeeDescription() {
		return allFeeDescription;
	}


	public void setAllFeeDescription(Collection<FeeDescription> allFeeDescription) {
		this.allFeeDescription = allFeeDescription;
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


	public String getSelectedSystemUrl() {
		return selectedSystemUrl;
	}


	public void setSelectedSystemUrl(String selectedSystemUrl) {
		this.selectedSystemUrl = selectedSystemUrl;
	}


	public String getSelectedPlatformCountry() {
		return selectedPlatformCountry;
	}


	public void setSelectedPlatformCountry(String selectedPlatformCountry) {
		this.selectedPlatformCountry = selectedPlatformCountry;
	}


	public String getSelectedPlatformBank() {
		return selectedPlatformBank;
	}


	public void setSelectedPlatformBank(String selectedPlatformBank) {
		this.selectedPlatformBank = selectedPlatformBank;
	}


	public String getSelectedTaxCompanyAccount() {
		return selectedTaxCompanyAccount;
	}


	public void setSelectedTaxCompanyAccount(String selectedTaxCompanyAccount) {
		this.selectedTaxCompanyAccount = selectedTaxCompanyAccount;
	}


	public String getSelectedTaxCompanySortCode() {
		return selectedTaxCompanySortCode;
	}


	public void setSelectedTaxCompanySortCode(String selectedTaxCompanySortCode) {
		this.selectedTaxCompanySortCode = selectedTaxCompanySortCode;
	}


	public String getSelectedTwoStepLogin() {
		return selectedTwoStepLogin;
	}


	public void setSelectedTwoStepLogin(String selectedTwoStepLogin) {
		this.selectedTwoStepLogin = selectedTwoStepLogin;
	}


	public String getSelectedSendingEmailUsername() {
		return selectedSendingEmailUsername;
	}


	public void setSelectedSendingEmailUsername(
			String selectedSendingEmailUsername) {
		this.selectedSendingEmailUsername = selectedSendingEmailUsername;
	}


	public String getSelectedApprovalProcess() {
		return selectedApprovalProcess;
	}


	public void setSelectedApprovalProcess(String selectedApprovalProcess) {
		this.selectedApprovalProcess = selectedApprovalProcess;
	}


	public String getSelectedSendingEmail() {
		return selectedSendingEmail;
	}


	public void setSelectedSendingEmail(String selectedSendingEmail) {
		this.selectedSendingEmail = selectedSendingEmail;
	}


	public String getSelectedSendingEmailPassword() {
		return selectedSendingEmailPassword;
	}


	public void setSelectedSendingEmailPassword(
			String selectedSendingEmailPassword) {
		this.selectedSendingEmailPassword = selectedSendingEmailPassword;
	}


	public String getSelectedSendingEmailPort() {
		return selectedSendingEmailPort;
	}


	public void setSelectedSendingEmailPort(String selectedSendingEmailPort) {
		this.selectedSendingEmailPort = selectedSendingEmailPort;
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


	public Settings getBankPaymentWebServiceUrl() {
		return bankPaymentWebServiceUrl;
	}


	public void setBankPaymentWebServiceUrl(Settings bankPaymentWebServiceUrl) {
		this.bankPaymentWebServiceUrl = bankPaymentWebServiceUrl;
	}


	public Settings getZraWebServiceUrl() {
		return zraWebServiceUrl;
	}


	public void setZraWebServiceUrl(Settings zraWebServiceUrl) {
		this.zraWebServiceUrl = zraWebServiceUrl;
	}


	public String getSelectedApplicationName() {
		return selectedApplicationName;
	}


	public void setSelectedApplicationName(String selectedApplicationName) {
		this.selectedApplicationName = selectedApplicationName;
	}


	public String getSelectedMobileApplicationName() {
		return selectedMobileApplicationName;
	}


	public void setSelectedMobileApplicationName(
			String selectedMobileApplicationName) {
		this.selectedMobileApplicationName = selectedMobileApplicationName;
	}


	public String getSelectedProxyHost() {
		return selectedProxyHost;
	}


	public void setSelectedProxyHost(String selectedProxyHost) {
		this.selectedProxyHost = selectedProxyHost;
	}


	public String getSelectedProxyPort() {
		return selectedProxyPort;
	}


	public void setSelectedProxyPort(String selectedProxyPort) {
		this.selectedProxyPort = selectedProxyPort;
	}


	public String getSelectedProxyUsername() {
		return selectedProxyUsername;
	}


	public void setSelectedProxyUsername(String selectedProxyUsername) {
		this.selectedProxyUsername = selectedProxyUsername;
	}


	public String getSelectedProxyPassword() {
		return selectedProxyPassword;
	}


	public void setSelectedProxyPassword(String selectedProxyPassword) {
		this.selectedProxyPassword = selectedProxyPassword;
	}


	public String getSelectedBankName() {
		return selectedBankName;
	}


	public void setSelectedBankName(String selectedBankName) {
		this.selectedBankName = selectedBankName;
	}


	public String getSelectedcurrency() {
		return selectedcurrency;
	}


	public void setSelectedcurrency(String selectedcurrency) {
		this.selectedcurrency = selectedcurrency;
	}


	public String getSelectedBankPaymentWebServiceUrl() {
		return selectedBankPaymentWebServiceUrl;
	}


	public void setSelectedBankPaymentWebServiceUrl(
			String selectedBankPaymentWebServiceUrl) {
		this.selectedBankPaymentWebServiceUrl = selectedBankPaymentWebServiceUrl;
	}


	public String getSelectedZraWebServiceUrl() {
		return selectedZraWebServiceUrl;
	}


	public void setSelectedZraWebServiceUrl(String selectedZraWebServiceUrl) {
		this.selectedZraWebServiceUrl = selectedZraWebServiceUrl;
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


	public Settings getSettingsZRASortCode() {
		return settingsZRASortCode;
	}


	public void setSettingsZRASortCode(Settings settingsZRASortCode) {
		this.settingsZRASortCode = settingsZRASortCode;
	}


	public Settings getSettingsZRAAccount() {
		return settingsZRAAccount;
	}


	public void setSettingsZRAAccount(Settings settingsZRAAccount) {
		this.settingsZRAAccount = settingsZRAAccount;
	}


	public Settings getEODSetting() {
		return EODSetting;
	}


	public void setEODSetting(Settings eODSetting) {
		EODSetting = eODSetting;
	}


	public Settings getPaySplitterSetting() {
		return paySplitterSetting;
	}


	public void setPaySplitterSetting(Settings paySplitterSetting) {
		this.paySplitterSetting = paySplitterSetting;
	}


	public CronScheduler getCronScheduler() {
		return cronScheduler;
	}


	public void setCronScheduler(CronScheduler cronScheduler) {
		this.cronScheduler = cronScheduler;
	}


	public Collection<PaymentBreakDownHistory> getPaymentBreakdownHistoryListing() {
		return paymentBreakdownHistoryListing;
	}


	public void setPaymentBreakdownHistoryListing(
			Collection<PaymentBreakDownHistory> paymentBreakdownHistoryListing) {
		this.paymentBreakdownHistoryListing = paymentBreakdownHistoryListing;
	}

	
}
