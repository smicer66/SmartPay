package com.probase.smartpay.admin.usermanagementsystemadmin;

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
import com.probase.smartpay.admin.companymanagement.CompanyManagementPortletState;
import com.probase.smartpay.commins.ComminsApplicationState;
import com.probase.smartpay.commins.Util;
import com.sf.primepay.smartpay13.ServiceLocator;

public class UserManagementSystemAdminPortletState {

	private static Logger log = Logger.getLogger(UserManagementSystemAdminPortletState.class);
	private static UserManagementSystemAdminPortletUtil userManagementSystemAdminPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private ArrayList<RoleType> portalUserRoleType;
	private static ComminsApplicationState cas;
	

	private String successMessage;
	private String remoteIPAddress;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	
	private Collection<PortalUser> allCompanyPersonnel;
	private Collection<PortalUser> portalUserListing;
	
	

	
	/****settings*****/
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
	
	
	
	/****user signup****/	
	private String portaluserfirstname;
	private String portaluserlastname;
	private String portalusermiddlename;
	private String portaluserfirstemail;
	private String portalusersecondemail;
	private String portaluserthirdemail;
	private String portaluserfirstmobile;
	private String portalusersecondmobile;
	private String portaluserthirdmobile;
	private String portaluserAddressLine1;
	private String portaluserAddressLine2;
	private Collection<Company> companyListing;
	private String selectedUserRoleId;
	private Collection<RoleType> roleTypeListing;
	private String selectedPortalUserId;
	private Company company;
	private String userCRUD;		//o:Initiator 1:Approver
	private String companyCRUD;		//o:Initiator 1:Approver
	private String countryCodeAlt1;
	private String countryCodeAlt2;
	private String countryCodeAlt3;
	
	private PortalUserCRUDRights portalUserCRUDRights;
	
	
	
	
	/****enum section****/
    
    
    public static enum VIEW_TABS{
    	CREATE_A_PORTAL_USER, VIEW_PORTAL_USER_LISTINGS
    }
    
    
    public static enum USER_MANAGEMENT_SYSTEM_ADMIN_VIEW
    {
    	CREATE_A_PORTAL_USER, VIEW_PORTAL_USERS
    }
	public static enum USER_MANAGEMENT_SYSTEM_ADMIN_ACTIONS{
		CREATE_A_PORTAL_USER_STEP_ONE, CREATE_A_PORTAL_USER_STEP_THREE, LIST_PORTAL_USERS, 
		UPDATE_A_PORTAL_USER_STEP_ONE, UPDATE_A_PORTAL_USER_STEP_THREE, VIEW_A_PORTAL_USER_ACTION, 
		LOGIN_STEP_TWO
	}
	
	public static enum NAVIGATE{
		NAVIGATE_ACTIONS
	}
	
	
	/****core section starts here****/
	public static UserManagementSystemAdminPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		UserManagementSystemAdminPortletState portletState = null;
		Logger.getLogger(UserManagementSystemAdminPortletState.class).info("------getInstance");
		try {
			
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (UserManagementSystemAdminPortletState) session.getAttribute(UserManagementSystemAdminPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new UserManagementSystemAdminPortletState();
					UserManagementSystemAdminPortletUtil util = new UserManagementSystemAdminPortletUtil();
					portletState.setUserManagementSystemAdminPortletUtil(util);
					session.setAttribute(UserManagementSystemAdminPortletState.class.getName(), portletState);
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
	
	

	public void reinitializeForCreateCorporateIndividual(
			UserManagementSystemAdminPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setportaluserfirstname(null);
		portletState.setportaluserlastname(null);
		portletState.setportaluserfirstemail(null);
		portletState.setportalusersecondemail(null);
		portletState.setportaluserthirdemail(null);
		portletState.setportaluserfirstmobile(null);
		portletState.setportalusersecondmobile(null);
		portletState.setportaluserthirdmobile(null);
		portletState.setportaluserAddressLine1(null);
		portletState.setportaluserAddressLine2(null);
		
	}
	
	
	private static void loadAccessLevels(
			UserManagementSystemAdminPortletState portletState) {
		// TODO Auto-generated method stub
		PortalUserCRUDRights pucr = portletState.getUserManagementSystemAdminPortletUtil().getPortalUserCRUDRightsByPortalUser(portletState.getPortalUser());
		portletState.setPortalUserCRUDRights(pucr);
	}
	
	
	
	
	private void setUserManagementSystemAdminPortletUtil(UserManagementSystemAdminPortletUtil util) {
		// TODO Auto-generated method stub
		this.userManagementSystemAdminPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, UserManagementSystemAdminPortletState portletState) {
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
				UserManagementSystemAdminPortletUtil util = UserManagementSystemAdminPortletUtil.getInstance();
				portletState.setUserManagementSystemAdminPortletUtil(util);
				
				ArrayList<RoleType> pur = portletState.getUserManagementSystemAdminPortletUtil().
						getRoleTypeByPortalUser(portletState.getPortalUser());	
				portletState.setPortalUserRoleType(pur);
				portletState.setRoleTypeListing(portletState.getUserManagementSystemAdminPortletUtil().getAllRoleTypes());
				loadAccessLevels(portletState);
				loadSettings(portletState);
				
				if(portletState.getPortalUserCRUDRights()!=null && 
						portletState.getPortalUserCRUDRights().getCudInitiatorRights()!=null && 
							portletState.getPortalUserCRUDRights().getCudInitiatorRights().equals(Boolean.TRUE))
				{
					portletState.setCurrentTab(VIEW_TABS.CREATE_A_PORTAL_USER);
				}
				else
				{
					portletState.setCurrentTab(VIEW_TABS.VIEW_PORTAL_USER_LISTINGS);
					portletState.setPortalUserListing(portletState.getUserManagementSystemAdminPortletUtil().getAllPortalUsers(portletState));
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

	
	private static void loadCompanyListing(UserManagementSystemAdminPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setCompanyListing(portletState.getUserManagementSystemAdminPortletUtil().getCompanyListing(CompanyStatusConstants.COMPANY_STATUS_ACTIVE));
	}
	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			UserManagementSystemAdminPortletState portletState) {

		log.info("Eeeror Msg = " + errorMessage);
		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			UserManagementSystemAdminPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			UserManagementSystemAdminPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			UserManagementSystemAdminPortletState.log.debug("Error including error message", e);
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


	public UserManagementSystemAdminPortletUtil getUserManagementSystemAdminPortletUtil() {
		// TODO Auto-generated method stub
		return this.userManagementSystemAdminPortletUtil;
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
	



	public String getportaluserfirstname() {
		return portaluserfirstname;
	}


	public void setportaluserfirstname(
			String portaluserfirstname) {
		this.portaluserfirstname = portaluserfirstname;
	}


	public String getportaluserlastname() {
		return portaluserlastname;
	}


	public void setportaluserlastname(
			String portaluserlastname) {
		this.portaluserlastname = portaluserlastname;
	}


	public String getportaluserfirstemail() {
		return portaluserfirstemail;
	}


	public void setportaluserfirstemail(
			String portaluserfirstemail) {
		this.portaluserfirstemail = portaluserfirstemail;
	}


	public String getportalusersecondemail() {
		return portalusersecondemail;
	}


	public void setportalusersecondemail(
			String portalusersecondemail) {
		this.portalusersecondemail = portalusersecondemail;
	}


	public String getportaluserthirdemail() {
		return portaluserthirdemail;
	}


	public void setportaluserthirdemail(
			String portaluserthirdemail) {
		this.portaluserthirdemail = portaluserthirdemail;
	}


	public String getportaluserfirstmobile() {
		return portaluserfirstmobile;
	}


	public void setportaluserfirstmobile(
			String portaluserfirstmobile) {
		this.portaluserfirstmobile = portaluserfirstmobile;
	}


	public String getportalusersecondmobile() {
		return portalusersecondmobile;
	}


	public void setportalusersecondmobile(
			String portalusersecondmobile) {
		this.portalusersecondmobile = portalusersecondmobile;
	}


	public String getportaluserthirdmobile() {
		return portaluserthirdmobile;
	}


	public void setportaluserthirdmobile(
			String portaluserthirdmobile) {
		this.portaluserthirdmobile = portaluserthirdmobile;
	}




	public VIEW_TABS getCurrentTab() {
		return currentTab;
	}



	public void setCurrentTab(VIEW_TABS currentTab) {
		this.currentTab = currentTab;
	}




	public String getportaluserAddressLine1() {
		return portaluserAddressLine1;
	}



	public void setportaluserAddressLine1(
			String portaluserAddressLine1) {
		this.portaluserAddressLine1 = portaluserAddressLine1;
	}



	public String getportaluserAddressLine2() {
		return portaluserAddressLine2;
	}



	public void setportaluserAddressLine2(
			String portaluserAddressLine2) {
		this.portaluserAddressLine2 = portaluserAddressLine2;
	}



	public String getportalusermiddlename() {
		return portalusermiddlename;
	}



	public  void setportalusermiddlename(
			String portalusermiddlename) {
		this.portalusermiddlename = portalusermiddlename;
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



	public String getSelectedUserRoleId() {
		return selectedUserRoleId;
	}



	public void setSelectedUserRoleId(String selectedUserRoleId) {
		this.selectedUserRoleId = selectedUserRoleId;
	}

	
	public static boolean loadSettings(UserManagementSystemAdminPortletState portletState) {
		// TODO Auto-generated method stub
		Settings primaryFeeSetting = portletState.getUserManagementSystemAdminPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_PRIMARY_FEE_DESCRIPTION);
		portletState.setPrimaryFeeSetting(primaryFeeSetting);
		Settings notifyCorporateFirmEmail = portletState.getUserManagementSystemAdminPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_EMAIL);
		portletState.setNotifyCorporateFirmEmail(notifyCorporateFirmEmail);
		Settings notifyCorporateFirmSms = portletState.getUserManagementSystemAdminPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_SMS);
		portletState.setNotifyCorporateFirmSms(notifyCorporateFirmSms);
		Settings notifyCorporateIndividualEmail = portletState.getUserManagementSystemAdminPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_EMAIL);
		portletState.setNotifyCorporateIndividualEmail(notifyCorporateIndividualEmail);
		Settings notifyCorporateIndividualSMS = portletState.getUserManagementSystemAdminPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_SMS);
		portletState.setNotifyCorporateIndividualSMS(notifyCorporateIndividualSMS);
		Settings etaxPaymentNotifyEmail = portletState.getUserManagementSystemAdminPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_EMAIL);
		portletState.setEtaxPaymentNotifyEmail(etaxPaymentNotifyEmail);
		Settings etaxPaymentNotifySMS = portletState.getUserManagementSystemAdminPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_SMS);
		portletState.setEtaxPaymentNotifySMS(etaxPaymentNotifySMS);
		Settings systemUrl = portletState.getUserManagementSystemAdminPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_URL);
		portletState.setSystemUrl(systemUrl);
		
		Settings twostep = portletState.getUserManagementSystemAdminPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_TWO_STEP_LOGIN);
		portletState.setTwoStepLogin(twostep);
		Settings appr = portletState.getUserManagementSystemAdminPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_APPROVAL_PROCESS);
		portletState.setApprovalProcess(appr);
		Settings usname = portletState.getUserManagementSystemAdminPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_USERNAME);
		portletState.setSendingEmailUsername(usname);
		Settings semail = portletState.getUserManagementSystemAdminPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_EMAIL);
		portletState.setSendingEmail(semail);
		Settings pswd = portletState.getUserManagementSystemAdminPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_PASSWORD);
		portletState.setSendingEmailPassword(pswd);
		Settings port = portletState.getUserManagementSystemAdminPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_PORT);
		portletState.setSendingEmailPort(port);
		Settings spc = portletState.getUserManagementSystemAdminPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_COUNTRY);
		portletState.setPlatformCountry(spc);
		Settings spb = portletState.getUserManagementSystemAdminPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATORM_BANK);
		portletState.setPlatformBank(spb);
		Settings zacc = portletState.getUserManagementSystemAdminPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_NUMBER);
		portletState.setTaxCompanyAccount(zacc);
		Settings zacs = portletState.getUserManagementSystemAdminPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_SORT_CODE);
		portletState.setTaxCompanySortCode(zacs);
		
		
		
		Settings app = portletState.getUserManagementSystemAdminPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_APPLICATION_NAME);
		portletState.setApplicationName(app);
		Settings mapp = portletState.getUserManagementSystemAdminPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_MOBILE_APPLICATION_NAME);
		portletState.setMobileApplicationName(mapp);
		Settings proxH = portletState.getUserManagementSystemAdminPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_HOST);
		portletState.setProxyHost(proxH);
		Settings proxP = portletState.getUserManagementSystemAdminPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_PORT);
		portletState.setProxyPort(proxP);
		Settings proxU = portletState.getUserManagementSystemAdminPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_USERNAME);
		portletState.setProxyUsername(proxU);
		Settings proxPwd = portletState.getUserManagementSystemAdminPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_PASSWORD);
		portletState.setProxyPassword(proxPwd);
		Settings bank = portletState.getUserManagementSystemAdminPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_BANK_NAME);
		portletState. setBankName(bank);
		Settings currency = portletState.getUserManagementSystemAdminPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_CURRENCY);
		portletState.setCurrency(currency);
		Settings bpwsu = portletState.getUserManagementSystemAdminPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_BANK_PAYMENT_WS_URL);
		portletState.setBankPaymentWebServiceUrl(bpwsu);
		Settings zwsu = portletState.getUserManagementSystemAdminPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_TAX_BODY_WS_URL);
		portletState.setTaxCompanySortCode(zwsu);
		
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



	public Collection<RoleType> getRoleTypeListing() {
		return roleTypeListing;
	}



	public void setRoleTypeListing(Collection<RoleType> roleTypeListing) {
		this.roleTypeListing = roleTypeListing;
	}



	public Collection<PortalUser> getPortalUserListing() {
		return portalUserListing;
	}



	public void setPortalUserListing(Collection<PortalUser> portalUserListing) {
		this.portalUserListing = portalUserListing;
	}



	public String getSelectedPortalUserId() {
		return selectedPortalUserId;
	}



	public void setSelectedPortalUserId(String selectedPortalUserId) {
		this.selectedPortalUserId = selectedPortalUserId;
	}



	public void setSelectedCompany(Company company) {
		// TODO Auto-generated method stub
		this.company = company;
	}
	
	
	public Company getSelectedCompany() {
		// TODO Auto-generated method stub
		return this.company;
	}



	public String getUserCRUD() {
		return userCRUD;
	}



	public void setUserCRUD(String userCRUD) {
		this.userCRUD = userCRUD;
	}



	public String getCompanyCRUD() {
		return companyCRUD;
	}



	public void setCompanyCRUD(String companyCRUD) {
		this.companyCRUD = companyCRUD;
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



	public PortalUserCRUDRights getPortalUserCRUDRights() {
		return portalUserCRUDRights;
	}



	public void setPortalUserCRUDRights(PortalUserCRUDRights portalUserCRUDRights) {
		this.portalUserCRUDRights = portalUserCRUDRights;
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



	public static ComminsApplicationState getCas() {
		return cas;
	}



	public static void setCas(ComminsApplicationState cas) {
		UserManagementSystemAdminPortletState.cas = cas;
	}

	
}
