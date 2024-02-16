package com.probase.smartpay.admin.taxtype;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

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

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;

import smartpay.audittrail.AuditTrail;
import smartpay.entity.ApprovalFlowTransit;
import smartpay.entity.AuthorizePanelCombination;
import smartpay.entity.CompanyCRUDRights;
import smartpay.entity.PortalUser;
import smartpay.entity.PortalUserCRUDRights;
import smartpay.entity.Ports;
import smartpay.entity.TaxType;
import smartpay.entity.TaxTypeAccount;
import smartpay.entity.enumerations.ActionTypeConstants;
import smartpay.entity.enumerations.PortalUserStatusConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.service.SwpService;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.probase.smartpay.admin.companymanagement.CompanyManagementPortletState;
import com.probase.smartpay.admin.mandatepanelmanagement.MandatePanelPortletState.AUTHORISATION_PANEL;
import com.probase.smartpay.admin.taxtype.TaxTypePortlet;
import com.probase.smartpay.admin.taxtype.TaxTypePortletState;
import com.probase.smartpay.admin.taxtype.TaxTypePortletUtil;
import com.probase.smartpay.admin.taxtype.TaxTypePortletState.NAVIGATE;
import com.probase.smartpay.admin.taxtype.TaxTypePortletState.TAXTYPE_ACTION;
import com.probase.smartpay.admin.taxtype.TaxTypePortletState.TAXTYPE_VIEW;
import com.probase.smartpay.admin.taxtype.TaxTypePortletState.VIEW_TABS;
import com.probase.smartpay.commins.ComminsApplicationState;
import com.probase.smartpay.commins.Mailer;
import com.probase.smartpay.commins.ProbaseConstants;
import com.probase.smartpay.commins.SendSms;
import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Portlet implementation class TaxTypePortlet
 */
public class TaxTypePortlet extends MVCPortlet {
	private Logger log = Logger.getLogger(TaxTypePortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
	TaxTypePortletUtil util = TaxTypePortletUtil.getInstance();
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
		TaxTypePortletState portletState = 
				TaxTypePortletState.getInstance(renderRequest, renderResponse);

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
		
		TaxTypePortletState portletState = TaxTypePortletState.getInstance(aReq, aRes);
		
		
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
        if(action.equalsIgnoreCase(TAXTYPE_ACTION.LOGIN_STEP_TWO.name()))
        {
        	loginStepTwo(aReq, aRes, portletState);
        }
        if(action.equalsIgnoreCase(TAXTYPE_ACTION.CREATE_A_NEW_TAXTYPE_ACTION.name()))
        {
        	log.info("handle create a tax type post action");
        	handleCreateNewTaxType(aReq, aRes, portletState);
        }if(action.equalsIgnoreCase(TAXTYPE_ACTION.UPDATE_NEW_TAXTYPE_ACTION.name()))
        {
        	handleUpdateTaxType(aReq, aRes, portletState);
        }
        if(action.equalsIgnoreCase(TAXTYPE_ACTION.HANDLE_TAXTYPE_LISTING_ACTION.name()))
        {
        	handleTaxTypeListingAction(aReq, aRes, portletState);
        }if(action.equalsIgnoreCase(NAVIGATE.NAVIGATE_ACTIONS.name()))
        {
        	handleNavigations(aReq, aRes, portletState);
        }if(action.equalsIgnoreCase(TAXTYPE_VIEW.CREATE_A_NEW_TAXTYPE.name()))
        {
        	portletState.reinitializeForTaxType(portletState);
        	aRes.setRenderParameter("jspPage", "/html/taxtypeportlet/createataxtype.jsp");
        	portletState.setCurrentTab(VIEW_TABS.CREATE_A_NEW_TAXTYPE);
        }if(action.equalsIgnoreCase(TAXTYPE_VIEW.VIEW_TAXTYPE_LISTINGS.name()))
        {
        	portletState.setAllTaxTypeListing(portletState.getTaxTypePortletUtil().getAllTaxTypeListing(true));
        	aRes.setRenderParameter("jspPage", "/html/taxtypeportlet/taxtypelisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.VIEW_TAXTYPE_LISTINGS);
        }
		
	}
	
	
	private void loginStepTwo(ActionRequest aReq, ActionResponse aRes,
			TaxTypePortletState portletState) {
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
				PortalUser pu = portletState.getTaxTypePortletUtil().getPortalUserByEmailAddress(email2);
				if(pu.getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
				{
					if(portletState.getCompanyCRUDRights().getCudInitiatorRights().equals(Boolean.TRUE))
					{
						if(pu!=null && pu.getStatus().equals(PortalUserStatusConstants.PORTAL_USER_STATUS_ACTIVE))
						{
							CompanyCRUDRights pcrs = portletState.getTaxTypePortletUtil().getCompanyCRUDRightsByPortalUser(pu);
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
	
	public boolean loginStepTwoForPortalUserManagement(PortalUser portalUser, CompanyCRUDRights portalUserCRUDRights,
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


	private void handleCreateNewTaxType(ActionRequest aReq,
			ActionResponse aRes, TaxTypePortletState portletState) {
		// TODO Auto-generated method stub
		log.info("inside handleCreateNewTypeCode ");
		portletState.setTaxTypeCode(aReq.getParameter("taxTypeCode"));
		log.info("taxTypeCode =  " + portletState.getTaxTypeCode());
		portletState.setTaxTypeName(aReq.getParameter("taxTypeName"));
		log.info("taxTypeName =  " + portletState.getTaxTypeName());
		portletState.setTaxTypeAccount(aReq.getParameter("taxTypeAccount"));
		log.info("taxTypeAccount =  " + portletState.getTaxTypeAccount());
		portletState.setTaxTypeSortCode(aReq.getParameter("taxTypeSortCode"));
		log.info("taxTypeSortCode =  " + portletState.getTaxTypeSortCode());
		
		ComminsApplicationState cappState = portletState.getCas();
		Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
		Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());

		
		try
		{
			
			
				if(isTaxTypeDataValid(portletState, aReq, aRes, true))
				{
					log.info("fee description data is valid ");
					
					
						TaxType taxType = new TaxType();
						taxType.setTaxName(portletState.getTaxTypeName());
						taxType.setTaxCode(portletState.getTaxTypeCode());
						taxType = (TaxType)swpService.createNewRecord(taxType);

						TaxTypeAccount taxTypeAccount = new TaxTypeAccount();
						taxTypeAccount.setAccountNumber(portletState.getTaxTypeAccount());
						taxTypeAccount.setAccountSortCode(portletState.getTaxTypeSortCode());
						taxTypeAccount.setCreatedByPortalUserId(Long.toString(portletState.getPortalUser().getId()));
						taxTypeAccount.setDateCreated(new Timestamp((new Date()).getTime()));
						taxTypeAccount.setTaxType(taxType);
						taxTypeAccount.setStatus(Boolean.TRUE);
						taxTypeAccount = (TaxTypeAccount)swpService.createNewRecord(taxTypeAccount);
						
						
						handleAudit("TaxType Creation", Long.toString(taxType.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
						portletState.addSuccess(aReq, "Fee Description - " + taxType.getTaxName() + " (" + taxType.getTaxCode() + ") - was created successfully!", portletState);
						portletState.setAllTaxTypeListing(portletState.getTaxTypePortletUtil().getAllTaxTypeListing(true));
			        	aRes.setRenderParameter("jspPage", "/html/taxtypeportlet/taxtypelisting.jsp");
			        	portletState.setCurrentTab(VIEW_TABS.VIEW_TAXTYPE_LISTINGS);
			        	portletState.setTaxTypeCode(null);
			    		portletState.setTaxTypeName(null);
			    		portletState.setTaxTypeAccount(null);
			    		portletState.setTaxTypeSortCode(null);
					
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/taxtypeportlet/createataxtype.jsp");
					portletState.reinitializeForTaxType(portletState);
		        	portletState.setCurrentTab(VIEW_TABS.CREATE_A_NEW_TAXTYPE);
				}
			
		}catch(Exception e)
		{
			portletState.addError(aReq, "Issues where encountered while creating a new tax type. Contact Technical team for assistance", portletState);
			aRes.setRenderParameter("jspPage", "/html/taxtypeportlet/createataxtype.jsp");
			portletState.reinitializeForTaxType(portletState);
        	portletState.setCurrentTab(VIEW_TABS.CREATE_A_NEW_TAXTYPE);
			e.printStackTrace();
		}
		
		
		
	}
	
	private void handleUpdateTaxType(ActionRequest aReq, ActionResponse aRes,
			TaxTypePortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setTaxTypeCode(aReq.getParameter("taxTypeCode"));
		log.info("taxTypeCode =  " + portletState.getTaxTypeCode());
		portletState.setTaxTypeName(aReq.getParameter("taxTypeName"));
		log.info("taxTypeName =  " + portletState.getTaxTypeName());
		portletState.setTaxTypeAccount(aReq.getParameter("taxTypeAccount"));
		log.info("taxTypeAccount =  " + portletState.getTaxTypeAccount());
		portletState.setTaxTypeSortCode(aReq.getParameter("taxTypeSortCode"));
		log.info("taxTypeSortCode =  " + portletState.getTaxTypeSortCode());
		
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		
		
		ComminsApplicationState cappState = portletState.getCas();
		
			try
			{
				if(isTaxTypeDataValid(portletState, aReq, aRes, false))
				{
					
					TaxType taxType = (TaxType)portletState.getTaxTypePortletUtil().getEntityObjectById(TaxType.class, Long.valueOf(portletState.getSelectedTypeId()));
					taxType.setTaxName(portletState.getTaxTypeName());
					taxType.setTaxCode(portletState.getTaxTypeCode());
					swpService.updateRecord(taxType);

					TaxTypeAccount txAcct = portletState.getTaxTypePortletUtil().getCurrentTaxTypeAccountByTaxTypeId(taxType.getId());
					if(txAcct.getAccountNumber().equals(portletState.getTaxTypeAccount()) && 
							txAcct.getAccountSortCode().equals(portletState.getTaxTypeSortCode()))
					{
						
					}else
					{
						txAcct.setStatus(Boolean.FALSE);
						
						swpService.updateRecord(txAcct);
						
						TaxTypeAccount taxTypeAccount = new TaxTypeAccount();
						taxTypeAccount.setAccountNumber(portletState.getTaxTypeAccount());
						taxTypeAccount.setAccountSortCode(portletState.getTaxTypeSortCode());
						taxTypeAccount.setCreatedByPortalUserId(Long.toString(portletState.getPortalUser().getId()));
						taxTypeAccount.setDateCreated(new Timestamp((new Date()).getTime()));
						taxTypeAccount.setTaxType(taxType);
						taxTypeAccount.setStatus(Boolean.TRUE);
						taxTypeAccount = (TaxTypeAccount)swpService.createNewRecord(taxTypeAccount);
						log.info("taxTypeAccount = " + taxTypeAccount.getId());
					}
					

					log.info("taxType = " + taxType.getId());
					handleAudit("TaxType Update", Long.toString(taxType.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
					aRes.setRenderParameter("jspPage", "/html/taxtypeportlet/taxtypelisting.jsp");
					portletState.addSuccess(aReq, "Tax type update successful!", portletState);
					portletState.reinitializeForTaxType(portletState);
					portletState.setAllTaxTypeListing(portletState.getTaxTypePortletUtil().getAllTaxTypeListing(true));
					
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/taxtypeportlet/editataxtype.jsp");
				}
			}catch(NumberFormatException e)
			{
				e.printStackTrace();
				portletState.addError(aReq, "Select a valid tax type to update", portletState);
				aRes.setRenderParameter("jspPage", "/html/taxtypeportlet/editataxtype.jsp");
			}
		
		
		
	}
	
	
	
	
	private boolean isTaxTypeDataValid(TaxTypePortletState portletState,
			ActionRequest aReq, ActionResponse aRes, boolean checkExistingForNew) {
		// TODO Auto-generated method stub
		String errorMessage = null;
		
		if(portletState.getTaxTypeCode()!=null && portletState.getTaxTypeCode().trim().length()>0)
		{
			if(portletState.getTaxTypeName()!=null && portletState.getTaxTypeName().trim().length()>0)
			{
				if(portletState.getTaxTypeAccount()!=null && portletState.getTaxTypeAccount().trim().length()>0)
				{
					if(portletState.getTaxTypeSortCode()!=null && portletState.getTaxTypeSortCode().trim().length()>0)
					{
						if(checkExistingForNew)
						{
							TaxType fd = null;
							fd = portletState.getTaxTypePortletUtil().getTaxTypeByNameOrPortCode(
									portletState.getTaxTypeName(), portletState.getTaxTypeCode());
							if(fd!=null)
							{
								errorMessage =  "The tax code or name provided already exists on the system. Provide another create one on the system.";
							}else
							{
								
							}
						}else
						{
							Collection<TaxType> fd = null;
							fd = portletState.getTaxTypePortletUtil().getTaxTypeByNameOrCodeAndNotId(
									portletState.getTaxTypeName(), portletState.getTaxTypeCode(), 
									Long.valueOf(portletState.getSelectedTypeId()));
							if(fd==null || (fd!=null && fd.size()==0))
							{
								
							}else
							{
								errorMessage =  "The port code or name provided already exists on the system. Provide another port code to create one on the system";
							}
						}
				
					}else
					{
						
					}
				}else
				{
					
				}
				
					
			
			}
			else
			{
				errorMessage =  "Provide a port name";
			}
		}else
		{
			errorMessage =  "Provide a port code";
		}
		
		if(errorMessage==null)
		{
			log.info("Error message: == null");
			return true;
		}
		else
		{
			log.info("Error message: ==" + errorMessage);
			portletState.addError(aReq, errorMessage, portletState);
			return false;
		}
	}

	private void handleNavigations(ActionRequest aReq, ActionResponse aRes,
			TaxTypePortletState portletState) {
		// TODO Auto-generated method stub
		String action = aReq.getParameter("actionUrl");
		if(action.equalsIgnoreCase("createfeedescription"))
		{
			portletState.reinitializeForTaxType(portletState);
			aRes.setRenderParameter("jspPage", "/html/taxtypeportlet/createataxtype.jsp");
			portletState.setCurrentTab(VIEW_TABS.CREATE_A_NEW_TAXTYPE);
		}else if(action.equalsIgnoreCase("taxtypelistings"))
		{
			portletState.setAllTaxTypeListing(portletState.getTaxTypePortletUtil().getAllTaxTypeListing(true));
			aRes.setRenderParameter("jspPage", "/html/taxtypeportlet/taxtypelisting.jsp");
			portletState.setCurrentTab(VIEW_TABS.VIEW_TAXTYPE_LISTINGS);
		}
	}


	
	
	
	
	private void handleTaxTypeListingAction(ActionRequest aReq,
			ActionResponse aRes, TaxTypePortletState portletState) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		String taxTypeId = aReq.getParameter("selectedTaxType").trim();
		log.info("taxTypeId = " + taxTypeId);
		try
		{
			Long taxTypeIdL = Long.valueOf(taxTypeId);
			log.info("taxTypeIdL = " + taxTypeIdL);
			TaxType tt = (TaxType)portletState.getTaxTypePortletUtil().getEntityObjectById(TaxType.class, taxTypeIdL);
			TaxTypeAccount tta = (TaxTypeAccount)portletState.getTaxTypePortletUtil().getCurrentTaxTypeAccountByTaxTypeId(tt.getId());
			
			portletState.setSelectedTypeId(taxTypeId);
			if(tt!=null && tta!=null)
			{
						
				if(aReq.getParameter("selectedTaxTypeAction")!=null && aReq.getParameter("selectedTaxTypeAction").equalsIgnoreCase("update"))
				{
					portletState.setTaxTypeName(tt.getTaxName());
					portletState.setTaxTypeCode(tt.getTaxCode());
					portletState.setTaxTypeAccount(tta.getAccountNumber());
					portletState.setTaxTypeSortCode(tta.getAccountSortCode());
					aRes.setRenderParameter("jspPage", "/html/taxtypeportlet/editataxtype.jsp");
				
				}
				else if(aReq.getParameter("selectedTaxTypeAction")!=null && aReq.getParameter("selectedTaxTypeAction").equalsIgnoreCase("suspend"))
				{
					JSONObject jsonObject = new JSONObject();
					try {
						jsonObject.put("taxname", tt.getTaxName());
						jsonObject.put("taxcode", tt.getTaxCode());
						jsonObject.put("taxacctno", tta.getAccountNumber());
						jsonObject.put("taxacctsortcode", tta.getAccountSortCode());
						jsonObject.put("requestBy", portletState.getPortalUser().getFirstName() + " " + portletState.getPortalUser().getLastName());
						
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.TAX_TYPE_SUSPEND);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(tta.getId());
						aft.setEntityName(TaxType.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						
						Collection<PortalUser> pus = portletState.getTaxTypePortletUtil().getApprovingPortalUsers(
								portletState.getPortalUser().getRoleType().getRoleTypeName());
						portletState.addSuccess(aReq, "Your request to suspend this tax type has been forwarded for approval", portletState);
						
						for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
						{
							PortalUser pu1 = it.next();
							emailer.emailApprovalRequest(
									pu1.getFirstName(), 
									pu1.getLastName(), 
									pu1.getEmailAddress(), 
									portletState.getSystemUrl().getValue(), 
									"eTax - Approval Request for the " +
									"Update of a Mandate-Panel/User Mapping", portletState.getApplicationName().getValue());
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
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
					aRes.setRenderParameter("jspPage", "/html/taxtypeportlet/taxtypelisting.jsp");
				
				}else if(aReq.getParameter("selectedTaxTypeAction")!=null && aReq.getParameter("selectedTaxTypeAction").equalsIgnoreCase("activate"))
				{
					JSONObject jsonObject = new JSONObject();
					try {
						jsonObject.put("taxname", tt.getTaxName());
						jsonObject.put("taxcode", tt.getTaxCode());
						jsonObject.put("taxacctno", tta.getAccountNumber());
						jsonObject.put("taxacctsortcode", tta.getAccountSortCode());
						jsonObject.put("requestBy", portletState.getPortalUser().getFirstName() + " " + portletState.getPortalUser().getLastName());
						
						ApprovalFlowTransit aft = new ApprovalFlowTransit();
						aft.setActionType(ActionTypeConstants.TAX_TYPE_REACTIVATE);
						aft.setDateCreated(new Timestamp((new Date()).getTime()));
						aft.setEntityId(tta.getId());
						aft.setEntityName(TaxType.class.getSimpleName());
						aft.setObjectData(jsonObject.toString());
						aft.setPortalUser(portletState.getPortalUser());
						aft.setWorkerId(null);
						swpService.createNewRecord(aft);
						
						Collection<PortalUser> pus = portletState.getTaxTypePortletUtil().getApprovingPortalUsers(
								portletState.getPortalUser().getRoleType().getRoleTypeName());
						portletState.addSuccess(aReq, "Your request to reactivate this tax type has been forwarded for approval", portletState);
						
						for(Iterator<PortalUser> it = pus.iterator(); it.hasNext();)
						{
							PortalUser pu1 = it.next();
							emailer.emailApprovalRequest(
									pu1.getFirstName(), 
									pu1.getLastName(), 
									pu1.getEmailAddress(), 
									portletState.getSystemUrl().getValue(), 
									"eTax - Approval Request for the " +
									"Update of a Mandate-Panel/User Mapping", portletState.getApplicationName().getValue());
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
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					aRes.setRenderParameter("jspPage", "/html/taxtypeportlet/taxtypelisting.jsp");
				}
			}else
			{
				portletState.addError(aReq, "This action can not be carried out on the selected tax type. Select one before proceeding", portletState);
				aRes.setRenderParameter("jspPage", "/html/taxtypeportlet/taxtypelisting.jsp");
			}
		}catch(NumberFormatException e)
		{
			e.printStackTrace();
			portletState.addError(aReq, "This action can not be carried out on the selected tax type. Select a valid tax type before proceeding", portletState);
			aRes.setRenderParameter("jspPage", "/html/taxtypeportlet/taxtypelisting.jsp");
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
