package com.probase.smartpay.commins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PrivilegedActionException;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSSecurityEngine;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.message.WSSAddUsernameToken;
import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.WSSecSignature;
import org.apache.ws.security.message.WSSecUsernameToken;
import org.apache.ws.security.message.WSSignEnvelope;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import smartpay.entity.PaymentBreakDownHistory;
import smartpay.entity.PaymentHistory;
import smartpay.entity.PaymentTempHolder;
import smartpay.entity.PortalUser;
import smartpay.entity.Settings;
import smartpay.entity.TaxType;
import smartpay.entity.TaxTypeAccount;
import smartpay.entity.enumerations.PaymentBreakDownHistoryConstants;
import smartpay.entity.enumerations.PaymentHistoryConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.service.SwpService;

import com.onbarcode.barcode.Codabar;
import com.sf.primepay.smartpay13.ServiceLocator;

public class EndOfDayDomT implements Job {

	
	Logger log = Logger.getLogger(EndOfDayDomT.class);
	private Boolean mode = Boolean.FALSE;
	ServiceLocator serviceLocator = ServiceLocator.getInstance();
	SwpService swpService = serviceLocator.getSwpService();
	PrbCustomService swpCustomService = PrbCustomService.getInstance();
	SOAPConnectionFactory soapConnectionFactory;
	SOAPConnection soapConnection;
	WSSecurityEngine secEngine = new WSSecurityEngine();
	
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub

//		PrbCustomService pcs = PrbCustomService.getInstance();
//		List<Settings> allSettings = pcs.getValidSettings();
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
//			String hql = "Select st FROM Settings st where st.status = 'true'";
//			Collection<Settings> allSettings = (Collection<Settings>)swpService.getAllRecordsByHQL(hql);
//			for(Iterator<Settings> iter = allSettings.iterator(); iter.hasNext();)
//			{
//				Settings temp = iter.next();
//				if(temp.getName().equals(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_NUMBER))
//				{
//					log.info("SETTINGS_ZRA_BANK_ACCOUNT_NUMBER = " + temp.getId());
//					zraAccount = temp;
//				}
//				if(temp.getName().equals(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_SORT_CODE))
//				{
//					log.info("SETTINGS_ZRA_BANK_ACCOUNT_SORT_CODE = " + temp.getId());
//					zraSortCode = temp;
//				}
//				if(temp.getName().equals(SmartPayConstants.SETTINGS_PLATFORM_COUNTRY))
//				{
//					log.info("Platform country = " + temp.getId());
//					currency = temp;
//				}
//				if(temp.getName().equals(SmartPayConstants.SETTINGS_APPLICATION_NAME))
//				{
//					log.info("Platform sysApplication = " + temp.getId());
//					sysApplication = temp;
//				}
//				
//				
//				if(temp.getName().equals(SmartPayConstants.SETTINGS_PROXY_HOST))
//				{
//					log.info("SETTINGS_ZRA_BANK_ACCOUNT_NUMBER = " + temp.getId());
//					proxyHost = temp;
//				}
//				if(temp.getName().equals(SmartPayConstants.SETTINGS_PROXY_USERNAME))
//				{
//					log.info("SETTINGS_ZRA_BANK_ACCOUNT_SORT_CODE = " + temp.getId());
//					proxyUsername = temp;
//				}
//				if(temp.getName().equals(SmartPayConstants.SETTINGS_PROXY_PASSWORD))
//				{
//					log.info("Platform country = " + temp.getId());
//					proxyPassword = temp;
//				}
//				if(temp.getName().equals(SmartPayConstants.SETTINGS_PROXY_PORT))
//				{
//					log.info("SETTINGS_ZRA_BANK_ACCOUNT_NUMBER = " + temp.getId());
//					proxyPort = temp;
//				}
//				if(temp.getName().equals(SmartPayConstants.SETTINGS_BANK_PAYMENT_WS_URL))
//				{
//					log.info("Platform country = " + temp.getId());
//					bankPaymentUrl = temp;
//				}
//			}
			
			
//			if(currency!=null && zraSortCode!=null && zraAccount!=null)
//			{
//				List<PaymentBreakDownHistory> paymentBreakDownHistory = null;
				
//				paymentBreakDownHistory = pcs.getPaymentBreakDownHistory(
//						PaymentBreakDownHistoryConstants.PAYMENT_BREAKDOWN_STATUS_PENDING.getValue(), 
//						PaymentHistoryConstants.PAYMENTHISTORY_STATUS_ZRA_CONFIRMED.getValue());
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
						    			SOAPBody soapBody = envelope.getBody();
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
//						    		        MimeHeaders headers = soapMessage.getMimeHeaders();
//						    		        headers.addHeader("SOAPAction", serverURI  + "getUnpaidAssessmentsByTPIN");
						    		        log.info("url14 = > ");
						    		        soapMessage.saveChanges();
						    		        log.info("url109 = > ");
						    		        
						    		        Logger l = Logger.getLogger(Util.class);
						    				l.info("44");
//						    				try
//						    				{
//						    					l.info("45");
////						    					@SuppressWarnings("deprecation")
//						    				    Document doc = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
//						    				    l.info("451");
//						    					WSSecHeader secHeader = new WSSecHeader();
//						    					l.info("452");
//						    			        secHeader.insertSecurityHeader(doc);
//						    			        l.info("453");
//						    			        WSSAddUsernameToken builder = new WSSAddUsernameToken("", false);
//						    			        l.info("4531");
//						    			        builder.setPasswordType(WSConstants.PASSWORD_TEXT);
//						    			        l.info("4532");
//						    			        builder.setUserInfo("wernerd", "verySecret");
//						    			        l.info("4533");
////						    			        builder.addCreated();
////						    			        l.info("4534");
////						    			        builder.addNonce();
////						    			        l.info("4535");
////						    			        builder.prepare(doc);
//						    			        builder.build(doc, "wernerd", "verySecret");
//						    			        l.info("4536");
//						    					WSSecSignature sign = new WSSecSignature();
//						    					l.info("454");
//						    					
////						    					sign.setUsernameToken(builder);
//						    			        sign.setKeyIdentifierType(WSConstants.UT_SIGNING);
//						    			        sign.setSignatureAlgorithm(XMLSignature.ALGO_ID_MAC_HMAC_SHA1);
//						    			        log.info("Before signing with UT text....");
//						    			        sign.build(doc, null, secHeader);
//						    			        log.info("Before adding UsernameToken PW Text....");
//						    			        //builder.prependToHeader(secHeader);
//						    			        Document signedDoc = doc;
//						    			        log.debug("Message with UserNameToken PW Text:");
//						    			            String outputString = 
//						    			                org.apache.ws.security.util.XMLUtils.PrettyDocumentToString(signedDoc);
//						    			            log.debug(outputString);
//						    			        log.info("After adding UsernameToken PW Text....");
//						    			        //verify(signedDoc);
//						    				}catch(Exception e)
//						    				{
//						    					e.printStackTrace();
////						    					return null;
//						    				}
//						    				
////						    		        soapMessage = Util1.signSOAPEnvelope(soapMessage.getSOAPPart().getEnvelope());
//						    		        soapMessage.saveChanges();
//						    		        log.info("44");
//						    				try
//						    				{
//						    					log.info("45");
//						    					@SuppressWarnings("deprecation")
//						    					WSSignEnvelope signer = new WSSignEnvelope();
//						    					Codabar c = new Codabar();
//						    					log.info("451");
//						    					String alias = "privkey"; //"16c73ab6-b892-458f-abf5-2f875f74882e";
//						    					log.info("452");
//						    				    String password = "changeit";
//						    				    log.info("453");
//						    				    signer.setUserInfo(alias, password);
//						    				    log.info("454");
//						    				    Document doc = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
//						    				    log.info("455");
//						    				    Document signedDoc = signer.build(doc, crypto);
//						    				    SOAPMessage signedSOAPMsg = SOAPUtil.toSOAPMessage(signedDoc);
//						    				    log.info("46");
//						    				   // return signedSOAPMsg;
//						    				}catch(Exception e)
//						    				{
//						    					e.printStackTrace();
//						    				//	return null;
//						    				}
						    	        }
						    			
						    			System.out.print("Request SOAP Message for Probase Service = ");
						            	soapMessage.writeTo(System.out);
						            	
						    			String keystoreType = "JKS";
						    			String keystoreFile = "C:" + File.separator + "g_d"  + File.separator + "test_tax_keystore";
						    			String keystorePass = "changeit";
						    			String privateKeyAlias = "privkey";
						    			String privateKeyPass = "changeit";
						    			String certificateAlias = "test_tax_key";
						    			Element element = null;
						    			KeyStore ks = KeyStore.getInstance(keystoreType);
						    			FileInputStream fis = new FileInputStream(keystoreFile);
						    			ks.load(fis, keystorePass.toCharArray());
						    			PrivateKey privateKey = (PrivateKey) ks.getKey(privateKeyAlias, privateKeyPass.toCharArray());
						    			javax.xml.parsers.DocumentBuilderFactory dbf =
						    			javax.xml.parsers.DocumentBuilderFactory.newInstance();
						    			dbf.setNamespaceAware(true);
						    			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
						    			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//						    			Document doc = dBuilder.parse(attachmentFile);
						    			Document doc = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
						    			File signatureFile = new File("C:"  + File.separator + "jcodes"  + File.separator + "signature.xml");
						    			String BaseURI = signatureFile.toURI().toURL().toString();
						    			XMLSignature sig = new XMLSignature(doc, BaseURI, XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);
						    			element = doc.getDocumentElement();
						    			element.normalize();
						    			element.getElementsByTagName("soap:Header").item(0).appendChild(sig.getElement());
						    			{
						    				Transforms transforms = new Transforms(doc);
						    				transforms.addTransform(Transforms.TRANSFORM_C14N_OMIT_COMMENTS);
						    				sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);
						    				sig.addDocument("../resources/attachment.xml", transforms, Constants.ALGO_ID_DIGEST_SHA1);
						    			}
						    			
						    			{
						    				X509Certificate cert = (X509Certificate) ks.getCertificate(certificateAlias);
						    				sig.addKeyInfo(cert);
						    				sig.addKeyInfo(cert.getPublicKey());
						    				sig.sign(privateKey);
						    			}
						    			
						    			FileOutputStream f = new FileOutputStream(signatureFile);
						    			XMLUtils.outputDOMc14nWithComments(doc, f);
						    			f.close();




						            /* Print the request message */
						            	System.out.print("Request SOAP Message for Probase Service = ");
						            	soapMessage.writeTo(System.out);

//						            	return soapMessage;
						    		}
						    		catch(Exception e)
						    		{
						    			e.printStackTrace();
						    			MessageFactory messageFactory;
//						    			SOAPMessage soapMessage = null;
						    			try {
						    				messageFactory = MessageFactory.newInstance();
						    				
						    				try {
						    					soapMessage = messageFactory.createMessage();
						    				} catch (SOAPException e1) {
						    					// TODO Auto-generated catch block
						    					e1.printStackTrace();
//						    					return null;
						    				}
						    			} catch (SOAPException e2) {
						    				// TODO Auto-generated catch block
						    				e2.printStackTrace();
//						    				return null;
						    			}
						    			
//						    			return soapMessage;
						    		}
//						            soapResponse = soapConnection.call(
//						            		Util.createSOAPRequestForEndOfDayDomT(paymentTempHolder), url);
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
				
				
//			}
					
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		
		
		
		
//		List<TaxType> allTaxType = pcs.getTaxTypes();
		
		
				
					
//					PaymentTempHolder pth = new PaymentTempHolder();
//					pth.setPaymentBreakDownHistory(pbdh);
//					pth.setDateAdded(new Timestamp((new Date()).getTime()));
//					pth = (PaymentTempHolder)pcs.createNewRecord(pth);
//					log.info("pth id = " + pth.getId());
					
				
//		String hql = "Select st FROM Settings where st.status = 'true'";
//		Collection<Settings> allSettings = (Collection<Settings>)swpService.getAllRecordsByHQL(hql);
//		Settings zraAccount = null;
//		Settings zraSortCode = null;
//		Settings currency = null;
//		
//		for(Iterator<Settings> iter = allSettings.iterator(); iter.hasNext();)
//		{
//			Settings temp = iter.next();
//			if(temp.getName().equals(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_NUMBER))
//				zraAccount = temp;
//			if(temp.getName().equals(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_SORT_CODE))
//				zraSortCode = temp;
//			if(temp.getName().equals(SmartPayConstants.SETTINGS_PLATFORM_COUNTRY))
//				currency = temp;
//		}
//		
//		
//		hql = "Select st FROM TaxType";
//		Collection<TaxType> allTaxType = (Collection<TaxType>)swpService.getAllRecordsByHQL(hql);
//		
//		
//		if(currency!=null && zraSortCode!=null && zraAccount!=null)
//		{
//			Collection<PaymentBreakDownHistory> paymentBreakDownHistory = null;
//			hql = "Select top 100 pbdh FROM PaymentBreakDownHistory pbdh " +
//					" WHERE pbdh.id not in (Select pth.paymentBreakDownHistory.id FROM " +
//					"PaymentTempHolder pth) AND lower(pbdh.status) = lower('" + 
//					PaymentBreakDownHistoryConstants.PAYMENT_BREAKDOWN_STATUS_PENDING.getValue() + "') " +
//					" AND lower(pbdh.paymentHistory.status) = " +
//					" lower('" + PaymentHistoryConstants.PAYMENTHISTORY_STATUS_ZRA_CONFIRMED.getValue() + "') " +
//					" AND pbdh.id !=  ORDER by pbdh.entryDate ASC";
//			log.info("hql = " + hql);
//			
//			paymentBreakDownHistory = (Collection<PaymentBreakDownHistory>) swpService.getAllRecordsByHQL(hql);
//			if(paymentBreakDownHistory != null){
//				log.info("paymentBreakDownHistory Id = "+paymentBreakDownHistory.size());
//				for(Iterator<PaymentBreakDownHistory> pbdhIter = paymentBreakDownHistory.iterator(); pbdhIter.hasNext();)
//				{
//					PaymentBreakDownHistory pbdh = pbdhIter.next();
//					log.info("pbdh id = " + pbdh.getId());
//					PaymentTempHolder pth = new PaymentTempHolder();
//					pth.setPaymentBreakDownHistory(pbdh);
//					pth.setDateAdded(new Timestamp((new Date()).getTime()));
//					pth = (PaymentTempHolder)swpService.createNewRecord(pth);
//					TaxType tt = null;
//					TaxTypeAccount tta = null;
//					for(Iterator<TaxType> ttIter = allTaxType.iterator(); ttIter.hasNext();)
//					{
//						TaxType taxType = ttIter.next();
//						if(taxType.getId().equals(pbdh.getTaxType().getId()))
//						{
//							tt = pbdh.getTaxType();
//							hql = "Select tta From TaxTypeAccount tta where lower(tta.taxType.taxCode) = " +
//									"lower('" + tt.getTaxCode() + "')";
//							log.info("hql = " + hql);
//							tta = (TaxTypeAccount)swpService.getUniqueRecordByHQL(hql);
//							break;
//						}
//					}
//					if(tt!=null && tta!=null)
//					{
//						log.info("tta id = " + tta.getId());
//						log.info("tt id = " + tt.getId());
//						hql = "Select st FROM TaxTypeAccount where st.taxType.id = " + tt.getId();
//						log.info("hql = " + hql);
//						TaxTypeAccount taxTypeAccount = (TaxTypeAccount)swpService.getUniqueRecordByHQL(hql);
//						if(taxTypeAccount!=null)
//						{
//							DateFormat df = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss:S");
//					        DateFormat df1 = new SimpleDateFormat( "SssmmHHddMMyy");
//					        DateFormat df2 = new SimpleDateFormat( "yyyyMMdd");
//							DateFormat df3 = new SimpleDateFormat( "Sssmm");
//					        String currentTimeStamp = df.format(new Date());
//					        String currentTimeStamp1 = df1.format(new Date());
//					        String currentTimeStamp2 = df2.format(new Date());
//					        String currentTimeStamp3 = df3.format(new Date());
//					        String uniqId = "SPI" +  currentTimeStamp1;
//					        
//					        
//					        log.info("currentTimeStamp1 = " + currentTimeStamp1);
//					        log.info("currentTimeStamp = " + currentTimeStamp);
//					        log.info("uniqId = " + uniqId);
//					        log.info("zraAccount = " + zraAccount.getValue());
//					        log.info("zraSortCode = " + zraSortCode.getValue());
//					        log.info("pbdh = " + Double.toString(pbdh.getAmount()));
//					        log.info("currentTimeStamp2 = " + currentTimeStamp2);
//					        log.info("currentTimeStamp3 = " + currentTimeStamp3);
//					        log.info("tta.getAccountNumber = " + tta.getAccountNumber());
//					        log.info("tta.getAccountSortCode = " + tta.getAccountSortCode());
//							
//							
//					        FundsTransferResponse ftr= null;
//							try {
//								if(this.mode .equals(Boolean.FALSE))
//								{
//									
//									ftr = Util.doFundsTransfer("SmartPay", "SMARTPAY:ZM:REVENUE:ENTRY:REQUEST", 
//										  currentTimeStamp1, currentTimeStamp, uniqId, 
//										"ZMW", "1095", zraAccount.getValue(), 
//										zraSortCode.getValue(), Double.toString(pbdh.getAmount()), currentTimeStamp2, 
//										currentTimeStamp3, "00-00-00", tta.getAccountNumber(), 
//										tta.getAccountSortCode());
//									
//								}
//								else
//								{
//									ftr = new FundsTransferResponse();
//									ftr.setAccountNumber(zraAccount.getValue());
//									ftr.setResMessageId(RandomStringUtils.random(8).toUpperCase());
//									ftr.setResTimeStamp(Boolean.TRUE);
//									ftr.setStatus(Boolean.TRUE);
//								}
//								
//								if(ftr!=null)
//								{
//									if(ftr.getStatus().equals(Boolean.TRUE))
//									{
//										pbdh.setStatus(PaymentBreakDownHistoryConstants.PAYMENT_BREAKDOWN_STATUS_APPROVED);
//										pbdh.setDateOfTransaction(new Timestamp((new Date()).getTime()));
//										pbdh.setReceipientAccountNumber(tta.getAccountNumber());
//										pbdh.setSourceAccountNumber(zraAccount.getValue());
//										pbdh.setTransactionNumber(ftr.getResMessageId());
//										swpService.deleteRecord(pth);
//										swpService.updateRecord(pbdh);
//										
//									}
//								}
//							} catch (MalformedURLException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//					}
//				}
//					
//			}
//		}
		
	}

}
