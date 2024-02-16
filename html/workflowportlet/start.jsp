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
<%@page import="smartpay.entity.AuthorizePanel"%>
<%@page import="smartpay.entity.Assessment"%>
<%@page import="smartpay.entity.DomTax"%>
<%@page import="smartpay.entity.AuthorizePanelCombination"%>
<%@page import="java.util.ArrayList"%>
<%@page import="smartpay.entity.WorkFlow"%>
<%@page import="smartpay.entity.WorkFlowAssessment"%>
<%@page import="com.probase.smartpay.commins.Util"%>
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
  		<%
  		if(portletState.getWorkFlowList()!=null && portletState.getWorkFlowList().size()>0 && portletState.getWorkFlowAssessmentList()!=null && portletState.getWorkFlowAssessmentList().size()>0)
		{
  		%>
    	<p>Click on a workflow items associated action button to carry out an action on the workflow item</p>
    	<%
		}else
		{
    	%>
    	You do not have any work-flow items to awaiting your action at the moment. You may check back later!
    	<%
		}
    	%>
  	</div>
	<div style="padding:10px;">
		
		
		<form action="<%=handleWorkFLowListing%>" enctype="application/x-www-form-urlencoded" method="post" id="workOnWorkFlowItems">
					<%
					int c= 0;
					if(portletState.getWorkFlowList()!=null && portletState.getWorkFlowList().size()>0 && portletState.getWorkFlowAssessmentList()!=null && portletState.getWorkFlowAssessmentList().size()>0)
					{	
						
						for(Iterator<WorkFlow> iter1 = portletState.getWorkFlowList().iterator(); iter1.hasNext();)
						{
							WorkFlow wf = iter1.next();
							String bgColor = "#cccccc";
							String borderColor = "#ffffff";
							Long recId = null;
							Double totalAmount = 0.00;
							boolean status2 = false;
							
							
							
							%>
								<table width="100%" border="0" class="table table-striped table-hover">
									<thead>
										<th>WorkFlow Ref Id</th>
										<th>Ass/DomTax Reg No</th>
										<th>Amount Payable</th>
										<th>Interest</th>
										<th>Last Worked on by</th>
										<th>Forwarded to Mandate Panel Members</th>
										<th>Mandate Panel Level</th>
										<th>Status</th>
									</thead>
									<tbody>
								
								<%
								for(Iterator<WorkFlowAssessment> iter = portletState.getWorkFlowAssessmentList().iterator(); iter.hasNext();)
								{
									WorkFlowAssessment workflowAssessment = iter.next();
									if(wf.getId().equals(workflowAssessment.getWorkFlow().getId()))
									{
										AuthorizePanel ap = (AuthorizePanel)portletState.getWorkFlowPortletUtil().
												getEntityObjectById(AuthorizePanel.class, wf.getWorkFlowReceipientPanelId());
										if(c%2==0)
										{
											bgColor = "#cccccc";
											borderColor = "#ffffff";
										}else
										{
											bgColor = "#ffffff";
											borderColor = "#ffffff";
										}
										WorkFlow workflow = workflowAssessment.getWorkFlow();
										Assessment asssessment = workflowAssessment.getAssessment();
										DomTax domTax = workflowAssessment.getDomTax();
										recId = workflow.getWorkFlowReceipientPanelId();
										if(asssessment!=null)
										{
											totalAmount = totalAmount + asssessment.getAmount();
										}else
										{
											totalAmount = totalAmount + domTax.getAmountPayable();
										}
										
										
			
										PortalUser pu = (PortalUser)portletState.getWorkFlowPortletUtil().getEntityObjectById(PortalUser.class, workflowAssessment.getWorkFlow().getWorkFlowInitiatorId());
										PortalUser pu1 = null;
										String status = "";
										if(workflowAssessment.getStatus().equals(WorkFlowConstants.WORKFLOW_STATUS_CREATED))
										{
											status2 = true;
											status = "<span style='background-color: #ff6600; font-weight: bold; padding-3px;'>In Progress</span>";
													
										}else if(workflowAssessment.getStatus().equals(WorkFlowConstants.WORKFLOW_STATUS_APPROVED))
										{
											
											status = "<span style='background-color: #90EE90; font-weight: bold; padding-3px;'>Approved & Paid</span>";
										}else if(workflowAssessment.getStatus().equals(WorkFlowConstants.WORKFLOW_STATUS_FORWARDED))
										{
											status2 = true;
											status = "<span style='background-color: #ff6600; font-weight: bold; padding-3px;'>In Progress</span>";
											
										}
								%>
									<tr>
										<td style="height:30px; padding:5px; background-color:<%=bgColor%>; border-top:<%=borderColor%> 1px solid"><%=wf.getReferenceId()%></td>
										<td style="height:30px; padding:5px; background-color:<%=bgColor%>; border-top:<%=borderColor%> 1px solid"><%=asssessment!=null ? asssessment.getRegistrationNumber() : domTax.getPaymentRegNo()%></td>
										<td style="height:30px; padding:5px; background-color:<%=bgColor%>; border-top:<%=borderColor%> 1px solid">ZMW <%=new Util().roundUpAmount(asssessment!=null ? asssessment.getAmount() : domTax.getAmountPayable())%></td>
										<td style="height:30px; padding:5px; background-color:<%=bgColor%>; border-top:<%=borderColor%> 1px solid">ZMW <%=asssessment!=null ? (asssessment.getInterestAmount()==null ? 0.00 : new Util().roundUpAmount(asssessment.getInterestAmount())) : "0.00"%></td>
										<td style="height:30px; padding:5px; background-color:<%=bgColor%>; border-top:<%=borderColor%> 1px solid"><%=pu.getFirstName() + " " + pu.getLastName()%></td>
										<td style="height:30px; padding:5px; background-color:<%=bgColor%>; border-top:<%=borderColor%> 1px solid"><%=ap.getPanelName()%></td>
										<td style="height:30px; padding:5px; background-color:<%=bgColor%>; border-top:<%=borderColor%> 1px solid"><%=wf.getWorkFlowReceipientPositionId()%></td>
										<td style="height:30px; padding:5px; background-color:<%=bgColor%>; border-top:<%=borderColor%> 1px solid"><%=status%></td>
										
									</tr>
								<%
									}
								}
							%>
								</tbody>
							</table>
							<%
							
								Collection<AuthorizePanelCombination> apcCollection = null;
								if(apcCollection!=null)
								{
									//AuthorizePanelCombination apcCurrentUser = portletState.getWorkFlowPortletUtil().getAuthorizedPanelCombinationForAPortalUser(
										//	portletState.getPortalUser(), totalAmount, PanelTypeConstants.AUTHORIZE_PANEL_TYPE_AUTHORISER);
									
									//apcCollection = portletState.getWorkFlowPortletUtil().getAuthorizedPanelCombinationForSubsequentAuthorizers(
										//	SmartPayConstants.AUTHORIZE_PANEL_COMBINATION_STATUS_ACTIVE, totalAmount, apcCurrentUser.getPosition());
									
									apcCollection = portletState.getWorkFlowPortletUtil().getAPCForSubsequentPortalUsers(
											SmartPayConstants.AUTHORIZE_PANEL_COMBINATION_STATUS_ACTIVE, wf);
								}
							%>
							<div>
								<div style="clear:both">&nbsp;</div>
										<%
										if(apcCollection!=null && status2==true)
										{
										%>
											<button type="button" class="btn btn-success" onclick="javascript:handleViewOneAssessment('approveNow', '<%=wf.getId()%>')">Approve & Forward WorkFlow Item</button>
											<button type="button" class="btn btn-danger" onclick="javascript:document.getElementById('showreason<%=wf.getId() %>').style.display='block'; return false;">Disapprove WorkFlow Item</button>
										<%
										}if(apcCollection==null && status2==true)
										{
										%>
											<button type="button" class="btn btn-success" onclick="javascript:handleViewOneAssessment('approveNow', '<%=wf.getId()%>')">Approve WorkFlow Item & Complete Payment</button>
											<button type="button" class="btn btn-danger" onclick="javascript:document.getElementById('showreason<%=wf.getId() %>').style.display='block'; return false;">Disapprove WorkFlow Item</button>
										<%
										}
									  	%>
							</div>
							<div id="showreason<%=wf.getId() %>" style="display:none; padding-top:5px;">
								<div style="font-weight:bold">Provide a reason for disapproving</div>
								<textarea name="reason<%=wf.getId()%>" style="width:200px; height: 100px;"><%=portletState.getReason()!=null ? portletState.getReason() : "" %></textarea>
								<div style="clear:both; padding-top:10px">
									<button type="button" class="btn btn-danger" onclick="javascript:handleViewOneAssessment('reject', '<%=wf.getId()%>')">Proceed to Disapprove</button>
								</div>
							</div>	
							<div style="clear:both; padding:10px;">&nbsp;</div>
					<%
						}
					}
					%>
					
					
					
					
					
					<div style="float:left; text-align: left; ">
								<%
								if(portletState.getBalanceInquiry()!=null)
								{
								%>
									<div class="panel panel-info" style="clear:both">
									   <!-- Default panel contents -->
									   <div class="panel-heading"><strong>Current Bank Balance</strong></div>
									   <div style="float:left; width:150px; padding:5px">Account Number:</div>
									   <div style="float:left;  padding:5px"><%=portletState.getBalanceInquiry().getAccountNumber()%></div>
									   
									   <div style="clear:both; float:left; width:150px; padding:5px">Type of Account:</div>
									   <di style="float:left; padding:5px"><%=portletState.getBalanceInquiry().getType()%></div>
									   
									   <div style="clear:both; float:left; width:150px; padding:5px">Account Balance:</div>
									   <div style="float:left; padding:5px"><strong>ZMW <%=new Util().roundUpAmount(portletState.getBalanceInquiry().getAvailableBalance())%></strong></div>
								   
										 
								   	</div>
							     <%
							      }
							      %>
					</div>
					
					<div style="float:right">
						<%
						if(portletState.getBalanceInquiry()!=null && portletState.getBalanceInquiry().getAvailableBalance()>0.00)
						{
						%>
							<div class="btn-group">
								<%
								if(portletState.getBalanceInquiry()!=null)
								{
								%>
									<button type="button" class="btn btn-success" onclick="javascript:return false;">Options</button>
								<%
								}
								%>
								<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
								<ul class="dropdown-menu" role="menu">
									<%
									if(portletState.getBalanceInquiry()!=null)
									{
									%>
										<li><a href="javascript:handleButtonAction('getBalance1', '')">Refresh My Company Bank Account Balance</a></li>
									<%
									}
									%>
									
								</ul>
							</div>
						<%
						}else
						{
						%>
						<button type="button" class="btn btn-success" onclick="javascript:handleViewOneAssessment('getBalance', '')">View Your Company Bank Account Balance</button>
						<%
						}
						%>
					</div>
					
					
			<input type="hidden" name="selectedWorkFlow" id="selectedWorkFlow" value="<%=portletState.getSelectedWorkFlow()%>" />
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