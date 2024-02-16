package com.probase.smartpay.approvalflow;

import java.io.IOException;
import java.security.PrivilegedActionException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import smartpay.audittrail.AuditTrail;
import smartpay.entity.ApprovalFlowTransit;
import smartpay.entity.Assessment;
import smartpay.entity.AuthorizePanel;
import smartpay.entity.AuthorizePanelCombination;
import smartpay.entity.BankBranches;
import smartpay.entity.Company;
import smartpay.entity.CompanyCRUDRights;
import smartpay.entity.CompanyFeeDescription;
import smartpay.entity.FeeDescription;
import smartpay.entity.PaymentHistory;
import smartpay.entity.PortalUser;
import smartpay.entity.PortalUserCRUDRights;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.TaxType;
import smartpay.entity.TaxTypeAccount;
import smartpay.entity.Tokens;
import smartpay.entity.TpinInfo;
import smartpay.entity.WorkFlow;
import smartpay.entity.enumerations.ActionTypeConstants;
import smartpay.entity.enumerations.CompanyStatusConstants;
import smartpay.entity.enumerations.CompanyTypeConstants;
import smartpay.entity.enumerations.PanelTypeConstants;
import smartpay.entity.enumerations.PaymentHistoryConstants;
import smartpay.entity.enumerations.PaymentTypeConstants;
import smartpay.entity.enumerations.PortalUserStatusConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.entity.enumerations.WorkFlowConstants;
import smartpay.service.SwpService;

import com.liferay.portal.DuplicateUserScreenNameException;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.probase.smartpay.admin.companymanagement.CompanyManagementPortletState;
import com.probase.smartpay.admin.feedescriptionmanagement.FeeDescriptionPortletState.FEE_DESCRIPTION_APPROVAL_TYPE;
import com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortlet;
import com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortletState;
import com.probase.smartpay.approvalflow.ApprovalFlowPortletState.APPROVAL_FLOW_ACTIONS;
import com.probase.smartpay.commins.ComminsApplicationState;
import com.probase.smartpay.commins.Emailer;
import com.probase.smartpay.commins.Mailer;
import com.probase.smartpay.commins.ProbaseConstants;
import com.probase.smartpay.commins.SendMail;
import com.probase.smartpay.commins.SendSms;
import com.probase.smartpay.commins.TaxBreakDownResponse;
import com.probase.smartpay.commins.Util;
import com.probase.smartpay.commins.Util.DETERMINE_ACCESS;
import com.probase.smartpay.approvalflow.ApprovalFlowPortlet;
import com.probase.smartpay.approvalflow.ApprovalFlowPortletState;
import com.probase.smartpay.approvalflow.ApprovalFlowPortletState.APPROVAL_TAB_ACTION;
import com.probase.smartpay.approvalflow.ApprovalFlowPortletState.VIEW_TABS;
import com.probase.smartpay.approvalflow.ApprovalFlowPortletUtil;
import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Portlet implementation class ApprovalFlowPortlet
 */
public class ApprovalFlowPortlet extends MVCPortlet {

	
	private Logger log = Logger.getLogger(ApprovalFlowPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
	ApprovalFlowPortletUtil util = ApprovalFlowPortletUtil.getInstance();
	ServiceContext serviceContext = new ServiceContext();
	
	
	@Override
	public void init(PortletConfig config) throws PortletException {
		// TODO Auto-generated method stub
		log.info("ApprovalFlow portlet init called...");		
		pContext = config.getPortletContext();
		super.init(config);
		pConfig = config;
	    this.swpService = this.serviceLocator.getSwpService();
	    Calendar cal = Calendar.getInstance();
	    cal.get(Calendar.YEAR);
	    
	}
	
	@Override
	public void render(RenderRequest renderRequest,
			RenderResponse renderResponse) throws PortletException, IOException {
		log.info("WorkFlow render called...");	
		PortletSession ps = renderRequest.getPortletSession();
		ApprovalFlowPortletState portletState = 
				ApprovalFlowPortletState.getInstance(renderRequest, renderResponse);

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
		
		ApprovalFlowPortletState portletState = ApprovalFlowPortletState.getInstance(aReq, aRes);
		
		
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
        
        if(action.equalsIgnoreCase(APPROVAL_FLOW_ACTIONS.SELECT_APPROVAL_ENTITY.name()))
        {
        	log.info("handleSelectApprovalEntity");
        	handleSelectApprovalEntity(aReq, aRes, portletState);
        }if(action.equalsIgnoreCase(APPROVAL_FLOW_ACTIONS.HANDLE_APPROVAL_LISTINGS.name()))
        {
        	handleWorkFlowListingsAction(aReq, aRes, portletState);
        }
        if(action.equalsIgnoreCase(APPROVAL_FLOW_ACTIONS.LOGIN_STEP_TWO.name()))
        {
        	log.info("We are inside step two of login");
        	if(loginStepTwo(aReq, aRes, portletState.getPortalUser(), portletState.getPortalUserCRUDRights(), 
        			swpService, portletState)==false)
        		portletState.addError(aReq, "Invalid login credentials!", portletState);
        }
        if(action.equalsIgnoreCase(APPROVAL_TAB_ACTION.HANDLE_CREATE_NEW_USER.name()))
        {
        	getApprovingItemsByType(portletState, aReq, aRes, ActionTypeConstants.PORTAL_USER_ACTION_CREATE, VIEW_TABS.VNEW_USER);
        	
        }
        if(action.equalsIgnoreCase(APPROVAL_TAB_ACTION.HANDLE_BLOCK_NEW_USER.name()))
        {
        	getApprovingItemsByType(portletState, aReq, aRes, ActionTypeConstants.PORTAL_USER_ACTION_BLOCK, VIEW_TABS.VBLOCK_USER);
        	
        }
        if(action.equalsIgnoreCase(APPROVAL_TAB_ACTION.HANDLE_UNBLOCK_NEW_USER.name()))
        {
        	getApprovingItemsByType(portletState, aReq, aRes, ActionTypeConstants.PORTAL_USER_ACTION_UNBLOCK, VIEW_TABS.VUNBLOCK_USER);
        }
        if(action.equalsIgnoreCase(APPROVAL_TAB_ACTION.HANDLE_DELETE_NEW_USER.name()))
        {
        	getApprovingItemsByType(portletState, aReq, aRes, ActionTypeConstants.PORTAL_USER_ACTION_DELETE, VIEW_TABS.VDELETE_USER);
        }
        if(action.equalsIgnoreCase(APPROVAL_TAB_ACTION.HANDLE_UPDATE_NEW_USER.name()))
        {
        	getApprovingItemsByType(portletState, aReq, aRes, ActionTypeConstants.PORTAL_USER_ACTION_UPDATE, VIEW_TABS.VUPDATE_USER);
        }
        if(action.equalsIgnoreCase(APPROVAL_TAB_ACTION.HANDLE_CREATE_NEW_COMPANY.name()))
        {
        	getApprovingItemsByType(portletState, aReq, aRes, ActionTypeConstants.COMPANY_ACTION_CREATE, VIEW_TABS.VNEW_COMPANY);
        }
        if(action.equalsIgnoreCase(APPROVAL_TAB_ACTION.HANDLE_UNBLOCK_COMPANY.name()))
        {
        	getApprovingItemsByType(portletState, aReq, aRes, ActionTypeConstants.COMPANY_ACTION_UNBLOCK, VIEW_TABS.VUNBLOCK_COMPANY);
        }
        if(action.equalsIgnoreCase(APPROVAL_TAB_ACTION.HANDLE_BLOCK_COMPANY.name()))
        {
        	getApprovingItemsByType(portletState, aReq, aRes, ActionTypeConstants.COMPANY_ACTION_BLOCK, VIEW_TABS.VBLOCK_COMPANY);
        }
        if(action.equalsIgnoreCase(APPROVAL_TAB_ACTION.HANDLE_DELETE_COMPANY.name()))
        {
        	getApprovingItemsByType(portletState, aReq, aRes, ActionTypeConstants.COMPANY_ACTION_DELETE, VIEW_TABS.VDELETE_COMPANY);
        }
        if(action.equalsIgnoreCase(APPROVAL_TAB_ACTION.HANDLE_UPDATE_COMPANY.name()))
        {
        	getApprovingItemsByType(portletState, aReq, aRes, ActionTypeConstants.COMPANY_ACTION_UPDATE, VIEW_TABS.VUPDATE_COMPANY);
        }
        if(action.equalsIgnoreCase(APPROVAL_TAB_ACTION.HANDLE_MAP_PANEL.name()))
        {
        	getApprovingItemsByType(portletState, aReq, aRes, ActionTypeConstants.MANDATE_PANEL_MAP_USERS, VIEW_TABS.VMAP_PANEL);
        }
        if(action.equalsIgnoreCase(APPROVAL_TAB_ACTION.HANDLE_UNMAP_PANEL.name()))
        {
        	getApprovingItemsByType(portletState, aReq, aRes, ActionTypeConstants.MANDATE_PANEL_UNMAP_USERS, VIEW_TABS.VUNMAP_PANEL);
        }
        if(action.equalsIgnoreCase(APPROVAL_TAB_ACTION.HANDLE_CREATE_FEE.name()))
        {
        	portletState.setFeeDescriptionApprovalType(FEE_DESCRIPTION_APPROVAL_TYPE.CORE_FEE_VIEW);
        	getApprovingItemsByType(portletState, aReq, aRes, ActionTypeConstants.FEE_DESCRIPTION_CREATE_NEW, VIEW_TABS.VNEW_FEE);
        }
        if(action.equalsIgnoreCase(APPROVAL_TAB_ACTION.HANDLE_UPDATE_FEE.name()))
        {
        	portletState.setFeeDescriptionApprovalType(FEE_DESCRIPTION_APPROVAL_TYPE.CORE_FEE_VIEW);
        	getApprovingItemsByType(portletState, aReq, aRes, ActionTypeConstants.FEE_DESCRIPTION_UPDATE, VIEW_TABS.VUPDATE_FEE);
        }
        if(action.equalsIgnoreCase(APPROVAL_TAB_ACTION.HANDLE_DELETE_FEE.name()))
        {
        	portletState.setFeeDescriptionApprovalType(FEE_DESCRIPTION_APPROVAL_TYPE.CORE_FEE_VIEW);
        	getApprovingItemsByType(portletState, aReq, aRes, ActionTypeConstants.FEE_DESCRIPTION_DELETE, VIEW_TABS.VDELETE_FEE);
        }
        if(action.equalsIgnoreCase(APPROVAL_TAB_ACTION.HANDLE_MAP_FEE.name()))
        {
        	portletState.setFeeDescriptionApprovalType(FEE_DESCRIPTION_APPROVAL_TYPE.COMPANY_MAPPINGS);
        	getApprovingItemsByType(portletState, aReq, aRes, ActionTypeConstants.FEE_DESCRIPTION_MAP_TO_USER, VIEW_TABS.VMAP_FEE);
        }
        if(action.equalsIgnoreCase(APPROVAL_TAB_ACTION.HANDLE_UNMAP_FEE.name()))
        {
        	portletState.setFeeDescriptionApprovalType(FEE_DESCRIPTION_APPROVAL_TYPE.COMPANY_MAPPINGS);
        	getApprovingItemsByType(portletState, aReq, aRes, ActionTypeConstants.FEE_DESCRIPTION_UNMAP_FROM_COMPANY, VIEW_TABS.VUNMAP_FEE);
        }
        if(action.equalsIgnoreCase(APPROVAL_TAB_ACTION.HANDLE_UPDATE_SETTINGS.name()))
        {
        	getApprovingItemsByType(portletState, aReq, aRes, ActionTypeConstants.SETTINGS_UPDATE, VIEW_TABS.VUPDATE_SETTINGS);
        }
        if(action.equalsIgnoreCase(APPROVAL_TAB_ACTION.HANDLE_NEW_TAX_TYPE.name()))
        {
        	getApprovingItemsByType(portletState, aReq, aRes, ActionTypeConstants.TAX_TYPE_ADD, VIEW_TABS.VUNMAP_FEE);
        }
        if(action.equalsIgnoreCase(APPROVAL_TAB_ACTION.HANDLE_TAX_TYPE_LIST_REACTIVATE.name()))
        {
        	getApprovingItemsByType(portletState, aReq, aRes, ActionTypeConstants.TAX_TYPE_REACTIVATE, VIEW_TABS.VACT_TAXTYPE_REACTIVATE);
        }if(action.equalsIgnoreCase(APPROVAL_TAB_ACTION.HANDLE_TAX_TYPE_LIST_SUSPEND.name()))
        {
        	getApprovingItemsByType(portletState, aReq, aRes, ActionTypeConstants.TAX_TYPE_SUSPEND, VIEW_TABS.VACT_TAXTYPE_SUSPEND);
        }if(action.equalsIgnoreCase(APPROVAL_TAB_ACTION.HANDLE_TAX_TYPE_LIST_UPDATE.name()))
        {
        	getApprovingItemsByType(portletState, aReq, aRes, ActionTypeConstants.TAX_TYPE_ADD, VIEW_TABS.VUPDATE_TAXTYPE);
        }
	}
	
	
	
	public void getApprovingItemsByType(ApprovalFlowPortletState portletState, ActionRequest aReq, ActionResponse aRes, ActionTypeConstants atc, VIEW_TABS vt)
	{
		if(portletState.getApprovalDirect().getValue().equals("0"))
		{
			Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
					portletState.getSelectedApprovalItemType(),
					portletState.getPortalUser().getRoleType().getRoleTypeName(),
					atc);
			portletState.setAllApprovalFlowTransitListing(approvalItems);
			portletState.setCurrentTab(vt);
		}
		else
		{
			Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
					portletState.getSelectedApprovalItemType(),
					portletState.getPortalUser(),
					atc);
			portletState.setAllApprovalFlowTransitListing(approvalItems);
			portletState.setCurrentTab(vt);
		}
		if(portletState.getAllApprovalFlowTransitListing()!=null && portletState.getAllApprovalFlowTransitListing().size()>0)
		{
			aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
		}else
		{
			portletState.addError(aReq, "There are no items currently requiring your approval. Check the other tabs to see if there are other items under those tabs", portletState);
			aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
		}
	}
	
	
	
	private boolean loginStepTwo(ActionRequest aReq, ActionResponse aRes,
			PortalUser currentPortalUser, PortalUserCRUDRights portalUserCRUDRights, SwpService sService, 
			ApprovalFlowPortletState portletState) {
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
	
	private void handleSelectApprovalEntity(ActionRequest aReq,
			ActionResponse aRes, ApprovalFlowPortletState portletState) {
		// TODO Auto-generated method stub
		String itemType = aReq.getParameter("itemType");
		portletState.setSelectedApprovalItemType(itemType);
		if(itemType!=null && !itemType.equals("-1"))
		{
			
			
			ComminsApplicationState cappState = portletState.getCas();
			Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
			Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
			DETERMINE_ACCESS determinAccessForUser = new Util().determineAccessForUserFxns(twoStep, approvalProcess, cappState, portletState.getPortalUserCRUDRights());
			DETERMINE_ACCESS determinAccessForCompany = new Util().determineAccessForCompanyFxns(twoStep, approvalProcess, cappState, portletState.getCompanyCRUDRights());
			
			
			
			if(itemType.equalsIgnoreCase(PortalUser.class.getSimpleName()))
			{
				if(determinAccessForUser.equals(DETERMINE_ACCESS.GRANT_APPROVER_ACCESS))
				{
					showPortalUserItems(portletState, aReq, aRes);
					portletState.setWorkItemTypeDescription("Creation of Users");
				}else
				{
					portletState.addError(aReq, "You do not have approval rights to carry out this action. " +
							"Contact appropriate authorities for this rights", portletState);
					portletState.setSelectedApprovalItemType(null);
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/starthere.jsp");
				}
				
			}
			if(itemType.equalsIgnoreCase(Company.class.getSimpleName()))
			{
				if(determinAccessForCompany.equals(DETERMINE_ACCESS.GRANT_APPROVER_ACCESS))
				{
					showCompanyItems(portletState, aReq, aRes);
					portletState.setWorkItemTypeDescription("Creation of Companies");
				}else
				{
					portletState.addError(aReq, "You do not have approval rights to carry out this action. " +
							"Contact appropriate authorities for this rights", portletState);
					portletState.setSelectedApprovalItemType(null);
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/starthere.jsp");
				}
			}
			if(itemType.equalsIgnoreCase(AuthorizePanelCombination.class.getSimpleName()))
			{
				if(determinAccessForCompany.equals(DETERMINE_ACCESS.GRANT_APPROVER_ACCESS) && 
						determinAccessForUser.equals(DETERMINE_ACCESS.GRANT_APPROVER_ACCESS))
				{
					showAuthorizePanelCombinationItems(portletState, aReq, aRes);
					portletState.setWorkItemTypeDescription("Mapping Users to Authorization Panels");
				}else
				{
					portletState.addError(aReq, "You do not have approval rights to carry out this action. " +
							"Contact appropriate authorities for this rights", portletState);
					portletState.setSelectedApprovalItemType(null);
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/starthere.jsp");
				}
			}
			if(itemType.equalsIgnoreCase(FeeDescription.class.getSimpleName()))
			{
				if(determinAccessForCompany.equals(DETERMINE_ACCESS.GRANT_APPROVER_ACCESS))
				{
					showFeeDescriptionItems(portletState, aReq, aRes);
					portletState.setWorkItemTypeDescription("Requests for New Transaction Fees");
				}else
				{
					portletState.addError(aReq, "You do not have approval rights to carry out this action. " +
							"Contact appropriate authorities for this rights", portletState);
					portletState.setSelectedApprovalItemType(null);
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/starthere.jsp");
				}
			}
			if(itemType.equalsIgnoreCase(Settings.class.getSimpleName()))
			{
				if(determinAccessForCompany.equals(DETERMINE_ACCESS.GRANT_APPROVER_ACCESS))
				{
					showSettingsItems(portletState, aReq, aRes);
					portletState.setWorkItemTypeDescription("Update on System Settings");
				}else
				{
					portletState.addError(aReq, "You do not have approval rights to carry out this action. " +
							"Contact appropriate authorities for this rights", portletState);
					portletState.setSelectedApprovalItemType(null);
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/starthere.jsp");
				}
			}
			if(itemType.equalsIgnoreCase(TaxType.class.getSimpleName()))
			{
				if(determinAccessForCompany.equals(DETERMINE_ACCESS.GRANT_APPROVER_ACCESS))
				{
					showTaxTypeItems(portletState, aReq, aRes);
					portletState.setWorkItemTypeDescription("Update on System Settings");
				}else
				{
					portletState.addError(aReq, "You do not have approval rights to carry out this action. " +
							"Contact appropriate authorities for this rights", portletState);
					portletState.setSelectedApprovalItemType(null);
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/starthere.jsp");
				}
			}
		}else
		{
			portletState.addError(aReq, "Select an item type first before you can approve or disapprove of item changes", portletState);
		}
	}

	private void showSettingsItems(ApprovalFlowPortletState portletState, ActionRequest aReq, ActionResponse aRes) {
		// TODO Auto-generated method stub
		if(portletState.getApprovalDirect().getValue().equals("0"))
		{
			Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
					portletState.getSelectedApprovalItemType(),
					portletState.getPortalUser().getRoleType().getRoleTypeName(),
					ActionTypeConstants.SETTINGS_UPDATE);
			portletState.setAllApprovalFlowTransitListing(approvalItems);
			portletState.setCurrentTab(VIEW_TABS.VUPDATE_SETTINGS);
		}
		else
		{
			Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
					portletState.getSelectedApprovalItemType(),
					portletState.getPortalUser(),
					ActionTypeConstants.SETTINGS_UPDATE);
			portletState.setAllApprovalFlowTransitListing(approvalItems);
			portletState.setCurrentTab(VIEW_TABS.VUPDATE_SETTINGS);
		}
		if(portletState.getAllApprovalFlowTransitListing()!=null && portletState.getAllApprovalFlowTransitListing().size()>0)
		{
			aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
		}else
		{
			portletState.addError(aReq, "There are no items currently requiring your approval. Check the other tabs to see if there are other items under those tabs", portletState);
			aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
		}
	}

	private void showFeeDescriptionItems(ApprovalFlowPortletState portletState, ActionRequest aReq, ActionResponse aRes) {
		// TODO Auto-generated method stub
		if(portletState.getApprovalDirect().getValue().equals("0"))
		{
			Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
					portletState.getSelectedApprovalItemType(),
					portletState.getPortalUser().getRoleType().getRoleTypeName(),
					ActionTypeConstants.FEE_DESCRIPTION_CREATE_NEW);
			portletState.setAllApprovalFlowTransitListing(approvalItems);
			portletState.setCurrentTab(VIEW_TABS.VNEW_FEE);
		}
		else
		{
			Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
					portletState.getSelectedApprovalItemType(),
					portletState.getPortalUser(),
					ActionTypeConstants.FEE_DESCRIPTION_CREATE_NEW);
			portletState.setAllApprovalFlowTransitListing(approvalItems);
			portletState.setCurrentTab(VIEW_TABS.VNEW_FEE);
		}

		portletState.setFeeDescriptionApprovalType(FEE_DESCRIPTION_APPROVAL_TYPE.CORE_FEE_VIEW);
		if(portletState.getAllApprovalFlowTransitListing()!=null && portletState.getAllApprovalFlowTransitListing().size()>0)
		{
			aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
		}else
		{
			portletState.addError(aReq, "There are no items currently requiring your approval. Check the other tabs to see if there are other items under those tabs", portletState);
			aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
		}
	}
	
	
	private void showTaxTypeItems(ApprovalFlowPortletState portletState, ActionRequest aReq, ActionResponse aRes) {
		// TODO Auto-generated method stub
		if(portletState.getApprovalDirect().getValue().equals("0"))
		{
			Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
					portletState.getSelectedApprovalItemType(),
					portletState.getPortalUser().getRoleType().getRoleTypeName(),
					ActionTypeConstants.TAX_TYPE_ADD);
			portletState.setAllApprovalFlowTransitListing(approvalItems);
			portletState.setCurrentTab(VIEW_TABS.VNEW_TAXTYPE);
		}
		else
		{
			Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
					portletState.getSelectedApprovalItemType(),
					portletState.getPortalUser(),
					ActionTypeConstants.TAX_TYPE_ADD);
			portletState.setAllApprovalFlowTransitListing(approvalItems);
			portletState.setCurrentTab(VIEW_TABS.VNEW_TAXTYPE);
		}
		if(portletState.getAllApprovalFlowTransitListing()!=null && portletState.getAllApprovalFlowTransitListing().size()>0)
		{
			aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
		}else
		{
			portletState.addError(aReq, "There are no items currently requiring your approval. Check the other tabs to see if there are other items under those tabs", portletState);
			aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
		}
	}

	private void showAuthorizePanelCombinationItems(
			ApprovalFlowPortletState portletState, ActionRequest aReq, ActionResponse aRes) {
		// TODO Auto-generated method stub
		if(portletState.getApprovalDirect().getValue().equals("0"))
		{
			Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
					portletState.getSelectedApprovalItemType(),
					portletState.getPortalUser().getRoleType().getRoleTypeName(),
					ActionTypeConstants.MANDATE_PANEL_MAP_USERS);
			portletState.setAllApprovalFlowTransitListing(approvalItems);
			portletState.setCurrentTab(VIEW_TABS.VMAP_PANEL);
		}
		else
		{
			Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
					portletState.getSelectedApprovalItemType(),
					portletState.getPortalUser(),
					ActionTypeConstants.MANDATE_PANEL_MAP_USERS);
			portletState.setAllApprovalFlowTransitListing(approvalItems);
			portletState.setCurrentTab(VIEW_TABS.VMAP_PANEL);
		}

		if(portletState.getAllApprovalFlowTransitListing()!=null && portletState.getAllApprovalFlowTransitListing().size()>0)
		{
			aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
		}else
		{
			portletState.addError(aReq, "There are no items currently requiring your approval. Check the other tabs to see if there are other items under those tabs", portletState);
			aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
		}
	}

	private void showCompanyItems(ApprovalFlowPortletState portletState, ActionRequest aReq, ActionResponse aRes) {
		// TODO Auto-generated method stub
		if(portletState.getApprovalDirect().getValue().equals("0"))
		{
			Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
					portletState.getSelectedApprovalItemType(),
					portletState.getPortalUser().getRoleType().getRoleTypeName(),
					ActionTypeConstants.COMPANY_ACTION_CREATE);
			portletState.setAllApprovalFlowTransitListing(approvalItems);
			portletState.setCurrentTab(VIEW_TABS.VNEW_COMPANY);
		}
		else
		{
			Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
					portletState.getSelectedApprovalItemType(),
					portletState.getPortalUser(),
					ActionTypeConstants.COMPANY_ACTION_CREATE);
			portletState.setAllApprovalFlowTransitListing(approvalItems);
			portletState.setCurrentTab(VIEW_TABS.VNEW_COMPANY);
		}

		if(portletState.getAllApprovalFlowTransitListing()!=null && portletState.getAllApprovalFlowTransitListing().size()>0)
		{
			aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
		}else
		{
			portletState.addError(aReq, "There are no items currently requiring your approval. Check the other tabs to see if there are other items under those tabs", portletState);
			aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
		}
	}

	private void showPortalUserItems(ApprovalFlowPortletState portletState, ActionRequest aReq, ActionResponse aRes) {
		// TODO Auto-generated method stub
		if(portletState.getApprovalDirect().getValue().equals("0"))
		{
			Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
					portletState.getSelectedApprovalItemType(),
					portletState.getPortalUser().getRoleType().getRoleTypeName(),
					ActionTypeConstants.PORTAL_USER_ACTION_CREATE);
			portletState.setAllApprovalFlowTransitListing(approvalItems);
			portletState.setCurrentTab(VIEW_TABS.VNEW_USER);
		}
		else
		{
			Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
					portletState.getSelectedApprovalItemType(),
					portletState.getPortalUser(),
					ActionTypeConstants.PORTAL_USER_ACTION_CREATE);
			portletState.setAllApprovalFlowTransitListing(approvalItems);
			portletState.setCurrentTab(VIEW_TABS.VNEW_USER);
		}


		if(portletState.getAllApprovalFlowTransitListing()!=null && portletState.getAllApprovalFlowTransitListing().size()>0)
		{
			aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
		}else
		{
			portletState.addError(aReq, "There are no items currently requiring your approval. Check the other tabs to see if there are other items under those tabs", portletState);
			aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
		}
	}

	private void handleWorkFlowListingsAction(ActionRequest aReq,
			ActionResponse aRes, ApprovalFlowPortletState portletState) {
		// TODO Auto-generated method stub
		String selectedApprovalFlow = aReq.getParameter("selectedApprovalFlow");
		String selectedApprovalFlowAction = aReq.getParameter("selectedApprovalFlowAction");
		
		if(selectedApprovalFlowAction.equalsIgnoreCase("gobacktostart"))
			handleGoToApprovalFlow(selectedApprovalFlow, aReq, aRes, portletState);
		else if(selectedApprovalFlowAction.equalsIgnoreCase("cancel"))
		{
			try{
				Long id = Long.valueOf(selectedApprovalFlow);
				portletState.setSelectedApprovalItem(selectedApprovalFlow);
				
				ApprovalFlowTransit approvalflow = (ApprovalFlowTransit)portletState.getApprovalFlowPortletUtil().getEntityObjectById(ApprovalFlowTransit.class, Long.valueOf(selectedApprovalFlow));
				swpService.deleteRecord(approvalflow);
				portletState.addError(aReq, "Request canceled successfully.", portletState);
				aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/starthere.jsp");
			}catch(NumberFormatException e){
				e.printStackTrace();
				portletState.addError(aReq, "Invalid item selected. Please select a valid request before proceeding", portletState);
				portletState.setSelectedApprovalItemType(null);
				portletState.setSelectedApprovalItem(null);
				portletState.setCurrentTab(null);
			}
		}
		else
		{
			try{
				Long id = Long.valueOf(selectedApprovalFlow);
				portletState.setSelectedApprovalItem(selectedApprovalFlow);
				
				ApprovalFlowTransit approvalflow = (ApprovalFlowTransit)portletState.getApprovalFlowPortletUtil().getEntityObjectById(ApprovalFlowTransit.class, Long.valueOf(selectedApprovalFlow));
				
				if(selectedApprovalFlowAction.equalsIgnoreCase("approve"))
				{
					log.info("Approve Now = " + 1);
					handleApproveRejectForwardApprovalFlow("approve", approvalflow, aReq, aRes, portletState);
				}
	//			if(selectedWorkFlowAction.equalsIgnoreCase("forward"))
	//				handleApproveRejectForwardWorkFlow("forward", workflow, aReq, aRes, portletState);
				else if(selectedApprovalFlowAction.equalsIgnoreCase("reject"))
					handleApproveRejectForwardApprovalFlow("reject", approvalflow, aReq, aRes, portletState);
	//			else if(selectedApprovalFlowAction.equalsIgnoreCase("view"))
	//				handleView(selectedApprovalFlow, aReq, aRes, portletState);
				
			}catch(NumberFormatException e){
				e.printStackTrace();
				portletState.addError(aReq, "Invalid item selected. Please select a valid item to work on before proceeding", portletState);
				portletState.setSelectedApprovalItemType(null);
				portletState.setSelectedApprovalItem(null);
				portletState.setCurrentTab(null);
			}
		}
	}
	
	
	private void handleGoToApprovalFlow(String selectedApprovalFlow,
			ActionRequest aReq, ActionResponse aRes,
			ApprovalFlowPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setSelectedApprovalItem(null);
		portletState.setSelectedApprovalItemType(null);
		aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/starthere.jsp");
	}

	private void handleApproveRejectForwardApprovalFlow(String action, ApprovalFlowTransit approvalFlowTransit,
			ActionRequest aReq, ActionResponse aRes,
			ApprovalFlowPortletState portletState) {
		// TODO Auto-generated method stub
		
		if(action.equalsIgnoreCase("approve"))
		{
			log.info("Approve Now = " + 2);
			if(portletState.getSelectedApprovalItemType()!=null && portletState.getSelectedApprovalItemType().equalsIgnoreCase(PortalUser.class.getSimpleName()))
			{
				log.info("Approve Now = " + 3);
				approvalPortalUser(portletState, approvalFlowTransit, aReq, aRes);
				
				if(portletState.getAllApprovalFlowTransitListing()!=null && portletState.getAllApprovalFlowTransitListing().size()>0)
				{
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				}
			}
			else if(portletState.getSelectedApprovalItemType()!=null && portletState.getSelectedApprovalItemType().equalsIgnoreCase(Company.class.getSimpleName()))
			{
				log.info("Approve Now = " + 23);
				approveCompanyItems(portletState, approvalFlowTransit, aReq, aRes);
				
				if(portletState.getAllApprovalFlowTransitListing()!=null && portletState.getAllApprovalFlowTransitListing().size()>0)
				{
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				}
			}
			else if(portletState.getSelectedApprovalItemType()!=null && portletState.getSelectedApprovalItemType().equalsIgnoreCase(AuthorizePanelCombination.class.getSimpleName()))
			{
				approveAuthorizePanelCombinationItems(portletState, approvalFlowTransit, aReq, aRes);
				
				if(portletState.getAllApprovalFlowTransitListing()!=null && portletState.getAllApprovalFlowTransitListing().size()>0)
				{
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				}
			}
			else if(portletState.getSelectedApprovalItemType()!=null && portletState.getSelectedApprovalItemType().equalsIgnoreCase(FeeDescription.class.getSimpleName()))
			{
				approveFeeDescriptionItems(portletState, approvalFlowTransit, aReq, aRes);
				
				if(portletState.getAllApprovalFlowTransitListing()!=null && portletState.getAllApprovalFlowTransitListing().size()>0)
				{
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				}
			}
			else if(portletState.getSelectedApprovalItemType()!=null && portletState.getSelectedApprovalItemType().equalsIgnoreCase(Settings.class.getSimpleName()))
			{
				approveSettingsItems(portletState, approvalFlowTransit, aReq, aRes);
				
				if(portletState.getApprovalDirect().getValue().equals("0"))
				{
					Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
							portletState.getSelectedApprovalItemType(),
							portletState.getPortalUser().getRoleType().getRoleTypeName(),
							ActionTypeConstants.SETTINGS_UPDATE);
					portletState.setAllApprovalFlowTransitListing(approvalItems);
					portletState.setCurrentTab(VIEW_TABS.VUPDATE_SETTINGS);
				}
				else
				{
					Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
							portletState.getSelectedApprovalItemType(),
							portletState.getPortalUser(),
							ActionTypeConstants.SETTINGS_UPDATE);
					portletState.setAllApprovalFlowTransitListing(approvalItems);
					portletState.setCurrentTab(VIEW_TABS.VUPDATE_SETTINGS);
				}
				if(portletState.getAllApprovalFlowTransitListing()!=null && portletState.getAllApprovalFlowTransitListing().size()>0)
				{
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				}
			}
			else if(portletState.getSelectedApprovalItemType()!=null && portletState.getSelectedApprovalItemType().equalsIgnoreCase(TaxType.class.getSimpleName()))
			{
				approveTaxTypeItems(portletState, approvalFlowTransit, aReq, aRes);
				
				if(portletState.getAllApprovalFlowTransitListing()!=null && portletState.getAllApprovalFlowTransitListing().size()>0)
				{
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				}
			}
		}
		else if(action.equalsIgnoreCase("reject"))
		{
			if(portletState.getSelectedApprovalItemType()!=null && portletState.getSelectedApprovalItemType().equalsIgnoreCase(PortalUser.class.getSimpleName()))
			{
				disapprovalPortalUser(portletState, approvalFlowTransit, aReq, aRes);
				
				if(portletState.getAllApprovalFlowTransitListing()!=null && portletState.getAllApprovalFlowTransitListing().size()>0)
				{
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				}
			}
			else if(portletState.getSelectedApprovalItemType()!=null && portletState.getSelectedApprovalItemType().equalsIgnoreCase(Company.class.getSimpleName()))
			{
				disapproveCompanyItems(portletState, approvalFlowTransit, aReq, aRes);
				if(portletState.getAllApprovalFlowTransitListing()!=null && portletState.getAllApprovalFlowTransitListing().size()>0)
				{
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				}
			}
			else if(portletState.getSelectedApprovalItemType()!=null && portletState.getSelectedApprovalItemType().equalsIgnoreCase(AuthorizePanelCombination.class.getSimpleName()))
			{
				disapproveAuthorizePanelCombinationItems(portletState, approvalFlowTransit, aReq, aRes);
				if(portletState.getAllApprovalFlowTransitListing()!=null && portletState.getAllApprovalFlowTransitListing().size()>0)
				{
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				}
			}
			else if(portletState.getSelectedApprovalItemType()!=null && portletState.getSelectedApprovalItemType().equalsIgnoreCase(FeeDescription.class.getSimpleName()))
			{
				disapproveFeeDescriptionItems(portletState, approvalFlowTransit, aReq, aRes);
				if(portletState.getApprovalDirect().getValue().equals("0"))
				{
					Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
							portletState.getSelectedApprovalItemType(),
							portletState.getPortalUser().getRoleType().getRoleTypeName(),
							ActionTypeConstants.FEE_DESCRIPTION_MAP_TO_USER);
					portletState.setAllApprovalFlowTransitListing(approvalItems);
					portletState.setCurrentTab(VIEW_TABS.VMAP_FEE);
				}
				else
				{
					Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
							portletState.getSelectedApprovalItemType(),
							portletState.getPortalUser(),
							ActionTypeConstants.FEE_DESCRIPTION_MAP_TO_USER);
					portletState.setAllApprovalFlowTransitListing(approvalItems);
					portletState.setCurrentTab(VIEW_TABS.VMAP_FEE);
				}
				
				
				if(portletState.getAllApprovalFlowTransitListing()!=null && portletState.getAllApprovalFlowTransitListing().size()>0)
				{
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				}
			}
			else if(portletState.getSelectedApprovalItemType()!=null && portletState.getSelectedApprovalItemType().equalsIgnoreCase(Settings.class.getSimpleName()))
			{
				disapproveSettingsItems(portletState, approvalFlowTransit, aReq, aRes);
				if(portletState.getApprovalDirect().getValue().equals("0"))
				{
					Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
							portletState.getSelectedApprovalItemType(),
							portletState.getPortalUser().getRoleType().getRoleTypeName(),
							ActionTypeConstants.SETTINGS_UPDATE);
					portletState.setAllApprovalFlowTransitListing(approvalItems);
					portletState.setCurrentTab(VIEW_TABS.VUPDATE_SETTINGS);
				}
				else
				{
					Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
							portletState.getSelectedApprovalItemType(),
							portletState.getPortalUser(),
							ActionTypeConstants.SETTINGS_UPDATE);
					portletState.setAllApprovalFlowTransitListing(approvalItems);
					portletState.setCurrentTab(VIEW_TABS.VUPDATE_SETTINGS);
				}
				if(portletState.getAllApprovalFlowTransitListing()!=null && portletState.getAllApprovalFlowTransitListing().size()>0)
				{
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				}
			}

		}
	}

	private void disapproveSettingsItems(ApprovalFlowPortletState portletState,
			ApprovalFlowTransit approvalFlowTransit, ActionRequest aReq,
			ActionResponse aRes) {
		// TODO Auto-generated method stub
		
			portletState.setCurrentTab(VIEW_TABS.VUPDATE_SETTINGS);
			
		String reason  = aReq.getParameter("reason");
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(approvalFlowTransit.getObjectData());
			if(jsonObject!=null)
			{
			
				
				emailer.emailDisapproval(
					approvalFlowTransit.getPortalUser().getEmailAddress(),
					approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
					"Your request to update the system settings was disapproved<br>", portletState.getSystemUrl().getValue(), 
					portletState.getApplicationName().getValue() + " - Disapproval of Request to update system settings", 
					portletState.getApplicationName().getValue());
			
				
					String message = "Your request to update the system settings was disapproved";
					SendSms sendSms = new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
							portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
					
			}
		}catch(JSONException e)
		{
			e.printStackTrace();
		}
		swpService.deleteRecord(approvalFlowTransit);
		portletState.addSuccess(aReq, "Approval Request successfully disapproved", portletState);
	}

	private void disapproveFeeDescriptionItems(
			ApprovalFlowPortletState portletState,
			ApprovalFlowTransit approvalFlowTransit, ActionRequest aReq,
			ActionResponse aRes) {
		// TODO Auto-generated method stub
		String reason  = aReq.getParameter("reason");
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(approvalFlowTransit.getObjectData());
			if(jsonObject!=null)
			{
			
				
				emailer.emailDisapproval(
					approvalFlowTransit.getPortalUser().getEmailAddress(),
					approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
					"Your request to update transaction fees was disapproved<br>", portletState.getSystemUrl().getValue(), 
					portletState.getApplicationName().getValue() + " - Approval of Request to update a transaction fee", portletState.getApplicationName().getValue());
				
				String message = "Your request to update transaction fees was disapproved";
				SendSms sendSms = new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
						portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
			
			}
		}catch(JSONException e)
		{
			e.printStackTrace();
		}
		
		swpService.deleteRecord(approvalFlowTransit);
		portletState.addSuccess(aReq, "Request successfully disapproved", portletState);
		if(portletState.getApprovalDirect().getValue().equals("0"))
		{
			Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
					portletState.getSelectedApprovalItemType(),
					portletState.getPortalUser().getRoleType().getRoleTypeName(),
					ActionTypeConstants.FEE_DESCRIPTION_CREATE_NEW);
			portletState.setAllApprovalFlowTransitListing(approvalItems);
			portletState.setCurrentTab(VIEW_TABS.VNEW_FEE);
		}
		else
		{
			Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
					portletState.getSelectedApprovalItemType(),
					portletState.getPortalUser(),
					ActionTypeConstants.FEE_DESCRIPTION_CREATE_NEW);
			portletState.setAllApprovalFlowTransitListing(approvalItems);
			portletState.setCurrentTab(VIEW_TABS.VNEW_FEE);
		}
	}

	private void disapproveAuthorizePanelCombinationItems(
			ApprovalFlowPortletState portletState,
			ApprovalFlowTransit approvalFlowTransit, ActionRequest aReq,
			ActionResponse aRes) {
		// TODO Auto-generated method stub
		if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.MANDATE_PANEL_MAP_USERS))
		{
			Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
					portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
							Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
					portletState.getSendingEmailUsername().getValue());

			JSONObject jsonObject =null;
			try {
				jsonObject = new JSONObject(approvalFlowTransit.getObjectData());
				PortalUser pu = (PortalUser)portletState.getApprovalFlowPortletUtil().getEntityObjectById(PortalUser.class, Long.valueOf(jsonObject.getString("selectedMapPanelPortalUser")));
				AuthorizePanel ap = (AuthorizePanel)portletState.getApprovalFlowPortletUtil().getEntityObjectById(AuthorizePanel.class, Long.valueOf(jsonObject.getString("selectedMapPanel")));

				swpService.deleteRecord(approvalFlowTransit);
				emailer.emailDisapproval(
					approvalFlowTransit.getPortalUser().getEmailAddress(),
					approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
					"Your request to add the company staff - " + pu.getFirstName() + " " + pu.getLastName() + " - to the authorisation mandate panel - " +
					ap.getPanelName() + " for amounts Between ZMW" + ap.getFinancialAmountRestriction().getLowerLimitValue() + " and " + 
							ap.getFinancialAmountRestriction().getUpperLimitValue() + " - was disapproved<br>", portletState.getSystemUrl().getValue(), 
					"Disapproval of the Removal of a company staff from a mandate panel", portletState.getApplicationName().getValue());
				
				String message = "Your request to add the company staff - " + pu.getFirstName() + " " + pu.getLastName() + " - to an authorisation mandate panel - " +
					ap.getPanelName() + " was disapproved";
				SendSms sendSms = new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
						portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
			}catch(JSONException e)
			{
				e.printStackTrace();
			}
			
			if(portletState.getApprovalDirect().getValue().equals("0"))
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser().getRoleType().getRoleTypeName(),
						ActionTypeConstants.MANDATE_PANEL_MAP_USERS);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
				portletState.setCurrentTab(VIEW_TABS.VMAP_PANEL);
			}
			else
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser(),
						ActionTypeConstants.MANDATE_PANEL_MAP_USERS);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
				portletState.setCurrentTab(VIEW_TABS.VMAP_PANEL);
			}
		}
		if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.MANDATE_PANEL_UNMAP_USERS))
		{
			Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
					portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
							Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
					portletState.getSendingEmailUsername().getValue());

			JSONObject jsonObject =null;
			try {
				jsonObject = new JSONObject(approvalFlowTransit.getObjectData());
				PortalUser pu = (PortalUser)portletState.getApprovalFlowPortletUtil().getEntityObjectById(PortalUser.class, Long.valueOf(jsonObject.getString("selectedMapPanelPortalUser")));
				AuthorizePanel ap = (AuthorizePanel)portletState.getApprovalFlowPortletUtil().getEntityObjectById(AuthorizePanel.class, Long.valueOf(jsonObject.getString("selectedMapPanel")));

				swpService.deleteRecord(approvalFlowTransit);
				emailer.emailDisapproval(
					approvalFlowTransit.getPortalUser().getEmailAddress(),
					approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
					"Your request to remove the company staff - " + pu.getFirstName() + " " + pu.getLastName() + " - from the authorisation mandate panel - " +
					ap.getPanelName() + " for amounts Between ZMW" + ap.getFinancialAmountRestriction().getLowerLimitValue() + " and " + 
							ap.getFinancialAmountRestriction().getUpperLimitValue() + " - was disapproved<br>", portletState.getSystemUrl().getValue(), 
					"Disapproval of the Removal of a company staff from a mandate panel", portletState.getApplicationName().getValue());
				
				String message = "Your request to remove the company staff - " + pu.getFirstName() + " " + pu.getLastName() + " - from authorisation mandate panel - " +
						ap.getPanelName() + " was disapproved";
					SendSms sendSms = new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
							portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
			}catch(JSONException e)
			{
				e.printStackTrace();
			}
			
			if(portletState.getApprovalDirect().getValue().equals("0"))
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser().getRoleType().getRoleTypeName(),
						ActionTypeConstants.MANDATE_PANEL_UNMAP_USERS);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
				portletState.setCurrentTab(VIEW_TABS.VUNMAP_PANEL);
			}
			else
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser(),
						ActionTypeConstants.MANDATE_PANEL_UNMAP_USERS);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
				portletState.setCurrentTab(VIEW_TABS.VUNMAP_PANEL);
			}
		}
		String reason  = aReq.getParameter("reason");
		
		portletState.addSuccess(aReq, "Approval Request successfully disapproved", portletState);
	}

	private void disapproveCompanyItems(ApprovalFlowPortletState portletState,
			ApprovalFlowTransit approvalFlowTransit, ActionRequest aReq,
			ActionResponse aRes) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());

		JSONObject jsonObject =null;
		if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.COMPANY_ACTION_CREATE))
		{
			portletState.setCurrentTab(VIEW_TABS.VNEW_COMPANY);
			if(portletState.getApprovalDirect().getValue().equals("0"))
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser().getRoleType().getRoleTypeName(),
						ActionTypeConstants.COMPANY_ACTION_CREATE);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			else
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser(),
						ActionTypeConstants.COMPANY_ACTION_CREATE);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			
			try {
				jsonObject = new JSONObject(approvalFlowTransit.getObjectData());
				if(jsonObject!=null)
				{
				

					swpService.deleteRecord(approvalFlowTransit);
					emailer.emailDisapproval(
						approvalFlowTransit.getPortalUser().getEmailAddress(),
						approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
						"Your request to create the company- " + jsonObject.getString("companyname") + " (" + jsonObject.getString("companyrcnumber") + ") - " +
								"was disapproved<br>", portletState.getSystemUrl().getValue(), 
						portletState.getApplicationName().getValue() + " - Disapproval of the creation of a company profile", portletState.getApplicationName().getValue());
					

					String message = "Your request to create the company- " + jsonObject.getString("companyname") + " (" + jsonObject.getString("companyrcnumber") + ") - " +
								"was disapproved";
						new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
								portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
				
				}
			}catch(JSONException e)
			{
				e.printStackTrace();
			}
			
		}
		if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.COMPANY_ACTION_DELETE))
		{
			portletState.setCurrentTab(VIEW_TABS.VDELETE_COMPANY);
			if(portletState.getApprovalDirect().getValue().equals("0"))
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser().getRoleType().getRoleTypeName(),
						ActionTypeConstants.COMPANY_ACTION_DELETE);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			else
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser(),
						ActionTypeConstants.COMPANY_ACTION_DELETE);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			try {
				jsonObject = new JSONObject(approvalFlowTransit.getObjectData());
				if(jsonObject!=null)
				{
				

					swpService.deleteRecord(approvalFlowTransit);
					emailer.emailDisapproval(
						approvalFlowTransit.getPortalUser().getEmailAddress(),
						approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
						"Your request to delete the company- " + jsonObject.getString("companyname") + " (" + jsonObject.getString("companyrcnumber") + ") - " +
								"was disapproved<br>", portletState.getSystemUrl().getValue(), 
						portletState.getApplicationName().getValue() + " - Disapproval of the deletion of a company profile", portletState.getApplicationName().getValue());
				
					
					String message = "Your request to delete the company- " + jsonObject.getString("companyname") + " (" + jsonObject.getString("companyrcnumber") + ") - " +
							"was disapproved";
					new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
							portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
				}
			}catch(JSONException e)
			{
				e.printStackTrace();
			}
		}
		if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.COMPANY_ACTION_BLOCK))
		{
			portletState.setCurrentTab(VIEW_TABS.VBLOCK_COMPANY);
			if(portletState.getApprovalDirect().getValue().equals("0"))
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser().getRoleType().getRoleTypeName(),
						ActionTypeConstants. COMPANY_ACTION_BLOCK);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			else
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser(),
						ActionTypeConstants.COMPANY_ACTION_BLOCK);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			
			
			try {
				jsonObject = new JSONObject(approvalFlowTransit.getObjectData());
				if(jsonObject!=null)
				{
				

					swpService.deleteRecord(approvalFlowTransit);
					emailer.emailDisapproval(
						approvalFlowTransit.getPortalUser().getEmailAddress(),
						approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
						"Your request to suspend the company- " + jsonObject.getString("companyname") + " (" + jsonObject.getString("companyrcnumber") + ") - " +
								"was disapproved<br>", portletState.getSystemUrl().getValue(), 
						portletState.getApplicationName().getValue() + " - Disapproval of the suspension of a company profile", portletState.getApplicationName().getValue());
					
					String message = "Your request to suspend the company- " + jsonObject.getString("companyname") + " (" + jsonObject.getString("companyrcnumber") + ") - " +
							"was disapproved";
					new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
							portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
				
				}
			}catch(JSONException e)
			{
				e.printStackTrace();
			}
		}
		if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.COMPANY_ACTION_UNBLOCK))
		{
			portletState.setCurrentTab(VIEW_TABS.VUNBLOCK_COMPANY);
			if(portletState.getApprovalDirect().getValue().equals("0"))
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser().getRoleType().getRoleTypeName(),
						ActionTypeConstants.COMPANY_ACTION_UNBLOCK);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			else
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser(),
						ActionTypeConstants.COMPANY_ACTION_UNBLOCK);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			try {
				jsonObject = new JSONObject(approvalFlowTransit.getObjectData());
				if(jsonObject!=null)
				{
				

					swpService.deleteRecord(approvalFlowTransit);
					emailer.emailDisapproval(
						approvalFlowTransit.getPortalUser().getEmailAddress(),
						approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
						"Your request to reactivate the company- " + jsonObject.getString("companyname") + " (" + jsonObject.getString("companyrcnumber") + ") - " +
								"was disapproved<br>", portletState.getSystemUrl().getValue(), 
						portletState.getApplicationName().getValue() + " - Disapproval of the reactivation of a company profile", portletState.getApplicationName().getValue());
					
					String message = "Your request to reactivate the company- " + jsonObject.getString("companyname") + " (" + jsonObject.getString("companyrcnumber") + ") - " +
							"was disapproved";
					new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
							portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
				
				}
			}catch(JSONException e)
			{
				e.printStackTrace();
			}
		}
		if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.COMPANY_ACTION_UPDATE))
		{
			portletState.setCurrentTab(VIEW_TABS.VUPDATE_COMPANY);
			if(portletState.getApprovalDirect().getValue().equals("0"))
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser().getRoleType().getRoleTypeName(),
						ActionTypeConstants.COMPANY_ACTION_UPDATE);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			else
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser(),
						ActionTypeConstants.COMPANY_ACTION_UPDATE);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			try {
				jsonObject = new JSONObject(approvalFlowTransit.getObjectData());
				if(jsonObject!=null)
				{
				

					swpService.deleteRecord(approvalFlowTransit);
					emailer.emailDisapproval(
						approvalFlowTransit.getPortalUser().getEmailAddress(),
						approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
						"Your request to update the company- " + jsonObject.getString("companyname") + " (" + jsonObject.getString("companyrcnumber") + ") - " +
								"was disapproved<br>", portletState.getSystemUrl().getValue(), 
						portletState.getApplicationName().getValue() + " - Disapproval of the update of a company profile", portletState.getApplicationName().getValue());
					
					String message = "Your request to update the company- " + jsonObject.getString("companyname") + " (" + jsonObject.getString("companyrcnumber") + ") - " +
							"was disapproved";
					new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
							portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
				
				}
			}catch(JSONException e)
			{
				e.printStackTrace();
			}
		}
		String reason  = aReq.getParameter("reason");
		portletState.addSuccess(aReq, "Approval Request successfully disapproved", portletState);
	}

	private void disapprovalPortalUser(ApprovalFlowPortletState portletState,
			ApprovalFlowTransit approvalFlowTransit, ActionRequest aReq,
			ActionResponse aRes) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		JSONObject jsonObject =null;
		try {
			jsonObject = new JSONObject(approvalFlowTransit.getObjectData());
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		swpService.deleteRecord(approvalFlowTransit);
		if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.PORTAL_USER_ACTION_CREATE))
		{
			portletState.setCurrentTab(VIEW_TABS.VNEW_USER);
			try {
				if(jsonObject!=null)
				{
					emailer.emailDisapproval(
						approvalFlowTransit.getPortalUser().getEmailAddress(),
						approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
						"Your request for the creation of a user profile account - " + jsonObject.getString("firstName") + " " + 
						jsonObject.getString("lastName") + " (" + jsonObject.getString("email") + ") - was disapproved<br>", portletState.getSystemUrl().getValue(), 
						"Disapproval of User Profile Account Creation Request", portletState.getApplicationName().getValue());
					
					String message = "Your request to create a user profile account- " + jsonObject.getString("firstName") + " " + 
							jsonObject.getString("lastName") + " (" + jsonObject.getString("email") + ") - " +
							"was disapproved";
					new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
							portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(portletState.getApprovalDirect().getValue().equals("0"))
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser().getRoleType().getRoleTypeName(),
						ActionTypeConstants.PORTAL_USER_ACTION_CREATE);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			else
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser(),
						ActionTypeConstants.PORTAL_USER_ACTION_CREATE);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
		}
		if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.PORTAL_USER_ACTION_DELETE))
		{
			portletState.setCurrentTab(VIEW_TABS.VDELETE_USER);
			try {
				if(jsonObject!=null)
				{
					emailer.emailDisapproval(
						approvalFlowTransit.getPortalUser().getEmailAddress(),
						approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
						"Your request for the deletion of a user profile account - " + jsonObject.getString("firstName") + " " + 
						jsonObject.getString("lastName") + " (" + jsonObject.getString("email") + ") - was disapproved<br>", portletState.getSystemUrl().getValue(), 
						"Disapproval of User Profile Account Deletion Request", portletState.getApplicationName().getValue());
				
					String message = "Your request to delete a user profile account- " + jsonObject.getString("firstName") + " " + 
						jsonObject.getString("lastName") + " (" + jsonObject.getString("email") + ") - " +
						"was disapproved";

					new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
						portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(portletState.getApprovalDirect().getValue().equals("0"))
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser().getRoleType().getRoleTypeName(),
						ActionTypeConstants.PORTAL_USER_ACTION_DELETE);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			else
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser(),
						ActionTypeConstants.PORTAL_USER_ACTION_DELETE);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
		}
		if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.PORTAL_USER_ACTION_BLOCK))
		{
			portletState.setCurrentTab(VIEW_TABS.VBLOCK_USER);
			try {
				if(jsonObject!=null)
				{
					emailer.emailDisapproval(
						approvalFlowTransit.getPortalUser().getEmailAddress(),
						approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
						"Your request for the suspension of a user profile account - " + jsonObject.getString("firstName") + " " + 
						jsonObject.getString("lastName") + " (" + jsonObject.getString("email") + ") - was disapproved<br>", portletState.getSystemUrl().getValue(), 
						"Disapproval of User Profile Account Suspension Request", portletState.getApplicationName().getValue());
				
					String message = "Your request to suspend a user profile account- " + jsonObject.getString("firstName") + " " + 
						jsonObject.getString("lastName") + " (" + jsonObject.getString("email") + ") - " +
						"was disapproved";

					new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
						portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(portletState.getApprovalDirect().getValue().equals("0"))
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser().getRoleType().getRoleTypeName(),
						ActionTypeConstants.PORTAL_USER_ACTION_BLOCK);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			else
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser(),
						ActionTypeConstants.PORTAL_USER_ACTION_BLOCK);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
		}
		if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.PORTAL_USER_ACTION_UNBLOCK))
		{
			portletState.setCurrentTab(VIEW_TABS.VUNBLOCK_USER);
			try {
				if(jsonObject!=null)
				{
					emailer.emailDisapproval(
						approvalFlowTransit.getPortalUser().getEmailAddress(),
						approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
						"Your request for the reactivation of a user profile - " + jsonObject.getString("firstName") + " " + 
						jsonObject.getString("lastName") + " (" + jsonObject.getString("email") + ") - was disapproved<br>", portletState.getSystemUrl().getValue(), 
						"Disapproval of User Profile Account Reactivation Request", portletState.getApplicationName().getValue());
				
					String message = "Your request to reactivate a user profile account- " + jsonObject.getString("firstName") + " " + 
						jsonObject.getString("lastName") + " (" + jsonObject.getString("email") + ") - " +
						"was disapproved";

					new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
						portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(portletState.getApprovalDirect().getValue().equals("0"))
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser().getRoleType().getRoleTypeName(),
						ActionTypeConstants.PORTAL_USER_ACTION_UNBLOCK);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			else
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser(),
						ActionTypeConstants.PORTAL_USER_ACTION_UNBLOCK);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
		}
		if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.PORTAL_USER_ACTION_UPDATE))
		{
			portletState.setCurrentTab(VIEW_TABS.VUPDATE_USER);
			try {
				if(jsonObject!=null)
				{
					emailer.emailDisapproval(
						approvalFlowTransit.getPortalUser().getEmailAddress(),
						approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
						"Your request for the update of a user profile - " + jsonObject.getString("firstName") + " " + 
						jsonObject.getString("lastName") + " (" + jsonObject.getString("email") + ") - was disapproved<br>", portletState.getSystemUrl().getValue(), 
						"Disapproval of User Profile Account Update Request", portletState.getApplicationName().getValue());
				
					String message = "Your request to update a user profile account- " + jsonObject.getString("firstName") + " " + 
							jsonObject.getString("lastName") + " (" + jsonObject.getString("email") + ") - " +
							"was disapproved";
	
					new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
							portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(portletState.getApprovalDirect().getValue().equals("0"))
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser().getRoleType().getRoleTypeName(),
						ActionTypeConstants.PORTAL_USER_ACTION_UPDATE);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			else
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser(),
						ActionTypeConstants.PORTAL_USER_ACTION_UPDATE);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
		}
		portletState.addSuccess(aReq, "Request disapproved successfully!", portletState);
		String reason  = aReq.getParameter("reason");
	}

	private void approveSettingsItems(ApprovalFlowPortletState portletState,
			ApprovalFlowTransit approvalFlowTransit, ActionRequest aReq,
			ActionResponse aRes) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		String settingsData = approvalFlowTransit.getObjectData();
		try
		{
			JSONObject jsonObject = new JSONObject(settingsData);
			Iterator iter = jsonObject.keys();
			boolean proceed = false;
			while(iter.hasNext())
			{
				String key = (String)iter.next();
				Settings setting = portletState.getApprovalFlowPortletUtil().getSettingByName(SmartPayConstants.fromString(key));
				if(setting!=null)
				{
					proceed = true;
					setting.setValue((String)jsonObject.getString(key));
					swpService.updateRecord(setting);
				}
				
			}
			
			
			if(proceed)
			{
				ApprovalFlowPortletState.loadSettings(portletState);
				swpService.deleteRecord(approvalFlowTransit);
				
				if(jsonObject!=null)
				{
					emailer.emailDisapproval(
						approvalFlowTransit.getPortalUser().getEmailAddress(),
						approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
						"Your request for the update of system settings was approved<br>", portletState.getSystemUrl().getValue(), 
						"Approval of System Settings Update Request", portletState.getApplicationName().getValue());
				
				String message = "Your request to update system settings was approved";

				new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
						portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
				}
				
				aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				portletState.addSuccess(aReq, "The system settings request have been approved and updated successfully!", portletState);
			}else{
				aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				portletState.addError(aReq, "The system settings request have been not been approved and was not updated successfully!", portletState);
			}
		}catch(JSONException e)
		{
			portletState.addError(aReq, "Interpreting system settings data failed. The settings data provided by the initiating officer has an invalid structure. " +
					"Disapprove this approval item.", portletState);
			aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
		}
		portletState.setCurrentTab(VIEW_TABS.VUPDATE_SETTINGS);
	}

	
	private void approveTaxTypeItems(
			ApprovalFlowPortletState portletState,
			ApprovalFlowTransit approvalFlowTransit, ActionRequest aReq,
			ActionResponse aRes) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		String feeDescriptionData = approvalFlowTransit.getObjectData();
		try
		{
			
			JSONObject jsonObject = new JSONObject(feeDescriptionData);
			if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.TAX_TYPE_ADD))
			{
				if(approvalFlowTransit.getEntityId()!=null)
				{
					//UPDATE
					Long id = approvalFlowTransit.getEntityId();
					TaxType tt = (TaxType)portletState.getApprovalFlowPortletUtil().getEntityObjectById(TaxType.class, id);
					
					if(tt!=null)
					{
						tt.setTaxName(jsonObject.getString("taxname"));
						tt.setTaxCode(jsonObject.getString("taxcode"));
						swpService.updateRecord(tt);
						
						
						
						handleAudit("Tax Type Reactivated", Long.toString(tt.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
						
						TaxTypeAccount txAcct = (TaxTypeAccount)portletState.getApprovalFlowPortletUtil().getTaxTypeAccountByTaxType(id);
						if(txAcct!=null && txAcct.getAccountNumber().equals(jsonObject.getString("taxacctno")) && 
								txAcct.getAccountSortCode().equals(jsonObject.getString("taxacctsortcode")))
						{
							
						}else
						{
							if(txAcct!=null)
							{
								txAcct.setStatus(Boolean.FALSE);
								
								swpService.updateRecord(txAcct);
								
								TaxTypeAccount taxTypeAccount = new TaxTypeAccount();
								taxTypeAccount.setAccountNumber(jsonObject.getString("taxacctno"));
								taxTypeAccount.setAccountSortCode(jsonObject.getString("taxacctsortcode"));
								taxTypeAccount.setCreatedByPortalUserId(Long.toString(portletState.getPortalUser().getId()));
								taxTypeAccount.setDateCreated(new Timestamp((new Date()).getTime()));
								taxTypeAccount.setTaxType(tt);
								taxTypeAccount.setStatus(Boolean.TRUE);
								taxTypeAccount = (TaxTypeAccount)swpService.createNewRecord(taxTypeAccount);
								log.info("taxTypeAccount = " + taxTypeAccount.getId());
							}
						}
						portletState.addSuccess(aReq, "Tax Type created successfully", portletState);
						handleAudit("TaxType Update", Long.toString(tt.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
					}
					else
					{
						portletState.addError(aReq, "Tax Type not created successfully", portletState);
						
					}
	
					
					
					
					
				}else
				{
					//ADD
					
					TaxType taxType = new TaxType();
					taxType.setTaxName(jsonObject.getString("taxname"));
					taxType.setTaxCode(jsonObject.getString("taxcode"));
					taxType = (TaxType)swpService.createNewRecord(taxType);

					TaxTypeAccount taxTypeAccount = new TaxTypeAccount();
					taxTypeAccount.setAccountNumber(jsonObject.getString("taxacctno"));
					taxTypeAccount.setAccountSortCode(jsonObject.getString("taxacctsortcode"));
					taxTypeAccount.setCreatedByPortalUserId(Long.toString(portletState.getPortalUser().getId()));
					taxTypeAccount.setDateCreated(new Timestamp((new Date()).getTime()));
					taxTypeAccount.setTaxType(taxType);
					taxTypeAccount.setStatus(Boolean.TRUE);
					taxTypeAccount = (TaxTypeAccount)swpService.createNewRecord(taxTypeAccount);
					
					swpService.deleteRecord(approvalFlowTransit);
					portletState.addSuccess(aReq, "Tax Type updated successfully", portletState);
				}
			}else if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.TAX_TYPE_REACTIVATE))
			{
				Long id = approvalFlowTransit.getEntityId();
				TaxType tt = (TaxType)portletState.getApprovalFlowPortletUtil().getEntityObjectById(TaxType.class, id);
				if(tt!=null)
				{
					tt.setStatus(SmartPayConstants.STATUS_ACTIVE);
					swpService.updateRecord(tt);
					portletState.addSuccess(aReq, "Tax Type reactivated successfully", portletState);
					handleAudit("Tax Type Reactivated", Long.toString(tt.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				}else
				{
					portletState.addError(aReq, "Tax Type not reactivated successfully", portletState);
				}
			}else if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.TAX_TYPE_SUSPEND))
			{
				Long id = approvalFlowTransit.getEntityId();
				TaxType tt = (TaxType)portletState.getApprovalFlowPortletUtil().getEntityObjectById(TaxType.class, id);
				if(tt!=null)
				{
					tt.setStatus(SmartPayConstants.STATUS_INACTIVE);
					swpService.updateRecord(tt);
					portletState.addSuccess(aReq, "Tax Type suspended successfully", portletState);
					handleAudit("Tax Type Suspended", Long.toString(tt.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				}else
				{
					portletState.addError(aReq, "Tax Type not suspended successfully", portletState);
				}
				
			}
		}
		catch(JSONException e)
		{
			
		}
	}
	
	
	private void approveFeeDescriptionItems(
			ApprovalFlowPortletState portletState,
			ApprovalFlowTransit approvalFlowTransit, ActionRequest aReq,
			ActionResponse aRes) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		String feeDescriptionData = approvalFlowTransit.getObjectData();
		try
		{
			
			JSONObject jsonObject = new JSONObject(feeDescriptionData);
			if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.FEE_DESCRIPTION_CREATE_NEW))
			{
				FeeDescription apc = handleApproveAndCreateFeeDescription(portletState, aReq, aRes, (String)jsonObject.get("amountApplicable"), 
						(String)jsonObject.get("feeDescription"), (String)jsonObject.get("feeName"), (String)jsonObject.get("primaryFee"));
				
				
//				jsonObject.put("feeName", portletState.getFeeDescriptionName());
//				jsonObject.put("feeDescription", portletState.getFeeDescriptionDetail());
//				jsonObject.put("amountApplicable", portletState.getFeeDescriptionAmount());
//				jsonObject.put("primaryFee", portletState.isPrimaryFeeChecked() ? "1" : "0");
//				jsonObject.put("requestBy", portletState.getPortalUser().getFirstName() + " " + portletState.getPortalUser().getLastName());
				
				portletState.setFeeDescriptionApprovalType(FEE_DESCRIPTION_APPROVAL_TYPE.CORE_FEE_VIEW);
				
				if(apc!=null)
				{
					swpService.deleteRecord(approvalFlowTransit);
					if(portletState.getApprovalDirect().getValue().equals("0"))
					{
						Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
								portletState.getSelectedApprovalItemType(),
								portletState.getPortalUser().getRoleType().getRoleTypeName(),
								ActionTypeConstants.FEE_DESCRIPTION_CREATE_NEW);
						portletState.setAllApprovalFlowTransitListing(approvalItems);
						portletState.setCurrentTab(VIEW_TABS.VNEW_FEE);
					}
					else
					{
						Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
								portletState.getSelectedApprovalItemType(),
								portletState.getPortalUser(),
								ActionTypeConstants.FEE_DESCRIPTION_CREATE_NEW);
						portletState.setAllApprovalFlowTransitListing(approvalItems);
						portletState.setCurrentTab(VIEW_TABS.VNEW_FEE);
					}
					
					try {
						if(jsonObject!=null)
						{
							emailer.emailDisapproval(
								approvalFlowTransit.getPortalUser().getEmailAddress(),
								approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
								"Your request for the creation of a new transaction fee - " + jsonObject.getString("feeName") + " " + " " +
										"(ZMW" + jsonObject.getString("amountApplicable") + ") - was approved<br>", portletState.getSystemUrl().getValue(), 
								"Approval of Transaction Fee Creation Request", portletState.getApplicationName().getValue());
						
						String message = "Your request to create a new transaction fee - " + jsonObject.getString("feeName") +
								"was approved";

						new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
								portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
					portletState.addSuccess(aReq, "The selected Transaction Fee has been approved and created successfully!", portletState);
				}else{
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
					portletState.addError(aReq, "The selected Transaction Fee has been not been approved and was not created successfully!", portletState);
				}
			}else if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.FEE_DESCRIPTION_DELETE))
			{
				portletState.setFeeDescriptionApprovalType(FEE_DESCRIPTION_APPROVAL_TYPE.CORE_FEE_VIEW);
				FeeDescription fd = (FeeDescription) portletState.getApprovalFlowPortletUtil().getEntityObjectById(FeeDescription.class, approvalFlowTransit.getEntityId());
				if(fd!=null)
				{
					fd.setStatus(SmartPayConstants.FEE_DESCRIPTION_STATUS_INACTIVE);
					swpService.updateRecord(fd);
					swpService.deleteRecord(approvalFlowTransit);
					
					if(portletState.getApprovalDirect().getValue().equals("0"))
					{
						Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
								portletState.getSelectedApprovalItemType(),
								portletState.getPortalUser().getRoleType().getRoleTypeName(),
								ActionTypeConstants.FEE_DESCRIPTION_DELETE);
						portletState.setAllApprovalFlowTransitListing(approvalItems);
						portletState.setCurrentTab(VIEW_TABS.VDELETE_FEE);
					}
					else
					{
						Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
								portletState.getSelectedApprovalItemType(),
								portletState.getPortalUser(),
								ActionTypeConstants.FEE_DESCRIPTION_DELETE);
						portletState.setAllApprovalFlowTransitListing(approvalItems);
						portletState.setCurrentTab(VIEW_TABS.VDELETE_FEE);
					}
					try {
						if(jsonObject!=null)
						{
							emailer.emailDisapproval(
								approvalFlowTransit.getPortalUser().getEmailAddress(),
								approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
								"Your request for the deletion of a transaction fee - " + jsonObject.getString("feeName") + " (ZMW" + 
								jsonObject.getString("amount applicable") + ") - was approved<br>", portletState.getSystemUrl().getValue(), 
								"Approval of Transaction Fee Deletion Request", portletState.getApplicationName().getValue());
						
							String message = "Your request to delete a transaction fee - " + jsonObject.getString("feeName") +
								"was approved";

							new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
								portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
							
							
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					portletState.addSuccess(aReq, "This transaction fee has been deleted successfully.", portletState);
					handleAudit("Fee Description Delete", Long.toString(fd.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				}else
				{
					portletState.addError(aReq, "This transaction fee could not be found on the system. Try repeating the process again.", portletState);
				}
			}else if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.FEE_DESCRIPTION_MAP_TO_USER))
			{
				portletState.setFeeDescriptionApprovalType(FEE_DESCRIPTION_APPROVAL_TYPE.COMPANY_MAPPINGS);
				CompanyFeeDescription rt = portletState.getApprovalFlowPortletUtil().getCompanyFeeDescriptionByCompanyAndFeeDescription(
						Long.valueOf((String)jsonObject.get("companyId")), 
						Long.valueOf((String)jsonObject.get("feeId")));
				if(rt==null)
				{
					CompanyFeeDescription companyFeeDescription = new CompanyFeeDescription();
					Company company = (Company)portletState.getApprovalFlowPortletUtil().getEntityObjectById(
							Company.class, 
							Long.valueOf((String)jsonObject.get("companyId")));
					companyFeeDescription.setCompany(company);
					FeeDescription fd = (FeeDescription)portletState.getApprovalFlowPortletUtil().getEntityObjectById(
							FeeDescription.class, 
							Long.valueOf((String)jsonObject.get("feeId")));
					companyFeeDescription.setFeeDescription(fd);
					companyFeeDescription.setDateAdded(new Timestamp((new Date()).getTime()));
					companyFeeDescription.setStatus(Boolean.TRUE);
					companyFeeDescription = (CompanyFeeDescription)swpService.createNewRecord(companyFeeDescription);
					
					
					if(companyFeeDescription!=null)
					{
						handleAudit("Create Company Fee Description", 
								Long.toString(companyFeeDescription.getId()), 
								new Timestamp((new Date()).getTime()), 
								portletState.getRemoteIPAddress(), 
								portletState.getPortalUser().getUserId());
						swpService.deleteRecord(approvalFlowTransit);
						
						if(jsonObject!=null)
						{
							emailer.emailDisapproval(
								approvalFlowTransit.getPortalUser().getEmailAddress(),
								approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
								"Your request for the mapping of a transaction fee - " + fd.getFeeName() + " (ZMW" + fd.getAmountApplicable() + ") - " +
										"to registered company - " +
										"" + company.getCompanyName()  + " (" + company.getCompanyRCNumber() + ") was approved" +
										"<br>", portletState.getSystemUrl().getValue(), 
								"Approval of Transaction Fee Mapping to Company Request", portletState.getApplicationName().getValue());
						
							String message = "Your request to map a transaction fee - " + jsonObject.getString("feeName") +
								"to " + company.getCompanyName() + "was approved";

							new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
								portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
						}
						
						portletState.addSuccess(aReq, "Mapping a transaction fee to the selected company was successful.", portletState);
						aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/mapfeedescriptiontocompany.jsp");
					}else
					{
						portletState.addError(aReq, "Mapping a transaction fee to the selected company was not successful. Please try again", portletState);
					}
					
					
				}else
				{
					if(rt.getStatus().equals(Boolean.TRUE))
					{
						portletState.addSuccess(aReq, "The transaction fee has already been mapped to the selected company - "  + rt.getCompany().getCompanyName(), portletState);

						swpService.deleteRecord(approvalFlowTransit);
						
					}else
					{
						rt.setStatus(Boolean.TRUE);
						swpService.updateRecord(rt);
						swpService.deleteRecord(approvalFlowTransit);
						handleAudit("Update Company Fee Description", 
								Long.toString(rt.getId()), 
								new Timestamp((new Date()).getTime()), 
								portletState.getRemoteIPAddress(), 
								portletState.getPortalUser().getUserId());
						portletState.addSuccess(aReq, "Transaction Fee Mapped successfully to selected company - " + rt.getCompany().getCompanyName(), portletState);

						Company company = (Company)portletState.getApprovalFlowPortletUtil().getEntityObjectById(
								Company.class, 
								Long.valueOf((String)jsonObject.get("companyId")));
						FeeDescription fd = (FeeDescription)portletState.getApprovalFlowPortletUtil().getEntityObjectById(
								FeeDescription.class, 
								Long.valueOf((String)jsonObject.get("feeId")));
						
						if(jsonObject!=null)
						{
							emailer.emailDisapproval(
								approvalFlowTransit.getPortalUser().getEmailAddress(),
								approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
								"Your request for the mapping of a transaction fee - " + fd.getFeeName() + " (ZMW" + fd.getAmountApplicable() + ") - " +
										"to registered company - " +
										"" + company.getCompanyName()  + " (" + company.getCompanyRCNumber() + ") was approved" +
										"<br>", portletState.getSystemUrl().getValue(), 
								"Approval of Transaction Fee Mapping to Company Request", portletState.getApplicationName().getValue());
							String message = "Your request to map a transaction fee - " + jsonObject.getString("feeName") +
									"to " + company.getCompanyName() + " was approved";

								new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
									portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
						}
						
					}
				}
			}else if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.FEE_DESCRIPTION_UNMAP_FROM_COMPANY))
			{
				portletState.setFeeDescriptionApprovalType(FEE_DESCRIPTION_APPROVAL_TYPE.COMPANY_MAPPINGS);
				CompanyFeeDescription cfd = (CompanyFeeDescription) portletState.getApprovalFlowPortletUtil().getEntityObjectById(CompanyFeeDescription.class, approvalFlowTransit.getEntityId());
				cfd.setStatus(Boolean.FALSE);
				swpService.updateRecord(cfd);
				swpService.deleteRecord(approvalFlowTransit);
				log.info("cfd = " + cfd.getId());
				handleAudit("Company Fee Description Mapping", Long.toString(cfd.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				portletState.addSuccess(aReq, "Company mapping to Fee Description was updated successfully!", portletState);
				
				
				Company company = (Company)portletState.getApprovalFlowPortletUtil().getEntityObjectById(
						Company.class, 
						Long.valueOf((String)jsonObject.get("companyId")));
				FeeDescription fd = (FeeDescription)portletState.getApprovalFlowPortletUtil().getEntityObjectById(
						FeeDescription.class, 
						Long.valueOf((String)jsonObject.get("feeId")));
				
				if(jsonObject!=null)
				{
					emailer.emailDisapproval(
						approvalFlowTransit.getPortalUser().getEmailAddress(),
						approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
						"Your request for the removal of the mapping of a transaction fee - " + fd.getFeeName() + " (ZMW" + fd.getAmountApplicable() + ") - " +
								"to registered company - " +
								"" + company.getCompanyName()  + " (" + company.getCompanyRCNumber() + ") was approved" +
								"<br>", portletState.getSystemUrl().getValue(), 
						"Approval of the Removal of Transaction Fee Mapping to Company Request", portletState.getApplicationName().getValue());
					
					String message = "Your request to remove the mapping of a transaction fee - " + jsonObject.getString("feeName") +
							"to " + company.getCompanyName() + " was approved";

						new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
							portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
				}
				
				if(portletState.getApprovalDirect().getValue().equals("0"))
				{
					Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
							portletState.getSelectedApprovalItemType(),
							portletState.getPortalUser().getRoleType().getRoleTypeName(),
							ActionTypeConstants.FEE_DESCRIPTION_CREATE_NEW);
					portletState.setAllApprovalFlowTransitListing(approvalItems);
					portletState.setCurrentTab(VIEW_TABS.VNEW_FEE);
				}
				else
				{
					Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
							portletState.getSelectedApprovalItemType(),
							portletState.getPortalUser(),
							ActionTypeConstants.FEE_DESCRIPTION_CREATE_NEW);
					portletState.setAllApprovalFlowTransitListing(approvalItems);
					portletState.setCurrentTab(VIEW_TABS.VNEW_FEE);
				}
			}else if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.FEE_DESCRIPTION_UPDATE))
			{
				portletState.setFeeDescriptionApprovalType(FEE_DESCRIPTION_APPROVAL_TYPE.COMPANY_MAPPINGS);
				FeeDescription feeDescription = (FeeDescription) portletState.getApprovalFlowPortletUtil().getEntityObjectById(FeeDescription.class, approvalFlowTransit.getEntityId());
				feeDescription.setAmountApplicable(Double.valueOf((String)jsonObject.get("amountApplicable")));
				feeDescription.setDescription((String)jsonObject.get("feeDescription"));
				feeDescription.setFeeName((String)jsonObject.get("feeName"));
				feeDescription.setIsPrimaryFee(((String)jsonObject.get("feeName")).equals("1") ? Boolean.TRUE : Boolean.FALSE);
				
	
				swpService.updateRecord(feeDescription);
				swpService.deleteRecord(approvalFlowTransit);
				log.info("feeDescription = " + feeDescription.getId());
				handleAudit("Fee Description Update", Long.toString(feeDescription.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/feedescriptionlisting.jsp");
				portletState.addSuccess(aReq, "Fee Description - " + feeDescription.getFeeName() + " - was updated successfully!", portletState);
				
				
				if(jsonObject!=null)
				{
					emailer.emailDisapproval(
						approvalFlowTransit.getPortalUser().getEmailAddress(),
						approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
						"Your request for the update of transaction fee - " + feeDescription.getFeeName() + " (ZMW" + feeDescription.getAmountApplicable() + ") " +
								"was approved" +
								"<br>", portletState.getSystemUrl().getValue(), 
						"Approval of the Update of Transaction Fee", portletState.getApplicationName().getValue());
					
					
					String message = "Your request to update a transaction fee was approved";

						new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
							portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
				}

				if(portletState.getApprovalDirect().getValue().equals("0"))
				{
					Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
							portletState.getSelectedApprovalItemType(),
							portletState.getPortalUser().getRoleType().getRoleTypeName(),
							ActionTypeConstants.FEE_DESCRIPTION_CREATE_NEW);
					portletState.setAllApprovalFlowTransitListing(approvalItems);
					portletState.setCurrentTab(VIEW_TABS.VNEW_FEE);
				}
				else
				{
					Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
							portletState.getSelectedApprovalItemType(),
							portletState.getPortalUser(),
							ActionTypeConstants.FEE_DESCRIPTION_CREATE_NEW);
					portletState.setAllApprovalFlowTransitListing(approvalItems);
					portletState.setCurrentTab(VIEW_TABS.VNEW_FEE);
				}
			}
			
		}catch(JSONException e)
		{
			portletState.addError(aReq, "Interpreting user data failed. The transaction fee data provided by the initiating officer has an invalid structure. " +
					"Disapprove this approval item.", portletState);
			aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
		}
	}

	private FeeDescription handleApproveAndCreateFeeDescription(
			ApprovalFlowPortletState portletState, ActionRequest aReq,
			ActionResponse aRes, String feeDescriptionAmount, String feeDescriptionDetail, String feeDescriptionName, String primaryFee) {
		// TODO Auto-generated method stub
		try
		{
			FeeDescription feeDescription = new FeeDescription();
			feeDescription.setAmountApplicable(Double.valueOf(feeDescriptionAmount));
			feeDescription.setDateCreated(new Timestamp((new Date()).getTime()));
			feeDescription.setDescription(feeDescriptionDetail);
			feeDescription.setFeeName(feeDescriptionName);
			feeDescription.setIsPrimaryFee(primaryFee!=null && primaryFee.equals("1") ? Boolean.TRUE : Boolean.FALSE);
			feeDescription.setStatus(SmartPayConstants.FEE_DESCRIPTION_STATUS_ACTIVE);
			

			feeDescription = (FeeDescription)swpService.createNewRecord(feeDescription);
			
			
			
			log.info("feeDescription = " + feeDescription.getId());
			handleAudit("Fee Description Creation", Long.toString(feeDescription.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
			return feeDescription;
		}catch(NumberFormatException e)
		{
			aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/createafeedescription.jsp");
			portletState.addError(aReq, "Provide a valid amount in the Applicable amount field. E.g. 2000.00, 12900", portletState); 
			e.printStackTrace();
			return null;
		}
	}

	private void approveAuthorizePanelCombinationItems(
			ApprovalFlowPortletState portletState,
			ApprovalFlowTransit approvalFlowTransit, ActionRequest aReq,
			ActionResponse aRes) {
		// TODO Auto-generated method stub
		
		log.info(1);
		log.info(approvalFlowTransit.getActionType());
		log.info(approvalFlowTransit.getActionType().getValue());
		
		if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.MANDATE_PANEL_MAP_USERS))
		{
			log.info(11);
			doaApproveMapUserToPanel(portletState, approvalFlowTransit, aReq, aRes);
			portletState.setCurrentTab(VIEW_TABS.VMAP_PANEL);
			
			if(portletState.getApprovalDirect().getValue().equals("0"))
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser().getRoleType().getRoleTypeName(),
						ActionTypeConstants.MANDATE_PANEL_MAP_USERS);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			else
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser(),
						ActionTypeConstants.MANDATE_PANEL_MAP_USERS);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
		}
		else if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.MANDATE_PANEL_UNMAP_USERS))
		{
			log.info(21);
			doApproveUnMapUserFromPanel(portletState, approvalFlowTransit, aReq, aRes);
			portletState.setCurrentTab(VIEW_TABS.VUNMAP_PANEL);
			
			if(portletState.getApprovalDirect().getValue().equals("0"))
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser().getRoleType().getRoleTypeName(),
						ActionTypeConstants.MANDATE_PANEL_UNMAP_USERS);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			else
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser(),
						ActionTypeConstants.MANDATE_PANEL_UNMAP_USERS);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
		}
			
		
	}

	private void doApproveUnMapUserFromPanel(
			ApprovalFlowPortletState portletState,
			ApprovalFlowTransit approvalFlowTransit, ActionRequest aReq,
			ActionResponse aRes) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		log.info(22);
		String authorizePanelCombinationData = approvalFlowTransit.getObjectData();
		try
		{
			log.info(23);
			JSONObject jsonObject = new JSONObject(authorizePanelCombinationData);
			try{
				log.info(24);
				AuthorizePanelCombination apc =  apc = (AuthorizePanelCombination)portletState.getApprovalFlowPortletUtil().
						getEntityObjectById(AuthorizePanelCombination.class, Long.valueOf(approvalFlowTransit.getEntityId()));
				if(apc!=null)
				{
					log.info(25);
					apc.setStatus(SmartPayConstants.AUTHORIZE_PANEL_COMBINATION_STATUS_DELETED);
					swpService.updateRecord(apc);
					swpService.deleteRecord(approvalFlowTransit);
					try {
						jsonObject = new JSONObject(approvalFlowTransit.getObjectData());
						if(jsonObject!=null)
						{
						
							
							emailer.emailDisapproval(
								approvalFlowTransit.getPortalUser().getEmailAddress(),
								approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
								"Your request to remove a company staff from an authorisation mandate panel - " +
										"was approved<br>", portletState.getSystemUrl().getValue(), 
								portletState.getApplicationName().getValue() + " - Approval of Request to remove a company staff from an authorisation panel"
								, portletState.getApplicationName().getValue());
							
							
							String message = "Your request to remove a company staff from an authorisation mandate panel was aproved";

								new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
									portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
						
						}
					}catch(JSONException e)
					{
						e.printStackTrace();
					}
					portletState.addSuccess(aReq, "The selected Authorization Panel Combination was updated successfully!", portletState);
				}else
				{
					log.info(26);
					aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
					portletState.addError(aReq, "The selected Authorization Panel Combination was not updated successfully!", portletState);
				}
			}catch(NumberFormatException e)
			{
				log.info(27);
				e.printStackTrace();
				aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				portletState.addError(aReq, "The selected Authorization Panel Combination was not updated successfully!", portletState);
			}
			
		}catch(JSONException e)
		{
			log.info(28);
			portletState.addError(aReq, "Interpreting user data failed. The authorization panel data provided by the initiating officer has an invalid structure. " +
					"Disapprove this approval item.", portletState);
			aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
		}
	}

	private void doaApproveMapUserToPanel(
			ApprovalFlowPortletState portletState,
			ApprovalFlowTransit approvalFlowTransit, ActionRequest aReq,
			ActionResponse aRes) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		String authorizePanelCombinationData = approvalFlowTransit.getObjectData();
		try
		{
			JSONObject jsonObject = new JSONObject(authorizePanelCombinationData);
			
			AuthorizePanelCombination apc = handleApproveAuthorizePanelCombination((String)jsonObject.get("selectedMapPosition"), (String)jsonObject.get("selectedMapPanel"), 
					(String)jsonObject.get("selectedMapPanelPortalUser"), portletState, aReq, aRes);
			
			if(apc!=null)
			{
				swpService.deleteRecord(approvalFlowTransit);
				
				try {
					jsonObject = new JSONObject(approvalFlowTransit.getObjectData());
					if(jsonObject!=null)
					{
					
						
						emailer.emailDisapproval(
							approvalFlowTransit.getPortalUser().getEmailAddress(),
							approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
							"Your request to map a company staff to an authorisation mandate panel - " +
									"was approved<br>", portletState.getSystemUrl().getValue(), 
							portletState.getApplicationName().getValue() + " - " +
									"Approval of Request to map a company staff to an authorisation panel", portletState.getApplicationName().getValue());
						
						String message = "Your request to map a company staff to an authorisation mandate panel was aproved";

						new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
							portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
					
					}
				}catch(JSONException e)
				{
					e.printStackTrace();
				}
				
				
				aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				portletState.addSuccess(aReq, "The selected Authorization Panel Combination has been created successfully!", portletState);
			}else{
				aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				portletState.addError(aReq, "The selected Authorization Panel Combination was not created successfully!", portletState);
			}
		}catch(JSONException e)
		{
			portletState.addError(aReq, "Interpreting user data failed. The authorization panel data provided by the initiating officer has an invalid structure. " +
					"Disapprove this approval item.", portletState);
			aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
		}
	}

	private void approveCompanyItems(ApprovalFlowPortletState portletState,
			ApprovalFlowTransit approvalFlowTransit, ActionRequest aReq,
			ActionResponse aRes) {
		// TODO Auto-generated method stub
		log.info("Approve Now = " + 24);
		if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.COMPANY_ACTION_CREATE))
		{
			doApproveCompany(portletState, approvalFlowTransit, aReq, aRes);
			portletState.setCurrentTab(VIEW_TABS.VNEW_COMPANY);

			if(portletState.getApprovalDirect().getValue().equals("0"))
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser().getRoleType().getRoleTypeName(),
						ActionTypeConstants.COMPANY_ACTION_CREATE);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			else
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser(),
						ActionTypeConstants.COMPANY_ACTION_CREATE);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
		}
		if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.COMPANY_ACTION_BLOCK))
		{
			log.info("Approve Now = " + 24);
			doBlockCompany(portletState, approvalFlowTransit, aReq, aRes);
			portletState.setCurrentTab(VIEW_TABS.VBLOCK_COMPANY);
			
			
			if(portletState.getApprovalDirect().getValue().equals("0"))
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser().getRoleType().getRoleTypeName(),
						ActionTypeConstants.COMPANY_ACTION_BLOCK);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			else
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser(),
						ActionTypeConstants.COMPANY_ACTION_BLOCK);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
		}
		if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.COMPANY_ACTION_DELETE))
		{
			doDeleteCompany(portletState, approvalFlowTransit, aReq, aRes);
			portletState.setCurrentTab(VIEW_TABS.VDELETE_COMPANY);
			if(portletState.getApprovalDirect().getValue().equals("0"))
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser().getRoleType().getRoleTypeName(),
						ActionTypeConstants.COMPANY_ACTION_DELETE);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			else
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser(),
						ActionTypeConstants.COMPANY_ACTION_DELETE);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
		}
		if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.COMPANY_ACTION_UNBLOCK))
		{
			doUnblockCompany(portletState, approvalFlowTransit, aReq, aRes);
			portletState.setCurrentTab(VIEW_TABS.VUNBLOCK_COMPANY);
			
			if(portletState.getApprovalDirect().getValue().equals("0"))
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser().getRoleType().getRoleTypeName(),
						ActionTypeConstants.COMPANY_ACTION_UNBLOCK);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			else
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser(),
						ActionTypeConstants.COMPANY_ACTION_UNBLOCK);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
		}
		if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.COMPANY_ACTION_UPDATE))
		{
			doUpdateCompany(portletState, approvalFlowTransit, aReq, aRes);
			portletState.setCurrentTab(VIEW_TABS.VUPDATE_COMPANY);
			
			if(portletState.getApprovalDirect().getValue().equals("0"))
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser().getRoleType().getRoleTypeName(),
						ActionTypeConstants.COMPANY_ACTION_UPDATE);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			else
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser(),
						ActionTypeConstants.COMPANY_ACTION_UPDATE);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
		}
		
	}
	
	
	private void doUpdateCompany(
			ApprovalFlowPortletState portletState, 
			ApprovalFlowTransit approvalFlowTransit,
			ActionRequest aReq, ActionResponse aRes) {
		// TODO Auto-generated method stub
		
		try
		{
			JSONObject jsonObject = new JSONObject(approvalFlowTransit.getObjectData());
			Company company = portletState.getApprovalFlowPortletUtil().getCompanyById(
					approvalFlowTransit.getEntityId());
				if(company!=null)
				{
					company.setAccountNumber((String)jsonObject.get("bankNumber"));
					company.setAddressLine1((String)jsonObject.get("line1addressofcompany"));
					company.setAddressLine2((String)jsonObject.get("line2addressofcompany"));
					company.setCompanyName((String)jsonObject.get("companyname"));
					company.setCompanyRCNumber((String)jsonObject.get("companyrcnumber"));
					company.setEmailAddress((String)jsonObject.get("companyemailaddress"));
					company.setMobileNumber((String)jsonObject.get("companycontactphonenumber"));
					
					Long id = null;
					try{
						id = Long.valueOf((String)jsonObject.get("selectedBankBranchId"));
						BankBranches bb = (BankBranches)(portletState.getApprovalFlowPortletUtil().getEntityObjectById(BankBranches.class, id));
						company.setBankBranches(bb);
					}catch(NumberFormatException e)
					{
						
					}
					company.setCompanyType(CompanyTypeConstants.fromString((String)jsonObject.get("selectedCompanyType")));
					
					if(company.getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY))
						company.setClearingAgent(((String)jsonObject.get("selectedCompanyClass")).equals("0") ? Boolean.FALSE : Boolean.TRUE);
					if(company.getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY))
						company.setMandatePanelsOn(((String)jsonObject.get("mandatePanelsOn")).equals("1") ? true : false);
					
					this.swpService.updateRecord(company);
					
					
					if(company!=null)
					{
						
						handleAudit("Company Update", Long.toString(company.getId()), new Timestamp((new Date()).getTime()), 
							portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
						
						
					}
					this.swpService.deleteRecord(approvalFlowTransit);
					portletState.addSuccess(aReq, "Company details for - " + company.getCompanyName() + " - has been saved successfully!", portletState);
					aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
				}else
				{
	
					portletState.addError(aReq, "The Company details were not saved! Please select a valid company before editing it.", portletState);
					aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
				}
			
		}catch(NumberFormatException e)
		{
			portletState.addError(aReq, "Select a valid maximum number of authorizations for this company. Must be a valid number", portletState);
			aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companycreation/stepone.jsp");
			e.printStackTrace();
		}catch(Exception e)
		{
			portletState.addError(aReq, "Issues where encountered while creation new company. Contact Technical team for assistance", portletState);
			aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companycreation/stepone.jsp");
			e.printStackTrace();
		}
	}

	private void doUnblockCompany(ApprovalFlowPortletState portletState,
			ApprovalFlowTransit approvalFlowTransit, ActionRequest aReq,
			ActionResponse aRes) {
		// TODO Auto-generated method stub
		String portalUserData = approvalFlowTransit.getObjectData();
		try
		{
			JSONObject jsonObject = new JSONObject(portalUserData);
			Company company = null;
			if(approvalFlowTransit.getEntityId()!=null)
			{
//				try{
//					Long id = Long.valueOf((String)jsonObject.get("company"));
					company = (Company)portletState.getApprovalFlowPortletUtil().getEntityObjectById(Company.class, approvalFlowTransit.getEntityId());

					if(company!=null)
					{
						company.setStatus(CompanyStatusConstants.COMPANY_STATUS_ACTIVE);
						swpService.updateRecord(company);
						swpService.deleteRecord(approvalFlowTransit);
						aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
						portletState.addSuccess(aReq, "Company Profile was reactivated successfully", portletState);
					}else{
						aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
						portletState.addError(aReq, "Company Profile Reactivation Request was not successfully Approved. The bank staff profile has not been suspended..", portletState);
					}
//				}catch(NumberFormatException e){
//					e.printStackTrace();
//				}
			}
				
			
		}catch(JSONException e)
		{
			portletState.addError(aReq, "Interpreting user data failed. The company data provided by the initiating officer has an invalid structure. " +
					"Disapprove this approval item.", portletState);
			aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
		}
	}

	private void doBlockCompany(ApprovalFlowPortletState portletState,
			ApprovalFlowTransit approvalFlowTransit, ActionRequest aReq,
			ActionResponse aRes) {
		// TODO Auto-generated method stub
		log.info("Approve Now = " + 25);
		String portalUserData = approvalFlowTransit.getObjectData();
		try
		{
			JSONObject jsonObject = new JSONObject(portalUserData);
			Company company = null;
			if(approvalFlowTransit.getEntityId()!=null)
			{
				log.info("Approve Now = " + 26);
//				try{
					//Long id = Long.valueOf((String)jsonObject.get("company"));
					company = (Company)portletState.getApprovalFlowPortletUtil().getEntityObjectById(Company.class, approvalFlowTransit.getEntityId());

					if(company!=null)
					{
						log.info("Approve Now = " + 27);
						company.setStatus(CompanyStatusConstants.COMPANY_STATUS_SUSPENDED);
						if(company.getClearingAgent()==null)
							company.setClearingAgent(Boolean.FALSE);
						
						swpService.updateRecord(company);
						swpService.deleteRecord(approvalFlowTransit);
						aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
						portletState.addSuccess(aReq, "Company Profile was suspended successfully.", portletState);
					}else{
						aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
						portletState.addError(aReq, "Company Profile Suspension Request was not successfully Approved. The bank staff profile has not been suspended..", portletState);
					}
//				}catch(NumberFormatException e){
//					e.printStackTrace();
//				}
			}
				
			
		}catch(JSONException e)
		{
			portletState.addError(aReq, "Interpreting user data failed. The company data provided by the initiating officer has an invalid structure. " +
					"Disapprove this approval item.", portletState);
			aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
		}
	}
	
	
	private void doDeleteCompany(ApprovalFlowPortletState portletState,
			ApprovalFlowTransit approvalFlowTransit, ActionRequest aReq,
			ActionResponse aRes) {
		// TODO Auto-generated method stub
		String portalUserData = approvalFlowTransit.getObjectData();
		try
		{
			JSONObject jsonObject = new JSONObject(portalUserData);
			Company company = null;
			if(approvalFlowTransit.getEntityId()!=null)
			{
//				try{
					company = (Company)portletState.getApprovalFlowPortletUtil().getEntityObjectById(Company.class, approvalFlowTransit.getEntityId());

					if(company!=null)
					{
						company.setStatus(CompanyStatusConstants.COMPANY_STATUS_DELETED);
						swpService.updateRecord(company);
						swpService.deleteRecord(approvalFlowTransit);
						aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
						portletState.addSuccess(aReq, "Company Profile was deleted successfully", portletState);
					}else{
						aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
						portletState.addError(aReq, "Company Profile Deletion Request was not successfully Approved. The company profile has not been deleted..", portletState);
					}
//				}catch(NumberFormatException e){
//					e.printStackTrace();
//				}
			}
				
			
		}catch(JSONException e)
		{
			portletState.addError(aReq, "Interpreting user data failed. The company data provided by the initiating officer has an invalid structure. " +
					"Disapprove this approval item.", portletState);
			aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
		}
	}

	private void doApproveCompany(ApprovalFlowPortletState portletState,
			ApprovalFlowTransit approvalFlowTransit, ActionRequest aReq,
			ActionResponse aRes) {
		// TODO Auto-generated method stub
		String portalUserData = approvalFlowTransit.getObjectData();
		try
		{
			log.info(1);
			JSONObject jsonObject = new JSONObject(portalUserData);
			log.info(2);
			
			log.info(7);
			Calendar c1 = GregorianCalendar.getInstance();
			c1.set(1980, Calendar.JANUARY, 1);
			Date d =c1.getTime();
			Timestamp dob = new Timestamp(d.getTime());
			log.info(8);
			Company co = handleCreateNewCompany((String)jsonObject.get("bankNumber"), (String)jsonObject.get("line1addressofcompany"), (String)jsonObject.get("line2addressofcompany"), 
					(String)jsonObject.get("companyname"), (String)jsonObject.get("companyrcnumber"), (String)jsonObject.get("companyemailaddress"), (String)jsonObject.get("companycontactphonenumber"), 
					(String)jsonObject.get("selectedBankBranchId"), (String)jsonObject.get("selectedCompanyType"), 
					((String)jsonObject.get("selectedCompanyType")).equalsIgnoreCase(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY.getValue()) ? (String)jsonObject.get("selectedCompanyClass") : null, 
					(String)jsonObject.get("tpin"),(String)jsonObject.get("mandatePanelsOn"),  
					portletState, aReq, aRes);
			
			if(co!=null)
			{
				swpService.deleteRecord(approvalFlowTransit);
				aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				portletState.addSuccess(aReq, "Company Profile was approved and created successfully. Appropriate email & sms notifications have been sent to the company email with instructions on how to log into the application", portletState);
			}else{
				aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
				portletState.addError(aReq, "Company Profile Creation Request was not successfully Approved. The bank staff profile has not been created..", portletState);
			}
		}catch(JSONException e)
		{
			e.printStackTrace();
			portletState.addError(aReq, "Interpreting user data failed. The company data provided by the initiating officer has an invalid structure. " +
					"Disapprove this approval item.", portletState);
			aRes.setRenderParameter("jspPage", "/html/approvalflowportlet/itemboard.jsp");
		}
	}

	private void approvalPortalUser(ApprovalFlowPortletState portletState,
			ApprovalFlowTransit approvalFlowTransit, ActionRequest aReq, ActionResponse aRes) {
		// TODO Auto-generated method stub
		log.info("Approve Now = " + 4);
		log.info("approvalFlowTransit.getActionType() = " + approvalFlowTransit.getActionType() + " && = " + approvalFlowTransit.getActionType().getValue());
		if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.PORTAL_USER_ACTION_CREATE))
		{
			log.info("Approve Now = " + 5);
			doApproveCreateNewPortalUser(portletState, approvalFlowTransit, aReq, aRes);
			portletState.setCurrentTab(VIEW_TABS.VNEW_USER);
			
			if(portletState.getApprovalDirect().getValue().equals("0"))
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser().getRoleType().getRoleTypeName(),
						ActionTypeConstants.PORTAL_USER_ACTION_CREATE);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			else
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser(),
						ActionTypeConstants.PORTAL_USER_ACTION_CREATE);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
		}
		if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.PORTAL_USER_ACTION_DELETE))
		{
			log.info("Approve Now = " + 51);
			doApproveDeletePortalUser(portletState, approvalFlowTransit, aReq, aRes);
			portletState.setCurrentTab(VIEW_TABS.VDELETE_USER);
			
			
			if(portletState.getApprovalDirect().getValue().equals("0"))
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser().getRoleType().getRoleTypeName(),
						ActionTypeConstants.PORTAL_USER_ACTION_DELETE);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			else
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser(),
						ActionTypeConstants.PORTAL_USER_ACTION_DELETE);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
		}
		if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.PORTAL_USER_ACTION_BLOCK))
		{
			doApproveBlockPortalUser(portletState, approvalFlowTransit, aReq, aRes);
			portletState.setCurrentTab(VIEW_TABS.VBLOCK_USER);

			if(portletState.getApprovalDirect().getValue().equals("0"))
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser().getRoleType().getRoleTypeName(),
						ActionTypeConstants.PORTAL_USER_ACTION_BLOCK);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			else
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser(),
						ActionTypeConstants.PORTAL_USER_ACTION_BLOCK);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
		}
		if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.PORTAL_USER_ACTION_UNBLOCK))
		{
			doApproveUnBlockPortalUser(portletState, approvalFlowTransit, aReq, aRes);
			portletState.setCurrentTab(VIEW_TABS.VUNBLOCK_USER);
			
			if(portletState.getApprovalDirect().getValue().equals("0"))
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser().getRoleType().getRoleTypeName(),
						ActionTypeConstants.PORTAL_USER_ACTION_UNBLOCK);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			else
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser(),
						ActionTypeConstants.PORTAL_USER_ACTION_UNBLOCK);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
		}
		if(approvalFlowTransit.getActionType().equals(ActionTypeConstants.PORTAL_USER_ACTION_UPDATE))
		{
			doApproveUpdatePortalUser(portletState, approvalFlowTransit, aReq, aRes);
			portletState.setCurrentTab(VIEW_TABS.VUPDATE_USER);
			
			
			

			if(portletState.getApprovalDirect().getValue().equals("0"))
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsCreatedByRoleType(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser().getRoleType().getRoleTypeName(),
						ActionTypeConstants.PORTAL_USER_ACTION_UPDATE);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
			else
			{
				Collection<ApprovalFlowTransit> approvalItems = portletState.getApprovalFlowPortletUtil().getApprovalFlowTransitListingsToBeWorkedOnByPortalUser(
						portletState.getSelectedApprovalItemType(),
						portletState.getPortalUser(),
						ActionTypeConstants.PORTAL_USER_ACTION_UPDATE);
				portletState.setAllApprovalFlowTransitListing(approvalItems);
			}
		}
			
	}
	
	
	private void doApproveUpdatePortalUser(
			ApprovalFlowPortletState portletState,
			ApprovalFlowTransit approvalFlowTransit, ActionRequest aReq,
			ActionResponse aRes) {
		// TODO Auto-generated method stub
		String portalUserData = approvalFlowTransit.getObjectData();
		try
		{
			AuditTrail auditTrail = new AuditTrail();
			JSONObject jsonObject = new JSONObject(portalUserData);
			PortalUser pu = (PortalUser)portletState.getApprovalFlowPortletUtil().getEntityObjectById(PortalUser.class, approvalFlowTransit.getEntityId());
			handleUpdateUserOrbitaAccount(pu,  (String)jsonObject.get("firstName"),  
					(String)jsonObject.get("addressLine1"),  (String)jsonObject.get("addressLine2"),  
					(String)jsonObject.get("alternativeEmail1"),  (String)jsonObject.get("alternativeEmail2"),  
					(String)jsonObject.get("alternativeMobile1"),  (String)jsonObject.get("alternativeMobile2"),  
					(String)jsonObject.get("mobile"),  "",  (String)jsonObject.get("lastName"),  (String)jsonObject.get("email"),  
					auditTrail,  serviceContext,  swpService, 
					false,  true,  portletState.getNotifyCorporateFirmEmail().getValue().equals("1") ? true : false,  
					portletState.getNotifyCorporateFirmSms().getValue().equals("1") ? true : false,  
					portletState.getSystemUrl().getValue(),  
					portletState, aReq, aRes);
			pu.setFirstName((String)jsonObject.get("firstName"));
			pu.setLastName((String)jsonObject.get("lastName"));
			pu.setAddressLine1((String)jsonObject.get("addressLine1"));
			pu.setAddressLine2((String)jsonObject.get("addressLine2"));
			pu.setFirstAlternativeEmailAddress((String)jsonObject.get("alternativeEmail1"));
			pu.setSecondAlternativeEmailAddress((String)jsonObject.get("alternativeEmail2"));
			pu.setFirstAlternativeMobileNumber((String)jsonObject.get("alternativeMobile1"));
			pu.setSecondAlternativeMobileNumber((String)jsonObject.get("alternativeMobile2"));
			pu.setMobileNumber((String)jsonObject.get("mobile"));
			pu.setEmailAddress((String)jsonObject.get("email"));
			swpService.updateRecord(pu);
			
			
			swpService.deleteRecord(approvalFlowTransit);
			
		}catch(JSONException e)
		{
			e.printStackTrace();
			portletState.addError(aReq, "Bank Staff Profile Updated Request was not successfully Approved. The bank staff profile has not been updated..", portletState);
		}
				
	}
			
			
	public static User handleUpdateUserOrbitaAccount(PortalUser user, String firstname, 
			String addressLine1, String addressLine2, 
			String firstAlternativeEmailAddress, String secondAlternativeEmailAddress, 
			String firstAlternativeMobileNumber, String secondAlternativeMobileNumber, 
			String mobileNumber, String middlename, String surname, String email, 
			AuditTrail auditTrail, ServiceContext serviceContext, SwpService sService,
			boolean passwordReset, boolean active, boolean sendEmail, boolean sendSms, String systemUrl, 
			ApprovalFlowPortletState portletState, ActionRequest aReq, ActionResponse aRes) {			
		Logger log = Logger.getLogger(UserManagementSystemAdminPortlet.class);
		log.info("Update Orbita User Account");
		Logger log1 = Logger.getLogger(UserManagementSystemAdminPortlet.class);
		PortalUser existingUser = user;
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
					portletState.addSuccess(aReq, "Bank Staff Profile Reactivation Request was successfully Approved.", portletState);
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
				portletState.addSuccess(aReq, "Bank Staff Profile Reactivation Request was successfully Approved.", portletState);

				log.info(6);
			}
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			portletState.addError(aReq, "Bank Staff Profile Reactivation Request was not successfully Approved.", portletState);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			portletState.addError(aReq, "Bank Staff Profile Reactivation Request was not successfully Approved.", portletState);
		}
		

		sService.updateRecord(existingUser);
		return updatedUser;		
	}
			

	private void doApproveUnBlockPortalUser(
			ApprovalFlowPortletState portletState,
			ApprovalFlowTransit approvalFlowTransit, ActionRequest aReq,
			ActionResponse aRes) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		String portalUserData = approvalFlowTransit.getObjectData();
		try
		{
			JSONObject jsonObject = new JSONObject(portalUserData);
			PortalUser pu = (PortalUser)portletState.getApprovalFlowPortletUtil().getEntityObjectById(PortalUser.class, approvalFlowTransit.getEntityId());
			try {
				User lpUser = UserLocalServiceUtil.getUserById(pu.getUserId());
				if(pu!=null && lpUser!=null)
				{
					UserLocalServiceUtil.updateStatus(lpUser.getUserId(), 1);
					pu.setStatus(PortalUserStatusConstants.PORTAL_USER_STATUS_ACTIVE);
					swpService.updateRecord(pu);
					swpService.deleteRecord(approvalFlowTransit);
					
					try {
						jsonObject = new JSONObject(approvalFlowTransit.getObjectData());
						if(jsonObject!=null)
						{
						
							
							emailer.emailDisapproval(
								approvalFlowTransit.getPortalUser().getEmailAddress(),
								approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
								"Your request to reactivate the user profile account - " + pu.getFirstName() + " " + pu.getLastName() + " - " +
								"was approved<br>", portletState.getSystemUrl().getValue(), 
								portletState.getApplicationName().getValue() + " - Approval of Request to reactivate a user profile account", portletState.getApplicationName().getValue());
						
							String message = "Your request to reactivate a user profile account was aproved";

							new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
								portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
						}
					}catch(JSONException e)
					{
						e.printStackTrace();
					}
					portletState.addSuccess(aReq, "Bank Staff Profile Reactivation Request Successfully Approved. The bank staff profile has been reactivated..", portletState);
				}else{
					portletState.addError(aReq, "Bank Staff Profile Reactivation Request was not successfully Approved. The bank staff profile has not been reactivated..", portletState);
				}
			} catch (PortalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				portletState.addError(aReq, "Bank Staff Profile Reactivation Request was not successfully Approved. The bank staff profile has not been reactivated..", portletState);
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				portletState.addError(aReq, "Bank Staff Profile Reactivation Request was not successfully Approved. The bank staff profile has not been reactivated..", portletState);
			}
			
			
		}catch(JSONException e)
		{
			portletState.addError(aReq, "Interpreting user data failed. The user data provided by the initiating officer has an invalid structure. " +
					"Disapprove this approval item.", portletState);
		}
	}

	private void doApproveBlockPortalUser(
			ApprovalFlowPortletState portletState,
			ApprovalFlowTransit approvalFlowTransit, ActionRequest aReq,
			ActionResponse aRes) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		String portalUserData = approvalFlowTransit.getObjectData();
		try
		{
			JSONObject jsonObject = new JSONObject(portalUserData);
			PortalUser pu = (PortalUser)portletState.getApprovalFlowPortletUtil().getEntityObjectById(PortalUser.class, approvalFlowTransit.getEntityId());
			try {
				User lpUser = UserLocalServiceUtil.getUserById(pu.getUserId());
				if(pu!=null && lpUser!=null)
				{
					UserLocalServiceUtil.updateStatus(lpUser.getUserId(), 1);
					
					
					pu.setStatus(PortalUserStatusConstants.PORTAL_USER_STATUS_SUSPENDED);
					swpService.updateRecord(pu);
					swpService.deleteRecord(approvalFlowTransit);
					
					try {
						jsonObject = new JSONObject(approvalFlowTransit.getObjectData());
						if(jsonObject!=null)
						{
						
							
							emailer.emailDisapproval(
								approvalFlowTransit.getPortalUser().getEmailAddress(),
								approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
								"Your request to suspend the user profile account - " + pu.getFirstName() + " " + pu.getLastName() + " - " +
								"was approved<br>", portletState.getSystemUrl().getValue(), 
								portletState.getApplicationName().getValue() + " - Approval of Request to suspend a user profile account", portletState.getApplicationName().getValue());
							
							String message = "Your request to suspend a user profile account was aproved";

							new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
								portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
						
						}
					}catch(JSONException e)
					{
						e.printStackTrace();
					}
					portletState.addSuccess(aReq, "Bank Staff Profile Suspension Request Successfully Approved. The bank staff profile has been blocked..", portletState);
				}else{
					portletState.addError(aReq, "Bank Staff Profile Suspension Request was not successfully Approved. The bank staff profile has not been suspended..", portletState);
				}
			} catch (PortalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				portletState.addError(aReq, "Bank Staff Profile Suspension Request was not successfully Approved. The bank staff profile has not been suspended..", portletState);
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				portletState.addError(aReq, "Bank Staff Profile Suspension Request was not successfully Approved. The bank staff profile has not been suspended..", portletState);
			}
			
			
		}catch(JSONException e)
		{
			portletState.addError(aReq, "Interpreting user data failed. The user data provided by the initiating officer has an invalid structure. " +
					"Disapprove this approval item.", portletState);
		}
	}

	private void doApproveDeletePortalUser(
			ApprovalFlowPortletState portletState,
			ApprovalFlowTransit approvalFlowTransit, ActionRequest aReq,
			ActionResponse aRes) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		String portalUserData = approvalFlowTransit.getObjectData();
		log.info("Approve Now = " + 53 + " && = " + portalUserData);
		try
		{
			log.info("Approve Now = " + 54);
			JSONObject jsonObject = new JSONObject(portalUserData);
			PortalUser pu = (PortalUser)portletState.getApprovalFlowPortletUtil().getEntityObjectById(PortalUser.class, approvalFlowTransit.getEntityId());
			log.info("Approve Now = " + 55);
			
			if(pu!=null)
			{
				log.info("Approve Now = " + 56);
				try {
					UserLocalServiceUtil.updateStatus(pu.getUserId(), 1);
					log.info("Approve Now = " + 57);
					pu.setStatus(PortalUserStatusConstants.PORTAL_USER_DELETED);
					swpService.updateRecord(pu);
					handleAudit("PORTAL USER DELETE", "Delete this portalUser", new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
					
					swpService.deleteRecord(approvalFlowTransit);
					
					try {
						jsonObject = new JSONObject(approvalFlowTransit.getObjectData());
						if(jsonObject!=null)
						{
						
							
							emailer.emailDisapproval(
								approvalFlowTransit.getPortalUser().getEmailAddress(),
								approvalFlowTransit.getPortalUser().getFirstName(), approvalFlowTransit.getPortalUser().getLastName(), 
								"Your request to delete the user profile account - " + pu.getFirstName() + " " + pu.getLastName() + " - " +
								"was approved<br>", portletState.getSystemUrl().getValue(), 
								portletState.getApplicationName().getValue() + " - Approval of Request to delete a user profile account", portletState.getApplicationName().getValue());
							
							String message = "Your request to delete a user profile account was aproved";

							new SendSms(approvalFlowTransit.getPortalUser().getMobileNumber(), message, 
								portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
						
						}
					}catch(JSONException e)
					{
						e.printStackTrace();
					}
					portletState.addSuccess(aReq, "Bank Staff Profile Deletion Request Successfully Approved. The bank staff profile has been deleted.", portletState);
				} catch (PortalException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					portletState.addError(aReq, "Bank Staff Profile Creation Request was not successfully Approved. The bank staff profile has not been created..", portletState);
				} catch (SystemException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					portletState.addError(aReq, "Bank Staff Profile Creation Request was not successfully Approved. The bank staff profile has not been created..", portletState);
				}
				
				
				
			}else{
				portletState.addError(aReq, "Bank Staff Profile Creation Request was not successfully Approved. The bank staff profile has not been created..", portletState);
			}
		}catch(JSONException e)
		{
			portletState.addError(aReq, "Interpreting user data failed. The user data provided by the initiating officer has an invalid structure. " +
					"Disapprove this approval item.", portletState);
		}
		
	}

	private void doApproveCreateNewPortalUser(
			ApprovalFlowPortletState portletState,
			ApprovalFlowTransit approvalFlowTransit, ActionRequest aReq,
			ActionResponse aRes) {
		// TODO Auto-generated method stub
		log.info("Approve Now = " + 6);
		String portalUserData = approvalFlowTransit.getObjectData();
		try
		{
			log.info("Approve Now = " + 7);
			JSONObject jsonObject = new JSONObject(portalUserData);
			Company company = null;
			if(jsonObject.has("company"))
			{
				try{
					Long id = Long.valueOf(Integer.toString(jsonObject.getInt("company")));
					log.info("Approve Now = " + id);
					company = (Company)portletState.getApprovalFlowPortletUtil().getEntityObjectById(Company.class, id);
				}catch(NumberFormatException e){
					e.printStackTrace();
				}
			}
			
			Calendar c1 = GregorianCalendar.getInstance();
			c1.set(1980, Calendar.JANUARY, 1);
			Date date =c1.getTime();
			Timestamp dob = new Timestamp(date.getTime());
			RoleType roleType = portletState.getApprovalFlowPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.fromString((String)jsonObject.get("roleType")));
			log.info("Approve Now = " + 8);
			PortalUser pu = handleCreateNewPortalUser((String)jsonObject.get("firstName"), 
					(String)jsonObject.get("lastName"), 
					(String)jsonObject.get("email"),
					(String)jsonObject.get("alternativeEmail1"),
					(String)jsonObject.get("alternativeEmail2"),
					(String)jsonObject.get("mobile"), 
					(String)jsonObject.get("alternativeMobile1"), 
					(String)jsonObject.get("alternativeMobile2"),
					(String)jsonObject.get("addressLine1"),
					(String)jsonObject.get("addressLine2"),
					dob,
					roleType,
					PortalUserStatusConstants.PORTAL_USER_STATUS_ACTIVE,
					company, portletState, 
					aReq,
					aRes,
					jsonObject.has("userCRUD") ? (String)jsonObject.get("userCRUD") : null, 
					jsonObject.has("companyCRUD") ? (String)jsonObject.get("companyCRUD") : null);
			if(pu!=null)
			{
				swpService.deleteRecord(approvalFlowTransit);
				portletState.addSuccess(aReq, "Bank Staff Profile Creation Request Successfully Approved. The bank staff profile has been created. " +
						"Appropriate notifications containing the bank staff login details have been sent to the bank staff contact email/mobile number.", portletState);
			}else{
				portletState.addError(aReq, "Bank Staff Profile Creation Request was not successfully Approved. The bank staff profile has not been created..", portletState);
			}
		}catch(JSONException e)
		{
			portletState.addError(aReq, "Interpreting user data failed. The user data provided by the initiating officer has an invalid structure. " +
					"Disapprove this approval item.", portletState);
		}
		
	}

	private PortalUser handleCreateNewPortalUser(
			String firstname,
			String lastname,
			String firstemail,
			String secondemail,
			String thirdemail,
			String firstmobile,
			String secondmobile,
			String thirdmobile,
			String AddressLine1,
			String AddressLine2,
			Timestamp dob,
			RoleType roleType,
			PortalUserStatusConstants portalUserInactive, Company company, 
			ApprovalFlowPortletState portletState, 
			ActionRequest aReq,
			ActionResponse aRes,
			String userCRUD, 
			String companyCRUD) {
		
		log.info("Approve Now = " + 9);
		// TODO Auto-generated method stub
		PortalUser pu = new PortalUser();
		pu.setAddressLine1(AddressLine1);
		pu.setAddressLine2(AddressLine2);
		pu.setCompany(company);
		pu.setDateOfBirth(dob);
		pu.setEmailAddress(firstemail);
		pu.setFirstAlternativeEmailAddress(secondemail);
		pu.setSecondAlternativeEmailAddress(thirdemail);
		pu.setMobileNumber(firstmobile);
		pu.setFirstAlternativeMobileNumber(secondmobile);
		pu.setSecondAlternativeMobileNumber(thirdmobile);
		pu.setFirstName(firstname);
		pu.setLastName(lastname);
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

		
		log.info("Approve Now = " + 10);
		handleCreateUserOrbitaAccount(pu, 
				firstname, 
				"",
				lastname,
				firstemail,
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

	
	
	
	
	private AuthorizePanelCombination handleApproveAuthorizePanelCombination(String selectedMapPosition, 
			String selectedMapPanel, String selectedMapPanelPortalUser,
			ApprovalFlowPortletState portletState, ActionRequest aReq, ActionResponse aRes)
	{
		Logger log = Logger.getLogger(ApprovalFlowPortlet.class);
		try
		{
			boolean sendEmail = portletState.getNotifyCorporateFirmEmail().getValue().equals("1") ? true : false;
			boolean sendSms = portletState.getNotifyCorporateFirmSms().getValue().equals("1") ? true : false;
			String systemUrl = portletState.getSystemUrl().getValue();
			
			AuthorizePanelCombination apc = new AuthorizePanelCombination();
			apc.setAuthorizePanel((AuthorizePanel)portletState.getApprovalFlowPortletUtil().getEntityObjectById(AuthorizePanel.class, Long.valueOf(selectedMapPanel)));
			apc.setDateCreated(new Timestamp((new Date()).getTime()));
			apc.setPortalUser((PortalUser)portletState.getApprovalFlowPortletUtil().getEntityObjectById(PortalUser.class, Long.valueOf(selectedMapPanelPortalUser)));
			apc.setStatus(SmartPayConstants.AUTHORIZE_PANEL_COMBINATION_STATUS_ACTIVE);
			apc.setPosition(Integer.valueOf(selectedMapPosition));
			
			apc = (AuthorizePanelCombination)swpService.createNewRecord(apc);
			
			if(apc!=null && sendEmail)
			{
				//sendEmailForApprovedMandatePanel();
			}
			if(apc!=null && sendSms)
			{
				//sendEmailForApprovedMandatePanel();
			}

			return apc;
			
		}catch(NumberFormatException e)
		{
			portletState.addError(aReq, "Select a valid maximum number of authorizations for this company. Must be a valid number", portletState);
			aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companycreation/stepone.jsp");
			e.printStackTrace();
			return null;
		}catch(Exception e)
		{
			portletState.addError(aReq, "Issues where encountered while creation new company. Contact Technical team for assistance", portletState);
			aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companycreation/stepone.jsp");
			e.printStackTrace();
			return null;
		}
	}
	
	private Company handleCreateNewCompany(String bankNumber, String line1addressofcompany, String line2addressofcompany, 
			String companyname, String companyrcnumber, String companyemailaddress, String companycontactphonenumber, 
			String selectedBankBranchId, String selectedCompanyType, String selectedCompanyClass, String tpin, String mandatePanelsOn,
			ApprovalFlowPortletState portletState, ActionRequest aReq, ActionResponse aRes)
	{
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		Logger log = Logger.getLogger(ApprovalFlowPortlet.class);
		try
		{
			boolean sendEmail = portletState.getNotifyCorporateFirmEmail().getValue().equals("1") ? true : false;
			boolean sendSms = portletState.getNotifyCorporateFirmSms().getValue().equals("1") ? true : false;
			String systemUrl = portletState.getSystemUrl().getValue();
			
			Company company = new Company();
			company.setAccountNumber(bankNumber.trim());
			company.setAddressLine1(line1addressofcompany.trim());
			company.setAddressLine2(line2addressofcompany.trim());
			company.setCompanyName(companyname);
			company.setCompanyRCNumber(companyrcnumber);
			company.setEmailAddress(companyemailaddress);
			company.setMobileNumber(companycontactphonenumber);
			company.setBankBranches((BankBranches)portletState.getApprovalFlowPortletUtil().getEntityObjectById(BankBranches.class, 
					Long.valueOf(selectedBankBranchId)));
			company.setCompanyType(CompanyTypeConstants.fromString(selectedCompanyType));
			company.setStatus(CompanyStatusConstants.COMPANY_STATUS_ACTIVE);
			company.setClearingAgent(selectedCompanyClass==null ? Boolean.FALSE : (selectedCompanyClass.equals("0") ? Boolean.FALSE : Boolean.TRUE));
			company.setCreatedByPortalUserId(portletState.getPortalUser().getId());
			company.setMandatePanelsOn(mandatePanelsOn.equals("0") ? false : true);
			//company.setCreateMandatePanel(portletState.getSelectedCreateMandatePanels()!=null && portletState.getSelectedCreateMandatePanels().equals("1") ? Boolean.TRUE : Boolean.FALSE);
			
			
			company = (Company)this.swpService.createNewRecord(company);
			if(company!=null)
			{
				handleAudit("Company Creation", Long.toString(company.getId()), new Timestamp((new Date()).getTime()), 
					portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				
				log.info("We are in create a portal user1");
				Calendar c1 = GregorianCalendar.getInstance();
				c1.set(1980, Calendar.JANUARY, 1);
				Date date =c1.getTime();
				Timestamp dob = new Timestamp(date.getTime());
					
					TpinInfo tpinInfo = new TpinInfo();
					tpinInfo.setCompany(company);
					tpinInfo.setDateUpdated(new Timestamp((new Date()).getTime()));
					tpinInfo.setTpin(tpin);
					tpinInfo = (TpinInfo)this.swpService.createNewRecord(tpinInfo);
					
				Settings settingEmail = portletState.getNotifyCorporateIndividualEmail();
				Settings settingSms = portletState.getNotifyCorporateIndividualSMS();
				Settings settingSystemUrl = portletState.getSystemUrl();
				
				
				if(sendEmail)
				{
								
					SendMail sm1 = emailer.emailNewCorporateCompany(company.getEmailAddress(), 
							company.getCompanyName(),
							systemUrl, 
							selectedCompanyType,
							tpinInfo.getTpin(),
							company.getAccountNumber(),
							company.getBankBranches().getName(),
							company.getMobileNumber(),
							portletState.getApplicationName().getValue() + " New Company Account Creation for " +
							"" + company.getCompanyName(), 
							selectedCompanyClass==null ? selectedCompanyType : (selectedCompanyClass.equals("0") ? "Sole Trader" : "Agent"), portletState.getApplicationName().getValue());
					
				}
				if(sendSms){
					
					try{
					//new SendSms(createdUser.getMobileNumber(), message, "C_Portal");
							
						String message = "Hello, Your Corporate Company account for " + 
								company.getCompanyName() + " has been " +
								"successfully created on " + systemUrl;
						new SendSms(company.getMobileNumber(), message, portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
					}catch(Exception e){
						log.error("error sending sms ",e);
					}
				}
			}
			
			portletState.setCurrentTab(VIEW_TABS.VNEW_COMPANY);
			return company;
			
		}catch(NumberFormatException e)
		{
			portletState.addError(aReq, "Select a valid maximum number of authorizations for this company. Must be a valid number", portletState);
			aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companycreation/stepone.jsp");
			e.printStackTrace();
			return null;
		}catch(Exception e)
		{
			portletState.addError(aReq, "Issues where encountered while creation new company. Contact Technical team for assistance", portletState);
			aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companycreation/stepone.jsp");
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static PortalUser handleCreateUserOrbitaAccount(PortalUser user, String firstname, String middlename, String surname, String email, 
			long[] communities, AuditTrail auditTrail, ServiceContext serviceContext, SwpService sService, long loggedInUserId,
			boolean passwordReset, boolean active, boolean sendEmail, boolean sendSms, String systemUrl, 
			ApprovalFlowPortletState portletState, ActionRequest aReq, ActionResponse aRes, String userCRUD, String companyCRUD) {			
		
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		Logger log1 = Logger.getLogger(ApprovalFlowPortletState.class);
		log1.info("Approve Now = " + 11);
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
					
					if(createdUser.getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_NON_ADMINISTRATOR_PERSONNEL) || 
							createdUser.getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR) || 
							createdUser.getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_RETAIL_STAFF))
					{
						emailer.emailNewCorporateIndividualAccount(emailAddress, 
							createdUser.getCompany().getCompanyName(), password1, portletState.getSystemUrl().getValue(), 
							createdUser.getFirstName(), createdUser.getLastName(), createdUser.getRoleType().getRoleTypeName().getValue(), 
							"New eTax Corporate Individual Account for " + createdUser.getCompany().getCompanyName(), portletState.getApplicationName().getValue());
					}else
					{
						emailer.emailNewBankStaffAccount(emailAddress, 
								"", password1, portletState.getSystemUrl().getValue(), 
								createdUser.getFirstName(), createdUser.getLastName(), createdUser.getRoleType().getRoleTypeName().getValue(), 
								"New eTax Bank Staff Account Created for you", portletState.getApplicationName().getValue());
					}
							
						
					
				}
				if(sendSms){
					String message = "Hello, Your Online Account has been " +
							"successfully created on " + systemUrl + ". Your login email is " +  
							emailAddress + " and password: " + password1;
					try{
						new SendSms(createdUser.getEmailAddress(), message, 
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
	
	
	
	
	
	public static boolean addUserToCommmunity(long userId, long communityId) {
		boolean status = false;
		try {
			Logger logger = Logger.getLogger(ApprovalFlowPortletState.class);
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
