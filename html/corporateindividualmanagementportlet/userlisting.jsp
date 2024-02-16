<%@page import="com.probase.smartpay.admin.corporateindividualmanagement.CorporateIndividualManagementPortletState"%>
<%@page import="com.probase.smartpay.admin.corporateindividualmanagement.CorporateIndividualManagementPortletState.*"%>
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
<%@page import="smartpay.entity.PortalUser"%>
<%@page import="smartpay.entity.Company"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.enumerations.PortalUserStatusConstants"%>
<%@page import="smartpay.entity.RoleType"%>
<%@page import="smartpay.entity.AuthorizePanelCombination"%>
<%@page import="java.text.DateFormat"%>
<%@page import="com.probase.smartpay.commins.ComminsApplicationState"%>
<%@page import="com.probase.smartpay.commins.Util"%>
<%@page import="com.probase.smartpay.commins.Util.DETERMINE_ACCESS"%>
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

CorporateIndividualManagementPortletState portletState = CorporateIndividualManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(CorporateIndividualManagementPortletState.class);
ComminsApplicationState cappState = portletState.getCas();
Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
DETERMINE_ACCESS determinAccessForUser = new Util().determineAccessForUserFxns(twoStep, approvalProcess, cappState, portletState.getPortalUserCRUDRights());

%>

<jsp:include page="/html/corporateindividualmanagementportlet/corporateindividual/tabs.jsp" flush="" />


<portlet:actionURL var="modifyPortalUser" name="processAction">
	<portlet:param name="action"
		value="<%=COMPANY_CREATE_INDIVIDUAL_ACTIONS.MODIFY_COMPANY.name()%>" />
</portlet:actionURL>

<div style="padding:10px">
<div class="panel panel-info">
  	<!-- Default panel contents -->
  	<div class="panel-heading"><strong>List of Company Staff</strong></div>
  	<div class="panel-body">
		<p>Click on a company staff to view the company staff details. Click on an EDIT button to update a company staff details</p>
  	</div>
		<form  id="userListFormId" action="<%=modifyPortalUser%>" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">
			
		<legend>Portal Users Listing</legend>
		  <%
		  if(portletState.getAllCompanyPersonnel()!=null && portletState.getAllCompanyPersonnel().size()>0)
		  {
			  %>
			  
			  <table width="100%" class="table table-hover" id="btable">
				<thead>
				  <th>First Name</th>
				  <th>Last Name</th>
				  <th>Company Name</th>
				  <th>Email Address</th>
				  <th>Mobile Number</th>
				  <th>Type of User</th>
				  <th>Status</th>
				  <th>&nbsp;</th>
				</thead>
			  <%
			  for(Iterator<PortalUser> iter = portletState.getAllCompanyPersonnel().iterator(); iter.hasNext();)
			  {
				  	PortalUser pu = iter.next();
				 
						  String userType = null;
						  if(pu.getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
						  {
								userType = "BANK ADMINISTRATOR";
						  }else if(pu.getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_HEAD_OF_OPERATIONS))
						  {
								userType = "HEAD OF OPERATIONS";
						  }else if(pu.getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_TELLER))
						  {
								userType = "BANK TELLER";
						  }else if(pu.getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_NON_ADMINISTRATOR_PERSONNEL))
						  {
								userType = "CORPORATE COMPANY STAFF";
						  }else if(pu.getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
						  {
								userType = "COMPANY ADMINISTRATOR";
						  }else if(pu.getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_HQ_OPERATIONS_ASSISTANT))
						  {
								userType = "HQ OPERATIONS ASSISTANT";
						  }else if(pu.getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_RETAIL_STAFF))
						  {
								userType = "RETAIL COMPANY STAFF";
						  }
						  
						  
						  String status = null;
						  if(pu.getStatus().equals(PortalUserStatusConstants.PORTAL_USER_STATUS_ACTIVE))
								  status = "Active";
						  if(pu.getStatus().equals(PortalUserStatusConstants.PORTAL_USER_INACTIVE))
								  status = "Inactive";
						  if(pu.getStatus().equals(PortalUserStatusConstants.PORTAL_USER_STATUS_SUSPENDED))
								  status = "Blocked";
						  if(pu.getStatus().equals(PortalUserStatusConstants.PORTAL_USER_DELETED))
							  status = "Deleted";
						  
						  if(userType!=null && status!=null)
						  {
					  %>
					 <tr>
						<td><%=pu.getFirstName()==null ? "N/A" : pu.getFirstName() %></td>
						<td><%=pu.getLastName()==null ? "N/A" : pu.getLastName()%></td>
						<td><%=pu.getCompany()!=null && pu.getCompany().getCompanyName()!=null ? pu.getCompany().getCompanyName() : "N/A"%></td>
						<td><%=pu.getEmailAddress()==null ? "N/A" : pu.getEmailAddress()%></td>
						<td><%=pu.getMobileNumber()==null ? "N/A" : pu.getMobileNumber()%></td>
						<td><%=userType%></td>
						<td><%=status%></td>
						<td>
							<%
							if(determinAccessForUser!=null && (determinAccessForUser.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS)
									 || determinAccessForUser.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS)))
							{
								if(!pu.getStatus().equals(PortalUserStatusConstants.PORTAL_USER_DELETED))
								{
								%>
								<div class="btn-group">
									<%
									if(pu.getStatus().equals(PortalUserStatusConstants.PORTAL_USER_STATUS_ACTIVE))
									{
									%>
								  <button type="button" class="btn btn-success" onclick="javascript:handleButtonAction('update', '<%=pu.getId()%>')">Update User Details</button>
								  	<%
									}else if(pu.getStatus().equals(PortalUserStatusConstants.PORTAL_USER_STATUS_SUSPENDED))
									{
									%>
								  <button type="button" class="btn btn-success" onclick="javascript:handleButtonAction('reactivate', '<%=pu.getId()%>')">Unblock User</button>
								  	<%
									}else
									{
										if(determinAccessForUser!=null && (determinAccessForUser.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS)))
										{
											if(pu.getStatus().equals(PortalUserStatusConstants.PORTAL_USER_INACTIVE) && 
													portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
											{
										%>
										<button type="button" class="btn btn-success" onclick="javascript:handleButtonAction('approveActivate', '<%=pu.getId()%>')">Approve & Activate Company Staff</button>
										<%
											}
										}
									}
								  	%>
								  <button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								  </button>
								  <ul class="dropdown-menu" role="menu">
									<%
									if(pu.getStatus().equals(PortalUserStatusConstants.PORTAL_USER_STATUS_ACTIVE))
									{
									%>
									<li><a href="javascript: handleButtonAction('update', '<%=pu.getId()%>')">Update User Details</a></li>
									<li><a href="javascript: handleButtonAction('suspend', '<%=pu.getId()%>')">Block User</a></li>
									<li><a href="javascript: handleButtonAction('delete', '<%=pu.getId()%>')">Delete User</a></li>
									<%
									}
									if(pu.getStatus().equals(PortalUserStatusConstants.PORTAL_USER_STATUS_SUSPENDED))
									{
									%>
									<li><a href="javascript: handleButtonAction('reactivate', '<%=pu.getId()%>')">Unblock User</a></li>
									<%
									}
									if(determinAccessForUser!=null && (determinAccessForUser.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS)))
									{
										if(pu.getStatus().equals(PortalUserStatusConstants.PORTAL_USER_INACTIVE) && 
												portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
										{
											if(pu.getStatus().equals(PortalUserStatusConstants.PORTAL_USER_INACTIVE))
											{
										%>
											<li><a href="javascript: handleButtonAction('approveActivate', '<%=pu.getId()%>')">Approve & Activate Company Staff</a></li>
										<%
											}
										}
									}
									%>
								  </ul>
								</div>
								<%
								}
							}
							%>
						</td>
					  </tr> 
			  <%
			  			  }
			  }
		  }
		  %>
		</table>
		<input type="hidden" name="selectedPortalUser" id="selectedPortalUser" value="" />
		<input type="hidden" name="selectedPortalUserAction" id="selectedPortalUserAction" value="" />	
	</form>
</div>
</div>


<script type="text/javascript" charset="utf-8" src="<%=jqueryDataTableUrl%>"></script>

<script type="text/javascript">
$(document).ready(function() {
    $('#btable').dataTable();
} );


function handleButtonAction(action, usId){
	
	if(action=='delete')
	{
		if(confirm("Are you sure you want to delete this user? You will not be able to undo this action after deleting the user!"))
		{
			document.getElementById('selectedPortalUser').value = usId;
			document.getElementById('selectedPortalUserAction').value = action;
			document.getElementById('userListFormId').submit();
		}
	}else
	{
		document.getElementById('selectedPortalUser').value = usId;
		document.getElementById('selectedPortalUserAction').value = action;
		document.getElementById('userListFormId').submit();
	}
}
</script>