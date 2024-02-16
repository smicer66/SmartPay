package com.probase.smartpay.admin.bankbranchmanagement;

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

public class BankBranchManagementPortletState {

	private static Logger log = Logger.getLogger(BankBranchManagementPortletState.class);
	private static BankBranchManagementPortletUtil bankBranchPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private String remoteIPAddress;
	private ArrayList<RoleType> portalUserRoleType;
	

	private String successMessage;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	
	private Collection<BankBranches> allBankBranchListing;
	private static ComminsApplicationState cas = null; 
	
	
	/*****************NAVIGATION*****************/
	private VIEW_TABS currentTab;
	
	
	
	/****bank branch creator****/
	private String bankBranchName;
	private String bankBranchCode;
	private String selectedBankBranchId;
	
    
    public static enum VIEW_TABS{
    	CREATE_A_BANK_BRANCH, VIEW_BANK_BRANCHES
    }
    
    public static enum BANK_BRANCHES_VIEW
    {
    	CREATE_A_BANK_BRANCH, VIEW_BANK_BRANCHES
    }
    
    
	public static enum NAVIGATE{
		NAVIGATE_ACTIONS
	}
	
	
	public static enum BANK_BRANCH_ACTION{
		CREATE_A_BANK_BRANCH, BANK_BRANCH_LISTING_ACTION, 
		EDIT_A_BANK_BRANCH
	}
	
	
	
	/****core section starts here****/
	public static BankBranchManagementPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		BankBranchManagementPortletState portletState = null;
		Logger.getLogger(BankBranchManagementPortletState.class).info("------getInstance");
		try {
			
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (BankBranchManagementPortletState) session.getAttribute(BankBranchManagementPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new BankBranchManagementPortletState();
					BankBranchManagementPortletUtil util = new BankBranchManagementPortletUtil();
					portletState.setBankBranchPortletUtil(util);
					session.setAttribute(BankBranchManagementPortletState.class.getName(), portletState);
					defaultInit(request, portletState);
					portletState.setCurrentTab(VIEW_TABS.CREATE_A_BANK_BRANCH);
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
	
	public void reinitializeForCreateBankBranch(BankBranchManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setBankBranchCode(null);
		portletState.setBankBranchName(null);
	}
	
	
	private void setBankBranchPortletUtil(BankBranchManagementPortletUtil util) {
		// TODO Auto-generated method stub
		this.bankBranchPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, BankBranchManagementPortletState portletState) {
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
				BankBranchManagementPortletUtil util = BankBranchManagementPortletUtil.getInstance();
				portletState.setBankBranchPortletUtil(util);
				
				ArrayList<RoleType> pur = portletState.getBankBranchPortletUtil().
						getRoleTypeByPortalUser(portletState.getPortalUser());	
				portletState.setPortalUserRoleType(pur);
				
				loadBankBranches(portletState);
				
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


	private static void loadBankBranches(BankBranchManagementPortletState portletState) {
		// TODO Auto-generated method stub
		Collection<BankBranches> bankBranchListing = portletState.getBankBranchPortletUtil().getAllBankBranchListing();
		portletState.setAllBankBranchListing(bankBranchListing);
	}


	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			BankBranchManagementPortletState portletState) {

		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			BankBranchManagementPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			BankBranchManagementPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			BankBranchManagementPortletState.log.debug("Error including error message", e);
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


	public BankBranchManagementPortletUtil getBankBranchPortletUtil() {
		// TODO Auto-generated method stub
		return this.bankBranchPortletUtil;
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
	

	public ArrayList<String> getErrorList() {
		return errorList;
	}


	public void setErrorList(ArrayList<String> errorList) {
		this.errorList = errorList;
	}
	
	
	

	public String getBankBranchName() {
		return bankBranchName;
	}


	public void setBankBranchName(String bankBranchName) {
		this.bankBranchName = bankBranchName;
	}


	public String getBankBranchCode() {
		return bankBranchCode;
	}


	public void setBankBranchCode(String bankBranchCode) {
		this.bankBranchCode = bankBranchCode;
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

	public String getSelectedBankBranchId() {
		return selectedBankBranchId;
	}

	public void setSelectedBankBranchId(String id) {
		this.selectedBankBranchId = id;
	}



	
}
