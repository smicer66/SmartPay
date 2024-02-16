package com.probase.smartpay.admin.portmanagement;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;

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

import smartpay.audittrail.AuditTrail;
import smartpay.entity.Ports;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.service.SwpService;

import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.service.ServiceContext;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.probase.smartpay.admin.portmanagement.PortManagementPortletState.PORTS_ACTION;
import com.probase.smartpay.admin.portmanagement.PortManagementPortletState.PORTS_VIEW;
import com.probase.smartpay.admin.portmanagement.PortManagementPortletState.NAVIGATE;
import com.probase.smartpay.admin.portmanagement.PortManagementPortletState.VIEW_TABS;
import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Portlet implementation class PortManagementPortlet
 */
public class PortManagementPortlet extends MVCPortlet {
 
	private Logger log = Logger.getLogger(PortManagementPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
	PortManagementPortletUtil util = PortManagementPortletUtil.getInstance();
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
		PortManagementPortletState portletState = 
				PortManagementPortletState.getInstance(renderRequest, renderResponse);

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
		
		PortManagementPortletState portletState = PortManagementPortletState.getInstance(aReq, aRes);
		
		
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
        
        if(action.equalsIgnoreCase(PORTS_ACTION.CREATE_A_NEW_PORT_ACTION.name()))
        {
        	log.info("handle create a fee description post action");
        	handleCreateNewPort(aReq, aRes, portletState);
        }if(action.equalsIgnoreCase(PORTS_ACTION.UPDATE_NEW_PORT_ACTION.name()))
        {
        	handleUpdatePort(aReq, aRes, portletState);
        }
        if(action.equalsIgnoreCase(PORTS_ACTION.HANDLE_PORT_LISTING_ACTION.name()))
        {
        	handlePortListingAction(aReq, aRes, portletState);
        }if(action.equalsIgnoreCase(NAVIGATE.NAVIGATE_ACTIONS.name()))
        {
        	handleNavigations(aReq, aRes, portletState);
        }if(action.equalsIgnoreCase(PORTS_VIEW.CREATE_A_NEW_PORT.name()))
        {
        	portletState.reinitializeForPorts(portletState);
        	aRes.setRenderParameter("jspPage", "/html/portmanagementportlet/createaport.jsp");
        	portletState.setCurrentTab(VIEW_TABS.CREATE_A_NEW_PORT);
        }if(action.equalsIgnoreCase(PORTS_VIEW.VIEW_PORT_LISTINGS.name()))
        {
        	portletState.setAllPortsListing(portletState.getPortManagementPortletUtil().getAllPortsListing(true));
        	aRes.setRenderParameter("jspPage", "/html/portmanagementportlet/portlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.VIEW_PORT_LISTINGS);
        }
		
	}


	private void handleCreateNewPort(ActionRequest aReq,
			ActionResponse aRes, PortManagementPortletState portletState) {
		// TODO Auto-generated method stub
		log.info("inside handleCreateNewPort ");
		portletState.setPortCode(aReq.getParameter("portCode"));
		log.info("portCode =  " + portletState.getPortCode());
		portletState.setFullName(aReq.getParameter("fullName"));
		
		
		if(isPortDataValid(portletState, aReq, aRes, true))
		{
			log.info("fee description data is valid ");
			
			
				Ports port = new Ports();
				port.setFullName(portletState.getFullName());
				port.setPortCode(portletState.getPortCode());
				port.setStatus(true);
				

				port = (Ports)swpService.createNewRecord(port);
				log.info("port = " + port.getId());
				handleAudit("Port Creation", Long.toString(port.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				portletState.addSuccess(aReq, "Fee Description - " + port.getFullName() + " (" + port.getPortCode() + ") - was created successfully!", portletState);
				portletState.setAllPortsListing(portletState.getPortManagementPortletUtil().getAllPortsListing(true));
	        	aRes.setRenderParameter("jspPage", "/html/portmanagementportlet/portlisting.jsp");
	        	portletState.setCurrentTab(VIEW_TABS.VIEW_PORT_LISTINGS);
			
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/portmanagementportlet/createaport.jsp");
		}
	}
	
	private void handleUpdatePort(ActionRequest aReq, ActionResponse aRes,
			PortManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setPortCode(aReq.getParameter("portCode"));
		portletState.setFullName(aReq.getParameter("fullName"));
		
		
		try
		{
			if(isPortDataValid(portletState, aReq, aRes, false))
			{
				
				Ports port = (Ports)portletState.getPortManagementPortletUtil().getEntityObjectById(Ports.class, Long.valueOf(portletState.getSelectedPortId()));
				port.setFullName(portletState.getFullName());
				port.setPortCode(portletState.getPortCode());
				
	
				swpService.updateRecord(port);
				log.info("feeDescription = " + port.getId());
				handleAudit("Port Update", Long.toString(port.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				aRes.setRenderParameter("jspPage", "/html/portmanagementportlet/portlisting.jsp");
				portletState.addSuccess(aReq, "Port update was successfully!", portletState);
				portletState.reinitializeForPorts(portletState);
				portletState.setAllPortsListing(portletState.getPortManagementPortletUtil().getAllPortsListing(true));
				
			}else
			{
				aRes.setRenderParameter("jspPage", "/html/portmanagementportlet/editaport.jsp");
			}
		}catch(NumberFormatException e)
		{
			e.printStackTrace();
			portletState.addError(aReq, "Select a valid fee description to update", portletState);
			aRes.setRenderParameter("jspPage", "/html/portmanagementportlet/editaport.jsp");
		}
		
	}
	
	
	
	
	private boolean isPortDataValid(PortManagementPortletState portletState,
			ActionRequest aReq, ActionResponse aRes, boolean checkExistingForNew) {
		// TODO Auto-generated method stub
		String errorMessage = null;
		
		if(portletState.getPortCode()!=null && portletState.getPortCode().trim().length()>0)
		{
			if(portletState.getFullName()!=null && portletState.getFullName().trim().length()>0)
			{
				
				
				if(checkExistingForNew)
				{
					Ports fd = null;
					fd = portletState.getPortManagementPortletUtil().getPortByNameOrPortCode(
							portletState.getFullName(), portletState.getPortCode());
					if(fd==null)
					{
						
					}else
					{
						errorMessage =  "The port code or name provided already exists on the system. Provide another port code to create one on the system.";
					}
				}else
				{
					Collection<Ports> fd = null;
					fd = portletState.getPortManagementPortletUtil().getPortByNameOrCodeAndNotId(
							portletState.getFullName(), portletState.getPortCode(), Long.valueOf(portletState.getSelectedPortId()));
					if(fd==null || (fd!=null && fd.size()==0))
					{
						
					}else
					{
						errorMessage =  "The port code or name provided already exists on the system. Provide another port code to create one on the system";
					}
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
			PortManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String action = aReq.getParameter("actionUrl");
		if(action.equalsIgnoreCase("createfeedescription"))
		{
			portletState.reinitializeForPorts(portletState);
			aRes.setRenderParameter("jspPage", "/html/portmanagementportlet/createaport.jsp");
			portletState.setCurrentTab(VIEW_TABS.CREATE_A_NEW_PORT);
		}else if(action.equalsIgnoreCase("portlistings"))
		{
			portletState.setAllPortsListing(portletState.getPortManagementPortletUtil().getAllPortsListing(true));
			aRes.setRenderParameter("jspPage", "/html/portmanagementportlet/portlisting.jsp");
			portletState.setCurrentTab(VIEW_TABS.VIEW_PORT_LISTINGS);
		}
	}


	
	
	
	
	private void handlePortListingAction(ActionRequest aReq,
			ActionResponse aRes, PortManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String feeDescId = aReq.getParameter("selectedPort").trim();
		log.info("selectedPort = " + feeDescId);
		try
		{
			Long feeDescIdL = Long.valueOf(feeDescId);
			log.info("feeDescIdL = " + feeDescIdL);
			Ports fd = (Ports)portletState.getPortManagementPortletUtil().getEntityObjectById(Ports.class, feeDescIdL);
			portletState.setSelectedPortId(feeDescId);
			if(fd!=null)
			{
				if(aReq.getParameter("selectedPortAction")!=null && aReq.getParameter("selectedPortAction").equalsIgnoreCase("update"))
				{
					portletState.setFullName(fd.getFullName());
					portletState.setPortCode(fd.getPortCode());
					aRes.setRenderParameter("jspPage", "/html/portmanagementportlet/editaport.jsp");
				
				}else if(aReq.getParameter("selectedPortAction")!=null && aReq.getParameter("selectedPortAction").equalsIgnoreCase("delete"))
				{
					fd.setStatus(false);
					swpService.updateRecord(fd);
					handleAudit("Port Delete", Long.toString(fd.getId()), new Timestamp((new Date()).getTime()), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
					aRes.setRenderParameter("jspPage", "/html/portmanagementportlet/portlisting.jsp");
					portletState.addSuccess(aReq, "Selected Port - " + fd.getFullName() + " - has been deleted successfully.", portletState);
					portletState.setAllPortsListing(portletState.getPortManagementPortletUtil().getAllPortsListing(true));
				}
			}else
			{
				portletState.addError(aReq, "This action can not be carried out on the selected port. You seem to have not selected a valid authorisation panel. Select one before proceeding", portletState);
				aRes.setRenderParameter("jspPage", "/html/portmanagementportlet/portlisting.jsp");
			}
		}catch(NumberFormatException e)
		{
			e.printStackTrace();
			portletState.addError(aReq, "This action can not be carried out on the selected port. Select a valid authorisation panel before proceeding", portletState);
			aRes.setRenderParameter("jspPage", "/html/portmanagementportlet/portlisting.jsp");
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
