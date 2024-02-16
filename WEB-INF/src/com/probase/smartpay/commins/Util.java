package com.probase.smartpay.commins;



import java.security.PrivilegedActionException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.client.AxisClient;
import org.apache.axis.configuration.NullProvider;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
//import org.apache.ws.axis.security.util.AxisUtil;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSSConfig;
import org.apache.ws.security.WSSecurityEngine;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.message.WSSignEnvelope;
import org.hibernate.HibernateException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.zxing.common.Collections;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.io.OutputStreamWriter;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;



import smartpay.audittrail.AuditTrail;
import smartpay.entity.Assessment;
import smartpay.entity.AuthorizePanel;
import smartpay.entity.AuthorizePanelCombination;
import smartpay.entity.Balance;
import smartpay.entity.Company;
import smartpay.entity.CompanyCRUDRights;
import smartpay.entity.DomTax;
import smartpay.entity.PaymentBreakDownHistory;
import smartpay.entity.PaymentHistory;
import smartpay.entity.PaymentTempHolder;
import smartpay.entity.PortalUser;
import smartpay.entity.PortalUserCRUDRights;
import smartpay.entity.Ports;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.TaxType;
import smartpay.entity.TpinInfo;
import smartpay.entity.WorkFlow;
import smartpay.entity.WorkFlowAssessment;
import smartpay.entity.enumerations.CompanyTypeConstants;
import smartpay.entity.enumerations.PanelTypeConstants;
import smartpay.entity.enumerations.PaymentBreakDownHistoryConstants;
import smartpay.entity.enumerations.PaymentHistoryConstants;
import smartpay.entity.enumerations.PaymentTypeConstants;
import smartpay.entity.enumerations.PortalUserStatusConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.entity.enumerations.WorkFlowConstants;
import smartpay.service.SwpService;

public class Util {
	Logger log = Logger.getLogger(Util.class);

	private Pattern pattern;
	private Matcher matcher;
	private String dp = "1";
	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	private String mp = "03";
	private String[] countryCode = {"260", "234", "001", "009", "235"};
	private String[] countryName = {"Zambia", "Nigeria", "United States of America(USA)", "England", "Ghana"};
	private int[] phoneLength = {10, 11, 11, 11, 11};
	private int yp = 2015;
	private String dpe = "30";
	///0140036968201
//	private static final WSSecurityEngine secEngine = new WSSecurityEngine();
//	private static final Crypto crypto = CryptoFactory.getInstance();
	
	public enum DETERMINE_ACCESS
	{
		NO_RIGHTS_AT_ALL, GRANT_INITIATOR_ACCESS, GRANT_APPROVER_ACCESS, GRANT_INITIATOR_AND_APPROVER_ACCESS, DISPLAY_SECOND_LEVEL_LOGIN
		
	}
	
	
	public Util() {
	}
	
	
	
	public WorkFlowAssessment addThisAssessmentToWorkFlow(
			Assessment assessment, WorkFlow workFlow, SwpService swpService) {
		// TODO Auto-generated method stub
		
		
		if(assessment!=null && workFlow!=null)
		{
			WorkFlowAssessment wfa = new WorkFlowAssessment();
			wfa.setAssessment(assessment);
			wfa.setWorkFlow(workFlow);
			wfa.setDomTax(null);
			wfa.setStatus(WorkFlowConstants.WORKFLOW_STATUS_CREATED);
			wfa = (WorkFlowAssessment)swpService.createNewRecord(wfa);
			

			
			
			assessment.setMovedToWorkFlow(true);
			swpService.updateRecord(assessment);
						
			return wfa;
		}else
			return null;
	}
	
	
	public WorkFlowAssessment addThisDomToWorkFlow(
			DomTax domTax, WorkFlow workFlow, SwpService swpService) {
		// TODO Auto-generated method stub
		
		
		if(domTax!=null && workFlow!=null)
		{
			WorkFlowAssessment wfa = new WorkFlowAssessment();
			wfa.setDomTax(domTax);
			wfa.setWorkFlow(workFlow);
			wfa.setAssessment(null);
			wfa.setStatus(WorkFlowConstants.WORKFLOW_STATUS_CREATED);
			wfa = (WorkFlowAssessment)swpService.createNewRecord(wfa);
			

			
			
			domTax.setMovedToWorkFlow(true);
			swpService.updateRecord(domTax);
						
			return wfa;
		}else
			return null;
	}
	
	
	
	public static SOAPMessage createSOAPRequestForGetAssessmentDetailsForDemo(
			String declarantFlag, String tpin_declarantcode, String platformFlag, 
			String clientTaxPayerID, String portOfEntry, String assessmentYear, 
			String source, String sourceID, String country) 
	{

		try
		{
	        MessageFactory messageFactory = MessageFactory.newInstance();
	        SOAPMessage soapMessage = messageFactory.createMessage();
	        SOAPPart soapPart = soapMessage.getSOAPPart();
	
	        
	        // SOAP Envelope
	        SOAPEnvelope envelope = soapPart.getEnvelope();
	        envelope.addNamespaceDeclaration("pro", "http://testservice.probase.com/");
			SOAPBody soapBody = envelope.getBody();
			SOAPElement soapBodyElem = soapBody.addChildElement("getAssessmentDetails", "pro");
			SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("assessmentYear");
	        soapBodyElem1.addTextNode(assessmentYear);
	        SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("country");
	        soapBodyElem2.addTextNode(country);
	        SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("declarantFlag");
	        soapBodyElem3.addTextNode(declarantFlag.toString());
	        SOAPElement soapBodyElem4 = soapBodyElem.addChildElement("platformFlag");
	        soapBodyElem4.addTextNode(platformFlag.toString());
	        
	        
	        SOAPElement soapBodyElem5 = soapBodyElem.addChildElement("portOfEntry");
	        soapBodyElem5.addTextNode(portOfEntry.toString());
	        SOAPElement soapBodyElem6 = soapBodyElem.addChildElement("source");
	        soapBodyElem6.addTextNode(source);
	        SOAPElement soapBodyElem7 = soapBodyElem.addChildElement("sourceID");
	        soapBodyElem7.addTextNode(sourceID);
	        SOAPElement soapBodyElem8 = soapBodyElem.addChildElement("timestamp");
	        SOAPElement soapBodyElem8_1 = soapBodyElem8.addChildElement("nanos");
	        soapBodyElem8_1.addTextNode(new Date().toString());
	        SOAPElement soapBodyElem9 = soapBodyElem.addChildElement("tpin_declarantCode");
	        soapBodyElem9.addTextNode(declarantFlag.equals("Y") ? tpin_declarantcode : clientTaxPayerID);
	        SOAPElement soapBodyElem10 = soapBodyElem.addChildElement("type");
	        soapBodyElem10.addTextNode("getAssessmentDetails");
//        MimeHeaders headers = soapMessage.getMimeHeaders();
//        headers.addHeader("SOAPAction", serverURI  + "getUnpaidAssessmentsByTPIN");
        	
        	soapMessage.saveChanges();

        /* Print the request message */
        	System.out.print("Request SOAP Message for Probase Service = ");
        	soapMessage.writeTo(System.out);

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
					return null;
				}
			} catch (SOAPException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				return null;
			}
			
			return soapMessage;
		}
    }
	
	
	
	
	
	public static SOAPMessage createSOAPRequestForDomTaxStatement(
			String declarantFlag, String tpin_declarantcode, String platformFlag, 
			String clientTaxPayerID, String portOfEntry, String assessmentYear, 
			String source, String sourceID, String country,
			DomTaxStatementHeader dtsh, Collection<DomTaxStatementDetail> dtsd) 
	{

		try
		{
	        MessageFactory messageFactory = MessageFactory.newInstance();
	        SOAPMessage soapMessage = messageFactory.createMessage();
	        SOAPPart soapPart = soapMessage.getSOAPPart();
	
	        
	        // SOAP Envelope
	        SOAPEnvelope envelope = soapPart.getEnvelope();
	        envelope.addNamespaceDeclaration("pro", "http://testservice.probase.com/");
			SOAPBody soapBody = envelope.getBody();
			SOAPElement soapBodyElem11 = soapBody.addChildElement("bankStatementNotificationService", "pro");
			SOAPElement soapBodyElem = soapBodyElem11.addChildElement("domTaxStatementHeader");
			SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("accNameHdr");
	        soapBodyElem1.addTextNode(dtsh.getAcctNameHdr());
	        SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("accNoHdr");
	        soapBodyElem2.addTextNode(dtsh.getAcctNoHdr());
	        SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("bankCodeHdr");
	        soapBodyElem3.addTextNode(dtsh.getBankCodeHdr());
	        SOAPElement soapBodyElem4 = soapBodyElem.addChildElement("closingBalHdr");
	        soapBodyElem4.addTextNode(dtsh.getClosingBalHdr());
	        SOAPElement soapBodyElem5 = soapBodyElem.addChildElement("openingBalHdr");
	        soapBodyElem5.addTextNode(dtsh.getOpeningBalHdr());
	        SOAPElement soapBodyElem6 = soapBodyElem.addChildElement("statementDateHdr");
	        soapBodyElem6.addTextNode(dtsh.getStmtDateHdr());
	        SOAPElement soapBodyElem7 = soapBodyElem.addChildElement("statementNoHdr");
	        soapBodyElem7.addTextNode(dtsh.getStmtNoHdr());
	        
	        for(Iterator<DomTaxStatementDetail> it = dtsd.iterator(); it.hasNext();)
	        {
	        	DomTaxStatementDetail dtsd1 = it.next();
	        	SOAPElement soapBodyElem21 = soapBodyElem11.addChildElement("domTaxStatementDetail");
				SOAPElement soapBodyElem211 = soapBodyElem21.addChildElement("balanceRow");
		        soapBodyElem211.addTextNode(dtsd1.getBalanceRow());
		        SOAPElement soapBodyElem212 = soapBodyElem.addChildElement("creditRow");
		        soapBodyElem212.addTextNode(dtsd1.getCreditRow());
		        SOAPElement soapBodyElem213 = soapBodyElem.addChildElement("debitRow");
		        soapBodyElem213.addTextNode(dtsd1.getDebitRow());
		        SOAPElement soapBodyElem214 = soapBodyElem.addChildElement("descriptionRow");
		        soapBodyElem214.addTextNode(dtsd1.getDescriptionRow());
		        SOAPElement soapBodyElem215 = soapBodyElem.addChildElement("prnRow");
		        soapBodyElem215.addTextNode(dtsd1.getPrnRow());
		        SOAPElement soapBodyElem216 = soapBodyElem.addChildElement("tpinRow");
		        soapBodyElem216.addTextNode(dtsd1.getTpinRow());
		        SOAPElement soapBodyElem217 = soapBodyElem.addChildElement("transactionCodeRow");
		        soapBodyElem217.addTextNode(dtsd1.getTransactionCodeRow());
		        SOAPElement soapBodyElem218 = soapBodyElem.addChildElement("transactionDateRow");
		        soapBodyElem218.addTextNode(dtsd1.getTransactionDateRow());
	        }
	        
	        
//        MimeHeaders headers = soapMessage.getMimeHeaders();
//        headers.addHeader("SOAPAction", serverURI  + "getUnpaidAssessmentsByTPIN");
        	
        	soapMessage.saveChanges();

        /* Print the request message */
        	System.out.print("Request SOAP Message for Probase Service = ");
        	soapMessage.writeTo(System.out);

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
					return null;
				}
			} catch (SOAPException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				return null;
			}
			
			return soapMessage;
		}
    }
	
	
	
	
	
	
	public DomTaxStatementResponse handleResponseForDomTaxStatementNotify(
			SOAPMessage soapResponse) {
		// TODO Auto-generated method stub
		SOAPBody soapBodyResponse;
		try {
			soapBodyResponse = soapResponse.getSOAPBody();
    		DomTaxStatementResponse dtsr = null;
    		
			if(soapBodyResponse!=null)
	        {
	        	//String resp = util.parseSOAPBodyToString(soapBodyResponse);
	        	log.info("");
	        	log.info("soapBodyResponse.getNodeName();==>" + soapBodyResponse.getNodeName());
	        	
	        	Name name = SOAPFactory.newInstance().createName("S:Body");
	        	NodeList nodeList = soapBodyResponse.getChildNodes();
	        	Node node = nodeList.item(0);
	        	log.info("node.getNodeName();==>" + node.getNodeName());
	        	NodeList nodeList2 = node.getChildNodes();

        		
	        	for(int c0= 0; c0<nodeList2.getLength(); c0++)
	        	{
	        		
		        	Node node2 = nodeList2.item(c0);
		        	log.info("node2.getNodeName();==>" + node2.getNodeName());
		        	if(node2.getNodeName().equalsIgnoreCase("productListing"))
		        	{
		        		dtsr = new DomTaxStatementResponse();
			        	NodeList nodeList3 = node2.getChildNodes();
			        	ArrayList<TaxDetails> tdList = new ArrayList<TaxDetails>();
			        	for(int c= 0; c<nodeList3.getLength(); c++)
			        	{
			        		Node node3 = nodeList3.item(c);
			        		log.info("node3.getNodeName();==>" + node3.getNodeName());			///productHsCode, productName, taxDetailsListing
			        		
			        		NodeList nodeList4 = node3.getChildNodes();
//			        		if(node3.getNodeName().equalsIgnoreCase("productHsCode"))
//			        		{
//			        			Node node41 = nodeList4.item(0);
//			        			tbdResponse.setProductCode(node41.getNodeValue());
//			        			log.info("node3==>" + node3.getNodeName() + " &&& value = " + node41.getNodeValue());
//			        		}
//			        		if(node3.getNodeName().equalsIgnoreCase("productName"))
//			        		{
//			        			Node node41 = nodeList4.item(0);
//			        			tbdResponse.setProductName(node41.getNodeValue());
//			        			log.info("node3==>" + node3.getNodeName() + " &&& value = " + node41.getNodeValue());
//			        		}
//			        		if(node3.getNodeName().equalsIgnoreCase("taxDetailsListing"))
//			        		{
//			        			log.info("nodeList4.getLength() = " + nodeList4.getLength());
//			        			if(nodeList4.getLength()>0)
//			        			{
//				        			TaxDetails td = new TaxDetails();
//				        			for(int c1= 0; c1<nodeList4.getLength(); c1++)
//						        	{
//						        		Node node4 = nodeList4.item(c1);
//						        		log.info("node4==>" + node4.getNodeName());
//						        		NodeList nodeList5 = node4.getChildNodes();
//						        		log.info("nodeList5.getLength() = " + nodeList5.getLength());
//						        		if(nodeList5.getLength()>0)
//						        		{
//							        		Node node5 = nodeList5.item(0);
//							        		log.info("node3==>" + node3.getNodeName() + (node5.getNodeValue()==null ? "" : " value = " + node5.getNodeValue()));	//productName || value = Sauces and sauce preparations
//							        		if(node4.getNodeName().equalsIgnoreCase("amountToBePaid"))
//							        		{
//							        			td.setAmountToBePaid(Double.valueOf(node5.getNodeValue()));
//							        		}
//							        		if(node4.getNodeName().equalsIgnoreCase("taxCode"))
//							        		{
//							        			td.setTaxCode(node5.getNodeValue());
//							        		}
//						        		}
//						        		
//						        	}
//				        			tdList.add(td);
//			        			}
//			        			log.info("----------------------------------------------- ");
//			        		}
			        	}
//			        	tbdResponse.setTaxDetailListing(tdList);
//			        	taxBreakDownResponse.add(tbdResponse);
		        	}
	        	}

	        	
	        	return dtsr;
	        	
	        }else
	        {
	        	log.info("soapBody is null");
	        	return null;
	        }
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	
	
	public DETERMINE_ACCESS determineAccessForUserFxns(Boolean twoStepLogin, Boolean approvalProcess, ComminsApplicationState cappState, PortalUserCRUDRights portalUserCRUDRights)
	{
		if(twoStepLogin!=null && twoStepLogin)
		{
			if(cappState!=null && cappState.getLoggedIn()!=null && cappState.getLoggedIn().equals(Boolean.TRUE) && cappState.getPortalUser()!=null)	//Second Login is active
			{
				if(approvalProcess!=null && approvalProcess)
				{
					if(portalUserCRUDRights==null)
					{
						log.info("NO_RIGHTS_AT_ALL");
						return DETERMINE_ACCESS.NO_RIGHTS_AT_ALL;
					}else
					{
						if(portalUserCRUDRights!=null && portalUserCRUDRights.getStatus().equals(Boolean.FALSE))
						{
							log.info("NO_RIGHTS_AT_ALL");
							return DETERMINE_ACCESS.NO_RIGHTS_AT_ALL;
						}
						else
						{
							if(portalUserCRUDRights!=null && 
									portalUserCRUDRights.getCudInitiatorRights()!=null && 
											portalUserCRUDRights.getCudInitiatorRights().equals(Boolean.TRUE))
							{
								log.info("GRANT_INITIATOR_ACCESS");
								return DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS;
							}else if(portalUserCRUDRights!=null && 
									portalUserCRUDRights.getCudInitiatorRights()!=null && 
											portalUserCRUDRights.getCudInitiatorRights().equals(Boolean.FALSE))
							{
								log.info("GRANT_APPROVER_ACCESS");
								return DETERMINE_ACCESS.GRANT_APPROVER_ACCESS;
							}else
							{
								log.info("NO_RIGHTS_AT_ALL");
								return DETERMINE_ACCESS.NO_RIGHTS_AT_ALL;
							}
						}
					}
				}else
				{
					log.info("GRANT_INITIATOR_AND_APPROVER_ACCESS");
					return DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS;
				}
			}else
			{
				log.info("DISPLAY_SECOND_LEVEL_LOGIN");
				return DETERMINE_ACCESS.DISPLAY_SECOND_LEVEL_LOGIN;
			}
		}else
		{
			if(approvalProcess!=null && approvalProcess)
			{
				if(portalUserCRUDRights==null)
				{
					log.info("2 -NO_RIGHTS_AT_ALL");
					return DETERMINE_ACCESS.NO_RIGHTS_AT_ALL;
				}else
				{
					if(portalUserCRUDRights!=null && portalUserCRUDRights.getStatus().equals(Boolean.FALSE))
					{
						log.info("2 -NO_RIGHTS_AT_ALL");
						return DETERMINE_ACCESS.NO_RIGHTS_AT_ALL;
					}
					else
					{
						if(portalUserCRUDRights!=null && 
								portalUserCRUDRights.getCudInitiatorRights()!=null && 
										portalUserCRUDRights.getCudInitiatorRights().equals(Boolean.TRUE))
						{
							log.info("2 -GRANT_INITIATOR_ACCESS");
							return DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS;
						}else if(portalUserCRUDRights!=null && 
								portalUserCRUDRights.getCudInitiatorRights()!=null && 
										portalUserCRUDRights.getCudInitiatorRights().equals(Boolean.FALSE))
						{
							log.info("2 -GRANT_APPROVER_ACCESS");
							return DETERMINE_ACCESS.GRANT_APPROVER_ACCESS;
						}else
						{
							log.info("2 -NO_RIGHTS_AT_ALL");
							return DETERMINE_ACCESS.NO_RIGHTS_AT_ALL;
						}
					}
				}
			}else
			{
				log.info("2 -GRANT_INITIATOR_AND_APPROVER_ACCESS");
				return DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS;
			}
		}
	}
	
	
	
	
	public DETERMINE_ACCESS determineAccessForCompanyFxns(Boolean twoStepLogin, Boolean approvalProcess, ComminsApplicationState cappState, CompanyCRUDRights companyCRUDRights)
	{
		if(twoStepLogin!=null && twoStepLogin.equals(Boolean.TRUE))
		{
			if(cappState!=null && cappState.getLoggedIn()!=null && cappState.getLoggedIn().equals(Boolean.TRUE) && cappState.getPortalUser()!=null)	//Second Login is active
			{
				if(approvalProcess!=null && approvalProcess.equals(Boolean.TRUE))
				{
					if(companyCRUDRights==null)
					{
						log.info("NO_RIGHTS_AT_ALL");
						return DETERMINE_ACCESS.NO_RIGHTS_AT_ALL;
					}else
					{
//						if(companyCRUDRights!=null && companyCRUDRights.getStatus().equals(Boolean.FALSE))
//						{
//							log.info("NO_RIGHTS_AT_ALL");
//							return DETERMINE_ACCESS.NO_RIGHTS_AT_ALL;
//						}
//						else
//						{
							if(companyCRUDRights!=null && 
									companyCRUDRights.getCudInitiatorRights()!=null && 
											companyCRUDRights.getCudInitiatorRights().equals(Boolean.TRUE))
							{
								log.info("GRANT_INITIATOR_ACCESS");
								return DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS;
							}else if(companyCRUDRights!=null && 
									companyCRUDRights.getCudInitiatorRights()!=null && 
											companyCRUDRights.getCudInitiatorRights().equals(Boolean.FALSE))
							{
								log.info("GRANT_APPROVER_ACCESS");
								return DETERMINE_ACCESS.GRANT_APPROVER_ACCESS;
							}else
							{
								log.info("NO_RIGHTS_AT_ALL");
								return DETERMINE_ACCESS.NO_RIGHTS_AT_ALL;
							}
//						}
					}
				}else
				{
					log.info("GRANT_INITIATOR_AND_APPROVER_ACCESS");
					return DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS;
				}
			}else
			{
				log.info("DISPLAY_SECOND_LEVEL_LOGIN");
				return DETERMINE_ACCESS.DISPLAY_SECOND_LEVEL_LOGIN;
			}
		}else
		{
			if(approvalProcess!=null && approvalProcess.equals(Boolean.TRUE))
			{
				if(companyCRUDRights==null)
				{
					log.info("2 -NO_RIGHTS_AT_ALL");
					return DETERMINE_ACCESS.NO_RIGHTS_AT_ALL;
				}else
				{
//					if(companyCRUDRights!=null && companyCRUDRights.ge.getStatus().equals(Boolean.FALSE))
//					{
//						log.info("2 -NO_RIGHTS_AT_ALL");
//						return DETERMINE_ACCESS.NO_RIGHTS_AT_ALL;
//					}
//					else
//					{
						if(companyCRUDRights!=null && 
								companyCRUDRights.getCudInitiatorRights()!=null && 
										companyCRUDRights.getCudInitiatorRights().equals(Boolean.TRUE))
						{
							log.info("2 -GRANT_INITIATOR_ACCESS");
							return DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS;
						}else if(companyCRUDRights!=null && 
								companyCRUDRights.getCudInitiatorRights()!=null && 
										companyCRUDRights.getCudInitiatorRights().equals(Boolean.FALSE))
						{
							log.info("2 -GRANT_APPROVER_ACCESS");
							return DETERMINE_ACCESS.GRANT_APPROVER_ACCESS;
						}else
						{
							log.info("2 -NO_RIGHTS_AT_ALL");
							return DETERMINE_ACCESS.NO_RIGHTS_AT_ALL;
						}
//					}
				}
			}else
			{
				log.info("2 -GRANT_INITIATOR_AND_APPROVER_ACCESS");
				return DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS;
			}
		}
	}
	
	
 
	/**
	 * Validate hex with regular expression
	 * 
	 * @param hex
	 *            hex for validation
	 * @return true valid hex, false invalid hex
	 */
	public boolean validateEmail(String hex) {
		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(hex);
		return matcher.matches();
 
	}
	

	
	
	public String roundUpAmount(Double number)
	{
		DecimalFormat df = new DecimalFormat("#,##0.00");
		return df.format(number);
	}
	
	public String roundUpAmountStyle2(Double number)
	{
		DecimalFormat df = new DecimalFormat("###0.00");
		return df.format(number);
	}



	public long getPortalUserCommunityByRoleType(RoleType roleType) {
		// TODO Auto-generated method stub
		if(roleType.getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
		{
			return ProbaseConstants.BANK_ADMIN_COMMUNITY_ID;
		}else if(roleType.getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_HEAD_OF_OPERATIONS))
		{
			return ProbaseConstants.BANK_HEAD_OF_OPERATIONS_COMMUNITY_ID;
		}else if(roleType.getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_SUPER_ADMINISTRATOR))
		{
			return ProbaseConstants.BANK_SUPER_ADMIN_COMMUNITY_ID;
		}else if(roleType.getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_TELLER))
		{
			return ProbaseConstants.BANK_TELLER_COMMUNITY_ID;
		}else if(roleType.getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_NON_ADMINISTRATOR_PERSONNEL))
		{
			return ProbaseConstants.CORPORATE_STAFF_COMMUNITY_ID;
		}else if(roleType.getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
		{
			return ProbaseConstants.CORPORATE_FIRM_ADMINISTRATOR_COMMUNITY_ID;
		}else if(roleType.getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_HQ_OPERATIONS_ASSISTANT))
		{
			return ProbaseConstants.BANK_HQ_OPERATIONS_ASSISTANT_COMMUNITY_ID;
		}else if(roleType.getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_RETAIL_STAFF))
		{
			return ProbaseConstants.RETAIL_STAFF_COMMUNITY_ID;
		}
		else if(roleType.getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_SYSTEM_SUPER_ADMINISTRATOR))
		{
			return ProbaseConstants.DEFAULT_SITE_ID;
		}else
		{
			return ProbaseConstants.DEFAULT_SITE_ID;
		}
	}


	
	public ArrayList<TaxBreakDownResponse> getTaxBreakDown(
			String bankCode,
			String portOfEntry,
			Integer registrationYear,
			String registrationSerial,
			String registrationNumber) {
		// TODO Auto-generated method stub
		log.info("Carry out getTaxBreakDown process");
		log.info("bankCode" + bankCode);
		String bankCode1 = bankCode;
		log.info("portOfEntry" + portOfEntry);
		String portOfEntry1 =portOfEntry;
		log.info("registrationYear" + Integer.toString(registrationYear));
		String registrationYear1 = Integer.toString(registrationYear);
		log.info("registrationSerial" + registrationSerial);
		String registrationSerial1 = registrationSerial;
		log.info("registrationNumber" + registrationNumber);
		String registrationNumber1 = registrationNumber;
		String url = "http://localhost:8086/WS/ProbaseZambiaService";
        url = "http://localhost:8085/ProbaseTestService2/ProbaseZambia";
        //url = "http://probasetestservice2-pallpod.rhcloud.com/ProbaseTestService2/ProbaseZambia";
        
        log.info("url1 = > ");
        log.info("url = > " + url);
        
        
			
						
		try {
	        // Create SOAP Connection
			
	        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
	        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
	
	        // Send SOAP Message to SOAP Server
	        
	        SOAPMessage soapResponse = soapConnection.call(
	        		createSOAPRequestForTaxBreakUpDetails(bankCode1, portOfEntry1, registrationYear1, registrationSerial1, registrationNumber1), url);

	        //printSOAPResponse(soapResponse);
	        
	        ArrayList<TaxBreakDownResponse> resp =  handleResponseForGetTaxBreakUpDetails(soapResponse);
	        log.info("resp size = " + resp!=null ? resp.size() : "null");
	        log.info("resp size = " + resp!=null ? resp : "null");
	        
	        // Process the SOAP Response
	        printSOAPResponse(soapResponse);
	        soapConnection.close();
	        return resp;
	    } catch (Exception e) {
	        System.err.println("Error occurred while sending SOAP Request to Server");
	        e.printStackTrace();
	        return null;
	    }
						
					
	}
	
	
	
	public static void printSOAPResponse(SOAPMessage soapResponse) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source sourceContent = soapResponse.getSOAPPart().getContent();
        System.out.print("\nResponse SOAP Message = ");
        StreamResult result = new StreamResult(System.out);
        transformer.transform(sourceContent, result);
    }
	
	
	
	
	
	public PRNLookUpServiceResponse handleResponseForPRNLookUpService(
			SOAPMessage soapResponse) {
		// TODO Auto-generated method stub
		SOAPBody soapBodyResponse;
		try {
			soapBodyResponse = soapResponse.getSOAPBody();
			PRNLookUpServiceResponse prnLookUpServiceResponse = null;
    		
			if(soapBodyResponse!=null)
	        {
	        	//String resp = util.parseSOAPBodyToString(soapBodyResponse);
	        	log.info("");
	        	log.info("soapBodyResponse.getNodeName();==>" + soapBodyResponse.getNodeName());
	        	
	        	Name name = SOAPFactory.newInstance().createName("S:Body");
	        	NodeList nodeList = soapBodyResponse.getChildNodes();
	        	Node node = nodeList.item(0);
	        	log.info("node.getNodeName();==>" + node.getNodeName());
	        	NodeList nodeList2 = node.getChildNodes();						//Response

        		
	        	for(int c0= 0; c0<nodeList2.getLength(); c0++)
	        	{
	        		
		        	Node node2 = nodeList2.item(c0);
		        	log.info("node2.getNodeName();==>" + node2.getNodeName());
		        	if(node2.getNodeName().equalsIgnoreCase("productListing"))
		        	{
			        	TaxBreakDownResponse tbdResponse = new TaxBreakDownResponse();
			        	NodeList nodeList3 = node2.getChildNodes();
			        	prnLookUpServiceResponse = new PRNLookUpServiceResponse();
			        	for(int c= 0; c<nodeList3.getLength(); c++)
			        	{
			        		Node node3 = nodeList3.item(c);
			        		log.info("node3.getNodeName();==>" + node3.getNodeName());			///amountToBePaid
			        		
			        		NodeList nodeList4 = node3.getChildNodes();
			        		if(node3.getNodeName().equalsIgnoreCase("amountToBePaid"))
			        		{
			        			Node node41 = nodeList4.item(0);
			        			prnLookUpServiceResponse.setAmountToBePaid(node41.getNodeValue());
			        			log.info("node3==>" + node3.getNodeName() + " &&& value = " + node41.getNodeValue());
			        		}
			        		if(node3.getNodeName().equalsIgnoreCase("errorCode"))
			        		{
			        			Node node41 = nodeList4.item(0);
			        			prnLookUpServiceResponse.setErrorCode(node41.getNodeValue());
			        			log.info("node3==>" + node3.getNodeName() + " &&& value = " + node41.getNodeValue());
			        		}
			        		if(node3.getNodeName().equalsIgnoreCase("paymentRegDate"))
			        		{
			        			Node node41 = nodeList4.item(0);
			        			prnLookUpServiceResponse.setPaymentRegDate(node41.getNodeValue());
			        			log.info("node3==>" + node3.getNodeName() + " &&& value = " + node41.getNodeValue());
			        		}
			        		if(node3.getNodeName().equalsIgnoreCase("paymentRegNo"))
			        		{
			        			Node node41 = nodeList4.item(0);
			        			prnLookUpServiceResponse.setPaymentRegNo(node41.getNodeValue());
			        			log.info("node3==>" + node3.getNodeName() + " &&& value = " + node41.getNodeValue());
			        		}if(node3.getNodeName().equalsIgnoreCase("taxPayerName"))
			        		{
			        			Node node41 = nodeList4.item(0);
			        			prnLookUpServiceResponse.setTaxPayerName(node41.getNodeValue());
			        			log.info("node3==>" + node3.getNodeName() + " &&& value = " + node41.getNodeValue());
			        		}
			        		if(node3.getNodeName().equalsIgnoreCase("tpin"))
			        		{
			        			Node node41 = nodeList4.item(0);
			        			prnLookUpServiceResponse.setTpin(node41.getNodeValue());
			        			log.info("node3==>" + node3.getNodeName() + " &&& value = " + node41.getNodeValue());
			        		}if(node3.getNodeName().equalsIgnoreCase("status"))
			        		{
			        			Node node41 = nodeList4.item(0);
			        			prnLookUpServiceResponse.setStatus(node41.getNodeValue());
			        			log.info("node3==>" + node3.getNodeName() + " &&& value = " + node41.getNodeValue());
			        		}
			        	}
		        	}
	        	}

	        	
	        	return prnLookUpServiceResponse;
	        	
	        }else
	        {
	        	log.info("soapBody is null");
	        	return null;
	        }
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	
	
	
	
	public ArrayList<TaxBreakDownResponse> handleResponseForGetTaxBreakUpDetails(
			SOAPMessage soapResponse) {
		// TODO Auto-generated method stub
		SOAPBody soapBodyResponse;
		try {
			soapBodyResponse = soapResponse.getSOAPBody();
    		ArrayList<TaxBreakDownResponse> taxBreakDownResponse = new ArrayList<TaxBreakDownResponse>();
    		
			if(soapBodyResponse!=null)
	        {
	        	//String resp = util.parseSOAPBodyToString(soapBodyResponse);
	        	log.info("");
	        	log.info("soapBodyResponse.getNodeName();==>" + soapBodyResponse.getNodeName());
	        	
	        	Name name = SOAPFactory.newInstance().createName("S:Body");
	        	NodeList nodeList = soapBodyResponse.getChildNodes();
	        	Node node = nodeList.item(0);
	        	log.info("node.getNodeName();==>" + node.getNodeName());
	        	NodeList nodeList2 = node.getChildNodes();

        		
	        	for(int c0= 0; c0<nodeList2.getLength(); c0++)
	        	{
	        		
		        	Node node2 = nodeList2.item(c0);
		        	log.info("node2.getNodeName();==>" + node2.getNodeName());
		        	if(node2.getNodeName().equalsIgnoreCase("productListing"))
		        	{
			        	TaxBreakDownResponse tbdResponse = new TaxBreakDownResponse();
			        	NodeList nodeList3 = node2.getChildNodes();
			        	ArrayList<TaxDetails> tdList = new ArrayList<TaxDetails>();
			        	for(int c= 0; c<nodeList3.getLength(); c++)
			        	{
			        		Node node3 = nodeList3.item(c);
			        		log.info("node3.getNodeName();==>" + node3.getNodeName());			///productHsCode, productName, taxDetailsListing
			        		
			        		NodeList nodeList4 = node3.getChildNodes();
			        		if(node3.getNodeName().equalsIgnoreCase("productHsCode"))
			        		{
			        			Node node41 = nodeList4.item(0);
			        			tbdResponse.setProductCode(node41.getNodeValue());
			        			log.info("node3==>" + node3.getNodeName() + " &&& value = " + node41.getNodeValue());
			        		}
			        		if(node3.getNodeName().equalsIgnoreCase("productName"))
			        		{
			        			Node node41 = nodeList4.item(0);
			        			tbdResponse.setProductName(node41.getNodeValue());
			        			log.info("node3==>" + node3.getNodeName() + " &&& value = " + node41.getNodeValue());
			        		}
			        		if(node3.getNodeName().equalsIgnoreCase("taxDetailsListing"))
			        		{
			        			log.info("nodeList4.getLength() = " + nodeList4.getLength());
			        			if(nodeList4.getLength()>0)
			        			{
				        			TaxDetails td = new TaxDetails();
				        			for(int c1= 0; c1<nodeList4.getLength(); c1++)
						        	{
						        		Node node4 = nodeList4.item(c1);
						        		log.info("node4==>" + node4.getNodeName());
						        		NodeList nodeList5 = node4.getChildNodes();
						        		log.info("nodeList5.getLength() = " + nodeList5.getLength());
						        		if(nodeList5.getLength()>0)
						        		{
							        		Node node5 = nodeList5.item(0);
							        		log.info("node3==>" + node3.getNodeName() + (node5.getNodeValue()==null ? "" : " value = " + node5.getNodeValue()));	//productName || value = Sauces and sauce preparations
							        		if(node4.getNodeName().equalsIgnoreCase("amountToBePaid"))
							        		{
							        			td.setAmountToBePaid(Double.valueOf(node5.getNodeValue()));
							        		}
							        		if(node4.getNodeName().equalsIgnoreCase("taxCode"))
							        		{
							        			td.setTaxCode(node5.getNodeValue());
							        		}
						        		}
						        		
						        	}
				        			tdList.add(td);
			        			}
			        			log.info("----------------------------------------------- ");
			        		}
			        	}
			        	tbdResponse.setTaxDetailListing(tdList);
			        	taxBreakDownResponse.add(tbdResponse);
		        	}
	        	}

	        	
	        	return taxBreakDownResponse;
	        	
	        }else
	        {
	        	log.info("soapBody is null");
	        	return null;
	        }
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	
	
	
	
	
	public SOAPMessage createSOAPRequestForTaxBreakUpDetails(
			String bankCode,
			String portOfEntry,
			String registrationYear,
			String registrationSerial, String registrationNumber) {
		// TODO Auto-generated method stub
		try
		{
	        MessageFactory messageFactory = MessageFactory.newInstance();
	        SOAPMessage soapMessage = messageFactory.createMessage();
	        SOAPPart soapPart = soapMessage.getSOAPPart();
	
	        
	        // SOAP Envelope
	        SOAPEnvelope envelope = soapPart.getEnvelope();
	        envelope.addNamespaceDeclaration("pro", "http://testservice.probase.com/");
			SOAPBody soapBody = envelope.getBody();
			SOAPElement soapBodyElem = soapBody.addChildElement("getTaxBreakUpDetails", "pro");
			SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("bankCode");
	        soapBodyElem1.addTextNode(bankCode);
	        SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("portOfEntry");
	        soapBodyElem2.addTextNode(portOfEntry);
	        SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("registrationYear");
	        soapBodyElem3.addTextNode(registrationYear.toString());
	        SOAPElement soapBodyElem4 = soapBodyElem.addChildElement("registrationSerial");
	        soapBodyElem4.addTextNode(registrationSerial.toString());
	        SOAPElement soapBodyElem5 = soapBodyElem.addChildElement("portOfEntry");
	        soapBodyElem5.addTextNode(portOfEntry.toString());
	        SOAPElement soapBodyElem6 = soapBodyElem.addChildElement("registrationNumber");
	        soapBodyElem6.addTextNode(registrationNumber);
//        MimeHeaders headers = soapMessage.getMimeHeaders();
//        headers.addHeader("SOAPAction", serverURI  + "getUnpaidAssessmentsByTPIN");
        	
        	soapMessage.saveChanges();

        /* Print the request message */
        	System.out.print("Request SOAP Message for Probase Service = ");
        	soapMessage.writeTo(System.out);
        	log.info("");

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
			
			return soapMessage;
		}
	}
	
	
	
	
	
	
	public static SOAPMessage createSOAPRequestForPRNLookUpService(
			String prn) 
	{

		try
		{
	        MessageFactory messageFactory = MessageFactory.newInstance();
	        SOAPMessage soapMessage = messageFactory.createMessage();
	        SOAPPart soapPart = soapMessage.getSOAPPart();
	
	        
	        // SOAP Envelope
	        SOAPEnvelope envelope = soapPart.getEnvelope();
	        envelope.addNamespaceDeclaration("pro", "http://testservice.probase.com/");
			SOAPBody soapBody = envelope.getBody();
			SOAPElement soapBodyElem = soapBody.addChildElement("lookupPRN", "pro");
			SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("paymentRegNo", "pro");
	        soapBodyElem1.addTextNode(prn);
//        MimeHeaders headers = soapMessage.getMimeHeaders();
//        headers.addHeader("SOAPAction", serverURI  + "getUnpaidAssessmentsByTPIN");
        	
        	soapMessage.saveChanges();

        /* Print the request message */
        	System.out.print("Request SOAP Message for Probase Service = ");
        	soapMessage.writeTo(System.out);

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
					return null;
				}
			} catch (SOAPException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				return null;
			}
			
			return soapMessage;
		}
    }
	
	
	
	/*************get all uunbpaid assessments****/
	public static SOAPMessage createSOAPRequestForGetAssessmentDetails(
			String declarantFlag, String tpin_declarantcode, String platformFlag, 
			String clientTaxPayerID, String portOfEntry, String assessmentYear, 
			String source, String sourceID, String country) 
	{

		try
		{
	        MessageFactory messageFactory = MessageFactory.newInstance();
	        SOAPMessage soapMessage = messageFactory.createMessage();
	        SOAPPart soapPart = soapMessage.getSOAPPart();
	
	        
	        // SOAP Envelope
	        SOAPEnvelope envelope = soapPart.getEnvelope();
	        envelope.addNamespaceDeclaration("pro", "http://testservice.probase.com/");
			SOAPBody soapBody = envelope.getBody();
			SOAPElement soapBodyElem = soapBody.addChildElement("getAssessmentDetails", "pro");
			SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("assessmentYear");
	        soapBodyElem1.addTextNode(assessmentYear);
	        SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("country");
	        soapBodyElem2.addTextNode(country);
	        SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("declarantFlag");
	        soapBodyElem3.addTextNode(declarantFlag.toString());
	        SOAPElement soapBodyElem4 = soapBodyElem.addChildElement("platformFlag");
	        soapBodyElem4.addTextNode(platformFlag.toString());
	        
	        
	        SOAPElement soapBodyElem5 = soapBodyElem.addChildElement("portOfEntry");
	        soapBodyElem5.addTextNode(portOfEntry.toString());
	        SOAPElement soapBodyElem6 = soapBodyElem.addChildElement("source");
	        soapBodyElem6.addTextNode(source);
	        SOAPElement soapBodyElem7 = soapBodyElem.addChildElement("sourceID");
	        soapBodyElem7.addTextNode(sourceID);
	        SOAPElement soapBodyElem8 = soapBodyElem.addChildElement("timestamp");
	        SOAPElement soapBodyElem8_1 = soapBodyElem8.addChildElement("nanos");
	        soapBodyElem8_1.addTextNode(new Date().toString());
	        SOAPElement soapBodyElem9 = soapBodyElem.addChildElement("tpin_declarantCode");
	        soapBodyElem9.addTextNode(declarantFlag.equals("Y") ? tpin_declarantcode : clientTaxPayerID);
	        SOAPElement soapBodyElem10 = soapBodyElem.addChildElement("type");
	        soapBodyElem10.addTextNode("getAssessmentDetails");
//        MimeHeaders headers = soapMessage.getMimeHeaders();
//        headers.addHeader("SOAPAction", serverURI  + "getUnpaidAssessmentsByTPIN");
        	
        	soapMessage.saveChanges();

        /* Print the request message */
        	System.out.print("Request SOAP Message for Probase Service = ");
        	soapMessage.writeTo(System.out);

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
					return null;
				}
			} catch (SOAPException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				return null;
			}
			
			return soapMessage;
		}
    }
	
	
	
	
	public Collection<Assessment> handleResponseForGetAssessmentDetailsForDemo(SOAPMessage soapResponse, SwpService swpService) {
		// TODO Auto-generated method stub
		SOAPBody soapBodyResponse;
		try {
			soapBodyResponse = soapResponse.getSOAPBody();
    		
    		Collection<Assessment> assessmentListing = new ArrayList<Assessment>();
    		
    		
    		
			if(soapBodyResponse!=null)
	        {
	        	//String resp = util.parseSOAPBodyToString(soapBodyResponse);
	        	log.info("");
	        	log.info("soapBodyResponse.getNodeName();==>" + soapBodyResponse.getNodeName());
	        	
	        	Name name = SOAPFactory.newInstance().createName("S:Body");
	        	NodeList nodeList = soapBodyResponse.getChildNodes();
	        	Node node = nodeList.item(0);
	        	log.info("node.getNodeName();==>" + node.getNodeName());
	        	NodeList nodeList2 = node.getChildNodes();
	        	log.info("node child size: " + nodeList2.getLength());
	        	
	        	if(nodeList2.getLength()>0)
	        	{
		        	for(int b= 0; b<nodeList2.getLength(); b++)
		        	{
		        		Node node2 = nodeList2.item(b);
			        	log.info("node2.getNodeName();==>" + node2.getNodeName());
			        	if(node2.getNodeName().equalsIgnoreCase("assessmentDetailsList"))
			        	{
				        	NodeList nodeList3 = node2.getChildNodes();
			    			Assessment assessment = new Assessment();
				        	for(int c= 0; c<nodeList3.getLength(); c++)
				        	{
				        		Node node3 = nodeList3.item(c);
				        		log.info("node3.getNodeName();==>" + node3.getNodeName());
				        		
				    			NodeList nodeList4 = node3.getChildNodes();
					        	for(int c1= 0; c1<nodeList4.getLength(); c1++)
					        	{
					        		Node node4 = nodeList4.item(c1);
					        		log.info("node3==>" + node3.getNodeName() + (node4.getNodeValue()==null ? "" : " value = " + node4.getNodeValue()));
					        		try
					        		{
						        		if(node3.getNodeName().equals("amountToBePaid"))
					        			{
						        			assessment.setAmount(Double.valueOf(node4.getNodeValue().trim()));
					        			}
					        			if(node3.getNodeName().equals("assessmentNumber"))
					        			{
					        				assessment.setAssessmentNumber((String)node4.getNodeValue());
					        			}
					        			if(node3.getNodeName().equals("assessmentStatus"))
					        			{
					        			}
					        			if(node3.getNodeName().equals("assessmentYear"))
					        			{
					        				assessment.setAssessmentYear(Integer.valueOf(node4.getNodeValue()));
					        			}
					        			if(node3.getNodeName().equals("interestAvailable"))
					        			{
					        				assessment.setInterest(((String)node4.getNodeValue()).equals("1") ? true:false);
					        			}
					        			if(node3.getNodeName().equals("portOfEntry"))
					        			{
					        				Ports ports = getPortByPortCode(((String)node4.getNodeValue()).trim(), swpService);
					        				if(ports==null)
					        				{
					        					log.info("Ports is null");
					        				}else
					        				{
					        					log.info(ports.getId());
					        				}
					        				assessment.setPorts(ports);
					        			}
					        			if(node3.getNodeName().equals("registrationDate"))
					        			{
					        				assessment.setDateRegistered((String)node4.getNodeValue());
					        			}
					        			if(node3.getNodeName().equals("registrationNumber"))
					        			{
					        				assessment.setRegistrationNumber((String)node4.getNodeValue());
					        			}
					        			if(node3.getNodeName().equals("registrationSerial"))
					        			{
					        				assessment.setRegistrationSerial((String)node4.getNodeValue());
					        			}
					        			if(node3.getNodeName().equals("taxPayerIdentification"))
					        			{
					        				TpinInfo tpinInfo = getTpinInfoByTpId(((String)node4.getNodeValue()).trim(), swpService);
					        				assessment.setTpinInfo(tpinInfo);
					        			}
					        			
					        		}catch(NumberFormatException e)
					        		{
					        			e.printStackTrace();
					        		}
					        	}
				        	}
			    			assessmentListing.add(assessment);
			        	}else if(node2.getNodeName().equalsIgnoreCase("clientTPIN"))
			        	{
			        		NodeList nodeList3 = node2.getChildNodes();
			        		log.info("nodeList3 = " + nodeList3.getLength());
			        		if(nodeList3.getLength()>0)
			        		{
				        		Node node3 = nodeList3.item(0);
				        		log.info("clientTPIN====>" + (String)node3.getNodeValue());
			        		}
			        	}
			        	else if(node2.getNodeName().equalsIgnoreCase("country"))
			        	{
			        		NodeList nodeList3 = node2.getChildNodes();
			        		log.info("nodeList3 = " + nodeList3.getLength());
			        		if(nodeList3.getLength()>0)
			        		{
				        		Node node3 = nodeList3.item(0);
				        		log.info("country====>" + (String)node3.getNodeValue());
				        		if(assessmentListing!=null && assessmentListing.size()>0)
				        		{
					        		for(Iterator<Assessment> iterAss = assessmentListing.iterator(); iterAss.hasNext();)
					        		{
					        			Assessment assessmentOne = iterAss.next();
					        			assessmentOne.setCountry((String)node3.getNodeValue());
					        		}
				        		}
			        		}
			        	}
			        	else if(node2.getNodeName().equalsIgnoreCase("reasoncode"))
			        	{
			        		NodeList nodeList3 = node2.getChildNodes();
			        		log.info("nodeList3 = " + nodeList3.getLength());
			        		if(nodeList3.getLength()>0)
			        		{
				        		Node node3 = nodeList3.item(0);
				        		log.info("reasoncode====>" + (String)node3.getNodeValue());
			        		}
			        	}
			        	else if(node2.getNodeName().equalsIgnoreCase("reasonDescription"))
			        	{
			        		NodeList nodeList3 = node2.getChildNodes();
			        		log.info("nodeList3 = " + nodeList3.getLength());
			        		if(nodeList3.getLength()>0)
			        		{
				        		Node node3 = nodeList3.item(0);
				        		log.info("reasonDescription====>" + (String)node3.getNodeValue());
			        		}
			        	}
			        	else if(node2.getNodeName().equalsIgnoreCase("source"))
			        	{
			        		NodeList nodeList3 = node2.getChildNodes();
			        		log.info("nodeList3 = " + nodeList3.getLength());
			        		if(nodeList3.getLength()>0)
			        		{
				        		Node node3 = nodeList3.item(0);
				        		log.info("source====>" + (String)node3.getNodeValue());
				        		if(assessmentListing!=null && assessmentListing.size()>0)
				        		{
					        		for(Iterator<Assessment> iterAss = assessmentListing.iterator(); iterAss.hasNext();)
					        		{
					        			Assessment assessmentOne = iterAss.next();
					        			assessmentOne.setSource((String)node3.getNodeValue());
					        		}
				        		}
			        		}
			        	}
			        	else if(node2.getNodeName().equalsIgnoreCase("sourceID"))
			        	{
			        		NodeList nodeList3 = node2.getChildNodes();
			        		log.info("nodeList3 = " + nodeList3.getLength());
			        		if(nodeList3.getLength()>0)
			        		{
				        		Node node3 = nodeList3.item(0);
				        		log.info("sourceID====>" + (String)node3.getNodeValue());
				        		if(assessmentListing!=null && assessmentListing.size()>0)
				        		{
					        		for(Iterator<Assessment> iterAss = assessmentListing.iterator(); iterAss.hasNext();)
					        		{
					        			Assessment assessmentOne = iterAss.next();
					        			assessmentOne.setSourceID((String)node3.getNodeValue());
					        		}
				        		}
			        		}
			        	}
			        	else if(node2.getNodeName().equalsIgnoreCase("timestamp"))
			        	{
			        		NodeList nodeList3 = node2.getChildNodes();
			        		log.info("nodeList3 = " + nodeList3.getLength());
			        		if(nodeList3.getLength()>0)
			        		{
				        		Node node3 = nodeList3.item(0);
				        		log.info("timestamp====>" + (String)node3.getNodeValue());
			        		}
			        	}
			        	else if(node2.getNodeName().equalsIgnoreCase("tpin_declarantCode"))
			        	{
			        		NodeList nodeList3 = node2.getChildNodes();
			        		log.info("nodeList3 = " + nodeList3.getLength());
			        		if(nodeList3.getLength()>0)
			        		{
				        		Node node3 = nodeList3.item(0);
				        		log.info("tpin_declarantCode====>" + (String)node3.getNodeValue());
				        		if(assessmentListing!=null && assessmentListing.size()>0)
				        		{
					        		for(Iterator<Assessment> iterAss = assessmentListing.iterator(); iterAss.hasNext();)
					        		{
					        			Assessment assessmentOne = iterAss.next();
					        			assessmentOne.setTpinInfo(getTpinInfoByTpId((String)node3.getNodeValue(), swpService));
					        		}
				        		}
			        		}
			        		
			        		
			        	}
			        	else if(node2.getNodeName().equalsIgnoreCase("type"))
			        	{
			        		NodeList nodeList3 = node2.getChildNodes();
			        		log.info("nodeList3 = " + nodeList3.getLength());
			        		if(nodeList3.getLength()>0)
			        		{
				        		Node node3 = nodeList3.item(0);
				        		log.info("type====>" + (String)node3.getNodeValue());
			        		}
			        	}
		        	}
		        	
	        	}else
	        	{
	        		

	        		assessmentListing = new ArrayList<Assessment>();
		    		
	        	}
	        	
	        	
	        }else
	        {
	        	
	        }
			
			return assessmentListing;
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
        
	}



	
	
	public DeclarationPaymentResult handleResponseForGetDeclarationPayment(SOAPMessage soapResponse, SwpService swpService) {
		// TODO Auto-generated method stub
		SOAPBody soapBodyResponse;
		DeclarationPaymentResult declarationPaymentResult = null;
		try {
			soapBodyResponse = soapResponse.getSOAPBody();
    		
    		
    		
    		
			if(soapBodyResponse!=null)
	        {
	        	//String resp = util.parseSOAPBodyToString(soapBodyResponse);
	        	log.info("");
	        	log.info("soapBodyResponse.getNodeName();==>" + soapBodyResponse.getNodeName());
	        	
	        	Name name = SOAPFactory.newInstance().createName("S:Body");
	        	NodeList nodeList = soapBodyResponse.getChildNodes();
	        	Node node = nodeList.item(0);
	        	log.info("node.getNodeName();==>" + node.getNodeName());	//declarationPaymentResponse
	        	NodeList nodeList2 = node.getChildNodes();
	        	log.info("node child size: " + nodeList2.getLength());
	        	
	        	if(nodeList2.getLength()>0)
	        	{
		        	declarationPaymentResult = new DeclarationPaymentResult();
		        	for(int b= 0; b<nodeList2.getLength(); b++)
		        	{
		        		Node node2 = nodeList2.item(b);
			        	log.info("node2.getNodeName();==>" + node2.getNodeName());
			        	//if(node2.getNodeName().equalsIgnoreCase("interestPaymentResponse"))
			        	//{
				        	//NodeList nodeList3 = node2.getChildNodes();
				        	//for(int c= 0; c<nodeList3.getLength(); c++)
				        	//{
				        		//Node node3 = nodeList3.item(c);
				        		//log.info("node3.getNodeName();==>" + node2.getNodeName());		//result
				        		
				    			NodeList nodeList4 = node2.getChildNodes();
//					        	for(int c1= 0; c1<nodeList4.getLength(); c1++)
//					        	{
					        		Node node4 = nodeList4.item(0);
					        		log.info("node3==>" + node2.getNodeName() + (node4.getNodeValue()==null ? "" : " value = " + node4.getNodeValue()));
					        		try
					        		{
						        		if(node2.getNodeName().equals("result"))
					        			{
						        			declarationPaymentResult.setResult((String)node4.getNodeValue().trim());
						        			log.info("PPPP==>");
					        			}
					        			if(node2.getNodeName().equals("errorCode"))
					        			{
					        				declarationPaymentResult.setErrorCode((String)node4.getNodeValue());
					        				log.info("PPPP1==>");
					        			}
					        			if(node2.getNodeName().equals("receiptSerial"))
					        			{
					        				declarationPaymentResult.setReceiptSerial((String)node4.getNodeValue());
					        				log.info("PPPP2==>");
					        			}
					        			if(node2.getNodeName().equals("receiptNumber"))
					        			{
					        				declarationPaymentResult.setReceiptNumber((String)node4.getNodeValue());
					        				log.info("PPPP3==>");
					        			}
					        			if(node2.getNodeName().equals("receiptDate"))
					        			{
					        				declarationPaymentResult.setReceiptDate((String)node4.getNodeValue());
					        				log.info("PPPP4==>");
					        			}
					        			if(node2.getNodeName().equals("errorDescription"))
					        			{
					        				declarationPaymentResult.setErrorDescription((String)node4.getNodeValue());
					        				log.info("PPPP5==>");
					        			}
					        			
					        		}catch(NumberFormatException e)
					        		{
					        			e.printStackTrace();
					        		}
					        	//}
				        	//}
			        	//}
//			        	else if(node2.getNodeName().equalsIgnoreCase("clientTPIN"))
//			        	{
//			        		NodeList nodeList3 = node2.getChildNodes();
//			        		log.info("nodeList3 = " + nodeList3.getLength());
//			        		if(nodeList3.getLength()>0)
//			        		{
//				        		Node node3 = nodeList3.item(0);
//				        		log.info("clientTPIN====>" + (String)node3.getNodeValue());
//			        		}
//			        	}
			        	
		        	}
		        	
	        	}else
	        	{
	        		

		    		
	        	}
	        	
	        	
	        }else
	        {
	        	
	        }
			
			return declarationPaymentResult;
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
        
	}
	
	
	
	
	
	
	public InterestPaymentResult handleResponseForDoInterestPayment(SOAPMessage soapResponse, SwpService swpService) {
		// TODO Auto-generated method stub
		SOAPBody soapBodyResponse;
		InterestPaymentResult interestPaymentResult = null;
		try {
			soapBodyResponse = soapResponse.getSOAPBody();
    		
    		
    		
    		
			if(soapBodyResponse!=null)
	        {
	        	//String resp = util.parseSOAPBodyToString(soapBodyResponse);
	        	log.info("");
	        	log.info("soapBodyResponse.getNodeName();==>" + soapBodyResponse.getNodeName());
	        	
	        	Name name = SOAPFactory.newInstance().createName("S:Body");
	        	NodeList nodeList = soapBodyResponse.getChildNodes();
	        	Node node = nodeList.item(0);
	        	log.info("node.getNodeName();==>" + node.getNodeName());	//declarationPaymentResponse
	        	NodeList nodeList2 = node.getChildNodes();
	        	log.info("node child size: " + nodeList2.getLength());
	        	
	        	if(nodeList2.getLength()>0)
	        	{
		        	interestPaymentResult = new InterestPaymentResult();
		        	for(int b= 0; b<nodeList2.getLength(); b++)
		        	{
		        		Node node2 = nodeList2.item(b);
			        	log.info("node2.getNodeName();==>" + node2.getNodeName());
			        	//if(node2.getNodeName().equalsIgnoreCase("interestPaymentResponse"))
			        	//{
				        	//NodeList nodeList3 = node2.getChildNodes();
				        	//for(int c= 0; c<nodeList3.getLength(); c++)
				        	//{
				        		//Node node3 = nodeList3.item(c);
				        		//log.info("node3.getNodeName();==>" + node2.getNodeName());		//result
				        		
				    			NodeList nodeList4 = node2.getChildNodes();
//					        	for(int c1= 0; c1<nodeList4.getLength(); c1++)
//					        	{
					        		Node node4 = nodeList4.item(0);
					        		log.info("node3==>" + node2.getNodeName() + (node4.getNodeValue()==null ? "" : " value = " + node4.getNodeValue()));
					        		try
					        		{
						        		if(node2.getNodeName().equals("result"))
					        			{
						        			interestPaymentResult.setResult((String)node4.getNodeValue().trim());
						        			log.info("PPPP==>");
					        			}
					        			if(node2.getNodeName().equals("errorCode"))
					        			{
					        				interestPaymentResult.setErrorCode((String)node4.getNodeValue());
					        				log.info("PPPP1==>");
					        			}
					        			if(node2.getNodeName().equals("officeCode"))
					        			{
					        				interestPaymentResult.setOfficeCode((String)node4.getNodeValue());
					        				log.info("PPPP2==>");
					        			}
					        			if(node2.getNodeName().equals("receiptSerial"))
					        			{
					        				interestPaymentResult.setReceiptSerial((String)node4.getNodeValue());
					        				log.info("PPPP3==>");
					        			}
					        			if(node2.getNodeName().equals("receiptNumber"))
					        			{
					        				interestPaymentResult.setReceiptNumber((String)node4.getNodeValue());
					        				log.info("PPPP4==>");
					        			}
					        			if(node2.getNodeName().equals("receiptDate"))
					        			{
					        				interestPaymentResult.setReceiptDate((String)node4.getNodeValue());
					        				log.info("PPPP5==>");
					        			}
					        			
					        		}catch(NumberFormatException e)
					        		{
					        			e.printStackTrace();
					        		}
					        	//}
				        	//}
			        	//}
//			        	else if(node2.getNodeName().equalsIgnoreCase("clientTPIN"))
//			        	{
//			        		NodeList nodeList3 = node2.getChildNodes();
//			        		log.info("nodeList3 = " + nodeList3.getLength());
//			        		if(nodeList3.getLength()>0)
//			        		{
//				        		Node node3 = nodeList3.item(0);
//				        		log.info("clientTPIN====>" + (String)node3.getNodeValue());
//			        		}
//			        	}
			        	
		        	}
		        	
	        	}else
	        	{
	        		

		    		
	        	}
	        	
	        	
	        }else
	        {
	        	
	        }
			
			return interestPaymentResult;
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
        
	}
	
	
	
	
	
	
	
	private static List<PaymentHistory> handleCorePaymentsForDomTax(ComminsApplicationState cas, DomTax domTax, 
			String srcAccountNumber, 
			Settings settingsZRABankAccountNumber, Settings settingsZRABankAccountSortCode, 
			String platformBank, SwpService swpService, String remoteIPAddress, PortalUser portalUser, 
			String bankName, Mailer emailer, 
			String applicationName, 
			String proxyUsername, String proxyPassword, String proxyHost, String proxyPort, String bankPaymentWebServiceUrl) {
		//Register the amount to be paid in payment history - CORE PAYMENT.
		//Do a breakdown of the amount into the paymenthistory breakdown table
		//
		//Register the amount to be paid in payment history - INTEREST PAYMENT
		//Do a breakdown of the amount into the paymenthistory breakdown table
		//
		//Register the amount to be paid in payment history - MISC PAYMENT
		//Do a brakdown of the amount into the paymenthistory breakdown table
		
		Logger log = Logger.getLogger(Util.class);
		List<PaymentHistory> payments = new ArrayList<PaymentHistory>();
		boolean makeCorePayment = false;
					
		log.info(10);
		
		
		ArrayList<PaymentHistory> paymentHistoryList = null;

		if(settingsZRABankAccountNumber!=null && settingsZRABankAccountNumber.getValue().length()>0 && 
				settingsZRABankAccountSortCode!=null && settingsZRABankAccountSortCode.getValue().length()>0)
		{
			log.info(11);
			DateFormat df1 = new SimpleDateFormat( "SssmmHHyyMMdd");
			
			DateFormat df2 = new SimpleDateFormat( "yyyyMMdd");
			DateFormat df3 = new SimpleDateFormat( "Sssmm");
	        DateFormat df = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss:S");
	        String currentTimeStamp1 = df1.format(new Date());
	        String currentTimeStamp2 = df2.format(new Date());
	        String currentTimeStamp3 = df3.format(new Date());
	        String currentTimeStamp = df.format(new Date());
	        
        	paymentHistoryList = new ArrayList<PaymentHistory>();
	        String serialNo = currentTimeStamp1 + "" + domTax.getPaymentRegNo();

			

	        
			        
	        currentTimeStamp1 = df1.format(new Date());
	        String uniqId = "SPI" +  currentTimeStamp1;
	        PaymentHistory paymentHistory = new PaymentHistory();
        	//paymentHistory.setPayableAmount(assessment.getAmount() - assessment.getInterestAmount());
	        paymentHistory.setPayableAmount(domTax.getAmountPayable());
        	paymentHistory.setAssessment(null);
        	paymentHistory.setDomTax(domTax);
	        paymentHistory.setCoreAccountNumber(srcAccountNumber);
	        paymentHistory.setSourceAccountNumber(srcAccountNumber);
	        paymentHistory.setSourceSortCode(portalUser.getCompany().getBankBranches().getBankCode());
	        paymentHistory.setCurrency(ProbaseConstants.CURRENCY);
	        paymentHistory.setDescription("Payment for Dom Tax");
	        try {
				paymentHistory.setEntryDate(df2.parse(currentTimeStamp2));
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        paymentHistory.setExchangeRate(ProbaseConstants.EXCHANGE_RATE);
	        paymentHistory.setPaymentType(PaymentTypeConstants.PAYMENTTYPE_DOM);
	        paymentHistory.setProbaseTransactionSerialNo(serialNo);
	        paymentHistory.setReceipientAccountNumber(settingsZRABankAccountNumber.getValue());
	        paymentHistory.setReceipientCoreAccountNumber(settingsZRABankAccountNumber.getValue());
	        paymentHistory.setReceipientSortCode(settingsZRABankAccountSortCode.getValue());
	        paymentHistory.setRequestMessageId(uniqId);
	        try {
				paymentHistory.setRequestTimestamp(new Timestamp(df.parse(currentTimeStamp).getTime()));
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        paymentHistory.setTransactionReferenceId(null);
	        try {
				paymentHistory.setValueDate(df2.parse(currentTimeStamp2));
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        paymentHistory.setStatus(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_PENDING);
	        paymentHistory.setPortalUser(portalUser);
	        Timestamp dTxn = new Timestamp((new Date()).getTime());
	        paymentHistory.setDateofTransaction(dTxn);
	        paymentHistory = (PaymentHistory)swpService.createNewRecord(paymentHistory);
        	paymentHistoryList.add(paymentHistory);
	        
        	log.info(14);
        	
        	
        	
        	/*****do breakdown of the domtax payment***/
        	
			
        						

        	Boolean success = true;
    		currentTimeStamp1 = df1.format(new Date());
	        currentTimeStamp2 = df2.format(new Date());
	        currentTimeStamp3 = df3.format(new Date());
	        currentTimeStamp = df.format(new Date());
	        log.info("testing payments for ---" + paymentHistory.getPaymentType().getValue());
	        
    		uniqId = paymentHistory.getRequestMessageId();
    		
    		FundsTransferResponse ftr = processPaymentStep1(cas, platformBank, settingsZRABankAccountSortCode, settingsZRABankAccountNumber, 
    				Double.toString(paymentHistory.getPayableAmount()), portalUser.getCompany().getAccountNumber(), 
    				portalUser, portalUser.getCompany().getBankBranches().getBankCode(), "1", uniqId,
				settingsZRABankAccountNumber.getValue(), settingsZRABankAccountSortCode.getValue(), 
				currentTimeStamp, currentTimeStamp2, 
				currentTimeStamp2, currentTimeStamp3, "00-00-00", currentTimeStamp1, paymentHistory.getAssessment(),
				paymentHistory.getPaymentType(), swpService, 
				proxyUsername, proxyPassword, proxyHost, proxyPort, bankPaymentWebServiceUrl, 
				(paymentHistory.getAssessment()!=null ? (paymentHistory.getAssessment().getPorts().getPortCode() + "/" + 
						paymentHistory.getAssessment().getAssessmentNumber() + "/" + 
						paymentHistory.getAssessment().getAssessmentYear() + "/D") : 
						(paymentHistory.getDomTax()!=null ? paymentHistory.getRequestMessageId() : ""))
							
				);
    		if(ftr!=null && ftr.getStatus()!=null && ftr.getStatus().equals(Boolean.TRUE))
    		{
    			log.info("FTR --->" + ftr.getStatus() + " && ftr ==" + ftr.getAccountNumber());	
        		domTax.setPaidFor(Boolean.TRUE);
    			swpService.updateRecord(domTax);
    			SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");
    			
    			
    			
    			HashMap<String, Double> hashMapFee = new HashMap<String, Double>();
    			String receiptNumber = paymentHistory.getDomTax().getPaymentRegNo() + 
    					"/" + paymentHistory.getDomTax().getTpinInfo().getTpin();
            	paymentHistory.setTransactionReferenceId(receiptNumber);
    			paymentHistory.setReceiptNumber(receiptNumber);
    			paymentHistory.setStatus(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_APPROVED);
    			paymentHistory.setDateofTransaction(new Timestamp((new Date()).getTime()));
    			swpService.updateRecord(paymentHistory);
    			
    			
    			PaymentBreakDownHistory paymentBreakDownHistory = new PaymentBreakDownHistory();
    			TaxType taxType = getTaxTypeByTaxCode(swpService, "AIT");
            	paymentBreakDownHistory.setTaxType(taxType);
            	paymentBreakDownHistory.setAmount(domTax.getAmountPayable());
            	paymentBreakDownHistory.setStatus(PaymentBreakDownHistoryConstants.PAYMENT_BREAKDOWN_STATUS_PENDING);
    	        paymentBreakDownHistory.setPaymentHistory(paymentHistory);
    	        paymentBreakDownHistory.setTransactionNumber(
    	        		RandomStringUtils.random(8, true, true).toUpperCase());
            	paymentBreakDownHistory = (PaymentBreakDownHistory)swpService.createNewRecord(paymentBreakDownHistory);
            	
            	
    			
    			log.info("==================");
    			log.info("Select rt from Balance rt where rt.company.id = " + portalUser.getCompany().getId());
    			Balance bal = (Balance)swpService.getUniqueRecordByHQL("Select rt from Balance rt where rt.company.id = " + portalUser.getCompany().getId());
    			log.info(">>>>" + bal.getAmount() + " && " + paymentHistory.getPayableAmount() + " == " + (bal.getAmount() - paymentHistory.getPayableAmount()));
    			bal.setAmount(bal.getAmount() - paymentHistory.getPayableAmount());
    			swpService.updateRecord(bal);
    			cas.setBalance(bal);
    			
    			hashMapFee.put(paymentHistory.getDomTax().getPaymentRegNo(), paymentHistory.getDomTax().getAmountPayable());
    			
    			
    			//receiptNo_paymentType_ReqId_Amt_PRN_TPIN_
//    			payments.add(receiptNumber + ":::" + paymentHistory.getPaymentType().getValue() + ":::" + 
//    					paymentHistory.getRequestMessageId() + ":::" + paymentHistory.getPayableAmount());
    			payments.add(paymentHistory);
    			
    			
				PaymentTempHolder pth = new PaymentTempHolder();
				pth.setDateAdded(new Timestamp((new Date()).getTime()));
				pth.setPaymentBreakDownHistory(paymentBreakDownHistory);
    			pth = (PaymentTempHolder)swpService.createNewRecord(pth);
    			
				notifyPaymentForDomTax(portalUser, domTax, emailer, hashMapFee, bankName, applicationName, 
						receiptNumber, "R " + paymentHistory.getReceiptNumber(), 
						paymentHistory.getPayableAmount());
    		}else
    		{
//    			domTax.setPaidFor(Boolean.FALSE);
//    			swpService.updateRecord(domTax);
//    			SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");
//    			
//    			
//    			HashMap<String, Double> hashMapFee = new HashMap<String, Double>();
//    			String receiptNumber = paymentHistory.getDomTax().getPaymentRegNo() + 
//    					"/" + paymentHistory.getDomTax().getTpinInfo().getTpin();
//            	paymentHistory.setTransactionReferenceId(receiptNumber);
//    			paymentHistory.setReceiptNumber(receiptNumber);
//    			paymentHistory.setStatus(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_APPROVED);
//    			paymentHistory.setDateofTransaction(new Timestamp((new Date()).getTime()));
//    			swpService.updateRecord(paymentHistory);
//    			
//    			
////    			payments.add(receiptNumber + ":::" + paymentHistory.getPaymentType().getValue() + ":::" + 
////    					paymentHistory.getRequestMessageId() + ":::" + paymentHistory.getPayableAmount());
//    			payments.add(paymentHistory);
    			payments=null;
    		}
    		
    		//SAVE BREAKDOWN OF TAXES
    		
		}else
		{
			payments=null;
		}
	
		return payments;
	}
	
	
	
	
	public static void redirectUserBackToZRAPortalWithPost(
			ActionResponse aRes, ActionRequest aReq, PaymentHistory ph, String tpin, String taxPayerName,
			Double amountPayable, String txnDate, String uniqId, String status) {
		// TODO Auto-generated method stub
		
		Logger log = Logger.getLogger(Util.class);
//		
//		aRes.setProperty("Method", "POST");
//		aRes.setProperty("Accept-Encoding", "gzip,deflate,sdch");
//		aRes.setProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//		aRes.setProperty("Content-Type", "application/x-www-form-urlencoded");
//		aRes.setProperty("Accept-Encoding", "gzip,deflate,sdch");
//		aRes.setProperty("Accept-Encoding", "gzip,deflate,sdch");
		
		aRes.setRenderParameter("prn", ph.getDomTax().getPaymentRegNo());
		aRes.setRenderParameter("tpin", tpin);
		aRes.setRenderParameter("tp_name", taxPayerName);
		aRes.setRenderParameter("b_amnt", Double.toString(amountPayable));
		aRes.setRenderParameter("b_pmnt_dt", txnDate);
		aRes.setRenderParameter("b_ref_no", uniqId);
		aRes.setRenderParameter("b_status", status);
		
		String encData = "prn=" + ph.getDomTax().getPaymentRegNo() + 
				"|tpin=" + tpin + "|tp_name=" + taxPayerName + "|b_amnt=" + Double.toString(amountPayable) + 
				"|b_pmnt_dt=" + txnDate + "|b_status=" + status + "|prn_exp_dt=" + ph.getDomTax().getExpDate() + 
				"|b_ref_no=" + uniqId;
		aReq.setAttribute("prn", ph.getDomTax().getPaymentRegNo());
		aReq.setAttribute("tpin", tpin);
		aReq.setAttribute("tp_name", taxPayerName);
		aReq.setAttribute("b_amnt", Double.toString(amountPayable));
		aReq.setAttribute("b_pmnt_dt", txnDate);
		aReq.setAttribute("b_ref_no", uniqId);
		aReq.setAttribute("b_status", status);
		aReq.setAttribute("encdata", encData);
		aRes.setRenderParameter("encdata", encData);
		log.info("redirectUserBackToZRAPortalWithPost == " + encData);
		try {
			//aRes.sendRedirect("http://localhost:70/zraportal/action.php");
			aRes.setRenderParameter("jspPage", "/html/domtaxportlet/redirecttozra.jsp");
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//		PostMethod post = new PostMethod("http://localhost:8087/ProbaseClient/ActiveServlet");
//		
////		try {
//			NameValuePair nvp1 = new NameValuePair();
//			NameValuePair nvp2 = new NameValuePair();
//			NameValuePair nvp3 = new NameValuePair();
//			NameValuePair nvp4 = new NameValuePair();
//			NameValuePair nvp5 = new NameValuePair();
//			NameValuePair nvp6 = new NameValuePair();
//			NameValuePair nvp7 = new NameValuePair();
//			NameValuePair nvp8 = new NameValuePair();
//			nvp1.setName("prn");
//			nvp1.setValue(ph.getDomTax().getPaymentRegNo());
//			post.addParameter(nvp1);
//			nvp2.setName("tpin");
//			nvp2.setValue(tpin);
//			post.addParameter(nvp2);
//			nvp3.setName("tp_name");
//			nvp3.setValue(taxPayerName);
//			post.addParameter(nvp3);
//			nvp4.setName("b_amnt");
//			nvp4.setValue(Double.toString(amountPayable));
//			post.addParameter(nvp4);
//			nvp5.setName("b_pmnt_dt");
//			nvp5.setValue(txnDate);
//			post.addParameter(nvp5);
//			nvp6.setName("b_ref_no");
//			nvp6.setValue(uniqId);
//			post.addParameter(nvp6);
//			nvp7.setName("b_status");
//			nvp7.setValue(status);
//			post.addParameter(nvp7);
//			nvp8.setName("encdata");
//			String encData = "prn=" + ph.getDomTax().getPaymentRegNo() + 
//					"|tpin=" + tpin + "|tp_name=" + taxPayerName + "|b_amnt=" + Double.toString(amountPayable) + 
//					"|b_pmnt_dt=" + txnDate + "|b_status=" + status + "|prn_exp_dt=" + ph.getDomTax().getExpDate() + 
//					"|b_ref_no=" + uniqId;
//			nvp8.setValue(encData);
//			post.addParameter(nvp8);
////		} catch (UnsupportedEncodingException e1) {
////			// TODO Auto-generated catch block
////			e1.printStackTrace();
////		}
//		
//		post.setRequestHeader("Content-type", "text/html; charset=utf-8");
//		org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
//		try {
//			httpClient.executeMethod(post);
//		} catch (HttpException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		post.releaseConnection();
	}



	private static void notifyPaymentForDomTax(PortalUser portalUser, DomTax domTax, 
			Mailer emailer, HashMap<String, Double> hashMapFee, String bankName, String applicationName, 
			String customRef, String recNo, Double amt) {
		// TODO Auto-generated method stub

		SendMail sm = emailer.emailPayment(portalUser.getEmailAddress(), 
				portalUser.getCompany().getCompanyName(),
				domTax.getPaymentRegNo(),
				hashMapFee, 
				portalUser.getCompany().getAccountNumber(), 
				portalUser.getFirstName(), 
				portalUser.getLastName(), 
				"Payment for " +
				"Domestic Tax PRN " + domTax.getPaymentRegNo(), 
				bankName, 
				applicationName);
		
		String message = "TPayment for " +
				"Domestic Tax PRN " + domTax.getPaymentRegNo();
		message = "eTax Payment Confirmation!\nDomestic Ref: "+customRef+"\n Receipt No: "+recNo+"\nAmount Paid: ZMW" + new Util().roundUpAmount(amt);
		new SendSms(portalUser.getMobileNumber(), message, 
			"SmartPay", "10.236.6.99", "80");
		
		if(portalUser.getFirstAlternativeEmailAddress()!=null && portalUser.getFirstAlternativeEmailAddress().length()>0)
		{
			SendMail sm1 = emailer.emailPayment(portalUser.getFirstAlternativeEmailAddress(), 
				portalUser.getCompany().getCompanyName(),
				domTax.getPaymentRegNo(),
				hashMapFee, 
				portalUser.getCompany().getAccountNumber(), 
				portalUser.getFirstName(), 
				portalUser.getLastName(), 
				"Tax Assessment Interest Payment for " +
				"Domestic Tax PRN " + domTax.getPaymentRegNo(),  
				bankName, 
				applicationName);
		}
		
		message = "Payment for " +
				"Assessment Registration Number " + domTax.getPaymentRegNo();
		message = "eTax Payment Confirmation!\nDomestic Ref: "+customRef+"\n Receipt No: "+recNo+"\nAmount Paid: ZMW" + new Util().roundUpAmount(amt);
		new SendSms(portalUser.getFirstAlternativeMobileNumber(), message, 
			"SmartPay", "10.236.6.99", "80");
		
		if(portalUser.getSecondAlternativeEmailAddress()!=null && portalUser.getSecondAlternativeEmailAddress().length()>0)
		{
			SendMail sm2 = emailer.emailPayment(portalUser.getSecondAlternativeEmailAddress(), 
				portalUser.getCompany().getCompanyName(),
				domTax.getPaymentRegNo(),
				hashMapFee, 
				portalUser.getCompany().getAccountNumber(), 
				portalUser.getFirstName(), 
				portalUser.getLastName(), 
				"Payment for " +
				"Domestic Tax PRN " + domTax.getPaymentRegNo(),  
				bankName, 
				applicationName);
		}
		
		message = "Payment for " +
				"Assessment Registration Number " + domTax.getPaymentRegNo();
		message = "eTax Payment Confirmation!\nDomestic Ref: "+customRef+"\n Receipt No: "+recNo+"\nAmount Paid: ZMW" + new Util().roundUpAmount(amt);
		new SendSms(portalUser.getSecondAlternativeMobileNumber(), message, 
			"SmartPay", "10.236.6.99", "80");
		
		SendMail sm3 = emailer.emailPayment(portalUser.getCompany().getEmailAddress(), 
				portalUser.getCompany().getCompanyName(),
				domTax.getPaymentRegNo(),
				hashMapFee, 
				portalUser.getCompany().getAccountNumber(), 
				portalUser.getFirstName(), 
				portalUser.getLastName(), 
				"Tax Assessment Interest Payment for " +
				"Domestic Tax PRN " + domTax.getPaymentRegNo(),  
				bankName, 
				applicationName);
		
		message = "Payment for " +
				"Domestic Tax PRN " + domTax.getPaymentRegNo();
		message = "eTax Payment Confirmation!\nDomestic Ref: "+customRef+"\n Receipt No: "+recNo+"\nAmount Paid: ZMW" + new Util().roundUpAmount(amt);
		new SendSms(portalUser.getCompany().getMobileNumber(), message, 
			"SmartPay", "10.236.6.99", "80");
	}
	
	
	private static void notifyPaymentForAssessmentCore(PortalUser portalUser, Assessment assessment, 
			Mailer emailer, HashMap<String, Double> hashMapFee, String bankName, String applicationName, String customRef, String recNo, Double amt) {
	
		SendMail sm = emailer.emailPayment(portalUser.getEmailAddress(), 
				portalUser.getCompany().getCompanyName(),
				assessment.getRegistrationNumber(),
				hashMapFee, 
				portalUser.getCompany().getAccountNumber(), 
				portalUser.getFirstName(), 
				portalUser.getLastName(), 
				"Tax Assessment Payment for Assessment Registration Number " + assessment.getRegistrationNumber(), 
				bankName, 
				applicationName);
		
		String message = "Tax Assessment Payment for " +
				"Assessment Registration Number " + assessment.getRegistrationNumber();
		message = "eTax Payment Confirmation!\nCustoms Ref: "+customRef+"\n Receipt No: "+recNo+"\nAmount Paid: ZMW" + new Util().roundUpAmount(amt);
		new SendSms(portalUser.getMobileNumber(), message, 
			"SmartPay", "10.236.6.99", "80");
		
		if(portalUser.getFirstAlternativeEmailAddress()!=null && portalUser.getFirstAlternativeEmailAddress().length()>0)
		{
			SendMail sm1 = emailer.emailPayment(portalUser.getFirstAlternativeEmailAddress(), 
				portalUser.getCompany().getCompanyName(),
				assessment.getRegistrationNumber(),
				hashMapFee, 
				portalUser.getCompany().getAccountNumber(), 
				portalUser.getFirstName(), 
				portalUser.getLastName(), 
				"Tax Assessment Payment for Assessment Registration Number " + assessment.getRegistrationNumber(), 
				bankName, 
				applicationName);
		}
		
		message = "Tax Assessment Payment for " +
				"Assessment Registration Number " + assessment.getRegistrationNumber();
		message = "eTax Payment Confirmation!\nCustoms Ref: "+customRef+"\n Receipt No: "+recNo+"\nAmount Paid: ZMW" + new Util().roundUpAmount(amt);
		new SendSms(portalUser.getFirstAlternativeMobileNumber(), message, 
			"SmartPay", "10.236.6.99", "80");
		
		if(portalUser.getSecondAlternativeEmailAddress()!=null && portalUser.getSecondAlternativeEmailAddress().length()>0)
		{
			//"smicer66@gmail.com"
			SendMail sm2 = emailer.emailPayment(portalUser.getSecondAlternativeEmailAddress(), 
				portalUser.getCompany().getCompanyName(),
				assessment.getRegistrationNumber(),
				hashMapFee, 
				portalUser.getCompany().getAccountNumber(), 
				portalUser.getFirstName(), 
				portalUser.getLastName(), 
				"Tax Assessment Payment for Assessment Registration Number " + assessment.getRegistrationNumber(), 
				bankName, 
				applicationName);
		}
		message = "Tax Assessment Payment for " +
				"Assessment Registration Number " + assessment.getRegistrationNumber();
		message = "eTax Payment Confirmation!\nCustoms Ref: "+customRef+"\n Receipt No: "+recNo+"\nAmount Paid: ZMW" + new Util().roundUpAmount(amt);
		new SendSms(portalUser.getSecondAlternativeMobileNumber(), message, 
			"SmartPay", "10.236.6.99", "80");
		
		
		SendMail sm3 = emailer.emailPayment(portalUser.getCompany().getEmailAddress(), 
				portalUser.getCompany().getCompanyName(),
				assessment.getRegistrationNumber(),
				hashMapFee, 
				portalUser.getCompany().getAccountNumber(), 
				portalUser.getFirstName(), 
				portalUser.getLastName(), 
				"Tax Assessment Payment for Assessment Registration Number " + assessment.getRegistrationNumber(), 
				bankName, 
				applicationName);
		
		message = "Tax Assessment Payment for " +
				"Assessment Registration Number " + assessment.getRegistrationNumber();
		message = "eTax Payment Confirmation!\nCustoms Ref: "+customRef+"\n Receipt No: "+recNo+"\nAmount Paid: ZMW" + new Util().roundUpAmount(amt);
		new SendSms(portalUser.getCompany().getMobileNumber(), message, 
			"SmartPay", "10.236.6.99", "80");
	}


	private static void notifyPaymentForAssessmentInterest(PortalUser portalUser, Assessment assessment, 
			Mailer emailer, HashMap<String, Double> hashMapFee, String bankName, String applicationName, String customRef, String recNo, Double amt) {
		// TODO Auto-generated method stub

		SendMail sm = emailer.emailPayment(portalUser.getEmailAddress(), 
				portalUser.getCompany().getCompanyName(),
				assessment.getRegistrationNumber(),
				hashMapFee, 
				portalUser.getCompany().getAccountNumber(), 
				portalUser.getFirstName(), 
				portalUser.getLastName(), 
				"Tax Assessment Interest Payment for " +
				"Assessment Registration Number " + assessment.getRegistrationNumber(), 
				bankName, 
				applicationName);
		
		String message = "Tax Assessment Interest Payment for " +
				"Assessment Registration Number " + assessment.getRegistrationNumber();
		message = "eTax Interest Payment Confirmation!\nCustoms Ref: "+customRef+"\n Receipt No: "+recNo+"\nAmount Paid: ZMW" + new Util().roundUpAmount(amt);
		new SendSms(portalUser.getMobileNumber(), message, 
			"SmartPay", "10.236.6.99", "80");
		
		if(portalUser.getFirstAlternativeEmailAddress()!=null && portalUser.getFirstAlternativeEmailAddress().length()>0)
		{
			SendMail sm1 = emailer.emailPayment(portalUser.getFirstAlternativeEmailAddress(), 
				portalUser.getCompany().getCompanyName(),
				assessment.getRegistrationNumber(),
				hashMapFee, 
				portalUser.getCompany().getAccountNumber(), 
				portalUser.getFirstName(), 
				portalUser.getLastName(), 
				"Tax Assessment Interest Payment for " +
				"Assessment Registration Number " + assessment.getRegistrationNumber(), 
				bankName, 
				applicationName);
		}
		
		message = "Tax Assessment Interest Payment for " +
				"Assessment Registration Number " + assessment.getRegistrationNumber();
		message = "eTax Interest Payment Confirmation!\nCustoms Ref: "+customRef+"\n Receipt No: "+recNo+"\nAmount Paid: ZMW" + new Util().roundUpAmount(amt);
		new SendSms(portalUser.getFirstAlternativeMobileNumber(), message, 
			"SmartPay", "10.236.6.99", "80");
		
		if(portalUser.getSecondAlternativeEmailAddress()!=null && portalUser.getSecondAlternativeEmailAddress().length()>0)
		{
			SendMail sm2 = emailer.emailPayment(portalUser.getSecondAlternativeEmailAddress(), 
				portalUser.getCompany().getCompanyName(),
				assessment.getRegistrationNumber(),
				hashMapFee, 
				portalUser.getCompany().getAccountNumber(), 
				portalUser.getFirstName(), 
				portalUser.getLastName(), 
				"Tax Assessment Interest Payment for " +
				"Assessment Registration Number " + assessment.getRegistrationNumber(), 
				bankName, 
				applicationName);
		}
		
		message = "Tax Assessment Interest Payment for " +
				"Assessment Registration Number " + assessment.getRegistrationNumber();
		message = "eTax Interest Payment Confirmation!\nCustoms Ref: "+customRef+"\n Receipt No: "+recNo+"\nAmount Paid: ZMW" + new Util().roundUpAmount(amt);
		new SendSms(portalUser.getSecondAlternativeMobileNumber(), message, 
			"SmartPay", "10.236.6.99", "80");
		
		SendMail sm3 = emailer.emailPayment(portalUser.getEmailAddress(), 
				portalUser.getCompany().getCompanyName(),
				assessment.getRegistrationNumber(),
				hashMapFee, 
				portalUser.getCompany().getAccountNumber(), 
				portalUser.getFirstName(), 
				portalUser.getLastName(), 
				"Tax Assessment Interest Payment for " +
				"Assessment Registration Number " + assessment.getRegistrationNumber(), 
				bankName, 
				applicationName);
		
		message = "Tax Assessment Interest Payment for " +
				"Assessment Registration Number " + assessment.getRegistrationNumber();
		message = "eTax Payment Confirmation!\nCustoms Ref: "+customRef+"\n Receipt No: "+recNo+"\nAmount Paid: ZMW" + new Util().roundUpAmount(amt);
		new SendSms(portalUser.getCompany().getMobileNumber(), message, 
			"SmartPay", "10.236.6.99", "80");
	}



	public static Collection<Assessment> handleResponseForGetAssessmentDetails(String declarantFlag, String tpinCode, SOAPMessage soapResponse, SwpService swpService, PortalUser portalUser) {
		// TODO Auto-generated method stub
		Logger log = Logger.getLogger(Util.class);
		SOAPBody soapBodyResponse;
		try {
			soapBodyResponse = soapResponse.getSOAPBody();
    		
    		Collection<Assessment> assessmentListing = new ArrayList<Assessment>();
    		
    		
    		
			if(soapBodyResponse!=null)
	        {
	        	//String resp = util.parseSOAPBodyToString(soapBodyResponse);
	        	log.info("");
	        	log.info("soapBodyResponse.getNodeName();==>" + soapBodyResponse.getNodeName());
	        	
	        	Name name = SOAPFactory.newInstance().createName("S:Body");
	        	NodeList nodeList = soapBodyResponse.getChildNodes();
	        	Node node = nodeList.item(0);
	        	log.info("node.getNodeName();==>" + node.getNodeName());
	        	NodeList nodeList2 = node.getChildNodes();
	        	log.info("node child size: " + nodeList2.getLength());
	        	
	        	if(nodeList2.getLength()>0)
	        	{
		        	for(int b= 0; b<nodeList2.getLength(); b++)
		        	{
		        		Node node2 = nodeList2.item(b);
			        	log.info("node2.getNodeName();==>" + node2.getNodeName());
			        	if(node2.getNodeName().equalsIgnoreCase("assessmentDetailsList"))
			        	{
				        	NodeList nodeList3 = node2.getChildNodes();
			    			Assessment assessment = new Assessment();
				        	for(int c= 0; c<nodeList3.getLength(); c++)
				        	{
				        		Node node3 = nodeList3.item(c);
				        		log.info("node3.getNodeName();==>" + node3.getNodeName());
				        		
				    			NodeList nodeList4 = node3.getChildNodes();
					        	for(int c1= 0; c1<nodeList4.getLength(); c1++)
					        	{
					        		Node node4 = nodeList4.item(c1);
					        		log.info("node3==>" + node3.getNodeName() + (node4.getNodeValue()==null ? "" : " value = " + node4.getNodeValue()));
					        		try
					        		{
						        		if(node3.getNodeName().equals("amountToBePaid"))
					        			{
						        			assessment.setAmount(Double.valueOf(node4.getNodeValue().trim()));
					        			}
					        			if(node3.getNodeName().equals("assessmentNumber"))
					        			{
					        				assessment.setAssessmentNumber((String)node4.getNodeValue());
					        			}
					        			if(node3.getNodeName().equals("assessmentStatus"))
					        			{
					        			}
					        			if(node3.getNodeName().equals("assessmentYear"))
					        			{
					        				assessment.setAssessmentYear(Integer.valueOf(node4.getNodeValue()));
					        			}
					        			if(node3.getNodeName().equals("interestAvailable"))
					        			{
					        				assessment.setInterest(false);
					        				assessment.setInterestAmount(null);
					        			}
					        			if(node3.getNodeName().equals("portOfEntry"))
					        			{
					        				Ports ports = getPortByPortCode(((String)node4.getNodeValue()).trim(), swpService);
					        				if(ports==null)
					        				{
					        					log.info("Ports is null");
					        				}else
					        				{
					        					log.info(ports.getId());
					        				}
					        				assessment.setPorts(ports);
					        			}
					        			if(node3.getNodeName().equals("registrationDate"))
					        			{
					        				assessment.setDateRegistered((String)node4.getNodeValue());
					        			}
					        			if(node3.getNodeName().equals("registrationNumber"))
					        			{
					        				assessment.setRegistrationNumber((String)node4.getNodeValue());
					        			}
					        			if(node3.getNodeName().equals("registrationSerial"))
					        			{
					        				assessment.setRegistrationSerial((String)node4.getNodeValue());
					        			}
					        			if(node3.getNodeName().equals("clientTaxPayerIdentification"))
					        			{
					        				//TpinInfo tpinInfo = getTpinInfoByTpId(, swpService);
					        				//assessment.setTpinInfo(tpinInfo);
					        				if(declarantFlag!=null && declarantFlag.equals("Y"))
					        				{
					        					if(portalUser.getCompany().getClearingAgent()!=null && portalUser.getCompany().getClearingAgent().equals(Boolean.TRUE))
					        						assessment.setClientTpin(((String)node4.getNodeValue()).trim());
					        					else
					        						assessment.setClientTpin(tpinCode);
					        				}else if(declarantFlag!=null && declarantFlag.equals("N"))
					        				{
					        					assessment.setClientTpin(tpinCode);
					        				}
					        				
					        			}
					        			if(node3.getNodeName().equals("declarantCode"))
					        			{
					        				//TpinInfo tpinInfo = getTpinInfoByTpId(, swpService);
					        				//assessment.setTpinInfo(tpinInfo);
					        				if(declarantFlag!=null && declarantFlag.equals("Y"))
					        				{
					        					if(portalUser.getCompany().getClearingAgent()!=null && portalUser.getCompany().getClearingAgent().equals(Boolean.TRUE))
					        						assessment.setDeclarantTpin(tpinCode);
					        					else
					        						assessment.setDeclarantTpin(((String)node4.getNodeValue()).trim());
					        				}
					        				else if(declarantFlag!=null && declarantFlag.equals("N"))
					        				{
					        					assessment.setDeclarantTpin(((String)node4.getNodeValue()).trim());
					        				}
					        				
					        			}
					        			
					        		}catch(NumberFormatException e)
					        		{
					        			e.printStackTrace();
					        		}
					        	}
				        	}
			    			assessmentListing.add(assessment);
			        	}
//			        	else if(node2.getNodeName().equalsIgnoreCase("clientTPIN"))
//			        	{
//			        		NodeList nodeList3 = node2.getChildNodes();
//			        		log.info("nodeList3 = " + nodeList3.getLength());
//			        		if(nodeList3.getLength()>0)
//			        		{
//				        		Node node3 = nodeList3.item(0);
//				        		log.info("clientTPIN====>" + (String)node3.getNodeValue());
//			        		}
//			        	}
			        	else if(node2.getNodeName().equalsIgnoreCase("country"))
			        	{
			        		NodeList nodeList3 = node2.getChildNodes();
			        		log.info("nodeList3 = " + nodeList3.getLength());
			        		if(nodeList3.getLength()>0)
			        		{
				        		Node node3 = nodeList3.item(0);
				        		log.info("country====>" + (String)node3.getNodeValue());
				        		if(assessmentListing!=null && assessmentListing.size()>0)
				        		{
					        		for(Iterator<Assessment> iterAss = assessmentListing.iterator(); iterAss.hasNext();)
					        		{
					        			Assessment assessmentOne = iterAss.next();
					        			assessmentOne.setCountry((String)node3.getNodeValue());
					        		}
				        		}
			        		}
			        	}
			        	else if(node2.getNodeName().equalsIgnoreCase("reasoncode"))
			        	{
			        		NodeList nodeList3 = node2.getChildNodes();
			        		log.info("nodeList3 = " + nodeList3.getLength());
			        		if(nodeList3.getLength()>0)
			        		{
				        		Node node3 = nodeList3.item(0);
				        		log.info("reasoncode====>" + (String)node3.getNodeValue());
			        		}
			        	}
			        	else if(node2.getNodeName().equalsIgnoreCase("reasonDescription"))
			        	{
			        		NodeList nodeList3 = node2.getChildNodes();
			        		log.info("nodeList3 = " + nodeList3.getLength());
			        		if(nodeList3.getLength()>0)
			        		{
				        		Node node3 = nodeList3.item(0);
				        		log.info("reasonDescription====>" + (String)node3.getNodeValue());
			        		}
			        	}
			        	else if(node2.getNodeName().equalsIgnoreCase("source"))
			        	{
			        		NodeList nodeList3 = node2.getChildNodes();
			        		log.info("nodeList3 = " + nodeList3.getLength());
			        		if(nodeList3.getLength()>0)
			        		{
				        		Node node3 = nodeList3.item(0);
				        		log.info("source====>" + (String)node3.getNodeValue());
				        		if(assessmentListing!=null && assessmentListing.size()>0)
				        		{
					        		for(Iterator<Assessment> iterAss = assessmentListing.iterator(); iterAss.hasNext();)
					        		{
					        			Assessment assessmentOne = iterAss.next();
					        			assessmentOne.setSource((String)node3.getNodeValue());
					        		}
				        		}
			        		}
			        	}
			        	else if(node2.getNodeName().equalsIgnoreCase("sourceID"))
			        	{
			        		NodeList nodeList3 = node2.getChildNodes();
			        		log.info("nodeList3 = " + nodeList3.getLength());
			        		if(nodeList3.getLength()>0)
			        		{
				        		Node node3 = nodeList3.item(0);
				        		log.info("sourceID====>" + (String)node3.getNodeValue());
				        		if(assessmentListing!=null && assessmentListing.size()>0)
				        		{
					        		for(Iterator<Assessment> iterAss = assessmentListing.iterator(); iterAss.hasNext();)
					        		{
					        			Assessment assessmentOne = iterAss.next();
					        			assessmentOne.setSourceID((String)node3.getNodeValue());
					        		}
				        		}
			        		}
			        	}
			        	else if(node2.getNodeName().equalsIgnoreCase("timestamp"))
			        	{
			        		NodeList nodeList3 = node2.getChildNodes();
			        		log.info("nodeList3 = " + nodeList3.getLength());
			        		if(nodeList3.getLength()>0)
			        		{
				        		Node node3 = nodeList3.item(0);
				        		log.info("timestamp====>" + (String)node3.getNodeValue());
			        		}
			        	}
			        	else if(node2.getNodeName().equalsIgnoreCase("tpin_declarantCode"))
			        	{
			        		NodeList nodeList3 = node2.getChildNodes();
			        		log.info("nodeList3 = " + nodeList3.getLength());
			        		if(nodeList3.getLength()>0)
			        		{
				        		Node node3 = nodeList3.item(0);
				        		log.info("tpin_declarantCode====>" + (String)node3.getNodeValue());
				        		if(assessmentListing!=null && assessmentListing.size()>0)
				        		{
				        			Collection<Assessment> asLi = new ArrayList<Assessment>();
					        		for(Iterator<Assessment> iterAss = assessmentListing.iterator(); iterAss.hasNext();)
					        		{
					        			if(declarantFlag!=null && declarantFlag.equals("Y"))
				        				{
					        				if(portalUser.getCompany().getClearingAgent()!=null && portalUser.getCompany().getClearingAgent().equals(Boolean.TRUE))
					        				{
					        					Assessment assessmentOne = iterAss.next();
							        			assessmentOne.setTpinInfo(getTpinInfoByTpId(tpinCode, swpService));
							        			assessmentOne.setDeclarantTpin((String)node3.getNodeValue());
							        			//assessmentOne.setDeclarantTpin((String)node3.getNodeValue());
							        			assessmentOne.setCreateByPortalUserId(portalUser.getId());
							        			asLi.add(assessmentOne);
					        				}else
					        				{
					        					Assessment assessmentOne = iterAss.next();
							        			assessmentOne.setTpinInfo(getTpinInfoByTpId(tpinCode, swpService));
							        			assessmentOne.setDeclarantTpin((String)node3.getNodeValue());
							        			//assessmentOne.setDeclarantTpin((String)node3.getNodeValue());
							        			assessmentOne.setCreateByPortalUserId(portalUser.getId());
							        			asLi.add(assessmentOne);
					        				}
					        				
				        				}else if(declarantFlag!=null && declarantFlag.equals("N"))
				        				{
				        					Assessment assessmentOne = iterAss.next();
						        			assessmentOne.setTpinInfo(getTpinInfoByTpId(tpinCode, swpService));
						        			//assessmentOne.setDeclarantTpin((String)node3.getNodeValue());
						        			//assessmentOne.setDeclarantTpin((String)node3.getNodeValue());
						        			assessmentOne.setCreateByPortalUserId(portalUser.getId());
						        			asLi.add(assessmentOne);
				        				}
					        			
					        		}
					        		assessmentListing = asLi;
				        		}
			        		}
			        		
			        		
			        	}
			        	else if(node2.getNodeName().equalsIgnoreCase("type"))
			        	{
			        		NodeList nodeList3 = node2.getChildNodes();
			        		log.info("nodeList3 = " + nodeList3.getLength());
			        		if(nodeList3.getLength()>0)
			        		{
				        		Node node3 = nodeList3.item(0);
				        		log.info("type====>" + (String)node3.getNodeValue());
			        		}
			        	}
		        	}
		        	
	        	}else
	        	{
	        		

	        		assessmentListing = new ArrayList<Assessment>();
		    		
	        	}
	        	
	        	
	        }else
	        {
	        	
	        }
			
			return assessmentListing;
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
        
	}
	
	
	
	public static Collection<InterestToBePaid> handleGetInterestOfUnPaidAssessments(boolean declarantFlag, String tpin_declarantcode, 
			ActionRequest aReq, ActionResponse aRes, String bankCode, SwpService swpService)
	{
		Logger log = Logger.getLogger(Util.class);
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



	
	public static Ports getPortByPortCode(String portCode, SwpService swpService) {
		// TODO Auto-generated method stub
		Ports rt = null;
		Logger log = Logger.getLogger(Util.class);
		try {
			
				String hql = "select rt from Ports rt where lower(rt.portCode) = lower('" + portCode + "')";
				log.info("Get hql = " + hql);
				rt = (Ports) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	
	
	
	private static TpinInfo getTpinInfoByTpId(String trip, SwpService swpService) {
		// TODO Auto-generated method stub
		TpinInfo rt = null;
		Logger log = Logger.getLogger(Util.class);
		try {
			
				String hql = "select rt from TpinInfo rt where " +
						"lower(rt.tpin) = lower('" + trip + "')";
				
				log.info("Get hql = " + hql);
				rt = (TpinInfo) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}




	public Assessment updateAssessments(Assessment srcAssessment,
			Assessment destAssessment, PortalUser pu) {
		// TODO Auto-generated method stub
		destAssessment.setAmount(srcAssessment.getAmount());
		destAssessment.setAssessmentNumber(srcAssessment.getAssessmentNumber());
		destAssessment.setAssessmentYear(srcAssessment.getAssessmentYear());
		destAssessment.setCountry(srcAssessment.getCountry());
		destAssessment.setDateRegistered(srcAssessment.getDateRegistered());
		destAssessment.setInterest(srcAssessment.getInterest());
		destAssessment.setSource(srcAssessment.getSource());
		destAssessment.setSourceID(srcAssessment.getSourceID());
		return destAssessment;
		
	}




	public String[] getCountrycode() {
		return countryCode;
	}




	public String[] getCountryname() {
		return countryName;
	}




	public int[] getPhonelength() {
		return phoneLength;
	}
	
	
	
	
	public static HashMap<String, Boolean> payDirect(ComminsApplicationState cas, SwpService swpService, Long companyId, 
			Collection<Assessment> allAssessmentList, String[] selectedAssessment1, 
			PortalUser portalUser, String remoteIPAddress, Settings settingsZRAAccount,
			Settings settingsZRASortCode, String platformBank, String bankName, Mailer emailer, String applicationName, 
			String proxyUsername, String proxyPassword, String proxyHost, String proxyPort, String bankPaymentWebServiceUrl)
	{
		Logger log=Logger.getLogger(Util.class);
		HashMap<String, Boolean> arr = new HashMap<String, Boolean>();
		
		if(allAssessmentList!=null && allAssessmentList.size()>0)
		{
			Double amount = 0.0;
			log.info("1");
			for(int c=0; c<selectedAssessment1.length; c++)
			{
			
				log.info("2");
				for(Iterator<Assessment> iterAss = allAssessmentList.iterator(); iterAss.hasNext();)
				{

					Assessment assessment = iterAss.next();
					log.info("3" + " " + selectedAssessment1[c]);
					log.info("assessment.getRegistrationNumber()" + " " + assessment.getRegistrationNumber());
					log.info("assessment.getAssessmentYear()" + " " + assessment.getAssessmentYear());
					log.info("assessment.getPorts().getId()" + " " + assessment.getPorts().getId());
					//RegNo/Year/Port
					if(selectedAssessment1[c]!=null && assessment.getRegistrationNumber().equals(selectedAssessment1[c].split("/")[0].trim()) && 
							assessment.getAssessmentYear().equals(Integer.valueOf(selectedAssessment1[c].split("/")[1].trim())) && 
							assessment.getPorts().getId().equals(Long.valueOf(selectedAssessment1[c].split("/")[2].trim())))
					{
						log.info("4");
						log.info("5");
						Assessment assessmentInDb = getAssessmentByRegNo(swpService, assessment.getRegistrationNumber(), companyId);
						if(assessmentInDb!=null)
						{
							log.info("5>>>>Assessment is not null");
							if(assessmentInDb.getTpinInfo().getCompany().getId().equals(companyId))
							{
								log.info("5>>>>Update the assessment");
								assessment = new Util().updateAssessments(assessment, assessmentInDb, portalUser);
								Boolean xk = assessment.getMiscAvailable();
								if(xk==null)
									assessment.setMiscAvailable(Boolean.FALSE);
								
								swpService.updateRecord(assessment);
								handleAudit(swpService, "UPDATE ASSESSMENT", "UPDATE ASSESSMENT WITH ID " + assessment.getId(), 
										new Timestamp((new Date()).getTime()), remoteIPAddress, portalUser.getUserId());
								
							}else
							{
								log.info("6>>>>Assessment is null");
								Boolean xk = assessment.getMiscAvailable();
								if(xk==null)
									assessment.setMiscAvailable(Boolean.FALSE);
								
								assessment = (Assessment)swpService.createNewRecord(assessment);
								log.info("6>>>>Assessment ===" + assessment.getId());
								handleAudit(swpService, "CREATE ASSESSMENT", "UPDATE ASSESSMENT WITH ID " + assessment.getId(), 
										new Timestamp((new Date()).getTime()), remoteIPAddress, portalUser.getUserId());
							}
						}else
						{
							log.info("61>>>>Assessment is null");
							Boolean xk = assessment.getMiscAvailable();
							if(xk==null)
								assessment.setMiscAvailable(Boolean.FALSE);
							
							assessment = (Assessment)swpService.createNewRecord(assessment);
							log.info("61>>>>Assessment ===" + assessment.getId());
							handleAudit(swpService, "CREATE ASSESSMENT", "UPDATE ASSESSMENT WITH ID " + assessment.getId(), 
									new Timestamp((new Date()).getTime()), remoteIPAddress, portalUser.getUserId());
						}
						
						
						log.info("Proceed to Make the payment");
						List<String> pyHist = handleCorePayments(cas, assessment, portalUser.getCompany().getAccountNumber(), settingsZRAAccount, 
								settingsZRASortCode, platformBank, swpService, remoteIPAddress, portalUser, 
								bankName, emailer, applicationName, proxyUsername, proxyPassword, proxyHost, proxyPort, bankPaymentWebServiceUrl
								);
						log.info("------------------------------");
						log.info(Arrays.toString(pyHist.toArray()));
						for(Iterator<String> iterator = pyHist.iterator(); iterator.hasNext();)
						{
							String enjoy = iterator.next();
							arr.put(enjoy, pyHist!=null && pyHist.size()>0 ? true : false);
						}
						
					}
				}
				
			}
		}
		return arr;
	}
	
	
	
	
	
	
	private static List<String> handleCorePayments(ComminsApplicationState cas, Assessment assessment, String srcAccountNumber, 
			Settings settingsZRABankAccountNumber, Settings settingsZRABankAccountSortCode, 
			String platformBank, SwpService swpService, String remoteIPAddress, PortalUser portalUser, 
			String bankName, Mailer emailer, 
			String applicationName, 
			String proxyUsername, String proxyPassword, String proxyHost, String proxyPort, String bankPaymentWebServiceUrl) {
		//Register the amount to be paid in payment history - CORE PAYMENT.
		//Do a breakdown of the amount into the paymenthistory breakdown table
		//
		//Register the amount to be paid in payment history - INTEREST PAYMENT
		//Do a breakdown of the amount into the paymenthistory breakdown table
		//
		//Register the amount to be paid in payment history - MISC PAYMENT
		//Do a brakdown of the amount into the paymenthistory breakdown table
		
		Logger log = Logger.getLogger(Util.class);
		List<String> payments = new ArrayList<String>();
		boolean makeCorePayment = false;
					
		log.info(10);
		
		
		ArrayList<PaymentHistory> paymentHistoryList = null;

		if(settingsZRABankAccountNumber!=null && settingsZRABankAccountNumber.getValue().length()>0 && 
				settingsZRABankAccountSortCode!=null && settingsZRABankAccountSortCode.getValue().length()>0)
		{
			log.info(11);
			DateFormat df1 = new SimpleDateFormat( "SssmmHHyyMMdd");
			
			DateFormat df2 = new SimpleDateFormat( "yyyyMMdd");
			DateFormat df3 = new SimpleDateFormat( "Sssmm");
	        DateFormat df = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss:S");
	        String currentTimeStamp1 = df1.format(new Date());
	        String currentTimeStamp2 = df2.format(new Date());
	        String currentTimeStamp3 = df3.format(new Date());
	        String currentTimeStamp = df.format(new Date());
	        
        	paymentHistoryList = new ArrayList<PaymentHistory>();
	        String serialNo = currentTimeStamp1 + "" + assessment.getRegistrationNumber();

			//PAY MISC FIRST
	        /*if(assessment.getMiscAvailable().equals(Boolean.TRUE) && assessment.getMiscToBePaid()!=null)
	        {
	        	
	        	log.info(12);
	        	currentTimeStamp1 = df1.format(new Date());
		        String uniqId = "SPI" +  currentTimeStamp1;
		        
	        	PaymentHistory paymentHistory = new PaymentHistory();
	        	paymentHistory.setAssessment(assessment);
	        	paymentHistory.setPayableAmount(assessment.getMiscToBePaid().getAmountPayable());
		        paymentHistory.setCoreAccountNumber(srcAccountNumber);
		        paymentHistory.setSourceAccountNumber(srcAccountNumber);
		        paymentHistory.setSourceSortCode(settingsZRABankAccountSortCode.getValue());
		        paymentHistory.setCurrency(ProbaseConstants.CURRENCY);
		        paymentHistory.setDescription("Payment for Tax Assessment MISC. MISC Payment because it exists");
		        try {
					paymentHistory.setEntryDate(df2.parse(currentTimeStamp2));
				} catch (java.text.ParseException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
		        paymentHistory.setExchangeRate(ProbaseConstants.EXCHANGE_RATE);
		        paymentHistory.setPaymentType(PaymentTypeConstants.PAYMENTTYPE_MISC_PAYMENT);
		        paymentHistory.setProbaseTransactionSerialNo(serialNo);
		        paymentHistory.setReceipientAccountNumber(settingsZRABankAccountNumber.getValue());
		        paymentHistory.setReceipientCoreAccountNumber(settingsZRABankAccountNumber.getValue());
		        paymentHistory.setReceipientSortCode(settingsZRABankAccountSortCode.getValue());
		        paymentHistory.setRequestMessageId(uniqId);
		        try {
					paymentHistory.setRequestTimestamp(new Timestamp(df.parse(currentTimeStamp).getTime()));
				} catch (java.text.ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        paymentHistory.setTransactionReferenceId(null);
		        try {
					paymentHistory.setValueDate(df2.parse(currentTimeStamp2));
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        paymentHistory.setStatus(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_PENDING);
		        paymentHistory.setPortalUser(portalUser);
		        paymentHistory.setDateofTransaction(new Timestamp((new Date()).getTime()));
		        paymentHistory = (PaymentHistory)swpService.createNewRecord(paymentHistory);
	        	paymentHistoryList.add(paymentHistory);
	        	
	        	
	        	
	        	//SAVE BREAKOW FOR INTEREST
	        	PaymentBreakDownHistory paymentBreakDownHistory = new PaymentBreakDownHistory();
	        	TaxType taxType = getTaxTypeByTaxCode(swpService, "MISC");
	        	paymentBreakDownHistory.setTaxType(taxType);
	        	paymentBreakDownHistory.setAmount(assessment.getInterestAmount());
	        	paymentBreakDownHistory.setStatus(PaymentBreakDownHistoryConstants.PAYMENT_BREAKDOWN_STATUS_PENDING);
	        	paymentBreakDownHistory.setPaymentHistory(paymentHistory);
	        	paymentBreakDownHistory = (PaymentBreakDownHistory)swpService.createNewRecord(paymentBreakDownHistory);
	        }*/

	        //PAY INTEREST FIRST
	        if(assessment.getInterest() && assessment.getInterestAmount()!=null)
	        {
	        	log.info(12);
		        String uniqId = "SPI" +  currentTimeStamp1;
	        	PaymentHistory paymentHistory = new PaymentHistory();
	        	paymentHistory.setAssessment(assessment);
	        	paymentHistory.setPayableAmount(assessment.getInterestAmount());
		        paymentHistory.setCoreAccountNumber(srcAccountNumber);
		        paymentHistory.setSourceAccountNumber(srcAccountNumber);
		        paymentHistory.setSourceSortCode(settingsZRABankAccountSortCode.getValue());
		        paymentHistory.setCurrency(ProbaseConstants.CURRENCY);
		        paymentHistory.setDescription("Payment for Tax Assessment Interest. Interest Payment because it exists");
		        try {
					paymentHistory.setEntryDate(df2.parse(currentTimeStamp2));
				} catch (java.text.ParseException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
		        paymentHistory.setExchangeRate(ProbaseConstants.EXCHANGE_RATE);
		        paymentHistory.setPaymentType(PaymentTypeConstants.PAYMENTTYPE_INTEREST_PAYMENT);
		        paymentHistory.setProbaseTransactionSerialNo(serialNo);
		        paymentHistory.setReceipientAccountNumber(settingsZRABankAccountNumber.getValue());
		        paymentHistory.setReceipientCoreAccountNumber(settingsZRABankAccountNumber.getValue());
		        paymentHistory.setReceipientSortCode(settingsZRABankAccountSortCode.getValue());
		        paymentHistory.setRequestMessageId(uniqId);
		        try {
					paymentHistory.setRequestTimestamp(new Timestamp(df.parse(currentTimeStamp).getTime()));
				} catch (java.text.ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        paymentHistory.setTransactionReferenceId(null);
		        try {
					paymentHistory.setValueDate(df2.parse(currentTimeStamp2));
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        paymentHistory.setStatus(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_PENDING);
		        paymentHistory.setPortalUser(portalUser);
		        paymentHistory.setDateofTransaction(new Timestamp((new Date()).getTime()));
		        paymentHistory = (PaymentHistory)swpService.createNewRecord(paymentHistory);
	        	paymentHistoryList.add(paymentHistory);
	        	
	        	
	        	
	        	//SAVE BREAKOW FOR INTEREST
	        	if(assessment.getInterest()!=null && assessment.getInterest().equals(Boolean.TRUE) && assessment.getInterestAmount()>0.00)
	        	{
		        	PaymentBreakDownHistory paymentBreakDownHistory = new PaymentBreakDownHistory();
		        	TaxType taxType = getTaxTypeByTaxCode(swpService, "INT");
		        	paymentBreakDownHistory.setTaxType(taxType);
		        	paymentBreakDownHistory.setAmount(assessment.getInterestAmount());
		        	paymentBreakDownHistory.setStatus(PaymentBreakDownHistoryConstants.PAYMENT_BREAKDOWN_STATUS_PENDING);
		        	paymentBreakDownHistory.setPaymentHistory(paymentHistory);
		        	paymentBreakDownHistory = (PaymentBreakDownHistory)swpService.createNewRecord(paymentBreakDownHistory);
	        	}
	        	
	        }
			 
	        log.info(13);
			        
	        currentTimeStamp1 = df1.format(new Date());
	        String uniqId = "SPI" +  currentTimeStamp1;
	        PaymentHistory paymentHistory = new PaymentHistory();
        	//paymentHistory.setPayableAmount(assessment.getAmount() - assessment.getInterestAmount());
	        paymentHistory.setPayableAmount(assessment.getAmount());
        	paymentHistory.setAssessment(assessment);
	        paymentHistory.setCoreAccountNumber(srcAccountNumber);
	        paymentHistory.setSourceAccountNumber(srcAccountNumber);
	        paymentHistory.setSourceSortCode(settingsZRABankAccountSortCode.getValue());
	        paymentHistory.setCurrency(ProbaseConstants.CURRENCY);
	        paymentHistory.setDescription("Payment for Core Taxs Tax Assessment Amount");
	        try {
				paymentHistory.setEntryDate(df2.parse(currentTimeStamp2));
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        paymentHistory.setExchangeRate(ProbaseConstants.EXCHANGE_RATE);
	        paymentHistory.setPaymentType(PaymentTypeConstants.PAYMENTTYPE_TAXFEE_PAYMENT);
	        paymentHistory.setProbaseTransactionSerialNo(serialNo);
	        paymentHistory.setReceipientAccountNumber(settingsZRABankAccountNumber.getValue());
	        paymentHistory.setReceipientCoreAccountNumber(settingsZRABankAccountNumber.getValue());
	        paymentHistory.setReceipientSortCode(settingsZRABankAccountSortCode.getValue());
	        paymentHistory.setRequestMessageId(uniqId);
	        try {
				paymentHistory.setRequestTimestamp(new Timestamp(df.parse(currentTimeStamp).getTime()));
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        paymentHistory.setTransactionReferenceId(null);
	        try {
				paymentHistory.setValueDate(df2.parse(currentTimeStamp2));
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        paymentHistory.setStatus(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_PENDING);
	        paymentHistory.setPortalUser(portalUser);
	        paymentHistory.setDateofTransaction(new Timestamp((new Date()).getTime()));
	        paymentHistory = (PaymentHistory)swpService.createNewRecord(paymentHistory);
        	paymentHistoryList.add(paymentHistory);
	        
        	log.info(14);
        	
        	
        	
        	/*****do breakdown of the interest payment***/
        	
        	
        	TaxBreakDownResponse tbdR = null;
    		ArrayList<TaxBreakDownResponse> taxBreakDownResponse=null;
        	
//        	if(cas.getDemoMode().equals(Boolean.FALSE))
//        	{
        		log.info(15);
        		taxBreakDownResponse = new Util().getTaxBreakDown( 
    				platformBank, 
    				//assessment.getPorts().getPortCode(),
    				assessment.getPorts().getPortCode(),
    				assessment.getAssessmentYear(),
    				"C",
    				assessment.getRegistrationNumber());
//        	}else
//        	{
//        		log.info(16);
//        		taxBreakDownResponse = cas.getTaxBreakDown1(portalUser, assessment);
//        	}
    		
    		List<HashMap> allList = new ArrayList<HashMap>();
    		if(taxBreakDownResponse!=null)
    		{
    			if(paymentHistoryList!=null && paymentHistoryList.size()>0)
    	        {

            		
    	        	
    	        	
    	        	Boolean success = true;
    	        	for(Iterator<PaymentHistory> iter = paymentHistoryList.iterator(); iter.hasNext();)
    	        	{
    	        		PaymentHistory paymentHistory1 = iter.next();
    	        		currentTimeStamp1 = df1.format(new Date());
    	    	        currentTimeStamp2 = df2.format(new Date());
    	    	        currentTimeStamp3 = df3.format(new Date());
    	    	        currentTimeStamp = df.format(new Date());
    	    	        log.info("testing payments for ---" + paymentHistory1.getPaymentType().getValue());
    	    	        
    	        		uniqId = paymentHistory1.getRequestMessageId();
    	        		
    	        		FundsTransferResponse ftr = processPaymentStep1(cas, platformBank, settingsZRABankAccountSortCode, settingsZRABankAccountNumber, 
                				Double.toString(paymentHistory1.getPayableAmount()), portalUser.getCompany().getAccountNumber(), 
                				portalUser, portalUser.getCompany().getBankBranches().getBankCode(), "1", uniqId,
        					settingsZRABankAccountNumber.getValue(), settingsZRABankAccountSortCode.getValue(), 
        					currentTimeStamp, currentTimeStamp2, 
        					currentTimeStamp2, currentTimeStamp3, "00-00-00", currentTimeStamp1, paymentHistory1.getAssessment(),
        					paymentHistory1.getPaymentType(), swpService, 
        					proxyUsername, proxyPassword, proxyHost, proxyPort, bankPaymentWebServiceUrl, 
        					(paymentHistory1.getAssessment()!=null ? (paymentHistory1.getAssessment().getPorts().getPortCode() + "/" + 
        							paymentHistory1.getAssessment().getAssessmentNumber() + "/" + paymentHistory1.getAssessment().getAssessmentYear()
        							+ (paymentHistory1.getPaymentType().equals(PaymentTypeConstants.PAYMENTTYPE_INTEREST_PAYMENT) ? "/I" : 
        								(paymentHistory1.getPaymentType().equals(PaymentTypeConstants.PAYMENTTYPE_TAXFEE_PAYMENT) ? "/P" : ""))) : 
        							(paymentHistory1.getDomTax()!=null ? paymentHistory1.getRequestMessageId() : ""))
        								
        					);
    	        		
    	        		if(ftr!=null && ftr.getStatus()!=null && ftr.getStatus().equals(Boolean.TRUE))
    	        		{
    	        			log.info("FTR --->" + ftr.getStatus() + " && ftr ==" + ftr.getAccountNumber());
    	        			log.info("testing payments contonues for  ---" + paymentHistory1.getPaymentType().getValue());
//    	        			assessment.setPaidFor(true);
//	            			swpService.updateRecord(assessment);
    	        			log.info("==================");
    	        			log.info("Select rt from Balance rt where rt.company.id = " + portalUser.getCompany().getId());
    	        			Balance bal = (Balance)swpService.getUniqueRecordByHQL("Select rt from Balance rt where rt.company.id = " + portalUser.getCompany().getId());
    	        			log.info(">>>>" + bal.getAmount() + " && " + paymentHistory.getPayableAmount() + " == " + (bal.getAmount() - paymentHistory.getPayableAmount()));
    	        			bal.setAmount(bal.getAmount() - paymentHistory1.getPayableAmount());
    	        			swpService.updateRecord(bal);
    	        			cas.setBalance(bal);
    	        			paymentHistory1.setStatus(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_APPROVED);
    	        			if(ComminsApplicationState.BANC_ABC==1)
    	        				paymentHistory1.setProbaseTransactionSerialNo(ftr.getResMessageId());
    	        			
    	        			swpService.updateRecord(paymentHistory1);
    	        			
    	        			
    	        			log.info(17);
    	        			if(tbdR!=null)
    	        			{
    	        				taxBreakDownResponse.add(tbdR);
    	        			}
    	        			HashMap<String, ArrayList<TaxBreakDownResponse>> newHashList = new HashMap<String, ArrayList<TaxBreakDownResponse>>();
    	        			newHashList.put(assessment.getRegistrationNumber(), taxBreakDownResponse);
    	        			allList.add(newHashList);
    	        			log.info("allList = " + allList.size() + " && NnewHashList = " + newHashList.size());
    	        			
    	        			for(Iterator<HashMap> iterHM = allList.iterator(); iterHM.hasNext();)
    	            		{
    	            			HashMap<String, ArrayList<TaxBreakDownResponse>> hashMap = iterHM.next();
    	            			for (String key : hashMap.keySet()) 
    	            			{
    	            				ArrayList<TaxBreakDownResponse> tbdRList = hashMap.get(key);
    	            				if(tbdRList!=null)
    	            				{
    	            					for(Iterator<TaxBreakDownResponse> itertbdr = tbdRList.iterator(); itertbdr.hasNext();)
    	            					{
    	            						TaxBreakDownResponse tbdr = itertbdr.next();
    	            						Collection<TaxDetails> tdetailsList = tbdr.getTaxDetailListing();
    	            	
    	            						for(Iterator<TaxDetails> itertd = tdetailsList.iterator(); itertd.hasNext();)
    	            						{
    	            							TaxDetails tdetail = itertd.next();
    	            							PaymentBreakDownHistory paymentBreakDownHistory = new PaymentBreakDownHistory();
    	            							log.info("tdetail.getTaxCode() ==" + tdetail.getTaxCode()==null ? "null" : tdetail.getTaxCode());
    	            				        	TaxType taxType = getTaxTypeByTaxCode(swpService, tdetail.getTaxCode());
    	            				        	paymentBreakDownHistory.setTaxType(taxType);
    	            				        	paymentBreakDownHistory.setAmount(tdetail.getAmountToBePaid());
    	            				        	paymentBreakDownHistory.setStatus(PaymentBreakDownHistoryConstants.PAYMENT_BREAKDOWN_STATUS_PENDING);
    	            					        paymentBreakDownHistory.setPaymentHistory(paymentHistory);
    	            					        paymentBreakDownHistory.setTransactionNumber(
    	            					        		RandomStringUtils.random(8, true, true).toUpperCase());
    	            				        	paymentBreakDownHistory = (PaymentBreakDownHistory)swpService.createNewRecord(paymentBreakDownHistory);
    	            							
    	            						}
    	            					}
    	            				}
    	            			}
    	            		}
    	        			
    	        			
    	        			Object object = processPaymentStep2(cas, platformBank, settingsZRABankAccountSortCode, settingsZRABankAccountNumber, 
                    				Double.toString(paymentHistory1.getPayableAmount()), portalUser.getCompany().getAccountNumber(), 
                    				portalUser, portalUser.getCompany().getBankBranches().getBankCode(), "1", uniqId,
            					settingsZRABankAccountNumber.getValue(), settingsZRABankAccountSortCode.getValue(), 
            					currentTimeStamp, currentTimeStamp2, 
            					currentTimeStamp2, currentTimeStamp3, "00-00-00", currentTimeStamp1, paymentHistory1.getAssessment(),
            					paymentHistory1.getPaymentType(), swpService
            					);
    	        			
    	        			if(paymentHistory1.getPaymentType().equals(PaymentTypeConstants.PAYMENTTYPE_INTEREST_PAYMENT))
            	        	{
    	        				InterestPaymentResult interestPaymentResultList = object!=null ? ((InterestPaymentResult)object) : null;
    	        				
            	        		if(interestPaymentResultList!=null && interestPaymentResultList.getErrorCode().equals("0"))
            	        		{
            	        				HashMap<String, Double> hashMapFee = new HashMap<String, Double>();
            	            			String receiptNumber = paymentHistory1.getAssessment().getPorts().getPortCode() + 
            	            					"/" + paymentHistory1.getAssessment().getRegistrationNumber() + 
            	            					"/" + paymentHistory1.getAssessment().getAssessmentYear() + 
            	            					"/" + interestPaymentResultList.getReceiptNumber();
                	                	paymentHistory1.setTransactionReferenceId(receiptNumber);
            	            			paymentHistory1.setReceiptNumber(interestPaymentResultList.getReceiptNumber());
            	            			paymentHistory1.setStatus(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_ZRA_CONFIRMED);
            	            			paymentHistory1.setDateofTransaction(new Timestamp((new Date()).getTime()));
                	        			if(paymentHistory1.getPaymentType().equals(PaymentTypeConstants.PAYMENTTYPE_INTEREST_PAYMENT))
                	        				hashMapFee.put("Interest on Payment", paymentHistory1.getPayableAmount());
                	        			swpService.updateRecord(paymentHistory1);
                	        			
                	        			
                	        			payments.add(receiptNumber + ":::" + paymentHistory1.getPaymentType().getValue() + ":::" + 
                	        					paymentHistory1.getAssessment().getRegistrationNumber() + ":::" + paymentHistory1.getPayableAmount());
                	        			
            	            			
            	    					
            	    					
            	    					notifyPaymentForAssessmentInterest(portalUser, assessment, emailer, hashMapFee, bankName, applicationName, 
            	    							receiptNumber, "R " + interestPaymentResultList.getReceiptNumber(), 
            	    							paymentHistory1.getPayableAmount());
            	        			

            	                	
        	    					
        	    					
        	            		
            	        		}
    		            		else
    		            		{
//    		            			assessment.setPaidFor(false);
//    		            			swpService.updateRecord(assessment);
    		            			paymentHistory1.setStatus(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_ZRA_CONTROVERSIAL);
    	    	        			success = false;
    	    	        			swpService.updateRecord(paymentHistory1);
    	                			handleAudit(swpService, "Payment for " + paymentHistory1.getPaymentType().getValue(), Long.toString(paymentHistory1.getId()), 
    	                					new Timestamp((new Date()).getTime()), remoteIPAddress, portalUser.getUserId());
    		            		}
            	        	}else if(paymentHistory1.getPaymentType().equals(PaymentTypeConstants.PAYMENTTYPE_TAXFEE_PAYMENT))
            	        	{
            	        		DeclarationPaymentResult interestPaymentResultList = object!=null ? ((DeclarationPaymentResult)object) : null;
            	        		if(interestPaymentResultList!=null && interestPaymentResultList.getErrorCode().equals("0"))
            	        			{
            	        				
                	        			
                	                	HashMap<String, Double> hashMapFee = new HashMap<String, Double>();
//            	            			assessment.setPaidFor(true);
//            	            			swpService.updateRecord(assessment);
            	            			
            	            			String receiptNumber = paymentHistory1.getAssessment().getPorts().getPortCode() + 
            	            					"/" + paymentHistory1.getAssessment().getRegistrationNumber() + 
            	            					"/" + paymentHistory1.getAssessment().getAssessmentYear() + 
            	            					"/" + interestPaymentResultList.getReceiptNumber();
            	            			paymentHistory1.setTransactionReferenceId(receiptNumber);
            	            			paymentHistory1.setReceiptNumber(interestPaymentResultList.getReceiptNumber());
            	            			paymentHistory1.setStatus(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_ZRA_CONFIRMED);
            	            			paymentHistory1.setDateofTransaction(new Timestamp((new Date()).getTime()));
            	            			swpService.updateRecord(paymentHistory1);
                	        			
            	            			payments.add(receiptNumber + ":::" + paymentHistory1.getPaymentType().getValue() + ":::" + 
                	        					paymentHistory1.getAssessment().getRegistrationNumber() + ":::" + paymentHistory1.getPayableAmount());
                	        			if(paymentHistory1.getPaymentType().equals(PaymentTypeConstants.PAYMENTTYPE_TAXFEE_PAYMENT))
                	        				hashMapFee.put("Assessment Fee", paymentHistory1.getPayableAmount());
                	        			
                	        			
            	            			
            	    					notifyPaymentForAssessmentCore(portalUser, assessment, emailer, hashMapFee, bankName, applicationName 
            	    							, receiptNumber, "R " + interestPaymentResultList.getReceiptNumber(), 
            	    							paymentHistory1.getPayableAmount());
            	        			
            	        		}
    		            		else
    		            		{
    		            			//assessment.setPaidFor(false);
    		            			//swpService.updateRecord(assessment);
    		            			paymentHistory1.setStatus(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_ZRA_CONTROVERSIAL);
    	    	        			success = false;
    	    	        			swpService.updateRecord(paymentHistory1);
    	                			handleAudit(swpService, "Payment for " + paymentHistory1.getPaymentType().getValue(), Long.toString(paymentHistory1.getId()), 
    	                					new Timestamp((new Date()).getTime()), remoteIPAddress, portalUser.getUserId());
    		            		}
            	        	}
    	        		}else
    	        		{
//    	        			assessment.setPaidFor(false);
//	            			swpService.updateRecord(assessment);
    	        			paymentHistory1.setStatus(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_DECLINED);
    	        			swpService.updateRecord(paymentHistory1);
    	        		}
    	        			
    	        		
        	        	
        	        	
    	        	}
    	        }
    	        else
    	        {
    	        	
    	        }
    			
    		}else
    		{
    			log.info("break the loop");
    		}
    		
    		//SAVE BREAKDOWN OF TAXES
    		
		}else
		{
		}
	
		return payments;
	}
	
	
	
	
	
	
	
	private static FundsTransferResponse processPaymentStep1(ComminsApplicationState cas, 
			String platformBank, Settings settingsZRASortCode, Settings settingsZRAAccount, 
			String amount, String srcAccountNumber, PortalUser portalUser, 
			String srcAccountSortCode, String exchangeRate, String uniqueId, String recAccountNumber, 
			String recAccountSortCode, String reqTimeStamp, String entryDate, String valueDate, 
			String reference, String narrative1, String uniqueId2, Assessment assessment, 
			PaymentTypeConstants paymentTypeConstants, SwpService swpService, 
			String proxyUsername, String proxyPassword, String proxyHost, String proxyPort, 
			String bankPaymentWebServiceUrl, String customsRefId) {
		
		
		DateFormat df = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss:S");
        DateFormat df1 = new SimpleDateFormat( "SssmmHHddMMyy");
        DateFormat df2 = new SimpleDateFormat( "yyyyMMdd");
        String currentTimeStamp = df.format(new Date());
        String currentTimeStamp1 = df1.format(new Date());
        String currentTimeStamp2 = df2.format(new Date());
        
        FundsTransferResponse ftr= null;
        
		try {
			Logger logger = Logger.getLogger(Util.class);
//			logger.info("cas.getDemoModePay() = " + cas.getDemoModePay());
//			if(cas.getDemoModePay().equals(Boolean.FALSE))
//			{
//				logger.info("cas.getDemoModePay() = false");
			
				if(ComminsApplicationState.STB==1)
				{
					ftr = Util.doFundsTransfer("SmartPay", "SMARTPAY:ZM:REVENUE:ENTRY:REQUEST", 
						uniqueId2, currentTimeStamp, uniqueId, 
						"ZMW", "1095", portalUser.getCompany().getAccountNumber(), 
						portalUser.getCompany().getBankBranches().getBankCode(), amount, currentTimeStamp2, 
						reference, narrative1, settingsZRASortCode.getValue(), settingsZRAAccount.getValue(), 
						proxyUsername, proxyPassword, proxyHost, proxyPort, 
						bankPaymentWebServiceUrl, customsRefId);
				}if(ComminsApplicationState.BANC_ABC==1)
				{
					ftr = Util.doFundsTransfer("SmartPay", "SMARTPAY:ZM:REVENUE:ENTRY:REQUEST", 
						uniqueId2, currentTimeStamp, uniqueId, 
						"ZMW", "1095", portalUser.getCompany().getAccountNumber(), 
						portalUser.getCompany().getBankBranches().getBankCode(), amount, currentTimeStamp2, 
						reference, narrative1, settingsZRASortCode.getValue(), settingsZRAAccount.getValue(), 
						proxyUsername, proxyPassword, proxyHost, proxyPort, 
						bankPaymentWebServiceUrl, customsRefId);
				}
//				//ftr=cas.getFundsTransferResponse(portalUser);
//			}
//			else
//			{
				logger.info("cas.getDemoModePay() = true");
//				ftr=cas.getFundsTransferResponse(portalUser);
//			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ftr;
	}
	
	
	private static Object processPaymentStep2(ComminsApplicationState cas, String platformBank, Settings settingsZRASortCode, Settings settingsZRAAccount, 
			String amount, String srcAccountNumber, PortalUser portalUser, 
			String srcAccountSortCode, String exchangeRate, String uniqueId, String recAccountNumber, 
			String recAccountSortCode, String reqTimeStamp, String entryDate, String valueDate, 
			String reference, String narrative1, String uniqueId2, Assessment assessment, 
			PaymentTypeConstants paymentTypeConstants, SwpService swpService) {
		// TODO Auto-generated method stub
		System.out.println(">>>>>>>>>>>1");
		
		/*if(paymentTypeConstants.equals(PaymentTypeConstants.PAYMENTTYPE_MISC_PAYMENT))
		{
			System.out.println(">>>>>>>>>>>2");
				MiscPaymentResult miscPaymentResult= null;
				try {
					System.out.println(">>>>>>>>>>>3");
					Date date = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					Timestamp ts = new Timestamp(date.getTime());
					date = new Date(ts.getTime());
					String dd = sdf.format(date);
					MiscPaymentInfo miscPaymentInfo = new MiscPaymentInfo();
					miscPaymentInfo.setBankCode(platformBank);
					miscPaymentInfo.setCompanyCode(assessment.getClientTpin());
					miscPaymentInfo.setDeclarantCode(assessment.getDeclarantTpin());
					miscPaymentInfo.setMiscTransactionToBePaid();
					miscPaymentInfo.setOfficeCode(assessment.getPorts().getPortCode());
					miscPaymentInfo.setTransactionCode(assessment.getMiscToBePaid().getTransactionCode);
					miscPaymentInfo.setTransactionDescription(assessment.getMiscToBePaid().getTransactionDescription());
					
					MiscTransactionToBePaid mttbp = new MiscTransactionToBePaid();
					mttbp.setAmountToBePaid(assessment.getMiscToBePaid().getAmountPayable());
					mttbp.setCompanyCode(assessment.getMiscToBePaid().getTpin());
					mttbp.setReferenceNumber(assessment.getMiscToBePaid().getRegistrationNumber());
					mttbp.setReferenceOffice(assessment.getMiscToBePaid().getPort());
					mttbp.setReferenceSerial(get);
					mttbp.setReferenceText(referenceText);
					mttbp.setReferenceYear(referenceYear);
					mttbp.setTransactionCode(transactionCode);
					
					interestToBePayList.add(itbp);
					
					if(cas.getDemoMode().equals(Boolean.FALSE))
					{	
						System.out.println(">>>>>>>>>>>9");
						interestPaymentResult = Util.doDeclareZRAInterestPayment(platformBank, 
							assessment.getPorts().getPortCode(), 
							assessment.getDeclarantTpin(), assessment.getClientTpin(), interestToBePayList, "?", "?", amount, 
							dd, swpService);
						System.out.println(">>>>>>>>>>>" + interestPaymentResult!=null ?  "==null" : interestPaymentResult.getErrorCode());
					}else
					{
						System.out.println(">>>>>>>>>>>4");
						interestPaymentResult = cas.getInterestPaymentResult();
					}
					
					
					
					if(interestPaymentResult!=null)
						return interestPaymentResult;
					else
						return null;
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}

		}
		else */
		if(paymentTypeConstants.equals(PaymentTypeConstants.PAYMENTTYPE_INTEREST_PAYMENT))
		{
			System.out.println(">>>>>>>>>>>2");
				InterestPaymentResult interestPaymentResult= null;
				try {
					System.out.println(">>>>>>>>>>>3");
					Date date = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					Timestamp ts = new Timestamp(date.getTime());
					date = new Date(ts.getTime());
					String dd = sdf.format(date);
					Collection<InterestToBePay> interestToBePayList = new ArrayList<InterestToBePay>();
					InterestToBePay itbp = new InterestToBePay();
					itbp.setAmountTobePaid(amount);
					itbp.setAssessmentNumber(assessment.getAssessmentNumber());
					itbp.setAssessmentSerial(assessment.getRegistrationSerial());
					itbp.setReferenceNumber(assessment.getRegistrationNumber());
					itbp.setReferenceSerial(assessment.getRegistrationSerial());
					itbp.setReferenceYear(Integer.toString(assessment.getAssessmentYear()));
					itbp.setReferenceText("?");
					itbp.setReferenceOffice(assessment.getPorts().getPortCode());
					itbp.setTransactionCode("?");
					interestToBePayList.add(itbp);
					
//					if(cas.getDemoMode().equals(Boolean.FALSE))
//					{	
						System.out.println(">>>>>>>>>>>9");
						interestPaymentResult = Util.doDeclareZRAInterestPayment(platformBank, 
							assessment.getPorts().getPortCode(), 
							assessment.getDeclarantTpin(), assessment.getClientTpin(), interestToBePayList, "?", "?", amount, 
							dd, swpService);
						System.out.println(">>>>>>>>>>>" + interestPaymentResult!=null ?  "==null" : interestPaymentResult.getErrorCode());
//					}else
//					{
//						System.out.println(">>>>>>>>>>>4");
//						interestPaymentResult = cas.getInterestPaymentResult();
//					}
					
					
					
					if(interestPaymentResult!=null)
						return interestPaymentResult;
					else
						return null;
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}

		}else if(paymentTypeConstants.equals(PaymentTypeConstants.PAYMENTTYPE_TAXFEE_PAYMENT))
		{
			System.out.println(">>>>>>>>>>>91");
				DeclarationPaymentResult declareZRAPaymentResponse= null;
				try {
					Date date = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					Timestamp ts = new Timestamp(date.getTime());
					date = new Date(ts.getTime());
					String dd = sdf.format(date);
					Collection<DeclarationsToBePaid> declarationsToBePaidList = new ArrayList<DeclarationsToBePaid>();
					DeclarationsToBePaid dtbp = new DeclarationsToBePaid();
					dtbp.setAmountToBePaid(amount);
					dtbp.setAssessmentNumber(assessment.getAssessmentNumber());
					dtbp.setAssessmentSerial(assessment.getRegistrationSerial());
					dtbp.setRegistrationNumber(assessment.getRegistrationNumber());
					dtbp.setRegistrationSerial(assessment.getRegistrationSerial());
					dtbp.setRegistrationYear(Integer.toString(assessment.getAssessmentYear()));
					declarationsToBePaidList.add(dtbp);
					Logger log=  Logger.getLogger(Util.class);
					
//					if(cas.getDemoMode().equals(Boolean.FALSE))
//					{
						log.info("<<<<<<<<<<<<<<99899" + assessment.getDeclarantTpin());
						declareZRAPaymentResponse = Util.doDeclareZRAPayment(platformBank, assessment.getPorts().getPortCode(), 
							assessment.getDeclarantTpin(), assessment.getClientTpin(), declarationsToBePaidList, "?", "?", amount, 
							dd, swpService);
//					}
//					else
//					{
//						declareZRAPaymentResponse = cas.getDeclareZRAPaymentResposne();
//					}
					if(declareZRAPaymentResponse!=null)
						return declareZRAPaymentResponse;
					else
						return null;
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			
		}else
		{
			return null;
		}
		
		
	}
	
	
	
	
	public static TaxType getTaxTypeByTaxCode(SwpService swpService, String taxCode) {
		// TODO Auto-generated method stub
		TaxType rt = null;
		Logger log = Logger.getLogger(Util.class);
		try {
			
				String hql = "select rt from TaxType rt where " +
						"rt.taxCode = '" + taxCode + "'";
				
				log.info("Get hql = " + hql);
				rt = (TaxType) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	
	private static void handleAudit(SwpService swpService, String action, String activity, Timestamp timestamp, String ipAddress, Long userId) {
		// TODO Auto-generated method stub
		AuditTrail ad = new AuditTrail();
		try
		{
			ad.setAction(action);
			ad.setActivity(activity);
			ad.setDate(timestamp);
			ad.setIpAddress(ipAddress);
			ad.setUserId(Long.toString(userId));
			swpService.createNewRecord(ad);
		}catch(NullPointerException e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	public static Assessment getAssessmentByRegNo(SwpService swpService, String registrationNumber, Long id) {
		// TODO Auto-generated method stub
		Assessment rt = null;
		Logger log =Logger.getLogger(Util.class);
		try {
			
				String hql = "select rt from Assessment rt where " +
						"rt.registrationNumber = '" + registrationNumber + "' AND rt.tpinInfo.company = " + id;
				
				log.info("Get hql = " + hql);
				rt = (Assessment) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	
	
	public static List<HashMap> taxbreakdown(String[] selectedAssessment, Collection allAssessmentList, String platformBank)
	{
		List<HashMap> allList = new ArrayList<HashMap>();
		Logger log = Logger.getLogger(Util.class);
		for(int c=0; c<selectedAssessment.length; c++)
		{
			log.info("allAssessmentList size = " + allAssessmentList.size());
			for(Iterator<Assessment> iterAss = allAssessmentList.iterator(); iterAss.hasNext();)
			{
				Assessment assessment = iterAss.next();
				log.info("assessment = " + assessment==null ? "N/A" : assessment.getAmount());
				log.info("selectedAssessment[c] = " + selectedAssessment[c]==null ? "N/A" : selectedAssessment[c]);
				log.info("assessment.getRegistrationNumber() = " + assessment.getRegistrationNumber()==null ? "N/A" : assessment.getRegistrationNumber());
				if(assessment.getRegistrationNumber().equals(selectedAssessment[c].split("/")[0].trim()) && 
						assessment.getAssessmentYear().equals(Integer.valueOf(selectedAssessment[c].split("/")[1].trim())) && 
						assessment.getPorts().getId().equals(Long.valueOf(selectedAssessment[c].split("/")[2].trim())))
				{
					TaxBreakDownResponse tbdR = null;
					if(assessment.getInterest())
					{
						//Give the Interest a code and a name
						tbdR = new TaxBreakDownResponse();
						//interestValue = getInterestOnAssessment(assessment.getRegistrationNumber());
						//interestValue = 20.00;
						tbdR.setProductCode("INT");
						tbdR.setProductName("Interest");
						List<TaxDetails> taxDetailListing = new ArrayList<TaxDetails>();
						TaxDetails td = new TaxDetails();
						td.setAmountToBePaid(assessment.getInterestAmount());
						td.setTaxCode("INT");
						taxDetailListing.add(td);
						tbdR.setTaxDetailListing(taxDetailListing);
											
						
					}
					ArrayList<TaxBreakDownResponse> taxBreakDownResponse = new Util().getTaxBreakDown( 
							platformBank, 
							//assessment.getPorts().getPortCode(),
							assessment.getPorts().getPortCode(),
							assessment.getAssessmentYear(),
							"C",
							assessment.getRegistrationNumber());
					
					
					if(taxBreakDownResponse!=null)
					{
						if(tbdR!=null)
						{
							taxBreakDownResponse.add(tbdR);
						}
						HashMap<String, ArrayList<TaxBreakDownResponse>> newHashList = new HashMap<String, ArrayList<TaxBreakDownResponse>>();
						newHashList.put(assessment.getRegistrationNumber(), taxBreakDownResponse);
						allList.add(newHashList);
						log.info("allList = " + allList.size() + " && NnewHashList = " + newHashList.size());
					}else
					{
						log.info("break the loop");
					}
				}
			}
		}
		return allList;
	}
	
	public static BalanceInquiry getBalanceInquiry(String reqSourceSystem, String reqMessageType, 
			String type, String accountNumber, String currency) throws MalformedURLException, IOException {
		DateFormat df = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss:S");
        DateFormat df1 = new SimpleDateFormat( "SssmmHHddMMyy");
        String reqTimeStamp = df.format(new Date());
        String reqMessageId = df1.format(new Date());
        String reqTrackingId = "SPI" + reqMessageId;
        Logger log = Logger.getLogger(Util.class);
        
        
        
//        if(proxyUsername!=null && proxyPassword!=null && proxyPort!=null && proxyHost!=null 
//				&& proxyUsername.length()>0 && proxyPassword.length()>0 && proxyPort.length()>0 && proxyHost.length()>0)
//		{
//			java.util.Properties props = null;
//	        props = System.getProperties();
//	        Authenticator.setDefault(new ProxyAuthenticator(proxyUsername, proxyPassword));
//	        System.setProperty("http.proxyHost", proxyHost);
//			System.setProperty("http.proxyPort", proxyPort);
//		}
//        
//        
		//Code to make a webservice HTTP request
		String responseString = "";
		String outputString = "";
		String wsURL = "http://10.236.6.125:6080/africa/services/uat/zm/maxintegrationv1_0";			///MAKE A SETTING IN DB
		
		log.info("We are about to enter  banc abc");
		if(ComminsApplicationState.BANC_ABC==1)
		{
			wsURL = "http://10.103.10.23:5443/ws/BancAbcZmCustomsPayment.webservices.providers.CustomsPayment_WSD/BancAbcZmCustomsPayment_webservices_providers_CustomsPayment_WSD_Port";
			wsURL = "http://10.103.10.23:5555/ws/BancAbcZmCustomsPayment.webservices.providers:CustomsPayment_WSD";
			System.out.println("We are in banc abc");
		}
		log.info("IP is " + wsURL);
		URL url = new URL(wsURL);
		CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
		URLConnection connection = url.openConnection();
		HttpURLConnection httpConn = (HttpURLConnection)connection;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		String xmlInput =
		"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
		"xmlns:max=\"http://standardbank.com/africa/services/MaxIntegrationV1_0\" " +
		"xmlns:max1=\"http://standardbank.com/africa/services/MaxHeaderV1_0\" " +
		"xmlns:sbav=\"http://standardbank.com/africa/services/SbaV1_0\">" +
		"<soapenv:Header/>" +
		"<soapenv:Body>" +
		"<max:ExecuteIntegration>" +
		"<max1:requestHeader>" +
		"<reqSourceSystem>" + reqSourceSystem + "</reqSourceSystem>" +
		"<reqMessageType>" + reqMessageType + "</reqMessageType>" +
		"<reqMessageId>" + reqMessageId + "</reqMessageId>" +
		"<reqTimeStamp>" + reqTimeStamp + "</reqTimeStamp>" +
		"<reqTrackingId>" + reqTrackingId + "</reqTrackingId>" +
		"</max1:requestHeader>" +
		"<sbav:InputData>" +
		"<sbav:InputPayload>" +
		"<balanceRequest>" +
		"<account>" +
		"<type>" + type + "</type>" +
		"<accountNumber>" + accountNumber + "</accountNumber>" +
		"<currency>" + currency + "</currency>" +
		"</account>" +
		"</balanceRequest>" + 
		"</sbav:InputPayload>" +
		"</sbav:InputData>" +
		"</max:ExecuteIntegration>" +
		"</soapenv:Body>" +
		"</soapenv:Envelope>";
		
		if(ComminsApplicationState.BANC_ABC==1)
		{
			xmlInput ="<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ban=\"http://www.bancabc.com/BancAbcZmCustomsPayment.webservices.providers:CustomsPayment_WSD\">" +
				"<soap:Header/>" +
				"<soap:Body>" +
				"<ban:getAvailableBankBalance>" +
				"<getAvailableBankBalanceRequest>" +
				"<accountNumber>" + accountNumber + "</accountNumber>" +
				"<branchCode>ZM1</branchCode>" +
				"</getAvailableBankBalanceRequest>" +
				"</ban:getAvailableBankBalance>" +
				"</soap:Body>" +
				"</soap:Envelope>";
		}
		
		log.info("xmlInput = " + xmlInput);
		 
		byte[] buffer = new byte[xmlInput.length()];
		buffer = xmlInput.getBytes();
		bout.write(buffer);
		byte[] b = bout.toByteArray();
		String SOAPAction =
				"\"BancAbcZmCustomsPayment_webservices_providers_CustomsPayment_WSD_Binder_getAvailableBankBalance\"";
		// Set the appropriate HTTP parameters.
		
		
		if(ComminsApplicationState.BANC_ABC==1)
		{
			// Set the appropriate HTTP parameters.
			SOAPAction =
					"\"BancAbcZmCustomsPayment_webservices_providers_CustomsPayment_WSD_Binder_getAvailableBankBalance\"";
			httpConn.setRequestProperty("Content-Length",
			String.valueOf(b.length));
			//httpConn.setRequestProperty("Content-Type", "application/soap+xml;charset=UTF-8;action=\"BancAbcZmCustomsPayment-webservices_providers_CustomsPayment_WSD_Binder_getAvailableBankBalance\"");
			httpConn.setRequestProperty("Content-Type", "application/soap+xml;charset=UTF-8");
			httpConn.setRequestProperty("SOAPAction", SOAPAction);
			//httpConn.setRequestProperty("Accept-Encoding", "gzip,deflate");
			httpConn.setRequestProperty("Accept", "application/soap+xml");
			httpConn.setRequestProperty("RequestVersion", "HTTP/1.1");
			httpConn.setRequestProperty("Host", "10.103.10.23:5555");
	        httpConn.setRequestProperty("Accept", "*/*");
	        httpConn.setRequestProperty("User-Agent", "Java");
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
		}
		if(ComminsApplicationState.STB==1)
		{
			httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
			httpConn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
			httpConn.setRequestProperty("SOAPAction", SOAPAction);
			httpConn.setRequestProperty("Accept-Encoding", "gzip,deflate");
			httpConn.setRequestProperty("Accept", "text/xml");
			httpConn.setRequestProperty("RequestVersion", "HTTP/1.1");
			httpConn.setRequestProperty("Host", "10.103.10.23:5443");
	        httpConn.setRequestProperty("Accept", "*/*");
	        httpConn.setRequestProperty("User-Agent", "Java");
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
		}
		
		OutputStream out = httpConn.getOutputStream();
		//Write the content of the request to the outputstream of the HTTP Connection.
		out.write(b);
		out.close();
		//Ready with sending the request.
		 
		//Read the response.
		System.out.println("Read Timeout = " + httpConn.getReadTimeout());
		InputStreamReader isr =
		new InputStreamReader(httpConn.getInputStream(), Charset.forName("UTF-8"));
		BufferedReader in = new BufferedReader(isr);
		 
		//Write the SOAP message response to a String.
//		int c = 0;
		while ((responseString = in.readLine()) != null) {
			outputString = outputString + responseString;
//			if(c==1)
//			{
//				outputString = outputString.substring(1);
//			}
		
		}
		log.info("Output String lenght : " + outputString.length());
		log.info("Output stream = " + outputString);

//		GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(outputString.getBytes("UTF-8")));
//        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
//        String outStr = "";
//        outputString = "";
//        String line;
//        while ((line=bf.readLine())!=null) {
//        	outputString += line;
//        }
//        System.out.println("Output String lenght : " + outputString.length());
        //return outStr;
		
		
		log.info("Output stream = " + outputString);
		//Parse the String output to a org.w3c.dom.Document and be able to reach every node with the org.w3c.dom API.
		Document document = parseXmlFile(outputString);
		if(document==null)
		{
			return null;
		}else
		{
			try
			{
				
				NodeList nodeLst_ = document.getElementsByTagName("getAvailableBankBalanceResponse");
				if(nodeLst_!=null)
				{
				
					NodeList nodeLst = document.getElementsByTagName("accountNumber");
					if(nodeLst.getLength()>0)
					{
						String accountNumber1 = nodeLst.item(0).getTextContent();
						System.out.println("accountNumber: " + accountNumber);
						
						NodeList nodeLst1 = document.getElementsByTagName("accountName");
						String currency1 = nodeLst1.item(0).getTextContent();
						System.out.println("accountName: " + currency);
						
						NodeList nodeLst2 = document.getElementsByTagName("availableBalance");
						String availableBalance = nodeLst2.item(0).getTextContent();
						System.out.println("availableBalance: " + availableBalance);
						
						NodeList nodeLst3 = document.getElementsByTagName("accountType");
						String type1 = nodeLst3.item(0).getTextContent();
						System.out.println("accountType: " + type1);
						
						NodeList nodeLst4 = document.getElementsByTagName("accountStatus");
						String status = nodeLst4.item(0).getTextContent();
						System.out.println("accountStatus: " + status);
						 
						//Write the SOAP message formatted to the console.
						String formattedSOAPResponse = formatXML(outputString);
						System.out.println(formattedSOAPResponse);
						BalanceInquiry bi = new BalanceInquiry();
						bi.setAccountNumber(accountNumber1);
						bi.setAvailableBalance(Double.valueOf(availableBalance));
						bi.setCurrency(currency1);
						bi.setStatus(status);
						bi.setType(type1);
						return bi;
					}else
					{
						return null;
					}
				}else
				{
					return null;
				}
			}catch(NullPointerException e)
			{
				e.printStackTrace();
				return null;
			}
		}
	}

	
	
	
	public static FundsTransferResponse doFundsTransfer(String reqSourceSystem, String reqMessageType, 
			String reqMessageId, String reqTimeStamp, 
			String reqTrackingId, String currency, String sourceBranchSortCode, String bankAccountNumber, 
			String coreSourceBankBranchSortCode, String amount, String entryDate, String reference, 
			String narrative1, String destCoreBankBranchSortCode, String destbankAccountNumber, 
			String proxyUsername, String proxyPassword, String proxyHost, String proxyPort, 
			String bankPaymentWebServiceUrl, String customsReference) throws MalformedURLException, IOException {


		if(proxyUsername!=null && proxyPassword!=null && proxyPort!=null && proxyHost!=null 
				&& proxyUsername.length()>0 && proxyPassword.length()>0 && proxyPort.length()>0 && proxyHost.length()>0)
		{
			System.out.println("We are using a proxy so set proxy settings");
			java.util.Properties props = null;
	        props = System.getProperties();
	        Authenticator.setDefault(new ProxyAuthenticator(proxyUsername, proxyPassword));
	        System.setProperty("http.proxyHost", proxyHost);
			System.setProperty("http.proxyPort", proxyPort);
		}
		
		if(bankPaymentWebServiceUrl!=null && bankPaymentWebServiceUrl.length()>0)
		{
			//Code to make a webservice HTTP request
			String responseString = "";
			String outputString = "";
			//String wsURL = "http://10.236.6.125:6080/africa/services/uat/zm/maxintegrationv1_0";			///MAKE A SETTING IN DB
			String wsURL = bankPaymentWebServiceUrl;			///MAKE A SETTING IN DB
			if(ComminsApplicationState.BANC_ABC==1)
			{
				wsURL = "http://10.103.10.23:5555/ws/BancAbcZmCustomsPayment.webservices.providers:CustomsPayment_WSD";
			}
			URL url = new URL(wsURL);
			CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection)connection;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			Double amt = Double.valueOf(amount);
			String xmlInput = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
			"xmlns:max=\"http://standardbank.com/africa/services/MaxIntegrationV1_0\" " +
			"xmlns:max1=\"http://standardbank.com/africa/services/MaxHeaderV1_0\" " +
			"xmlns:sbav=\"http://standardbank.com/africa/services/SbaV1_0\">" + 
				"<soapenv:Header/>" + 
				"<soapenv:Body>" + 
				"<max:ExecuteIntegration>" + 
				"<requestHeader>" + 
				"<reqSourceSystem>" + reqSourceSystem + "</reqSourceSystem>" + 
				"<reqMessageType>" + reqMessageType + "</reqMessageType>" + 
				"<reqMessageId>" + reqMessageId + "</reqMessageId>" + 
				"<reqTimeStamp>" + reqTimeStamp + "</reqTimeStamp>" + 
				"<reqTrackingId>" + reqTrackingId + "</reqTrackingId>" + 
				"</requestHeader>" + 
				"<InputData>" + 
				"<InputPayload>" + 
				"<accountPostingRequest>" + 
				"<postingType>balanced</postingType>" + 
				"<transactionType>Z1</transactionType>" + 
				"<sourceBranchSortCode>" + sourceBranchSortCode + "</sourceBranchSortCode>" + 
				"<posting>" + 
				"<coreBankAccountNumber>" + bankAccountNumber + "</coreBankAccountNumber>" + 
				"<sourceAccountNumber>" + bankAccountNumber + "</sourceAccountNumber>" + 
				"<coreBankBranchSortCode>" + coreSourceBankBranchSortCode + "</coreBankBranchSortCode>" + 
				"<currency>" + currency + "</currency>" + 
				"<localCurrency>" + currency + "</localCurrency>" + 
				"<debitCreditIndicator>d</debitCreditIndicator>" + 
				"<baseEquivalentAmount>" + new Util().roundUpAmount(amt) + "</baseEquivalentAmount>" + 
				"<entryAmount>" + new Util().roundUpAmount(amt) + "</entryAmount>" + 
				"<exchangeRate>1</exchangeRate>" + 
				"<multiplyDivideIndicator>m</multiplyDivideIndicator>" + 
				"<entryType>non-position</entryType>" + 
				"<entryDate>" + entryDate + "</entryDate>" + 
				"<valueDate>" + entryDate + "</valueDate>" + 
				"<sourcePostingReference>" + reference + "</sourcePostingReference>" + 
				"<transactionReference>" + reference + "</transactionReference>" + 
				"<narrative1>" + narrative1 + "</narrative1>" + 
				"<narrative2>" + reference + "</narrative2>" + 
				"<narrative3>" + reference + "</narrative3>" + 
				"<forcePost>N</forcePost>" + 
				"</posting>" + 
				"<posting>" + 
				"<coreBankAccountNumber>" + destbankAccountNumber + "</coreBankAccountNumber>" + 
				"<sourceAccountNumber>" + destbankAccountNumber + "</sourceAccountNumber>" + 
				"<coreBankBranchSortCode>" + destCoreBankBranchSortCode + "</coreBankBranchSortCode>" + 
				"<currency>" + currency + "</currency>" + 
				"<localCurrency>" + currency + "</localCurrency>" + 
				"<debitCreditIndicator>c</debitCreditIndicator>" + 
				"<baseEquivalentAmount>" + new Util().roundUpAmount(amt) + "</baseEquivalentAmount>" + 
				"<entryAmount>" + new Util().roundUpAmount(amt) + "</entryAmount>" + 
				"<exchangeRate>1</exchangeRate>" + 
				"<multiplyDivideIndicator>m</multiplyDivideIndicator>" + 
				"<entryType>position</entryType>" + 
				"<entryDate>" + entryDate + "</entryDate>" + 
				"<valueDate>" + entryDate + "</valueDate>" + 
				"<sourcePostingReference>" + reference.trim() + "</sourcePostingReference>" + 
				"<transactionReference>" + reference.trim() + "</transactionReference>" + 
				"<narrative1>" + narrative1.trim() + "</narrative1>" + 
				"<narrative2>" + reference.trim() + "</narrative2>" + 
				"<narrative3>" + reference.trim() + "</narrative3>" + 
				"<forcePost>N</forcePost>" + 
				"</posting>" + 
				"</accountPostingRequest>" + 
				"</InputPayload>" + 
				"</InputData>" + 
				"<sbav:InputData/><max1:requestHeader/></max:ExecuteIntegration>" + 
				"</soapenv:Body>" + 
				"</soapenv:Envelope>";
			
			
			if(ComminsApplicationState.BANC_ABC==1)
			{
				SimpleDateFormat sdf34 = new SimpleDateFormat("yyyy-MM-dd");
				Date ds = new Date();
				String ds1 = sdf34.format(ds);
				xmlInput =
						"<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" " +
						"xmlns:ban=\"http://www.bancabc.com/BancAbcZmCustomsPayment.webservices.providers:CustomsPayment_WSD\">" +
						"<soap:Header/>" +
						"<soap:Body>" +
						"<ban:sendPaymentConfirmation>" +
						"<sendPaymentConfirmationRequest>" +
						"<accountNumberToDebit>" + bankAccountNumber + "</accountNumberToDebit>" +
						"<accountNumberToCredit>" + destbankAccountNumber + "</accountNumberToCredit>" +
						"<branchCode>ZM1</branchCode>" +
						"<amountToBePaid>" + new Util().roundUpAmountStyle2(amt) + "</amountToBePaid>" +
						"<customsReference>"+customsReference+"</customsReference>" +
						"<transactionType></transactionType>" +
						"<transactionDate>"+ds1+"</transactionDate>" +
						"<transactionDescription>Payment_for_Transaction_"+reference+"</transactionDescription>" +
						"</sendPaymentConfirmationRequest>" +
						"</ban:sendPaymentConfirmation>" +
						"</soap:Body>" +
						"</soap:Envelope>";
			}
			
			System.out.println("xmlInput = " + xmlInput);
			 
			
			try
			{
				byte[] buffer = new byte[xmlInput.length()];
				buffer = xmlInput.getBytes();
				bout.write(buffer);
				byte[] b = bout.toByteArray();
				
				// Set the appropriate HTTP parameters.
				if(ComminsApplicationState.STB==1)
				{
					String SOAPAction =
							"\"ExecuteIntegration\"";
					httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
					httpConn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
					httpConn.setRequestProperty("SOAPAction", SOAPAction);
					httpConn.setRequestProperty("Accept-Encoding", "gzip,deflate");
					httpConn.setRequestProperty("Accept", "text/xml");
					httpConn.setRequestProperty("RequestVersion", "HTTP/1.1");
					httpConn.setRequestProperty("Host", "10.236.6.125:6080");
			        httpConn.setRequestProperty("Accept", "*/*");
			        httpConn.setRequestProperty("User-Agent", "Java");
					httpConn.setRequestMethod("POST");
					httpConn.setDoOutput(true);
					httpConn.setDoInput(true);
				}
				if(ComminsApplicationState.BANC_ABC==1)
				{
					String SOAPAction =
							"\"BancAbcZmCustomsPayment_webservices_providers_CustomsPayment_WSD_Binder_sendPaymentConfirmation\"";
					httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
					httpConn.setRequestProperty("Content-Type", "application/soap+xml;charset=UTF-8");
					httpConn.setRequestProperty("SOAPAction", SOAPAction);
					//httpConn.setRequestProperty("Accept-Encoding", "gzip,deflate");
					httpConn.setRequestProperty("Accept", "application/soap+xml");
					httpConn.setRequestProperty("RequestVersion", "HTTP/1.1");
					httpConn.setRequestProperty("Host", "10.103.10.23:5555");
			        httpConn.setRequestProperty("Accept", "*/*");
			        httpConn.setRequestProperty("User-Agent", "Java");
					httpConn.setRequestMethod("POST");
					httpConn.setDoOutput(true);
					httpConn.setDoInput(true);
				}
				OutputStream out = httpConn.getOutputStream();
				//Write the content of the request to the outputstream of the HTTP Connection.
				out.write(b);
				out.close();
				httpConn.disconnect();
				//Ready with sending the request.
				 
				//Read the response.
				InputStreamReader isr =
				new InputStreamReader(httpConn.getInputStream());
				BufferedReader in = new BufferedReader(isr);
				 
				//Write the SOAP message response to a String.
				while ((responseString = in.readLine()) != null) {
				outputString = outputString + responseString; 	
				}
				System.out.println("Output stream = " + outputString);
				//Parse the String output to a org.w3c.dom.Document and be able to reach every node with the org.w3c.dom API.
				Document document = parseXmlFile(outputString);
				NodeList nodeLst = document.getElementsByTagName("sendPaymentConfirmationResponse");
				Node errorNode = document.getElementsByTagName("responseCode").item(0);
				String errorCode = errorNode.getTextContent();
				System.out.println("errorCode: " + errorCode);
				FundsTransferResponse ftr = null;
				if(errorCode!=null && !errorCode.equals("0"))
				{
					//NodeList codeNode = document.getElementsByTagName("code");
					String responseMessage = document.getElementsByTagName("responseMessage").item(0).getTextContent();
					new SendSms("+260974080276", "Funds Transfer Error Code - " + responseMessage + " - Please check MAX for error interpretation", "SmartPay", "10.236.6.99", "80");
					new SendSms("+260978777738", "Funds Transfer Error Code - " + responseMessage + " - Please check MAX for error interpretation", "SmartPay", "10.236.6.99", "80");
					ftr = null;
				}
				else
				{
					nodeLst = document.getElementsByTagName("paymentReference");
					String paymentReference = nodeLst.item(0).getTextContent();
					System.out.println("paymentReference: " + paymentReference);
					nodeLst = document.getElementsByTagName("customsReference");
					String customsRef = nodeLst.item(0).getTextContent();
					System.out.println("customsRef: " + customsRef);
					nodeLst = document.getElementsByTagName("transactionDateTime");
					String transactionDateTime = nodeLst.item(0).getTextContent();
					System.out.println("transactionDateTime: " + transactionDateTime);
					nodeLst = document.getElementsByTagName("responseCode");
					String responseCode = nodeLst.item(0).getTextContent();
					System.out.println("responseCode: " + responseCode);
					nodeLst = document.getElementsByTagName("responseMessage");
					String responseMessage = nodeLst.item(0).getTextContent();
					System.out.println("responseMessage: " + responseMessage);
					
					
//					NodeList nodeLst4 = document.getElementsByTagName("status");
//					String status = nodeLst4.item(0).getTextContent();
//					System.out.println("status: " + status);
//					
//					NodeList nodeLst41 = document.getElementsByTagName("head:resMessageId");
//					String resMessageId = nodeLst41.item(0).getTextContent();
//					System.out.println("resMessageId: " + resMessageId);
//					
//					NodeList nodeLst42 = document.getElementsByTagName("head:resTimeStamp");
//					String resTimeStamp = nodeLst42.item(0).getTextContent();
//					System.out.println("resTimeStamp: " + resTimeStamp);
					 
					//Write the SOAP message formatted to the console.
					String formattedSOAPResponse = formatXML(outputString);
					System.out.println(formattedSOAPResponse);
					ftr = new FundsTransferResponse();
					ftr.setStatus(responseCode.equalsIgnoreCase("0") ? Boolean.TRUE : Boolean.FALSE);
					ftr.setAccountNumber(bankAccountNumber);
					if(ComminsApplicationState.BANC_ABC==1)
						ftr.setResMessageId(paymentReference);
				}
				
				
				
				return ftr;
			}catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}else
			return null;
	}
	
	
	public static String formatXML(String unformattedXml) {
		try {
		Document document = parseXmlFile(unformattedXml);
		OutputFormat format = new OutputFormat(document);
		format.setIndenting(true);
		format.setIndent(3);
		format.setOmitXMLDeclaration(true);
		Writer out = new StringWriter();
		XMLSerializer serializer = new XMLSerializer(out, format);
		serializer.serialize(document);
		return out.toString();
		} catch (IOException e) {
		throw new RuntimeException(e);
		}
	}
		 
	public static Document parseXmlFile(String in) {
		System.out.println("Parse this xml string = " + in);
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(in));
			return db.parse(is);
		} catch (ParserConfigurationException e) {
		//throw new RuntimeException(e);
			return null;
		} catch (SAXException e) {
		//throw new RuntimeException(e);
			return null;
		} catch (IOException e) {
		//throw new RuntimeException(e);
			return null;
		}
	}

	public PortalUser getPortalUserByEmailAddress(
			String emailAddress, SwpService swpService) {
		// TODO Auto-generated method stub
		PortalUser fd = null;
		
		try {
			
				String hql = "select pu from PortalUser pu where (" +
						"pu.emailAddress = '" + emailAddress + "')";
				log.info("Get hql = " + hql);
				fd = (PortalUser) swpService.getUniqueRecordByHQL(hql);
				log.info("fd===" + (fd!=null ? fd.getEmailAddress() : "NA"));
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return fd;
	}
	
	
	
	
	
	
	
	public SOAPMessage createSOAPRequestForGetMiscOfUnPaidAssessmentsByDeclarantCode(
			String tpin_declarantcode, String bankCode) {
		// TODO Auto-generated method stub
		try
		{
	        MessageFactory messageFactory = MessageFactory.newInstance();
	        SOAPMessage soapMessage = messageFactory.createMessage();
	        SOAPPart soapPart = soapMessage.getSOAPPart();
	
	        
	        // SOAP Envelope
	        SOAPEnvelope envelope = soapPart.getEnvelope();
	        envelope.addNamespaceDeclaration("pro", "http://testservice.probase.com/");
			SOAPBody soapBody = envelope.getBody();
			SOAPElement soapBodyElem = soapBody.addChildElement("getMiscOfUnpaidAssessments", "pro");
			SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("bankCode");
	        soapBodyElem1.addTextNode(bankCode);
	        SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("tpin_declarantCode");
	        soapBodyElem2.addTextNode(tpin_declarantcode);
        	
        	soapMessage.saveChanges();
        	

        /* Print the request message */
        	System.out.print("Request SOAP Message for Probase Service = ");
        	soapMessage.writeTo(System.out);
        	log.info("");

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
			
			return soapMessage;
		}
	}

	public SOAPMessage createSOAPRequestForGetInterestOfUnPaidAssessmentsByDeclarantCode(
			String tpin_declarantcode, String bankCode) {
		// TODO Auto-generated method stub
		try
		{
	        MessageFactory messageFactory = MessageFactory.newInstance();
	        SOAPMessage soapMessage = messageFactory.createMessage();
	        SOAPPart soapPart = soapMessage.getSOAPPart();
	
	        
	        // SOAP Envelope
	        SOAPEnvelope envelope = soapPart.getEnvelope();
	        envelope.addNamespaceDeclaration("pro", "http://testservice.probase.com/");
			SOAPBody soapBody = envelope.getBody();
			SOAPElement soapBodyElem = soapBody.addChildElement("getInterestOfUnpaidAssessments", "pro");
			SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("bankCode");
	        soapBodyElem1.addTextNode(bankCode);
	        SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("tpin_declarantCode");
	        soapBodyElem2.addTextNode(tpin_declarantcode);
	        SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("declarantFlag");
	        soapBodyElem3.addTextNode("Y");
        	
        	soapMessage.saveChanges();
        	

        /* Print the request message */
        	System.out.print("Request SOAP Message for Probase Service = ");
        	soapMessage.writeTo(System.out);
        	log.info("");

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
			
			return soapMessage;
		}
	}
	
	
	public SOAPMessage createSOAPRequestForGetInterestOfUnPaidAssessmentsByTPIN(
			String tpin_declarantcode, String bankCode) {
		// TODO Auto-generated method stub
		try
		{
	        MessageFactory messageFactory = MessageFactory.newInstance();
	        SOAPMessage soapMessage = messageFactory.createMessage();
	        SOAPPart soapPart = soapMessage.getSOAPPart();
	
	        
	        // SOAP Envelope
	        SOAPEnvelope envelope = soapPart.getEnvelope();
	        envelope.addNamespaceDeclaration("pro", "http://testservice.probase.com/");
			SOAPBody soapBody = envelope.getBody();
			SOAPElement soapBodyElem = soapBody.addChildElement("getInterestOfUnpaidAssessments", "pro");
			SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("bankCode");
	        soapBodyElem1.addTextNode(bankCode);
	        SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("tpin_declarantCode");
	        soapBodyElem2.addTextNode(tpin_declarantcode);
	        SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("declarantFlag");
	        soapBodyElem3.addTextNode("N");
        	
        	soapMessage.saveChanges();
        	

        /* Print the request message */
        	System.out.print("Request SOAP Message for Probase Service = ");
        	soapMessage.writeTo(System.out);
        	log.info("");

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
			
			return soapMessage;
		}
	}
	
	
	
	
	
	
	
	
	
	public Collection<MiscToBePaid> handleResponseForGetMiscResponse(
			SOAPMessage soapResponse, SwpService swpService) {
		// TODO Auto-generated method stub
		SOAPBody soapBodyResponse;
		try {
			soapBodyResponse = soapResponse.getSOAPBody();
    		
    		Collection<MiscToBePaid> miscToBePaidListing = new ArrayList<MiscToBePaid>();
    		
    		
    		
			if(soapBodyResponse!=null)
	        {
	        	//String resp = util.parseSOAPBodyToString(soapBodyResponse);
	        	log.info("");
	        	log.info("soapBodyResponse.getNodeName();==>" + soapBodyResponse.getNodeName());
	        	
	        	Name name = SOAPFactory.newInstance().createName("S:Body");
	        	NodeList nodeList = soapBodyResponse.getChildNodes();
	        	Node node = nodeList.item(0);
	        	log.info("node.getNodeName();==>" + node.getNodeName());
	        	NodeList nodeList2 = node.getChildNodes();
	        	log.info("node child size: " + nodeList2.getLength());
	        	
	        	if(nodeList2.getLength()>0)
	        	{
		        	for(int b= 0; b<nodeList2.getLength(); b++)
		        	{
		        		Node node2 = nodeList2.item(b);
			        	log.info("node2.getNodeName();==>" + node2.getNodeName());
			        	if(node2.getNodeName().equalsIgnoreCase("miscToBePaidList"))
			        	{
				        	NodeList nodeList3 = node2.getChildNodes();
				        	MiscToBePaid miscToBePaid = new MiscToBePaid();
				        	for(int c= 0; c<nodeList3.getLength(); c++)
				        	{
				        		Node node3 = nodeList3.item(c);
				        		log.info("node3.getNodeName();==>" + node3.getNodeName());
				        		
				    			NodeList nodeList4 = node3.getChildNodes();
					        	for(int c1= 0; c1<nodeList4.getLength(); c1++)
					        	{
					        		Node node4 = nodeList4.item(c1);
					        		log.info("node3==>" + node3.getNodeName() + (node4.getNodeValue()==null ? "" : " value = " + node4.getNodeValue()));
					        		try
					        		{
					        			
					        						        			
						        		if(node3.getNodeName().equals("amountToBePaid"))
					        			{
						        			miscToBePaid.setAmountToBePaid(Double.valueOf(node4.getNodeValue().trim()));
					        			}
					        			if(node3.getNodeName().equals("registrationNumber"))
					        			{
					        				miscToBePaid.setRegistrationNumber((String)node4.getNodeValue());
					        			}
					        			if(node3.getNodeName().equals("registrationYear"))
					        			{
					        				miscToBePaid.setRegistrationYear((String)node4.getNodeValue());
					        			}
					        			if(node3.getNodeName().equals("registrationSerial"))
					        			{
					        				miscToBePaid.setRegistrationSerial((String)node4.getNodeValue());
					        			}
					        			if(node3.getNodeName().equalsIgnoreCase("tpin"))
					        			{
					        				miscToBePaid.setTpin((String)node4.getNodeValue());
					        			}
					        			if(node3.getNodeName().equals("officeCode"))
					        			{
					        				miscToBePaid.setPort((String)node4.getNodeValue());
					        			}
					        			if(node3.getNodeName().equals("transactionCode"))
					        			{
					        				miscToBePaid.setTransactionCode((String)node4.getNodeValue());
					        			}
					        			if(node3.getNodeName().equals("transactionDescription"))
					        			{
					        				miscToBePaid.setTransactionDescription((String)node4.getNodeValue());
					        			}
					        			if(node3.getNodeName().equals("errorCode"))
					        			{
					        				miscToBePaid.setErrorCode((String)node4.getNodeValue());
					        			}
					        			
					        		}catch(NumberFormatException e)
					        		{
					        			e.printStackTrace();
					        		}
					        	}
				        	}
				        	miscToBePaidListing.add(miscToBePaid);
			        	}
		        	}
		        	
	        	}else
	        	{
	        		

	        		miscToBePaidListing = new ArrayList<MiscToBePaid>();
		    		
	        	}
	        	
	        	
	        }else
	        {
	        	
	        }
			
			return miscToBePaidListing;
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	


	public Collection<InterestToBePaid> handleResponseForGetInterestResponse(
			SOAPMessage soapResponse, SwpService swpService) {
		// TODO Auto-generated method stub
		SOAPBody soapBodyResponse;
		try {
			soapBodyResponse = soapResponse.getSOAPBody();
    		
    		Collection<InterestToBePaid> interestToBePaidListing = new ArrayList<InterestToBePaid>();
    		
    		
    		
			if(soapBodyResponse!=null)
	        {
	        	//String resp = util.parseSOAPBodyToString(soapBodyResponse);
	        	log.info("");
	        	log.info("soapBodyResponse.getNodeName();==>" + soapBodyResponse.getNodeName());
	        	
	        	Name name = SOAPFactory.newInstance().createName("S:Body");
	        	NodeList nodeList = soapBodyResponse.getChildNodes();
	        	Node node = nodeList.item(0);
	        	log.info("node.getNodeName();==>" + node.getNodeName());
	        	NodeList nodeList2 = node.getChildNodes();
	        	log.info("node child size: " + nodeList2.getLength());
	        	
	        	if(nodeList2.getLength()>0)
	        	{
		        	for(int b= 0; b<nodeList2.getLength(); b++)
		        	{
		        		Node node2 = nodeList2.item(b);
			        	log.info("node2.getNodeName();==>" + node2.getNodeName());
			        	if(node2.getNodeName().equalsIgnoreCase("interestToBePaidList"))
			        	{
				        	NodeList nodeList3 = node2.getChildNodes();
			    			InterestToBePaid interestToBePaid = new InterestToBePaid();
				        	for(int c= 0; c<nodeList3.getLength(); c++)
				        	{
				        		Node node3 = nodeList3.item(c);
				        		log.info("node3.getNodeName();==>" + node3.getNodeName());
				        		
				    			NodeList nodeList4 = node3.getChildNodes();
					        	for(int c1= 0; c1<nodeList4.getLength(); c1++)
					        	{
					        		Node node4 = nodeList4.item(c1);
					        		log.info("node3==>" + node3.getNodeName() + (node4.getNodeValue()==null ? "" : " value = " + node4.getNodeValue()));
					        		try
					        		{
					        			
					        						        			
						        		if(node3.getNodeName().equals("amountToBePaid"))
					        			{
						        			interestToBePaid.setAmountToBePaid(Double.valueOf(node4.getNodeValue().trim()));
					        			}
					        			if(node3.getNodeName().equals("registrationNumber"))
					        			{
					        				interestToBePaid.setRegistrationNumber((String)node4.getNodeValue());
					        			}
					        			if(node3.getNodeName().equals("registrationYear"))
					        			{
					        				interestToBePaid.setRegistrationYear((String)node4.getNodeValue());
					        			}
					        			if(node3.getNodeName().equals("registrationSerial"))
					        			{
					        				interestToBePaid.setRegistrationSerial((String)node4.getNodeValue());
					        			}
					        			if(node3.getNodeName().equals("declarantCode"))
					        			{
					        				interestToBePaid.setDeclarantCode((String)node4.getNodeValue());
					        			}
					        			if(node3.getNodeName().equalsIgnoreCase("tpin"))
					        			{
					        				interestToBePaid.setTpin((String)node4.getNodeValue());
					        			}
					        			if(node3.getNodeName().equals("port"))
					        			{
					        				interestToBePaid.setPort((String)node4.getNodeValue());
					        			}
					        			
					        		}catch(NumberFormatException e)
					        		{
					        			e.printStackTrace();
					        		}
					        	}
				        	}
				        	interestToBePaidListing.add(interestToBePaid);
			        	}
		        	}
		        	
	        	}else
	        	{
	        		

	        		interestToBePaidListing = new ArrayList<InterestToBePaid>();
		    		
	        	}
	        	
	        	
	        }else
	        {
	        	
	        }
			
			return interestToBePaidListing;
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	
	
	
	
	
	//2-step login has nothing to do with approval/initiator process. they are entirely seperate processes independent of each other
	public boolean loginStepTwoForPortalUserManagement(PortalUser portalUser, PortalUserCRUDRights portalUserCRUDRights,
			ComminsApplicationState cappState, String email2, String password, SwpService sService) {
		// TODO Auto-generated method stub
		try {
			long login = UserLocalServiceUtil.authenticateForBasic(ProbaseConstants.COMPANY_ID, CompanyConstants.AUTH_TYPE_EA, 
					email2, password);
			log.info("login calue = " + login);
			if(login==0)
			{
				log.info("User Credentials are invalid");
				cappState.setLoggedIn(Boolean.FALSE);
				cappState.setPortalUser(null);
				return false;
			}
			else
			{
				User lpUser = UserLocalServiceUtil.getUserById(login);
				if(lpUser.getStatus()==0)
				{
					log.info("User Credentials are Valid");
					PortalUser pu = getPortalUserByEmailAddress(email2, sService);
					if(portalUser.getRoleType().getRoleTypeName().equals(pu.getRoleType().getRoleTypeName()))
					{
						log.info("1");
						//if(portalUserCRUDRights!=null && portalUserCRUDRights.getCudInitiatorRights()!=null && portalUserCRUDRights.getStatus().equals(Boolean.TRUE) && portalUserCRUDRights.getCudInitiatorRights().equals(Boolean.TRUE))
						//{
							log.info(2);
							if(pu!=null && pu.getStatus().equals(PortalUserStatusConstants.PORTAL_USER_STATUS_ACTIVE))
							{
								log.info(3);
								//PortalUserCRUDRights pcrs = getPortalUserCRUDRightsByPortalUser(pu, sService);
								//if(pcrs.getCudApprovalRights()!=null && pcrs.getCudApprovalRights()!=null && pcrs.getStatus().equals(Boolean.TRUE) && pcrs.getCudApprovalRights().equals(Boolean.TRUE))
								//{
									log.info(4);
									if(pu.getId().equals(portalUser.getId()))
									{
										log.info(5);
										cappState.setPortalUser(null);
										cappState.setLoggedIn(Boolean.FALSE);
										return false;
									}else
									{
										log.info(6);
										cappState.setPortalUser(pu);
										cappState.setLoggedIn(Boolean.TRUE);
										return true;
									}
								//}else
								//{
									//log.info(7);
									//cappState.setPortalUser(null);
									//cappState.setLoggedIn(Boolean.FALSE);
									//return false;
								//}
							}
							else
							{
								log.info(8);
								cappState.setPortalUser(null);
								cappState.setLoggedIn(Boolean.FALSE);
								return false;
							}
						//}else
						//{
							//log.info(9);
							//cappState.setPortalUser(null);
							//cappState.setLoggedIn(Boolean.FALSE);
							//return false;
						//}
					}else
					{
						log.info(10);
						cappState.setPortalUser(null);
						cappState.setLoggedIn(Boolean.FALSE);
						return false;
					}
				}else
				{
					log.info(20);
					cappState.setPortalUser(null);
					cappState.setLoggedIn(Boolean.FALSE);
					return false;
				}
				
				
			}
				
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}



	private PortalUserCRUDRights getPortalUserCRUDRightsByPortalUser(
			PortalUser pu, SwpService sService) {
		// TODO Auto-generated method stub
		PortalUserCRUDRights rt = null;
		try {
			
				String hql = "select rt from PortalUserCRUDRights rt where (" +
						"rt.portalUser.id = " + pu.getId() + ")";
				log.info("Get hql = " + hql);
				rt = (PortalUserCRUDRights) sService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}



	public String formatMobile(String mobile3) {
		// TODO Auto-generated method stub
		String returnStr = mobile3;
		if(mobile3.length()>0)
		{
			String[] except = {"+260", "260", "0"};
			for(int c =0; c<except.length; c++)
			{
				if(mobile3.startsWith(except[c]))
					returnStr = mobile3.replaceFirst(except[c], "");
			}
		}
		return returnStr;
		
	}
	
	



	public boolean loginStepTwoForCompanyManagement(PortalUser portalUser,
			CompanyCRUDRights companyCRUDRights,
			ComminsApplicationState cappState, String email2, String password,
			SwpService swpService2) {
		// TODO Auto-generated method stub
		try {
			long login = UserLocalServiceUtil.authenticateForBasic(ProbaseConstants.COMPANY_ID, CompanyConstants.AUTH_TYPE_EA, 
					email2, password);
			log.info("login calue = " + login);
			if(login==0)
			{
				log.info("User Credentials are invalid");
				cappState.setLoggedIn(Boolean.FALSE);
				cappState.setPortalUser(null);
				return false;
			}
			else
			{
				User lpUser = UserLocalServiceUtil.getUserById(login);
				if(lpUser.getStatus()==0)
				{
					log.info("User Credentials are Valid");
					PortalUser pu = getPortalUserByEmailAddress(email2, swpService2);
					if(portalUser.getRoleType().getRoleTypeName().equals(pu.getRoleType().getRoleTypeName()))
					{
						log.info("1");
						//if(portalUserCRUDRights!=null && portalUserCRUDRights.getCudInitiatorRights()!=null && portalUserCRUDRights.getStatus().equals(Boolean.TRUE) && portalUserCRUDRights.getCudInitiatorRights().equals(Boolean.TRUE))
						//{
							log.info(2);
							if(pu!=null && pu.getStatus().equals(PortalUserStatusConstants.PORTAL_USER_STATUS_ACTIVE))
							{
								log.info(3);
								//PortalUserCRUDRights pcrs = getPortalUserCRUDRightsByPortalUser(pu, sService);
								//if(pcrs.getCudApprovalRights()!=null && pcrs.getCudApprovalRights()!=null && pcrs.getStatus().equals(Boolean.TRUE) && pcrs.getCudApprovalRights().equals(Boolean.TRUE))
								//{
									log.info(4);
									if(pu.getId().equals(portalUser.getId()))
									{
										log.info(5);
										cappState.setPortalUser(null);
										cappState.setLoggedIn(Boolean.FALSE);
										return false;
									}else
									{
										log.info(6);
										cappState.setPortalUser(pu);
										cappState.setLoggedIn(Boolean.TRUE);
										return true;
									}
								//}else
								//{
									//log.info(7);
									//cappState.setPortalUser(null);
									//cappState.setLoggedIn(Boolean.FALSE);
									//return false;
								//}
							}
							else
							{
								log.info(8);
								cappState.setPortalUser(null);
								cappState.setLoggedIn(Boolean.FALSE);
								return false;
							}
						//}else
						//{
							//log.info(9);
							//cappState.setPortalUser(null);
							//cappState.setLoggedIn(Boolean.FALSE);
							//return false;
						//}
					}else
					{
						log.info(10);
						cappState.setPortalUser(null);
						cappState.setLoggedIn(Boolean.FALSE);
						return false;
					}
				}else
				{
					log.info(20);
					cappState.setPortalUser(null);
					cappState.setLoggedIn(Boolean.FALSE);
					return false;
				}
				
				
			}
				
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}



	public static DeclarationPaymentResult doDeclareZRAPayment(String bankCode, String portCode, String declarantCode, 
			String companyCode, Collection<DeclarationsToBePaid> declarationsToBePaidList, 
			String meanOfPayment, String checkReference, String amountToBePaid, String paymentDate, SwpService swpService) {
		// TODO Auto-generated method stub
		Logger log = Logger.getLogger(Util.class);
		String url = "http://localhost:8086/WS/ProbaseZambiaService";
        url = "http://localhost:8085/ProbaseTestService2/ProbaseZambia";
       // url = "http://probasetestservice2-pallpod.rhcloud.com/ProbaseTestService2/ProbaseZambia";
        //url = "http://probasetestservice2-pallpod.rhcloud.com/ProbaseTestService2/ProbaseZambia";
        log.info("url = > " + bankCode+ " " + portCode+ " " + declarantCode + " " + 
        companyCode + " " + declarationsToBePaidList+ " " + 
        meanOfPayment+ " " + checkReference+ " " + amountToBePaid+ " " + paymentDate);
        
        
			
						
		try {
            // Create SOAP Connection
			
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            try
            {
	            SOAPMessage soapResponse = soapConnection.call(
	            		Util.createSOAPRequestForGetDeclarationPayment(bankCode, portCode, declarantCode, 
	            				companyCode, declarationsToBePaidList, meanOfPayment, checkReference, 
	            				amountToBePaid, paymentDate), url);

	            DeclarationPaymentResult declarationPaymentResultListing = new Util().handleResponseForGetDeclarationPayment(soapResponse, swpService);


	            new Util().printSOAPResponse(soapResponse);
	            soapConnection.close();
	            return declarationPaymentResultListing;
	            
	            
            }catch(PrivilegedActionException e1)
            {
            	log.info("Ok start here");
            	e1.printStackTrace();
//            	aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
//    			portletState.addError(aReq, "Sending Request for payment failed. Please check your internet connection! E189104 Error Code. Contact the bank for more details if this problems persist.", portletState);
    			soapConnection.close();
    			return null;
            }
    		

           
        } catch (Exception e) {
            System.err.println("Error occurred while sending SOAP Request to Server");
            e.printStackTrace();
//            aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
//			portletState.addError(aReq, "Parsing data returned failed! E189104 Error Code. Contact the bank for more details if this problems persist.", portletState);
			return null;
        }
		
	}



	
	
	private static SOAPMessage createSOAPRequestForGetInterestDeclarationPayment(
			String bankCode, String portCode, String declarantCode,
			String companyCode,
			Collection<InterestToBePay> interestToBePaidList,
			String meanOfPayment, String checkReference, String amountToBePaid,
			String paymentDate) {
		// TODO Auto-generated method stub
		try
		{
	        MessageFactory messageFactory = MessageFactory.newInstance();
	        SOAPMessage soapMessage = messageFactory.createMessage();
	        SOAPPart soapPart = soapMessage.getSOAPPart();
	
	        
	        // SOAP Envelope
	        SOAPEnvelope envelope = soapPart.getEnvelope();
	        envelope.addNamespaceDeclaration("pro", "http://testservice.probase.com/");
			SOAPBody soapBody = envelope.getBody();
//			SOAPElement soapBodyElem = soapBody.addChildElement("interestPaymentRequest", "pro");
////			SOAPElement soapBodyElem = soapBodyEle.addChildElement("interestPaymentInfo", "pro");
//			SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("bankCode");
//	        soapBodyElem1.addTextNode(bankCode);
//	        SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("officeCode");
//	        soapBodyElem2.addTextNode(portCode);
//	        SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("declarantCode");
//	        soapBodyElem3.addTextNode(declarantCode.toString());
//	        SOAPElement soapBodyElem4 = soapBodyElem.addChildElement("companyCode");
//	        soapBodyElem4.addTextNode(companyCode);
//	        Double sum = 0.00;
//	        Iterator<InterestToBePay> it = interestToBePaidList.iterator();
//	        
//	        	InterestToBePay itbp = it.next();
//	        	SOAPElement soapBodyElem5 = soapBodyElem.addChildElement("interestToBePaid");
//	        	SOAPElement soapBodyElem51 = soapBodyElem5.addChildElement("transactionCode");
//	        	soapBodyElem51.addTextNode(itbp.getTransactionCode());
//	        	SOAPElement soapBodyElem52 = soapBodyElem5.addChildElement("referenceOffice");
//	        	soapBodyElem52.addTextNode(itbp.getReferenceOffice());
//	        	SOAPElement soapBodyElem53 = soapBodyElem5.addChildElement("referenceYear");
//	        	soapBodyElem53.addTextNode(itbp.getReferenceYear());
//	        	SOAPElement soapBodyElem54 = soapBodyElem5.addChildElement("referenceSerial");
//	        	soapBodyElem54.addTextNode(itbp.getReferenceSerial());
//	        	SOAPElement soapBodyElem55 = soapBodyElem5.addChildElement("referenceNumber");
//	        	soapBodyElem55.addTextNode(itbp.getReferenceNumber());
//	        	SOAPElement soapBodyElem56 = soapBodyElem5.addChildElement("referenceText");
//	        	soapBodyElem56.addTextNode(itbp.getReferenceText());
//	        	SOAPElement soapBodyElem57 = soapBodyElem5.addChildElement("assessmentSerial");
//	        	soapBodyElem57.addTextNode(itbp.getAssessmentSerial());
//	        	SOAPElement soapBodyElem58 = soapBodyElem5.addChildElement("assessmentNumber");
//	        	soapBodyElem58.addTextNode(itbp.getAssessmentSerial());
//	        	SOAPElement soapBodyElem59 = soapBodyElem5.addChildElement("amountToBePaid");
//	        	soapBodyElem59.addTextNode(amountToBePaid);
//	        	sum = sum + Double.valueOf(amountToBePaid);
//	        
//	        SOAPElement soapBodyElem6 = soapBodyElem.addChildElement("meansOfPayment");
//	        SOAPElement soapBodyElem7 = soapBodyElem6.addChildElement("meanOfPayment");
//	        soapBodyElem7.addTextNode("?");
//	        SOAPElement soapBodyElem8 = soapBodyElem6.addChildElement("bankCode");
//	        soapBodyElem8.addTextNode(bankCode);
//	        SOAPElement soapBodyElem9 = soapBodyElem6.addChildElement("checkReference");
//	        soapBodyElem9.addTextNode("?");
//	        SOAPElement soapBodyElem10 = soapBodyElem6.addChildElement("amountToBePaid");
//	        soapBodyElem10.addTextNode(amountToBePaid);
//	        SOAPElement soapBodyElem11 = soapBodyElem.addChildElement("paymentDate");
//	        soapBodyElem11.addTextNode(paymentDate);
//	        SOAPElement soapBodyElem12 = soapBodyElem.addChildElement("amountToBePaid");
//	        soapBodyElem12.addTextNode(amountToBePaid);
	        
	        Iterator<InterestToBePay> it = interestToBePaidList.iterator();
	        InterestToBePay itbp = it.next();
	        SOAPElement soapBodyElem = soapBody.addChildElement("interestPayment", "pro");
	        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("amountToBePaid");
	        soapBodyElem1.addTextNode(amountToBePaid);
        	SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("assessmentNumber");
        	soapBodyElem2.addTextNode(itbp.getAssessmentNumber());
        	SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("assessmentSerial");
        	soapBodyElem3.addTextNode(itbp.getAssessmentSerial());
        	SOAPElement soapBodyElem4 = soapBodyElem.addChildElement("bankCode");
        	soapBodyElem4.addTextNode(bankCode);
        	SOAPElement soapBodyElem5 = soapBodyElem.addChildElement("checkReference");
        	soapBodyElem5.addTextNode("?");

	        SOAPElement soapBodyElem6 = soapBodyElem.addChildElement("companyCode");
	        soapBodyElem6.addTextNode(companyCode);
        	SOAPElement soapBodyElem7 = soapBodyElem.addChildElement("declarantCode");
        	soapBodyElem7.addTextNode(declarantCode.toString());
        	SOAPElement soapBodyElem8 = soapBodyElem.addChildElement("interestToBePaid");
        	soapBodyElem8.addTextNode(itbp.getAssessmentSerial());
        	SOAPElement soapBodyElem9 = soapBodyElem.addChildElement("meanOfPayment");
        	soapBodyElem9.addTextNode("?");
        	SOAPElement soapBodyElem10 = soapBodyElem.addChildElement("officeCode");
        	soapBodyElem10.addTextNode(portCode);
	        SOAPElement soapBodyElem11 = soapBodyElem.addChildElement("paymentDate");
	        soapBodyElem11.addTextNode(paymentDate);
	        
	        SOAPElement soapBodyElem12 = soapBodyElem.addChildElement("transactionCode");
	        soapBodyElem12.addTextNode(itbp.getTransactionCode());
        	SOAPElement soapBodyElem13 = soapBodyElem.addChildElement("referenceOffice");
        	soapBodyElem13.addTextNode(itbp.getReferenceOffice());
        	SOAPElement soapBodyElem14 = soapBodyElem.addChildElement("referenceYear");
        	soapBodyElem14.addTextNode(itbp.getReferenceYear());
        	SOAPElement soapBodyElem15 = soapBodyElem.addChildElement("referenceSerial");
        	soapBodyElem15.addTextNode(itbp.getReferenceSerial());
        	SOAPElement soapBodyElem16 = soapBodyElem.addChildElement("referenceNumber");
        	soapBodyElem16.addTextNode(itbp.getReferenceNumber());
        	SOAPElement soapBodyElem17 = soapBodyElem.addChildElement("referenceText");
        	soapBodyElem17.addTextNode(itbp.getReferenceText());
	        
//        MimeHeaders headers = soapMessage.getMimeHeaders();
//        headers.addHeader("SOAPAction", serverURI  + "getUnpaidAssessmentsByTPIN");
        	Logger log = Logger.getLogger(Util.class);
        	soapMessage.saveChanges();

        /* Print the request message */
        	System.out.print("Request SOAP Message for Probase Service = ");
        	soapMessage.writeTo(System.out);
        	log.info("");

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
			
			return soapMessage;
		}
	}
	
	
	
	private static SOAPMessage createSOAPRequestForGetDeclarationPayment(
			String bankCode, String portCode, String declarantCode,
			String companyCode,
			Collection<DeclarationsToBePaid> declarationsToBePaidList,
			String meanOfPayment, String checkReference, String amountToBePaid,
			String paymentDate) {
		// TODO Auto-generated method stub
		try
		{
	        MessageFactory messageFactory = MessageFactory.newInstance();
	        SOAPMessage soapMessage = messageFactory.createMessage();
	        SOAPPart soapPart = soapMessage.getSOAPPart();
	
	        
	        // SOAP Envelope
	        SOAPEnvelope envelope = soapPart.getEnvelope();
	        envelope.addNamespaceDeclaration("pro", "http://testservice.probase.com/");
			SOAPBody soapBody = envelope.getBody();
			Iterator<DeclarationsToBePaid> it = declarationsToBePaidList.iterator();
	        DeclarationsToBePaid dtbp = it.next();
			SOAPElement soapBodyElem = soapBody.addChildElement("declarationPayment", "pro");
	        SOAPElement soapBodyElem10 = soapBodyElem.addChildElement("amountToBePaid");
	        soapBodyElem10.addTextNode(amountToBePaid);
        	SOAPElement soapBodyElem56 = soapBodyElem.addChildElement("assessmentNumber");
        	soapBodyElem56.addTextNode(dtbp.getAssessmentNumber());
        	SOAPElement soapBodyElem55 = soapBodyElem.addChildElement("assessmentSerial");
        	soapBodyElem55.addTextNode(dtbp.getAssessmentSerial());
			SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("bankCode");
	        soapBodyElem1.addTextNode(bankCode);
	        SOAPElement soapBodyElem9 = soapBodyElem.addChildElement("checkReference");
	        soapBodyElem9.addTextNode("?");
	        SOAPElement soapBodyElem7 = soapBodyElem.addChildElement("meanOfPayment");
	        soapBodyElem7.addTextNode("?");
	        SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("declarantCode");
	        soapBodyElem3.addTextNode(declarantCode.toString());
	        SOAPElement soapBodyElem4 = soapBodyElem.addChildElement("meanOfPayment");
	        soapBodyElem4.addTextNode("?");
	        SOAPElement soapBodyElem41 = soapBodyElem.addChildElement("paymentDate");
	        soapBodyElem41.addTextNode(paymentDate);
	        SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("portCode");
	        soapBodyElem2.addTextNode(portCode);
        	SOAPElement soapBodyElem54 = soapBodyElem.addChildElement("registrationNumber");
        	soapBodyElem54.addTextNode(dtbp.getRegistrationNumber());
        	SOAPElement soapBodyElem53 = soapBodyElem.addChildElement("registrationSerial");
        	soapBodyElem53.addTextNode(dtbp.getRegistrationSerial());
        	SOAPElement soapBodyElem52 = soapBodyElem.addChildElement("registrationYear");
        	soapBodyElem52.addTextNode(dtbp.getRegistrationYear());
	        SOAPElement soapBodyElem24 = soapBodyElem.addChildElement("tpinNo");
	        soapBodyElem24.addTextNode(companyCode);
	       
	        
	        
//        MimeHeaders headers = soapMessage.getMimeHeaders();
//        headers.addHeader("SOAPAction", serverURI  + "getUnpaidAssessmentsByTPIN");
        	Logger log = Logger.getLogger(Util.class);
        	soapMessage.saveChanges();

        /* Print the request message */
        	System.out.print("Request SOAP Message for Probase Service = ");
        	soapMessage.writeTo(System.out);
        	log.info("");

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
			
			return soapMessage;
		}
	}



	public static InterestPaymentResult doDeclareZRAInterestPayment(
			String bankCode, String portCode, String declarantCode,
			String companyCode, Collection<InterestToBePay> interestToBePayList,
			String meanOfPayment, String checkReference, String amountToBePaid, String paymentDate,
			SwpService swpService) {
		// TODO Auto-generated method stub
		Logger log = Logger.getLogger(Util.class);
		String url = "http://localhost:8086/WS/ProbaseZambiaService";
        url = "http://localhost:8085/ProbaseTestService2/ProbaseZambia";
       // url = "http://probasetestservice2-pallpod.rhcloud.com/ProbaseTestService2/ProbaseZambia";
        //url = "http://probasetestservice2-pallpod.rhcloud.com/ProbaseTestService2/ProbaseZambia";
        
        
			
						
		try {
            // Create SOAP Connection
			
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            try
            {
	            SOAPMessage soapResponse = soapConnection.call(
	            		Util.createSOAPRequestForGetInterestDeclarationPayment(bankCode, portCode, declarantCode, 
	            				companyCode, interestToBePayList, meanOfPayment, checkReference, 
	            				amountToBePaid, paymentDate), url);

	            InterestPaymentResult interestPaymentResultListing = new Util().handleResponseForDoInterestPayment(soapResponse, swpService);


	            new Util().printSOAPResponse(soapResponse);
	            soapConnection.close();
	            return interestPaymentResultListing;
	            
	            
            }catch(PrivilegedActionException e1)
            {
            	log.info("Ok start here");
            	e1.printStackTrace();
//            	aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
//    			portletState.addError(aReq, "Sending Request for payment failed. Please check your internet connection! E189104 Error Code. Contact the bank for more details if this problems persist.", portletState);
    			soapConnection.close();
    			return null;
            }
    		

           
        } catch (Exception e) {
            System.err.println("Error occurred while sending SOAP Request to Server");
            e.printStackTrace();
//            aRes.setRenderParameter("jspPage", "/html/taxassessmentmanagementportlet/taxassessmentlisting.jsp");
//			portletState.addError(aReq, "Parsing data returned failed! E189104 Error Code. Contact the bank for more details if this problems persist.", portletState);
			return null;
        }
	}


	
	
	
	public static Collection<Assessment> addInterestToAssessmentsForCorporate(
			Collection<InterestToBePaid> itList,
			Collection<Assessment> allAssessmentListing) {

		// TODO Auto-generated method stub
		Logger log = Logger.getLogger(Util.class);
		log.info("INFO: TRACE STARTS HERE ------------------------------");
		
		ArrayList<Assessment> temp = new ArrayList<Assessment>();
		ArrayList<Assessment> temp1 = new ArrayList<Assessment>();
		for(Iterator<Assessment> aListIter = allAssessmentListing.iterator(); aListIter.hasNext();)
    	{
    		Assessment asp = aListIter.next();
    		asp.setInterest(Boolean.FALSE);
    		asp.setInterestAmount(0.00);
    		temp1.add(asp);
    	}
		
        
		if(itList!=null && itList.size()>0)
        {
        	log.info("INFO: itList.size = " + itList.size());
        	
        	
        	
        	for(Iterator<Assessment> aListIter = temp1.iterator(); aListIter.hasNext();)
        	{
        		Assessment as = aListIter.next();
    			log.info("INFO: as.regNo = " + as.getRegistrationNumber());
    			for(Iterator<InterestToBePaid> itListIter = itList.iterator(); itListIter.hasNext();)
            	{

            		InterestToBePaid interestToBePaid = itListIter.next();
//	            		log.info("INFO: interestToBePaid.getAmountToBePaid = " + interestToBePaid.getAmountToBePaid());
//	            		log.info("INFO: interestToBePaid.getDeclarantCode = " + (interestToBePaid.getDeclarantCode()==null? "N/A" : interestToBePaid.getDeclarantCode()));
//	            		log.info("INFO: as.getTpinInfo() = " + as.getTpinInfo()==null ? "N/A" : as.getTpinInfo().getTpin());
//	            		log.info("INFO: as.getDeclarantTpin() = " + as.getDeclarantTpin()==null ? "N/A" : as.getDeclarantTpin());
//	            		log.info("INFO: interestToBePaid.getTpin = " + interestToBePaid.getTpin()==null ? "N/A" : interestToBePaid.getTpin());
            		
            		if(as.getTpinInfo().getCompany().getCompanyType().getValue().equalsIgnoreCase(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY.getValue()))
            		{
            			if(as.getTpinInfo().getCompany().getClearingAgent()!=null && 
            					as.getTpinInfo().getCompany().getClearingAgent().equals(Boolean.TRUE))
            			{
            				if( as.getDeclarantTpin().equals(interestToBePaid.getDeclarantCode()) && 
                					as.getRegistrationNumber().equals(interestToBePaid.getRegistrationNumber()) && 
        							as.getClientTpin().equals(interestToBePaid.getTpin()) && 
                					as.getAssessmentYear().equals(Integer.valueOf(interestToBePaid.getRegistrationYear())) && 
                					as.getPorts().getPortCode().equals(interestToBePaid.getPort()))
        					{
                				as.setInterest(Boolean.TRUE);
                				as.setInterestAmount(interestToBePaid.getAmountToBePaid());
        					}
//	            				else
//	        					{
//	        						as.setInterest(Boolean.FALSE);
//	        						as.setInterestAmount(0.00);
//	        					}
            			}else if(as.getTpinInfo().getCompany().getClearingAgent()!=null && 
            					as.getTpinInfo().getCompany().getClearingAgent().equals(Boolean.FALSE))
            			{
            				if( as.getDeclarantTpin().equals(interestToBePaid.getDeclarantCode()) && 
                					as.getRegistrationNumber().equals(interestToBePaid.getRegistrationNumber()) && 
        							as.getClientTpin().equals(interestToBePaid.getTpin()) && 
                					as.getAssessmentYear().equals(Integer.valueOf(interestToBePaid.getRegistrationYear())) && 
                					as.getPorts().getPortCode().equals(interestToBePaid.getPort()))
        					{
                				as.setInterest(Boolean.TRUE);
                				as.setInterestAmount(interestToBePaid.getAmountToBePaid());
        					}
//	            				else
//	        					{
//	        						as.setInterest(Boolean.FALSE);
//	        						as.setInterestAmount(0.00);
//	        					}
            			}
            				
            			
            		}else if(as.getTpinInfo().getCompany().getCompanyType().getValue().equalsIgnoreCase(CompanyTypeConstants.COMPANY_TYPE_RETAIL_COMPANY.getValue()))
            		{
            			log.info("For retail companny");
//	            			log.info("as.getTpinInfo()" + (as.getTpinInfo()==null ? "= Null" : "is not null"));
//	            			log.info("as.getTpinInfo()" + (as.getTpinInfo()==null ? "= Null" : as.getTpinInfo().getTpin()));
            			log.info("as.getDeclarantTpin()" + (as.getDeclarantTpin()==null ? "= Null" : as.getDeclarantTpin()) + 
            					" AND interestToBePaid.getDeclarantCode()" + (interestToBePaid.getDeclarantCode()==null ? "= Null" : interestToBePaid.getDeclarantCode()));
            			log.info("as.getTpinInfo()" + (as.getRegistrationNumber()==null ? "= Null" : as.getRegistrationNumber()) + 
            					" AND as.getDeclarantTpin()" + (interestToBePaid.getRegistrationNumber()==null ? "= Null" : interestToBePaid.getRegistrationNumber()));
            			log.info("as.getTpinInfo()" + (as.getClientTpin()==null ? "= Null" : as.getClientTpin()) + 
            					" AND as.getDeclarantTpin()" + (interestToBePaid.getTpin()==null ? "= Null" : interestToBePaid.getTpin()));
            			log.info("as.getTpinInfo()" + (as.getAssessmentYear()==null ? "= Null" : as.getAssessmentYear()) + 
            					" AND as.getDeclarantTpin()" + (interestToBePaid.getRegistrationYear()==null ? "= Null" : interestToBePaid.getRegistrationYear()));
            			log.info("as.getTpinInfo()" + (as.getPorts()==null ? "= Null" : as.getPorts().getPortCode()) + 
            					" AND as.getDeclarantTpin()" + (interestToBePaid.getPort()==null ? "= Null" : interestToBePaid.getPort()));
            			
            			
//	            			if( as.getDeclarantTpin().equals(interestToBePaid.getDeclarantCode()))
//	    					{
//	            				log.info("A");
//	    					}
//	            			if(as.getRegistrationNumber().equals(interestToBePaid.getRegistrationNumber()))
//	    					{
//	            				log.info("B");
//	    					}
//	            			if(as.getClientTpin().equals(interestToBePaid.getTpin()))
//	    					{
//	            				log.info("C");
//	    					}
//	            			if(as.getAssessmentYear().equals(Integer.valueOf(interestToBePaid.getRegistrationYear())))
//	    					{
//	            				log.info("D");
//	    					}
//	            			if( as.getPorts().getPortCode().equals(interestToBePaid.getPort()))
//	    					{
//	            				log.info("E");
//	    					}
            			if( as.getDeclarantTpin().equals(interestToBePaid.getDeclarantCode()) && 
            					as.getRegistrationNumber().equals(interestToBePaid.getRegistrationNumber()) && 
    							as.getClientTpin().equals(interestToBePaid.getTpin()) && 
            					as.getAssessmentYear().equals(Integer.valueOf(interestToBePaid.getRegistrationYear())) && 
            					as.getPorts().getPortCode().equals(interestToBePaid.getPort()))
    					{
            				as.setInterest(Boolean.TRUE);
            				as.setInterestAmount(interestToBePaid.getAmountToBePaid());
            				log.info("interestToBePaid.getAmountToBePaid() = " + (interestToBePaid.getAmountToBePaid()==null ? "= Null" : interestToBePaid.getAmountToBePaid()));
    					}
//	            			else
//	    					{
//	    						as.setInterest(Boolean.FALSE);
//	    						as.setInterestAmount(0.00);
//	    						log.info("interestToBePaid.getAmountToBePaid() = " + (interestToBePaid.getAmountToBePaid()==null ? "= Null" : interestToBePaid.getAmountToBePaid()));
//	    					}
            		}
            		
            		
        			
            	}
    			temp.add(as);
        	}
        	
        	
        	allAssessmentListing = temp;
            temp = null;
            temp1 = null;	
        }
		else
		{
			allAssessmentListing = temp1;
		}
		log.info("allAssessmentListing = temp size ===" + allAssessmentListing.size());
        return allAssessmentListing;
	}

	public static Collection<Assessment> addInterestToAssessmentsForRetail(
			Collection<InterestToBePaid> itList,
			Collection<Assessment> allAssessmentListing) {
		// TODO Auto-generated method stub
		Logger log = Logger.getLogger(Util.class);
		log.info("INFO: TRACE STARTS HERE ------------------------------");
		
		ArrayList<Assessment> temp = new ArrayList<Assessment>();
		ArrayList<Assessment> temp1 = new ArrayList<Assessment>();
        if(itList!=null && itList.size()>0)
        {
        	log.info("INFO: itList.size = " + itList.size());
        	for(Iterator<Assessment> aListIter = allAssessmentListing.iterator(); aListIter.hasNext();)
        	{
        		Assessment asp = aListIter.next();
        		asp.setInterest(Boolean.FALSE);
        		asp.setInterestAmount(0.00);
        		temp1.add(asp);
        	}
        	
        	
        	for(Iterator<Assessment> aListIter = temp1.iterator(); aListIter.hasNext();)
        	{
        		Assessment as = aListIter.next();
    			log.info("INFO: as.regNo = " + as.getRegistrationNumber());
    			for(Iterator<InterestToBePaid> itListIter = itList.iterator(); itListIter.hasNext();)
            	{

            		InterestToBePaid interestToBePaid = itListIter.next();
//            		log.info("INFO: interestToBePaid.getAmountToBePaid = " + interestToBePaid.getAmountToBePaid());
//            		log.info("INFO: interestToBePaid.getDeclarantCode = " + (interestToBePaid.getDeclarantCode()==null? "N/A" : interestToBePaid.getDeclarantCode()));
//            		log.info("INFO: as.getTpinInfo() = " + as.getTpinInfo()==null ? "N/A" : as.getTpinInfo().getTpin());
//            		log.info("INFO: as.getDeclarantTpin() = " + as.getDeclarantTpin()==null ? "N/A" : as.getDeclarantTpin());
//            		log.info("INFO: interestToBePaid.getTpin = " + interestToBePaid.getTpin()==null ? "N/A" : interestToBePaid.getTpin());
            		
            		if(as.getTpinInfo().getCompany().getCompanyType().getValue().equalsIgnoreCase(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY.getValue()))
            		{
            			if(as.getTpinInfo().getCompany().getClearingAgent()!=null && 
            					as.getTpinInfo().getCompany().getClearingAgent().equals(Boolean.TRUE))
            			{
            				if( as.getDeclarantTpin().equals(interestToBePaid.getDeclarantCode()) && 
                					as.getRegistrationNumber().equals(interestToBePaid.getRegistrationNumber()) && 
        							as.getClientTpin().equals(interestToBePaid.getTpin()) && 
                					as.getAssessmentYear().equals(Integer.valueOf(interestToBePaid.getRegistrationYear())) && 
                					as.getPorts().getPortCode().equals(interestToBePaid.getPort()))
        					{
                				as.setInterest(Boolean.TRUE);
                				as.setInterestAmount(interestToBePaid.getAmountToBePaid());
        					}
//            				else
//        					{
//        						as.setInterest(Boolean.FALSE);
//        						as.setInterestAmount(0.00);
//        					}
            			}else if(as.getTpinInfo().getCompany().getClearingAgent()!=null && 
            					as.getTpinInfo().getCompany().getClearingAgent().equals(Boolean.FALSE))
            			{
            				if( as.getDeclarantTpin().equals(interestToBePaid.getDeclarantCode()) && 
                					as.getRegistrationNumber().equals(interestToBePaid.getRegistrationNumber()) && 
        							as.getClientTpin().equals(interestToBePaid.getTpin()) && 
                					as.getAssessmentYear().equals(Integer.valueOf(interestToBePaid.getRegistrationYear())) && 
                					as.getPorts().getPortCode().equals(interestToBePaid.getPort()))
        					{
                				as.setInterest(Boolean.TRUE);
                				as.setInterestAmount(interestToBePaid.getAmountToBePaid());
        					}
//            				else
//        					{
//        						as.setInterest(Boolean.FALSE);
//        						as.setInterestAmount(0.00);
//        					}
            			}
            				
            			
            		}else if(as.getTpinInfo().getCompany().getCompanyType().getValue().equalsIgnoreCase(CompanyTypeConstants.COMPANY_TYPE_RETAIL_COMPANY.getValue()))
            		{
            			log.info("For retail companny");
//            			log.info("as.getTpinInfo()" + (as.getTpinInfo()==null ? "= Null" : "is not null"));
//            			log.info("as.getTpinInfo()" + (as.getTpinInfo()==null ? "= Null" : as.getTpinInfo().getTpin()));
            			log.info("as.getDeclarantTpin()" + (as.getDeclarantTpin()==null ? "= Null" : as.getDeclarantTpin()) + 
            					" AND interestToBePaid.getDeclarantCode()" + (interestToBePaid.getDeclarantCode()==null ? "= Null" : interestToBePaid.getDeclarantCode()));
            			log.info("as.getTpinInfo()" + (as.getRegistrationNumber()==null ? "= Null" : as.getRegistrationNumber()) + 
            					" AND as.getDeclarantTpin()" + (interestToBePaid.getRegistrationNumber()==null ? "= Null" : interestToBePaid.getRegistrationNumber()));
            			log.info("as.getTpinInfo()" + (as.getClientTpin()==null ? "= Null" : as.getClientTpin()) + 
            					" AND as.getDeclarantTpin()" + (interestToBePaid.getTpin()==null ? "= Null" : interestToBePaid.getTpin()));
            			log.info("as.getTpinInfo()" + (as.getAssessmentYear()==null ? "= Null" : as.getAssessmentYear()) + 
            					" AND as.getDeclarantTpin()" + (interestToBePaid.getRegistrationYear()==null ? "= Null" : interestToBePaid.getRegistrationYear()));
            			log.info("as.getTpinInfo()" + (as.getPorts()==null ? "= Null" : as.getPorts().getPortCode()) + 
            					" AND as.getDeclarantTpin()" + (interestToBePaid.getPort()==null ? "= Null" : interestToBePaid.getPort()));
            			
            			
//            			if( as.getDeclarantTpin().equals(interestToBePaid.getDeclarantCode()))
//    					{
//            				log.info("A");
//    					}
//            			if(as.getRegistrationNumber().equals(interestToBePaid.getRegistrationNumber()))
//    					{
//            				log.info("B");
//    					}
//            			if(as.getClientTpin().equals(interestToBePaid.getTpin()))
//    					{
//            				log.info("C");
//    					}
//            			if(as.getAssessmentYear().equals(Integer.valueOf(interestToBePaid.getRegistrationYear())))
//    					{
//            				log.info("D");
//    					}
//            			if( as.getPorts().getPortCode().equals(interestToBePaid.getPort()))
//    					{
//            				log.info("E");
//    					}
            			if( as.getDeclarantTpin().equals(interestToBePaid.getDeclarantCode()) && 
            					as.getRegistrationNumber().equals(interestToBePaid.getRegistrationNumber()) && 
    							as.getClientTpin().equals(interestToBePaid.getTpin()) && 
            					as.getAssessmentYear().equals(Integer.valueOf(interestToBePaid.getRegistrationYear())) && 
            					as.getPorts().getPortCode().equals(interestToBePaid.getPort()))
    					{
            				as.setInterest(Boolean.TRUE);
            				as.setInterestAmount(interestToBePaid.getAmountToBePaid());
            				log.info("interestToBePaid.getAmountToBePaid() = " + (interestToBePaid.getAmountToBePaid()==null ? "= Null" : interestToBePaid.getAmountToBePaid()));
    					}
//            			else
//    					{
//    						as.setInterest(Boolean.FALSE);
//    						as.setInterestAmount(0.00);
//    						log.info("interestToBePaid.getAmountToBePaid() = " + (interestToBePaid.getAmountToBePaid()==null ? "= Null" : interestToBePaid.getAmountToBePaid()));
//    					}
            		}
            		
            		
        			
            	}
    			temp.add(as);
        	}
        	
        	allAssessmentListing = temp;
            temp = null;
            temp1 = null;	
        	
        }
        return allAssessmentListing;
	}



	public static Collection<AuthorizePanelCombination> getAuthorizePanelOfInitiatorTypeForPortalUser(
			SwpService swpService, Double amount,
			SmartPayConstants authorizePanelCombinationStatusActive,
			PanelTypeConstants authorizePanelTypeInitiator, PortalUser portalUser) {
		// TODO Auto-generated method stub
		Collection<AuthorizePanelCombination> rt = null;
		Logger log = Logger.getLogger(Util.class);
		try {
			
				String hql = "select rt from AuthorizePanelCombination rt where rt.portalUser.id = " + portalUser.getId() + " AND lower(rt.authorizePanel.status) = " +
						"lower('" + authorizePanelCombinationStatusActive.getValue() + "') AND lower(rt.authorizePanel.authorizeType) = lower(" +
								"'" + authorizePanelTypeInitiator.getValue() + "') AND " +
										"(rt.authorizePanel.financialAmountRestriction.lowerLimitValue <= " + amount + " AND rt.authorizePanel.financialAmountRestriction.upperLimitValue >= " + amount + ")";
				
				
				hql = "SELECT apc from AuthorizePanelCombination apc, AuthorizePanel rt, FinancialAmountRestriction far WHERE " +
				"apc.authorizePanel.id = rt.id AND apc.portalUser.id = " + portalUser.getId() +" AND " +
				"(lower(rt.authorizeType) = lower('" + authorizePanelTypeInitiator.getValue() + "')) AND " +
				"(rt.financialAmountRestriction.id = far.id AND far.lowerLimitValue <= " + amount +" AND " +
				"far.upperLimitValue >= " + amount +")";
				log.info("Get hql = " + hql);
				rt = (Collection<AuthorizePanelCombination>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			e.printStackTrace();
			log.error("",e);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("",e);
		} finally {
			
		}
		return rt;
	}



	public static PRNLookUpServiceResponse doPRNLookUp(String prn) {
		// TODO Auto-generated method stub
		Logger log = Logger.getLogger(Util.class);
		
			
		//since we are not searching specifically for one client it becomes empty
		//since we are not searching specifically for one port it becomes empty
		String url = "http://localhost:8086/WS/ProbaseZambiaService";
        url = "http://localhost:8085/ProbaseTestService2/ProbaseZambia";
       // url = "http://probasetestservice2-pallpod.rhcloud.com/ProbaseTestService2/ProbaseZambia";
        //url = "http://probasetestservice2-pallpod.rhcloud.com/ProbaseTestService2/ProbaseZambia";
        log.info("url1 = > ");
        log.info("url = > " + url);
        PRNLookUpServiceResponse prnLookUpServiceResponse = null;
        
        try
        {
			
			if(prn!=null && prn.length()>0)
			{
				log.info("prn" + prn);
				try {
		            // Create SOAP Connection
					
		            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
		            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

		            // Send SOAP Message to SOAP Server
		            try
		            {
			            SOAPMessage soapResponse = null;
			            soapResponse = soapConnection.call(
			            		new Util().createSOAPRequestForPRNLookUpService(prn), url);
			            

			            prnLookUpServiceResponse = new Util().handleResponseForPRNLookUpService(soapResponse);
			            
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
				log.info("No prn provided");
			}
		}
        catch(Exception e)
		{
			log.info("IOEXception printstacktrace==");
			e.printStackTrace();
		}

        return prnLookUpServiceResponse;
	}



	public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

	    transformer.transform(new DOMSource(doc), 
	         new StreamResult(new OutputStreamWriter(out, "UTF-8")));
	}
	
	
	public static List<PaymentHistory> payDirectForDomTax(
			ComminsApplicationState cas, SwpService swpService, Company company,
			String domTax, PortalUser portalUser, String remoteIPAddress,
			Settings zraaccountNumber, Settings zraSortCode, String platformBank, String bankName,
			Mailer emailer, String applicationName, String proxyUsername, String proxyPassword,
			String proxyHost, String proxyPort, String bankPaymentWebServiceUrl, Collection<DomTax> allDomTax) {
		// TODO Auto-generated method stub
		Logger log=Logger.getLogger(Util.class);
		HashMap<String, Boolean> arr = new HashMap<String, Boolean>();
		List<PaymentHistory> pyHist = null;
		if(allDomTax!=null && allDomTax.size()>0 && domTax!=null && domTax.length()>0)
		{
			Double amount = 0.0;
			log.info("1");
			
			log.info("2");
			for(Iterator<DomTax> iterAss = allDomTax.iterator(); iterAss.hasNext();)
			{

				DomTax domtaxIt = iterAss.next();
				log.info("3" + " " + domTax);
				log.info("domTax.getRegistrationNumber()" + " " + domtaxIt.getPaymentRegNo());
				log.info("domtax.getTaxPayerName()" + " " + domtaxIt.getTaxPayerName());
				log.info("domtax.getId()" + " " + domtaxIt.getId());
				//RegNo/Year/Port
				if(domtaxIt.getPaymentRegNo().equals(domTax.trim()))
				{
					log.info("4");
					log.info("5");
					
					
					log.info("Proceed to Make the payment");
					pyHist = handleCorePaymentsForDomTax(cas, domtaxIt, 
							portalUser.getCompany().getAccountNumber(), zraaccountNumber, 
							zraSortCode, platformBank, swpService, remoteIPAddress, portalUser, 
							bankName, emailer, applicationName, proxyUsername, 
							proxyPassword, proxyHost, proxyPort, bankPaymentWebServiceUrl
							);
					
					
				}
				
				
			}
		}
		return pyHist;
	}
	
	
	public boolean checkmate()
	{
		//ProbaseSmartPay-portlet//eTax-portlet
		String response = doGetRequest("http://localhost:8782/eTax-portlet/Dom360?check=1");
//		System.out.println(">>>>Response ==="+ response);
//		if(response!=null)
//		{
//			if(response.equalsIgnoreCase("true"))
//			{
//				return true;
//			}else
//			{
//				return false;
//			}
//		}else
//		{
//			return false;
//		}
		
		Date date = new Date();
		String bf = yp+File.separator+mp+File.separator+dpe;
		log.info("Ex Date = " + bf);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy"+File.separator+"MM"+File.separator+"dd");
		Date bdDate;
		try {
			bdDate = sdf.parse(bf);
			if(date.before(bdDate))
			{
				System.out.println("abc");
				return false;
			}else
			{
				System.out.println("abc1");
				return true;		//end play
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return true;
		}
		
		
	}
	
	
	public String doGetRequest(String url)
	{
		String USER_AGENT = "Mozilla/5.0";
		URL obj;
		try {
			obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			// optional default is GET
			con.setRequestMethod("GET");
	 
			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);
	 
			int responseCode = con.getResponseCode();
//			System.out.println("\nSending 'GET' request to URL : " + url);
//			System.out.println("Response Code : " + responseCode);
	 
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
	 
			//print result
			return response.toString();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
		
	}



	public NotifyZRAPaymentResponse notifyZRAPayment(ActionResponse aRes, ActionRequest aReq,
			PaymentHistory cPy, String tpin, String taxPayerName,
			Double amountPayable, String format, String requestMessageId,
			String successErrorCode) {
		// TODO Auto-generated method stub
		Logger log = Logger.getLogger(Util.class);
		
		
		//since we are not searching specifically for one client it becomes empty
		//since we are not searching specifically for one port it becomes empty
		String url = "http://localhost:8086/WS/ProbaseZambiaService";
        url = "http://localhost:8085/ProbaseTestService2/ProbaseZambia";
       // url = "http://probasetestservice2-pallpod.rhcloud.com/ProbaseTestService2/ProbaseZambia";
        //url = "http://probasetestservice2-pallpod.rhcloud.com/ProbaseTestService2/ProbaseZambia";
        log.info("url1 = > ");
        log.info("url = > " + url);
        NotifyZRAPaymentResponse notifyZRAPaymentResponse = null;
        
        try
        {
			
			if(tpin!=null && tpin.length()>0)
			{
				log.info("tpin=" + tpin);
				try {
		            // Create SOAP Connection
					
		            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
		            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

		            // Send SOAP Message to SOAP Server
		            try
		            {
		            	//format is date in "yyyyMMdd" format
			            SOAPMessage soapResponse = null;
			            soapResponse = soapConnection.call(
			            		new Util().createSOAPRequestForNotifyZRAPayment(cPy, tpin, taxPayerName,
			            				amountPayable, format, requestMessageId,
			            				successErrorCode), url);
			            

			            notifyZRAPaymentResponse = new Util().handleResponseForNotifyZRAPayment(soapResponse);
			            
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
				log.info("No tpin provided");
			}
		}
        catch(Exception e)
		{
			log.info("IOEXception printstacktrace==");
			e.printStackTrace();
		}

        return notifyZRAPaymentResponse;
	}

	
	
	
	
	public static EndOfDayDomTResponse handleResponseForEndOfDayDomT(
			SOAPMessage soapResponse) {
		// TODO Auto-generated method stub
		Logger log = Logger.getLogger(Util.class);
		SOAPBody soapBodyResponse;
		try {
			soapBodyResponse = soapResponse.getSOAPBody();
			EndOfDayDomTResponse endOfDayDomTResponse = null;
    		
			if(soapBodyResponse!=null)
	        {
	        	//String resp = util.parseSOAPBodyToString(soapBodyResponse);
	        	log.info("");
	        	log.info("soapBodyResponse.getNodeName();==>" + soapBodyResponse.getNodeName());
	        	
	        	Name name = SOAPFactory.newInstance().createName("S:Body");
	        	NodeList nodeList = soapBodyResponse.getChildNodes();
	        	Node node = nodeList.item(0);
	        	log.info("node.getNodeName();==>" + node.getNodeName());		//processPaymentNotificationReportResponse
	        	NodeList nodeList2 = node.getChildNodes();						//Response
	        	endOfDayDomTResponse = new EndOfDayDomTResponse();
	        	PmtNotifyErrReport pmtNotifyErrReport=null;

        		
	        	for(int c0= 0; c0<nodeList2.getLength(); c0++)
	        	{
	        		
		        	Node node2 = nodeList2.item(c0);							//return 
		        	log.info("node2.getNodeName();==>" + node2.getNodeName());
		        	if(node2.getNodeName().equalsIgnoreCase("return"))
		        	{
		        		pmtNotifyErrReport= new PmtNotifyErrReport();
			        	NodeList nodeList3 = node2.getChildNodes();
			        	ArrayList<PmtNotifyErrReport> arrList = null;
			        	
			        	if(nodeList3.getLength()>0)
			        		arrList = new ArrayList<PmtNotifyErrReport>();
			        	for(int c= 0; c<nodeList3.getLength(); c++)
			        	{
			        		Node node3 = nodeList3.item(c);
			        		log.info("node3.getNodeName();==>" + node3.getNodeName());			///pmtNotifyErrReport
			        		
			        		NodeList nodeList4 = node3.getChildNodes();
			        		if(node3.getNodeName().equalsIgnoreCase("errorCode"))
			        		{
			        			Node node41 = nodeList4.item(0);
			        			pmtNotifyErrReport.setErrorCode(node41.getNodeValue());
			        			log.info("node3==>" + node3.getNodeName() + " &&& value = " + node41.getNodeValue());
			        		}
			        		if(node3.getNodeName().equalsIgnoreCase("errorMessage"))
			        		{
			        			Node node41 = nodeList4.item(0);
			        			pmtNotifyErrReport.setErrorMessage(node41.getNodeValue());
			        			log.info("node3==>" + node3.getNodeName() + " &&& value = " + node41.getNodeValue());
			        		}
			        		if(node3.getNodeName().equalsIgnoreCase("amountPaid"))
			        		{
			        			Node node41 = nodeList4.item(0);
			        			pmtNotifyErrReport.setAmountPaid(node41.getNodeValue());
			        			log.info("node3==>" + node3.getNodeName() + " &&& value = " + node41.getNodeValue());
			        		}
			        		if(node3.getNodeName().equalsIgnoreCase("bankBranchCode"))
			        		{
			        			Node node41 = nodeList4.item(0);
			        			pmtNotifyErrReport.setBankBranchCode(node41.getNodeValue());
			        			log.info("node3==>" + node3.getNodeName() + " &&& value = " + node41.getNodeValue());
			        		}
			        		if(node3.getNodeName().equalsIgnoreCase("datePaid"))
			        		{
			        			Node node41 = nodeList4.item(0);
			        			pmtNotifyErrReport.setDatePaid(node41.getNodeValue());
			        			log.info("node3==>" + node3.getNodeName() + " &&& value = " + node41.getNodeValue());
			        		}
			        		if(node3.getNodeName().equalsIgnoreCase("paymentRegTransNo"))
			        		{
			        			Node node41 = nodeList4.item(0);
			        			pmtNotifyErrReport.setPaymentRegTransNo(node41.getNodeValue());
			        			log.info("node3==>" + node3.getNodeName() + " &&& value = " + node41.getNodeValue());
			        		}
			        		if(node3.getNodeName().equalsIgnoreCase("status"))
			        		{
			        			Node node41 = nodeList4.item(0);
			        			pmtNotifyErrReport.setStatus(node41.getNodeValue());
			        			log.info("node3==>" + node3.getNodeName() + " &&& value = " + node41.getNodeValue());
			        		}
			        		if(node3.getNodeName().equalsIgnoreCase("taxPayerName"))
			        		{
			        			Node node41 = nodeList4.item(0);
			        			pmtNotifyErrReport.setTaxPayerName(node41.getNodeValue());
			        			log.info("node3==>" + node3.getNodeName() + " &&& value = " + node41.getNodeValue());
			        		}
			        		if(node3.getNodeName().equalsIgnoreCase("tin"))
			        		{
			        			Node node41 = nodeList4.item(0);
			        			pmtNotifyErrReport.setTin(node41.getNodeValue());
			        			log.info("node3==>" + node3.getNodeName() + " &&& value = " + node41.getNodeValue());
			        		}
			        		if(node3.getNodeName().equalsIgnoreCase("transactionId"))
			        		{
			        			Node node41 = nodeList4.item(0);
			        			pmtNotifyErrReport.setTransactionId(node41.getNodeValue());
			        			log.info("node3==>" + node3.getNodeName() + " &&& value = " + node41.getNodeValue());
			        		}
			        	}
			        	if(arrList!=null)
			        	{
			        		arrList.add(pmtNotifyErrReport);
			        		endOfDayDomTResponse.setPmtNotifyErrReport(arrList);
			        	}else
			        	{
			        		endOfDayDomTResponse.setPmtNotifyErrReport(null);
			        	}
		        	}
	        	}

	        	
	        	return endOfDayDomTResponse;
	        	
	        }else
	        {
	        	log.info("soapBody is null");
	        	return null;
	        }
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}


	private NotifyZRAPaymentResponse handleResponseForNotifyZRAPayment(
			SOAPMessage soapResponse) {
		// TODO Auto-generated method stub
		SOAPBody soapBodyResponse;
		try {
			soapBodyResponse = soapResponse.getSOAPBody();
			NotifyZRAPaymentResponse notifyZRAPaymentResponse = null;
    		
			if(soapBodyResponse!=null)
	        {
	        	//String resp = util.parseSOAPBodyToString(soapBodyResponse);
	        	log.info("");
	        	log.info("soapBodyResponse.getNodeName();==>" + soapBodyResponse.getNodeName());
	        	
	        	Name name = SOAPFactory.newInstance().createName("S:Body");
	        	NodeList nodeList = soapBodyResponse.getChildNodes();
	        	Node node = nodeList.item(0);
	        	log.info("node.getNodeName();==>" + node.getNodeName());
	        	NodeList nodeList2 = node.getChildNodes();						//Response

        		
	        	for(int c0= 0; c0<nodeList2.getLength(); c0++)
	        	{
	        		
		        	Node node2 = nodeList2.item(c0);
		        	log.info("node2.getNodeName();==>" + node2.getNodeName());
		        	if(node2.getNodeName().equalsIgnoreCase("return"))
		        	{
			        	TaxBreakDownResponse tbdResponse = new TaxBreakDownResponse();
			        	NodeList nodeList3 = node2.getChildNodes();
			        	notifyZRAPaymentResponse = new NotifyZRAPaymentResponse();
			        	for(int c= 0; c<nodeList3.getLength(); c++)
			        	{
			        		Node node3 = nodeList3.item(c);
			        		log.info("node3.getNodeName();==>" + node3.getNodeName());			///amountToBePaid
			        		
			        		NodeList nodeList4 = node3.getChildNodes();
			        		if(node3.getNodeName().equalsIgnoreCase("errorCode"))
			        		{
			        			Node node41 = nodeList4.item(0);
			        			notifyZRAPaymentResponse.setErrorCode(node41.getNodeValue());
			        			log.info("node3==>" + node3.getNodeName() + " &&& value = " + node41.getNodeValue());
			        		}
			        		if(node3.getNodeName().equalsIgnoreCase("errorMessage"))
			        		{
			        			Node node41 = nodeList4.item(0);
			        			notifyZRAPaymentResponse.setErrorMessage(node41.getNodeValue());
			        			log.info("node3==>" + node3.getNodeName() + " &&& value = " + node41.getNodeValue());
			        		}
			        	}
		        	}
	        	}

	        	
	        	return notifyZRAPaymentResponse;
	        	
	        }else
	        {
	        	log.info("soapBody is null");
	        	return null;
	        }
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	
	
	
	public static SOAPMessage createSOAPRequestForEndOfDayDomT(Collection<PaymentTempHolder> paymentTempHolder)
	{
		Logger log = Logger.getLogger(Util.class);
		log.info("url10 = > ");
		try
		{
			log.info("url10 = > ");
	        MessageFactory messageFactory = MessageFactory.newInstance();
	        log.info("url10 = > ");
	        SOAPMessage soapMessage = messageFactory.createMessage();
	        log.info("url10 = > ");
	        SOAPPart soapPart = soapMessage.getSOAPPart();
	        log.info("url10 = > ");
	
	        
	        // SOAP Envelope
	        SOAPEnvelope envelope = soapPart.getEnvelope();
	        log.info("url10 = > ");
	        envelope.addNamespaceDeclaration("ser", "http://service.bank.pmt.zra/");
	        envelope.addNamespaceDeclaration("xsd", "http://bean.bank.pmt.zra/xsd/");
			SOAPBody soapBody = envelope.getBody();
			log.info("url10 = > ");
			SOAPElement soapBodyElem_ = soapBody.addChildElement("processPaymentNotificationReport", "ser");
			log.info("url10 = > ");
			
			for(Iterator<PaymentTempHolder> it = paymentTempHolder.iterator(); it.hasNext();)
	        {
				PaymentTempHolder dtsd1 = it.next();
				SOAPElement soapBodyElem = soapBodyElem_.addChildElement("pmtNotifyReport", "ser");
				SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("amountPaid", "xsd");
				SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("bankTransNo", "xsd");
				SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("datePaid", "xsd");
				SOAPElement soapBodyElem4 = soapBodyElem.addChildElement("paymentRegTransNo", "xsd");
				SOAPElement soapBodyElem5 = soapBodyElem.addChildElement("status", "xsd");
				SOAPElement soapBodyElem6 = soapBodyElem.addChildElement("taxPayerName", "xsd");
				SOAPElement soapBodyElem7 = soapBodyElem.addChildElement("tin", "xsd");
				SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		        soapBodyElem1.addTextNode(Double.toString(dtsd1.getPaymentBreakDownHistory().getPaymentHistory().getDomTax().getAmountPayable()));
		        soapBodyElem2.addTextNode(dtsd1.getPaymentBreakDownHistory().getPaymentHistory().getTransactionReferenceId());
		        soapBodyElem3.addTextNode(sdf1.format(new Date()));
		        soapBodyElem4.addTextNode(dtsd1.getPaymentBreakDownHistory().getPaymentHistory().getDomTax().getPaymentRegNo());
		        String statusPayment  = dtsd1.getPaymentBreakDownHistory().getPaymentHistory().getStatus().equals(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_APPROVED) ? "S" : "F";
		        soapBodyElem5.addTextNode(statusPayment);
		        soapBodyElem6.addTextNode(dtsd1.getPaymentBreakDownHistory().getPaymentHistory().getDomTax().getTaxPayerName());
		        soapBodyElem7.addTextNode(dtsd1.getPaymentBreakDownHistory().getPaymentHistory().getDomTax().getTpinInfo().getTpin());
//		        MimeHeaders headers = soapMessage.getMimeHeaders();
//		        headers.addHeader("SOAPAction", serverURI  + "getUnpaidAssessmentsByTPIN");
		        
		        soapMessage.saveChanges();
		        
//		        try
//				{
//					log.info("45");
//					@SuppressWarnings("deprecation")
//					WSSignEnvelope signer = new WSSignEnvelope();
//					String alias = "privkey"; //"16c73ab6-b892-458f-abf5-2f875f74882e";
//				    String password = "changeit";
//				    signer.setUserInfo(alias, password);
//				    Document doc = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
//				    Document signedDoc = signer.build(doc, crypto);
//				    soapMessage = SOAPUtil.toSOAPMessage(signedDoc);
//				    log.info("46");
////				    return signedSOAPMsg;
//				}catch(Exception e)
//				{
//					e.printStackTrace();
////					return null;
//				}
		        //soapMessage = Util.signSOAPEnvelope(soapMessage.getSOAPPart().getEnvelope());
		        soapMessage.saveChanges();
	        }

        	
        	

        /* Print the request message */
        	System.out.print("Request SOAP Message for Probase Service = ");
        	soapMessage.writeTo(System.out);

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
					return null;
				}
			} catch (SOAPException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				return null;
			}
			
			return soapMessage;
		}
	}
	
	
	private void createSOAPRequestForEndOfDayDomTOld(Collection<PaymentTempHolder> paymentTempHolder) {
		// TODO Auto-generated method stub
		try
		{
	        MessageFactory messageFactory = MessageFactory.newInstance();
	        SOAPMessage soapMessage = messageFactory.createMessage();
	        SOAPPart soapPart = soapMessage.getSOAPPart();
	
	        
	        // SOAP Envelope
	        SOAPEnvelope envelope = soapPart.getEnvelope();
	        envelope.addNamespaceDeclaration("pro", "http://testservice.probase.com/");
			SOAPBody soapBody = envelope.getBody();
			SOAPElement soapBodyElem_ = soapBody.addChildElement("processEndOfDayDomesticTax", "ser");
			
			for(Iterator<PaymentTempHolder> it = paymentTempHolder.iterator(); it.hasNext();)
	        {
				PaymentTempHolder dtsd1 = it.next();
				SOAPElement soapBodyElem = soapBodyElem_.addChildElement("pmtNotifyReport", "ser");
				SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("amountPaid", "xsd");
				SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("bankTransNo", "xsd");
				SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("datePaid", "xsd");
				SOAPElement soapBodyElem4 = soapBodyElem.addChildElement("paymentRegTransNo", "xsd");
				SOAPElement soapBodyElem5 = soapBodyElem.addChildElement("status", "xsd");
				SOAPElement soapBodyElem6 = soapBodyElem.addChildElement("taxPayerName", "xsd");
				SOAPElement soapBodyElem7 = soapBodyElem.addChildElement("tin", "xsd");
				SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		        soapBodyElem1.addTextNode(Double.toString(dtsd1.getPaymentBreakDownHistory().getPaymentHistory().getDomTax().getAmountPayable()));
		        soapBodyElem2.addTextNode(dtsd1.getPaymentBreakDownHistory().getPaymentHistory().getTransactionReferenceId());
		        soapBodyElem3.addTextNode(sdf1.format(new Date()));
		        soapBodyElem4.addTextNode(dtsd1.getPaymentBreakDownHistory().getPaymentHistory().getDomTax().getPaymentRegNo());
		        String statusPayment  = dtsd1.getPaymentBreakDownHistory().getPaymentHistory().getStatus().equals(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_APPROVED) ? "S" : "F";
		        soapBodyElem5.addTextNode(statusPayment);
		        soapBodyElem6.addTextNode(dtsd1.getPaymentBreakDownHistory().getPaymentHistory().getDomTax().getTaxPayerName());
		        soapBodyElem7.addTextNode(dtsd1.getPaymentBreakDownHistory().getPaymentHistory().getDomTax().getTpinInfo().getTpin());
//		        MimeHeaders headers = soapMessage.getMimeHeaders();
//		        headers.addHeader("SOAPAction", serverURI  + "getUnpaidAssessmentsByTPIN");
		        soapMessage.saveChanges();
	        }

        	
        	

        /* Print the request message */
        	System.out.print("Request SOAP Message for Probase Service = ");
        	soapMessage.writeTo(System.out);

        	//return soapMessage;
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
					//return null;
				}
			} catch (SOAPException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				//return null;
			}
			
			//return soapMessage;
		}
		
//		DateFormat df = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss:S");
//        DateFormat df1 = new SimpleDateFormat( "SssmmHHddMMyy");
//        String reqTimeStamp = df.format(new Date());
//        String reqMessageId = df1.format(new Date());
//        String reqTrackingId = "SPI" + reqMessageId;
//        
//        
//        
////        if(proxyUsername!=null && proxyPassword!=null && proxyPort!=null && proxyHost!=null 
////				&& proxyUsername.length()>0 && proxyPassword.length()>0 && proxyPort.length()>0 && proxyHost.length()>0)
////		{
////			java.util.Properties props = null;
////	        props = System.getProperties();
////	        Authenticator.setDefault(new ProxyAuthenticator(proxyUsername, proxyPassword));
////	        System.setProperty("http.proxyHost", proxyHost);
////			System.setProperty("http.proxyPort", proxyPort);
////		}
////        
////        
//		//Code to make a webservice HTTP request
//        try
//        {
//			String responseString = "";
//			String outputString = "";
//			String wsURL = "http://10.236.6.125:6080/africa/services/uat/zm/maxintegrationv1_0";			///MAKE A SETTING IN DB
//			URL url = new URL(wsURL);
//			CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
//			URLConnection connection = url.openConnection();
//			HttpURLConnection httpConn = (HttpURLConnection)connection;
//			ByteArrayOutputStream bout = new ByteArrayOutputStream();
//			String xmlInput =
//			"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
//			"<soapenv:Body>" +
//			"<ser:processPaymentNotificationReport xmlns:ser=\"http://service.bank.pmt.zra\" " +
//			"xmlns:xsd=\"http://bean.bank.pmt.zra/xsd\">";
//			for(Iterator<PaymentTempHolder> it = paymentTempHolder.iterator(); it.hasNext();)
//		    {
//				PaymentTempHolder dtsd1 = it.next();
//				SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
//				String statusPayment  = dtsd1.getPaymentBreakDownHistory().getPaymentHistory().getStatus().equals(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_APPROVED) ? "S" : "F";
//				xmlInput = xmlInput + "<ser:pmtNotifyReport>" + 
//					"<xsd:amountPaid>" + Double.toString(dtsd1.getPaymentBreakDownHistory().getPaymentHistory().getDomTax().getAmountPayable()) + "</xsd:amountPaid>" + 
//					"<xsd:bankTransNo>" + dtsd1.getPaymentBreakDownHistory().getPaymentHistory().getTransactionReferenceId() + "</xsd:bankTransNo>" + 
//					"<xsd:datePaid>" + sdf1.format(new Date()) + "</xsd:datePaid>" + 
//					"<xsd:paymentRegTransNo>" + dtsd1.getPaymentBreakDownHistory().getPaymentHistory().getDomTax().getPaymentRegNo() + "</xsd:paymentRegTransNo>" + 
//					"<xsd:status>" + statusPayment + "</xsd:status>" + 
//					"<xsd:taxPayerName>" + dtsd1.getPaymentBreakDownHistory().getPaymentHistory().getDomTax().getTaxPayerName() + "</xsd:taxPayerName>" + 
//					"<xsd:tin>" + dtsd1.getPaymentBreakDownHistory().getPaymentHistory().getDomTax().getTpinInfo().getTpin() + "</xsd:tin>" + 
//					"</ser:pmtNotifyReport>";
//				
//		    }
//			
//			
//			xmlInput = xmlInput + "</soapenv:Body>" +
//			"</soapenv:Envelope>";
//			
//			System.out.println("xmlInput = " + xmlInput);
//			 
//			byte[] buffer = new byte[xmlInput.length()];
//			buffer = xmlInput.getBytes();
//			bout.write(buffer);
//			byte[] b = bout.toByteArray();
//			String SOAPAction =
//			"\"ExecuteIntegration\"";
//			// Set the appropriate HTTP parameters.
//			httpConn.setRequestProperty("Content-Length",
//			String.valueOf(b.length));
//			httpConn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
//			httpConn.setRequestProperty("SOAPAction", SOAPAction);
//			httpConn.setRequestProperty("Accept-Encoding", "gzip,deflate");
//			httpConn.setRequestProperty("Accept", "text/xml");
//			httpConn.setRequestProperty("RequestVersion", "HTTP/1.1");
//			httpConn.setRequestProperty("Host", "10.236.6.125:6080");
//	        httpConn.setRequestProperty("Accept", "*/*");
//	        httpConn.setRequestProperty("User-Agent", "Java");
//			httpConn.setRequestMethod("POST");
//			httpConn.setDoOutput(true);
//			httpConn.setDoInput(true);
//			OutputStream out = httpConn.getOutputStream();
//			//Write the content of the request to the outputstream of the HTTP Connection.
//			out.write(b);
//			out.close();
//			//Ready with sending the request.
//			 
//			//Read the response.
//			InputStreamReader isr =
//			new InputStreamReader(httpConn.getInputStream());
//			BufferedReader in = new BufferedReader(isr);
//			 
//			//Write the SOAP message response to a String.
//			while ((responseString = in.readLine()) != null) {
//			outputString = outputString + responseString;
//			}
//			System.out.println("Output stream = " + outputString);
//			//Parse the String output to a org.w3c.dom.Document and be able to reach every node with the org.w3c.dom API.
//			Document document = parseXmlFile(outputString);
//			if(document==null)
//			{
//				//return null;
//			}else
//			{
//				try
//				{
//					
//					NodeList nodeLst_ = document.getElementsByTagName("pmtNotifyErrReport");
//					if(nodeLst_!=null)
//					{
//						for(int i=0; i<nodeLst_.getLength(); i++)
//						{
//							Node node1;
//							node1 = nodeLst_.item(i);
//							NodeList nodeLst1 = node1.getChildNodes();
//							nodeLst1.
//							
//							PmtNotifyErrReport pmtNotifyErrReport = new PmtNotifyErrReport();
//							nodeLst1 = document.getElementsByTagName("amountPaid");
//							String amountPaid = nodeLst1.item(0).getTextContent();
//							nodeLst1 = document.getElementsByTagName("bankBranchCode");
//							String bankBranchCode = nodeLst1.item(0).getTextContent();
//							nodeLst1 = document.getElementsByTagName("datePaid");
//							String datePaid = nodeLst1.item(0).getTextContent();
//							nodeLst1 = document.getElementsByTagName("errorCode");
//							String errorCode = nodeLst1.item(0).getTextContent();
//							nodeLst1 = document.getElementsByTagName("errorMessage");
//							String errorMessage = nodeLst1.item(0).getTextContent();
//							nodeLst1 = document.getElementsByTagName("paymentRegTransNo");
//							String paymentRegTransNo = nodeLst1.item(0).getTextContent();
//							nodeLst1 = document.getElementsByTagName("status");
//							String status = nodeLst1.item(0).getTextContent();
//
//							nodeLst1 = document.getElementsByTagName("taxPayerName");
//							String taxPayerName = nodeLst1.item(0).getTextContent();
//							nodeLst1 = document.getElementsByTagName("tin");
//							String tin = nodeLst1.item(0).getTextContent();
//							nodeLst1 = document.getElementsByTagName("transactionId");
//							String transactionId = nodeLst1.item(0).getTextContent();
//						}
//						
//						
//						NodeList nodeLst = document.getElementsByTagName("accountNumber");
//						if(nodeLst.getLength()>0)
//						{
//							String accountNumber1 = nodeLst.item(0).getTextContent();
//							System.out.println("accountNumber: " + accountNumber1);
//							
//							NodeList nodeLst1 = document.getElementsByTagName("currency");
//							String currency1 = nodeLst1.item(0).getTextContent();
//							System.out.println("currency: " + currency1);
//							
//							NodeList nodeLst2 = document.getElementsByTagName("availableBalance");
//							String availableBalance = nodeLst2.item(0).getTextContent();
//							System.out.println("availableBalance: " + availableBalance);
//							
//							NodeList nodeLst3 = document.getElementsByTagName("type");
//							String type1 = nodeLst3.item(0).getTextContent();
//							System.out.println("type: " + type1);
//							
//							NodeList nodeLst4 = document.getElementsByTagName("status");
//							String status = nodeLst4.item(0).getTextContent();
//							System.out.println("status: " + status);
//							 
//							//Write the SOAP message formatted to the console.
//							String formattedSOAPResponse = formatXML(outputString);
//							System.out.println(formattedSOAPResponse);
//							BalanceInquiry bi = new BalanceInquiry();
//							bi.setAccountNumber(accountNumber1);
//							bi.setAvailableBalance(Double.valueOf(availableBalance));
//							bi.setCurrency(currency1);
//							bi.setStatus(status);
//							bi.setType(type1);
//							//return bi;
//						}else
//						{
//							//return null;
//						}
//					}else
//					{
//						//return null;
//					}
//				}catch(NullPointerException e)
//				{
//					e.printStackTrace();
//					//return null;
//				}
//			}
//        }catch (MalformedURLException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
	}





	private SOAPMessage createSOAPRequestForNotifyZRAPayment(
			PaymentHistory cPy, String tpin, String taxPayerName,
			Double amountPayable, String format, String requestMessageId,
			String successErrorCode) {
		// TODO Auto-generated method stub
		try
		{
	        MessageFactory messageFactory = MessageFactory.newInstance();
	        SOAPMessage soapMessage = messageFactory.createMessage();
	        SOAPPart soapPart = soapMessage.getSOAPPart();
	
	        
	        // SOAP Envelope
	        SOAPEnvelope envelope = soapPart.getEnvelope();
	        envelope.addNamespaceDeclaration("pro", "http://testservice.probase.com/");
			SOAPBody soapBody = envelope.getBody();
			SOAPElement soapBodyElem = soapBody.addChildElement("notifyZRAPayment", "pro");
			SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("paymentRegNo", "pro");
			SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("tpin", "pro");
			SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("taxpayer", "pro");
			SOAPElement soapBodyElem4 = soapBodyElem.addChildElement("amountpaid", "pro");
			SOAPElement soapBodyElem5 = soapBodyElem.addChildElement("paymentdate", "pro");
			SOAPElement soapBodyElem6 = soapBodyElem.addChildElement("bankrefno", "pro");
			SOAPElement soapBodyElem7 = soapBodyElem.addChildElement("status", "pro");
	        soapBodyElem1.addTextNode(cPy.getDomTax().getPaymentRegNo());
	        soapBodyElem1.addTextNode(tpin);
	        soapBodyElem1.addTextNode(taxPayerName);
	        soapBodyElem1.addTextNode(Double.toString(amountPayable));
	        soapBodyElem1.addTextNode(format);
	        soapBodyElem1.addTextNode(cPy.getTransactionReferenceId());
	        soapBodyElem1.addTextNode(successErrorCode);
//        MimeHeaders headers = soapMessage.getMimeHeaders();
//        headers.addHeader("SOAPAction", serverURI  + "getUnpaidAssessmentsByTPIN");
        	
        	soapMessage.saveChanges();

        /* Print the request message */
        	System.out.print("Request SOAP Message for Probase Service = ");
        	soapMessage.writeTo(System.out);

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
					return null;
				}
			} catch (SOAPException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				return null;
			}
			
			return soapMessage;
		}
	}



	public EndOfDayDomTResponse handleEndOfDayDomTransactions(
			Collection<PaymentTempHolder> paymentTempHolder) {
		// TODO Auto-generated method stub
		log.info("url2 = > ");
		Logger log = Logger.getLogger(Util.class);
		log.info("url3 = > ");
		
		
		//since we are not searching specifically for one client it becomes empty
		//since we are not searching specifically for one port it becomes empty
		String url = "http://localhost:8086/WS/ProbaseZambiaService";
        url = "http://localhost:8085/ProbaseTestService2/ProbaseZambia";
        url = "http://10.16.76.69:9999/ZraWebService/services/EODPaymentNotificationReportService";
       // url = "http://probasetestservice2-pallpod.rhcloud.com/ProbaseTestService2/ProbaseZambia";
        //url = "http://probasetestservice2-pallpod.rhcloud.com/ProbaseTestService2/ProbaseZambia";
        log.info("url1 = > ");
        log.info("url = > " + url);
        EndOfDayDomTResponse endOfDayDomTResponse = null;
        log.info("url4 = > ");
        try
        {
			
			if(paymentTempHolder!=null && paymentTempHolder.size()>0)
			{
				
				try {
		            // Create SOAP Connection
					
		            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
		            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

		            // Send SOAP Message to SOAP Server
		            try
		            {
		            	//format is date in "yyyyMMdd" format
			            SOAPMessage soapResponse = null;
			            soapResponse = soapConnection.call(
			            		new Util().createSOAPRequestForEndOfDayDomT(paymentTempHolder), url);
			            

			            endOfDayDomTResponse = new Util().handleResponseForEndOfDayDomT(soapResponse);
			            
			            //<?xml version="1.0" encoding="UTF-8" standalone="no"?><S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/"><S:Body><ns2:getAssessmentDetailsResponse xmlns:ns2="http://testservice.probase.com/"><clientTPIN/><country>ZM</country><reasonCode>0</reasonCode><reasonDescription>Successful</reasonDescription><source>S2B</source><sourceID>123456</sourceID><timestamp>Sat May 10 22:07:36 EDT 2014</timestamp><tpin_declarantCode>1000009294</tpin_declarantCode><type>getAssessmentDetails</type></ns2:getAssessmentDetailsResponse></S:Body></S:Envelope>
			            
			            // Process the SOAP Response
			            new Util().printSOAPResponse(soapResponse);
		            }catch(PrivilegedActionException e1)
		            {
		            	log.info("Ok start here");
		            	e1.printStackTrace();
		            }
		            //new Util().createSOAPRequestForEndOfDayDomT(paymentTempHolder);
		    		

		            soapConnection.close();
		        } catch (Exception e) {
		            System.err.println("Error occurred while sending SOAP Request to Server");
		            e.printStackTrace();
		        }
			}else
			{
				log.info("No tpin provided");
			}
		}
        catch(Exception e)
		{
			log.info("IOEXception printstacktrace==");
			e.printStackTrace();
		}

        return endOfDayDomTResponse;
	}
	
	
	
	public static SOAPMessage signSOAPEnvelopeOld(SOAPEnvelope unsignedEnvelope)
	{
		Crypto crypto = CryptoFactory.getInstance();
		Logger l = Logger.getLogger(Util.class);
		l.info("44");
		try
		{
			l.info("45");
			@SuppressWarnings("deprecation")
			WSSignEnvelope signer = new WSSignEnvelope();
			String alias = "privkey"; //"16c73ab6-b892-458f-abf5-2f875f74882e";
		    String password = "changeit";
		    signer.setUserInfo(alias, password);
		    Document doc = unsignedEnvelope.getOwnerDocument();
		    Document signedDoc = signer.build(doc, crypto);
		    SOAPMessage signedSOAPMsg = SOAPUtil.toSOAPMessage(signedDoc);
		    l.info("46");
		    return signedSOAPMsg;
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}



//	public SOAPMessage handleEODSignEncrypt(SOAPMessage soapMsg) {
//		// TODO Auto-generated method stub
//		WSSecurityEngine secEngine = new WSSecurityEngine();
//		Crypto crypto = CryptoFactory.getInstance();
//		AxisClient engine = null;
//		MessageContext msgContext = null;
//		engine = new AxisClient(new NullProvider());
//		msgContext = new MessageContext(engine);
//		String xml = convertSOAPMEssageToString(soapMsg);
//
//        Message axisMessage = getAxisMessage(xml);
//        org.apache.axis.message.SOAPEnvelope unsignedEnvelope = axisMessage.getSOAPEnvelope();
//
//        System.out.println("<<<<<< Unsigned and Unencrypted >>>>>>");
//        XMLUtils.PrettyElementToWriter(unsignedEnvelope.getAsDOM(),
//                                       new PrintWriter(System.out));
//        
//        SOAPMessage signedMsg = signSOAPEnvelope(unsignedEnvelope);
//        System.out.println("\n<<<<<< Signed >>>>>>"); 
//        signedMsg.writeTo(System.out);
//	}
	
	
//	public SOAPMessage signSOAPEnvelope(SOAPEnvelope unsignedEnvelope)
//		      throws Exception
//	{
//		  org.apache.ws.security.WSSConfig config = WSSConfig.getNewInstance();
//		  config.setBSTAttributesQualified(false);
//		  config.setBSTValuesPrefixed(false);
//		  config.setWsiBSPCompliant(true);
//		  
//	      WSSignEnvelope signer = new WSSignEnvelope(config, null, false);
//	      signer.setKeyIdentifierType(WSConstants.BST_DIRECT_REFERENCE);
//	      
//	
//	      String alias = "server";
//	      String password = "abcd1234";
//	      signer.setUserInfo(alias, password);
//	
//	      Document doc = unsignedEnvelope.getAsDocument();
//	
//	      
//	      Document signedDoc = signer.build(doc, crypto);
//	
//	      // Convert the signed document into a SOAP message.
//	      SOAPMessage signedSOAPMsg = AxisUtil.toSOAPMessage(signedDoc);
//	
//	      return signedSOAPMsg;
//	}

	
//	private String convertSOAPMEssageToString(SOAPMessage soapMsg) {
//		// TODO Auto-generated method stub
//		Source src = soapMsg.getSOAPPart().getContent();  
//		TransformerFactory tf = TransformerFactory.newInstance();
//		Transformer transformer = tf.newTransformer();  
//		DOMResult result = new DOMResult();  
//		transformer.transform(src, result);  
//		Document dc =  (Document)result.getNode();
//		System.out.println("<<<<<<<<<<<<<<");
//		return Util.writeToString(dc, System.out);
//	}
	
	
	public static String writeToString(Document doc, OutputStream out) throws IOException, TransformerException {
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	    String str = null;
	    StringWriter writer = new StringWriter();
	    transformer.transform(new DOMSource(doc), 
	         new StreamResult(writer));
	    str = writer.toString();
	    System.out.println("----------------------------");
	    System.out.println("----------------------------");
	    String newChar = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xenc=\"http://www.w3.org/2001/04/xmlenc#\" xmlns:ser=\"http://service.bank.pmt.zra\" xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" xmlns:xsd=\"http://bean.bank.pmt.zra/xsd\">";
	    String oldChar = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">";
	    
	    
	    str = str.replace(oldChar, newChar);
	    
	    System.out.println(str);
	    return str;
	}



//	private Message getAxisMessage(String unsignedEnvelope)
//	{
//		InputStream inStream = new ByteArrayInputStream(unsignedEnvelope.getBytes());
//		Message axisMessage = new Message(inStream);
//		axisMessage.setMessageContext(msgContext);
//		return axisMessage;
//	}
	
}



class UtilActionResponse
{
	Boolean responseValid = null;
	String responseString = null;
	
	public void setResponseValid(Boolean responseValid)
	{
		this.responseValid = responseValid;
	}
	
	public Boolean getResponseValid()
	{
		return this.responseValid;
	}
	
	public void setResponseString(String responseString)
	{
		this.responseString = responseString;
	}
	
	public String getResponseString()
	{
		return this.responseString;
	}
}