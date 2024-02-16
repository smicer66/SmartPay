package com.probase.smartpay.admin.financialamountrestrictionmanagement;

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
import smartpay.entity.PortalUser;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.enumerations.CompanyStatusConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.service.SwpService;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.probase.smartpay.commins.ComminsApplicationState;
import com.probase.smartpay.commins.Util;
import com.sf.primepay.smartpay13.ServiceLocator;

public class FinancialAmountRestrictionManagementPortletState {

	private static Logger log = Logger.getLogger(FinancialAmountRestrictionManagementPortletState.class);
	private static FinancialAmountRestrictionManagementPortletUtil FinancialAmountRestrictionManagementPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private ArrayList<RoleType> portalUserRoleType;
	private static ComminsApplicationState cas;
	

	private String successMessage;
	private String remoteIPAddress;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	
	
	private Collection <FinancialAmountRestriction> allFinancialRestrictionsListing;
	private Collection<Company> allCompanyListing;
	private String selectedCompanyId;
	
	
	/****settings*****/
	private Settings primaryFeeSetting;
	private Settings notifyCorporateFirmEmail;
	private Settings notifyCorporateFirmSms;
	private Settings notifyCorporateIndividualEmail;
	private Settings notifyCorporateIndividualSMS;
	private Settings etaxPaymentNotifyEmail;
	private Settings etaxPaymentNotifySMS;
	private Settings systemUrl;
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
	
	
    
    /*********financial restriction****/
    private String financialPaymentRestrictionName;
	private String minimumPaymentThreshold;
	private String maximumPaymentThreshold;
	private Collection<FinancialAmountRestriction> financialAmountRestrictionListing;
	private Long selectedFinancialAmountRestrictionId;
	
	/****enum section****/
    
    
    public static enum FINANCIAL_AMOUNT_RESTRICTION{
    	CREATE_A_FINANCIAL_AMOUNT_RESTRICTION, EDIT_A_FINANCIAL_AMOUNT_RESTRICTION, FINANCIAL_AMOUNT_RESTRICTION_LISTINGS, PRE_CREATE_FAR, PRE_FAR_LISTING
    }
    
    public static enum VIEW_TABS{
		FINANCIAL_AMOUNT_RESTRICTION_LISTING, CREATE_FINANCIAL_AMOUNT_RESTRICTION
    	
    }
    
    public static enum FINANCIAL_AMOUNT_RESTRICTION_VIEW
    {
    	CREATE_A_FINANCIAL_AMOUNT_RESTRICTION_VIEW, FINANCIAL_AMOUNT_RESTRICTION_LISTINGS_VIEW
    }
    
    
	
	public static enum NAVIGATE{
		NAVIGATE_ACTIONS
	}
	
	
	
	/****core section starts here****/
	public static FinancialAmountRestrictionManagementPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		FinancialAmountRestrictionManagementPortletState portletState = null;
		Logger.getLogger(FinancialAmountRestrictionManagementPortletState.class).info("------getInstance");
		try {
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (FinancialAmountRestrictionManagementPortletState) session.getAttribute(FinancialAmountRestrictionManagementPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new FinancialAmountRestrictionManagementPortletState();
					FinancialAmountRestrictionManagementPortletUtil util = new FinancialAmountRestrictionManagementPortletUtil();
					portletState.setFinancialAmountRestrictionManagementPortletUtil(util);
					session.setAttribute(FinancialAmountRestrictionManagementPortletState.class.getName(), portletState);
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
	
	
	
	
	private void setFinancialAmountRestrictionManagementPortletUtil(FinancialAmountRestrictionManagementPortletUtil util) {
		// TODO Auto-generated method stub
		this.FinancialAmountRestrictionManagementPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, FinancialAmountRestrictionManagementPortletState portletState) {
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
				FinancialAmountRestrictionManagementPortletUtil util = FinancialAmountRestrictionManagementPortletUtil.getInstance();
				portletState.setFinancialAmountRestrictionManagementPortletUtil(util);
				
				ArrayList<RoleType> pur = portletState.getFinancialAmountRestrictionManagementPortletUtil().
						getRoleTypeByPortalUser(portletState.getPortalUser());	
				portletState.setPortalUserRoleType(pur);
				
				loadSettings(portletState);
				loadCompany(portletState);
				portletState.setCurrentTab(VIEW_TABS.CREATE_FINANCIAL_AMOUNT_RESTRICTION);
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


	


	private static void loadCompany(
			FinancialAmountRestrictionManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setAllCompanyListing(portletState.getFinancialAmountRestrictionManagementPortletUtil().getCompanyListing(CompanyStatusConstants.COMPANY_STATUS_ACTIVE));
	}




	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			FinancialAmountRestrictionManagementPortletState portletState) {

		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			FinancialAmountRestrictionManagementPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			FinancialAmountRestrictionManagementPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			FinancialAmountRestrictionManagementPortletState.log.debug("Error including error message", e);
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


	public FinancialAmountRestrictionManagementPortletUtil getFinancialAmountRestrictionManagementPortletUtil() {
		// TODO Auto-generated method stub
		return this.FinancialAmountRestrictionManagementPortletUtil;
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
	
	
	public Collection<FinancialAmountRestriction> getFinancialAmountRestrictionListing()
	{
		return this.financialAmountRestrictionListing;
	}
	
	public void setFinancialAmountRestrictionListing(Collection<FinancialAmountRestriction> financialAmountRestrictionListing)
	{
		this.financialAmountRestrictionListing = financialAmountRestrictionListing;
	}
	
	
	public Long getSelectedFinancialAmountRestrictionId()
	{
		return this.selectedFinancialAmountRestrictionId;
	}
	
	public void setSelectedFinancialAmountRestrictionId(Long selectedFinancialAmountRestrictionId)
	{
		this.selectedFinancialAmountRestrictionId = selectedFinancialAmountRestrictionId;
	}
	


	

	public VIEW_TABS getCurrentTab() {
		return currentTab;
	}



	public void setCurrentTab(VIEW_TABS currentTab) {
		this.currentTab = currentTab;
	}



	public static boolean loadSettings(FinancialAmountRestrictionManagementPortletState portletState) {
		// TODO Auto-generated method stub
		Settings primaryFeeSetting = portletState.getFinancialAmountRestrictionManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_PRIMARY_FEE_DESCRIPTION);
		portletState.setPrimaryFeeSetting(primaryFeeSetting);
		Settings notifyCorporateFirmEmail = portletState.getFinancialAmountRestrictionManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_EMAIL);
		portletState.setNotifyCorporateFirmEmail(notifyCorporateFirmEmail);
		Settings notifyCorporateFirmSms = portletState.getFinancialAmountRestrictionManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_SMS);
		portletState.setNotifyCorporateFirmSms(notifyCorporateFirmSms);
		Settings notifyCorporateIndividualEmail = portletState.getFinancialAmountRestrictionManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_EMAIL);
		portletState.setNotifyCorporateIndividualEmail(notifyCorporateIndividualEmail);
		Settings notifyCorporateIndividualSMS = portletState.getFinancialAmountRestrictionManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_SMS);
		portletState.setNotifyCorporateIndividualSMS(notifyCorporateIndividualSMS);
		Settings etaxPaymentNotifyEmail = portletState.getFinancialAmountRestrictionManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_EMAIL);
		portletState.setEtaxPaymentNotifyEmail(etaxPaymentNotifyEmail);
		Settings etaxPaymentNotifySMS = portletState.getFinancialAmountRestrictionManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_SMS);
		portletState.setEtaxPaymentNotifySMS(etaxPaymentNotifySMS);
		Settings systemUrl = portletState.getFinancialAmountRestrictionManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_URL);
		portletState.setSystemUrl(systemUrl);
		
		Settings twostep = portletState.getFinancialAmountRestrictionManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_TWO_STEP_LOGIN);
		portletState.setTwoStepLogin(twostep);
		Settings appr = portletState.getFinancialAmountRestrictionManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_APPROVAL_PROCESS);
		portletState.setApprovalProcess(appr);
		Settings usname = portletState.getFinancialAmountRestrictionManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_USERNAME);
		portletState.setSendingEmailUsername(usname);
		Settings semail = portletState.getFinancialAmountRestrictionManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_EMAIL);
		portletState.setSendingEmail(semail);
		Settings pswd = portletState.getFinancialAmountRestrictionManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_PASSWORD);
		portletState.setSendingEmailPassword(pswd);
		Settings port = portletState.getFinancialAmountRestrictionManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_PORT);
		portletState.setSendingEmailPort(port);
		Settings spc = portletState.getFinancialAmountRestrictionManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_COUNTRY);
		portletState.setPlatformCountry(spc);
		Settings spb = portletState.getFinancialAmountRestrictionManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATORM_BANK);
		portletState.setPlatformBank(spb);
		Settings zacc = portletState.getFinancialAmountRestrictionManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_NUMBER);
		portletState.setTaxCompanyAccount(zacc);
		Settings zacs = portletState.getFinancialAmountRestrictionManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_SORT_CODE);
		portletState.setTaxCompanySortCode(zacs);
		
		
		
		Settings app = portletState.getFinancialAmountRestrictionManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_APPLICATION_NAME);
		portletState.setApplicationName(app);
		Settings mapp = portletState.getFinancialAmountRestrictionManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_MOBILE_APPLICATION_NAME);
		portletState.setMobileApplicationName(mapp);
		Settings proxH = portletState.getFinancialAmountRestrictionManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_HOST);
		portletState.setProxyHost(proxH);
		Settings proxP = portletState.getFinancialAmountRestrictionManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_PORT);
		portletState.setProxyPort(proxP);
		Settings proxU = portletState.getFinancialAmountRestrictionManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_USERNAME);
		portletState.setProxyUsername(proxU);
		Settings proxPwd = portletState.getFinancialAmountRestrictionManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_PASSWORD);
		portletState.setProxyPassword(proxPwd);
		Settings bank = portletState.getFinancialAmountRestrictionManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_BANK_NAME);
		portletState. setBankName(bank);
		Settings currency = portletState.getFinancialAmountRestrictionManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_CURRENCY);
		portletState.setCurrency(currency);
		Settings bpwsu = portletState.getFinancialAmountRestrictionManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_BANK_PAYMENT_WS_URL);
		portletState.setBankPaymentWebServiceUrl(bpwsu);
		Settings tbws = portletState.getFinancialAmountRestrictionManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_TAX_BODY_WS_URL);
		portletState.setZraWebServiceUrl(tbws);
		
		
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



	public String getFinancialPaymentRestrictionName() {
		return financialPaymentRestrictionName;
	}



	public void setFinancialPaymentRestrictionName(
			String financialPaymentRestrictionName) {
		this.financialPaymentRestrictionName = financialPaymentRestrictionName;
	}



	public Collection <FinancialAmountRestriction> getAllFinancialRestrictionsListing() {
		return allFinancialRestrictionsListing;
	}



	public void setAllFinancialRestrictionsListing(
			Collection <FinancialAmountRestriction> allFinancialRestrictionsListing) {
		this.allFinancialRestrictionsListing = allFinancialRestrictionsListing;
	}



	public String getMinimumPaymentThreshold() {
		return minimumPaymentThreshold;
	}



	public void setMinimumPaymentThreshold(String minimumPaymentThreshold) {
		this.minimumPaymentThreshold = minimumPaymentThreshold;
	}



	public String getMaximumPaymentThreshold() {
		return maximumPaymentThreshold;
	}



	public void setMaximumPaymentThreshold(String maximumPaymentThreshold) {
		this.maximumPaymentThreshold = maximumPaymentThreshold;
	}


	public Settings getSystemUrl() {
		return systemUrl;
	}



	public void setSystemUrl(Settings systemUrl) {
		this.systemUrl = systemUrl;
	}




	public void reinitializeForFinancialAmountRestriction(
			FinancialAmountRestrictionManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setMinimumPaymentThreshold(null);
		portletState.setMaximumPaymentThreshold(null);
	}




	public Collection<Company> getAllCompanyListing() {
		return allCompanyListing;
	}




	public void setAllCompanyListing(Collection<Company> allCompanyListing) {
		this.allCompanyListing = allCompanyListing;
	}




	public void setSelectedCompanyId(String parameter) {
		// TODO Auto-generated method stub
		this.selectedCompanyId = parameter;
	}

	public String getSelectedCompanyId() {
		// TODO Auto-generated method stub
		return this.selectedCompanyId;
	}



	private Settings platformCountry;
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




	public Settings getBankName() {
		return bankName;
	}




	public void setBankName(Settings bankName) {
		this.bankName = bankName;
	}




	public Settings getProxyPassword() {
		return proxyPassword;
	}




	public void setProxyPassword(Settings proxyPassword) {
		this.proxyPassword = proxyPassword;
	}




	public Settings getCurrency() {
		return currency;
	}




	public void setCurrency(Settings currency) {
		this.currency = currency;
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




	public Settings getProxyUsername() {
		return proxyUsername;
	}




	public void setProxyUsername(Settings proxyUsername) {
		this.proxyUsername = proxyUsername;
	}




	public static ComminsApplicationState getCas() {
		return cas;
	}




	public static void setCas(ComminsApplicationState cas) {
		FinancialAmountRestrictionManagementPortletState.cas = cas;
	}

	
}
