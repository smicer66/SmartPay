<%@page import="smartpay.entity.enumerations.PaymentHistoryConstants"%>
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

PaymentsPortletState portletState = PaymentsPortletState.getInstance(renderRequest, renderResponse);
Collection<PaymentHistory> bankbranches = portletState.getAllPaymentsHistoryListing();
SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

%>

<jsp:include page="/html/paymentsportlet/tabs.jsp" flush="" />


<portlet:actionURL var="handlePaymentListings" name="processAction">
	<portlet:param name="action"
		value="<%=PAYMENTS_ACTION.HANDLE_PAYMENT_LISTING.name()%>" />
</portlet:actionURL>


<div style="padding:10px;">
	<h2>Payment Listings</h2>
	
	<div class="panel panel-info">
	  	<!-- Default panel contents -->
	  	<%
	  	if(portletState.getPortalUser().getCompany()!=null)
	  	{
	  	%>
	  	<div class="panel-heading"><strong>My Payments Listings</strong></div>
	  	<%
	  	}else
	  	{
	  	%>
	  	<div class="panel-heading"><strong>Payments Listings</strong></div>
	  	<%
	  	}
	  	%>
	  	<div class="panel-body">
	    	<p>Click on a button to view a payments detail</p>
	  	</div>
	  	<div style="padding:10px;">
			<form  id="paymentsListingForm" action="<%=handlePaymentListings%>" method="post" enctype="application/x-www-form-urlencoded">
			
			  <%
			  if(portletState.getAllPaymentsHistoryListing()!=null && portletState.getAllPaymentsHistoryListing().size()>0)
			  {
				  %>
				  
				  <table width="100%" class="table" id="btable">
					<thead>
					  <th>Payment Date</th>
					  <th>Transaction Ref ID</th>
					  <th>Transaction Serial No</th>
					  <th>Company Name</th>
					  <th>Assessment Reg No</th>
					  <th>Payment Type</th>
					  <th>Amount (ZMW) </th>
					  <th>&nbsp;</th>
					</thead>
				  <%
				  for(Iterator<PaymentHistory> iter = portletState.getAllPaymentsHistoryListing().iterator(); iter.hasNext();)
				  {
					  PaymentHistory ph = iter.next();
					  String paymenttype = null;
					  if(ph.getPaymentType().equals(PaymentTypeConstants.PAYMENTTYPE_INTEREST_PAYMENT))
							  paymenttype = "Interest Payment";

					  if(ph.getPaymentType().equals(PaymentTypeConstants.PAYMENTTYPE_TAXFEE_PAYMENT))
							  paymenttype = "Assessment Fee";

					  if(ph.getPaymentType().equals(PaymentTypeConstants.PAYMENTTYPE_TAXFEE_PLUS_INTEREST_PAYMENT))
							  paymenttype = "Transaction Fee";

					  if(ph.getPaymentType().equals(PaymentTypeConstants.PAYMENTTYPE_DOM))
							  paymenttype = "Domestic Tax Fee";
				  %>
				  <tr>
					<td><%=ph.getEntryDate()==null ? "N/A" : sdf.format(ph.getEntryDate()) %></td>
					<td><%=ph.getTransactionReferenceId()==null ? "N/A" : ph.getTransactionReferenceId()%></td>
					<td><%=ph.getProbaseTransactionSerialNo()==null ? "N/A" : ph.getProbaseTransactionSerialNo() %></td>
					<td><%
					String companyName = "";
					if(ph.getAssessment()!=null)
					{
						if(ph.getAssessment().getTpinInfo().getCompany()==null)
						{
							companyName = "N/A"; 
						}else
						{
							companyName = ph.getAssessment().getTpinInfo().getCompany().getCompanyName();
						}
					}else if(ph.getDomTax()!=null)
					{
						if(ph.getDomTax().getTpinInfo().getCompany()==null)
						{
							companyName = "N/A"; 
						}else
						{
							companyName = ph.getDomTax().getTpinInfo().getCompany().getCompanyName();
						}
					}
					%>
					<%=companyName %>
					</td>
					<td>
					<%
					String regNo = "";
					if(ph.getAssessment()!=null)
					{
						if(ph.getAssessment().getRegistrationNumber()==null)
						{
							regNo = "N/A"; 
						}else
						{
							regNo = ph.getAssessment().getRegistrationNumber();
						}
					}else if(ph.getDomTax()!=null)
					{
						if(ph.getDomTax().getPaymentRegNo()==null)
						{
							regNo = "N/A"; 
						}else
						{
							regNo = ph.getDomTax().getPaymentRegNo();
						}
					}
					%>
					<%=regNo %></td>
					<td><%=paymenttype %></td>
					<td style="text-align: right"><%=new Util().roundUpAmount(ph.getPayableAmount())%></td>
					<td>
						<%
						if((ph.getStatus().equals(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_ZRA_CONFIRMED) && ph.getAssessment()!=null) || 
								(ph.getStatus().equals(PaymentHistoryConstants.PAYMENTHISTORY_STATUS_APPROVED) && ph.getDomTax()!=null))
						{
						%>
						<div class="btn-group">
							  <button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('view', '<%=ph.getId()%>')">View Details</button>
							  <button type="button" class="btn btn-danger dropdown-toggle" data-toggle="dropdown">
							    <span class="caret"></span>
							    <span class="sr-only">Toggle Dropdown</span>
							  </button>
							  <ul class="dropdown-menu" role="menu">
							    <li><a href="javascript: handleButtonAction('view', '<%=ph.getId()%>')">View Details</a></li>
							    <%
							    if(portletState.getPortalUser().getCompany()!=null && ph.getTransactionReferenceId()!=null)
							    {
							    	if(ph.getDomTax()==null)
							    	{
							    %>
							    	<li><a href="javascript: downloadSlip('<%=ph.getAssessment().getId() %>', '<%=ph.getPayableAmount() %>', 1)">Print Receipt [PDF]</a></li>
							    <%
							    	}else
							    	{
						    	%>
							    	<li><a href="javascript: downloadSlip('<%=ph.getRequestMessageId() %>', '<%=ph.getPayableAmount() %>', 2)">Print Receipt [PDF]</a></li>
							    <%		
							    	}
							    }
							    %>
							  </ul>
						</div>
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
					  <th>Payment Date</th>
					  <th>Transaction Ref ID</th>
					  <th>Transaction Serial No</th>
					  <th>Company Name</th>
					  <th>Assessment Reg No</th>
					  <th>Payment Type</th>
					  <th>Amount Paid</th>
					</thead>
					</thead>
				  <tr>
					<td colspan="6">There are no payments made on the platform yet</td>
				  </tr>
				  <%  
			  }
			  %>
			</table>
			<input type="hidden" name="selectedPaymentHistory" id="selectedPaymentHistory" value="" />
			<input type="hidden" name="selectedPaymentHistoryAction" id="selectedPaymentHistoryAction" value="" />	
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
	
	
		document.getElementById('selectedPaymentHistory').value = companyId;
		document.getElementById('selectedPaymentHistoryAction').value = action;
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