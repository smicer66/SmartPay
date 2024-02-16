package com.probase.smartpay.admin.financialamountrestrictionmanagement;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
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
import org.hibernate.HibernateException;

import smartpay.audittrail.AuditTrail;
import smartpay.entity.AuthorizePanel;
import smartpay.entity.AuthorizePanelCombination;
import smartpay.entity.BankBranches;
import smartpay.entity.Company;
import smartpay.entity.FeeDescription;
import smartpay.entity.FinancialAmountRestriction;
import smartpay.entity.PortalUser;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.enumerations.CompanyStatusConstants;
import smartpay.entity.enumerations.PortalUserStatusConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.entity.enumerations.SmartPayConstants;
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
import com.probase.smartpay.admin.financialamountrestrictionmanagement.FinancialAmountRestrictionManagementPortlet;
import com.probase.smartpay.admin.financialamountrestrictionmanagement.FinancialAmountRestrictionManagementPortletState;
import com.probase.smartpay.admin.financialamountrestrictionmanagement.FinancialAmountRestrictionManagementPortletState.FINANCIAL_AMOUNT_RESTRICTION_VIEW;
import com.probase.smartpay.admin.financialamountrestrictionmanagement.FinancialAmountRestrictionManagementPortletUtil;
import com.probase.smartpay.admin.financialamountrestrictionmanagement.FinancialAmountRestrictionManagementPortletState.FINANCIAL_AMOUNT_RESTRICTION;
import com.probase.smartpay.admin.financialamountrestrictionmanagement.FinancialAmountRestrictionManagementPortletState.NAVIGATE;
import com.probase.smartpay.admin.financialamountrestrictionmanagement.FinancialAmountRestrictionManagementPortletState.VIEW_TABS;
import com.probase.smartpay.commins.ProbaseConstants;
import com.probase.smartpay.commins.Util;
import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Portlet implementation class FinancialAmountRestrictionManagementPortlet
 */
public class FinancialAmountRestrictionManagementPortlet extends MVCPortlet {
	private Logger log = Logger.getLogger(FinancialAmountRestrictionManagementPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
	FinancialAmountRestrictionManagementPortletUtil util = FinancialAmountRestrictionManagementPortletUtil.getInstance();
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
		FinancialAmountRestrictionManagementPortletState portletState = 
				FinancialAmountRestrictionManagementPortletState.getInstance(renderRequest, renderResponse);

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
		
		FinancialAmountRestrictionManagementPortletState portletState = FinancialAmountRestrictionManagementPortletState.getInstance(aReq, aRes);
		
		
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
        
        if(action.equalsIgnoreCase(FINANCIAL_AMOUNT_RESTRICTION.PRE_CREATE_FAR.name()))
        {
        	precreateAFinancialRestriction(aReq, aRes, portletState);
        }
        if(action.equalsIgnoreCase(FINANCIAL_AMOUNT_RESTRICTION.PRE_FAR_LISTING.name()))
        {
        	preListFinancialRestrictions(aReq, aRes, portletState);
        }
        if(action.equalsIgnoreCase(FINANCIAL_AMOUNT_RESTRICTION.CREATE_A_FINANCIAL_AMOUNT_RESTRICTION.name()))
        {
        	createAFinancialRestriction(aReq, aRes, portletState);
        }
        else if(action.equalsIgnoreCase(FINANCIAL_AMOUNT_RESTRICTION.FINANCIAL_AMOUNT_RESTRICTION_LISTINGS.name()))
        {
        	handleFinancialAmountRestrictionListing(aReq, aRes, portletState);
        }
        else if(action.equalsIgnoreCase(FINANCIAL_AMOUNT_RESTRICTION_VIEW.FINANCIAL_AMOUNT_RESTRICTION_LISTINGS_VIEW.name()))
        {
    		if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
    		{
				aRes.setRenderParameter("jspPage", "/html/financialamountrestrictionmanagementportlet/prefinancialrestrictionlisting.jsp");
    			portletState.setAllFinancialRestrictionsListing(null);
    			portletState.setCurrentTab(VIEW_TABS.FINANCIAL_AMOUNT_RESTRICTION_LISTING);
    		}else if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
    		{
    			Collection<FinancialAmountRestriction> farList = portletState.getFinancialAmountRestrictionManagementPortletUtil().
    					getFinancialAmountRestrictionsByCompanyId(portletState.getPortalUser().getCompany().getId());
    			if(farList!=null && farList.size()>0)
    			{
        			aRes.setRenderParameter("jspPage", "/html/financialamountrestrictionmanagementportlet/financialamountrestrictionlisting.jsp");
        			portletState.setAllFinancialRestrictionsListing(farList);
        			portletState.setCurrentTab(VIEW_TABS.FINANCIAL_AMOUNT_RESTRICTION_LISTING);
    			}else
    			{
    				portletState.addError(aReq, "There are no financial restrictions selected for this company",
    						portletState);
    			}
    		}
        	
			
        }
        else if(action.equalsIgnoreCase(FINANCIAL_AMOUNT_RESTRICTION_VIEW.CREATE_A_FINANCIAL_AMOUNT_RESTRICTION_VIEW.name()))
        {
        	aRes.setRenderParameter("jspPage", "/html/financialamountrestrictionmanagementportlet/createafinancialrestriction.jsp");
        	portletState.setCurrentTab(VIEW_TABS.CREATE_FINANCIAL_AMOUNT_RESTRICTION);
        }
        else if(action.equalsIgnoreCase(FINANCIAL_AMOUNT_RESTRICTION.EDIT_A_FINANCIAL_AMOUNT_RESTRICTION.name()))
        {
        	handleEditFAR(aReq, aRes, portletState);
        }
        
	}

	private void preListFinancialRestrictions(ActionRequest aReq,
			ActionResponse aRes,
			FinancialAmountRestrictionManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setSelectedCompanyId(aReq.getParameter("companySelected"));
		if(portletState.getSelectedCompanyId().equals("-1"))
		{
			portletState.addError(aReq, "Select a company to proceed", portletState);
			aRes.setRenderParameter("jspPage", "/html/financialamountrestrictionmanagementportlet/prefinancialrestrictionlisting.jsp");
		}else
		{
			portletState.setCurrentTab(VIEW_TABS.FINANCIAL_AMOUNT_RESTRICTION_LISTING);
			Collection<FinancialAmountRestriction> farList = portletState.getFinancialAmountRestrictionManagementPortletUtil().getFinancialAmountRestrictionsByCompanyId(Long.valueOf(portletState.getSelectedCompanyId()));
			if(farList!=null && farList.size()>0)
			{
				portletState.setAllFinancialRestrictionsListing(
						portletState.getFinancialAmountRestrictionManagementPortletUtil().getFinancialAmountRestrictionsByCompanyId(Long.valueOf(portletState.getSelectedCompanyId())));
				aRes.setRenderParameter("jspPage", "/html/financialamountrestrictionmanagementportlet/financialamountrestrictionlisting.jsp");
			}
			else
			{
				portletState.addError(aReq, "You do not have any Financial Restrictions Created for the selected company", portletState);
				aRes.setRenderParameter("jspPage", "/html/financialamountrestrictionmanagementportlet/prefinancialrestrictionlisting.jsp");
			}
		}
	}

	private void precreateAFinancialRestriction(ActionRequest aReq,
			ActionResponse aRes,
			FinancialAmountRestrictionManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setSelectedCompanyId(aReq.getParameter("companySelected"));
		if(portletState.getSelectedCompanyId().equals("-1"))
		{
			portletState.addError(aReq, "Select a company to proceed", portletState);
			aRes.setRenderParameter("jspPage", "/html/financialamountrestrictionmanagementportlet/precreatefinancialrestriction.jsp");
		}else
		{
			
			aRes.setRenderParameter("jspPage", "/html/financialamountrestrictionmanagementportlet/createafinancialrestriction.jsp");
		}
	}

	private void handleFinancialAmountRestrictionListing(ActionRequest aReq,
			ActionResponse aRes,
			FinancialAmountRestrictionManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String selectedFinancialAmountRestrictionAction = aReq.getParameter("selectedFARAction");
		if(selectedFinancialAmountRestrictionAction.equalsIgnoreCase("goback"))
		{
			if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
			{
				portletState.setSelectedCompanyId(null);
				aRes.setRenderParameter("jspPage", "/html/financialamountrestrictionmanagementportlet/prefinancialrestrictionlisting.jsp");
			}else
			{

				portletState.setSelectedCompanyId(null);
				portletState.setCurrentTab(VIEW_TABS.CREATE_FINANCIAL_AMOUNT_RESTRICTION);
				aRes.setRenderParameter("jspPage", "/html/financialamountrestrictionmanagementportlet/createafinancialrestriction.jsp");
			}
		}else
		{
			String selectedFinancialAmountRestriction = aReq.getParameter("selectedFAR");
			
			if(selectedFinancialAmountRestriction!=null)
			{
				try{
					Long selectedFinancialAmountRestrictionId = Long.valueOf(selectedFinancialAmountRestriction);
					portletState.setSelectedFinancialAmountRestrictionId(selectedFinancialAmountRestrictionId);
					FinancialAmountRestriction far = (FinancialAmountRestriction)portletState.getFinancialAmountRestrictionManagementPortletUtil().getEntityObjectById(
							FinancialAmountRestriction.class, selectedFinancialAmountRestrictionId);
					if(far!=null && selectedFinancialAmountRestrictionAction.equalsIgnoreCase("upda918te"))
					{
						portletState.setSelectedCompanyId(Long.toString(far.getCompany().getId()));
						portletState.setMinimumPaymentThreshold(Double.toString(far.getLowerLimitValue()));
						portletState.setMaximumPaymentThreshold(Double.toString(far.getUpperLimitValue()));
						aRes.setRenderParameter("jspPage", "/html/financialamountrestrictionmanagementportlet/editafinancialrestriction.jsp");
						portletState.setCurrentTab(VIEW_TABS.FINANCIAL_AMOUNT_RESTRICTION_LISTING);
					}else if(far!=null && selectedFinancialAmountRestrictionAction.equalsIgnoreCase("delete"))
					{
						swpService.deleteRecord(far);
						portletState.setAllFinancialRestrictionsListing(
								portletState.getFinancialAmountRestrictionManagementPortletUtil().getFinancialAmountRestrictionsByCompanyId(Long.valueOf(portletState.getSelectedCompanyId())));
						portletState.addSuccess(aReq, "Financial Amount Restriction deleted successfully!", portletState);
						aRes.setRenderParameter("jspPage", "/html/financialamountrestrictionmanagementportlet/financialamountrestrictionlisting.jsp");
						portletState.setCurrentTab(VIEW_TABS.FINANCIAL_AMOUNT_RESTRICTION_LISTING);
					}
				}catch(NumberFormatException e)
				{
					aRes.setRenderParameter("jspPage", "/html/financialamountrestrictionmanagementportlet/financialamountrestrictionlisting.jsp");
					portletState.addError(aReq, "Invalid Financial Amount Restriction selected. Please select a valid one before proceeding", portletState);
				}
			}else
			{
				aRes.setRenderParameter("jspPage", "/html/financialamountrestrictionmanagementportlet/financialamountrestrictionlisting.jsp");
				portletState.addError(aReq, "Invalid Financial Amount Restriction selected. Please select a valid one before proceeding", portletState);
			}
		}
	}

	private void createAFinancialRestriction(ActionRequest aReq,
			ActionResponse aRes, FinancialAmountRestrictionManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setMinimumPaymentThreshold(aReq.getParameter("minpaymentthreshold"));
		portletState.setMaximumPaymentThreshold(aReq.getParameter("maxpaymentthreshold"));
		if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
		{
			portletState.setSelectedCompanyId(aReq.getParameter("companySelected"));
		}else if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
		{
			portletState.setSelectedCompanyId(Long.toString(portletState.getPortalUser().getCompany().getId()));
		}
		
		if(isFinancialRestrictionDataValid(portletState, aReq, aRes, true))
		{
			log.info("Bank branch Data is valid");
			Util util = new Util();
			FinancialAmountRestriction far = new FinancialAmountRestriction();
			portletState.setFinancialPaymentRestrictionName("Between ZMW" + 
					util.roundUpAmount(Double.valueOf(portletState.getMinimumPaymentThreshold())) + " and ZMW" + 
					util.roundUpAmount(Double.valueOf(portletState.getMaximumPaymentThreshold())));
			far.setLowerLimitValue(Double.valueOf(portletState.getMinimumPaymentThreshold()));
			far.setUpperLimitValue(Double.valueOf(portletState.getMaximumPaymentThreshold()));
			far.setCreatedByPortalUserId(portletState.getPortalUser().getId());
			far.setDateCreated(new Timestamp((new Date()).getTime()));
			far.setName(portletState.getFinancialPaymentRestrictionName());
			far.setStatus(Boolean.TRUE);
			if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
			{
				far.setCompany((Company)portletState.getFinancialAmountRestrictionManagementPortletUtil().getEntityObjectById(
						Company.class, Long.valueOf(portletState.getSelectedCompanyId())));
			}else if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
			{
				far.setCompany(portletState.getPortalUser().getCompany());
			}
			
			
			far = (FinancialAmountRestriction)swpService.createNewRecord(far);
			handleAudit("Financial Amount Restriction Creation", Long.toString(far.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
			aRes.setRenderParameter("jspPage", "/html/financialamountrestrictionmanagementportlet/financialamountrestrictionlisting.jsp");
			portletState.addSuccess(aReq, "Financial Amount Restriction - " + far.getName() + " - was created successfully!", portletState);
			portletState.reinitializeForFinancialAmountRestriction(portletState);
			portletState.setAllFinancialRestrictionsListing(portletState.getFinancialAmountRestrictionManagementPortletUtil().getAllFinancialAmountRestrictionListing());
			portletState.setCurrentTab(VIEW_TABS.FINANCIAL_AMOUNT_RESTRICTION_LISTING);
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/financialamountrestrictionmanagementportlet/createafinancialrestriction.jsp");
		}
	}
	
	
	private void handleEditFAR(ActionRequest aReq,
			ActionResponse aRes, FinancialAmountRestrictionManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setMinimumPaymentThreshold(aReq.getParameter("minpaymentthreshold"));
		portletState.setMaximumPaymentThreshold(aReq.getParameter("maxpaymentthreshold"));
		if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
		{
			portletState.setSelectedCompanyId(aReq.getParameter("companySelected"));
		}else if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
		{
			portletState.setSelectedCompanyId(Long.toString(portletState.getPortalUser().getCompany().getId()));
		}
		
		if(isFinancialRestrictionDataValid(portletState, aReq, aRes, false))
		{
			log.info("Bank branch Data is valid");
			Util util = new Util();
			FinancialAmountRestriction far = new FinancialAmountRestriction();
			portletState.setFinancialPaymentRestrictionName("Between ZMW" + 
					util.roundUpAmount(Double.valueOf(portletState.getMinimumPaymentThreshold())) + " and ZMW" + 
					util.roundUpAmount(Double.valueOf(portletState.getMaximumPaymentThreshold())));
			far.setLowerLimitValue(Double.valueOf(portletState.getMinimumPaymentThreshold()));
			far.setUpperLimitValue(Double.valueOf(portletState.getMaximumPaymentThreshold()));
			far.setCreatedByPortalUserId(portletState.getPortalUser().getId());
			far.setDateCreated(new Timestamp((new Date()).getTime()));
			far.setName(portletState.getFinancialPaymentRestrictionName());
			far.setStatus(Boolean.TRUE);
			if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
			{
				far.setCompany((Company)portletState.getFinancialAmountRestrictionManagementPortletUtil().getEntityObjectById(
						Company.class, Long.valueOf(portletState.getSelectedCompanyId())));
			}else if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
			{
				far.setCompany(portletState.getPortalUser().getCompany());
			}
			
			
			swpService.updateRecord(far);
			handleAudit("Financial Amount Restriction Update", Long.toString(far.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
			aRes.setRenderParameter("jspPage", "/html/financialamountrestrictionmanagementportlet/financialamountrestrictionlisting.jsp");
			portletState.addSuccess(aReq, "Financial Amount Restriction - " + far.getName() + " - was created successfully!", portletState);
			portletState.reinitializeForFinancialAmountRestriction(portletState);
			portletState.setAllFinancialRestrictionsListing(portletState.getFinancialAmountRestrictionManagementPortletUtil().getAllFinancialAmountRestrictionListing());
			portletState.setCurrentTab(VIEW_TABS.FINANCIAL_AMOUNT_RESTRICTION_LISTING);
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/financialamountrestrictionmanagementportlet/createafinancialrestriction.jsp");
		}
	}

	
	
	private boolean isFinancialRestrictionDataValid(FinancialAmountRestrictionManagementPortletState portletState,
			ActionRequest aReq, ActionResponse aRes, boolean checkExistingForNew) {
		// TODO Auto-generated method stub
		String errorMessage = null;
		
		if(portletState.getMaximumPaymentThreshold()!=null && portletState.getMaximumPaymentThreshold().trim().length()>0)
		{
			if(portletState.getMinimumPaymentThreshold()!=null && portletState.getMinimumPaymentThreshold().trim().length()>0)
			{
				if(portletState.getSelectedCompanyId()!=null && !portletState.getSelectedCompanyId().equals("-1"))
				{
					Double lowerLimit = Double.valueOf(portletState.getMinimumPaymentThreshold());
					Double upperLimit = Double.valueOf(portletState.getMaximumPaymentThreshold());
					
					if(lowerLimit<upperLimit)
					{
						try{
							Collection<FinancialAmountRestriction> farList = null;
							if(checkExistingForNew)
							{
								farList = portletState.getFinancialAmountRestrictionManagementPortletUtil().getFinancialAmountRestrictionBasedOnBoundaries(
									upperLimit, lowerLimit, Long.valueOf(portletState.getSelectedCompanyId()));
							}else
							{
								farList = portletState.getFinancialAmountRestrictionManagementPortletUtil().
										getFinancialAmountRestrictionBasedOnBoundariesExceptId(
										upperLimit, lowerLimit, 
										Long.valueOf(portletState.getSelectedCompanyId()), portletState.getSelectedFinancialAmountRestrictionId());
							}
							
							if((farList!=null && farList.size()>0))
							{
								errorMessage =  "The Financial Amount Restriction you provided is invalid. Your Financial Amount Restriction must have a boundary above already existing financial amount restrictions.";
							}
							else
							{
								
							}
						}catch(NumberFormatException e)
						{
							e.printStackTrace();
							errorMessage= "Select a valid company before proceeding";
						}
					}else
					{
						errorMessage =  "The maximum amount specified is less than the mimimum amount specified.";
					}
				}else
				{
					errorMessage =  "This process cannot be completed. Please specify the company this financial amount restriction belongs to";
				}
				
			}else
			{
				errorMessage =  "Provide a valid minimum amount";
			}
		}else
		{
			errorMessage =  "Provide a valid maximum amount";
		}
		
		if(errorMessage==null)
		{
			
			return true;
		}
		else
		{
			log.info("errorMessage = " + errorMessage);
			portletState.addError(aReq, errorMessage, portletState);
			return false;
		}
	}
	

	private void handleNavigations(ActionRequest aReq, ActionResponse aRes,
			FinancialAmountRestrictionManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String action = aReq.getParameter("actionUrl");
		
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
