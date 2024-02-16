<%@page import="com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortletUtil"%>
<%@page	import="com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortletState.*"%>
<%@page	import="com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortletState"%>
<%@page import="com.probase.smartpay.commins.ComminsApplicationState"%>
<%@page import="com.probase.smartpay.commins.Util"%>
<%@page import="com.probase.smartpay.commins.Util.DETERMINE_ACCESS"%>
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
<%@page import="smartpay.entity.enumerations.SmartPayConstants"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@page import="org.apache.log4j.Logger"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />

<%

UserManagementSystemAdminPortletState portletState = UserManagementSystemAdminPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(UserManagementSystemAdminPortletState.class);
ComminsApplicationState cappState = portletState.getCas();
Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
DETERMINE_ACCESS determinAccess = new Util().determineAccessForUserFxns(twoStep, approvalProcess, cappState, portletState.getPortalUserCRUDRights());
%>
<portlet:actionURL var="managesettings" name="processAction">
	<portlet:param name="action"
		value="<%=USER_MANAGEMENT_SYSTEM_ADMIN_VIEW.CREATE_A_PORTAL_USER.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="viewsettings" name="processAction">
	<portlet:param name="action"
		value="<%=USER_MANAGEMENT_SYSTEM_ADMIN_VIEW.VIEW_PORTAL_USERS.name()%>" />
</portlet:actionURL>
<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>



<%
String manageSettingsColor="#000000";	String manageSettingsBgColor="#CCCCCC";	
String viewSettingsColor="#000000"; String viewSettingsBgColor="#CCCCCC";
String manageSettingsClassType="label-default"; String viewSettingsClassType="label-default";


if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.CREATE_A_PORTAL_USER))
{
	manageSettingsColor="#ffffff";
	manageSettingsBgColor="#000000";
	viewSettingsClassType="label-default";
	manageSettingsClassType="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_PORTAL_USER_LISTINGS))
{
	viewSettingsColor="#ffffff";
	viewSettingsBgColor="#000000";
	manageSettingsClassType="label-default";
	viewSettingsClassType="label-primary";
}else
{
	manageSettingsColor="#ffffff";
	manageSettingsBgColor="#000000";
	manageSettingsClassType="label-primary";
	viewSettingsClassType="label-default";
}

%>

<div style="padding-top: 20px">
<%
if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_SYSTEM_SUPER_ADMINISTRATOR))
{
%>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=managesettings%>">
		<div class="label <%=manageSettingsClassType%>" style="padding:10px; color:<%=manageSettingsColor %>">
		Create A Portal User</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=viewsettings%>">
		<div class="label <%=viewSettingsClassType%>" style="padding:10px; color:<%=viewSettingsColor %>">
		View Portal Users</div></a>
	</div>
<%
}else
if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_SUPER_ADMINISTRATOR))
{
	if(determinAccess.equals(DETERMINE_ACCESS.DISPLAY_SECOND_LEVEL_LOGIN))
	{
		%>
		<jsp:include page="/html/usermanagementsystemadminportlet/login_step2.jsp" flush="" />
		<%
	}
	if(determinAccess.equals(DETERMINE_ACCESS.GRANT_APPROVER_ACCESS))
	{
		%>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=viewsettings%>">
			<div class="label <%=viewSettingsClassType%>" style="padding:10px; color:<%=viewSettingsColor %>">
			View Portal Users</div></a>
		</div>
		<%
	}
	if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
	{
		%>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=managesettings%>">
			<div class="label <%=manageSettingsClassType%>" style="padding:10px; color:<%=manageSettingsColor %>">
			Create A Portal User</div></a>
		</div>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=viewsettings%>">
			<div class="label <%=viewSettingsClassType%>" style="padding:10px; color:<%=viewSettingsColor %>">
			View Portal Users</div></a>
		</div>
		<%
	}
	if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
	{
		%>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=managesettings%>">
			<div class="label <%=manageSettingsClassType%>" style="padding:10px; color:<%=manageSettingsColor %>">
			Create A Portal User</div></a>
		</div>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=viewsettings%>">
			<div class="label <%=viewSettingsClassType%>" style="padding:10px; color:<%=viewSettingsColor %>">
			View Portal Users</div></a>
		</div>
		<%
	}
	if(determinAccess.equals(DETERMINE_ACCESS.NO_RIGHTS_AT_ALL))
	{
		
	}
}else
if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
{
	if(determinAccess.equals(DETERMINE_ACCESS.DISPLAY_SECOND_LEVEL_LOGIN))
	{
		%>
		<jsp:include page="/html/usermanagementsystemadminportlet/login_step2.jsp" flush="" />
		<%
	}
	if(determinAccess.equals(DETERMINE_ACCESS.GRANT_APPROVER_ACCESS))
	{
		%>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=viewsettings%>">
			<div class="label <%=viewSettingsClassType%>" style="padding:10px; color:<%=viewSettingsColor %>">
			View Portal Users</div></a>
		</div>
		<%
	}
	if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
	{
		%>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=managesettings%>">
			<div class="label <%=manageSettingsClassType%>" style="padding:10px; color:<%=manageSettingsColor %>">
			Create A Portal User</div></a>
		</div>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=viewsettings%>">
			<div class="label <%=viewSettingsClassType%>" style="padding:10px; color:<%=viewSettingsColor %>">
			View Portal Users</div></a>
		</div>
		<%
	}
	if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
	{
		%>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=managesettings%>">
			<div class="label <%=manageSettingsClassType%>" style="padding:10px; color:<%=manageSettingsColor %>">
			Create A Portal User</div></a>
		</div>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=viewsettings%>">
			<div class="label <%=viewSettingsClassType%>" style="padding:10px; color:<%=viewSettingsColor %>">
			View Portal Users</div></a>
		</div>
		<%
	}
	if(determinAccess.equals(DETERMINE_ACCESS.NO_RIGHTS_AT_ALL))
	{
		
	}
}else
{

}
%>
</div>

<div style="clear:both; font-size:20px; height:20px">&nbsp;</div>