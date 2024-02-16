<%@page import="com.probase.smartpay.admin.settingsmanagement.SettingsManagementPortletUtil"%>
<%@page	import="com.probase.smartpay.admin.settingsmanagement.SettingsManagementPortletState.*"%>
<%@page	import="com.probase.smartpay.admin.settingsmanagement.SettingsManagementPortletState"%>
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
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />

<%

SettingsManagementPortletState portletState = SettingsManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(SettingsManagementPortletState.class);

%>
<portlet:actionURL var="managesettings" name="processAction">
	<portlet:param name="action"
		value="<%=SETTINGS_VIEW.MANAGE_SETTINGS.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="viewsettings" name="processAction">
	<portlet:param name="action"
		value="<%=SETTINGS_VIEW.VIEW_SETTINGS.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="jobssettings" name="processAction">
	<portlet:param name="action"
		value="<%=SETTINGS_VIEW.MANAGE_JOBS.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="paybreakList" name="processAction">
	<portlet:param name="action"
		value="<%=SETTINGS_VIEW.VIEW_PAYMENT_BREAKDOWN.name()%>" />
</portlet:actionURL>
<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>



<%
String manageSettingsColor="#000000";	String manageSettingsBgColor="#CCCCCC";	
String viewSettingsColor="#000000"; String viewSettingsBgColor="#CCCCCC";	
String jobsSettingsColor="#000000"; String jobsSettingsBgColor="#CCCCCC";	
String paybreakListColor="#000000"; String paybreakListBgColor="#CCCCCC";


if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.MANAGE_SETTINGS))
{
	manageSettingsColor="#ffffff";
	manageSettingsBgColor="#000000";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_SETTINGS))
{
	viewSettingsColor="#ffffff";
	viewSettingsBgColor="#000000";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.MANAGE_JOBS))
{
	jobsSettingsColor="#ffffff";
	jobsSettingsBgColor="#000000";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_PAYMENT_BREAKDOWN))
{
	paybreakListColor="#ffffff";
	paybreakListBgColor="#000000";
}else
{
	manageSettingsColor="#ffffff";
	manageSettingsBgColor="#000000";
}

%>

<div style="padding-top: 20px">
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=managesettings%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px; 
		background-color:<%=manageSettingsBgColor %>; color:<%=manageSettingsColor %>">
		Update Settings</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=viewsettings%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px;  
		background-color:<%=viewSettingsBgColor %>; color:<%=viewSettingsColor %>">
		View Settings</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=jobssettings%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px;  
		background-color:<%=jobsSettingsBgColor %>; color:<%=jobsSettingsColor %>">
		Jobs & Schedulers</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=paybreakList%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px;  
		background-color:<%=paybreakListBgColor %>; color:<%=paybreakListColor %>">
		Payment Listings</div></a>
	</div>
</div>

<div style="clear:both; font-size:20px; height:20px">&nbsp;</div>