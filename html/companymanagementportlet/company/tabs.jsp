<%@page import="com.probase.smartpay.admin.companymanagement.CompanyManagementPortletUtil"%>
<%@page	import="com.probase.smartpay.admin.companymanagement.CompanyManagementPortletState.*"%>
<%@page import="java.util.Collection"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@ page import="com.liferay.portal.kernel.util.Validator"%>
<%@ page import="javax.portlet.PortletPreferences"%>
<%@ page import="javax.portlet.PortletURL"%>
<%@ page import="javax.portlet.ActionRequest"%>
<%@ page import="javax.portlet.RenderResponse"%>
<%@ page import="com.liferay.portal.util.PortalUtil"%>
<%@ page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="com.probase.smartpay.admin.companymanagement.CompanyManagementPortletState"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />

<%

CompanyManagementPortletState portletState = CompanyManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(CompanyManagementPortletState.class);

%>
<portlet:actionURL var="createCompany" name="processAction">
	<portlet:param name="action"
		value="<%=COMPANY_VIEW.CREATE_A_COMPANY.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="viewCompanyListings" name="processAction">
	<portlet:param name="action"
		value="<%=COMPANY_VIEW.VIEW_COMPANY_LISTINGS.name()%>" />
</portlet:actionURL>
<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>



<%
String createCompanyColor="#000000";	String createCompanyBgColor="#CCCCCC";	
String viewCompanyColor="#000000"; String viewCompanyBgColor="#CCCCCC";	


if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.CREATE_A_COMPANY))
{
	createCompanyColor="#ffffff";
	createCompanyBgColor="#000000";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_COMPANY_LISTINGS))
{
	viewCompanyColor="#ffffff";
	viewCompanyBgColor="#000000";
}else
{
	createCompanyColor="#ffffff";
	createCompanyBgColor="#000000";
}

%>

<div style="padding-top: 20px; padding-left: 10px;">
	<%
	boolean proceed = true;
	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
	{
		
		if(portletState.getCompanyCRUDRights()!=null && 
				portletState.getCompanyCRUDRights().getCudInitiatorRights()!=null && 
					portletState.getCompanyCRUDRights().getCudInitiatorRights().equals(Boolean.TRUE))
		{
			
		}
		else
		{
			proceed= false;
			viewCompanyColor="#ffffff";
			viewCompanyBgColor="#000000";
		}
	}
	
	if(proceed)
	{
	%>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=createCompany%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px; 
		background-color:<%=createCompanyBgColor %>; color:<%=createCompanyColor %>">
		Create A Company</div></a>
	</div>
	<%
	}
	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
	%>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=viewCompanyListings%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px;  
		background-color:<%=viewCompanyBgColor %>; color:<%=viewCompanyColor %>">
		View Company Listings</div></a>
	</div>
</div>

<div style="clear:both; font-size:20px; height:20px">&nbsp;</div>