<%@page import="com.probase.smartpay.workflow.WorkFlowPortletState"%>
<%@page import="com.probase.smartpay.workflow.WorkFlowPortletState.*"%>
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
<%@page import="smartpay.entity.Assessment"%>
<%@page import="smartpay.entity.AuthorizePanelCombination"%>
<%@page import="java.util.ArrayList"%>
<%@page import="smartpay.entity.WorkFlow"%>
<%@page import="smartpay.entity.WorkFlowAssessment"%>
<%@page import="smartpay.entity.enumerations.WorkFlowConstants"%>
<%@page import="smartpay.entity.enumerations.PanelTypeConstants"%>
<%@page import="smartpay.entity.enumerations.SmartPayConstants"%>
<%@page import="java.text.DateFormat"%>
<%@page import="com.probase.smartpay.commins.TaxBreakDownResponse"%>
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

<script type="text/javascript" charset="utf-8" src="<%=jqueryJsUrl%>"></script>
<script type="text/javascript"
	src="<%=(resourceBaseURL + "/js/jquery.validate.js")%>"></script>
<script type="text/javascript"
	src="<%=(resourceBaseURL + "/js/facebox.js")%>"></script>
<script type="text/javascript"
	src="<%=(resourceBaseURL + "/js/paging.js")%>"></script>
<link rel="stylesheet" href='<%=faceboxCssUrl%>' type="text/css" />
<link rel="stylesheet" href='<%=pagingUrl%>' type="text/css" />
<link href="<%=jqueryUICssUrl%>" rel="stylesheet" type="text/css" /><%

WorkFlowPortletState portletState = WorkFlowPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(WorkFlowPortletState.class);


%>


<portlet:actionURL var="findAssessment" name="processAction">
	<portlet:param name="action"
		value="<%=WORK_FLOW_ACTION.FIND_ASSESSMENT_BY_TOKEN.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="handleWorkFLowListing" name="processAction">
	<portlet:param name="action"
		value="<%=WORK_FLOW_ACTION.HANDLE_WORKFLOW_LISTINGS.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="handleWorkFlowOneAssessment" name="processAction">
	<portlet:param name="action"
		value="<%=WORK_FLOW_ACTION.HANDLE_WORKFLOW_FOR_ONE_ASSESSMENT.name()%>" />
</portlet:actionURL>
<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>


<div class="panel panel-info">
  	<!-- Default panel contents -->
  	<div class="panel-heading"><strong>Approval WorkFlow</strong></div>
  	<div class="panel-body">
  	</div>
	<div style="padding:10px;">
	
	
	<form action="<%=handleWorkFLowListing%>" enctype="application/x-www-form-urlencoded" method="post" id="workOnWorkFlowItems">
			<fieldset>
			<label>Provide the Token For the Selected Work-Flow (Ref Id #<%=portletState.getSelectedSearchedWorkFlow().getReferenceId() %>) </label>
			<input type="text" name="token" id="token" value="<%=portletState.getToken()==null ? "" : portletState.getToken()%>" placeholder="Type Access Token here to search">
			
			<div style="padding:10px;">&nbsp;</div>
			<button id="searchAssessment" class="btn btn-danger" onclick="javascript:handleViewOneAssessment('goBack', '<%=portletState.getSelectedSearchedWorkFlow().getId()%>')">Cancel</button>
			<button id="searchAssessment2" class="btn btn-success" onclick="javascript:document.getElementById('searchAssessment2').disabled=true; document.getElementById('loadImage').style.display='block'; handleViewOneAssessment('approve', '<%=portletState.getSelectedSearchedWorkFlow().getId()%>')">Proceed</button>
			
			<center>
				<div id="loadImage" style="display:none;">
					<img src="<%=resourceBaseURL %>/images/ajax-load1.gif">
				</div>
			</center>
					
			
			</fieldset>
	<input type="hidden" name="selectedWorkFlow" id="selectedWorkFlow" value="<%=portletState.getSelectedSearchedWorkFlow().getId()%>" />
			<input type="hidden" name="selectedWorkFlowAction" id="selectedWorkFlowAction" value="" />
		</form>
	</div>
</div>


<script type="text/javascript" charset="utf-8" src="<%=jqueryDataTableUrl%>"></script>

<script type="text/javascript">

function handleViewOneAssessment(action, id)
{
	document.getElementById('selectedWorkFlow').value = id;
	document.getElementById('selectedWorkFlowAction').value = action;
	document.getElementById('workOnWorkFlowItems').submit();
}
</script>