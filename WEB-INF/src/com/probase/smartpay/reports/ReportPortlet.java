package com.probase.smartpay.reports;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import jxl.write.WriteException;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;

import smartpay.audittrail.AuditTrail;
import smartpay.entity.ApprovalFlowTransit;
import smartpay.entity.Assessment;
import smartpay.entity.AuthorizePanel;
import smartpay.entity.AuthorizePanelCombination;
import smartpay.entity.BankBranches;
import smartpay.entity.Company;
import smartpay.entity.FeeDescription;
import smartpay.entity.FinancialAmountRestriction;
import smartpay.entity.PaymentHistory;
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
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.probase.smartpay.reports.ReportPortletState.NAVIGATE;
import com.probase.smartpay.reports.ReportPortletState.REPORTING_ACTIONS;
import com.probase.smartpay.commins.ComminsApplicationState;
import com.probase.smartpay.commins.Emailer;
import com.probase.smartpay.commins.ProbaseConstants;
import com.probase.smartpay.commins.Util;
import com.probase.smartpay.commins.Util.DETERMINE_ACCESS;
import com.probase.smartpay.commins.WriteExcel;
import com.sf.primepay.smartpay13.ServiceLocator;
import com.liferay.portal.DuplicateUserScreenNameException;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * Portlet implementation class ReportPortlet
 */
public class ReportPortlet extends MVCPortlet {
 
	private Logger log = Logger.getLogger(ReportPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
	ReportPortletUtil util = ReportPortletUtil.getInstance();
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
		ReportPortletState portletState = 
				ReportPortletState.getInstance(renderRequest, renderResponse);

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
		
		ReportPortletState portletState = ReportPortletState.getInstance(aReq, aRes);
		
		
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
        if(action.equalsIgnoreCase(REPORTING_ACTIONS.CREATE_A_REPORT_STEP_ONE.name()))
        {
        	selectReportType(aReq, aRes, portletState);
        }
        if(action.equalsIgnoreCase(REPORTING_ACTIONS.LOGIN_STEP_TWO.name()))
        {
        	handleLogin(aReq, aRes, portletState);
        }
        if(action.equalsIgnoreCase(REPORTING_ACTIONS.CREATE_A_PAYMENT_REPORT_STEP_TWO.name()))
        {
        	log.info("start date = " + aReq.getParameter("startDate"));
        	log.info("end date = " + aReq.getParameter("endDate"));
        	portletState.setStartDate(aReq.getParameter("startDate"));
        	portletState.setEndDate(aReq.getParameter("endDate"));
        	portletState.setSourceAccount(aReq.getParameter("sourceAccount"));
        	portletState.setReceipientAccount(aReq.getParameter("receipientAccount"));
        	portletState.setSourceSortCode(aReq.getParameter("sourceSortCode"));
        	portletState.setTpin(aReq.getParameter("tpin"));
        	portletState.setDeclarantTpin(aReq.getParameter("declarantTpin"));
        	portletState.setPaymentType(aReq.getParameter("paymentType"));
        	portletState.setPaymentStatus(aReq.getParameter("paymentStatus"));
        	portletState.setAmountLowerLimit(aReq.getParameter("amountLowerLimit"));
        	portletState.setAmountUpperLimit(aReq.getParameter("amountUpperLimit"));
        	portletState.setAssessmentRegNo(aReq.getParameter("assessmentRegNo"));
        	portletState.setAssessmentYear(aReq.getParameter("assessmentYear"));
        	portletState.setPortofEntry(aReq.getParameter("portofEntry"));
        	portletState.setCompanyRegNo(aReq.getParameter("companyRegNo"));
        	portletState.setShowTxnRefNo(aReq.getParameter("showTxnRefNo"));
        	portletState.setShowRectNo(aReq.getParameter("showRectNo"));
        	portletState.setShowSrcAcctNo(aReq.getParameter("showSrcAcctNo"));
        	portletState.setShowRecAcctNo(aReq.getParameter("showRecAcctNo"));
        	portletState.setShowSrcSortCode(aReq.getParameter("showSrcSortCode"));
        	portletState.setShowPayStatus(aReq.getParameter("showPayStatus"));
        	portletState.setShowPayType(aReq.getParameter("showPayType"));
        	portletState.setShowTxnAmt(aReq.getParameter("showTxnAmt"));
        	portletState.setShowAssPaid(aReq.getParameter("showAssPaid"));
        	portletState.setShowPayeeTpin(aReq.getParameter("showPayeeTpin"));
        	portletState.setShowPayeeComp(aReq.getParameter("showPayeeComp"));
        	portletState.setShowPayeeName(aReq.getParameter("showPayeeName"));
        	portletState.setShowDatePaid(aReq.getParameter("showDatePaid"));
        	portletState.setShowClientPaidFor(aReq.getParameter("showClientPaidFor"));
        	portletState.setReportEmailSend(aReq.getParameter("reportEmailSend"));
        	portletState.setDefaultColumnShow(aReq.getParameter("defaultColumnShow"));
        	
        	
        	String hql="";
        	String errorMessage=null;
        	DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        	
        	try{
	        	if(portletState.getStartDate()!=null && portletState.getStartDate().length()>0 && 
	        			portletState.getEndDate()!=null && portletState.getEndDate().length()>0)
	        	{
	        		
	        		Date dd1 = df.parse(portletState.getStartDate());
	        		Date dd2 = df.parse(portletState.getEndDate());
	        		
	        		Timestamp t1 = new Timestamp(dd1.getTime());
	        		Date dd11 = new Date(t1.getTime());
	        		
	        		Timestamp t2 = new Timestamp(dd2.getTime());
	        		Date dd12 = new Date(t2.getTime());
	        		
	        		log.info(df.format(dd11));
	        		log.info(df.format(dd11));
	        		if(t1.getTime()<t2.getTime())
	        		{
	        			hql += " (ph.entryDate >= '" + df.format(dd11) + "' AND  ph.entryDate <= '" + df.format(dd12) + "') ";
	        		}else if(t2.getTime()<t1.getTime())
	        		{
	        			hql += " (ph.entryDate >= '" + df.format(dd12) + "' AND  ph.entryDate <= '" + df.format(dd11) + "') ";
	        		}else {
	        			hql += " (ph.entryDate >= '" + df.format(dd11) + "' AND  ph.entryDate <= '" + df.format(dd12) + "') ";
	        		}
	        		log.info(hql);
	        	}else if(portletState.getStartDate()!=null && portletState.getStartDate().length()>0)
	        	{
	        		Date dd1 = df.parse(portletState.getStartDate());
	        		Timestamp t1 = new Timestamp(dd1.getTime());
	        		Date dd11 = new Date(t1.getTime());
	        		hql +=  " ph.entryDate = ('" + df.format(dd11) + "') ";
	        		log.info(hql);
	        	}else if(portletState.getEndDate()!=null && portletState.getEndDate().length()>0)
	        	{
	        		Date dd1 = df.parse(portletState.getEndDate());
	        		Timestamp t1 = new Timestamp(dd1.getTime());
	        		Date dd11 = new Date(t1.getTime());
	        		hql +=  " ph.entryDate = ('" + df.format(dd11) + "') ";
	        		log.info(hql);
	        	}
        	}catch(ParseException e)
        	{
        		errorMessage = "Invalid date provided for one of the dates";
        		log.info(errorMessage);
        	}
        	
        	if(portletState.getSourceAccount()!=null && portletState.getSourceAccount().length()>0)
        	{
        		hql +=  hql.length()>0 ? " AND ph.sourceAccountNumber = '" + portletState.getSourceAccount()+ "' " :  "ph.sourceAccountNumber = '" + portletState.getSourceAccount()+ "' ";
        	}
        	
        	if(portletState.getSourceSortCode()!=null && portletState.getSourceSortCode().length()>0)
        	{
        		hql +=  hql.length()>0 ? " AND ph.sourceSortCode = '" + portletState.getSourceAccount()+ "' " :  "ph.sourceSortCode = '" + portletState.getSourceSortCode()+ "' ";
        	}
        	
        	if(portletState.getReceipientAccount()!=null && portletState.getReceipientAccount().length()>0)
        	{
        		hql +=  hql.length()>0 ? " AND ph.receipientAccountNumber = '" + portletState.getReceipientAccount()+ "' " :  "ph.receipientAccountNumber = '" + portletState.getReceipientAccount()+ "' ";
        	}
        	
        	if(portletState.getTpin()!=null && portletState.getTpin().length()>0)
        	{
        		hql +=  hql.length()>0 ? " AND ph.assessment.clientTpin = '" + portletState.getTpin()+ "' " :  "ph.assessment.clientTpin = '" + portletState.getTpin()+ "' ";
        	}
        	
        	if(portletState.getAssessmentTpin()!=null && portletState.getAssessmentTpin().length()>0)
        	{
        		hql +=  hql.length()>0 ? " AND ph.assessment.clientTpin = '" + portletState.getAssessmentTpin()+ "' " :  "ph.assessment.clientTpin = '" + portletState.getAssessmentTpin()+ "' ";
        	}
        	
        	if(portletState.getDeclarantTpin()!=null && portletState.getDeclarantTpin().length()>0)
        	{
        		hql +=  hql.length()>0 ? " AND ph.assessment.declarantTpin = '" + portletState.getDeclarantTpin()+ "' " :  "ph.assessment.declarantTpin = '" + portletState.getDeclarantTpin()+ "' ";
        	}
        	
        	if(portletState.getPaymentType()!=null && !portletState.getPaymentType().equals("-1") && portletState.getPaymentType().length()>0)
        	{
        		hql +=  hql.length()>0 ? " AND lower(ph.paymentType) = lower('" + portletState.getPaymentType()+ "') " :  "lower(ph.paymentType) = ('" + portletState.getPaymentType()+ "') ";
        	}
        	
        	if(portletState.getPaymentStatus()!=null && !portletState.getPaymentStatus().equals("-1") && portletState.getPaymentStatus().length()>0)
        	{
        		hql +=  hql.length()>0 ? " AND lower(ph.status) = lower('" + portletState.getPaymentStatus()+ "') " :  "lower(ph.status) = ('" + portletState.getPaymentStatus()+ "') ";
        	}
        	
        	if(portletState.getAmountLowerLimit()!=null && portletState.getAmountLowerLimit().length()>0 && 
        			portletState.getAmountUpperLimit()!=null && portletState.getAmountUpperLimit().length()>0)
        	{
        		hql += hql.length()>0 ? " AND (ph.payableAmount >= " + Double.valueOf(portletState.getAmountLowerLimit()) + " AND ph.payableAmount <= " + Double.valueOf(portletState.getAmountUpperLimit()) + ") " : 
        			" (ph.payableAmount >= " + Double.valueOf(portletState.getAmountLowerLimit()) + " AND ph.payableAmount <=  " + Double.valueOf(portletState.getAmountUpperLimit()) + ") " ;
        	}else if(portletState.getAmountLowerLimit()!=null && portletState.getAmountLowerLimit().length()>0)
        	{
        		hql += hql.length()>0 ? " AND ph.payableAmount = " + Double.valueOf(portletState.getAmountLowerLimit()) :  " ph.payableAmount = " + Double.valueOf(portletState.getAmountLowerLimit());
        	}else if(portletState.getAmountUpperLimit()!=null && portletState.getAmountUpperLimit().length()>0)
        	{
        		hql += hql.length()>0 ? " AND ph.payableAmount = " + Double.valueOf(portletState.getAmountUpperLimit()) :  " ph.payableAmount = " + Double.valueOf(portletState.getAmountUpperLimit());
        	}
        	
        	

        	if(portletState.getAssessmentRegNo()!=null && portletState.getAssessmentRegNo().length()>0)
        	{
        		hql +=  hql.length()>0 ? " AND ph.assessment.registrationNumber = '" + portletState.getAssessmentRegNo()+ "' " :  "ph.assessment.registrationNumber = '" + portletState.getAssessmentRegNo()+ "' ";
        	}
        	
        	if(portletState.getAssessmentYear()!=null && !portletState.getAssessmentYear().equals("-1") && portletState.getAssessmentYear().length()>0)
        	{
        		hql +=  hql.length()>0 ? " AND ph.assessment.assessmentYear = " + portletState.getAssessmentYear()+ " " :  "ph.assessment.assessmentYear = " + portletState.getAssessmentYear()+ " ";
        	}
        	
        	if(portletState.getPortofEntry()!=null && !portletState.getPortofEntry().equals("-1") && portletState.getPortofEntry().length()>0)
        	{
        		hql +=  hql.length()>0 ? " AND (ph.assessment.ports.portCode = '" + portletState.getPortofEntry()+ "' OR ph.assessment.ports.fullName = '" + portletState.getPortofEntry()+ "') " :  "(ph.assessment.ports.portCode = '" + portletState.getPortofEntry()+ "' OR ph.assessment.ports.fullName = '" + portletState.getPortofEntry()+ "') ";
        	}
        	
        	if(portletState.getCompanyRegNo()!=null && portletState.getCompanyRegNo().length()>0)
        	{
        		hql +=  hql.length()>0 ? " AND ph.assessment.tpinInfo.company.companyRCNumber = '" + portletState.getCompanyRegNo()+ "' " :  "ph.assessment.tpinInfo.company.companyRCNumber = '" + portletState.getCompanyRegNo()+ "' ";
        	}
        	
        	
        	if(hql!=null && hql.length()>0)
        	{
        		if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_NON_ADMINISTRATOR_PERSONNEL) || 
        				portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR) ||
        				portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_RETAIL_STAFF))
        		{
        			hql= "select ph from PaymentHistory ph WHERE "+ hql + " AND (ph.assessment.tpinInfo.company.id = " + portletState.getPortalUser().getCompany().getId() + ") " +
        					" ORDER by ph.entryDate DESC";
        		}
        		else
        		{
        			hql= "select ph from PaymentHistory ph WHERE "+ hql + " ORDER by ph.entryDate DESC";
        		}
        	}
        	log.info("HQL for report ==" + hql);
        	Collection<PaymentHistory> payList = portletState.getReportPortletUtil().runPaymentHistoryHQL(hql);
        	if(payList!=null && payList.size()>0)
        	{
        		log.info("phList size===" + payList.size());
        		portletState.setPayList(payList);
        		ArrayList<ArrayList<String>> payHistParent= new ArrayList<ArrayList<String>>();
        		
        		/*****LABEL*****/
        		ArrayList<String> labelRow = new ArrayList<String>();
        		
        		
        		/***Conetent****/
        		
        		if(portletState.getDefaultColumnShow()!=null && portletState.getDefaultColumnShow().equals("1"))
    			{
        				labelRow.add("BANK TRANS ID");
    					labelRow.add("CUSTOMS REF");
                		labelRow.add("RECEIPT NO");
                		labelRow.add("SOURCE ACCT NO");
                		if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_NON_ADMINISTRATOR_PERSONNEL) || 
        						portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR) || 
        						portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_RETAIL_STAFF) || 
        						portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_SYSTEM_SUPER_ADMINISTRATOR))
        				{
                			
        				}else
        				{
        					labelRow.add("RECIPIENT ACCT NO");
        				}
                		//labelRow.add("SOURCE SORT CODE");
                		labelRow.add("PAYMENT STATUS");
                		labelRow.add("PAYMENT TYPE");
                		labelRow.add("AMOUNT PAID");
                		labelRow.add("ASSESSMENT NO");
                		labelRow.add("PAYEE TPIN");
                		labelRow.add("PAYEE COMPANY");
                		labelRow.add("PAYEE NAME");
                		labelRow.add("DATE PAID");
                		labelRow.add("CLIENT PAID FOR");
    			}else
    			{
    				labelRow.add("BANK TRANS ID");
    				if(portletState.getShowTxnRefNo()!=null && portletState.getShowTxnRefNo().equalsIgnoreCase("TXNREFNO"))
                    	labelRow.add("CUSTOMS REF");
                	if(portletState.getShowRecAcctNo()!=null && portletState.getShowRecAcctNo().equalsIgnoreCase("RECTNO"))
                		labelRow.add("RECEIPT NO");
                	if(portletState.getShowSrcAcctNo()!=null && portletState.getShowSrcAcctNo().equalsIgnoreCase("SRCACCTNO"))
                		labelRow.add("SOURCE ACCT NO");
                	if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_NON_ADMINISTRATOR_PERSONNEL) || 
    						portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR) || 
    						portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_RETAIL_STAFF) || 
    						portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_SYSTEM_SUPER_ADMINISTRATOR))
    				{
    				
    				}else
    				{
	                	if(portletState.getShowRecAcctNo()!=null && portletState.getShowRecAcctNo().equalsIgnoreCase("RECACCTNO"))
	                		labelRow.add("RECIPIENT ACCT NO");
    				}
                	if(portletState.getShowSrcSortCode()!=null && portletState.getShowSrcSortCode().equalsIgnoreCase("SRCSORTCODE"))
                		//labelRow.add("SOURCE SORT CODE");
                	if(portletState.getShowPayStatus()!=null && portletState.getShowPayStatus().equalsIgnoreCase("PAYSTATUS"))
                		labelRow.add("PAYMENT STATUS");
                	if(portletState.getShowPayType()!=null && portletState.getShowPayType().equalsIgnoreCase("PAYTYPE"))
                		labelRow.add("PAYMENT TYPE");
                	if(portletState.getShowTxnAmt()!=null && portletState.getShowTxnAmt().equalsIgnoreCase("TXNAMT"))
                		labelRow.add("AMOUNT PAID");
                	if(portletState.getShowAssPaid()!=null && portletState.getShowAssPaid().equalsIgnoreCase("ASSPAID"))
                		labelRow.add("ASSESSMENT NO");
                	if(portletState.getShowPayeeTpin()!=null && portletState.getShowPayeeTpin().equalsIgnoreCase("PAYEETPIN"))
                		labelRow.add("PAYEE TPIN");
                	if(portletState.getShowPayeeComp()!=null && portletState.getShowPayeeComp().equalsIgnoreCase("PAYEECOMP"))
                		labelRow.add("PAYEE COMPANY");
                	if(portletState.getShowPayeeName()!=null && portletState.getShowPayeeName().equalsIgnoreCase("PAYEENAME"))
                		labelRow.add("PAYEE NAME");
                	if(portletState.getShowDatePaid()!=null && portletState.getShowDatePaid().equalsIgnoreCase("DATEPAID"))
                		labelRow.add("DATE PAID");
                	if(portletState.getShowClientPaidFor()!=null && portletState.getShowClientPaidFor().equalsIgnoreCase("CLIENTPAIDFOR"))
                		labelRow.add("CLIENT PAID FOR");
    			}
        		
        		for(Iterator<PaymentHistory> it = payList.iterator(); it.hasNext();)
        		{
        			PaymentHistory payHist = it.next();
        			ArrayList<String> payHistoryRow = new ArrayList<String>();
        			if(portletState.getDefaultColumnShow()!=null && portletState.getDefaultColumnShow().equals("1"))
        			{
        				payHistoryRow.add(payHist.getRequestMessageId()!=null && payHist.getRequestMessageId().length()>0 ? payHist.getRequestMessageId() : "N/A");
        				payHistoryRow.add(payHist.getTransactionReferenceId()!=null && payHist.getTransactionReferenceId().length()>0 ? payHist.getTransactionReferenceId() : "N/A");
        				payHistoryRow.add(payHist.getReceiptNumber());
        				payHistoryRow.add(payHist.getSourceAccountNumber());
        				if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_NON_ADMINISTRATOR_PERSONNEL) || 
        						portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR) || 
        						portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_RETAIL_STAFF) || 
        						portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_SYSTEM_SUPER_ADMINISTRATOR))
        				{
        					
        				}else
        				{
        					payHistoryRow.add(payHist.getReceipientAccountNumber());
        				}
        				//payHistoryRow.add(payHist.getSourceSortCode());
        				payHistoryRow.add(payHist.getStatus().getValue());
        				payHistoryRow.add(payHist.getPaymentType().getValue());
        				payHistoryRow.add(Double.toString(payHist.getPayableAmount()));
        				payHistoryRow.add(payHist.getAssessment()!=null ? 
        						payHist.getAssessment().getRegistrationNumber() : payHist.getDomTax().getPaymentRegNo());
        				payHistoryRow.add(payHist.getAssessment()!=null ? 
        						payHist.getAssessment().getTpinInfo().getTpin() : payHist.getDomTax().getTpinInfo().getTpin());
        				payHistoryRow.add(payHist.getPortalUser().getCompany().getCompanyName() + " - " + payHist.getPortalUser().getCompany().getCompanyRCNumber());
        				payHistoryRow.add(payHist.getPortalUser().getFirstName() + " " + payHist.getPortalUser().getLastName());
        				payHistoryRow.add(payHist.getDateofTransaction().toString());
        				payHistoryRow.add(payHist.getAssessment()!=null ? 
        						payHist.getAssessment().getClientTpin() : "N/A");
        			}else
        			{
        				
        				payHistoryRow.add(payHist.getRequestMessageId()!=null && payHist.getRequestMessageId().length()>0 ? payHist.getRequestMessageId() : "N/A");
        				if(portletState.getShowTxnRefNo()!=null && portletState.getShowTxnRefNo().equalsIgnoreCase("TXNREFNO"))
    	                	payHistoryRow.add(payHist.getTransactionReferenceId());
                    	if(portletState.getShowRecAcctNo()!=null && portletState.getShowRecAcctNo().equalsIgnoreCase("RECTNO"))
                    		payHistoryRow.add(payHist.getReceiptNumber());
                    	if(portletState.getShowSrcAcctNo()!=null && portletState.getShowSrcAcctNo().equalsIgnoreCase("SRCACCTNO"))
                    		payHistoryRow.add(payHist.getSourceAccountNumber());
                    	if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_NON_ADMINISTRATOR_PERSONNEL) || 
        						portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR) || 
        						portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_RETAIL_STAFF) || 
        						portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_SYSTEM_SUPER_ADMINISTRATOR))
        				{
                    		
        				}else
        				{
	                    	if(portletState.getShowRecAcctNo()!=null && portletState.getShowRecAcctNo().equalsIgnoreCase("RECACCTNO"))
	                    		payHistoryRow.add(payHist.getReceipientAccountNumber());
        				}
                    	if(portletState.getShowSrcSortCode()!=null && portletState.getShowSrcSortCode().equalsIgnoreCase("SRCSORTCODE"))
                    		//payHistoryRow.add(payHist.getSourceSortCode());
                    	if(portletState.getShowPayStatus()!=null && portletState.getShowPayStatus().equalsIgnoreCase("PAYSTATUS"))
                    		payHistoryRow.add(payHist.getStatus().getValue());
                    	if(portletState.getShowPayType()!=null && portletState.getShowPayType().equalsIgnoreCase("PAYTYPE"))
                    		payHistoryRow.add(payHist.getPaymentType().getValue());
                    	if(portletState.getShowTxnAmt()!=null && portletState.getShowTxnAmt().equalsIgnoreCase("TXNAMT"))
                    		payHistoryRow.add(Double.toString(payHist.getPayableAmount()));
                    	if(portletState.getShowAssPaid()!=null && portletState.getShowAssPaid().equalsIgnoreCase("ASSPAID"))
                    		payHistoryRow.add(payHist.getAssessment()!=null ? 
                    				payHist.getAssessment().getRegistrationNumber() : payHist.getDomTax().getPaymentRegNo());
                    	if(portletState.getShowPayeeTpin()!=null && portletState.getShowPayeeTpin().equalsIgnoreCase("PAYEETPIN"))
                    		payHistoryRow.add(payHist.getAssessment()!=null ? 
                    				payHist.getAssessment().getTpinInfo().getTpin() : payHist.getDomTax().getTpinInfo().getTpin());
                    	if(portletState.getShowPayeeComp()!=null && portletState.getShowPayeeComp().equalsIgnoreCase("PAYEECOMP"))
                    		payHistoryRow.add(payHist.getPortalUser().getCompany().getCompanyName() + " - " + payHist.getPortalUser().getCompany().getCompanyRCNumber());
                    	if(portletState.getShowPayeeName()!=null && portletState.getShowPayeeName().equalsIgnoreCase("PAYEENAME"))
                    		payHistoryRow.add(payHist.getPortalUser().getFirstName() + payHist.getPortalUser().getLastName());
                    	if(portletState.getShowDatePaid()!=null && portletState.getShowDatePaid().equalsIgnoreCase("DATEPAID"))
                    		payHistoryRow.add(payHist.getDateofTransaction().toString());
                    	if(portletState.getShowClientPaidFor()!=null && portletState.getShowClientPaidFor().equalsIgnoreCase("CLIENTPAIDFOR"))
                    		payHistoryRow.add(payHist.getAssessment()!=null ? 
                    				payHist.getAssessment().getClientTpin() : "N/A");
        			}
        			payHistParent.add(payHistoryRow);
        			
        			
        			log.info("payHis id = " + payHist.getId());
        		}
        		
        		
        		log.info("-----------------user dir is " + System.getProperty("user.dir"));
        		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-S");
        		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        		Timestamp tstamp = new Timestamp((new Date()).getTime());
//                String userDirFile = System.getProperty("user.dir") + File.separator + "Payments" + File.separator + 
//                		"plr_for_" + portletState.getPortalUser().getCompany().
//                		getCompanyRCNumber().toLowerCase().replace(" ", "_") + "_on_" + sdf.format(new Date(tstamp.getTime()));
        		String userDirFile = aReq.getScheme() + "://"
            			+ aReq.getServerName() + ":" + aReq.getServerPort()
            			+ File.separator + "resources" + File.separator + "Payments" + File.separator + 
                		"plr_for_" + (portletState.getPortalUser().getCompany()!=null ? 
                				portletState.getPortalUser().getCompany().
                		getCompanyName().toLowerCase().replace(" ", "_") : "all_companies") + "_on_" + sdf.format(new Date(tstamp.getTime()));
        		
        		String userDir = System.getProperty("user.dir");
        		String sep = File.separator;
        		String filname = "plr_for_" + (portletState.getPortalUser().getCompany()!=null ? 
        				portletState.getPortalUser().getCompany().
        		getCompanyName().toLowerCase().replace(" ", "_") : "all_companies") + "_on_" + sdf.format(new Date(tstamp.getTime())) + ".xls";
        		String userDirFile1 = userDir +sep+"ReportEngine"+sep+"Reports" + File.separator + "Payments" + File.separator + filname;
                		
        				
//        				"/resources" + File.separator + "Payments" + File.separator + 
//                		"plr_for_" + portletState.getPortalUser().getCompany().
//                		getCompanyRCNumber().toLowerCase().replace(" ", "_") + "_on_" + sdf.format(new Date(tstamp.getTime()));
                log.info("File: " + userDirFile);
        		WriteExcel writeExcel = new WriteExcel(userDirFile1, 
        				"Payment Listing Report - Generated " + sdf1.format(new Date(tstamp.getTime())), 
        				labelRow, payHistParent );
        		try {
					writeExcel.write();
					portletState.addSuccess(aReq, "Report Generated successfully. To download your report, right-click on the link below and " +
							"click on SAVE LINK AS option.<br>Download Report: " +
							"<a href='javascript:downloadReport()'>Payment Listings</a>", portletState);
					aRes.setRenderParameter("jspPage", "/html/reportportlet/filterresultsforpayments.jsp");
					portletState.setFilName(filname);
				} catch (WriteException e) {
					// TODO Auto-generated catch block
					portletState.addError(aReq, "We experienced errors generating your report. Please try again.", portletState);
					aRes.setRenderParameter("jspPage", "/html/reportportlet/filterresultsforpayments.jsp");
				}
        	}
        	else
        	{
        		portletState.addError(aReq, "There are currently no payments matching the criteria you provided. Please refine your filter criteria and run the report again.", portletState);
				aRes.setRenderParameter("jspPage", "/html/reportportlet/filterresultsforpayments.jsp");
        	}
        	
        	
        }
		
	}

	
	private void handleLogin(ActionRequest aReq, ActionResponse aRes,
			ReportPortletState portletState) {
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
				PortalUser pu = portletState.getReportPortletUtil().getPortalUserByEmailAddress(email2);
				if(pu.getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
				{
					if(portletState.getPortalUserCRUDRights().getCudInitiatorRights().equals(Boolean.TRUE))
					{
						if(pu!=null && pu.getStatus().equals(PortalUserStatusConstants.PORTAL_USER_STATUS_ACTIVE))
						{
							PortalUserCRUDRights pcrs = portletState.getReportPortletUtil().getPortalUserCRUDRightsByPortalUser(pu);
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

	private void selectReportType(ActionRequest aReq, ActionResponse aRes,
			ReportPortletState portletState) {
		// TODO Auto-generated method stub
		String reportType = aReq.getParameter("reportSelected");
		if(reportType!=null && !reportType.equals("-1"))
		{
			portletState.setSelectedReportType(reportType);
			if(reportType.equalsIgnoreCase(PaymentHistory.class.getSimpleName()))
			{
				portletState.setPortsList(portletState.getReportPortletUtil().getAllPortListing());
				aRes.setRenderParameter("jspPage", "/html/reportportlet/filterresultsforpayments.jsp");
			}
			else if(reportType.equalsIgnoreCase(Assessment.class.getSimpleName()))
			{
				portletState.setPortsList(portletState.getReportPortletUtil().getAllPortListing());
				aRes.setRenderParameter("jspPage", "/html/reportportlet/filterresultsforassessments.jsp");
			}
		}else
		{
			
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
