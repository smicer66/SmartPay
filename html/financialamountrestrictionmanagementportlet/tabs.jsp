<%@page import="com.probase.smartpay.admin.financialamountrestrictionmanagement.FinancialAmountRestrictionManagementPortletUtil"%>
<%@page	import="com.probase.smartpay.admin.financialamountrestrictionmanagement.FinancialAmountRestrictionManagementPortletState.*"%>
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
<%@page import="com.probase.smartpay.admin.financialamountrestrictionmanagement.FinancialAmountRestrictionManagementPortletState"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />

<%

FinancialAmountRestrictionManagementPortletState portletState = FinancialAmountRestrictionManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(FinancialAmountRestrictionManagementPortletState.class);

%>
<portlet:actionURL var="createBankBranch" name="processAction">
	<portlet:param name="action"
		value="<%=FINANCIAL_AMOUNT_RESTRICTION_VIEW.CREATE_A_FINANCIAL_AMOUNT_RESTRICTION_VIEW.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="viewBankBranch" name="processAction">
	<portlet:param name="action"
		value="<%=FINANCIAL_AMOUNT_RESTRICTION_VIEW.FINANCIAL_AMOUNT_RESTRICTION_LISTINGS_VIEW.name()%>" />
</portlet:actionURL>
<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>



<%
String createBankBranchColor="#000000";	String createBankBranchBgColor="#CCCCCC";	
String viewBankBranchColor="#000000"; String viewBankBranchBgColor="#CCCCCC";	


if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.CREATE_FINANCIAL_AMOUNT_RESTRICTION))
{
	createBankBranchColor="#ffffff";
	createBankBranchBgColor="#000000";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.FINANCIAL_AMOUNT_RESTRICTION_LISTING))
{
	viewBankBranchColor="#ffffff";
	viewBankBranchBgColor="#000000";
}else
{
	createBankBranchColor="#ffffff";
	createBankBranchBgColor="#000000";
}

%>

<div style="padding:10px">
	<div style="padding-top: 20px">
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=createBankBranch%>">
			<div style="padding:5px; padding-left:8px; padding-right:10px; 
			background-color:<%=createBankBranchBgColor %>; color:<%=createBankBranchColor %>">
			Create A Financial Amount Restriction</div></a>
		</div>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=viewBankBranch%>">
			<div style="padding:5px; padding-left:8px; padding-right:10px;  
			background-color:<%=viewBankBranchBgColor %>; color:<%=viewBankBranchColor %>">
			View Financial Amount Restrictions</div></a>
		</div>
	</div>
</div>
<div style="clear:both; font-size:20px; height:20px">&nbsp;</div>