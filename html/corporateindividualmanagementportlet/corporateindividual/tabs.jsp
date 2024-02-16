<%@page import="com.probase.smartpay.admin.corporateindividualmanagement.CorporateIndividualManagementPortletUtil"%>
<%@page	import="com.probase.smartpay.admin.corporateindividualmanagement.CorporateIndividualManagementPortletState.*"%>
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
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="com.probase.smartpay.commins.ComminsApplicationState"%>
<%@page import="com.probase.smartpay.commins.Util"%>
<%@page import="com.probase.smartpay.commins.Util.DETERMINE_ACCESS"%>
<%@page import="com.probase.smartpay.admin.corporateindividualmanagement.CorporateIndividualManagementPortletState"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />

<%

CorporateIndividualManagementPortletState portletState = CorporateIndividualManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(CorporateIndividualManagementPortletState.class);
ComminsApplicationState cappState = portletState.getCas();
Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
DETERMINE_ACCESS determinAccess = new Util().determineAccessForUserFxns(twoStep, approvalProcess, cappState, portletState.getPortalUserCRUDRights());

%>
<portlet:actionURL var="createcorporateindividual" name="processAction">
	<portlet:param name="action"
		value="<%=CORPORATE_INDIVIDUAL_VIEW.CREATE_A_CORPORATE_INDIVIDUAL.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="viewcorporateindividuallisting" name="processAction">
	<portlet:param name="action"
		value="<%=CORPORATE_INDIVIDUAL_VIEW.VIEW_CORPORATE_INDIVIDUAL_LISTINGS.name()%>" />
</portlet:actionURL>
<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>



<%
boolean proceed = true;
if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
{
	
	if(determinAccess.equals(DETERMINE_ACCESS.DISPLAY_SECOND_LEVEL_LOGIN))
	{
		proceed = false;
		portletState.setCurrentTab(VIEW_TABS.VIEW_CORPORATE_INDIVIDUAL_LISTINGS);
	}else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_APPROVER_ACCESS))
	{
		proceed = false;
		portletState.setCurrentTab(VIEW_TABS.VIEW_CORPORATE_INDIVIDUAL_LISTINGS);
	}else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
	{
		proceed = true;
		portletState.setCurrentTab(VIEW_TABS.CREATE_A_CORPORATE_INDIVIDUAL);
	}else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
	{
		proceed = true;
		portletState.setCurrentTab(VIEW_TABS.CREATE_A_CORPORATE_INDIVIDUAL);
	}else if(determinAccess.equals(DETERMINE_ACCESS.NO_RIGHTS_AT_ALL))
	{
		proceed = false;
		portletState.setCurrentTab(VIEW_TABS.VIEW_CORPORATE_INDIVIDUAL_LISTINGS);
	}
}

String createCorporateIndividualColor="#000000";	String createCorporateIndividualBgColor="#CCCCCC";	
String viewCorporateIndividualColor="#000000"; String viewCorporateIndividualBgColor="#CCCCCC";	


if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.CREATE_A_CORPORATE_INDIVIDUAL))
{
	createCorporateIndividualColor="#ffffff";
	createCorporateIndividualBgColor="#000000";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_CORPORATE_INDIVIDUAL_LISTINGS))
{
	viewCorporateIndividualColor="#ffffff";
	viewCorporateIndividualBgColor="#000000";
}else
{
	if(determinAccess.equals(DETERMINE_ACCESS.DISPLAY_SECOND_LEVEL_LOGIN))
	{
		viewCorporateIndividualColor="#ffffff";
		viewCorporateIndividualBgColor="#000000";
	}else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_APPROVER_ACCESS))
	{
		viewCorporateIndividualColor="#ffffff";
		viewCorporateIndividualBgColor="#000000";
	}else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
	{
		createCorporateIndividualColor="#ffffff";
		createCorporateIndividualBgColor="#000000";
	}else if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
	{
		createCorporateIndividualColor="#ffffff";
		createCorporateIndividualBgColor="#000000";;
	}else if(determinAccess.equals(DETERMINE_ACCESS.NO_RIGHTS_AT_ALL))
	{
		viewCorporateIndividualColor="#ffffff";
		viewCorporateIndividualBgColor="#000000";
	}
	
}

%>

<div style="padding-top: 20px">
	<%
	
	
	if(proceed)
	{
	%>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=createcorporateindividual%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px; 
		background-color:<%=createCorporateIndividualBgColor %>; color:<%=createCorporateIndividualColor %>">
		Create A Company Staff Profile</div></a>
	</div>
	<%
	}
	%>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=viewcorporateindividuallisting%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px;  
		background-color:<%=viewCorporateIndividualBgColor %>; color:<%=viewCorporateIndividualColor %>">
		View Company Staff Listings</div></a>
	</div>
</div>

<div style="clear:both; font-size:20px; height:20px">&nbsp;</div>