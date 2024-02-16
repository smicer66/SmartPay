package com.probase.smartpay.admin.bankbranchmanagement;

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


import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.probase.smartpay.admin.bankbranchmanagement.BankBranchManagementPortletState.BANK_BRANCHES_VIEW;
import com.probase.smartpay.admin.bankbranchmanagement.BankBranchManagementPortletState.BANK_BRANCH_ACTION;
import com.probase.smartpay.admin.bankbranchmanagement.BankBranchManagementPortletState.VIEW_TABS;
import com.probase.smartpay.commins.ProbaseConstants;
import com.probase.smartpay.commins.Util;
import com.sf.primepay.smartpay13.ServiceLocator;
import com.liferay.portal.DuplicateUserScreenNameException;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * Portlet implementation class BankBranchManagementPortlet
 */
public class BankBranchManagementPortlet extends MVCPortlet {
 
	private Logger log = Logger.getLogger(BankBranchManagementPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
	BankBranchManagementPortletUtil util = BankBranchManagementPortletUtil.getInstance();
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
		BankBranchManagementPortletState portletState = 
				BankBranchManagementPortletState.getInstance(renderRequest, renderResponse);

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
		
		BankBranchManagementPortletState portletState = BankBranchManagementPortletState.getInstance(aReq, aRes);
		
		
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
        if(action.equalsIgnoreCase(BANK_BRANCH_ACTION.CREATE_A_BANK_BRANCH.name()))
        {
        	handleBankBranchCreation(aReq, aRes, portletState);
        }if(action.equalsIgnoreCase(BANK_BRANCH_ACTION.EDIT_A_BANK_BRANCH.name()))
        {
        	handleBankBranchUpdate(aReq, aRes, portletState);
        }
        if(action.equalsIgnoreCase(BANK_BRANCH_ACTION.BANK_BRANCH_LISTING_ACTION.name()))
        {
        	handleBankBranchListingAction(aReq, aRes, portletState);
        }
        if(action.equalsIgnoreCase(BANK_BRANCHES_VIEW.CREATE_A_BANK_BRANCH.name()))
        {
        	portletState.reinitializeForCreateBankBranch(portletState);
        	aRes.setRenderParameter("jspPage", "/html/bankbranchmanagementportlet/bankbranch/createabankbranch.jsp");
        	portletState.setCurrentTab(VIEW_TABS.CREATE_A_BANK_BRANCH);
        }if(action.equalsIgnoreCase(BANK_BRANCHES_VIEW.VIEW_BANK_BRANCHES.name()))
        {
        	portletState.setAllBankBranchListing(portletState.getBankBranchPortletUtil().getAllBankBranchListing());
        	aRes.setRenderParameter("jspPage", "/html/bankbranchmanagementportlet/bankbranch/bankbranchlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.VIEW_BANK_BRANCHES);
        }
		
	}

	
	private void handleBankBranchCreation(ActionRequest aReq,
			ActionResponse aRes, BankBranchManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setBankBranchName(aReq.getParameter("bankbranchname"));
		portletState.setBankBranchCode(aReq.getParameter("bankbranchcode"));
		
		if(isBankBranchDataValid(portletState, aReq, aRes, true))
		{
			log.info("Bank branch Data is valid");
			BankBranches bankBranches = new BankBranches();
			bankBranches.setBankCode(portletState.getBankBranchCode());
			bankBranches.setName(portletState.getBankBranchName());
			
			bankBranches = (BankBranches)swpService.createNewRecord(bankBranches);
			handleAudit("Bank Branch Creation", Long.toString(bankBranches.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
			aRes.setRenderParameter("jspPage", "/html/bankbranchmanagementportlet/bankbranch/bankbranchlisting.jsp");
			portletState.addSuccess(aReq, "Bank branch - " + bankBranches.getName() + " - was created successfully!", portletState);
			portletState.reinitializeForCreateBankBranch(portletState);
			portletState.setAllBankBranchListing(portletState.getBankBranchPortletUtil().getAllBankBranchListing());
        	portletState.setCurrentTab(VIEW_TABS.VIEW_BANK_BRANCHES);
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/bankbranchmanagementportlet/bankbranch/createabankbranch.jsp");
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
	
	
	private void handleBankBranchUpdate(ActionRequest aReq,
			ActionResponse aRes, BankBranchManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setBankBranchName(aReq.getParameter("bankbranchname"));
		portletState.setBankBranchCode(aReq.getParameter("bankbranchcode"));
		
		if(isBankBranchDataValid(portletState, aReq, aRes, false))
		{
			BankBranches bankBranches = (BankBranches)portletState.getBankBranchPortletUtil().getEntityObjectById(BankBranches.class, Long.valueOf(portletState.getSelectedBankBranchId()));
			if(bankBranches!=null)
			{
				bankBranches.setBankCode(portletState.getBankBranchCode());
				bankBranches.setName(portletState.getBankBranchName());
				
				swpService.updateRecord(bankBranches);
				handleAudit("Bank Branch Update", Long.toString(bankBranches.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				aRes.setRenderParameter("jspPage", "/html/bankbranchmanagementportlet/bankbranch/bankbranchlisting.jsp");
				portletState.addSuccess(aReq, "Bank branch - " + bankBranches.getName() + " - was updated successfully!", portletState);
				portletState.reinitializeForCreateBankBranch(portletState);
				portletState.setAllBankBranchListing(portletState.getBankBranchPortletUtil().getAllBankBranchListing());
	        	portletState.setCurrentTab(VIEW_TABS.VIEW_BANK_BRANCHES);
			}else
			{
				portletState.addError(aReq, "The bank branch details were not saved! Please select a valid bank branch before editing it.", portletState);
				aRes.setRenderParameter("jspPage", "/html/bankbranchmanagementportlet/bankbranch/bankbranchlisting.jsp");
			}
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/bankbranchmanagementportlet/bankbranch/editabankbranch.jsp");
		}
	}
	
	

	private boolean isBankBranchDataValid(BankBranchManagementPortletState portletState,
			ActionRequest aReq, ActionResponse aRes, boolean checkExistingForNew) {
		// TODO Auto-generated method stub
		String errorMessage = null;
		
		if(portletState.getBankBranchName()!=null && portletState.getBankBranchName().trim().length()>0)
		{
			if(portletState.getBankBranchCode()!=null && portletState.getBankBranchCode().trim().length()>0)
			{
				Collection<BankBranches> checkBankBranch = null;
				if(checkExistingForNew)
				{
					checkBankBranch = portletState.getBankBranchPortletUtil().getBankBranchByNameOrCode(
							portletState.getBankBranchName(), portletState.getBankBranchCode());
				}else
				{
					checkBankBranch = portletState.getBankBranchPortletUtil().getBankBranchByNameOrCodeForEdit(
							portletState.getBankBranchName(), portletState.getBankBranchCode(), Long.valueOf(portletState.getSelectedBankBranchId()));
				}
				
				
				if((checkBankBranch!=null && checkBankBranch.size()>0))
				{
					errorMessage =  "The bank branch name and bank code provided already exist on the system. This bank branch already has been created.";
				}
				else
				{
					
				}
				
			}else
			{
				errorMessage =  "Provide a bank branch code before proceeding";
			}
		}else
		{
			errorMessage =  "Provide a bank branch name before proceeding";
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
			BankBranchManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String action = aReq.getParameter("actionUrl");
		if(action.equalsIgnoreCase("createbankbranch"))
		{
			portletState.reinitializeForCreateBankBranch(portletState);
			aRes.setRenderParameter("jspPage", "/html/bankbranchmanagementportlet/bankbranch/createabankbranch.jsp");
			portletState.setCurrentTab(VIEW_TABS.CREATE_A_BANK_BRANCH);
		}else if(action.equalsIgnoreCase("bankbranchlistings"))
		{
			portletState.setAllBankBranchListing(portletState.getBankBranchPortletUtil().getAllBankBranchListing());
			aRes.setRenderParameter("jspPage", "/html/bankbranchmanagementportlet/bankbranch/bankbranchlisting.jsp");
			portletState.setCurrentTab(VIEW_TABS.VIEW_BANK_BRANCHES);
		}
	}

	private void handleBankBranchListingAction(ActionRequest aReq,
			ActionResponse aRes, BankBranchManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String bankBranchId = aReq.getParameter("selectedBankBranch").trim();
		log.info("bankBranchId = " + bankBranchId);
		try
		{
			Long bankBranchIdL = Long.valueOf(bankBranchId);
			log.info("bankBranchIdL = " + bankBranchIdL);
			BankBranches bankBranches = (BankBranches)portletState.getBankBranchPortletUtil().getEntityObjectById(BankBranches.class, bankBranchIdL);
			portletState.setSelectedBankBranchId(Long.toString(bankBranchIdL));
			if(bankBranches!=null)
			{
				if(aReq.getParameter("selectedBankBranchAction")!=null && aReq.getParameter("selectedBankBranchAction").equalsIgnoreCase("update"))
				{
					portletState.setBankBranchName(bankBranches.getName());
					portletState.setBankBranchCode(bankBranches.getBankCode());
					aRes.setRenderParameter("jspPage", "/html/bankbranchmanagementportlet/bankbranch/editabankbranch.jsp");
				
				}else if(aReq.getParameter("selectedBankBranchAction")!=null && aReq.getParameter("selectedBankBranchAction").equalsIgnoreCase("delete"))
				{
					bankBranches.setStatus(Boolean.FALSE);
					swpService.updateRecord(bankBranches);
					handleAudit("Bank Branch Delete", Long.toString(bankBranches.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
					aRes.setRenderParameter("jspPage", "/html/bankbranchmanagementportlet/bankbranch/bankbranchlisting.jsp");
					portletState.addSuccess(aReq, "Selected Bank Branch - " + bankBranches.getName() + " - has been deleted successfully.", portletState);
					portletState.setAllBankBranchListing(portletState.getBankBranchPortletUtil().getAllBankBranchListing());
				}
			}else
			{
				portletState.addError(aReq, "This action can not be carried out on the selected bank branch. You seem to have not selected a valid bank branch. Select one before proceeding", portletState);
				aRes.setRenderParameter("jspPage", "/html/bankbranchmanagementportlet/bankbranch/bankbranchlisting.jsp");
			}
		}catch(NumberFormatException e)
		{
			e.printStackTrace();
			portletState.addError(aReq, "This action can not be carried out on the selected bank branch. Select a valid bank branch before proceeding", portletState);
			aRes.setRenderParameter("jspPage", "/html/bankbranchmanagementportlet/bankbranch/bankbranchlisting.jsp");
		}
		
		
	}
	

	

}

