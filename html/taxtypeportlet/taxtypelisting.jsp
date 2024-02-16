<%@page import="smartpay.entity.enumerations.SmartPayConstants"%>
<%@page import="com.probase.smartpay.admin.taxtype.TaxTypePortletState"%>
<%@page import="com.probase.smartpay.admin.taxtype.TaxTypePortletState.*"%>
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
<%@page import="smartpay.entity.TaxType"%>
<%@page import="smartpay.entity.TaxTypeAccount"%>
<%@page import="com.probase.smartpay.commins.ComminsApplicationState"%>
<%@page import="com.probase.smartpay.commins.ComminsApplicationState"%>
<%@page import="smartpay.entity.Ports"%>
<%@page import="smartpay.entity.AuthorizePanelCombination"%>
<%@page import="java.text.DateFormat"%>
<%@page import="com.probase.smartpay.commins.Util.DETERMINE_ACCESS"%>
<%@page import="com.probase.smartpay.commins.Util"%>
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

TaxTypePortletState portletState = TaxTypePortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(TaxTypePortletState.class);
boolean proceed = false;
ComminsApplicationState cappState = portletState.getCas();
Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
DETERMINE_ACCESS determinAccess = new Util().determineAccessForCompanyFxns(twoStep, approvalProcess, 
		cappState, portletState.getCompanyCRUDRights());


/*<jsp:include page="/html/annualreturnsportlet/tabs.jsp" flush="" />*/
%>


<jsp:include page="/html/taxtypeportlet/tabs.jsp" flush="" />

<portlet:actionURL var="modifyPort" name="processAction">
	<portlet:param name="action"
		value="<%=TAXTYPE_ACTION.HANDLE_TAXTYPE_LISTING_ACTION.name()%>" />
</portlet:actionURL>

<div style="padding:10px;"> 	
    <div class="panel panel-primary">
		<div class="panel-heading"><span style="color:white; font-weight: bold">List of Tax Types</span></div>
		<div class="panel-body">
			<div style="padding:10px">
				<form  id="portListingForm" action="<%=modifyPort%>" method="post" enctype="application/x-www-form-urlencoded">
					
				<legend>Tax Type Listing</legend>
				<div>
				Click on a button to manage a Tax Type
				</div>
				  <%
				  if(portletState.getAllTaxTypeListing()!=null && portletState.getAllTaxTypeListing().size()>0)
				  {
					  %>
					  
					  <table width="100%" class="table" id="btable">
						<thead>
						  <th>Tax Type Name</th>
						  <th>Tax Type Code</th>
						  <th>Tax Type Account Number</th>
						  <th>Tax Type Account Sort Code</th>
						  <th>Status</th>
						  <%
							if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
							{
							%>
						  <th>&nbsp;</th>
						  <%
							}
						  %>
						</thead>
					  <%
					  for(Iterator<TaxType> iter = portletState.getAllTaxTypeListing().iterator(); iter.hasNext();)
					  {
						  TaxType fd = iter.next();
						  TaxTypeAccount tta = portletState.getTaxTypePortletUtil().getCurrentTaxTypeAccountByTaxTypeId(fd.getId());
						  
					  %>
					  <tr>
						<td><%=fd.getTaxName()==null ? "N/A" : fd.getTaxName() %></td>
						<td><%=fd.getTaxCode()==null ? "N/A" : fd.getTaxCode()%></td>
						<td><%=tta!=null && tta.getAccountNumber()!=null ? tta.getAccountNumber() : "N/A" %></td>
						<td><%=tta!=null && tta.getAccountSortCode()!=null ? tta.getAccountSortCode() : "N/A" %></td>
						<td><%=fd.getStatus()!=null && fd.getStatus().equals(SmartPayConstants.STATUS_ACTIVE) ? "Active" : "Inactive" %></td>
						<%
						if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
						{
						%>
						<td>
							<div class="btn-group">
							  <button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('update', '<%=fd.getId()%>')">Update Details</button>
							  <button type="button" class="btn btn-danger dropdown-toggle" data-toggle="dropdown">
							    <span class="caret"></span>
							    <span class="sr-only">Toggle Dropdown</span>
							  </button>
							  <ul class="dropdown-menu" role="menu">
							    <li><a href="javascript: handleButtonAction('suspend', '<%=fd.getId()%>')">Deactivate Tax Type</a></li>
							    <li><a href="javascript: handleButtonAction('activate', '<%=fd.getId()%>')">Activate Tax Type</a></li>
							  </ul>
							</div>
						</td>
						<%
						}
						%>
					  </tr>
					  <%
					  }
				  }else
				  {
					  %>
					  <table width="100%" class="table" id="">
						<thead>
						  
						  <th>Tax Type Name</th>
						  <th>Tax Type Code</th>
						  <th>Tax Type Account Number</th>
						  <th>Tax Type Account Sort Code</th>
						  <th>&nbsp;</th>
						</thead>
					  <tr>
						<td colspan="4">There are no Tax Types created on the platform yet</td>
					  </tr>
					  <%  
				  }
				  %>
				</table>
				<input type="hidden" name="selectedTaxType" id="selectedTaxType" value="" />
				<input type="hidden" name="selectedTaxTypeAction" id="selectedTaxTypeAction" value="" />	
				</form>
			</div>
		</div>
  	</div>
</div>


<script type="text/javascript" charset="utf-8" src="<%=jqueryDataTableUrl%>"></script>

<script type="text/javascript">
$(document).ready(function() {
    $('#btable').dataTable();
} );


function handleButtonAction(action, companyId){
	
	if(action=='delete')
	{
		if(confirm("Are you sure you want to delete this tax type? You will not be able to recover the tax type after deleting it!"))
		{
			document.getElementById('selectedTaxType').value = companyId;
			document.getElementById('selectedTaxTypeAction').value = action;
			document.getElementById('portListingForm').submit();
		}
	}else
	{
		document.getElementById('selectedTaxType').value = companyId;
		document.getElementById('selectedTaxTypeAction').value = action;
		document.getElementById('portListingForm').submit();
	}
}
</script>