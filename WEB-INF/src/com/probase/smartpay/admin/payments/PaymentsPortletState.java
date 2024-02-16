package com.probase.smartpay.admin.payments;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;

import org.apache.log4j.Logger;

import smartpay.entity.AuthorizePanel;
import smartpay.entity.AuthorizePanelCombination;
import smartpay.entity.PaymentHistory;
import smartpay.entity.Company;
import smartpay.entity.FeeDescription;
import smartpay.entity.FinancialAmountRestriction;
import smartpay.entity.PortalUser;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.enumerations.CompanyStatusConstants;
import smartpay.entity.enumerations.PaymentHistoryConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.service.SwpService;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.probase.smartpay.commins.ComminsApplicationState;
import com.probase.smartpay.commins.Util;
import com.sf.primepay.smartpay13.ServiceLocator;

public class PaymentsPortletState {

	private static Logger log = Logger.getLogger(PaymentsPortletState.class);
	private static PaymentsPortletUtil paymentsPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private String remoteIPAddress;
	private ArrayList<RoleType> portalUserRoleType;
	private static ComminsApplicationState cas;
	

	private String successMessage;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	
	private Collection<PaymentHistory> allPaymentsHistoryListing;
	private PaymentHistory selectedPaymentHistory;
	
	
	/*****************NAVIGATION*****************/
	private VIEW_TABS currentTab;
	private String selectedPaymentHistoryId;
	
	
	
	
    
    public static enum VIEW_TABS{
    	VIEW_ZRA_SUCCESSFUL_PAYMENT_LISTING_VIEW, VIEW_ZRA_DECLINED_PAYMENT_LISTING_VIEW, VIEW_REVERSED_PAYMENT_LISTING_VIEW, 
    	VIEW_BANL_DECLINED_PAYMENT_LISTING_VIEW, VIEW_BANK_APPROVED_PAYMENT_LISTING_VIEW
    }
    
    public static enum PAYMENTS_VIEW
    {
    	VIEW_ZRA_SUCCESSFUL_PAYMENT_LISTING, VIEW_ZRA_DECLINED_PAYMENT_LISTING, VIEW_REVERSED_PAYMENT_LISTING, 
    	VIEW_BANL_DECLINED_PAYMENT_LISTING, VIEW_BANK_APPROVED_PAYMENT_LISTING
    }
    
    
	public static enum NAVIGATE{
		NAVIGATE_ACTIONS
	}
	
	
	public static enum PAYMENTS_ACTION{
		HANDLE_PAYMENT_LISTING, HANDLE_SINGLE_VIEW_ACTIONS
	}
	
	
	
	/****core section starts here****/
	public static PaymentsPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		PaymentsPortletState portletState = null;
		Logger.getLogger(PaymentsPortletState.class).info("------getInstance");
		try {
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (PaymentsPortletState) session.getAttribute(PaymentsPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new PaymentsPortletState();
					PaymentsPortletUtil util = new PaymentsPortletUtil();
					portletState.setPaymentsPortletUtil(util);
					session.setAttribute(PaymentsPortletState.class.getName(), portletState);
					defaultInit(request, portletState);
					portletState.setCurrentTab(VIEW_TABS.VIEW_ZRA_SUCCESSFUL_PAYMENT_LISTING_VIEW);
					portletState.setCas(ComminsApplicationState.getInstance(request, response));
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
	
	public void reinitializeForCreatePaymentsHistory(PaymentsPortletState portletState) {
		// TODO Auto-generated method stub
	}
	
	
	private void setPaymentsPortletUtil(PaymentsPortletUtil util) {
		// TODO Auto-generated method stub
		this.paymentsPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, PaymentsPortletState portletState) {
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
				PaymentsPortletUtil util = PaymentsPortletUtil.getInstance();
				portletState.setPaymentsPortletUtil(util);
				
				ArrayList<RoleType> pur = portletState.getPaymentsPortletUtil().
						getRoleTypeByPortalUser(portletState.getPortalUser());	
				portletState.setPortalUserRoleType(pur);
				
				loadPayments(portletState, PaymentHistoryConstants.PAYMENTHISTORY_STATUS_ZRA_CONFIRMED);
				
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


	private static void loadPayments(PaymentsPortletState portletState, PaymentHistoryConstants paymenthistoryStatus) {
		// TODO Auto-generated method stub
		if(portletState.getPortalUser().getCompany()==null)
		{
			Collection<PaymentHistory> paymentHistoryListing = portletState.getPaymentsPortletUtil().getPaymentsByStatus(paymenthistoryStatus);
			portletState.setAllPaymentsHistoryListing(paymentHistoryListing);
		}
		else
		{
			Collection<PaymentHistory> paymentHistoryListing = portletState.getPaymentsPortletUtil().getPaymentsByStatusAndCompany(paymenthistoryStatus, portletState.getPortalUser().getCompany().getId());
			portletState.setAllPaymentsHistoryListing(paymentHistoryListing);
		}
	}


	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			PaymentsPortletState portletState) {

		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			PaymentsPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			PaymentsPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			PaymentsPortletState.log.debug("Error including error message", e);
		}
	}
	
	public void setSuccessMessage(String successMessage)
	{
		this.successMessage=successMessage;
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


	public PaymentsPortletUtil getPaymentsPortletUtil() {
		// TODO Auto-generated method stub
		return this.paymentsPortletUtil;
	}


	public ArrayList<RoleType> getPortalUserRoleType() {
		return portalUserRoleType;
	}


	public void setPortalUserRoleType(ArrayList<RoleType> portalUserRoleType) {
		this.portalUserRoleType = portalUserRoleType;
	}
	
	
	public void setAllPaymentsHistoryListing(Collection<PaymentHistory> allPaymentsHistoryListing)
	{
		this.allPaymentsHistoryListing = allPaymentsHistoryListing;
	}
	
	public Collection<PaymentHistory> getAllPaymentsHistoryListing()
	{
		return this.allPaymentsHistoryListing;
	}


	/****company creation section starts here****/
	

	public ArrayList<String> getErrorList() {
		return errorList;
	}


	public void setErrorList(ArrayList<String> errorList) {
		this.errorList = errorList;
	}
	
	
	



	


	public VIEW_TABS getCurrentTab() {
		return currentTab;
	}



	public void setCurrentTab(VIEW_TABS currentTab) {
		this.currentTab = currentTab;
	}

	public String getRemoteIPAddress() {
		return remoteIPAddress;
	}

	public void setRemoteIPAddress(String remoteIPAddress) {
		this.remoteIPAddress = remoteIPAddress;
	}

	public String getSelectedPaymentHistoryId() {
		return selectedPaymentHistoryId;
	}

	public void setSelectedPaymentHistoryId(String id) {
		this.selectedPaymentHistoryId = id;
	}

	public PaymentHistory getSelectedPaymentHistory() {
		return selectedPaymentHistory;
	}

	public void setSelectedPaymentHistory(PaymentHistory selectedPaymentHistory) {
		this.selectedPaymentHistory = selectedPaymentHistory;
	}

	public static ComminsApplicationState getCas() {
		return cas;
	}

	public static void setCas(ComminsApplicationState cas) {
		PaymentsPortletState.cas = cas;
	}



	
}
