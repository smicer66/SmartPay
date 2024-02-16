package com.probase.smartpay.admin.corporateindividualmanagement;

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

import com.liferay.portal.DuplicateUserScreenNameException;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.probase.smartpay.admin.corporateindividualmanagement.CorporateIndividualManagementPortlet;
import com.probase.smartpay.admin.corporateindividualmanagement.CorporateIndividualManagementPortletState;
import com.probase.smartpay.admin.corporateindividualmanagement.CorporateIndividualManagementPortletUtil;
import com.probase.smartpay.admin.corporateindividualmanagement.CorporateIndividualManagementPortletState.COMPANY_CREATE_INDIVIDUAL_ACTIONS;
import com.probase.smartpay.admin.corporateindividualmanagement.CorporateIndividualManagementPortletState.CORPORATE_INDIVIDUAL_VIEW;
import com.probase.smartpay.admin.corporateindividualmanagement.CorporateIndividualManagementPortletState.NAVIGATE;
import com.probase.smartpay.admin.corporateindividualmanagement.CorporateIndividualManagementPortletState.VIEW_TABS;
import com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortlet;
import com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortletState;
import com.probase.smartpay.commins.ComminsApplicationState;
import com.probase.smartpay.commins.Emailer;
import com.probase.smartpay.commins.Mailer;
import com.probase.smartpay.commins.ProbaseConstants;
import com.probase.smartpay.commins.SendMail;
import com.probase.smartpay.commins.SendSms;
import com.probase.smartpay.commins.Util;
import com.probase.smartpay.commins.Util.DETERMINE_ACCESS;
import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Portlet implementation class CorporateIndividualManagementPortlet
 */
public class CorporateIndividualManagementPortlet extends MVCPortlet {

	private Logger log = Logger.getLogger(CorporateIndividualManagementPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
	CorporateIndividualManagementPortletUtil util = CorporateIndividualManagementPortletUtil.getInstance();
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
		CorporateIndividualManagementPortletState portletState = 
				CorporateIndividualManagementPortletState.getInstance(renderRequest, renderResponse);

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
		
		CorporateIndividualManagementPortletState portletState = CorporateIndividualManagementPortletState.getInstance(aReq, aRes);
		
		
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
        boolean proceed = false;
        
        if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
        {
	        if(portletState.getPortalUser().getCompany()!=null && 
	        		portletState.getPortalUser().getCompany().getStatus().equals(CompanyStatusConstants.COMPANY_STATUS_ACTIVE))
	        {
	        	proceed =true;
	        }
        }else if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
        {
	        proceed = true;
        }
        	
        
        if(proceed)
        {
        	if(action.equalsIgnoreCase(COMPANY_CREATE_INDIVIDUAL_ACTIONS.LOGIN_STEP_TWO.name()))
            {
            	log.info("We are inside step two of login");
            	loginStepTwo(aReq, aRes, portletState);
            }
        	if(action.equalsIgnoreCase(COMPANY_CREATE_INDIVIDUAL_ACTIONS.MODIFY_COMPANY.name()))
            {
            	handleCompanyStaffListingAction(aReq, aRes, portletState);
            }
        	if(action.equalsIgnoreCase(COMPANY_CREATE_INDIVIDUAL_ACTIONS.CREATE_AN_INDIVIDUAL__PRE_STEP_ONE.name()))
	        {
	        	addCompanyStaffPreStepOne(aReq, aRes, portletState);
	        }
        	if(action.equalsIgnoreCase(COMPANY_CREATE_INDIVIDUAL_ACTIONS.VIEW_USERS__PRE_STEP_ONE.name()))
	        {
        		portletState.setCurrentTab(VIEW_TABS.VIEW_CORPORATE_INDIVIDUAL_LISTINGS);
	        	viewCompanyStaffPreStepOne(aReq, aRes, portletState);
	        }
        	if(action.equalsIgnoreCase(COMPANY_CREATE_INDIVIDUAL_ACTIONS.CREATE_AN_INDIVIDUAL_STEP_ONE.name()))
	        {
        		String formaction = aReq.getParameter("corporatestaffaction");
        		log.info("formaction = " + formaction );
        		if(formaction.equalsIgnoreCase("next"))
        		{
            		log.info("handle next = ");
        			createAnCompanyIndividualStepOne(aReq, aRes, portletState);
        		}else if(formaction.equalsIgnoreCase("back"))
        		{
            		log.info("handle back = ");
        			portletState.setSelectedCompanyId(null);
    	        	aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/selectCompany.jsp");
        			
        		}
	        }if(action.equalsIgnoreCase(COMPANY_CREATE_INDIVIDUAL_ACTIONS.CREATE_AN_INDIVIDUAL_STEP_THREE.name()))
	        {
	        	createAnCompanyIndividualStepThree(aReq, aRes, portletState);
	        }if(action.equalsIgnoreCase(CORPORATE_INDIVIDUAL_VIEW.CREATE_A_CORPORATE_INDIVIDUAL.name()))
	        {
	        	portletState.reinitializeForCreateCorporateIndividual(portletState);
	        	
	        	
	        	if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
	        	{
	        		aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/corporateindividual/register/stepone.jsp");
		        	portletState.setCurrentTab(VIEW_TABS.CREATE_A_CORPORATE_INDIVIDUAL);
	        	}else if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
	        	{
	        		ComminsApplicationState cappState = portletState.getCas();
		        	Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
		        	Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
		        	DETERMINE_ACCESS determinAccess = new Util().determineAccessForUserFxns(twoStep, approvalProcess, cappState, portletState.getPortalUserCRUDRights());
		        	if(determinAccess.equals(DETERMINE_ACCESS.DISPLAY_SECOND_LEVEL_LOGIN))
		        	{
		        		
		        	}else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_APPROVER_ACCESS))
		        	{
		        		aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/selectcompanyforuserlist.jsp");
			        	portletState.setCurrentTab(VIEW_TABS.CREATE_A_CORPORATE_INDIVIDUAL);
		        	}else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
		        	{
		        		aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/selectCompany.jsp");
			        	portletState.setCurrentTab(VIEW_TABS.CREATE_A_CORPORATE_INDIVIDUAL);
		        	}else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
		        	{
		        		aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/selectCompany.jsp");
			        	portletState.setCurrentTab(VIEW_TABS.CREATE_A_CORPORATE_INDIVIDUAL);
		        	}else if(determinAccess.equals(DETERMINE_ACCESS.NO_RIGHTS_AT_ALL))
		        	{
		        		aRes.setRenderParameter("jspPage", "/html/access/accessrestricted.jsp");
			        	portletState.setCurrentTab(VIEW_TABS.CREATE_A_CORPORATE_INDIVIDUAL);
		        	}
	        	}
	        	
	        }if(action.equalsIgnoreCase(CORPORATE_INDIVIDUAL_VIEW.VIEW_CORPORATE_INDIVIDUAL_LISTINGS.name()))
	        {
	        	//set corpoate indivudla listings
	        	
	        	if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
	        	{
	        		aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/selectcompanyforuserlist.jsp");
	        	}else
	        	{
	        		portletState.setAllCompanyPersonnel(portletState.getCorporateIndividualManagementPortletUtil().getAllPortalUserByCompany(portletState.getPortalUser().getCompany()));
		        	aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/userlisting.jsp");
	        	}
	        	portletState.setCurrentTab(VIEW_TABS.VIEW_CORPORATE_INDIVIDUAL_LISTINGS);
	        }   
	        if(action.equalsIgnoreCase(COMPANY_CREATE_INDIVIDUAL_ACTIONS.UPDATE_AN_INDIVIDUAL_STEP_ONE.name()))
	        {
	        	//set corpoate indivudla listings
	        	String action_ = aReq.getParameter("selectedPortalUserAction");
	        	log.info("action_" + action_);
	        	if(action_.equalsIgnoreCase("next"))
	        	{
	        		log.info("update now");
		        	handleUpdateCompanyStaffStepOne(aReq, aRes, portletState);
		        	portletState.setCurrentTab(VIEW_TABS.VIEW_CORPORATE_INDIVIDUAL_LISTINGS);
	        	}else if(action_.equalsIgnoreCase("back"))
	        	{
	        		log.info("cancel update");
	        		portletState.setSelectedPortalUserId(null);
	        		aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/userlisting.jsp");
		        	portletState.setCurrentTab(VIEW_TABS.VIEW_CORPORATE_INDIVIDUAL_LISTINGS);
	        	}
	        }  
	        if(action.equalsIgnoreCase(COMPANY_CREATE_INDIVIDUAL_ACTIONS.UPDATE_AN_INDIVIDUAL_STEP_THREE.name()))
	        {
	        	//set corpoate indivudla listings
	        	String action_ = aReq.getParameter("selectedPortalUserAction");
	        	if(action_.equalsIgnoreCase("updateNow"))
	        	{
		        	handleUpdateCompanyStaffStepThree(aReq, aRes, portletState);
	        		portletState.setSelectedPortalUserId(null);
	        	}else if(action_.equalsIgnoreCase("cancelupdate"))
	        	{
	        		portletState.setSelectedPortalUserId(null);
	        		aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/userlisting.jsp");
	        	}
	        	portletState.setCurrentTab(VIEW_TABS.VIEW_CORPORATE_INDIVIDUAL_LISTINGS);
	        }  
	        
        }else
        {
        	portletState.reinitializeForCreateCorporateIndividual(portletState);
        	aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/corporateindividual/register/stepone.jsp");
        	portletState.setCurrentTab(VIEW_TABS.CREATE_A_CORPORATE_INDIVIDUAL);
        	portletState.addError(aReq, "You do not have appropriate rights to carry out this action on the selected company staff", portletState);
        }
        
	}
	
	
	
	
	
	
	private void handleUpdateCompanyStaffStepThree(ActionRequest aReq,
			ActionResponse aRes,
			CorporateIndividualManagementPortletState portletState) {
		// TODO Auto-generated method stub

		if(isCompanyIndividualCreationDataValid(portletState, aReq, aRes, true, portletState.getSelectedPortalUserId()))
		{
			if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR)
					|| portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
			{
				Calendar c1 = GregorianCalendar.getInstance();
				c1.set(1980, Calendar.JANUARY, 1);
				Date date =c1.getTime();
				Timestamp dob = new Timestamp(date.getTime());
				
				
				ComminsApplicationState cappState = portletState.getCas();
				Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
				Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
				DETERMINE_ACCESS determinAccess = new Util().determineAccessForUserFxns(twoStep, approvalProcess, cappState, portletState.getPortalUserCRUDRights());
				

				if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
				{
					handleInitiateUpdateUser(aReq, aRes, portletState, dob);
				}
				else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
				{
					handleUpdateUser(aReq, aRes, portletState, dob);
				}
				
			}else
			{
				portletState.addError(aReq, "You do not have appropriate access rights to this module", portletState);
				portletState.reinitializeForCreateCorporateIndividual(portletState);
			}
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/corporateindividual/register/stepone_edit.jsp");
		}
	}

	private void handleInitiateUpdateUser(ActionRequest aReq,
			ActionResponse aRes,
			CorporateIndividualManagementPortletState portletState,
			Timestamp dob) {
		// TODO Auto-generated method stub
		log.info("Portlet state = " + portletState.getCorporateindividualfirstname() + " && "  + 
				portletState.getCorporateindividuallastname() + " && "  + 
				portletState.getCorporateindividualfirstemail() + " && " + 
				portletState.getCorporateindividualsecondemail() + " && " + 
				portletState.getCorporateindividualthirdemail() + " && " + 
				portletState.getCorporateindividualfirstmobile() + " && "  + 
				portletState.getCorporateindividualsecondmobile() + " && "  + 
				portletState.getCorporateindividualthirdmobile() + " && " + 
				portletState.getCorporateindividualAddressLine1() + " && " + 
				portletState.getCorporateindividualAddressLine2());
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		
		PortalUser pu = (PortalUser)portletState.getCorporateIndividualManagementPortletUtil().getEntityObjectById(PortalUser.class, 
				Long.valueOf(portletState.getSelectedPortalUserId()));
		if(pu!=null)
		{
			
			try
			{
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("firstName", portletState.getCorporateindividualfirstname());
				jsonObject.put("lastName", portletState.getCorporateindividuallastname());
				jsonObject.put("email", portletState.getCorporateindividualfirstemail());
				jsonObject.put("alternativeEmail1", portletState.getCorporateindividualsecondemail());
				jsonObject.put("alternativeEmail2", portletState.getCorporateindividualthirdemail());
				jsonObject.put("mobile", portletState.getCorporateindividualfirstmobile());
				jsonObject.put("alternativeMobile1", portletState.getCorporateindividualfirstmobile());
				jsonObject.put("alternativeMobile2", portletState.getCorporateindividualthirdmobile());
				jsonObject.put("addressLine1", portletState.getCorporateindividualAddressLine1());
				jsonObject.put("addressLine2", portletState.getCorporateindividualAddressLine2());
				jsonObject.put("dob", dob);
				jsonObject.put("roleType", pu.getRoleType().getRoleTypeName().getValue());
				jsonObject.put("company", pu.getCompany().getId());
				
				ApprovalFlowTransit approvalFlowTransit = new ApprovalFlowTransit();
				log.info("Start tracking here ");
				approvalFlowTransit.setEntityName(PortalUser.class.getSimpleName());
				log.info("class simple name =" + PortalUser.class.getSimpleName() + " && length = " + PortalUser.class.getSimpleName().length());
				approvalFlowTransit.setDateCreated(new Timestamp((new Date()).getTime()));
				log.info("new Timestamp((new Date()).getTime()) =" + new Timestamp((new Date()).getTime()));
				approvalFlowTransit.setObjectData(jsonObject.toString());
				log.info("jsonObject.toString() =" + jsonObject.toString() + " && length = " + jsonObject.toString().length());
				approvalFlowTransit.setActionType(ActionTypeConstants.PORTAL_USER_ACTION_UPDATE);
				approvalFlowTransit.setPortalUser(portletState.getPortalUser());
				approvalFlowTransit.setEntityId(pu.getId());
				swpService.createNewRecord(approvalFlowTransit);
				
				//FORWARD EMAIIL/SMS to Approving officers
				Collection<PortalUser> pus = portletState.getCorporateIndividualManagementPortletUtil().getApprovingPortalUsers(
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
									portletState.getApplicationName().getValue() + " - Approval " +
											"Request for the Update of A Company Staff Profile", portletState.getApplicationName().getValue());
						
					}
					
					if(settingSms.getValue().equals("1"))
					{
						
						try{
							String message = "Approval request awaiting your action. " +
									"Visit " + portletState.getSystemUrl().getValue() + 
									" to view requests awaiting your " +
									"approval/disapproval action";
							SendSms sendSms = new SendSms(pu1.getMobileNumber(), message, 
									portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
						}catch(Exception e){
							log.error("error sending sms ",e);
						}
					}
					
				}
		
				portletState.addSuccess(aReq, "A request has been sent to an approving officer to approve the creation of this " +
						"company staff. On approving, the company staff will receive an email/sms notifying them of the creation", portletState);
				
				aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/userlisting.jsp");
			}catch(JSONException e)
			{
				e.printStackTrace();
				portletState.addError(aReq, "A request for creation of a company staff could not be created. Please try again!", portletState);
				aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/userlisting.jsp");
			}
		}else
		{
			portletState.addError(aReq, "A request for creation of a company staff could not be created. Please try again!", portletState);
			
			aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/userlisting.jsp");
		}
		
	}

	private PortalUser handleUpdatePortalUser(
			String corporateindividualfirstname,
			String corporateindividuallastname,
			String corporateindividualfirstemail,
			String corporateindividualsecondemail,
			String corporateindividualthirdemail,
			String corporateindividualfirstmobile,
			String corporateindividualsecondmobile,
			String corporateindividualthirdmobile,
			String corporateindividualAddressLine1,
			String corporateindividualAddressLine2, Timestamp dob,
			CorporateIndividualManagementPortletState portletState, ActionRequest aReq, ActionResponse aRes) {
		// TODO Auto-generated method stub
		PortalUser pu = (PortalUser)portletState.getCorporateIndividualManagementPortletUtil().getEntityObjectById(PortalUser.class, Long.valueOf(portletState.getSelectedPortalUserId()));
		if(pu!=null)
		{
			pu.setAddressLine1(corporateindividualAddressLine1);
			pu.setAddressLine2(corporateindividualAddressLine2);
			pu.setEmailAddress(corporateindividualfirstemail);
			pu.setFirstAlternativeEmailAddress(corporateindividualsecondemail);
			pu.setSecondAlternativeEmailAddress(corporateindividualthirdemail);
			pu.setMobileNumber(corporateindividualfirstmobile);
			pu.setFirstAlternativeMobileNumber(corporateindividualsecondmobile);
			pu.setSecondAlternativeMobileNumber(corporateindividualthirdmobile);
			pu.setFirstName(corporateindividualfirstname);
			pu.setLastName(corporateindividuallastname);
			
			
			log.info("----Portlet state = " + corporateindividualfirstname + " && "  + 
					corporateindividuallastname + " && "  + 
					corporateindividualfirstemail + " && " + 
					corporateindividualsecondemail + " && " + 
					corporateindividualthirdemail + " && " + 
					corporateindividualfirstmobile + " && "  + 
					corporateindividualsecondmobile + " && "  + 
					corporateindividualthirdmobile + " && " + 
					corporateindividualAddressLine1 + " && " + 
					corporateindividualAddressLine2);
		}
		
		long communities[] = new long[1];
		
		communities[0] = ProbaseConstants.CORPORATE_STAFF_COMMUNITY_ID;
		Settings settingEmail = portletState.getNotifyCorporateIndividualEmail();
		Settings settingSms = portletState.getNotifyCorporateIndividualSMS();
		Settings settingSystemUrl = portletState.getSystemUrl();
		
		AuditTrail auditTrail = new AuditTrail();
		auditTrail.setAction("Create Portal User");
		auditTrail.setDate(new Timestamp((new Date()).getTime()));
		auditTrail.setIpAddress(portletState.getRemoteIPAddress());
		auditTrail.setUserId(Long.toString(portletState.getPortalUser().getUserId()));

		handleUpdateUserOrbitaAccount(pu, 
				corporateindividualfirstname, 
				corporateindividualAddressLine1,
				corporateindividualAddressLine2,
				corporateindividualfirstemail,
				corporateindividualsecondemail,
				corporateindividualsecondmobile,
				corporateindividualthirdmobile,
				corporateindividualfirstmobile,
				"",
				corporateindividuallastname,
				corporateindividualthirdemail,
				auditTrail, 
				serviceContext, 
				swpService,
				true,
				settingEmail.equals("1") ? true : false,
				settingSms.equals("1") ? true : false,
				settingSystemUrl.getValue()==null ? portletState.getApplicationName().getValue() : settingSystemUrl.getValue(),
				portletState, 
				aReq, aRes);
		
		return pu;
	}

	private void handleUpdateCompanyStaffStepOne(ActionRequest aReq,
			ActionResponse aRes,
			CorporateIndividualManagementPortletState portletState) {
		// TODO Auto-generated method stub
		if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
		{
			portletState.setSelectedCompanyId(portletState.getPortalUser().getCompany().getId());
			
		}else if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
		{
			portletState.setSelectedCompanyId(portletState.getSelectedCompany().getId());
		}
		portletState.setCorporateindividualfirstname(aReq.getParameter("firstname"));
		portletState.setCorporateindividuallastname(aReq.getParameter("lastname"));
//		portletState.setCorporateindividualfirstmobile(aReq.getParameter("contactMobileNumber"));
//		portletState.setCorporateindividualsecondmobile(aReq.getParameter("contactMobileNumberFirstAlternative"));
//		portletState.setCorporateindividualthirdmobile(aReq.getParameter("contactMobileNumberSecondAlternative"));
		if(aReq.getParameter("contactMobileNumber").length()>0)
			portletState.setCorporateindividualfirstmobile(aReq.getParameter("countryCode1") + new Util().formatMobile(aReq.getParameter("contactMobileNumber")));
		else
			portletState.setCorporateindividualfirstmobile("");
		if(aReq.getParameter("contactMobileNumberFirstAlternative").length()>0)
			portletState.setCorporateindividualsecondmobile(aReq.getParameter("countryCode2") + new Util().formatMobile(aReq.getParameter("contactMobileNumberFirstAlternative")));
		else
			portletState.setCorporateindividualsecondmobile("");
		if(aReq.getParameter("contactMobileNumberSecondAlternative").length()>0)
			portletState.setCorporateindividualthirdmobile(aReq.getParameter("countryCode3") + new Util().formatMobile(aReq.getParameter("contactMobileNumberSecondAlternative")));
		else 
			portletState.setCorporateindividualthirdmobile("");
		portletState.setCorporateindividualfirstemail(aReq.getParameter("contactEmailAddress"));
		portletState.setCorporateindividualsecondemail(aReq.getParameter("contactEmailAddressFirstAlternative"));
		portletState.setCorporateindividualthirdemail(aReq.getParameter("contactEmailAddressSecondAlternative"));
		portletState.setCorporateindividualAddressLine1(aReq.getParameter("contactAddressLine1"));
		portletState.setCorporateindividualAddressLine2(aReq.getParameter("contactAddressLine2"));
		
		
		log.info("Portlet state = " + portletState.getCorporateindividualfirstname() + " && "  + 
				portletState.getCorporateindividuallastname() + " && "  + 
				portletState.getCorporateindividualfirstemail() + " && " + 
				portletState.getCorporateindividualsecondemail() + " && " + 
				portletState.getCorporateindividualthirdemail() + " && " + 
				portletState.getCorporateindividualfirstmobile() + " && "  + 
				portletState.getCorporateindividualsecondmobile() + " && "  + 
				portletState.getCorporateindividualthirdmobile() + " && " + 
				portletState.getCorporateindividualAddressLine1() + " && " + 
				portletState.getCorporateindividualAddressLine2());
		log.info("handleUpdateCompanyStaffStepOne");
		
		if(isCompanyIndividualCreationDataValid(portletState, aReq, aRes, true, portletState.getSelectedPortalUserId()))
		{
			if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR) 
					|| portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
			{
				aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/corporateindividual/register/stepthree_edit.jsp");
			}else
			{
				portletState.addError(aReq, "You do not have the administrative rights to register a company personnel. Contact " +
						"System Administrator for rights", portletState);
				aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/corporateindividual/register/stepone_edit.jsp");
			}
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/corporateindividual/register/stepone_edit.jsp");
		}
	}

	private void loginStepTwo(ActionRequest aReq, ActionResponse aRes,
			CorporateIndividualManagementPortletState portletState) {
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
				cappState.setLoggedIn(Boolean.TRUE);
				PortalUser pu = portletState.getCorporateIndividualManagementPortletUtil().getPortalUserByEmailAddress(email2);
				if(pu!=null && pu.getStatus().equals(PortalUserStatusConstants.PORTAL_USER_STATUS_ACTIVE))
					cappState.setPortalUser(pu);
				else
					cappState.setPortalUser(null);
			}
				
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handleCompanyStaffListingAction(ActionRequest aReq,
			ActionResponse aRes,
			CorporateIndividualManagementPortletState portletState) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		String selectedPortalUser = aReq.getParameter("selectedPortalUser").trim();
		try
		{
			Long puId = Long.valueOf(selectedPortalUser);
			PortalUser pu = (PortalUser)portletState.getCorporateIndividualManagementPortletUtil().getEntityObjectById(PortalUser.class, puId);
			portletState.setSelectedPortalUserId(selectedPortalUser);
			if(pu!=null)
			{
				ComminsApplicationState cappState = portletState.getCas();
				Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
				Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
				DETERMINE_ACCESS determinAccess = new Util().determineAccessForUserFxns(twoStep, approvalProcess, cappState, portletState.getPortalUserCRUDRights());
				
				if(aReq.getParameter("selectedPortalUserAction")!=null && aReq.getParameter("selectedPortalUserAction").equalsIgnoreCase("update"))
				{
					
					if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS) || determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
					{
						portletState.setCorporateindividualfirstname(pu.getFirstName());
						portletState.setCorporateindividuallastname(pu.getLastName());
						if(pu.getMobileNumber().length()>0)
							portletState.setCorporateindividualfirstmobile(pu.getMobileNumber()!=null && pu.getMobileNumber().length()>0 ? pu.getMobileNumber().substring(4,  pu.getFirstAlternativeMobileNumber().length()): "");
						else 
							portletState.setCorporateindividualfirstmobile("");
						if(pu.getFirstAlternativeMobileNumber().length()>0)
							portletState.setCorporateindividualsecondmobile(pu.getFirstAlternativeMobileNumber()!=null && pu.getFirstAlternativeMobileNumber().length()>0 ? pu.getFirstAlternativeMobileNumber().substring(4,  pu.getFirstAlternativeMobileNumber().length()) : "");
						else 
							portletState.setCorporateindividualsecondmobile("");
						if(pu.getSecondAlternativeMobileNumber().length()>0)
							portletState.setCorporateindividualthirdmobile(pu.getSecondAlternativeMobileNumber()!=null && pu.getSecondAlternativeMobileNumber().length()>0 ? pu.getSecondAlternativeMobileNumber().substring(4,  pu.getFirstAlternativeMobileNumber().length()) : "");
						else 
							portletState.setCorporateindividualsecondmobile("");
//						portletState.setCorporateindividualfirstmobile(pu.getMobileNumber());
//						portletState.setCorporateindividualsecondmobile(pu.getFirstAlternativeMobileNumber());
//						portletState.setCorporateindividualthirdmobile(pu.getSecondAlternativeMobileNumber());
						portletState.setCorporateindividualfirstemail(pu.getEmailAddress());
						portletState.setCorporateindividualsecondemail(pu.getFirstAlternativeEmailAddress());
						portletState.setCorporateindividualthirdemail(pu.getSecondAlternativeEmailAddress());
						portletState.setCorporateindividualAddressLine1(pu.getAddressLine1());
						portletState.setCorporateindividualAddressLine2(pu.getAddressLine2());
						aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/corporateindividual/register/stepone_edit.jsp");
					}else
					{
						portletState.addError(aReq, "You do not have appropriate rights to carry out this action. Request for these rights from appropriate administrators", portletState);
						aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/corporateindividual/userlisting.jsp");
					}
					
				}else if(aReq.getParameter("selectedPortalUserAction")!=null && 
						aReq.getParameter("selectedPortalUserAction").equalsIgnoreCase("delete"))
				{
					if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
					{
						try
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
							jsonObject.put("company", pu.getCompany().getId());
							
							aft.setActionType(ActionTypeConstants.PORTAL_USER_ACTION_DELETE);
							aft.setDateCreated(new Timestamp((new Date()).getTime()));
							aft.setEntityId(pu.getId());
							aft.setObjectData(jsonObject.toString());
							aft.setPortalUser(portletState.getPortalUser());
							aft.setWorkerId(null);
							aft.setEntityName(PortalUser.class.getSimpleName());
							swpService.createNewRecord(aft);
							
							Collection<PortalUser> pus = portletState.getCorporateIndividualManagementPortletUtil().getApprovingPortalUsers(
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
												"Request for the Deletion of A Company Staff Profile", portletState.getApplicationName().getValue());
							}
							
							for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
							{
								PortalUser pu1 = it.next();
								String message = "Approval request awaiting your action. " +
										"Visit " + portletState.getSystemUrl().getValue() + 
										" to view requests awaiting your " +
										"approval/disapproval action";
								SendSms sendSms = new SendSms(pu1.getMobileNumber(), message, 
										portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
							}
							
							
								
							portletState.addSuccess(aReq, "Request to delete the selected company staff has been sent to approvers successfully.", portletState);
						}catch(JSONException e)
						{
							e.printStackTrace();
							portletState.addError(aReq, "Error encountered processing this action. Please try again.", portletState);
						}
					}else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
					{
						try {
							UserLocalServiceUtil.updateStatus(
									pu.getUserId(), 1);
							pu.setStatus(PortalUserStatusConstants.PORTAL_USER_DELETED);
							swpService.updateRecord(pu);
							
							
							handleAudit("Portal User Delete", Long.toString(pu.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
							aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/userlisting.jsp");
							portletState.addSuccess(aReq, "Selected company staff - " + pu.getFirstName() + " " + pu.getLastName() + " - has been deleted successfully.", portletState);
							portletState.setAllCompanyPersonnel(portletState.getCorporateIndividualManagementPortletUtil().getAllPortalUserByCompany(portletState.getSelectedCompany()));
							
							Settings settingEmail = portletState.getNotifyCorporateFirmEmail();
							Settings settingSms = portletState.getNotifyCorporateFirmSms();
							Settings settingSystemUrl = portletState.getSystemUrl();
							
							boolean sendEmail = settingEmail.equals("1") ? true : false;
							boolean sendSms = settingSms.equals("1") ? true : false;
							String systemUrl = settingSystemUrl.getValue()==null ? portletState.getApplicationName().getValue() : settingSystemUrl.getValue();
							
							
							emailer.emailChangeOfAccountStatus(pu.getEmailAddress(),
									systemUrl, pu.getFirstName(), pu.getLastName(), 
									portletState.getApplicationName().getValue() + " - User " +
											"profile account deletion", "Your user profile account " +
													"on " + portletState.getApplicationName().getValue() + " system has been deleted. " +
															"You can no longer access your account!", portletState.getApplicationName().getValue());
							
							String message = "Approval request awaiting your action. " +
									"Visit " + portletState.getSystemUrl().getValue() + 
									" to view requests awaiting your " +
									"approval/disapproval action";
							SendSms sendSms1 = new SendSms(pu.getMobileNumber(), message, 
									portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
							
							
							
						} catch (PortalException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							portletState.addError(aReq, "Selected company staff - " + pu.getFirstName() + " " + pu.getLastName() + " - has not been deleted successfully.", portletState);
							aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/userlisting.jsp");
						} catch (SystemException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							portletState.addError(aReq, "Selected company staff - " + pu.getFirstName() + " " + pu.getLastName() + " - has not been deleted successfully.", portletState);
							aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/userlisting.jsp");
						}
					}
					
					
					
				}else if(aReq.getParameter("selectedPortalUserAction")!=null && 
						aReq.getParameter("selectedPortalUserAction").equalsIgnoreCase("suspend"))
				{
					
					if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
					{
						try
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
							jsonObject.put("company", pu.getCompany().getId());
							
							aft.setActionType(ActionTypeConstants.PORTAL_USER_ACTION_BLOCK);
							aft.setDateCreated(new Timestamp((new Date()).getTime()));
							aft.setEntityId(pu.getId());
							aft.setObjectData(jsonObject.toString());
							aft.setPortalUser(portletState.getPortalUser());
							aft.setWorkerId(null);
							aft.setEntityName(PortalUser.class.getSimpleName());
							swpService.createNewRecord(aft);
							
							Collection<PortalUser> pus = portletState.getCorporateIndividualManagementPortletUtil().getApprovingPortalUsers(
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
												"Suspension of A Company Staff Profile", portletState.getApplicationName().getValue());
							}
							
							for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
							{
								PortalUser pu1 = it.next();
								String message = "Approval request awaiting your action. " +
										"Visit " + portletState.getSystemUrl().getValue() + 
										" to view requests awaiting your " +
										"approval/disapproval action";
								SendSms sendSms = new SendSms(pu1.getMobileNumber(), message, 
										portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
							}
							
							
							portletState.addSuccess(aReq, "Request to block the selected company staff has been sent to approvers successfully.", portletState);
								
						}catch(JSONException e)
						{
							e.printStackTrace();
							portletState.addError(aReq, "Error encountered processing this action. Please try again.", portletState);
						}
					}else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
					{
						try {
							UserLocalServiceUtil.updateStatus(
									pu.getUserId(), 1);
							pu.setStatus(PortalUserStatusConstants.PORTAL_USER_STATUS_SUSPENDED);
							swpService.updateRecord(pu);
							handleAudit("Portal User Suspend", Long.toString(pu.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
							aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/userlisting.jsp");
							portletState.addSuccess(aReq, "Selected company staff - " + pu.getFirstName() + " " + pu.getLastName() + " - has been blocked.", portletState);
							portletState.setAllCompanyPersonnel(portletState.getCorporateIndividualManagementPortletUtil().getAllPortalUserByCompany(portletState.getSelectedCompany()));
							
							Settings settingEmail = portletState.getNotifyCorporateFirmEmail();
							Settings settingSms = portletState.getNotifyCorporateFirmSms();
							Settings settingSystemUrl = portletState.getSystemUrl();
							
							boolean sendEmail = settingEmail.equals("1") ? true : false;
							boolean sendSms = settingSms.equals("1") ? true : false;
							String systemUrl = settingSystemUrl.getValue()==null ? portletState.getApplicationName().getValue() : settingSystemUrl.getValue();
							emailer.emailChangeOfAccountStatus(pu.getEmailAddress(),
									systemUrl, pu.getFirstName(), pu.getLastName(), 
									portletState.getApplicationName().getValue() + " - User profile account suspension", 
									"Your user profile account on " + portletState.getApplicationName().getValue() + " " +
											"system has been suspended. You can no longer access your profile " +
											"account until your profile account is reactivate!", portletState.getApplicationName().getValue());
							
								String message = "Your user profile account on " + portletState.getApplicationName().getValue() + " system has been suspended. You can not longer access your account until your profile account is reactivate";
								SendSms sendSms1 = new SendSms(pu.getMobileNumber(), message, 
										portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
							
						} catch (PortalException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							portletState.addError(aReq, "Selected company staff - " + pu.getFirstName() + " " + pu.getLastName() + " - has not been blocked successfully.", portletState);
							aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/userlisting.jsp");
						} catch (SystemException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							portletState.addError(aReq, "Selected company staff - " + pu.getFirstName() + " " + pu.getLastName() + " - has not been blocked successfully.", portletState);
							aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/userlisting.jsp");
						}
					}
					
					
					
				}else if(aReq.getParameter("selectedPortalUserAction")!=null && 
						aReq.getParameter("selectedPortalUserAction").equalsIgnoreCase("reactivate"))
				{
					if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
					{
						try
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
							jsonObject.put("company", pu.getCompany().getId());
							
							aft.setActionType(ActionTypeConstants.PORTAL_USER_ACTION_UNBLOCK);
							aft.setDateCreated(new Timestamp((new Date()).getTime()));
							aft.setEntityId(pu.getId());
							aft.setObjectData(jsonObject.toString());
							aft.setPortalUser(portletState.getPortalUser());
							aft.setWorkerId(null);
							aft.setEntityName(PortalUser.class.getSimpleName());
							swpService.createNewRecord(aft);
							
							Collection<PortalUser> pus = portletState.getCorporateIndividualManagementPortletUtil().getApprovingPortalUsers(
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
												"Reactivation of A Company Staff Profile", portletState.getApplicationName().getValue());
							}
							
							for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
							{
								PortalUser pu1 = it.next();
								String message = "Approval request awaiting your action. " +
										"Visit " + portletState.getSystemUrl().getValue() + 
										" to view requests awaiting your " +
										"approval/disapproval action";
								SendSms sendSms = new SendSms(pu1.getMobileNumber(), message, 
										portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
							}
							
							
							portletState.addSuccess(aReq, "Request to reactivate the selected company staff has been sent to approvers successfully.", portletState);
							aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/userlisting.jsp");
								
						}catch(JSONException e)
						{
							e.printStackTrace();
							portletState.addError(aReq, "Error encountered processing this action. Please try again.", portletState);
						}
					}else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
					{
						try {
							UserLocalServiceUtil.updateStatus(
									pu.getUserId(), 0);
							pu.setStatus(PortalUserStatusConstants.PORTAL_USER_STATUS_ACTIVE);
							swpService.updateRecord(pu);
							handleAudit("Company Reactivation", Long.toString(pu.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
							aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/userlisting.jsp");
							portletState.addSuccess(aReq, "Selected company staff - " + pu.getFirstName() + " " + pu.getLastName() + " - has been reactivated.", portletState);
							portletState.setAllCompanyPersonnel(portletState.getCorporateIndividualManagementPortletUtil().getAllPortalUserByCompany(portletState.getSelectedCompany()));
							
							Settings settingEmail = portletState.getNotifyCorporateFirmEmail();
							Settings settingSms = portletState.getNotifyCorporateFirmSms();
							Settings settingSystemUrl = portletState.getSystemUrl();
							
							boolean sendEmail = settingEmail.equals("1") ? true : false;
							boolean sendSms = settingSms.equals("1") ? true : false;
							String systemUrl = settingSystemUrl.getValue()==null ? portletState.getApplicationName().getValue() : settingSystemUrl.getValue();
							emailer.emailChangeOfAccountStatus(pu.getEmailAddress(),
									systemUrl, pu.getFirstName(), pu.getLastName(), 
									portletState.getApplicationName().getValue() + " - User profile account reactivation", 
									"Your user profile account on " + portletState.getApplicationName().getValue() + " " +
											"system has been reactivated. You can now access your profile account!", portletState.getApplicationName().getValue());
							
								String message = "Your user profile account on our platform has been reactivated. You can now access your profile account";
								SendSms sendSms1 = new SendSms(pu.getMobileNumber(), message, 
										portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
							
									
						} catch (PortalException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							portletState.addError(aReq, "Selected company staff - " + pu.getFirstName() + " " + pu.getLastName() + " - has not been reactivated successfully.", portletState);
							aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/userlisting.jsp");
						} catch (SystemException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							portletState.addError(aReq, "Selected company staff - " + pu.getFirstName() + " " + pu.getLastName() + " - has not been reactivated successfully.", portletState);
							aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/userlisting.jsp");
						}
					}
					
					
				}else if(aReq.getParameter("selectedPortalUserAction")!=null && aReq.getParameter("selectedPortalUserAction").equalsIgnoreCase("approveActivate"))
				{
					//handleReactivate(pu, portletState, aRes, aReq);
				}
			}else
			{
				portletState.addError(aReq, "This action can not be carried out on the selected company staff. You seem to have not selected a valid company staff. Select one before proceeding", portletState);
				aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/userlisting.jsp");
			}
		}catch(NumberFormatException e)
		{
			e.printStackTrace();
			portletState.addError(aReq, "This action can not be carried out on the selected company staff. Select a valid company staff before proceeding", portletState);
			aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/userlisting.jsp");
		}
	}

	private void handleReactivate(PortalUser pu, CorporateIndividualManagementPortletState portletState, ActionResponse aRes, ActionRequest aReq) {
		// TODO Auto-generated method stub
		try {
			UserLocalServiceUtil.updateStatus(
					pu.getUserId(), 0);
			pu.setStatus(PortalUserStatusConstants.PORTAL_USER_STATUS_ACTIVE);
			swpService.updateRecord(pu);
			handleAudit("Portal User Reactivation", Long.toString(pu.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
			aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/userlisting.jsp");

			Settings settingEmail = portletState.getNotifyCorporateFirmEmail();
			Settings settingSms = portletState.getNotifyCorporateFirmSms();
			Settings settingSystemUrl = portletState.getSystemUrl();
			
			boolean sendEmail = settingEmail.equals("1") ? true : false;
			boolean sendSms = settingSms.equals("1") ? true : false;
			String systemUrl = settingSystemUrl.getValue()==null ? portletState.getApplicationName().getValue() : settingSystemUrl.getValue();
			
			sendNotificationOnCompanyStaffCreation(sendEmail, sendSms, pu, 
					systemUrl);
			portletState.addSuccess(aReq, "Selected company staff - " + pu.getFirstName() + " " + pu.getLastName() + " - has been activated.", portletState);
			portletState.setAllCompanyPersonnel(portletState.getCorporateIndividualManagementPortletUtil().getAllPortalUserByCompany(portletState.getSelectedCompany()));
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			portletState.addError(aReq, "Selected company staff - " + pu.getFirstName() + " " + pu.getLastName() + " - has not been activated successfully.", portletState);
			aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/userlisting.jsp");
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			portletState.addError(aReq, "Selected company staff - " + pu.getFirstName() + " " + pu.getLastName() + " - has not been activated successfully.", portletState);
			aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/userlisting.jsp");
		}
	}

	private void handleUpdateUser(ActionRequest aReq,
			ActionResponse aRes,
			CorporateIndividualManagementPortletState portletState, Timestamp dob) {
		// TODO Auto-generated method stub
		log.info("Portlet state = " + portletState.getCorporateindividualfirstname() + " && "  + 
				portletState.getCorporateindividuallastname() + " && "  + 
				portletState.getCorporateindividualfirstemail() + " && " + 
				portletState.getCorporateindividualsecondemail() + " && " + 
				portletState.getCorporateindividualthirdemail() + " && " + 
				portletState.getCorporateindividualfirstmobile() + " && "  + 
				portletState.getCorporateindividualsecondmobile() + " && "  + 
				portletState.getCorporateindividualthirdmobile() + " && " + 
				portletState.getCorporateindividualAddressLine1() + " && " + 
				portletState.getCorporateindividualAddressLine2());
		PortalUser pu = handleUpdatePortalUser(portletState.getCorporateindividualfirstname(), 
				portletState.getCorporateindividuallastname(), 
				portletState.getCorporateindividualfirstemail(),
				portletState.getCorporateindividualsecondemail(),
				portletState.getCorporateindividualthirdemail(),
				portletState.getCorporateindividualfirstmobile(), 
				portletState.getCorporateindividualsecondmobile(), 
				portletState.getCorporateindividualthirdmobile(),
				portletState.getCorporateindividualAddressLine1(),
				portletState.getCorporateindividualAddressLine2(),
				dob, portletState, aReq, aRes);
		if(pu!=null)
		{
			if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
			{
				portletState.addSuccess(aReq, "Account updated successfully", portletState);
			}else
			{
				portletState.addSuccess(aReq, "Account updated successfully. Appropriate email & SMS notifications have been sent to the accounts owner", portletState);
			}
    		aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/userlisting.jsp");
			portletState.setAllCompanyPersonnel(portletState.getCorporateIndividualManagementPortletUtil().getAllPortalUserByCompany(portletState.getSelectedCompany()));
			portletState.reinitializeForCreateCorporateIndividual(portletState);
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/corporateindividual/register/stepone_edit.jsp");
			portletState.reinitializeForCreateCorporateIndividual(portletState);
		}
		
	}

	private void sendNotificationOnCompanyStaffCreation(boolean sendEmail,
			boolean sendSms, PortalUser pu, String systemUrl) {
		// TODO Auto-generated method stub
		
	}

	private void sendNotificationOnCompanytSaffReactivation(boolean sendEmail,
			boolean sendSms, PortalUser pu, String systemUrl) {
		// TODO Auto-generated method stub
		
	}

	private void sendNotificationOnCompanyStaffSuspension(boolean sendEmail,
			boolean sendSms, PortalUser pu, String systemUrl) {
		// TODO Auto-generated method stub
		
	}

	private void sendNotificationOnCompanyStaffDelete(boolean sendEmail,
			boolean sendSms, PortalUser pu, String systemUrl) {
		// TODO Auto-generated method stub
		
	}

	private void addCompanyStaffPreStepOne(ActionRequest aReq,
			ActionResponse aRes, CorporateIndividualManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String companySelected = aReq.getParameter("companySelected");
		if(companySelected!=null && !companySelected.equals("-1"))
		{
			Company company = (Company)portletState.getCorporateIndividualManagementPortletUtil().
					getEntityObjectById(Company.class, Long.valueOf(companySelected));
			if(company!=null)
			{
				portletState.setSelectedCompany(company);
				aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/corporateindividual/register/stepone.jsp");
			}else
			{
				portletState.setSelectedCompany(null);
				portletState.addError(aReq, "Select a company before proceeding", portletState);
			}
			
		}else
		{
			portletState.setSelectedCompany(null);
			portletState.addError(aReq, "Select a company before proceeding", portletState);
		}
		
	}
	
	private void viewCompanyStaffPreStepOne(ActionRequest aReq,
			ActionResponse aRes, CorporateIndividualManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String companySelected = aReq.getParameter("companySelected");
		portletState.setAllCompanyPersonnel(null);
		if(companySelected!=null && !companySelected.equals("-1"))
		{
			Company company = (Company)portletState.getCorporateIndividualManagementPortletUtil().
					getEntityObjectById(Company.class, Long.valueOf(companySelected));
			if(company!=null)
			{
				portletState.setSelectedCompany(company);
				portletState.setAllCompanyPersonnel(portletState.getCorporateIndividualManagementPortletUtil().
						getAllPortalUserByCompany(company));
				aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/userlisting.jsp");
			}else
			{
				portletState.setSelectedCompany(null);
				portletState.addError(aReq, "Select a company before proceeding", portletState);
			}
			
		}else
		{
			portletState.setSelectedCompany(null);
			portletState.addError(aReq, "Select a company before proceeding", portletState);
		}
		
	}
	private void createAnCompanyIndividualStepOne(ActionRequest aReq,
			ActionResponse aRes, CorporateIndividualManagementPortletState portletState) {
		// TODO Auto-generated method stub
		if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
		{
			portletState.setSelectedCompanyId(portletState.getPortalUser().getCompany().getId());
		}else if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
		{
			portletState.setSelectedCompanyId(portletState.getSelectedCompany().getId());
			portletState.setSelectedUserRoleId(aReq.getParameter("roletype"));
		}
		portletState.setCorporateindividualfirstname(aReq.getParameter("firstname"));
		portletState.setCorporateindividuallastname(aReq.getParameter("lastname"));
		portletState.setCountryCodeAlt1(aReq.getParameter("countryCode1"));
		portletState.setCountryCodeAlt2(aReq.getParameter("countryCode2"));
		portletState.setCountryCodeAlt3(aReq.getParameter("countryCode3"));
		if(aReq.getParameter("contactMobileNumber").length()>0)
		{
			portletState.setCorporateindividualfirstmobile(aReq.getParameter("countryCode1") + new Util().formatMobile(aReq.getParameter("contactMobileNumber")));
		}
		else 
		{
			portletState.setCorporateindividualfirstmobile("");
		}
		
		if(aReq.getParameter("contactMobileNumberFirstAlternative").length()>0)
		{
			portletState.setCorporateindividualsecondmobile(aReq.getParameter("countryCode2") + new Util().formatMobile(aReq.getParameter("contactMobileNumberFirstAlternative")));
		}
		else 
		{
			portletState.setCorporateindividualsecondmobile("");
		}
		
		if(aReq.getParameter("contactMobileNumberSecondAlternative").length()>0)
		{
			portletState.setCorporateindividualthirdmobile(aReq.getParameter("countryCode3") + new Util().formatMobile(aReq.getParameter("contactMobileNumberSecondAlternative")));
		}
		else
		{
			portletState.setCorporateindividualthirdmobile("");
		}
//		portletState.setCorporateindividualfirstmobile(aReq.getParameter("contactMobileNumber"));
//		portletState.setCorporateindividualsecondmobile(aReq.getParameter("contactMobileNumberFirstAlternative"));
//		portletState.setCorporateindividualthirdmobile(aReq.getParameter("contactMobileNumberSecondAlternative"));
		portletState.setCorporateindividualfirstemail(aReq.getParameter("contactEmailAddress"));
		portletState.setCorporateindividualsecondemail(aReq.getParameter("contactEmailAddressFirstAlternative"));
		portletState.setCorporateindividualthirdemail(aReq.getParameter("contactEmailAddressSecondAlternative"));
		portletState.setCorporateindividualAddressLine1(aReq.getParameter("contactAddressLine1"));
		portletState.setCorporateindividualAddressLine2(aReq.getParameter("contactAddressLine2"));
		
		
		if(isCompanyIndividualCreationDataValid(portletState, aReq, aRes, false, null))
		{
			if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR) 
					|| portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
			{
				aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/corporateindividual/register/stepthree.jsp");
			}else
			{
				portletState.addError(aReq, "You do not have the administrative rights to register a company personnel. Contact " +
						"System Administrator for rights", portletState);
				aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/corporateindividual/register/stepone.jsp");
			}
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/corporateindividual/register/stepone.jsp");
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
	
	
	private void createAnCompanyIndividualStepThree(ActionRequest aReq,
			ActionResponse aRes, CorporateIndividualManagementPortletState portletState) {
		// TODO Auto-generated method stub
		ComminsApplicationState cappState = portletState.getCas();
		Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
		Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
		DETERMINE_ACCESS determinAccess = new Util().determineAccessForUserFxns(twoStep, approvalProcess, cappState, portletState.getPortalUserCRUDRights());
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		
		
		
		if(isCompanyIndividualCreationDataValid(portletState, aReq, aRes, false, null))
		{
			if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR)
					|| portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
			{
				Calendar c1 = GregorianCalendar.getInstance();
				c1.set(1980, Calendar.JANUARY, 1);
				Date date =c1.getTime();
				Timestamp dob = new Timestamp(date.getTime());
				RoleType roleType = null;
				
				if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
				{
					roleType = (RoleType)portletState.getCorporateIndividualManagementPortletUtil().getEntityObjectById(RoleType.class, Long.valueOf(portletState.getSelectedUserRoleId()));
				}else
				{
					roleType = portletState.getCorporateIndividualManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_TYPE_COMPANY_NON_ADMINISTRATOR_PERSONNEL);
					
				}
				
				
				
				
				PortalUserStatusConstants pusc = PortalUserStatusConstants.PORTAL_USER_STATUS_ACTIVE;
				if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
				{
					if(portletState.getPortalUserCRUDRights()!=null && 
							portletState.getPortalUserCRUDRights().getCudInitiatorRights()!=null && 
								portletState.getPortalUserCRUDRights().getCudInitiatorRights().equals(Boolean.TRUE))
					{
						pusc = PortalUserStatusConstants.PORTAL_USER_INACTIVE;
					}
				}
				
				Company comp = null;
				if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
					comp = portletState.getPortalUser().getCompany();
				else 
					comp = portletState.getSelectedCompany(); 
					
				
				if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
				{
					JSONObject jsonObject = new JSONObject();
					try {
						jsonObject.put("firstName", portletState.getCorporateindividualfirstname());
						jsonObject.put("lastName", portletState.getCorporateindividuallastname());
						jsonObject.put("email", portletState.getCorporateindividualfirstemail());
						jsonObject.put("alternativeEmail1", portletState.getCorporateindividualsecondemail());
						jsonObject.put("alternativeEmail2", portletState.getCorporateindividualthirdemail());
						jsonObject.put("mobile", portletState.getCorporateindividualfirstmobile());
						jsonObject.put("alternativeMobile1", portletState.getCorporateindividualfirstmobile());
						jsonObject.put("alternativeMobile2", portletState.getCorporateindividualthirdmobile());
						jsonObject.put("addressLine1", portletState.getCorporateindividualAddressLine1());
						jsonObject.put("addressLine2", portletState.getCorporateindividualAddressLine2());
						jsonObject.put("dob", dob);
						jsonObject.put("roleType", roleType.getRoleTypeName().getValue());
						jsonObject.put("company", comp.getId());
						
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
						Collection<PortalUser> pus = portletState.getCorporateIndividualManagementPortletUtil().getApprovingPortalUsers(
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
											portletState.getApplicationName().getValue() + " - Approval " +
													"Request for the Suspension of A Company Staff Profile", portletState.getApplicationName().getValue());
								
							}
							
							if(settingSms.getValue().equals("1"))
							{
								try{
								//new SendSms(createdUser.getMobileNumber(), message, "C_Portal");

										String message = "Approval request awaiting your action. " +
												"Visit " + portletState.getSystemUrl().getValue() + 
												" to view requests awaiting your " +
												"approval/disapproval action";
										SendSms sendSms = new SendSms(pu1.getMobileNumber(), message, 
												portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
									
								}catch(Exception e){
									log.error("error sending sms ",e);
								}
							}
							
						}
						
						
						portletState.setCorporateindividualfirstname("");
						portletState.setCorporateindividuallastname("");
						portletState.setCorporateindividualfirstemail("");
						portletState.setCorporateindividualsecondemail("");
						portletState.setCorporateindividualthirdemail("");
						portletState.setCorporateindividualfirstmobile("");
						portletState.setCorporateindividualfirstmobile("");
						portletState.setCorporateindividualthirdmobile("");
						portletState.setCorporateindividualAddressLine1("");
						portletState.setCorporateindividualAddressLine2("");

						portletState.addSuccess(aReq, "A request has been successfully logged requiring an approval officer to approve the creation " +
								"of this user profile.", portletState);
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						portletState.addError(aReq, "Problems were encountered creating this user profile. Please try again" +
								"System Administrator for rights", portletState);
						aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/register/stepone.jsp");
					}
				}else
				{
					if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
					{
						JSONObject jsonObject = new JSONObject();
						try {
							jsonObject.put("firstName", portletState.getCorporateindividualfirstname());
							jsonObject.put("lastName", portletState.getCorporateindividuallastname());
							jsonObject.put("email", portletState.getCorporateindividualfirstemail());
							jsonObject.put("alternativeEmail1", portletState.getCorporateindividualsecondemail());
							jsonObject.put("alternativeEmail2", portletState.getCorporateindividualthirdemail());
							jsonObject.put("mobile", portletState.getCorporateindividualfirstmobile());
							jsonObject.put("alternativeMobile1", portletState.getCorporateindividualfirstmobile());
							jsonObject.put("alternativeMobile2", portletState.getCorporateindividualthirdmobile());
							jsonObject.put("addressLine1", portletState.getCorporateindividualAddressLine1());
							jsonObject.put("addressLine2", portletState.getCorporateindividualAddressLine2());
							jsonObject.put("dob", dob);
							jsonObject.put("roleType", roleType.getRoleTypeName().getValue());
							jsonObject.put("company", comp.getId());
							
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
							Collection<PortalUser> pus = portletState.getCorporateIndividualManagementPortletUtil().getApprovingPortalUsers(
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
										portletState.getApplicationName().getValue() + " - Approval Request " +
												"for the Suspension of A Company Staff Profile", portletState.getApplicationName().getValue());
									
								}
								
								if(settingSms.getValue().equals("1"))
								{
									try{
									//new SendSms(createdUser.getMobileNumber(), message, "C_Portal");

											String message = "Approval request awaiting your action. " +
													"Visit " + portletState.getSystemUrl().getValue() + 
													" to view requests awaiting your " +
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
									"System Administrator for rights", portletState);
							aRes.setRenderParameter("jspPage", "/html/usermanagementsystemadminportlet/register/stepone.jsp");
						}
					}else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
					{
						PortalUser pu = handleCreateNewPortalUser(portletState.getCorporateindividualfirstname(), 
								portletState.getCorporateindividuallastname(), 
								portletState.getCorporateindividualfirstemail(),
								portletState.getCorporateindividualsecondemail(),
								portletState.getCorporateindividualthirdemail(),
								portletState.getCorporateindividualfirstmobile(), 
								portletState.getCorporateindividualsecondmobile(), 
								portletState.getCorporateindividualthirdmobile(),
								portletState.getCorporateindividualAddressLine1(),
								portletState.getCorporateindividualAddressLine2(),
								dob,
								roleType,
								pusc,
								comp, portletState);
						if(pu!=null)
						{
							if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
							{
								portletState.addSuccess(aReq, "Account created successfully. Activation of account will be required by an Approval officer", portletState);
							}else
							{
								portletState.addSuccess(aReq, "Account created successfully. Appropriate email & SMS notifications have been sent to the accounts owner", portletState);
							}
							aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/userlisting.jsp");
							portletState.setAllCompanyPersonnel(portletState.getCorporateIndividualManagementPortletUtil().getAllPortalUserByCompany(portletState.getSelectedCompany()));
							portletState.reinitializeForCreateCorporateIndividual(portletState);
						}else
						{
							aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/corporateindividual/register/stepone.jsp");
							portletState.reinitializeForCreateCorporateIndividual(portletState);
						}
					}
				}
					
			}else
			{
				portletState.addError(aReq, "You do not have appropriate access rights to this module", portletState);
				portletState.reinitializeForCreateCorporateIndividual(portletState);
			}
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/corporateindividual/register/stepone.jsp");
		}
	}

	private PortalUser handleCreateNewPortalUser(
			String corporateindividualfirstname,
			String corporateindividuallastname,
			String corporateindividualfirstemail,
			String corporateindividualsecondemail,
			String corporateindividualthirdemail,
			String corporateindividualfirstmobile,
			String corporateindividualsecondmobile,
			String corporateindividualthirdmobile,
			String corporateindividualAddressLine1,
			String corporateindividualAddressLine2,
			Timestamp dob,
			RoleType roleType,
			PortalUserStatusConstants pusc, Company company, CorporateIndividualManagementPortletState portletState) {
		// TODO Auto-generated method stub
		PortalUser pu = new PortalUser();
		pu.setAddressLine1(portletState.getCorporateindividualAddressLine1());
		pu.setAddressLine2(portletState.getCorporateindividualAddressLine2());
		pu.setCompany(company);
		pu.setDateOfBirth(dob);
		pu.setEmailAddress(portletState.getCorporateindividualfirstemail());
		pu.setFirstAlternativeEmailAddress(portletState.getCorporateindividualsecondemail());
		pu.setSecondAlternativeEmailAddress(portletState.getCorporateindividualthirdemail());
		pu.setMobileNumber(portletState.getCorporateindividualfirstmobile());
		pu.setFirstAlternativeMobileNumber(portletState.getCorporateindividualsecondmobile());
		pu.setSecondAlternativeMobileNumber(portletState.getCorporateindividualthirdmobile());
		pu.setFirstName(portletState.getCorporateindividualfirstname());
		pu.setLastName(portletState.getCorporateindividuallastname());
		pu.setStatus(pusc);
		pu.setRoleType(roleType);
		pu.setDisableEmailNotification(Boolean.FALSE);
		pu.setDisableSMSNotification(Boolean.FALSE);
		long communities[] = new long[1];
		
		communities[0] = ProbaseConstants.CORPORATE_STAFF_COMMUNITY_ID;
		Settings settingEmail = portletState.getNotifyCorporateIndividualEmail();
		Settings settingSms = portletState.getNotifyCorporateIndividualSMS();
		Settings settingSystemUrl = portletState.getSystemUrl();
		
		AuditTrail auditTrail = new AuditTrail();
		auditTrail.setAction("Create Portal User");
		auditTrail.setDate(new Timestamp((new Date()).getTime()));
		auditTrail.setIpAddress(portletState.getRemoteIPAddress());
		auditTrail.setUserId(Long.toString(portletState.getPortalUser().getUserId()));

		handleCreateUserOrbitaAccount(pu, 
				portletState.getCorporateindividualfirstname(), 
				portletState.getCorporateindividualmiddlename(),
				portletState.getCorporateindividuallastname(),
				portletState.getCorporateindividualfirstemail(),
				communities,
				auditTrail, 
				serviceContext, 
				swpService,
				portletState.getPortalUser().getUserId(),
				true,
				pusc.equals(PortalUserStatusConstants.PORTAL_USER_STATUS_ACTIVE) ? true : false, 
				settingEmail.equals("1") ? true : false,
				settingSms.equals("1") ? true : false,
				settingSystemUrl.getValue()==null ? portletState.getApplicationName().getValue() : settingSystemUrl.getValue(),
				swpService, portletState);
		return pu;
	}
	
	
	
	
	public static User handleUpdateUserOrbitaAccount(PortalUser user, String firstname, 
			String addressLine1, String addressLine2, 
			String firstAlternativeEmailAddress, String secondAlternativeEmailAddress, 
			String firstAlternativeMobileNumber, String secondAlternativeMobileNumber, 
			String mobileNumber, String middlename, String surname, String email, 
			AuditTrail auditTrail, ServiceContext serviceContext, SwpService sService,
			boolean passwordReset, boolean sendEmail, boolean sendSms, String systemUrl, 
			CorporateIndividualManagementPortletState portletState, ActionRequest aReq, ActionResponse aRes) {			
		Logger log = Logger.getLogger(CorporateIndividualManagementPortletState.class);
		log.info("Update Orbita User Account");
		Logger log1 = Logger.getLogger(CorporateIndividualManagementPortletState.class);
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
					emailer.emailUpdateUserProfileAccount(
							email, 
							user.getFirstName(), 
							user.getLastName(), 
							portletState.getApplicationName().getValue() + " - User Profile Update", 
							systemUrl, portletState.getApplicationName().getValue());
					
						String message = "Your User profile has been updated successfully";
						new SendSms(user.getMobileNumber(), message, 
								portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
						
//					updatedUser = UserLocalServiceUtil.updateUser(existingUser.getUserId(), 
//							lpUser.getPassword(), lpUser.getPassword(), lpUser.getPassword(), false, 
//							lpUser.getReminderQueryQuestion(), lpUser.getReminderQueryAnswer(), 
//							screenName, email, 0, "", Locale.US.toString(), "", "Welcome, " + firstname + "!", "", 
//							firstname, middlename, surname, 
//							prefixId, suffixId, male, birthdayMonth, birthdayDay, birthdayYear, 
//							"", "", "", "", "", "", "", "", "", "", lpUser.getJobTitle(), lpUser.getGroupIds(), 
//							lpUser.getOrganizationIds(), lpUser.getRoleIds(), null, lpUser.getUserGroupIds(), 
//							serviceContext);

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
				
				emailer.emailUpdateUserProfileAccount(
						email, 
						user.getFirstName(), 
						user.getLastName(), 
						portletState.getApplicationName().getValue() + " - User Profile Update", 
						systemUrl, portletState.getApplicationName().getValue());
				
					String message = "Your User profile has been updated successfully";
					new SendSms(user.getMobileNumber(), message, 
							portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());

					

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
	
	
	
	public static PortalUser handleCreateUserOrbitaAccount(PortalUser user, String firstname, String middlename, String surname, String email, 
			long[] communities, AuditTrail auditTrail, ServiceContext serviceContext, SwpService sService, long loggedInUserId,
			boolean passwordReset, boolean active, boolean sendEmail, boolean sendSms, String systemUrl, SwpService swpService, 
			CorporateIndividualManagementPortletState portletState) {			
		
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		Logger log1 = Logger.getLogger(CorporateIndividualManagementPortlet.class);
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
								throw new HibernateException(e);
							}
						}
						}

					} catch (DuplicateUserScreenNameException t) {
						log1.error("DuplicateScreenNameException");
						return null ;
					} catch (Exception e) {
						log1.error("", e);
						throw new HibernateException(e);
					}

					System.out.println("Added succcessful");

					

					System.out.println("Setting Orbita StaffId");
					createdUser.setUserId(newlyCreatedUser.getUserId());
				}

			} catch (Exception e) { throw new HibernateException(e); }

			try {
				
				

				createdUser = (PortalUser)sService.createNewRecord(createdUser);
//				auditTrail.setActivity("Create Portal User " + createdUser.getId());
//				sService.createNewRecord(auditTrail);
				String companyType = createdUser.getCompany().
							getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY) ? "Corporate Staff Profile" : "Retail Staff Profile";
				
				
				emailer.emailNewCorporateIndividualAccount(
						email, 
						user.getCompany().getCompanyName(), 
						password1, 
						systemUrl, 
						user.getFirstName(), 
						user.getLastName(), 
						user.getRoleType().getRoleTypeName().getValue().replace("_", " "),
						portletState.getApplicationName().getValue() + " New " + companyType + " Creation for " +
								"" + createdUser.getCompany().getCompanyName(), 
								portletState.getApplicationName().getValue());
				
					String message = "A " + portletState.getApplicationName().getValue() + " " + companyType + " account has been created for your company - " +
							"" + createdUser.getCompany().getCompanyName() +
							". Visit " + systemUrl + 
							" to view requests awaiting your " +
							"approval/disapproval action";
					new SendSms(createdUser.getMobileNumber(), message, 
							portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
				
				
				
				
			} catch (Exception e) { log1.error("", e); }

						
			long staffId = createdUser.getId();
			

		}

		return createdUser;		
	}
	
	
	
	
	
	
	public static boolean addUserToCommmunity(long userId, long communityId) {
		boolean status = false;
		try {
			Logger logger = Logger.getLogger(CorporateIndividualManagementPortlet.class);
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
			CorporateIndividualManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String action = aReq.getParameter("actionUrl");
		if(action.equalsIgnoreCase("createcorporateindividual"))
		{
			portletState.reinitializeForCreateCorporateIndividual(portletState);
			aRes.setRenderParameter("jspPage", "/html/corporateindividualmanagementportlet/corporateindividual/register/stepone.jsp");
			portletState.setCurrentTab(VIEW_TABS.CREATE_A_CORPORATE_INDIVIDUAL);
		}
	}

	private void reinitializeForCreateCorporat1eIndividual(
			CorporateIndividualManagementPortletState portletState) {
		// TODO Auto-generated method stub
		
	}

	

	
	
	private boolean isCompanyIndividualCreationDataValid(CorporateIndividualManagementPortletState portletState, 
			ActionRequest aReq, ActionResponse aRes, boolean editYes, String userId)
	{
		// TODO Auto-generated method stub
		String errorMessage = null;
		log.info("isCompanyIndividualCreationDataValid" + editYes);
		if(portletState.getSelectedCompanyId()!=null)
		{
			if(portletState.getCorporateindividualfirstname()!=null && portletState.getCorporateindividualfirstname().trim().length()>0)
			{
				if(portletState.getCorporateindividuallastname()!=null && portletState.getCorporateindividuallastname().trim().length()>0)
				{
					if(portletState.getCorporateindividualfirstemail()!=null && portletState.getCorporateindividualfirstemail().trim().length()>0)
					{
						if(portletState.getCorporateindividualfirstmobile()!=null && portletState.getCorporateindividualfirstmobile().trim().length()>0)
						{	
							PortalUser pu = null;
							if(editYes)
							{
								
									
								if(editYes)
								{
									try
									{
										Long puId = Long.valueOf(portletState.getSelectedPortalUserId());
										log.info("puId = " + puId);
										pu = portletState.getCorporateIndividualManagementPortletUtil().getPortalUserByEmailAddressForEdit(portletState.getCorporateindividualfirstemail(),puId);
										
										if(pu!=null)
										{
											errorMessage =  "The email address provided has already been used on this platform. Provide another email address.";
											
										}else
										{
											
										}
									}
									catch(NumberFormatException e)
									{
										e.printStackTrace();
										errorMessage =  "Invalid Company Staff selected. Select a valid company staff before proceeding.";
									}
								}
								
								
								if(editYes==false)
								{
									
									if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
									{
										if(portletState.getSelectedUserRoleId()!=null && portletState.getSelectedUserRoleId().equals("-1"))
										{
											errorMessage =  "Invalid Role selected for the staff. Select a valid role before proceeding.";
										}else
										{
											try
											{
												Long id = Long.valueOf(portletState.getSelectedUserRoleId());
											}catch(NumberFormatException e)
											{
												errorMessage =  "Invalid Role selected for the staff. Select a valid role before proceeding.";
											}
										}
									}
								}
								
								
							}
							else
							{
								pu = portletState.getCorporateIndividualManagementPortletUtil().getPortalUserByEmailAddress(portletState.getCorporateindividualfirstemail());
								if(pu!=null)
								{
									errorMessage =  "The email address provided has already been used on this platform. Provide another email address.";
									
								}else
								{
									
								}
							}
						}else
						{
							errorMessage =  "Provide personnels primary mobile number in the primary contact mobile number field";
						}
							
					}else
					{
						errorMessage =  "Provide personnels primary email address in the primary contact email address field";
					}
				}else
				{
					errorMessage =  "Provide the personnels last name in the last name field";
				}
			}else
			{
				errorMessage =  "Provide the personnels first name in the first name field";
			}
		}else
		{
			errorMessage =  "Select the company before proceeding";
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
	
	
}
