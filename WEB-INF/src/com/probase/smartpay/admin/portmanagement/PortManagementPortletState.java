package com.probase.smartpay.admin.portmanagement;

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
import smartpay.entity.Ports;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.enumerations.CompanyStatusConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.service.SwpService;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.probase.smartpay.commins.Util;
import com.sf.primepay.smartpay13.ServiceLocator;

public class PortManagementPortletState {

	private static Logger log = Logger.getLogger(PortManagementPortletState.class);
	private static PortManagementPortletUtil feeDescriptionPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private ArrayList<RoleType> portalUserRoleType;
	

	private String successMessage;
	private String remoteIPAddress;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	
	private Collection<Ports> allPortsListing;
	private String selectedPortId;
	
	
	
	
	
	/*****************NAVIGATION*****************/
	private VIEW_TABS currentTab;
	
	
	
	/****Ports***/
	private String portCode;
    private String fullName;
    
   
	/****enum section****/
    
    
    public static enum VIEW_TABS{
    	CREATE_A_NEW_PORT, VIEW_PORT_LISTINGS
    	
    }
    
    public static enum PORTS_VIEW
    {
    	CREATE_A_NEW_PORT, VIEW_PORT_LISTINGS
    }
    
	
	public static enum NAVIGATE{
		NAVIGATE_ACTIONS
	}
	
	
	public static enum PORTS_ACTION{
		CREATE_A_NEW_PORT_ACTION, UPDATE_NEW_PORT_ACTION, 
		HANDLE_PORT_LISTING_ACTION 
	}
	
	/****core section starts here****/
	public static PortManagementPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		PortManagementPortletState portletState = null;
		Logger.getLogger(PortManagementPortletState.class).info("------getInstance");
		try {
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (PortManagementPortletState) session.getAttribute(PortManagementPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new PortManagementPortletState();
					PortManagementPortletUtil util = new PortManagementPortletUtil();
					portletState.setPortManagementPortletUtil(util);
					session.setAttribute(PortManagementPortletState.class.getName(), portletState);
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
	
	
	private void setPortManagementPortletUtil(PortManagementPortletUtil util) {
		// TODO Auto-generated method stub
		this.feeDescriptionPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, PortManagementPortletState portletState) {
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
				PortManagementPortletUtil util = PortManagementPortletUtil.getInstance();
				portletState.setPortManagementPortletUtil(util);
				
				ArrayList<RoleType> pur = portletState.getPortManagementPortletUtil().
						getRoleTypeByPortalUser(portletState.getPortalUser());	
				portletState.setPortalUserRoleType(pur);
				
				portletState.setAllPortsListing(portletState.getPortManagementPortletUtil().getAllPortsListing(true));
				portletState.setCurrentTab(VIEW_TABS.CREATE_A_NEW_PORT);
				
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
			PortManagementPortletState portletState) {

		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			PortManagementPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			PortManagementPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			PortManagementPortletState.log.debug("Error including error message", e);
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


	public PortManagementPortletUtil getPortManagementPortletUtil() {
		// TODO Auto-generated method stub
		return this.feeDescriptionPortletUtil;
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
	
	








	public void reinitializeForPorts(PortManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setPortCode(null);
		portletState.setFullName(null);
	}



	public VIEW_TABS getCurrentTab() {
		return currentTab;
	}



	public void setCurrentTab(VIEW_TABS currentTab) {
		this.currentTab = currentTab;
	}




	public String getPortCode() {
		return portCode;
	}


	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}


	public String getFullName() {
		return fullName;
	}


	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Collection<Ports> getAllPortsListing() {
		return this.allPortsListing;
	}


	public void setAllPortsListing(Collection<Ports> allPortsListing) {
		this.allPortsListing = allPortsListing;
	}


	public String getSelectedPortId() {
		// TODO Auto-generated method stub
		return this.selectedPortId;
	}


	public void setSelectedPortId(String selectedPortId) {
		this.selectedPortId = selectedPortId;
	}


	
}
