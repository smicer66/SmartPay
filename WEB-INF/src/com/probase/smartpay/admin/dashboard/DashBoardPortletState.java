package com.probase.smartpay.admin.dashboard;

import java.util.ArrayList;
import java.util.Collection;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;

import org.apache.log4j.Logger;

import smartpay.entity.AuthorizePanel;
import smartpay.entity.BankBranches;
import smartpay.entity.Company;
import smartpay.entity.FeeDescription;
import smartpay.entity.FinancialAmountRestriction;
import smartpay.entity.PortalUser;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.enumerations.CompanyStatusConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.service.SwpService;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.probase.smartpay.admin.dashboard.DashBoardPortletState;
import com.probase.smartpay.admin.dashboard.DashBoardPortletUtil;
import com.probase.smartpay.commins.Util;
import com.sf.primepay.smartpay13.ServiceLocator;

public class DashBoardPortletState {
	private static Logger log = Logger.getLogger(DashBoardPortletState.class);
	private static DashBoardPortletUtil DashBoardPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private ArrayList<RoleType> portalUserRoleType;
	

	private String successMessage;
	private String remoteIPAddress;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	
	
	/*****************NAVIGATION*****************/
	
	
	
	/****enum section****/
    
   
    
    
	public static enum BANK_ADMIN_ACTION{
		SETUP_FEE_CHARGES, SETUP_BANK_BRANCHES, CREATE_COMPANY, 
		CREATE_COMPANY_STAFF, CREATE_MANDATE_PANELS, MAP_PORTAL_USERS_TO_PANELS,
		CREATE_RESTRICTIONS_TO_FINANCE, SETUP_PORTS
	}
	
	public static enum BANK_SUPER_ADMIN_ACTION{
		CREATE_BANK_STAFF, MANAGE_SETTINGS
	}
	
	public static enum CORPORATE_COMPANY_ADMIN_ACTION{
		CREATE_COMPANY_STAFF, CREATE_MANDATE_PANELS, MAP_PORTAL_USERS_TO_PANELS,
		CREATE_RESTRICTIONS_TO_FINANCE
	}
	
	public static enum CORPORATE_COMPANY_NON_ADMIN_ACTION{
		CREATE_A_COMPANY_STEP_ONE, CREATE_A_COMPANY_STEP_TWO, 
		EDIT_A_COMPANY_STEP_ONE, EDIT_A_COMPANY_STEP_TWO
	}
	
	public static enum RETAIL_COMPANY_STAFF_ACTION{
		CREATE_A_COMPANY_STEP_ONE, CREATE_A_COMPANY_STEP_TWO, 
		EDIT_A_COMPANY_STEP_ONE, EDIT_A_COMPANY_STEP_TWO
	}
	
	public static enum BANK_TELLER_ACTION{
		CREATE_A_COMPANY_STEP_ONE, CREATE_A_COMPANY_STEP_TWO, 
		EDIT_A_COMPANY_STEP_ONE, EDIT_A_COMPANY_STEP_TWO
	}
	
	public static enum BANK_HEAD_OF_OPERATIONS{
		CREATE_A_COMPANY_STEP_ONE, CREATE_A_COMPANY_STEP_TWO, 
		EDIT_A_COMPANY_STEP_ONE, EDIT_A_COMPANY_STEP_TWO
	}
	
	public static enum BANK_HQ_OPERATIONS_ASSISTANT{
		CREATE_A_COMPANY_STEP_ONE, CREATE_A_COMPANY_STEP_TWO, 
		EDIT_A_COMPANY_STEP_ONE, EDIT_A_COMPANY_STEP_TWO
	}
	
	
	
	
	/****core section starts here****/
	public static DashBoardPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		DashBoardPortletState portletState = null;
		Logger.getLogger(DashBoardPortletState.class).info("------getInstance");
		try {
			
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (DashBoardPortletState) session.getAttribute(DashBoardPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new DashBoardPortletState();
					DashBoardPortletUtil util = new DashBoardPortletUtil();
					portletState.setDashBoardPortletUtil(util);
					session.setAttribute(DashBoardPortletState.class.getName(), portletState);
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
	
	
	
	
	
	private void setDashBoardPortletUtil(DashBoardPortletUtil util) {
		// TODO Auto-generated method stub
		this.DashBoardPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, DashBoardPortletState portletState) {
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
				DashBoardPortletUtil util = DashBoardPortletUtil.getInstance();
				portletState.setDashBoardPortletUtil(util);
				
				ArrayList<RoleType> pur = portletState.getDashBoardPortletUtil().
						getRoleTypeByPortalUser(portletState.getPortalUser());	
				portletState.setPortalUserRoleType(pur);
				
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
			DashBoardPortletState portletState) {

		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			DashBoardPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			DashBoardPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			DashBoardPortletState.log.debug("Error including error message", e);
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


	public DashBoardPortletUtil getDashBoardPortletUtil() {
		// TODO Auto-generated method stub
		return this.DashBoardPortletUtil;
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
}
