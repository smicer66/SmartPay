package com.probase.smartpay.commins;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

import javax.naming.NamingException;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import lombok.extern.log4j.Log4j;

import common.Logger;


import java.io.IOException;
import java.sql.Timestamp;
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

import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.HibernateException;

import smartpay.audittrail.AuditTrail;
import smartpay.entity.Assessment;
import smartpay.entity.Balance;
import smartpay.entity.Company;
import smartpay.entity.CompanyCRUDRights;
import smartpay.entity.PortalUser;
import smartpay.entity.PortalUserCRUDRights;
import smartpay.entity.Ports;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.TpinInfo;
import smartpay.entity.enumerations.CompanyTypeConstants;
import smartpay.entity.enumerations.PortalUserStatusConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.exception.SwpException;
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
import com.probase.smartpay.commins.Emailer;
import com.probase.smartpay.commins.ProbaseConstants;
import com.probase.smartpay.commins.SendMail;
import com.probase.smartpay.commins.Util;
import com.sf.primepay.smartpay13.HibernateUtils;
import com.sf.primepay.smartpay13.ServiceLocator;



@Log4j
public class ComminsApplicationState implements Serializable {

	public static final int BANC_ABC = 1;
	public static final int STB = 0;
	static Logger log = Logger.getLogger(ComminsApplicationState.class);
	//
	private Boolean loggedIn = null;
	private PortalUser pu = null;
	private Boolean demoMode_1 = Boolean.FALSE;
	private Boolean demoModeBalance_1 = Boolean.FALSE;
	private Boolean demoModePay_1 = Boolean.FALSE;
	private static ServiceLocator serviceLocator = ServiceLocator.getInstance();
	private static SwpService swpService = serviceLocator.getSwpService();
	public static int SMS_SEND = 1;
	
	
	private Settings zraAccount;
	private Settings zraSortCode;
	private Settings currency;
	private String proxyHost=null;
	private String proxyPort=null;
	
	private Balance balance =null;
	
	
	private ComminsApplicationState() {

    }
	

	
    public static ComminsApplicationState getInstance(PortletRequest request,PortletResponse response) {

        //ServiceLocator serviceLocator = (ServiceLocator) pContext.getAttribute("serviceLocator");
        //ResourceBundle bundle = (ResourceBundle) pContext.getAttribute("bundle");        

    	ComminsApplicationState applicationState = null;
        try {
            PortletSession session = request.getPortletSession();
            applicationState = (ComminsApplicationState) session.getAttribute(ComminsApplicationState.class
                    .getName(), PortletSession.APPLICATION_SCOPE);

            if (applicationState == null) {

                applicationState = new ComminsApplicationState();
                /* add state initialization codes here */
                log.info("--------------->>>> im in the process application state!!!");
                
                session.setAttribute(ComminsApplicationState.class.getName(), applicationState, PortletSession.APPLICATION_SCOPE);
                
                loadProxy();
                loadDemoMode(applicationState);
                loadDemoModeBalance(applicationState);
                loadDemoModePay(applicationState);
            }

            return applicationState;
        } catch (Exception e) {
		System.out.println("Error while getting application state");
            //PortalLogger.error("Error while getting application state", e);
            e.printStackTrace();
            return null;
        }
    }





	private static void loadDemoMode(ComminsApplicationState portletState) {
		// TODO Auto-generated method stub
		String hql = "Select se.value from Settings se where lower(se.name) = lower('DEMO MODE ACTIVE')";
		String demoModeSettings = (String)swpService.getUniqueRecordByHQL(hql);
		System.out.println("demoModeSettings for DEMO MODE BALANCE ACTIVE = " +demoModeSettings);
		if(demoModeSettings!=null && demoModeSettings.equals("1"))
		{
			portletState.setDemoMode_1(Boolean.TRUE);
		}else
		{
			portletState.setDemoMode_1(Boolean.FALSE);
		}
	}
	
	
	private static void loadDemoModeBalance(ComminsApplicationState portletState) {
		// TODO Auto-generated method stub
		String hql = "Select se.value from Settings se where lower(se.name) = lower('DEMO MODE BALANCE ACTIVE')";
		String demoModeSettings = (String)swpService.getUniqueRecordByHQL(hql);
		System.out.println("demoModeSettings for DEMO MODE BALANCE ACTIVE = " +demoModeSettings);
		if(demoModeSettings!=null && demoModeSettings.equals("1"))
		{
			portletState.setDemoModeBalance(Boolean.TRUE);
		}else
		{
			portletState.setDemoModeBalance(Boolean.FALSE);
		}
	}
	
	private static void loadDemoModePay(ComminsApplicationState portletState) {
		// TODO Auto-generated method stub
		String hql = "Select se.value from Settings se where lower(se.name) = lower('DEMO MODE PAY ACTIVE')";
		String demoModeSettings = (String)swpService.getUniqueRecordByHQL(hql);
		System.out.println("demoModeSettings for DEMO MODE BALANCE ACTIVE = " +demoModeSettings);
		if(demoModeSettings!=null && demoModeSettings.equals("1"))
		{
			log.info("Set this value to true");
			portletState.setDemoModeBalance(Boolean.TRUE);
		}else
		{
			log.info("Set this value to false");
			portletState.setDemoModeBalance(Boolean.FALSE);
		}
	}



	private static void loadProxy() {
		// TODO Auto-generated method stub
		
	}



	public String getUniquePaymentReferenceId() {

        String uniqueGeneratedRefId = UUID.randomUUID().toString().replace("-", "");
        if (uniqueGeneratedRefId.length() < 16) {
            uniqueGeneratedRefId = uniqueGeneratedRefId + UUID.randomUUID().toString().replace("-", "");
        }
        uniqueGeneratedRefId = uniqueGeneratedRefId.substring(0, 4) + "-" + uniqueGeneratedRefId.substring(4, 9) + "-"
                + uniqueGeneratedRefId.substring(9, 13) + "-" + uniqueGeneratedRefId.substring(13, 18);
        
        //System.out.println("UUID = " + uniqueGeneratedRefId.toUpperCase() + "\n");
        
        return uniqueGeneratedRefId.toUpperCase();
	}
	public String getUniquePaymentTellerId() {

        String uniqueGeneratedRefId = UUID.randomUUID().toString().replace("-", "");
        if (uniqueGeneratedRefId.length() < 16) {
            uniqueGeneratedRefId = uniqueGeneratedRefId + UUID.randomUUID().toString().replace("-", "");
        }
        uniqueGeneratedRefId = uniqueGeneratedRefId.substring(0, 4) + "-" + uniqueGeneratedRefId.substring(4, 9) + "-"
               + uniqueGeneratedRefId.substring(9, 13) + "-" + uniqueGeneratedRefId.substring(13, 18);
        
       return uniqueGeneratedRefId.toUpperCase();
	}
	
	public Session getSession () throws HibernateException, NamingException {
		return HibernateUtils.getSessionFactory().openSession();
	}
	
	public void closeSession (Session session) {
		if (session != null) {
			session.close();
		}
	}



	
	public Boolean getLoggedIn() {
		return loggedIn;
	}



	public void setLoggedIn(Boolean loggedIn) {
		this.loggedIn = loggedIn;
	}



	public void setPortalUser(PortalUser pu) {
		// TODO Auto-generated method stub
		this.pu = pu;
	}
	
	
	public PortalUser getPortalUser() {
		// TODO Auto-generated method stub
		return this.pu;
	}



	public Boolean getDemoMode_1() {
		return demoMode_1;
	}



	public void setDemoMode_1(Boolean demoMode) {
		this.demoMode_1 = demoMode;
	}



	public BalanceInquiry getBalanceInquiry(PortalUser pu, SwpService swpService) {
		// TODO Auto-generated method stub
		Balance balance = null;
		
		
		try {
			
				String hql = "select rt from Balance rt where (" +
						"rt.company.id = " + pu.getCompany().getId() + ")";
				log.info("Get hql = " + hql);
				balance = (Balance) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		
		
		BalanceInquiry bi = null;
		if(balance!=null)
		{
			bi = new BalanceInquiry();
			bi.setAccountNumber(pu.getCompany().getAccountNumber());
			bi.setAvailableBalance(balance.getAmount());
			bi.setCurrency("ZM");
			bi.setResMessageId("10912812");
			bi.setResMessageType("Type");
			bi.setResSourceSystem("SCB");
			bi.setResTimeStamp(new Timestamp((new Date()).getTime()).toString());
			bi.setResTrackingId("12121232");
			bi.setStatus("1");
			bi.setType("Current Account");
		}
		
		return bi;
	}



	public Collection<Assessment> getAssessmentList(PortalUser portalUser, SwpService swpService) {
		// TODO Auto-generated method stub
		Collection<Assessment> assessmentList = new ArrayList<Assessment>();
		
		if(portalUser.getCompany().getClearingAgent()!=null && portalUser.getCompany().getClearingAgent().equals(Boolean.TRUE))
		{
			TpinInfo tpin = getTPINInfoByCompany(portalUser.getCompany().getId(), swpService);
			Ports ports = getPortByPortCode("NKO", swpService);
			Assessment assessment = new Assessment();
			assessment.setAmount(1000.00);
			assessment.setAssessmentNumber("1");
			assessment.setAssessmentYear(2014);
			assessment.setClientTpin("2842728937");
			assessment.setCountry("ZM");
			assessment.setCreateByPortalUserId(portalUser.getId());
			assessment.setDateRegistered(new Date().toString());
			assessment.setDeclarantTpin(tpin.getTpin());
			assessment.setInterest(Boolean.TRUE);
			assessment.setInterestAmount(500.00);
			assessment.setMovedToWorkFlow(false);
			assessment.setPaidFor(false);
			assessment.setPorts(ports);
			assessment.setRegistrationNumber("1");
			assessment.setRegistrationSerial("C");
			assessment.setSource("SCB");
			assessment.setSourceID("SCB");
			assessment.setTpinInfo(tpin);
			assessmentList.add(assessment);
			
			assessment = new Assessment();
			assessment.setAmount(2000.00);
			assessment.setAssessmentNumber("2");
			assessment.setAssessmentYear(2014);
			assessment.setClientTpin("8739128371");
			assessment.setCountry("ZM");
			assessment.setCreateByPortalUserId(portalUser.getId());
			assessment.setDateRegistered(new Date().toString());
			assessment.setDeclarantTpin(tpin.getTpin());
			assessment.setInterest(Boolean.TRUE);
			assessment.setInterestAmount(300.00);
			assessment.setMovedToWorkFlow(false);
			assessment.setPaidFor(false);
			assessment.setPorts(ports);
			assessment.setRegistrationNumber("2");
			assessment.setRegistrationSerial("C");
			assessment.setSource("SCB");
			assessment.setSourceID("SCB");
			assessment.setTpinInfo(tpin);
			assessmentList.add(assessment);
			
			assessment = new Assessment();
			assessment.setAmount(1500.00);
			assessment.setAssessmentNumber("3");
			assessment.setAssessmentYear(2014);
			assessment.setClientTpin("8372928274");
			assessment.setCountry("ZM");
			assessment.setCreateByPortalUserId(portalUser.getId());
			assessment.setDateRegistered(new Date().toString());
			assessment.setDeclarantTpin(tpin.getTpin());
			assessment.setInterest(Boolean.TRUE);
			assessment.setInterestAmount(340.00);
			assessment.setMovedToWorkFlow(false);
			assessment.setPaidFor(false);
			assessment.setPorts(ports);
			assessment.setRegistrationNumber("3");
			assessment.setRegistrationSerial("C");
			assessment.setSource("SCB");
			assessment.setSourceID("SCB");
			assessment.setTpinInfo(tpin);
			assessmentList.add(assessment);
		
			return assessmentList;
		}else
		{
			if(portalUser.getCompany().getClearingAgent()!=null && portalUser.getCompany().getClearingAgent().equals(Boolean.FALSE) 
					&& portalUser.getCompany().getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY))
			{

				TpinInfo tpinInfo = getTPINInfoByCompany(portalUser.getCompany().getId(), swpService);
				String tpin = tpinInfo.getTpin();
				Ports ports = getPortByPortCode("NKO", swpService);
				Assessment assessment = new Assessment();
				assessment.setAmount(1000.00);
				assessment.setAssessmentNumber("52");
				assessment.setAssessmentYear(2014);
				assessment.setClientTpin(tpin);
				assessment.setCountry("ZM");
				assessment.setCreateByPortalUserId(portalUser.getId());
				assessment.setDateRegistered(new Date().toString());
				assessment.setDeclarantTpin(tpin);
				assessment.setInterest(Boolean.TRUE);
				assessment.setInterestAmount(500.00);
				assessment.setMovedToWorkFlow(false);
				assessment.setPaidFor(false);
				assessment.setPorts(ports);
				assessment.setRegistrationNumber("52");
				assessment.setRegistrationSerial("C");
				assessment.setSource("SCB");
				assessment.setSourceID("SCB");
				assessment.setTpinInfo(tpinInfo);
				assessmentList.add(assessment);
				
				assessment = new Assessment();
				assessment.setAmount(2000.00);
				assessment.setAssessmentNumber("62");
				assessment.setAssessmentYear(2014);
				assessment.setClientTpin(tpin);
				assessment.setCountry("ZM");
				assessment.setCreateByPortalUserId(portalUser.getId());
				assessment.setDateRegistered(new Date().toString());
				assessment.setDeclarantTpin(tpin);
				assessment.setInterest(Boolean.TRUE);
				assessment.setInterestAmount(300.00);
				assessment.setMovedToWorkFlow(false);
				assessment.setPaidFor(false);
				assessment.setPorts(ports);
				assessment.setRegistrationNumber("62");
				assessment.setRegistrationSerial("C");
				assessment.setSource("SCB");
				assessment.setSourceID("SCB");
				assessment.setTpinInfo(tpinInfo);
				assessmentList.add(assessment);
				
				assessment = new Assessment();
				assessment.setAmount(1500.00);
				assessment.setAssessmentNumber("72");
				assessment.setAssessmentYear(2014);
				assessment.setClientTpin(tpin);
				assessment.setCountry("ZM");
				assessment.setCreateByPortalUserId(portalUser.getId());
				assessment.setDateRegistered(new Date().toString());
				assessment.setDeclarantTpin(tpin);
				assessment.setInterest(Boolean.TRUE);
				assessment.setInterestAmount(340.00);
				assessment.setMovedToWorkFlow(false);
				assessment.setPaidFor(false);
				assessment.setPorts(ports);
				assessment.setRegistrationNumber("72");
				assessment.setRegistrationSerial("C");
				assessment.setSource("SCB");
				assessment.setSourceID("SCB");
				assessment.setTpinInfo(tpinInfo);
				assessmentList.add(assessment);
				
				
				
				assessment = new Assessment();
				assessment.setAmount(1000.00);
				assessment.setAssessmentNumber("1");
				assessment.setAssessmentYear(2010);
				assessment.setClientTpin(tpin);
				assessment.setCountry("ZM");
				assessment.setCreateByPortalUserId(portalUser.getId());
				assessment.setDateRegistered(new Date().toString());
				assessment.setDeclarantTpin(tpin);
				assessment.setInterest(Boolean.TRUE);
				assessment.setInterestAmount(500.00);
				assessment.setMovedToWorkFlow(false);
				assessment.setPaidFor(false);
				assessment.setPorts(ports);
				assessment.setRegistrationNumber("1");
				assessment.setRegistrationSerial("C");
				assessment.setSource("SCB");
				assessment.setSourceID("SCB");
				assessment.setTpinInfo(tpinInfo);
				assessmentList.add(assessment);
				
				assessment = new Assessment();
				assessment.setAmount(2000.00);
				assessment.setAssessmentNumber("2");
				assessment.setAssessmentYear(2010);
				assessment.setClientTpin(tpin);
				assessment.setCountry("ZM");
				assessment.setCreateByPortalUserId(portalUser.getId());
				assessment.setDateRegistered(new Date().toString());
				assessment.setDeclarantTpin(tpin);
				assessment.setInterest(Boolean.TRUE);
				assessment.setInterestAmount(300.00);
				assessment.setMovedToWorkFlow(false);
				assessment.setPaidFor(false);
				assessment.setPorts(ports);
				assessment.setRegistrationNumber("2");
				assessment.setRegistrationSerial("C");
				assessment.setSource("SCB");
				assessment.setSourceID("SCB");
				assessment.setTpinInfo(tpinInfo);
				assessmentList.add(assessment);
				
				assessment = new Assessment();
				assessment.setAmount(1500.00);
				assessment.setAssessmentNumber("3");
				assessment.setAssessmentYear(2010);
				assessment.setClientTpin(tpin);
				assessment.setCountry("ZM");
				assessment.setCreateByPortalUserId(portalUser.getId());
				assessment.setDateRegistered(new Date().toString());
				assessment.setDeclarantTpin(tpin);
				assessment.setInterest(Boolean.TRUE);
				assessment.setInterestAmount(340.00);
				assessment.setMovedToWorkFlow(false);
				assessment.setPaidFor(false);
				assessment.setPorts(ports);
				assessment.setRegistrationNumber("3");
				assessment.setRegistrationSerial("C");
				assessment.setSource("SCB");
				assessment.setSourceID("SCB");
				assessment.setTpinInfo(tpinInfo);
				assessmentList.add(assessment);
			
				return assessmentList;
			}else if(portalUser.getCompany().getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_RETAIL_COMPANY))
			{
				Assessment assessment = new Assessment();
				TpinInfo tpinInfo = getTPINInfoByCompany(portalUser.getCompany().getId(), swpService);
				String tpin = tpinInfo.getTpin();
				Ports ports = getPortByPortCode("NKO", swpService);
				assessment.setAmount(1000.00);
				assessment.setAssessmentNumber("152");
				assessment.setAssessmentYear(2014);
				assessment.setClientTpin(tpin);
				assessment.setCountry("ZM");
				assessment.setCreateByPortalUserId(portalUser.getId());
				assessment.setDateRegistered(new Date().toString());
				assessment.setDeclarantTpin(tpin);
				assessment.setInterest(Boolean.TRUE);
				assessment.setInterestAmount(500.00);
				assessment.setMovedToWorkFlow(false);
				assessment.setPaidFor(false);
				assessment.setPorts(ports);
				assessment.setRegistrationNumber("52");
				assessment.setRegistrationSerial("C");
				assessment.setSource("SCB");
				assessment.setSourceID("SCB");
				assessment.setTpinInfo(tpinInfo);
				assessmentList.add(assessment);
				
				assessment = new Assessment();
				assessment.setAmount(2000.00);
				assessment.setAssessmentNumber("162");
				assessment.setAssessmentYear(2014);
				assessment.setClientTpin(tpin);
				assessment.setCountry("ZM");
				assessment.setCreateByPortalUserId(portalUser.getId());
				assessment.setDateRegistered(new Date().toString());
				assessment.setDeclarantTpin(tpin);
				assessment.setInterest(Boolean.TRUE);
				assessment.setInterestAmount(300.00);
				assessment.setMovedToWorkFlow(false);
				assessment.setPaidFor(false);
				assessment.setPorts(ports);
				assessment.setRegistrationNumber("62");
				assessment.setRegistrationSerial("C");
				assessment.setSource("SCB");
				assessment.setSourceID("SCB");
				assessment.setTpinInfo(tpinInfo);
				assessmentList.add(assessment);
				
				assessment = new Assessment();
				assessment.setAmount(1500.00);
				assessment.setAssessmentNumber("172");
				assessment.setAssessmentYear(2014);
				assessment.setClientTpin(tpin);
				assessment.setCountry("ZM");
				assessment.setCreateByPortalUserId(portalUser.getId());
				assessment.setDateRegistered(new Date().toString());
				assessment.setDeclarantTpin(tpin);
				assessment.setInterest(Boolean.TRUE);
				assessment.setInterestAmount(340.00);
				assessment.setMovedToWorkFlow(false);
				assessment.setPaidFor(false);
				assessment.setPorts(ports);
				assessment.setRegistrationNumber("72");
				assessment.setRegistrationSerial("C");
				assessment.setSource("SCB");
				assessment.setSourceID("SCB");
				assessment.setTpinInfo(tpinInfo);
				assessmentList.add(assessment);
				
				
				assessment = new Assessment();
				assessment.setAmount(1000.00);
				assessment.setAssessmentNumber("1");
				assessment.setAssessmentYear(2011);
				assessment.setClientTpin(tpin);
				assessment.setCountry("ZM");
				assessment.setCreateByPortalUserId(portalUser.getId());
				assessment.setDateRegistered(new Date().toString());
				assessment.setDeclarantTpin(tpin);
				assessment.setInterest(Boolean.TRUE);
				assessment.setInterestAmount(500.00);
				assessment.setMovedToWorkFlow(false);
				assessment.setPaidFor(false);
				assessment.setPorts(ports);
				assessment.setRegistrationNumber("1");
				assessment.setRegistrationSerial("C");
				assessment.setSource("SCB");
				assessment.setSourceID("SCB");
				assessment.setTpinInfo(tpinInfo);
				assessmentList.add(assessment);
				
				assessment = new Assessment();
				assessment.setAmount(2000.00);
				assessment.setAssessmentNumber("2");
				assessment.setAssessmentYear(2011);
				assessment.setClientTpin(tpin);
				assessment.setCountry("ZM");
				assessment.setCreateByPortalUserId(portalUser.getId());
				assessment.setDateRegistered(new Date().toString());
				assessment.setDeclarantTpin(tpin);
				assessment.setInterest(Boolean.TRUE);
				assessment.setInterestAmount(300.00);
				assessment.setMovedToWorkFlow(false);
				assessment.setPaidFor(false);
				assessment.setPorts(ports);
				assessment.setRegistrationNumber("2");
				assessment.setRegistrationSerial("C");
				assessment.setSource("SCB");
				assessment.setSourceID("SCB");
				assessment.setTpinInfo(tpinInfo);
				assessmentList.add(assessment);
				
				assessment = new Assessment();
				assessment.setAmount(1500.00);
				assessment.setAssessmentNumber("3");
				assessment.setAssessmentYear(2011);
				assessment.setClientTpin(tpin);
				assessment.setCountry("ZM");
				assessment.setCreateByPortalUserId(portalUser.getId());
				assessment.setDateRegistered(new Date().toString());
				assessment.setDeclarantTpin(tpin);
				assessment.setInterest(Boolean.TRUE);
				assessment.setInterestAmount(340.00);
				assessment.setMovedToWorkFlow(false);
				assessment.setPaidFor(false);
				assessment.setPorts(ports);
				assessment.setRegistrationNumber("3");
				assessment.setRegistrationSerial("C");
				assessment.setSource("SCB");
				assessment.setSourceID("SCB");
				assessment.setTpinInfo(tpinInfo);
				assessmentList.add(assessment);
			
				return assessmentList;
			}else
			{
				return null;
			}
		}
	}
	
	
	
	public Ports getPortByPortCode(String portCode, SwpService swpService) {
		// TODO Auto-generated method stub
		Ports rt = null;
		
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
	
	
	public TpinInfo getTPINInfoByCompany(Long id, SwpService swpService) {
		// TODO Auto-generated method stub
		TpinInfo rt = null;
		
		try {
			
				String hql = "select rt from TpinInfo rt where (" +
						"rt.company.id = " + id + ")";
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



	public ArrayList<TaxBreakDownResponse> getTaxBreakDown1(PortalUser portalUser, Assessment assessment) {
		// TODO Auto-generated method stub
		ArrayList<TaxBreakDownResponse> tbr = new ArrayList<TaxBreakDownResponse>();
		TaxBreakDownResponse tbd = new TaxBreakDownResponse();
		tbd.setProductCode("RET1");
		tbd.setProductName("Tennis balls with Paint");
		Collection<TaxDetails> taxDetailListing = new ArrayList<TaxDetails>();
		
		
		
		Double amount = assessment.getAmount();
		if(assessment.getInterest().equals(Boolean.TRUE))
		{
			TaxDetails td = new TaxDetails();
			td.setAmountToBePaid(assessment.getInterestAmount());
			td.setTaxCode("INT");
			taxDetailListing.add(td);
			amount = amount - assessment.getInterestAmount();
		}
		TaxDetails td = new TaxDetails();
		td.setAmountToBePaid(amount);
		td.setTaxCode("EDI");
		taxDetailListing.add(td);
		
		tbd.setTaxDetailListing(taxDetailListing);
		tbr.add(tbd);
		
		return tbr;
	}



	public FundsTransferResponse getFundsTransferResponse(PortalUser pu) {
		// TODO Auto-generated method stub
		FundsTransferResponse ftr = new FundsTransferResponse();
		ftr.setAccountNumber(pu.getCompany().getAccountNumber());
		ftr.setResMessageId(RandomStringUtils.random(8, false, true));
		ftr.setResTimeStamp(Boolean.TRUE);
		ftr.setStatus(Boolean.TRUE);
		return ftr;
	}



	public InterestPaymentResult getInterestPaymentResult() {
		// TODO Auto-generated method stub
		InterestPaymentResult ipr = new InterestPaymentResult();
		ipr.setErrorCode("0");
		ipr.setOfficeCode("NKO");
		ipr.setReceiptDate(new Date().toString());
		ipr.setReceiptNumber(RandomStringUtils.random(8, false, true));
		ipr.setReceiptSerial("21");
		ipr.setResult("Success");
		return ipr;
	}

	public DeclarationPaymentResult getDeclareZRAPaymentResposne() {
		// TODO Auto-generated method stub
		DeclarationPaymentResult dpr = new DeclarationPaymentResult();
		dpr.setErrorCode("0");
		dpr.setReceiptDate(new Date().toString());
		dpr.setReceiptNumber(RandomStringUtils.random(8, false, true));
		dpr.setReceiptSerial("21");
		dpr.setResult("Success");
		return dpr;
	}



	public Settings getZraAccount() {
		return zraAccount;
	}



	public void setZraAccount(Settings zraAccount) {
		this.zraAccount = zraAccount;
	}



	public Settings getZraSortCode() {
		return zraSortCode;
	}



	public void setZraSortCode(Settings zraSortCode) {
		this.zraSortCode = zraSortCode;
	}



	public Settings getCurrency() {
		return currency;
	}



	public void setCurrency(Settings currency) {
		this.currency = currency;
	}



	public String getProxyHost() {
		return proxyHost;
	}



	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}



	public String getProxyPort() {
		return proxyPort;
	}



	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}



	public Balance getBalance() {
		return balance;
	}



	public void setBalance(Balance balance) {
		this.balance = balance;
	}



	public Boolean getDemoModeBalance() {
		return demoModeBalance_1;
	}



	public void setDemoModeBalance(Boolean demoModeBalance) {
		this.demoModeBalance_1 = demoModeBalance;
	}



	public Boolean getDemoModePay_1() {
		return demoModePay_1;
	}



	public void setDemoModePay_1(Boolean demoModePay) {
		this.demoModePay_1 = demoModePay;
	}

}
