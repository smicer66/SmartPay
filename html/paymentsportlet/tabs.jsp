<%@page import="com.probase.smartpay.admin.payments.PaymentsPortletUtil"%>
<%@page	import="com.probase.smartpay.admin.payments.PaymentsPortletState.*"%>
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
<%@page import="com.probase.smartpay.admin.payments.PaymentsPortletState"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />

<%

PaymentsPortletState portletState = PaymentsPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(PaymentsPortletState.class);

%>
<portlet:actionURL var="zraapprove" name="processAction">
	<portlet:param name="action"
		value="<%=PAYMENTS_VIEW.VIEW_ZRA_SUCCESSFUL_PAYMENT_LISTING.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="zradecline" name="processAction">
	<portlet:param name="action"
		value="<%=PAYMENTS_VIEW.VIEW_ZRA_DECLINED_PAYMENT_LISTING.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="bankapprove" name="processAction">
	<portlet:param name="action"
		value="<%=PAYMENTS_VIEW.VIEW_BANK_APPROVED_PAYMENT_LISTING.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="bankdecline" name="processAction">
	<portlet:param name="action"
		value="<%=PAYMENTS_VIEW.VIEW_BANL_DECLINED_PAYMENT_LISTING.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="bankreverse" name="processAction">
	<portlet:param name="action"
		value="<%=PAYMENTS_VIEW.VIEW_REVERSED_PAYMENT_LISTING.name()%>" />
</portlet:actionURL>
<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>



<%
String zraapproveColor="#000000";	String zraapproveBgColor="#CCCCCC";	
String zradeclineBranchColor="#000000"; String zradeclineBgColor="#CCCCCC";	
String bankapproveColor="#000000";	String bankapproveBgColor="#CCCCCC";	
String bankdeclineBranchColor="#000000"; String bankdeclineBgColor="#CCCCCC";
String bankreverseColor="#000000";	String bankreverseBgColor="#CCCCCC";	



if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_ZRA_SUCCESSFUL_PAYMENT_LISTING_VIEW))
{
	zraapproveColor="#ffffff";
	zraapproveBgColor="#000000";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_ZRA_DECLINED_PAYMENT_LISTING_VIEW))
{
	zradeclineBranchColor="#ffffff";
	zradeclineBgColor="#000000";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_BANK_APPROVED_PAYMENT_LISTING_VIEW))
{
	bankapproveColor="#ffffff";
	bankapproveBgColor="#000000";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_BANL_DECLINED_PAYMENT_LISTING_VIEW))
{
	bankdeclineBranchColor="#ffffff";
	bankdeclineBgColor="#000000";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_REVERSED_PAYMENT_LISTING_VIEW))
{
	bankreverseColor="#ffffff";
	bankreverseBgColor="#000000";
}else
{
	zraapproveColor="#ffffff";
	zraapproveBgColor="#000000";
}

%>

<div style="padding:10px;">
	<div style="padding-top: 20px">
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=zraapprove%>" title="Payments approved by ZRA - Transactions Completed!">
			<div style="padding:5px; padding-left:8px; padding-right:10px; 
			background-color:<%=zraapproveBgColor %>; color:<%=zraapproveColor %>">
			Payments Approved By ZRA</div></a>
		</div>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=zradecline%>" title="Payments not approved by ZRA - Transactions Not Complete!">
			<div style="padding:5px; padding-left:8px; padding-right:10px;  
			background-color:<%=zradeclineBgColor %>; color:<%=zradeclineBranchColor %>">
			Payments Declined by ZRA</div></a>
		</div>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=bankapprove%>" title="Payments approved by Bank - Transactions Yet To Be Approved by ZRA!">
			<div style="padding:5px; padding-left:8px; padding-right:10px;  
			background-color:<%=bankapproveBgColor %>; color:<%=bankapproveColor %>">
			Payments Approved by Bank</div></a>
		</div>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=bankdecline%>" title="Payments not approved by Bank - Transactions Failed!">
			<div style="padding:5px; padding-left:8px; padding-right:10px;  
			background-color:<%=bankdeclineBgColor %>; color:<%=bankdeclineBranchColor %>">
			Payments Declined by Bank</div></a>
		</div>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=bankreverse%>" title="Payments reversed by Bank - Payment Reversal!">
			<div style="padding:5px; padding-left:8px; padding-right:10px;  
			background-color:<%=bankreverseBgColor %>; color:<%=bankreverseColor %>">
			Payments Reversed by Bank</div></a>
		</div>
	</div>
</div>

<div style="clear:both; font-size:20px; height:20px">&nbsp;</div>