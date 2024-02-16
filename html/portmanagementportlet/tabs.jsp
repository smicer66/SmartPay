<%@page import="com.probase.smartpay.admin.portmanagement.PortManagementPortletState.*"%>
<%@page	import="com.probase.smartpay.admin.portmanagement.PortManagementPortletState"%>
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
<%@page import="com.probase.smartpay.admin.feedescriptionmanagement.FeeDescriptionPortletState"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />

<%

PortManagementPortletState portletState = PortManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(FeeDescriptionPortletState.class);

%>
<portlet:actionURL var="createport" name="processAction">
	<portlet:param name="action"
		value="<%=PORTS_VIEW.CREATE_A_NEW_PORT.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="viewports" name="processAction">
	<portlet:param name="action"
		value="<%=PORTS_VIEW.VIEW_PORT_LISTINGS.name()%>" />
</portlet:actionURL>
<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>



<%
String createFeeDescriptionColor="#000000";	String createFeeDescriptionBgColor="#CCCCCC";	
String viewFeeDescriptionColor="#000000"; String viewFeeDescriptionBgColor="#CCCCCC";	


if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.CREATE_A_NEW_PORT))
{
	createFeeDescriptionColor="#ffffff";
	createFeeDescriptionBgColor="#000000";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_PORT_LISTINGS))
{
	viewFeeDescriptionColor="#ffffff";
	viewFeeDescriptionBgColor="#000000";
}else
{
	createFeeDescriptionColor="#ffffff";
	createFeeDescriptionBgColor="#000000";
}

%>

<div style="padding:10px;">
	<div style="padding-top: 20px">
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=createport%>">
			<div style="padding:5px; padding-left:8px; padding-right:10px; 
			background-color:<%=createFeeDescriptionBgColor %>; color:<%=createFeeDescriptionColor %>">
			Create A New Port</div></a>
		</div>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=viewports%>">
			<div style="padding:5px; padding-left:8px; padding-right:10px;  
			background-color:<%=viewFeeDescriptionBgColor %>; color:<%=viewFeeDescriptionColor %>">
			View Port Listings</div></a>
		</div>
	</div>
</div>

<div style="clear:both; font-size:20px; height:20px">&nbsp;</div>