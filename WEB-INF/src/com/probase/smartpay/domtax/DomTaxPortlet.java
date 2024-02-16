package com.probase.smartpay.domtax;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

import smartpay.audittrail.AuditTrail;
import smartpay.entity.Assessment;
import smartpay.entity.AuthorizePanelCombination;
import smartpay.entity.DomTax;
import smartpay.entity.PaymentHistory;
import smartpay.entity.Tokens;
import smartpay.entity.WorkFlow;
import smartpay.entity.WorkFlowAssessment;
import smartpay.entity.enumerations.CompanyTypeConstants;
import smartpay.entity.enumerations.PanelTypeConstants;
import smartpay.entity.enumerations.PaymentHistoryConstants;
import smartpay.entity.enumerations.PaymentTypeConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.entity.enumerations.WorkFlowConstants;
import smartpay.service.SwpService;

import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.service.ServiceContext;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.probase.smartpay.admin.taxassessmentmanagement.TaxAssessmentManagementPortletState;
import com.probase.smartpay.admin.taxassessmentmanagement.TaxAssessmentManagementPortletState.TAX_ASSESSMENT_ACTION;
import com.probase.smartpay.commins.BalanceInquiry;
import com.probase.smartpay.commins.ComminsApplicationState;
import com.probase.smartpay.commins.Mailer;
import com.probase.smartpay.commins.NotifyZRAPaymentResponse;
import com.probase.smartpay.commins.SendMail;
import com.probase.smartpay.commins.SendSms;
import com.probase.smartpay.commins.Util;
import com.probase.smartpay.domtax.DomTaxPortletState;
import com.probase.smartpay.domtax.DomTaxPortletUtil;
import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Portlet implementation class DomTaxPortlet
 */
public class DomTaxPortlet extends MVCPortlet {
 
	private Logger log = Logger.getLogger(DomTaxPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
	DomTaxPortletUtil util = DomTaxPortletUtil.getInstance();
	ServiceContext serviceContext = new ServiceContext();
	
	
	@Override
	public void init(PortletConfig config) throws PortletException {
		// TODO Auto-generated method stub
		log.info("Administrative portlet init called...");		
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
		log.info("Administrative render called...");	
		PortletSession ps = renderRequest.getPortletSession();
		DomTaxPortletState portletState = 
				DomTaxPortletState.getInstance(renderRequest, renderResponse);

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
		String action = aReq.getParameter("action");
		log.info("action == " + action);
		DomTaxPortletState portletState = DomTaxPortletState.getInstance(aReq, aRes);
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
        
        
        if(action.equalsIgnoreCase(DomTaxPortletState.DOM_TAX_ACTION.HANDLE_DOM_TAX_LISTING.name()))
        {
        	log.info("handle create a fee description post action");
        	handleDomTaxListingAction(aReq, aRes, portletState);
        }
	}

	
	
	private void handleDomTaxListingAction(ActionRequest aReq,
			ActionResponse aRes, DomTaxPortletState portletState) {
		// TODO Auto-generated method stub
		log.info("handleDomTaxAction");
		String action = aReq.getParameter("selectedDomTaxAction");
		log.info("action === " + action);
		String selectedDomTax = aReq.getParameter("selectedDomTax");
		log.info("selectedDomTax  = " + selectedDomTax);
		
		
		if(action!=null && (action.equalsIgnoreCase("getBalance")))
		{
			ComminsApplicationState cas = portletState.getCas();
			BalanceInquiry balanceInquiry = null;
			if(cas.getDemoModeBalance().equals(Boolean.TRUE))
			{
				balanceInquiry = cas.getBalanceInquiry(portletState.getPortalUser(), swpService);
			}else
			{
				try {
					balanceInquiry = Util.getBalanceInquiry(portletState.getApplicationName().getValue(), 
							"SMARTPAY:ZM:REVENUE:BALANCE:REQUEST", "TYPE", portletState.getPortalUser().getCompany().getAccountNumber(), 
							"ZMW");
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(balanceInquiry!=null)
			{
				portletState.setBalanceInquiry(balanceInquiry);
				aRes.setRenderParameter("jspPage", "/html/domtaxportlet/domtaxlisting.jsp");
			}else
			{
				portletState.setBalanceInquiry(null);
				aRes.setRenderParameter("jspPage", "/html/domtaxportlet/domtaxlisting.jsp");
				portletState.addError(aReq, "We are experiencing problems getting your bank balance. E189301 Error Code. Contact the bank for more details if this problems persist.", portletState);
			}
		}else if(action!=null && (action.equalsIgnoreCase("getBalance1")))
		{
			ComminsApplicationState cas = portletState.getCas();
			BalanceInquiry balanceInquiry = null;
			if(cas.getDemoModeBalance().equals(Boolean.TRUE))
			{
				balanceInquiry = cas.getBalanceInquiry(portletState.getPortalUser(), swpService);
			}else
			{
				try {
					balanceInquiry = Util.getBalanceInquiry(portletState.getApplicationName().getValue(), 
							"SMARTPAY:ZM:REVENUE:BALANCE:REQUEST", "TYPE", portletState.getPortalUser().getCompany().getAccountNumber(), 
							"ZMW");
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(balanceInquiry!=null)
			{
				portletState.setBalanceInquiry(balanceInquiry);
				aRes.setRenderParameter("jspPage", "/html/domtaxportlet/domtaxlisting.jsp");
			}else
			{
				portletState.setBalanceInquiry(null);
				aRes.setRenderParameter("jspPage", "/html/domtaxportlet/domtaxlisting.jsp");
				portletState.addError(aReq, "We are experiencing problems getting your bank balance. E189301 Error Code. Contact the bank for more details if this problems persist.", portletState);
			}
		}
		else {
			if(selectedDomTax!=null && selectedDomTax.length()>0)
			{
				log.info("selectedDomTax  = " + selectedDomTax);
				
				if(action!=null && (action.equalsIgnoreCase("pay")))
				{
					handleNewPay(aReq, aRes, portletState, selectedDomTax);
					portletState.setAllWorkFlowDomTax(portletState.getDomTaxPortletUtil().
							getDomTaxPaidByCompany(portletState.getPortalUser().getCompany()));
					//handlePay(aReq, aRes, portletState);
					
				}
				else if(action!=null && (action.equalsIgnoreCase("initiatePayment")))
				{
					handleInitiatePayment(aReq, aRes, portletState);
					portletState.setAllWorkFlowDomTax(portletState.getDomTaxPortletUtil().getDomTaxPaidByCompany(portletState.getPortalUser().getCompany()));
				}
			}else
			{
				if(action!=null && (!action.equalsIgnoreCase("getBalance")))
				{
					portletState.addError(aReq, "Select at least a domestic tax before clicking on a button", portletState);
					aRes.setRenderParameter("jspPage", "/html/domtaxportlet/domtaxlisting.jsp");
				}
				
				
			}
		}
		
	}
	
	
	
	private void handleInitiatePayment(ActionRequest aReq, ActionResponse aRes,
			DomTaxPortletState portletState) {
		// TODO Auto-generated method stub
		log.info("handleTaxAssessmentAction");
		String action_ = aReq.getParameter("selectedAssessmentAction");
		log.info("action === " + action_);
		String selectedAssessment1 = aReq.getParameter("selectedDomTax");
		log.info("selectedAssessment = " + selectedAssessment1);
		//log.info("selected assessments = " + aReq.getParameter("selectedAssessmentsClicked").trim());
		String[] selectedAssessment = {selectedAssessment1};
		log.info("break");
		ArrayList<String> allList = new ArrayList<String>();
		
		
		if(portletState.getPortalUser().getCompany().getMandatePanelsOn()!=null && portletState.getPortalUser().getCompany().getMandatePanelsOn().equals(Boolean.TRUE))
		{
			doHandleInitiatePaymentToWorkFlow(aReq, aRes, portletState, selectedAssessment, allList,  action_);
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/domtaxportlet/domtaxlisting.jsp");
			if(portletState.getPortalUser().getCompany().getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY))
			{
				portletState.addError(aReq, "You cannot add the selected domestic taxes to your companys workflow tray as workflow capabilities have been turned off for your company. You may rather proceed to pay directly by clicking " +
						"on the -Proceed to Pay for Selected Assessments- button", portletState);
			}else
			{
				portletState.addError(aReq, "You cannot add the selected domestic taxes to your companys workflow tray. You may rather proceed to pay directly by clicking " +
					"on the -Proceed to Pay for Selected Assessments- button", portletState);
			}
		}
	}

	private void doHandleInitiatePaymentToWorkFlow(ActionRequest aReq,
			ActionResponse aRes, DomTaxPortletState portletState,
			String[] selectedAssessment, ArrayList<String> allList,
			String action_) {
		// TODO Auto-generated method stub
		Double amount = 0.00;
		for(int c=0; c<selectedAssessment.length; c++)
		{
			Collection<DomTax> allDomTaxList = portletState.getAllDomTaxListing();
			log.info("111allAssessmentList size = " + (allDomTaxList!=null ? allDomTaxList.size() : "null"));
			
			for(Iterator<DomTax> iterAss = allDomTaxList.iterator(); iterAss.hasNext();)
			{
				
				DomTax domTax = iterAss.next();
				if(domTax.getPaymentRegNo()!=null && domTax.getPaymentRegNo().equalsIgnoreCase(selectedAssessment[c]))
				{
					log.info("1111assessment = " + domTax==null ? "N/A" : domTax.getAmountPayable());
					log.info("111selectedAssessment[c] = " + selectedAssessment[c]==null ? "N/A" : selectedAssessment[c]);
					log.info("111assessment.getRegistrationNumber() = " + domTax.getPaymentRegNo()==null ? "N/A" : domTax.getPaymentRegNo());
					log.info("112>>>>" + domTax.getPaymentRegNo());
					log.info("221>>>>" + domTax.getPaymentRegNo());
					amount = amount + domTax.getAmountPayable();
				}
			}
		}
		
		
		Collection<AuthorizePanelCombination> apcList = Util.getAuthorizePanelOfInitiatorTypeForPortalUser(swpService, amount, SmartPayConstants.AUTHORIZE_PANEL_COMBINATION_STATUS_ACTIVE, 
				PanelTypeConstants.AUTHORIZE_PANEL_TYPE_INITIATOR, portletState.getPortalUser());
		Collection<AuthorizePanelCombination> apc = portletState.getDomTaxPortletUtil().getNextInWorkFlow(portletState, amount);
		
		log.info(apcList!=null ? apcList.size() : "null");
		log.info(apc!=null ? apc.size() : "null");
		boolean success =false;
		int count = 0;
		List<String> assessmentlistString =new ArrayList<String>(); 
		if(apcList!=null && apcList.size()>0 && apc!=null && apc.size()>0)
		{
			AuthorizePanelCombination apc1 = apc.iterator().next();
			WorkFlow workFlow = new WorkFlow();
			workFlow.setDateLastModified(new Timestamp((new Date()).getTime()));
			workFlow.setStatus(WorkFlowConstants.WORKFLOW_STATUS_CREATED);
			workFlow.setWorkFlowInitiatorId(portletState.getPortalUser().getId());
			workFlow.setWorkFlowReceipientPositionId(apc1.getPosition());
			workFlow.setWorkFlowReceipientPanelId(apc1.getAuthorizePanel().getId());
			//workFlow.setWorkFlowReceipientId(apc.getPortalUser().getId());
			//workFlow.setToken(token);
			String refId = RandomStringUtils.random(8, false, true);
			workFlow.setReferenceId(refId);
			workFlow = (WorkFlow)swpService.createNewRecord(workFlow);
			if(workFlow!=null)
			{
				handleAudit("Create WorkFlow", "Create New WorkFlow with Id " + workFlow.getId(), 
						new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), 
						portletState.getPortalUser().getUserId());
				

				String token = RandomStringUtils.random(6, false, true) + "" + workFlow.getId();
				Tokens token1 = new Tokens();
				token1.setCreatedByPortalUserId(portletState.getPortalUser().getId());
				//token1.setCreatedForPortalUserId(apc.getPortalUser().getId());
				token1.setCreatedForAPCId(apc1.getId());
				token1.setCreatedForAPCPosition(apc1.getPosition());
				token1.setDateCreated(new Timestamp((new Date()).getTime()));
				token1.setTokenValue(token);
				token1.setWorkFlow(workFlow);
				token1.setIsValid(Boolean.TRUE);
				token1 = (Tokens)swpService.createNewRecord(token1);
				if(token1!=null)
				{
					for(int c=0; c<selectedAssessment.length; c++)
					{
						success =false;
						Collection<DomTax> allDomTaxList = portletState.getAllDomTaxListing();
						log.info("allDomTaxList size = " + allDomTaxList.size());
						
						for(Iterator<DomTax> iterAss = allDomTaxList.iterator(); iterAss.hasNext();)
						{
							
							DomTax domTax = iterAss.next();
							
							if(domTax.getPaymentRegNo()!=null && domTax.getPaymentRegNo().equalsIgnoreCase(selectedAssessment[c]))
							{
								log.info("assessment = " + domTax==null ? "N/A" : domTax.getAmountPayable());
								log.info("selectedAssessment[c] = " + selectedAssessment[c]==null ? "N/A" : selectedAssessment[c]);
								log.info("assessment.getRegistrationNumber() = " + domTax.getPaymentRegNo()==null ? "N/A" : domTax.getPaymentRegNo());
								log.info("1>>>>" + domTax.getPaymentRegNo());
								log.info("2>>>>" + domTax.getPaymentRegNo());
							

								log.info("3>>>>both are equal");
								
								DomTax domTaxInDb = portletState.getDomTaxPortletUtil().getDomTaxByPRNAndPortalUser(domTax.getPaymentRegNo(), portletState.getPortalUser().getCompany().getId());
								if(domTaxInDb!=null)
								{
									//UPATE ASSESSEMENT TO NEW SELECTED ASSESSMENT
									//domTax = new Util().updateAssessments(assessment, assessmentInDb, portletState.getPortalUser());
									//swpService.updateRecord(assessment);
									handleAudit("UPDATE DOMTAX", "UPDATE DOMTAX WITH ID " + domTax.getId(), 
											new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
									success = true;
									WorkFlowAssessment wfa= new Util().addThisDomToWorkFlow(domTax, workFlow, swpService);
									if(wfa!=null)
										handleAudit("Create DomTax", "Create New DomTax with Id " + wfa.getId(), 
											new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), 
											portletState.getPortalUser().getUserId());
									
								}else
								{
									//CREATE NEW ASSESSMENT SINCE NONE EXISTS
									domTaxInDb = (DomTax)swpService.createNewRecord(domTaxInDb);
									log.info("5>>>>create new domtax" + domTaxInDb.getId());
									
									handleAudit("CREATE DOMTAX", "CREATE DOMTAX WITH ID " + domTaxInDb.getId(), 
											new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
									WorkFlowAssessment wfa= new Util().addThisDomToWorkFlow(domTaxInDb, workFlow, swpService);

									if(wfa!=null)
									{
										success = true;
										handleAudit("Create DomTax", "Create New DomTax with Id " + wfa.getId(), 
											new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), 
											portletState.getPortalUser().getUserId());
									}
								}
								
								if(success)
								{
									count++;
									assessmentlistString.add(domTaxInDb.getPaymentRegNo());
								}
							}
						}
					}
				}
				
				if(count>0)
				{
					Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
							portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
									Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, portletState.getSendingEmailUsername().getValue());
					
					for(Iterator<AuthorizePanelCombination> apc11 = apc.iterator(); apc11.hasNext();)
					{
						AuthorizePanelCombination apc41 = apc11.next();
						SendMail sm = emailer.emailWorkFlow(apc41.getPortalUser().getEmailAddress(), 
							portletState.getPortalUser().getCompany().getCompanyName(),
							assessmentlistString,
							token1.getTokenValue(), 
							portletState.getSystemUrl().getValue(),
							apc41.getPortalUser().getFirstName(), 
							apc41.getPortalUser().getLastName(), 
							"Work Flow Item - Request for Approval of Payment for Domestic Tax - ", portletState.getApplicationName().getValue());
					
						String message = "eTax Approval Request!\nTxn Id:" + workFlow.getReferenceId() + "\nToken:" + token1.getTokenValue() +
								"\nVisit " + portletState.getSystemUrl().getValue() + " to approve these requests";
						SendSms sendSms = new SendSms(apc41.getPortalUser().getMobileNumber(), message, 
								portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
					
					
					
						
					
						message ="";
					
						if(apc41.getPortalUser().getSecondAlternativeEmailAddress()!=null && apc41.getPortalUser().getSecondAlternativeEmailAddress().length()>0)
						{
							SendMail sm2 = emailer.emailWorkFlow(apc41.getPortalUser().getSecondAlternativeEmailAddress(), 
								portletState.getPortalUser().getCompany().getCompanyName(),
								assessmentlistString,
								token1.getTokenValue(), 
								portletState.getSystemUrl().getValue(),
								apc41.getPortalUser().getFirstName(), 
								apc41.getPortalUser().getLastName(), 
								"Work Flow Item - Request for Approval of Payment for Domestic Tax - ", portletState.getApplicationName().getValue());
							
							message = "eTax Approval Request!\nTxn Id:" + workFlow.getReferenceId() + "\nToken:" + token1.getTokenValue() +
									"\nVisit " + portletState.getSystemUrl().getValue() + " to approve these requests";
							
						
						}
						sendSms = new SendSms(apc41.getPortalUser().getSecondAlternativeMobileNumber(), message, 
									portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
						if(apc41.getPortalUser().getFirstAlternativeEmailAddress()!=null && apc41.getPortalUser().getFirstAlternativeEmailAddress().length()>0)
						{
							SendMail sm3 = emailer.emailWorkFlow(apc41.getPortalUser().getFirstAlternativeEmailAddress(), 
								portletState.getPortalUser().getCompany().getCompanyName(),
								assessmentlistString,
								token1.getTokenValue(), 
								portletState.getSystemUrl().getValue(),
								apc41.getPortalUser().getFirstName(), 
								apc41.getPortalUser().getLastName(), 
								"Work Flow Item - Request for Approval of Payment for Domestic Tax - ", portletState.getApplicationName().getValue());
							
							message = "eTax Approval Request!\nTxn Id:" + workFlow.getReferenceId() + "\nToken:" + token1.getTokenValue() +
									"Visit " + portletState.getSystemUrl().getValue() + " to approve these requests";
							sendSms = new SendSms(apc41.getPortalUser().getFirstAlternativeMobileNumber(), message, 
									portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
						}
					}
				}
			}
			
			
			portletState.loadWorkFlowAssessmentIds(portletState);
			
			if(assessmentlistString!=null && assessmentlistString.size()>0)
			{
				String list="";
				for(int c=0; c<assessmentlistString.size(); c++)
				{
					list = list + "Domestic Tax Payment Reg No: " + assessmentlistString.get(c) + "<br>";
				}
				aRes.setRenderParameter("jspPage", "/html/domtaxportlet/domtaxlisting.jsp");
				portletState.addSuccess(aReq, "The following domestic tax payments have been " +
						"added to the approval workflow.<br>" + list, portletState);
				
				
			}else
			{
				aRes.setRenderParameter("jspPage", "/html/domtaxportlet/domtaxlisting.jsp");
				portletState.addError(aReq, "Adding domestic taxes to the approval workflow failed. You do not belong to any valid authorization " +
							"mandate panels for the domestic tax amounts you wish to pay for. Request the bank or your company administrator for appropriate rights", portletState);
				
			}
			
			
		}else
		{ 
			aRes.setRenderParameter("jspPage", "/html/domtaxportlet/domtaxlisting.jsp");
			portletState.addError(aReq, "You cannot add this domestic tax to your company's workflow. Request your " +
					"company administrator to add you to an authorization panel of initiator type " +
					"having enough funds to cover the total amount of the domestic taxes you selected", portletState);
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

	private void handleNewPay(ActionRequest aReq, ActionResponse aRes,
			DomTaxPortletState portletState, String selectedDomTax) {
		// TODO Auto-generated method stub
		ComminsApplicationState cas = DomTaxPortletState.getCas();
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		ArrayList<DomTax> d = new ArrayList<DomTax>();
		d.add(portletState.getDomTaxPortletUtil().getDomTaxByPRN(selectedDomTax));
		
		if(portletState.getPortalUser().getCompany().getMandatePanelsOn()!=null && 
				portletState.getPortalUser().getCompany().getMandatePanelsOn().equals(Boolean.TRUE))
		{
			aRes.setRenderParameter("jspPage", "/html/domtaxportlet/domtaxlisting.jsp");
			portletState.addError(aReq, "You can not pay for the selected domestic taxes" +
					" as your company's setup requires payments to pass through an approval workflow. " +
					"Click on the - Add Selected Domestic Taxes to Workflow Tray - button to do this.", portletState);
		}else
		{
			try
			{
				List<PaymentHistory> hashMap = Util.payDirectForDomTax(cas, swpService, 
						portletState.getPortalUser().getCompany(), 
						selectedDomTax, portletState.getPortalUser(), portletState.getRemoteIPAddress(), 
						portletState.getSettingsZRAAccount(), portletState.getSettingsZRASortCode(), 
						portletState.getPlatformBank().getValue(), portletState.getBankName().getValue(), 
						emailer, 
						portletState.getApplicationName().getValue(), 
						portletState.getProxyUsername().getValue(), portletState.getProxyPassword().getValue(), 
						portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue(), 
						portletState.getBankPaymentWebServiceUrl().getValue(), 
						d);
					
	
				log.info("DomTaxPortlet....");
	
				PaymentHistory cPy = null;
				if(hashMap!=null && hashMap.size()>0)
				{
					log.info("hashMap.size==...." + hashMap.size());
					for(Iterator<PaymentHistory> py = hashMap.iterator(); py.hasNext();)
					{
						PaymentHistory py1 = py.next();
						if(py1.getPaymentType().equals(PaymentTypeConstants.PAYMENTTYPE_DOM))
						{
							cPy= py1;
						}
					}
					
					
					BalanceInquiry balanceInquiry = null;
					if(portletState.getCas()!=null && portletState.getCas().getDemoModeBalance().equals(Boolean.TRUE))
					{
						
						balanceInquiry = cas.getBalanceInquiry(portletState.getPortalUser(), swpService);
						log.info("1balanceInquiry==...." + balanceInquiry.getAvailableBalance());
					}else
					{
						try {
							balanceInquiry = Util.getBalanceInquiry(portletState.getApplicationName().getValue(), 
									"SMARTPAY:ZM:REVENUE:BALANCE:REQUEST", "TYPE", portletState.getPortalUser().getCompany().getAccountNumber(), 
									"ZMW");
							log.info("2balanceInquiry==...." + balanceInquiry.getAvailableBalance());
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(portletState.getCas().getDemoModeBalance()==false)
					{
						portletState.setBalanceInquiry(balanceInquiry);
						portletState.loadDomTaxes(portletState, false);
					}else
					{
						portletState.setBalanceInquiry(balanceInquiry);
						portletState.loadDomTaxes(portletState, false);
					}
					
					SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");
					NotifyZRAPaymentResponse notifyZRAPaymentResponse  = null;
					if(cPy!=null && cPy.getStatus().equals(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_APPROVED))
					{
						log.info("6cPy.getStatus()==...." + cPy.getStatus().getValue());
//						notifyZRAPaymentResponse = new Util().notifyZRAPayment(aRes, aReq, cPy, cPy.getDomTax().getTpinInfo().getTpin(), 
//								cPy.getDomTax().getTaxPayerName(), cPy.getDomTax().getAmountPayable(), 
//								sdf3.format(new Date(cPy.getDateofTransaction().getTime())), cPy.getRequestMessageId(), "S");
						new Util().redirectUserBackToZRAPortalWithPost(aRes, aReq, cPy, cPy.getDomTax().getTpinInfo().getTpin(), 
								cPy.getDomTax().getTaxPayerName(), cPy.getDomTax().getAmountPayable(), 
								sdf3.format(new Date(cPy.getDateofTransaction().getTime())), cPy.getRequestMessageId(), "S");
					}	
					else if(cPy!=null && cPy.getStatus().equals(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_DECLINED))
					{
						log.info("7balanceInquiry==...." + cPy.getStatus().getValue());
//						notifyZRAPaymentResponse = new Util().notifyZRAPayment(aRes, aReq, cPy, cPy.getDomTax().getTpinInfo().getTpin(), 
//								cPy.getDomTax().getTaxPayerName(), cPy.getDomTax().getAmountPayable(), 
//								sdf3.format(new Date(cPy.getDateofTransaction().getTime())), cPy.getRequestMessageId(), "F");
						new Util().redirectUserBackToZRAPortalWithPost(aRes, aReq, cPy, cPy.getDomTax().getTpinInfo().getTpin(), 
								cPy.getDomTax().getTaxPayerName(), cPy.getDomTax().getAmountPayable(), 
								sdf3.format(new Date(cPy.getDateofTransaction().getTime())), cPy.getRequestMessageId(), "F");
					
					}
					
					String links = "";
					
//					if(notifyZRAPaymentResponse!=null && notifyZRAPaymentResponse.getErrorCode().equalsIgnoreCase("SUC00"))
//					{
//						links = "<span style='font-weight:100'>The following domestic taxes have been <strong><u>paid for successfully</u></strong>.<br>Click on the links to download your payment receipt(s)</span>";
//					}else
//					{
//						aRes.setRenderParameter("jspPage", "/html/domtaxportlet/domtaxlisting.jsp");
//						links = "Payment confirmation to ZRA was not successful. However approporiate funds may have been deducted from your bank account. If you have links provided on " +
//								"this page to download your payment receipts, then appropriate funds " +
//								"were deducted successfully from your account.<br><br>" + 
//								"<span style='font-weight:100'>The following domestic taxes have been <strong><u>paid for successfully</u></strong>.<br>Click on the links to download your payment receipt(s)</span>";
//					}
					
					
					
//					if(hashMap!=null && hashMap.size()>0)
//					{
//						
//						PaymentHistory paidPH = null;
//						for(Iterator<PaymentHistory> iter = hashMap.iterator(); iter.hasNext();)
//						{
//							paidPH = iter.next();
//						}
//						
//						
//						if(paidPH!=null)
//						{
//							aRes.setRenderParameter("jspPage", "/html/domtaxportlet/domtaxlisting.jsp");
//							
//							links = "<span style='font-weight:100'>The following domestic taxes have been <strong><u>paid for successfully</u></strong>.<br>Click on the links to download your payment receipt(s)</span>";
//							
//							/*******SuccessStr handled***/
//							//(receiptNumber + ":::" + paymentHistory1.getPaymentType().getValue() + ":::" + paymentHistory1.getAssessment().getRegistrationNumber());
//							
//							
//							
//							String paymentTyper = "N/A";
//							if(paidPH.getStatus().getValue().equalsIgnoreCase(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_APPROVED.getValue()))
//							{
//								links = links + "<span><br><br><u>Receipt(s) for Domestic Tax Paid</u><br></span>";
//								paymentTyper = "Payment For Domestic Tax";
//								
//								DomTax ass_ = paidPH.getDomTax();
//								if(ass_!=null)
//								{
//									links = links + "<span style='font-weight:100'><br>" +
//										"<strong>Domestic Tax PRN:</strong> " + ass_.getPaymentRegNo() + "<br>" +
//										"<strong>Payment Type:</strong> " +  paymentTyper + "<br>" +
//										"<strong>Amount Paid:</strong> ZMW " + new Util().roundUpAmount(paidPH.getPayableAmount()) + "<br>" +
//										"<strong>Download Link:</strong><a target='_blank' href='/eTax-portlet/ActiveServlet?action=domTaxId&domTaxId=" + 
//										paidPH.getRequestMessageId() + "&amount=" + paidPH.getPayableAmount() + "'>Download Receipt</a></span><br>";
//								}
//								ass_.setPaidFor(Boolean.TRUE);
//								swpService.updateRecord(ass_);
//							}
//							
//							balanceInquiry = null;
//							if(portletState.getCas()!=null && portletState.getCas().getDemoModeBalance().equals(Boolean.TRUE))
//							{
//								balanceInquiry = cas.getBalanceInquiry(portletState.getPortalUser(), swpService);
//							}else
//							{
//								try {
//									balanceInquiry = Util.getBalanceInquiry(portletState.getApplicationName().getValue(), 
//											"SMARTPAY:ZM:REVENUE:BALANCE:REQUEST", "TYPE", portletState.getPortalUser().getCompany().getAccountNumber(), 
//											"ZMW");
//								} catch (MalformedURLException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								} catch (IOException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//							}
//							if(portletState.getCas().getDemoModeBalance()==false)
//							{
//								portletState.setBalanceInquiry(balanceInquiry);
//								portletState.loadDomTaxes(portletState, false);
//							}else
//							{
//								portletState.setBalanceInquiry(balanceInquiry);
//								portletState.loadDomTaxes(portletState, false);
//							}
//							
//							portletState.addSuccess(aReq,links, portletState);
//							
//						}else
//						{
//							aRes.setRenderParameter("jspPage", "/html/domtaxportlet/domtaxlisting.jsp");
//							portletState.addError(aReq, "Payments for the domestic tax failed. Ensure you have enough funds in your account before trying again", portletState);
//						}
//					}else
//					{
//						aRes.setRenderParameter("jspPage", "/html/domtaxportlet/domtaxlisting.jsp");
//						portletState.addError(aReq, "Payments for the domestic tax failed. Connection to the Zambia Revenue Authority seems to be down. Please try again", portletState);
//					}
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/domtaxportlet/domtaxlisting.jsp");
					portletState.addError(aReq, "Payments for the domestic tax failed. Please try again", portletState);
				}
	
				
				
				
	
//				if(hashMap!=null && hashMap.size()>0)
//				{
//					PaymentHistory paidPH = null;
//					for(Iterator<PaymentHistory> iter = hashMap.iterator(); iter.hasNext();)
//					{
//						paidPH = iter.next();
//					}
//					
//					
//					if(paidPH!=null)
//					{
//						aRes.setRenderParameter("jspPage", "/html/domtaxportlet/domtaxlisting.jsp");
//						
//						
//						/*******SuccessStr handled***/
//						//(receiptNumber + ":::" + paymentHistory1.getPaymentType().getValue() + ":::" + paymentHistory1.getAssessment().getRegistrationNumber());
//						
//						
//						String links = "";
//						String paymentTyper = "N/A";
//						if(paidPH.getStatus().getValue().equalsIgnoreCase(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_APPROVED.getValue()))
//						{
//							links = links + "<span><br><br><u>Receipt(s) for Domestic Tax Paid</u><br></span>";
//							paymentTyper = "Payment For Domestic Tax";
//							
//							DomTax ass_ = paidPH.getDomTax();
//							if(ass_!=null)
//							{
//								links = links + "<span style='font-weight:100'><br>" +
//									"<strong>Domestic Tax PRN:</strong> " + ass_.getPaymentRegNo() + "<br>" +
//									"<strong>Payment Type:</strong> " +  paymentTyper + "<br>" +
//									"<strong>Amount Paid:</strong> ZMW " + new Util().roundUpAmount(paidPH.getPayableAmount()) + "<br>" +
//									"<strong>Download Link:</strong><a target='_blank' href='/eTax-portlet/ActiveServlet?action=domTaxId&domTaxId=" + 
//									paidPH.getRequestMessageId() + "&amount=" + paidPH.getPayableAmount() + "'>Download Receipt</a></span><br>";
//							}
//							ass_.setPaidFor(Boolean.TRUE);
//							swpService.updateRecord(ass_);
//						}
//						
//						BalanceInquiry balanceInquiry = null;
//						if(portletState.getCas()!=null && portletState.getCas().getDemoModeBalance().equals(Boolean.TRUE))
//						{
//							balanceInquiry = cas.getBalanceInquiry(portletState.getPortalUser(), swpService);
//						}else
//						{
//							try {
//								balanceInquiry = Util.getBalanceInquiry(portletState.getApplicationName().getValue(), 
//										"SMARTPAY:ZM:REVENUE:BALANCE:REQUEST", "TYPE", portletState.getPortalUser().getCompany().getAccountNumber(), 
//										"ZMW");
//							} catch (MalformedURLException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//						if(portletState.getCas().getDemoModeBalance()==false)
//						{
//							portletState.setBalanceInquiry(balanceInquiry);
//							portletState.loadDomTaxes(portletState, false);
//						}else
//						{
//							portletState.setBalanceInquiry(balanceInquiry);
//							portletState.loadDomTaxes(portletState, false);
//						}
//						
//						portletState.addSuccess(aReq, "<span style='font-weight:100'>The following domestic taxes have been <strong><u>paid for successfully</u></strong>.<br>Click on the links to download your payment receipt(s)</span>" + links, portletState);
//						
//					}else
//					{
//						aRes.setRenderParameter("jspPage", "/html/domtaxportlet/domtaxlisting.jsp");
//						portletState.addError(aReq, "Payments for the domestic tax failed. Ensure you have enough funds in your account before trying again", portletState);
//					}
//				}else
//				{
//					aRes.setRenderParameter("jspPage", "/html/domtaxportlet/domtaxlisting.jsp");
//					portletState.addError(aReq, "Payments for the domestic tax failed. Connection to the Zambia Revenue Authority seems to be down. Please try again", portletState);
//				}
			}catch(Exception e)
			{
				e.printStackTrace();
				aRes.setRenderParameter("jspPage", "/html/domtaxportlet/domtaxlisting.jsp");
				portletState.addError(aReq, "Payments for the domestic tax failed. Connection to the Zambia Revenue Authority seems to be down. Please try again", portletState);
			}
		}
		
		
		
	}

	
	private void getBalance(DomTaxPortletState portletState, ActionRequest aReq, ActionResponse aRes) {
		// TODO Auto-generated method stub
		ComminsApplicationState cas = portletState.getCas();
		BalanceInquiry balanceInquiry = null;
		if(cas.getDemoModeBalance().equals(Boolean.TRUE))
		{
			balanceInquiry = cas.getBalanceInquiry(portletState.getPortalUser(), swpService);
		}else
		{
			try {
				balanceInquiry = Util.getBalanceInquiry(portletState.getApplicationName().getValue(), 
						"SMARTPAY:ZM:REVENUE:BALANCE:REQUEST", "TYPE", portletState.getPortalUser().getCompany().getAccountNumber(), 
						"ZMW");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(balanceInquiry!=null)
		{
			portletState.setBalanceInquiry(balanceInquiry);
			aRes.setRenderParameter("jspPage", "/html/domtaxportlet/domtaxlisting.jsp");
		}else
		{
			portletState.setBalanceInquiry(null);
			aRes.setRenderParameter("jspPage", "/html/domtaxportlet/domtaxlisting.jsp");
			portletState.addError(aReq, "We are experiencing problems getting your bank balance. E189301 Error Code. Contact the bank for more details if this problems persist.", portletState);
		}
	}

}
