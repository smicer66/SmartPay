package com.probase.smartpay.admin.companymanagement;

import java.io.IOException;
import java.sql.Timestamp;
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
import smartpay.entity.FeeDescription;
import smartpay.entity.FinancialAmountRestriction;
import smartpay.entity.PortalUser;
import smartpay.entity.PortalUserCRUDRights;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.TpinInfo;
import smartpay.entity.enumerations.ActionTypeConstants;
import smartpay.entity.enumerations.CompanyStatusConstants;
import smartpay.entity.enumerations.CompanyTypeConstants;
import smartpay.entity.enumerations.PortalUserStatusConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.service.SwpService;


import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.probase.smartpay.admin.companymanagement.CompanyManagementPortletState.COMPANY_CREATION;
import com.probase.smartpay.admin.companymanagement.CompanyManagementPortletState.COMPANY_LISTING;
import com.probase.smartpay.admin.companymanagement.CompanyManagementPortletState.COMPANY_VIEW;
import com.probase.smartpay.admin.companymanagement.CompanyManagementPortletState.NAVIGATE;
import com.probase.smartpay.admin.companymanagement.CompanyManagementPortletState.VIEW_TABS;
import com.probase.smartpay.commins.ComminsApplicationState;
import com.probase.smartpay.commins.Mailer;
import com.probase.smartpay.commins.ProbaseConstants;
import com.probase.smartpay.commins.SendMail;
import com.probase.smartpay.commins.SendSms;
import com.probase.smartpay.commins.Util;
import com.probase.smartpay.commins.Util.DETERMINE_ACCESS;
import com.sf.primepay.smartpay13.ServiceLocator;
import com.liferay.portal.DuplicateUserScreenNameException;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * Portlet implementation class CompanyManagementPortlet
 */
public class CompanyManagementPortlet extends MVCPortlet {
 
	private Logger log = Logger.getLogger(CompanyManagementPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
	CompanyManagementPortletUtil util = CompanyManagementPortletUtil.getInstance();
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
		CompanyManagementPortletState portletState = 
				CompanyManagementPortletState.getInstance(renderRequest, renderResponse);

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
		
		CompanyManagementPortletState portletState = CompanyManagementPortletState.getInstance(aReq, aRes);
		
		
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
        if (action.equalsIgnoreCase(COMPANY_CREATION.LOGIN_STEP_TWO.name()))
        {
        	loginStepTwo(aReq, aRes, portletState);
        }
        if (action.equalsIgnoreCase(COMPANY_CREATION.CREATE_A_COMPANY_STEP_ONE.name()))
        {
        	createACompanyStepOne(aReq, aRes, portletState);
        }if (action.equalsIgnoreCase(COMPANY_CREATION.CREATE_A_COMPANY_STEP_TWO.name()))
        {
        	createACompanyStepTwo(aReq, aRes, portletState);
        }if (action.equalsIgnoreCase(COMPANY_CREATION.EDIT_A_COMPANY_STEP_ONE.name()))
        {
        	log.info("We are in edit a company step one");
        	editACompanyStepOne(aReq, aRes, portletState);
        }if (action.equalsIgnoreCase(COMPANY_CREATION.EDIT_A_COMPANY_STEP_TWO.name()))
        {
        	editACompanyStepTwo(aReq, aRes, portletState);
        }if(action.equalsIgnoreCase(COMPANY_LISTING.MODIFY_COMPANY.name()))
        {
        	handleCompanyListingAction(aReq, aRes, portletState);
        }if(action.equalsIgnoreCase(COMPANY_VIEW.VIEW_COMPANY_LISTINGS.name()))
        {
        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
        	{
	        	portletState.setCompanyListing(portletState.getCompanyManagementPortletUtil().getAllCompanyListing());
				aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
				portletState.setCurrentTab(VIEW_TABS.VIEW_COMPANY_LISTINGS);
        	}else if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_NON_ADMINISTRATOR_PERSONNEL))
        	{
	        	portletState.setCompanyListing(portletState.getCompanyManagementPortletUtil().getAllCompanyListing());
				aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companycreation/stepone.jsp");
				portletState.setCurrentTab(VIEW_TABS.CREATE_A_COMPANY);
				portletState.addError(aReq, "You do not have the appropriate rights to view companies on this platform. Please contact Administrative personnel for rights", portletState);
        	}
        }if(action.equalsIgnoreCase(COMPANY_VIEW.CREATE_A_COMPANY.name()))
        {
        	log.info("Role Type ===" + portletState.getPortalUser().getRoleType().getRoleTypeName().getValue());
        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
        	{
	        	aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companycreation/stepone.jsp");
				portletState.setCurrentTab(VIEW_TABS.CREATE_A_COMPANY);
        	}
        }
		
	}

	
	
	private void handleCompanyListingAction(ActionRequest aReq,
			ActionResponse aRes, CompanyManagementPortletState portletState) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
							portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
									Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
							portletState.getSendingEmailUsername().getValue());
		String companyId = aReq.getParameter("selectedCompany").trim();
		
		ComminsApplicationState cappState = portletState.getCas();
		Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
		Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
		DETERMINE_ACCESS determinAccess = new Util().determineAccessForCompanyFxns(twoStep, approvalProcess, cappState, portletState.getCompanyCRUDRights());
		
		
		try
		{
			Long companyIdL = Long.valueOf(companyId);
			Company company = portletState.getCompanyManagementPortletUtil().getCompanyById(companyIdL);
			portletState.setSelectedCompanyId(companyIdL);
			if(company!=null)
			{
				if(aReq.getParameter("selectedCompanyAction")!=null && aReq.getParameter("selectedCompanyAction").equalsIgnoreCase("update"))
				{
					
					portletState.setCompanyname(company.getCompanyName());
					portletState.setCompanyrcnumber(company.getCompanyRCNumber());
					portletState.setCompanycontactphonenumber(company.getMobileNumber());
					portletState.setCompanyemailaddress(company.getEmailAddress());
					portletState.setLine1addressofcompany(company.getAddressLine1());
					portletState.setLine2addressofcompany(company.getAddressLine2());
					portletState.setSelectedCompanyType(company.getCompanyType().getValue());
					TpinInfo tpinInfo = portletState.getCompanyManagementPortletUtil().getTPINForCompany(company);
					portletState.setTpin(tpinInfo==null ? "" : tpinInfo.getTpin());
					portletState.setBankNumber(company.getAccountNumber());
					portletState.setSelectedBankBranchId(Long.toString(company.getBankBranches().getId()));
					portletState.setMandatePanelsOn(company.getMandatePanelsOn()!=null && company.getMandatePanelsOn().equals(Boolean.TRUE) ? "1" : "0");
					aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companycreation/stepone_edit.jsp");
					
				}else if(aReq.getParameter("selectedCompanyAction")!=null && aReq.getParameter("selectedCompanyAction").equalsIgnoreCase("delete"))
				{
					

					if(company!=null)
					{
						if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
						{
							TpinInfo tpinInfo = portletState.getCompanyManagementPortletUtil().getTPINForCompany(company);
							JSONObject jsonObject = new JSONObject();
							try {
								jsonObject.put("bankNumber", company.getAccountNumber());
								jsonObject.put("line1addressofcompany", company.getAddressLine1());
								jsonObject.put("line2addressofcompany", company.getAddressLine2());
								jsonObject.put("companyname", company.getCompanyName());
								jsonObject.put("companyrcnumber", company.getCompanyRCNumber());
								jsonObject.put("companyemailaddress", company.getEmailAddress());
								jsonObject.put("companycontactphonenumber", company.getMobileNumber());
								jsonObject.put("selectedBankBranchId", company.getBankBranches().getId());
								jsonObject.put("selectedCompanyType", company.getCompanyType().getValue());
								jsonObject.put("selectedCompanyClass", company.getClearingAgent()==null ? "0" : (company.getClearingAgent().equals(Boolean.TRUE) ? "1" : "0"));
								jsonObject.put("tpin", tpinInfo.getTpin());
								
								ApprovalFlowTransit aft = new ApprovalFlowTransit();
								aft.setActionType(ActionTypeConstants.COMPANY_ACTION_DELETE);
								aft.setDateCreated(new Timestamp((new Date()).getTime()));
								aft.setEntityId(company.getId());
								aft.setEntityName(Company.class.getSimpleName());
								aft.setObjectData(jsonObject.toString());
								aft.setPortalUser(portletState.getPortalUser());
								aft.setWorkerId(null);
								swpService.createNewRecord(aft);
								
								Collection<PortalUser> pus = portletState.getCompanyManagementPortletUtil().getApprovingPortalUsers(
										portletState.getPortalUser().getRoleType().getRoleTypeName());
								
								for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
								{
									PortalUser pu1 = it.next();
									emailer.emailApprovalRequest(
											pu1.getFirstName(), 
											pu1.getLastName(), 
											pu1.getEmailAddress(), 
											portletState.getSystemUrl().getValue(), 
											portletState.getMobileApplicationName().getValue() + " - " +
													"Approval Request for the Deletion of A Company Profile", portletState.getApplicationName().getValue());
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
								
								
								
								aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
								portletState.setCompanyListing(portletState.getCompanyManagementPortletUtil().getAllCompanyListing());
								portletState.addSuccess(aReq, "Request for deletion of company successfully created", portletState);
							} catch (org.codehaus.jettison.json.JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								portletState.addError(aReq, "Request for deletion of company could not be created", portletState);
								aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
							}
									
								
							
						}
						else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
						{
							company.setStatus(CompanyStatusConstants.COMPANY_STATUS_DELETED);
							swpService.updateRecord(company);
							handleAudit("Company Delete", Long.toString(company.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
							aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
							portletState.addSuccess(aReq, "Selected company - " + company.getCompanyName() + " - has been deleted successfully.", portletState);
							portletState.setCompanyListing(portletState.getCompanyManagementPortletUtil().getAllCompanyListing());
							
							Settings settingEmail = portletState.getNotifyCorporateFirmEmail();
							Settings settingSms = portletState.getNotifyCorporateFirmSms();
							Settings settingSystemUrl = portletState.getSystemUrl();
							
							boolean sendEmail = settingEmail.equals("1") ? true : false;
							boolean sendSms = settingSms.equals("1") ? true : false;
							String systemUrl = settingSystemUrl.getValue()==null ? "SmartPay" : settingSystemUrl.getValue();
							String companyType = company.getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY) ? "Corporate Account" : "Retail Account";
							TpinInfo tpinInfo = portletState.getCompanyManagementPortletUtil().getTPINForCompany(company);
//							sendNotificationOnCompanyClosure(sendEmail, sendSms, company, 
//									systemUrl, companyType, tpinInfo);
						}
					}else
					{
						portletState.addError(aReq, "Company could not be found. Select a Valid company and try again", portletState);
						aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
					}
					
					
				}else if(aReq.getParameter("selectedCompanyAction")!=null && aReq.getParameter("selectedCompanyAction").equalsIgnoreCase("suspend"))
				{
					
					if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
					{
						TpinInfo tpinInfo = portletState.getCompanyManagementPortletUtil().getTPINForCompany(company);
						JSONObject jsonObject = new JSONObject();
						try {
							jsonObject.put("bankNumber", company.getAccountNumber());
							jsonObject.put("line1addressofcompany", company.getAddressLine1());
							jsonObject.put("line2addressofcompany", company.getAddressLine2());
							jsonObject.put("companyname", company.getCompanyName());
							jsonObject.put("companyrcnumber", company.getCompanyRCNumber());
							jsonObject.put("companyemailaddress", company.getEmailAddress());
							jsonObject.put("companycontactphonenumber", company.getMobileNumber());
							jsonObject.put("selectedBankBranchId", company.getBankBranches().getId());
							jsonObject.put("selectedCompanyType", company.getCompanyType().getValue());
							jsonObject.put("selectedCompanyClass", company.getClearingAgent()==null ? "0" : (company.getClearingAgent().equals(Boolean.TRUE) ? "1" : "0"));
							jsonObject.put("tpin", tpinInfo.getTpin());
							
							ApprovalFlowTransit aft = new ApprovalFlowTransit();
							aft.setActionType(ActionTypeConstants.COMPANY_ACTION_BLOCK);
							aft.setDateCreated(new Timestamp((new Date()).getTime()));
							aft.setEntityId(company.getId());
							aft.setEntityName(Company.class.getSimpleName());
							aft.setObjectData(jsonObject.toString());
							aft.setPortalUser(portletState.getPortalUser());
							aft.setWorkerId(null);
							swpService.createNewRecord(aft);
							
							Collection<PortalUser> pus = portletState.getCompanyManagementPortletUtil().getApprovingPortalUsers(
									portletState.getPortalUser().getRoleType().getRoleTypeName());
							
							
							for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
							{
								PortalUser pu1 = it.next();
								emailer.emailApprovalRequest(
										pu1.getFirstName(), 
										pu1.getLastName(), 
										pu1.getEmailAddress(), 
										portletState.getSystemUrl().getValue(), 
										portletState.getApplicationName().getValue() + " - Approval " +
												"Request for the Suspension of A Company Profile", portletState.getApplicationName().getValue());
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
							
							
							portletState.addSuccess(aReq, "Request for suspension of company successfully created", portletState);
							aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
						} catch (org.codehaus.jettison.json.JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							portletState.addError(aReq, "Request for suspension of company could not be created", portletState);
							aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
							
						}
								
							
						
					}
					else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
					{
						company.setStatus(CompanyStatusConstants.COMPANY_STATUS_SUSPENDED);
						swpService.updateRecord(company);
						handleAudit("Company Suspend", Long.toString(company.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
						aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
						portletState.addSuccess(aReq, "Selected company - " + company.getCompanyName() + " - has been suspended.", portletState);
						portletState.setCompanyListing(portletState.getCompanyManagementPortletUtil().getAllCompanyListing());
						
						Settings settingEmail = portletState.getNotifyCorporateFirmEmail();
						Settings settingSms = portletState.getNotifyCorporateFirmSms();
						Settings settingSystemUrl = portletState.getSystemUrl();
						
						boolean sendEmail = settingEmail.equals("1") ? true : false;
						boolean sendSms = settingSms.equals("1") ? true : false;
						String systemUrl = settingSystemUrl.getValue()==null ? "SmartPay" : settingSystemUrl.getValue();
						String companyType = company.getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY) ? "Corporate Account" : "Retail Account";
						TpinInfo tpinInfo = portletState.getCompanyManagementPortletUtil().getTPINForCompany(company);
//						sendNotificationOnCompanySuspension(sendEmail, sendSms, company, 
//								systemUrl, companyType, tpinInfo);
					}
					
					
				}else if(aReq.getParameter("selectedCompanyAction")!=null && aReq.getParameter("selectedCompanyAction").equalsIgnoreCase("reactivate"))
				{
					
					if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
					{
						TpinInfo tpinInfo = portletState.getCompanyManagementPortletUtil().getTPINForCompany(company);
						JSONObject jsonObject = new JSONObject();
						try {
							jsonObject.put("bankNumber", company.getAccountNumber());
							jsonObject.put("line1addressofcompany", company.getAddressLine1());
							jsonObject.put("line2addressofcompany", company.getAddressLine2());
							jsonObject.put("companyname", company.getCompanyName());
							jsonObject.put("companyrcnumber", company.getCompanyRCNumber());
							jsonObject.put("companyemailaddress", company.getEmailAddress());
							jsonObject.put("companycontactphonenumber", company.getMobileNumber());
							jsonObject.put("selectedBankBranchId", company.getBankBranches().getId());
							jsonObject.put("selectedCompanyType", company.getCompanyType().getValue());
							jsonObject.put("selectedCompanyClass", company.getClearingAgent()==null ? "0" : (company.getClearingAgent().equals(Boolean.TRUE) ? "1" : "0"));
							jsonObject.put("tpin", tpinInfo.getTpin());
							
							ApprovalFlowTransit aft = new ApprovalFlowTransit();
							aft.setActionType(ActionTypeConstants.COMPANY_ACTION_UNBLOCK);
							aft.setDateCreated(new Timestamp((new Date()).getTime()));
							aft.setEntityId(company.getId());
							aft.setEntityName(Company.class.getSimpleName());
							aft.setObjectData(jsonObject.toString());
							aft.setPortalUser(portletState.getPortalUser());
							aft.setWorkerId(null);
							swpService.createNewRecord(aft);
							
							Collection<PortalUser> pus = portletState.getCompanyManagementPortletUtil().getApprovingPortalUsers(
									portletState.getPortalUser().getRoleType().getRoleTypeName());
							
							
							for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
							{
								PortalUser pu1 = it.next();
								emailer.emailApprovalRequest(
										pu1.getFirstName(), 
										pu1.getLastName(), 
										pu1.getEmailAddress(), 
										portletState.getSystemUrl().getValue(), 
										portletState.getApplicationName().getValue() + " - Approval " +
												"Request for the Reactivation of A Company Profile", portletState.getApplicationName().getValue());
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
							
							
							aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
							portletState.addSuccess(aReq, "Request for deletion of company successfully created", portletState);
						} catch (org.codehaus.jettison.json.JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							portletState.addError(aReq, "Request for deletion of company could not be created", portletState);
						}
								
							
						
					}
					else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
					{
						company.setStatus(CompanyStatusConstants.COMPANY_STATUS_ACTIVE);
						swpService.updateRecord(company);
						handleAudit("Company Reactivation", Long.toString(company.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
						aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
						portletState.addSuccess(aReq, "Selected company - " + company.getCompanyName() + " - has been reactivated.", portletState);
						portletState.setCompanyListing(portletState.getCompanyManagementPortletUtil().getAllCompanyListing());
						
						Settings settingEmail = portletState.getNotifyCorporateFirmEmail();
						Settings settingSms = portletState.getNotifyCorporateFirmSms();
						Settings settingSystemUrl = portletState.getSystemUrl();
						
						boolean sendEmail = settingEmail.equals("1") ? true : false;
						boolean sendSms = settingSms.equals("1") ? true : false;
						String systemUrl = settingSystemUrl.getValue()==null ? "SmartPay" : settingSystemUrl.getValue();
						String companyType = company.getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY) ? "Corporate Account" : "Retail Account";
						TpinInfo tpinInfo = portletState.getCompanyManagementPortletUtil().getTPINForCompany(company);
//						sendNotificationOnCompanyReactivation(sendEmail, sendSms, company, 
//								systemUrl, companyType, tpinInfo);
					}
					
					
				}else if(aReq.getParameter("selectedCompanyAction")!=null && aReq.getParameter("selectedCompanyAction").equalsIgnoreCase("approveActivate"))
				{
					if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
					{
						TpinInfo tpinInfo = portletState.getCompanyManagementPortletUtil().getTPINForCompany(company);
						JSONObject jsonObject = new JSONObject();
						try {
							jsonObject.put("bankNumber", company.getAccountNumber());
							jsonObject.put("line1addressofcompany", company.getAddressLine1());
							jsonObject.put("line2addressofcompany", company.getAddressLine2());
							jsonObject.put("companyname", company.getCompanyName());
							jsonObject.put("companyrcnumber", company.getCompanyRCNumber());
							jsonObject.put("companyemailaddress", company.getEmailAddress());
							jsonObject.put("companycontactphonenumber", company.getMobileNumber());
							jsonObject.put("selectedBankBranchId", company.getBankBranches().getId());
							jsonObject.put("selectedCompanyType", company.getCompanyType().getValue());
							jsonObject.put("selectedCompanyClass", company.getClearingAgent()==null ? "0" : (company.getClearingAgent().equals(Boolean.TRUE) ? "1" : "0"));
							jsonObject.put("tpin", tpinInfo.getTpin());
							
							ApprovalFlowTransit aft = new ApprovalFlowTransit();
							aft.setActionType(ActionTypeConstants.COMPANY_ACTION_UNBLOCK);
							aft.setDateCreated(new Timestamp((new Date()).getTime()));
							aft.setEntityId(company.getId());
							aft.setEntityName(Company.class.getSimpleName());
							aft.setObjectData(jsonObject.toString());
							aft.setPortalUser(portletState.getPortalUser());
							aft.setWorkerId(null);
							swpService.createNewRecord(aft);
							
							Collection<PortalUser> pus = portletState.getCompanyManagementPortletUtil().getApprovingPortalUsers(
									portletState.getPortalUser().getRoleType().getRoleTypeName());
							
							
							for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
							{
								PortalUser pu1 = it.next();
								emailer.emailApprovalRequest(
										pu1.getFirstName(), 
										pu1.getLastName(), 
										pu1.getEmailAddress(), 
										portletState.getSystemUrl().getValue(), 
										portletState.getApplicationName().getValue() + " - Approval " +
												"Request for the Activation of A Company Profile", portletState.getApplicationName().getValue());
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
							
							aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
							portletState.addSuccess(aReq, "Request for deletion of company successfully created", portletState);
						} catch (org.codehaus.jettison.json.JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							portletState.addError(aReq, "Request for deletion of company could not be created", portletState);
							aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
						}
								
							
						
					}
					else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
					{
//						company.setStatus(CompanyStatusConstants.COMPANY_STATUS_ACTIVE);
//						swpService.updateRecord(company);
//						handleAudit("Company Reactivation", Long.toString(company.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
//						aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
//						portletState.addSuccess(aReq, "Selected company - " + company.getCompanyName() + " - has been reactivated.", portletState);
//						portletState.setCompanyListing(portletState.getCompanyManagementPortletUtil().getAllCompanyListing());
//						
//						Settings settingEmail = portletState.getNotifyCorporateFirmEmail();
//						Settings settingSms = portletState.getNotifyCorporateFirmSms();
//						Settings settingSystemUrl = portletState.getSystemUrl();
//						
//						boolean sendEmail = settingEmail.equals("1") ? true : false;
//						boolean sendSms = settingSms.equals("1") ? true : false;
//						String systemUrl = settingSystemUrl.getValue()==null ? "SmartPay" : settingSystemUrl.getValue();
//						String companyType = company.getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY) ? "Corporate Account" : "Retail Account";
//						TpinInfo tpinInfo = portletState.getCompanyManagementPortletUtil().getTPINForCompany(company);
//						sendNotificationOnCompanyReactivation(sendEmail, sendSms, company, 
//								systemUrl, companyType, tpinInfo);
					}
					
					
					
					
				}
			}else
			{
				portletState.addError(aReq, "This action can not be carried out on the selected company. You seem to have not selected a valid company. Select one before proceeding", portletState);
				aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
			}
		}catch(NumberFormatException e)
		{
			e.printStackTrace();
			portletState.addError(aReq, "This action can not be carried out on the selected company. Select a valid company before proceeding", portletState);
			aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
		}
		
		
	}

	private void createACompanyStepTwo(ActionRequest aReq, ActionResponse aRes, CompanyManagementPortletState portletState) {
		// TODO Auto-generated method stub
		aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companycreation/stepone.jsp");
		createACompany(aReq, aRes, portletState);
	}
	
	private void editACompanyStepTwo(ActionRequest aReq, ActionResponse aRes, CompanyManagementPortletState portletState) {
		// TODO Auto-generated method stub

		String companyeditaction = aReq.getParameter("companyeditaction");
		if(companyeditaction!=null && companyeditaction.equalsIgnoreCase("goBack"))
		{
			aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companycreation/stepone_edit.jsp");
		}else if(companyeditaction!=null && companyeditaction.equalsIgnoreCase("yessave"))
		{
			aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
			editACompany(aReq, aRes, portletState);
		}
	}
	
	
	
	private void loginStepTwo(ActionRequest aReq, ActionResponse aRes,
			CompanyManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String email2 = aReq.getParameter("usernameemail");
		log.info("email2 = "+ email2);
		String password = aReq.getParameter("password");
		log.info("password = " + password);
		
		ComminsApplicationState cappState = portletState.getCas();
		log.info("cappState  we just got the application state");
		
		try {
			long login = UserLocalServiceUtil.authenticateForBasic(ProbaseConstants.COMPANY_ID, CompanyConstants.AUTH_TYPE_EA, 
					email2, password);
			log.info("login calue = " + login);
			if(login==0)
			{
				log.info("User Credentials are invalid");
				cappState.setLoggedIn(Boolean.FALSE);
				cappState.setPortalUser(null);
				portletState.addError(aReq, "Invalid login credentials", portletState);
			}
			else
			{
				log.info("User Credentials are Valid");
				PortalUser pu = portletState.getCompanyManagementPortletUtil().getPortalUserByEmailAddress(email2);
				if(pu.getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
				{
					if(portletState.getPortalUserCRUDRights().getCudInitiatorRights().equals(Boolean.TRUE))
					{
						if(pu!=null && pu.getStatus().equals(PortalUserStatusConstants.PORTAL_USER_STATUS_ACTIVE))
						{
							PortalUserCRUDRights pcrs = portletState.getCompanyManagementPortletUtil().getPortalUserCRUDRightsByPortalUser(pu);
							if(pcrs.getCudApprovalRights()!=null && pcrs.getCudApprovalRights().equals(Boolean.TRUE))
							{
								cappState.setPortalUser(pu);
								cappState.setLoggedIn(Boolean.TRUE);
							}else
							{
								cappState.setPortalUser(null);
								cappState.setLoggedIn(Boolean.FALSE);
								portletState.addError(aReq, "Invalid login credentials", portletState);
							}
						}
						else
						{
							cappState.setPortalUser(null);
							cappState.setLoggedIn(Boolean.FALSE);
							portletState.addError(aReq, "Invalid login credentials", portletState);
						}
					}else
					{
						cappState.setPortalUser(null);
						cappState.setLoggedIn(Boolean.FALSE);
						portletState.addError(aReq, "Invalid login credentials", portletState);
					}
				}else
				{

					cappState.setPortalUser(null);
					cappState.setLoggedIn(Boolean.FALSE);
					portletState.addError(aReq, "Invalid login credentials", portletState);
				}
				
			}
				
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	
	
	private void createACompany(ActionRequest aReq, ActionResponse aRes,
			CompanyManagementPortletState portletState) {
		// TODO Auto-generated method stub
		ComminsApplicationState cappState = portletState.getCas();
		Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
		Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
		DETERMINE_ACCESS determinAccess = new Util().determineAccessForCompanyFxns(twoStep, approvalProcess, cappState, portletState.getCompanyCRUDRights());
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());

		
		try
		{
			
			if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
			{
				JSONObject jsonObject = new JSONObject();
				

				jsonObject.put("bankNumber", portletState.getBankNumber().trim());
				jsonObject.put("line1addressofcompany", portletState.getLine1addressofcompany().trim());
				jsonObject.put("line2addressofcompany", portletState.getLine2addressofcompany().trim());
				jsonObject.put("companyname", portletState.getCompanyname());
				jsonObject.put("companyrcnumber", portletState.getCompanyrcnumber());
				jsonObject.put("companyemailaddress", portletState.getCompanyemailaddress());
				jsonObject.put("companycontactphonenumber", portletState.getCompanycontactphonenumber());
				jsonObject.put("selectedBankBranchId", portletState.getSelectedBankBranchId());
				jsonObject.put("selectedCompanyType", portletState.getSelectedCompanyType());
				jsonObject.put("selectedCompanyClass", portletState.getSelectedCompanyClass());
				jsonObject.put("tpin", portletState.getTpin());
				if(portletState.getSelectedCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY.getValue()))
					jsonObject.put("mandatePanelsOn", portletState.getMandatePanelsOn());
				else
					jsonObject.put("mandatePanelsOn", "0");
				log.info(jsonObject.toString());
				ApprovalFlowTransit aft = new ApprovalFlowTransit();
				aft.setActionType(ActionTypeConstants.COMPANY_ACTION_CREATE);
				aft.setDateCreated(new Timestamp((new Date()).getTime()));
				aft.setEntityId(null);
				aft.setEntityName(Company.class.getSimpleName());
				aft.setObjectData(jsonObject.toString());
				aft.setPortalUser(portletState.getPortalUser());
				aft.setWorkerId(null);
				swpService.createNewRecord(aft);
				
				
				Collection<PortalUser> pus = portletState.getCompanyManagementPortletUtil().getApprovingPortalUsers(
						portletState.getPortalUser().getRoleType().getRoleTypeName());
				
				for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
				{
					PortalUser pu1 = it.next();
					emailer.emailApprovalRequest(
							pu1.getFirstName(), 
							pu1.getLastName(), 
							pu1.getEmailAddress(), 
							portletState.getSystemUrl().getValue(), 
							portletState.getApplicationName().getValue() + " - Approval " +
									"Request for the Creation of A Company", portletState.getApplicationName().getValue());
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
				
				
				if(portletState.getNotifyCorporateFirmSms().getValue().equals("1"))
					//sendSms();
				
				aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
				portletState.setCompanyListing(portletState.getCompanyManagementPortletUtil().getAllCompanyListing());
				portletState.setCurrentTab(VIEW_TABS.VIEW_COMPANY_LISTINGS);
				portletState.addSuccess(aReq, "Company account request was created successfully. This request will need to be approved " +
						"by an approval officer before this company profile can be created.", portletState);
			}
			else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
			{
				Company company = new Company();
				company.setAccountNumber(portletState.getBankNumber().trim());
				company.setAddressLine1(portletState.getLine1addressofcompany().trim());
				company.setAddressLine2(portletState.getLine2addressofcompany().trim());
				company.setCompanyName(portletState.getCompanyname());
				company.setCompanyRCNumber(portletState.getCompanyrcnumber());
				company.setEmailAddress(portletState.getCompanyemailaddress());
				company.setMobileNumber(portletState.getCompanycontactphonenumber());
				company.setBankBranches((BankBranches)portletState.getCompanyManagementPortletUtil().getEntityObjectById(BankBranches.class, 
						Long.valueOf(portletState.getSelectedBankBranchId())));
				company.setCompanyType(CompanyTypeConstants.fromString(portletState.getSelectedCompanyType()));
				company.setStatus(CompanyStatusConstants.COMPANY_STATUS_INACTIVE);
				company.setClearingAgent(portletState.getSelectedCompanyClass()!=null && portletState.getSelectedCompanyClass().equals("1") ? Boolean.TRUE : Boolean.FALSE);
				company.setCreatedByPortalUserId(portletState.getPortalUser().getId());
				if(portletState.getSelectedCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY.getValue()))
					company.setMandatePanelsOn(portletState.getMandatePanelsOn().equals("1") ? true : false);
				else
					company.setMandatePanelsOn(false);
				
				
				//company.setCreateMandatePanel(portletState.getSelectedCreateMandatePanels()!=null && portletState.getSelectedCreateMandatePanels().equals("1") ? Boolean.TRUE : Boolean.FALSE);
				
				
				company = (Company)this.swpService.createNewRecord(company);
				if(company!=null)
				{
					handleAudit("Company Creation", Long.toString(company.getId()), new Timestamp((new Date()).getTime()), 
						portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
					
					portletState.setSelectedCompany(company);
					log.info("We are in create a portal user1");
					Calendar c1 = GregorianCalendar.getInstance();
					c1.set(1980, Calendar.JANUARY, 1);
					Date date =c1.getTime();
					Timestamp dob = new Timestamp(date.getTime());
					RoleType roleType = (RoleType)portletState.getCompanyManagementPortletUtil().getEntityObjectById(RoleType.class, Long.valueOf(portletState.getSelectedUserRoleId()));
					
						
						TpinInfo tpinInfo = new TpinInfo();
						tpinInfo.setCompany(portletState.getSelectedCompany());
						tpinInfo.setDateUpdated(new Timestamp((new Date()).getTime()));
						tpinInfo.setTpin(portletState.getTpin());
						tpinInfo = (TpinInfo)this.swpService.createNewRecord(tpinInfo);
						
					Settings settingEmail = portletState.getNotifyCorporateIndividualEmail();
					Settings settingSms = portletState.getNotifyCorporateIndividualSMS();
					Settings settingSystemUrl = portletState.getSystemUrl();
					
				}
				aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
				portletState.setCompanyListing(portletState.getCompanyManagementPortletUtil().getAllCompanyListing());
				portletState.setCurrentTab(VIEW_TABS.VIEW_COMPANY_LISTINGS);
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

	private void editACompany(ActionRequest aReq, ActionResponse aRes,
			CompanyManagementPortletState portletState) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		ComminsApplicationState cappState = portletState.getCas();
		Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
		Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
		DETERMINE_ACCESS determinAccess = new Util().determineAccessForCompanyFxns(twoStep, approvalProcess, cappState, portletState.getCompanyCRUDRights());
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		
		
		try
		{
			
			if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
			{
				Company company = portletState.getCompanyManagementPortletUtil().getCompanyById(portletState.getSelectedCompanyId());
				if(company!=null)
				{
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("bankNumber", portletState.getBankNumber().trim());
					jsonObject.put("line1addressofcompany", portletState.getLine1addressofcompany().trim());
					jsonObject.put("line2addressofcompany", portletState.getLine2addressofcompany().trim());
					jsonObject.put("companyname", portletState.getCompanyname());
					jsonObject.put("companyrcnumber", portletState.getCompanyrcnumber());
					jsonObject.put("companyemailaddress", portletState.getCompanyemailaddress());
					jsonObject.put("companycontactphonenumber", portletState.getCompanycontactphonenumber());
					jsonObject.put("selectedBankBranchId", portletState.getSelectedBankBranchId());
					jsonObject.put("selectedCompanyType", portletState.getSelectedCompanyType());
					jsonObject.put("selectedCompanyClass", portletState.getSelectedCompanyClass());
					jsonObject.put("tpin", portletState.getTpin());
					jsonObject.put("mandatePanelsOn", company.getMandatePanelsOn()==null ? "0" : (company.getMandatePanelsOn()));
					
					if(company.getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY))
						jsonObject.put("mandatePanelsOn", portletState.getMandatePanelsOn());
					else
						jsonObject.put("mandatePanelsOn", "0");
					
					
					ApprovalFlowTransit aft = new ApprovalFlowTransit();
					aft.setActionType(ActionTypeConstants.COMPANY_ACTION_UPDATE);
					aft.setDateCreated(new Timestamp((new Date()).getTime()));
					aft.setEntityId(company.getId());
					aft.setEntityName(Company.class.getSimpleName());
					aft.setObjectData(jsonObject.toString());
					aft.setPortalUser(portletState.getPortalUser());
					aft.setWorkerId(null);
					swpService.createNewRecord(aft);
					
					Collection<PortalUser> pus = portletState.getCompanyManagementPortletUtil().getApprovingPortalUsers(
							portletState.getPortalUser().getRoleType().getRoleTypeName());
					
					
					for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
					{
						PortalUser pu1 = it.next();
						emailer.emailApprovalRequest(
								pu1.getFirstName(), 
								pu1.getLastName(), 
								pu1.getEmailAddress(), 
								portletState.getSystemUrl().getValue(), 
								portletState.getApplicationName().getValue() + " - Approval " +
										"Request for the Update of A Company Profile", portletState.getApplicationName().getValue());
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
					
					
					portletState.addSuccess(aReq, "Request for Update of Company details for - " + company.getCompanyName() + " - has been saved successfully!", portletState);
					portletState.setCompanyListing(portletState.getCompanyManagementPortletUtil().getAllCompanyListing());
					aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
				}else
				{
	
					portletState.addError(aReq, "Request for Update of Company details was not saved! Please select a valid company before editing it.", portletState);
					aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
				}
			}
			else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
			{
				Company company = portletState.getCompanyManagementPortletUtil().getCompanyById(portletState.getSelectedCompanyId());
				if(company!=null)
				{
					company.setAccountNumber(portletState.getBankNumber().trim());
					company.setAddressLine1(portletState.getLine1addressofcompany().trim());
					company.setAddressLine2(portletState.getLine2addressofcompany().trim());
					company.setCompanyName(portletState.getCompanyname());
					company.setCompanyRCNumber(portletState.getCompanyrcnumber());
					company.setEmailAddress(portletState.getCompanyemailaddress());
					company.setMobileNumber(portletState.getCompanycontactphonenumber());
					company.setClearingAgent(portletState.getSelectedCompanyClass()!=null && portletState.getSelectedCompanyClass().equals("1") ? Boolean.TRUE : Boolean.FALSE);
					if(company.getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY))
						company.setMandatePanelsOn(portletState.getMandatePanelsOn().equals("1") ? true: false);
					else
						company.setMandatePanelsOn(false);
					this.swpService.updateRecord(company);
					
					
					if(company!=null)
					{
						
						emailer.emailNewCorporateCompany(company.getEmailAddress(), 
								company.getCompanyName(),
								portletState.getSystemUrl().getValue(), 
								company.getCompanyType().getValue().replace("_", " "),
								portletState.getTpin(),
								company.getAccountNumber(),
								company.getBankBranches().getName(),
								company.getMobileNumber(),
								portletState.getApplicationName().getValue() + " - Update of Company Profile Account", 
								portletState.getSelectedCompanyClass()==null ? company.getCompanyType().getValue().replace("_", " ") : 
									(portletState.getSelectedCompanyClass().equals("1") ? "Agent" : "SoleTrader"), portletState.getApplicationName().getValue());
						
						
							String message = "Approval request awaiting your action. " +
									"Visit " + portletState.getSystemUrl().getValue() + 
									" to view requests awaiting your " +
									"approval/disapproval action";
							SendSms sendSms = new SendSms(company.getMobileNumber(), message, 
									portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
							
						handleAudit("Company Update", Long.toString(company.getId()), new Timestamp((new Date()).getTime()), 
							portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
						
						
					}
					portletState.addSuccess(aReq, "Company details for - " + company.getCompanyName() + " - has been saved successfully!", portletState);
					portletState.setCompanyListing(portletState.getCompanyManagementPortletUtil().getAllCompanyListing());
					aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
				}else
				{
	
					portletState.addError(aReq, "The Company details were not saved! Please select a valid company before editing it.", portletState);
					aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
				}
			}else
			{
				portletState.addError(aReq, "Editing the selected company was not successful. You do not have the appropriate rights to update companies. " +
						"Contact the approprate authorities for these rights.", portletState);
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
	
	
	

	private void createACompanyStepOne(ActionRequest aReq, ActionResponse aRes, CompanyManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setCompanyname(aReq.getParameter("companyName"));
		portletState.setCompanyrcnumber(aReq.getParameter("companyRCNumber"));
		portletState.setCompanycontactphonenumber(aReq.getParameter("contactMobileNumber"));
		portletState.setCompanyemailaddress(aReq.getParameter("contactEmailAddress"));
		portletState.setLine1addressofcompany(aReq.getParameter("contactAddressLine1"));
		portletState.setLine2addressofcompany(aReq.getParameter("contactAddressLine2"));
		portletState.setBankNumber(aReq.getParameter("accountNumber"));
		portletState.setSelectedBankBranchId(aReq.getParameter("bankBranch"));
		
		portletState.setSelectedCompanyType(aReq.getParameter("companyType"));
		portletState.setSelectedCompanyClass(aReq.getParameter("classification"));
		portletState.setMandatePanelsOn(aReq.getParameter("mandatePanelsOn")==null ? "0" : aReq.getParameter("mandatePanelsOn"));

		portletState.setTpin(aReq.getParameter("tpin"));
		for(Iterator<RoleType> iterRt = portletState.getRoleTypeListing().iterator(); iterRt.hasNext();)
		{
			RoleType rt = iterRt.next();
			
			if(portletState.getSelectedCompanyType()!=null && !portletState.getSelectedCompanyType().equals("-1"))
			{
				CompanyTypeConstants ctc = CompanyTypeConstants.fromString(portletState.getSelectedCompanyType());
				
				if(ctc.equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY) && rt.getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
				{
					portletState.setSelectedUserRoleId(Long.toString(rt.getId()));
					log.info("Cd Check = 1");
				}
				else if(ctc.equals(CompanyTypeConstants.COMPANY_TYPE_RETAIL_COMPANY) && rt.getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_RETAIL_STAFF))
				{
					portletState.setSelectedUserRoleId(Long.toString(rt.getId()));
					log.info("Cd Check = 2");
				}
			}
		}
		
		log.info("Check if data is valid");
		log.info("Selected User Role ==" + portletState.getSelectedUserRoleId());
		
		
		if(isCreationDataValid(portletState, aReq, aRes, true))
		{
			//if(isPortalUserCreationDataValid(portletState, aReq, aRes))
			//{
				aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companycreation/steptwo.jsp");
			//}else
			//{
			//	aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companycreation/stepone.jsp");
			//}
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companycreation/stepone.jsp");
		}
	}
	
	
	
	
	
	private void editACompanyStepOne(ActionRequest aReq, ActionResponse aRes, CompanyManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setCompanyname(aReq.getParameter("companyName"));
		portletState.setCompanyrcnumber(aReq.getParameter("companyRCNumber"));
		portletState.setCompanycontactphonenumber(aReq.getParameter("contactMobileNumber"));
		portletState.setCompanyemailaddress(aReq.getParameter("contactEmailAddress"));
		portletState.setLine1addressofcompany(aReq.getParameter("contactAddressLine1"));
		portletState.setLine2addressofcompany(aReq.getParameter("contactAddressLine2"));
		portletState.setBankNumber(aReq.getParameter("accountNumber"));
		portletState.setSelectedBankBranchId(aReq.getParameter("bankBranch"));
		String companyeditaction = aReq.getParameter("companyeditaction");
		portletState.setSelectedCompanyType(aReq.getParameter("companyType"));
		portletState.setSelectedCompanyClass(aReq.getParameter("classification"));
		portletState.setMandatePanelsOn(aReq.getParameter("mandatePanelsOn")==null ? "0" : aReq.getParameter("mandatePanelsOn"));
		log.info(companyeditaction==null ? "" : companyeditaction);
		if(companyeditaction!=null && companyeditaction.equalsIgnoreCase("caneledit"))
		{
			aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companylisting/companylisting.jsp");
		}else if(companyeditaction!=null && companyeditaction.equalsIgnoreCase("continueedit"))
		{
			if(isCreationDataValid(portletState, aReq, aRes, false))
			{
				aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companycreation/steptwo_edit.jsp");
			}else
			{
				aRes.setRenderParameter("jspPage", "/html/companymanagementportlet/company/companycreation/stepone_edit.jsp");
			}
		}
		
		
		
	}
	
	
	
	private boolean isCreationDataValid(CompanyManagementPortletState portletState,
			ActionRequest aReq, ActionResponse aRes, boolean checkExistingForNew) {
		// TODO Auto-generated method stub
		String errorMessage = null;
		
		if(portletState.getCompanyname()!=null && portletState.getCompanyname().trim().length()>0)
		{
			if(portletState.getCompanyrcnumber()!=null && portletState.getCompanyrcnumber().trim().length()>0)
			{
				if(portletState.getCompanycontactphonenumber()!=null && portletState.getCompanycontactphonenumber().trim().length()>0)
				{
					if(portletState.getCompanyemailaddress()!=null && portletState.getCompanyemailaddress().trim().length()>0)
					{
						if(portletState.getLine1addressofcompany()!=null && portletState.getLine1addressofcompany().trim().length()>0)
						{
							if(portletState.getBankNumber()!=null && portletState.getBankNumber().trim().length()>0)
							{
								if(portletState.getSelectedBankBranchId()!=null && portletState.getSelectedBankBranchId().trim().length()>0)
								{
									
									
										if(checkExistingForNew && portletState.getSelectedCompanyType()!=null && portletState.getSelectedCompanyType().trim().equalsIgnoreCase("-1"))
										{
											errorMessage =  "Select the company type before proceeding";
										}
										
										
										
										Collection<Company> checkCompany = null;
										if(checkExistingForNew)
										{
											checkCompany = portletState.getCompanyManagementPortletUtil().getCompanyByNameOrRCNumber(
													portletState.getCompanyname(), portletState.getCompanyrcnumber());
										}else
										{
											checkCompany = portletState.getCompanyManagementPortletUtil().getCompanyByNameOrRCNumberForEdit(
													portletState.getCompanyname(), portletState.getCompanyrcnumber(), portletState.getSelectedCompanyId());
										}
										
										
										
										
										if(checkCompany!=null && checkCompany.size()>0)
										{
											errorMessage =  "The company name and Company Registration Number provided already exist on the system. This company already has been created.";
											
										}else
										{
											TpinInfo tpin = null;
											if(checkExistingForNew)
											{
												tpin = portletState.getCompanyManagementPortletUtil().getTPINByTPinNumber(
														portletState.getTpin());
												if(tpin!=null)
												{
													errorMessage =  "The Tax Payer Identification Number provided already exist on the system. Provide another valid Tax Payer Identification number.";
													
												}else
												{
													Company checkCompany1 = null;
													if(checkExistingForNew)
													{
														checkCompany1 = portletState.getCompanyManagementPortletUtil().getCompanyByAccountNumber(
																portletState.getBankNumber());
													}else
													{
														checkCompany1 = portletState.getCompanyManagementPortletUtil().getCompanyByAccountNumber(
																portletState.getBankNumber(), portletState.getSelectedCompanyId());
													}
													
													
													if(tpin!=null)
													{
														errorMessage =  "The Account Number provided belongs to another company on this system. Provide another valid bank account number.";
														
													}else
													{
														
													}
												}
											}else
											{
												
											}
											
											
											
										}
										
										
										if(portletState.getSelectedCompanyType()!=null && portletState.getSelectedCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY.getValue()))
										{
											if(portletState.getSelectedCompanyClass()!=null && !portletState.getSelectedCompanyClass().trim().equals("-1"))
											{
												
											}else
											{
												errorMessage =  "Select a company classification before proceeding";
											}
										}else
										{
											
										}
									
								
								}else
								{
									errorMessage =  "Select a company bank branch before proceeding";
								}
							}else
							{
								errorMessage =  "Provide the company's account number before proceeding";
							}
						}else
						{
							errorMessage =  "Provide the company's first line of address before proceeding";
						}
					}else
					{
						errorMessage =  "Provide the company's email address before proceeding";
					}
				}else
				{
					errorMessage =  "Provide the company's mobile number before proceeding";
				}
			}else
			{
				errorMessage =  "Provide the company's rc number before proceeding";
			}
		}else
		{
			errorMessage =  "Provide the name of the company before proceeding";
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
