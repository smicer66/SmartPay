package com.probase.smartpay.admin.mandatepanelmanagement;

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
import smartpay.entity.FeeDescription;
import smartpay.entity.FinancialAmountRestriction;
import smartpay.entity.PortalUser;
import smartpay.entity.PortalUserCRUDRights;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.enumerations.CompanyStatusConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.service.SwpService;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.probase.smartpay.commins.ComminsApplicationState;
import com.probase.smartpay.commins.Util;
import com.sf.primepay.smartpay13.ServiceLocator;

public class MandatePanelPortletState {

	private static Logger log = Logger.getLogger(MandatePanelPortletState.class);
	private static MandatePanelPortletUtil mandatePanelPortletUtil;
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
	private Collection<Company> allCompanyListing;
	private Collection<AuthorizePanelCombination> allAuthorizePanelCombination;
	
	
	private CompanyCRUDRights companyCRUDRights;
	private PortalUserCRUDRights portalUserCRUDRights;
	
	
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
	
	/****authorization panel****/
	private String panelName;
	private Long selectedAuthorizationPanelId;
	private String panelType;
	private String selectedMapPanelPortalUser;
	private String selectedMapPanel;
	private String selectedMapPosition;
	private String selectedMapPanelPortalUserCombination;
	private VIEW_TABS currentTab;
	private String selectedCompanyId;
	private String selectedPanelType;
	private String selectedFinancialAmountRestriction;
	private Settings platformCountry;
	
	
	/****enum section****/
    
    
	public static enum VIEW_TABS{
    	CREATE_A_MANDATE_PANEL, VIEW_MANDATE_PANEL_LISTINGS,
    	MANAGE_SETTINGS, VIEW_SETTINGS, MAP_PANEL_TO_PORTAL_USER, USERS_MAPPED_TO_PANEL
    }
    
    public static enum MANDATE_PANEL_VIEW
    {
    	CREATE_A_MANDATE_PANEL, VIEW_MANDATE_PANEL_LISTINGS, MAP_PANEL_TO_PORTAL_USER, VIEW_USERS_MAPPED_TO_PANEL
    }
    
    
	public static enum NAVIGATE{
		NAVIGATE_ACTIONS
	}
	
	
	public static enum AUTHORISATION_PANEL{
		PRE_CREATE_AUTH_PANEL, CREATE_AUTH_PANEL, AUTH_PANEL_LISTING_ACTION, 
		EDIT_AUTH_PANEL, MAP_PORTAL_USER_STEP_ONE, MAP_PORTAL_USER_STEP_TWO,
		PRE_MAP_PANEL_TO_PORTAL_USER, PRE_VIEW_LISTING, UPDATE_USER_PANEL_MAPPING, LOGIN_STEP_TWO
	}
	
	/****core section starts here****/
	public static MandatePanelPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		MandatePanelPortletState portletState = null;
		Logger.getLogger(MandatePanelPortletState.class).info("------getInstance");
		try {
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (MandatePanelPortletState) session.getAttribute(MandatePanelPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new MandatePanelPortletState();
					MandatePanelPortletUtil util = new MandatePanelPortletUtil();
					portletState.setMandatePanelPortletUtil(util);
					session.setAttribute(MandatePanelPortletState.class.getName(), portletState);
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
	
	
	private void setMandatePanelPortletUtil(MandatePanelPortletUtil util) {
		// TODO Auto-generated method stub
		this.mandatePanelPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, MandatePanelPortletState portletState) {
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
				MandatePanelPortletUtil util = MandatePanelPortletUtil.getInstance();
				portletState.setMandatePanelPortletUtil(util);
				
				ArrayList<RoleType> pur = portletState.getMandatePanelPortletUtil().
						getRoleTypeByPortalUser(portletState.getPortalUser());	
				portletState.setPortalUserRoleType(pur);
				
				loadSigningMandate(portletState);
				loadSettings(portletState);
				loadAccessLevels(portletState);
				if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(
						RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
				{
					portletState.setAllFinancialRestrictionsListing(portletState.getMandatePanelPortletUtil().getFinancialAmountRestrictionsByCompanyId(
							portletState.getPortalUser().getCompany().getId()));
				}
				if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
				{
					portletState.setAllCompanyListing(portletState.getMandatePanelPortletUtil().getCompanyListing(CompanyStatusConstants.COMPANY_STATUS_ACTIVE));
				}
				
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


	
	
	

	private static void loadAccessLevels(MandatePanelPortletState portletState) {
		// TODO Auto-generated method stub
		CompanyCRUDRights ccr = portletState.getMandatePanelPortletUtil().getCompanyCRUDRightsByPortalUser(portletState.getPortalUser());
		PortalUserCRUDRights pucr = portletState.getMandatePanelPortletUtil().getPortalUserCRUDRightsByPortalUser(portletState.getPortalUser());
		portletState.setCompanyCRUDRights(ccr);
		portletState.setPortalUserCRUDRights(pucr);
	}


	private static void loadSigningMandate(MandatePanelPortletState portletState) {
		// TODO Auto-generated method stub
		//portletState.setAuthorizePanelCombinationListing(portletState.getMandatePanelPortletUtil().getAuthorizePanelCombinationListingGroupByCode());
		
	}


	

	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			MandatePanelPortletState portletState) {

		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			MandatePanelPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			MandatePanelPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			MandatePanelPortletState.log.debug("Error including error message", e);
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


	public MandatePanelPortletUtil getMandatePanelPortletUtil() {
		// TODO Auto-generated method stub
		return this.mandatePanelPortletUtil;
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
	
	
	


	public Collection<String> getAuthorizePanelCombinationListing() {
		return authorizePanelCombinationListing;
	}


	public void setAuthorizePanelCombinationListing(
			Collection<String> authorizePanelCombinationListing) {
		this.authorizePanelCombinationListing = authorizePanelCombinationListing;
	}


	public Integer getMaximumAuthorizationAllowed() {
		return maximumAuthorizationAllowed;
	}


	public void setMaximumAuthorizationAllowed(
			Integer maximumAuthorizationAllowed) {
		this.maximumAuthorizationAllowed = maximumAuthorizationAllowed;
	}




	public String getPanelName() {
		return panelName;
	}



	public void setPanelName(String panelName) {
		this.panelName = panelName;
	}



	public Long getSelectedAuthorizationPanelId() {
		return selectedAuthorizationPanelId;
	}



	public void setSelectedAuthorizationPanelId(
			Long selectedAuthorizationPanelId) {
		this.selectedAuthorizationPanelId = selectedAuthorizationPanelId;
	}



	public Collection<AuthorizePanel> getAllAuthorizePanel() {
		return allAuthorizePanel;
	}



	public void setAllAuthorizePanel(Collection<AuthorizePanel> allAuthorizePanel) {
		this.allAuthorizePanel = allAuthorizePanel;
	}







	public Collection<FeeDescription> getAllFeeDescription() {
		return allFeeDescription;
	}



	public void setAllFeeDescription(Collection<FeeDescription> allFeeDescription) {
		this.allFeeDescription = allFeeDescription;
	}





	public VIEW_TABS getCurrentTab() {
		return currentTab;
	}



	public void setCurrentTab(VIEW_TABS currentTab) {
		this.currentTab = currentTab;
	}



	public static boolean loadSettings(MandatePanelPortletState portletState) {
		// TODO Auto-generated method stub
		Settings primaryFeeSetting = portletState.getMandatePanelPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_PRIMARY_FEE_DESCRIPTION);
		portletState.setPrimaryFeeSetting(primaryFeeSetting);
		Settings notifyCorporateFirmEmail = portletState.getMandatePanelPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_EMAIL);
		portletState.setNotifyCorporateFirmEmail(notifyCorporateFirmEmail);
		Settings notifyCorporateFirmSms = portletState.getMandatePanelPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_SMS);
		portletState.setNotifyCorporateFirmSms(notifyCorporateFirmSms);
		Settings notifyCorporateIndividualEmail = portletState.getMandatePanelPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_EMAIL);
		portletState.setNotifyCorporateIndividualEmail(notifyCorporateIndividualEmail);
		Settings notifyCorporateIndividualSMS = portletState.getMandatePanelPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_SMS);
		portletState.setNotifyCorporateIndividualSMS(notifyCorporateIndividualSMS);
		Settings etaxPaymentNotifyEmail = portletState.getMandatePanelPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_EMAIL);
		portletState.setEtaxPaymentNotifyEmail(etaxPaymentNotifyEmail);
		Settings etaxPaymentNotifySMS = portletState.getMandatePanelPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_SMS);
		portletState.setEtaxPaymentNotifySMS(etaxPaymentNotifySMS);
		Settings systemUrl = portletState.getMandatePanelPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_URL);
		portletState.setSystemUrl(systemUrl);
		
		Settings twostep = portletState.getMandatePanelPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_TWO_STEP_LOGIN);
		portletState.setTwoStepLogin(twostep);
		Settings appr = portletState.getMandatePanelPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_APPROVAL_PROCESS);
		portletState.setApprovalProcess(appr);
		Settings usname = portletState.getMandatePanelPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_USERNAME);
		portletState.setSendingEmailUsername(usname);
		Settings semail = portletState.getMandatePanelPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_EMAIL);
		portletState.setSendingEmail(semail);
		Settings pswd = portletState.getMandatePanelPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_PASSWORD);
		portletState.setSendingEmailPassword(pswd);
		Settings port = portletState.getMandatePanelPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_PORT);
		portletState.setSendingEmailPort(port);
		Settings spc = portletState.getMandatePanelPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_COUNTRY);
		portletState.setPlatformCountry(spc);
		Settings spb = portletState.getMandatePanelPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATORM_BANK);
		portletState.setPlatformBank(spb);
		Settings zacc = portletState.getMandatePanelPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_NUMBER);
		portletState.setTaxCompanyAccount(zacc);
		Settings zacs = portletState.getMandatePanelPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_SORT_CODE);
		portletState.setTaxCompanySortCode(zacs);
		
		
		
		Settings app = portletState.getMandatePanelPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_APPLICATION_NAME);
		portletState.setApplicationName(app);
		Settings mapp = portletState.getMandatePanelPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_MOBILE_APPLICATION_NAME);
		portletState.setMobileApplicationName(mapp);
		Settings proxH = portletState.getMandatePanelPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_HOST);
		portletState.setProxyHost(proxH);
		Settings proxP = portletState.getMandatePanelPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_PORT);
		portletState.setProxyPort(proxP);
		Settings proxU = portletState.getMandatePanelPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_USERNAME);
		portletState.setProxyUsername(proxU);
		Settings proxPwd = portletState.getMandatePanelPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_PASSWORD);
		portletState.setProxyPassword(proxPwd);
		Settings bank = portletState.getMandatePanelPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_BANK_NAME);
		portletState. setBankName(bank);
		Settings currency = portletState.getMandatePanelPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_CURRENCY);
		portletState.setCurrency(currency);
		Settings bpwsu = portletState.getMandatePanelPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_BANK_PAYMENT_WS_URL);
		portletState.setBankPaymentWebServiceUrl(bpwsu);
		Settings tbws = portletState.getMandatePanelPortletUtil().
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




	public String getPanelType() {
		return panelType;
	}



	public void setPanelType(String panelType) {
		this.panelType = panelType;
	}



	public String getSelectedMapPanelPortalUser() {
		return selectedMapPanelPortalUser;
	}



	public void setSelectedMapPanelPortalUser(String selectedMapPanelPortalUser) {
		this.selectedMapPanelPortalUser = selectedMapPanelPortalUser;
	}



	public String getSelectedMapPanel() {
		return selectedMapPanel;
	}



	public void setSelectedMapPanel(String selectedMapPanel) {
		this.selectedMapPanel = selectedMapPanel;
	}



	public String getSelectedMapPosition() {
		return selectedMapPosition;
	}



	public void setSelectedMapPosition(String selectedMapPosition) {
		this.selectedMapPosition = selectedMapPosition;
	}



	public void reinitializeForMapPanelToPortalUser(
			MandatePanelPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setSelectedMapPanelPortalUser(null);
		portletState.setSelectedMapPanel(null);
		portletState.setSelectedMapPosition(null);
	}





	public String getSelectedMapPanelPortalUserCombination() {
		return selectedMapPanelPortalUserCombination;
	}



	public void setSelectedMapPanelPortalUserCombination(
			String selectedMapPanelPortalUserCombination) {
		this.selectedMapPanelPortalUserCombination = selectedMapPanelPortalUserCombination;
	}




	public Settings getSystemUrl() {
		return systemUrl;
	}



	public void setSystemUrl(Settings systemUrl) {
		this.systemUrl = systemUrl;
	}



	public Collection <Integer> getPersonnelPositionList() {
		return personnelPositionList;
	}



	public void setPersonnelPositionList(Collection <Integer> personnelPositionList) {
		this.personnelPositionList = personnelPositionList;
	}



	public Collection<PortalUser> getAllCompanyPersonnel() {
		return allCompanyPersonnel;
	}



	public void setAllCompanyPersonnel(Collection<PortalUser> allCompanyPersonnel) {
		this.allCompanyPersonnel = allCompanyPersonnel;
	}

	


	public Collection <FinancialAmountRestriction> getAllFinancialRestrictionsListing() {
		return allFinancialRestrictionsListing;
	}



	public void setAllFinancialRestrictionsListing(
			Collection <FinancialAmountRestriction> allFinancialRestrictionsListing) {
		this.allFinancialRestrictionsListing = allFinancialRestrictionsListing;
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


	public void setSelectedPanelType(String parameter) {
		// TODO Auto-generated method stub
		this.selectedPanelType = parameter;
	}
	
	
	public String getSelectedPanelType() {
		// TODO Auto-generated method stub
		return this.selectedPanelType;
	}


	public void setSelectedFinancialAmountRestriction(String parameter) {
		// TODO Auto-generated method stub
		this.selectedFinancialAmountRestriction = parameter;
	}
	
	
	public String getSelectedFinancialAmountRestriction() {
		// TODO Auto-generated method stub
		return this.selectedFinancialAmountRestriction;
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


	public Collection<AuthorizePanelCombination> getAllAuthorizePanelCombination() {
		return allAuthorizePanelCombination;
	}


	public void setAllAuthorizePanelCombination(
			Collection<AuthorizePanelCombination> allAuthorizePanelCombination) {
		this.allAuthorizePanelCombination = allAuthorizePanelCombination;
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
		MandatePanelPortletState.cas = cas;
	}

}
