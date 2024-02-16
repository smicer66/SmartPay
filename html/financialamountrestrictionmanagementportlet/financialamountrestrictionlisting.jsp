<%@page import="com.probase.smartpay.admin.financialamountrestrictionmanagement.FinancialAmountRestrictionManagementPortletState"%>
<%@page import="com.probase.smartpay.admin.financialamountrestrictionmanagement.FinancialAmountRestrictionManagementPortletState.*"%>
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
<%@page import="smartpay.entity.FinancialAmountRestriction"%>
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

FinancialAmountRestrictionManagementPortletState portletState = FinancialAmountRestrictionManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(FinancialAmountRestrictionManagementPortletState.class);
Collection<FinancialAmountRestriction> financialAmountListing = portletState.getAllFinancialRestrictionsListing();


%>

<jsp:include page="/html/financialamountrestrictionmanagementportlet/tabs.jsp" flush="" />


<portlet:actionURL var="modifyFinancialAmountRestriction" name="processAction">
	<portlet:param name="action"
		value="<%=FINANCIAL_AMOUNT_RESTRICTION.FINANCIAL_AMOUNT_RESTRICTION_LISTINGS.name()%>" />
</portlet:actionURL>

<div style="padding:10px;"> 	
    <div class="panel panel-primary">
		<div class="panel-heading"><span style="color:white; font-weight: bold">Update Financial Restriction</span></div>
		<div class="panel-body">
			<form  id="financialAmountRestrictionForm" action="<%=modifyFinancialAmountRestriction%>" method="post" enctype="application/x-www-form-urlencoded">
			<div style="padding:10px;">
			<div>
			Click on an EDIT button to update a financial restriction
			</div>
			  <%
			  if(portletState.getAllFinancialRestrictionsListing()!=null && portletState.getAllFinancialRestrictionsListing().size()>0)
			  {
				  %>
				  
				  <table width="100%" class="table" id="btable">
					<thead>
					  <th>Financial Amount</th>
					  <th>Owning Company</th>
					  <th>Date Created</th>
					  <th>&nbsp;</th>
					</thead>
				  <%
				  for(Iterator<FinancialAmountRestriction> iter = portletState.getAllFinancialRestrictionsListing().iterator(); iter.hasNext();)
				  {
					  FinancialAmountRestriction financialAmountRestriction = iter.next();
					  
				  %>
				  <tr>
					<td><%=financialAmountRestriction.getName()==null ? "N/A" : financialAmountRestriction.getName() %></td>
					<td><%=financialAmountRestriction.getCompany()==null ? "N/A" : financialAmountRestriction.getCompany().getCompanyName() %></td>
					<td><%=financialAmountRestriction.getDateCreated()==null ? "N/A" : financialAmountRestriction.getDateCreated()%></td>
					<td>
						<div class="btn-group">
							  <button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('delete', '<%=financialAmountRestriction.getId()%>')">Delete Financial Amount Restriction</button>
							  <button type="button" class="btn btn-danger dropdown-toggle" data-toggle="dropdown">
							    <span class="caret"></span>
							    <span class="sr-only">Toggle Dropdown</span>
							  </button>
							  <ul class="dropdown-menu" role="menu">
							    <li><a href="javascript: handleButtonAction('delete', '<%=financialAmountRestriction.getId()%>')">Delete Financial Amount Restriction</a></li>
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
					  
					  <th>Financial Amount</th>
					  <th>Date Created</th>
					</thead>
				  <tr>
					<td colspan="2">There are no Financial Amount Restrictions created on the platform yet</td>
				  </tr>
				  <%  
			  }
			  %>
			</table>
			<button float="left" class="btn btn-danger" onclick="javascript:handleButtonAction('goback', '')">Go Back To Change Selected Company</button>
			<input type="hidden" name="selectedFAR" id="selectedFAR" value="" />
			<input type="hidden" name="selectedFARAction" id="selectedFARAction" value="" />	
			</div>
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
		if(confirm("Are you sure you want to delete this Financial Amount Restriction? You will not be able to recover the Financial Amount Restriction after deleting it!"))
		{
			document.getElementById('selectedFAR').value = companyId;
			document.getElementById('selectedFARAction').value = action;
			document.getElementById('financialAmountRestrictionForm').submit();
		}
	}else
	{
		document.getElementById('selectedFAR').value = companyId;
		document.getElementById('selectedFARAction').value = action;
		document.getElementById('financialAmountRestrictionForm').submit();
	}
}
</script>