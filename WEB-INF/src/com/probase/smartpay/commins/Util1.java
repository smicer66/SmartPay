package com.probase.smartpay.commins;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.log4j.Logger;
import org.apache.ws.security.WSSecurityEngine;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.WSSecSignature;
import org.apache.ws.security.message.WSSignEnvelope;
import org.w3c.dom.Document;

import smartpay.entity.PaymentTempHolder;
import smartpay.entity.enumerations.PaymentHistoryConstants;

public class Util1 {

	Logger log = Logger.getLogger(Util.class);
	private static final WSSecurityEngine secEngine = new WSSecurityEngine();
	private static final Crypto crypto = CryptoFactory.getInstance();
	
	
	
	public static SOAPMessage signSOAPEnvelope(SOAPEnvelope unsignedEnvelope)
	{
		Logger l = Logger.getLogger(Util.class);
		l.info("44");
		try
		{
			l.info("45");
//			@SuppressWarnings("deprecation")
		    Document doc = unsignedEnvelope.getOwnerDocument();
			WSSecHeader secHeader = new WSSecHeader();
	        secHeader.insertSecurityHeader(doc);
			WSSecSignature sign = new WSSecSignature();
			
//			WSSignEnvelope signer = new WSSignEnvelope();
			String alias = "privkey"; //"16c73ab6-b892-458f-abf5-2f875f74882e";
		    String password = "changeit";
		    sign.setUserInfo(alias, password);
		    sign.prepare(doc, crypto, secHeader);
//		    signer.setUserInfo(alias, password);
//		    Document signedDoc = signer.build(doc, crypto);
//		    SOAPMessage signedSOAPMsg = SOAPUtil.toSOAPMessage(signedDoc);
		    l.info("46");
		    return SOAPUtil.toSOAPMessage(doc);
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	
	public SOAPMessage createSOAPRequestForEndOfDayDomT(Collection<PaymentTempHolder> paymentTempHolder)
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
		        
		        try
				{
					log.info("45");
					@SuppressWarnings("deprecation")
					WSSignEnvelope signer = new WSSignEnvelope();
					String alias = "privkey"; //"16c73ab6-b892-458f-abf5-2f875f74882e";
				    String password = "changeit";
				    signer.setUserInfo(alias, password);
				    Document doc = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
				    Document signedDoc = signer.build(doc, crypto);
				    soapMessage = SOAPUtil.toSOAPMessage(signedDoc);
				    log.info("46");
//				    return signedSOAPMsg;
				}catch(Exception e)
				{
					e.printStackTrace();
//					return null;
				}
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
	
}
