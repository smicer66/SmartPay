<%@page import="com.probase.smartpay.approvalflow.ApprovalFlowPortlet"%>
<%@page import="com.probase.smartpay.approvalflow.ApprovalFlowPortletState.*"%>
<%@page import="com.probase.smartpay.approvalflow.ApprovalFlowPortletState"%>
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
<%@page import="smartpay.entity.FeeDescription"%>
<%@page import="smartpay.entity.enumerations.SmartPayConstants"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.enumerations.ActionTypeConstants"%>
<%@page import="smartpay.entity.Settings"%>
<%@page import="smartpay.entity.TaxType"%>
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
if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
{
	portletState.setAuthorizePanelCombinationRequestListing(portletState.getApprovalFlowPortletUtil().
			getApprovalRequestByPortalUser(portletState.getPortalUser(), AuthorizePanelCombination.class.getSimpleName()));
	portletState.setCompanyRequestListing(portletState.getApprovalFlowPortletUtil().
			getApprovalRequestByPortalUser(portletState.getPortalUser(), Company.class.getSimpleName()));
	portletState.setPortalUserRequestListing(portletState.getApprovalFlowPortletUtil().
			getApprovalRequestByPortalUser(portletState.getPortalUser(), PortalUser.class.getSimpleName()));
	portletState.setFeeDescriptionRequestListing(portletState.getApprovalFlowPortletUtil().
			getApprovalRequestByPortalUser(portletState.getPortalUser(), FeeDescription.class.getSimpleName()));
	portletState.setSettingsRequestListing(portletState.getApprovalFlowPortletUtil().
			getApprovalRequestByPortalUser(portletState.getPortalUser(), Settings.class.getSimpleName()));
	
}

%>

<jsp:include page="/html/approvalflowportlet/tabs.jsp" flush="" />


<portlet:actionURL var="proceedToStepOne" name="processAction">
	<portlet:param name="action"
		value="<%=APPROVAL_FLOW_ACTIONS.SELECT_APPROVAL_ENTITY.name()%>" />
</portlet:actionURL>


<div style="padding:10px; width:900px;">
	<h2>Approvals</h2> 	
    <div class="panel  panel-primary">
		<div class="panel-heading"><span style="color:white; font-weight: bold">To view Approval Requests created by you, select one of the item types</span></div>
		<div class="panel-body">
		    <form  id="panelcreatorform" action="<%=proceedToStepOne%>" method="post" enctype="application/x-www-form-urlencoded">
			    <fieldset>
			      <div> <strong>Item Type:</strong>
			      	<div>
			      	
			      	<%
			      	if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
			      	{
			      	%>
			      		<select name="itemType" id="companySelected" class="form-control">
						  	<option value="-1">-Select An Item Type-</option>
							<option value="<%=AuthorizePanelCombination.class.getSimpleName()%>">Authorization Mandate Panels 
							<%=portletState.getAuthorizePanelCombinationRequestListing()!=null ? "(" + 
							portletState.getAuthorizePanelCombinationRequestListing().size() + " request items)" : 
								"(0 request items)" %></option>
							<option value="<%=Company.class.getSimpleName()%>">Company <%=portletState.getCompanyRequestListing()!=null ? "(" + 
							portletState.getCompanyRequestListing().size() + " request items)" : 
								"(0 request items)" %></option>
							<option value="<%=PortalUser.class.getSimpleName()%>">Users <%=portletState.getPortalUserRequestListing()!=null ? "(" + 
							portletState.getPortalUserRequestListing().size() + " request items)" : 
								"(0 request items)" %></option>
							<option value="<%=FeeDescription.class.getSimpleName()%>">Transaction Fees 
							<%=portletState.getFeeDescriptionRequestListing()!=null ? "(" + 
							portletState.getFeeDescriptionRequestListing().size() + " request items)" : 
								"(0 request items)" %></option>
							<option value="<%=Settings.class.getSimpleName()%>">Settings <%=portletState.getAuthorizePanelCombinationRequestListing()!=null ? "(" + 
							portletState.getSettingsRequestListing().size() + " request items)" : 
								"(0 request items)" %></option>
					  	</select>
			      	<%
			      	}
			      	else
			      	{
			      	%>
			          	<select name="itemType" id="companySelected" class="form-control">
						  	<option value="-1">-Select An Item Type-</option>
							<option value="<%=AuthorizePanelCombination.class.getSimpleName()%>">Authorization Mandate Panels</option>
							<option value="<%=Company.class.getSimpleName()%>">Company</option>
							<option value="<%=PortalUser.class.getSimpleName()%>">Users</option>
							<option value="<%=FeeDescription.class.getSimpleName()%>">Transaction Fees</option>
							<option value="<%=Settings.class.getSimpleName()%>">Settings</option>
							<option value="<%=TaxType.class.getSimpleName()%>">Tax Type</option>
					  	</select>
					<%
			      	}
					%>
			        </div>
			      </div>
				  <div style="padding-top:10px">
			        <button name="itemButton" id="createPanelName" class="btn btn-success">Next</button>
			      </div>
			    </fieldset>
		    </form>
		</div>
	</div>
</div>