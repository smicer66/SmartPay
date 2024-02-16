<%@page import="smartpay.entity.enumerations.PaymentBreakDownHistoryConstants"%>
<%@page import="smartpay.entity.PaymentBreakDownHistory"%>
<%@page import="smartpay.entity.enumerations.PaymentHistoryConstants"%>
<%@page import="com.probase.smartpay.admin.settingsmanagement.SettingsManagementPortletState.*"%>
<%@page import="com.probase.smartpay.admin.settingsmanagement.SettingsManagementPortletState"%>
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
<%@page import="com.probase.smartpay.commins.Util"%>
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
	
	String jqueryJsUrl = resourceBaseURL + "/js/jquery-1.9.0.js";
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

SettingsManagementPortletState portletState = SettingsManagementPortletState.getInstance(renderRequest, renderResponse);
SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

%>

<jsp:include page="/html/settingsmanagementportlet/settings/tabs.jsp" flush="" />


<portlet:actionURL var="handlePaymentListings" name="processAction">
	<portlet:param name="action"
		value="<%=SETTINGS.MANAGE_ONE_PAYMENT_BREAKDOWN_HISTORY.name()%>" />
</portlet:actionURL>


<div style="padding:10px;">
	<h2>Payment Breakdown Listings</h2>
	
	<div class="panel panel-info">
	  	<!-- Default panel contents -->
	  	
	  	<div class="panel-heading"><strong>Payments Listings</strong></div>
	  	
	  	<div class="panel-body" style="padding:10px;">
			<form  id="paymentsListingForm" action="<%=handlePaymentListings%>" method="post" enctype="application/x-www-form-urlencoded">
			
			  <%
			  if(portletState.getPaymentBreakdownHistoryListing()!=null && portletState.getPaymentBreakdownHistoryListing().size()>0)
			  {
				  %>
				  
				  <table width="100%" class="table" id="btable">
					<thead>
					  <th>Transaction Ref</th>
					  <th>Tax Type</th>
					  <th>Assess Reg No/Dom Reg No</th>
					  <th>Amount (ZMW) </th>
					  <th>Payment Type </th>
					  <th>&nbsp;</th>
					</thead>
				  <%
				  for(Iterator<PaymentBreakDownHistory> iter = portletState.getPaymentBreakdownHistoryListing().iterator(); iter.hasNext();)
				  {
					  PaymentBreakDownHistory ph = iter.next();
					  String paymenttype = null;
				  %>
				  <tr>
					<td><%=ph.getTransactionNumber()==null ? "N/A" : ph.getTransactionNumber() %></td>
					<td><%=ph.getTaxType()==null ? "N/A" : (ph.getTaxType().getTaxName() + " - " + ph.getTaxType().getTaxCode())%></td>
					<td><%=ph.getPaymentHistory().getAssessment()==null ? 
							(ph.getPaymentHistory().getDomTax()!=null ? ph.getPaymentHistory().getDomTax().getPaymentRegNo() : "N/A") : 
						ph.getPaymentHistory().getAssessment().getRegistrationNumber() %></td>
					<td><%=ph.getAmount()!=null ? new Util().roundUpAmount(ph.getAmount()) : "N/A" %></td>
					<td><%=ph.getPaymentHistory().getPaymentType().getValue() %></td>
					<td>
						<%
						if((ph.getStatus().equals(PaymentBreakDownHistoryConstants.PAYMENT_BREAKDOWN_STATUS_PENDING)) && 
								ph.getPaymentHistory().getStatus().equals(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_ZRA_CONFIRMED))
						{
						%>
						<div class="btn-group">
							  <button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('view', '<%=ph.getId()%>')">Action</button>
							  <button type="button" class="btn btn-danger dropdown-toggle" data-toggle="dropdown">
							    <span class="caret"></span>
							    <span class="sr-only">Toggle Dropdown</span>
							  </button>
							  <ul class="dropdown-menu" role="menu">
							    	<li><a href="javascript: handleButtonAction('splitOnePayment', '<%=ph.getId() %>')">Move From ZRA Suspense Account</a></li>
							  </ul>
						</div>
						<%
						}
						else
						{
							%>
							<div class="btn-group">&nbsp;</div>
							<%	
						}
						%>
						
					</td>
				  </tr>
				  <%
				  }
			  }else
			  {
				  %>
				  <table width="100%" class="table" id="">
					<thead>
					  
					  <thead>
					  <th>Transaction Ref</th>
					  <th>Tax Type</th>
					  <th>Assessment Reg No</th>
					  <th>Amount (ZMW) </th>
					  <th>Payment Type </th>
					</thead>
					</thead>
				  <tr>
					<td colspan="5">There are no payments available to move from ZRA Suspense Account</td>
				  </tr>
				  <%  
			  }
			  %>
			</table>
			<input type="hidden" name="selectedPBDH" id="selectedPBDH" value="" />
			<input type="hidden" name="selectedPBDHAction" id="selectedPBDHAction" value="" />	
			</form>
		</div>
	</div>
</div>

<script type="text/javascript" charset="utf-8" src="<%=jqueryJsUrl%>"></script>
<script type="text/javascript" charset="utf-8" src="<%=jqueryDataTableUrl%>"></script>

<script type="text/javascript">
$(document).ready(function() {
    $('#btable').dataTable();
} );


function handleButtonAction(action, companyId){
	
	
		document.getElementById('selectedPBDH').value = companyId;
		document.getElementById('selectedPBDHAction').value = action;
		document.getElementById('paymentsListingForm').submit();
	
}


function downloadSlip(paymentTxnId, amount, a){

	if(a==1)
	{
	 	document.location.href='<%=response.encodeURL(request.getContextPath()+"/ActiveServlet?action=downloadLumpSlip&assessmentId=")%>'+paymentTxnId +'&amount='+amount;
	}else if(a==2)
	{
		document.location.href='<%=response.encodeURL(request.getContextPath()+"/ActiveServlet?action=downloadDomTaxSlip&domTaxId=")%>'+paymentTxnId +'&amount='+amount;
		
	}

}
</script>