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
<%@page import="java.util.ArrayList"%>
<%@page import="smartpay.entity.WorkFlow"%>
<%@page import="smartpay.entity.WorkFlowAssessment"%>
<%@page import="smartpay.entity.enumerations.WorkFlowConstants"%>
<%@page import="smartpay.entity.enumerations.SmartPayConstants"%>
<%@page import="smartpay.entity.enumerations.PanelTypeConstants"%>
<%@page import="smartpay.entity.AuthorizePanelCombination"%>
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
WorkFlow workflow = (WorkFlow)portletState.getWorkFlowPortletUtil().getEntityObjectById(WorkFlow.class, Long.valueOf(portletState.getSelectedWorkFlow()));
WorkFlowAssessment wfa = (WorkFlowAssessment)portletState.getWorkFlowPortletUtil().getWorkFlowAssessmentsByWorkFlow(workflow);
AuthorizePanelCombination apcCurrentUser = portletState.getWorkFlowPortletUtil().getAuthorizedPanelCombinationForAPortalUser(
					portletState.getPortalUser(), wfa.getAssessment().getAmount(), PanelTypeConstants.AUTHORIZE_PANEL_TYPE_AUTHORISER);

AuthorizePanelCombination apcCollection = null;
if(apcCurrentUser!=null)
{
	//apcCollection = portletState.getWorkFlowPortletUtil().getAuthorizedPanelCombinationForSubsequentAuthorizers(SmartPayConstants.AUTHORIZE_PANEL_COMBINATION_STATUS_ACTIVE, wfa.getAssessment().getAmount(), apcCurrentUser.getPosition());
}
					
			

%>


<portlet:actionURL var="handleWorkFlowOneAssessment" name="processAction">
	<portlet:param name="action"
		value="<%=WORK_FLOW_ACTION.HANDLE_WORKFLOW_FOR_ONE_ASSESSMENT.name()%>" />
</portlet:actionURL>
<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>


	<%
	if(portletState.getTaxBreakDownList()!=null && portletState.getTaxBreakDownList().size()>0)
	{
	%>
	<form action="<%=handleWorkFlowOneAssessment%>" method="post" enctype="application/x-www-form-urlencoded" id="transactionViewForm">
	<div class="panel panel-info">
			<!-- Default panel contents -->
			<div class="panel-heading"><strong>View WorkFlow Item - Assessment</strong></div>
			<div class="panel-body">
				<p class="panel-warning">Note the amount shown here may not match the amount listed previously in the workflow items. In such cases, this is due to an increase in amount as a result of the accumulating interest</p>
			</div>
			
			<div style="padding:10px;">


			<%
			for(Iterator<HashMap> iterHM = portletState.getTaxBreakDownList().iterator(); iterHM.hasNext();)
			{
				int c = 0;
				HashMap<String, ArrayList<TaxBreakDownResponse>> hashMap = iterHM.next();
				for (String key : hashMap.keySet()) 
				{
			%>
			<div class="panel panel-info">
				<!-- Default panel contents -->
				<div class="panel-heading"><strong>Assessment Registration Number: <%=key %></strong></div>
				<div style="padding:10px;">
				<table width="100%" class="table table-striped table-hover" id="">				  
				    <thead>
					  	<th>Tax</th>
					  	<th>Registration Number</th>
					  	<th>Amount(ZMW)</th>
					</thead>
				  <tbody>
			  		<%
						
							
								ArrayList<TaxBreakDownResponse> tbdRList = hashMap.get(key);
								if(tbdRList!=null)
								{
									for(Iterator<TaxBreakDownResponse> iterBd = tbdRList.iterator(); iterBd.hasNext();)
									{
										TaxBreakDownResponse taxBreakDownResponse = iterBd.next();
									%>
										<tr>
											<td><%=taxBreakDownResponse.getProductCode()%></td>
											<td><%=key%></td>
											<td><%=taxBreakDownResponse.getProductName()%></td>
										</tr>
									<%
									}
								}
							
					%>
				  </tbody>
				</table>
				</div>
			</div>
			<%
				}
			}
			%>
			</div>
	</div>
	<input type="hidden" name="selectedWorkFlowAction" id="selectedWorkFlowAction" value="" />
	<input type="hidden" name="selectedWorkFlow" id="selectedWorkFlow" value="<%=portletState.getSelectedWorkFlow()%>" />
	<div style="padding:10px">
	<button name="backAction" class="btn btn-primary" id="backAction" onclick="javascript:handleViewOneAssessment('gobacktoworkflow', '<%=portletState.getSelectedWorkFlow()%>')" >Go Back</button></div>
		<%
		
		//if(workflow!=null && workflow.getWorkFlowReceipientId()!=null && workflow.getWorkFlowReceipientId().equals(portletState.getPortalUser().getId())) 
		//{
		%>
		<button name="approveAction" class="btn btn-danger" id="approveAction" onclick="javascript:handleViewOneAssessment('reject', '<%=portletState.getSelectedWorkFlow()%>')" >Disapprove</button>	
			<%
			if(apcCollection==null)
			{
			%>
			<button name="approveAction" class="btn btn-success" id="approveAction" onclick="javascript:handleViewOneAssessment('approve', '<%=portletState.getSelectedWorkFlow()%>')" >Approve</button>	
			<%
			}else
			{
			%>
			<button name="approveAction" class="btn btn-success" id="approveAction" onclick="javascript:handleViewOneAssessment('approve', '<%=portletState.getSelectedWorkFlow()%>')" >Approve & Complete Payment</button>	
			<%
			}
			%>
		<%
		//}
		%>
</form>
	<%
	}
	%>


<script type="text/javascript" charset="utf-8" src="<%=jqueryDataTableUrl%>"></script>

<script type="text/javascript">

function handleViewOneAssessment(action, id)
{
	document.getElementById('selectedWorkFlow').value = id;
	document.getElementById('selectedWorkFlowAction').value = action;
	document.getElementById('transactionViewForm').submit();
}
</script>