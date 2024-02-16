package com.probase.smartpay.admin.companymanagement;

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
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.service.SwpService;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.probase.smartpay.commins.ComminsApplicationState;
import com.probase.smartpay.commins.Util;
import com.sf.primepay.smartpay13.ServiceLocator;

public class CompanyManagementPortletState {

	private static Logger log = Logger.getLogger(CompanyManagementPortletState.class);
	private static CompanyManagementPortletUtil CompanyManagementPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private ArrayList<RoleType> portalUserRoleType;
	

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
	private Collection<RoleType> roleTypeListing;
	private String mandatePanelsOn;
	private static ComminsApplicationState cas;
	
	
	
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
	private Settings settingsZRAAccountSortCode;
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
	
	/***company creation***/
	private String companyname;
	private String companyrcnumber;
	private String line1addressofcompany;
	private String line2addressofcompany;
	private String companyemailaddress;
	private String companycontactphonenumber;
	private String bankNumber;
	private String selectedBankBranchId;
	private String selectedUserRoleId;
	private String selectedCompanyType;
	private String selectedCompanyClass;
	private String selectedCreateMandatePanels;
	private String tpin;
	
	
	
	/****settings*********/
	private String selectedFeeDescription;
	private String selectedEmailNotificationForCorporateFirm;
	private String selectedMobileNotificationForCorporateFirm;
	private String selectedEmailNotificationForCorporateIndivididuals;
	private String selectedMobileNotificationForCorporateIndivididuals;
	private String selectedEtaxPaymentEmailNotify;
	private String selectedEtaxPaymentSmsNotify;
	private Collection<Company> companyListing;
	private Company selectedCompany;
	
	
	private Long selectedCompanyId;
	private PortalUserCRUDRights portalUserCRUDRights;
	private CompanyCRUDRights companyCRUDRights;
	
	/****enum section****/
    
   
    
    public static enum VIEW_TABS{
    	CREATE_A_COMPANY, VIEW_COMPANY_LISTINGS, 
    }
    
    
    
    public static enum COMPANY_VIEW
    {
    	CREATE_A_COMPANY, VIEW_COMPANY_LISTINGS
    }
    
    
	public static enum COMPANY_CREATION{
		CREATE_A_COMPANY_STEP_ONE, CREATE_A_COMPANY_STEP_TWO, 
		EDIT_A_COMPANY_STEP_ONE, EDIT_A_COMPANY_STEP_TWO, LOGIN_STEP_TWO
	}
	
	public static enum COMPANY_LISTING{
		MODIFY_COMPANY
	}
	
	
	public static enum NAVIGATE{
		NAVIGATE_ACTIONS
	}
	
	
	
	/****core section starts here****/
	public static CompanyManagementPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		CompanyManagementPortletState portletState = null;
		Logger.getLogger(CompanyManagementPortletState.class).info("------getInstance");
		try {
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (CompanyManagementPortletState) session.getAttribute(CompanyManagementPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new CompanyManagementPortletState();
					CompanyManagementPortletUtil util = new CompanyManagementPortletUtil();
					portletState.setCompanyManagementPortletUtil(util);
					session.setAttribute(CompanyManagementPortletState.class.getName(), portletState);
					defaultInit(request, portletState);
					cas = ComminsApplicationState.getInstance(request, response);
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
	
	
	
	
	public void reinitializeForCreateCompany(CompanyManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setCompanyname(null);
		portletState.setCompanyrcnumber(null);
		portletState.setCompanycontactphonenumber(null);
		portletState.setCompanyemailaddress(null);
		portletState.setLine1addressofcompany(null);
		portletState.setLine2addressofcompany(null);
		portletState.setBankNumber(null);
		portletState.setSelectedBankBranchId(null);
	}
	
	
	private void setCompanyManagementPortletUtil(CompanyManagementPortletUtil util) {
		// TODO Auto-generated method stub
		this.CompanyManagementPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, CompanyManagementPortletState portletState) {
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
				CompanyManagementPortletUtil util = CompanyManagementPortletUtil.getInstance();
				portletState.setCompanyManagementPortletUtil(util);
				
				ArrayList<RoleType> pur = portletState.getCompanyManagementPortletUtil().
						getRoleTypeByPortalUser(portletState.getPortalUser());	
				portletState.setPortalUserRoleType(pur);
				
				loadBankBranches(portletState);
				loadCompanyListing(portletState);
				loadSettings(portletState);
				loadRoleTypes(portletState);
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
			CompanyManagementPortletState portletState) {
		// TODO Auto-generated method stub
		CompanyCRUDRights ccr = portletState.getCompanyManagementPortletUtil().getCompanyCRUDRightsByPortalUser(portletState.getPortalUser());
		PortalUserCRUDRights pucr = portletState.getCompanyManagementPortletUtil().getPortalUserCRUDRightsByPortalUser(portletState.getPortalUser());
		portletState.setCompanyCRUDRights(ccr);
		portletState.setPortalUserCRUDRights(pucr);
	}




	private static void loadRoleTypes(CompanyManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setRoleTypeListing(portletState.getCompanyManagementPortletUtil().getAllRoleTypes());
	}




	private static void loadCompanyListing(CompanyManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setCompanyListing(portletState.getCompanyManagementPortletUtil().getAllCompanyListing());
	}



	private static void loadBankBranches(CompanyManagementPortletState portletState) {
		// TODO Auto-generated method stub
		Collection<BankBranches> bankBranchListing = portletState.getCompanyManagementPortletUtil().getAllBankBranchListing();
		portletState.setAllBankBranchListing(bankBranchListing);
	}


	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			CompanyManagementPortletState portletState) {

		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			CompanyManagementPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			CompanyManagementPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			CompanyManagementPortletState.log.debug("Error including error message", e);
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


	public CompanyManagementPortletUtil getCompanyManagementPortletUtil() {
		// TODO Auto-generated method stub
		return this.CompanyManagementPortletUtil;
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


	/****company creation section starts here****/
	public String getCompanyname() {
		return companyname;
	}


	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}


	public String getCompanyrcnumber() {
		return companyrcnumber;
	}


	public void setCompanyrcnumber(String companyrcnumber) {
		this.companyrcnumber = companyrcnumber;
	}


	public String getLine1addressofcompany() {
		return line1addressofcompany;
	}


	public void setLine1addressofcompany(String line1addressofcompany) {
		this.line1addressofcompany = line1addressofcompany;
	}


	public String getLine2addressofcompany() {
		return line2addressofcompany;
	}


	public void setLine2addressofcompany(String line2addressofcompany) {
		this.line2addressofcompany = line2addressofcompany;
	}


	public String getCompanyemailaddress() {
		return companyemailaddress;
	}


	public void setCompanyemailaddress(String companyemailaddress) {
		this.companyemailaddress = companyemailaddress;
	}


	public String getCompanycontactphonenumber() {
		return companycontactphonenumber;
	}


	public void setCompanycontactphonenumber(String companycontactphonenumber) {
		this.companycontactphonenumber = companycontactphonenumber;
	}

	public String getBankNumber() {
		return bankNumber;
	}


	public void setBankNumber(String bankNumber) {
		this.bankNumber = bankNumber;
	}


	public ArrayList<String> getErrorList() {
		return errorList;
	}


	public void setErrorList(ArrayList<String> errorList) {
		this.errorList = errorList;
	}
	

	public String getSelectedBankBranchId() {
		return selectedBankBranchId;
	}


	public void setSelectedBankBranchId(String selectedBankBranchId) {
		this.selectedBankBranchId = selectedBankBranchId;
	}

	public Collection<Company> getCompanyListing() {
		return companyListing;
	}


	public void setCompanyListing(Collection<Company> companyListing) {
		this.companyListing = companyListing;
	}


	public Company getSelectedCompany() {
		return selectedCompany;
	}


	public void setSelectedCompany(Company company) {
		this.selectedCompany = company;
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



	public static boolean loadSettings(CompanyManagementPortletState portletState) {
		// TODO Auto-generated method stub
		Settings primaryFeeSetting = portletState.getCompanyManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_PRIMARY_FEE_DESCRIPTION);
		portletState.setPrimaryFeeSetting(primaryFeeSetting);
		Settings notifyCorporateFirmEmail = portletState.getCompanyManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_EMAIL);
		portletState.setNotifyCorporateFirmEmail(notifyCorporateFirmEmail);
		Settings notifyCorporateFirmSms = portletState.getCompanyManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_SMS);
		portletState.setNotifyCorporateFirmSms(notifyCorporateFirmSms);
		Settings notifyCorporateIndividualEmail = portletState.getCompanyManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_EMAIL);
		portletState.setNotifyCorporateIndividualEmail(notifyCorporateIndividualEmail);
		Settings notifyCorporateIndividualSMS = portletState.getCompanyManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_SMS);
		portletState.setNotifyCorporateIndividualSMS(notifyCorporateIndividualSMS);
		Settings etaxPaymentNotifyEmail = portletState.getCompanyManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_EMAIL);
		portletState.setEtaxPaymentNotifyEmail(etaxPaymentNotifyEmail);
		Settings etaxPaymentNotifySMS = portletState.getCompanyManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_SMS);
		portletState.setEtaxPaymentNotifySMS(etaxPaymentNotifySMS);
		Settings systemUrl = portletState.getCompanyManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_URL);
		portletState.setSystemUrl(systemUrl);
		
		Settings twostep = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_TWO_STEP_LOGIN);
		portletState.setTwoStepLogin(twostep);
		Settings appr = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_APPROVAL_PROCESS);
		portletState.setApprovalProcess(appr);
		Settings usname = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_USERNAME);
		portletState.setSendingEmailUsername(usname);
		Settings semail = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_EMAIL);
		portletState.setSendingEmail(semail);
		Settings pswd = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_PASSWORD);
		portletState.setSendingEmailPassword(pswd);
		Settings port = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_PORT);
		portletState.setSendingEmailPort(port);
		Settings spc = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_COUNTRY);
		portletState.setPlatformCountry(spc);
		Settings spb = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATORM_BANK);
		portletState.setPlatformBank(spb);
		Settings zacc = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_NUMBER);
		portletState.setSettingsZRAAccount(zacc);
		Settings zacs = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_SORT_CODE);
		portletState.setSettingsZRAAccountSortCode(zacs);
		
		
		
		Settings app = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_APPLICATION_NAME);
		portletState.setApplicationName(app);
		Settings mapp = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_MOBILE_APPLICATION_NAME);
		portletState.setMobileApplicationName(mapp);
		Settings proxH = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_HOST);
		portletState.setProxyHost(proxH);
		Settings proxP = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_PORT);
		portletState.setProxyPort(proxP);
		Settings proxU = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_USERNAME);
		portletState.setProxyUsername(proxU);
		Settings proxPwd = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_PASSWORD);
		portletState.setProxyPassword(proxPwd);
		Settings bank = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_BANK_NAME);
		portletState. setBankName(bank);
		Settings currency = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_CURRENCY);
		portletState.setCurrency(currency);
		Settings bpwsu = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_BANK_PAYMENT_WS_URL);
		portletState.setBankPaymentWebServiceUrl(bpwsu);
		Settings tbws = portletState.getCompanyManagementPortletUtil().
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




	public Settings getSystemUrl() {
		return systemUrl;
	}



	public void setSystemUrl(Settings systemUrl) {
		this.systemUrl = systemUrl;
	}









	public String getSelectedUserRoleId() {
		return selectedUserRoleId;
	}




	public void setSelectedUserRoleId(String selectedUserRoleId) {
		this.selectedUserRoleId = selectedUserRoleId;
	}




	public void setSelectedCompanyId(Long companyIdL) {
		// TODO Auto-generated method stub
		this.selectedCompanyId = companyIdL;
	}
	
	public Long getSelectedCompanyId() {
		// TODO Auto-generated method stub
		return this.selectedCompanyId;
	}




	public Collection<RoleType> getRoleTypeListing() {
		return roleTypeListing;
	}




	public void setRoleTypeListing(Collection<RoleType> roleTypeListing) {
		this.roleTypeListing = roleTypeListing;
	}




	public String getSelectedCompanyType() {
		return selectedCompanyType;
	}




	public void setSelectedCompanyType(String selectedCompanyType) {
		this.selectedCompanyType = selectedCompanyType;
	}




	public String getTpin() {
		return tpin;
	}




	public void setTpin(String tpin) {
		this.tpin = tpin;
	}




	public String getSelectedCompanyClass() {
		return selectedCompanyClass;
	}




	public void setSelectedCompanyClass(String selectedCompanyClass) {
		this.selectedCompanyClass = selectedCompanyClass;
	}




	public String getSelectedCreateMandatePanels() {
		return selectedCreateMandatePanels;
	}




	public void setSelectedCreateMandatePanels(
			String selectedCreateMandatePanels) {
		this.selectedCreateMandatePanels = selectedCreateMandatePanels;
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




	public String getMandatePanelsOn() {
		return mandatePanelsOn;
	}




	public void setMandatePanelsOn(String mandatePanelsOn) {
		this.mandatePanelsOn = mandatePanelsOn;
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




	public Settings getSettingsZRAAccountSortCode() {
		return settingsZRAAccountSortCode;
	}




	public void setSettingsZRAAccountSortCode(Settings settingsZRAAccountSortCode) {
		this.settingsZRAAccountSortCode = settingsZRAAccountSortCode;
	}




	public Settings getSettingsZRAAccount() {
		return settingsZRAAccount;
	}




	public void setSettingsZRAAccount(Settings settingsZRAAccount) {
		this.settingsZRAAccount = settingsZRAAccount;
	}




	public ComminsApplicationState getCas() {
		return cas;
	}




	public void setCas(ComminsApplicationState cas) {
		this.cas = cas;
	}

	
}
