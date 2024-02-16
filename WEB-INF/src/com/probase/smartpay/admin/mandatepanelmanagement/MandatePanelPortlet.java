package com.probase.smartpay.admin.mandatepanelmanagement;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
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
import smartpay.entity.AuthorizePanel;
import smartpay.entity.AuthorizePanelCombination;
import smartpay.entity.BankBranches;
import smartpay.entity.Company;
import smartpay.entity.FeeDescription;
import smartpay.entity.FinancialAmountRestriction;
import smartpay.entity.PortalUser;
import smartpay.entity.PortalUserCRUDRights;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.enumerations.ActionTypeConstants;
import smartpay.entity.enumerations.CompanyStatusConstants;
import smartpay.entity.enumerations.PanelTypeConstants;
import smartpay.entity.enumerations.PaymentTypeConstants;
import smartpay.entity.enumerations.PortalUserStatusConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.service.SwpService;


import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.probase.smartpay.admin.mandatepanelmanagement.MandatePanelPortletState.AUTHORISATION_PANEL;
import com.probase.smartpay.admin.mandatepanelmanagement.MandatePanelPortletState.MANDATE_PANEL_VIEW;
import com.probase.smartpay.admin.mandatepanelmanagement.MandatePanelPortletState.NAVIGATE;
import com.probase.smartpay.admin.mandatepanelmanagement.MandatePanelPortletState.VIEW_TABS;
import com.probase.smartpay.commins.ComminsApplicationState;
import com.probase.smartpay.commins.Emailer;
import com.probase.smartpay.commins.Mailer;
import com.probase.smartpay.commins.ProbaseConstants;
import com.probase.smartpay.commins.SendSms;
import com.probase.smartpay.commins.Util;
import com.probase.smartpay.commins.Util.DETERMINE_ACCESS;
import com.sf.primepay.smartpay13.ServiceLocator;
import com.liferay.portal.DuplicateUserScreenNameException;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * Portlet implementation class MandatePanelPortlet
 */
public class MandatePanelPortlet extends MVCPortlet {
 
	private Logger log = Logger.getLogger(MandatePanelPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
	MandatePanelPortletUtil util = MandatePanelPortletUtil.getInstance();
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
		MandatePanelPortletState portletState = 
				MandatePanelPortletState.getInstance(renderRequest, renderResponse);

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
		
		MandatePanelPortletState portletState = MandatePanelPortletState.getInstance(aReq, aRes);
		
		
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
        
        if(action.equalsIgnoreCase(AUTHORISATION_PANEL.LOGIN_STEP_TWO.name()))
        {
        	if(loginStepTwo(aReq, aRes, portletState.getPortalUser(), portletState.getPortalUserCRUDRights(), 
        			swpService, portletState)==false)
        	{
        		portletState.addError(aReq, "Invalid login credentials!", portletState);
        		if(portletState.getCurrentTab().equals(VIEW_TABS.MAP_PANEL_TO_PORTAL_USER))
        		{
        			aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/addusertopanel.jsp");
        		}else if(portletState.getCurrentTab().equals(VIEW_TABS.USERS_MAPPED_TO_PANEL))
        		{
        			aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/authpanelmapping.jsp");
        		}
        	}else
        	{
        		aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/addusertopanel.jsp");
        	}
        }
        if(action.equalsIgnoreCase(MANDATE_PANEL_VIEW.MAP_PANEL_TO_PORTAL_USER.name()))
        {
        	mapPanelToPortalUser(aReq, aRes, portletState);
        }
        if(action.equalsIgnoreCase(MANDATE_PANEL_VIEW.VIEW_USERS_MAPPED_TO_PANEL.name()))
        {
        	mapPanelToPortalUser(aReq, aRes, portletState);
        }
        if(action.equalsIgnoreCase(AUTHORISATION_PANEL.PRE_VIEW_LISTING.name()))
        {
        	preAuthPanelListing(aReq, aRes, portletState);
        	
        }
        if(action.equalsIgnoreCase(AUTHORISATION_PANEL.PRE_CREATE_AUTH_PANEL.name()))
        {
        	if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
        	{
        		handlePreCreateNewPanel(aReq, aRes, portletState);
        	}else
        	{
        		portletState.addError(aReq, "You are not allowed to carry out this process. Contact Administrator for appropriate rights" +
        				"", portletState);
        	}
        }
        if(action.equalsIgnoreCase(AUTHORISATION_PANEL.CREATE_AUTH_PANEL.name()))
        {
        	handleCreateNewPanel(aReq, aRes, portletState);
        }
        if(action.equalsIgnoreCase(AUTHORISATION_PANEL.PRE_MAP_PANEL_TO_PORTAL_USER.name()))
        {
        	if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
        	{
        		handlePreMapPanelToUser(aReq, aRes, portletState);
        	}else
        	{
        		portletState.addError(aReq, "You are not allowed to carry out this process. Contact Administrator for appropriate rights" +
        				"", portletState);
        	}
        }
        if(action.equalsIgnoreCase(AUTHORISATION_PANEL.AUTH_PANEL_LISTING_ACTION.name()))
        {
        	handleAuthPanelListingAction(aReq, aRes, portletState);
        }
        if(action.equalsIgnoreCase(AUTHORISATION_PANEL.EDIT_AUTH_PANEL.name()))
        {
        	handleAuthPanelUpdate(aReq, aRes, portletState);
        }if(action.equalsIgnoreCase(AUTHORISATION_PANEL.MAP_PORTAL_USER_STEP_ONE.name()))
        {
        	handleMapPanelToPortalUserStepOne(aReq, aRes, portletState);
        }if(action.equalsIgnoreCase(AUTHORISATION_PANEL.MAP_PORTAL_USER_STEP_TWO.name()))
        {
        	handleMapPanelToPortalUserStepTwo(aReq, aRes, portletState);
        }
        if(action.equalsIgnoreCase(AUTHORISATION_PANEL.UPDATE_USER_PANEL_MAPPING.name()))
        {
        	
        	handleUnMapUserFromPanel(aReq, aRes, portletState);
        }
        if(action.equalsIgnoreCase(MANDATE_PANEL_VIEW.CREATE_A_MANDATE_PANEL.name()))
        {
        	portletState.setPanelName(null);
        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
        	{
        		aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/createapanel_stepone.jsp");
        	}else
        	{
        		aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/createapanel.jsp");
        		portletState.setAllAuthorizePanel(portletState.getMandatePanelPortletUtil().getAllAuthorizePanelsByCompanyId(true, portletState.getPortalUser().getCompany().getId()));
            	
        	}
        	portletState.setCurrentTab(VIEW_TABS.CREATE_A_MANDATE_PANEL);
        }if(action.equalsIgnoreCase(MANDATE_PANEL_VIEW.VIEW_MANDATE_PANEL_LISTINGS.name()))
        {
        	try
        	{
	        	
	        	if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
	        	{
	        		//portletState.setAllAuthorizePanel(portletState.getMandatePanelPortletUtil().getAllAuthorizePanels(true));
	        		portletState.setSelectedCompanyId(null);
	        		aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/preauthpanellisting.jsp");
	        		
	        	}else
	        	{
	        		if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
		        		portletState.setSelectedCompanyId(Long.toString(portletState.getPortalUser().getCompany().getId()));
		        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
		        		portletState.setSelectedCompanyId(aReq.getParameter("selectedCompanyId"));
		        		
		        	Long companyId = portletState.getSelectedCompanyId()==null ? null : Long.valueOf(portletState.getSelectedCompanyId());
		        	portletState.setAllAuthorizePanel(portletState.getMandatePanelPortletUtil().getAllAuthorizePanelsByCompanyId(true, companyId));
		        	aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/authpanellisting.jsp");
	        	}
	        	
	        	portletState.setCurrentTab(VIEW_TABS.VIEW_MANDATE_PANEL_LISTINGS);
        	}catch(NumberFormatException ex)
        	{
        		ex.printStackTrace();
        		portletState.addError(aReq, "Select a company before proceeding with this action", portletState);
        		if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
        			aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/createapanel.jsp");
	        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
	        		aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/createapanel_stepone.jsp");
	        	portletState.setCurrentTab(VIEW_TABS.CREATE_A_MANDATE_PANEL);
        	}
        }
		
	}

	private void handleUnMapUserFromPanel(ActionRequest aReq,
			ActionResponse aRes, MandatePanelPortletState portletState) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		String actiion_ = aReq.getParameter("selectedAuthPanelAction");
		String id = aReq.getParameter("selectedAuthPanel");
		try
		{
			Long idL = Long.valueOf(id);
			if(actiion_.equalsIgnoreCase("removeUser"))
			{
				AuthorizePanelCombination apc = (AuthorizePanelCombination)portletState.getMandatePanelPortletUtil().
						getEntityObjectById(AuthorizePanelCombination.class, idL);
				if(apc!=null)
				{
					try
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("panelName", apc.getAuthorizePanel().getPanelName());
						jsonObject.put("minAmt", Double.toString(apc.getAuthorizePanel().getFinancialAmountRestriction().getLowerLimitValue()));
						jsonObject.put("maxAmt", Double.toString(apc.getAuthorizePanel().getFinancialAmountRestriction().getUpperLimitValue()));
						jsonObject.put("companyName", apc.getAuthorizePanel().getCompany().getCompanyName());
						jsonObject.put("companyStaff", apc.getPortalUser().getFirstName() + " " + apc.getPortalUser().getLastName());
						jsonObject.put("requestBy", portletState.getPortalUser().getFirstName() + " " + portletState.getPortalUser().getLastName());
						
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.MANDATE_PANEL_UNMAP_USERS);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(apc.getId());
						aft.setEntityName(AuthorizePanelCombination.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						
						Collection<PortalUser> pus = portletState.getMandatePanelPortletUtil().getApprovingPortalUsers(
								portletState.getPortalUser().getRoleType().getRoleTypeName());
						
						
						for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
						{
							PortalUser pu1 = it.next();
							emailer.emailApprovalRequest(
									pu1.getFirstName(), 
									pu1.getLastName(), 
									pu1.getEmailAddress(), 
									portletState.getSystemUrl().getValue(), 
									"eTax - Approval Request for the " +
									"Update of a Mandate-Panel/User Mapping", portletState.getApplicationName().getValue());
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
						
						portletState.addSuccess(aReq, "Request to remove this user sent successfully", portletState);
					}catch(JSONException e)
					{
						e.printStackTrace();
						portletState.addError(aReq, "Issues were experienced sending a request to remove this user. Please try again", portletState);
					}
					
				}else
				{
					portletState.addError(aReq, "Invalid Mandate Panel to User Mapping selected. Please try again", portletState);
				}
			}else if(actiion_.equalsIgnoreCase("goback"))
			{
				aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/preauthpanellisting.jsp");
			}
		}catch(NumberFormatException e)
		{
			e.printStackTrace();
		}
		
		
		
	}

	private void preAuthPanelListing(ActionRequest aReq, ActionResponse aRes,
			MandatePanelPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setSelectedCompanyId(aReq.getParameter("companySelected"));
		if(portletState.getSelectedCompanyId().equals("-1"))
		{
			portletState.addError(aReq, "Select a company to proceed", portletState);
			aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/preauthpanellisting.jsp");
		}else
		{
			//portletState.setAllAuthorizePanel(portletState.getMandatePanelPortletUtil().getAllAuthorizePanelsByCompanyId(true, 
				//	Long.valueOf(portletState.getSelectedCompanyId())));

			portletState.setAllAuthorizePanel(portletState.getMandatePanelPortletUtil().getAllAuthorizePanelsByCompanyId(
					true, Long.valueOf(portletState.getSelectedCompanyId())));
			portletState.setAllFinancialRestrictionsListing(
					portletState.getMandatePanelPortletUtil().getFinancialAmountRestrictionsByCompanyId(Long.valueOf(portletState.getSelectedCompanyId())));
			aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/authpanellisting.jsp");
		}
	}

	private void handlePreCreateNewPanel(ActionRequest aReq,
			ActionResponse aRes, MandatePanelPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setSelectedCompanyId(aReq.getParameter("companySelected"));
		if(portletState.getSelectedCompanyId().equals("-1"))
		{
			portletState.addError(aReq, "Select a company to proceed", portletState);
			aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/createapanel_stepone.jsp");
		}else
		{
			//portletState.setAllAuthorizePanel(portletState.getMandatePanelPortletUtil().getAllAuthorizePanelsByCompanyId(true, 
				//	Long.valueOf(portletState.getSelectedCompanyId())));
			Collection<FinancialAmountRestriction> farListing = portletState.getMandatePanelPortletUtil().getFinancialAmountRestrictionsByCompanyId(Long.valueOf(portletState.getSelectedCompanyId()));
			if(farListing!=null && farListing.size()>0)
			{
				portletState.setAllFinancialRestrictionsListing(farListing);
				portletState.setAllAuthorizePanel(portletState.getMandatePanelPortletUtil().getAllAuthorizePanelsByCompanyId(
						true, Long.valueOf(portletState.getSelectedCompanyId())));
				aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/createapanel.jsp");
			}else
			{
				portletState.setAllFinancialRestrictionsListing(null);
				portletState.addError(aReq, "You do not have any Financial Restrictions Created for the selected company. " +
						"Create one before you can create an Authorisation Mandate Panel.", portletState);
				aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/createapanel_stepone.jsp");
			}
		}
	}
	
	
	private void handlePreMapPanelToUser(ActionRequest aReq,
			ActionResponse aRes, MandatePanelPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setSelectedCompanyId(aReq.getParameter("companySelected"));
		if(portletState.getSelectedCompanyId().equals("-1"))
		{
			portletState.addError(aReq, "Select a company to proceed", portletState);
			aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/addusertopanel.jsp");
		}else
		{
			//portletState.setAllAuthorizePanel(portletState.getMandatePanelPortletUtil().getAllAuthorizePanelsByCompanyId(true, 
				//	Long.valueOf(portletState.getSelectedCompanyId())));
			try
			{
				portletState.setAllCompanyPersonnel(
						portletState.getMandatePanelPortletUtil().getAllPortalUserByCompanyId(Long.valueOf(portletState.getSelectedCompanyId())));
				portletState.setAllAuthorizePanel(portletState.getMandatePanelPortletUtil().getAllAuthorizePanelsByCompanyId(
						true, 
						Long.valueOf(portletState.getSelectedCompanyId())));
				if(portletState.getAllAuthorizePanel()!=null && portletState.getAllAuthorizePanel().size()>0)
				{
					aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/addusertopanelstepone.jsp");
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/addusertopanel.jsp");
					portletState.addError(aReq, "There are no authorization panels created for the selected company. Create authorization panels first " +
							"before proceeding to map authorization panels to users", portletState);
				}
			}catch(NumberFormatException e)
			{
				portletState.addError(aReq, "Select a valid company to proceed", portletState);
				aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/addusertopanel.jsp");
			}
		}
	}

	private void mapPanelToPortalUser(ActionRequest aReq, ActionResponse aRes,
			MandatePanelPortletState portletState) {
		// TODO Auto-generated method stub
		
		if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
		{
			portletState.setAllAuthorizePanel(portletState.getMandatePanelPortletUtil().getAllAuthorizePanelsByCompanyId(true, portletState.getPortalUser().getCompany().getId()));
			portletState.setAllCompanyPersonnel(portletState.getMandatePanelPortletUtil().getAllPortalUserByCompanyId(portletState.getPortalUser().getCompany().getId()));
			aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/addusertopanelstepone.jsp");
		}
		else if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
		{
			aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/addusertopanel.jsp");
		}
		portletState.setCurrentTab(VIEW_TABS.MAP_PANEL_TO_PORTAL_USER);
	}
	
	
	private boolean loginStepTwo(ActionRequest aReq, ActionResponse aRes,
			PortalUser currentPortalUser, PortalUserCRUDRights portalUserCRUDRights, SwpService sService, MandatePanelPortletState portletState) {
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

	

	private void handleMapPanelToPortalUserStepTwo(ActionRequest aReq,
			ActionResponse aRes, MandatePanelPortletState portletState) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		portletState.setSelectedMapPanelPortalUser(aReq.getParameter("personnel"));
		portletState.setSelectedMapPosition(aReq.getParameter("processposition"));
		log.info(90);
		
		ComminsApplicationState cappState = portletState.getCas();
		Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
		Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
		DETERMINE_ACCESS determinAccess = new Util().determineAccessForCompanyFxns(twoStep, approvalProcess, cappState, portletState.getCompanyCRUDRights());
		log.info(91);
		if(isMapPanelToPortalUserDataValid(portletState, aReq, aRes, true))
		{
			log.info(92);
			if(determinAccess!=null && determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
			{
				log.info(93);
				try
				{
					log.info(94);
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("selectedMapPosition", portletState.getSelectedMapPosition());
					jsonObject.put("selectedMapPanel", portletState.getSelectedMapPanel());
					jsonObject.put("selectedMapPanelPortalUser", portletState.getSelectedMapPanelPortalUser());
					
					AuthorizePanel ap = (AuthorizePanel)portletState.getMandatePanelPortletUtil().getEntityObjectById(AuthorizePanel.class, Long.valueOf(portletState.getSelectedMapPanel()));
					PortalUser pu = (PortalUser)portletState.getMandatePanelPortletUtil().getEntityObjectById(PortalUser.class, Long.valueOf(portletState.getSelectedMapPanelPortalUser()));
					
					jsonObject.put("panelName", ap.getPanelName());
					jsonObject.put("minAmt", Double.toString(ap.getFinancialAmountRestriction().getLowerLimitValue()));
					jsonObject.put("maxAmt", Double.toString(ap.getFinancialAmountRestriction().getUpperLimitValue()));
					jsonObject.put("companyName", ap.getCompany().getCompanyName());
					jsonObject.put("companyStaff", pu.getFirstName() + " " + pu.getLastName());
					jsonObject.put("requestBy", portletState.getPortalUser().getFirstName() + " " + portletState.getPortalUser().getLastName());
					
					
					log.info(95);
					log.info(jsonObject.toString());
					ApprovalFlowTransit aft = new ApprovalFlowTransit();
					aft.setActionType(ActionTypeConstants.MANDATE_PANEL_MAP_USERS);
					aft.setEntityId(null);
					aft.setEntityName(AuthorizePanelCombination.class.getSimpleName());
					aft.setObjectData(jsonObject.toString());
					aft.setPortalUser(portletState.getPortalUser());
					aft.setWorkerId(null);
					swpService.createNewRecord(aft);
					
					Collection<PortalUser> pus = portletState.getMandatePanelPortletUtil().getApprovingPortalUsers(
							portletState.getPortalUser().getRoleType().getRoleTypeName());
					
					
					for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
					{
						PortalUser pu1 = it.next();
						emailer.emailApprovalRequest(
								pu1.getFirstName(), 
								pu1.getLastName(), 
								pu1.getEmailAddress(), 
								portletState.getSystemUrl().getValue(), 
								"eTax - Approval Request for the " +
								"Update of a Mandate-Panel/User Mapping", portletState.getApplicationName().getValue());
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
					portletState.addSuccess(aReq, "Request to map users to mandate panels was successful. This request will need to be approved by an approving officer", portletState);
				}catch(JSONException e)
				{
					log.info(96);
					e.printStackTrace();
					portletState.addError(aReq, "Request to map users to mandate panels was not successful. Please try again", portletState);
				}
				
			}else if(determinAccess!=null && determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
			{
				log.info(97);
				AuthorizePanelCombination apc = new AuthorizePanelCombination();
				apc.setAuthorizePanel((AuthorizePanel)portletState.getMandatePanelPortletUtil().getEntityObjectById(AuthorizePanel.class, Long.valueOf(portletState.getSelectedMapPanel())));
				apc.setDateCreated(new Timestamp((new Date()).getTime()));
				apc.setPortalUser((PortalUser)portletState.getMandatePanelPortletUtil().getEntityObjectById(PortalUser.class, Long.valueOf(portletState.getSelectedMapPanelPortalUser())));
				apc.setStatus(SmartPayConstants.AUTHORIZE_PANEL_COMBINATION_STATUS_ACTIVE);
				apc.setPosition(Integer.valueOf(portletState.getSelectedMapPosition()));
				
				apc = (AuthorizePanelCombination)swpService.createNewRecord(apc);

				handleAudit("Authorize Panel Mapping To Portal User", Long.toString(apc.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/authpanellisting.jsp");
				portletState.addSuccess(aReq, "Authorize Panel Mapping to Selected Company Personnel was successfully!", portletState);
				portletState.reinitializeForMapPanelToPortalUser(portletState);
				portletState.setAllBankBranchListing(portletState.getMandatePanelPortletUtil().getAllBankBranchListing());
	        	portletState.setCurrentTab(VIEW_TABS.VIEW_MANDATE_PANEL_LISTINGS);
	        	log.info(98);
			}
			else
			{
				portletState.addError(aReq, "You do not have appropriate rights to carry out this action", portletState);
			}
			log.info(99);
			
			
		}else
		{
			log.info(100);
			aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/addusertopanelsteptwo.jsp");
			portletState.setCurrentTab(VIEW_TABS.MAP_PANEL_TO_PORTAL_USER);
		}
	}
	
	
	private void handleMapPanelToPortalUserStepOne(ActionRequest aReq,
			ActionResponse aRes, MandatePanelPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setSelectedMapPanel(aReq.getParameter("mandatepanel"));
		if(aReq.getParameter("mandatepanel")!=null && !aReq.getParameter("mandatepanel").equals("-1"))
		{
			if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
			{
				portletState.setPersonnelPositionList(portletState.getMandatePanelPortletUtil().getAuthorizedPanelCombinationByCompanyAndMapPanel(
						portletState.getPortalUser().getCompany().getId(),
					Long.valueOf(portletState.getSelectedMapPanel())));
			}else
			{
				portletState.setPersonnelPositionList(portletState.getMandatePanelPortletUtil().getAuthorizedPanelCombinationByCompanyAndMapPanel(
						Long.valueOf(portletState.getSelectedCompanyId()),
						Long.valueOf(portletState.getSelectedMapPanel())));
			}
			aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/addusertopanelsteptwo.jsp");
		}else
		{
			portletState.addError(aReq, "Please a select valid mandate panel", portletState);
			aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/addusertopanelstepone.jsp");
			portletState.setCurrentTab(VIEW_TABS.MAP_PANEL_TO_PORTAL_USER);
		}
	}

	private boolean isMapPanelToPortalUserDataValid(
			MandatePanelPortletState portletState, ActionRequest aReq,
			ActionResponse aRes, boolean b) {
		// TODO Auto-generated method stub
		String errorMessage = null;
		try
		{
			if(!portletState.getSelectedMapPanelPortalUser().equals("-1"))
			{
				
					if(!portletState.getSelectedMapPosition().equals("-1"))
					{
						Collection<AuthorizePanelCombination> apcList = portletState.getMandatePanelPortletUtil().getPanelMappingToPortalUser(
								Long.valueOf(portletState.getSelectedMapPanelPortalUser()), 
								Long.valueOf(portletState.getSelectedMapPanel()), 
								portletState.getSelectedMapPanelPortalUserCombination()==null ? null : Long.valueOf(portletState.getSelectedMapPanelPortalUserCombination()), 
								b);
						if(apcList!=null && apcList.size()>0)
						{
							if(b==true)
							{
								errorMessage = "You have already mapped this personnel to this mandate panel";
							}else
							{
								errorMessage = "You have already mapped this personnel to the selected panel";
							}
						}else
						{
							
						}
					}else
					{
						errorMessage = "Please select a valid panel position";
					}
				
			}else
			{
				errorMessage = "Please select a company personnel";
			}
		}catch(NumberFormatException e)
		{
			errorMessage = "Please select valid panel position, mandate panel and company personnel";
		}
		
		if(errorMessage==null)
		{
			log.info("Error message: == null");
			return true;
		}
		else
		{
			log.info("Error message: ==" + errorMessage);
			portletState.addError(aReq, errorMessage, portletState);
			return false;
		}
	}

	

	private void handleAuthPanelUpdate(ActionRequest aReq, ActionResponse aRes,
			MandatePanelPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setPanelName(aReq.getParameter("panelname"));
		portletState.setSelectedFinancialAmountRestriction(aReq.getParameter("financialAmountRestriction"));
		portletState.setSelectedPanelType(aReq.getParameter("panelType"));
		if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
			portletState.setSelectedCompanyId(Long.toString(portletState.getPortalUser().getCompany().getId()));
		
		
		try
		{
			
			if(isPanelDataValid(portletState, aReq, aRes, false))
			{
				AuthorizePanel ap = (AuthorizePanel)portletState.getMandatePanelPortletUtil().getEntityObjectById(AuthorizePanel.class, portletState.getSelectedAuthorizationPanelId());
				if(ap!=null)
				{
					ap.setPanelName(portletState.getPanelName());
					
					swpService.updateRecord(ap);
					handleAudit("Authorize Panel Update", Long.toString(ap.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
					portletState.addSuccess(aReq, "Authorization Panel - " + ap.getPanelName() + " - was updated successfully!", portletState);
					portletState.setPanelName(null);
					try
					{
						if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
			        		portletState.setSelectedCompanyId(Long.toString(portletState.getPortalUser().getCompany().getId()));
			        		
			        	Long companyId = portletState.getSelectedCompanyId()==null ? null : Long.valueOf(portletState.getSelectedCompanyId());
			        	if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
			        	{
			        		portletState.setAllAuthorizePanel(portletState.getMandatePanelPortletUtil().getAllAuthorizePanels(true));
			        	}else
			        	{
			        		portletState.setAllAuthorizePanel(portletState.getMandatePanelPortletUtil().getAllAuthorizePanelsByCompanyId(true, companyId));
			        	}
			        	aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/authpanellisting.jsp");
			        	portletState.setCurrentTab(VIEW_TABS.VIEW_MANDATE_PANEL_LISTINGS);
					}catch(NumberFormatException ex)
					{
						ex.printStackTrace();
		        		portletState.addError(aReq, "Select a company before proceeding with this action", portletState);
		        		if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
		        			aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/createapanel.jsp");
			        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
			        		aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/createapanel_stepone.jsp");
			        	portletState.setCurrentTab(VIEW_TABS.CREATE_A_MANDATE_PANEL);
					}
				}else
				{
					portletState.addError(aReq, "The authorization panels' details were not saved! Please select a valid authorisation Panel before editing it.", portletState);
					aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/authpanellisting.jsp");
				}
				
			}else
			{
				aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/editauthpanel.jsp");
			}
		}catch(NumberFormatException e)
		{
			portletState.addError(aReq, "Select a valid Authorisation panel to edit.", portletState);
			aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/authpanellisting.jsp");
		}
	}
	
	
	
	private void handleCreateNewPanel(ActionRequest aReq,
			ActionResponse aRes, MandatePanelPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setPanelName(aReq.getParameter("panelname"));
		portletState.setSelectedFinancialAmountRestriction(aReq.getParameter("financialAmountRestriction"));
		portletState.setSelectedPanelType(aReq.getParameter("panelType"));
		
		if(portletState.getPortalUser().getRoleType().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
		{
			portletState.setSelectedCompanyId(Long.toString(portletState.getPortalUser().getCompany().getId()));
		}
		
		if(isPanelDataValid(portletState, aReq, aRes, true))
		{
			AuthorizePanel ap = new AuthorizePanel();
			ap.setPanelName(portletState.getPanelName());
			ap.setDateGenerated(new Timestamp((new Date()).getTime()));
			ap.setGeneratedByPortalUserId(portletState.getPortalUser().getId());
			ap.setCompany(portletState.getMandatePanelPortletUtil().getCompanyById(Long.valueOf(portletState.getSelectedCompanyId())));
			ap.setFinancialAmountRestriction((FinancialAmountRestriction)portletState.getMandatePanelPortletUtil().
					getEntityObjectById(FinancialAmountRestriction.class, Long.valueOf(portletState.getSelectedFinancialAmountRestriction())));
			ap.setAuthorizeType(PanelTypeConstants.fromString(portletState.getSelectedPanelType()));
			ap.setStatus(true);
			
			ap = (AuthorizePanel)swpService.createNewRecord(ap);
			handleAudit("Authorisation Panel Creation", Long.toString(ap.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
			aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/authpanellisting.jsp");
			portletState.addSuccess(aReq, "Authorisation Panel - " + ap.getPanelName() + " - was created successfully!", portletState);
			portletState.setPanelName(null);
			try
			{
				if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
	        		portletState.setSelectedCompanyId(Long.toString(portletState.getPortalUser().getCompany().getId()));
	        	
	        		
	        	Long companyId = portletState.getSelectedCompanyId()==null ? null : Long.valueOf(portletState.getSelectedCompanyId());
	        	if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
	        	{
	        		portletState.setAllAuthorizePanel(portletState.getMandatePanelPortletUtil().getAllAuthorizePanels(true));
	        	}else
	        	{
	        		portletState.setAllAuthorizePanel(portletState.getMandatePanelPortletUtil().getAllAuthorizePanelsByCompanyId(true, companyId));
	        	}
	        	aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/authpanellisting.jsp");
	        	portletState.setCurrentTab(VIEW_TABS.VIEW_MANDATE_PANEL_LISTINGS);
			}catch(NumberFormatException ec)
			{
				ec.printStackTrace();
				aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/createapanel.jsp");
	        	portletState.setCurrentTab(VIEW_TABS.CREATE_A_MANDATE_PANEL);
			}
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/createapanel.jsp");
		}
	}
	
	
	
	private boolean isPanelDataValid(MandatePanelPortletState portletState,
			ActionRequest aReq, ActionResponse aRes, boolean checkExistingForNew) {
		// TODO Auto-generated method stub
		String errorMessage = null;
		
		try
		{
			if(portletState.getPanelName()!=null && portletState.getPanelName().trim().length()>0)
			{
				portletState.setSelectedFinancialAmountRestriction(aReq.getParameter("financialAmountRestriction"));
				portletState.setSelectedPanelType(aReq.getParameter("panelType"));
				if(portletState.getSelectedFinancialAmountRestriction()!=null && !portletState.getSelectedFinancialAmountRestriction().trim().equals("-1"))
				{
					if(portletState.getSelectedPanelType()!=null && !portletState.getSelectedPanelType().equals("-1"))
					{
						AuthorizePanel ap = new AuthorizePanel();
						if(checkExistingForNew)
						{
							ap = portletState.getMandatePanelPortletUtil().getAuthorizePanelByNameAndCompanyId(
									portletState.getPanelName(), Long.valueOf(portletState.getSelectedCompanyId()));
						}else
						{
							ap = portletState.getMandatePanelPortletUtil().getAuthorizePanelByNameAndCompanyIdForEdit(
									portletState.getPanelName(), Long.valueOf(portletState.getSelectedCompanyId()),Long.valueOf(portletState.getSelectedAuthorizationPanelId()));
						}
						
						
						if(ap==null)
						{
							
						}else
						{
							errorMessage =  "The bank branch name and bank code provided already exist on the system. This bank branch already has been created.";
						}
					}else
					{
						errorMessage =  "You have not selected a mandate panel type. Select one before proceeding";
					}
				}else
				{
					errorMessage =  "You have not selected a mandate panel type. Select one before proceeding";
				}
				
			}else
			{
				errorMessage =  "You have not selected a financial amount restriction. Select one before proceeding";
			}
		}catch(NumberFormatException e)
		{
			errorMessage = "Invalid processing. Please start afresh.";
		}
		
		if(errorMessage==null)
		{
			return true;
		}
		else
		{
			portletState.addError(aReq, errorMessage, portletState);
			return false;
		}
	}

	private void handleNavigations(ActionRequest aReq, ActionResponse aRes,
			MandatePanelPortletState portletState) {
		// TODO Auto-generated method stub
		String action = aReq.getParameter("actionUrl");
		if(action.equalsIgnoreCase("createauthpanel"))
		{
			portletState.setPanelName(null);
			aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/createapanel.jsp");
			portletState.setCurrentTab(VIEW_TABS.CREATE_A_MANDATE_PANEL);
		}else if(action.equalsIgnoreCase("authpanellistings"))
		{
			try
			{
				if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
	        		portletState.setSelectedCompanyId(Long.toString(portletState.getPortalUser().getCompany().getId()));
	        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
	        		portletState.setSelectedCompanyId(aReq.getParameter("selectedCompanyId"));
	        		
	        	Long companyId = portletState.getSelectedCompanyId()==null ? null : Long.valueOf(portletState.getSelectedCompanyId());
	        	if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
	        	{
	        		portletState.setAllAuthorizePanel(portletState.getMandatePanelPortletUtil().getAllAuthorizePanels(true));
	        	}else
	        	{
	        		portletState.setAllAuthorizePanel(portletState.getMandatePanelPortletUtil().getAllAuthorizePanelsByCompanyId(true, companyId));
	        	}
	        	aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/authpanellisting.jsp");
	        	portletState.setCurrentTab(VIEW_TABS.VIEW_MANDATE_PANEL_LISTINGS);
			}catch(NumberFormatException ec)
			{
				ec.printStackTrace();
				aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/createapanel.jsp");
	        	portletState.setCurrentTab(VIEW_TABS.CREATE_A_MANDATE_PANEL);
			}
		}
	}

	private void reinitializeForCreateCorporat1eIndividual(
			MandatePanelPortletState portletState) {
		// TODO Auto-generated method stub
		
	}

	

	
	private void handleAuthPanelListingAction(ActionRequest aReq,
			ActionResponse aRes, MandatePanelPortletState portletState) {
		// TODO Auto-generated method stub
		
		if(aReq.getParameter("selectedAuthPanelAction")!=null && aReq.getParameter("selectedAuthPanelAction").equalsIgnoreCase("goback"))
		{
			aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/preauthpanellisting.jsp");
		}
		else
		{
			String authPanelId = aReq.getParameter("selectedAuthPanel").trim();
			log.info("authPanelId ==" + authPanelId);
			try
			{
				Long authPanelIdL = Long.valueOf(authPanelId);
				AuthorizePanel ap = (AuthorizePanel)portletState.getMandatePanelPortletUtil().getEntityObjectById(AuthorizePanel.class, authPanelIdL);
				portletState.setSelectedAuthorizationPanelId(authPanelIdL);
				portletState.setSelectedCompanyId(Long.toString(ap.getCompany().getId()));
				
				if(ap!=null)
				{
					if(aReq.getParameter("selectedAuthPanelAction")!=null && aReq.getParameter("selectedAuthPanelAction").equalsIgnoreCase("update"))
					{
						portletState.setPanelName(ap.getPanelName());
						portletState.setPanelType(ap.getAuthorizeType().getValue());
						if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
			        		portletState.setSelectedCompanyId(Long.toString(portletState.getPortalUser().getCompany().getId()));
			        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
			        		portletState.setSelectedCompanyId(Long.toString(ap.getCompany().getId()));
			        	portletState.setSelectedFinancialAmountRestriction(Long.toString(ap.getFinancialAmountRestriction().getId()));
						aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/editauthpanel.jsp");
					
					}
					else if(aReq.getParameter("selectedAuthPanelAction")!=null && aReq.getParameter("selectedAuthPanelAction").equalsIgnoreCase("viewusersmapped"))
					{
						portletState.setAllAuthorizePanelCombination(portletState.getMandatePanelPortletUtil().getAuthorizedPanelCombinationByAuthorizePanelId(authPanelIdL));
						portletState.setPanelName(ap.getPanelName());
						portletState.setPanelType(ap.getAuthorizeType().getValue());
						if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
			        		portletState.setSelectedCompanyId(Long.toString(portletState.getPortalUser().getCompany().getId()));
			        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
			        		portletState.setSelectedCompanyId(Long.toString(ap.getCompany().getId()));
						aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/authpanelmapping.jsp");
					
					}else if(aReq.getParameter("selectedAuthPanelAction")!=null && aReq.getParameter("selectedAuthPanelAction").equalsIgnoreCase("delete"))
					{
						ap.setStatus(Boolean.FALSE);
						swpService.updateRecord(ap);
						handleAudit("Authorize Panel Delete", Long.toString(ap.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
						aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/authpanellisting.jsp");
						try
						{
							if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
				        		portletState.setSelectedCompanyId(Long.toString(portletState.getPortalUser().getCompany().getId()));
				        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
				        		portletState.setSelectedCompanyId(Long.toString(ap.getCompany().getId()));
				        		
				        	Long companyId = portletState.getSelectedCompanyId()==null ? null : Long.valueOf(portletState.getSelectedCompanyId());
				        	if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
				        	{
				        		portletState.setAllAuthorizePanel(portletState.getMandatePanelPortletUtil().getAllAuthorizePanels(true));
				        	}else
				        	{
				        		portletState.setAllAuthorizePanel(portletState.getMandatePanelPortletUtil().getAllAuthorizePanelsByCompanyId(true, companyId));
				        	}
				        	aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/authpanellisting.jsp");
				        	portletState.setCurrentTab(VIEW_TABS.VIEW_MANDATE_PANEL_LISTINGS);
						}catch(NumberFormatException ec)
						{
							ec.printStackTrace();
							aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/createapanel.jsp");
				        	portletState.setCurrentTab(VIEW_TABS.CREATE_A_MANDATE_PANEL);
						}
						portletState.addSuccess(aReq, "Selected Authorisation Panel - " + ap.getPanelName() + " - has been deleted successfully.", portletState);
					}
				}else
				{
					portletState.addError(aReq, "This action can not be carried out on the selected authorisation panel. You seem to have not selected a valid authorisation panel. Select one before proceeding", portletState);
					aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/authpanellisting.jsp");
				}
			}catch(NumberFormatException e)
			{
				e.printStackTrace();
				portletState.addError(aReq, "This action can not be carried out on the selected authorisation panel. Select a valid authorisation panel before proceeding", portletState);
				aRes.setRenderParameter("jspPage", "/html/mandatepanelportlet/mandatepanel/authpanellisting.jsp");
			}
		}
		
	}
	
	
	
	
	private void handleAudit(String action, String activity, Timestamp timestamp, String ipAddress, Long userId) {
		// TODO Auto-generated method stub
		AuditTrail ad = new AuditTrail();
		try
		{
			ad.setAction(action);
			ad.setActivity(activity);
			ad.setDate(timestamp);
			ad.setIpAddress(ipAddress);
			ad.setUserId(Long.toString(userId));
			this.swpService.createNewRecord(ad);
		}catch(NullPointerException e)
		{
			e.printStackTrace();
		}
		
	}


}
