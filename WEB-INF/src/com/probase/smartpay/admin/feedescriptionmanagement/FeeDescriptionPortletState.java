package com.probase.smartpay.admin.feedescriptionmanagement;

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
import smartpay.entity.CompanyFeeDescription;
import smartpay.entity.FeeDescription;
import smartpay.entity.FinancialAmountRestriction;
import smartpay.entity.PortalUser;
import smartpay.entity.PortalUserCRUDRights;
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

public class FeeDescriptionPortletState {

	private static Logger log = Logger.getLogger(FeeDescriptionPortletState.class);
	private static FeeDescriptionPortletUtil feeDescriptionPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private ArrayList<RoleType> portalUserRoleType;
	private static ComminsApplicationState cas;
	

	private String successMessage;
	private String remoteIPAddress;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	
	private Collection<BankBranches> allBankBranchListing;
	private Collection<String> authorizePanelCombinationListing;
	private Integer maximumAuthorizationAllowed;
	private Collection<AuthorizePanel> allAuthorizePanel;
	private Collection<FeeDescription> allFeeDescription;
	private Collection <FinancialAmountRestriction> allFinancialRestrictionsListing;
	private Collection <Integer> personnelPositionList;
	private Collection<PortalUser> allCompanyPersonnel;
	private String selectedFeeDescription;
	private Collection<Company> allCompanyListing;
	private Collection<CompanyFeeDescription> activeCompanyFeeDesciptionListing;
	private String selectedCompany;
	
	
	
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
	
	private CompanyCRUDRights companyCRUDRights;
	private PortalUserCRUDRights portalUserCRUDRights;
	
	
	/****Fee Description***/
	private String Name;
    private String Detail;
    private String Amount;
    private String selectedFeeDescriptionId;
    private Boolean primaryFeeChecked;
	private Settings approvalDirect;
	
	
    
   
	/****enum section****/
    
    
    public static enum FEE_DESCRIPTION_APPROVAL_TYPE{
    	CORE_FEE_VIEW, COMPANY_MAPPINGS
    }
    
    public static enum VIEW_TABS{
    	CREATE_A_FEE_DESCRIPTION, VIEW_FEE_DESCRIPTION_LISTINGS, 
    	MAP_FEE_TO_COMPANY, MAPPED_FEES_TO_COMPANY
    }
    
    public static enum FEE_DESCRIPTION_VIEW
    {
    	CREATE_A_FEE_DESCRIPTION, VIEW_FEE_DESCRIPTION_LISTINGS, 
    	MAP_FEE_TO_COMPANY_VIEW, MAPPED_FEES_TO_COMPANY
    }
    
	
	public static enum NAVIGATE{
		NAVIGATE_ACTIONS
	}
	
	
	public static enum FEE_DESCRIPTION{
		CREATE_A_FEE_DESCRIPTION, UPDATE_FEE_DESCRIPTION, 
		HANDLE_FEE_DESCRIPTION_LISTING, MAP_FEE_TO_COMPANY, UPDATE_FEE_COMPANY_MAPPING, LOGIN_STEP_TWO
	}
	
	/****core section starts here****/
	public static FeeDescriptionPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		FeeDescriptionPortletState portletState = null;
		Logger.getLogger(FeeDescriptionPortletState.class).info("------getInstance");
		try {
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (FeeDescriptionPortletState) session.getAttribute(FeeDescriptionPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new FeeDescriptionPortletState();
					FeeDescriptionPortletUtil util = new FeeDescriptionPortletUtil();
					portletState.setFeeDescriptionPortletUtil(util);
					session.setAttribute(FeeDescriptionPortletState.class.getName(), portletState);
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
	
	
	private void setFeeDescriptionPortletUtil(FeeDescriptionPortletUtil util) {
		// TODO Auto-generated method stub
		this.feeDescriptionPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, FeeDescriptionPortletState portletState) {
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
				FeeDescriptionPortletUtil util = FeeDescriptionPortletUtil.getInstance();
				portletState.setFeeDescriptionPortletUtil(util);
				
				ArrayList<RoleType> pur = portletState.getFeeDescriptionPortletUtil().
						getRoleTypeByPortalUser(portletState.getPortalUser());	
				portletState.setPortalUserRoleType(pur);
				
				loadFeeDescription(portletState);
				loadSettings(portletState);
				loadCompanyListing(portletState);
				loadActiveCompanyFeeDescription(portletState);
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


	
	private static void loadAccessLevels(FeeDescriptionPortletState portletState) {
		// TODO Auto-generated method stub
		CompanyCRUDRights ccr = portletState.getFeeDescriptionPortletUtil().getCompanyCRUDRightsByPortalUser(portletState.getPortalUser());
		PortalUserCRUDRights pucr = portletState.getFeeDescriptionPortletUtil().getPortalUserCRUDRightsByPortalUser(portletState.getPortalUser());
		portletState.setCompanyCRUDRights(ccr);
		portletState.setPortalUserCRUDRights(pucr);
	}
	private static void loadCompanyListing(
			FeeDescriptionPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setAllCompanyListing(portletState.getFeeDescriptionPortletUtil().getCompanyListing(CompanyStatusConstants.COMPANY_STATUS_ACTIVE));
	}


	private static void loadActiveCompanyFeeDescription(
			FeeDescriptionPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setActiveCompanyFeeDesciptionListing(portletState.getFeeDescriptionPortletUtil().getCompanyFeeDescriptionListingByStatus(true));
	}


	private static void loadFeeDescription(FeeDescriptionPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setAllFeeDescription(portletState.getFeeDescriptionPortletUtil().getFeeDescriptionListing(Boolean.TRUE));
	}
	
	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			FeeDescriptionPortletState portletState) {

		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			FeeDescriptionPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			FeeDescriptionPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			FeeDescriptionPortletState.log.debug("Error including error message", e);
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


	public FeeDescriptionPortletUtil getFeeDescriptionPortletUtil() {
		// TODO Auto-generated method stub
		return this.feeDescriptionPortletUtil;
	}


	public ArrayList<RoleType> getPortalUserRoleType() {
		return portalUserRoleType;
	}


	public void setPortalUserRoleType(ArrayList<RoleType> portalUserRoleType) {
		this.portalUserRoleType = portalUserRoleType;
	}
	
	
	public void setAllBankBranchListing(Collection<BankBranches> allBankBranchListing)
	{
		this.allBankBranchListing = allBankBranchListing;
	}
	
	public Collection<BankBranches> getAllBankBranchListing()
	{
		return this.allBankBranchListing;
	}




	public ArrayList<String> getErrorList() {
		return errorList;
	}


	public void setErrorList(ArrayList<String> errorList) {
		this.errorList = errorList;
	}
	
	






	public Collection<FeeDescription> getAllFeeDescription() {
		return allFeeDescription;
	}



	public void setAllFeeDescription(Collection<FeeDescription> allFeeDescription) {
		this.allFeeDescription = allFeeDescription;
	}



	public String getFeeDescriptionName() {
		return Name;
	}



	public void setFeeDescriptionName(String Name) {
		this.Name = Name;
	}



	public String getFeeDescriptionDetail() {
		return Detail;
	}



	public void setFeeDescriptionDetail(String Detail) {
		this.Detail = Detail;
	}



	public String getFeeDescriptionAmount() {
		return Amount;
	}



	public void setFeeDescriptionAmount(String Amount) {
		this.Amount = Amount;
	}



	public String getSelectedFeeDescriptionId() {
		return selectedFeeDescriptionId;
	}



	public void setSelectedFeeDescriptionId(String selectedFeeDescriptionId) {
		this.selectedFeeDescriptionId = selectedFeeDescriptionId;
	}



	public void reinitializeForFeeDescription(FeeDescriptionPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setFeeDescriptionAmount(null);
		portletState.setFeeDescriptionDetail(null);
		portletState.setFeeDescriptionName(null);
	}



	public VIEW_TABS getCurrentTab() {
		return currentTab;
	}



	public void setCurrentTab(VIEW_TABS currentTab) {
		this.currentTab = currentTab;
	}



	public static boolean loadSettings(FeeDescriptionPortletState portletState) {
		// TODO Auto-generated method stub
		Settings primaryFeeSetting = portletState.getFeeDescriptionPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_PRIMARY_FEE_DESCRIPTION);
		portletState.setPrimaryFeeSetting(primaryFeeSetting);
		Settings notifyCorporateFirmEmail = portletState.getFeeDescriptionPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_EMAIL);
		portletState.setNotifyCorporateFirmEmail(notifyCorporateFirmEmail);
		Settings notifyCorporateFirmSms = portletState.getFeeDescriptionPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_SMS);
		portletState.setNotifyCorporateFirmSms(notifyCorporateFirmSms);
		Settings notifyCorporateIndividualEmail = portletState.getFeeDescriptionPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_EMAIL);
		portletState.setNotifyCorporateIndividualEmail(notifyCorporateIndividualEmail);
		Settings notifyCorporateIndividualSMS = portletState.getFeeDescriptionPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_SMS);
		portletState.setNotifyCorporateIndividualSMS(notifyCorporateIndividualSMS);
		Settings etaxPaymentNotifyEmail = portletState.getFeeDescriptionPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_EMAIL);
		portletState.setEtaxPaymentNotifyEmail(etaxPaymentNotifyEmail);
		Settings etaxPaymentNotifySMS = portletState.getFeeDescriptionPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_SMS);
		portletState.setEtaxPaymentNotifySMS(etaxPaymentNotifySMS);
		Settings systemUrl = portletState.getFeeDescriptionPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_URL);
		portletState.setSystemUrl(systemUrl);
		
		Settings twostep = portletState.getFeeDescriptionPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_TWO_STEP_LOGIN);
		portletState.setTwoStepLogin(twostep);
		Settings appr = portletState.getFeeDescriptionPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_APPROVAL_PROCESS);
		portletState.setApprovalProcess(appr);
		Settings usname = portletState.getFeeDescriptionPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_USERNAME);
		portletState.setSendingEmailUsername(usname);
		Settings semail = portletState.getFeeDescriptionPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_EMAIL);
		portletState.setSendingEmail(semail);
		Settings pswd = portletState.getFeeDescriptionPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_PASSWORD);
		portletState.setSendingEmailPassword(pswd);
		Settings port = portletState.getFeeDescriptionPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_PORT);
		portletState.setSendingEmailPort(port);
		Settings spc = portletState.getFeeDescriptionPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_COUNTRY);
		portletState.setPlatformCountry(spc);
		Settings spb = portletState.getFeeDescriptionPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATORM_BANK);
		portletState.setPlatformBank(spb);
		Settings zacc = portletState.getFeeDescriptionPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_NUMBER);
		portletState.setTaxCompanyAccount(zacc);
		Settings zacs = portletState.getFeeDescriptionPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_SORT_CODE);
		portletState.setTaxCompanySortCode(zacs);
		
		
		
		Settings app = portletState.getFeeDescriptionPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_APPLICATION_NAME);
		portletState.setApplicationName(app);
		Settings mapp = portletState.getFeeDescriptionPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_MOBILE_APPLICATION_NAME);
		portletState.setMobileApplicationName(mapp);
		Settings proxH = portletState.getFeeDescriptionPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_HOST);
		portletState.setProxyHost(proxH);
		Settings proxP = portletState.getFeeDescriptionPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_PORT);
		portletState.setProxyPort(proxP);
		Settings proxU = portletState.getFeeDescriptionPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_USERNAME);
		portletState.setProxyUsername(proxU);
		Settings proxPwd = portletState.getFeeDescriptionPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_PASSWORD);
		portletState.setProxyPassword(proxPwd);
		Settings bank = portletState.getFeeDescriptionPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_BANK_NAME);
		portletState. setBankName(bank);
		Settings currency = portletState.getFeeDescriptionPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_CURRENCY);
		portletState.setCurrency(currency);
		Settings bpwsu = portletState.getFeeDescriptionPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_BANK_PAYMENT_WS_URL);
		portletState.setBankPaymentWebServiceUrl(bpwsu);
		Settings tbws = portletState.getFeeDescriptionPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_TAX_BODY_WS_URL);
		portletState.setZraWebServiceUrl(tbws);
		
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


	public String getSelectedFeeDescription() {
		return selectedFeeDescription;
	}


	public void setSelectedFeeDescription(String selectedFeeDescription) {
		this.selectedFeeDescription = selectedFeeDescription;
	}


	public Boolean isPrimaryFeeChecked() {
		return primaryFeeChecked;
	}


	public void setPrimaryFeeChecked(Boolean primaryFeeChecked) {
		this.primaryFeeChecked = primaryFeeChecked;
	}


	public Collection<Company> getAllCompanyListing() {
		return allCompanyListing;
	}


	public void setAllCompanyListing(Collection<Company> allCompanyListing) {
		this.allCompanyListing = allCompanyListing;
	}


	public Collection<CompanyFeeDescription> getActiveCompanyFeeDesciptionListing() {
		return activeCompanyFeeDesciptionListing;
	}


	public void setActiveCompanyFeeDesciptionListing(
			Collection<CompanyFeeDescription> activeCompanyFeeDesciptionListing) {
		this.activeCompanyFeeDesciptionListing = activeCompanyFeeDesciptionListing;
	}


	public String getSelectedCompany() {
		return selectedCompany;
	}


	public void setSelectedCompany(String selectedCompany) {
		this.selectedCompany = selectedCompany;
	}


	public Settings getTwoStepLogin() {
		return twoStepLogin;
	}


	public void setTwoStepLogin(Settings twoStepLogin) {
		this.twoStepLogin = twoStepLogin;
	}


	public Settings getApprovalProcess() {
		return approvalProcess;
	}


	public void setApprovalProcess(Settings approvalProcess) {
		this.approvalProcess = approvalProcess;
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


	public Settings getSendingEmailPassword() {
		return sendingEmailPassword;
	}


	public void setSendingEmailPassword(Settings sendingEmailPassword) {
		this.sendingEmailPassword = sendingEmailPassword;
	}


	public Settings getSendingEmail() {
		return sendingEmail;
	}


	public void setSendingEmail(Settings sendingEmail) {
		this.sendingEmail = sendingEmail;
	}


	public Settings getTaxCompanySortCode() {
		return taxCompanySortCode;
	}


	public void setTaxCompanySortCode(Settings taxCompanySortCode) {
		this.taxCompanySortCode = taxCompanySortCode;
	}


	public Settings getTaxCompanyAccount() {
		return taxCompanyAccount;
	}


	public void setTaxCompanyAccount(Settings taxCompanyAccount) {
		this.taxCompanyAccount = taxCompanyAccount;
	}


	public Settings getPlatformBank() {
		return platformBank;
	}


	public void setPlatformBank(Settings platformBank) {
		this.platformBank = platformBank;
	}


	public Settings getPlatformCountry() {
		return platformCountry;
	}


	public void setPlatformCountry(Settings platformCountry) {
		this.platformCountry = platformCountry;
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


	public Settings getCurrency() {
		return currency;
	}


	public void setCurrency(Settings currency) {
		this.currency = currency;
	}


	public static ComminsApplicationState getCas() {
		return cas;
	}


	public static void setCas(ComminsApplicationState cas) {
		FeeDescriptionPortletState.cas = cas;
	}




	
}
