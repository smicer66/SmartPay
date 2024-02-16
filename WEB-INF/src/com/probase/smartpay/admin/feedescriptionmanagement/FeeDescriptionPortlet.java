package com.probase.smartpay.admin.feedescriptionmanagement;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;
import org.json.JSONException;

import smartpay.audittrail.AuditTrail;
import smartpay.entity.ApprovalFlowTransit;
import smartpay.entity.AuthorizePanel;
import smartpay.entity.AuthorizePanelCombination;
import smartpay.entity.BankBranches;
import smartpay.entity.Company;
import smartpay.entity.CompanyCRUDRights;
import smartpay.entity.CompanyFeeDescription;
import smartpay.entity.FeeDescription;
import smartpay.entity.FinancialAmountRestriction;
import smartpay.entity.PortalUser;
import smartpay.entity.PortalUserCRUDRights;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.enumerations.ActionTypeConstants;
import smartpay.entity.enumerations.CompanyStatusConstants;
import smartpay.entity.enumerations.PortalUserStatusConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.service.SwpService;


import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.probase.smartpay.admin.feedescriptionmanagement.FeeDescriptionPortletState.FEE_DESCRIPTION;
import com.probase.smartpay.admin.feedescriptionmanagement.FeeDescriptionPortletState.FEE_DESCRIPTION_VIEW;
import com.probase.smartpay.admin.feedescriptionmanagement.FeeDescriptionPortletState.NAVIGATE;
import com.probase.smartpay.admin.feedescriptionmanagement.FeeDescriptionPortletState.VIEW_TABS;
import com.probase.smartpay.commins.ComminsApplicationState;
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
 * Portlet implementation class FeeDescriptionPortlet
 */
public class FeeDescriptionPortlet extends MVCPortlet {
 
	private Logger log = Logger.getLogger(FeeDescriptionPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
	FeeDescriptionPortletUtil util = FeeDescriptionPortletUtil.getInstance();
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
		FeeDescriptionPortletState portletState = 
				FeeDescriptionPortletState.getInstance(renderRequest, renderResponse);

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
		
		FeeDescriptionPortletState portletState = FeeDescriptionPortletState.getInstance(aReq, aRes);
		
		
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
        if(action.equalsIgnoreCase(FEE_DESCRIPTION.LOGIN_STEP_TWO.name()))
        {
        	log.info("We are inside step two of login");
        	if(loginStepTwo(aReq, aRes, portletState.getPortalUser(), 
        			portletState.getCompanyCRUDRights(), swpService, portletState)==false)
        		portletState.addError(aReq, "Invalid login credentials!", portletState);
        	else
        	{
        		if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.CREATE_A_FEE_DESCRIPTION))
        		{
        			portletState.reinitializeForFeeDescription(portletState);
                	aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/createafeedescription.jsp");
                	portletState.setCurrentTab(VIEW_TABS.CREATE_A_FEE_DESCRIPTION);
        		}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.MAP_FEE_TO_COMPANY))
        		{
        			portletState.setAllFeeDescription(portletState.getFeeDescriptionPortletUtil().getFeeDescriptionListing(true));
                	portletState.setActiveCompanyFeeDesciptionListing(portletState.getFeeDescriptionPortletUtil().getAllCompanyFeeDescription());
                	aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/mapfeedescriptiontocompany.jsp");
                	portletState.setCurrentTab(VIEW_TABS.MAP_FEE_TO_COMPANY);
        		}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.MAPPED_FEES_TO_COMPANY))
        		{
        			portletState.setAllFeeDescription(portletState.getFeeDescriptionPortletUtil().getFeeDescriptionListing(true));
                	portletState.setActiveCompanyFeeDesciptionListing(portletState.getFeeDescriptionPortletUtil().getAllCompanyFeeDescription());
                	aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/mappedlisting.jsp");
                	portletState.setCurrentTab(VIEW_TABS.MAPPED_FEES_TO_COMPANY);
        		}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_FEE_DESCRIPTION_LISTINGS))
        		{
        			portletState.setAllFeeDescription(portletState.getFeeDescriptionPortletUtil().getFeeDescriptionListing(true));
                	aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/feedescriptionlisting.jsp");
                	portletState.setCurrentTab(VIEW_TABS.VIEW_FEE_DESCRIPTION_LISTINGS);
        		}else {
        			portletState.setAllFeeDescription(portletState.getFeeDescriptionPortletUtil().getFeeDescriptionListing(true));
                	aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/view.jsp");
        		}
        	}
        }
        if(action.equalsIgnoreCase(FEE_DESCRIPTION.CREATE_A_FEE_DESCRIPTION.name()))
        {
        	log.info("handle create a fee description post action");
        	handleCreateNewFeeDescription(aReq, aRes, portletState);
        }if(action.equalsIgnoreCase(FEE_DESCRIPTION.UPDATE_FEE_DESCRIPTION.name()))
        {
        	handleUpdateFeeDescription(aReq, aRes, portletState);
        }
        if(action.equalsIgnoreCase(FEE_DESCRIPTION.HANDLE_FEE_DESCRIPTION_LISTING.name()))
        {
        	handleFeeDescriptionListingAction(aReq, aRes, portletState);
        }if(action.equalsIgnoreCase(FEE_DESCRIPTION.MAP_FEE_TO_COMPANY.name()))
        {
        	handleMapFeeToCompany(aReq, aRes, portletState);
        }
        if(action.equalsIgnoreCase(FEE_DESCRIPTION.UPDATE_FEE_COMPANY_MAPPING.name()))
        {
        	handleUpdateMapFeeToCompany(aReq, aRes, portletState);
        }
        if(action.equalsIgnoreCase(NAVIGATE.NAVIGATE_ACTIONS.name()))
        {
        	handleNavigations(aReq, aRes, portletState);
        }if(action.equalsIgnoreCase(FEE_DESCRIPTION_VIEW.CREATE_A_FEE_DESCRIPTION.name()))
        {
        	portletState.reinitializeForFeeDescription(portletState);
        	aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/createafeedescription.jsp");
        	portletState.setCurrentTab(VIEW_TABS.CREATE_A_FEE_DESCRIPTION);
        }if(action.equalsIgnoreCase(FEE_DESCRIPTION_VIEW.VIEW_FEE_DESCRIPTION_LISTINGS.name()))
        {
        	portletState.setAllFeeDescription(portletState.getFeeDescriptionPortletUtil().getFeeDescriptionListing(true));
        	aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/feedescriptionlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.VIEW_FEE_DESCRIPTION_LISTINGS);
        }if(action.equalsIgnoreCase(FEE_DESCRIPTION_VIEW.MAP_FEE_TO_COMPANY_VIEW.name()))
        {
        	portletState.setAllFeeDescription(portletState.getFeeDescriptionPortletUtil().getFeeDescriptionListing(true));
        	portletState.setActiveCompanyFeeDesciptionListing(portletState.getFeeDescriptionPortletUtil().getAllCompanyFeeDescription());
        	aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/mapfeedescriptiontocompany.jsp");
        	portletState.setCurrentTab(VIEW_TABS.MAP_FEE_TO_COMPANY);
        }if(action.equalsIgnoreCase(FEE_DESCRIPTION_VIEW.MAPPED_FEES_TO_COMPANY.name()))
        {
        	portletState.setAllFeeDescription(portletState.getFeeDescriptionPortletUtil().getFeeDescriptionListing(true));
        	portletState.setActiveCompanyFeeDesciptionListing(portletState.getFeeDescriptionPortletUtil().getAllCompanyFeeDescription());
        	aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/mappedlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.MAPPED_FEES_TO_COMPANY);
        }
		
	}


	private boolean loginStepTwo(ActionRequest aReq, ActionResponse aRes,
			PortalUser portalUser, PortalUserCRUDRights portalUserCRUDRights,
			SwpService swpService2, FeeDescriptionPortletState portletState) {
		// TODO Auto-generated method stub
		String email2 = aReq.getParameter("usernameemail");
		log.info("email2 = "+ email2);
		String password = aReq.getParameter("password");
		log.info("password = " + password);
		
		ComminsApplicationState cappState = portletState.getCas();
		log.info("cappState  we just got the application state");
		
		
		
		boolean trueCheck = new Util().loginStepTwoForPortalUserManagement(portalUser, portalUserCRUDRights, 
				cappState, email2, password, swpService2);
		return trueCheck;
	}
	
	private boolean loginStepTwo(ActionRequest aReq, ActionResponse aRes,
			PortalUser portalUser, CompanyCRUDRights companyCRUDRights,
			SwpService swpService2, FeeDescriptionPortletState portletState) {
		// TODO Auto-generated method stub
		String email2 = aReq.getParameter("usernameemail");
		log.info("email2 = "+ email2);
		String password = aReq.getParameter("password");
		log.info("password = " + password);
		
		ComminsApplicationState cappState = portletState.getCas();
		log.info("cappState  we just got the application state");
		
		
		
		boolean trueCheck = new Util().loginStepTwoForCompanyManagement(portalUser, companyCRUDRights, 
				cappState, email2, password, swpService2);
		return trueCheck;
	}

	private void handleUpdateMapFeeToCompany(ActionRequest aReq,
			ActionResponse aRes, FeeDescriptionPortletState portletState) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		
		String[] allMapSelected = aReq.getParameterValues("map");
		log.info("allMapSelected = " + allMapSelected.length);
		List arrayList = null;
		if(allMapSelected!=null && allMapSelected.length>0)
		{
			arrayList = Arrays.asList(allMapSelected);
		}else
		{
			arrayList = new ArrayList<String>();
		}
		Collection<CompanyFeeDescription> cfdAll = portletState.getActiveCompanyFeeDesciptionListing();
		
		ComminsApplicationState cappState = portletState.getCas();
		Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
		Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
		DETERMINE_ACCESS determinAccess = new Util().determineAccessForCompanyFxns(twoStep, approvalProcess, cappState, portletState.getCompanyCRUDRights());
		
		for(Iterator<CompanyFeeDescription> iter = cfdAll.iterator(); iter.hasNext();)
		{
			if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
			{
				CompanyFeeDescription cfd = iter.next();
				log.info("cfd id = " + cfd.getId());
				if(arrayList!=null && arrayList.size()>0)
				{
					try{
						log.info("Long.toString(cfd.getId()) = " + Long.toString(cfd.getId()));
						if(arrayList.contains(Long.toString(cfd.getId())))
						{
							
							JSONObject jsonObject = new JSONObject();
							try {
								jsonObject.put("feeName", cfd.getFeeDescription().getFeeName());
								jsonObject.put("amountApplicable", Double.toString(cfd.getFeeDescription().getAmountApplicable()));
								jsonObject.put("companyName", cfd.getCompany().getCompanyName());
								jsonObject.put("companyRCNumber", cfd.getCompany().getCompanyRCNumber());
								jsonObject.put("companyId", Long.toString(cfd.getCompany().getId()));
								jsonObject.put("feeId", Long.toString(cfd.getFeeDescription().getId()));
								jsonObject.put("requestBy", portletState.getPortalUser().getFirstName() + " " + portletState.getPortalUser().getLastName());
								
								
								
								ApprovalFlowTransit aft = new ApprovalFlowTransit();
								aft.setActionType(ActionTypeConstants.FEE_DESCRIPTION_MAP_TO_USER);
								aft.setDateCreated(new Timestamp((new Date()).getTime()));
								aft.setPortalUser(portletState.getPortalUser());
								aft.setEntityId(cfd.getId());
								aft.setEntityName(FeeDescription.class.getSimpleName());
								aft.setObjectData(jsonObject.toString());
								aft.setWorkerId(null);
								swpService.createNewRecord(aft);
								
								Collection<PortalUser> pus = portletState.getFeeDescriptionPortletUtil().getApprovingPortalUsers(
										portletState.getPortalUser().getRoleType().getRoleTypeName());
								
								
								for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
								{
									PortalUser pu1 = it.next();
									emailer.emailApprovalRequest(
											pu1.getFirstName(), 
											pu1.getLastName(), 
											pu1.getEmailAddress(), 
											portletState.getSystemUrl().getValue(), 
											portletState.getApplicationName().getValue() + " - " +
													"Approval Request for the Update of a Company/Transaction " +
													"Fee Mapping", portletState.getApplicationName().getValue());
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
								
							} catch (org.codehaus.jettison.json.JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else
						{
							JSONObject jsonObject = new JSONObject();
							try {
								jsonObject.put("feeName", cfd.getFeeDescription().getFeeName());
								jsonObject.put("amountApplicable", Double.toString(cfd.getFeeDescription().getAmountApplicable()));
								jsonObject.put("companyName", cfd.getCompany().getCompanyName());
								jsonObject.put("companyRCNumber", cfd.getCompany().getCompanyRCNumber());
								jsonObject.put("companyId", Long.toString(cfd.getCompany().getId()));
								jsonObject.put("feeId", Long.toString(cfd.getFeeDescription().getId()));
								jsonObject.put("requestBy", portletState.getPortalUser().getFirstName() + " " + portletState.getPortalUser().getLastName());
								
								
								
								ApprovalFlowTransit aft = new ApprovalFlowTransit();
								aft.setActionType(ActionTypeConstants.FEE_DESCRIPTION_UNMAP_FROM_COMPANY);
								aft.setDateCreated(new Timestamp((new Date()).getTime()));
								aft.setPortalUser(portletState.getPortalUser());
								aft.setEntityId(cfd.getId());
								aft.setEntityName(FeeDescription.class.getSimpleName());
								aft.setObjectData(jsonObject.toString());
								aft.setWorkerId(null);
								swpService.createNewRecord(aft);
								
								
								Collection<PortalUser> pus = portletState.getFeeDescriptionPortletUtil().getApprovingPortalUsers(
										portletState.getPortalUser().getRoleType().getRoleTypeName());
								
								
								for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
								{
									PortalUser pu1 = it.next();
									emailer.emailApprovalRequest(
											pu1.getFirstName(), 
											pu1.getLastName(), 
											pu1.getEmailAddress(), 
											portletState.getSystemUrl().getValue(), 
											portletState.getApplicationName().getValue() + " - " +
													"Approval Request for the Update of a Company/Transaction " +
													"Fee Mapping", portletState.getApplicationName().getValue());
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
								
								
							} catch (org.codehaus.jettison.json.JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}catch(NumberFormatException e)
					{
						e.printStackTrace();
					}
					
					portletState.addSuccess(aReq, "Requests to change Transaction Fee to Company Mappings have been sent to appropriate approvers", portletState);
				}else
				{
					JSONObject jsonObject = new JSONObject();
					try {
						Company co = (Company)portletState.getFeeDescriptionPortletUtil().getEntityObjectById(FeeDescription.class, 
								Long.valueOf(portletState.getSelectedCompany()));
						jsonObject.put("feeName", cfd.getFeeDescription().getFeeName());
						jsonObject.put("amountApplicable", Double.toString(cfd.getFeeDescription().getAmountApplicable()));
						jsonObject.put("companyName", cfd.getCompany().getCompanyName());
						jsonObject.put("companyRCNumber", cfd.getCompany().getCompanyRCNumber());
						jsonObject.put("companyId", Long.toString(cfd.getCompany().getId()));
						jsonObject.put("feeId", Long.toString(cfd.getFeeDescription().getId()));
						jsonObject.put("requestBy", portletState.getPortalUser().getFirstName() + " " + portletState.getPortalUser().getLastName());
						
						
						
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.MANDATE_PANEL_MAP_USERS);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setPortalUser(portletState.getPortalUser());
						aft.setEntityId(cfd.getId());
						aft.setEntityName(FeeDescription.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						
						Collection<PortalUser> pus = portletState.getFeeDescriptionPortletUtil().getApprovingPortalUsers(
								portletState.getPortalUser().getRoleType().getRoleTypeName());
						
						
						for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
						{
							PortalUser pu1 = it.next();
							emailer.emailApprovalRequest(
									pu1.getFirstName(), 
									pu1.getLastName(), 
									pu1.getEmailAddress(), 
									portletState.getSystemUrl().getValue(), 
									portletState.getApplicationName().getValue() + " - " +
											"Approval Request for the Update of a " +
											"Company/Transaction Fee Mapping", portletState.getApplicationName().getValue());
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
						
					} catch (org.codehaus.jettison.json.JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				

		    	portletState.addSuccess(aReq, "Request to update Company-to-Transaction Fees created successfully!", portletState);
				
			}
			else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
			{
				CompanyFeeDescription cfd = iter.next();
				log.info("cfd id = " + cfd.getId());
				if(arrayList!=null && arrayList.size()>0)
				{
					try{
						log.info("Long.toString(cfd.getId()) = " + Long.toString(cfd.getId()));
						if(arrayList.contains(Long.toString(cfd.getId())))
						{
							
							cfd.setStatus(Boolean.TRUE);
						}else
						{
							cfd.setStatus(Boolean.FALSE);
						}
						swpService.updateRecord(cfd);
					}catch(NumberFormatException e)
					{
						e.printStackTrace();
					}
				}else
				{
					cfd.setStatus(Boolean.FALSE);
					swpService.updateRecord(cfd);
				}

		    	portletState.addSuccess(aReq, "Company to Transaction Fee Mappings updated successfully!", portletState);
			}
			
		}
		portletState.setActiveCompanyFeeDesciptionListing(portletState.getFeeDescriptionPortletUtil().getAllCompanyFeeDescription());
		aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/mappedlisting.jsp");
    	portletState.setCurrentTab(VIEW_TABS.MAPPED_FEES_TO_COMPANY);
	}
	
	

	private void handleMapFeeToCompany(ActionRequest aReq, ActionResponse aRes,
			FeeDescriptionPortletState portletState) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		portletState.setSelectedFeeDescription(aReq.getParameter("feedescription"));
		portletState.setSelectedCompany(aReq.getParameter("company"));
		log.info("compay = " + aReq.getParameter("company"));
		log.info("feedescription = " + aReq.getParameter("feedescription"));
		if(portletState.getSelectedCompany()!=null && !portletState.getSelectedCompany().equals("-1") && 
				portletState.getSelectedFeeDescription()!=null && !portletState.getSelectedFeeDescription().equals("-1"))
		{

			log.info("portletState.getSelectedCompany() = " + portletState.getSelectedCompany());
			ComminsApplicationState cappState = portletState.getCas();
			Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
			Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
			DETERMINE_ACCESS determinAccess = new Util().determineAccessForCompanyFxns(twoStep, approvalProcess, cappState, portletState.getCompanyCRUDRights());
			
			FeeDescription feeDescription = (FeeDescription)portletState.getFeeDescriptionPortletUtil().getEntityObjectById(FeeDescription.class, Long.valueOf(portletState.getSelectedFeeDescription()));
			
			if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
			{
				log.info("initiator access = " + determinAccess);
				CompanyFeeDescription rt = portletState.getFeeDescriptionPortletUtil().getCompanyFeeDescriptionByCompanyAndFeeDescription(
						Long.valueOf(portletState.getSelectedCompany()), 
						Long.valueOf(portletState.getSelectedFeeDescription()));
				if(rt==null)
				{
					log.info("rt = null");
					JSONObject jsonObject = new JSONObject();
					try {
						Company co = (Company)portletState.getFeeDescriptionPortletUtil().getEntityObjectById(
								Company.class, 
								Long.valueOf(portletState.getSelectedCompany()));
						jsonObject.put("feeName", feeDescription.getFeeName());
						jsonObject.put("amountApplicable", Double.toString(feeDescription.getAmountApplicable()));
						jsonObject.put("companyName", co.getCompanyName());
						jsonObject.put("companyRCNumber", co.getCompanyRCNumber());
						jsonObject.put("companyId", Long.toString(co.getId()));
						jsonObject.put("feeId", Long.toString(feeDescription.getId()));
						jsonObject.put("requestBy", portletState.getPortalUser().getFirstName() + " " + portletState.getPortalUser().getLastName());
						
						
						
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.FEE_DESCRIPTION_MAP_TO_USER);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setPortalUser(portletState.getPortalUser());
						aft.setEntityId(null);
						aft.setEntityName(FeeDescription.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						
						Collection<PortalUser> pus = portletState.getFeeDescriptionPortletUtil().getApprovingPortalUsers(
								portletState.getPortalUser().getRoleType().getRoleTypeName());
						
						
						for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
						{
							PortalUser pu1 = it.next();
							emailer.emailApprovalRequest(
									pu1.getFirstName(), 
									pu1.getLastName(), 
									pu1.getEmailAddress(), 
									portletState.getSystemUrl().getValue(), 
									portletState.getApplicationName().getValue() + " - Approval Request for the " +
											"Update of a Company/Transaction Fee Mapping", portletState.getApplicationName().getValue());
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
						
						
						portletState.addSuccess(aReq, "Request to assign the transaction fee to the selected company has been " +
								"created successfully. An approving officer will approve/disapprove your request", portletState);
					} catch (org.codehaus.jettison.json.JSONException e) {
						// TODO Auto-generated catch block
						
						e.printStackTrace();
						portletState.addError(aReq, "Issues were experienced creating an approval request for deleting this Fee Description", portletState);
					}
				}else
				{
					log.info("rt != null");
					portletState.addError(aReq, "You have already mapped the selected company - "  + rt.getCompany().getCompanyName(), portletState);
				}
				
			}
			else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
			{
				CompanyFeeDescription rt = portletState.getFeeDescriptionPortletUtil().getCompanyFeeDescriptionByCompanyAndFeeDescription(
						Long.valueOf(portletState.getSelectedCompany()), 
						Long.valueOf(portletState.getSelectedFeeDescription()));
				if(rt==null)
				{
					CompanyFeeDescription companyFeeDescription = new CompanyFeeDescription();
					companyFeeDescription.setCompany((Company)portletState.getFeeDescriptionPortletUtil().getEntityObjectById(
							Company.class, 
							Long.valueOf(portletState.getSelectedCompany())));
					companyFeeDescription.setFeeDescription((FeeDescription)portletState.getFeeDescriptionPortletUtil().getEntityObjectById(
							FeeDescription.class, 
							Long.valueOf(portletState.getSelectedFeeDescription())));
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
						aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/mapfeedescriptiontocompany.jsp");
						portletState.addSuccess(aReq, "Mapping transaction fee to selected company was successful", portletState);
					}else
					{
						portletState.addError(aReq, "Mapping a transaction fee to the selected company was not successful. Please try again", portletState);
						aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/mapfeedescriptiontocompany.jsp");
					}
					
					
				}else
				{
					if(rt.getStatus().equals(Boolean.TRUE))
					{
						portletState.addError(aReq, "You have already mapped the selected company - "  + rt.getCompany().getCompanyName(), portletState);
						aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/mapfeedescriptiontocompany.jsp");
					}else
					{
						rt.setStatus(Boolean.TRUE);
						swpService.updateRecord(rt);
						handleAudit("Update Company Fee Description", 
								Long.toString(rt.getId()), 
								new Timestamp((new Date()).getTime()), 
								portletState.getRemoteIPAddress(), 
								portletState.getPortalUser().getUserId());
						portletState.addSuccess(aReq, "Transaction Fee Mapped successfully to selected company - " + rt.getCompany().getCompanyName(), portletState);
						aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/mapfeedescriptiontocompany.jsp");
					}
				}
			}
			
			
			portletState.setActiveCompanyFeeDesciptionListing(portletState.getFeeDescriptionPortletUtil().getAllCompanyFeeDescription());
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/mapfeedescriptiontocompany.jsp");
			portletState.addSuccess(aReq, "Invalid company selected. Select one before proceedig", portletState);
		}
	}

	private void handleCreateNewFeeDescription(ActionRequest aReq,
			ActionResponse aRes, FeeDescriptionPortletState portletState) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		log.info("inside handleCreateNewFeeDescription ");
		portletState.setFeeDescriptionName(aReq.getParameter("feeDescriptionName"));
		log.info("feeDescriptionName =  " + portletState.getFeeDescriptionName());
		portletState.setFeeDescriptionDetail(aReq.getParameter("feeDescriptionDetail"));
		log.info("feeDescriptionDetail =  " + portletState.getFeeDescriptionDetail());
		portletState.setFeeDescriptionAmount(aReq.getParameter("feeDescriptionAmount"));
		log.info("feeDescriptionAmount =  " + portletState.getFeeDescriptionDetail());
		portletState.setPrimaryFeeChecked(aReq.getParameter("primaryFee")!=null && 
				aReq.getParameter("primaryFee").equals("1") ? true : false);
		log.info("feeDescriptionAmount =  " + portletState.getFeeDescriptionDetail());
		
		if(isFeeDescriptionDataValid(portletState, aReq, aRes, true))
		{
			log.info("fee description data is valid ");
			
			try
			{
				log.info(1);
				ComminsApplicationState cappState = portletState.getCas();
				Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
				Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
				DETERMINE_ACCESS determinAccess = new Util().determineAccessForCompanyFxns(twoStep, approvalProcess, cappState, portletState.getCompanyCRUDRights());
				log.info(twoStep!=null && twoStep.equals(Boolean.TRUE) ? "true" : "false");
				log.info(twoStep==null ? "null" : "not null");
				log.info(approvalProcess!=null && approvalProcess.equals(Boolean.TRUE) ? "true" : "false");
				log.info(approvalProcess==null ? "null" : "not null");
				log.info(cappState==null ? "cappState is null" : "cappState not null");
				log.info(portletState.getCompanyCRUDRights()==null ? "portletState.getCompanyCRUDRights() is null" : "portletState.getCompanyCRUDRights() not null");
				log.info(portletState.getCompanyCRUDRights()!=null ? portletState.getCompanyCRUDRights().getCudApprovalRights() + " -- " + portletState.getCompanyCRUDRights().getCudInitiatorRights() : "empty");
				
				log.info(2);
				if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
				{
					log.info(3);
					try
					{
						log.info(4);
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("feeName", portletState.getFeeDescriptionName());
						jsonObject.put("feeDescription", portletState.getFeeDescriptionDetail());
						jsonObject.put("amountApplicable", portletState.getFeeDescriptionAmount());
						jsonObject.put("primaryFee", portletState.isPrimaryFeeChecked() ? "1" : "0");
						jsonObject.put("requestBy", portletState.getPortalUser().getFirstName() + " " + portletState.getPortalUser().getLastName());
						
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.FEE_DESCRIPTION_CREATE_NEW);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setPortalUser(portletState.getPortalUser());
						aft.setEntityId(null);
						aft.setEntityName(FeeDescription.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						
						Collection<PortalUser> pus = portletState.getFeeDescriptionPortletUtil().getApprovingPortalUsers(
								portletState.getPortalUser().getRoleType().getRoleTypeName());
						
						
						for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
						{
							PortalUser pu1 = it.next();
							emailer.emailApprovalRequest(
									pu1.getFirstName(), 
									pu1.getLastName(), 
									pu1.getEmailAddress(), 
									portletState.getSystemUrl().getValue(), 
									portletState.getApplicationName().getValue() + " - Approval Request for the " +
											"Creation of a Transaction Fee", portletState.getApplicationName().getValue());
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
						log.info(5);
						portletState.addSuccess(aReq, "Request for the creation of Fee Description - " + portletState.getFeeDescriptionName() + " - was created successfully!", portletState);
					}catch(org.codehaus.jettison.json.JSONException e)
					{
						log.info(6);
						e.printStackTrace();
						portletState.addError(aReq, "Issues were experienced creating an approval request for deleting this Fee Description", portletState);
					}
				}else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
				{
					FeeDescription feeDescription = new FeeDescription();
					feeDescription.setAmountApplicable(Double.valueOf(portletState.getFeeDescriptionAmount()));
					feeDescription.setDateCreated(new Timestamp((new Date()).getTime()));
					feeDescription.setDescription(portletState.getFeeDescriptionDetail());
					feeDescription.setFeeName(portletState.getFeeDescriptionName());
					feeDescription.setStatus(SmartPayConstants.FEE_DESCRIPTION_STATUS_ACTIVE);
					feeDescription.setIsPrimaryFee(portletState.isPrimaryFeeChecked() ? Boolean.TRUE : Boolean.FALSE);
					

					feeDescription = (FeeDescription)swpService.createNewRecord(feeDescription);
					log.info("feeDescription = " + feeDescription.getId());
					handleAudit("Fee Description Creation", Long.toString(feeDescription.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
					portletState.addSuccess(aReq, "Fee Description - " + feeDescription.getFeeName() + " - was created successfully!", portletState);
					portletState.setAllFeeDescription(portletState.getFeeDescriptionPortletUtil().getFeeDescriptionListing(true));
		        	aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/feedescriptionlisting.jsp");
		        	portletState.setCurrentTab(VIEW_TABS.VIEW_FEE_DESCRIPTION_LISTINGS);
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/feedescriptionlisting.jsp");
		        	portletState.setCurrentTab(VIEW_TABS.VIEW_FEE_DESCRIPTION_LISTINGS);
		        	portletState.addError(aReq, "You do not have appropriate rights to carry out this action" +
		        			"", portletState);
				}
				
				
			}catch(NumberFormatException e)
			{
				aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/createafeedescription.jsp");
				portletState.addError(aReq, "Provide a valid amount in the Applicable amount field. E.g. 2000.00, 12900", portletState); 
				e.printStackTrace();
			}
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/createafeedescription.jsp");
		}
	}
	
	private void handleUpdateFeeDescription(ActionRequest aReq, ActionResponse aRes,
			FeeDescriptionPortletState portletState) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		portletState.setFeeDescriptionName(aReq.getParameter("feeDescriptionName"));
		portletState.setFeeDescriptionDetail(aReq.getParameter("feeDescriptionDetail"));
		portletState.setFeeDescriptionAmount(aReq.getParameter("feeDescriptionAmount"));
		
		try
		{
			if(portletState.getSelectedFeeDescriptionId()!=null && isFeeDescriptionDataValid(portletState, aReq, aRes, false))
			{
				
				ComminsApplicationState cappState = portletState.getCas();
				Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
				Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
				DETERMINE_ACCESS determinAccess = new Util().determineAccessForCompanyFxns(twoStep, approvalProcess, cappState, portletState.getCompanyCRUDRights());
				
				FeeDescription feeDescription = (FeeDescription)portletState.getFeeDescriptionPortletUtil().getEntityObjectById(FeeDescription.class, Long.valueOf(portletState.getSelectedFeeDescriptionId()));
				
				if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
				{
					try
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("feeName", portletState.getFeeDescriptionName());
						jsonObject.put("feeDescription", portletState.getFeeDescriptionDetail());
						jsonObject.put("amountApplicable", (portletState.getFeeDescriptionAmount()));
						jsonObject.put("primaryFee", (feeDescription.getIsPrimaryFee()!=null && feeDescription.getIsPrimaryFee()) ? "1" : "0");
						jsonObject.put("requestBy", portletState.getPortalUser().getFirstName() + " " + portletState.getPortalUser().getLastName());
						
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.FEE_DESCRIPTION_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setPortalUser(portletState.getPortalUser());
						aft.setEntityId(feeDescription.getId());
						aft.setEntityName(FeeDescription.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						
						Collection<PortalUser> pus = portletState.getFeeDescriptionPortletUtil().getApprovingPortalUsers(
								portletState.getPortalUser().getRoleType().getRoleTypeName());
						
						
						for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
						{
							PortalUser pu1 = it.next();
							emailer.emailApprovalRequest(
									pu1.getFirstName(), 
									pu1.getLastName(), 
									pu1.getEmailAddress(), 
									portletState.getSystemUrl().getValue(), 
									portletState.getApplicationName().getValue() + " - Approval Request for the " +
											"Update of a Transaction Fee", portletState.getApplicationName().getValue());
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
						
						portletState.addError(aReq, "Request to update your transaction fee has been sent for approval", portletState);
					}catch(org.codehaus.jettison.json.JSONException e)
					{
						e.printStackTrace();
						portletState.addError(aReq, "Issues were experienced while " +
								"creating an approval request for the creation of a transaction fee", portletState);
					}
					
				}else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
				{
					
					feeDescription.setAmountApplicable(Double.valueOf(portletState.getFeeDescriptionAmount()));
					feeDescription.setDescription(portletState.getFeeDescriptionDetail());
					feeDescription.setFeeName(portletState.getFeeDescriptionName());
					feeDescription.setIsPrimaryFee(portletState.isPrimaryFeeChecked());
					
		
					swpService.updateRecord(feeDescription);
					log.info("feeDescription = " + feeDescription.getId());
					handleAudit("Fee Description Update", Long.toString(feeDescription.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
					aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/feedescriptionlisting.jsp");
					portletState.addSuccess(aReq, "Fee Description - " + feeDescription.getFeeName() + " - was updated successfully!", portletState);
					portletState.reinitializeForFeeDescription(portletState);
					portletState.setAllFeeDescription(portletState.getFeeDescriptionPortletUtil().getFeeDescriptionListing(true));
				}
				
				
			}else
			{
				portletState.addError(aReq, "Select a valid fee description to update", portletState);
				aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/editafeedescription.jsp");
			}
		}catch(NumberFormatException e)
		{
			e.printStackTrace();
			portletState.addError(aReq, "Select a valid fee description to update", portletState);
			aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/editafeedescription.jsp");
		}
		
	}
	
	
	
	
	private boolean isFeeDescriptionDataValid(FeeDescriptionPortletState portletState,
			ActionRequest aReq, ActionResponse aRes, boolean checkExistingForNew) {
		// TODO Auto-generated method stub
		String errorMessage = null;
		
		if(portletState.getFeeDescriptionName()!=null && portletState.getFeeDescriptionName().trim().length()>0)
		{
			if(portletState.getFeeDescriptionDetail()!=null && portletState.getFeeDescriptionDetail().trim().length()>0)
			{
				if(portletState.getFeeDescriptionAmount()!=null && portletState.getFeeDescriptionAmount().trim().length()>0 && Double.valueOf(portletState.getFeeDescriptionAmount())>0)
				{
					FeeDescription fd = null;
					if(checkExistingForNew)
					{
						fd = portletState.getFeeDescriptionPortletUtil().getFeeDescriptionByName(
								portletState.getFeeDescriptionName());
					}else
					{
						fd = portletState.getFeeDescriptionPortletUtil().getFeeDescriptionByNameAndNotId(
								portletState.getFeeDescriptionName(), Long.valueOf(portletState.getSelectedFeeDescriptionId()));
					}
					
					
					if(fd==null)
					{
						
					}else
					{
						errorMessage =  "The fee description name provided already exist on the system. This fee description has already been created.";
					}
					
				}else
				{
					errorMessage =  "Provide a valid fee description amount which must be greater than zero before proceeding";
				}
			}
			else
			{
				errorMessage =  "The details of the fee description should be provided. This is what is displayed to the payee when they are making a payment";
			}
		}else
		{
			errorMessage =  "Provide a name for the fee description before proceeding";
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

	private void handleNavigations(ActionRequest aReq, ActionResponse aRes,
			FeeDescriptionPortletState portletState) {
		// TODO Auto-generated method stub
		String action = aReq.getParameter("actionUrl");
		if(action.equalsIgnoreCase("createfeedescription"))
		{
			portletState.reinitializeForFeeDescription(portletState);
			aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/createafeedescription.jsp");
			portletState.setCurrentTab(VIEW_TABS.CREATE_A_FEE_DESCRIPTION);
		}else if(action.equalsIgnoreCase("feedescriptionlistings"))
		{
			portletState.setAllFeeDescription(portletState.getFeeDescriptionPortletUtil().getFeeDescriptionListing(true));
			aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/feedescriptionlisting.jsp");
			portletState.setCurrentTab(VIEW_TABS.VIEW_FEE_DESCRIPTION_LISTINGS);
		}
	}

	private void reinitializeForCreateCorporat1eIndividual(
			FeeDescriptionPortletState portletState) {
		// TODO Auto-generated method stub
		
	}

	
	
	
	
	private void handleFeeDescriptionListingAction(ActionRequest aReq,
			ActionResponse aRes, FeeDescriptionPortletState portletState) {
		// TODO Auto-generated method stub
		ComminsApplicationState cappState = portletState.getCas();
		Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
		Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
		DETERMINE_ACCESS determinAccess = new Util().determineAccessForCompanyFxns(twoStep, approvalProcess, cappState, portletState.getCompanyCRUDRights());
		String feeDescId = aReq.getParameter("selectedFeeDescription").trim();
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		
		try
		{
			
			Long feeDescIdL = Long.valueOf(feeDescId);
			FeeDescription fd = (FeeDescription)portletState.getFeeDescriptionPortletUtil().getEntityObjectById(FeeDescription.class, feeDescIdL);
			portletState.setSelectedFeeDescriptionId(Long.toString(feeDescIdL));
			if(fd!=null)
			{
				if(aReq.getParameter("selectedFeeDescriptionAction")!=null && aReq.getParameter("selectedFeeDescriptionAction").equalsIgnoreCase("update"))
				{
					portletState.setFeeDescriptionAmount(Double.toString(fd.getAmountApplicable()));
					portletState.setFeeDescriptionDetail(fd.getDescription());
					portletState.setFeeDescriptionName(fd.getFeeName());
					aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/editafeedescription.jsp");
				
				}else if(aReq.getParameter("selectedFeeDescriptionAction")!=null && aReq.getParameter("selectedFeeDescriptionAction").equalsIgnoreCase("delete"))
				{
					if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
					{
						try
						{
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("feeName", fd.getFeeName());
							jsonObject.put("feeDescription", fd.getDescription().length()>15 ? (fd.getDescription().substring(0, 15) + "...") : fd.getDescription());
							jsonObject.put("amountApplicable", Double.toString(fd.getAmountApplicable()));
							jsonObject.put("primaryFee", fd.getIsPrimaryFee()!=null && fd.getIsPrimaryFee().equals(Boolean.TRUE) ? "1" : "0");
							jsonObject.put("requestBy", portletState.getPortalUser().getFirstName() + " " + portletState.getPortalUser().getLastName());
							
							ApprovalFlowTransit aft = new ApprovalFlowTransit();
							aft.setActionType(ActionTypeConstants.FEE_DESCRIPTION_DELETE);
							aft.setDateCreated(new Timestamp((new Date()).getTime()));
							aft.setPortalUser(portletState.getPortalUser());
							aft.setEntityId(fd.getId());
							aft.setEntityName(FeeDescription.class.getSimpleName());
							aft.setObjectData(jsonObject.toString());
							aft.setWorkerId(null);
							swpService.createNewRecord(aft);
							
							Collection<PortalUser> pus = portletState.getFeeDescriptionPortletUtil().getApprovingPortalUsers(
									portletState.getPortalUser().getRoleType().getRoleTypeName());
							
							
							for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
							{
								PortalUser pu1 = it.next();
								emailer.emailApprovalRequest(
										pu1.getFirstName(), 
										pu1.getLastName(), 
										pu1.getEmailAddress(), 
										portletState.getSystemUrl().getValue(), 
										portletState.getApplicationName().getValue() + " - " +
												"Approval Request for the " +
												"deletion of a Fee Mapping", portletState.getApplicationName().getValue());
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
							aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/feedescriptionlisting.jsp");
							portletState.addSuccess(aReq, "Approval Request for deletion of  Selected Fee Description - " + fd.getFeeName() + " - has been created successfully.", portletState);
						}catch(org.codehaus.jettison.json.JSONException e)
						{
							e.printStackTrace();
							portletState.addError(aReq, "Issues were experienced creating an approval request for deleting this Fee Description", portletState);
							aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/feedescriptionlisting.jsp");
						}
					}else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
					{
						fd.setStatus(SmartPayConstants.FEE_DESCRIPTION_STATUS_INACTIVE);
						swpService.updateRecord(fd);
						handleAudit("Authorize Panel Delete", Long.toString(fd.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
						aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/feedescriptionlisting.jsp");
						portletState.addSuccess(aReq, "Selected Fee Description - " + fd.getFeeName() + " - has been deleted successfully.", portletState);
						portletState.setAllFeeDescription(portletState.getFeeDescriptionPortletUtil().getFeeDescriptionListing(true));
					}
					
					
					
				}
			}else
			{
				portletState.addError(aReq, "This action can not be carried out on the selected authorisation panel. You seem to have not selected a valid authorisation panel. Select one before proceeding", portletState);
				aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/feedescriptionlisting.jsp");
			}
			
		}catch(NumberFormatException e)
		{
			e.printStackTrace();
			portletState.addError(aReq, "This action can not be carried out on the selected authorisation panel. Select a valid authorisation panel before proceeding", portletState);
			aRes.setRenderParameter("jspPage", "/html/feedescriptionportlet/feedescription/feedescriptionlisting.jsp");
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
