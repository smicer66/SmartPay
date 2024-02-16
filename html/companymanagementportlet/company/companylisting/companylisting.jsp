<%@page import="com.probase.smartpay.admin.companymanagement.CompanyManagementPortletState"%>
<%@page import="com.probase.smartpay.admin.companymanagement.CompanyManagementPortletState.*"%>
<%@page import="com.probase.smartpay.commins.ComminsApplicationState"%>
<%@page import="com.probase.smartpay.commins.Util"%>
<%@page import="com.probase.smartpay.commins.Util.DETERMINE_ACCESS"%>
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
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.enumerations.CompanyStatusConstants"%>
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
<link href="<%=jqueryUICssUrl%>" rel="stylesheet" type="text/css" />
<%

CompanyManagementPortletState portletState = CompanyManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(CompanyManagementPortletState.class);

ComminsApplicationState cappState = portletState.getCas();
Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
DETERMINE_ACCESS determinAccessForCompany = new Util().determineAccessForCompanyFxns(twoStep, approvalProcess, cappState, portletState.getCompanyCRUDRights());

%>

<jsp:include page="/html/companymanagementportlet/company/tabs.jsp" flush="" />


<portlet:actionURL var="modifyCompany" name="processAction">
	<portlet:param name="action"
		value="<%=COMPANY_LISTING.MODIFY_COMPANY.name()%>" />
</portlet:actionURL>

<div style="padding:10px;">
<div class="panel panel-info">
  	<!-- Default panel contents -->
  	<div class="panel-heading"><strong>List of Corporate Firms Registered</strong></div>
  	<div class="panel-body">
		<p>Click on a corporate firm to view its details. Click on an EDIT button to update a corporate firms details</p>
  	</div>
		<form  id="startRegFormId" action="<%=modifyCompany%>" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">
			
		<legend>Corporate Firms Listing</legend>
		  <%
		  if(portletState.getCompanyListing()!=null && portletState.getCompanyListing().size()>0)
		  {
			  %>
			  
			  <table width="100%" class="table table-hover" id="btable">
				<thead>
				  <th>Company Name</th>
				  <th>RC Number</th>
				  <th>Bank Account Number</th>
				  <th>Email Address</th>
				  <th>Mobile Number</th>
				  <th>Status</th>
				  <th>&nbsp;</th>
				
				</thead>
			  <%
			  for(Iterator<Company> iter = portletState.getCompanyListing().iterator(); iter.hasNext();)
			  {
				  Company company = iter.next();
				  if(!company.getStatus().equals(CompanyStatusConstants.COMPANY_STATUS_DELETED))
				  {
			  %>
			  <tr>
				<td><%=company.getCompanyName()==null ? "N/A" : company.getCompanyName() %></td>
				<td><%=company.getCompanyRCNumber()==null ? "N/A" : company.getCompanyRCNumber()%></td>
				<td><%=company.getAccountNumber()==null ? "N/A" : company.getAccountNumber()%></td>
				<td><%=company.getEmailAddress()==null ? "N/A" : company.getEmailAddress()%></td>
				<td><%=company.getMobileNumber()==null ? "N/A" : company.getMobileNumber()%></td>
				<td><%if(company.getStatus().equals(CompanyStatusConstants.COMPANY_STATUS_ACTIVE))
				{
				%>
				Active
				<%
				}else if(company.getStatus().equals(CompanyStatusConstants.COMPANY_STATUS_INACTIVE))
				{
				%>
				Inactive
				<%
				}else if(company.getStatus().equals(CompanyStatusConstants.COMPANY_STATUS_DELETED))
				{
				%>
				Deleted
				<%
				}else if(company.getStatus().equals(CompanyStatusConstants.COMPANY_STATUS_SUSPENDED))
				{
				%>
				Blocked
				<%
				}
				%></td>
				<td>
				
				
				<%
				if(determinAccessForCompany.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
				{
				%>
					<div class="btn-group">
						<%
						if(company.getStatus().equals(CompanyStatusConstants.COMPANY_STATUS_ACTIVE))
						{
						%>
					    <button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('update', '<%=company.getId()%>')">Update Details</button>
						<%
						}else if(!company.getStatus().equals(CompanyStatusConstants.COMPANY_STATUS_DELETED))
						{
						%>
					    <button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('delete', '<%=company.getId()%>')">Delete Company</button>
						<%	
						}
					  	%>
					  <button type="button" class="btn btn-danger dropdown-toggle" data-toggle="dropdown">
					    <span class="caret"></span>
					    <span class="sr-only">Toggle Dropdown</span>
					  </button>
					  <ul class="dropdown-menu" role="menu">
					  	<%
						if(company.getStatus().equals(CompanyStatusConstants.COMPANY_STATUS_ACTIVE))
						{
						%>
					    <li><a href="javascript: handleButtonAction('update', '<%=company.getId()%>')">Update Details</a></li>
						<%
						}if(!company.getStatus().equals(CompanyStatusConstants.COMPANY_STATUS_DELETED))
						{
						%>
					    <li><a href="javascript: handleButtonAction('delete', '<%=company.getId()%>')">Delete Company</a></li>
						<%
						}if(company.getStatus().equals(CompanyStatusConstants.COMPANY_STATUS_ACTIVE))
						{
						%>
					    <li><a href="javascript: handleButtonAction('suspend', '<%=company.getId()%>')">Block Company</a></li>
					    <%
						}if(company.getStatus().equals(CompanyStatusConstants.COMPANY_STATUS_SUSPENDED))
						{
						%>
					    <li><a href="javascript: handleButtonAction('reactivate', '<%=company.getId()%>')">Unblock Company</a></li>
					    <%
						}
						%>
					  </ul>
					</div>
				<%
				}else if(determinAccessForCompany.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
				{
				%>
					<div class="btn-group">
					  <button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('update', '<%=company.getId()%>')">Update Details</button>
					  <button type="button" class="btn btn-danger dropdown-toggle" data-toggle="dropdown">
					    <span class="caret"></span>
					    <span class="sr-only">Toggle Dropdown</span>
					  </button>
					  <ul class="dropdown-menu" role="menu">
					  	<%
						if(company.getStatus().equals(CompanyStatusConstants.COMPANY_STATUS_ACTIVE))
						{
						%>
					    <li><a href="javascript: handleButtonAction('update', '<%=company.getId()%>')">Update Details</a></li>
						<%
						}if(!company.getStatus().equals(CompanyStatusConstants.COMPANY_STATUS_DELETED))
						{
						%>
					    <li><a href="javascript: handleButtonAction('delete', '<%=company.getId()%>')">Delete Company</a></li>
						<%
						}if(company.getStatus().equals(CompanyStatusConstants.COMPANY_STATUS_ACTIVE))
						{
						%>
					    <li><a href="javascript: handleButtonAction('suspend', '<%=company.getId()%>')">Block Company</a></li>
					    <%
						}if(company.getStatus().equals(CompanyStatusConstants.COMPANY_STATUS_SUSPENDED))
						{
						%>
					    <li><a href="javascript: handleButtonAction('reactivate', '<%=company.getId()%>')">Unblock Company</a></li>
					    <%
						}
						if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
						{
							if(company.getStatus().equals(CompanyStatusConstants.COMPANY_STATUS_INACTIVE))
							{
							%>
								<li><a href="javascript: handleButtonAction('approveActivate', '<%=company.getId()%>')">Approve & Activate Company</a></li>
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
			  }
		  }else
		  {
			  %>
			  <table width="100%" class="table table-hover" id="btable">
				<thead>
				  <th>Company Name</th>
				  <th>RC Number</th>
				  <th>Bank Account Number</th>
				  <th>Email Address</th>
				  <th>Mobile Number</th>
				  <th>&nbsp;</th>
				</thead>
			  <tr>
				<td colspan="6">There are no Companies created on the platform yet</td>
			  </tr>
			  <%  
		  }
		  %>
		</table>
		<input type="hidden" name="selectedCompany" id="selectedCompany" value="" />
		<input type="hidden" name="selectedCompanyAction" id="selectedCompanyAction" value="" />	
	</form>
</div>
</div>



<script type="text/javascript" charset="utf-8" src="<%=jqueryJsUrl%>"></script>
<script type="text/javascript" charset="utf-8" src="<%=jqueryDataTableUrl%>"></script>

<script type="text/javascript">
$(document).ready(function() {
    $('#btable').dataTable();
} );


function handleButtonAction(action, companyId){
	
	if(action=='delete')
	{
		if(confirm("Are you sure you want to delete this company? You will not be able to recover the company after deleting it!"))
		{
			document.getElementById('selectedCompany').value = companyId;
			document.getElementById('selectedCompanyAction').value = action;
			document.getElementById('startRegFormId').submit();
		}
	}else
	{
		document.getElementById('selectedCompany').value = companyId;
		document.getElementById('selectedCompanyAction').value = action;
		document.getElementById('startRegFormId').submit();
	}
}
</script>