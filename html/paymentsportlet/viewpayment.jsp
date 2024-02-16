<%@page import="com.probase.smartpay.admin.payments.PaymentsPortletState"%>
<%@page import="com.probase.smartpay.admin.payments.PaymentsPortletState.*"%>
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
<%@page import="smartpay.entity.PaymentHistory"%>
<%@page import="smartpay.entity.enumerations.PaymentTypeConstants"%>
<%@page import="smartpay.entity.Company"%>
<%@page import="smartpay.entity.AuthorizePanelCombination"%>
<%@page import="java.text.DateFormat"%>
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

PaymentsPortletState portletState = PaymentsPortletState.getInstance(renderRequest, renderResponse);
Collection<PaymentHistory> bankbranches = portletState.getAllPaymentsHistoryListing();
SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
PaymentHistory ph = portletState.getSelectedPaymentHistory();


%>

<jsp:include page="/html/paymentsportlet/tabs.jsp" flush="" />


<portlet:actionURL var="handleSingleViewActions" name="processAction">
	<portlet:param name="action"
		value="<%=PAYMENTS_ACTION.HANDLE_SINGLE_VIEW_ACTIONS.name()%>" />
</portlet:actionURL>


<form action="<%=handleSingleViewActions %>" method="post" name="billForm" id="billForm">	
	<div class="secondarymain">    
		<div class="panel panel-info">
			<!-- Default panel contents -->
			<div class="panel-heading"><strong>Payment Receipt</strong></div>
			<table width="100%" id="">	
			  <tbody>
					<tr>
						<td style="background-color:#F0F0F0">Tax Payer Identification Number</td>
						<td><%=ph.getAssessment().getTpinInfo().getTpin()%></td>
					</tr>	
					<tr>
						<td style="background-color:#F0F0F0">Company Name</td>
						<td><%=ph.getAssessment().getTpinInfo().getCompany().getCompanyName()%></td>
					</tr>	
			  </tbody>
			</table>
			
			
			<table width="100%" id="btable2">				  
				<thead>
	  		  <th>S/N</th>
					<th>Txn Ref No</th>
					<th>Assessment Reg No</th>
					<th>Amount(ZMW)</th>
				</thead>
			  <tbody>
					<tr>
						<td>1</td>
						<td><%=ph.getTransactionReferenceId()%></td>
						<td><%=ph.getAssessment().getRegistrationNumber()%></td>
						<td><%=ph.getPayableAmount()%></td>
					</tr>
			  </tbody>
			</table>
			
			<table width="100%" id="">	
			  <tbody>
					<tr>
						<td style="background-color:#F0F0F0;font-size:14px; padding:5px">Transaction Date</td>
						<td style="font-size:14px; padding:5px"><%=ph.getEntryDate()%></td>
					</tr>	
					<tr>
						<td style="background-color:#F0F0F0;font-size:14px; padding:5px">Account Number</td>
						<td style="font-size:14px; padding:5px"><%=ph.getAssessment().getTpinInfo().getCompany().getAccountNumber()%></td>
					</tr>
					<tr>
						<td style="background-color:#F0F0F0;font-size:14px; padding:5px">Transaction Branch</td>
						<td style="font-size:14px; padding:5px"><%=ph.getAssessment().getTpinInfo().getCompany().getBankBranches().getName()%></td>
					</tr>	
					<tr>
						<td style="background-color:#F0F0F0;font-size:14px;">Payee Name</td>
						<td style="font-size:14px; padding:5px"><%=ph.getPortalUser().getFirstName() + " " + ph.getPortalUser().getLastName()%></td>
					</tr>
					<tr>
						<td style="background-color:#F0F0F0;font-size:14px; padding:5px">Payment Description</td>
						<td style="font-size:14px; padding:5px"><%=ph.getPaymentType().getValue()%></td>
					</tr>	
					<tr>
						<td style="background-color:#666666; color: #ffffff; font-weight:bold;font-size:14px; padding:5px">Total Amount Paid</td>
						<td style="background-color:#666666; color: #ffffff; font-weight:bold;font-size:14px; padding:5px"><%=ph.getPayableAmount()%></td>
					</tr>	
			  </tbody>
			</table>
		</div>
	</div>
	
	<div>
		<button class="btn btn-danger" name="backButton" id="backButton" style="float:left">Back</button>
	</div>
</form>


<script type="text/javascript" charset="utf-8" src="<%=jqueryDataTableUrl%>"></script>

<script type="text/javascript">
$(document).ready(function() {
    $('#btable2').dataTable();
} );


</script>