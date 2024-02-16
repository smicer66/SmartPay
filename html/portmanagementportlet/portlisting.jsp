<%@page import="com.probase.smartpay.admin.portmanagement.PortManagementPortletState"%>
<%@page import="com.probase.smartpay.admin.portmanagement.PortManagementPortletState.*"%>
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
<%@page import="smartpay.entity.Ports"%>
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

PortManagementPortletState portletState = PortManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(PortManagementPortletState.class);

/*<jsp:include page="/html/annualreturnsportlet/tabs.jsp" flush="" />*/
%>


<jsp:include page="/html/portmanagementportlet/tabs.jsp" flush="" />

<portlet:actionURL var="modifyPort" name="processAction">
	<portlet:param name="action"
		value="<%=PORTS_ACTION.HANDLE_PORT_LISTING_ACTION.name()%>" />
</portlet:actionURL>

<div style="padding:10px; width:900px"> 	
    <div class="panel panel-primary">
		<div class="panel-heading"><span style="color:white; font-weight: bold">List of Fee Descriptions</span></div>
		<div class="panel-body">
			<div style="padding:10px">
				<form  id="portListingForm" action="<%=modifyPort%>" method="post" enctype="application/x-www-form-urlencoded">
					
				<legend>Fee Description Listing</legend>
				<div>
				Click on an EDIT button to update a fee description details
				</div>
				  <%
				  if(portletState.getAllPortsListing()!=null && portletState.getAllPortsListing().size()>0)
				  {
					  %>
					  
					  <table width="100%" class="table" id="btable">
						<thead>
						  <th>Port Name</th>
						  <th>Port Code</th>
						  <th>&nbsp;</th>
						</thead>
					  <%
					  for(Iterator<Ports> iter = portletState.getAllPortsListing().iterator(); iter.hasNext();)
					  {
						  Ports fd = iter.next();
						  
					  %>
					  <tr>
						<td><%=fd.getFullName()==null ? "N/A" : fd.getFullName() %></td>
						<td><%=fd.getPortCode()==null ? "N/A" : fd.getPortCode()%></td>
						<td>
							<div class="btn-group">
							  <button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('update', '<%=fd.getId()%>')">Update Details</button>
							  <button type="button" class="btn btn-danger dropdown-toggle" data-toggle="dropdown">
							    <span class="caret"></span>
							    <span class="sr-only">Toggle Dropdown</span>
							  </button>
							  <ul class="dropdown-menu" role="menu">
							    <li><a href="javascript: handleButtonAction('update', '<%=fd.getId()%>')">Update Details</a></li>
							    <li><a href="javascript: handleButtonAction('delete', '<%=fd.getId()%>')">Delete Port Code</a></li>
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
						  
						  <th>Port Name</th>
						  <th>Port Code</th>
						  <th>&nbsp;</th>
						</thead>
					  <tr>
						<td colspan="3">There are no Fee Descriptions created on the platform yet</td>
					  </tr>
					  <%  
				  }
				  %>
				</table>
				<input type="hidden" name="selectedPort" id="selectedPort" value="" />
				<input type="hidden" name="selectedPortAction" id="selectedPortAction" value="" />	
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
		if(confirm("Are you sure you want to delete this port? You will not be able to recover the port after deleting it!"))
		{
			document.getElementById('selectedPort').value = companyId;
			document.getElementById('selectedPortAction').value = action;
			document.getElementById('portListingForm').submit();
		}
	}else
	{
		document.getElementById('selectedPort').value = companyId;
		document.getElementById('selectedPortAction').value = action;
		document.getElementById('portListingForm').submit();
	}
}
</script>