<%@page import="com.probase.smartpay.admin.settingsmanagement.SettingsManagementPortletState.SETTINGS"%>
<%@page import="com.probase.smartpay.approvalflow.ApprovalFlowPortlet"%>
<%@page import="com.probase.smartpay.approvalflow.ApprovalFlowPortletState.APPROVAL_FLOW_ACTIONS"%>
<%@page import="com.probase.smartpay.approvalflow.ApprovalFlowPortletState"%>
<%@page import="com.probase.smartpay.approvalflow.ApprovalFlowPortletState.VIEW_TABS"%>
<%@page import="com.probase.smartpay.admin.feedescriptionmanagement.FeeDescriptionPortletState.FEE_DESCRIPTION_APPROVAL_TYPE"%>
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
<%@page import="smartpay.entity.PortalUser"%>
<%@page import="smartpay.entity.TaxType"%>
<%@page import="smartpay.entity.FeeDescription"%>
<%@page import="org.codehaus.jettison.json.JSONObject"%>
<%@page import="smartpay.entity.enumerations.SmartPayConstants"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.enumerations.ActionTypeConstants"%>
<%@page import="smartpay.entity.Settings"%>
<%@page import="smartpay.entity.RoleType"%>
<%@page import="smartpay.entity.AuthorizePanelCombination"%>
<%@page import="smartpay.entity.ApprovalFlowTransit"%>
<%@page import="java.text.DateFormat"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />
<%
	String resourceBaseURL = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ "/resources";
	String faceboxCssUrl = resourceBaseURL + "/css/facebox.css";
	String pagingUrl = resourceBaseURL + "/css/paging.css";


		
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

ApprovalFlowPortletState portletState = ApprovalFlowPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ApprovalFlowPortletState.class);

boolean proceed = true;
boolean proceed1 = false;
String workItemType = "";

%>

<jsp:include page="/html/approvalflowportlet/tabs.jsp" flush="" />


<portlet:actionURL var="modifyCompany" name="processAction">
	<portlet:param name="action"
		value="<%=APPROVAL_FLOW_ACTIONS.HANDLE_APPROVAL_LISTINGS.name()%>" />
</portlet:actionURL>

<div style="padding:10px;">
<h2>Approvals</h2> 	
<div class="panel panel-info">
  	<!-- Default panel contents -->
  	<div class="panel-heading"><strong>Approvals For <%=portletState.getWorkItemTypeDescription() %></strong></div>
  	<div class="panel-body">
  	<%
  	if(portletState.getAllApprovalFlowTransitListing()!=null && portletState.getAllApprovalFlowTransitListing().size()>0){
  	%>
		<p>Click on an items button to carry out an action on that item</p>
	<%
  	}else{
	%>
		<p>Your Approval Work Tray is empty. There are no items to work on at the moment!</p>
	<%
  	}
	%>
  	</div>
		<form  id="startRegFormId" action="<%=modifyCompany%>" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">
		  <%
		  if(portletState.getAllApprovalFlowTransitListing()!=null && portletState.getAllApprovalFlowTransitListing().size()>0)
		  {
			  %>
			   
			  <table width="100%" class="table table-hover" id="btable">
				<thead>
				<%
				if(portletState.getSelectedApprovalItemType().equalsIgnoreCase(PortalUser.class.getSimpleName()))
				{
				%>
				  <th>First Name</th>
				  <th>Last Name</th>
				  <th>Email Address</th>
				  <th>Mobile Number</th>
				  <th>User Role</th>
				  <th>Company</th>
				  <th>Request From</th>
				  <th>&nbsp;</th>
				<%
				}else if(portletState.getSelectedApprovalItemType().equalsIgnoreCase(TaxType.class.getSimpleName()))
				{
				%>
				  <th>Tax Type Name</th>
				  <th>Tax Type Code</th>
				  <th>Tax Type Account Number</th>
				  <th>Tax Type Account Sort Code</th>
				  <th>Request From</th>
				  <th>&nbsp;</th>
				<%
				}else if(portletState.getSelectedApprovalItemType().equalsIgnoreCase(Company.class.getSimpleName()))
				{
				%>
				  <th>Company Name</th>
				  <th>Company RC Number</th>
				  <th>Contact Email</th>
				  <th>Mobile Number</th>
				  <th>Account Number</th>
				  <th>Company Type</th>
				  <th>Request From</th>
				  <th>&nbsp;</th>
				<%
				}else if(portletState.getSelectedApprovalItemType().equalsIgnoreCase(Settings.class.getSimpleName()))
				{
				%>
				  <th>Setting</th>
				  <th>Value</th>
				  <th>Request By</th>
				  <th>&nbsp;</th>
				<%
				}else if(portletState.getSelectedApprovalItemType().equalsIgnoreCase(AuthorizePanelCombination.class.getSimpleName()))
				{
				%>
				  <th>Mandate Panel Name</th>
				  <th>Min Amount</th>
				  <th>Max Amount</th>
				  <th>Company</th>
				  <th>Company Staff Added to Panel</th>
				  <th>Request By</th>
				  <th>&nbsp;</th>
				<%
				}else if(portletState.getSelectedApprovalItemType().equalsIgnoreCase(FeeDescription.class.getSimpleName()))
				{
					if(portletState.getFeeDescriptionApprovalType()!=null && portletState.getFeeDescriptionApprovalType().equals(FEE_DESCRIPTION_APPROVAL_TYPE.CORE_FEE_VIEW))
					{
				%>
						  <th>Transaction Fee</th>
						  <th>Description</th>
						  <th>Amount Applicable</th>
						  <th>Primary Fee</th>
						  <th>Request By</th>
						  <th>&nbsp;</th>
				<%
					}else if(portletState.getFeeDescriptionApprovalType()!=null && portletState.getFeeDescriptionApprovalType().equals(FEE_DESCRIPTION_APPROVAL_TYPE.COMPANY_MAPPINGS))
					{
				%>
						  <th>Transaction Fee</th>
						  <th>Amount Applicable</th>
						  <th>Company Mapped To</th>
						  <th>Request By</th>
						  <th>&nbsp;</th>
				<%
					}
				%>
				  
				<%
				}
				%>
				</thead>
			  <%
			  if(portletState.getSelectedApprovalItemType().equalsIgnoreCase(PortalUser.class.getSimpleName()))
			  {
				  for(Iterator<ApprovalFlowTransit> iter = portletState.getAllApprovalFlowTransitListing().iterator(); iter.hasNext();)
				  {
					  ApprovalFlowTransit aft = iter.next();
					  PortalUser puReq = aft.getPortalUser();
					  JSONObject jsonObject = new JSONObject(aft.getObjectData());
					  
					  String approve = "Approve";
					  String disapprove = "Disapprove";
					  if(aft.getActionType().equals(ActionTypeConstants.PORTAL_USER_ACTION_CREATE))
					  {
						  approve = "Approve & Create User Profile";
						  disapprove = "Disapprove User Profile";
					  }else if(aft.getActionType().equals(ActionTypeConstants.PORTAL_USER_ACTION_BLOCK))
					  {
						  approve = "Approve User Profile Block";
						  disapprove = "Disapprove User Profile Block";
					  }else if(aft.getActionType().equals(ActionTypeConstants.PORTAL_USER_ACTION_DELETE))
					  {
						  approve = "Approve & Delete User Profile";
						  disapprove = "Disapprove User Profile Deletion";
					  }else if(aft.getActionType().equals(ActionTypeConstants.PORTAL_USER_ACTION_UNBLOCK))
					  {
						  approve = "Approve & UnBlock User Profile";
						  disapprove = "Disapprove User Profile UnBlock";
					  }else if(aft.getActionType().equals(ActionTypeConstants.PORTAL_USER_ACTION_UPDATE))
					  {
						  approve = "Approve & Update User Profile";
						  disapprove = "Disapprove User Profile Update";
					  }
					  Company co = null;
					  if(jsonObject.has("company"))
					  {
						  String company = jsonObject.getString("company");
						  Long companyId = Long.valueOf(company);
						  co = (Company)portletState.getApprovalFlowPortletUtil().getEntityObjectById(Company.class, companyId);
					  }
					  
					  %>
					  <tr>
						<td><%=jsonObject.getString("firstName") %></td>
						<td><%=jsonObject.getString("lastName") %></td>
						<td><%=jsonObject.getString("email") %></td>
						<td><%=jsonObject.getString("mobile")%></td>
						<td><%=jsonObject.getString("roleType")%></td>
						<td><%=co!=null ? co.getCompanyName(): "N/A"%></td>
						<td><%=puReq.getFirstName() + " " + puReq.getLastName() %></td>
						<td>
							<%
							if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
							{
							%>
							<button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('cancel', '<%=aft.getId()%>')">Cancel Request</button>
							<%
							}else
							{
							%>
								<div class="btn-group">
								  <button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('approve', '<%=aft.getId()%>')"><%=approve %></button>
								  <button type="button" class="btn btn-danger dropdown-toggle" data-toggle="dropdown">
								    <span class="caret"></span>
								    <span class="sr-only">Toggle Dropdown</span>
								  </button>
								  <ul class="dropdown-menu" role="menu">
								  	<li><a href="javascript: handleButtonAction('approve', '<%=aft.getId()%>')"><%=approve %></a></li>
									<li><a href="javascript: handleButtonAction('reject', '<%=aft.getId()%>')"><%=disapprove %></a></li>
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
			  %>
			  <%
			  if(portletState.getSelectedApprovalItemType().equalsIgnoreCase(Company.class.getSimpleName()))
			  {
				  for(Iterator<ApprovalFlowTransit> iter = portletState.getAllApprovalFlowTransitListing().iterator(); iter.hasNext();)
				  {
					  ApprovalFlowTransit aft = iter.next();
					  PortalUser puReq = aft.getPortalUser();
					  JSONObject jsonObject = new JSONObject(aft.getObjectData());
					  
					  
					  String approve = "Approve";
					  String disapprove = "Disapprove";
					  if(aft.getActionType().equals(ActionTypeConstants.COMPANY_ACTION_CREATE))
					  {
						  approve = "Approve & Create Company Profile";
						  disapprove = "Disapprove Company Profile";
					  }else if(aft.getActionType().equals(ActionTypeConstants.COMPANY_ACTION_BLOCK))
					  {
						  approve = "Approve Company Profile Block";
						  disapprove = "Disapprove Company Profile Block";
					  }else if(aft.getActionType().equals(ActionTypeConstants.COMPANY_ACTION_DELETE))
					  {
						  approve = "Approve & Delete Company Profile";
						  disapprove = "Disapprove User Company Deletion";
					  }else if(aft.getActionType().equals(ActionTypeConstants.COMPANY_ACTION_UNBLOCK))
					  {
						  approve = "Approve & UnBlock Company Profile";
						  disapprove = "Disapprove Company Profile UnBlock";
					  }else if(aft.getActionType().equals(ActionTypeConstants.COMPANY_ACTION_UPDATE))
					  {
						  approve = "Approve & Update Company Profile";
						  disapprove = "Disapprove Company Profile Update";
					  }
					  
					  %>
					 
					  <tr>
						<td><%=jsonObject.getString("companyname") %></td>
						<td><%=jsonObject.getString("companyrcnumber") %></td>
						<td><%=jsonObject.getString("companyemailaddress") %></td>
						<td><%=jsonObject.getString("companycontactphonenumber")%></td>
						<td><%=jsonObject.getString("bankNumber") %></td>
						<td><%=jsonObject.getString("selectedCompanyType") %></td>
						<td><%=puReq.getFirstName() + " " + puReq.getLastName() %></td>
						<td>
							<%
							if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
							{
							%>
							<button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('cancel', '<%=aft.getId()%>')">Cancel Request</button>
							<%
							}else
							{
							%>
							<div class="btn-group">
							  <button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('approve', '<%=aft.getId()%>')"><%=approve %></button>
							  <button type="button" class="btn btn-danger dropdown-toggle" data-toggle="dropdown">
							    <span class="caret"></span>
							    <span class="sr-only">Toggle Dropdown</span>
							  </button>
							  <ul class="dropdown-menu" role="menu">
							  
							  	<li><a href="javascript: handleButtonAction('approve', '<%=aft.getId()%>')"><%=approve %></a></li>
								<li><a href="javascript: handleButtonAction('reject', '<%=aft.getId()%>')"><%=disapprove %></a></li>
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
			  %>
			  <%
			  if(portletState.getSelectedApprovalItemType().equalsIgnoreCase(TaxType.class.getSimpleName()))
			  {
				  for(Iterator<ApprovalFlowTransit> iter = portletState.getAllApprovalFlowTransitListing().iterator(); iter.hasNext();)
				  {
					  ApprovalFlowTransit aft = iter.next();
					  PortalUser puReq = aft.getPortalUser();
					  JSONObject jsonObject = new JSONObject(aft.getObjectData());
					  
					  
					  String approve = "Approve";
					  String disapprove = "Disapprove";
					  if(aft.getActionType().equals(ActionTypeConstants.TAX_TYPE_ADD))
					  {
						  if(aft.getEntityId()!=null)
						  {
							  approve = "Approve & Update Tax Type";
							  disapprove = "Disapprove Tax Type Update";
						  }else
						  {
							  approve = "Approve & Create Tax Type";
							  disapprove = "Disapprove Tax Type Creation";
						  }
					  }else if(aft.getActionType().equals(ActionTypeConstants.TAX_TYPE_REACTIVATE))
					  {
						  approve = "Approve Tax Type Reactivation";
						  disapprove = "Disapprove Tax Type Reactivation";
					  }else if(aft.getActionType().equals(ActionTypeConstants.TAX_TYPE_SUSPEND))
					  {
						  approve = "Approve & Suspend Tax Type";
						  disapprove = "Disapprove Tax Type Suspension";
					  }
					  
					  
					  if(aft.getEntityId()==null && portletState.getCurrentTab().equals((VIEW_TABS.VNEW_TAXTYPE)))
					  {
						  proceed1 = true;
					  }else if(aft.getEntityId()!=null && portletState.getCurrentTab().equals((VIEW_TABS.VUPDATE_TAXTYPE)))
					  {
						  proceed1 = true;
					  }if(aft.getEntityId()!=null && portletState.getCurrentTab().equals((VIEW_TABS.VACT_TAXTYPE_REACTIVATE)))
					  {
						  proceed1 = true;
					  }if(aft.getEntityId()!=null && portletState.getCurrentTab().equals((VIEW_TABS.VACT_TAXTYPE_SUSPEND)))
					  {
						  proceed1 = true;
					  }
					  
					  
					  
					  if(proceed1)
					  {
					  %>
					 
					  <tr>
						<td><%=jsonObject.getString("taxname") %></td>
						<td><%=jsonObject.getString("taxcode") %></td>
						<td><%=jsonObject.getString("taxacctno") %></td>
						<td><%=jsonObject.getString("taxacctsortcode")%></td>
						<td><%=puReq.getFirstName() + " " + puReq.getLastName() %></td>
						<td>
							<%
							if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
							{
							%>
							<button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('cancel', '<%=aft.getId()%>')">Cancel Request</button>
							<%
							}else
							{
							%>
							<div class="btn-group">
							  <button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('approve', '<%=aft.getId()%>')"><%=approve %></button>
							  <button type="button" class="btn btn-danger dropdown-toggle" data-toggle="dropdown">
							    <span class="caret"></span>
							    <span class="sr-only">Toggle Dropdown</span>
							  </button>
							  <ul class="dropdown-menu" role="menu">
							  
							  	<li><a href="javascript: handleButtonAction('approve', '<%=aft.getId()%>')"><%=approve %></a></li>
								<li><a href="javascript: handleButtonAction('reject', '<%=aft.getId()%>')"><%=disapprove %></a></li>
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
			  }
			  %>
			  <%
			  if(portletState.getSelectedApprovalItemType().equalsIgnoreCase(Settings.class.getSimpleName()))
			  {
				  for(Iterator<ApprovalFlowTransit> iter = portletState.getAllApprovalFlowTransitListing().iterator(); iter.hasNext();)
				  {
					  ApprovalFlowTransit aft = iter.next();
					  PortalUser puReq = aft.getPortalUser();
					  JSONObject jsonObject = new JSONObject(aft.getObjectData());
					  
					  
					  String approve = "Approve";
					  String disapprove = "Disapprove";
					  if(aft.getActionType().equals(ActionTypeConstants.SETTINGS_UPDATE))
					  {
						  approve = "Approve & Update System Setting";
						  disapprove = "Disapprove System Setting Change";
					  }
					  Iterator keyIter = jsonObject.keys();
					  while(keyIter.hasNext())
					  {
						  String key = (String)keyIter.next();
						  String value = "";
						  if(((String)jsonObject.get(key)).equalsIgnoreCase(SmartPayConstants.SETTINGS_APPROVAL_DIRECT_TO_ONE_PORTAL_USER.getValue()))
							  value = ((String)jsonObject.get(key)).equals("1") ? "Yes" : "No";
						  if(((String)jsonObject.get(key)).equalsIgnoreCase(SmartPayConstants.SETTINGS_APPROVAL_PROCESS.getValue()))
							  value = ((String)jsonObject.get(key)).equals("1") ? "Yes" : "No";
						  if(((String)jsonObject.get(key)).equalsIgnoreCase(SmartPayConstants.SETTINGS_EMAIL_PORT.getValue()))
							  value = ((String)jsonObject.get(key));
						  if(((String)jsonObject.get(key)).equalsIgnoreCase(SmartPayConstants.SETTINGS_EMAIL_SENDER_EMAIL.getValue()))
							  value = ((String)jsonObject.get(key));
						  if(((String)jsonObject.get(key)).equalsIgnoreCase(SmartPayConstants.SETTINGS_EMAIL_SENDER_PASSWORD.getValue()))
							  value = ((String)jsonObject.get(key));
						  if(((String)jsonObject.get(key)).equalsIgnoreCase(SmartPayConstants.SETTINGS_EMAIL_SENDER_USERNAME.getValue()))
							  value = ((String)jsonObject.get(key));
						  if(((String)jsonObject.get(key)).equalsIgnoreCase(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_EMAIL.getValue()))
							  value = ((String)jsonObject.get(key)).equals("1") ? "Yes" : "No";
						  if(((String)jsonObject.get(key)).equalsIgnoreCase(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_SMS.getValue()))
							  value = ((String)jsonObject.get(key)).equals("1") ? "Yes" : "No";
						  if(((String)jsonObject.get(key)).equalsIgnoreCase(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_EMAIL.getValue()))
							  value = ((String)jsonObject.get(key)).equals("1") ? "Yes" : "No";
						  if(((String)jsonObject.get(key)).equalsIgnoreCase(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_SMS.getValue()))
							  value = ((String)jsonObject.get(key)).equals("1") ? "Yes" : "No";
						  if(((String)jsonObject.get(key)).equalsIgnoreCase(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_EMAIL.getValue()))
							  value = ((String)jsonObject.get(key)).equals("1") ? "Yes" : "No";
						  if(((String)jsonObject.get(key)).equalsIgnoreCase(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_SMS.getValue()))
							  value = ((String)jsonObject.get(key)).equals("1") ? "Yes" : "No";
						  if(((String)jsonObject.get(key)).equalsIgnoreCase(SmartPayConstants.SETTINGS_PLATFORM_COUNTRY.getValue()))
							  value = ((String)jsonObject.get(key));
						  if(((String)jsonObject.get(key)).equalsIgnoreCase(SmartPayConstants.SETTINGS_PLATORM_BANK.getValue()))
							  value = ((String)jsonObject.get(key));
						  if(((String)jsonObject.get(key)).equalsIgnoreCase(SmartPayConstants.SETTINGS_PRIMARY_FEE_DESCRIPTION.getValue()))
							  value = Double.toString(((FeeDescription) portletState.getApprovalFlowPortletUtil().getEntityObjectById(FeeDescription.class, Long.valueOf(((String)jsonObject.get(key))))).getAmountApplicable());
						  if(((String)jsonObject.get(key)).equalsIgnoreCase(SmartPayConstants.SETTINGS_SYSTEM_URL.getValue()))
							  value = ((String)jsonObject.get(key));
						  if(((String)jsonObject.get(key)).equalsIgnoreCase(SmartPayConstants.SETTINGS_TWO_STEP_LOGIN.getValue()))
							  value = ((String)jsonObject.get(key)).equals("1") ? "Yes" : "No";
						  if(((String)jsonObject.get(key)).equalsIgnoreCase(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_NUMBER.getValue()))
							  value = ((String)jsonObject.get(key));
						  if(((String)jsonObject.get(key)).equalsIgnoreCase(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_SORT_CODE.getValue()))
							  value = ((String)jsonObject.get(key));
						  
						  %>
							<tr>
								<td><%=key.replace("_", " ") %></td>
								<td><%=value %></td>
								<td><%=puReq.getFirstName() + " " + puReq.getLastName() %></td>
								<td>
									<%
									if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
									{
									%>
									<button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('cancel', '<%=aft.getId()%>')">Cancel Request</button>
									<%
									}else
									{
									%>
									<div class="btn-group">
									  <button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('approve', '<%=aft.getId()%>')"><%=approve %></button>
									  <button type="button" class="btn btn-danger dropdown-toggle" data-toggle="dropdown">
									    <span class="caret"></span>
									    <span class="sr-only">Toggle Dropdown</span>
									  </button>
									  <ul class="dropdown-menu" role="menu">
									  
									  	<li><a href="javascript: handleButtonAction('approve', '<%=aft.getId()%>')"><%=approve %></a></li>
										<li><a href="javascript: handleButtonAction('reject', '<%=aft.getId()%>')"><%=disapprove %></a></li>
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
			  }
			  %>
			  <%
			  if(portletState.getSelectedApprovalItemType().equalsIgnoreCase(AuthorizePanelCombination.class.getSimpleName()))
			  {
				  for(Iterator<ApprovalFlowTransit> iter = portletState.getAllApprovalFlowTransitListing().iterator(); iter.hasNext();)
				  {
					  ApprovalFlowTransit aft = iter.next();
					  PortalUser puReq = aft.getPortalUser();
					  JSONObject jsonObject = new JSONObject(aft.getObjectData());
					  
					  
					  String approve = "Approve";
					  String disapprove = "Disapprove";
					  if(aft.getActionType().equals(ActionTypeConstants.MANDATE_PANEL_MAP_USERS))
					  {
						  approve = "Approve This Request";
						  disapprove = "Disapprove This Request";
					  }else if(aft.getActionType().equals(ActionTypeConstants.MANDATE_PANEL_UNMAP_USERS))
					  {
						  approve = "Approve This Request";
						  disapprove = "Disapprove This Request";
					  }
					  
						  %>
							<tr>
								<td><%=(String)jsonObject.get("panelName") %></td>
								<td>ZMW<%=new Util().roundUpAmount(Double.valueOf((String)jsonObject.get("minAmt"))) %></td>
								<td>ZMW <%=new Util().roundUpAmount(Double.valueOf((String)jsonObject.get("maxAmt"))) %></td>
								<td><%=(String)jsonObject.get("companyName") %></td>
								<td><%=(String)jsonObject.get("companyStaff") %></td>
								<td><%=(String)jsonObject.get("requestBy") %></td>
								<td>
									<%
									if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
									{
									%>
									<button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('cancel', '<%=aft.getId()%>')">Cancel Request</button>
									<%
									}else
									{
									%>
									<div class="btn-group">
									  <button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('approve', '<%=aft.getId()%>')"><%=approve %></button>
									  <button type="button" class="btn btn-danger dropdown-toggle" data-toggle="dropdown">
									    <span class="caret"></span>
									    <span class="sr-only">Toggle Dropdown</span>
									  </button>
									  <ul class="dropdown-menu" role="menu">
									  
									  	<li><a href="javascript: handleButtonAction('approve', '<%=aft.getId()%>')"><%=approve %></a></li>
										<li><a href="javascript: handleButtonAction('reject', '<%=aft.getId()%>')"><%=disapprove %></a></li>
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
			  if(portletState.getSelectedApprovalItemType().equalsIgnoreCase(FeeDescription.class.getSimpleName()))
			  {
				
				if(portletState.getFeeDescriptionApprovalType()!=null && portletState.getFeeDescriptionApprovalType().equals(FEE_DESCRIPTION_APPROVAL_TYPE.CORE_FEE_VIEW))
				{
					  
					  
					  
					  for(Iterator<ApprovalFlowTransit> iter = portletState.getAllApprovalFlowTransitListing().iterator(); iter.hasNext();)
					  {
						  ApprovalFlowTransit aft = iter.next();
						  PortalUser puReq = aft.getPortalUser();
						  JSONObject jsonObject = new JSONObject(aft.getObjectData());
						  
						  
						  String approve = "Approve";
						  String disapprove = "Disapprove";
						  if(aft.getActionType().equals(ActionTypeConstants.MANDATE_PANEL_MAP_USERS))
						  {
							  approve = "Approve Request";
							  disapprove = "Disapprove This Request";
						  }else if(aft.getActionType().equals(ActionTypeConstants.MANDATE_PANEL_UNMAP_USERS))
						  {
							  approve = "Approve Request";
							  disapprove = "Disapprove This Request";
						  }
				%>
						  <tr>
								<td><%=(String)jsonObject.get("feeName") %></td>
								<td><%=(String)jsonObject.get("feeDescription") %></td>
								<td><%=(String)jsonObject.get("amountApplicable") %></td>
								<td><%=((String)jsonObject.get("primaryFee")).equals("1") ? "Yes" : "No" %></td>
								<td><%=(String)jsonObject.get("requestBy") %></td>
								<td>
								<%
								if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
								{
								%>
								<button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('cancel', '<%=aft.getId()%>')">Cancel Request</button>
								<%
								}else
								{
								%>
									<div class="btn-group">
									  <button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('approve', '<%=aft.getId()%>')"><%=approve %></button>
									  <button type="button" class="btn btn-danger dropdown-toggle" data-toggle="dropdown">
									    <span class="caret"></span>
									    <span class="sr-only">Toggle Dropdown</span>
									  </button>
									  <ul class="dropdown-menu" role="menu">
									  
									  	<li><a href="javascript: handleButtonAction('approve', '<%=aft.getId()%>')"><%=approve %></a></li>
										<li><a href="javascript: handleButtonAction('reject', '<%=aft.getId()%>')"><%=disapprove %></a></li>
									  </ul>
									</div>
								<%
								}
								%>
								</td>
						  	</tr>
				<%
					  }
				}else if(portletState.getFeeDescriptionApprovalType()!=null && portletState.getFeeDescriptionApprovalType().equals(FEE_DESCRIPTION_APPROVAL_TYPE.COMPANY_MAPPINGS))
				{
					  for(Iterator<ApprovalFlowTransit> iter = portletState.getAllApprovalFlowTransitListing().iterator(); iter.hasNext();)
					  {
						  ApprovalFlowTransit aft = iter.next();
						  PortalUser puReq = aft.getPortalUser();
						  JSONObject jsonObject = new JSONObject(aft.getObjectData());
						  
						  
						  String approve = "Approve";
						  String disapprove = "Disapprove";
						  if(aft.getActionType().equals(ActionTypeConstants.MANDATE_PANEL_MAP_USERS))
						  {
							  approve = "Approve & Update System Setting";
							  disapprove = "Disapprove This Request";
						  }else if(aft.getActionType().equals(ActionTypeConstants.MANDATE_PANEL_UNMAP_USERS))
						  {
							  approve = "Approve & Remove User From Panel";
							  disapprove = "Disapprove This Request";
						  }
			%>
					  
					  		<tr>
								<td><%=(String)jsonObject.get("feeName") %></td>
								<td><%=(String)jsonObject.get("amountApplicable") %></td>
								<td><%=((String)jsonObject.get("companyName")) + " - (" + ((String)jsonObject.get("companyRCNumber")) + ")"%></td>
								<td><%=(String)jsonObject.get("requestBy") %></td>
								<td>
								<%
								if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
								{
								%>
								<button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('cancel', '<%=aft.getId()%>')">Cancel Request</button>
								<%
								}else
								{
								%>
									<div class="btn-group">
									  <button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('approve', '<%=aft.getId()%>')"><%=approve %></button>
									  <button type="button" class="btn btn-danger dropdown-toggle" data-toggle="dropdown">
									    <span class="caret"></span>
									    <span class="sr-only">Toggle Dropdown</span>
									  </button>
									  <ul class="dropdown-menu" role="menu">
									  
									  	<li><a href="javascript: handleButtonAction('approve', '<%=aft.getId()%>')"><%=approve %></a></li>
										<li><a href="javascript: handleButtonAction('reject', '<%=aft.getId()%>')"><%=disapprove %></a></li>
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
				  
			  }
			  %>
			  <%  
		  }
		  %>
		</table>
		<div style="clear:both; padding-bottom:30px;">
		<button class="btn btn-danger" name="goback" style="float:left" onclick="javascript:handleButtonAction('gobacktostart', '')">Go Back to Change Approval Item Type</button></div>
		<input type="hidden" name="selectedApprovalFlow" id="selectedApprovalFlow" value="" />
		<input type="hidden" name="selectedApprovalFlowAction" id="selectedApprovalFlowAction" value="" />	
	</form>
</div>
</div>



<script type="text/javascript" charset="utf-8" src="<%=jqueryJsUrl%>"></script>

<script type="text/javascript">
$(document).ready(function() {
    $('#btable').dataTable();
} );


function handleButtonAction(action, companyId){
	
	if(action=='reject')
	{
		if(confirm("Are you sure you want to disapprove this request item? You will not be able to undo this action after disapproving!"))
		{
			document.getElementById('selectedApprovalFlow').value = companyId;
			document.getElementById('selectedApprovalFlowAction').value = action;
			document.getElementById('startRegFormId').submit();
		}
	}else if(action=='approve')
	{
		if(confirm("Are you sure you want to approve this request item? You will not be able to undo this action after approving!"))
		{
			document.getElementById('selectedApprovalFlow').value = companyId;
			document.getElementById('selectedApprovalFlowAction').value = action;
			document.getElementById('startRegFormId').submit();
		}
	}else
	{
		document.getElementById('selectedApprovalFlow').value = companyId;
		document.getElementById('selectedApprovalFlowAction').value = action;
		document.getElementById('startRegFormId').submit();
	}
}
</script>