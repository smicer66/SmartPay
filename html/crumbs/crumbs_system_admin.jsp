<%@page import="com.probase.smartpay.admin.taxassessmentmanagement.TaxAssessmentManagementPortletState"%>
<%@page import="com.probase.smartpay.admin.taxassessmentmanagement.TaxAssessmentManagementPortletState.*"%>
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
<%@page import="smartpay.entity.Ports"%>
<%@page import="smartpay.entity.Company"%>
<%@page import="smartpay.entity.Assessment"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="com.probase.smartpay.commins.ProbaseConstants.CORE_VIEW"%>
<%@page import="java.util.ArrayList"%>
<%@page import="smartpay.entity.TpinInfo"%>
<%@page import="java.text.DateFormat"%>
<%@page import="com.probase.smartpay.commins.TaxBreakDownResponse"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />
<%
	String resourceBaseURL = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ "/resources";
	String faceboxCssUrl = resourceBaseURL + "/css/facebox.css";
	String pagingUrl = resourceBaseURL + "/css/paging.css";

	String jqueryDataTableCssUrl = resourceBaseURL + "/css/jquery.dataTables.css";
	String jqueryDataTableThemeCssUrl = resourceBaseURL + "/css/jquery.dataTables_themeroller.css";
	String jqueryDataTableUrl = resourceBaseURL + "/js/jquery.dataTables.min.js";

		
	String jqueryUICssUrl = resourceBaseURL + "/css/jquery-ui.min.css";
	
	String jqueryJsUrl = resourceBaseURL + "/js/jquery-1.10.2.min.js";
	String jqueryUIJsUrl = resourceBaseURL + "/js/jquery-ui.min.js";
%>
<script type="text/javascript"
	src="<%=(resourceBaseURL + "/js/jquery.validate.js")%>"></script>
<script type="text/javascript"
	src="<%=(resourceBaseURL + "/js/facebox.js")%>"></script>
<script type="text/javascript"
	src="<%=(resourceBaseURL + "/js/paging.js")%>"></script>
<link rel="stylesheet" href='<%=faceboxCssUrl%>' type="text/css" />
<link rel="stylesheet" href='<%=pagingUrl%>' type="text/css" />
<link href="<%=jqueryUICssUrl%>" rel="stylesheet" type="text/css" /><%

TaxAssessmentManagementPortletState portletState = TaxAssessmentManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(TaxAssessmentManagementPortletState.class);

String bankStaffMgmtColor="#000000";	String bankStaffMgmtBgColor="#CCCCCC";	
String bankBranchMgmtColor="#000000"; String bankBranchMgmtBgColor="#CCCCCC";	
String taxFeesMgmtColor="#000000";	String taxFeesMgmtBgColor="#CCCCCC";	
String portsMgmtColor="#000000"; String portsMgmtBgColor="#CCCCCC";	
String portalSettingsColor="#000000";	String portalSettingsBgColor="#CCCCCC";	

String finAmtRestMgmtColor="#000000";	String finAmtRestMgmtBgColor="#CCCCCC";	
String authMandateMgmtColor="#000000"; String authMandateMgmtBgColor="#CCCCCC";	
String corporateFirmsMgmtColor="#000000";	String corporateFirmsMgmtBgColor="#CCCCCC";	
String corporateStaffMgmtColor="#000000"; String corporateStaffMgmtBgColor="#CCCCCC";	

String corp_admin_finAmtRestMgmtColor="#000000";	String corp_admin_finAmtRestMgmtBgColor="#CCCCCC";	
String corp_admin_authMandateMgmtColor="#000000"; String corp_admin_authMandateMgmtBgColor="#CCCCCC";	
String corp_admin_corporateFirmsMgmtColor="#000000";	String corp_admin_corporateFirmsMgmtBgColor="#CCCCCC";	
String corp_admin_corporateStaffMgmtColor="#000000"; String corp_admin_corporateStaffMgmtBgColor="#CCCCCC";	

String corporateIndividualsMgmtColor="#000000";	String corporateIndividualsMgmtBgColor="#CCCCCC";	
String corp_staff_assessementsColor="#000000";	String corp_staff_assessementsBgColor="#CCCCCC";	
String corp_staff_workflowsColor="#000000";	String corp_staff_workflowsBgColor="#CCCCCC";	

String corporateStaffColor="#000000"; String corporateStaffBgColor="#CCCCCC";	

%>




<%
if(portletState.getPortalUser()!=null && 
portletState.getPortalUser().getRoleType().getRoleTypeName().equals(
		RoleTypeConstants.ROLE_TYPE_BANK_SUPER_ADMINISTRATOR))
{
	if(portletState.getCoreCurrentTab()!=null && portletState.getCoreCurrentTab().equals(CORE_VIEW.BANK_STAFF_MGMT))
	{
		bankStaffMgmtColor="#ffffff";
		bankStaffMgmtBgColor="#000000";
	}else if(portletState.getCoreCurrentTab()!=null && portletState.getCoreCurrentTab().equals(CORE_VIEW.PORTAL_SETTINGS))
	{
		portalSettingsColor="#ffffff";
		portalSettingsBgColor="#000000";
	}else
	{
		bankStaffMgmtColor="#ffffff";
		bankStaffMgmtBgColor="#000000";
	}
%>
<div style="width:250px; padding:2px;">
	<a href="#">
<div style="padding:5px; padding-left:8px; padding-right:10px; background-color:#000066; color:#FFFFFF">My Tools: Quick Access</div>
	<a href="#">
<div style="padding:5px; padding-left:8px; padding-right:10px; 
		background-color:<%=bankStaffMgmtBgColor %>; color:<%=bankStaffMgmtColor %>">Bank Staff Management</div></a>
	<a href="#">
<div style="padding:5px; padding-left:8px; padding-right:10px; 
		background-color:<%=portalSettingsBgColor %>; color:<%=portalSettingsColor %>">Portal Settings</div></a>
</div>
<% 
}
if(portletState.getPortalUser()!=null &&
portletState.getPortalUser().getRoleType().getRoleTypeName().equals(
		RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
{
	if(portletState.getCoreCurrentTab()!=null && portletState.getCoreCurrentTab().equals(CORE_VIEW.BANK_BRANCH_MGMT))
	{
		bankBranchMgmtColor="#ffffff";
		bankBranchMgmtBgColor="#000000";
	}else if(portletState.getCoreCurrentTab()!=null && portletState.getCoreCurrentTab().equals(CORE_VIEW.TAX_FEES_MGMT))
	{
		taxFeesMgmtColor="#ffffff";
		taxFeesMgmtBgColor="#000000";
	}else if(portletState.getCoreCurrentTab()!=null && portletState.getCoreCurrentTab().equals(CORE_VIEW.PORT_MGMT))
	{
		portsMgmtColor="#ffffff";
		portsMgmtBgColor="#000000";
	}else if(portletState.getCoreCurrentTab()!=null && portletState.getCoreCurrentTab().equals(CORE_VIEW.CORPORATE_FIRMS_MGMT))
	{
		corporateFirmsMgmtColor="#ffffff";
		corporateFirmsMgmtBgColor="#000000";
	}else if(portletState.getCoreCurrentTab()!=null && portletState.getCoreCurrentTab().equals(CORE_VIEW.INDIVIDUAL_MANAGEMENT))
	{
		corporateFirmsMgmtColor="#ffffff";
		corporateFirmsMgmtBgColor="#000000";
	}else
	{
		bankStaffMgmtColor="#ffffff";
		bankStaffMgmtBgColor="#000000";
	}
%>
<div style="width:250px; padding:2px;">
	<a href="#">
<div style="padding:5px; padding-left:8px; padding-right:10px; background-color:#000066; color:#FFFFFF">My Tools: Quick Access</div>
	<a href="#">
<div style="padding:5px; padding-left:8px; padding-right:10px; 
		background-color:<%=bankBranchMgmtBgColor %>; color:<%=bankBranchMgmtColor %>">Bank Branches Management</div></a>
	<a href="#">
<div style="padding:5px; padding-left:8px; padding-right:10px; 
		background-color:<%=taxFeesMgmtBgColor %>; color:<%=taxFeesMgmtColor %>">Tax Fees Management</div></a>
	<a href="#">
<div style="padding:5px; padding-left:8px; padding-right:10px; 
		background-color:<%=portsMgmtBgColor %>; color:<%=portsMgmtColor %>">Ports Management</div></a>
	<a href="#">
<div style="padding:5px; padding-left:8px; padding-right:10px; 
		background-color:<%=corporateFirmsMgmtBgColor %>; color:<%=corporateFirmsMgmtColor %>">Corporate Firms Management</div></a>
	<a href="#">
<div style="padding:5px; padding-left:8px; padding-right:10px; 
		background-color:<%=corporateIndividualsMgmtBgColor %>; color:<%=corporateIndividualsMgmtColor %>">Retail Management</div></a>
</div>
<%
}
if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
{
%>
<div style="width:250px; padding:2px;">
	<div style="background-color:#000066; color:#FFFFFF">My Tools: Quick Access</div>
	<a href="#">
<div style="padding:5px; padding-left:8px; padding-right:10px; 
		background-color:<%=corp_admin_finAmtRestMgmtBgColor %>; color:<%=corp_admin_finAmtRestMgmtColor %>">Financial Amount Restrictions</div></a>
	<a href="#">
<div style="padding:5px; padding-left:8px; padding-right:10px; 
		background-color:<%=corp_admin_authMandateMgmtBgColor %>; color:<%=corp_admin_authMandateMgmtColor %>">Authorisation Mandate Panels</div></a>
	<a href="#">
<div style="padding:5px; padding-left:8px; padding-right:10px; 
		background-color:<%=corp_admin_corporateStaffMgmtBgColor %>; color:<%=corp_admin_corporateStaffMgmtColor %>">Corporate Firm Staff Management</div></a>
</div>
<%
}
if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_NON_ADMINISTRATOR_PERSONNEL))
{
%>
<div style="width:250px; padding:2px;">
	<a href="#">
<div style="background-color:#000066; color:#FFFFFF">My Tools: Quick Access</div>
	<a href="#">
<div style="padding:5px; padding-left:8px; padding-right:10px; 
		background-color:<%=corp_staff_assessementsBgColor %>; color:<%=corp_staff_assessementsColor %>">Assessments</div></a>
	<a href="#">
<div style="padding:5px; padding-left:8px; padding-right:10px; 
		background-color:<%=corp_staff_workflowsBgColor %>; color:<%=corp_staff_workflowsColor%>">Work Flows</div></a>
</div>
<%
}
%>
</body>
</html>