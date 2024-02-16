<%@page import="com.probase.smartpay.admin.feedescriptionmanagement.FeeDescriptionPortletUtil"%>
<%@page	import="com.probase.smartpay.admin.feedescriptionmanagement.FeeDescriptionPortletState.*"%>
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
<%@page import="com.probase.smartpay.commins.ComminsApplicationState"%>
<%@page import="com.probase.smartpay.commins.Util"%>
<%@page import="com.probase.smartpay.commins.Util.DETERMINE_ACCESS"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="com.probase.smartpay.admin.feedescriptionmanagement.FeeDescriptionPortletState"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />

<%

FeeDescriptionPortletState portletState = FeeDescriptionPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(FeeDescriptionPortletState.class);
ComminsApplicationState cappState = portletState.getCas();
Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
DETERMINE_ACCESS determinAccess = new Util().determineAccessForUserFxns(twoStep, approvalProcess, cappState, portletState.getPortalUserCRUDRights());
%>
<portlet:actionURL var="createfeedescription" name="processAction">
	<portlet:param name="action"
		value="<%=FEE_DESCRIPTION_VIEW.CREATE_A_FEE_DESCRIPTION.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="viewfeedescriptions" name="processAction">
	<portlet:param name="action"
		value="<%=FEE_DESCRIPTION_VIEW.VIEW_FEE_DESCRIPTION_LISTINGS.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="mapfeetocompany" name="processAction">
	<portlet:param name="action"
		value="<%=FEE_DESCRIPTION_VIEW.MAP_FEE_TO_COMPANY_VIEW.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="mappedfees" name="processAction">
	<portlet:param name="action"
		value="<%=FEE_DESCRIPTION_VIEW.MAPPED_FEES_TO_COMPANY.name()%>" />
</portlet:actionURL>
<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>



<%
String createFeeDescriptionColor="#000000";	String createFeeDescriptionBgColor="#CCCCCC";	
String viewFeeDescriptionColor="#000000"; String viewFeeDescriptionBgColor="#CCCCCC";	
String mapmandatepanelColor = "#000000"; String mapmandatepanelBgColor="#CCCCCC";	
String mappedmandatepanelColor = "#000000"; String mappedmandatepanelBgColor="#CCCCCC";

String createFeeDescriptionClassType="label-default"; String viewFeeDescriptionClassType="label-default";
String mapFeeToCompanyClassType="label-default"; String mappedfeesToCompanyClassType="label-default";


if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.CREATE_A_FEE_DESCRIPTION))
{
	createFeeDescriptionColor="#ffffff";
	createFeeDescriptionBgColor="#000000";
	mapFeeToCompanyClassType="label-default";
	viewFeeDescriptionClassType="label-default";
	mappedfeesToCompanyClassType="label-default";
	createFeeDescriptionClassType="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_FEE_DESCRIPTION_LISTINGS))
{
	viewFeeDescriptionColor="#ffffff";
	viewFeeDescriptionBgColor="#000000";
	mapFeeToCompanyClassType="label-default";
	viewFeeDescriptionClassType="label-primary";
	createFeeDescriptionClassType="label-default";
	mappedfeesToCompanyClassType="label-default";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.MAP_FEE_TO_COMPANY))
{
	mapmandatepanelColor="#ffffff";
	mapmandatepanelBgColor="#000000";
	mapFeeToCompanyClassType="label-primary";
	viewFeeDescriptionClassType="label-default";
	createFeeDescriptionClassType="label-default";
	mappedfeesToCompanyClassType="label-default";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.MAPPED_FEES_TO_COMPANY))
{
	mappedmandatepanelColor="#ffffff";
	mappedmandatepanelBgColor="#000000";
	mappedfeesToCompanyClassType="label-primary";
	viewFeeDescriptionClassType="label-default";
	createFeeDescriptionClassType="label-default";
	mapFeeToCompanyClassType="label-default";
}else
{
	if(determinAccess!=null && determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS) || determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
	{
		createFeeDescriptionColor="#ffffff";
		createFeeDescriptionBgColor="#000000";
		mapFeeToCompanyClassType="label-default";
		viewFeeDescriptionClassType="label-default";
		mappedfeesToCompanyClassType="label-default";
		createFeeDescriptionClassType="label-primary";
	}else
	{
		viewFeeDescriptionColor="#ffffff";
		viewFeeDescriptionBgColor="#000000";
		mapFeeToCompanyClassType="label-default";
		viewFeeDescriptionClassType="label-primary";
		createFeeDescriptionClassType="label-default";
		mappedfeesToCompanyClassType="label-default";
	}
	
}

%>

<div style="padding-top: 20px">
	<%
	if(determinAccess!=null && determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS) || determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
	{
	%>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=createfeedescription%>">
		<div class="<%=createFeeDescriptionClassType %>" style="padding:5px; padding-left:8px; padding-right:10px; color:<%=createFeeDescriptionColor %>">
		New Transaction Fee</div></a>
	</div>
	<%
	}
	%>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=viewfeedescriptions%>">
		<div class="<%=viewFeeDescriptionClassType %>" style="padding:5px; padding-left:8px; padding-right:10px; color:<%=viewFeeDescriptionColor %>">
		Transaction Fees Listing</div></a>
	</div>
	<%
	if(determinAccess!=null && determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS) || determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
	{
	%>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=mapfeetocompany%>">
		<div class="<%=mapFeeToCompanyClassType %>" style="padding:5px; padding-left:8px; padding-right:10px; color:<%=mapmandatepanelColor %>">
		Map Transaction Fee to A Company</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=mappedfees%>">
		<div class="<%=mappedfeesToCompanyClassType %>" style="padding:5px; padding-left:8px; padding-right:10px; color:<%=mappedmandatepanelColor %>">
		Transaction Fees/Company Mapping</div></a>
	</div>
	<%
	}
	%>
</div>

<div style="clear:both; font-size:20px; height:20px">&nbsp;</div>