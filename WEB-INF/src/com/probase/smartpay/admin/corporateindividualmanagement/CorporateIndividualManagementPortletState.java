package com.probase.smartpay.admin.corporateindividualmanagement;

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
import com.probase.smartpay.commins.Util.DETERMINE_ACCESS;
import com.sf.primepay.smartpay13.ServiceLocator;

public class CorporateIndividualManagementPortletState {

	private static Logger log = Logger.getLogger(CorporateIndividualManagementPortletState.class);
	private static CorporateIndividualManagementPortletUtil corporateindividualmanagementPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private ArrayList<RoleType> portalUserRoleType;
	
	

	private String successMessage;
	private String remoteIPAddress;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	private Collection<RoleType> roleTypeListing;
	
	private Collection<PortalUser> allCompanyPersonnel;
	private String selectedUserRoleId;
	private Collection<Company> allCompanyListing;
	private String selectedPortalUserId;
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
	
	
	
	/****user signup****/	
	private String corporateindividualfirstname;
	private String corporateindividuallastname;
	private String corporateindividualmiddlename;
	private String corporateindividualfirstemail;
	private String corporateindividualsecondemail;
	private String corporateindividualthirdemail;
	private String corporateindividualfirstmobile;
	private String corporateindividualsecondmobile;
	private String corporateindividualthirdmobile;
	private String corporateindividualAddressLine1;
	private String corporateindividualAddressLine2;
	private Collection<Company> companyListing;
	private Long selectedCompanyId;
	private CompanyCRUDRights companyCRUDRights;
	private PortalUserCRUDRights portalUserCRUDRights;
	private Company selectedCompany;
	private String countryCodeAlt1;
	private String countryCodeAlt2;
	private String countryCodeAlt3;
	
	/****enum section****/
    
	
	

	public static boolean loadSettings(CorporateIndividualManagementPortletState portletState) {
		// TODO Auto-generated method stub
		Settings primaryFeeSetting = portletState.getCorporateIndividualManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_PRIMARY_FEE_DESCRIPTION);
		portletState.setPrimaryFeeSetting(primaryFeeSetting);
		Settings notifyCorporateFirmEmail = portletState.getCorporateIndividualManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_EMAIL);
		portletState.setNotifyCorporateFirmEmail(notifyCorporateFirmEmail);
		Settings notifyCorporateFirmSms = portletState.getCorporateIndividualManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_SMS);
		portletState.setNotifyCorporateFirmSms(notifyCorporateFirmSms);
		Settings notifyCorporateIndividualEmail = portletState.getCorporateIndividualManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_EMAIL);
		portletState.setNotifyCorporateIndividualEmail(notifyCorporateIndividualEmail);
		Settings notifyCorporateIndividualSMS = portletState.getCorporateIndividualManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_SMS);
		portletState.setNotifyCorporateIndividualSMS(notifyCorporateIndividualSMS);
		Settings etaxPaymentNotifyEmail = portletState.getCorporateIndividualManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_EMAIL);
		portletState.setEtaxPaymentNotifyEmail(etaxPaymentNotifyEmail);
		Settings etaxPaymentNotifySMS = portletState.getCorporateIndividualManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_SMS);
		portletState.setEtaxPaymentNotifySMS(etaxPaymentNotifySMS);
		Settings systemUrl = portletState.getCorporateIndividualManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_URL);
		portletState.setSystemUrl(systemUrl);
		
		Settings twostep = portletState.getCorporateIndividualManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_TWO_STEP_LOGIN);
		portletState.setTwoStepLogin(twostep);
		Settings appr = portletState.getCorporateIndividualManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_APPROVAL_PROCESS);
		portletState.setApprovalProcess(appr);
		Settings usname = portletState.getCorporateIndividualManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_USERNAME);
		portletState.setSendingEmailUsername(usname);
		Settings semail = portletState.getCorporateIndividualManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_EMAIL);
		portletState.setSendingEmail(semail);
		Settings pswd = portletState.getCorporateIndividualManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_PASSWORD);
		portletState.setSendingEmailPassword(pswd);
		Settings port = portletState.getCorporateIndividualManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_PORT);
		portletState.setSendingEmailPort(port);
		Settings spc = portletState.getCorporateIndividualManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_COUNTRY);
		portletState.setPlatformCountry(spc);
		Settings spb = portletState.getCorporateIndividualManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATORM_BANK);
		portletState.setPlatformBank(spb);
		Settings zacc = portletState.getCorporateIndividualManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_NUMBER);
		portletState.setTaxCompanyAccount(zacc);
		Settings zacs = portletState.getCorporateIndividualManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_SORT_CODE);
		portletState.setTaxCompanySortCode(zacs);
		
		
		
		Settings app = portletState.getCorporateIndividualManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_APPLICATION_NAME);
		portletState.setApplicationName(app);
		Settings mapp = portletState.getCorporateIndividualManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_MOBILE_APPLICATION_NAME);
		portletState.setMobileApplicationName(mapp);
		Settings proxH = portletState.getCorporateIndividualManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_HOST);
		portletState.setProxyHost(proxH);
		Settings proxP = portletState.getCorporateIndividualManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_PORT);
		portletState.setProxyPort(proxP);
		Settings proxU = portletState.getCorporateIndividualManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_USERNAME);
		portletState.setProxyUsername(proxU);
		Settings proxPwd = portletState.getCorporateIndividualManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_PASSWORD);
		portletState.setProxyPassword(proxPwd);
		Settings bank = portletState.getCorporateIndividualManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_BANK_NAME);
		portletState. setBankName(bank);
		Settings currency = portletState.getCorporateIndividualManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_CURRENCY);
		portletState.setCurrency(currency);
		Settings bpwsu = portletState.getCorporateIndividualManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_BANK_PAYMENT_WS_URL);
		portletState.setBankPaymentWebServiceUrl(bpwsu);
		Settings tbws = portletState.getCorporateIndividualManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_TAX_BODY_WS_URL);
		portletState.setZraWebServiceUrl(tbws);
		
		return false;
	}
    
    public static enum VIEW_TABS{
    	CREATE_A_CORPORATE_INDIVIDUAL, VIEW_CORPORATE_INDIVIDUAL_LISTINGS
    }
    
    
    public static enum CORPORATE_INDIVIDUAL_VIEW
    {
    	CREATE_A_CORPORATE_INDIVIDUAL, VIEW_CORPORATE_INDIVIDUAL_LISTINGS, HANDLE_CORPORATE_INDIVIDUAL_LISTING_ACTION
    }
	public static enum COMPANY_CREATE_INDIVIDUAL_ACTIONS{
		CREATE_AN_INDIVIDUAL_STEP_ONE, CREATE_AN_INDIVIDUAL_STEP_THREE, 
		CREATE_AN_INDIVIDUAL__PRE_STEP_ONE, VIEW_USERS__PRE_STEP_ONE, LOGIN_STEP_TWO, 
		MODIFY_COMPANY, UPDATE_AN_INDIVIDUAL_STEP_THREE, UPDATE_AN_INDIVIDUAL_STEP_ONE
	}
	
	public static enum NAVIGATE{
		NAVIGATE_ACTIONS
	}
	
	
	/****core section starts here****/
	public static CorporateIndividualManagementPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		CorporateIndividualManagementPortletState portletState = null;
		Logger.getLogger(CorporateIndividualManagementPortletState.class).info("------getInstance");
		try {
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (CorporateIndividualManagementPortletState) session.getAttribute(CorporateIndividualManagementPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new CorporateIndividualManagementPortletState();
					CorporateIndividualManagementPortletUtil util = new CorporateIndividualManagementPortletUtil();
					portletState.setCorporateIndividualManagementPortletUtil(util);
					session.setAttribute(CorporateIndividualManagementPortletState.class.getName(), portletState);
					defaultInit(request, portletState);
					cas = ComminsApplicationState.getInstance(request, response);
	            }
			}
			
			
			//initSettings(portletState, swpService);
			// init settings
			return portletState;
		} catch (Exception e) {
			return null;
		}


	}

	private static void loadRoleTypes(CorporateIndividualManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setRoleTypeListing(portletState.getCorporateIndividualManagementPortletUtil().getAllRoleTypes());
	}
	
	private static void loadCompanyList(CorporateIndividualManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setAllCompanyListing(portletState.getCorporateIndividualManagementPortletUtil().getCompanyListing(CompanyStatusConstants.COMPANY_STATUS_ACTIVE));
	}
	

	public void reinitializeForCreateCorporateIndividual(
			CorporateIndividualManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setCorporateindividualfirstname(null);
		portletState.setCorporateindividuallastname(null);
		portletState.setCorporateindividualfirstemail(null);
		portletState.setCorporateindividualsecondemail(null);
		portletState.setCorporateindividualthirdemail(null);
		portletState.setCorporateindividualfirstmobile(null);
		portletState.setCorporateindividualsecondmobile(null);
		portletState.setCorporateindividualthirdmobile(null);
		portletState.setSelectedCompanyId(null);
		portletState.setCorporateindividualAddressLine1(null);
		portletState.setCorporateindividualAddressLine2(null);
		
	}
	
	
	
	
	private void setCorporateIndividualManagementPortletUtil(CorporateIndividualManagementPortletUtil util) {
		// TODO Auto-generated method stub
		this.corporateindividualmanagementPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, CorporateIndividualManagementPortletState portletState) {
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
				CorporateIndividualManagementPortletUtil util = CorporateIndividualManagementPortletUtil.getInstance();
				portletState.setCorporateIndividualManagementPortletUtil(util);
				
				ArrayList<RoleType> pur = portletState.getCorporateIndividualManagementPortletUtil().
						getRoleTypeByPortalUser(portletState.getPortalUser());	
				portletState.setPortalUserRoleType(pur);
				
				loadSettings(portletState);
				loadCompanyListing(portletState);
				loadAccessLevels(portletState);
				loadRoleTypes(portletState);
				loadCompanyList(portletState);
				
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
			CorporateIndividualManagementPortletState portletState) {
		// TODO Auto-generated method stub
		CompanyCRUDRights ccr = portletState.getCorporateIndividualManagementPortletUtil().getCompanyCRUDRightsByPortalUser(portletState.getPortalUser());
		PortalUserCRUDRights pucr = portletState.getCorporateIndividualManagementPortletUtil().getPortalUserCRUDRightsByPortalUser(portletState.getPortalUser());
		portletState.setCompanyCRUDRights(ccr);
		portletState.setPortalUserCRUDRights(pucr);
		ComminsApplicationState cappState = portletState.getCas();
		Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
		Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
		DETERMINE_ACCESS determinAccess = new Util().determineAccessForCompanyFxns(twoStep, approvalProcess, cappState, portletState.getCompanyCRUDRights());
		DETERMINE_ACCESS determinAccess2 = new Util().determineAccessForUserFxns(twoStep, approvalProcess, cappState, portletState.getPortalUserCRUDRights());
		
		if(determinAccess.equals(DETERMINE_ACCESS.GRANT_APPROVER_ACCESS) && determinAccess2.equals(DETERMINE_ACCESS.GRANT_APPROVER_ACCESS))
		{
			portletState.setCurrentTab(VIEW_TABS.VIEW_CORPORATE_INDIVIDUAL_LISTINGS);
		}else
		{
			portletState.setCurrentTab(VIEW_TABS.CREATE_A_CORPORATE_INDIVIDUAL);
		}
	}
	
	private static void loadCompanyListing(CorporateIndividualManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setCompanyListing(portletState.getCorporateIndividualManagementPortletUtil().getCompanyListing(CompanyStatusConstants.COMPANY_STATUS_ACTIVE));
	}
	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			CorporateIndividualManagementPortletState portletState) {

		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			CorporateIndividualManagementPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			CorporateIndividualManagementPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			CorporateIndividualManagementPortletState.log.debug("Error including error message", e);
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


	public CorporateIndividualManagementPortletUtil getCorporateIndividualManagementPortletUtil() {
		// TODO Auto-generated method stub
		return this.corporateindividualmanagementPortletUtil;
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
	
	

	public Long getSelectedCompanyId() {
		return selectedCompanyId;
	}


	public void setSelectedCompanyId(Long companyId) {
		this.selectedCompanyId = companyId;
	}




	public String getCorporateindividualfirstname() {
		return corporateindividualfirstname;
	}


	public void setCorporateindividualfirstname(
			String corporateindividualfirstname) {
		this.corporateindividualfirstname = corporateindividualfirstname;
	}


	public String getCorporateindividuallastname() {
		return corporateindividuallastname;
	}


	public void setCorporateindividuallastname(
			String corporateindividuallastname) {
		this.corporateindividuallastname = corporateindividuallastname;
	}


	public String getCorporateindividualfirstemail() {
		return corporateindividualfirstemail;
	}


	public void setCorporateindividualfirstemail(
			String corporateindividualfirstemail) {
		this.corporateindividualfirstemail = corporateindividualfirstemail;
	}


	public String getCorporateindividualsecondemail() {
		return corporateindividualsecondemail;
	}


	public void setCorporateindividualsecondemail(
			String corporateindividualsecondemail) {
		this.corporateindividualsecondemail = corporateindividualsecondemail;
	}


	public String getCorporateindividualthirdemail() {
		return corporateindividualthirdemail;
	}


	public void setCorporateindividualthirdemail(
			String corporateindividualthirdemail) {
		this.corporateindividualthirdemail = corporateindividualthirdemail;
	}


	public String getCorporateindividualfirstmobile() {
		return corporateindividualfirstmobile;
	}


	public void setCorporateindividualfirstmobile(
			String corporateindividualfirstmobile) {
		this.corporateindividualfirstmobile = corporateindividualfirstmobile;
	}


	public String getCorporateindividualsecondmobile() {
		return corporateindividualsecondmobile;
	}


	public void setCorporateindividualsecondmobile(
			String corporateindividualsecondmobile) {
		this.corporateindividualsecondmobile = corporateindividualsecondmobile;
	}


	public String getCorporateindividualthirdmobile() {
		return corporateindividualthirdmobile;
	}


	public void setCorporateindividualthirdmobile(
			String corporateindividualthirdmobile) {
		this.corporateindividualthirdmobile = corporateindividualthirdmobile;
	}




	public VIEW_TABS getCurrentTab() {
		return currentTab;
	}



	public void setCurrentTab(VIEW_TABS currentTab) {
		this.currentTab = currentTab;
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



	public String getCorporateindividualAddressLine1() {
		return corporateindividualAddressLine1;
	}



	public void setCorporateindividualAddressLine1(
			String corporateindividualAddressLine1) {
		this.corporateindividualAddressLine1 = corporateindividualAddressLine1;
	}



	public String getCorporateindividualAddressLine2() {
		return corporateindividualAddressLine2;
	}



	public void setCorporateindividualAddressLine2(
			String corporateindividualAddressLine2) {
		this.corporateindividualAddressLine2 = corporateindividualAddressLine2;
	}



	public String getCorporateindividualmiddlename() {
		return corporateindividualmiddlename;
	}



	public  void setCorporateindividualmiddlename(
			String corporateindividualmiddlename) {
		this.corporateindividualmiddlename = corporateindividualmiddlename;
	}



	public Settings getSystemUrl() {
		return systemUrl;
	}



	public void setSystemUrl(Settings systemUrl) {
		this.systemUrl = systemUrl;
	}




	public Collection<PortalUser> getAllCompanyPersonnel() {
		return allCompanyPersonnel;
	}



	public void setAllCompanyPersonnel(Collection<PortalUser> allCompanyPersonnel) {
		this.allCompanyPersonnel = allCompanyPersonnel;
	}



	public Collection<Company> getCompanyListing() {
		return companyListing;
	}



	public void setCompanyListing(Collection<Company> companyListing) {
		this.companyListing = companyListing;
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



	public Company getSelectedCompany() {
		return selectedCompany;
	}



	public void setSelectedCompany(Company selectedCompany) {
		this.selectedCompany = selectedCompany;
	}

	public Collection<RoleType> getRoleTypeListing() {
		return roleTypeListing;
	}

	public void setRoleTypeListing(Collection<RoleType> roleTypeListing) {
		this.roleTypeListing = roleTypeListing;
	}

	public String getSelectedUserRoleId() {
		return selectedUserRoleId;
	}

	public void setSelectedUserRoleId(String selectedUserRoleId) {
		this.selectedUserRoleId = selectedUserRoleId;
	}

	public Collection<Company> getAllCompanyListing() {
		return allCompanyListing;
	}

	public void setAllCompanyListing(Collection<Company> allCompanyListing) {
		this.allCompanyListing = allCompanyListing;
	}

	public String getSelectedPortalUserId() {
		return selectedPortalUserId;
	}

	public void setSelectedPortalUserId(String selectedPortalUserId) {
		this.selectedPortalUserId = selectedPortalUserId;
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

	public String getCountryCodeAlt1() {
		return countryCodeAlt1;
	}

	public void setCountryCodeAlt1(String countryCodeAlt1) {
		this.countryCodeAlt1 = countryCodeAlt1;
	}

	public String getCountryCodeAlt2() {
		return countryCodeAlt2;
	}

	public void setCountryCodeAlt2(String countryCodeAlt2) {
		this.countryCodeAlt2 = countryCodeAlt2;
	}

	public String getCountryCodeAlt3() {
		return countryCodeAlt3;
	}

	public void setCountryCodeAlt3(String countryCodeAlt3) {
		this.countryCodeAlt3 = countryCodeAlt3;
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
		CorporateIndividualManagementPortletState.cas = cas;
	}

	

	
}
