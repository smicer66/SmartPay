<%@page import="smartpay.entity.enumerations.CompanyTypeConstants"%>
<%@page import="com.probase.smartpay.admin.taxassessmentmanagement.TaxAssessmentManagementPortletState"%>
<%@page import="com.probase.smartpay.admin.taxassessmentmanagement.TaxAssessmentManagementPortletState.*"%>
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
<%@page import="smartpay.entity.Ports"%>
<%@page import="smartpay.entity.Company"%>
<%@page import="smartpay.entity.Assessment"%>
<%@page import="smartpay.entity.WorkFlowAssessment"%>
<%@page import="smartpay.entity.enumerations.WorkFlowConstants"%>
<%@page import="java.util.ArrayList"%>
<%@page import="smartpay.entity.TpinInfo"%>
<%@page import="java.text.DateFormat"%>
<%@page import="com.probase.smartpay.commins.Util"%>
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
	String jqueryDataTableUrl = resourceBaseURL + "/js/jquery.dataTables.js";

		
	String jqueryUICssUrl = resourceBaseURL + "/css/jquery-ui.min.css";
	
	String jqueryJsUrl = resourceBaseURL + "/js/jquery-1.10.2.min.js";
	String jqueryUIJsUrl = resourceBaseURL + "/js/jquery-ui.min.js";
%>


<link href="<%=jqueryDataTableCssUrl %>" rel="stylesheet" type="text/css" />
<%
TaxAssessmentManagementPortletState portletState = TaxAssessmentManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(TaxAssessmentManagementPortletState.class);
TpinInfo ctpinInfo = portletState.getTaxAssessmentManagementPortletUtil().getTPINInfoByCompany(portletState.getPortalUser().getCompany().getId());
%>


<portlet:actionURL var="simpleGetAssUrl" name="processAction">
	<portlet:param name="action"
		value="<%=TAX_ASSESSMENT_ACTION.VIEW_A_TAX_ASSESSMENT_SIMPLE.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="advGetAssUrl" name="processAction">
	<portlet:param name="action"
		value="<%=TAX_ASSESSMENT_ACTION.VIEW_A_TAX_ASSESSMENT_ADV.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="handleAssessmentListing" name="processAction">
	<portlet:param name="action"
		value="<%=TAX_ASSESSMENT_ACTION.HANDLE_ASSESSMENT_LISTING.name()%>" />
</portlet:actionURL>
<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>


<div style="padding:10px;">

<%
String display = "none";
String display1 = "block";
if(portletState.getSelectedClientTPID()!=null || portletState.getSelectedPortId()!=null || portletState.getSelectedYear()!=null)
{
	display = "block";
	display1 = "none";
}
%>



<%
boolean proceed1=true;	//Advanced Search view
boolean proceed2=true;	//Simple Search View
boolean proceed3=true;	//Toggle Link between Advanced and simple views
if(portletState.getPortalUser().getCompany()!=null && portletState.getPortalUser().getCompany().getCompanyType().getValue().equalsIgnoreCase(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY.getValue()))
{
	if(portletState.getPortalUser().getCompany()!=null && portletState.getPortalUser().getCompany().getClearingAgent().equals(Boolean.TRUE))
	{
		proceed1 = true;
		proceed2 = true;
		proceed3 = true;
	}else if(portletState.getPortalUser().getCompany()!=null && portletState.getPortalUser().getCompany().getClearingAgent().equals(Boolean.FALSE))
	{
		proceed1 = false;
		proceed2 = true;
		proceed3 = false;
	}
}else if(portletState.getPortalUser().getCompany().getCompanyType().getValue().equalsIgnoreCase(CompanyTypeConstants.COMPANY_TYPE_RETAIL_COMPANY.getValue()))
{
	proceed1 = false;
	proceed2 = true;
	proceed3 = true;
}
%>


    <div style="padding:5px">
    	<div style=" clear:both; float:left; font-weight:bold; padding:5px; width:150px;">Company Name:</div>
        <div style="float:left; padding:5px; width:150px;"><%=portletState.getPortalUser().getCompany().getCompanyName()%></div>
        
        <!-- <div style=" clear:both; float:left; font-weight:bold;padding:5px; width:150px;">Company Reg No:</div>
        <div style="float:left; padding:5px; width:150px;"><%=portletState.getPortalUser().getCompany().getCompanyRCNumber()%></div>-->
        
        <div style=" clear:both; float:left; font-weight:bold;padding:5px; width:150px;">Tax Payer Id No:</div>
        <div style="float:left; padding:5px; width:150px;"><%=ctpinInfo.getTpin()%></div>
    </div>
    
    <div style="clear:both">&nbsp;</div>


<div class="panel panel-info" id="advancedSearch" style="display:<%=display%>">
  	<!-- Default panel contents -->
  	<div class="panel-heading"><strong>Search For Assessments</strong></div>
    
</div>

<%
if(proceed1)
{
%>

<div class="panel panel-info" id="advancedSearch" style="display:<%=display%>">
  	<!-- Default panel contents -->
  	<div class="panel-heading"><strong>Search For Assessments</strong></div>
		<form  id="advgetassfm" action="<%=advGetAssUrl%>" method="post" enctype="application/x-www-form-urlencoded">
				
			<div style="text-align:center; padding: 5px;">
			<label>Year: <select name="assessmentYear">
				<%
				Calendar cal = Calendar.getInstance();
				int year = cal.get(Calendar.YEAR);
				for(int c=year; c>(year -10); c--)
				{
					String select="";
					if(portletState.getSelectedYear()!=null && portletState.getSelectedYear().equals(Integer.toString(c)))
					{
						select = "selected = 'selected'";
					}
				%>
					<option <%=select%> value="<%=c%>"><%=c%></option>
				<%
				}
				%>
			</select></label>
			<br />
			<label>Port: <select name="portOfEntry">
				<option value="-1">-Select A Port</option>
				<%
				if(portletState.getAllPortListing()!=null && portletState.getAllPortListing().size()>0)
				{
				for(Iterator<Ports> iterPort = portletState.getAllPortListing().iterator(); iterPort.hasNext();)
				{
					Ports port = iterPort.next();
					String select="";
					if(portletState.getSelectedPortId()!=null && portletState.getSelectedPortId().equals(Long.toString(port.getId())))
					{
						select = "selected = 'selected'";
					}
				%>
					<option <%=select%> value="<%=port.getId()%>"><%=port.getFullName()%></option>
				<%
				}
				}
				%>
			</select></label>
			<%
			if(portletState.getPortalUser().getCompany().getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY))
			{
			%>
			<br />
			<label>Client TPIN: <input type="text" value="<%=portletState.getSelectedClientTPID()==null ? "" : portletState.getSelectedClientTPID()%>" class="form-control" name="clientTaxPayerID"></label>
			<%
			}
			%>
			<br />
			<button id="viewAssessmentsAdv" class="btn btn-success">SEARCH FOR ASSESSMENTS</button><br />
			  <a href="#" onclick="javascript:toggleView('simpleSearch', 'advancedSearch'); return false;" style="cursor:pointer">Simple Search For Assessments</a></div>
			 <input type="hidden" name="platformFlag" value="1" id="platformFlag" />
		</form>
</div>

<%
}
%>		


<%
if(proceed2)
{
%>
		
<div class="panel panel-info" id="simpleSearch" style="display:<%=display1%>">
  	<!-- Default panel contents -->
  	<div class="panel-heading"><strong>Search For Assessments</strong></div>
  	<form  id="simplegetassfm" action="<%=simpleGetAssUrl%>" method="post" enctype="application/x-www-form-urlencoded">
		<div style="text-align:center; padding:5px;">
		<button id="viewAssessmentsSimple" class="btn btn-success"
		 onclick="javascript:document.getElementById('viewAssessmentsSimple').disabled=true; document.getElementById('loadImage').style.display='block'; document.getElementById('simplegetassfm').submit()" >VIEW ASSESSMENTS</button><br />
		<input type="hidden" name="platformFlag" value="2" id="platformFlag" />
		<%
		if(proceed3)
		{
		%>
		<!-- <a  title="Search by year and port" href="#" onclick="javascript:toggleView('advancedSearch', 'simpleSearch'); return false;" style="cursor:pointer">Advanced Search</a></div>-->
		<%
		}
		%>
		</form>
			
</div>


<center>
	<div id="loadImage" style="display:none;">
		<img src="<%=resourceBaseURL %>/images/ajax-load1.gif">
	</div>
</center>



<%
}
%>



	<%
	  if(portletState.getAllAssessmentListing()!=null)
	  {
		  if(portletState.getAllAssessmentListing().size()>0)
		  {
	  %>
<div class="panel panel-info">
  	<!-- Default panel contents -->
  	<div class="panel-heading"><strong>Assessment Listing</strong></div>
  	<div class="panel-body">
    	<p>Click on an assessment to carry out an action on the assessment. To view actions that can be carried out, click on the dropdown button</p>
  	</div>
		<form  id="taxassessmentForm" action="<%=handleAssessmentListing%>" method="post" enctype="application/x-www-form-urlencoded">
			
		<div style="font-size:13px; padding:5px;">
		  <%
		  Double totalAmount = 0.0;
		  if(portletState.getAllAssessmentListing()!=null && portletState.getAllAssessmentListing().size()>0)
		  {
		  %>
			  
			  <table width="100%" id="btable">
				<thead>
		  			<th valign="middle" align="center"><input type="checkbox" name="clickSelectAll" id="clickSelectAll" onclick="javascript:handleSelectAll(); changeTotalAmount()" /></th>
				  	<th valign="middle" align="left">Reg No</th>
				  	<th valign="middle" align="left">Assessment<br>No</th>
				  	<th valign="middle" align="left">Year</th>
				  	<th valign="middle" align="left">Port of Entry</th>
				  	<th valign="middle" align="left">Declarant<br>Code</th>
				  	<th valign="middle" align="left">TPIN</th>
				  	<th valign="middle" align="left">Interests<br>Exist?</th>
				  	<th valign="middle" align="right" style="text-align:right">Interest<br>Amount</th>
				  	<th valign="middle" align="right" style="text-align:right">Amount (ZMW)</th>
				  	<th valign="middle" align="left">Status</th>
				</thead>
				<tbody>
			  <%
			  int c=0;
			  
			  for(Iterator<Assessment> iter = portletState.getAllAssessmentListing().iterator(); iter.hasNext();)
			  {
				  Assessment taxAssessment = iter.next();
				  
				  String bgColor = "#ffffff";
				  if(c%2==0)
				  {
				  	bgColor = "#cccccc";
				  }
				  boolean show = true;
				  boolean show1 = true;
				  String paidForString = "Active";
				  
				  if(portletState.getAllWorkFlowAssessments()!=null && portletState.getAllWorkFlowAssessments().size()>0)
				  {
					  for(Iterator<Assessment> iterAss = portletState.getAllWorkFlowAssessments().iterator(); iterAss.hasNext();)
					  {
						  Assessment temp = iterAss.next();
						  if(temp!=null && taxAssessment!=null )
						  {
							  Collection<WorkFlowAssessment> wfaList1= portletState.getTaxAssessmentManagementPortletUtil().getWorkFlowAssessmentByAssessment(temp);
							  if(temp.getRegistrationNumber().equals(taxAssessment.getRegistrationNumber()) && 
									  temp.getAssessmentYear().equals(taxAssessment.getAssessmentYear()) && 
									  temp.getPorts().getId().equals(taxAssessment.getPorts().getId()))
							  {
								  if(temp.getPaidFor().equals(Boolean.TRUE))
								  {
									  show = false;
									  show1 = false;
									  paidForString = "<span style='background-color:#ff6600; padding:2px;'>Paid For</span>";
								  }else
								  {
									  show = false;
									  paidForString = "<span style='background-color:#5cb85c; color: #ffffff; padding:2px;'>Moved to Approval Workflow</span>";
								  }
							  }
						  }
					  }
				  }
				  
				  
				  
				  if(show==false && taxAssessment!=null && show1==true)
				  {
					  
					  	%>
				  <tr>
				  	<td align="center">&nbsp;</td>
					<td><%=taxAssessment.getRegistrationNumber()==null ? "N/A" : taxAssessment.getRegistrationNumber() %></td>
					<td><%=taxAssessment.getRegistrationNumber()==null ? "N/A" : taxAssessment.getRegistrationNumber() %></td>
					<td><%=taxAssessment.getAssessmentYear()==null ? "N/A" : taxAssessment.getAssessmentYear() %></td>
					<td><%=taxAssessment.getPorts()==null ? "N/A" : taxAssessment.getPorts().getFullName() %></td>
					<!-- <td><%=taxAssessment.getTpinInfo().getTpin()==null ? "N/A" : taxAssessment.getTpinInfo().getTpin()%></td>-->
					<td><%=taxAssessment.getDeclarantTpin()==null ? "NA" : taxAssessment.getDeclarantTpin() %></td>
					<td><%=taxAssessment.getClientTpin()==null ? "N/A" : taxAssessment.getClientTpin() %></td>
					<td><%=taxAssessment.getInterest()==null ? "N/A" : (taxAssessment.getInterest().equals(Boolean.TRUE) ? "Yes" : "No") %></td>
					<td style="text-align:right"><%=taxAssessment.getInterest()==null ? "0.00" : (taxAssessment.getInterest().equals(Boolean.TRUE) ? new Util().roundUpAmount(taxAssessment.getInterestAmount()) : "0.00") %></td>
					<td style="text-align:right"><%=taxAssessment.getAmount()==null ? "N/A" : new Util().roundUpAmount(taxAssessment.getAmount()) %></td>
					
					<td><%=paidForString%></td>
					
				  </tr>
				  <%
				  }else if(show==true && taxAssessment!=null)
				  {
			  %>
				  <tr>
					<td><input class="centerCell" onchange="javascript:changeTotalAmount()" type="checkbox" name="selectAllCheckbox" id="selectAllCheckbox" value="<%=taxAssessment.getRegistrationNumber() + "/" + taxAssessment.getAssessmentYear() + "/" + taxAssessment.getPorts().getId()%>"  /></td>
					<td><%=taxAssessment.getRegistrationNumber()==null ? "N/A" : taxAssessment.getRegistrationNumber() %></td>
					<td><%=taxAssessment.getRegistrationNumber()==null ? "N/A" : taxAssessment.getRegistrationNumber() %></td>
					<td><%=taxAssessment.getAssessmentYear()==null ? "N/A" : taxAssessment.getAssessmentYear() %></td>
					<td><%=taxAssessment.getPorts()==null ? "N/A" : taxAssessment.getPorts().getFullName() %></td>
					<td><%=taxAssessment.getDeclarantTpin()==null ? "NA" : taxAssessment.getDeclarantTpin() %></td>
					<td><%=taxAssessment.getClientTpin()==null ? "N/A" : taxAssessment.getClientTpin() %></td>
					<td><%=taxAssessment.getInterest()==null ? "N/A" : (taxAssessment.getInterest().equals(Boolean.TRUE) ? "Yes" : "No") %></td>
					<td style="text-align:right"><%=taxAssessment.getInterest()==null ? "0.00" : (taxAssessment.getInterest().equals(Boolean.TRUE) ? new Util().roundUpAmount(taxAssessment.getInterestAmount()) : "0.00") %></td>
					<td style="text-align:right"><%=taxAssessment.getAmount()==null ? "N/A" :  new Util().roundUpAmount(taxAssessment.getAmount()) %></td>
					<td>Active
					<input type="hidden" id="amtChange<%=taxAssessment.getRegistrationNumber() + "_" + taxAssessment.getAssessmentYear() + "_" + taxAssessment.getPorts().getId()%>" value="<%=taxAssessment.getAmount()==null ? 0.00 : taxAssessment.getAmount()%>">
					<input type="hidden" id="intChange<%=taxAssessment.getRegistrationNumber() + "_" + taxAssessment.getAssessmentYear() + "_" + taxAssessment.getPorts().getId()%>" value="<%=taxAssessment.getInterestAmount()==null ? 0.00 : taxAssessment.getInterestAmount()%>">
					</td>
				  </tr>
				  
				  
			  <%
					}
				  
			  }
			  %>
			  </tbody>
			</table>
			<input type="hidden" name="selectedAssessmentsClicked" id="SelectedAssessmentsClicked" value="<%=portletState.getSelectedAssessmentsClicked() %>" />
			
			  <div id="totalAmountDiv" style="clear:both; text-align: right; padding-bottom:10px; padding-right:10px;">&nbsp;
			  </div>
			  <div style="clear:both; text-align: right; padding-bottom:40px; padding-right:10px;">
					
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
									<button type="button" class="btn btn-success" onclick="javascript:return false;" id="optbtn">
				<div id="loadImage" style="display:none; float:left">
					<img src="<%=resourceBaseURL %>/images/ajax-load1.gif">
				</div>
				<div id="optionsId" style="float:left">Options</div></button>
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
									<%
									if(portletState.getBalanceInquiry()!=null && portletState.getBalanceInquiry().getAvailableBalance()>0.00)
									{
										if(portletState.getPortalUser().getCompany().getMandatePanelsOn().equals(Boolean.TRUE))
										{
											
									%>
											<li><a href="javascript: handleButtonAction('initiatePayment', '<%=portletState.getBalanceInquiry().getAvailableBalance()%>')">Add Selected Assessments to Workflow Tray</a></li>
									<%
										}else
										{
									%>
											<li><a href="javascript: document.getElementById('optionsId').innerHTML='Processing Payment'; javascript:document.getElementById('optbtn').disabled=true; handleButtonAction('pay', '<%=portletState.getBalanceInquiry().getAvailableBalance()%>')">Proceed to Pay for Selected Assessments</a></li>
									<%	
										}
									%>
										<li><a href="javascript: handleButtonAction('breakdown', '')">View Breakdown of Tax(es)</a></li>
									<%
									}
									%>
								</ul>
							</div>
						<%
						}else
						{
						%>
						<button type="button" class="btn btn-success" onclick="javascript:handleButtonAction('getBalance', '')">First View Your Company Bank Account Balance</button>
						<%
						}
						%>
					</div>	
					
					  
				</div>
				
				
				
			
			  <%
		  }else
		  {
			  %>
			  <table width="100%" id="btable1">				  
			    <thead>
				  	<th>Registration Number</th>
				  	<th>Assessment Number</th>
				  	<th>Assessment Year</th>
				  	<th>Port</th>
				  	<th>Declarant Code</th>
				  	<th>TPIN</th>
				  	<th>Interests Accumulated</th>
				  	<th>Date Registered</th>
				  	<th>Amount(ZMW)</th>
				</thead>
			  </table>
			  <div>There are no outstanding tax assessments</div>
			  <%  
		  }
		  %>
			
		
		<input type="hidden" name="selectedAssessment" id="selectedAssessment" value="" />
		<input type="hidden" name="selectedAssessmentAction" id="selectedAssessmentAction" value="" />	
		</form>
</div>
		
				
		</div>
	<%
		  }
	  }
	%>
</div>


<script type="text/javascript" charset="utf-8" src="<%=jqueryJsUrl%>"></script>
<script type="text/javascript" charset="utf-8" src="<%=jqueryDataTableUrl%>"></script>


<script type="text/javascript">


$(document).ready(function() {
    $('#btable').dataTable();
} );

$(document).ready(function() {
    $('#btable1').dataTable();
} );

$(document).ready(function() {
    $('#btable2').dataTable();
} );

$(document).ready(function() {
    $('#btable3').dataTable();
} );

$(document).ready(function() {
    $('#btable4').dataTable();
} );





function toggleView(a, b)
{
	document.getElementById(a).style.display='block';
	document.getElementById(b).style.display='none';
}

function handleButtonAction(action, id){
	
	var proceed = false;
	var cbs = document.getElementsByName('selectAllCheckbox');
	for(var i=0; i<cbs.length; i++)
	{
		if(cbs[i].checked == true)
		{
			proceed = true;
		}
	}
	if(action=='getBalance')
	{
		proceed = true;
	}
	if(action=='getBalance1')
	{
		proceed = true;
	}
	
	if(proceed==true)
	{
		if(action=='pay')
		{
			//alert(1);
			if(new String(id).length > 0)
			{
				//alert(2);
				var total = changeTotalAmount();
				//alert(total);
				var amt = parseFloat(id);
				//alert(amt);
				if(amt>total)
				{
					if(confirm("Confirm you wish to pay for selected tax assessments!"))
					{
						document.getElementById('selectedAssessmentAction').value = action;
						document.getElementById('taxassessmentForm').submit();
					}else
					{
						
					}
				}else
				{
					alert("You can not add the selected assessments to your company's workflow tray as you do not have enough funds in your account." +
					"Try choosing assessments having lesser amounts");
				}
			}else
			{
				alert("First click on VIEW MY COMPANYS ACCOUNT BALANCE button to view your account balance before trying again");
			}
		}else if(action=='breakdownSpecific')
		{
			var str = selectAllCheckbox + id;
			document.getElementById(str).checked = true;
			document.getElementById('selectedAssessmentAction').value = action;
			document.getElementById('taxassessmentForm').submit();
		}
		else if(action=='breakdown')
		{
			document.getElementById('selectedAssessmentAction').value = action;
			document.getElementById('taxassessmentForm').submit();
		}
		else if(action=='initiatePayment')
		{
			if(new String(id).length > 0)
			{
				var total = changeTotalAmount();
				//alert(total);

				var amt = parseFloat(id);
				//alert(amt);
				if(amt>total)
				{
					document.getElementById('selectedAssessmentAction').value = action;
					document.getElementById('taxassessmentForm').submit();
				}else
				{
					alert("You can not add the selected assessments to your company's workflow tray as you do not have enough funds in your account." +
							"Try choosing assessments having lesser amounts");
				}
			}else
			{
				alert("First click on VIEW MY COMPANYS ACCOUNT BALANCE button to view your account balance before trying again");
			}
			
		}else if(action=='getBalance')
		{
			document.getElementById('selectedAssessmentAction').value = action;
			document.getElementById('taxassessmentForm').submit();
		}else if(action=='getBalance1')
		{
			document.getElementById('selectedAssessmentAction').value = action;
			document.getElementById('taxassessmentForm').submit();
		}
	}else
	{
		alert('You must select at least one tax item before carrying out this action. Select tax items by clicking on their respective checkboxes');
	}
}



function viewAdvancedSearch(divId)
{
}


function handleSelectAll()
{
	if(document.getElementById('clickSelectAll').checked==true)
	{
		var cbs = document.getElementsByName('selectAllCheckbox');
		for(var i=0; i<cbs.length; i++)
		{
			cbs[i].checked = true;
		}
	}	  
	else
	{
		var cbs = document.getElementsByName('selectAllCheckbox');
		for(var i=0; i<cbs.length; i++)
		{
			cbs[i].checked = false;
		}
	}
}



function isCheckBoxesChecked()
{
	var cbs = document.getElementsByName('selectAllCheckbox');
	var c = 0;
	for(var i=0; i < cbs.length; i++) {
		if(document.getElementsByName('selectAllCheckbox')[i].checked)
		{
			c++;
		}
	}
	
	if(c==0)
		return false;
	else
		return true;
}



function uncheckAll()
{
	var cbs = document.getElementsByName('selectAllCheckbox');
	var c = 0;
	for(var i=0; i < cbs.length; i++) {
		document.getElementsByName('selectAllCheckbox')[i].checked=false;
	}
}



function changeTotalAmount()
{
	var total = 0.0;
	//alert(2);
	  var allCheckbox = document.getElementsByName("selectAllCheckbox");
	  //alert(3);
	  for(c1 =0; c1<allCheckbox.length; c1++)
	  {
		  //alert(4);
		  	if(allCheckbox[c1].checked==true)
	  		{
		  		//alert(5);
	  			var value=allCheckbox[c1].value;
	  			//alert(6);
	  			var id1 = "amtChange" + value.replace('/', '_');
	  			id1 = id1.replace('/', '_');
	  			//alert(id1);
	  			var amtChange = document.getElementById(id1).value;
	  			//alert(8);
	  			var id2 = "intChange" + value.replace('/', '_');
	  			id2 = id2.replace('/', '_');
	  			//alert(id2);
	  			var intChange = document.getElementById(id2).value;
	  			//alert(10);
	  			total = total + parseFloat(amtChange);
	  			//alert(11);
	  			total = total + parseFloat(intChange);
	  			//alert(12);
	  			
	  		}
	  }
	 // alert(13);
	  var total1 = total;
	  //alert(14);
	  total1 = total.formatMoney(2);
	  //alert(15);
	  document.getElementById('totalAmountDiv').innerHTML = "<strong>Total Amount Payable: ZMW " + total1 + "</strong>";
	  //alert(16);
	  return total;
}



Number.prototype.formatMoney = function(c, d, t){
	var n = this, 
	    c = isNaN(c = Math.abs(c)) ? 2 : c, 
	    d = d == undefined ? "." : d, 
	    t = t == undefined ? "," : t, 
	    s = n < 0 ? "-" : "", 
	    i = parseInt(n = Math.abs(+n || 0).toFixed(c)) + "", 
	    j = (j = i.length) > 3 ? j % 3 : 0;
	   return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
	 };
</script>