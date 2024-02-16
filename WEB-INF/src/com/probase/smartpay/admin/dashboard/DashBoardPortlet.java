package com.probase.smartpay.admin.dashboard;

import java.io.IOException;
import java.sql.Timestamp;
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
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import smartpay.audittrail.AuditTrail;
import smartpay.entity.BankBranches;
import smartpay.entity.Company;
import smartpay.entity.PortalUser;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.TpinInfo;
import smartpay.entity.enumerations.CompanyStatusConstants;
import smartpay.entity.enumerations.CompanyTypeConstants;
import smartpay.entity.enumerations.PortalUserStatusConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
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
import com.probase.smartpay.admin.dashboard.DashBoardPortlet;
import com.probase.smartpay.admin.dashboard.DashBoardPortletState;
import com.probase.smartpay.admin.dashboard.DashBoardPortletUtil;
import com.probase.smartpay.commins.Emailer;
import com.probase.smartpay.commins.ProbaseConstants;
import com.probase.smartpay.commins.SendMail;
import com.probase.smartpay.commins.SendSms;
import com.probase.smartpay.commins.Util;
import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Portlet implementation class DashBoardPortlet
 */
public class DashBoardPortlet extends MVCPortlet {
	private Logger log = Logger.getLogger(DashBoardPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
	DashBoardPortletUtil util = DashBoardPortletUtil.getInstance();
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
		DashBoardPortletState portletState = 
				DashBoardPortletState.getInstance(renderRequest, renderResponse);

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
		
		DashBoardPortletState portletState = DashBoardPortletState.getInstance(aReq, aRes);
		
		
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
//        if (action.equalsIgnoreCase(COMPANY_CREATION.CREATE_A_COMPANY_STEP_ONE.name()))
//        {
//        	createACompanyStepOne(aReq, aRes, portletState);
//        }
		
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
