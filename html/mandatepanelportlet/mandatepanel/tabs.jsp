<%@page import="com.probase.smartpay.admin.mandatepanelmanagement.MandatePanelPortletUtil"%>
<%@page	import="com.probase.smartpay.admin.mandatepanelmanagement.MandatePanelPortletState.*"%>
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
<%@page import="com.probase.smartpay.admin.mandatepanelmanagement.MandatePanelPortletState"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />

<%

MandatePanelPortletState portletState = MandatePanelPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(MandatePanelPortletState.class);

%>
<portlet:actionURL var="createmandatepanel" name="processAction">
	<portlet:param name="action"
		value="<%=MANDATE_PANEL_VIEW.CREATE_A_MANDATE_PANEL.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="viewmandatepanellistings" name="processAction">
	<portlet:param name="action"
		value="<%=MANDATE_PANEL_VIEW.VIEW_MANDATE_PANEL_LISTINGS.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="mapmandatepanellistings" name="processAction">
	<portlet:param name="action"
		value="<%=MANDATE_PANEL_VIEW.MAP_PANEL_TO_PORTAL_USER.name()%>" />
</portlet:actionURL>
<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>



<%
String createmandatepanelColor="#000000";	String createmandatepanelBgColor="#CCCCCC";	
String viewmandatepanelColor="#000000"; String viewmandatepanelBgColor="#CCCCCC";
String mapmandatepanelColor="#000000"; String mapmandatepanelBgColor="#CCCCCC";


if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.CREATE_A_MANDATE_PANEL))
{
	createmandatepanelColor="#ffffff";
	createmandatepanelBgColor="#000000";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_MANDATE_PANEL_LISTINGS))
{
	viewmandatepanelColor="#ffffff";
	viewmandatepanelBgColor="#000000";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.MAP_PANEL_TO_PORTAL_USER))
{
	mapmandatepanelColor="#ffffff";
	mapmandatepanelBgColor="#000000";
}else
{
	createmandatepanelColor="#ffffff";
	createmandatepanelBgColor="#000000";
}

%>

<div style="padding:10px;">
	<div style="padding-top: 20px">
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=createmandatepanel%>">
			<div style="padding:5px; padding-left:8px; padding-right:10px; 
			background-color:<%=createmandatepanelBgColor %>; color:<%=createmandatepanelColor %>">
			Create A Mandate Panel</div></a>
		</div>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=viewmandatepanellistings%>">
			<div style="padding:5px; padding-left:8px; padding-right:10px;  
			background-color:<%=viewmandatepanelBgColor %>; color:<%=viewmandatepanelColor %>">
			View Mandate Panel Listings</div></a>
		</div>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=mapmandatepanellistings%>">
			<div style="padding:5px; padding-left:8px; padding-right:10px;  
			background-color:<%=mapmandatepanelBgColor %>; color:<%=mapmandatepanelColor %>">
			Add Company Personnel To Mandate Panels</div></a>
		</div>
		
	</div>
</div>

<div style="clear:both; font-size:20px; height:20px">&nbsp;</div>