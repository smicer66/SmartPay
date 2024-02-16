package com.probase.smartpay.admin.settingsmanagement;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PrivilegedActionException;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import smartpay.audittrail.AuditTrail;
import smartpay.entity.ApprovalFlowTransit;
import smartpay.entity.AuthorizePanel;
import smartpay.entity.AuthorizePanelCombination;
import smartpay.entity.BankBranches;
import smartpay.entity.Company;
import smartpay.entity.FeeDescription;
import smartpay.entity.FinancialAmountRestriction;
import smartpay.entity.PaymentBreakDownHistory;
import smartpay.entity.PaymentHistory;
import smartpay.entity.PaymentTempHolder;
import smartpay.entity.PortalUser;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.TaxType;
import smartpay.entity.TaxTypeAccount;
import smartpay.entity.enumerations.ActionTypeConstants;
import smartpay.entity.enumerations.CompanyStatusConstants;
import smartpay.entity.enumerations.PaymentBreakDownHistoryConstants;
import smartpay.entity.enumerations.PaymentHistoryConstants;
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
import com.probase.smartpay.admin.settingsmanagement.SettingsManagementPortlet;
import com.probase.smartpay.admin.settingsmanagement.SettingsManagementPortletState;
import com.probase.smartpay.admin.settingsmanagement.SettingsManagementPortletUtil;
import com.probase.smartpay.admin.settingsmanagement.SettingsManagementPortletState.NAVIGATE;
import com.probase.smartpay.admin.settingsmanagement.SettingsManagementPortletState.SETTINGS;
import com.probase.smartpay.admin.settingsmanagement.SettingsManagementPortletState.SETTINGS_VIEW;
import com.probase.smartpay.admin.settingsmanagement.SettingsManagementPortletState.VIEW_TABS;
import com.probase.smartpay.commins.CronScheduler;
import com.probase.smartpay.commins.CronScheduler.ACTION;
import com.probase.smartpay.commins.ComminsApplicationState;
import com.probase.smartpay.commins.EndOfDayDomTResponse;
import com.probase.smartpay.commins.FundsTransferResponse;
import com.probase.smartpay.commins.Mailer;
import com.probase.smartpay.commins.ProbaseConstants;
import com.probase.smartpay.commins.ProxyAuthenticator;
import com.probase.smartpay.commins.SendSms;
import com.probase.smartpay.commins.Util;
import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Portlet implementation class SettingsManagementPortlet
 */
public class SettingsManagementPortlet extends MVCPortlet {
	private Logger log = Logger.getLogger(SettingsManagementPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
	SettingsManagementPortletUtil util = SettingsManagementPortletUtil.getInstance();
	ServiceContext serviceContext = new ServiceContext();
	public Boolean mode = Boolean.TRUE;
	
	
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
		SettingsManagementPortletState portletState = 
				SettingsManagementPortletState.getInstance(renderRequest, renderResponse);

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
		
		SettingsManagementPortletState portletState = SettingsManagementPortletState.getInstance(aReq, aRes);
		
		
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
        if(action.equalsIgnoreCase(SETTINGS.UPDATE_SETTINGS.name()))
        {
        	handleUpdateSettings(aReq, aRes, portletState);
        }if(action.equalsIgnoreCase(SETTINGS.MANAGE_JOBS_EOD.name()))
        {
        	handleEOD(aReq, aRes, portletState);
        }if(action.equalsIgnoreCase(SETTINGS.MANAGE_JOBS_PAY_SPLIT.name()))
        {
        	handleSplitter(aReq, aRes, portletState);
        }
        
        if(action.equalsIgnoreCase(SETTINGS.MANAGE_ONE_PAYMENT_BREAKDOWN_HISTORY.name()))
        {
        	manageOnePaymentBreakdownHistory(aReq, aRes, portletState);
        }
        if(action.equalsIgnoreCase(SETTINGS.GO_TO_UPDATE_SETTINGS_INTERFACE.name()))
        {
        	portletState.reinitializeForSettings(portletState);
        	portletState.loadSettings(portletState);
        	if(portletState.getPrimaryFeeSetting()!=null)
        	{
        		aRes.setRenderParameter("jspPage", "/html/settingsmanagementportlet/settings/managesettings.jsp");
        		portletState.setCurrentTab(VIEW_TABS.MANAGE_SETTINGS);
        	}
        	else
        	{
        		if(portletState.getPrimaryFeeSetting()==null)
        		{
        			portletState.addError(aReq, "No Primary Fee Charge has been set on the platform. Please set one first before you can proceed", portletState);
        		}
        	}
        }
        if(action.equalsIgnoreCase(SETTINGS_VIEW.MANAGE_SETTINGS.name()))
        {
        	portletState.reinitializeForSettings(portletState);
        	portletState.loadSettings(portletState);
        	if(portletState.getPrimaryFeeSetting()!=null)
        	{
        		aRes.setRenderParameter("jspPage", "/html/settingsmanagementportlet/settings/managesettings.jsp");
        		portletState.setCurrentTab(VIEW_TABS.MANAGE_SETTINGS);
        	}
        	else
        	{
        		if(portletState.getPrimaryFeeSetting()==null)
        		{
        			portletState.addError(aReq, "No Primary Fee Charge has been set on the platform. Please set one first before you can proceed", portletState);
        		}
        	}
        	portletState.setCurrentTab(VIEW_TABS.MANAGE_SETTINGS);
        }if(action.equalsIgnoreCase(SETTINGS_VIEW.VIEW_SETTINGS.name()))
        {
        	aRes.setRenderParameter("jspPage", "/html/settingsmanagementportlet/settings/viewsettings.jsp");
        	portletState.setCurrentTab(VIEW_TABS.VIEW_SETTINGS);
        }if(action.equalsIgnoreCase(SETTINGS_VIEW.MANAGE_JOBS.name()))
        {
        	aRes.setRenderParameter("jspPage", "/html/settingsmanagementportlet/settings/manageschedulers.jsp");
        	portletState.setCurrentTab(VIEW_TABS.MANAGE_JOBS);
        }
        if(action.equalsIgnoreCase(SETTINGS_VIEW.MANAGE_JOBS.name()))
        {
        	aRes.setRenderParameter("jspPage", "/html/settingsmanagementportlet/settings/manageschedulers.jsp");
        	portletState.setCurrentTab(VIEW_TABS.MANAGE_JOBS);
        }
        if(action.equalsIgnoreCase(SETTINGS_VIEW.VIEW_PAYMENT_BREAKDOWN.name()))
        {
        	portletState.setCurrentTab(VIEW_TABS.VIEW_PAYMENT_BREAKDOWN);
        	Collection<PaymentBreakDownHistory> paymentBreakDownHistory = (Collection<PaymentBreakDownHistory>) swpService.getAllRecords(PaymentBreakDownHistory.class);
			portletState.setPaymentBreakdownHistoryListing(paymentBreakDownHistory);
			aRes.setRenderParameter("jspPage", "/html/settingsmanagementportlet/settings/paymentbreakdownlisting.jsp");
        }
		
	}

	

	private void manageOnePaymentBreakdownHistory(ActionRequest aReq,
			ActionResponse aRes, SettingsManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String selectedPBDH = aReq.getParameter("selectedPBDH").trim();
		String selectedPBDHAction = aReq.getParameter("selectedPBDHAction");
		log.info("selectedPBDH=" + selectedPBDH);
		log.info("selectedPBDHAction=" + selectedPBDHAction);
		
		if(selectedPBDHAction!=null && selectedPBDHAction.equals("splitOnePayment") && selectedPBDH!=null)
		{
			try{
				Long selectedPBDHId = Long.valueOf(selectedPBDH);
				log.info("selectedPBDHId=" + selectedPBDHId);
//				PaymentBreakDownHistory pbdh = (PaymentBreakDownHistory)portletState.getSettingsManagementPortletUtil().
//					getEntityObjectById(PaymentBreakDownHistory.class, selectedPBDHId);
//				hql = "Select pbdh FROM PaymentBreakDownHistory pbdh " +
//						" WHERE lower(pbdh.status) = lower('" + 
//						PaymentBreakDownHistoryConstants.PAYMENT_BREAKDOWN_STATUS_PENDING.getValue() + "') AND " +
//								"lower(pbdh.paymentHistory.status) = " +
//						" lower('" + PaymentHistoryConstants.PAYMENTHISTORY_STATUS_ZRA_CONFIRMED.getValue() + "')  " +
//								"ORDER by pbdh.paymentHistory.entryDate ASC";
				
				String hql = "Select pbdh FROM PaymentBreakDownHistory pbdh " +
						" WHERE lower(pbdh.status) = lower('" + 
						PaymentBreakDownHistoryConstants.PAYMENT_BREAKDOWN_STATUS_PENDING.getValue() + "') AND " +
								"lower(pbdh.paymentHistory.status) = " +
						" lower('" + PaymentHistoryConstants.PAYMENTHISTORY_STATUS_ZRA_CONFIRMED.getValue() + "') AND " +
								"pbdh.id = " + selectedPBDHId;
				log.info("hql = " + hql);
				
				PaymentBreakDownHistory paymentBreakDownHistory = (PaymentBreakDownHistory) swpService.getUniqueRecordByHQL(hql);
				ArrayList<PaymentBreakDownHistory> pdlist = null;
				if(paymentBreakDownHistory!=null)
				{
					pdlist = new ArrayList<PaymentBreakDownHistory>();
					pdlist.add(paymentBreakDownHistory);
					handleManualSplitter(aReq, aRes, portletState, pdlist);
				}else
				{
					handleManualSplitter(aReq, aRes, portletState, pdlist);
				}
			}
			catch(NumberFormatException e)
			{
				portletState.addError(aReq, "Invalid Payment Selected. Select a Valid payment before proceeding!", portletState);
				e.printStackTrace();
			}
			aRes.setRenderParameter("jspPage", "/html/settingsmanagementportlet/settings/paymentbreakdownlisting.jsp");
		}
	}

	private void handleEOD(ActionRequest aReq, ActionResponse aRes,
			SettingsManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String actEod = aReq.getParameter("actEod");
		if(actEod.equals("1"))
		{
			//start running
			log.info("Command: Start Cron Scheduler");
			try
			{
				//portletState.setCronScheduler(new CronScheduler(ACTION.EOD));
				log.info("Action: Cron Scheduler Started");
				portletState.addSuccess(aReq, "End-Of-Day Scheduler has been started successfully!", portletState);
				
				Settings settings = portletState.getEODSetting();
				settings.setValue("1");
				swpService.updateRecord(settings);
				handleEODNOW(portletState);
			}catch(Exception e)
			{
				e.printStackTrace();
				portletState.addError(aReq, "End-Of-Day Scheduler was not started successfully!", portletState);
			}
		}
		else if(actEod.equals("0"))
		{
			//stop running
			try
			{
				log.info("Command: Stop Cron Scheduler");
				if(portletState.getCronScheduler()==null)
				{
					Settings settings = portletState.getEODSetting();
					settings.setValue("0");
					swpService.updateRecord(settings);
					
					handleEODNOWOld(portletState);
				}else
				{
					portletState.getCronScheduler().shutdownScheduler(ACTION.EOD);
					log.info("Action: Cron Scheduler Stopped");

					Settings settings = portletState.getEODSetting();
					settings.setValue("0");
					swpService.updateRecord(settings);
					
				}
				portletState.addSuccess(aReq, "End-Of-Day Scheduler has been stopped successfully!", portletState);
			}catch(Exception e)
			{
				e.printStackTrace();
				portletState.addError(aReq, "End-Of-Day Scheduler was not started successfully!", portletState);
			}
		}else
		{
			//error
			portletState.addError(aReq, "Action on End-Of-Day Scheduler not carried out!", portletState);
		}
		portletState.setCurrentTab(VIEW_TABS.MANAGE_JOBS);
		aRes.setRenderParameter("jspPage", "/html/settingsmanagementportlet/settings/manageschedulers.jsp");
	}

	private void handleEODNOWOld(SettingsManagementPortletState portletState) throws IOException {
		// TODO Auto-generated method stub
		
		java.util.Properties props = null;
        props = System.getProperties();
        String proxyUsername = portletState.getProxyUsername().getValue();
        String proxyPassword = portletState.getProxyPassword().getValue();
        String proxyHost = portletState.getProxyHost().getValue();
        String proxyPort = portletState.getProxyPort().getValue();
        
        Authenticator.setDefault(new ProxyAuthenticator(proxyUsername, proxyPassword));
        System.setProperty("http.proxyHost", proxyHost);
		System.setProperty("http.proxyPort", proxyPort);
		// trustStore has the certificates that are presented by the server that
        // this application is to trust
		//System.setProperty("javax.net.ssl.trustStore", "keys/client.jks");
		//System.setProperty("javax.net.ssl.trustStorePassword", "password");
         
        // keystore has the certificates presented to the server when a server
        // requests one to authenticate this application to the server
		//System.setProperty("javax.net.ssl.keyStore", "keys/client.jks");
		//System.setProperty("javax.net.ssl.keyStorePassword", "password");
		
		
		String responseString = "";
		String outputString = "";
		String wsURL = "http://10.236.6.125:6080/africa/services/uat/zm/maxintegrationv1_0";			///MAKE A SETTING IN DB
		wsURL = "http://10.16.76.69:9999/ZraWebService/services/EODPaymentNotificationReportService";
		URL url = new URL(wsURL);
		CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
		URLConnection connection = url.openConnection();
		HttpURLConnection httpConn = (HttpURLConnection)connection; 
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		String xmlInput =
		"<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" " +
		"xmlns:ser=\"http://service.bank.pmt.zra\" " +
		"xmlns:xsd=\"http://bean.bank.pmt.zra/xsd\" " +
		"xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\" " +
		"xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" " +
		"xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">" +
		"<soapenv:Header/>" +
		"<soap:Body>" +
		"<ser:processPaymentNotificationReport>" +
		"<ser:pmtNotifyReport>" +
		"<xsd:amountPaid>11.15</xsd:amountPaid>" +
		"<xsd:bankBranchCode>03442</xsd:bankBranchCode>" +
		"<xsd:bankTransNo>SPI887424007140930</xsd:bankTransNo>" +
		"<xsd:datePaid>20140930</xsd:datePaid>" +
		"<xsd:paymentRegTransNo>113002194363</xsd:paymentRegTransNo>" +
		"<xsd:status>S</xsd:status>" +
		"<xsd:taxPayerName>ORMA CONSTRUCTION LIMITED</xsd:taxPayerName>" +
		"<xsd:tin>1002094680</xsd:tin>" +
		"<xsd:transactionId>99888</xsd:transactionId>" +
		"</ser:pmtNotifyReport>" +
		"</ser:processPaymentNotificationReport>" +
		"</soap:Body>" +
		"</soap:Envelope>";
		
		System.out.println("xmlInput = " + xmlInput);
		 
		byte[] buffer = new byte[xmlInput.length()];
		buffer = xmlInput.getBytes();
		bout.write(buffer);
		byte[] b = bout.toByteArray();
		String SOAPAction =
		"\"notifyPayment\"";
		// Set the appropriate HTTP parameters.
		httpConn.setRequestProperty("Content-Length",
		String.valueOf(b.length));
		httpConn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
		httpConn.setRequestProperty("SOAPAction", SOAPAction);
		httpConn.setRequestProperty("Accept-Encoding", "gzip,deflate");
		httpConn.setRequestProperty("Accept", "text/xml");
		httpConn.setRequestProperty("RequestVersion", "HTTP/1.1");
		httpConn.setRequestProperty("Host", "10.16.76.69:9999");
        httpConn.setRequestProperty("Accept", "*/*");
        httpConn.setRequestProperty("User-Agent", "Java");
		httpConn.setRequestMethod("POST");
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		OutputStream out = httpConn.getOutputStream();
		//Write the content of the request to the outputstream of the HTTP Connection.
		out.write(b);
		out.close();
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
		Document document = new Util().parseXmlFile(outputString);
	}

	private void handleEODNOW(SettingsManagementPortletState portletState) throws IOException {
		// TODO Auto-generated method stub
		
		java.util.Properties props = null;
        props = System.getProperties();
        String proxyUsername = portletState.getProxyUsername().getValue();
        String proxyPassword = portletState.getProxyPassword().getValue();
        String proxyHost = portletState.getProxyHost().getValue();
        String proxyPort = portletState.getProxyPort().getValue();
        
        Authenticator.setDefault(new ProxyAuthenticator(proxyUsername, proxyPassword));
        System.setProperty("http.proxyHost", proxyHost);
		System.setProperty("http.proxyPort", proxyPort);
		// TODO Auto-generated method stub
		SOAPConnectionFactory soapConnectionFactory;
		SOAPConnection soapConnection;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		
		String hql = "Select pth FROM PaymentTempHolder pth " +
				" WHERE pth.dateAdded > '" + sdf.format(date) + " 00:00:00' AND " +
						"pth.dateAdded < '" + sdf.format(date) + " 23:59:59'";
		log.info("hql2 = " + hql);
		
		Collection<PaymentTempHolder> paymentTempHolder = (Collection<PaymentTempHolder>) swpService.getAllRecordsByHQL(hql);
		
		if(paymentTempHolder != null){
			log.info("paymentTempHolder1 size = "+paymentTempHolder.size());
			//new Util().handleEndOfDayDomTransactions(paymentTempHolder);
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
	        	log.info("url5 = > ");
				if(paymentTempHolder!=null && paymentTempHolder.size()>0)
				{
					log.info("url6 = > ");
					try {
			            // Create SOAP Connection
						
						soapConnectionFactory = SOAPConnectionFactory.newInstance();
			            soapConnection = soapConnectionFactory.createConnection();
			            log.info("url7 = > ");
			            // Send SOAP Message to SOAP Server
			            try
			            {
			            	log.info("url8 = > ");
			            	//format is date in "yyyyMMdd" format
				            SOAPMessage soapResponse = null;
				            log.info("url9 = > ");


				            SOAPMessage soapMessage = null;
				    		try
				    		{
				    			log.info("url10 = > ");
				    	        MessageFactory messageFactory = MessageFactory.newInstance();
				    	        log.info("url10 = > ");
				    	        soapMessage = messageFactory.createMessage();
				    	        log.info("url10 = > ");
				    	        SOAPPart soapPart = soapMessage.getSOAPPart();
				    	        log.info("url10 = > ");
				    	
				    	        
				    	        // SOAP Envelope
				    	        SOAPEnvelope envelope = soapPart.getEnvelope();
				    	        log.info("url10 = > ");
				    	        envelope.addNamespaceDeclaration("ser", "http://service.bank.pmt.zra/");
				    	        envelope.addNamespaceDeclaration("xsd", "http://bean.bank.pmt.zra/xsd/");
				    	        envelope.addNamespaceDeclaration("wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
				    	        envelope.addNamespaceDeclaration("wsa", "http://schemas.xmlsoap.org/ws/2004/08/addressing");
				    	        envelope.addNamespaceDeclaration("wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
				    	        
//				    	        "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" " +
//				    			"xmlns:ser=\"http://service.bank.pmt.zra\" " +
//				    			"xmlns:xsd=\"http://bean.bank.pmt.zra/xsd\" " +
//				    			"xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\" " +
//				    			"xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" " +
//				    			"xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">" +
//				    			"<soapenv:Header/>" +
				    	        //soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:ser="http://service.bank.pmt.zra" xmlns:xsd="http://bean.bank.pmt.zra/xsd" xmlns:wsa="http://schemas.xmlsoap.org/ws/2004/08/addressing" xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd"
				    	        SOAPHeader header = envelope.getHeader();
				    	        if(header==null)
				    	        	header = envelope.addHeader();
				    	        
				                //SOAPElement security =  header.addChildElement("Security", "wsse");
				    			SOAPBody soapBody = envelope.getBody();
				    			//soapBody.addAttribute(new QName("id"), "342");
				    			log.info("url10 = > ");
				    			SOAPElement soapBodyElem_ = soapBody.addChildElement("processPaymentNotificationReport", "ser");
				    			log.info("url10 = > ");
				    			
				    			for(Iterator<PaymentTempHolder> it = paymentTempHolder.iterator(); it.hasNext();)
				    	        {
				    				PaymentTempHolder dtsd1 = it.next();
				    				log.info("url12 = > ");
				    				SOAPElement soapBodyElem = soapBodyElem_.addChildElement("pmtNotifyReport", "ser");
				    				SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("amountPaid", "xsd");
				    				SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("bankTransNo", "xsd");
				    				SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("datePaid", "xsd");
				    				SOAPElement soapBodyElem4 = soapBodyElem.addChildElement("paymentRegTransNo", "xsd");
				    				SOAPElement soapBodyElem5 = soapBodyElem.addChildElement("status", "xsd");
				    				SOAPElement soapBodyElem6 = soapBodyElem.addChildElement("taxPayerName", "xsd");
				    				SOAPElement soapBodyElem7 = soapBodyElem.addChildElement("tin", "xsd");
				    				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
				    		        soapBodyElem1.addTextNode(Double.toString(dtsd1.getPaymentBreakDownHistory().getPaymentHistory().getDomTax().getAmountPayable()));
				    		        soapBodyElem2.addTextNode(dtsd1.getPaymentBreakDownHistory().getPaymentHistory().getTransactionReferenceId());
				    		        soapBodyElem3.addTextNode(sdf1.format(new Date()));
				    		        soapBodyElem4.addTextNode(dtsd1.getPaymentBreakDownHistory().getPaymentHistory().getDomTax().getPaymentRegNo());
				    		        String statusPayment  = dtsd1.getPaymentBreakDownHistory().getPaymentHistory().getStatus().equals(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_APPROVED) ? "S" : "F";
				    		        soapBodyElem5.addTextNode(statusPayment);
				    		        soapBodyElem6.addTextNode(dtsd1.getPaymentBreakDownHistory().getPaymentHistory().getDomTax().getTaxPayerName());
				    		        soapBodyElem7.addTextNode(dtsd1.getPaymentBreakDownHistory().getPaymentHistory().getDomTax().getTpinInfo().getTpin());
//				    		        MimeHeaders headers = soapMessage.getMimeHeaders();
//				    		        headers.addHeader("SOAPAction", serverURI  + "getUnpaidAssessmentsByTPIN");
				    		        log.info("url14 = > ");
				    		        soapMessage.saveChanges();
				    		        log.info("url109 = > ");
				    		        
				    		        Logger l = Logger.getLogger(Util.class);
				    				l.info("44");

				    	        }
				    			
				    			System.out.print("Request SOAP Message for Probase Service = ");
				            	soapMessage.writeTo(System.out);
				            	
				            	
				            	//soapMessage = new Util().handleEODSignEncrypt(soapMessage);
				            	
				    		}
				    		catch(Exception e)
				    		{
				    			e.printStackTrace();
				    			MessageFactory messageFactory;
//				    			SOAPMessage soapMessage = null;
				    			try {
				    				messageFactory = MessageFactory.newInstance();
				    				
				    				try {
				    					soapMessage = messageFactory.createMessage();
				    				} catch (SOAPException e1) {
				    					// TODO Auto-generated catch block
				    					e1.printStackTrace();
//				    					return null;
				    				}
				    			} catch (SOAPException e2) {
				    				// TODO Auto-generated catch block
				    				e2.printStackTrace();
//				    				return null;
				    			}
				    			
//				    			return soapMessage;
				    		}
//				            soapResponse = soapConnection.call(
//				            		Util.createSOAPRequestForEndOfDayDomT(paymentTempHolder), url);
				    		soapResponse = soapConnection.call(
				    				soapMessage, url);
				            

				            endOfDayDomTResponse =  Util.handleResponseForEndOfDayDomT(soapResponse);
				            
				            //<?xml version="1.0" encoding="UTF-8" standalone="no"?><S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/"><S:Body><ns2:getAssessmentDetailsResponse xmlns:ns2="http://testservice.probase.com/"><clientTPIN/><country>ZM</country><reasonCode>0</reasonCode><reasonDescription>Successful</reasonDescription><source>S2B</source><sourceID>123456</sourceID><timestamp>Sat May 10 22:07:36 EDT 2014</timestamp><tpin_declarantCode>1000009294</tpin_declarantCode><type>getAssessmentDetails</type></ns2:getAssessmentDetailsResponse></S:Body></S:Envelope>
				            
				            // Process the SOAP Response
				            Util.printSOAPResponse(soapResponse);
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
		}
		
	}
	
	
	
	private void handleManualSplitter(ActionRequest aReq, ActionResponse aRes, 
			SettingsManagementPortletState portletState, 
			Collection<PaymentBreakDownHistory> paymentBreakDownHistory)
	{
		int c=0;
		int d=0;
		Settings zraAccount = null;
		Settings zraSortCode = null;
		Settings currency = null;
		Settings proxyHost = null;
		Settings proxyUsername = null;
		Settings proxyPassword = null;
		Settings proxyPort = null;
		Settings bankPaymentUrl = null;
		Settings sysApplication = null;
		
		
		try{
			zraAccount = portletState.getSettingsZRAAccount();
			zraSortCode = portletState.getSettingsZRASortCode();
			currency = portletState.getCurrency();
			sysApplication = portletState.getApplicationName();
			proxyHost = portletState.getProxyHost();
			proxyUsername = portletState.getProxyUsername();
			proxyPassword = portletState.getProxyPassword();
			proxyPort = portletState.getProxyPort();
			bankPaymentUrl = portletState.getBankPaymentWebServiceUrl();
			
			
			String hql = "Select st FROM TaxType st";
			Collection<TaxType> allTaxType = (Collection<TaxType>)swpService.getAllRecordsByHQL(hql);
			log.info("allTaxType Size = " + allTaxType.size());
			
			if(currency!=null && zraSortCode!=null && zraAccount!=null)
			{
//				List<PaymentBreakDownHistory> paymentBreakDownHistory = null;
				
//				paymentBreakDownHistory = pcs.getPaymentBreakDownHistory(
//						PaymentBreakDownHistoryConstants.PAYMENT_BREAKDOWN_STATUS_PENDING.getValue(), 
//						PaymentHistoryConstants.PAYMENTHISTORY_STATUS_ZRA_CONFIRMED.getValue());
				
				
				if(paymentBreakDownHistory != null){
					log.info("paymentBreakDownHistory Id = "+paymentBreakDownHistory.size());
					for(Iterator<PaymentBreakDownHistory> pbdhIter = paymentBreakDownHistory.iterator(); pbdhIter.hasNext();)
					{
						PaymentBreakDownHistory pbdh = pbdhIter.next();
						log.info("pbdh id = " + pbdh.getId());
						pbdh.setStatus(PaymentBreakDownHistoryConstants.PAYMENT_BREAKDOWN_STATUS_CANCELED);
						swpService.updateRecord(pbdh);
						TaxType tt = null;
						TaxTypeAccount tta = null;
						c++;
						for(Iterator<TaxType> ttIter = allTaxType.iterator(); ttIter.hasNext();)
						{
							TaxType taxType = ttIter.next();
							log.info("---> " + taxType.getId() + " &&& " + (pbdh.getTaxType().getId()));
							if(taxType.getId().equals(pbdh.getTaxType().getId()))
							{
								tt = pbdh.getTaxType();
								if(tt!=null)
									log.info("tt = " + tt.getId() + " && tt = " + tt.getTaxCode());
								
								hql = "Select tta From TaxTypeAccount tta where lower(tta.taxType.taxCode) = " +
										"lower('" + tt.getTaxCode() + "')";
								tta = (TaxTypeAccount)swpService.getUniqueRecordByHQL(hql);
								if(tta!=null)
									log.info("tta = " + tta.getId() + " && tt = " + tta.getAccountNumber());
								
								if(tt!=null && tta!=null)
								{
									log.info("tta id = " + tta.getId());
									log.info("tt id = " + tt.getId());
									String query = "Select st FROM TaxTypeAccount st where st.taxType.id = " + tt.getId();
									TaxTypeAccount taxTypeAccount = (TaxTypeAccount)swpService.getUniqueRecordByHQL(query);
									
									
									if(taxTypeAccount!=null)
									{
										DateFormat df = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss:S");
								        DateFormat df1 = new SimpleDateFormat( "SssmmHHddMMyy");
								        DateFormat df2 = new SimpleDateFormat( "yyyyMMdd");
										DateFormat df3 = new SimpleDateFormat( "Sssmm");
								        String currentTimeStamp = df.format(new Date());
								        String currentTimeStamp1 = df1.format(new Date());
								        String currentTimeStamp2 = df2.format(new Date());
								        String currentTimeStamp3 = df3.format(new Date());
								        String uniqId = "SPI" +  currentTimeStamp1;
								        
								        
								        log.info("currentTimeStamp1 = " + currentTimeStamp1);
								        log.info("currentTimeStamp = " + currentTimeStamp);
								        log.info("uniqId = " + uniqId);
								        log.info("zraAccount = " + zraAccount.getValue());
								        log.info("zraSortCode = " + zraSortCode.getValue());
								        log.info("pbdh = " + Double.toString(pbdh.getAmount()));
								        log.info("currentTimeStamp2 = " + currentTimeStamp2);
								        log.info("currentTimeStamp3 = " + currentTimeStamp3);
								        log.info("tta.getAccountNumber = " + tta.getAccountNumber());
								        log.info("tta.getAccountSortCode = " + tta.getAccountSortCode());
										
										
								        FundsTransferResponse ftr= null;
								        
								        
										try {
											if(pbdh.getAmount().equals(null) && pbdh.getAmount().equals(0.00))
									        {
												if(this.mode.equals(Boolean.TRUE))
												{
													log.info("We are in 0");
													DateFormat dfg = new SimpleDateFormat("yyyyddMMHHmmssS");
													PaymentHistory pbdh12 = pbdh.getPaymentHistory();
	//												String ref = pbdh12.getAssessment()!=null ? (pbdh12.getAssessment().getPorts().getPortCode() + "/" + 
	//														pbdh12.getAssessment().getAssessmentNumber() + "/" + 
	//														pbdh12.getAssessment().getAssessmentYear() + "/" + 
	//														pbdh.getTaxType().getTaxCode()+"/"+ dfg.format(new Date())) : 
	//															(pbdh12.getDomTax()!=null ? pbdh12.getRequestMessageId() : "");
													String ref = pbdh12.getAssessment()!=null ? ("SP/" + pbdh.getTransactionNumber()) : 
																(pbdh12.getDomTax()!=null ? pbdh12.getRequestMessageId() : "");
	 
	
													if(ComminsApplicationState.STB==1)
													{
														ftr = Util.doFundsTransfer(sysApplication.getValue(), "SMARTPAY:ZM:REVENUE:ENTRY:REQUEST", 
															  currentTimeStamp1, currentTimeStamp, uniqId, 
															"ZMW", "1095", zraAccount.getValue(), 
															zraSortCode.getValue(), Double.toString(pbdh.getAmount()), currentTimeStamp2, 
															currentTimeStamp3, "00-00-00", tta.getAccountNumber(), 
															tta.getAccountSortCode(),
										    				proxyUsername.getValue(), 
										    				proxyPassword.getValue(), 
										    				proxyHost.getValue(), 
										    				proxyPort.getValue(), 
															bankPaymentUrl.getValue(), ref);
													}
													
													if(ComminsApplicationState.BANC_ABC==1)
													{
														ftr = Util.doFundsTransfer(sysApplication.getValue(), "SMARTPAY:ZM:REVENUE:ENTRY:REQUEST", 
															  currentTimeStamp1, currentTimeStamp, uniqId, 
															"ZMW", "1095", zraAccount.getValue(), 
															zraSortCode.getValue(), Double.toString(pbdh.getAmount()), currentTimeStamp2, 
															currentTimeStamp3 + "" + pbdh.getTaxType().getTaxCode(), "00-00-00", tta.getAccountNumber(), 
															tta.getAccountNumber(),
										    				proxyUsername.getValue(), 
										    				proxyPassword.getValue(), 
										    				proxyHost.getValue(), 
										    				proxyPort.getValue(), 
															bankPaymentUrl.getValue(), ref);
													}
													
													
												}
												else
												{
													log.info("We are in 01");
													ftr = new FundsTransferResponse();
													ftr.setAccountNumber(zraAccount.getValue());
													ftr.setResMessageId(pbdh.getTransactionNumber());
													ftr.setResTimeStamp(Boolean.TRUE);
													ftr.setStatus(Boolean.TRUE);
												}
									        }
											
											if(ftr!=null)
											{
												log.info("We are in 3");
												if(ftr.getStatus().equals(Boolean.TRUE))
												{
													log.info("We are in 1");
													pbdh.setStatus(PaymentBreakDownHistoryConstants.PAYMENT_BREAKDOWN_STATUS_APPROVED);
													pbdh.setDateOfTransaction(new Timestamp((new Date()).getTime()));
													pbdh.setReceipientAccountNumber(tta.getAccountNumber());
													pbdh.setSourceAccountNumber(zraAccount.getValue());
													//pbdh.setTransactionNumber(ftr.getResMessageId());
													//pcs.deleteRecord(pth);
													swpService.updateRecord(pbdh);
													log.info("We are in 11");
													d++;
													
												}else
												{
													log.info("We are in 2");
													//pcs.deleteRecord(pth);
													pbdh.setStatus(PaymentBreakDownHistoryConstants.PAYMENT_BREAKDOWN_STATUS_PENDING);
													swpService.updateRecord(pbdh);
													log.info("We are in 21");
												}
											}else
											{
												log.info("We are in 30");
											}
										} catch (MalformedURLException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								}
							}
						}
					}
				}
				
				
			}
					
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		if(paymentBreakDownHistory!=null && paymentBreakDownHistory.size()>0)
		{
			if(c==d)
			{
				portletState.addSuccess(aReq, "All payments have been moved successfully from ZRA suspense account to their various tax-type accounts!", portletState);
			}else
			{
				portletState.addSuccess(aReq, "Some Payments have been moved from ZRA suspense account to their various tax-type accounts", portletState);
			}
		}else
		{
			portletState.addError(aReq, "There are no payments to move from ZRA suspense account to various tax-type accounts!", portletState);
		}
		aRes.setRenderParameter("jspPage", "/html/settingsmanagementportlet/settings/manageschedulers.jsp");
	}

	private void handleSplitter(ActionRequest aReq, ActionResponse aRes,
			SettingsManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String actSplit = aReq.getParameter("actSplit");
		if(actSplit.equals("1"))
		{
			//start running
			log.info("Command: Start Cron Scheduler");
			try
			{
				portletState.setCronScheduler(new CronScheduler(ACTION.PAY_SPLIT));
				log.info("Action: Cron Scheduler Started");
				

				Settings settings = portletState.getPaySplitterSetting();
				settings.setValue("1");
				swpService.updateRecord(settings);
				portletState.setPaySplitterSetting(settings);
				
				portletState.addSuccess(aReq, "End-Of-Day Scheduler has been started successfully!", portletState);
			}catch(Exception e)
			{
				e.printStackTrace();
				portletState.addError(aReq, "End-Of-Day Scheduler was not started successfully!", portletState);
			}
		}
		else if(actSplit.equals("0"))
		{
			//stop running
			try
			{
				log.info("Command: Stop Cron Scheduler");
				if(portletState.getCronScheduler()==null)
				{
					
				}else
				{
					portletState.getCronScheduler().shutdownScheduler(ACTION.PAY_SPLIT);
					log.info("Action: Cron Scheduler Started");
					Settings settings = portletState.getPaySplitterSetting();
					settings.setValue("0");
					swpService.updateRecord(settings);
					portletState.setPaySplitterSetting(settings);
					
				}
				portletState.addSuccess(aReq, "End-Of-Day Scheduler has been stopped successfully!", portletState);
			}catch(Exception e)
			{
				e.printStackTrace();
				portletState.addError(aReq, "End-Of-Day Scheduler was not started successfully!", portletState);
			}
		}
		else if(actSplit.equals("2"))
		{
			//start running
			log.info("Command: Start Cron Scheduler Manually");
			String hql = "Select pbdh FROM PaymentBreakDownHistory pbdh " +
					" WHERE lower(pbdh.status) = lower('" + 
					PaymentBreakDownHistoryConstants.PAYMENT_BREAKDOWN_STATUS_PENDING.getValue() + "') AND " +
							"lower(pbdh.paymentHistory.status) = " +
					" lower('" + PaymentHistoryConstants.PAYMENTHISTORY_STATUS_ZRA_CONFIRMED.getValue() + "')  " +
							"ORDER by pbdh.paymentHistory.entryDate ASC";
			
			Collection<PaymentBreakDownHistory> pdlist = (Collection<PaymentBreakDownHistory>) swpService.getAllRecordsByHQL(hql);
			handleManualSplitter(aReq, aRes, portletState, pdlist);
		}
		
		else
		{
			//error
			portletState.addError(aReq, "Action on End-Of-Day Scheduler not carried out!", portletState);
		}
		portletState.setCurrentTab(VIEW_TABS.MANAGE_JOBS);
		aRes.setRenderParameter("jspPage", "/html/settingsmanagementportlet/settings/manageschedulers.jsp");
	}

	private void handleUpdateSettings(ActionRequest aReq, ActionResponse aRes,
			SettingsManagementPortletState portletState) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		
		portletState.setSelectedFeeDescription(aReq.getParameter("feedescription"));
		portletState.setSelectedEmailNotificationForCorporateFirm(aReq.getParameter("emailnotificationforcorporatefirm"));
		portletState.setSelectedMobileNotificationForCorporateFirm(aReq.getParameter("mobilenotificationforcorporatefirm"));
		portletState.setSelectedEmailNotificationForCorporateIndivididuals(aReq.getParameter("emailnotificationforcorporateindivididuals"));
		portletState.setSelectedMobileNotificationForCorporateIndivididuals(aReq.getParameter("mobilenotificationforcorporateindivididuals"));
		portletState.setSelectedEtaxPaymentEmailNotify(aReq.getParameter("etaxpaymentemailnotify"));
		portletState.setSelectedEtaxPaymentSmsNotify(aReq.getParameter("etaxpaymentsmsnotify"));
		portletState.setSelectedSystemUrl(aReq.getParameter("systemUrl"));
		portletState.setSelectedPlatformCountry(aReq.getParameter("platformCountry"));
		portletState.setSelectedPlatformBank(aReq.getParameter("platformBank"));
		portletState.setSelectedTaxCompanyAccount(aReq.getParameter("taxCompanyAccount"));
		portletState.setSelectedTaxCompanySortCode(aReq.getParameter("taxCompanySortCode"));
		portletState.setSelectedSendingEmail(aReq.getParameter("sendingEmail"));
		portletState.setSelectedSendingEmailPassword(aReq.getParameter("sendingEmailPassword"));
		portletState.setSelectedSendingEmailPort(aReq.getParameter("sendingEmailPort"));
		portletState.setSelectedSendingEmailUsername(aReq.getParameter("sendingEmailUsername"));
		portletState.setSelectedApprovalProcess(aReq.getParameter("approvalProcess"));
		portletState.setSelectedTwoStepLogin(aReq.getParameter("twoStepLogin"));
		
		
		
		portletState.setSelectedApplicationName(aReq.getParameter("applicationName"));
		portletState.setSelectedMobileApplicationName(aReq.getParameter("mobileApplicationName"));
		portletState.setSelectedProxyHost(aReq.getParameter("proxyHost"));
		portletState.setSelectedProxyPort(aReq.getParameter("proxyPort"));
		portletState.setSelectedProxyUsername(aReq.getParameter("proxyUsername"));
		portletState.setSelectedProxyPassword(aReq.getParameter("proxyPassword"));
		portletState.setSelectedBankName(aReq.getParameter("bankName"));
		portletState.setSelectedcurrency(aReq.getParameter("currency"));
		portletState.setSelectedBankPaymentWebServiceUrl(aReq.getParameter("bankPaymentWebServiceUrl"));
		portletState.setSelectedZraWebServiceUrl(aReq.getParameter("zraWebServiceUrl"));
		
		if(isSettingsDataValid(portletState, aReq, aRes, false))
		{
			Settings pfs = portletState.getPrimaryFeeSetting();
			Settings ncfm = portletState.getNotifyCorporateFirmEmail();
			Settings ncfs = portletState.getNotifyCorporateFirmSms();
			Settings ncim = portletState.getNotifyCorporateIndividualEmail();
			Settings ncis = portletState.getNotifyCorporateIndividualSMS();
			Settings epnm = portletState.getEtaxPaymentNotifyEmail();
			Settings epns = portletState.getEtaxPaymentNotifySMS();
			
			Settings sys = portletState.getSystemUrl();
			Settings pc = portletState.getPlatformCountry();
			Settings pb = portletState.getPlatformBank();
			Settings tca = portletState.getSettingsZRAAccount();
			Settings tcsc = portletState.getSettingsZRASortCode();
			Settings se = portletState.getSendingEmail();
			Settings sep = portletState.getSendingEmailPassword();
			Settings seu = portletState.getSendingEmailPort();
			Settings sepo = portletState.getSendingEmailUsername();
			Settings ap = portletState.getApprovalProcess();
			Settings tsl = portletState.getTwoStepLogin();
			
			Settings app = portletState.getApplicationName();
			Settings mapp = portletState.getMobileApplicationName();
			Settings proxH = portletState.getProxyHost();
			Settings proxP = portletState.getProxyPort();
			Settings proxU = portletState.getProxyUsername();
			Settings proxPwd = portletState.getProxyPassword();
			Settings bank = portletState.getBankName();
			Settings currency = portletState.getCurrency();
			Settings bpwsu = portletState.getBankPaymentWebServiceUrl();
			Settings zwsu = portletState.getZraWebServiceUrl();

			
			if(portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1"))
			{
				boolean processComplete = false;
				try {
					if(sys!=null && !portletState.getSelectedSystemUrl().equals(sys.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_SYSTEM_URL.getValue(), portletState.getSelectedSystemUrl());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(sys.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}
					
					if(pfs!=null && !portletState.getSelectedFeeDescription().equals(pfs.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_PRIMARY_FEE_DESCRIPTION.getValue(), portletState.getSelectedFeeDescription());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(pfs.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}
					
					if(ncfm!=null && !portletState.getSelectedEmailNotificationForCorporateFirm().equals(ncfm.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_EMAIL.getValue(), portletState.getSelectedEmailNotificationForCorporateFirm());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(ncfm.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}
					
					if(ncfs!=null && !portletState.getSelectedMobileNotificationForCorporateFirm().equals(ncfs.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_SMS.getValue(), portletState.getSelectedMobileNotificationForCorporateFirm());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(ncfs.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}
					
					if(ncim!=null && !portletState.getSelectedEmailNotificationForCorporateIndivididuals().equals(ncim.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_EMAIL.getValue(), portletState.getSelectedEmailNotificationForCorporateIndivididuals());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(ncim.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}


					if(ncis!=null && !portletState.getSelectedMobileNotificationForCorporateIndivididuals().equals(ncis.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_SMS.getValue(), portletState.getSelectedMobileNotificationForCorporateIndivididuals());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(ncis.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}

					if(epnm!=null && !portletState.getSelectedEtaxPaymentEmailNotify().equals(epnm.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_EMAIL.getValue(), portletState.getSelectedEtaxPaymentEmailNotify());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(epnm.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}

					if(epns!=null && !portletState.getSelectedEtaxPaymentSmsNotify().equals(epns.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_SMS.getValue(), portletState.getSelectedEtaxPaymentSmsNotify());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(epns.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}


					if(pc!=null && !portletState.getSelectedPlatformCountry().equals(pc.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_PLATORM_BANK.getValue(), portletState.getSelectedPlatformCountry());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(pc.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}


					if(pb!=null && !portletState.getSelectedPlatformBank().equals(pb.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_PLATORM_BANK.getValue(), portletState.getSelectedPlatformBank());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(pb.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}

					if(tca!=null && !portletState.getSelectedTaxCompanyAccount().equals(tca.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_NUMBER.getValue(), portletState.getSelectedTaxCompanyAccount());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(tca.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}

					if(tcsc!=null && !portletState.getSelectedTaxCompanySortCode().equals(tcsc.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_SORT_CODE.getValue(), portletState.getSelectedTaxCompanySortCode());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(tcsc.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}

					if(se!=null && !portletState.getSelectedSendingEmail().equals(se.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_EMAIL_SENDER_EMAIL.getValue(), portletState.getSelectedSendingEmail());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(se.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}


					if(sep!=null && !portletState.getSelectedSendingEmailPassword().equals(sep.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_EMAIL_SENDER_PASSWORD.getValue(), portletState.getSelectedSendingEmailPassword());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(sep.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}

					if(seu!=null && !portletState.getSelectedSendingEmailUsername().equals(seu.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_EMAIL_SENDER_USERNAME.getValue(), portletState.getSelectedSendingEmailUsername());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(seu.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}

					if(sepo!=null && !portletState.getSelectedSendingEmailPort().equals(sepo.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_EMAIL_PORT.getValue(), portletState.getSelectedSendingEmailPort());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(sepo.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}

					if(ap!=null && !portletState.getApprovalProcess().equals(ap.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_APPROVAL_PROCESS.getValue(), portletState.getSelectedApprovalProcess());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(ap.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}


					if(tsl!=null && !portletState.getTwoStepLogin().equals(tsl.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_TWO_STEP_LOGIN.getValue(), portletState.getSelectedTwoStepLogin());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(tsl.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}
					
					


					
					/*****/
					
					
					if(app!=null && !portletState.getSelectedApplicationName().equals(app.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_APPLICATION_NAME.getValue(), portletState.getSelectedApplicationName());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(app.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}
					
					if(mapp!=null && !portletState.getSelectedFeeDescription().equals(mapp.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_MOBILE_APPLICATION_NAME.getValue(), portletState.getSelectedMobileApplicationName());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(mapp.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}
					
					if(proxH!=null && !portletState.getSelectedEmailNotificationForCorporateFirm().equals(proxH.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_PROXY_HOST.getValue(), portletState.getSelectedProxyHost());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(proxH.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}
					
					
					if(proxP!=null && !portletState.getSelectedMobileNotificationForCorporateFirm().equals(proxP.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_PROXY_PORT.getValue(), portletState.getSelectedProxyPort());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(proxP.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}
					
					if(proxU!=null && !portletState.getSelectedEmailNotificationForCorporateIndivididuals().equals(proxU.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_PROXY_USERNAME.getValue(), portletState.getSelectedProxyUsername());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(proxU.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}
					
					
					if(proxPwd!=null && !portletState.getSelectedMobileNotificationForCorporateIndivididuals().equals(proxPwd.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_PROXY_PASSWORD.getValue(), portletState.getSelectedProxyPassword());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(proxPwd.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}

					if(bank!=null && !portletState.getSelectedEtaxPaymentEmailNotify().equals(bank.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_PLATFORM_BANK_NAME.getValue(), portletState.getSelectedBankName());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(bank.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}

					if(currency!=null && !portletState.getSelectedEtaxPaymentSmsNotify().equals(currency.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_SYSTEM_CURRENCY.getValue(), portletState.getSelectedcurrency());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(currency.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}



					if(bpwsu!=null && !portletState.getSelectedPlatformCountry().equals(bpwsu.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_BANK_PAYMENT_WS_URL.getValue(), portletState.getSelectedBankPaymentWebServiceUrl());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(bpwsu.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}


					if(zwsu!=null && !portletState.getSelectedPlatformBank().equals(zwsu.getValue()))
					{
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(SmartPayConstants.SETTINGS_TAX_BODY_WS_URL.getValue(), portletState.getSelectedZraWebServiceUrl());
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.SETTINGS_UPDATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(zwsu.getId());
						aft.setEntityName(Settings.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						processComplete = true;
					}
					
					
					if(processComplete)
					{
						Collection<PortalUser> pus = portletState.getSettingsManagementPortletUtil().getApprovingPortalUsers(
								portletState.getPortalUser().getRoleType().getRoleTypeName());
						
						
						for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
						{
							PortalUser pu1 = it.next();
							emailer.emailApprovalRequest(
									pu1.getFirstName(), 
									pu1.getLastName(), 
									pu1.getEmailAddress(), 
									portletState.getSystemUrl().getValue(), 
									portletState.getApplicationName().getValue() + 
									"- Approval Request for the Update of System Settings", 
									portletState.getApplicationName().getValue());
						}
						
						for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
						{
							PortalUser pu1 = it.next();
							String message = "Approval request awaiting your action. " +
									"Visit " + portletState.getSystemUrl().getValue() + " to view requests awaiting your " +
									"approval/disapproval action";
							SendSms sendSms = new SendSms(pu1.getMobileNumber(), message, 
									portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
							
							
						}
						aRes.setRenderParameter("jspPage", "/html/settingsmanagementportlet/settings/managesettings.jsp");
						portletState.addSuccess(aReq, "System Settings Update request has been created in the approval work flow.", portletState);
						portletState.reinitializeForSettings(portletState);
						portletState.setCurrentTab(VIEW_TABS.VIEW_SETTINGS);
					}else
					{
						aRes.setRenderParameter("jspPage", "/html/settingsmanagementportlet/settings/managesettings.jsp");
						portletState.addError(aReq, "System Settings Update request was not saved as no changes have been made.", portletState);
						portletState.reinitializeForSettings(portletState);
						portletState.setCurrentTab(VIEW_TABS.VIEW_SETTINGS);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					aRes.setRenderParameter("jspPage", "/html/settingsmanagementportlet/settings/managesettings.jsp");
					portletState.addError(aReq, "System Settings Update request was not saved as no changes have been made.", portletState);
					portletState.reinitializeForSettings(portletState);
					portletState.setCurrentTab(VIEW_TABS.VIEW_SETTINGS);
				}
				
				
			}else if(portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("0"))
			{
				if(sys!=null)
				{
					sys.setValue(portletState.getSelectedSystemUrl());
					sys.setStatus(Boolean.TRUE);
					swpService.updateRecord(sys);
					handleAudit("UPDATE SETTINGS - " + sys.getName(), Long.toString(sys.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				}
				
				if(pc!=null)
				{
					pc.setValue(portletState.getSelectedPlatformCountry());
					pc.setStatus(Boolean.TRUE);
					swpService.updateRecord(pc);
					handleAudit("UPDATE SETTINGS - " + pc.getName(), Long.toString(pc.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				}
				
				if(pb!=null)
				{
					pb.setValue(portletState.getSelectedPlatformBank());
					pb.setStatus(Boolean.TRUE);
					swpService.updateRecord(pb);
					handleAudit("UPDATE SETTINGS - " + pb.getName(), Long.toString(pb.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				}
				
				if(tca!=null)
				{
					tca.setValue(portletState.getSelectedTaxCompanyAccount());
					tca.setStatus(Boolean.TRUE);
					swpService.updateRecord(tca);
					handleAudit("UPDATE SETTINGS - " + tca.getName(), Long.toString(tca.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				}
				
				if(tcsc!=null)
				{
					tcsc.setValue(portletState.getSelectedTaxCompanySortCode());
					tcsc.setStatus(Boolean.TRUE);
					swpService.updateRecord(tcsc);
					handleAudit("UPDATE SETTINGS - " + tcsc.getName(), Long.toString(tcsc.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				}
				
				if(se!=null)
				{
					se.setValue(portletState.getSelectedFeeDescription());
					se.setStatus(Boolean.TRUE);
					swpService.updateRecord(se);
					handleAudit("UPDATE SETTINGS - " + se.getName(), Long.toString(se.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				}
				
				if(sep!=null)
				{
					sep.setValue(portletState.getSelectedSendingEmailPort());
					sep.setStatus(Boolean.TRUE);
					swpService.updateRecord(sep);
					handleAudit("UPDATE SETTINGS - " + sep.getName(), Long.toString(sep.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				}
				
				if(seu!=null)
				{
					seu.setValue(portletState.getSelectedSendingEmailUsername());
					seu.setStatus(Boolean.TRUE);
					swpService.updateRecord(seu);
					handleAudit("UPDATE SETTINGS - " + seu.getName(), Long.toString(seu.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				}
				
				
				if(tsl!=null)
				{
					tsl.setValue(portletState.getSelectedTwoStepLogin());
					tsl.setStatus(Boolean.TRUE);
					swpService.updateRecord(tsl);
					handleAudit("UPDATE SETTINGS - " + tsl.getName(), Long.toString(tsl.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				}
				
				if(ap!=null)
				{
					ap.setValue(portletState.getSelectedApprovalProcess());
					ap.setStatus(Boolean.TRUE);
					swpService.updateRecord(ap);
					handleAudit("UPDATE SETTINGS - " + ap.getName(), Long.toString(ap.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				}
				
				if(sepo!=null)
				{
					sepo.setValue(portletState.getSelectedSendingEmailPort());
					sepo.setStatus(Boolean.TRUE);
					swpService.updateRecord(sepo);
					handleAudit("UPDATE SETTINGS - " + sepo.getName(), Long.toString(sepo.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				}
				
				if(pfs!=null)
				{
					pfs.setValue(portletState.getSelectedFeeDescription());
					pfs.setStatus(Boolean.TRUE);
					swpService.updateRecord(pfs);
					handleAudit("UPDATE SETTINGS - " + pfs.getName(), Long.toString(pfs.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				}
					
				if(ncfm!=null)
				{
					ncfm.setValue(portletState.getSelectedEmailNotificationForCorporateFirm());
					ncfm.setStatus(Boolean.TRUE);
					swpService.updateRecord(ncfm);
					handleAudit("UPDATE SETTINGS - " + ncfm.getName(), Long.toString(ncfm.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				}
				
				if(ncfs!=null)
				{
					ncfs.setValue(portletState.getSelectedMobileNotificationForCorporateFirm());
					ncfs.setStatus(Boolean.TRUE);
					swpService.updateRecord(ncfs);
					handleAudit("UPDATE SETTINGS - " + ncfs.getName(), Long.toString(ncfs.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				}
				
				if(ncim!=null)
				{
					ncim.setValue(portletState.getSelectedEmailNotificationForCorporateIndivididuals());
					ncim.setStatus(Boolean.TRUE);
					swpService.updateRecord(ncim);
					handleAudit("UPDATE SETTINGS - " + ncim.getName(), Long.toString(ncim.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				}
				
				if(ncis!=null)
				{
					ncis.setValue(portletState.getSelectedMobileNotificationForCorporateIndivididuals());
					ncis.setStatus(Boolean.TRUE);
					swpService.updateRecord(ncis);
					handleAudit("UPDATE SETTINGS - " + ncis.getName(), Long.toString(ncis.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				}
				
				if(epnm!=null)
				{
					epnm.setValue(portletState.getSelectedEtaxPaymentEmailNotify());
					epnm.setStatus(Boolean.TRUE);
					swpService.updateRecord(epnm);
					handleAudit("UPDATE SETTINGS - " + epnm.getName(), Long.toString(epnm.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				}
				
				if(epns!=null)
				{
					epns.setValue(portletState.getSelectedEtaxPaymentSmsNotify());
					epns.setStatus(Boolean.TRUE);
					swpService.updateRecord(epns);
					handleAudit("UPDATE SETTINGS - " + epns.getName(), Long.toString(epns.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				}
				
				aRes.setRenderParameter("jspPage", "/html/settingsmanagementportlet/settings/viewsettings.jsp");
				portletState.addSuccess(aReq, "System Settings have been updated successfully!", portletState);
				portletState.reinitializeForSettings(portletState);
				portletState.setCurrentTab(VIEW_TABS.VIEW_SETTINGS);
			}
			
			
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/settingsmanagementportlet/settings/managesettings.jsp");
		}
	}
	
	

	
	private void handleNavigations(ActionRequest aReq, ActionResponse aRes,
			SettingsManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String action = aReq.getParameter("actionUrl");
		if(action.equalsIgnoreCase("adminsettings"))
		{
			aRes.setRenderParameter("jspPage", "/html/settingsmanagementportlet/settings/managesettings.jsp");
			portletState.setCurrentTab(VIEW_TABS.MANAGE_SETTINGS);
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

	
	private boolean isSettingsDataValid(SettingsManagementPortletState portletState,
			ActionRequest aReq, ActionResponse aRes, boolean checkExistingForNew) {
		// TODO Auto-generated method stub
		String errorMessage = null;
		Util util = new Util();
		
		if(portletState.getSelectedFeeDescription()!=null && !portletState.getSelectedFeeDescription().equals("-1"))
		{
			if(portletState.getSelectedEmailNotificationForCorporateFirm()!=null && !portletState.getSelectedEmailNotificationForCorporateFirm().equals("-1"))
			{
				if(portletState.getSelectedMobileNotificationForCorporateFirm()!=null && !portletState.getSelectedMobileNotificationForCorporateFirm().equals("-1"))
				{
					if(portletState.getSelectedEmailNotificationForCorporateIndivididuals()!=null && !portletState.getSelectedEmailNotificationForCorporateIndivididuals().equals("-1"))
					{
						if(portletState.getSelectedMobileNotificationForCorporateIndivididuals()!=null && !portletState.getSelectedMobileNotificationForCorporateIndivididuals().equals("-1"))
						{
							if(portletState.getSelectedSystemUrl()!=null && portletState.getSelectedSystemUrl().length()>0)
							{
								if(portletState.getSelectedPlatformCountry()!=null && portletState.getSelectedPlatformCountry().length()>0)
								{
									if(portletState.getSelectedPlatformBank()!=null && portletState.getSelectedPlatformBank().length()>0)
									{
										if(portletState.getSelectedTaxCompanyAccount()!=null && portletState.getSelectedTaxCompanyAccount().length()>0)
										{
											if(portletState.getSelectedTaxCompanySortCode()!=null && portletState.getSelectedTaxCompanySortCode().length()>0)
											{
												if(portletState.getSelectedSendingEmail()!=null && portletState.getSelectedSendingEmail().length()>0)
												{
													if(portletState.getSelectedSendingEmailPassword()!=null && portletState.getSelectedSendingEmailPassword().length()>0)
													{
														if(portletState.getSelectedSendingEmailPort()!=null && portletState.getSelectedSendingEmailPort().length()>0)
														{
															if(portletState.getSelectedSendingEmailUsername()!=null && portletState.getSelectedSendingEmailUsername().length()>0)
															{
																if(portletState.getSelectedApprovalProcess()!=null && !portletState.getSelectedApprovalProcess().equals("-1"))
																{
																	if(portletState.getSelectedTwoStepLogin()!=null && !portletState.getSelectedTwoStepLogin().equals("-1"))
																	{
																		
																	}
																	else
																	{
																		errorMessage =  "Specify if you want two-step authentication for critical processes turned on";
																	}
																}
																else
																{
																	errorMessage =  "Specify if you want approval process for critical processes turned on";
																}
															}
															else
															{
																errorMessage =  "Specify the email username to be used for sending email notifications ";
															}
														}
														else
														{
															errorMessage =  "Specify the port to be used when sending emails. Usually Port 25 or 465";
														}
													}
													else
													{
														errorMessage =  "Specify the password of the email to be used for sending email notifications";
													}
												}
												else
												{
													errorMessage =  "Specify the email to be used for sending email notifications";
												}
											}
											else
											{
												errorMessage =  "Specify the bank sort code for the Tax collection office e.g. ZRA Suspense collection bank Account sort code";
											}
										}
										else
										{
											errorMessage =  "Specify the bank account number for the Tax collection office e.g. ZRA Suspense collection Bank Account Number";
										}
									}
									else
									{
										errorMessage =  "Specify the bank code this application is deployed for e.g. SCB for Standard Chartered Bank";
									}
								}
								else
								{
									errorMessage =  "Specify the country code where this application is deployed. e.g ZM for Zambia, KE for Kenya";
								}
							}
							else
							{
								errorMessage =  "Specify the URL to access this application in the System URL field";
							}
						}else
						{
							errorMessage =  "Specify if you want sms notifications to be sent to corporate individuals on account creation";
						}
					}else
					{
						errorMessage =  "Specify if you want email notifications to be sent to corporate individuals on account creation";
					}
				}else
				{
					errorMessage =  "Specify if you want sms notifications to be sent to corporate firms when a company is created on the platform";
				}
			}else
			{
				errorMessage =  "Specify if you want email notifications to be sent to corporate firms when a company is created on the platform";
			}
		}else
		{
			errorMessage =  "Specify the primary fee charge for " + portletState.getApplicationName().getValue() + " payments on the platform";
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
