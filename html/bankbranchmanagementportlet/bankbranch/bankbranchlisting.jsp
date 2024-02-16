<%@page import="com.probase.smartpay.admin.bankbranchmanagement.BankBranchManagementPortletState"%>
<%@page import="com.probase.smartpay.admin.bankbranchmanagement.BankBranchManagementPortletState.*"%>
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

BankBranchManagementPortletState portletState = BankBranchManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(BankBranchManagementPortletState.class);
Collection<BankBranches> bankbranches = portletState.getAllBankBranchListing();




%>

<jsp:include page="/html/bankbranchmanagementportlet/bankbranch/tabs.jsp" flush="" />


<portlet:actionURL var="modifyBankbranch" name="processAction">
	<portlet:param name="action"
		value="<%=BANK_BRANCH_ACTION.BANK_BRANCH_LISTING_ACTION.name()%>" />
</portlet:actionURL>


<div style="padding:10px;">
	<h2>Existing Bank Branches</h2>
	
	<div class="panel panel-info">
	  	<!-- Default panel contents -->
	  	<div class="panel-heading">Bank Branch Listings</div>
	  	<div class="panel-body">
	    	<p>Click on an EDIT button to update a bank branch details</p>
	  	</div>
	  	<div style="padding:10px;">
			<form  id="bankBranchListingForm" action="<%=modifyBankbranch%>" method="post" enctype="application/x-www-form-urlencoded">
			
			  <%
			  if(portletState.getAllBankBranchListing()!=null && portletState.getAllBankBranchListing().size()>0)
			  {
				  %>
				  
				  <table width="100%" class="table" id="btable">
					<thead>
					  <th>Bank Branch</th>
					  <th>Bank Code</th>
					  <th>&nbsp;</th>
					</thead>
				  <%
				  for(Iterator<BankBranches> iter = portletState.getAllBankBranchListing().iterator(); iter.hasNext();)
				  {
					  BankBranches bankBranch = iter.next();
					  
				  %>
				  <tr>
					<td><%=bankBranch.getName()==null ? "N/A" : bankBranch.getName() %></td>
					<td><%=bankBranch.getBankCode()==null ? "N/A" : bankBranch.getBankCode()%></td>
					<td>
						
						<div class="btn-group">
							  <button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('update', '<%=bankBranch.getId()%>')">Update Details</button>
							  <button type="button" class="btn btn-danger dropdown-toggle" data-toggle="dropdown">
							    <span class="caret"></span>
							    <span class="sr-only">Toggle Dropdown</span>
							  </button>
							  <ul class="dropdown-menu" role="menu">
							    <li><a href="javascript: handleButtonAction('update', '<%=bankBranch.getId()%>')">Update Details</a></li>
							    <li><a href="javascript: handleButtonAction('delete', '<%=bankBranch.getId()%>')">Delete Bank Branch</a></li>
							    <li><a href="javascript: handleButtonAction('suspend', '<%=bankBranch.getId()%>')">Suspend Bank Branch</a></li>
							  </ul>
						</div>
					</td>
				  </tr>
				  <%
				  }
			  }else
			  {
				  %>
				  <table width="100%" class="table" id="">
					<thead>
					  
					  <th>Bank Branch</th>
					  <th>Bank Code</th>
					</thead>
				  <tr>
					<td colspan="2">There are no Bank Branches created on the platform yet</td>
				  </tr>
				  <%  
			  }
			  %>
			</table>
			<input type="hidden" name="selectedBankBranch" id="selectedBankBranch" value="" />
			<input type="hidden" name="selectedBankBranchAction" id="selectedBankBranchAction" value="" />	
			</form>
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
		if(confirm("Are you sure you want to delete this bank branch? You will not be able to recover the bank branch after deleting it!"))
		{
			document.getElementById('selectedBankBranch').value = companyId;
			document.getElementById('selectedBankBranchAction').value = action;
			document.getElementById('bankBranchListingForm').submit();
		}
	}else
	{
		document.getElementById('selectedBankBranch').value = companyId;
		document.getElementById('selectedBankBranchAction').value = action;
		document.getElementById('bankBranchListingForm').submit();
	}
}
</script>