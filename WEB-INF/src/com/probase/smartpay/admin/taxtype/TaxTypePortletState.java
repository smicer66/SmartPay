package com.probase.smartpay.admin.taxtype;

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
import smartpay.entity.CompanyCRUDRights;
import smartpay.entity.PortalUser;
import smartpay.entity.PortalUserCRUDRights;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.TaxType;
import smartpay.entity.enumerations.CompanyStatusConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.service.SwpService;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.probase.smartpay.commins.ComminsApplicationState;
import com.probase.smartpay.commins.Util;
import com.sf.primepay.smartpay13.ServiceLocator;

public class TaxTypePortletState {

	private static Logger log = Logger.getLogger(TaxTypePortletState.class);
	private static TaxTypePortletUtil taxTypePortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private ArrayList<RoleType> portalUserRoleType;
	private static ComminsApplicationState cas;

	private String successMessage;
	private String remoteIPAddress;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	
	private Collection<TaxType> allTaxTypeListing;
	private String selectedTypeId;
	private CompanyCRUDRights companyCRUDRights;
	private PortalUserCRUDRights portalUserCRUDRights; 
	
	
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
	private Settings taxCompanyAccount;
	private Settings taxCompanySortCode;
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
	
	
	
	/*****************NAVIGATION*****************/
	private VIEW_TABS currentTab;
	
	
	
	/****Ports***/
	private String taxTypeCode;
    private String taxTypeName;
    private String taxTypeAccount;
    private String taxTypeSortCode;
    
   
	/****enum section****/
    
    
    public static enum VIEW_TABS{
    	CREATE_A_NEW_TAXTYPE, VIEW_TAXTYPE_LISTINGS
    	
    }
    
    public static enum TAXTYPE_VIEW
    {
    	CREATE_A_NEW_TAXTYPE, VIEW_TAXTYPE_LISTINGS 
    }
    
	
	public static enum NAVIGATE{
		NAVIGATE_ACTIONS
	}
	
	
	public static enum TAXTYPE_ACTION{
		CREATE_A_NEW_TAXTYPE_ACTION, UPDATE_NEW_TAXTYPE_ACTION, 
		TAXTYPE, HANDLE_TAXTYPE_LISTING_ACTION, LOGIN_STEP_TWO
	}
	
	/****core section starts here****/
	public static TaxTypePortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		TaxTypePortletState portletState = null;
		Logger.getLogger(TaxTypePortletState.class).info("------getInstance");
		try {
			
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (TaxTypePortletState) session.getAttribute(TaxTypePortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new TaxTypePortletState();
					TaxTypePortletUtil util = new TaxTypePortletUtil();
					portletState.setTaxTypePortletUtil(util);
					session.setAttribute(TaxTypePortletState.class.getName(), portletState);
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
	
	
	private void setTaxTypePortletUtil(TaxTypePortletUtil util) {
		// TODO Auto-generated method stub
		this.taxTypePortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, TaxTypePortletState portletState) {
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
				TaxTypePortletUtil util = TaxTypePortletUtil.getInstance();
				portletState.setTaxTypePortletUtil(util);
				
				ArrayList<RoleType> pur = portletState.getTaxTypePortletUtil().
						getRoleTypeByPortalUser(portletState.getPortalUser());	
				portletState.setPortalUserRoleType(pur);
				loadSettings(portletState);
				loadAccessLevels(portletState);
				portletState.setAllTaxTypeListing(portletState.getTaxTypePortletUtil().getAllTaxTypeListing(true));
				portletState.setCurrentTab(VIEW_TABS.CREATE_A_NEW_TAXTYPE);
				
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

	private static void loadAccessLevels(TaxTypePortletState portletState) {
		// TODO Auto-generated method stub
		CompanyCRUDRights ccr = portletState.getTaxTypePortletUtil().getCompanyCRUDRightsByPortalUser(portletState.getPortalUser());
		PortalUserCRUDRights pucr = portletState.getTaxTypePortletUtil().getPortalUserCRUDRightsByPortalUser(portletState.getPortalUser());
		portletState.setCompanyCRUDRights(ccr);
		portletState.setPortalUserCRUDRights(pucr);
	}


	private static void loadSettings(TaxTypePortletState portletState) {
		// TODO Auto-generated method stub
		Settings primaryFeeSetting = portletState.getTaxTypePortletUtil().getSettingByName(SmartPayConstants.SETTINGS_PRIMARY_FEE_DESCRIPTION);
		portletState.setPrimaryFeeSetting(primaryFeeSetting);
		Settings notifyCorporateFirmEmail = portletState.getTaxTypePortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_EMAIL);
		portletState.setNotifyCorporateFirmEmail(notifyCorporateFirmEmail);
		Settings notifyCorporateFirmSms = portletState.getTaxTypePortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_SMS);
		portletState.setNotifyCorporateFirmSms(notifyCorporateFirmSms);
		Settings notifyCorporateIndividualEmail = portletState.getTaxTypePortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_EMAIL);
		portletState.setNotifyCorporateIndividualEmail(notifyCorporateIndividualEmail);
		Settings notifyCorporateIndividualSMS = portletState.getTaxTypePortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_SMS);
		portletState.setNotifyCorporateIndividualSMS(notifyCorporateIndividualSMS);
		Settings etaxPaymentNotifyEmail = portletState.getTaxTypePortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_EMAIL);
		portletState.setEtaxPaymentNotifyEmail(etaxPaymentNotifyEmail);
		Settings etaxPaymentNotifySMS = portletState.getTaxTypePortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_SMS);
		portletState.setEtaxPaymentNotifySMS(etaxPaymentNotifySMS);
		Settings systemUrl = portletState.getTaxTypePortletUtil().getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_URL);
		portletState.setSystemUrl(systemUrl);
		
		Settings twostep = portletState.getTaxTypePortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_TWO_STEP_LOGIN);
		portletState.setTwoStepLogin(twostep);
		Settings appr = portletState.getTaxTypePortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_APPROVAL_PROCESS);
		portletState.setApprovalProcess(appr);
		Settings usname = portletState.getTaxTypePortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_USERNAME);
		portletState.setSendingEmailUsername(usname);
		Settings semail = portletState.getTaxTypePortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_EMAIL);
		portletState.setSendingEmail(semail);
		Settings pswd = portletState.getTaxTypePortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_PASSWORD);
		portletState.setSendingEmailPassword(pswd);
		Settings port = portletState.getTaxTypePortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_PORT);
		portletState.setSendingEmailPort(port);
		Settings spc = portletState.getTaxTypePortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_COUNTRY);
		portletState.setPlatformCountry(spc);
		Settings spb = portletState.getTaxTypePortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATORM_BANK);
		portletState.setPlatformBank(spb);
		Settings zacc = portletState.getTaxTypePortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_NUMBER);
		portletState.setTaxCompanyAccount(zacc);
		Settings zacs = portletState.getTaxTypePortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_SORT_CODE);
		portletState.setTaxCompanySortCode(zacs);
		
		
		
		Settings app = portletState.getTaxTypePortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_APPLICATION_NAME);
		portletState.setApplicationName(app);
		Settings mapp = portletState.getTaxTypePortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_MOBILE_APPLICATION_NAME);
		portletState.setMobileApplicationName(mapp);
		Settings proxH = portletState.getTaxTypePortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_HOST);
		portletState.setProxyHost(proxH);
		Settings proxP = portletState.getTaxTypePortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_PORT);
		portletState.setProxyPort(proxP);
		Settings proxU = portletState.getTaxTypePortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_USERNAME);
		portletState.setProxyUsername(proxU);
		Settings proxPwd = portletState.getTaxTypePortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_PASSWORD);
		portletState.setProxyPassword(proxPwd);
		Settings bank = portletState.getTaxTypePortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_BANK_NAME);
		portletState. setBankName(bank);
		Settings currency = portletState.getTaxTypePortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_CURRENCY);
		portletState.setCurrency(currency);
		Settings bpwsu = portletState.getTaxTypePortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_BANK_PAYMENT_WS_URL);
		portletState.setBankPaymentWebServiceUrl(bpwsu);
		Settings zwsu = portletState.getTaxTypePortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_TAX_BODY_WS_URL);
		portletState.setTaxCompanySortCode(zwsu);
	}


	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			TaxTypePortletState portletState) {

		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			TaxTypePortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			TaxTypePortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			TaxTypePortletState.log.debug("Error including error message", e);
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


	public TaxTypePortletUtil getTaxTypePortletUtil() {
		// TODO Auto-generated method stub
		return this.taxTypePortletUtil;
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
	
	








	public void reinitializeForTaxType(TaxTypePortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setTaxTypeCode(null);
		portletState.setTaxTypeName(null);
		portletState.setTaxTypeAccount(null);
		portletState.setTaxTypeSortCode(null);
	}



	public VIEW_TABS getCurrentTab() {
		return currentTab;
	}



	public void setCurrentTab(VIEW_TABS currentTab) {
		this.currentTab = currentTab;
	}


	public Collection<TaxType> getAllTaxTypeListing() {
		return this.allTaxTypeListing;
	}


	public void setAllTaxTypeListing(Collection<TaxType> allTaxTypeListing) {
		this.allTaxTypeListing = allTaxTypeListing;
	}


	public String getTaxTypeCode() {
		return taxTypeCode;
	}


	public void setTaxTypeCode(String taxTypeCode) {
		this.taxTypeCode = taxTypeCode;
	}


	public String getTaxTypeName() {
		return taxTypeName;
	}


	public void setTaxTypeName(String taxTypeName) {
		this.taxTypeName = taxTypeName;
	}


	public String getTaxTypeAccount() {
		return taxTypeAccount;
	}


	public void setTaxTypeAccount(String taxTypeAccount) {
		this.taxTypeAccount = taxTypeAccount;
	}


	public String getTaxTypeSortCode() {
		return taxTypeSortCode;
	}


	public void setTaxTypeSortCode(String taxTypeSortCode) {
		this.taxTypeSortCode = taxTypeSortCode;
	}


	public String getSelectedTypeId() {
		return selectedTypeId;
	}


	public void setSelectedTypeId(String selectedTypeId) {
		this.selectedTypeId = selectedTypeId;
	}



	private Settings primaryFeeSetting;
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


	public Settings getBankPaymentWebServiceUrl() {
		return bankPaymentWebServiceUrl;
	}


	public void setBankPaymentWebServiceUrl(Settings bankPaymentWebServiceUrl) {
		this.bankPaymentWebServiceUrl = bankPaymentWebServiceUrl;
	}


	public CompanyCRUDRights getCompanyCRUDRights() {
		return companyCRUDRights;
	}


	public void setCompanyCRUDRights(CompanyCRUDRights companyCRUDRights) {
		this.companyCRUDRights = companyCRUDRights;
	}


	public PortalUserCRUDRights getPortalUserCRUDRights() {
		return portalUserCRUDRights;
	}


	public void setPortalUserCRUDRights(PortalUserCRUDRights portalUserCRUDRights) {
		this.portalUserCRUDRights = portalUserCRUDRights;
	}


	public static ComminsApplicationState getCas() {
		return cas;
	}


	public static void setCas(ComminsApplicationState cas) {
		TaxTypePortletState.cas = cas;
	}


	
}
