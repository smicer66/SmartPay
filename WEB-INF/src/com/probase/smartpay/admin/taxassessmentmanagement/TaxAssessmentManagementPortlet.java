package com.probase.smartpay.admin.taxassessmentmanagement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
import javax.portlet.PortletRequest;
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
import smartpay.entity.AuthorizePanel;
import smartpay.entity.AuthorizePanelCombination;
import smartpay.entity.Company;
import smartpay.entity.FeeDescription;
import smartpay.entity.PaymentBreakDownHistory;
import smartpay.entity.PaymentHistory;
import smartpay.entity.PortalUser;
import smartpay.entity.Ports;
import smartpay.entity.Settings;
import smartpay.entity.TaxType;
import smartpay.entity.Tokens;
import smartpay.entity.TpinInfo;
import smartpay.entity.WorkFlow;
import smartpay.entity.WorkFlowAssessment;
import smartpay.entity.enumerations.CompanyStatusConstants;
import smartpay.entity.enumerations.CompanyTypeConstants;
import smartpay.entity.enumerations.PanelTypeConstants;
import smartpay.entity.enumerations.PaymentBreakDownHistoryConstants;
import smartpay.entity.enumerations.PaymentHistoryConstants;
import smartpay.entity.enumerations.PaymentTypeConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.entity.enumerations.WorkFlowConstants;
import smartpay.service.SwpService;

import com.google.zxing.common.Collections;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.service.ServiceContext;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.probase.smartpay.admin.companymanagement.CompanyManagementPortletState;
import com.probase.smartpay.admin.taxassessmentmanagement.TaxAssessmentManagementPortlet;
import com.probase.smartpay.admin.taxassessmentmanagement.TaxAssessmentManagementPortletState;
import com.probase.smartpay.admin.taxassessmentmanagement.TaxAssessmentManagementPortletUtil;
import com.probase.smartpay.admin.taxassessmentmanagement.TaxAssessmentManagementPortletState.NAVIGATE;
import com.probase.smartpay.admin.taxassessmentmanagement.TaxAssessmentManagementPortletState.TAX_ASSESSMENT_ACTION;
import com.probase.smartpay.admin.taxassessmentmanagement.TaxAssessmentManagementPortletState.TAX_ASSESSMENT_VIEW;
import com.probase.smartpay.admin.taxassessmentmanagement.TaxAssessmentManagementPortletState.VIEW_TABS;
import com.probase.smartpay.commins.BalanceInquiry;
import com.probase.smartpay.commins.ComminsApplicationState;
import com.probase.smartpay.commins.DeclarationPaymentResult;
import com.probase.smartpay.commins.DeclarationsToBePaid;
import com.probase.smartpay.commins.Emailer;
import com.probase.smartpay.commins.FundsTransferResponse;
import com.probase.smartpay.commins.InterestPaymentResult;
import com.probase.smartpay.commins.InterestToBePaid;
import com.probase.smartpay.commins.InterestToBePay;
import com.probase.smartpay.commins.Mailer;
import com.probase.smartpay.commins.MiscToBePaid;
import com.probase.smartpay.commins.ProbaseConstants;
import com.probase.smartpay.commins.SendMail;
import com.probase.smartpay.commins.SendSms;
import com.probase.smartpay.commins.TaxBreakDownResponse;
import com.probase.smartpay.commins.TaxDetails;
import com.probase.smartpay.commins.Util;
import com.sf.primepay.smartpay13.ServiceLocator;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

/**
 * Portlet implementation class TaxAssessmentManagementPortlet
 */
public class TaxAssessmentManagementPortlet extends MVCPortlet {
 
	private Logger log = Logger.getLogger(TaxAssessmentManagementPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
	TaxAssessmentManagementPortletUtil util = TaxAssessmentManagementPortletUtil.getInstance();
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
		TaxAssessmentManagementPortletState portletState = 
				TaxAssessmentManagementPortletState.getInstance(renderRequest, renderResponse);

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
		
		TaxAssessmentManagementPortletState portletState = TaxAssessmentManagementPortletState.getInstance(aReq, aRes);
		
		
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
       
        boolean proceed = true;
        if(portletState.getPortalUser().getCompany()!=null && 
        		(portletState.getPortalUser().getCompany().getStatus().equals(CompanyStatusConstants.COMPANY_STATUS_DELETED)) || 
        		(portletState.getPortalUser().getCompany().getStatus().equals(CompanyStatusConstants.COMPANY_STATUS_INACTIVE)) || 
        		(portletState.getPortalUser().getCompany().getStatus().equals(CompanyStatusConstants.COMPANY_STATUS_SUSPENDED)))
        {
        	proceed =false;
        }
        
        if(proceed)
        {
	        if(action.equalsIgnoreCase(TAX_ASSESSMENT_ACTION.VIEW_A_TAX_ASSESSMENT_SIMPLE.name()))
	        {
	        	log.info("handle create a fee description post action");
	        	handleSimpleTaxAssessmentListing(aReq, aRes, portletState);
	        }if(action.equalsIgnoreCase(TAX_ASSESSMENT_ACTION.VIEW_A_TAX_ASSESSMENT_ADV.name()))
	        {
	        	handleAdvTaxAssessmentListing(aReq, aRes, portletState);
	        }if(action.equalsIgnoreCase(TAX_ASSESSMENT_ACTION.HANDLE_ASSESSMENT_LISTING.name()))
	        {
	        	handleTaxAssessmentAction(aReq, aRes, portletState);
	        }
        }else
        {
        	portletState.setBalanceInquiry(null);
			portletState.setTaxBreakDownList(null);
			portletState.setAllAssessmentListing(null);
			aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
			portletState.addError(aReq, "You can not carry out an action as your company on this platform is not active. Contact " +
					"the bank to activate your company for " + portletState.getApplicationName().getValue() + " payments", portletState);
        }
        
		
	}


	private void handleTaxAssessmentAction(ActionRequest aReq,
			ActionResponse aRes,
			TaxAssessmentManagementPortletState portletState) {
		// TODO Auto-generated method stub
		log.info("handleTaxAssessmentAction");
		String action = aReq.getParameter("selectedAssessmentAction");
		log.info("action === " + action);
		String[] selectedAssessment = aReq.getParameterValues("selectAllCheckbox");
		if(selectedAssessment!=null)
		{
			log.info("selectedAssessment size = " + selectedAssessment.length);
		}else
		{
			//String selectedAssessments = aReq.getParameter("selectedAssessments");
			String selectedAssessments = portletState.getSelectedAssessmentsClicked();
			if(selectedAssessments!=null && selectedAssessments.length()>0)
			{
				selectedAssessment = selectedAssessments.split(", ");
			}
		}
		if(action!=null && (action.equalsIgnoreCase("getBalance")))
		{
			ComminsApplicationState cas = portletState.getCas();
			BalanceInquiry balanceInquiry = null;
//			if(cas.getDemoMode().equals(Boolean.TRUE))
//			{
//				balanceInquiry = cas.getBalanceInquiry(portletState.getPortalUser(), swpService);
//			}else
//			{
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
//			}
			
			if(balanceInquiry!=null)
			{
				portletState.setBalanceInquiry(balanceInquiry);
				aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
			}else
			{
				portletState.setBalanceInquiry(null);
				aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
				portletState.addError(aReq, "We are experiencing problems getting your bank balance. E189301 Error Code. Contact the bank for more details if this problems persist.", portletState);
			}
		}else if(action!=null && (action.equalsIgnoreCase("getBalance1")))
		{
			ComminsApplicationState cas = portletState.getCas();
			BalanceInquiry balanceInquiry = null;
//			if(cas.getDemoMode().equals(Boolean.TRUE))
//			{
//				balanceInquiry = cas.getBalanceInquiry(portletState.getPortalUser(), swpService);
//			}else
//			{
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
//			}
			
			if(balanceInquiry!=null)
			{
				portletState.setBalanceInquiry(balanceInquiry);
				aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
			}else
			{
				portletState.setBalanceInquiry(null);
				aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
				portletState.addError(aReq, "We are experiencing problems getting your bank balance. E189301 Error Code. Contact the bank for more details if this problems persist.", portletState);
			}
		}
		else if(action!=null && (action.equalsIgnoreCase("goBackToAssessments")))
		{
			//BalanceInquiry balanceInquiry = getBalanceInquiry(portletState, aReq, aRes);
			//portletState.setBalanceInquiry(null);
			//portletState.setTaxBreakDownList(null);
			aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
		}
		
		else {
			if(selectedAssessment!=null && selectedAssessment.length>0)
			{
				log.info("selectedAssessment size = " + selectedAssessment.length);
				
				if(action!=null && (action.equalsIgnoreCase("pay")))
				{
					handleNewPay(aReq, aRes, portletState, selectedAssessment);
					portletState.setAllWorkFlowAssessments(portletState.getTaxAssessmentManagementPortletUtil().getAssessmentsPaidByCompany(portletState.getPortalUser().getCompany()));
					//handlePay(aReq, aRes, portletState);
					
				}
				if(action!=null && (action.equalsIgnoreCase("breakdown") || action.equalsIgnoreCase("breakdownSpecific")))
				{
					//portletState.setPlatformFlag("1");
					//portletState.setBalanceInquiry(null);
					handleBreakdownOfAssessments(aReq, aRes, portletState);
				}
				if(action!=null && (action.equalsIgnoreCase("initiatePayment")))
				{
					handleInitiatePayment(aReq, aRes, portletState);
					portletState.setAllWorkFlowAssessments(portletState.getTaxAssessmentManagementPortletUtil().getAssessmentsPaidByCompany(portletState.getPortalUser().getCompany()));
				}
			}else
			{
				if(action!=null && (!action.equalsIgnoreCase("getBalance")))
				{
					portletState.addError(aReq, "Select at least an assessment before clicking on a button", portletState);
					aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
				}
				
				
			}
		}
	}

	private void handleNewPay(ActionRequest aReq, ActionResponse aRes,
			TaxAssessmentManagementPortletState portletState, String[] selectedAssessment) {
		// TODO Auto-generated method stub
		ComminsApplicationState cas = portletState.getCas();
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		
		if(portletState.getPortalUser().getCompany().getMandatePanelsOn()!=null && 
				portletState.getPortalUser().getCompany().getMandatePanelsOn().equals(Boolean.TRUE))
		{
			aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
			portletState.addError(aReq, "You can not pay for the selected assessments as your company's setup requires payments to pass through an approval workflow. " +
					"Click on the - Add Selected Assessments to Workflow Tray - button to do this.", portletState);
		}else
		{
			HashMap<String,Boolean> hashMap = Util.payDirect(cas, swpService, portletState.getPortalUser().getCompany().getId(), portletState.getAllAssessmentListing(), 
					selectedAssessment, portletState.getPortalUser(), portletState.getRemoteIPAddress(), 
					portletState.getSettingsZRAAccount(), portletState.getSettingsZRASortCode(), 
					portletState.getPlatformBank().getValue(), 
					portletState.getBankName().getValue(), emailer, 
					portletState.getApplicationName().getValue(), 
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
				aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
				
				
				/*******SuccessStr handled***/
				//(receiptNumber + ":::" + paymentHistory1.getPaymentType().getValue() + ":::" + paymentHistory1.getAssessment().getRegistrationNumber());
				String[] sxs = successStr.substring(0, successStr.length() - 2).split(", ");	//recNo
				String[] sxs1 = successStr1.substring(0, successStr1.length() - 2).split(", ");	//payType
				String[] sxs2 = successStr2.substring(0, successStr2.length() - 2).split(", ");	//assRegNo
				String[] sxs3 = successStr3.substring(0, successStr3.length() - 2).split(", ");	//Amt
				
				
				String links = "";
				String paymentTyper = "N/A";
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
							
							Assessment ass_ = portletState.getTaxAssessmentManagementPortletUtil().getPaymentHistoryByTranxId(sxs[c]);
							if(ass_!=null)
							{
//								links = links + "<span style='font-weight:100'><br>" +
//									"<strong>Assessment Registration Number:</strong> " + sxs2[c] + "<br>" +
//									"<strong>Payment Type:</strong> " +  paymentTyper + "<br>" +
//									"<strong>Amount Paid:</strong> ZMW " + new Util().roundUpAmount(Double.valueOf(sxs3[c])) + "<br>" +
//									"<strong>Download Link:</strong><a target='_blank' href='/eTax-portlet/ActiveServlet?action=downloadLumpSlip&assessmentId=" + 
//									ass_.getId() + "&amount=" + sxs3[c] + "'>Download Receipt</a></span><br>";
								links = links + "<span style='font-weight:100'><br>" +
										"<strong>Receipt Number:</strong> " + sxs[c] + "<br>" +
										"<strong>Assessment Registration Number:</strong> " + sxs2[c] + "<br>" +
										"<strong>Payment Type:</strong> " +  paymentTyper + "<br>" +
										"<strong>Amount Paid:</strong> ZMW " + new Util().roundUpAmount(Double.valueOf(sxs3[c])) + "<br>" +
										"<strong>Download Link:</strong><a target='_blank' href='/ProbaseSmartPay-portlet/ActiveServlet?action=downloadLumpSlip&assessmentId=" + 
										ass_.getId() + "&amount=" + sxs3[c] + "'>Download Receipt</a></span><br>";
							}
							
					}
					for(int c5=0; c5<selectedAssessment.length; c5++)
					{
						String[] selectedAssessmentSplit = selectedAssessment[c5].split("/");
						log.info(Arrays.toString(selectedAssessmentSplit));
						Integer yr = null;
						Long portId = null;
						try{
								yr = Integer.valueOf(selectedAssessmentSplit[1]);
								portId = Long.valueOf(selectedAssessmentSplit[2]);
						}catch(NumberFormatException e)
						{
							e.printStackTrace();
						}
						
						TpinInfo ti = portletState.getTaxAssessmentManagementPortletUtil().getTPINInfoByCompany(portletState.getPortalUser().getCompany().getId());
						Assessment tempAssessment = portletState.getTaxAssessmentManagementPortletUtil().getAssessmentByRegNoAssessmentYearAndPortAndTpin(
								selectedAssessmentSplit[0], 
								yr, portId, ti.getId());
						log.info("tempAssessment ==" + tempAssessment.getAssessmentNumber());
						//as.getRegistrationNumber() + "/" + as.getAssessmentYear() + "/" + as.getPorts().getId()
						//(receiptNumber + ":::" + paymentHistory1.getPaymentType().getValue() + ":::" + paymentHistory1.getAssessment().getRegistrationNumber());
//						String receiptNumber = paymentHistory1.getAssessment().getPorts().getPortCode() + 
//            					"/" + paymentHistory1.getAssessment().getRegistrationNumber() + 
//            					"/" + paymentHistory1.getAssessment().getAssessmentYear() + 
//            					"/" + interestPaymentResultList.getReceiptNumber();
						
						int joint = 0;
						if(tempAssessment!=null)
						{
							
							for(int c=0; c<sxs.length; c++)
							{
								
								String[] recSplit = sxs[c].split("/");
								
								
								log.info("recSplit ==" + Arrays.toString(recSplit));
								log.info("tempAssessment.getRegistrationNumber() ==" + tempAssessment.getRegistrationNumber());
								log.info("tempAssessment.getAssessmentYear() ==" + tempAssessment.getAssessmentYear());
								log.info("tempAssessment.getPorts().getPortCode() ==" + tempAssessment.getPorts().getPortCode());
								

								log.info("recSplit[1] ==" + recSplit[1]);
								log.info("recSplit[2] ==" + recSplit[2]);
								log.info("recSplit[0] ==" + recSplit[0]);
								if(tempAssessment.getRegistrationNumber().equals(recSplit[1]) && 
										Integer.toString(tempAssessment.getAssessmentYear()).equals(recSplit[2]) && 
										tempAssessment.getPorts().getPortCode().equals(recSplit[0]))
								{
									if(sxs1[c].equalsIgnoreCase(PaymentTypeConstants.PAYMENTTYPE_INTEREST_PAYMENT.getValue()))
									{
										joint++;
									}else if(sxs1[c].equalsIgnoreCase(PaymentTypeConstants.PAYMENTTYPE_TAXFEE_PAYMENT.getValue()))
									{
										joint++;
									}
								}
									
							}
							
							if(tempAssessment.getInterest().equals(Boolean.TRUE) && joint==2)
							{
								tempAssessment.setPaidFor(Boolean.TRUE);
								swpService.updateRecord(tempAssessment);
								
							}
							if(tempAssessment.getInterest().equals(Boolean.FALSE) && joint==1)
							{
								tempAssessment.setPaidFor(Boolean.TRUE);
								swpService.updateRecord(tempAssessment);
								
							}
								
						}
						
					}
				
					BalanceInquiry balanceInquiry = null;
//					if(portletState.getCas().getDemoMode().equals(Boolean.TRUE))
//					{
//						balanceInquiry = cas.getBalanceInquiry(portletState.getPortalUser(), swpService);
//					}else
//					{
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
//					}
//					if(portletState.getCas().getDemoMode()==false)
//					{
						portletState.setBalanceInquiry(balanceInquiry);
						loadTaxAssessments(aReq, aRes, portletState);
//					}else
//					{
//						portletState.setBalanceInquiry(balanceInquiry);
//						loadTaxAssessmentsDemo(aReq, aRes, portletState);
//					}
				portletState.addSuccess(aReq, "<span style='font-weight:100'>The following assessments have been <strong><u>paid for successfully</u></strong>.<br>Click on the links to download your payment receipt(s)</span>" + links, portletState);
				
			}else
			{
				aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
				portletState.addError(aReq, "Payments for the selected assessments failed. Ensure you have enough funds in your account before trying again", portletState);
			}
		}
	}

	private void handleInitiatePayment(ActionRequest aReq,
			ActionResponse aRes,
			TaxAssessmentManagementPortletState portletState) {
		// TODO Auto-generated method stub
		log.info("handleTaxAssessmentAction");
		String action_ = aReq.getParameter("selectedAssessmentAction");
		log.info("action === " + action_);
		String[] selectedAssessment = aReq.getParameter("selectedAssessmentsClicked").trim().split(", ");
		log.info("selectedAssessment size = " + selectedAssessment.length);
		//log.info("selected assessments = " + aReq.getParameter("selectedAssessmentsClicked").trim());
		if(aReq.getParameterValues("selectAllCheckbox")!=null)
		{
			selectedAssessment = aReq.getParameterValues("selectAllCheckbox");
		}
		portletState.setTaxBreakDownList(null);
		log.info("break");
		ArrayList<String> allList = new ArrayList<String>();
		
		
		if(portletState.getPortalUser().getCompany().getMandatePanelsOn()!=null && portletState.getPortalUser().getCompany().getMandatePanelsOn().equals(Boolean.TRUE))
		{
			doHandleInitiatePaymentToWorkFlow(aReq, aRes, portletState, selectedAssessment, allList,  action_);
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
			if(portletState.getPortalUser().getCompany().getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY))
			{
				portletState.addError(aReq, "You cannot add the selected assessments to your companys workflow tray as workflow capabilities have been turned off for your company. You may rather proceed to pay directly by clicking " +
						"on the -Proceed to Pay for Selected Assessments- button", portletState);
			}else
			{
				portletState.addError(aReq, "You cannot add the selected assessments to your companys workflow tray. You may rather proceed to pay directly by clicking " +
					"on the -Proceed to Pay for Selected Assessments- button", portletState);
			}
		}
		
		
		
	}
	
	
	
	
	private void doHandleInitiatePaymentToWorkFlow(ActionRequest aReq,
			ActionResponse aRes,
			TaxAssessmentManagementPortletState portletState,
			String[] selectedAssessment, ArrayList<String> allList,
			String action_) {
		// TODO Auto-generated method stub
		
		Double amount = 0.00;
		for(int c=0; c<selectedAssessment.length; c++)
		{
			Collection<Assessment> allAssessmentList = portletState.getAllAssessmentListing();
			log.info("111allAssessmentList size = " + (allAssessmentList!=null ? allAssessmentList.size() : "null"));
			
			for(Iterator<Assessment> iterAss = allAssessmentList.iterator(); iterAss.hasNext();)
			{
				
				Assessment assessment = iterAss.next();
				log.info("1111assessment = " + assessment==null ? "N/A" : assessment.getAmount());
				log.info("111selectedAssessment[c] = " + selectedAssessment[c]==null ? "N/A" : selectedAssessment[c]);
				log.info("111assessment.getRegistrationNumber() = " + assessment.getRegistrationNumber()==null ? "N/A" : assessment.getRegistrationNumber());
				log.info("112>>>>" + assessment.getRegistrationNumber());
				log.info("221>>>>" + assessment.getRegistrationNumber());
				if(assessment.getRegistrationNumber().equals(selectedAssessment[c].split("/")[0].trim()) && 
						assessment.getAssessmentYear().equals(Integer.valueOf(selectedAssessment[c].split("/")[1].trim())) && 
						assessment.getPorts().getId().equals(Long.valueOf(selectedAssessment[c].split("/")[2].trim())))
				{
					amount = amount + assessment.getAmount();
				}
			}
		}
		
		
		Collection<AuthorizePanelCombination> apcList = Util.getAuthorizePanelOfInitiatorTypeForPortalUser(swpService, amount, SmartPayConstants.AUTHORIZE_PANEL_COMBINATION_STATUS_ACTIVE, 
				PanelTypeConstants.AUTHORIZE_PANEL_TYPE_INITIATOR, portletState.getPortalUser());
		Collection<AuthorizePanelCombination> apc = portletState.getTaxAssessmentManagementPortletUtil().getNextInWorkFlow(portletState, amount);
		
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
						Collection<Assessment> allAssessmentList = portletState.getAllAssessmentListing();
						log.info("allAssessmentList size = " + allAssessmentList.size());
						
						for(Iterator<Assessment> iterAss = allAssessmentList.iterator(); iterAss.hasNext();)
						{
							
							Assessment assessment = iterAss.next();
							log.info("assessment = " + assessment==null ? "N/A" : assessment.getAmount());
							log.info("selectedAssessment[c] = " + selectedAssessment[c]==null ? "N/A" : selectedAssessment[c]);
							log.info("assessment.getRegistrationNumber() = " + assessment.getRegistrationNumber()==null ? "N/A" : assessment.getRegistrationNumber());
							log.info("1>>>>" + assessment.getRegistrationNumber());
							log.info("2>>>>" + assessment.getRegistrationNumber());
							if(assessment.getRegistrationNumber().equals(selectedAssessment[c].split("/")[0].trim()) && 
									assessment.getAssessmentYear().equals(Integer.valueOf(selectedAssessment[c].split("/")[1].trim())) && 
									assessment.getPorts().getId().equals(Long.valueOf(selectedAssessment[c].split("/")[2].trim())))
							{

								log.info("3>>>>both are equal");
								
								Assessment assessmentInDb = portletState.getTaxAssessmentManagementPortletUtil().getAssessmentByRegNo(assessment.getRegistrationNumber(), portletState.getPortalUser().getCompany().getId());
								if(assessmentInDb!=null)
								{
									//UPATE ASSESSEMENT TO NEW SELECTED ASSESSMENT
									assessment = new Util().updateAssessments(assessment, assessmentInDb, portletState.getPortalUser());
									swpService.updateRecord(assessment);
									handleAudit("UPDATE ASSESSMENT", "UPDATE ASSESSMENT WITH ID " + assessment.getId(), 
											new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
									success = true;
									WorkFlowAssessment wfa= new Util().addThisAssessmentToWorkFlow(assessment, workFlow, swpService);
									if(wfa!=null)
										handleAudit("Create WorkFlowAssessment", "Create New WorkFlowAssessment with Id " + wfa.getId(), 
											new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), 
											portletState.getPortalUser().getUserId());
									
								}else
								{
									//CREATE NEW ASSESSMENT SINCE NONE EXISTS
									assessment = (Assessment)swpService.createNewRecord(assessment);
									log.info("5>>>>create new assessment" + assessment.getId());
									
									handleAudit("CREATE ASSESSMENT", "CREATE ASSESSMENT WITH ID " + assessment.getId(), 
											new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
									WorkFlowAssessment wfa= new Util().addThisAssessmentToWorkFlow(assessment, workFlow, swpService);

									if(wfa!=null)
									{
										success = true;
										handleAudit("Create WorkFlowAssessment", "Create New WorkFlowAssessment with Id " + wfa.getId(), 
											new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), 
											portletState.getPortalUser().getUserId());
									}
								}
								
								if(success)
								{
									count++;
									assessmentlistString.add(assessment.getRegistrationNumber());
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
							"Work Flow Item - Request for Approval of Payment for Assessment - ", portletState.getApplicationName().getValue());
					
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
								"Work Flow Item - Request for Approval of Payment for Assessment - ", portletState.getApplicationName().getValue());
							
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
								"Work Flow Item - Request for Approval of Payment for Assessments - ", portletState.getApplicationName().getValue());
							
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
					list = list + "Assessment Registration Number: " + assessmentlistString.get(c) + "<br>";
				}
				aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
				portletState.addSuccess(aReq, "The following Assessment payments have been " +
						"added to the approval workflow.<br>" + list, portletState);
				
				
			}else
			{
				aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
				portletState.addError(aReq, "Adding Assessments to the approval workflow failed. You do not belong to any valid authorization " +
							"mandate panels for the assessment amounts you wish to pay for. Request the bank or your company administrator for appropriate rights", portletState);
				
			}
			
			
		}else
		{ 
			aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
			portletState.addError(aReq, "You cannot add this assessment to your company's workflow. Request your " +
					"company administrator to add you to an authorization panel of initiator type " +
					"having enough funds to cover the total amount of the assessments you selected", portletState);
		}
		

		
	}

	

	private void handleBreakdownOfAssessments(ActionRequest aReq,
			ActionResponse aRes,
			TaxAssessmentManagementPortletState portletState) {
		// TODO Auto-generated method stub
		log.info("handleTaxAssessmentAction");
		String action = aReq.getParameter("selectedAssessmentAction");
		log.info("action === " + action);
		String[] selectedAssessment = aReq.getParameterValues("selectAllCheckbox");
		log.info("selectedAssessment size = " + selectedAssessment.length);
		String listSelected = "";
		for(int x=0; x<selectedAssessment.length; x++)
		{
			listSelected = listSelected + selectedAssessment[x] + ", ";
		}
		portletState.setSelectedAssessmentsClicked(null);
		portletState.setSelectedAssessmentsClicked(listSelected.substring(0, listSelected.length() - 2));
		portletState.setTaxBreakDownList(null);
		log.info("break");
		
		
		
//		if(portletState.getCas().getDemoMode()==true)
//		{
//			log.info("portletState.getSelectedAssessmentsClicked() ==" + portletState.getSelectedAssessmentsClicked());
//			String[] assessmentsList = portletState.getSelectedAssessmentsClicked().split(", ");
//			List<HashMap> allList1 = new ArrayList<HashMap>();
//			portletState.setTaxBreakDownList(null);
//			for(int c = 0; c<assessmentsList.length; c++)
//			{
//				String[] assessmentsDet = assessmentsList[c].split("/");
//				//Long assId=Long.valueOf(assessmentsDet[0]);
//				Integer yr = null;
//				Long portId = null;
//				try
//				{
//					yr = Integer.valueOf(assessmentsDet[1]);
//					portId = Long.valueOf(assessmentsDet[2]);
//					
//					Assessment assessment = null;
//					for(Iterator<Assessment> it = portletState.getAllAssessmentListing().iterator(); it.hasNext();)
//					{
//						Assessment asmt = it.next();
//						if(asmt.getRegistrationNumber().equals(assessmentsDet[0]) && 
//								asmt.getAssessmentYear().equals(yr) && 
//								asmt.getPorts().getId().equals(portId))
//						{
//							assessment = asmt;
//						}
//					}
//					
//					ArrayList<TaxBreakDownResponse> allList = portletState.getCas().getTaxBreakDown1(
//							portletState.getPortalUser(), assessment);
//					if(allList!=null)
//					{
//						HashMap<String, ArrayList<TaxBreakDownResponse>> newHashList = new HashMap<String, ArrayList<TaxBreakDownResponse>>();
//						newHashList.put(assessment.getRegistrationNumber(), allList);
//						allList1.add(newHashList);
//						portletState.setTaxBreakDownList(allList1);
//					}
//				}catch(NumberFormatException e)
//				{
//					e.printStackTrace();
//				}
//				
//			}
//			if(portletState.getTaxBreakDownList()!=null && portletState.getTaxBreakDownList().size()>0)
//			{
//				aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxbreakdown.jsp");
//			}else
//			{
//				portletState.addError(aReq, "We could not get a breakdown of your selected assessment. Please try again!", portletState);
//				aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
//			}
//			
//			
//		}else
//		{
			List<HashMap> allList = Util.taxbreakdown(selectedAssessment, portletState.getAllAssessmentListing(), portletState.getPlatformBank().getValue());
			portletState.setTaxBreakDownList(allList);
			
			if(allList!=null && allList.size()>0)
			{
				aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxbreakdown.jsp");
			}else
			{
				portletState.addError(aReq, "We could not get a breakdown of your selected assessment. Please try again!", portletState);
				aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
			}
//		}
		log.info("Taxbreakdown list size = " + portletState.getTaxBreakDownList().size());
		
		

	}
	
	
	
	
	
	
	
	
	
	
	private Collection<MiscToBePaid> handleGetMiscOfUnPaidAssessments(String tpin_declarantcode, 
			ActionRequest aReq, ActionResponse aRes, String bankCode, TaxAssessmentManagementPortletState portletState)
	{
		
		log.info("tpin_declarantcode" + tpin_declarantcode);	
		//since we are not searching specifically for one client it becomes empty
		//since we are not searching specifically for one port it becomes empty
		String url = "http://localhost:8086/WS/ProbaseZambiaService";
        url = "http://localhost:8085/ProbaseTestService2/ProbaseZambia";
       // url = "http://probasetestservice2-pallpod.rhcloud.com/ProbaseTestService2/ProbaseZambia";
        //url = "http://probasetestservice2-pallpod.rhcloud.com/ProbaseTestService2/ProbaseZambia";
        log.info("url1 = > ");
        log.info("url = > " + url);
        Collection<MiscToBePaid> miscListing = null;
        
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
			            soapResponse = soapConnection.call(
			            		new Util().createSOAPRequestForGetMiscOfUnPaidAssessmentsByDeclarantCode(tpin_declarantcode, bankCode), url);

			            miscListing = new Util().handleResponseForGetMiscResponse(soapResponse, swpService);
			            
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

        return miscListing;
	}
	
	
	
	
	
	

	private void getInterestOnAssessment(TaxAssessmentManagementPortletState portletState, String bankCode,
			ActionRequest aReq, ActionResponse aRes) {
		// TODO Auto-generated method stub
		String declarantFlag = null;
		
		Company company = portletState.getPortalUser().getCompany();
		TpinInfo tpinInfo = portletState.getTaxAssessmentManagementPortletUtil().getTPINInfoByCompany(company.getId());
		String tpin_declarantcode = null;
		tpin_declarantcode = tpinInfo.getTpin();
		
		log.info("tpin_declarantcode" + tpin_declarantcode);
		String country = portletState.getPlatformCountry().getValue();
		log.info("bankCode" + bankCode);
		String url = "http://localhost:8086/WS/ProbaseZambiaService";
        url = "http://localhost:8085/ProbaseTestService2/ProbaseZambia";
       // url = "http://probasetestservice2-pallpod.rhcloud.com/ProbaseTestService2/ProbaseZambia";
        //url = "http://probasetestservice2-pallpod.rhcloud.com/ProbaseTestService2/ProbaseZambia";
        log.info("url1 = > ");
        log.info("url = > " + url);
        
        try
        {
			
			if(tpin_declarantcode!=null && tpin_declarantcode.length()>0)
			{
				if(bankCode!=null && bankCode.length()>0)
				{
					
					try {
			            // Create SOAP Connection
						
			            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			            // Send SOAP Message to SOAP Server
			            try
			            {
				            SOAPMessage soapResponse = soapConnection.call(
				            		new Util().createSOAPRequestForGetInterestOfUnPaidAssessmentsByDeclarantCode(
				            				tpin_declarantcode, bankCode), url);

//				            Collection<InterestOnAssessment> assessmentListing = 
//				            		new Util().handleResponseForGetInterestOfUnPaidAssessmentsByDeclarantCode(soapResponse, swpService);
//				            portletState.setAllInterestOnAssessments(assessmentListing);
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
				}
			}else
			{
				
			}
		}
        catch(Exception e)
		{
			log.info("IOEXception printstacktrace==");
			e.printStackTrace();
		}
	}

	
	
	
	private BalanceInquiry handleResponseForBalanceInquiry(
			SOAPMessage soapResponse, ActionRequest aReq,
			ActionResponse aRes, TaxAssessmentManagementPortletState portletState) {
		// TODO Auto-generated method stub
		SOAPBody soapBodyResponse;
		BalanceInquiry balanceInquiryResponse = null;
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
	        							balanceInquiryResponse = new BalanceInquiry();
		        						for(int c2=0; c2<nodeList6.getLength(); c2++)
		        						{
		        							Node node7 = nodeList6.item(c2);
		        							
								        		if(node7.getNodeName().equals("accountNumber"))
							        			{
								        			balanceInquiryResponse.setAccountNumber((String)node7.getNodeValue());
							        			}
							        			if(node7.getNodeName().equals("currency"))
							        			{
							        				balanceInquiryResponse.setCurrency((String)node7.getNodeValue());
							        			}
							        			if(node7.getNodeName().equals("availableBalance"))
							        			{
							        				try{
							        				balanceInquiryResponse.setAvailableBalance(Double.valueOf((String)node7.getNodeValue()));}
							        				catch(NumberFormatException e)
							        				{
							        					e.printStackTrace();
							        					balanceInquiryResponse.setAvailableBalance(null);
							        				}
							        			}
							        			if(node7.getNodeName().equals("type"))
							        			{
							        				balanceInquiryResponse.setType((String)node7.getNodeValue());
							        			}
							        			if(node7.getNodeName().equals("status"))
							        			{
							        				balanceInquiryResponse.setStatus((String)node7.getNodeValue());
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
									        			balanceInquiryResponse.setResSourceSystem((String)node7.getNodeValue());
								        			}
								        			if(node7.getNodeName().equals("head:resMessageType"))
								        			{
								        				balanceInquiryResponse.setResMessageType((String)node7.getNodeValue());
								        			}
								        			if(node7.getNodeName().equals("head:resMessageId"))
								        			{
								        				balanceInquiryResponse.setResMessageId((String)node7.getNodeValue());
								        			}
								        			if(node7.getNodeName().equals("head:resTimeStamp"))
								        			{
								        				balanceInquiryResponse.setResTimeStamp((String)node7.getNodeValue());
								        			}
								        			if(node7.getNodeName().equals("head:resTrackingId"))
								        			{
								        				balanceInquiryResponse.setResTrackingId((String)node7.getNodeValue());
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
		return balanceInquiryResponse;
	}
	
	
	
	
	
	
	

	private void handleSimpleTaxAssessmentListing(ActionRequest aReq,
			ActionResponse aRes, TaxAssessmentManagementPortletState portletState) {
		// TODO Auto-generated method stub
//		if(portletState.getCas().getDemoMode()==false)
//		{
//			loadTaxAssessmentsDemo(aReq, aRes, portletState);
//		}else
//		{
			loadTaxAssessments(aReq, aRes, portletState);
//		}
	}
	
	
	public void loadTaxAssessmentsDemo(ActionRequest aReq, ActionResponse aRes, TaxAssessmentManagementPortletState portletState)
	{
		
		String declarantFlag = null;
		if(portletState.getPortalUser().getCompany().getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY))			
			declarantFlag = "Y";
		else if(portletState.getPortalUser().getCompany().getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_RETAIL_COMPANY))			
			declarantFlag = "N";
		
		log.info("declarantFlag" + declarantFlag);
		Company company = portletState.getPortalUser().getCompany();
		TpinInfo tpinInfo = portletState.getTaxAssessmentManagementPortletUtil().getTPINInfoByCompany(company.getId());
		String tpin_declarantcode = null;
		tpin_declarantcode = tpinInfo.getTpin();
		
		log.info("tpin_declarantcode" + tpin_declarantcode);
		String platformFlag = aReq.getParameter("platformFlag")==null ? (portletState.getPlatformFlag()== null ? null : portletState.getPlatformFlag()) : aReq.getParameter("platformFlag");
		log.info("platformFlag" + platformFlag);
		portletState.setPlatformFlag(platformFlag);
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
						
//						if(portletState.getCas().getDemoMode()==true)
//						{
//							Collection<Assessment> assessmentList = portletState.getCas().getAssessmentList(portletState.getPortalUser(), swpService);
//		            		portletState.setAllAssessmentListing(assessmentList);
//		            		
//		            		BalanceInquiry bi = portletState.getCas().getBalanceInquiry(portletState.getPortalUser(), swpService);
//		            		portletState.setBalanceInquiry(bi);
//		            		portletState.setAllWorkFlowAssessments(portletState.getTaxAssessmentManagementPortletUtil().getAssessmentsPaidByCompany(portletState.getPortalUser().getCompany()));
//		            		aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
//				            
//						}else
//						{
							try {
					            // Create SOAP Connection
								
					            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
					            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
		
					            // Send SOAP Message to SOAP Server
					            try
					            {
						            SOAPMessage soapResponse = soapConnection.call(
						            		new Util().createSOAPRequestForGetAssessmentDetailsForDemo(declarantFlag, tpin_declarantcode, platformFlag, clientTaxPayerID, 
						            				portOfEntry, assessmentYear, source, sourceID, country), url);
		
						            Collection<Assessment> assessmentListing = new Util().handleResponseForGetAssessmentDetailsForDemo(soapResponse, swpService);
						            portletState.setAllAssessmentListing(assessmentListing);
						            //<?xml version="1.0" encoding="UTF-8" standalone="no"?><S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/"><S:Body><ns2:getAssessmentDetailsResponse xmlns:ns2="http://testservice.probase.com/"><clientTPIN/><country>ZM</country><reasonCode>0</reasonCode><reasonDescription>Successful</reasonDescription><source>S2B</source><sourceID>123456</sourceID><timestamp>Sat May 10 22:07:36 EDT 2014</timestamp><tpin_declarantCode>1000009294</tpin_declarantCode><type>getAssessmentDetails</type></ns2:getAssessmentDetailsResponse></S:Body></S:Envelope>
						            
						            // Process the SOAP Response
						            new Util().printSOAPResponse(soapResponse);
					            }catch(PrivilegedActionException e1)
					            {
					            	log.info("Ok start here");
					            	e1.printStackTrace();
					            	aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
					    			portletState.addError(aReq, "Sending Request for Assessment details failed. Please check your internet connection! E189104 Error Code. Contact the bank for more details if this problems persist.", portletState);
					            }
					    		
		
					            soapConnection.close();
					        } catch (Exception e) {
					            System.err.println("Error occurred while sending SOAP Request to Server");
					            e.printStackTrace();
					            aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
				    			portletState.addError(aReq, "Parsing data returned failed! E189104 Error Code. Contact the bank for more details if this problems persist.", portletState);
				    			
					        }
//						}
						
					}else
					{
						aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
						portletState.addError(aReq, "12The request to view tax assessments listings was not successful! E189101 Error Code. Contact the bank for more details if this problems persist.", portletState);
						log.info("12");
					}
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
					portletState.addError(aReq, "14The request to view tax assessment listing was not successful! E189102 Error Code. Contact the bank for more details if this problems persist.", portletState);
					log.info("13");
				}
			}else
			{
				aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
				portletState.addError(aReq, "15The request to view tax assessment listings was not successful! E189103 Error Code. Contact the bank for more details if this problems persist.", portletState);
				log.info("14");
			}
		}
        catch(Exception e)
		{
			log.info("IOEXception printstacktrace==");
			e.printStackTrace();
			aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
			portletState.addError(aReq, "1The request to view tax assessment listings was not successful! E189104 Error Code. Contact the bank for more details if this problems persist.", portletState);
			log.info("15");
		}
	}
	
	private void handleAdvTaxAssessmentListing(ActionRequest aReq, ActionResponse aRes,
			TaxAssessmentManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String portOfEntry = "";
		String assessmentYear = "";
		String clientTPin = "";
		if(aReq.getParameter("portOfEntry")!=null && !aReq.getParameter("portOfEntry").equals("-1"))
		{
			portOfEntry = aReq.getParameter("portOfEntry");
			if(aReq.getParameter("assessmentYear")!=null && !aReq.getParameter("assessmentYear").equals("-1"))
			{
				assessmentYear = aReq.getParameter("assessmentYear");
				if(portletState.getPortalUser().getCompany().getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY))
				{
					if(aReq.getParameter("assessmentYear")!=null && !aReq.getParameter("assessmentYear").equals("-1"))
					{
						clientTPin = aReq.getParameter("clientTPin");
					}
				}
			}
			
		}
		loadTaxAssessments(aReq, aRes, portletState, clientTPin, assessmentYear, portOfEntry);
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
	
	
	public void loadTaxAssessments(ActionRequest aReq, ActionResponse aRes, TaxAssessmentManagementPortletState portletState)
	{
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
			declarantFlag = "N";
		
		log.info("declarantFlag" + declarantFlag);
		Company company = portletState.getPortalUser().getCompany();
		TpinInfo tpinInfo = portletState.getTaxAssessmentManagementPortletUtil().getTPINInfoByCompany(company.getId());
		String tpin_declarantcode = null;
		tpin_declarantcode = tpinInfo.getTpin();
		
		log.info("tpin_declarantcode" + tpin_declarantcode);
		String platformFlag = aReq.getParameter("platformFlag")==null ? (portletState.getPlatformFlag()== null ? null : portletState.getPlatformFlag()) : aReq.getParameter("platformFlag");
		log.info("platformFlag" + platformFlag);
		portletState.setPlatformFlag(platformFlag);
		String clientTaxPayerID = "";			
		clientTaxPayerID = declarantFlag.equalsIgnoreCase("Y") ? "" : tpin_declarantcode;					
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
		
						            Collection<Assessment> assessmentListing = Util.handleResponseForGetAssessmentDetails(declarantFlag, tpin_declarantcode, soapResponse, swpService, portletState.getPortalUser());
						            portletState.setAllAssessmentListing(assessmentListing);
						            
						            if(assessmentListing!=null && assessmentListing.size()>0)
						            {
							            System.out.println("SOAP RESPONSE::::::" + assessmentListing.size());
							            new Util().printSOAPResponse(soapResponse);
							            
							            String bnkCode = "";
							            //company.getBankBranches().getBankCode()
							            if(ComminsApplicationState.BANC_ABC==1)
							            {
							            	bnkCode = "BANKABC";
							            }
							            if(ComminsApplicationState.STB==1)
							            {
							            	bnkCode = "STB";
							            }
							            Collection<InterestToBePaid> itList = Util.handleGetInterestOfUnPaidAssessments(declarantFlag.equals("Y") ? true : false, tpin_declarantcode, 
					        					aReq, aRes, portletState.getPlatformBank().getValue(), swpService);
							            
							            ArrayList<Assessment> newAssessmentList = new ArrayList<Assessment>();
							            
							            
							            log.info("itList size===" + itList.size());
							            log.info("==================================================================");
							            for(Iterator<InterestToBePaid> itTBP = itList.iterator(); itTBP.hasNext();)
							            {
							            	InterestToBePaid it1 = itTBP.next();
							            	log.info("Interestto be paid===" + it1.getDeclarantCode() + " | " + it1.getErrorCode() + " | "
							            			 + it1.getPort() + " | " + it1.getRegistrationNumber() + " | " + it1.getRegistrationSerial() + " | "
							            			 + it1.getRegistrationYear() + " | " + it1.getTpin() + " | " + it1.getAmountToBePaid() + " | ");
							            }
							            log.info("==================================================================");
							            
							            
							            
							            Collection<Assessment> assessmentList = null;
							            if(declarantFlag.equals("Y"))
							            {
							            	assessmentList = Util.addInterestToAssessmentsForCorporate(itList, portletState.getAllAssessmentListing());
							            }else if(declarantFlag.equals("N"))
							            {
							            	assessmentList = Util.addInterestToAssessmentsForRetail(itList, portletState.getAllAssessmentListing());
							            }
							            portletState.setAllAssessmentListing(assessmentList);
							            
							            
//							            for(Iterator<Assessment> itr = assessmentListing.iterator(); itr.hasNext();)
//							            {
//							            	Assessment as = itr.next();
//							            	Collection<MiscToBePaid> miscList = handleGetMiscOfUnPaidAssessments(
//							            			as.getClientTpin(), 
//						        					aReq, aRes, company.getBankBranches().getBankCode(), portletState);
//							            	
//								            if(miscList!=null && miscList.size()>0)
//    							            {
//    							            	log.info("INFO: itList.size = " + itList.size());
//    							            	as.setMiscAvailable(Boolean.TRUE);
//    							            	updateDBAssessmentsWithMisc(as, miscList, swpService, tpinInfo);
//    							            }
//								            else
//								            {
//								            	as.setMiscAvailable(Boolean.FALSE);
//								            }
//							            	
//							            }
							            
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
//								        if(cas.getDemoMode().equals(Boolean.TRUE))
//								        {
//								        	
//								        }else
//								        {
//								        	
//								        }
//								        
//								        if(cas.getDemoMode().equals(Boolean.TRUE))
//										{
//											balanceInquiry = cas.getBalanceInquiry(portletState.getPortalUser(), swpService);
//										}else
//										{
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
//										}
//										try {
//											balanceInquiry = Util.getBalanceInquiry(portletState.getApplicationName().getValue(), 
//													"SMARTPAY:ZM:REVENUE:BALANCE:REQUEST", "TYPE", 
//													portletState.getPortalUser().getCompany().getAccountNumber(), 
//													"ZMW");
//										} catch (MalformedURLException e) {
//											// TODO Auto-generated catch block
//											e.printStackTrace();
//										} catch (IOException e) {
//											// TODO Auto-generated catch block
//											e.printStackTrace();
//										}
										
										if(balanceInquiry!=null)
										{
											portletState.setBalanceInquiry(balanceInquiry);
										}else
										{
											portletState.setBalanceInquiry(null);
										}
										portletState.setAllWorkFlowAssessments
											(portletState.getTaxAssessmentManagementPortletUtil().
													getAssessmentsPaidByCompany(
															portletState.getPortalUser().getCompany()));
							            aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
						            }
						            else
						            {
						            	portletState.addError(aReq, "Assessments could not be accessed. Please try again", portletState);
						            	portletState.setAllWorkFlowAssessments(portletState.getTaxAssessmentManagementPortletUtil().getAssessmentsPaidByCompany(portletState.getPortalUser().getCompany()));
							            aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
						            }
						            new Util().printSOAPResponse(soapResponse);
//				            	}else
//				            	{
//				            		
//				            		//DEMO PURPOSES
//				            		Collection<Assessment> assessmentList = cas.getAssessmentList(portletState.getPortalUser(), swpService);
//				            		portletState.setAllAssessmentListing(assessmentList);
//				            		
//				            		BalanceInquiry bi = cas.getBalanceInquiry(portletState.getPortalUser(), swpService);
//				            		portletState.setBalanceInquiry(bi);
//				            		portletState.setAllWorkFlowAssessments(portletState.getTaxAssessmentManagementPortletUtil().getAssessmentsPaidByCompany(portletState.getPortalUser().getCompany()));
//						            aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
//				            	}
								
				            	//portletState.setAllWorkFlowAssessments(portletState.getTaxAssessmentManagementPortletUtil().getWorkFlowsByCompany(portletState.getPortalUser().getCompany()));
				            	
					            
					            
				            }catch(PrivilegedActionException e1)
				            {
				            	log.info("Ok start here");
				            	e1.printStackTrace();
				            	aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
				    			portletState.addError(aReq, "Sending Request for Assessment details failed. Please check your internet connection! E189104 Error Code. Contact the bank for more details if this problems persist.", portletState);
				            }
				    		
	
				            soapConnection.close();
				        } catch (Exception e) {
				            System.err.println("Error occurred while sending SOAP Request to Server");
				            e.printStackTrace();
				            aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
			    			portletState.addError(aReq, "Parsing data returned failed! E189104 Error Code. Contact the bank for more details if this problems persist.", portletState);
				        }
						
					}else
					{
						aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
						portletState.addError(aReq, "2The request to view tax assessment listings was not successful! E189101 Error Code. Contact the bank for more details if this problems persist.", portletState);
						log.info("16");
					}
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
					portletState.addError(aReq, "3The request to view tax assessment listings was not successful! E189102 Error Code. Contact the bank for more details if this problems persist.", portletState);
					log.info("17");
				}
			}else
			{
				aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
				portletState.addError(aReq, "4The request to view tax assessment listings was not successful! E189103 Error Code. Contact the bank for more details if this problems persist.", portletState);
				log.info("18");
			}
		}
        catch(Exception e)
		{
			log.info("IOEXception printstacktrace==");
			e.printStackTrace();
			aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
			portletState.addError(aReq, "5The request to view tax assessment listings was not successful! E189104 Error Code. Contact the bank for more details if this problems persist.", portletState);
			log.info("19");
		}
	}
	
	
	/*private void updateDBAssessmentsWithMisc(Assessment as,
			Collection<MiscToBePaid> miscList, SwpService swpService2, TpinInfo tpinInfo) {
		// TODO Auto-generated method stub
		for(Iterator<MiscToBePaid> itr = miscList.iterator();)
		{
			MiscToBePaid miscToBePaid = itr.next();
			miscToBePaid.getRegistrationNumber();
			miscToBePaid.getRegistrationYear();
			miscToBePaid.getTpin();
		
			MiscToBePaid mtbp= as.getMiscToBePaid();
			if(mtbp!=null)
			{
				mtbp.setAmountToBePaid(miscToBePaid.getAmountToBePaid());
				mtbp.setRegistrationNumber(miscToBePaid.getRegistrationNumber());
				mtbp.setRegistrationYear(miscToBePaid.getRegistrationYear());
				mtbp.setTpin(miscToBePaid.getTpin());
				mtbp.setAmountToBePaid(miscToBePaid.getAmountToBePaid());
				mtbp.setRegistrationSerial(miscToBePaid.getRegistrationSerial());
				mtbp.setPort(miscToBePaid.getPort());
				mtbp.setTpinInfo(tpinInfo);
				swpService.updateRecord(mtbp);
			}else
			{
				mtbp.setAmountToBePaid(miscToBePaid.getAmountToBePaid());
				mtbp.setRegistrationNumber(miscToBePaid.getRegistrationNumber());
				mtbp.setRegistrationYear(miscToBePaid.getRegistrationYear());
				mtbp.setTpin(miscToBePaid.getTpin());
				mtbp.setAmountToBePaid(miscToBePaid.getAmountToBePaid());
				mtbp.setRegistrationSerial(miscToBePaid.getRegistrationSerial());
				mtbp.setPort(miscToBePaid.getPort());
				mtbp.setTpinInfo(tpinInfo);
				swpService.createNewRecord(mtbp);
				as.setMiscToBePaid(mtbp);
				as.setMiscAvailable(Boolean.FALSE);
				swpService.updateRecord(as);
			}
		}
	}*/

	public void loadTaxAssessments(ActionRequest aReq, ActionResponse aRes, TaxAssessmentManagementPortletState portletState, String clientTaxPayerID, 
			String assessmentYear, String portOfEntry)
	{
		portletState.setClientTPIN(clientTaxPayerID);
		portletState.setSelectedYear(assessmentYear);
		portletState.setSelectedPortId(portOfEntry);
		
		String declarantFlag = null;
		if(portletState.getPortalUser().getCompany().getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY))			
			declarantFlag = "Y";
		else if(portletState.getPortalUser().getCompany().getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_RETAIL_COMPANY))			
			declarantFlag = "N";
		
		log.info("declarantFlag" + declarantFlag);
		Company company = portletState.getPortalUser().getCompany();
		TpinInfo tpinInfo = portletState.getTaxAssessmentManagementPortletUtil().getTPINInfoByCompany(company.getId());
		String tpin_declarantcode = null;
		tpin_declarantcode = tpinInfo.getTpin();
		
		log.info("tpin_declarantcode" + tpin_declarantcode);
		String platformFlag = aReq.getParameter("platformFlag")==null ? (portletState.getPlatformFlag()== null ? null : portletState.getPlatformFlag()) : aReq.getParameter("platformFlag");
		log.info("platformFlag" + platformFlag);
		portletState.setPlatformFlag(platformFlag);
		//since we are not searching specifically for one client it becomes empty
		log.info("clientTaxPayerID" + clientTaxPayerID);
		//since we are not searching specifically for one port it becomes empty
		log.info("portOfEntry" + portOfEntry);
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
					            SOAPMessage soapResponse = soapConnection.call(
					            		new Util().createSOAPRequestForGetAssessmentDetails(declarantFlag, tpin_declarantcode, platformFlag, clientTaxPayerID, 
					            				portOfEntry, assessmentYear, source, sourceID, country), url);
	
					            Collection<Assessment> assessmentListing = new Util().handleResponseForGetAssessmentDetails(
					            		declarantFlag, tpin_declarantcode, soapResponse, swpService, portletState.getPortalUser());
					            portletState.setAllAssessmentListing(assessmentListing);
					            //<?xml version="1.0" encoding="UTF-8" standalone="no"?><S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/"><S:Body><ns2:getAssessmentDetailsResponse xmlns:ns2="http://testservice.probase.com/"><clientTPIN/><country>ZM</country><reasonCode>0</reasonCode><reasonDescription>Successful</reasonDescription><source>S2B</source><sourceID>123456</sourceID><timestamp>Sat May 10 22:07:36 EDT 2014</timestamp><tpin_declarantCode>1000009294</tpin_declarantCode><type>getAssessmentDetails</type></ns2:getAssessmentDetailsResponse></S:Body></S:Envelope>
					            
					            // Process the SOAP Response
					            new Util().printSOAPResponse(soapResponse);
				            }catch(PrivilegedActionException e1)
				            {
				            	log.info("Ok start here");
				            	e1.printStackTrace();
				            	aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
				    			portletState.addError(aReq, "Sending Request for Assessment details failed. Please check your internet connection! E189104 Error Code. Contact the bank for more details if this problems persist.", portletState);
				            }
				    		
	
				            soapConnection.close();
				        } catch (Exception e) {
				            System.err.println("Error occurred while sending SOAP Request to Server");
				            e.printStackTrace();
				            aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
			    			portletState.addError(aReq, "Parsing data returned failed! E189104 Error Code. Contact the bank for more details if this problems persist.", portletState);
				        }
						
					}else
					{
						aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
						portletState.addError(aReq, "6The request to view tax assessment listings was not successful! E189101 Error Code. Contact the bank for more details if this problems persist.", portletState);
						log.info("20");
					}
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
					portletState.addError(aReq, "7The request to view tax assessment listings was not successful! E189102 Error Code. Contact the bank for more details if this problems persist.", portletState);
					log.info("21");
				}
			}else
			{
				aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
				portletState.addError(aReq, "8The request to view tax assessment listings was not successful! E189103 Error Code. Contact the bank for more details if this problems persist.", portletState);
				log.info("22");
			}
		}
        catch(Exception e)
		{
			log.info("IOEXception printstacktrace==");
			e.printStackTrace();
			aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
			portletState.addError(aReq, "9The request to view tax assessment listings was not successful! E189104 Error Code. Contact the bank for more details if this problems persist.", portletState);
			log.info("23");
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
	
	
	private String printSOAP(SOAPMessage soapResponse) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source sourceContent = soapResponse.getSOAPPart().getContent();
        System.out.print("\nResponse SOAP Message = ");
        StreamResult result = new StreamResult(System.out);
        transformer.transform(sourceContent, result);
        
        
        StringWriter outWriter = new StringWriter();
        StringBuffer sb = outWriter.getBuffer(); 
        String finalstring = sb.toString();
        return finalstring;
    }
	
	
	

	private void reinitializeForTaxAssessment(
			TaxAssessmentManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setAmountToBePaid(null);
		portletState.setAssessmentNumber(null);
		portletState.setAssessmentStatus(null);
		portletState.setAssessmentYear(null);
		portletState.setInterestAvailable(null);
		portletState.setPortOfEntry(null);
		portletState.setRegistrationDate(null);
		portletState.setRegistrationNumber(null);
		portletState.setRegistrationSerial(null);
    	
		portletState.setAllAssessmentListing(null);
	}

	

	
	public static boolean loadSettings(CompanyManagementPortletState portletState) {
		// TODO Auto-generated method stub
		Settings primaryFeeSetting = portletState.getCompanyManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_PRIMARY_FEE_DESCRIPTION);
		portletState.setPrimaryFeeSetting(primaryFeeSetting);
		Settings notifyCorporateFirmEmail = portletState.getCompanyManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_EMAIL);
		portletState.setNotifyCorporateFirmEmail(notifyCorporateFirmEmail);
		Settings notifyCorporateFirmSms = portletState.getCompanyManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_SMS);
		portletState.setNotifyCorporateFirmSms(notifyCorporateFirmSms);
		Settings notifyCorporateIndividualEmail = portletState.getCompanyManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_EMAIL);
		portletState.setNotifyCorporateIndividualEmail(notifyCorporateIndividualEmail);
		Settings notifyCorporateIndividualSMS = portletState.getCompanyManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_SMS);
		portletState.setNotifyCorporateIndividualSMS(notifyCorporateIndividualSMS);
		Settings etaxPaymentNotifyEmail = portletState.getCompanyManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_EMAIL);
		portletState.setEtaxPaymentNotifyEmail(etaxPaymentNotifyEmail);
		Settings etaxPaymentNotifySMS = portletState.getCompanyManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_SMS);
		portletState.setEtaxPaymentNotifySMS(etaxPaymentNotifySMS);
		Settings systemUrl = portletState.getCompanyManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_URL);
		portletState.setSystemUrl(systemUrl);
		
		Settings twostep = portletState.getCompanyManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_TWO_STEP_LOGIN);
		portletState.setTwoStepLogin(twostep);
		Settings appr = portletState.getCompanyManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_APPROVAL_PROCESS);
		portletState.setApprovalProcess(appr);
		Settings usname = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_USERNAME);
		portletState.setSendingEmailUsername(usname);
		Settings semail = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_EMAIL);
		portletState.setSendingEmail(semail);
		Settings pswd = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_PASSWORD);
		portletState.setSendingEmailPassword(pswd);
		Settings port = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_PORT);
		portletState.setSendingEmailPort(port);
		Settings spc = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_COUNTRY);
		portletState.setPlatformCountry(spc);
		Settings spb = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATORM_BANK);
		portletState.setPlatformBank(spb);
		
		Settings zacc = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_NUMBER);
		portletState.setSettingsZRAAccount(zacc);
		Settings zacs = portletState.getCompanyManagementPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_SORT_CODE);
		portletState.setSettingsZRAAccountSortCode(zacs);
		
		return false;
	}
}
