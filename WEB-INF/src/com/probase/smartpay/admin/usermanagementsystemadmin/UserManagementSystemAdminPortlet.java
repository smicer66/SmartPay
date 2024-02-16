package com.probase.smartpay.admin.usermanagementsystemadmin;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;

import smartpay.audittrail.AuditTrail;
import smartpay.entity.ApprovalFlowTransit;
import smartpay.entity.Company;
import smartpay.entity.CompanyCRUDRights;
import smartpay.entity.PortalUser;
import smartpay.entity.PortalUserCRUDRights;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.enumerations.ActionTypeConstants;
import smartpay.entity.enumerations.PortalUserStatusConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.exception.SwpException;
import smartpay.service.SwpService;

import com.liferay.portal.DuplicateUserScreenNameException;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.Authenticator;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortlet;
import com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortletState;
import com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortletUtil;
import com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortletState.USER_MANAGEMENT_SYSTEM_ADMIN_ACTIONS;
import com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortletState.USER_MANAGEMENT_SYSTEM_ADMIN_VIEW;
import com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortletState.VIEW_TABS;
import com.probase.smartpay.commins.ComminsApplicationState;
import com.probase.smartpay.commins.Mailer;
import com.probase.smartpay.commins.ProbaseConstants;
import com.probase.smartpay.commins.SendMail;
import com.probase.smartpay.commins.SendSms;
import com.probase.smartpay.commins.Util;
import com.probase.smartpay.commins.Util.DETERMINE_ACCESS;
import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Portlet implementation class UserManagementSystemAdminPortlet
 */
public class UserManagementSystemAdminPortlet extends MVCPortlet {
 
	private Logger log = Logger.getLogger(UserManagementSystemAdminPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
	UserManagementSystemAdminPortletUtil util = UserManagementSystemAdminPortletUtil.getInstance();
	ServiceContext serviceContext = new ServiceContext();
	
	
	@Override
	public void init(PortletConfig config) throws PortletException {
		// TODO Auto-generated method stub
		log.info("Administrative portlet init called...");		
		pContext = config.getPortletContext();
		super.init(config);
		pConfig = config;
	    this.swpService = this.serviceLocator.getSwpService();
	}
	
	@Override
	public void render(RenderRequest renderRequest,
			RenderResponse renderResponse) throws PortletException, IOException {
		log.info("Administrative render called...");	
		PortletSession ps = renderRequest.getPortletSession();
		UserManagementSystemAdminPortletState portletState = 
				UserManagementSystemAdminPortletState.getInstance(renderRequest, renderResponse);

		log.info(">>>next page = " + renderRequest.getParameter("jspPage"));
		super.render(renderRequest, renderResponse);
	}

	@Override
	public void serveResource(ResourceRequest resourceRequest,
			ResourceResponse resourceResponse) throws IOException,
			PortletException {

		String resourceID = resourceRequest.getResourceID();
		if (resourceID == null || resourceID.equals(""))
			return;
	}
	
	
	@Override
	public void processAction(ActionRequest aReq,
			ActionResponse aRes) throws IOException, PortletException {
		SessionMessages.add(aReq, pConfig.getPortletName()
				+ SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
		log.info("inside process Action");
		
		UserManagementSystemAdminPortletState portletState = UserManagementSystemAdminPortletState.getInstance(aReq, aRes);
		
		
		String action = aReq.getParameter("action");
		log.info("action == " + action);
		if (action == null) {
			log.info("----------------action value is null----------------");
			return;
		}
		else
		{
			log.info("----------------action value is " + action +"----------------");
		}
        if (portletState == null) {
			log.info("----------------portletState is null----------------");
			return;
		}
        /*************GENERAL NAVIGATION***************/
        
        
        /*************POST ACTIONS*********************/
        if(action.equalsIgnoreCase(USER_MANAGEMENT_SYSTEM_ADMIN_ACTIONS.LOGIN_STEP_TWO.name()))
        {
        	log.info("We are inside step two of login");
        	if(loginStepTwo(aReq, aRes, portletState.getPortalUser(), portletState.getPortalUserCRUDRights(), 
        			swpService, portletState)==false)
        		portletState.addError(aReq, "Invalid login credentials!", portletState);
        		
        }
        if(action.equalsIgnoreCase(USER_MANAGEMENT_SYSTEM_ADMIN_ACTIONS.CREATE_A_PORTAL_USER_STEP_ONE.name()))
        {
        	createAPortalUserStepOne(aReq, aRes, portletState);
        }if(action.equalsIgnoreCase(USER_MANAGEMENT_SYSTEM_ADMIN_ACTIONS.CREATE_A_PORTAL_USER_STEP_THREE.name()))
        {
        	createAPortalUserStepThree(aReq, aRes, portletState);
        }
        if(action.equalsIgnoreCase(USER_MANAGEMENT_SYSTEM_ADMIN_ACTIONS.UPDATE_A_PORTAL_USER_STEP_ONE.name()))
        {
        	String selectedPortalUserAction = aReq.getParameter("selectedPortalUserAction");
        	if(selectedPortalUserAction.equalsIgnoreCase("goBackUPEdit"))
        	{
        		portletState.setSelectedPortalUserId(null);
            	aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/userlisting.jsp");
        	}else if(selectedPortalUserAction.equalsIgnoreCase("proceedToNextUPEdit"))
        	{
        		updateAPortalUserStepOne(aReq, aRes, portletState);
        	}
        }if(action.equalsIgnoreCase(USER_MANAGEMENT_SYSTEM_ADMIN_ACTIONS.UPDATE_A_PORTAL_USER_STEP_THREE.name()))
        {
        	String selectedPortalUserAction = aReq.getParameter("selectedPortalUserAction");
        	if(selectedPortalUserAction.equalsIgnoreCase("goBackToEditUPPg1"))
        	{
            	aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/register/stepone_edit.jsp");
        	}else if(selectedPortalUserAction.equalsIgnoreCase("createUserAccount"))
        	{
            	updateAPortalUserStepThree(aReq, aRes, portletState);
        	}
        }
        if(action.equalsIgnoreCase(USER_MANAGEMENT_SYSTEM_ADMIN_ACTIONS.LIST_PORTAL_USERS.name()))
        {
        	boolean proceed = false;
        	
        	handleUserListingActions(aReq, aRes, portletState);
        }if(action.equalsIgnoreCase(USER_MANAGEMENT_SYSTEM_ADMIN_VIEW.CREATE_A_PORTAL_USER.name()))
        {
        	portletState.reinitializeForCreateCorporateIndividual(portletState);
        	aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/register/stepone.jsp");
        	portletState.setCurrentTab(VIEW_TABS.CREATE_A_PORTAL_USER);
        }if(action.equalsIgnoreCase(USER_MANAGEMENT_SYSTEM_ADMIN_VIEW.VIEW_PORTAL_USERS.name()))
        {
        	//set corpoate indivudla listings
        	portletState.setPortalUserListing(portletState.getUserManagementSystemAdminPortletUtil().getAllPortalUsers(portletState));
        	aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/userlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.VIEW_PORTAL_USER_LISTINGS);
        }
		if(action.equalsIgnoreCase(USER_MANAGEMENT_SYSTEM_ADMIN_ACTIONS.VIEW_A_PORTAL_USER_ACTION.name()))
        {
        	//set corpoate indivudla listings
			String selectedPortalUserAction = aReq.getParameter("selectedPortalUserAction");
			String selectedPortalUserId = aReq.getParameter("selectedPortalUserActionId");
			portletState.setSelectedPortalUserId(selectedPortalUserId);
			
			if(selectedPortalUserId!=null)
			{
				try{
					PortalUser pu = (PortalUser) portletState.getUserManagementSystemAdminPortletUtil().
							getEntityObjectById(PortalUser.class, Long.valueOf(selectedPortalUserId));
		        	if(selectedPortalUserAction.equalsIgnoreCase("proceedtogoback"))
		        	{
		    			portletState.setSelectedPortalUserId(null);
		            	aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/userlisting.jsp");
		        	}else if(selectedPortalUserAction.equalsIgnoreCase("proceedtoedit"))
		        	{
		            	aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/register/stepone_edit.jsp");
		            	handleGoToUpdatePortalUser(pu, portletState, aReq, aRes);
		        	}
				}catch(NumberFormatException e)
				{
					e.printStackTrace();
					portletState.addError(aReq, "Invalid User selected. Ensure you select a user before attempting to update the user's profile", portletState);
	            	aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/userlisting.jsp");
				}
			}else
			{
				portletState.addError(aReq, "Invalid User selected. Ensure you select a user before attempting to update the user's profile", portletState);
            	aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/userlisting.jsp");
			}
        }
        
	}

	
	private boolean loginStepTwo(ActionRequest aReq, ActionResponse aRes,
			PortalUser currentPortalUser, PortalUserCRUDRights portalUserCRUDRights, SwpService sService, 
			UserManagementSystemAdminPortletState portletState) {
		// TODO Auto-generated method stub
		String email2 = aReq.getParameter("usernameemail");
		log.info("email2 = "+ email2);
		String password = aReq.getParameter("password");
		log.info("password = " + password);
		
		ComminsApplicationState cappState = portletState.getCas();
		log.info("cappState  we just got the application state");
		
		
		
		boolean trueCheck = new Util().loginStepTwoForPortalUserManagement(currentPortalUser, portalUserCRUDRights, 
				cappState, email2, password, sService);
		return trueCheck;
	}

	private void handleUserListingActions(ActionRequest aReq,
			ActionResponse aRes,
			UserManagementSystemAdminPortletState portletState) {
		// TODO Auto-generated method stub
		String userAction = aReq.getParameter("selectedPortalUserAction");
		String userActionOnId = aReq.getParameter("selectedPortalUser");
		
		if(userAction!=null && userAction.length()>0)
		{
			try
			{
				Long userActionId = Long.valueOf(userActionOnId);
				portletState.setSelectedPortalUserId(userActionOnId);
				PortalUser pu = (PortalUser) portletState.getUserManagementSystemAdminPortletUtil().
						getEntityObjectById(PortalUser.class, userActionId);
				
				boolean proceed = true;
//				if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
//	        	{
//					proceed = false;
//	        		if(portletState.getPortalUserCRUDRights().getCudInitiatorRights()!=null && 
//	        				portletState.getPortalUserCRUDRights().getCudInitiatorRights().equals(Boolean.TRUE))
//	        			proceed = true;
//	        	}
				ComminsApplicationState cappState = portletState.getCas();
				Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
				Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
				DETERMINE_ACCESS determinAccess = new Util().determineAccessForUserFxns(twoStep, approvalProcess, cappState, portletState.getPortalUserCRUDRights());
				
				
				
				if(pu!=null)
				{
					if(userAction.equalsIgnoreCase("update"))
							if(proceed)
								handleGoToUpdatePortalUser(pu, portletState, aReq, aRes);
					if(userAction.equalsIgnoreCase("view"))
						handleViewPortalUser(pu, portletState, aReq, aRes);
					if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
					{
						if(userAction.equalsIgnoreCase("delete"))
							if(proceed)
								handleDeletePortalUserInitiate(pu, portletState, aReq, aRes);
						if(userAction.equalsIgnoreCase("block"))
							if(proceed)
								handleBlockPortalUserInitiate(pu, portletState, aReq, aRes);
						if(userAction.equalsIgnoreCase("unblock"))
							if(proceed)
								handleUnblockPortalUserInititate(pu, portletState, aReq, aRes);
					}else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
					{
						
						if(userAction.equalsIgnoreCase("delete"))
							if(proceed)
								handleDeletePortalUser(pu, portletState, aReq, aRes);
						if(userAction.equalsIgnoreCase("block"))
							if(proceed)
								handleBlockPortalUser(pu, portletState, aReq, aRes);
						if(userAction.equalsIgnoreCase("unblock"))
							if(proceed)
								handleUnblockPortalUser(pu, portletState, aReq, aRes);
					}else
					{
						
					}
					
					
				}else
				{
					portletState.addError(aReq, "Invalid action on non-selected user. Select a user before carrying out this action", portletState);
					aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/userlisting.jsp");
				}
					
			}catch(NumberFormatException e)
			{
				e.printStackTrace();
				portletState.addError(aReq, "Invalid action on non-selected user. Select a user before carrying out this action", portletState);
				aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/userlisting.jsp");
			}
		}else
		{
			portletState.addError(aReq, "Invalid action on non-selected user. Select a user before carrying out this action", portletState);
			aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/userlisting.jsp");
		}
	}

	
	
	
	
	private void handleUnblockPortalUserInititate(PortalUser pu,
			UserManagementSystemAdminPortletState portletState,
			ActionRequest aReq, ActionResponse aRes) {
		// TODO Auto-generated method stub
		
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		
		User lpUser;
		try {
			lpUser = UserLocalServiceUtil.getUserById(pu.getUserId());
			if(lpUser!=null)
			{
				
				ApprovalFlowTransit aft = new ApprovalFlowTransit();
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("firstName", pu.getFirstName());
				jsonObject.put("lastName", pu.getLastName());
				jsonObject.put("email", pu.getEmailAddress());
				jsonObject.put("alternativeEmail1", pu.getFirstAlternativeEmailAddress());
				jsonObject.put("alternativeEmail2", pu.getSecondAlternativeEmailAddress());
				jsonObject.put("mobile", pu.getMobileNumber());
				jsonObject.put("alternativeMobile1", pu.getFirstAlternativeMobileNumber());
				jsonObject.put("alternativeMobile2", pu.getSecondAlternativeMobileNumber());
				jsonObject.put("addressLine1", pu.getAddressLine1());
				jsonObject.put("addressLine2", pu.getAddressLine2());
				jsonObject.put("roleType", pu.getRoleType().getRoleTypeName().getValue());
				
				aft.setActionType(ActionTypeConstants.PORTAL_USER_ACTION_UNBLOCK);
				aft.setDateCreated(new Timestamp((new Date()).getTime()));
				aft.setEntityId(pu.getId());
				aft.setEntityName(PortalUser.class.getSimpleName());
				aft.setObjectData(jsonObject.toString());
				aft.setPortalUser(portletState.getPortalUser());
				swpService.createNewRecord(aft);
				
				AuditTrail auditTrail = new AuditTrail();
				auditTrail.setAction("Unblock Portal User");
				auditTrail.setActivity(Long.toString(pu.getId()));
				auditTrail.setDate(new Timestamp((new Date()).getTime()));
				auditTrail.setIpAddress(portletState.getRemoteIPAddress());
				auditTrail.setUserId(Long.toString(portletState.getPortalUser().getUserId()));
				swpService.createNewRecord(auditTrail);
				
				Collection<PortalUser> pus = portletState.getUserManagementSystemAdminPortletUtil().getApprovingPortalUsers(
						portletState.getPortalUser().getRoleType().getRoleTypeName());
				
				
				for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
				{
					PortalUser pu1 = it.next();
					emailer.emailApprovalRequest(
							pu1.getFirstName(), 
							pu1.getLastName(), 
							pu1.getEmailAddress(), 
							portletState.getSystemUrl().getValue(), 
							portletState.getApplicationName().getValue() + " - Approval Request for the Update of a Mandate-Panel/User Mapping",  
							portletState.getApplicationName().getValue());
				}
				
				for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
				{
					PortalUser pu1 = it.next();
					String message = "Approval request awaiting your action. " +
							"Visit " + portletState.getSystemUrl().getValue() + " to view requests awaiting your " +
							"approval/disapproval action";
					SendSms sendSms = new SendSms(pu1.getMobileNumber(), message, 
							portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
					
					
				}
				
				
				portletState.setPortalUserListing(portletState.getUserManagementSystemAdminPortletUtil().getAllPortalUsers(portletState));
				aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/userlisting.jsp");
				portletState.addSuccess(aReq, "Unblock User Request Successful!", portletState);
				
				
				pus = portletState.getUserManagementSystemAdminPortletUtil().getApprovingPortalUsers(
						portletState.getPortalUser().getRoleType().getRoleTypeName());
				
				for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
				{
					PortalUser pu1 = it.next();
					emailer.emailApprovalRequest(
							pu1.getFirstName(), 
							pu1.getLastName(), 
							pu1.getEmailAddress(), 
							portletState.getSystemUrl().getValue(), 
							portletState.getApplicationName().getValue() + " - Approval Request for the Unblocking of a bank Staff", 
							portletState.getApplicationName().getValue());
				}
				
				for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
				{
					PortalUser pu1 = it.next();
					String message = "Approval request awaiting your action. " +
							"Visit " + portletState.getSystemUrl().getValue() + " to view requests awaiting your " +
							"approval/disapproval action";
					SendSms sendSms = new SendSms(pu1.getMobileNumber(), message, 
							portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
					
					
				}
			}
			else
			{

				portletState.addError(aReq, "UnBlocking the selected user failed. Please try again", portletState);
			}
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			portletState.addError(aReq, "UnBlocking the selected user failed. Please try again", portletState);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			portletState.addError(aReq, "UnBlocking the selected user failed. Please try again", portletState);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			portletState.addError(aReq, "UnBlocking the selected user failed. Please try again", portletState);
		}
		
			
		
	}
	
	
	private void handleUnblockPortalUser(PortalUser pu,
			UserManagementSystemAdminPortletState portletState,
			ActionRequest aReq, ActionResponse aRes) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		User lpUser;
		try {
			lpUser = UserLocalServiceUtil.getUserById(pu.getUserId());
			if(lpUser!=null)
			{
				UserLocalServiceUtil.updateLockout(lpUser, false);
				pu.setStatus(PortalUserStatusConstants.PORTAL_USER_STATUS_ACTIVE);
				swpService.updateRecord(pu);
				AuditTrail auditTrail = new AuditTrail();
				auditTrail.setAction("Unblock Portal User");
				auditTrail.setActivity(Long.toString(pu.getId()));
				auditTrail.setDate(new Timestamp((new Date()).getTime()));
				auditTrail.setIpAddress(portletState.getRemoteIPAddress());
				auditTrail.setUserId(Long.toString(portletState.getPortalUser().getUserId()));
				swpService.createNewRecord(auditTrail);
				portletState.setPortalUserListing(portletState.getUserManagementSystemAdminPortletUtil().getAllPortalUsers(portletState));
				aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/userlisting.jsp");
				portletState.addError(aReq, "Selected User unblocked successfully", portletState);
				//handleEmailUserChangeOfAccountStatus(portletState, "Reactivated", "Stanbic IBTC e-Tax Account Reactivation");
				
				
				Collection<PortalUser> pus = portletState.getUserManagementSystemAdminPortletUtil().getApprovingPortalUsers(
						portletState.getPortalUser().getRoleType().getRoleTypeName());
				
				for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
				{
					PortalUser pu1 = it.next();
					
					emailer.emailApprovalRequest(
							pu1.getFirstName(), 
							pu1.getLastName(),  
							pu1.getEmailAddress(), 
							portletState.getSystemUrl().getValue(), "Approval Request for the Deletion of a bank Staff", 
							portletState.getApplicationName().getValue());
				}
				
				for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
				{
					PortalUser pu1 = it.next();
					String message = "Approval request awaiting your action. " +
							"Visit " + portletState.getSystemUrl().getValue() + " to view requests awaiting your " +
							"approval/disapproval action";
					SendSms sendSms = new SendSms(pu1.getMobileNumber(), message, 
							portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
					
					
				}
			}
			else
			{

				portletState.addError(aReq, "UnBlocking the selected user failed. Please try again", portletState);
			}
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			portletState.addError(aReq, "UnBlocking the selected user failed. Please try again", portletState);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			portletState.addError(aReq, "UnBlocking the selected user failed. Please try again", portletState);
		}
		
			
		
	}

	private void handleBlockPortalUser(PortalUser pu,
			UserManagementSystemAdminPortletState portletState,
			ActionRequest aReq, ActionResponse aRes) {
		// TODO Auto-generated method stub
		User lpUser;
		try {
			lpUser = UserLocalServiceUtil.getUserById(pu.getUserId());
			if(lpUser!=null)
			{
				UserLocalServiceUtil.updateStatus(lpUser.getUserId(), 1);
				pu.setStatus(PortalUserStatusConstants.PORTAL_USER_STATUS_SUSPENDED);
				swpService.updateRecord(pu);
				AuditTrail auditTrail = new AuditTrail();
				auditTrail.setAction("Block Portal User");
				auditTrail.setActivity(Long.toString(pu.getId()));
				auditTrail.setDate(new Timestamp((new Date()).getTime()));
				auditTrail.setIpAddress(portletState.getRemoteIPAddress());
				auditTrail.setUserId(Long.toString(portletState.getPortalUser().getUserId()));
				swpService.createNewRecord(auditTrail);
				portletState.addSuccess(aReq, "User blocked successfully", portletState);
				portletState.setPortalUserListing(portletState.getUserManagementSystemAdminPortletUtil().getAllPortalUsers(portletState));
				aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/userlisting.jsp");
				handleEmailUserChangeOfAccountStatus(portletState, "Suspended", "Stanbic IBTC e-Tax Account Suspension");
			}else
			{
				portletState.addError(aReq, "Blocking the selected user failed. Please try again", portletState);
			}
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			portletState.addError(aReq, "Blocking the selected user failed. Please try again", portletState);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			portletState.addError(aReq, "Blocking the selected user failed. Please try again", portletState);
		}
		
		
	}
	
	
	private void handleBlockPortalUserInitiate(PortalUser pu,
			UserManagementSystemAdminPortletState portletState,
			ActionRequest aReq, ActionResponse aRes) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		
		User lpUser;
		try {
			lpUser = UserLocalServiceUtil.getUserById(pu.getUserId());
			if(lpUser!=null)
			{
				
				ApprovalFlowTransit aft = new ApprovalFlowTransit();
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("firstName", pu.getFirstName());
				jsonObject.put("lastName", pu.getLastName());
				jsonObject.put("email", pu.getEmailAddress());
				jsonObject.put("alternativeEmail1", pu.getFirstAlternativeEmailAddress());
				jsonObject.put("alternativeEmail2", pu.getSecondAlternativeEmailAddress());
				jsonObject.put("mobile", pu.getMobileNumber());
				jsonObject.put("alternativeMobile1", pu.getFirstAlternativeMobileNumber());
				jsonObject.put("alternativeMobile2", pu.getSecondAlternativeMobileNumber());
				jsonObject.put("addressLine1", pu.getAddressLine1());
				jsonObject.put("addressLine2", pu.getAddressLine2());
				jsonObject.put("roleType", pu.getRoleType().getRoleTypeName().getValue());
				
				
				aft.setActionType(ActionTypeConstants.PORTAL_USER_ACTION_BLOCK);
				aft.setDateCreated(new Timestamp((new Date()).getTime()));
				aft.setEntityId(pu.getId());
				aft.setEntityName(PortalUser.class.getSimpleName());
				aft.setObjectData(jsonObject.toString());
				aft.setPortalUser(portletState.getPortalUser());
				swpService.createNewRecord(aft);
				
				
				AuditTrail auditTrail = new AuditTrail();
				auditTrail.setAction("Block Portal User");
				auditTrail.setActivity(Long.toString(pu.getId()));
				auditTrail.setDate(new Timestamp((new Date()).getTime()));
				auditTrail.setIpAddress(portletState.getRemoteIPAddress());
				auditTrail.setUserId(Long.toString(portletState.getPortalUser().getUserId()));
				swpService.createNewRecord(auditTrail);
				
				Collection<PortalUser> pus = portletState.getUserManagementSystemAdminPortletUtil().getApprovingPortalUsers(
						portletState.getPortalUser().getRoleType().getRoleTypeName());
				
				
				for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
				{
					PortalUser pu1 = it.next();
					emailer.emailApprovalRequest(
							pu1.getFirstName(), 
							pu1.getLastName(), 
							pu1.getEmailAddress(), 
							portletState.getSystemUrl().getValue(), 
							portletState.getApplicationName().getValue() + " - Approval Request for the Suspension of a Bank Staff Profile", 
							portletState.getApplicationName().getValue());
				}
				
				
				for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
				{
					PortalUser pu1 = it.next();
					String message = "Approval request awaiting your action. " +
							"Visit " + portletState.getSystemUrl().getValue() + " to view requests awaiting your " +
							"approval/disapproval action";
					SendSms sendSms = new SendSms(pu1.getMobileNumber(), message, 
							portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
					
					
				}
				
				portletState.addSuccess(aReq, "Request to suspend bank staff profile created successfully. Approving officers will attend to this request", portletState);
				portletState.setPortalUserListing(portletState.getUserManagementSystemAdminPortletUtil().getAllPortalUsers(portletState));
				aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/userlisting.jsp");
				//handleEmailUserChangeOfAccountStatus(portletState, "Suspended", "Stanbic IBTC e-Tax Account Suspension");
				
			}else
			{
				portletState.addError(aReq, "Blocking the selected user failed. Please try again", portletState);
			}
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			portletState.addError(aReq, "Blocking the selected user failed. Please try again", portletState);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			portletState.addError(aReq, "Blocking the selected user failed. Please try again", portletState);
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			portletState.addError(aReq, "Blocking the selected user failed. Please try again", portletState);
		} 
		
		
	}

	
	private void handleDeletePortalUser(PortalUser pu,
			UserManagementSystemAdminPortletState portletState,
			ActionRequest aReq, ActionResponse aRes) {
		// TODO Auto-generated method stub
		

		try {
			User lpUser = UserLocalServiceUtil.getUserById(pu.getUserId());
			if(lpUser!=null)
			{
				UserLocalServiceUtil.deleteUser(lpUser);
				pu.setStatus(PortalUserStatusConstants.PORTAL_USER_DELETED);
				swpService.updateRecord(pu);
				AuditTrail auditTrail = new AuditTrail();
				auditTrail.setAction("Delete Portal User");
				auditTrail.setActivity(Long.toString(pu.getId()));
				auditTrail.setDate(new Timestamp((new Date()).getTime()));
				auditTrail.setIpAddress(portletState.getRemoteIPAddress());
				auditTrail.setUserId(Long.toString(portletState.getPortalUser().getUserId()));
				swpService.createNewRecord(auditTrail);
				portletState.setPortalUserListing(portletState.getUserManagementSystemAdminPortletUtil().getAllPortalUsers(portletState));
				portletState.addSuccess(aReq, "User deleted successfully", portletState);
				aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/userlisting.jsp");
				handleEmailUserChangeOfAccountStatus(portletState, "Deleted", "Stanbic IBTC e-Tax Account Deletion");
			}else
			{
				portletState.addError(aReq, "Deleting the selected user failed. Please try again", portletState);
			}
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			portletState.addError(aReq, "Deleting the selected user failed. Please try again", portletState);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			portletState.addError(aReq, "Deleting the selected user failed. Please try again", portletState);
		}
		
		
	}
	
	
	
	private void handleDeletePortalUserInitiate(PortalUser pu,
			UserManagementSystemAdminPortletState portletState,
			ActionRequest aReq, ActionResponse aRes) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());

		
		try {
			User lpUser = UserLocalServiceUtil.getUserById(pu.getUserId());
			if(lpUser!=null)
			{
				ApprovalFlowTransit aft = new ApprovalFlowTransit();
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("firstName", pu.getFirstName());
				jsonObject.put("lastName", pu.getLastName());
				jsonObject.put("email", pu.getEmailAddress());
				jsonObject.put("alternativeEmail1", pu.getFirstAlternativeEmailAddress());
				jsonObject.put("alternativeEmail2", pu.getSecondAlternativeEmailAddress());
				jsonObject.put("mobile", pu.getMobileNumber());
				jsonObject.put("alternativeMobile1", pu.getFirstAlternativeMobileNumber());
				jsonObject.put("alternativeMobile2", pu.getSecondAlternativeMobileNumber());
				jsonObject.put("addressLine1", pu.getAddressLine1());
				jsonObject.put("addressLine2", pu.getAddressLine2());
				jsonObject.put("roleType", pu.getRoleType().getRoleTypeName().getValue());
				
				aft.setActionType(ActionTypeConstants.PORTAL_USER_ACTION_DELETE);
				aft.setDateCreated(new Timestamp((new Date()).getTime()));
				aft.setEntityId(pu.getId());
				aft.setEntityName(PortalUser.class.getSimpleName());
				aft.setObjectData(jsonObject.toString());
				aft.setPortalUser(portletState.getPortalUser());
				swpService.createNewRecord(aft);
				
				AuditTrail auditTrail = new AuditTrail();
				auditTrail.setAction("Initiate Delete Portal User");
				auditTrail.setActivity(Long.toString(pu.getId()));
				auditTrail.setDate(new Timestamp((new Date()).getTime()));
				auditTrail.setIpAddress(portletState.getRemoteIPAddress());
				auditTrail.setUserId(Long.toString(portletState.getPortalUser().getUserId()));
				swpService.createNewRecord(auditTrail);
				portletState.setPortalUserListing(portletState.getUserManagementSystemAdminPortletUtil().getAllPortalUsers(portletState));
				portletState.addSuccess(aReq, "User deletion request sent for approval successfully", portletState);
				aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/userlisting.jsp");
				//handleEmailUserChangeOfAccountStatus(portletState, "Deleted", "Stanbic IBTC e-Tax Account Deletion");
				
						
				Collection<PortalUser> pus = portletState.getUserManagementSystemAdminPortletUtil().getApprovingPortalUsers(
						portletState.getPortalUser().getRoleType().getRoleTypeName());
				
				for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
				{
					PortalUser pu1 = it.next();
					emailer.emailApprovalRequest(
							pu1.getFirstName(), 
							pu1.getLastName(), 
							pu1.getEmailAddress(), 
							portletState.getSystemUrl().getValue(), 
							portletState.getApplicationName().getValue() + " - Request for the Deletion of a bank Staff", 
							portletState.getApplicationName().getValue());
				}
				
				for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
				{
					PortalUser pu1 = it.next();
					String message = "Approval request awaiting your action. " +
							"Visit " + portletState.getSystemUrl().getValue() + " to view requests awaiting your " +
							"approval/disapproval action";
					SendSms sendSms = new SendSms(pu1.getMobileNumber(), message, 
							portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
					
					
				}
								
								
			}else
			{
				portletState.addError(aReq, "Deleting the selected user failed. Please try again", portletState);
			}
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			portletState.addError(aReq, "Deleting the selected user failed. Please try again", portletState);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			portletState.addError(aReq, "Deleting the selected user failed. Please try again", portletState);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			portletState.addError(aReq, "Deleting the selected user failed. Please try again", portletState);
		}
		
		
	}

	private void handleEmailUserChangeOfAccountStatus(
			UserManagementSystemAdminPortletState portletState, String status, String subject) {
		// TODO Auto-generated method stub
		PortalUser pu = portletState.getPortalUser();
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		
		SendMail sm = emailer.emailChangeOfAccountStatus(pu.getEmailAddress(), 
				"http://www.stanbicibtc.com.zm", 
				pu.getFirstName(), 
				pu.getLastName(), 
				subject, 
				status, 
				portletState.getApplicationName().getValue());
		
			String message = "Approval request awaiting your action. " +
					"Visit " + portletState.getSystemUrl().getValue() + " to view requests awaiting your " +
					"approval/disapproval action";
			SendSms sendSms = new SendSms(pu.getMobileNumber(), message, 
					portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
	}

	private void handleViewPortalUser(PortalUser pu,
			UserManagementSystemAdminPortletState portletState,
			ActionRequest aReq, ActionResponse aRes) {
		// TODO Auto-generated method stub
		portletState.setSelectedCompany(pu.getCompany());
		portletState.setportaluserAddressLine1(pu.getAddressLine1());
		portletState.setportaluserAddressLine2(pu.getAddressLine2());
		portletState.setportaluserfirstemail(pu.getEmailAddress());
		portletState.setportaluserfirstmobile(pu.getMobileNumber());
		portletState.setportaluserfirstname(pu.getFirstName());
		portletState.setportaluserlastname(pu.getLastName());	
		portletState.setportalusersecondmobile(pu.getFirstAlternativeEmailAddress());
		portletState.setportalusersecondemail(pu.getFirstAlternativeEmailAddress());
		portletState.setportaluserthirdemail(pu.getSecondAlternativeMobileNumber());
		portletState.setportaluserthirdmobile(pu.getSecondAlternativeEmailAddress());
		portletState.setSelectedUserRoleId(Long.toString(pu.getRoleType().getId()));
		aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/viewdetails.jsp");
		
	}

	private void handleGoToUpdatePortalUser(PortalUser pu,
			UserManagementSystemAdminPortletState portletState,
			ActionRequest aReq, ActionResponse aRes) {
		// TODO Auto-generated method stub
		portletState.setportaluserAddressLine1(pu.getAddressLine1());
		portletState.setportaluserAddressLine2(pu.getAddressLine2());
		portletState.setportaluserfirstemail(pu.getEmailAddress());

		portletState.setCountryCodeAlt1(pu.getMobileNumber()!=null && pu.getMobileNumber().length()>0 ? pu.getMobileNumber().substring(0,  3): "");
		portletState.setCountryCodeAlt2(pu.getFirstAlternativeMobileNumber()!=null && pu.getFirstAlternativeMobileNumber().length()>0 ? pu.getFirstAlternativeMobileNumber().substring(0,  3) : "");
		portletState.setCountryCodeAlt3(pu.getSecondAlternativeMobileNumber()!=null && pu.getSecondAlternativeMobileNumber().length()>0 ? pu.getSecondAlternativeMobileNumber().substring(0,  3) : "");
		if(pu.getMobileNumber().length()>0)
			portletState.setportaluserfirstmobile(pu.getMobileNumber()!=null && pu.getMobileNumber().length()>0 ? pu.getMobileNumber().substring(3,  pu.getFirstAlternativeMobileNumber().length()): "");
		else
			portletState.setportaluserfirstmobile("");
		portletState.setportaluserfirstname(pu.getFirstName());
		portletState.setportaluserlastname(pu.getLastName());	
		if(pu.getFirstAlternativeMobileNumber()!=null && pu.getFirstAlternativeMobileNumber().length()>0)
			portletState.setportalusersecondmobile(pu.getFirstAlternativeMobileNumber()!=null && pu.getFirstAlternativeMobileNumber().length()>0 ? pu.getFirstAlternativeMobileNumber().substring(3,  pu.getFirstAlternativeMobileNumber().length()) : "");
		else 
			portletState.setportalusersecondmobile("");
		portletState.setportalusersecondemail(pu.getFirstAlternativeEmailAddress());
		if(pu.getSecondAlternativeMobileNumber()!=null && pu.getSecondAlternativeMobileNumber().length()>0)
			portletState.setportaluserthirdmobile(pu.getSecondAlternativeMobileNumber()!=null && pu.getSecondAlternativeMobileNumber().length()>0 ? pu.getSecondAlternativeMobileNumber().substring(3,  pu.getFirstAlternativeMobileNumber().length()) : "");
		else 
			portletState.setportaluserthirdmobile("");
		portletState.setportaluserthirdemail(pu.getSecondAlternativeEmailAddress());
		portletState.setSelectedUserRoleId(Long.toString(pu.getRoleType().getId()));
		aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/register/stepone_edit.jsp");
	}

	private void createAPortalUserStepOne(ActionRequest aReq,
			ActionResponse aRes, UserManagementSystemAdminPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setSelectedUserRoleId(aReq.getParameter("roletype"));
		portletState.setportaluserfirstname(aReq.getParameter("firstname"));
		portletState.setportaluserlastname(aReq.getParameter("lastname"));

		portletState.setCountryCodeAlt1(aReq.getParameter("countryCode1"));
		portletState.setCountryCodeAlt2(aReq.getParameter("countryCode2"));
		portletState.setCountryCodeAlt3(aReq.getParameter("countryCode3"));
		if(aReq.getParameter("contactMobileNumber").length()>0)
			portletState.setportaluserfirstmobile(aReq.getParameter("countryCode1") + new Util().formatMobile(aReq.getParameter("contactMobileNumber")));
		else
			portletState.setportaluserfirstmobile("");
		if(aReq.getParameter("contactMobileNumberFirstAlternative").length()>0)
			portletState.setportalusersecondmobile(aReq.getParameter("countryCode2") + new Util().formatMobile(aReq.getParameter("contactMobileNumberFirstAlternative")));
		else 
			portletState.setportalusersecondmobile("");
		if(aReq.getParameter("contactMobileNumberSecondAlternative").length()>0)
			portletState.setportaluserthirdmobile(aReq.getParameter("countryCode3") + new Util().formatMobile(aReq.getParameter("contactMobileNumberSecondAlternative")));
		else 
			portletState.setportaluserthirdmobile("");
		portletState.setportaluserfirstemail(aReq.getParameter("contactEmailAddress"));
		portletState.setportalusersecondemail(aReq.getParameter("contactEmailAddressFirstAlternative"));
		portletState.setportaluserthirdemail(aReq.getParameter("contactEmailAddressSecondAlternative"));
		portletState.setportaluserAddressLine1(aReq.getParameter("contactAddressLine1"));
		portletState.setportaluserAddressLine2(aReq.getParameter("contactAddressLine2"));
		portletState.setUserCRUD(aReq.getParameter("userCRUD"));
		portletState.setCompanyCRUD(aReq.getParameter("companyCRUD"));
		
		log.info("Check if user is valid");
		if(isPortalUserCreationDataValid(portletState, aReq, aRes, false))
		{
			log.info("user is valid");
			if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_SYSTEM_SUPER_ADMINISTRATOR) || 
					portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR) || 
					portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_SUPER_ADMINISTRATOR))
			{
				log.info("user is system super admin");
				aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/register/stepthree.jsp");
			}else
			{
				portletState.addError(aReq, "You do not have the administrative rights to register a company personnel. Contact " +
						"Administrator personnel for rights", portletState);
				aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/register/stepone.jsp");
			}
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/register/stepone.jsp");
		}
	}
	
	
	
	private void updateAPortalUserStepOne(ActionRequest aReq,
			ActionResponse aRes, UserManagementSystemAdminPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setportaluserfirstname(aReq.getParameter("firstname"));
		portletState.setportaluserlastname(aReq.getParameter("lastname"));
		portletState.setCountryCodeAlt1(aReq.getParameter("countryCode"));
		portletState.setCountryCodeAlt2(aReq.getParameter("countryCode2"));
		portletState.setCountryCodeAlt3(aReq.getParameter("countryCode3"));	
		if(aReq.getParameter("contactMobileNumber").length()>0)
		{
			portletState.setportaluserfirstmobile(aReq.getParameter("countryCode") + new Util().formatMobile(aReq.getParameter("contactMobileNumber")));
		}
		else
		{
			portletState.setportaluserfirstmobile("");
		}
		if(aReq.getParameter("contactMobileNumberFirstAlternative").length()>0)
		{
			log.info("contactMobileNumberFirstAlternative===" + aReq.getParameter("contactMobileNumberFirstAlternative"));
			portletState.setportalusersecondmobile((aReq.getParameter("countryCode2") + new Util().formatMobile(aReq.getParameter("contactMobileNumberFirstAlternative"))));
		}
		else
		{
			portletState.setportalusersecondmobile("");
		}
		if(aReq.getParameter("contactMobileNumberSecondAlternative").length()>0)
		{
			log.info("contactMobileNumberSecondAlternative===" + aReq.getParameter("contactMobileNumberSecondAlternative"));
			portletState.setportaluserthirdmobile((aReq.getParameter("countryCode3") + new Util().formatMobile(aReq.getParameter("contactMobileNumberSecondAlternative"))));
		}
		else
		{
			portletState.setportaluserthirdmobile("");
		}
		portletState.setportaluserfirstemail(aReq.getParameter("contactEmailAddress"));
		portletState.setportalusersecondemail(aReq.getParameter("contactEmailAddressFirstAlternative"));
		portletState.setportaluserthirdemail(aReq.getParameter("contactEmailAddressSecondAlternative"));
		portletState.setportaluserAddressLine1(aReq.getParameter("contactAddressLine1"));
		portletState.setportaluserAddressLine2(aReq.getParameter("contactAddressLine2"));
		portletState.setUserCRUD(aReq.getParameter("userCRUD"));
		portletState.setCompanyCRUD(aReq.getParameter("companyCRUD"));
		
		log.info("Check if user is valid");
		if(isPortalUserCreationDataValid(portletState, aReq, aRes, true))
		{
			log.info("user is valid");
			if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_SYSTEM_SUPER_ADMINISTRATOR) || 
					portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR) || 
					portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_SUPER_ADMINISTRATOR))
			{
				log.info("user is system super admin");
				aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/register/stepthree_edit.jsp");
			}else
			{
				portletState.addError(aReq, "You do not have the administrative rights to register a company personnel. Contact " +
						"Administrator personnel for rights", portletState);
				aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/register/stepone_edit.jsp");
			}
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/register/stepone_edit.jsp");
		}
	}
	
	
	
	private void createAPortalUserStepThree(ActionRequest aReq,
			ActionResponse aRes, UserManagementSystemAdminPortletState portletState) {
		// TODO Auto-generated method stub
		ComminsApplicationState cappState = portletState.getCas();
		Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
		Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
		DETERMINE_ACCESS determinAccess = new Util().determineAccessForUserFxns(twoStep, approvalProcess, cappState, portletState.getPortalUserCRUDRights());
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		
		
		if(isPortalUserCreationDataValid(portletState, aReq, aRes, false))
		{
			log.info("We are in create a portal user");
			if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_SYSTEM_SUPER_ADMINISTRATOR))
			{
				log.info("We are in create a portal user1");
				Calendar c1 = GregorianCalendar.getInstance();
				c1.set(1980, Calendar.JANUARY, 1);
				Date date =c1.getTime();
				Timestamp dob = new Timestamp(date.getTime());
				RoleType roleType = (RoleType)portletState.getUserManagementSystemAdminPortletUtil().getEntityObjectById(RoleType.class, Long.valueOf(portletState.getSelectedUserRoleId()));
				log.info("roleType = " + portletState.getSelectedUserRoleId());
				log.info("roleTypeName = " + roleType.getRoleTypeName());
				
				PortalUserStatusConstants pusc = null;
				pusc = PortalUserStatusConstants.PORTAL_USER_STATUS_ACTIVE;
				PortalUser pu = handleCreateNewPortalUser(portletState.getportaluserfirstname(), 
					portletState.getportaluserlastname(), 
					portletState.getportaluserfirstemail(),
					portletState.getportalusersecondemail(),
					portletState.getportaluserthirdemail(),
					portletState.getportaluserfirstmobile(), 
					portletState.getportalusersecondmobile(), 
					portletState.getportaluserthirdmobile(),
					portletState.getportaluserAddressLine1(),
					portletState.getportaluserAddressLine2(),
					dob,
					roleType,
					pusc,
					portletState.getPortalUser().getCompany(), portletState, 
					aReq,
					aRes,
					portletState.getUserCRUD(), 
					portletState.getCompanyCRUD());
				

				portletState.setPortalUserListing(portletState.getUserManagementSystemAdminPortletUtil().getPortalUserByRole(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR));
				aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/userlisting.jsp");
				portletState.setCurrentTab(VIEW_TABS.VIEW_PORTAL_USER_LISTINGS);
				portletState.reinitializeForCreateCorporateIndividual(portletState);
			}
			else if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR) || 
					portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_SUPER_ADMINISTRATOR))
			{
				log.info("We are in create a portal user1");
				Calendar c1 = GregorianCalendar.getInstance();
				c1.set(1980, Calendar.JANUARY, 1);
				Date date =c1.getTime();
				Timestamp dob = new Timestamp(date.getTime());
				RoleType roleType = (RoleType)portletState.getUserManagementSystemAdminPortletUtil().getEntityObjectById(RoleType.class, Long.valueOf(portletState.getSelectedUserRoleId()));
				log.info("roleType = " + portletState.getSelectedUserRoleId());
				log.info("roleTypeName = " + roleType.getRoleTypeName());
				
				if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR)
						|| portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_SUPER_ADMINISTRATOR))
				{
					PortalUserStatusConstants pusc = null;
					if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
					{
						pusc = PortalUserStatusConstants.PORTAL_USER_INACTIVE;
						JSONObject jsonObject = new JSONObject();
						try {
							jsonObject.put("firstName", portletState.getportaluserfirstname());
							jsonObject.put("lastName", portletState.getportaluserlastname());
							jsonObject.put("email", portletState.getportaluserfirstemail());
							jsonObject.put("alternativeEmail1", portletState.getportalusersecondemail());
							jsonObject.put("alternativeEmail2", portletState.getportaluserthirdemail());
							jsonObject.put("mobile", portletState.getportaluserfirstmobile());
							jsonObject.put("alternativeMobile1", portletState.getportalusersecondmobile());
							jsonObject.put("alternativeMobile2", portletState.getportaluserthirdmobile());
							jsonObject.put("addressLine1", portletState.getportaluserAddressLine1());
							jsonObject.put("addressLine2", portletState.getportaluserAddressLine1());
							jsonObject.put("dob", dob);
							jsonObject.put("roleType", roleType.getRoleTypeName().getValue());
							jsonObject.put("userCRUD", portletState.getUserCRUD());
							jsonObject.put("companyCRUD", portletState.getCompanyCRUD());
							
							ApprovalFlowTransit approvalFlowTransit = new ApprovalFlowTransit();
							log.info("Start tracking here ");
							approvalFlowTransit.setEntityName(PortalUser.class.getSimpleName());
							log.info("class simple name =" + PortalUser.class.getSimpleName() + " && length = " + PortalUser.class.getSimpleName().length());
							approvalFlowTransit.setDateCreated(new Timestamp((new Date()).getTime()));
							log.info("new Timestamp((new Date()).getTime()) =" + new Timestamp((new Date()).getTime()));
							approvalFlowTransit.setObjectData(jsonObject.toString());
							log.info("jsonObject.toString() =" + jsonObject.toString() + " && length = " + jsonObject.toString().length());
							approvalFlowTransit.setActionType(ActionTypeConstants.PORTAL_USER_ACTION_CREATE);
							approvalFlowTransit.setPortalUser(portletState.getPortalUser());
							swpService.createNewRecord(approvalFlowTransit);
							
							//FORWARD EMAIIL/SMS to Approving officers
							Collection<PortalUser> pus = portletState.getUserManagementSystemAdminPortletUtil().getApprovingPortalUsers(
									portletState.getPortalUser().getRoleType().getRoleTypeName());
							Settings settingEmail = portletState.getNotifyCorporateIndividualEmail();
							Settings settingSms = portletState.getNotifyCorporateIndividualSMS();
							Settings settingSystemUrl = portletState.getSystemUrl();
							
							
							for(Iterator<PortalUser> puIter = pus.iterator(); puIter.hasNext();)
							{
								PortalUser pu1 = puIter.next();
								if(settingEmail.getValue().equals("1"))
								{
									
									emailer.emailApprovalRequest(
											pu1.getFirstName(), 
											pu1.getLastName(), 
											pu1.getEmailAddress(), 
											portletState.getSystemUrl().getValue(), 
											portletState.getApplicationName().getValue() + " - New Bank Staff Profile Created Requires Approval", 
											portletState.getApplicationName().getValue());
									
								}
								
								if(settingSms.getValue().equals("1"))
								{
									String message = portletState.getApplicationName().getValue() + " - New Bank Staff Profile Created Requires Your Approval";
									try{
									//new SendSms(createdUser.getMobileNumber(), message, "C_Portal");
										message = "Approval request awaiting your action. " +
												"Visit " + portletState.getSystemUrl().getValue() + " to view requests awaiting your " +
												"approval/disapproval action";
										SendSms sendSms = new SendSms(pu1.getMobileNumber(), message, 
												portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
									}catch(Exception e){
										log.error("error sending sms ",e);
									}
								}
								
							}
							
							portletState.addSuccess(aReq, "A request has been successfully logged requiring an approval officer to approve the creation " +
									"of this user profile.", portletState);
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							portletState.addError(aReq, "Problems were encountered creating this user profile. Please try again" +
									"Administrator personnel for rights", portletState);
							aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/register/stepone.jsp");
						}
						
					}else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
					{
						pusc = PortalUserStatusConstants.PORTAL_USER_STATUS_ACTIVE;
						PortalUser pu = handleCreateNewPortalUser(portletState.getportaluserfirstname(), 
								portletState.getportaluserlastname(), 
								portletState.getportaluserfirstemail(),
								portletState.getportalusersecondemail(),
								portletState.getportaluserthirdemail(),
								portletState.getportaluserfirstmobile(), 
								portletState.getportalusersecondmobile(), 
								portletState.getportaluserthirdmobile(),
								portletState.getportaluserAddressLine1(),
								portletState.getportaluserAddressLine2(),
								dob,
								roleType,
								pusc,
								portletState.getPortalUser().getCompany(), portletState, 
								aReq,
								aRes,
								portletState.getUserCRUD(), 
								portletState.getCompanyCRUD());
					}
					
				}else
				{
					PortalUser pu = handleCreateNewPortalUser(portletState.getportaluserfirstname(), 
						portletState.getportaluserlastname(), 
						portletState.getportaluserfirstemail(),
						portletState.getportalusersecondemail(),
						portletState.getportaluserthirdemail(),
						portletState.getportaluserfirstmobile(), 
						portletState.getportalusersecondmobile(), 
						portletState.getportaluserthirdmobile(),
						portletState.getportaluserAddressLine1(),
						portletState.getportaluserAddressLine2(),
						dob,
						roleType,
						PortalUserStatusConstants.PORTAL_USER_STATUS_ACTIVE,
						portletState.getPortalUser().getCompany(), portletState, 
						aReq,
						aRes,
						portletState.getUserCRUD(), 
						portletState.getCompanyCRUD());
				}

				aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/userlisting.jsp");
				portletState.setCurrentTab(VIEW_TABS.VIEW_PORTAL_USER_LISTINGS);
				portletState.reinitializeForCreateCorporateIndividual(portletState);
				portletState.setPortalUserListing(portletState.getUserManagementSystemAdminPortletUtil().getPortalUserByRole(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR));
			}else
			{
				portletState.addError(aReq, "You do not have the administrative rights to register a company personnel. Contact " +
						"Administrator personnel for rights", portletState);
				aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/register/stepone.jsp");
			}
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/register/stepone.jsp");
		}
	}
	
	
	
	
	
	private void updateAPortalUserStepThree(ActionRequest aReq,
			ActionResponse aRes, UserManagementSystemAdminPortletState portletState) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		
		
		if(isPortalUserCreationDataValid(portletState, aReq, aRes, true))
		{
			log.info("We are in update a portal user");
			if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_SYSTEM_SUPER_ADMINISTRATOR))
			{
				log.info("We are in update a portal user1");
				Calendar c1 = GregorianCalendar.getInstance();
				c1.set(1980, Calendar.JANUARY, 1);
				Date date =c1.getTime();
				Timestamp dob = new Timestamp(date.getTime());
				PortalUser pu = null;

				pu = handleUpdatePortalUser(portletState.getportaluserfirstname(), 
					portletState.getportaluserlastname(), 
					portletState.getportaluserfirstemail(),
					portletState.getportalusersecondemail(),
					portletState.getportaluserthirdemail(),
					portletState.getportaluserfirstmobile(), 
					portletState.getportalusersecondmobile(), 
					portletState.getportaluserthirdmobile(),
					portletState.getportaluserAddressLine1(),
					portletState.getportaluserAddressLine2(),
					PortalUserStatusConstants.PORTAL_USER_STATUS_ACTIVE,
					portletState, 
					aReq,
					aRes, 
					portletState.getUserCRUD(), 
					portletState.getCompanyCRUD());
			}
			else if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR) || 
					portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_SUPER_ADMINISTRATOR))
			{
				ComminsApplicationState cappState = portletState.getCas();
				Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
				Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
				DETERMINE_ACCESS determinAccess = new Util().determineAccessForUserFxns(twoStep, approvalProcess, cappState, portletState.getPortalUserCRUDRights());
				
				log.info("We are in update a portal user1");
				Calendar c1 = GregorianCalendar.getInstance();
				c1.set(1980, Calendar.JANUARY, 1);
				Date date =c1.getTime();
				Timestamp dob = new Timestamp(date.getTime());
				PortalUser pu = null;
				
				if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
				{
					JSONObject jsonObject = new JSONObject();
					try {
						jsonObject.put("firstName", portletState.getportaluserfirstname());
						jsonObject.put("lastName", portletState.getportaluserlastname());
						jsonObject.put("email", portletState.getportaluserfirstemail());
						jsonObject.put("alternativeEmail1", portletState.getportalusersecondemail());
						jsonObject.put("alternativeEmail2", portletState.getportaluserthirdemail());
						jsonObject.put("mobile", portletState.getportaluserfirstmobile());
						jsonObject.put("alternativeMobile1", portletState.getportalusersecondmobile()==null ? "" : portletState.getportalusersecondmobile());
						jsonObject.put("alternativeMobile2", portletState.getportaluserthirdmobile());
						jsonObject.put("addressLine1", portletState.getportaluserAddressLine1());
						jsonObject.put("addressLine2", portletState.getportaluserAddressLine1());
						jsonObject.put("roleType", ((RoleType)portletState.getUserManagementSystemAdminPortletUtil().getEntityObjectById(RoleType.class, 
								Long.valueOf(portletState.getSelectedUserRoleId()))).getRoleTypeName().getValue());
						jsonObject.put("dob", dob);
						jsonObject.put("userCRUD", portletState.getUserCRUD());
						jsonObject.put("companyCRUD", portletState.getCompanyCRUD());
						
						ApprovalFlowTransit approvalFlowTransit = new ApprovalFlowTransit();
						approvalFlowTransit.setEntityName(PortalUser.class.getSimpleName());
						approvalFlowTransit.setDateCreated(new Timestamp((new Date()).getTime()));
						approvalFlowTransit.setObjectData(jsonObject.toString());
						approvalFlowTransit.setEntityId(Long.valueOf(portletState.getSelectedPortalUserId()));
						approvalFlowTransit.setActionType(ActionTypeConstants.PORTAL_USER_ACTION_UPDATE);
						approvalFlowTransit.setPortalUser(portletState.getPortalUser());
						swpService.createNewRecord(approvalFlowTransit);
						
						//FORWARD EMAIIL/SMS to Approving officers
						Collection<PortalUser> pus = portletState.getUserManagementSystemAdminPortletUtil().getApprovingPortalUsers(
								portletState.getPortalUser().getRoleType().getRoleTypeName());
						Settings settingEmail = portletState.getNotifyCorporateIndividualEmail();
						Settings settingSms = portletState.getNotifyCorporateIndividualSMS();
						Settings settingSystemUrl = portletState.getSystemUrl();
						
						
						for(Iterator<PortalUser> puIter = pus.iterator(); puIter.hasNext();)
						{
							PortalUser pu1 = puIter.next();
							if(settingEmail.getValue().equals("1"))
							{
								
								emailer.emailApprovalRequest(
										pu1.getFirstName(), 
										pu1.getLastName(), 
										pu1.getEmailAddress(), 
										portletState.getSystemUrl().getValue(), 
										portletState.getApplicationName().getValue() + " - Bank Staff Profile Update Requires Approval", 
										portletState.getApplicationName().getValue());
								
							}
							
							if(settingSms.getValue().equals("1"))
							{
								String message = portletState.getApplicationName().getValue() + " - New Bank Staff Profile Update Requires Your Approval";
								try{
									
										message = "Approval request awaiting your action. " +
												"Visit " + portletState.getSystemUrl().getValue() + " to view requests awaiting your " +
												"approval/disapproval action";
										new SendSms(pu1.getMobileNumber(), message, 
												portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
										
								}catch(Exception e){
									log.error("error sending sms ",e);
								}
							}
							
						}
						
						portletState.addSuccess(aReq, "Approval Request successfully sent!", portletState);
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						portletState.addError(aReq, "Problems were encountered creating this user profile. Please try again" +
								"Administrator personnel for rights", portletState);
						aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/register/stepone.jsp");
					}
				}else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
				{
					pu = handleUpdatePortalUser(portletState.getportaluserfirstname(), 
							portletState.getportaluserlastname(), 
							portletState.getportaluserfirstemail(),
							portletState.getportalusersecondemail(),
							portletState.getportaluserthirdemail(),
							portletState.getportaluserfirstmobile(), 
							portletState.getportalusersecondmobile(), 
							portletState.getportaluserthirdmobile(),
							portletState.getportaluserAddressLine1(),
							portletState.getportaluserAddressLine2(),
							PortalUserStatusConstants.PORTAL_USER_STATUS_ACTIVE,
							portletState, 
							aReq,
							aRes, 
							portletState.getUserCRUD(), 
							portletState.getCompanyCRUD());
				}
					
				
				
				
				
				
				if(pu!=null)
				{
					portletState.addSuccess(aReq, "User update was successful!", portletState);
					portletState.setPortalUserListing(portletState.getUserManagementSystemAdminPortletUtil().getAllPortalUsers(portletState));
					aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/userlisting.jsp");
					portletState.setCurrentTab(VIEW_TABS.VIEW_PORTAL_USER_LISTINGS);
					portletState.reinitializeForCreateCorporateIndividual(portletState);
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/userlisting.jsp");
					portletState.setCurrentTab(VIEW_TABS.VIEW_PORTAL_USER_LISTINGS);
					portletState.reinitializeForCreateCorporateIndividual(portletState);
				}
			}else
			{
				portletState.setCurrentTab(VIEW_TABS.VIEW_PORTAL_USER_LISTINGS);
				portletState.addError(aReq, "You do not have the administrative rights to update a personnel on this portal. Contact " +
						"System Administrator for rights", portletState);
				aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/register/stepone_edit.jsp");
			}
		}else
		{
			portletState.setCurrentTab(VIEW_TABS.VIEW_PORTAL_USER_LISTINGS);
			aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/register/stepone_edit.jsp");
		}
	}

	private PortalUser handleCreateNewPortalUser(
			String createportaluserfirstname,
			String createportaluserlastname,
			String createportaluserfirstemail,
			String createportalusersecondemail,
			String createportaluserthirdemail,
			String createportaluserfirstmobile,
			String createportalusersecondmobile,
			String createportaluserthirdmobile,
			String createportaluserAddressLine1,
			String createportaluserAddressLine2,
			Timestamp dob,
			RoleType roleType,
			PortalUserStatusConstants portalUserInactive, Company company, 
			UserManagementSystemAdminPortletState portletState, 
			ActionRequest aReq,
			ActionResponse aRes,
			String userCRUD, 
			String companyCRUD) {
		// TODO Auto-generated method stub
		PortalUser pu = new PortalUser();
		pu.setAddressLine1(createportaluserAddressLine1);
		pu.setAddressLine2(createportaluserAddressLine2);
		pu.setCompany(company);
		pu.setDateOfBirth(dob);
		pu.setEmailAddress(createportaluserfirstemail);
		pu.setFirstAlternativeEmailAddress(createportalusersecondemail);
		pu.setSecondAlternativeEmailAddress(createportaluserthirdemail);
		pu.setMobileNumber(createportaluserfirstmobile);
		pu.setFirstAlternativeMobileNumber(createportalusersecondmobile);
		pu.setSecondAlternativeMobileNumber(createportaluserthirdmobile);
		pu.setFirstName(createportaluserfirstname);
		pu.setLastName(createportaluserlastname);
		pu.setStatus(portalUserInactive);
		pu.setRoleType(roleType);
		pu.setCreatedByPortalUserId(portletState.getPortalUser().getId());
		pu.setDisableEmailNotification(Boolean.FALSE);
		pu.setDisableSMSNotification(Boolean.FALSE);
		
		long communities[] = new long[1];
		
		communities[0] = new Util().getPortalUserCommunityByRoleType(roleType);
		Settings settingEmail = portletState.getNotifyCorporateIndividualEmail();
		Settings settingSms = portletState.getNotifyCorporateIndividualSMS();
		Settings settingSystemUrl = portletState.getSystemUrl();
		
		AuditTrail auditTrail = new AuditTrail();
		auditTrail.setAction("Create Portal User");
		auditTrail.setDate(new Timestamp((new Date()).getTime()));
		auditTrail.setIpAddress(portletState.getRemoteIPAddress());
		auditTrail.setUserId(Long.toString(portletState.getPortalUser().getUserId()));

		handleCreateUserOrbitaAccount(pu, 
				portletState.getportaluserfirstname(), 
				portletState.getportalusermiddlename(),
				portletState.getportaluserlastname(),
				portletState.getportaluserfirstemail(),
				communities,
				auditTrail, 
				serviceContext, 
				swpService,
				portletState.getPortalUser().getUserId(),
				true,
				portalUserInactive.equals(PortalUserStatusConstants.PORTAL_USER_STATUS_ACTIVE) ? true : false, 
				settingEmail.equals("1") ? true : false,
				settingSms.equals("1") ? true : false,
				settingSystemUrl.getValue()==null ? "SmartPay" : settingSystemUrl.getValue(),
				portletState,
				aReq, 
				aRes, 
				userCRUD,
				companyCRUD
				);
		return pu;
	}
	
	
	
	
	private PortalUser handleUpdatePortalUser(
			String createportaluserfirstname,
			String createportaluserlastname,
			String createportaluserfirstemail,
			String createportalusersecondemail,
			String createportaluserthirdemail,
			String createportaluserfirstmobile,
			String createportalusersecondmobile,
			String createportaluserthirdmobile,
			String createportaluserAddressLine1,
			String createportaluserAddressLine2,
			PortalUserStatusConstants portalUserInactive,
			UserManagementSystemAdminPortletState portletState, 
			ActionRequest aReq,
			ActionResponse aRes, 
			String userCRUD, 
			String companyCRUD) {
		// TODO Auto-generated method stub
		PortalUser pu = new PortalUser();
//		pu.setAddressLine1(createportaluserAddressLine1);
//		pu.setAddressLine2(createportaluserAddressLine2);
//		pu.setEmailAddress(createportaluserfirstemail);
//		pu.setFirstAlternativeEmailAddress(createportalusersecondemail);
//		pu.setSecondAlternativeEmailAddress(createportaluserthirdemail);
//		pu.setMobileNumber(createportaluserfirstmobile);
//		pu.setFirstAlternativeMobileNumber(createportalusersecondmobile);
//		pu.setSecondAlternativeMobileNumber(createportaluserthirdmobile);
//		pu.setFirstName(createportaluserfirstname);
//		pu.setLastName(createportaluserlastname);
//		pu.setStatus(PortalUserStatusConstants.PORTAL_USER_STATUS_ACTIVE);
		pu = (PortalUser)portletState.getUserManagementSystemAdminPortletUtil().getEntityObjectById(
				PortalUser.class, 
				Long.valueOf(portletState.getSelectedPortalUserId()));
		if(pu!=null)
		{
			Settings settingEmail = portletState.getNotifyCorporateIndividualEmail();
			Settings settingSms = portletState.getNotifyCorporateIndividualSMS();
			Settings settingSystemUrl = portletState.getSystemUrl();
			
			AuditTrail auditTrail = new AuditTrail();
			auditTrail.setAction("Update Portal User");
			auditTrail.setDate(new Timestamp((new Date()).getTime()));
			auditTrail.setIpAddress(portletState.getRemoteIPAddress());
			auditTrail.setUserId(Long.toString(portletState.getPortalUser().getUserId()));
	
			User upUser = handleUpdateUserOrbitaAccount(
					pu, portletState.getportaluserfirstname(),
					portletState.getportaluserAddressLine1(), 
					portletState.getportaluserAddressLine2(),
					portletState.getportalusersecondemail(),
					portletState.getportaluserthirdemail(),
					portletState.getportalusersecondmobile(),
					portletState.getportaluserthirdmobile(),
					portletState.getportaluserfirstmobile(),
					"",
					portletState.getportaluserlastname(),
					portletState.getportaluserfirstemail(),
					auditTrail, serviceContext, 
					swpService, false, 
					settingEmail.getValue().equals("1") ? true : false, 
					settingSms.getValue().equals("1") ? true : false,
					true, settingSystemUrl.getValue(), 
					portletState, aReq, aRes);
			
	
			if(upUser!=null)
			{
				pu.setAddressLine1(createportaluserAddressLine1);
				pu.setAddressLine2(createportaluserAddressLine2);
				pu.setEmailAddress(createportaluserfirstemail);
				pu.setFirstAlternativeEmailAddress(createportalusersecondemail);
				pu.setSecondAlternativeEmailAddress(createportaluserthirdemail);
				pu.setFirstAlternativeMobileNumber(createportalusersecondmobile);
				pu.setSecondAlternativeMobileNumber(createportaluserthirdmobile);
				pu.setMobileNumber(createportaluserfirstmobile);
				pu.setFirstName(createportaluserfirstname);
				pu.setLastName(createportaluserlastname);
				swpService.updateRecord(pu);
				
				
				
				if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_SUPER_ADMINISTRATOR))
				{
					Collection<PortalUserCRUDRights> puCRUDList = portletState.getUserManagementSystemAdminPortletUtil().getAllPortalUserCRUDRightsByPortalUser(pu.getId());
					if(puCRUDList!=null && puCRUDList.size()>0)
					{
						for(Iterator<PortalUserCRUDRights> iter = puCRUDList.iterator(); iter.hasNext();)
						{
							swpService.deleteRecord(iter.next());
						}
					}
					
					Collection<CompanyCRUDRights> companyCRUDList = portletState.getUserManagementSystemAdminPortletUtil().getAllCompanyCRUDRightsRightsByPortalUser(pu.getId());
					if(companyCRUDList!=null && companyCRUDList.size()>0)
					{
						for(Iterator<CompanyCRUDRights> iter = companyCRUDList.iterator(); iter.hasNext();)
						{
							swpService.deleteRecord(iter.next());
						}
					}
					
					
					if(userCRUD!=null && companyCRUD!=null)
					{
						PortalUserCRUDRights puCRUD = new PortalUserCRUDRights();
						if(userCRUD.equals("1"))
						{
							puCRUD.setDefualtViewRights(Boolean.TRUE);
							puCRUD.setCudApprovalRights(Boolean.TRUE);
							puCRUD.setCudInitiatorRights(Boolean.FALSE);
						}else if(userCRUD.equals("0"))
						{
							puCRUD.setDefualtViewRights(Boolean.TRUE);
							puCRUD.setCudApprovalRights(Boolean.FALSE);
							puCRUD.setCudInitiatorRights(Boolean.TRUE);
						}else
						{
							puCRUD.setDefualtViewRights(Boolean.FALSE);
							puCRUD.setCudApprovalRights(Boolean.FALSE);
							puCRUD.setCudInitiatorRights(Boolean.FALSE);
						}
						puCRUD.setPortalUser(pu);
						puCRUD.setDateUpdated(new Timestamp((new Date()).getTime()));
						swpService.createNewRecord(puCRUD);
						auditTrail = new AuditTrail();
						auditTrail.setAction("Create Portal User CRUD");
						auditTrail.setActivity(Long.toString(pu.getId()));
						auditTrail.setDate(new Timestamp((new Date()).getTime()));
						auditTrail.setIpAddress(portletState.getRemoteIPAddress());
						auditTrail.setUserId(Long.toString(portletState.getPortalUser().getUserId()));
						swpService.createNewRecord(auditTrail);
						
						
						CompanyCRUDRights coCRUD = new CompanyCRUDRights();
						if(companyCRUD.equals("1"))
						{
							coCRUD.setDefaultViewRights(Boolean.TRUE);
							coCRUD.setCudApprovalRights(Boolean.TRUE);
							coCRUD.setCudInitiatorRights(Boolean.FALSE);
						}else if(companyCRUD.equals("0"))
						{
							coCRUD.setDefaultViewRights(Boolean.TRUE);
							coCRUD.setCudApprovalRights(Boolean.FALSE);
							coCRUD.setCudInitiatorRights(Boolean.TRUE);
						}else
						{
							coCRUD.setDefaultViewRights(Boolean.FALSE);
							coCRUD.setCudApprovalRights(Boolean.FALSE);
							coCRUD.setCudInitiatorRights(Boolean.FALSE);
						}
						coCRUD.setDateLastModified(new Timestamp((new Date()).getTime()));
						coCRUD.setPortalUser(pu);
						swpService.createNewRecord(coCRUD);
						auditTrail = new AuditTrail();
						auditTrail.setAction("Create Company CRUD");
						auditTrail.setActivity(Long.toString(coCRUD.getId()));
						auditTrail.setDate(new Timestamp((new Date()).getTime()));
						auditTrail.setIpAddress(portletState.getRemoteIPAddress());
						auditTrail.setUserId(Long.toString(portletState.getPortalUser().getUserId()));
						swpService.createNewRecord(auditTrail);
						
					}
				}
				return pu;
			}else
			{
				return null;
			}
		}else
		{
			portletState.addError(aReq, "Invalid User selected. Select a valid User before proceeding", portletState);
			return null;
		}
	}
	
	
	
	public static PortalUser handleCreateUserOrbitaAccount(PortalUser user, String firstname, String middlename, String surname, String email, 
			long[] communities, AuditTrail auditTrail, ServiceContext serviceContext, SwpService sService, long loggedInUserId,
			boolean passwordReset, boolean active, boolean sendEmail, boolean sendSms, String systemUrl, 
			UserManagementSystemAdminPortletState portletState, ActionRequest aReq, ActionResponse aRes, String userCRUD, String companyCRUD) {			
		
		
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		
		Logger log1 = Logger.getLogger(UserManagementSystemAdminPortlet.class);
		PortalUser createdUser = user;
		long creatorUserId = loggedInUserId;
		System.out.println("communities "+communities.length);
		boolean alreadyInOrbita = Boolean.FALSE;

		long companyId = ProbaseConstants.COMPANY_ID;
		String jobTitle = "";
		long organizationId = 0;
		long locationId = 0;
		long[] orgAndLocation = new long[2];
		orgAndLocation[0] = organizationId;
		orgAndLocation[1] = locationId;

		int prefixId = 1;
		int suffixId = 1;

		boolean male = true;
		boolean emailSend = true;

		int birthdayMonth = 2;
		int birthdayDay = 2;
		int birthdayYear = 1980;

		long facebookId = 0;
		String openId = "";

		User aUser = null;
		long[] groupIds = communities;
		if (createdUser.getId() == null) {
			System.out.println("In cService method, starting user creation");

			User newlyCreatedUser = null;

			boolean autoPassword = false;
			String password1 = RandomStringUtils.random(8, true, true);
			String password2 = password1;			
			String emailSuffix = ProbaseConstants.DOMAIN_EMAIL_SUFFIX;			

			boolean autoScreenName = false;
			String formattedUsername = "";
			try {
				formattedUsername = getUniqueScreenName(createdUser.getLastName(), createdUser.getFirstName(), emailSuffix);
			} catch (SystemException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (PortalException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			formattedUsername = formattedUsername.replaceAll("/", ".").replaceAll("-", ".").replaceAll("_", ".");
			String screenName = formattedUsername;
			String emailAddress = email;
			String firstName = firstname;
			String lastName = surname;
			String middleName = middlename;

			System.out.println("Successfully set all parameters");

			if (emailSuffix != null || !emailSuffix.equalsIgnoreCase("")) {
				//emailAddress = createdUser.getEmailAddress();
			}

			// if(emailAddress != null && !emailAddress.equalsIgnoreCase("")){
			// only make AuthorizedUser an orbita user account if he has an
			// email
			String greeting = "Welcome, " + firstName + "!";
			try {
				
								
				
				if (aUser == null){
					try {
						log1.debug("getting auser");
						aUser = UserLocalServiceUtil.getUserByScreenName(companyId, screenName);
						log1.debug("auser gotten" + aUser);
					} catch (NoSuchUserException e) {
						//log1.error("NoSuchUserException");
					}
				}				
				// try creating an orbita acct..

				
				
				long[] organizationIds = new long[1];
				long[] roleIds = new long[2];
				long[] userGroupIds = new long[1];

				
				organizationIds[0] = 10134;
				//roleIds[0] = ProbaseConstants.ORBITA_ADMIN_ROLE_ID; //Administrator role
				roleIds[0] = ProbaseConstants.ORBITA_USER_ROLE_ID;//User role
				userGroupIds[0] = ProbaseConstants.ORBITA_USER_GROUP_ID;

				boolean addGroupStatus = Boolean.FALSE;				

				if (aUser != null || alreadyInOrbita) {
					newlyCreatedUser = aUser; log1.debug("User already exists.");									
				} else {
					try {
						
						newlyCreatedUser = UserLocalServiceUtil.addUser(
								creatorUserId, companyId, autoPassword,
								password1, password2, autoScreenName,
								screenName, emailAddress, facebookId, openId,
								Locale.US, firstName, middleName, lastName,
								prefixId, suffixId, male, birthdayMonth,
								birthdayDay, birthdayYear, jobTitle, groupIds,
								organizationIds, roleIds, userGroupIds,
								emailSend, serviceContext);

						System.out.println("Password1 = " + password1);
						System.out.println("Password2 = " + password2);
						System.out.println("Creation succcessful..now adding to community!!! "+newlyCreatedUser.getUserId());

						UserLocalServiceUtil.updatePasswordReset(
								newlyCreatedUser.getUserId(), passwordReset);

						//Update user's status
						if(active){
							UserLocalServiceUtil.updateStatus(
									newlyCreatedUser.getUserId(), 0);
						}else{
							UserLocalServiceUtil.updateStatus(
									newlyCreatedUser.getUserId(), 1);
						}
						for(int i = 0; i < communities.length; i++){
						addGroupStatus = addUserToCommmunity(newlyCreatedUser.getUserId(), communities[i] );	
						if (addGroupStatus == false) {
							System.out.println("addGroupStatus is false");
							try {
								UserLocalServiceUtil.deleteUser(newlyCreatedUser
										.getUserId());
							} catch (Exception e) {
								log1.error("", e);
								portletState.addError(aReq, "Your Online Account could not be created" +
										" on " + systemUrl + ". Please try again", portletState);
							}
						}
						}

					} catch (DuplicateUserScreenNameException t) {
						log1.error("DuplicateScreenNameException");
						portletState.addError(aReq, "Your Online Account could not be created" +
								" on " + systemUrl + ". Please try again", portletState);
						return null ;
					} catch (Exception e) {
						log1.error("", e);
						portletState.addError(aReq, "Your Online Account could not be created" +
								" on " + systemUrl + ". Please try again", portletState);
					}

					System.out.println("Added succcessful");

					

					System.out.println("Setting Orbita StaffId");
					createdUser.setUserId(newlyCreatedUser.getUserId());
				}

			} catch (Exception e) { 
				portletState.addError(aReq, "Your Online Account could not be created" +
						" on " + systemUrl + ". Please try again", portletState); }

			try {
				
				

				createdUser = (PortalUser)sService.createNewRecord(createdUser);
				auditTrail.setActivity("Create Portal User " + createdUser.getId());
				sService.createNewRecord(auditTrail);
				
				if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_SUPER_ADMINISTRATOR) || 
						portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_SYSTEM_SUPER_ADMINISTRATOR))
				{
					if(userCRUD!=null && companyCRUD!=null)
					{
						PortalUserCRUDRights puCRUD = new PortalUserCRUDRights();
						if(userCRUD.equals("1"))
						{
							puCRUD.setDefualtViewRights(Boolean.TRUE);
							puCRUD.setCudApprovalRights(Boolean.TRUE);
							puCRUD.setCudInitiatorRights(Boolean.FALSE);
						}else if(userCRUD.equals("0"))
						{
							puCRUD.setDefualtViewRights(Boolean.TRUE);
							puCRUD.setCudApprovalRights(Boolean.FALSE);
							puCRUD.setCudInitiatorRights(Boolean.TRUE);
						}else
						{
							puCRUD.setDefualtViewRights(Boolean.FALSE);
							puCRUD.setCudApprovalRights(Boolean.FALSE);
							puCRUD.setCudInitiatorRights(Boolean.FALSE);
						}
						puCRUD.setDateUpdated(new Timestamp((new Date()).getTime()));
						puCRUD.setPortalUser(createdUser);
						puCRUD.setStatus(Boolean.TRUE);
						sService.createNewRecord(puCRUD);
						auditTrail = new AuditTrail();
						auditTrail.setAction("Create Portal User CRUD");
						auditTrail.setActivity(Long.toString(puCRUD.getId()));
						auditTrail.setDate(new Timestamp((new Date()).getTime()));
						auditTrail.setIpAddress(portletState.getRemoteIPAddress());
						auditTrail.setUserId(Long.toString(portletState.getPortalUser().getUserId()));
						sService.createNewRecord(auditTrail);
						
						
						CompanyCRUDRights coCRUD = new CompanyCRUDRights();
						if(companyCRUD.equals("1"))
						{
							coCRUD.setDefaultViewRights(Boolean.TRUE);
							coCRUD.setCudApprovalRights(Boolean.TRUE);
							coCRUD.setCudInitiatorRights(Boolean.FALSE);
						}else if(companyCRUD.equals("0"))
						{
							coCRUD.setDefaultViewRights(Boolean.TRUE);
							coCRUD.setCudApprovalRights(Boolean.FALSE);
							coCRUD.setCudInitiatorRights(Boolean.TRUE);
						}else
						{
							coCRUD.setDefaultViewRights(Boolean.FALSE);
							coCRUD.setCudApprovalRights(Boolean.FALSE);
							coCRUD.setCudInitiatorRights(Boolean.FALSE);
						}
						coCRUD.setDateLastModified(new Timestamp((new Date()).getTime()));
						coCRUD.setPortalUser(createdUser);
						coCRUD.setStatus(Boolean.TRUE);
						sService.createNewRecord(coCRUD);
						auditTrail = new AuditTrail();
						auditTrail.setAction("Create Company CRUD");
						auditTrail.setActivity(Long.toString(coCRUD.getId()));
						auditTrail.setDate(new Timestamp((new Date()).getTime()));
						auditTrail.setIpAddress(portletState.getRemoteIPAddress());
						auditTrail.setUserId(Long.toString(portletState.getPortalUser().getUserId()));
						sService.createNewRecord(auditTrail);
						
					}
				}
				
				
				if(sendEmail){
					//sendMail(firstname, surname, email);
					
					
					emailer.emailNewBankStaffAccount(emailAddress, 
							"", password1, portletState.getSystemUrl().getValue(), 
							createdUser.getFirstName(), createdUser.getLastName(), createdUser.getRoleType().getRoleTypeName().getValue(), 
							"New " + portletState.getApplicationName().getValue() + " Bank Staff Account Created for you", 
							portletState.getApplicationName().getValue());
				}
				if(sendSms){
					String message = "Hello, Your Online Account has been " +
							"successfully created on " + systemUrl + ". Your login email is " +  
							emailAddress + " and password: " + password1;
					try{
							message = "Approval request awaiting your action. " +
									"Visit " + portletState.getSystemUrl().getValue() + " to view requests awaiting your " +
									"approval/disapproval action";
							new SendSms(createdUser.getMobileNumber(), message, 
									portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
					}catch(Exception e){
						log1.error("error sending sms ",e);
					}
				}
				portletState.addSuccess(aReq, "The online " + portletState.getApplicationName().getValue() + " web account for <em>" + emailAddress + "</em> has been " +
							"successfully created on " + systemUrl + ". <br>Your login details are: <br><strong>Email Address:</strong> " +  
							emailAddress + "<br><strong>Password:</strong> " + password1, portletState);
				
			} catch (Exception e) { log1.error("", e);portletState.addError(aReq, "The online " + portletState.getApplicationName().getValue() + " web Account could not be created" +
					" on " + systemUrl + ". Please try again", portletState); }

						
			long staffId = createdUser.getId();
			

		}

		return createdUser;		
	}
	
	
	
	public static User handleUpdateUserOrbitaAccount(PortalUser user, String firstname, 
			String addressLine1, String addressLine2, 
			String firstAlternativeEmailAddress, String secondAlternativeEmailAddress, 
			String firstAlternativeMobileNumber, String secondAlternativeMobileNumber, 
			String mobileNumber, String middlename, String surname, String email, 
			AuditTrail auditTrail, ServiceContext serviceContext, SwpService sService,
			boolean passwordReset, boolean active, boolean sendEmail, boolean sendSms, String systemUrl, 
			UserManagementSystemAdminPortletState portletState, ActionRequest aReq, ActionResponse aRes) {		
		
		
		Logger log = Logger.getLogger(UserManagementSystemAdminPortlet.class);
		log.info("Update Orbita User Account");
		Logger log1 = Logger.getLogger(UserManagementSystemAdminPortlet.class);
		PortalUser existingUser = user;
		boolean alreadyInOrbita = Boolean.FALSE;
		
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());

		long companyId = ProbaseConstants.COMPANY_ID;
		String jobTitle = "";
		long organizationId = 0;
		long locationId = 0;
		long[] orgAndLocation = new long[2];
		orgAndLocation[0] = organizationId;
		orgAndLocation[1] = locationId;

		int prefixId = 1;
		int suffixId = 1;

		boolean male = true;
		boolean emailSend = true;

		long facebookId = 0;
		String openId = "";
		
		int birthdayMonth = 2;
		int birthdayDay = 2;
		int birthdayYear = 1980;
		
		
		
		//Get lportal User from pu id
		//check to see if the email of lportalUser is same as the new email provided
		//if not the same, check if another user has that email
			//if another user has that email, bounce the update
			//else simply update the user
		//if they are the same, simply update the user
		User lpUser;
		User updatedUser = null;
		try {
			log.info("1" + email);
			lpUser = UserLocalServiceUtil.getUserById(existingUser.getUserId());
			log.info("2");
			if(lpUser!=null && !lpUser.getEmailAddress().equals(email))
			{
				log.info("User's Email Address has been changed");
				try
				{
					log.info("firstName = " + lpUser.getFirstName());
					log.info("3");
					User checkUser = UserLocalServiceUtil.getUserByEmailAddress(companyId, email);
					log.info("4");
					if(checkUser!=null)
					{

						log.info("Bounce the update operation");
						//bounce the update operation
						portletState.addError(aReq, "The email address you provided has already been used " +
								"by another user. Provide another valid email address" +
								"", portletState);
					}
				}catch(NoSuchUserException ex)
				{

					log.info("Proceed with the update operation");
					String screenName = "";
					if(lpUser.getFirstName().equalsIgnoreCase(firstname) && lpUser.getLastName().equalsIgnoreCase(surname))
					{
						screenName = lpUser.getScreenName();
						log.info("screenname = " + screenName);
					}
					else
					{
						String emailSuffix = ProbaseConstants.DOMAIN_EMAIL_SUFFIX;	
						try {
							screenName = getUniqueScreenName(surname, firstname, emailSuffix);
							log.info("screenname1 = " + screenName);
						} catch (SystemException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (PortalException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

					log.info(5);
					log.info(lpUser.getPassword());
					log.info(lpUser.getPasswordUnencrypted());
					log.info(lpUser.getPasswordEncrypted());
					log.info(lpUser.getReminderQueryQuestion());
					log.info(lpUser.getReminderQueryAnswer());
					
					
					lpUser.setScreenName(screenName);
					lpUser.setEmailAddress(email);
					lpUser.setGreeting("Welcome, " + firstname + "!");
					lpUser.setFirstName(firstname);
					lpUser.setMiddleName(middlename);
					lpUser.setLastName(surname);
					
					
					updatedUser =  UserLocalServiceUtil.updateUser(lpUser);
//					updatedUser = UserLocalServiceUtil.updateUser(existingUser.getUserId(), 
//							lpUser.getPassword(), lpUser.getPassword(), lpUser.getPassword(), false, 
//							lpUser.getReminderQueryQuestion(), lpUser.getReminderQueryAnswer(), 
//							screenName, email, 0, "", Locale.US.toString(), "", "Welcome, " + firstname + "!", "", 
//							firstname, middlename, surname, 
//							prefixId, suffixId, male, birthdayMonth, birthdayDay, birthdayYear, 
//							"", "", "", "", "", "", "", "", "", "", lpUser.getJobTitle(), lpUser.getGroupIds(), 
//							lpUser.getOrganizationIds(), lpUser.getRoleIds(), null, lpUser.getUserGroupIds(), 
//							serviceContext);

					if(sendEmail){
						//sendMail(firstname, surname, email);
						
						emailer.emailUpdateUserProfileAccount(lpUser.getEmailAddress(), firstname, surname, 
								portletState.getApplicationName().getValue() + " - Update on Bank Staff Profile Account", 
								portletState.getSystemUrl().getValue(), 
								portletState.getApplicationName().getValue());
						
							
							
					}
					if(sendSms){
						String message = "Hello, Your " + portletState.getApplicationName().getValue() + " Profile Account has been " +
								"successfully updated on " + systemUrl + ".";
						try{
						//new SendSms(createdUser.getMobileNumber(), message, "C_Portal");
							new SendSms(user.getMobileNumber(), message, 
									portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
						}catch(Exception e){
							log1.error("error sending sms ",e);
						}
					}
					log.info(6);
						
				}
				
			}else if(lpUser!=null && lpUser.getEmailAddress().equals(email))
			{
				log.info(100);
				log.info("User's Email Address has not been changed");
				log.info("Proceed with the update operation");
				String screenName = "";
				if(lpUser.getFirstName().equalsIgnoreCase(firstname) && lpUser.getLastName().equalsIgnoreCase(surname))
				{
					screenName = lpUser.getScreenName();
					log.info("screenname = " + screenName);
				}
				else
				{
					String emailSuffix = ProbaseConstants.DOMAIN_EMAIL_SUFFIX;	
					try {
						screenName = getUniqueScreenName(surname, firstname, emailSuffix);
						log.info("screenname1 = " + screenName);
					} catch (SystemException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (PortalException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				log.info(5);
				log.info(lpUser.getPassword());
				log.info(lpUser.getPasswordUnencrypted());
				log.info(lpUser.getPasswordEncrypted());
				log.info(lpUser.getReminderQueryQuestion());
				log.info(lpUser.getReminderQueryAnswer());
				
				
				lpUser.setScreenName(screenName);
				lpUser.setEmailAddress(email);
				lpUser.setGreeting("Welcome, " + firstname + "!");
				lpUser.setFirstName(firstname);
				lpUser.setMiddleName(middlename);
				lpUser.setLastName(surname);
				
				
				updatedUser =  UserLocalServiceUtil.updateUser(lpUser);

				if(sendEmail){
					//sendMail(firstname, surname, email);
					emailer.emailUpdateUserProfileAccount(lpUser.getEmailAddress(), firstname, surname, 
							portletState.getApplicationName().getValue() + " - Update on Bank Staff Profile Account", portletState.getSystemUrl().getValue(), portletState.getApplicationName().getValue());
				}
				if(sendSms){
					String message = "Hello, Your Online Account has been " +
							"successfully updated on " + systemUrl + ". Your new email address is " + lpUser.getEmailAddress();
					try{
					//new SendSms(createdUser.getMobileNumber(), message, "C_Portal");
							new SendSms(user.getMobileNumber(), message, 
									portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
					}catch(Exception e){
						log1.error("error sending sms ",e);
					}
				}
				log.info(6);
			}
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		sService.updateRecord(existingUser);
		return updatedUser;		
	}
	
	
	
	public static boolean addUserToCommmunity(long userId, long communityId) {
		boolean status = false;
		try {
			Logger logger = Logger.getLogger(UserManagementSystemAdminPortlet.class);
			long[] group = new long[1];
			group[0] = userId; // possible null if user is not

			logger.info("userId is " + userId + " community id is " + communityId);

			try { UserLocalServiceUtil.getUserById(userId);
			}catch (NoSuchUserException e){ logger.error("NoSuchUserException"); return status; }			

			if (!UserLocalServiceUtil.hasGroupUser(communityId, userId)) {
				UserLocalServiceUtil.addGroupUsers(communityId, group);
			}			
			status = UserLocalServiceUtil.hasGroupUser(communityId, userId);

		} catch (Exception e) { throw new HibernateException(e); }
		return status;
	}
	
	
	
	public static String getUniqueScreenName(String surname, String firstname, String emailSuffix) throws SystemException, PortalException {
		for (int i = 1; i < surname.length(); i++) {
			try {
				User user = UserLocalServiceUtil.getUserByScreenName(ProbaseConstants.COMPANY_ID, surname.substring(0, i) + firstname);
			} catch (NoSuchUserException e) {
				try {
					User user = UserLocalServiceUtil.getUserByEmailAddress(ProbaseConstants.COMPANY_ID, surname.substring(0, i) + firstname
							+ emailSuffix);
				} catch (NoSuchUserException e1) {
					return surname.substring(0, i) + firstname;
				}
			}
		}
		for (int i = 1;; i++) {
			try {
				User user = UserLocalServiceUtil.getUserByScreenName(ProbaseConstants.COMPANY_ID, surname + firstname + i);
			} catch (NoSuchUserException e) {
				try {
					User user = UserLocalServiceUtil.getUserByEmailAddress(ProbaseConstants.COMPANY_ID, surname + firstname + i
							+ emailSuffix);
				} catch (NoSuchUserException e1) {
					return surname + firstname + i;
				}
			}
		}
	}

	

	private void handleNavigations(ActionRequest aReq, ActionResponse aRes,
			UserManagementSystemAdminPortletState portletState) {
		// TODO Auto-generated method stub
		String action = aReq.getParameter("actionUrl");
		if(action.equalsIgnoreCase("createcorporateindividual"))
		{
			portletState.reinitializeForCreateCorporateIndividual(portletState);
			aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/register/stepone.jsp");
			portletState.setCurrentTab(VIEW_TABS.CREATE_A_PORTAL_USER);
		}
	}

	private void reinitializeForCreateCorporat1eIndividual(
			UserManagementSystemAdminPortletState portletState) {
		// TODO Auto-generated method stub
		
	}

	

	
	
	private boolean isPortalUserCreationDataValid(UserManagementSystemAdminPortletState portletState, 
			ActionRequest aReq, ActionResponse aRes, boolean editTrue)
	{
		// TODO Auto-generated method stub
		String errorMessage = null;
		
		if(portletState.getSelectedUserRoleId()!=null && !portletState.getSelectedUserRoleId().equals("-1"))
		{
			if(portletState.getportaluserfirstname()!=null && portletState.getportaluserfirstname().trim().length()>0)
			{
				if(portletState.getportaluserlastname()!=null && portletState.getportaluserlastname().trim().length()>0)
				{
					if(portletState.getportaluserfirstemail()!=null && portletState.getportaluserfirstemail().trim().length()>0)
					{
						if(portletState.getportaluserfirstmobile()!=null && portletState.getportaluserfirstmobile().trim().length()>0)
						{									
							PortalUser pu = null;
							if(editTrue)
							{
								if(portletState.getSelectedPortalUserId()!=null && portletState.getSelectedPortalUserId().length()>0)
								{
									pu = portletState.getUserManagementSystemAdminPortletUtil().getPortalUserByEmailAddressAndNotUserId(
										portletState.getportaluserfirstemail(), Long.valueOf(portletState.getSelectedPortalUserId()));
									if(pu!=null)
									{
										errorMessage =  "The email address provided has already been used on this platform. Provide another email address.";
									}									
								}else
								{
									errorMessage =  "Invalid User has been selected.";
								}
							}else
							{
								pu = portletState.getUserManagementSystemAdminPortletUtil().getPortalUserByEmailAddress(
										portletState.getportaluserfirstemail());
							}
							
							if(pu!=null)
							{
								errorMessage =  "The email address provided has already been used on this platform. Provide another email address.";
								
							}else
							{
								
							}
							
							if(portletState.getPortalUser()!=null && 
									(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_SUPER_ADMINISTRATOR) || 
											portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_SYSTEM_SUPER_ADMINISTRATOR)))
							{
								if(portletState.getUserCRUD()!=null && portletState.getCompanyCRUD()!=null)
								{
									
								}else
								{
									errorMessage =  "Specify User Priviledges for the User Profile you are creating";
								}
								
							}else
							{
								
							}
						}else
						{
							errorMessage =  "Provide a primary mobile number in the primary contact mobile number field";
						}
							
					}else
					{
						errorMessage =  "Provide a primary email address in the primary contact email address field";
					}
				}else
				{
					errorMessage =  "Provide last name in the last name field";
				}
			}else
			{
				errorMessage =  "Provide first name in the first name field";
			}
		}else
		{
			errorMessage =  "Select the type of user before proceeding";
		}
		
		if(errorMessage==null)
		{
			return true;
		}
		else
		{
			log.info("Error message = " + errorMessage);
			portletState.addError(aReq, errorMessage, portletState);
			return false;
		}
	}
}
