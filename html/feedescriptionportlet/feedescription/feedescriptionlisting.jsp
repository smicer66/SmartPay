<%@page import="com.probase.smartpay.admin.feedescriptionmanagement.FeeDescriptionPortletState"%>
<%@page import="com.probase.smartpay.admin.feedescriptionmanagement.FeeDescriptionPortletState.*"%>
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
<%@page import="smartpay.entity.FeeDescription"%>
<%@page import="com.probase.smartpay.commins.Util"%>
<%@page import="smartpay.entity.Settings"%>
<%@page import="smartpay.entity.AuthorizePanelCombination"%>
<%@page import="smartpay.entity.enumerations.SmartPayConstants"%>
<%@page import="com.probase.smartpay.commins.ComminsApplicationState"%>
<%@page import="com.probase.smartpay.commins.Util"%>
<%@page import="com.probase.smartpay.commins.Util.DETERMINE_ACCESS"%>
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
<script type="text/javascript"
	src="<%=(resourceBaseURL + jqueryDataTableUrl)%>"></script>
<link rel="stylesheet" href='<%=faceboxCssUrl%>' type="text/css" />
<link rel="stylesheet" href='<%=pagingUrl%>' type="text/css" />
<link href="<%=jqueryUICssUrl%>" rel="stylesheet" type="text/css" />
<link href="<%=jqueryDataTableCssUrl%>" rel="stylesheet" type="text/css" /><%

FeeDescriptionPortletState portletState = FeeDescriptionPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(FeeDescriptionPortletState.class);
Settings primarySettings = portletState.getPrimaryFeeSetting();
ComminsApplicationState cappState = portletState.getCas();
Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
DETERMINE_ACCESS determinAccess = new Util().determineAccessForCompanyFxns(twoStep, approvalProcess, cappState, portletState.getCompanyCRUDRights());


/*<jsp:include page="/html/annualreturnsportlet/tabs.jsp" flush="" />*/
%>


<div style="padding-left:10px; padding-right:10px">
<jsp:include page="/html/feedescriptionportlet/feedescription/tabs.jsp" flush="" />
</div>

<portlet:actionURL var="modifyFeeDescription" name="processAction">
	<portlet:param name="action"
		value="<%=FEE_DESCRIPTION.HANDLE_FEE_DESCRIPTION_LISTING.name()%>" />
</portlet:actionURL>



<%
if(determinAccess!=null && determinAccess.equals(DETERMINE_ACCESS.DISPLAY_SECOND_LEVEL_LOGIN))
{
	%>
	<jsp:include page="/html/financialamountrestrictionmanagementportlet/login_step2.jsp" flush="" />
	<%
}else if(determinAccess!=null && determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS))
{
	%>
	<form  id="feeDescriptionListingForm" action="<%=modifyFeeDescription%>" method="post" enctype="application/x-www-form-urlencoded">
<div class="panel panel-info">
  	<!-- Default panel contents -->
  	<div class="panel-heading">Transaction Fee Listings</div>
  	<div class="panel-body">
    	<p>Click on the drop-down button for any of the transaction fees to either update or delete the transaction fee</p>
  	</div>
		
	  <%
	  if(portletState.getAllFeeDescription()!=null && portletState.getAllFeeDescription().size()>0)
	  {
		  %>
		  
		  <table width="100%" class="table table-striped table-hover" id="btable">
			<thead>
			  <th>Transaction Fee</th>
			  <th>Description</th>
			  <th>Applicable amount</th>
			  <th>&nbsp;</th>
			</thead>
		  <%
		  for(Iterator<FeeDescription> iter = portletState.getAllFeeDescription().iterator(); iter.hasNext();)
		  {
			  FeeDescription fd = iter.next();
			  String style = "";
			  if(primarySettings!=null && primarySettings.getValue()!=null && fd.getId().equals(Long.valueOf(primarySettings.getValue())))
			  {
			  		style= " style='font-weight:bold'";
			  }
		  %>
		  <tr>
			<td<%=style %>><%=fd.getFeeName()==null ? "N/A" : fd.getFeeName() %>
			<%
			if(primarySettings!=null && primarySettings.getValue()!=null && fd.getId().equals(Long.valueOf(primarySettings.getValue())))
			{
			%>
			<span class="taglib-icon-help"><img alt="" aria-labelledby="dplx" onblur="Liferay.Portal.ToolTip.hide();" onfocus="Liferay.Portal.ToolTip.show(this);" onmouseover="Liferay.Portal.ToolTip.show(this);" src="/html/themes/control_panel/images/portlet/help.png" tabindex="0" aria-controls="aui_3_4_0_1_18202" id="aui_3_4_0_1_18192"><span class="aui-helper-hidden-accessible tooltip-text" id="dplx">This is the current transaction fee on the system.</span></span>
			<%
			}
			%>
			</td>
			<td<%=style %>><%=fd.getDescription()==null ? "N/A" : fd.getDescription()%></td>
			<td<%=style %>>ZMW<%=fd.getAmountApplicable()==null ? "N/A" : new Util().roundUpAmount(fd.getAmountApplicable())%></td>
			
			<td>
				
				<div class="btn-group">
					  <button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('update', '<%=fd.getId()%>')">Update Details</button>
					  <button type="button" class="btn btn-danger dropdown-toggle" data-toggle="dropdown">
					    <span class="caret"></span>
					    <span class="sr-only">Toggle Dropdown</span>
					  </button>
					  <ul class="dropdown-menu" role="menu">
					    <li><a href="javascript: handleButtonAction('update', '<%=fd.getId()%>')">Update Details</a></li>
					    <li><a href="javascript: handleButtonAction('delete', '<%=fd.getId()%>')">Delete Transaction Fee</a></li>
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
			  
			  <th>Transaction Fee</th>
			  <th>Description</th>
			  <th>Applicable amount</th>
			</thead>
		  <tr>
			<td colspan="3">There are no Fee Descriptions created on the platform yet</td>
		  </tr>
		  <%  
	  }
	  %>
	</table>
	<input type="hidden" name="selectedFeeDescription" id="selectedFeeDescription" value="" />
	<input type="hidden" name="selectedFeeDescriptionAction" id="selectedFeeDescriptionAction" value="" />	
</div>
</form>
	<%
}else if(determinAccess!=null && determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
{
	%>
	<form  id="feeDescriptionListingForm" action="<%=modifyFeeDescription%>" method="post" enctype="application/x-www-form-urlencoded">
<div class="panel panel-info">
  	<!-- Default panel contents -->
  	<div class="panel-heading">Transaction Fee Listings</div>
  	<div class="panel-body">
    	<p>Click on the drop-down button for any of the transaction fees to either update or delete the transaction fee</p>
  	</div>
		
	  <%
	  if(portletState.getAllFeeDescription()!=null && portletState.getAllFeeDescription().size()>0)
	  {
		  %>
		  
		  <table width="100%" class="table table-striped table-hover" id="btable">
			<thead>
			  <th>Transaction Fee</th>
			  <th>Description</th>
			  <th>Applicable amount</th>
			  <th>&nbsp;</th>
			</thead>
		  <%
		  for(Iterator<FeeDescription> iter = portletState.getAllFeeDescription().iterator(); iter.hasNext();)
		  {
			  FeeDescription fd = iter.next();
			  String style = "";
			  if(primarySettings!=null && primarySettings.getValue()!=null && fd.getId().equals(Long.valueOf(primarySettings.getValue())))
			  {
			  		style= " style='font-weight:bold'";
			  }
		  %>
		  <tr>
			<td<%=style %>><%=fd.getFeeName()==null ? "N/A" : fd.getFeeName() %></td>
			<td<%=style %>><%=fd.getDescription()==null ? "N/A" : fd.getDescription()%></td>
			<td<%=style %>>ZMW<%=fd.getAmountApplicable()==null ? "N/A" : new Util().roundUpAmount(fd.getAmountApplicable())%></td>
			
			<td>
				
				<div class="btn-group">
					  <button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('update', '<%=fd.getId()%>')">Update Details</button>
					  <button type="button" class="btn btn-danger dropdown-toggle" data-toggle="dropdown">
					    <span class="caret"></span>
					    <span class="sr-only">Toggle Dropdown</span>
					  </button>
					  <ul class="dropdown-menu" role="menu">
					    <li><a href="javascript: handleButtonAction('update', '<%=fd.getId()%>')">Update Details</a></li>
					    <li><a href="javascript: handleButtonAction('delete', '<%=fd.getId()%>')">Delete Transaction Fee</a></li>
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
			  
			  <th>Transaction Fee</th>
			  <th>Description</th>
			  <th>Applicable amount</th>
			</thead>
		  <tr>
			<td colspan="3">There are no Fee Descriptions created on the platform yet</td>
		  </tr>
		  <%  
	  }
	  %>
	</table>
	<input type="hidden" name="selectedFeeDescription" id="selectedFeeDescription" value="" />
	<input type="hidden" name="selectedFeeDescriptionAction" id="selectedFeeDescriptionAction" value="" />	
</div>
</form>
	<%
}else if(determinAccess!=null && determinAccess.equals(DETERMINE_ACCESS.GRANT_APPROVER_ACCESS))
{
	%>
	<form  id="feeDescriptionListingForm" action="<%=modifyFeeDescription%>" method="post" enctype="application/x-www-form-urlencoded">
<div class="panel panel-info">
  	<!-- Default panel contents -->
  	<div class="panel-heading">Transaction Fee Listings</div>
  	<div class="panel-body">
    	<p>Click on the drop-down button for any of the transaction fees to either update or delete the transaction fee</p>
  	</div>
		
	  <%
	  if(portletState.getAllFeeDescription()!=null && portletState.getAllFeeDescription().size()>0)
	  {
		  %>
		  
		  <table width="100%" class="table table-striped table-hover" id="btable">
			<thead>
			  <th>Transaction Fee</th>
			  <th>Description</th>
			  <th>Applicable amount</th>
			</thead>
		  <%
		  for(Iterator<FeeDescription> iter = portletState.getAllFeeDescription().iterator(); iter.hasNext();)
		  {
			  FeeDescription fd = iter.next();
			  String style = "";
			  if(primarySettings!=null && primarySettings.getValue()!=null && fd.getId().equals(Long.valueOf(primarySettings.getValue())))
			  {
			  		style= " style='font-weight:bold'";
			  }
		  %>
		  <tr>
			<td<%=style %>><%=fd.getFeeName()==null ? "N/A" : fd.getFeeName() %></td>
			<td<%=style %>><%=fd.getDescription()==null ? "N/A" : fd.getDescription()%></td>
			<td<%=style %>>ZMW<%=fd.getAmountApplicable()==null ? "N/A" : new Util().roundUpAmount(fd.getAmountApplicable())%></td>
			
		  </tr>
		  <%
		  }
	  }else
	  {
		  %>
		  <table width="100%" class="table" id="">
			<thead>
			  
			  <th>Transaction Fee</th>
			  <th>Description</th>
			  <th>Applicable amount</th>
			</thead>
		  <tr>
			<td colspan="3">There are no Fee Descriptions created on the platform yet</td>
		  </tr>
		  <%  
	  }
	  %>
	</table>
	<input type="hidden" name="selectedFeeDescription" id="selectedFeeDescription" value="" />
	<input type="hidden" name="selectedFeeDescriptionAction" id="selectedFeeDescriptionAction" value="" />	
</div>
</form>
	<%
}else if(determinAccess!=null && determinAccess.equals(DETERMINE_ACCESS.NO_RIGHTS_AT_ALL))
{
	%>You are not allowed to view this page<%
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
		if(confirm("Are you sure you want to delete this fee description? You will not be able to recover the fee description after deleting it!"))
		{
			document.getElementById('selectedFeeDescription').value = companyId;
			document.getElementById('selectedFeeDescriptionAction').value = action;
			document.getElementById('feeDescriptionListingForm').submit();
		}
	}else
	{
		document.getElementById('selectedFeeDescription').value = companyId;
		document.getElementById('selectedFeeDescriptionAction').value = action;
		document.getElementById('feeDescriptionListingForm').submit();
	}
}
</script>