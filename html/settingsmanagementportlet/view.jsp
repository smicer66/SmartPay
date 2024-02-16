<%@page import="com.probase.smartpay.admin.settingsmanagement.SettingsManagementPortletState"%>
<%@page import="com.probase.smartpay.admin.settingsmanagement.SettingsManagementPortletState.*"%>
<%@page import="java.util.Collection"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@ page import="com.liferay.portal.kernel.util.Validator"%>
<%@ page import="javax.portlet.PortletPreferences"%>
<%@ page import="javax.portlet.PortletURL"%>
<%@ page import="javax.portlet.ActionRequest"%>
<%@ page import="javax.portlet.RenderResponse"%>
<%@ page import="java.lang.NumberFormatException"%>
<%@ page import="com.liferay.portal.util.PortalUtil"%>
<%@ page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.util.TimeZone"%>
<%@ page import="java.util.GregorianCalendar"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="smartpay.entity.BankBranches"%>
<%@page import="smartpay.entity.Company"%>
<%@page import="smartpay.entity.FeeDescription"%>
<%@page import="smartpay.entity.enumerations.SmartPayConstants"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.Settings"%>
<%@page import="smartpay.entity.AuthorizePanelCombination"%>
<%@page import="java.text.DateFormat"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />
<%

SettingsManagementPortletState portletState = SettingsManagementPortletState.getInstance(renderRequest, renderResponse);

%>

<%
if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_SUPER_ADMINISTRATOR))
{
	if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.MANAGE_JOBS))
	{
%>
<jsp:include page="/html/settingsmanagementportlet/settings/manageschedulers.jsp" flush="" />
<%
	}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.MANAGE_SETTINGS))
	{
%>
<jsp:include page="/html/settingsmanagementportlet/settings/managesettings.jsp" flush="" />
<%
	}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_SETTINGS))
	{
%>
<jsp:include page="/html/settingsmanagementportlet/settings/managesettings.jsp" flush="" />
<%
	}else
	{
%>
<jsp:include page="/html/settingsmanagementportlet/settings/managesettings.jsp" flush="" />
<%
	}
}else
{
%>
You are not allowed to view this page
<%
}
%>