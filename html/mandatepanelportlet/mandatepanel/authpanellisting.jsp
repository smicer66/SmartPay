<%@page import="com.probase.smartpay.admin.mandatepanelmanagement.MandatePanelPortletState"%>
<%@page import="com.probase.smartpay.admin.mandatepanelmanagement.MandatePanelPortletState.*"%>
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
<%@page import="smartpay.entity.AuthorizePanel"%>
<%@page import="smartpay.entity.AuthorizePanelCombination"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.ParseException"%>
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

MandatePanelPortletState portletState = MandatePanelPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(MandatePanelPortletState.class);
Collection<BankBranches> bankbranches = portletState.getAllBankBranchListing();
Company company = portletState.getMandatePanelPortletUtil().getCompanyById(Long.valueOf(portletState.getSelectedCompanyId()));

%>

<jsp:include page="/html/mandatepanelportlet/mandatepanel/tabs.jsp" flush="" />


<portlet:actionURL var="authPanelListingUrl" name="processAction">
	<portlet:param name="action"
		value="<%=AUTHORISATION_PANEL.AUTH_PANEL_LISTING_ACTION.name()%>" />
</portlet:actionURL>

<div style="padding:10px;"> 	
    <div class="panel  panel-primary">
		<div class="panel-heading"><span style="color:white; font-weight: bold">List of Authorisation Panels | Company - <%=company.getCompanyName() %></span></div>
		<div class="panel-body">
			<form  id="authPanelListingForm" action="<%=authPanelListingUrl%>" method="post" enctype="application/x-www-form-urlencoded">
			<div>
			Click on an EDIT button to update an authorisation panels' details
			</div>
			  <%
			  if(portletState.getAllAuthorizePanel()!=null && portletState.getAllAuthorizePanel().size()>0)
			  {
				  %>
				  
				  <table width="100%" class="table" id="btable">
					<thead>
					  <th>Authorisation Panel Name</th>
					  <th>Date Created</th>
					  <th>&nbsp;</th>
					</thead>
				  <%
				  if(portletState.getAllAuthorizePanel()!=null)
				  {
					  for(Iterator<AuthorizePanel> iter = portletState.getAllAuthorizePanel().iterator(); iter.hasNext();)
					  {
						  AuthorizePanel authorizePanel = iter.next();
						  DateFormat df = new SimpleDateFormat( "MMM-dd-yyyy");
						  String dateToShow ="N/A";
						  try{
						  	dateToShow = authorizePanel.getDateGenerated()==null ? "N/A" : df.format(new Date(authorizePanel.getDateGenerated().getTime()));
						  }catch(Exception e)
						  {
							  dateToShow = "N/A";
						  }
					  %>
					  <tr>
						<td><%=authorizePanel.getPanelName()==null ? "N/A" : authorizePanel.getPanelName() %></td>
						<td><%=dateToShow%></td>
						<td>
							<div class="btn-group">
							  <button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('update', '<%=authorizePanel.getId()%>')">Update This Mandate Panel</button>
							  <button type="button" class="btn btn-danger dropdown-toggle" data-toggle="dropdown">
							    <span class="caret"></span>
							    <span class="sr-only">Toggle Dropdown</span>
							  </button>
							  <ul class="dropdown-menu" role="menu">
							    <li><a href="javascript: handleButtonAction('update', '<%=authorizePanel.getId()%>')">Update This Mandate Panel</a></li>
							    <li><a href="javascript: handleButtonAction('delete', '<%=authorizePanel.getId()%>')">Delete This Mandate Panel</a></li>
								<li><a href="javascript: handleButtonAction('viewusersmapped', '<%=authorizePanel.getId()%>')">View Users Mapped To This Mandate Panel</a></li>
							  </ul>
							</div>
						</td>
					  </tr>
					  <%
					  }
				  }
				  else 
				  {
					  %>
					  <tr>
						<td colspan="2">There are no Authorisation Panels created on the platform yet</td>
					  </tr>
					  <%
				  }
			  }else
			  {
				  %>
				  <table width="100%" class="table" id="">
					<thead>				  
					  <th>Authorisation Panel Name</th>
					  <th>Date Created</th>
					</thead>
				  <tr>
					<td colspan="2">There are no Authorisation Panels created on the platform yet</td>
				  </tr>
				  <%  
			  }
			  %>
			</table>
			<button float="left" class="btn btn-danger" onclick="javascript:handleButtonAction('goback', '')">Go Back</button>
			<input type="hidden" name="selectedAuthPanel" id="selectedAuthPanel" value="" />
			<input type="hidden" name="selectedAuthPanelAction" id="selectedAuthPanelAction" value="" />	
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
		if(confirm("Are you sure you want to delete this authorisation panel? You will not be able to recover the authorisation panel after deleting it!"))
		{
			document.getElementById('selectedAuthPanel').value = companyId;
			document.getElementById('selectedAuthPanelAction').value = action;
			document.getElementById('authPanelListingForm').submit();
		}
	}else
	{
		document.getElementById('selectedAuthPanel').value = companyId;
		document.getElementById('selectedAuthPanelAction').value = action;
		document.getElementById('authPanelListingForm').submit();
	}
}
</script>