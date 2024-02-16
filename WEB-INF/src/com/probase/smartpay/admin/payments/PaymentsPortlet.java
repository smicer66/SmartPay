package com.probase.smartpay.admin.payments;

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
import smartpay.entity.PaymentHistory;
import smartpay.entity.PortalUser;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.enumerations.CompanyStatusConstants;
import smartpay.entity.enumerations.PaymentHistoryConstants;
import smartpay.entity.enumerations.PortalUserStatusConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.service.SwpService;


import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.probase.smartpay.admin.payments.PaymentsPortletState.PAYMENTS_ACTION;
import com.probase.smartpay.admin.payments.PaymentsPortletState.PAYMENTS_VIEW;
import com.probase.smartpay.admin.payments.PaymentsPortletState.VIEW_TABS;
import com.probase.smartpay.commins.ProbaseConstants;
import com.probase.smartpay.commins.Util;
import com.sf.primepay.smartpay13.ServiceLocator;
import com.liferay.portal.DuplicateUserScreenNameException;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * Portlet implementation class PaymentsPortlet
 */
public class PaymentsPortlet extends MVCPortlet {
 
	private Logger log = Logger.getLogger(PaymentsPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
	PaymentsPortletUtil util = PaymentsPortletUtil.getInstance();
	ServiceContext serviceContext = new ServiceContext();
	
	
	@Override
	public void init(PortletConfig config) throws PortletException {
		// TODO Auto-generated method stub
		log.info("Payments Portlet portlet init called...");		
		pContext = config.getPortletContext();
		super.init(config);
		pConfig = config;
	    this.swpService = this.serviceLocator.getSwpService();
	}
	
	@Override
	public void render(RenderRequest renderRequest,
			RenderResponse renderResponse) throws PortletException, IOException {
		log.info("Payments Portlet render called...");	
		PortletSession ps = renderRequest.getPortletSession();
		PaymentsPortletState portletState = 
				PaymentsPortletState.getInstance(renderRequest, renderResponse);

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
		
		PaymentsPortletState portletState = PaymentsPortletState.getInstance(aReq, aRes);
		
		
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
        Collection<PaymentHistory> paymentListing = null;
        
        if(action.equalsIgnoreCase(PAYMENTS_ACTION.HANDLE_PAYMENT_LISTING.name()))
        {
        	handlePaymentlistingAction(aReq, aRes, portletState);
        }
        if(action.equalsIgnoreCase(PAYMENTS_VIEW.VIEW_ZRA_SUCCESSFUL_PAYMENT_LISTING.name()))
        {
        
        	if(portletState.getPortalUser().getCompany()==null)
        	{
        		paymentListing = portletState.getPaymentsPortletUtil().getPaymentsByStatus(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_ZRA_CONFIRMED);
        	}else
        	{
        		paymentListing = portletState.getPaymentsPortletUtil().getPaymentsByStatusAndCompany(
        				PaymentHistoryConstants.PAYMENTHISTORY_STATUS_ZRA_CONFIRMED, portletState.getPortalUser().getCompany().getId());
        		
        	}
        	if(paymentListing!=null && paymentListing.size()>0)
        	{
        		portletState.setAllPaymentsHistoryListing(paymentListing);
        		portletState.setCurrentTab(VIEW_TABS.VIEW_ZRA_SUCCESSFUL_PAYMENT_LISTING_VIEW);
        	}
        	else
        	{
        		portletState.addError(aReq, "There are no payments currently approved by ZRA", portletState);
        	}
        	aRes.setRenderParameter("jspPage", "/html/paymentsportlet/paymentslisting.jsp");
        }if(action.equalsIgnoreCase(PAYMENTS_VIEW.VIEW_ZRA_DECLINED_PAYMENT_LISTING.name()))
        {
        	if(portletState.getPortalUser().getCompany()==null)
        	{
        		paymentListing = portletState.getPaymentsPortletUtil().getPaymentsByStatus(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_ZRA_CONTROVERSIAL);
        	}else
        	{
        		paymentListing = portletState.getPaymentsPortletUtil().getPaymentsByStatusAndCompany(
        				PaymentHistoryConstants.PAYMENTHISTORY_STATUS_ZRA_CONTROVERSIAL, portletState.getPortalUser().getCompany().getId());
        		
        	}
        	if(paymentListing!=null && paymentListing.size()>0)
        	{
        		portletState.setAllPaymentsHistoryListing(paymentListing);
        		portletState.setCurrentTab(VIEW_TABS.VIEW_ZRA_DECLINED_PAYMENT_LISTING_VIEW);
        	}
        	else
        	{
        		portletState.addError(aReq, "There are no payments currently declined by ZRA", portletState);
        	}
        	aRes.setRenderParameter("jspPage", "/html/paymentsportlet/paymentslisting.jsp");
        }
        if(action.equalsIgnoreCase(PAYMENTS_VIEW.VIEW_REVERSED_PAYMENT_LISTING.name()))
        {
        	if(portletState.getPortalUser().getCompany()==null)
        	{
        		paymentListing = portletState.getPaymentsPortletUtil().getPaymentsByStatus(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_REVERSED);
        	}else
        	{
        		paymentListing = portletState.getPaymentsPortletUtil().getPaymentsByStatusAndCompany(
        				PaymentHistoryConstants.PAYMENTHISTORY_STATUS_REVERSED, portletState.getPortalUser().getCompany().getId());
        		
        	}
        	if(paymentListing!=null && paymentListing.size()>0)
        	{
        		portletState.setAllPaymentsHistoryListing(paymentListing);
        		portletState.setCurrentTab(VIEW_TABS.VIEW_REVERSED_PAYMENT_LISTING_VIEW);
        	}
        	else
        	{
        		portletState.addError(aReq, "There are no payments currently reversed by the bank", portletState);
        	}
        	aRes.setRenderParameter("jspPage", "/html/paymentsportlet/paymentslisting.jsp");
        }
        if(action.equalsIgnoreCase(PAYMENTS_VIEW.VIEW_BANL_DECLINED_PAYMENT_LISTING.name()))
        {
        	if(portletState.getPortalUser().getCompany()==null)
        	{
        		paymentListing = portletState.getPaymentsPortletUtil().getPaymentsByStatus(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_DECLINED);
        	}else
        	{
        		paymentListing = portletState.getPaymentsPortletUtil().getPaymentsByStatusAndCompany(
        				PaymentHistoryConstants.PAYMENTHISTORY_STATUS_DECLINED, portletState.getPortalUser().getCompany().getId());
        		
        	}
        	if(paymentListing!=null && paymentListing.size()>0)
        	{
        		portletState.setAllPaymentsHistoryListing(paymentListing);
        		portletState.setCurrentTab(VIEW_TABS.VIEW_BANL_DECLINED_PAYMENT_LISTING_VIEW);
        	}
        	else
        	{
        		portletState.addError(aReq, "There are no payments currently declined by the Bank", portletState);
        	}
        	aRes.setRenderParameter("jspPage", "/html/paymentsportlet/paymentslisting.jsp");
        }
        if(action.equalsIgnoreCase(PAYMENTS_VIEW.VIEW_BANK_APPROVED_PAYMENT_LISTING.name()))
        {
        	if(portletState.getPortalUser().getCompany()==null)
        	{
        		paymentListing = portletState.getPaymentsPortletUtil().getPaymentsByStatus(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_APPROVED);
        	}else
        	{
        		paymentListing = portletState.getPaymentsPortletUtil().getPaymentsByStatusAndCompany(
        				PaymentHistoryConstants.PAYMENTHISTORY_STATUS_APPROVED, portletState.getPortalUser().getCompany().getId());
        		
        	}
        	if(paymentListing!=null && paymentListing.size()>0)
        	{
        		portletState.setAllPaymentsHistoryListing(paymentListing);
        		portletState.setCurrentTab(VIEW_TABS.VIEW_BANK_APPROVED_PAYMENT_LISTING_VIEW);
        	}
        	else
        	{
        		portletState.addError(aReq, "There are no payments currently approved by ZRA", portletState);
        	}
        	aRes.setRenderParameter("jspPage", "/html/paymentsportlet/paymentslisting.jsp");
        }
        if(action.equalsIgnoreCase(PAYMENTS_ACTION.HANDLE_SINGLE_VIEW_ACTIONS.name()))
        {
        	aRes.setRenderParameter("jspPage", "/html/paymentsportlet/paymentslisting.jsp");
        }
		
	}

	
	
	
	private void handlePaymentlistingAction(ActionRequest aReq,
			ActionResponse aRes, PaymentsPortletState portletState) {
		// TODO Auto-generated method stub
		String selectedAction = aReq.getParameter("selectedPaymentHistoryAction");
		String selectedActionId = aReq.getParameter("selectedPaymentHistory");
		
		long id = Long.valueOf(selectedActionId);
		PaymentHistory ph = (PaymentHistory)portletState.getPaymentsPortletUtil().getEntityObjectById(PaymentHistory.class, id);
		if(ph!=null)
		{
			if(selectedAction.equalsIgnoreCase("view"))
			{
				portletState.setSelectedPaymentHistory(ph);
	        	aRes.setRenderParameter("jspPage", "/html/paymentsportlet/viewpayment.jsp");
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

