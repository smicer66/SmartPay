package com.probase.smartpay.workflow;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.PrivilegedActionException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import smartpay.audittrail.AuditTrail;
import smartpay.entity.Assessment;
import smartpay.entity.AuthorizePanelCombination;
import smartpay.entity.Balance;
import smartpay.entity.Company;
import smartpay.entity.DomTax;
import smartpay.entity.FeeDescription;
import smartpay.entity.PaymentHistory;
import smartpay.entity.PaymentTempHolder;
import smartpay.entity.PortalUser;
import smartpay.entity.Ports;
import smartpay.entity.Settings;
import smartpay.entity.Tokens;
import smartpay.entity.TpinInfo;
import smartpay.entity.WorkFlow;
import smartpay.entity.WorkFlowAssessment;
import smartpay.entity.enumerations.CompanyTypeConstants;
import smartpay.entity.enumerations.PanelTypeConstants;
import smartpay.entity.enumerations.PaymentHistoryConstants;
import smartpay.entity.enumerations.PaymentTypeConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.entity.enumerations.WorkFlowConstants;
import smartpay.service.SwpService;

import com.google.zxing.common.Collections;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.service.ServiceContext;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.probase.smartpay.workflow.WorkFlowPortlet;
import com.probase.smartpay.workflow.WorkFlowPortletState;
import com.probase.smartpay.workflow.WorkFlowPortletState.WORK_FLOW_ACTION;
import com.probase.smartpay.workflow.WorkFlowPortletUtil;
import com.probase.smartpay.workflow.WorkFlowPortletState.NAVIGATE;
import com.probase.smartpay.workflow.WorkFlowPortletState.VIEW_TABS;
import com.probase.smartpay.commins.BalanceInquiry;
import com.probase.smartpay.commins.ComminsApplicationState;
import com.probase.smartpay.commins.Emailer;
import com.probase.smartpay.commins.FundsTransferResponse;
import com.probase.smartpay.commins.InterestToBePaid;
import com.probase.smartpay.commins.Mailer;
import com.probase.smartpay.commins.ProbaseConstants;
import com.probase.smartpay.commins.SendMail;
import com.probase.smartpay.commins.SendSms;
import com.probase.smartpay.commins.TaxBreakDownResponse;
import com.probase.smartpay.commins.Util;
import com.sf.primepay.smartpay13.ServiceLocator;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

/**
 * Portlet implementation class WorkFlowPortlet
 */
public class WorkFlowPortlet extends MVCPortlet {
 
	private Logger log = Logger.getLogger(WorkFlowPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
	WorkFlowPortletUtil util = WorkFlowPortletUtil.getInstance();
	ServiceContext serviceContext = new ServiceContext();
	
	
	@Override
	public void init(PortletConfig config) throws PortletException {
		// TODO Auto-generated method stub
		log.info("WorkFlow portlet init called...");		
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
		WorkFlowPortletState portletState = 
				WorkFlowPortletState.getInstance(renderRequest, renderResponse);

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
		
		WorkFlowPortletState portletState = WorkFlowPortletState.getInstance(aReq, aRes);
		
		
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
        
        if(action.equalsIgnoreCase(WORK_FLOW_ACTION.FIND_ASSESSMENT_BY_TOKEN.name()))
        {
        	log.info("handle create a fee description post action");
        	handleSearchForWorkFlowByToken(aReq, aRes, portletState);
        }if(action.equalsIgnoreCase(WORK_FLOW_ACTION.HANDLE_WORKFLOW_LISTINGS.name()))
        {
        	handleWorkFlowListingsAction(aReq, aRes, portletState);
        }if(action.equalsIgnoreCase(WORK_FLOW_ACTION.HANDLE_WORKFLOW_FOR_ONE_ASSESSMENT.name()))
        {
        	handleWorkFlowListingsAction(aReq, aRes, portletState);
        }
        
		
	}
	
	private void handleWorkFlowListingsAction(ActionRequest aReq,
			ActionResponse aRes, WorkFlowPortletState portletState) {
		// TODO Auto-generated method stub
		String selectedWorkFlow = aReq.getParameter("selectedWorkFlow");
		String selectedWorkFlowAction = aReq.getParameter("selectedWorkFlowAction");
		System.out.println("selectedWorkFlow = " + selectedWorkFlow);
		System.out.println("selectedWorkFlowAction = " + selectedWorkFlowAction);
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		WorkFlow workflow = (WorkFlow)portletState.getWorkFlowPortletUtil().getEntityObjectById(WorkFlow.class, Long.valueOf(selectedWorkFlow));
		
		if(selectedWorkFlowAction.equalsIgnoreCase("approveNow"))
		{
			System.out.println("approveNow = ");
			if(workflow!=null)
			{
				portletState.setSelectedSearchedWorkFlow(workflow);
				aRes.setRenderParameter("jspPage", "/html/workflowportlet/token.jsp");
			}else
			{
				portletState.setSelectedSearchedWorkFlow(null);
				aRes.setRenderParameter("jspPage", "/html/workflowportlet/token.jsp");
				portletState.addError(aReq, "No work flow matching the selected workflow could be found. Please try again", portletState);
			}
		}else if(selectedWorkFlowAction.equalsIgnoreCase("approve"))
		{
			System.out.println("approve = ");
			handleSearchForWorkFlowByToken(aReq, aRes, portletState);
			if(portletState.getSelectedSearchedWorkFlow()!=null)
			{
				System.out.println("not null");
				handleApproveRejectForwardWorkFlow("approve", portletState.getSelectedSearchedWorkFlow(), aReq, aRes, portletState);
				aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
			}
			else
			{
				aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
			}
		}
		else if(selectedWorkFlowAction.equalsIgnoreCase("forward"))
			handleApproveRejectForwardWorkFlow("forward", workflow, aReq, aRes, portletState);
		else if(selectedWorkFlowAction.equalsIgnoreCase("goback"))
			aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
		else if(selectedWorkFlowAction.equalsIgnoreCase("reject"))
		{
			System.out.println("rejectNow = ");
			handleApproveRejectForwardWorkFlow("reject", workflow, aReq, aRes, portletState);
		}
		else if(selectedWorkFlowAction.equalsIgnoreCase("pay"))
			handlePayWorkFlow(workflow, aReq, aRes, portletState, emailer);
		else if(selectedWorkFlowAction.equalsIgnoreCase("view"))
			handleBreakdownOfAssessments(selectedWorkFlow, aReq, aRes, portletState);
		else if(selectedWorkFlowAction.equalsIgnoreCase("gobacktoworkflow"))
			handleGoToWorkflow(selectedWorkFlow, aReq, aRes, portletState);
		else if(selectedWorkFlowAction.equalsIgnoreCase("getBalance1"))
		{
			try{
				portletState.setBalanceInquiry(
						Util.getBalanceInquiry(portletState.getApplicationName().getValue(), 
								"SMARTPAY:ZM:REVENUE:BALANCE:REQUEST", "TYPE", portletState.getPortalUser().getCompany().getAccountNumber(), 
								"ZMW"));
				}catch(Exception e)
				{
					e.printStackTrace();
					portletState.setBalanceInquiry(null);
				}
		}
		else if(selectedWorkFlowAction.equalsIgnoreCase("getBalance"))
		{
			try{
			portletState.setBalanceInquiry(
					Util.getBalanceInquiry(portletState.getApplicationName().getValue(), 
							"SMARTPAY:ZM:REVENUE:BALANCE:REQUEST", "TYPE", portletState.getPortalUser().getCompany().getAccountNumber(), 
							"ZMW"));
			}catch(Exception e)
			{
				e.printStackTrace();
				portletState.setBalanceInquiry(null);
			}
		}
		
	}
	
	
	private void handleGoToWorkflow(String selectedWorkFlow,
			ActionRequest aReq, ActionResponse aRes,
			WorkFlowPortletState portletState) {
		// TODO Auto-generated method stub
		aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
	}

	private Double getInterestOnAssessment(String registrationNumber) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	private void handleBreakdownOfAssessments(String workFlowId, ActionRequest aReq,
			ActionResponse aRes,
			WorkFlowPortletState portletState) {
		// TODO Auto-generated method stub
		String declarantFlag = "Y";
		log.info("declarantFlag" + declarantFlag);
		Company company = portletState.getPortalUser().getCompany();
		TpinInfo tpinInfo = portletState.getWorkFlowPortletUtil().getTPINInfoByCompany(company.getId());
		String tpin_declarantcode = tpinInfo.getTpin();
		log.info("tpin_declarantcode" + tpin_declarantcode);
		String platformFlag = "2";
		log.info("platformFlag" + platformFlag);
		String clientTaxPayerID = aReq.getParameter("clientTaxPayerID")==null ? "" : aReq.getParameter("clientTaxPayerID");
		log.info("clientTaxPayerID" + clientTaxPayerID);
		String portOfEntry = aReq.getParameter("portOfEntry")==null ? "" : aReq.getParameter("portOfEntry");
		log.info("portOfEntry" + portOfEntry);
		String assessmentYear = aReq.getParameter("assessmentYear") == null ? "" : aReq.getParameter("assessmentYear");
		log.info("assessmentYear" + assessmentYear);
		String source = portletState.getPlatformBank().getValue();
		log.info("source" + source);
		String sourceID = source + "-" + UUID.randomUUID().toString();
		log.info("sourceID" + sourceID);
		String country = portletState.getPlatformCountry().getValue();
		log.info("country" + country);
		String url = "http://localhost:8086/WS/ProbaseZambiaService";
        url = "http://localhost:8085/ProbaseTestService2/ProbaseZambia";
       // url = "http://probasetestservice2-pallpod.rhcloud.com/ProbaseTestService2/ProbaseZambia";
        //url = "http://probasetestservice2-pallpod.rhcloud.com/ProbaseTestService2/ProbaseZambia";
        log.info("url1 = > ");
        log.info("url = > " + url);
        
        try
        {
			if(declarantFlag!=null && declarantFlag.length()>0)
			{
				if(tpin_declarantcode!=null && tpin_declarantcode.length()>0)
				{
					if(platformFlag!=null && platformFlag.length()>0)
					{
						if(workFlowId!=null && workFlowId.length()>0)
						{
						
							portletState.setSelectedWorkFlow(workFlowId);
							WorkFlow wf = (WorkFlow)portletState.getWorkFlowPortletUtil().getEntityObjectById(WorkFlow.class, Long.valueOf(workFlowId));

									
							try {
					            // Create SOAP Connection
								
					            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
					            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
		
					            // Send SOAP Message to SOAP Server
					            try
					            {
						            SOAPMessage soapResponse = soapConnection.call(
						            		new Util().createSOAPRequestForGetAssessmentDetails(declarantFlag, tpin_declarantcode, platformFlag, clientTaxPayerID, 
						            				portOfEntry, assessmentYear, source, sourceID, country), url);
		
						            Collection<Assessment> assessmentListing = new Util().handleResponseForGetAssessmentDetails(declarantFlag, tpin_declarantcode, 
						            		soapResponse, swpService, portletState.getPortalUser());
						            portletState.setAssessmentListing(assessmentListing);
						            //<?xml version="1.0" encoding="UTF-8" standalone="no"?><S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/"><S:Body><ns2:getAssessmentDetailsResponse xmlns:ns2="http://testservice.probase.com/"><clientTPIN/><country>ZM</country><reasonCode>0</reasonCode><reasonDescription>Successful</reasonDescription><source>S2B</source><sourceID>123456</sourceID><timestamp>Sat May 10 22:07:36 EDT 2014</timestamp><tpin_declarantCode>1000009294</tpin_declarantCode><type>getAssessmentDetails</type></ns2:getAssessmentDetailsResponse></S:Body></S:Envelope>
						            
						            // Process the SOAP Response
						            Collection<InterestToBePaid> itList = handleGetInterestOfUnPaidAssessments(declarantFlag.equals("Y") ? true : false, tpin_declarantcode, 
				        					aReq, aRes, company.getBankBranches().getBankCode(), portletState);
						            
//						            Collection<MiscToBePaid> miscList = handleGetMiscOfUnPaidAssessments(declarantFlag.equals("Y") ? true : false, tpin_declarantcode, 
//				        					aReq, aRes, company.getBankBranches().getBankCode(), portletState);
						            
						            
						            log.info("INFO: TRACE STARTS HERE ------------------------------");
						            if(itList!=null && itList.size()>0)
						            {
						            	log.info("INFO: itList.size = " + itList.size());
						            	for(Iterator<Assessment> aListIter = portletState.getAssessmentListing().iterator(); aListIter.hasNext();)
						            	{
						            		Assessment as = aListIter.next();
					            			log.info("INFO: as.regNo = " + as.getRegistrationNumber());
					            			for(Iterator<InterestToBePaid> itListIter = itList.iterator(); itListIter.hasNext();)
							            	{

							            		InterestToBePaid interestToBePaid = itListIter.next();
							            		log.info("INFO: interestToBePaid.getAmountToBePaid = " + interestToBePaid.getAmountToBePaid());
						            			if(as.getTpinInfo().getTpin().equals(interestToBePaid.getDeclarantCode()) && 
						            					as.getRegistrationNumber().equals(interestToBePaid.getRegistrationNumber()))
				            					{
						            				as.setInterest(Boolean.TRUE);
						            				as.setInterestAmount(interestToBePaid.getAmountToBePaid());
						            				swpService.updateRecord(as);
				            					}else
				            					{
				            						
				            					}
							            	}
						            	}
						            	
						            }
						            
						            
						            new Util().printSOAPResponse(soapResponse);
						            

						            
						            if(portletState.getTaxBreakDownList()!=null)
						            {
						            	aRes.setRenderParameter("jspPage", "/html/workflowportlet/transactionview.jsp");
						            }else
						            {
						            	aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
						    			portletState.addError(aReq, "Sending Request for Assessment details failed. Please check your internet connection! E189104 Error Code. Contact the bank for more details if this problems persist.", portletState);
						            }
						            
					            }catch(PrivilegedActionException e1)
					            {
					            	log.info("Ok start here");
					            	e1.printStackTrace();
					            	aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
					    			portletState.addError(aReq, "Sending Request for Assessment details failed. Please check your internet connection! E189104 Error Code. Contact the bank for more details if this problems persist.", portletState);
					            }
					    		
		
					            soapConnection.close();
					        } catch (Exception e) {
					            System.err.println("Error occurred while sending SOAP Request to Server");
					            e.printStackTrace();
					            aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
				    			portletState.addError(aReq, "Parsing data returned failed! E189104 Error Code. Contact the bank for more details if this problems persist.", portletState);
					        }
						}
						else
						{
							aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
							portletState.addError(aReq, "The request to view tax assessment listings was not successful! E189100 Error Code. Contact the bank for more details if this problems persist.", portletState);
						}
					}else
					{
						aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
						portletState.addError(aReq, "The request to view tax assessment listings was not successful! E189101 Error Code. Contact the bank for more details if this problems persist.", portletState);
					}
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
					portletState.addError(aReq, "The request to view tax assessment listings was not successful! E189102 Error Code. Contact the bank for more details if this problems persist.", portletState);
				}
			}else
			{
				aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
				portletState.addError(aReq, "The request to view tax assessment listings was not successful! E189103 Error Code. Contact the bank for more details if this problems persist.", portletState);
			}
		}
        catch(Exception e)
		{
			log.info("IOEXception printstacktrace==");
			e.printStackTrace();
			aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
			portletState.addError(aReq, "The request to view tax assessment listings was not successful! E189104 Error Code. Contact the bank for more details if this problems persist.", portletState);
		}
	}
	
	
	
	private Collection<InterestToBePaid> handleGetInterestOfUnPaidAssessments(boolean declarantFlag, String tpin_declarantcode, 
			ActionRequest aReq, ActionResponse aRes, String bankCode, WorkFlowPortletState portletState)
	{
		log.info("declarantFlag" + declarantFlag);
		
		log.info("tpin_declarantcode" + tpin_declarantcode);	
		//since we are not searching specifically for one client it becomes empty
		//since we are not searching specifically for one port it becomes empty
		String url = "http://localhost:8086/WS/ProbaseZambiaService";
        url = "http://localhost:8085/ProbaseTestService2/ProbaseZambia";
       // url = "http://probasetestservice2-pallpod.rhcloud.com/ProbaseTestService2/ProbaseZambia";
        //url = "http://probasetestservice2-pallpod.rhcloud.com/ProbaseTestService2/ProbaseZambia";
        log.info("url1 = > ");
        log.info("url = > " + url);
        Collection<InterestToBePaid> interestListing = null;
        
        try
        {
			
			if(tpin_declarantcode!=null && tpin_declarantcode.length()>0)
			{
				try {
		            // Create SOAP Connection
					
		            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
		            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

		            // Send SOAP Message to SOAP Server
		            try
		            {
			            SOAPMessage soapResponse = null;
			            if(declarantFlag)
			            {
			            	soapResponse = soapConnection.call(
			            		new Util().createSOAPRequestForGetInterestOfUnPaidAssessmentsByDeclarantCode(tpin_declarantcode, bankCode), url);
			            }else
			            {
			            	soapResponse = soapConnection.call(
				            		new Util().createSOAPRequestForGetInterestOfUnPaidAssessmentsByTPIN(tpin_declarantcode, bankCode), url);
			            }

			            interestListing = new Util().handleResponseForGetInterestResponse(soapResponse, swpService);
			            
			            //<?xml version="1.0" encoding="UTF-8" standalone="no"?><S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/"><S:Body><ns2:getAssessmentDetailsResponse xmlns:ns2="http://testservice.probase.com/"><clientTPIN/><country>ZM</country><reasonCode>0</reasonCode><reasonDescription>Successful</reasonDescription><source>S2B</source><sourceID>123456</sourceID><timestamp>Sat May 10 22:07:36 EDT 2014</timestamp><tpin_declarantCode>1000009294</tpin_declarantCode><type>getAssessmentDetails</type></ns2:getAssessmentDetailsResponse></S:Body></S:Envelope>
			            
			            // Process the SOAP Response
			            new Util().printSOAPResponse(soapResponse);
		            }catch(PrivilegedActionException e1)
		            {
		            	log.info("Ok start here");
		            	e1.printStackTrace();
		            }
		    		

		            soapConnection.close();
		        } catch (Exception e) {
		            System.err.println("Error occurred while sending SOAP Request to Server");
		            e.printStackTrace();
		        }
			}else
			{
				log.info("No declaration code or tpin info provided");
			}
		}
        catch(Exception e)
		{
			log.info("IOEXception printstacktrace==");
			e.printStackTrace();
		}

        return interestListing;
	}



//	private void handleAspectOfBreakUpDetails(WorkFlow wf, Assessment assessment, Company company, String tpin_declarantcode, String platformFlag, 
//			String clientTaxPayerID, String portOfEntry, String assessmentYear, String source, String sourceID, String country, WorkFlowPortletState portletState) {
//		// TODO Auto-generated method stub
//		Double interestValue = null;
//		TaxBreakDownResponse tbdR = null;
//		List<HashMap> allList = new ArrayList<HashMap>();
//		if(assessment.getInterest())
//		{
//			tbdR = new TaxBreakDownResponse();
//			interestValue = getInterestOnAssessment(assessment.getRegistrationNumber());
//			interestValue = 20.00;
////			tbdR.setAmountToBePaid(Double.toString(interestValue));
////			tbdR.setAssessment(assessment);
////			tbdR.setTaxName("Interest");
//			assessment.setInterestAmount(interestValue);
//			
//		}
//		
//		Assessment wfAss = wf.getAssessment();
//		wfAss.setAmount(assessment.getAmount());
//		wfAss.setInterest(assessment.getInterest());
//		wfAss.setInterestAmount(assessment.getInterestAmount());
//		swpService.updateRecord(wfAss);
//		handleAudit("Assessment Update", Long.toString(wfAss.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
//		
//		
//		ArrayList<TaxBreakDownResponse> taxBreakDownResponse = null;
////		taxBreakDownResponse = new Util().getTaxBreakDown(tpin_declarantcode, 
////				platformFlag, 
////				clientTaxPayerID, 
////				source, 
////				sourceID, 
////				country, 
////				new Timestamp((new Date()).getTime()).toString(), 
////				assessment.getDateRegistered(), 
////				portOfEntry,
////				assessment.getRegistrationNumber(),
////				assessment.getRegistrationSerial(),
////				assessment, "N");
//		
//		
//		if(taxBreakDownResponse!=null)
//		{
//			if(tbdR!=null)
//			{
//				taxBreakDownResponse.add(tbdR);
//			}
//			HashMap<String, ArrayList<TaxBreakDownResponse>> newHashList = new HashMap<String, ArrayList<TaxBreakDownResponse>>();
//			newHashList.put(assessment.getRegistrationNumber(), taxBreakDownResponse);
//			allList.add(newHashList);
//			log.info("allList = " + allList.size() + " && NnewHashList = " + newHashList.size());
//			portletState.reinitializeForTaxBreakDown(portletState);
//		}else
//		{
//			log.info("break the loop");
//		}
//		portletState.setTaxBreakDownList(allList);
//	}

	private Assessment getOneAssessmentByRegNo(String regNo,
			WorkFlowPortletState portletState) {
		// TODO Auto-generated method stub
		Assessment returnAssessment  = null;
		if(portletState.getAssessmentListing()!=null && portletState.getAssessmentListing().size()>0)
		{
			for(Iterator<Assessment> iter = portletState.getAssessmentListing().iterator(); iter.hasNext();)
			{
				Assessment assessemnt = iter.next();
				if(assessemnt.getRegistrationNumber().equalsIgnoreCase(regNo))
				{
					returnAssessment = assessemnt;
				}
			}
		}
		System.out.println("assessment = " + returnAssessment);
		return returnAssessment;
	}

	private void handleApproveRejectForwardWorkFlow(String action, WorkFlow workflow,
			ActionRequest aReq, ActionResponse aRes,
			WorkFlowPortletState portletState) {
		// TODO Auto-generated method stub
		System.out.println("reject 333 = ");
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		String nm = "reason" + workflow.getId();
		String reason = aReq.getParameter(nm);
		System.out.println("reason--" + (reason==null ? "null" : reason));
		int type = 0;
		if(action.equalsIgnoreCase("approve"))
		{
			//Double amount = 0.00;
			//get all assessments to get the amount for upper and  lower limit
			Collection<WorkFlowAssessment> wfac = portletState.getWorkFlowPortletUtil().getAllWorkFlowAssessmentbyWorkFlowId(workflow.getId());
			if(wfac!=null && wfac.size()>0)
			{
				for(Iterator<WorkFlowAssessment> wfacIt = wfac.iterator(); wfacIt.hasNext();)
				{
					if(wfacIt.next().getAssessment()==null)
						type = 1;
					else
						type = 0;
				}
				
				
				Collection<AuthorizePanelCombination> apcCollection= null;
//				AuthorizePanelCombination apcCurrentUser = portletState.getWorkFlowPortletUtil().getAuthorizedPanelCombinationForAPortalUser(
//						portletState.getPortalUser(), amount, PanelTypeConstants.AUTHORIZE_PANEL_TYPE_AUTHORISER);
//				
//				apcCollection = portletState.getWorkFlowPortletUtil().getAuthorizedPanelCombinationForSubsequentAuthorizers(
//						SmartPayConstants.AUTHORIZE_PANEL_COMBINATION_STATUS_ACTIVE, amount, apcCurrentUser.getPosition());
				

//				AuthorizePanelCombination apcCurrentUser = portletState.getWorkFlowPortletUtil().getCurrentPortalUserAPC(
//						portletState.getPortalUser(), workflow);
				
				apcCollection = portletState.getWorkFlowPortletUtil().getAPCForSubsequentPortalUsers(
						SmartPayConstants.AUTHORIZE_PANEL_COMBINATION_STATUS_ACTIVE, workflow);
				
				
				log.info("We are finding if there are subsquent approvers");
				if(apcCollection==null || (apcCollection!=null && apcCollection.size()==0))
				{
					log.info("apc collecciton is null");
					if(type==0)
						handlePayWorkFlow(workflow, aReq, aRes, portletState, emailer);
					else
						handlePayDomTaxWorkFlow(workflow, aReq, aRes, portletState, emailer);
				}else
				{
					log.info("Apc collection is not null");
					AuthorizePanelCombination apc1 = apcCollection.iterator().next();
					log.info("Apc collection is not null");
					
					workflow.setStatus(WorkFlowConstants.WORKFLOW_STATUS_FORWARDED);
					log.info("Apc collection is not null");
					workflow.setDateLastModified(new Timestamp((new Date()).getTime()));
					log.info("Apc collection is not null");
					workflow.setWorkFlowInitiatorId(portletState.getPortalUser().getId());
					log.info("Apc collection is not null");
					workflow.setWorkFlowReceipientPanelId(apc1.getAuthorizePanel().getId());
					log.info("Apc collection is not null");
					workflow.setWorkFlowReceipientPositionId(apc1.getPosition());
					log.info("Apc collection is not null");
					String refId = RandomStringUtils.random(8, false, true);
					log.info("Apc collection is not null");
					swpService.updateRecord(workflow);
					log.info("Apc collection is not null");
					
					String token = RandomStringUtils.random(6, false, true);
					log.info("Apc collection is not null");
					Tokens token1 = new Tokens();
					log.info("Apc collection is not null");
					token1.setCreatedByPortalUserId(portletState.getPortalUser().getId());
					log.info("Apc collection is not null");
					token1.setCreatedForAPCId(apc1.getId());
					log.info("Apc collection is not null");
					token1.setCreatedForAPCPosition(apc1.getPosition());
					log.info("Apc collection is not null");
					token1.setDateCreated(new Timestamp((new Date()).getTime()));
					log.info("Apc collection is not null");
					token1.setTokenValue(token);
					log.info("Apc collection is not null");
					token1.setWorkFlow(workflow);
					log.info("Apc collection is not null");
					
					token1 = (Tokens)swpService.createNewRecord(token1);
					log.info("Apc collection is not null");
					if(token1!=null)
					{	
						log.info("Apc collection is not null");
						handleAudit("Create Token", "Create New Token with Id " + token1.getId(), 
								new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), 
								portletState.getPortalUser().getUserId());
						
					}
					
					log.info("Apc collection is not null");
					handleAudit("WORK FLOW APPROVAL", 
							Long.toString(workflow.getId()) , 
							new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
					log.info("Apc collection is not null");
					Collection<Assessment> asList = portletState.getWorkFlowPortletUtil().getAssessmentListingsOfWorkFlowAssessment(workflow.getId());
					log.info("Apc collection is not null");
					List<String> assessmentlistString = new ArrayList<String>();
					log.info("Apc collection is not null");
					
					for(Iterator<Assessment> strIt = asList.iterator(); strIt.hasNext();)
					{
						log.info("Apc collection is not null");
						Assessment strIt1 = strIt.next();
						if(strIt1.getPaidFor().equals(Boolean.FALSE))
						{
							log.info("Apc collection is not null");
							assessmentlistString.add(strIt1.getRegistrationNumber());
						}
					}
					
					log.info("Apc collection is not null");
					for(Iterator<AuthorizePanelCombination> it = apcCollection.iterator(); it.hasNext();)
					{
						log.info("Apc collection is not null");
						AuthorizePanelCombination apc41 = it.next();
						SendMail sm = emailer.emailWorkFlow(apc41.getPortalUser().getEmailAddress(), 
							portletState.getPortalUser().getCompany().getCompanyName(),
							assessmentlistString,
							token1.getTokenValue(), 
							portletState.getSystemUrl().getValue(),
							apc41.getPortalUser().getFirstName(), 
							apc41.getPortalUser().getLastName(), 
							"Work Flow Item - Request for Approval of Payment for Assessment - ", portletState.getApplicationName().getValue());
					
						String message = "Work Flow Item - Request for Approval of Payment for Assessment";
						message = "eTax Approval Request!\nTxn Id:" + workflow.getReferenceId() + "\nToken:" + token1.getTokenValue() +
								"\nVisit " + portletState.getSystemUrl().getValue() + " to approve these requests";
						new SendSms(apc41.getPortalUser().getMobileNumber(), message, 
							portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
					
					
						if(apc41.getPortalUser().getSecondAlternativeEmailAddress()!=null && apc41.getPortalUser().getSecondAlternativeEmailAddress().length()>0)
						{
							SendMail sm2 = emailer.emailWorkFlow(apc41.getPortalUser().getSecondAlternativeEmailAddress(), 
								portletState.getPortalUser().getCompany().getCompanyName(),
								assessmentlistString,
								token1.getTokenValue(), 
								portletState.getSystemUrl().getValue(),
								apc41.getPortalUser().getFirstName(), 
								apc41.getPortalUser().getLastName(), 
								"Work Flow Item - Request for Approval of Payment for Assessment - ", portletState.getApplicationName().getValue());
						}
						
						message = "Work Flow Item - Request for Approval of Payment for Assessment";
						message = "eTax Approval Request!\nTxn Id:" + workflow.getReferenceId() + "\nToken:" + token1.getTokenValue() +
								"\nVisit " + portletState.getSystemUrl().getValue() + " to approve these requests";
						new SendSms(apc41.getPortalUser().getSecondAlternativeMobileNumber(), message, 
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
								"Work Flow Item - Request for Approval of Payment for Assessments - ", portletState.getApplicationName().getValue());
						}
						message = "Work Flow Item - Request for Approval of Payment for Assessment";
						message = "eTax Approval Request!\nTxn Id:" + workflow.getReferenceId() + "\nToken:" + token1.getTokenValue() +
								"\nVisit " + portletState.getSystemUrl().getValue() + " to approve these requests";
						new SendSms(apc41.getPortalUser().getFirstAlternativeMobileNumber(), message, 
								portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
					}
					
					aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
					portletState.addSuccess(aReq, "Work Item has been forwarded to the next personnel for action", portletState);

					log.info("Apc collection is not null");
					//GET all workflows which belong to the apc that a user belongs to
					Collection<WorkFlowAssessment> wfList = portletState.getWorkFlowPortletUtil().getWorkFlowsByReceipientId(portletState.getPortalUser().getId());
					log.info("Apc collection is not null");
					Collection<WorkFlow> wfs = portletState.getWorkFlowPortletUtil().getWorkFlowListByReceipientId(portletState.getPortalUser().getId());
					log.info("Apc collection is not null");
					if(wfs!=null && wfList!=null && wfList.size()>0)
					{
						log.info("Apc collection is not null");
					//	portletState.setSelectedSearchedWorkFlow(workFlow);
						portletState.setWorkFlowAssessmentList(wfList);
						portletState.setWorkFlowList(wfs);
					}else
					{
						log.info("Apc collection is not null");
						portletState.setWorkFlowAssessmentList(null);
						portletState.setWorkFlowList(null);
						portletState.setWorkFlowAssessmentList(null);
					}
				}
				aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
			}
			else
			{
				aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
				portletState.addError(aReq, "Invalid selection. You do not have any tax assessments to approve for payment", portletState);
				
			}
			
		}
		else if(action.equalsIgnoreCase("reject"))
		{
			if(reason!=null && reason.length()>0)
			{
				workflow.setStatus(WorkFlowConstants.WORKFLOW_STATUS_REJECTED);
				workflow.setDateLastModified(new Timestamp((new Date()).getTime()));
				
				workflow.setReason(reason);
				swpService.updateRecord(workflow);
				Tokens tk = portletState.getWorkFlowPortletUtil().getTokenByWorkFlow(workflow);
				tk.setIsValid(Boolean.FALSE);
				swpService.updateRecord(tk);
				//swpService.deleteRecord(tk);
				Collection<WorkFlowAssessment> wfaLis = portletState.getWorkFlowPortletUtil().getWorkFlowAssessmentsByWorkFlow(workflow);

				Assessment asy = null;
				for(Iterator<WorkFlowAssessment> wfad = wfaLis.iterator(); wfad.hasNext();)
				{
					WorkFlowAssessment wfa = wfad.next();
					asy = wfa.getAssessment();
					//swpService.deleteRecord(wfa);
					wfa.setStatus(WorkFlowConstants.WORKFLOW_STATUS_REJECTED);
					swpService.updateRecord(wfa);
					if(asy!=null)
					{
						asy.setMovedToWorkFlow(Boolean.FALSE);
						asy.setPaidFor(Boolean.FALSE);
						swpService.updateRecord(asy);
					}
				}
				
				//swpService.deleteRecord(workflow);
				handleAudit("WORK FLOW REJECTION", 
						Long.toString(workflow.getId()), 
						new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				//portletState.setWorkFlowAssessmentList(portletState.getWorkFlowPortletUtil().getWorkFlowsByCompany(portletState.getPortalUser().getCompany()));
				//WorkFlow workFlow= portletState.getWorkFlowPortletUtil().getWorkFlowByTokenAndReceipientId(portletState.getToken(), portletState.getPortalUser().getId());
				
				
				
				Collection<WorkFlowAssessment> wfList = portletState.getWorkFlowPortletUtil().getWorkFlowsByReceipientId(portletState.getPortalUser().getId());
				Collection<WorkFlow> wfs = portletState.getWorkFlowPortletUtil().getWorkFlowListByReceipientId(portletState.getPortalUser().getId());
				if(wfs!=null && wfList!=null && wfList.size()>0)
				{
				//	portletState.setSelectedSearchedWorkFlow(workFlow);
					portletState.setWorkFlowAssessmentList(wfList);
					portletState.setWorkFlowList(wfs);
				}else
				{
				//	portletState.setWorkFlowAssessmentList(null);
					portletState.addError(aReq, "There are no work flow items currently available for you to work on", portletState);
					portletState.setWorkFlowAssessmentList(null);
					portletState.setWorkFlowList(null);
					portletState.setWorkFlowAssessmentList(null);
				}
				portletState.addSuccess(aReq, "The Workflow item has been disapproved!", portletState);
				
				
				PortalUser pulast = (PortalUser)portletState.getWorkFlowPortletUtil().getEntityObjectById(PortalUser.class, workflow.getWorkFlowInitiatorId());
				Collection<Assessment> assessment = portletState.getWorkFlowPortletUtil().getAssessmentListingsOfWorkFlowAssessment(workflow.getId());
				List<String> assessmentListingStr = new ArrayList<String>();
				for(Iterator<Assessment> stIt = assessment.iterator(); stIt.hasNext();)
				{
					assessmentListingStr.add(stIt.next().getRegistrationNumber());
				}
				
				
				SendMail sm = emailer.emailDisapproval(pulast.getEmailAddress(), 
						pulast.getFirstName(), pulast.getLastName(), portletState.getReason(),
						portletState.getSystemUrl().getValue(), 
						"Work Flow Item - Payment for Assessment Disapproved", portletState.getApplicationName().getValue());
				
				String message = "Work Flow Item - Payment for Assessment Disapproved";
				message = "eTax Approval Request Disapproved!\nTxn Id:" + workflow.getReferenceId() + "\nVisit " + portletState.getSystemUrl().getValue() + " to approve these requests";
				new SendSms(portletState.getPortalUser().getCompany().getMobileNumber(), message, 
						portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
				
			}else
			{
				aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
				portletState.addError(aReq, "Provide a reason for disapproving the work flow item before proceeding", portletState);
			}
		}
		else if(action.equalsIgnoreCase("pay"))
		{
			AuthorizePanelCombination apcCollection= null;
			workflow.setStatus(WorkFlowConstants.WORKFLOW_STATUS_APPROVED);
			workflow.setDateLastModified(new Timestamp((new Date()).getTime()));
			swpService.updateRecord(workflow);
			handleAudit("WORK FLOW APPROVAL", 
					Long.toString(workflow.getId()), 
					new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
			
			Collection<WorkFlowAssessment> wfac = portletState.getWorkFlowPortletUtil().getAllWorkFlowAssessmentbyWorkFlowId(workflow.getId());
			handlePayWorkFlow(workflow, aReq, aRes, portletState, emailer);
			//portletState.setWorkFlowAssessmentList(portletState.getWorkFlowPortletUtil().getWorkFlowsByCompany(portletState.getPortalUser().getCompany()));
			//WorkFlow workFlow= portletState.getWorkFlowPortletUtil().getWorkFlowByTokenAndReceipientId(portletState.getToken(), portletState.getPortalUser().getId());
			
			
			
			Collection<WorkFlowAssessment> wfList = portletState.getWorkFlowPortletUtil().getWorkFlowsByReceipientId(portletState.getPortalUser().getId());
			Collection<WorkFlow> wfs = portletState.getWorkFlowPortletUtil().getWorkFlowListByReceipientId(portletState.getPortalUser().getId());
			if(wfs!=null && wfList!=null && wfList.size()>0)
			{
			//	portletState.setSelectedSearchedWorkFlow(workFlow);
				portletState.setWorkFlowAssessmentList(wfList);
				portletState.setWorkFlowList(wfs);
			}else
			{
				portletState.setWorkFlowAssessmentList(null);
				portletState.addError(aReq, "There is no WorkFlow item matching this token.", portletState);
			}
		}
	}

	private void handlePayDomTaxWorkFlow(WorkFlow workflow, ActionRequest aReq,
			ActionResponse aRes, WorkFlowPortletState portletState,
			Mailer emailer) {
		// TODO Auto-generated method stub
		ComminsApplicationState cas = WorkFlowPortletState.getCas();
		
		DomTax selectedDomTax = portletState.getWorkFlowPortletUtil().getDomTaxByWorkFlow(workflow);
		
		BalanceInquiry balanceInquiry= null;
//        if(cas.getDemoModeBalance().equals(Boolean.FALSE))
//        {
        	
        	try {
				balanceInquiry = Util.getBalanceInquiry("SmartPay", 
						"SMARTPAY:ZM:REVENUE:BALANCE:REQUEST", "TYPE", 
						portletState.getPortalUser().getCompany().getAccountNumber(), 
						"ZMW");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//        }else
//        {
//        	balanceInquiry = cas.getBalanceInquiry(portletState.getPortalUser(), swpService);
//			
//        }
        
        
		if(balanceInquiry!=null)
		{
			portletState.setBalanceInquiry(balanceInquiry);
		}else
		{
			portletState.setBalanceInquiry(null);
		}
		
		
		if(portletState.getBalanceInquiry()!=null && portletState.getBalanceInquiry().getAvailableBalance()> selectedDomTax.getAmountPayable())
		{
			if(selectedDomTax!=null)
			{
				ArrayList<DomTax> li = new ArrayList<DomTax>();
				li.add(selectedDomTax);
				List<PaymentHistory> hashMap = null;
				
//				log.info("cas.getDemoModePay() = " + cas.getDemoModePay());
				
					log.info("cas.getDemoModePay() = false");
					hashMap=	Util.payDirectForDomTax(cas, swpService, 
							portletState.getPortalUser().getCompany(), 
							selectedDomTax.getPaymentRegNo(), portletState.getPortalUser(), portletState.getRemoteIPAddress(), 
							portletState.getSettingsZRAAccount(), portletState.getSettingsZRASortCode(), 
							portletState.getPlatformBank().getValue(), portletState.getBankName().getValue(), 
							emailer, 
							portletState.getApplicationName().getValue(), 
							portletState.getProxyUsername().getValue(), portletState.getProxyPassword().getValue(), 
							portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue(), 
							portletState.getBankPaymentWebServiceUrl().getValue(), 
							li);
				
				
		        
				
				//Set keySet = hashMap.keySet();
	    		

	            
	            String successStr="";
				String successStr1="";
				String successStr2="";
				String successStr3="";
				String successStr4="";
				String failStr="";
				if(hashMap!=null && hashMap.size()>0)
				{
					for(Iterator<PaymentHistory> iter = hashMap.iterator(); iter.hasNext();)
					{
						PaymentHistory key = iter.next();
						log.info("key====" + key);
						String links = "";
						if(key.getStatus().equals(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_APPROVED))
						{
							successStr = key.getReceiptNumber() + ", ";
							successStr1 = key.getPaymentType().getValue() + ", ";
							successStr2 = key.getDomTax().getPaymentRegNo() + ", ";
							successStr3 = key.getPayableAmount() + ", ";
							successStr4 = key.getRequestMessageId() + ", ";
						}
					}
					
					log.info("successStr====" + successStr);
					log.info("successStr1====" + successStr1);
					log.info("successStr2====" + successStr2);
					log.info("successStr3====" + successStr3);
					log.info("successStr4====" + successStr4);
					
					
					if(successStr.length()>0)
					{
						aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
						
						
						/*******SuccessStr handled***/
						//(receiptNumber + ":::" + paymentHistory1.getPaymentType().getValue() + ":::" + paymentHistory1.getAssessment().getRegistrationNumber());
						String[] sxs = successStr.substring(0, successStr.length() - 2).split(", ");	//recNo
						String[] sxs1 = successStr1.substring(0, successStr1.length() - 2).split(", ");	//payType
						String[] sxs2 = successStr2.substring(0, successStr2.length() - 2).split(", ");	//assRegNo
						String[] sxs3 = successStr3.substring(0, successStr3.length() - 2).split(", ");	//Amt
						String[] sxs4 = successStr4.substring(0, successStr4.length() - 2).split(", ");	//ReqMsgId
						
						
						String links = "";
						String paymentTyper = "N/A";
						int totalCount = 0;
						
						if(sxs.length>0)
						{
							links = links + "<span><br><br><u>Receipt(s) for Domestic Tax(es) Paid</u><br></span>";
						}
						
							for(int c=0; c<sxs.length; c++)
							{
									if(sxs1[c].equalsIgnoreCase(PaymentTypeConstants.PAYMENTTYPE_INTEREST_PAYMENT.getValue()))
									{
										paymentTyper = "Payment For Interest";
									}else if(sxs1[c].equalsIgnoreCase(PaymentTypeConstants.PAYMENTTYPE_TAXFEE_PAYMENT.getValue()))
									{
										paymentTyper = "Payment For Assessment Fee";
									}else if(sxs1[c].equalsIgnoreCase(PaymentTypeConstants.PAYMENTTYPE_DOM.getValue()))
									{
										paymentTyper = "Payment For Domestic Tax Fee";
									}
									links = links + "<span style='font-weight:100'><br>" +
											"<strong>Domestic Tax PRN:</strong> " + sxs2[c] + "<br>" +
											"<strong>Payment Type:</strong> " +  paymentTyper + "<br>" +
											"<strong>Amount Paid:</strong> ZMW " + new Util().roundUpAmount(Double.valueOf(sxs3[c])) + "<br> " +
											"<strong>Download Link:</strong> <a target='_blank' href='/ProbaseSmartPay-portlet/ActiveServlet?action=downloadDomTaxSlip&domTaxId=" + 
											sxs4[c] + "&amount=" + sxs3[c] + "'>Download Receipt</a></span><br>";
									
							}
							
							
								//og.info(Arrays.toString(selectedAssessment));
								
									
									
									//as.getRegistrationNumber() + "/" + as.getAssessmentYear() + "/" + as.getPorts().getId()
									//(receiptNumber + ":::" + paymentHistory1.getPaymentType().getValue() + ":::" + paymentHistory1.getAssessment().getRegistrationNumber());
	//											String receiptNumber = paymentHistory1.getAssessment().getPorts().getPortCode() + 
	//	            	            					"/" + paymentHistory1.getAssessment().getRegistrationNumber() + 
	//	            	            					"/" + paymentHistory1.getAssessment().getAssessmentYear() + 
	//	            	            					"/" + interestPaymentResultList.getReceiptNumber();
									
							WorkFlowAssessment wfac = portletState.getWorkFlowPortletUtil().getWorkFlowAssessmentsDomTax(selectedDomTax, workflow);
							if(wfac!=null)
							{
								wfac.setStatus(WorkFlowConstants.WORKFLOW_STATUS_APPROVED);
								swpService.updateRecord(wfac);
								
								DomTax ast = wfac.getDomTax();
								ast.setPaidFor(Boolean.TRUE);
								swpService.updateRecord(ast);
								workflow.setStatus(WorkFlowConstants.WORKFLOW_STATUS_APPROVED);
								workflow.setDateLastModified(new Timestamp((new Date()).getTime()));
								swpService.updateRecord(workflow);
							}
										
								
						
							handleAudit("WORK FLOW APPROVAL", 
								"WORK FLOW ID " + workflow.getId() + " HAS BEEN APPROVED BY " + portletState.getPortalUser().getId() , 
								new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
							portletState.addSuccess(aReq, "<span style='font-weight:100'>The following assessments have been <strong><u>paid for successfully</u></strong>.<br>Click on the links to download your payment receipt(s)</span>" + links, portletState);
							Collection<WorkFlowAssessment> wfList = portletState.getWorkFlowPortletUtil().getWorkFlowsByReceipientId(portletState.getPortalUser().getId());
							log.info("Apc collection is not null");
							Collection<WorkFlow> wfs = portletState.getWorkFlowPortletUtil().getWorkFlowListByReceipientId(portletState.getPortalUser().getId());
							log.info("Apc collection is not null");
							if(wfs!=null && wfList!=null && wfList.size()>0)
							{
								log.info("Apc collection is not null");
							//	portletState.setSelectedSearchedWorkFlow(workFlow);
								portletState.setWorkFlowAssessmentList(wfList);
								portletState.setWorkFlowList(wfs);
							}else
							{
								portletState.setWorkFlowAssessmentList(null);
								portletState.setWorkFlowList(null);
								log.info("Apc collection is not null");
								portletState.setWorkFlowAssessmentList(null);
							}
					
					}else
					{
						aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
						portletState.addError(aReq, "Payments for the selected assessments failed. Ensure you have enough funds in your account before trying again", portletState);
					}
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
					portletState.addError(aReq, "Payments for the selected domestic taxes was not successful. Please try again", portletState);
				}
				
				
			}else
			{
				portletState.addError(aReq, "Invalid Domestic Tax found. Please try again", portletState);
				
			}
		}
		else
		{
			if(portletState.getBalanceInquiry()!=null)
			{
				portletState.addError(aReq, "You do not have adequate balance in your account to make this payment." + "" + 
					portletState.getBalanceInquiry()==null ? "" : ("Current balance is ZMW" + portletState.getBalanceInquiry()), portletState);
			}else
			{
				portletState.addError(aReq, "Accessing your balance was not possible. Please try again later", portletState);
			}
		}
	}

	private void handlePayWorkFlow(WorkFlow workflow, ActionRequest aReq,
			ActionResponse aRes, WorkFlowPortletState portletState, Mailer emailer) {
		// TODO Auto-generated method stub
		log.info("handlePayWorkFlow");
		ComminsApplicationState cas = portletState.getCas();  
		String declarantFlag = null;
		if(portletState.getPortalUser().getCompany().getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY))
		{
			if(portletState.getPortalUser().getCompany().getClearingAgent().equals(Boolean.TRUE))
				declarantFlag = "Y";
			else
				declarantFlag = "N";
		}
		else if(portletState.getPortalUser().getCompany().getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_RETAIL_COMPANY))			
			declarantFlag = "Y";
		
		log.info("declarantFlag" + declarantFlag);
		Company company = portletState.getPortalUser().getCompany();
		TpinInfo tpinInfo = portletState.getWorkFlowPortletUtil().getTPINInfoByCompany(company.getId());
		String tpin_declarantcode = null;
		tpin_declarantcode = tpinInfo.getTpin();
		
		log.info("tpin_declarantcode" + tpin_declarantcode);
		String platformFlag = "2";
		log.info("platformFlag" + platformFlag);
		String clientTaxPayerID = "";					
		//since we are not searching specifically for one client it becomes empty
		log.info("clientTaxPayerID" + clientTaxPayerID);
		String portOfEntry =  "";
		//since we are not searching specifically for one port it becomes empty
		log.info("portOfEntry" + portOfEntry);
		String assessmentYear = "";
		//since we are not searching specifically for one assessment year it becomes empty
		log.info("assessmentYear" + assessmentYear);
		String source = portletState.getPlatformBank().getValue();
		log.info("source" + source);
		String sourceID = source + "-" + UUID.randomUUID().toString();
		log.info("sourceID" + sourceID);
		String country = portletState.getPlatformCountry().getValue();
		log.info("country" + country);
		String url = "http://localhost:8086/WS/ProbaseZambiaService";
        url = "http://localhost:8085/ProbaseTestService2/ProbaseZambia";
       // url = "http://probasetestservice2-pallpod.rhcloud.com/ProbaseTestService2/ProbaseZambia";
        //url = "http://probasetestservice2-pallpod.rhcloud.com/ProbaseTestService2/ProbaseZambia";
        log.info("url1 = > ");
        log.info("url = > " + url);
        
        try
        {
			if(declarantFlag!=null && declarantFlag.length()>0)
			{
				if(tpin_declarantcode!=null && tpin_declarantcode.length()>0)
				{
					if(platformFlag!=null && platformFlag.length()>0)
					{
						
						try {
				            // Create SOAP Connection
							
				            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
				            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
	
				            // Send SOAP Message to SOAP Server
				            try
				            {
				            	
//				            	if(cas.getDemoMode().equals(Boolean.FALSE))
//				            	{
						            SOAPMessage soapResponse = soapConnection.call(
						            		Util.createSOAPRequestForGetAssessmentDetails(declarantFlag, tpin_declarantcode, platformFlag, clientTaxPayerID, 
						            				portOfEntry, assessmentYear, source, sourceID, country), url);
		
						            Collection<Assessment> assessmentListing = Util.handleResponseForGetAssessmentDetails(declarantFlag, tpin_declarantcode, 
						            		soapResponse, swpService, portletState.getPortalUser());
						            portletState.setAllAssessmentListing(assessmentListing);
						            
						            Collection<InterestToBePaid> itList = Util.handleGetInterestOfUnPaidAssessments(declarantFlag.equals("Y") ? true : false, tpin_declarantcode, 
				        					aReq, aRes, company.getBankBranches().getBankCode(), swpService);
						            
	//					            Collection<MiscToBePaid> miscList = handleGetMiscOfUnPaidAssessments(declarantFlag.equals("Y") ? true : false, tpin_declarantcode, 
	//			        					aReq, aRes, company.getBankBranches().getBankCode(), portletState);
						            
						            
						            Collection<Assessment> assessmentList = null;
						            if(declarantFlag.equals("Y"))
						            {
						            	assessmentList = Util.addInterestToAssessmentsForCorporate(itList, portletState.getAllAssessmentListing());
						            }else if(declarantFlag.equals("N"))
						            {
						            	assessmentList = Util.addInterestToAssessmentsForRetail(itList, portletState.getAllAssessmentListing());
						            }
						            portletState.setAllAssessmentListing(assessmentList);
						            
						            
						            
	//					            /***MISC TRACE STARTS HERE***/
	//					            if(itList!=null && itList.size()>0)
	//					            {
	//					            	log.info("INFO: itList.size = " + itList.size());
	//					            	portletState.setAllInterestToBePaid(itList);
	//					            	for(Iterator<Assessment> aListIter = portletState.getAllAssessmentListing().iterator(); aListIter.hasNext();)
	//					            	{
	//					            		Assessment as = aListIter.next();
	//				            			log.info("INFO: as.regNo = " + as.getRegistrationNumber());
	//				            			for(Iterator<InterestToBePaid> itListIter = itList.iterator(); itListIter.hasNext();)
	//						            	{
	//
	//						            		InterestToBePaid interestToBePaid = itListIter.next();
	//						            		log.info("INFO: interestToBePaid.getAmountToBePaid = " + interestToBePaid.getAmountToBePaid());
	//					            			if(as.getTpinInfo().getTpin().equals(interestToBePaid.getDeclarantCode()) && 
	//					            					as.getRegistrationNumber().equals(interestToBePaid.getRegistrationNumber()))
	//			            					{
	//					            				as.setInterest(Boolean.TRUE);
	//					            				as.setInterestAmount(interestToBePaid.getAmountToBePaid());
	//			            					}else
	//			            					{
	//			            						
	//			            					}
	//						            	}
	//					            	}
	//					            	
	//					            }
						            //<?xml version="1.0" encoding="UTF-8" standalone="no"?><S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/"><S:Body><ns2:getAssessmentDetailsResponse xmlns:ns2="http://testservice.probase.com/"><clientTPIN/><country>ZM</country><reasonCode>0</reasonCode><reasonDescription>Successful</reasonDescription><source>S2B</source><sourceID>123456</sourceID><timestamp>Sat May 10 22:07:36 EDT 2014</timestamp><tpin_declarantCode>1000009294</tpin_declarantCode><type>getAssessmentDetails</type></ns2:getAssessmentDetailsResponse></S:Body></S:Envelope>
						            
						            // Process the SOAP Response
						            
						            BalanceInquiry balanceInquiry= null;
//						            if(cas.getDemoModeBalance().equals(Boolean.FALSE))
//						            {
						            	
						            	try {
											balanceInquiry = Util.getBalanceInquiry("SmartPay", 
													"SMARTPAY:ZM:REVENUE:BALANCE:REQUEST", "TYPE", 
													portletState.getPortalUser().getCompany().getAccountNumber(), 
													"ZMW");
										} catch (MalformedURLException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
//						            }else
//						            {
//						            	balanceInquiry = cas.getBalanceInquiry(portletState.getPortalUser(), swpService);
//										
//						            }
						            
						            
									if(balanceInquiry!=null)
									{
										portletState.setBalanceInquiry(balanceInquiry);
									}else
									{
										portletState.setBalanceInquiry(null);
									}
									
						            new Util().printSOAPResponse(soapResponse);
//				            	}else
//				            	{
//				            		portletState.setAllAssessmentListing(cas.getAssessmentList(portletState.getPortalUser(), swpService));
//				            		portletState.setBalanceInquiry(cas.getBalanceInquiry(portletState.getPortalUser(), swpService));
//				            	}
					            
					            Collection<Assessment> selectedAssessmentCollection = portletState.getWorkFlowPortletUtil().
					            		getAssessmentListingsOfWorkFlowAssessment(workflow.getId());
					            String[] selectedAssessment = new String[selectedAssessmentCollection.size()];
					            int c1 = 0;
					            for(Iterator<Assessment> its = selectedAssessmentCollection.iterator(); its.hasNext();)
					            {
					            	Assessment as = its.next();
					            	if(as.getPaidFor().equals(Boolean.TRUE))
					            	{
					            		
					            	}else
					            	{
					            		selectedAssessment[c1++] = (as.getRegistrationNumber() + "/" + as.getAssessmentYear() + "/" + as.getPorts().getId());
					            	}
					            }
					            
					            HashMap<String,Boolean> hashMap = Util.payDirect(cas, swpService, portletState.getPortalUser().getCompany().getId(), portletState.getAllAssessmentListing(), 
					    				selectedAssessment, portletState.getPortalUser(), portletState.getRemoteIPAddress(), 
					    				portletState.getSettingsZRAAccount(), portletState.getSettingsZRASortCode(), 
					    				portletState.getPlatformBank().getValue(), portletState.getBankName().getValue(), 
					    				emailer, portletState.getApplicationName().getValue(), 
					    				portletState.getProxyUsername().getValue(), portletState.getProxyPassword().getValue(), 
					    				portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue(), 
					    				portletState.getBankPaymentWebServiceUrl().getValue());
					            
					            
					    		
					    		Set keySet = hashMap.keySet();
					    		

					            
					            String successStr="";
								String successStr1="";
								String successStr2="";
								String successStr3="";
								String failStr="";
								for(Iterator<String> iter = keySet.iterator(); iter.hasNext();)
								{
									String key = iter.next();
									log.info("key====" + key);
									String links = "";
									if(hashMap.get(key)!=null && hashMap.get(key).equals(Boolean.TRUE))
									{
										successStr += key.split(":::")[0] + ", ";
										successStr1 += key.split(":::")[1] + ", ";
										successStr2 += key.split(":::")[2] + ", ";
										successStr3 += key.split(":::")[3] + ", ";
									}else
									{
										failStr += key.split(":::")[0] + ", ";
									}
								}
								
								log.info("successStr====" + successStr);
								log.info("successStr1====" + successStr1);
								log.info("successStr2====" + successStr2);
								log.info("successStr3====" + successStr3);
								
								
								if(successStr.length()>0)
								{
									aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
									
									
									/*******SuccessStr handled***/
									//(receiptNumber + ":::" + paymentHistory1.getPaymentType().getValue() + ":::" + paymentHistory1.getAssessment().getRegistrationNumber());
									String[] sxs = successStr.substring(0, successStr.length() - 2).split(", ");	//recNo
									String[] sxs1 = successStr1.substring(0, successStr1.length() - 2).split(", ");	//payType
									String[] sxs2 = successStr2.substring(0, successStr2.length() - 2).split(", ");	//assRegNo
									String[] sxs3 = successStr3.substring(0, successStr3.length() - 2).split(", ");	//Amt
									
									
									String links = "";
									String paymentTyper = "N/A";
									int totalCount = 0;
									
									if(sxs.length>0)
									{
										links = links + "<span><br><br><u>Receipt(s) for Assessment(s) Paid</u><br></span>";
									}
									
										for(int c=0; c<sxs.length; c++)
										{
												if(sxs1[c].equalsIgnoreCase(PaymentTypeConstants.PAYMENTTYPE_INTEREST_PAYMENT.getValue()))
												{
													paymentTyper = "Payment For Interest";
												}else if(sxs1[c].equalsIgnoreCase(PaymentTypeConstants.PAYMENTTYPE_TAXFEE_PAYMENT.getValue()))
												{
													paymentTyper = "Payment For Assessment Fee";
												}
												links = links + "<span style='font-weight:100'><br>" +
														"<strong>Receipt Number:</strong> " + sxs[c] + "<br>" +
														"<strong>Assessment Registration Number:</strong> " + sxs2[c] + "<br>" +
														"<strong>Payment Type:</strong> " +  paymentTyper + "<br>" +
														"<strong>Amount Paid:</strong> ZMW " + new Util().roundUpAmount(Double.valueOf(sxs3[c])) + "<br> " +
														"<strong>Download Link:</strong> <a target='_blank' href='/ProbaseSmartPay-portlet/ActiveServlet?action=downloadWFLumpSlip&workFlowRefId=" + 
														workflow.getReferenceId() + "&amount=" + sxs3[c] + "'>Download Receipt</a></span><br>";
												
										}
										
										
										for(int c5=0; c5<selectedAssessment.length; c5++)
										{
											log.info(Arrays.toString(selectedAssessment));
											if(selectedAssessment[c5]!=null)
											{
												String[] selectedAssessmentSplit = selectedAssessment[c5].split("/");
												log.info(Arrays.toString(selectedAssessmentSplit));
												Integer yr = null;
												Long id = null;
												try{
												yr = Integer.valueOf(selectedAssessmentSplit[1]);
												id = Long.valueOf(selectedAssessmentSplit[2]);
												}catch(NumberFormatException e)
												{
													e.printStackTrace();
												}
												Assessment tempAssessment = portletState.getWorkFlowPortletUtil().getAssessmentByRegNoAssessmentYearAndPort(
														selectedAssessmentSplit[0], 
														yr, id);
												//as.getRegistrationNumber() + "/" + as.getAssessmentYear() + "/" + as.getPorts().getId()
												//(receiptNumber + ":::" + paymentHistory1.getPaymentType().getValue() + ":::" + paymentHistory1.getAssessment().getRegistrationNumber());
	//											String receiptNumber = paymentHistory1.getAssessment().getPorts().getPortCode() + 
	//	            	            					"/" + paymentHistory1.getAssessment().getRegistrationNumber() + 
	//	            	            					"/" + paymentHistory1.getAssessment().getAssessmentYear() + 
	//	            	            					"/" + interestPaymentResultList.getReceiptNumber();
												
												int joint = 0;
												if(tempAssessment!=null)
												{
													
													for(int c20=0; c20<sxs.length; c20++)
													{
														
														String[] recSplit = sxs[c20].split("/");
														log.info(">>>>>>>>>>>>>>>>>>>>>" + sxs[c20]);
														log.info(Arrays.toString(recSplit));
														
														if(tempAssessment.getRegistrationNumber().equals(recSplit[1]) && 
																Integer.toString(tempAssessment.getAssessmentYear()).equals(recSplit[2]) && 
																tempAssessment.getPorts().getPortCode().equals(recSplit[0]))
														{
															if(sxs1[c20].equalsIgnoreCase(PaymentTypeConstants.PAYMENTTYPE_INTEREST_PAYMENT.getValue()))
															{
																joint++;
															}else if(sxs1[c20].equalsIgnoreCase(PaymentTypeConstants.PAYMENTTYPE_TAXFEE_PAYMENT.getValue()))
															{
																joint++;
															}
														}
															
													}
													
													if(tempAssessment.getInterest().equals(Boolean.TRUE) && joint==2)
													{
														WorkFlowAssessment wfac = portletState.getWorkFlowPortletUtil().getWorkFlowAssessmentsByAssessment(tempAssessment, workflow);
														if(wfac!=null)
														{
															wfac.setStatus(WorkFlowConstants.WORKFLOW_STATUS_APPROVED);
															swpService.updateRecord(wfac);
															
															Assessment ast = wfac.getAssessment();
															ast.setPaidFor(Boolean.TRUE);
															swpService.updateRecord(ast);
															totalCount++;
														}
													}
													if(tempAssessment.getInterest().equals(Boolean.FALSE) && joint==1)
													{
														WorkFlowAssessment wfac = portletState.getWorkFlowPortletUtil().getWorkFlowAssessmentsByAssessment(tempAssessment, workflow);
														if(wfac!=null)
														{
															wfac.setStatus(WorkFlowConstants.WORKFLOW_STATUS_APPROVED);
															swpService.updateRecord(wfac);
															
															Assessment ast = wfac.getAssessment();
															ast.setPaidFor(Boolean.TRUE);
															swpService.updateRecord(ast);
															
															totalCount++;
														}
													}
														
												}
											}
											
										}
									
										if(totalCount==selectedAssessment.length)
										{
											workflow.setStatus(WorkFlowConstants.WORKFLOW_STATUS_APPROVED);
											workflow.setDateLastModified(new Timestamp((new Date()).getTime()));
											swpService.updateRecord(workflow);
										}
										handleAudit("WORK FLOW APPROVAL", 
											"WORK FLOW ID " + workflow.getId() + " HAS BEEN APPROVED BY " + portletState.getPortalUser().getId() , 
											new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
										portletState.addSuccess(aReq, "<span style='font-weight:100'>The following assessments have been <strong><u>paid for successfully</u></strong>.<br>Click on the links to download your payment receipt(s)</span>" + links, portletState);
										Collection<WorkFlowAssessment> wfList = portletState.getWorkFlowPortletUtil().getWorkFlowsByReceipientId(portletState.getPortalUser().getId());
										log.info("Apc collection is not null");
										Collection<WorkFlow> wfs = portletState.getWorkFlowPortletUtil().getWorkFlowListByReceipientId(portletState.getPortalUser().getId());
										log.info("Apc collection is not null");
										if(wfs!=null && wfList!=null && wfList.size()>0)
										{
											log.info("Apc collection is not null");
										//	portletState.setSelectedSearchedWorkFlow(workFlow);
											portletState.setWorkFlowAssessmentList(wfList);
											portletState.setWorkFlowList(wfs);
										}else
										{
											portletState.setWorkFlowAssessmentList(null);
											portletState.setWorkFlowList(null);
											log.info("Apc collection is not null");
											portletState.setWorkFlowAssessmentList(null);
										}
								
								}else
								{
									aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
									portletState.addError(aReq, "Payments for the selected assessments failed. Ensure you have enough funds in your account before trying again", portletState);
								}
					            
					            
				            }catch(PrivilegedActionException e1)
				            {
				            	log.info("Ok start here");
				            	e1.printStackTrace();
				            	aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
				    			portletState.addError(aReq, "Sending Request for Assessment details failed. Please check your internet connection! E189104 Error Code. Contact the bank for more details if this problems persist.", portletState);
				            }
				    		
	
				            soapConnection.close();
				        } catch (Exception e) {
				            System.err.println("Error occurred while sending SOAP Request to Server");
				            e.printStackTrace();
				            aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
			    			portletState.addError(aReq, "Parsing data returned failed! E189104 Error Code. Contact the bank for more details if this problems persist.", portletState);
				        }
						
					}else
					{
						aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
						portletState.addError(aReq, "The request to view tax assessment listings was not successful! E189101 Error Code. Contact the bank for more details if this problems persist.", portletState);
					}
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
					portletState.addError(aReq, "The request to view tax assessment listings was not successful! E189102 Error Code. Contact the bank for more details if this problems persist.", portletState);
				}
			}else
			{
				aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
				portletState.addError(aReq, "The request to view tax assessment listings was not successful! E189103 Error Code. Contact the bank for more details if this problems persist.", portletState);
			}
		}
        catch(Exception e)
		{
			log.info("IOEXception printstacktrace==");
			e.printStackTrace();
			aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
			portletState.addError(aReq, "The request to view tax assessment listings was not successful! E189104 Error Code. Contact the bank for more details if this problems persist.", portletState);
		}
		portletState.setSelectedWorkFlow(null);
	}

	
	
	private Boolean handleResponseForFundsTransfer(
			SOAPMessage soapResponse, ActionRequest aReq,
			ActionResponse aRes, WorkFlowPortletState portletState) {
		// TODO Auto-generated method stub
		SOAPBody soapBodyResponse;
		Boolean success = false;
		try {
			soapBodyResponse = soapResponse.getSOAPBody();
			if(soapBodyResponse!=null)
	        {
	        	//String resp = util.parseSOAPBodyToString(soapBodyResponse);
	        	System.out.println();
	        	System.out.println("soapBodyResponse.getNodeName();==>" + soapBodyResponse.getNodeName());
	        	
	        	NodeList nodeList = soapBodyResponse.getChildNodes();		//BODY
	        	Node node = nodeList.item(0);
	        	System.out.println("node.getNodeName();==>" + node.getNodeName());
	        	NodeList nodeList2 = node.getChildNodes();					//ExecutionIntegration
	        	Node node2 = nodeList2.item(0);
	        	System.out.println("node2.getNodeName();==>" + node2.getNodeName());
	        	NodeList nodeList3 = node2.getChildNodes();					//responseHeader, OutputData
	        	
	        	
	        	
	        	
        		Node node3 = nodeList3.item(1);								//OUTPUTDATA
        		System.out.println("node3.getNodeName();==>" + node3.getNodeName());
        		
    			NodeList nodeList4 = node3.getChildNodes();
	        	
	        		Node node4 = nodeList4.item(0);							//OUTPUTPAYLOAD
	        		System.out.println("node3==>" + node3.getNodeName() + (node4.getNodeValue()==null ? "" : " value = " + node4.getNodeValue()));
	        		
	        		NodeList nodeList5 = node4.getChildNodes();				//BALANCE RESPONSE
	        		
	        			
	        				Node node5 = nodeList5.item(0);					//ACCOUNT
			        		
	        						NodeList nodeList6 = node5.getChildNodes();				//BALANCE RESPONSE
	        						if(nodeList6.getLength()>0)
	        						{
		        						for(int c2=0; c2<nodeList6.getLength(); c2++)
		        						{
		        							Node node7 = nodeList6.item(c2);
		        							
								        		if(node7.getNodeName().equals("accountNumber"))
							        			{
							        			}
							        			if(node7.getNodeName().equals("status"))
							        			{
							        				success = (((String)node7.getNodeValue()).equalsIgnoreCase("Y") ? true : false);
							        			}
		        						}
		        						
		        						
		        						Node nodeh1 = nodeList3.item(0);								//head:responseHeader
		        						NodeList nodeListh1 = nodeh1.getChildNodes();				//BALANCE RESPONSE
		        						if(nodeListh1.getLength()>0)
		        						{
			        						for(int c2=0; c2<nodeListh1.getLength(); c2++)
			        						{
			        							Node node7 = nodeListh1.item(c2);
			        							
									        		if(node7.getNodeName().equals("head:resSourceSystem"))
								        			{
									        			//balanceInquiryResponse.setResSourceSystem((String)node7.getNodeValue());
								        			}
								        			if(node7.getNodeName().equals("head:resMessageType"))
								        			{
								        				//balanceInquiryResponse.setResMessageType((String)node7.getNodeValue());
								        			}
								        			if(node7.getNodeName().equals("head:resMessageId"))
								        			{
								        				//balanceInquiryResponse.setResMessageId((String)node7.getNodeValue());
								        			}
								        			if(node7.getNodeName().equals("head:resTimeStamp"))
								        			{
								        				//balanceInquiryResponse.setResTimeStamp((String)node7.getNodeValue());
								        			}
								        			if(node7.getNodeName().equals("head:resTrackingId"))
								        			{
								        				//balanceInquiryResponse.setResTrackingId((String)node7.getNodeValue());
								        			}
			        						}
		        						}
	        						}
	        		
	        		
	        	
	        	
	        	
	        	
	        	
	        	
	        }else
	        {
	        	System.out.println("soapBody is null");
	        	portletState.addError(aReq, "We are experiencing problems getting your bank balance. E189301 Error Code. Contact the bank for more details if this problems persist.", portletState);
	        }
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			portletState.addError(aReq, "We are experiencing problems getting your bank balance. E189302 Error Code. Contact the bank for more details if this problems persist.", portletState);
			return null;
		}
		return success;
	}
	
	
	
	private Boolean processPayment(
			WorkFlowPortletState portletState,
			ActionRequest aReq, ActionResponse aRes, String amount, String srcAccountNumber, 
			String srcAccountSortCode, String exchangeRate, String uniqueId, String recAccountNumber, 
			String recAccountSortCode, String reqTimeStamp, String entryDate, String valueDate) {
		// TODO Auto-generated method stub
		String url = "http://localhost:8086/WS/ProbaseZambiaService";
        url = "http://localhost:8085/ProbaseTestService2/ProbaseZambia";	////URL of the bank platform
        //url = "http://probasetestservice2-pallpod.rhcloud.com/ProbaseTestService2/ProbaseZambia";
        
        log.info("url1 = > ");
        log.info("url = > " + url);
        
        try
        {			
			try {
	            // Create SOAP Connection
				
	            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
	            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

	            // Send SOAP Message to SOAP Server
	            
	            SOAPMessage soapMessage = createSOAPRequestForFundsTransfer(
        				portletState,
        				aReq, aRes, amount, srcAccountNumber, 
        				srcAccountSortCode, exchangeRate, uniqueId, recAccountNumber, 
        				recAccountSortCode, reqTimeStamp, entryDate, valueDate);
	            
	            if(soapMessage!=null)
	            {
	            	SOAPMessage soapResponse = soapConnection.call(soapMessage, url);
	            	boolean success =  handleResponseForFundsTransfer(soapResponse, aReq, aRes, portletState);
		            log.info("resp size = " + success);
		            printSOAPResponse(soapResponse);
		            soapConnection.close();
		            return success;
	            }else
	            {
	            	log.info("soapMessage is null");
		            soapConnection.close();
		            return false;
	            }
	        } catch (Exception e) {
	            System.err.println("Error occurred while sending SOAP Request to Server");
	            e.printStackTrace();
	            portletState.addError(aReq, "We are experiencing problems getting your bank balance. E189304 Error Code. Contact the bank for more details if this problems persist.", portletState);
	            return null;
	        }
		}
		catch(Exception e)
		{
			log.info("IOEXception printstacktrace==");
			e.printStackTrace();
			aRes.setRenderParameter("jspPage", "/html/workflowportlet/start.jsp");
			portletState.addError(aReq, "The request to view tax break down listings was not successful! E189204 Error Code. Contact the bank for more details if this problems persist.", portletState);
			return null;
		}
	}
	
	private static void printSOAPResponse(SOAPMessage soapResponse) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source sourceContent = soapResponse.getSOAPPart().getContent();
        System.out.print("\nResponse SOAP Message = ");
        StreamResult result = new StreamResult(System.out);
        transformer.transform(sourceContent, result);
    }

	
	private SOAPMessage createSOAPRequestForFundsTransfer(
			WorkFlowPortletState portletState,
			ActionRequest aReq, ActionResponse aRes, String amount, String srcAccountNumber, 
			String srcAccountSortCode, String exchangeRate, String uniqueId, String recAccountNumber, 
			String recAccountSortCode, String reqTimeStamp, String entryDate, String valueDate) {
		// TODO Auto-generated method stub
		try
		{
	        MessageFactory messageFactory = MessageFactory.newInstance();
	        SOAPMessage soapMessage = messageFactory.createMessage();
	        SOAPPart soapPart = soapMessage.getSOAPPart();
	
	        SOAPEnvelope envelope = soapPart.getEnvelope();
	        envelope.addNamespaceDeclaration("max", "http://standardbank.com/africa/services/MaxIntegrationV1_0");
	
	        SOAPBody soapBody = envelope.getBody();
	        SOAPElement soapBodyElem = soapBody.addChildElement("ExecuteIntegration", "max");
	        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("requestHeader", "max1");
	        SOAPElement soapBodyElem2 = soapBodyElem1.addChildElement("reqSourceSystem");
	        soapBodyElem2.addTextNode("SmartPay");
	        SOAPElement soapBodyElem3 = soapBodyElem1.addChildElement("reqMessageType");
	        soapBodyElem3.addTextNode("SMARTPAY:ZM:REVENUE:ENTRY:REQUEST");
	        SOAPElement soapBodyElem4 = soapBodyElem1.addChildElement("reqMessageId");
	        soapBodyElem4.addTextNode(uniqueId);
	        SOAPElement soapBodyElem5 = soapBodyElem1.addChildElement("reqTimeStamp");
	        soapBodyElem5.addTextNode(reqTimeStamp);
	        SOAPElement soapBodyElem6 = soapBodyElem1.addChildElement("reqTrackingId");
	        soapBodyElem6.addTextNode(uniqueId);
	        
	        
	        /***input data***/

	        SOAPElement soapBodyElem10 = soapBodyElem.addChildElement("InputData");
	        SOAPElement soapBodyElem20 = soapBodyElem10.addChildElement("InputPayload");
	        SOAPElement soapBodyElem30 = soapBodyElem20.addChildElement("accountPostingRequest");
	        SOAPElement soapBodyElem31 = soapBodyElem30.addChildElement("postingType");
	        soapBodyElem31.addTextNode("balanced");
	        SOAPElement soapBodyElem32 = soapBodyElem30.addChildElement("transactionType");
	        soapBodyElem32.addTextNode("Z1");
	        SOAPElement soapBodyElem33 = soapBodyElem30.addChildElement("sourceBranchSortCode");
	        soapBodyElem33.addTextNode(srcAccountSortCode);
	        /***first - src part starts here***/
	        SOAPElement soapBodyElem40 = soapBodyElem30.addChildElement("posting");
	        SOAPElement soapBodyElem41 = soapBodyElem40.addChildElement("coreBankAccountNumber");
	        soapBodyElem41.addTextNode(srcAccountNumber);
	        SOAPElement soapBodyElem42 = soapBodyElem40.addChildElement("sourceAccountNumber");
	        soapBodyElem42.addTextNode(srcAccountNumber);
	        SOAPElement soapBodyElem43 = soapBodyElem40.addChildElement("coreBankBranchSortCode");
	        soapBodyElem43.addTextNode(srcAccountSortCode);
	        SOAPElement soapBodyElem44 = soapBodyElem40.addChildElement("currency");
	        soapBodyElem44.addTextNode(ProbaseConstants.CURRENCY);
	        SOAPElement soapBodyElem45 = soapBodyElem40.addChildElement("localCurrency");
	        soapBodyElem45.addTextNode(ProbaseConstants.CURRENCY);
	        SOAPElement soapBodyElem46 = soapBodyElem40.addChildElement("debitCreditIndicator");
	        soapBodyElem46.addTextNode("d");
	        SOAPElement soapBodyElem47 = soapBodyElem40.addChildElement("baseEquivalentAmount");
	        soapBodyElem47.addTextNode(amount);
	        SOAPElement soapBodyElem48 = soapBodyElem40.addChildElement("entryAmount");
	        soapBodyElem48.addTextNode(amount);
	        SOAPElement soapBodyElem49 = soapBodyElem40.addChildElement("exchangeRate");
	        soapBodyElem49.addTextNode(exchangeRate);
	        SOAPElement soapBodyElem50 = soapBodyElem40.addChildElement("multiplyDivideIndicator");
	        soapBodyElem50.addTextNode("m");
	        SOAPElement soapBodyElem51 = soapBodyElem40.addChildElement("entryType");
	        soapBodyElem51.addTextNode("non-position");
	        SOAPElement soapBodyElem52 = soapBodyElem40.addChildElement("entryDate");
	        soapBodyElem52.addTextNode(entryDate);
	        SOAPElement soapBodyElem53 = soapBodyElem40.addChildElement("valueDate");
	        soapBodyElem53.addTextNode(valueDate);
	        SOAPElement soapBodyElem54 = soapBodyElem40.addChildElement("sourcePostingReference");
	        soapBodyElem54.addTextNode(uniqueId);
	        SOAPElement soapBodyElem55 = soapBodyElem40.addChildElement("transactionReference");
	        soapBodyElem55.addTextNode(uniqueId);
	        SOAPElement soapBodyElem56 = soapBodyElem40.addChildElement("narrative1");
	        soapBodyElem56.addTextNode("TEST3/15571/2014");
	        SOAPElement soapBodyElem57 = soapBodyElem40.addChildElement("narrative2");
	        soapBodyElem57.addTextNode(uniqueId);
	        SOAPElement soapBodyElem58 = soapBodyElem40.addChildElement("narrative3");
	        soapBodyElem58.addTextNode(uniqueId);
	        SOAPElement soapBodyElem59 = soapBodyElem40.addChildElement("forcePost");
	        soapBodyElem59.addTextNode("N");
	        
	        
	        /***second - rec part starts here***/
	        SOAPElement soapBodyElem40_1 = soapBodyElem30.addChildElement("posting");
	        SOAPElement soapBodyElem41_1 = soapBodyElem40_1.addChildElement("coreBankAccountNumber");
	        soapBodyElem41_1.addTextNode(recAccountNumber);
	        SOAPElement soapBodyElem42_1 = soapBodyElem40_1.addChildElement("sourceAccountNumber");
	        soapBodyElem42_1.addTextNode(recAccountNumber);
	        SOAPElement soapBodyElem43_1 = soapBodyElem40_1.addChildElement("coreBankBranchSortCode");
	        soapBodyElem43_1.addTextNode(recAccountSortCode);
	        SOAPElement soapBodyElem44_1 = soapBodyElem40_1.addChildElement("currency");
	        soapBodyElem44_1.addTextNode(ProbaseConstants.CURRENCY);
	        SOAPElement soapBodyElem45_1 = soapBodyElem40_1.addChildElement("localCurrency");
	        soapBodyElem45_1.addTextNode(ProbaseConstants.CURRENCY);
	        SOAPElement soapBodyElem46_1 = soapBodyElem40_1.addChildElement("debitCreditIndicator");
	        soapBodyElem46_1.addTextNode("c");
	        SOAPElement soapBodyElem47_1 = soapBodyElem40_1.addChildElement("baseEquivalentAmount");
	        soapBodyElem47_1.addTextNode(amount);
	        SOAPElement soapBodyElem48_1 = soapBodyElem40_1.addChildElement("entryAmount");
	        soapBodyElem48_1.addTextNode(amount);
	        SOAPElement soapBodyElem49_1 = soapBodyElem40_1.addChildElement("exchangeRate");
	        soapBodyElem49_1.addTextNode(exchangeRate);
	        SOAPElement soapBodyElem50_1 = soapBodyElem40_1.addChildElement("multiplyDivideIndicator");
	        soapBodyElem50_1.addTextNode("m");
	        SOAPElement soapBodyElem51_1 = soapBodyElem40_1.addChildElement("entryType");
	        soapBodyElem51_1.addTextNode("position");
	        SOAPElement soapBodyElem52_1 = soapBodyElem40_1.addChildElement("entryDate");
	        soapBodyElem52_1.addTextNode(entryDate);
	        SOAPElement soapBodyElem53_1 = soapBodyElem40_1.addChildElement("valueDate");
	        soapBodyElem53_1.addTextNode(valueDate);
	        SOAPElement soapBodyElem54_1 = soapBodyElem40_1.addChildElement("sourcePostingReference");
	        soapBodyElem54_1.addTextNode(uniqueId);
	        SOAPElement soapBodyElem55_1 = soapBodyElem40_1.addChildElement("transactionReference");
	        soapBodyElem55_1.addTextNode(uniqueId);
	        SOAPElement soapBodyElem56_1 = soapBodyElem40_1.addChildElement("narrative1");
	        soapBodyElem56_1.addTextNode("TEST3/15571/2014");
	        SOAPElement soapBodyElem57_1 = soapBodyElem40_1.addChildElement("narrative2");
	        soapBodyElem57_1.addTextNode(uniqueId);
	        SOAPElement soapBodyElem58_1 = soapBodyElem40_1.addChildElement("narrative3");
	        soapBodyElem58_1.addTextNode(uniqueId);
	        SOAPElement soapBodyElem59_1 = soapBodyElem40_1.addChildElement("forcePost");
	        soapBodyElem59_1.addTextNode("N");
	        
	        
	        	
	        soapMessage.saveChanges();
	
	        /******Print the request message******/
	        System.out.print("Request SOAP Message = ");
	        soapMessage.writeTo(System.out);
	        System.out.println();
	
	        return soapMessage;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			MessageFactory messageFactory;
			SOAPMessage soapMessage = null;
			try {
				messageFactory = MessageFactory.newInstance();
				
				try {
					soapMessage = messageFactory.createMessage();
				} catch (SOAPException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (SOAPException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			return null;
		}
	}

	
	
	private void handleSearchForWorkFlowByToken(ActionRequest aReq,
			ActionResponse aRes, WorkFlowPortletState portletState) {
		// TODO Auto-generated method stub
		String token = aReq.getParameter("token");
		portletState.setToken(token);
		if(token!=null && token.length()>0)
		{
			WorkFlow workFlow= portletState.getWorkFlowPortletUtil().
					getWorkFlowByTokenAndReceipientId(token, portletState.getPortalUser().getId());
			
			
			if(workFlow!=null && workFlow.getId().equals(portletState.getSelectedSearchedWorkFlow().getId()))
			{
				Collection<WorkFlowAssessment> wfList = portletState.getWorkFlowPortletUtil().getWorkFlowAssessmentsByWorkFlow(workFlow);
				portletState.setSelectedSearchedWorkFlow(workFlow);
				portletState.setWorkFlowAssessmentList(wfList);
			}else
			{
//				portletState.setWorkFlowAssessmentList(null);
//				portletState.setSelectedSearchedWorkFlow(null);
				portletState.addError(aReq, "There is no WorkFlow item matching this token.", portletState);
			}
		}else
		{
			portletState.setWorkFlowAssessmentList(null);
			portletState.setSelectedSearchedWorkFlow(null);
			portletState.addError(aReq, "There is no WorkFlow item matching this token.", portletState);
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
