<%@page import="com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortletState"%>
<%@page import="com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortletState.*"%>
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

UserManagementSystemAdminPortletState portletState = UserManagementSystemAdminPortletState.getInstance(renderRequest, renderResponse);
ComminsApplicationState cappState = portletState.getCas();
Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
DETERMINE_ACCESS determinAccess = new Util().determineAccessForUserFxns(twoStep, approvalProcess, cappState, portletState.getPortalUserCRUDRights());
%>

<%



if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_SYSTEM_SUPER_ADMINISTRATOR))
{
%>
<jsp:include page="/html/usermanagementsystemadminportlet/register/stepone.jsp" flush="" />
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
		<jsp:include page="/html/usermanagementsystemadminportlet/userlisting.jsp" flush="" />
		<%
	}
	if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
	{
		if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.CREATE_A_PORTAL_USER))
		{
		%>
		<jsp:include page="/html/usermanagementsystemadminportlet/register/stepone.jsp" flush="" />
		<%
		}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_PORTAL_USER_LISTINGS))
		{
		%>
		<jsp:include page="/html/usermanagementsystemadminportlet/userlisting.jsp" flush="" />
		<%	
		}
	}
	if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
	{
		if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.CREATE_A_PORTAL_USER))
		{
		%>
		<jsp:include page="/html/usermanagementsystemadminportlet/register/stepone.jsp" flush="" />
		<%
		}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_PORTAL_USER_LISTINGS))
		{
		%>
		<jsp:include page="/html/usermanagementsystemadminportlet/userlisting.jsp" flush="" />
		<%	
		}
	}
	if(determinAccess.equals(DETERMINE_ACCESS.NO_RIGHTS_AT_ALL))
	{
		%>
		<div class="panel panel-danger">You do not have access to carry out any actions as you do not have valid access. Contact Appropriate Administrators for rights</div>
		<%
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
		<jsp:include page="/html/usermanagementsystemadminportlet/userlisting.jsp" flush="" />
		<%
	}
	if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
	{
		if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.CREATE_A_PORTAL_USER))
		{
		%>
		<jsp:include page="/html/usermanagementsystemadminportlet/register/stepone.jsp" flush="" />
		<%
		}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_PORTAL_USER_LISTINGS))
		{
		%>
		<jsp:include page="/html/usermanagementsystemadminportlet/userlisting.jsp" flush="" />
		<%	
		}
	}
	if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
	{
		if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.CREATE_A_PORTAL_USER))
		{
		%>
		<jsp:include page="/html/usermanagementsystemadminportlet/register/stepone.jsp" flush="" />
		<%
		}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_PORTAL_USER_LISTINGS))
		{
		%>
		<jsp:include page="/html/usermanagementsystemadminportlet/userlisting.jsp" flush="" />
		<%	
		}
	}
	if(determinAccess.equals(DETERMINE_ACCESS.NO_RIGHTS_AT_ALL))
	{
		%>
		<div class="panel panel-danger">You do not have access to carry out any actions as you do not have valid access. Contact Appropriate Administrators for rights</div>
		<%
	}
}else
{
%>
<div class="panel panel-danger">You do not have access to carry out any actions as you do not have valid access. Contact Appropriate Administrators for rights</div>
<%
}
%>