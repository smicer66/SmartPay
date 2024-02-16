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
<%@page import="smartpay.entity.enumerations.SmartPayConstants"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="com.probase.smartpay.commins.Util"%>
<%@page import="java.text.DateFormat"%>
<%@page import="com.probase.smartpay.commins.ComminsApplicationState"%>
<%@page import="com.probase.smartpay.commins.Util"%>
<%@page import="com.probase.smartpay.commins.Util.DETERMINE_ACCESS"%>
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
Company company = portletState.getMandatePanelPortletUtil().getCompanyById(Long.valueOf(portletState.getSelectedCompanyId()));
ComminsApplicationState cappState = portletState.getCas();
Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
DETERMINE_ACCESS determinAccessForUser = new Util().determineAccessForUserFxns(twoStep, approvalProcess, cappState, portletState.getPortalUserCRUDRights());
%>

<jsp:include page="/html/mandatepanelportlet/mandatepanel/tabs.jsp" flush="" />


<portlet:actionURL var="authPanelListingUrl" name="processAction">
	<portlet:param name="action"
		value="<%=AUTHORISATION_PANEL.UPDATE_USER_PANEL_MAPPING.name()%>" />
</portlet:actionURL>



<%
if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
{
	%>
	<div style="padding:10px;"> 	
    <div class="panel  panel-primary">
		<div class="panel-heading"><span style="color:white; font-weight: bold">Current Users Mapped To Panels | Company - <%=company.getCompanyName() %></span></div>
		<div class="panel-body">
			<form  id="authPanelListingForm" action="<%=authPanelListingUrl%>" method="post" enctype="application/x-www-form-urlencoded">
			<div>
			Click on an EDIT button to update an authorisation panels' details
			</div>
			  <%
			  if(portletState.getAllAuthorizePanelCombination()!=null && portletState.getAllAuthorizePanelCombination().size()>0)
			  {
 %>
				  
				  <table width="100%" class="table" id="btable">
					<thead>
					  <th>Users Full Names</th>
					  <th>Authorisation Panel Name</th>
					  <th>Financial Restriction</th>
					  <th>Date Created</th>
					  <th>Status</th>
					  <th>&nbsp;</th>
					</thead>
				  <%
				  if(portletState.getAllAuthorizePanelCombination()!=null)
				  {
					  for(Iterator<AuthorizePanelCombination> iter = portletState.getAllAuthorizePanelCombination().iterator(); iter.hasNext();)
					  {
						  AuthorizePanelCombination authorizePanelCombination = iter.next();
						  DateFormat df = new SimpleDateFormat( "MMM-dd-yyyy");
						  String dateToShow ="N/A";
						  try{
						  	dateToShow = authorizePanelCombination.getDateCreated()==null ? "N/A" : df.format(new Date(authorizePanelCombination.getDateCreated().getTime()));
						  }catch(Exception e)
						  {
							  dateToShow = "N/A";
						  }
					  %>
					  <tr>
						<td><%=authorizePanelCombination.getPortalUser().getFirstName() + " " + authorizePanelCombination.getPortalUser().getLastName() %></td>
						<td><%=authorizePanelCombination.getAuthorizePanel().getPanelName()%></td>
						<td>Between ZMW<%=new Util().roundUpAmount(authorizePanelCombination.getAuthorizePanel().getFinancialAmountRestriction().getLowerLimitValue())%> and ZMW <%=new Util().roundUpAmount(authorizePanelCombination.getAuthorizePanel().getFinancialAmountRestriction().getUpperLimitValue()) %></td>
						<td><%=dateToShow%></td>
						<td><%=authorizePanelCombination.getStatus().getValue().replace("_", "")%></td>
						<td>&nbsp;
							<button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('removeUser', '<%=authorizePanelCombination.getId()%>')">Remove this User From This Mandate Panel</button>
						</td>
					  </tr>
					  <%
					  }
				  }
				  else 
				  {
					  %>
					  <tr>
						<td colspan="6">There are no Users Mapped to this Authorisation Panel currently</td>
					  </tr>
					  <%
				  }
			  }else
			  {
				  %>
				  <table width="100%" class="table" id="">
					<thead>
					  <th>Users Full Names</th>
					  <th>Authorisation Panel Name</th>
					  <th>Financial Restriction</th>
					  <th>Date Created</th>
					  <th>Status</th>
					</thead>
				  <tr>
						<td colspan="5">There are no Users Mapped to this Authorisation Panel currently</td>
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
	<%
}else
{
	if(determinAccessForUser.equals(DETERMINE_ACCESS.DISPLAY_SECOND_LEVEL_LOGIN) && determinAccessForUser.equals(DETERMINE_ACCESS.DISPLAY_SECOND_LEVEL_LOGIN))
	{
		%>
		<jsp:include page="/html/mandatepanelportlet/mandatepanel/login_step2.jsp" flush="" />
		<%
	}else if(determinAccessForUser.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS) && determinAccessForUser.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
	{
		%>
<div style="padding:10px;"> 	
    <div class="panel  panel-primary">
		<div class="panel-heading"><span style="color:white; font-weight: bold">Current Users Mapped To Panels | Company - <%=company.getCompanyName() %></span></div>
		<div class="panel-body">
			<form  id="authPanelListingForm" action="<%=authPanelListingUrl%>" method="post" enctype="application/x-www-form-urlencoded">
			<div>
			Click on an EDIT button to update an authorisation panels' details
			</div>
			  <%
			  if(portletState.getAllAuthorizePanelCombination()!=null && portletState.getAllAuthorizePanelCombination().size()>0)
			  {
				  %>
				  
				  <table width="100%" class="table" id="btable">
					<thead>
					  <th>Users Full Names</th>
					  <th>Authorisation Panel Name</th>
					  <th>Financial Restriction</th>
					  <th>Date Created</th>
					  <th>Status</th>
					  <th>&nbsp;</th>
					</thead>
				  <%
				  if(portletState.getAllAuthorizePanelCombination()!=null)
				  {
					  for(Iterator<AuthorizePanelCombination> iter = portletState.getAllAuthorizePanelCombination().iterator(); iter.hasNext();)
					  {
						  AuthorizePanelCombination authorizePanelCombination = iter.next();
						  DateFormat df = new SimpleDateFormat( "MMM-dd-yyyy");
						  String dateToShow ="N/A";
						  try{
						  	dateToShow = authorizePanelCombination.getDateCreated()==null ? "N/A" : df.format(new Date(authorizePanelCombination.getDateCreated().getTime()));
						  }catch(Exception e)
						  {
							  dateToShow = "N/A";
						  }
					  %>
					  <tr>
						<td><%=authorizePanelCombination.getPortalUser().getFirstName() + " " + authorizePanelCombination.getPortalUser().getLastName() %></td>
						<td><%=authorizePanelCombination.getAuthorizePanel().getPanelName()%></td>
						<td>Between ZMW<%=new Util().roundUpAmount(authorizePanelCombination.getAuthorizePanel().getFinancialAmountRestriction().getLowerLimitValue())%> and ZMW <%=new Util().roundUpAmount(authorizePanelCombination.getAuthorizePanel().getFinancialAmountRestriction().getUpperLimitValue()) %></td>
						<td><%=dateToShow%></td>
						<td><%=authorizePanelCombination.getStatus().getValue().replace("_", "")%></td>
						<td>&nbsp;
							<%
							if(authorizePanelCombination.getStatus().equals(SmartPayConstants.AUTHORIZE_PANEL_COMBINATION_STATUS_ACTIVE))
							{
							%>
							<button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('removeUser', '<%=authorizePanelCombination.getId()%>')">Remove this User From This Mandate Panel</button>
							<%
							}
							%>
						</td>
					  </tr>
					  <%
					  }
				  }
				  else 
				  {
					  %>
					  <tr>
						<td colspan="6">There are no Users Mapped to this Authorisation Panel currently</td>
					  </tr>
					  <%
				  }
			  }else
			  {
				  %>
				  <table width="100%" class="table" id="">
					<thead>
					  <th>Users Full Names</th>
					  <th>Authorisation Panel Name</th>
					  <th>Financial Restriction</th>
					  <th>Date Created</th>
					  <th>Status</th>
					</thead>
				  <tr>
						<td colspan="5">There are no Users Mapped to this Authorisation Panel currently</td>
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
		<%
	}else if(determinAccessForUser.equals(DETERMINE_ACCESS.GRANT_APPROVER_ACCESS) && determinAccessForUser.equals(DETERMINE_ACCESS.GRANT_APPROVER_ACCESS))
	{
		%>
		<div style="padding:10px;"> 	
    <div class="panel  panel-primary">
		<div class="panel-heading"><span style="color:white; font-weight: bold">Current Users Mapped To Panels | Company - <%=company.getCompanyName() %></span></div>
		<div class="panel-body">
			<form  id="authPanelListingForm" action="<%=authPanelListingUrl%>" method="post" enctype="application/x-www-form-urlencoded">
			<div>
			Click on an EDIT button to update an authorisation panels' details
			</div>
			  <%
			  if(portletState.getAllAuthorizePanelCombination()!=null && portletState.getAllAuthorizePanelCombination().size()>0)
			  {
				  %>
				  
				  <table width="100%" class="table" id="btable">
					<thead>
					  <th>Users Full Names</th>
					  <th>Authorisation Panel Name</th>
					  <th>Financial Restriction</th>
					  <th>Date Created</th>
					  <th>Status</th>
					</thead>
				  <%
				  if(portletState.getAllAuthorizePanelCombination()!=null)
				  {
					  for(Iterator<AuthorizePanelCombination> iter = portletState.getAllAuthorizePanelCombination().iterator(); iter.hasNext();)
					  {
						  AuthorizePanelCombination authorizePanelCombination = iter.next();
						  DateFormat df = new SimpleDateFormat( "MMM-dd-yyyy");
						  String dateToShow ="N/A";
						  try{
						  	dateToShow = authorizePanelCombination.getDateCreated()==null ? "N/A" : df.format(new Date(authorizePanelCombination.getDateCreated().getTime()));
						  }catch(Exception e)
						  {
							  dateToShow = "N/A";
						  }
					  %>
					  <tr>
						<td><%=authorizePanelCombination.getPortalUser().getFirstName() + " " + authorizePanelCombination.getPortalUser().getLastName() %></td>
						<td><%=authorizePanelCombination.getAuthorizePanel().getPanelName()%></td>
						<td>Between ZMW<%=new Util().roundUpAmount(authorizePanelCombination.getAuthorizePanel().getFinancialAmountRestriction().getLowerLimitValue())%> and ZMW <%=new Util().roundUpAmount(authorizePanelCombination.getAuthorizePanel().getFinancialAmountRestriction().getUpperLimitValue()) %></td>
						<td><%=dateToShow%></td>
						<td><%=authorizePanelCombination.getStatus().getValue().replace("_", "")%></td>
					  </tr>
					  <%
					  }
				  }
				  else 
				  {
					  %>
					  <tr>
						<td colspan="5">There are no Users Mapped to this Authorisation Panel currently</td>
					  </tr>
					  <%
				  }
			  }else
			  {
				  %>
				  <table width="100%" class="table" id="">
					<thead>
					  <th>Users Full Names</th>
					  <th>Authorisation Panel Name</th>
					  <th>Financial Restriction</th>
					  <th>Date Created</th>
					  <th>Status</th>
					</thead>
				  <tr>
						<td colspan="5">There are no Users Mapped to this Authorisation Panel currently</td>
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
		<%
	}else if(determinAccessForUser.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS) && determinAccessForUser.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
	{
		%>
<div style="padding:10px;"> 	
    <div class="panel  panel-primary">
		<div class="panel-heading"><span style="color:white; font-weight: bold">Current Users Mapped To Panels | Company - <%=company.getCompanyName() %></span></div>
		<div class="panel-body">
			<form  id="authPanelListingForm" action="<%=authPanelListingUrl%>" method="post" enctype="application/x-www-form-urlencoded">
			<div>
			Click on an EDIT button to update an authorisation panels' details
			</div>
			  <%
			  if(portletState.getAllAuthorizePanelCombination()!=null && portletState.getAllAuthorizePanelCombination().size()>0)
			  {
				  %>
				  
				  <table width="100%" class="table" id="btable">
					<thead>
					  <th>Users Full Names</th>
					  <th>Authorisation Panel Name</th>
					  <th>Financial Restriction</th>
					  <th>Date Created</th>
					  <th>Status</th>
					  <th>&nbsp;</th>
					</thead>
				  <%
				  if(portletState.getAllAuthorizePanelCombination()!=null)
				  {
					  for(Iterator<AuthorizePanelCombination> iter = portletState.getAllAuthorizePanelCombination().iterator(); iter.hasNext();)
					  {
						  AuthorizePanelCombination authorizePanelCombination = iter.next();
						  DateFormat df = new SimpleDateFormat( "MMM-dd-yyyy");
						  String dateToShow ="N/A";
						  try{
						  	dateToShow = authorizePanelCombination.getDateCreated()==null ? "N/A" : df.format(new Date(authorizePanelCombination.getDateCreated().getTime()));
						  }catch(Exception e)
						  {
							  dateToShow = "N/A";
						  }
					  %>
					  <tr>
						<td><%=authorizePanelCombination.getPortalUser().getFirstName() + " " + authorizePanelCombination.getPortalUser().getLastName() %></td>
						<td><%=authorizePanelCombination.getAuthorizePanel().getPanelName()%></td>
						<td>Between ZMW<%=new Util().roundUpAmount(authorizePanelCombination.getAuthorizePanel().getFinancialAmountRestriction().getLowerLimitValue())%> and ZMW <%=new Util().roundUpAmount(authorizePanelCombination.getAuthorizePanel().getFinancialAmountRestriction().getUpperLimitValue()) %></td>
						<td><%=dateToShow%></td>
						<td><%=authorizePanelCombination.getStatus().getValue().replace("_", "")%></td>
						<td>&nbsp;
							<%
							if(authorizePanelCombination.getStatus().equals(SmartPayConstants.AUTHORIZE_PANEL_COMBINATION_STATUS_ACTIVE))
							{
							%>
							<button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('removeUser', '<%=authorizePanelCombination.getId()%>')">Remove this User From This Mandate Panel</button>
							<%
							}
							%>
						</td>
					  </tr>
					  <%
					  }
				  }
				  else 
				  {
					  %>
					  <tr>
						<td colspan="6">There are no Users Mapped to this Authorisation Panel currently</td>
					  </tr>
					  <%
				  }
			  }else
			  {
				  %>
				  <table width="100%" class="table" id="">
					<thead>
					  <th>Users Full Names</th>
					  <th>Authorisation Panel Name</th>
					  <th>Financial Restriction</th>
					  <th>Date Created</th>
					  <th>Status</th>
					</thead>
				  <tr>
						<td colspan="5">There are no Users Mapped to this Authorisation Panel currently</td>
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
		<%
	}else if(determinAccessForUser.equals(DETERMINE_ACCESS.NO_RIGHTS_AT_ALL) && determinAccessForUser.equals(DETERMINE_ACCESS.NO_RIGHTS_AT_ALL))
	{
		%>
		<div class="panel panel-danger">You do not have access to carry out any actions as you do not have valid access. Contact Appropriate Administrators for rights</div>
		<%
	}
}
%>



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