package com.probase.smartpay.tester;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;

import org.apache.log4j.Logger;

import smartpay.entity.PortalUser;
import smartpay.entity.Role;
import smartpay.service.SwpService;


import com.liferay.portal.kernel.servlet.SessionErrors;
import com.sf.primepay.smartpay13.ServiceLocator;


public class TesterPortletState {

	private static Logger log = Logger.getLogger(TesterPortletState.class);
	private String oneResponseMessage;
	private String successMessage;
	private String onepayErrorMessage;
	private String remoteIPAddress;
	private String errorMessage;
	private PortalUser portalUser;
	private String yourcompanyname;
	private String companyrcnumber;
	private String line1addressofcompany;
	private String line2addressofcompany;
	private String yourwebsitename;
	private String yourwebsiteurl;
	private String yourwebsiteipaddress;
	private String yourfirstname;
	private String yourlastname;
	private String youremailaddress;
	private String yourcontactphonenumber; 
	private byte[] certificateofincorporation;
	private byte[] selectyourgeneratedcsrforsigning;
	private boolean editModeActivated;
	private boolean errorModeActivated;
	private String yourpassword;
	private String yourconfirmpassword;
	private ArrayList<String> errorList = new ArrayList<String>();
	private Timestamp merchantDate;
	private static TesterPortletUtil merchantManagementPortletUtil;
	private String companyname;
	private String websiteurl;
	private String ipaddress;
	private boolean searchIndicator;
	private String getSearchCompanyName;
	private String getSearchMerchantUrl;
	private String getSearchMerchantIP;
	private int newQueryInboxCount = 0;
	private PortalUser merchantSignUpPortalUser;
	private View currentView;
	private ArrayList<Role> userRoles;
	private String remoteUser;
	private ArrayList<Role> portalUserRoleList;
	
	
	/*SETTLEMENT*/
	private Double generalSumCollectedForADay=0.00;
	
	
	
	/***financial variables start here**/
	private String accountname;
	private  String accountnumber;
	private String accountbank;
	
	private String accountsaccruingmin;
	private String accountsaccruingmax;
	private String accountsaddedmin;
	private String accountsaddedmax;
	private String merchantsaccount;
	private int getNewQueryInboxCount=0;
	
	
	/*Transactions section*/
	private String amountsaccruingmin;
	private String amountsaccruingmax;
	private String amountsmadebetweendatesmin;
	private String amountsmadebetweendatesmax;
	
	
	/*Service Codes section*/
	private String servicecodetoassign;
	private String accounttoassign;
	private String percentagetoassign;
	private boolean modeActivated;
	public com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
	private String currentRemoteUser;
	private static ServiceLocator serviceLocator = ServiceLocator.getInstance();
	private String servicecodevalueis;
	private String searchCollectingaccount;
	private String serviceCodeDescription;
	private String selectedServiceCode;
	private int currentPageCount = 0;
	
	
	/***Reports****/
	private String reportType;
	private ArrayList<String> statusList;
	private ArrayList<String> selectedReportBank;
	private String selectedMerchantCode;
	private String selectedStartDate;
	private String selectedEndDate;
	private String selectedMinimumAmount;
	private String selectedMaximumAmount;
	private String selectedMerchantAccount;
	private String timestampSet;
	
	public static TesterPortletUtil getMerchantManagementPortletUtil()
	{
		return TesterPortletState.merchantManagementPortletUtil;
	}
	
	public static void setMerchantManagementPortletUtil(TesterPortletUtil merchantManagementPortletUtil)
	{
		TesterPortletState.merchantManagementPortletUtil = merchantManagementPortletUtil;
	}
	
	public static enum MerchantSignUp{
		PROCEED_TO_STEP_TWO, PROCEED_TO_STEP_THREE, LOGIN_NOW
	}
	
	
	public static enum ApprovalView{
		APPROVE_ENTITY, APPROVE_ENTITY_INSTANCE
	}
	
	public static enum ReportView{
		GENERATE_REPORT, GO_BACK_HOME
	}
	public static enum MerchantDetails {
		PROCEED_TO_STEP_TWO, DISPLAY_DETAILS, DISPLAY_LISTING, DEACTIVATE_MERCHANT, ALL_MERCHANTS, ALL_NEW_MERCHANT_REQUESTS, ALL_NEW_MERCHANTS
	}
	
	public static enum MerchantManage {
		ACCOUNT_MANAGEMENT, FINANCE_MANAGEMENT, PAYMENT_LOGS, REVERSALS, REPORTS, SERVICE_CODES, 
		SETTINGS, APPROVAL, RECONCILIATION, CREATE_NEW_USER
	}
	
	
	public static enum Approval{
		APPROVE_ENTITY_INSTANCE
	}
	
	public static enum Reconciliation{
		DISPLAY_TO_RECONCILE
	}
	
	public static enum View{
		VIEW_HOME_INTERFACE, VIEW_FINANCIAL_ACCOUNTS_INTERFACE, VIEW_PAYMENTS_INTERFACE, 
		VIEW_REVERSALS_INTERFACE, VIEW_REPORTS_INTERFACE, VIEW_SERVICE_CODES_INTERFACE,
		VIEW_MERCHANT_LISTING, VIEW_APPROVALS, VIEW_RECONCILIATION, VIEW_SETTINGS
	}
	
	public static enum MerchantListing {
		SEARCH_MERCHANTS	
	}
	
	public static enum GoBackView {
		GO_BACK_TO_VIEW, GO_BACK_TO_STEP_ONE, GO_BACK_TO_STEP_TWO, GO_BACK_TO_STEP_THREE,
		GO_BACK_TO_STEP_FOUR, GO_BACK_TO_STEP_FIVE
	}
	
	public static enum SaveContinueView {
		SAVE_CONTNUE_TO_HOME, SAVE_CONTNUE_TO_STEP_ONE, SAVE_CONTNUE_TO_STEP_TWO, SAVE_CONTNUE_TO_STEP_THREE, 
		SAVE_CONTNUE_TO_STEP_FOUR, SAVE_CONTNUE_TO_STEP_FIVE, SAVE_CONTNUE_TO_PREVIEW
	}
	
	public static enum SaveExitView {
		SAVE_EXIT_FROM_STEP_ONE, SAVE_EXIT_FROM_STEP_TWO, SAVE_EXIT_FROM_STEP_THREE, SAVE_EXIT_FROM_STEP_FOUR,
		SAVE_EXIT_FROM_STEP_FIVE
	}
		
	public static enum FormActionView {
		RECEIVE_ACTION_IN_STEP_ONE, RECEIVE_ACTION_IN_STEP_TWO, RECEIVE_ACTION_IN_STEP_THREE,
		RECEIVE_ACTION_IN_STEP_FOUR, RECEIVE_ACTION_FROM_OTHER_DIRECTORSHIP, RECEIVE_ACTION_FROM_SHARE_DETAILS
	}
	
	
	
	public static TesterPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		TesterPortletState portletState = null;
		Logger.getLogger(TesterPortletState.class).info("------set default init");
		try {
			PortletSession session = request.getPortletSession();
			portletState = (TesterPortletState) session.getAttribute(TesterPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
			
			if (portletState == null) {
				portletState = new TesterPortletState();
				TesterPortletUtil util = new TesterPortletUtil();
				portletState.setMerchantManagementPortletUtil(util);
				session.setAttribute(TesterPortletState.class.getName(), portletState);
				
				// init roles
            }
			
			ServiceLocator serviceLocator = ServiceLocator.getInstance();
			SwpService swpService = serviceLocator.getSwpService();
			
			defaultInit(request, portletState);
			//initSettings(portletState, swpService);
			// init settings
			return portletState;
		} catch (Exception e) {
			return null;
		}


	}
	
	private static void defaultInit(PortletRequest request, TesterPortletState portletState) {
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
				TesterPortletUtil util = TesterPortletUtil.getInstance();
				portletState.setMerchantManagementPortletUtil(util);
				
				ArrayList<Role> pur = portletState.getMerchantManagementPortletUtil().
						getRolesByPortalUser(portletState.getPortalUser());	
				portletState.setUserRoles(pur);
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
	public  void setUserRoles(ArrayList<Role> pur) {
		// TODO Auto-generated method stub
		this.portalUserRoleList = pur;
	}
	
	public ArrayList<Role> getUserRoles() {
		// TODO Auto-generated method stub
		return this.portalUserRoleList;
	}

	private void setCurrentRemoteUserId(String remoteUser) {
		// TODO Auto-generated method stub
		this.remoteUser = remoteUser;
	}
	
	private String getCurrentRemoteUserId()
	{
		return this.remoteUser;
	}

	public static void initSettings(TesterPortletState portletState, SwpService swpService)
	{
		com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
//		Collection<Role> role = portletState.getMerchantManagementPortletUtil().getAllRoles();
//		
//			String[] roleArray = {BytePayAppConstant.PORTAL_USER_ROLE_FINANCIAL_ACCOUNT_APPROVING_OFFICER.getValue(), 
//					BytePayAppConstant.PORTAL_USER_ROLE_MERCHANT.getValue(),
//					BytePayAppConstant.PORTAL_USER_ROLE_MERCHANT_APPROVER.getValue(),
//					BytePayAppConstant.PORTAL_USER_ROLE_PAYMENT_REVERSAL.getValue(),
//					BytePayAppConstant.PORTAL_USER_ROLE_RECONCILIATION_OFFICER.getValue(),
//					BytePayAppConstant.PORTAL_USER_ROLE_SERVICE_CODE_APPROVER.getValue()
//					};
//			
//		boolean proceed = false;
//		for(int c = 0; c<roleArray.length; c++)
//		{
//			proceed = false;
//			if(role==null)
//			{
//				proceed = true;
//			}else if(role!=null && role.contains(roleArray[c])==false)
//			{
//				proceed = true;
//			}
//			
//			
//			if(proceed)
//			{
//				Role role_ = new Role();
//				role_.setDescription(roleArray[c]);
//				role_.setRole(roleArray[c]);
//				swpService.createNewRecord(role_);
//			}
//		}
//		
//		/**User Type**/
//		Collection<PortalUserType> userTypeList = portletState.getMerchantManagementPortletUtil().getAllPortalUserType();
//		
//		String[] userTypeArray= {BytePayAppConstant.PORTAL_USER_TYPE_ADMIN.getValue(), 
//				BytePayAppConstant.PORTAL_USER_TYPE_MERCHANT.getValue()
//				};
//		
//		proceed = false;
//		for(int c = 0; c<userTypeArray.length; c++)
//		{
//			proceed = false;
//			if(userTypeList==null)
//			{
//				proceed = true;
//			}else if(userTypeList!=null && userTypeList.contains(userTypeArray[c])==false)
//			{
//				proceed = true;
//			}
//			
//			
//			if(proceed)
//			{
//				PortalUserType put_ = new PortalUserType();
//				put_.setDescription(userTypeArray[c]);
//				put_.setName(userTypeArray[c]);
//				swpService.createNewRecord(put_);
//			}
//		}
//			
//			
//		/*bank list*/
//		Collection<Bank> bankList = portletState.getMerchantManagementPortletUtil().getAllBanks();
//		
//		String[] bankArray = {"First Bank", "Access Bank", "Citibank", "Diamond Bank", "Ecobank Nigeria", "Enterprise Bank", 
//				"Fidelity Bank Nigeria", "First Bank of Nigeria", "First City Monument Bank", "FSDH Merchant Bank", 
//				"Guaranty Trust Bank", "Heritage Bank Plc", "Keystone Bank Limited", "Mainstreet Bank Limited", 
//				"Rand Merchant Bank", "Savannah Bank", "Skye Bank", "Stanbic IBTC Bank", "Standard Chartered Bank", 
//				"Sterling Bank", "Union Bank of Nigeria", "United Bank for Africa", "Unity Bank Plc", 
//				"Wema Bank", "Zenith Bank"};
//		
//		proceed = false;
//		for(int c = 0; c<bankArray.length; c++)
//		{
//			proceed = false;
//			if(bankList==null)
//			{
//				proceed = true;
//			}else if(bankList!=null && bankList.contains(bankArray[c])==false)
//			{
//				proceed = true;
//			}
//			
//			
//			if(proceed)
//			{
//				Bank bank = new Bank();
//				bank.setBankCode(bankArray[c]);
//				bank.setBankName(bankArray[c]);
//				swpService.createNewRecord(bank);
//			}
//		}
			
		
	}


	public String getOneResponseMessage() {
		return this.oneResponseMessage;
	}
	
	public void setOneResponseMessage(String oneResponseMessage) {
		this.oneResponseMessage = oneResponseMessage;
	}
	
	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			TesterPortletState portletState) {

		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			TesterPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String errorMessage,
			TesterPortletState portletState) {

		portletState.setErrorMessage(errorMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			TesterPortletState.log.debug("Error including error message", e);
		}
	}
	
	public void setSuccessMessage(String successMessage)
	{
		this.successMessage=successMessage;
	}
	public String getOnepayErrorMessage() {
		return this.onepayErrorMessage;
	}
	
	public String getRemoteIPAddress() {
		return remoteIPAddress;
	}

	public void setRemoteIPAddress(String remoteIPAddress) {
		this.remoteIPAddress = remoteIPAddress;
	}

	
	public void setOnepayErrorMessage(String onepayErrorMessage) {
		this.onepayErrorMessage = onepayErrorMessage;
	}
	
	public String getErrorMessage()
	{
		return this.errorMessage;
	}
	
	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
		
	}
	
	
	
	
	public boolean isUserAdmin()
	{
		return false;
	}


	


	public String getYourcompanyname() {
		return yourcompanyname;
	}


	public void setYourcompanyname(String yourcompanyname) {
		this.yourcompanyname = yourcompanyname;
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


	public String getYourwebsitename() {
		return yourwebsitename;
	}


	public void setYourwebsitename(String yourwebsitename) {
		this.yourwebsitename = yourwebsitename;
	}


	public String getLine2addressofcompany() {
		return line2addressofcompany;
	}


	public void setLine2addressofcompany(String line2addressofcompany) {
		this.line2addressofcompany = line2addressofcompany;
	}


	public String getYourwebsiteipaddress() {
		return yourwebsiteipaddress;
	}


	public void setYourwebsiteipaddress(String yourwebsiteipaddress) {
		this.yourwebsiteipaddress = yourwebsiteipaddress;
	}


	public String getYourwebsiteurl() {
		return yourwebsiteurl;
	}


	public void setYourwebsiteurl(String yourwebsiteurl) {
		this.yourwebsiteurl = yourwebsiteurl;
	}


	public String getYourfirstname() {
		return yourfirstname;
	}


	public void setYourfirstname(String yourfirstname) {
		this.yourfirstname = yourfirstname;
	}


	public String getYouremailaddress() {
		return youremailaddress;
	}


	public void setYouremailaddress(String youremailaddress) {
		this.youremailaddress = youremailaddress;
	}


	public String getYourlastname() {
		return yourlastname;
	}


	public void setYourlastname(String yourlastname) {
		this.yourlastname = yourlastname;
	}


	public String getYourcontactphonenumber() {
		return yourcontactphonenumber;
	}


	public void setYourcontactphonenumber(String yourcontactphonenumber) {
		this.yourcontactphonenumber = yourcontactphonenumber;
	}


	public boolean isEditModeActivated() {
		return editModeActivated;
	}


	public void setEditModeActivated(boolean editModeActivated) {
		this.editModeActivated = editModeActivated;
	}


	public boolean isErrorModeActivated() {
		return errorModeActivated;
	}


	public void setErrorModeActivated(boolean errorModeActivated) {
		this.errorModeActivated = errorModeActivated;
	}


	public ArrayList<String> getErrorList() {
		return errorList;
	}
	
	public void addToErrorList(String string) {
		errorList.add(string);
	}


	public void setErrorList(ArrayList<String> errorList) {
		this.errorList = errorList;
	}


	public byte[] getCertificateofincorporation() {
		return certificateofincorporation;
	}


	public byte[] setCertificateofincorporation(byte[] certificateofincorporation) {
		return this.certificateofincorporation;
	}


	public byte[] getSelectyourgeneratedcsrforsigning() {
		return selectyourgeneratedcsrforsigning;
	}


	public byte[] setSelectyourgeneratedcsrforsigning(
			byte[] selectyourgeneratedcsrforsigning) {
		return this.selectyourgeneratedcsrforsigning;
	}

	
	public void setMerchantDateCreated(Timestamp merchantDate)
	{
		this.merchantDate = merchantDate;
	}
	
	public Timestamp getMerchantDateCreated()
	{
		return this.merchantDate;
	}


	

	public String getCompanyname() {
		return companyname;
	}

	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}

	public String getWebsiteurl() {
		return websiteurl;
	}

	public void setWebsiteurl(String websiteurl) {
		this.websiteurl = websiteurl;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public boolean isSearchIndicator() {
		return searchIndicator;
	}

	public void setSearchIndicator(boolean searchIndicator) {
		this.searchIndicator = searchIndicator;
	}

	public String getSearchCompanyName() {
		return getSearchCompanyName;
	}

	public void setSearchCompanyName(String getSearchCompanyName) {
		this.getSearchCompanyName = getSearchCompanyName;
	}

	public String getSearchMerchantUrl() {
		return getSearchMerchantUrl;
	}

	public void setSearchMerchantUrl(String getSearchMerchantUrl) {
		this.getSearchMerchantUrl = getSearchMerchantUrl;
	}

	public String getSearchMerchantIP() {
		return getSearchMerchantIP;
	}

	public void setSearchMerchantIP(String getSearchMerchantIP) {
		this.getSearchMerchantIP = getSearchMerchantIP;
	}

	public int getNewQueryInboxCount() {
		return newQueryInboxCount;
	}

	public void setNewQueryInboxCount(int newQueryInboxCount) {
		this.newQueryInboxCount = newQueryInboxCount;
	}

	public PortalUser getPortalUser() {
		return portalUser;
	}

	public void setPortalUser(PortalUser portalUser) {
		this.portalUser = portalUser;
	}

	public String getYourpassword() {
		return yourpassword;
	}

	public void setYourpassword(String yourpassword) {
		this.yourpassword = yourpassword;
	}

	public String getYourconfirmpassword() {
		return yourconfirmpassword;
	}

	public void setYourconfirmpassword(String yourconfirmpassword) {
		this.yourconfirmpassword = yourconfirmpassword;
	}

	
	public void setMerchantSignUpPortalUser(PortalUser portalUser)
	{
		this.merchantSignUpPortalUser = portalUser;
	}
	
	public PortalUser getMerchantSignUpPortalUser()
	{
		return this.merchantSignUpPortalUser;
	}

	public View getCurrentView() {
		return currentView;
	}

	public void setCurrentView(View view) {
		this.currentView = view;
	}

	
	
	
	/******finance operations start here***/
	public static enum FinancialAccountListing {
		DISPLAY_LISTING, PROCESS_ACTION_FINANCIAL_ACCOUNT, SEARCH_ACCOUNTS, SEARCH_SERVICE_CODES, DEACTIVATE_SERVICE_CODE, 
		ALL_SERVICE_CODES, ADD_SERVICE_CODE, ASSIGN_SERVICE_CODE, GENERATE_SERVICE_CODE, ADD_FINANCIAL_ACCOUNT, 
		ADVANCED_FINANCIAL_ACCOUNT, ALL_FINANCIAL_ACCOUNTS
	}
	
	
	
	
	public static enum TransactionListing{
		PROCESS_ACTION_TRANSACTION_LISTING
	}
	
	
	public static enum PaymentHistoryListing{
		PROCESS_ACTION_PAYMENT_HISTORY
	}
	
	public static enum ServiceCodeListing{
		PROCESS_ACTION_SERVICE_CODE_LISTING
	}
	
	
	
	
	public String getAccountname() {
		return accountname;
	}

	public void setAccountname(String accountname) {
		this.accountname = accountname;
	}
	
	public int getCurrentPageCount()
	{
		return this.currentPageCount;
	}
	
	public void setCurrentPageCount(int currentPageCount)
	{
		this.currentPageCount = currentPageCount;
	}


	public void setAccountbank(String accountbank) {
		this.accountbank = accountbank;
	}

	public String getAccountnumber() {
		return accountnumber;
	}

	public void setAccountnumber(String accountnumber) {
		this.accountnumber = accountnumber;
	}

	
	
	public String getAccountsaccruingmin() {
		return accountsaccruingmin;
	}

	public void setAccountsaccruingmin(String accountsaccruingmin) {
		this.accountsaccruingmin = accountsaccruingmin;
	}

	public String getAccountsaccruingmax() {
		return accountsaccruingmax;
	}

	public void setAccountsaccruingmax(String accountsaccruingmax) {
		this.accountsaccruingmax = accountsaccruingmax;
	}

	public String getAccountsaddedmin() {
		return accountsaddedmin;
	}

	public void setAccountsaddedmin(String accountsaddedmin) {
		this.accountsaddedmin = accountsaddedmin;
	}

	public String getAccountsaddedmax() {
		return accountsaddedmax;
	}

	public void setAccountsaddedmax(String accountsaddedmax) {
		this.accountsaddedmax = accountsaddedmax;
	}

	public String getMerchantsaccount() {
		return merchantsaccount;
	}

	public void setMerchantsaccount(String merchantsaccount) {
		this.merchantsaccount = merchantsaccount;
	}
	

	public String getAmountsaccruingmin() {
		return amountsaccruingmin;
	}

	public void setAmountsaccruingmin(String amountsaccruingmin) {
		this.amountsaccruingmin = amountsaccruingmin;
	}

	public String getAmountsaccruingmax() {
		return amountsaccruingmax;
	}

	public void setAmountsaccruingmax(String amountsaccruingmax) {
		this.amountsaccruingmax = amountsaccruingmax;
	}

	public String getAmountsmadebetweendatesmin() {
		return amountsmadebetweendatesmin;
	}

	public void setAmountsmadebetweendatesmin(String amountsmadebetweendatesmin) {
		this.amountsmadebetweendatesmin = amountsmadebetweendatesmin;
	}

	public String getAmountsmadebetweendatesmax() {
		return amountsmadebetweendatesmax;
	}

	public void setAmountsmadebetweendatesmax(String amountsmadebetweendatesmax) {
		this.amountsmadebetweendatesmax = amountsmadebetweendatesmax;
	}

	public String getServicecodetoassign() {
		return servicecodetoassign;
	}

	public void setServicecodetoassign(String servicecodetoassign) {
		this.servicecodetoassign = servicecodetoassign;
	}

	public String getAccounttoassign() {
		return accounttoassign;
	}

	public void setAccounttoassign(String accounttoassign) {
		this.accounttoassign = accounttoassign;
	}

	public String getPercentagetoassign() {
		return percentagetoassign;
	}

	public void setPercentagetoassign(String percentagetoassign) {
		this.percentagetoassign = percentagetoassign;
	}


	public String getServicecodevalueis() {
		return servicecodevalueis;
	}


	public void setServicecodevalueis(String servicecodevalueis) {
		this.servicecodevalueis = servicecodevalueis;
	}


	public String getSearchCollectingaccount() {
		return searchCollectingaccount;
	}


	public void setSearchCollectingaccount(String searchCollectingaccount) {
		this.searchCollectingaccount = searchCollectingaccount;
	}


	public void setServiceCodeDescription(String desc) {
		// TODO Auto-generated method stub
		this.serviceCodeDescription = desc;
	}
	
	public String getServiceCodeDescription(String desc) {
		// TODO Auto-generated method stub
		return this.serviceCodeDescription;
	}
	
	public String getSelectedServiceCode()
	{
		return this.selectedServiceCode;
	}
	
	public void setSelectedServiceCode(String serviceCode)
	{
		this.selectedServiceCode = serviceCode;
	}


	public void setSelectedReportStatus(ArrayList<String> statusList) {
		// TODO Auto-generated method stub
		this.statusList = statusList;
	}
	
	public ArrayList<String> getSelectedReportStatus() {
		// TODO Auto-generated method stub
		return this.statusList;
		
	}

	public void setSelectedReportBank(ArrayList<String> arr) {
		// TODO Auto-generated method stub
		this.selectedReportBank = arr;
	}
	
	public ArrayList<String> getSelectedReportBank() {
		// TODO Auto-generated method stub
		return this.selectedReportBank;
	}

	public void setSelectedMerchantCode(String merchantcode) {
		// TODO Auto-generated method stub
		this.selectedMerchantCode = merchantcode;
	}
	
	public String getSelectedMerchantCode() {
		// TODO Auto-generated method stub
		return this.selectedMerchantCode;
	}

	public String getSelectedStartDate() {
		return selectedStartDate;
	}

	public void setSelectedStartDate(String selectedStartDate) {
		this.selectedStartDate = selectedStartDate;
	}

	public String getSelectedEndDate() {
		return selectedEndDate;
	}

	public void setSelectedEndDate(String selectedEndDate) {
		this.selectedEndDate = selectedEndDate;
	}

	public String getSelectedMinimumAmount() {
		return selectedMinimumAmount;
	}

	public void setSelectedMinimumAmount(String selectedMinimumAmount) {
		this.selectedMinimumAmount = selectedMinimumAmount;
	}

	public String getSelectedMaximumAmount() {
		return selectedMaximumAmount;
	}

	public void setSelectedMaximumAmount(String selectedMaximumAmount) {
		this.selectedMaximumAmount = selectedMaximumAmount;
	}

	public void setSelectedMerchantFinancialAccount(String mfaccount) {
		// TODO Auto-generated method stub
		this.selectedMerchantAccount = mfaccount;
	}
	
	public String getSelectedMerchantFinancialAccount() {
		// TODO Auto-generated method stub
		return this.selectedMerchantAccount;
	}

	public Double getGeneralSumCollectedForADay() {
		return generalSumCollectedForADay;
	}

	public void setGeneralSumCollectedForADay(
			Double generalSumCollectedForADay) {
		this.generalSumCollectedForADay = generalSumCollectedForADay;
	}

	public String getTimestampSet() {
		// TODO Auto-generated method stub
		return this.timestampSet;
	}
	
	public void setTimestampSet(String timestampSet) {
		// TODO Auto-generated method stub
		this.timestampSet = timestampSet;
	}


	
}
