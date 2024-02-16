package com.probase.smartpay.domtax;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;

import org.apache.log4j.Logger;

import smartpay.entity.Assessment;
import smartpay.entity.AuthorizePanel;
import smartpay.entity.AuthorizePanelCombination;
import smartpay.entity.DomTax;
import smartpay.entity.PRNTransit;
import smartpay.entity.PaymentHistory;
import smartpay.entity.Company;
import smartpay.entity.FeeDescription;
import smartpay.entity.FinancialAmountRestriction;
import smartpay.entity.PortalUser;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.TpinInfo;
import smartpay.entity.enumerations.CompanyStatusConstants;
import smartpay.entity.enumerations.PaymentHistoryConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.service.SwpService;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.probase.smartpay.commins.BalanceInquiry;
import com.probase.smartpay.commins.ComminsApplicationState;
import com.probase.smartpay.commins.Util;
import com.sf.primepay.smartpay13.ServiceLocator;

public class DomTaxPortletState {

	private static Logger log = Logger.getLogger(DomTaxPortletState.class);
	private static SwpService swpService = null;
	private static DomTaxPortletUtil domTaxPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private String remoteIPAddress;
	private ArrayList<RoleType> portalUserRoleType;
	private static ComminsApplicationState cas;
	

	private String successMessage;
	private String selectedDomTaxClicked;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	private BalanceInquiry balanceInquiry;
	
	private Collection<DomTax> allDomTaxListing;
	private Collection<DomTax> allWorkFlowDomTax;
	private TpinInfo currentPortalUserTpin;
	
	private Settings settingsZRAAccount;
	private Settings settingsZRASortCode;
	private Settings primaryFeeSetting;
	private Settings notifyCorporateFirmEmail;
	private Settings notifyCorporateFirmSms;
	private Settings notifyCorporateIndividualEmail;
	private Settings notifyCorporateIndividualSMS;
	private Settings etaxPaymentNotifyEmail;
	private Settings etaxPaymentNotifySMS;
	private Settings systemUrl;
	private Settings platformCountry;
	private Settings platformBank;
	private Settings sendingEmail;
	private Settings sendingEmailPassword;
	private Settings sendingEmailPort;
	private Settings sendingEmailUsername;
	private Settings approvalProcess;
	private Settings twoStepLogin;
	private Settings applicationName;
	private Settings mobileApplicationName;
	private Settings proxyUsername;
	private Settings proxyPassword;
	private Settings proxyHost;
	private Settings proxyPort;
	private Settings emailFooter;
	private Settings sameLevelLoginCombination;
	private Settings bankName;
	private Settings currency;
	private Settings bankPaymentWebServiceUrl;
	private Settings taxBodyWebServiceUrl;
	
	
	/*****************NAVIGATION*****************/
	private String selectedDomTaxId;
	private VIEW_TABS currentTab;
    
	public static enum VIEW_TABS{
		UNPAID_DOM_TAXES, PAID_DOM_TAXES
	}
	
	
	public static enum DOM_TAX_ACTION{
		HANDLE_DOM_TAX_LISTING
	}
	
	
	
	/****core section starts here****/
	public static DomTaxPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		DomTaxPortletState portletState = null;
		Logger.getLogger(DomTaxPortletState.class).info("------getInstance");
		try {
			
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (DomTaxPortletState) session.getAttribute(DomTaxPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new DomTaxPortletState();
					DomTaxPortletUtil util = new DomTaxPortletUtil();
					portletState.setDomTaxPortletUtil(util);
					session.setAttribute(DomTaxPortletState.class.getName(), portletState);
					ServiceLocator serviceLocator = ServiceLocator.getInstance();
					swpService = serviceLocator.getSwpService();
					defaultInit(request, portletState, response);
	            }
			}
			
			//initSettings(portletState, swpService);
			// init settings
			return portletState;
		} catch (Exception e) {
			return null;
		}


	}
	
	
	
	private static void loadSettings(
			DomTaxPortletState portletState) {
		// TODO Auto-generated method stub
		Settings primaryFeeSetting = portletState.getDomTaxPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_PRIMARY_FEE_DESCRIPTION);
		portletState.setPrimaryFeeSetting(primaryFeeSetting);
		Settings notifyCorporateFirmEmail = portletState.getDomTaxPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_EMAIL);
		portletState.setNotifyCorporateFirmEmail(notifyCorporateFirmEmail);
		Settings notifyCorporateFirmSms = portletState.getDomTaxPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_SMS);
		portletState.setNotifyCorporateFirmSms(notifyCorporateFirmSms);
		Settings notifyCorporateIndividualEmail = portletState.getDomTaxPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_EMAIL);
		portletState.setNotifyCorporateIndividualEmail(notifyCorporateIndividualEmail);
		Settings notifyCorporateIndividualSMS = portletState.getDomTaxPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_SMS);
		portletState.setNotifyCorporateIndividualSMS(notifyCorporateIndividualSMS);
		Settings etaxPaymentNotifyEmail = portletState.getDomTaxPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_EMAIL);
		portletState.setEtaxPaymentNotifyEmail(etaxPaymentNotifyEmail);
		Settings etaxPaymentNotifySMS = portletState.getDomTaxPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_SMS);
		portletState.setEtaxPaymentNotifySMS(etaxPaymentNotifySMS);
		Settings systemUrl = portletState.getDomTaxPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_URL);
		portletState.setSystemUrl(systemUrl);
		
		Settings twostep = portletState.getDomTaxPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_TWO_STEP_LOGIN);
		portletState.setTwoStepLogin(twostep);
		Settings appr = portletState.getDomTaxPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_APPROVAL_PROCESS);
		portletState.setApprovalProcess(appr);
		Settings usname = portletState.getDomTaxPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_USERNAME);
		portletState.setSendingEmailUsername(usname);
		Settings semail = portletState.getDomTaxPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_EMAIL);
		portletState.setSendingEmail(semail);
		Settings pswd = portletState.getDomTaxPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_SENDER_PASSWORD);
		portletState.setSendingEmailPassword(pswd);
		Settings port = portletState.getDomTaxPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_EMAIL_PORT);
		portletState.setSendingEmailPort(port);
		Settings spc = portletState.getDomTaxPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_COUNTRY);
		portletState.setPlatformCountry(spc);
		Settings spb = portletState.getDomTaxPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATORM_BANK);
		portletState.setPlatformBank(spb);
		Settings zacc = portletState.getDomTaxPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_NUMBER);
		portletState.setSettingsZRAAccount(zacc);
		Settings zacs = portletState.getDomTaxPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_SORT_CODE);
		portletState.setSettingsZRASortCode(zacs);
		
		
		
		Settings app = portletState.getDomTaxPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_APPLICATION_NAME);
		portletState.setApplicationName(app);
		Settings mapp = portletState.getDomTaxPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_MOBILE_APPLICATION_NAME);
		portletState.setMobileApplicationName(mapp);
		Settings proxH = portletState.getDomTaxPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_HOST);
		portletState.setProxyHost(proxH);
		Settings proxP = portletState.getDomTaxPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_PORT);
		portletState.setProxyPort(proxP);
		Settings proxU = portletState.getDomTaxPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_USERNAME);
		portletState.setProxyUsername(proxU);
		Settings proxPwd = portletState.getDomTaxPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PROXY_PASSWORD);
		portletState.setProxyPassword(proxPwd);
		Settings bank = portletState.getDomTaxPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_PLATFORM_BANK_NAME);
		portletState. setBankName(bank);
		Settings currency = portletState.getDomTaxPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_SYSTEM_CURRENCY);
		portletState.setCurrency(currency);
		Settings bpwsu = portletState.getDomTaxPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_BANK_PAYMENT_WS_URL);
		portletState.setBankPaymentWebServiceUrl(bpwsu);
		Settings zwsu = portletState.getDomTaxPortletUtil().
				getSettingByName(SmartPayConstants.SETTINGS_TAX_BODY_WS_URL);
		portletState.setTaxBodyWebServiceUrl(zwsu);
	}
	
	
	
	private void setDomTaxPortletUtil(DomTaxPortletUtil util) {
		// TODO Auto-generated method stub
		this.domTaxPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, DomTaxPortletState portletState, PortletResponse response) {
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
				DomTaxPortletUtil util = DomTaxPortletUtil.getInstance();
				portletState.setDomTaxPortletUtil(util);
				
				ArrayList<RoleType> pur = portletState.getDomTaxPortletUtil().
						getRoleTypeByPortalUser(portletState.getPortalUser());	
				portletState.setPortalUserRoleType(pur);
				
				loadCurrentPortalUserTin(portletState);
				loadSettings(portletState);
				if(portletState.getCurrentPortalUserTpin()!=null)
				{
					reloadDomTaxes(portletState, swpService);
					loadDomTaxes(portletState, false);
				}
				portletState.setCas(ComminsApplicationState.getInstance(request, response));
				getBankBalance(portletState, request, response);
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


	private static void getBankBalance(DomTaxPortletState portletState,
			PortletRequest aReq, PortletResponse aRes) {
		// TODO Auto-generated method stub
		ComminsApplicationState cappState = portletState.getCas();
		BalanceInquiry balanceInquiry = null;
//		if(cappState!=null && cappState.getDemoModeBalance().equals(Boolean.TRUE))
//		{
//			balanceInquiry = cas.getBalanceInquiry(portletState.getPortalUser(), swpService);
//		}else
//		{
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
//		}
		portletState.setBalanceInquiry(balanceInquiry);
		
	}



	private static void loadCurrentPortalUserTin(
			DomTaxPortletState portletState) {
		// TODO Auto-generated method stub
		TpinInfo tin = portletState.getDomTaxPortletUtil().getTpinInfoByPortalUser(portletState);
		portletState.setCurrentPortalUserTpin(tin);
	}



	public static void loadDomTaxes(DomTaxPortletState portletState, boolean yesOrNo) {
		// TODO Auto-generated method stub
		Collection<DomTax> domTaxList = portletState.getDomTaxPortletUtil().getDomTaxByPaidForValue(yesOrNo, portletState);
		portletState.setAllDomTaxListing(domTaxList);
		Collection<DomTax> domTaxWFList = portletState.getDomTaxPortletUtil().getWorkFlowsByCompany(portletState.getPortalUser().getCompany());
		portletState.setAllWorkFlowDomTax(domTaxWFList);
		
	}


	private static void reloadDomTaxes(DomTaxPortletState portletState, SwpService swpService) {
		// TODO Auto-generated method stub
		TpinInfo tpinInfo = portletState.getCurrentPortalUserTpin();
		if(tpinInfo!=null)
		{
			Collection<PRNTransit> prnTransitList = portletState.getDomTaxPortletUtil().getPRNListByTpin(tpinInfo);
			if(prnTransitList!=null && prnTransitList.size()>0)
			{
				for(Iterator<PRNTransit> it = prnTransitList.iterator(); it.hasNext();)
				{
					PRNTransit prnTransit = it.next();
					if(portletState.getDomTaxPortletUtil().getDomTaxByPRN(prnTransit.getPaymentRegNo())==null)
					{
						DomTax domTax = new DomTax();
						domTax.setAmountPayable(Double.valueOf(prnTransit.getAmountToBePaid()));
						domTax.setDateCreated(new Timestamp((new Date()).getTime()));
						domTax.setPaidFor(Boolean.FALSE);
						domTax.setMovedToWorkFlow(Boolean.FALSE);
						domTax.setPaymentRegDate(prnTransit.getPaymentRegDate());
						domTax.setPaymentRegNo(prnTransit.getPaymentRegNo());
						domTax.setTpinInfo(tpinInfo);
						domTax.setTaxPayerName(prnTransit.getTaxPayerName());
						domTax.setExpDate(prnTransit.getExpDate());
						domTax.setMerchantId(prnTransit.getMerchantId());
						swpService.createNewRecord(domTax);
						swpService.deleteRecord(prnTransit);
					}
				}
			}
		}
	}



	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			DomTaxPortletState portletState) {

		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			DomTaxPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			DomTaxPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			DomTaxPortletState.log.debug("Error including error message", e);
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


	public DomTaxPortletUtil getDomTaxPortletUtil() {
		// TODO Auto-generated method stub
		return this.domTaxPortletUtil;
	}


	public ArrayList<RoleType> getPortalUserRoleType() {
		return portalUserRoleType;
	}


	public void setPortalUserRoleType(ArrayList<RoleType> portalUserRoleType) {
		this.portalUserRoleType = portalUserRoleType;
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



	public Collection<DomTax> getAllDomTaxListing() {
		return allDomTaxListing;
	}



	public void setAllDomTaxListing(Collection<DomTax> allDomTaxListing) {
		this.allDomTaxListing = allDomTaxListing;
	}



	public BalanceInquiry getBalanceInquiry() {
		return balanceInquiry;
	}



	public void setBalanceInquiry(BalanceInquiry balanceInquiry) {
		this.balanceInquiry = balanceInquiry;
	}



	public String getSelectedDomTaxId() {
		return selectedDomTaxId;
	}



	public void setSelectedDomTaxId(String selectedDomTaxId) {
		this.selectedDomTaxId = selectedDomTaxId;
	}



	public TpinInfo getCurrentPortalUserTpin() {
		return currentPortalUserTpin;
	}



	public void setCurrentPortalUserTpin(TpinInfo currentPortalUserTpin) {
		this.currentPortalUserTpin = currentPortalUserTpin;
	}



	public Settings getSettingsZRAAccount() {
		return settingsZRAAccount;
	}



	public void setSettingsZRAAccount(Settings settingsZRAAccount) {
		this.settingsZRAAccount = settingsZRAAccount;
	}



	public Settings getSettingsZRASortCode() {
		return settingsZRASortCode;
	}



	public void setSettingsZRASortCode(Settings settingsZRASortCode) {
		this.settingsZRASortCode = settingsZRASortCode;
	}



	public Settings getPrimaryFeeSetting() {
		return primaryFeeSetting;
	}



	public void setPrimaryFeeSetting(Settings primaryFeeSetting) {
		this.primaryFeeSetting = primaryFeeSetting;
	}



	public Settings getNotifyCorporateFirmEmail() {
		return notifyCorporateFirmEmail;
	}



	public void setNotifyCorporateFirmEmail(Settings notifyCorporateFirmEmail) {
		this.notifyCorporateFirmEmail = notifyCorporateFirmEmail;
	}



	public Settings getNotifyCorporateFirmSms() {
		return notifyCorporateFirmSms;
	}



	public void setNotifyCorporateFirmSms(Settings notifyCorporateFirmSms) {
		this.notifyCorporateFirmSms = notifyCorporateFirmSms;
	}



	public Settings getNotifyCorporateIndividualEmail() {
		return notifyCorporateIndividualEmail;
	}



	public void setNotifyCorporateIndividualEmail(
			Settings notifyCorporateIndividualEmail) {
		this.notifyCorporateIndividualEmail = notifyCorporateIndividualEmail;
	}



	public Settings getNotifyCorporateIndividualSMS() {
		return notifyCorporateIndividualSMS;
	}



	public void setNotifyCorporateIndividualSMS(
			Settings notifyCorporateIndividualSMS) {
		this.notifyCorporateIndividualSMS = notifyCorporateIndividualSMS;
	}



	public Settings getEtaxPaymentNotifyEmail() {
		return etaxPaymentNotifyEmail;
	}



	public void setEtaxPaymentNotifyEmail(Settings etaxPaymentNotifyEmail) {
		this.etaxPaymentNotifyEmail = etaxPaymentNotifyEmail;
	}



	public Settings getEtaxPaymentNotifySMS() {
		return etaxPaymentNotifySMS;
	}



	public void setEtaxPaymentNotifySMS(Settings etaxPaymentNotifySMS) {
		this.etaxPaymentNotifySMS = etaxPaymentNotifySMS;
	}



	public Settings getSystemUrl() {
		return systemUrl;
	}



	public void setSystemUrl(Settings systemUrl) {
		this.systemUrl = systemUrl;
	}



	public Settings getPlatformCountry() {
		return platformCountry;
	}



	public void setPlatformCountry(Settings platformCountry) {
		this.platformCountry = platformCountry;
	}



	public Settings getPlatformBank() {
		return platformBank;
	}



	public void setPlatformBank(Settings platformBank) {
		this.platformBank = platformBank;
	}



	public Settings getSendingEmail() {
		return sendingEmail;
	}



	public void setSendingEmail(Settings sendingEmail) {
		this.sendingEmail = sendingEmail;
	}



	public Settings getSendingEmailPassword() {
		return sendingEmailPassword;
	}



	public void setSendingEmailPassword(Settings sendingEmailPassword) {
		this.sendingEmailPassword = sendingEmailPassword;
	}



	public Settings getSendingEmailPort() {
		return sendingEmailPort;
	}



	public void setSendingEmailPort(Settings sendingEmailPort) {
		this.sendingEmailPort = sendingEmailPort;
	}



	public Settings getSendingEmailUsername() {
		return sendingEmailUsername;
	}



	public void setSendingEmailUsername(Settings sendingEmailUsername) {
		this.sendingEmailUsername = sendingEmailUsername;
	}



	public Settings getApprovalProcess() {
		return approvalProcess;
	}



	public void setApprovalProcess(Settings approvalProcess) {
		this.approvalProcess = approvalProcess;
	}



	public Settings getTwoStepLogin() {
		return twoStepLogin;
	}



	public void setTwoStepLogin(Settings twoStepLogin) {
		this.twoStepLogin = twoStepLogin;
	}



	public Settings getApplicationName() {
		return applicationName;
	}



	public void setApplicationName(Settings applicationName) {
		this.applicationName = applicationName;
	}



	public Settings getMobileApplicationName() {
		return mobileApplicationName;
	}



	public void setMobileApplicationName(Settings mobileApplicationName) {
		this.mobileApplicationName = mobileApplicationName;
	}



	public Settings getProxyUsername() {
		return proxyUsername;
	}



	public void setProxyUsername(Settings proxyUsername) {
		this.proxyUsername = proxyUsername;
	}



	public Settings getProxyPassword() {
		return proxyPassword;
	}



	public void setProxyPassword(Settings proxyPassword) {
		this.proxyPassword = proxyPassword;
	}



	public Settings getProxyHost() {
		return proxyHost;
	}



	public void setProxyHost(Settings proxyHost) {
		this.proxyHost = proxyHost;
	}



	public Settings getProxyPort() {
		return proxyPort;
	}



	public void setProxyPort(Settings proxyPort) {
		this.proxyPort = proxyPort;
	}



	public Settings getEmailFooter() {
		return emailFooter;
	}



	public void setEmailFooter(Settings emailFooter) {
		this.emailFooter = emailFooter;
	}



	public Settings getSameLevelLoginCombination() {
		return sameLevelLoginCombination;
	}



	public void setSameLevelLoginCombination(Settings sameLevelLoginCombination) {
		this.sameLevelLoginCombination = sameLevelLoginCombination;
	}



	public Settings getBankName() {
		return bankName;
	}



	public void setBankName(Settings bankName) {
		this.bankName = bankName;
	}



	public Settings getCurrency() {
		return currency;
	}



	public void setCurrency(Settings currency) {
		this.currency = currency;
	}



	public Settings getBankPaymentWebServiceUrl() {
		return bankPaymentWebServiceUrl;
	}



	public void setBankPaymentWebServiceUrl(Settings bankPaymentWebServiceUrl) {
		this.bankPaymentWebServiceUrl = bankPaymentWebServiceUrl;
	}



	public Settings getTaxBodyWebServiceUrl() {
		return taxBodyWebServiceUrl;
	}



	public void setTaxBodyWebServiceUrl(Settings taxBodyWebServiceUrl) {
		this.taxBodyWebServiceUrl = taxBodyWebServiceUrl;
	}



	public String getSelectedDomTaxClicked() {
		return selectedDomTaxClicked;
	}



	public void setSelectedDomTaxClicked(String selectedDomTaxClicked) {
		this.selectedDomTaxClicked = selectedDomTaxClicked;
	}



	public Collection<DomTax> getAllWorkFlowDomTax() {
		return allWorkFlowDomTax;
	}



	public void setAllWorkFlowDomTax(Collection<DomTax> allWorkFlowDomTax) {
		this.allWorkFlowDomTax = allWorkFlowDomTax;
	}



	public static ComminsApplicationState getCas() {
		return cas;
	}



	public static void setCas(ComminsApplicationState cas) {
		log.info("Set CAS For DomTaxPortletState");
		DomTaxPortletState.cas = cas;
	}



	public void loadWorkFlowAssessmentIds(DomTaxPortletState portletState) {
		// TODO Auto-generated method stub
		Collection<DomTax> intColl = portletState.getDomTaxPortletUtil().getWorkFlowsByCompany(portletState.getPortalUser().getCompany());
		portletState.setAllWorkFlowDomTax(intColl);
	}



	
}
