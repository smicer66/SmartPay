<%@page import="com.probase.smartpay.reports.ReportPortletState"%>
<%@page import="com.probase.smartpay.reports.ReportPortletState.*"%>
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
<%@ page import="java.util.List"%>
<%@ page import="java.util.TimeZone"%>
<%@ page import="java.util.GregorianCalendar"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="smartpay.entity.BankBranches"%>
<%@page import="smartpay.entity.Company"%>
<%@page import="smartpay.entity.Ports"%>
<%@page import="smartpay.entity.enumerations.PaymentTypeConstants"%>
<%@page import="smartpay.entity.enumerations.PaymentHistoryConstants"%>
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
<link rel="stylesheet" href="<%=faceboxCssUrl%>" type="text/css" />
<link rel="stylesheet" href="<%=pagingUrl%>" type="text/css" />
<script type="text/javascript" src="<%=(resourceBaseURL + "/js/anytimec.js")%>"></script>
<link rel="stylesheet" href='<%=resourceBaseURL + "/css/anytimec.css"%>' type="text/css" />
<link href="<%=jqueryUICssUrl%>" rel="stylesheet" type="text/css" />
<%

ReportPortletState portletState = ReportPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ReportPortletState.class);
List<PaymentTypeConstants> paymentTypes = PaymentTypeConstants.values();
List<PaymentHistoryConstants> paymentStatus = PaymentHistoryConstants.values();
Collection<Ports> portList = portletState.getPortsList();


%>


<portlet:actionURL var="proceedToStepOne" name="processAction">
	<portlet:param name="action"
		value="<%=REPORTING_ACTIONS.CREATE_A_PAYMENT_REPORT_STEP_TWO.name()%>" />
</portlet:actionURL>


<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>



<div style="padding:10px; width:900px"> 	
    <div class="panel  panel-primary">
		<div class="panel-heading;"style="">
			<div style="color:white; font-weight: bold; float:left">Report on Payments!</div>
		</div>
		<div class="panel-body">
		    <form  id="panelcreatorform" action="<%=proceedToStepOne%>" method="post" enctype="application/x-www-form-urlencoded">
			    <fieldset>
			      <div style="width:500px">
				  	<div style="padding-bottom:10px"><h2><strong>Filter properties </strong></h2></div>
			      	<div>
			          	<div style="font-weight:bold">Date Period is between:</div>
						<div style="padding-bottom:10px">
							<div style="width:200px; float:left">
								<input  type="text"  class="form-control" name="startDate" id="startDate" value="<%=portletState.getStartDate()==null ? "" : portletState.getStartDate()%>" placeholder="yyyy/mm/dd" readonly="readonly" />
							</div>
							<div style="width:50px; float:left; padding-left:5px; padding-right:5px; padding-top:5px;"> and </div>
							<div style="width:200px; float:left">
								<input readonly="readonly"  type="text"  class="form-control" name="endDate" id="endDate" value="<%=portletState.getEndDate()==null ? "" : portletState.getEndDate()%>" placeholder="yyyy/mm/dd" />
							</div>
						</div>
			        </div>
					<div style="clear:both">
			          	<div style="font-weight:bold">Source Account:</div>
						<div style="padding-bottom:10px"><input onkeypress="return onlyNumKey(event)"  type="text"  class="form-control" name="sourceAccount" id="sourceAccount" value="<%=portletState.getSourceAccount()==null ? "" : portletState.getSourceAccount()%>" /></div>
			        </div>
					<div>
			          	<div style="font-weight:bold">Receipient Account:</div>
						<div style="padding-bottom:10px"><input onkeypress="return onlyNumKey(event)"  type="text"  class="form-control" name="receipientAccount" id="receipientAccount" value="<%=portletState.getReceipientAccount()==null ? "" : portletState.getReceipientAccount()%>" /></div>
			        </div>
					<div>
			          	<div style="font-weight:bold">Source Sort Code:</div>
						<div style="padding-bottom:10px"><input  onkeypress="return onlyNumKey(event)" type="text"  class="form-control" name="sourceSortCode" id="sourceSortCode" value="<%=portletState.getSourceSortCode()==null ? "" : portletState.getSourceSortCode()%>" /></div>
			        </div>
					<div>
			          	<div style="font-weight:bold">Payee TPIN:</div>
						<div style="padding-bottom:10px"><input onkeypress="return onlyNumKey(event)"  type="text"  class="form-control" name="tpin" id="tpin" value="<%=portletState.getTpin()==null ? "" : portletState.getTpin()%>" /></div>
			        </div>
					<div>
			          	<div style="font-weight:bold">Declarant TPIN:</div>
						<div style="padding-bottom:10px"><input  onkeypress="return onlyNumKey(event)" type="text"  class="form-control" name="declarantTpin" id="declarantTpin" value="<%=portletState.getDeclarantTpin()==null ? "" : portletState.getDeclarantTpin()%>" /></div>
			        </div>
					<div>
			          	<div style="font-weight:bold">Assessment TPIN:</div>
						<div style="padding-bottom:10px"><input onkeypress="return onlyNumKey(event)"  type="text"  class="form-control" name="assessmentTpin" id="assessmentTpin" value="<%=portletState.getAssessmentTpin()==null ? "" : portletState.getAssessmentTpin()%>" /></div>
			        </div>
			        
					<div>
			          	<div style="font-weight:bold">Payment Type:</div>
							<div style="padding-bottom:10px">
								<select name="paymentType" id="paymentType" class="form-control">
									<option value="-1">-Select A Payment Type-</option>
									<%
									String selected="";
									for(Iterator<PaymentTypeConstants> it = paymentTypes.iterator(); it.hasNext();)
									{
										PaymentTypeConstants itName = it.next();
										if(portletState.getPaymentType()!=null && portletState.getPaymentType().equals(itName))
										{
											selected = "selected='selected'";
										}
									%>
										<option <%=selected %> value="<%=itName.getValue()%>"><%=itName.getValue()%></option>
									<%
									}
									%>
								</select>
							<div>
						</div>
			        </div>
					<div>
			          	<div style="font-weight:bold">Status of Payment:</div>
							<div style="padding-bottom:10px">
								<select name="paymentStatus" id="paymentStatus" class="form-control">
									<option value="-1">-Select A Payment Status-</option>
									<%
									selected="";
									for(Iterator<PaymentHistoryConstants> it = paymentStatus.iterator(); it.hasNext();)
									{
										PaymentHistoryConstants itName = it.next();
										if(portletState.getPaymentStatus()!=null && portletState.getPaymentStatus().equals(itName))
										{
											selected = "selected='selected'";
										}
									%>
										<option <%=selected%> value="<%=itName.getValue()%>"><%=itName.getValue()%></option>
									<%
									}
									%>
								</select>
							<div>
						</div>
			        </div>
			        <div>
			          	<div style="font-weight:bold">Amount Paid is between:</div>
						<div style="padding-bottom:10px">
							<div style="width:200px; float:left">
								<input  type="text" onkeypress="return onlyDoubleKey(event, 'amountLowerLimit')"  class="form-control" name="amountLowerLimit" id="amountLowerLimit" value="<%=portletState.getAmountLowerLimit()==null ? "" : portletState.getAmountLowerLimit()%>" placeholder="" />
							</div>
							<div style="width:50px; float:left; padding-left:5px; padding-right:5px; padding-top:5px;"> and </div>
							<div style="width:200px; float:left">
								<input onkeypress="return onlyDoubleKey(event, 'amountUpperLimit')"  type="text"  class="form-control" name="amountUpperLimit" id="amountUpperLimit" value="<%=portletState.getAmountUpperLimit()==null ? "" : portletState.getAmountUpperLimit()%>" placeholder="" />
							</div>
						</div>
			        </div>
					<div>
			          	<div style="font-weight:bold">Payment is for Assessment Registration Number:</div>
						<div style="padding-bottom:10px"><input onkeypress="return onlyNumKey(event)"  type="text"  class="form-control" name="assessmentRegNo" id="assessmentRegNo" value="<%=portletState.getAssessmentRegNo()==null ? "" : portletState.getAssessmentRegNo()%>" placeholder="" /></div>
			        </div>
					<div>
			          	<div style="font-weight:bold">Assessment Year:</div>
						<div style="padding-bottom:10px">
							<select name="assessmentYear" id="assessmentYear" class="form-control">
								<option value="-1">-Select An Assessment Year-</option>
								<%
								Calendar cal = Calendar.getInstance();
								int year = cal.get(Calendar.YEAR);
								selected = "";
								for(int yearCount = year; yearCount>(year-10); yearCount-- )
								{
									if(portletState.getAssessmentYear()!=null && portletState.getAssessmentYear().equals(Integer.toString(yearCount)))
									{
										selected = "selected='selected'";
									}
							%>
								<option <%=selected%> value="<%=yearCount%>"><%=yearCount%></option>
								<%
								}
								%>
							</select>
						</div>
			        </div>
					<div>
			          	<div style="font-weight:bold">Port of Entry for Assessment Paid:</div>
						<div style="padding-bottom:10px">
							<select name="portofEntry" id="portofEntry" class="form-control">
								<option value="-1">-Select A Port Of Entry-</option>
								<%
								for(Iterator<Ports> it = portList.iterator(); it.hasNext();)
								{
									Ports port = it.next();
									if(portletState.getPortofEntry()!=null && portletState.getPortofEntry().equals(Long.toString(port.getId())))
									{
										selected = "selected='selected'";
									}
							%>
								<option <%=selected%> value="<%=port.getId()%>"><%=port.getFullName() + " - " + port.getPortCode()%></option>
								<%
								}
								%>
							</select>
						</div>
			        </div>
					<div>
			          	<div style="font-weight:bold">Payment is made by company registration number:</div>
						<div style="padding-bottom:10px"><input  type="text"  class="form-control" name="companyRegNo" id="companyRegNo" value="<%=portletState.getAssessmentRegNo()==null ? "" : portletState.getAssessmentRegNo()%>" placeholder="" /></div>
			        </div>
					<div>
			          	<div style="font-weight:bold"><input onclick="javascript:handleDefaultColumnShow()" type="checkbox" checked="checked" name="defaultColumnShow" id="defaultColumnShow" value="1" />&nbsp;&nbsp;&nbsp;&nbsp;Display default columns in generated report! <br><a href="javascript:displayCustomColumns('defaultColumnShow')">Customize Columns to display</a></div>
			        </div>
					<div style="display:none; padding:10px;" id="customizeColumn">
						<div style="padding:10px; width:500px"> 	
						    <div class="panel  panel-primary">
								<div class="panel-heading"><span style="color:white; font-weight: bold">Select Columns to Display:</span></div>
								<div class="panel-body">
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										String checked="";
										if(portletState.getShowTxnRefNo()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowTxnRefNo()!=null && portletState.getShowTxnRefNo().equals("TXNREFNO")) ? "checked='checked'" : "" %> name="showTxnRefNo" id="showTxnRefNo" value="TXNREFNO" />Transaction Reference Number
									</div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowRectNo()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowRectNo()!=null && portletState.getShowRectNo().equals("RECTNO")) ? "checked='checked'" : "" %> name="showRectNo" id="showRectNo" value="RECTNO" />Receipt Number
									</div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowSrcAcctNo()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowSrcAcctNo()!=null && portletState.getShowSrcAcctNo().equals("SRCACCTNO")) ? "checked='checked'" : "" %> name="showSrcAcctNo" id="showSrcAcctNo" value="SRCACCTNO" />Source Account Number
									</div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowRecAcctNo()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowRecAcctNo()!=null && portletState.getShowRecAcctNo().equals("RECACCTNO")) ? "checked='checked'" : "" %> name="showRecAcctNo" id="showRecAcctNo" value="RECACCTNO" />Receipient Account Number
									</div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowSrcSortCode()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowSrcSortCode()!=null && portletState.getShowSrcSortCode().equals("SRCSORTCODE")) ? "checked='checked'" : "" %> name="showSrcSortCode" id="showSrcSortCode" value="SRCSORTCODE" />Source Sort Code
									</div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowPayStatus()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowPayStatus()!=null && portletState.getShowPayStatus().equals("PAYSTATUS")) ? "checked='checked'" : "" %> name="showPayStatus" id="showPayStatus" value="PAYSTATUS" />Payment Status
									</div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowPayType()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowPayType()!=null && portletState.getShowPayType().equals("PAYTYPE")) ? "checked='checked'" : "" %> name="showPayType" id="showPayType" value="PAYTYPE" />Type of Payment
									</div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowTxnAmt()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowTxnAmt()!=null && portletState.getShowTxnAmt().equals("TXNAMT")) ? "checked='checked'" : "" %> name="showTxnAmt" id="showTxnAmt" value="TXNAMT" />Transaction Amount
									</div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowAssPaid()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowAssPaid()!=null && portletState.getShowAssPaid().equals("ASSPAID")) ? "checked='checked'" : "" %> name="showAssPaid" id="showAssPaid" value="ASSPAID" />Assessment Paid for
									</div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowPayeeTpin()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowPayeeTpin()!=null && portletState.getShowPayeeTpin().equals("PAYEETPIN")) ? "checked='checked'" : "" %> name="showPayeeTpin" id="showPayeeTpin" value="PAYEETPIN" />Payee TPIN
									</div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowPayeeComp()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowPayeeComp()!=null && portletState.getShowPayeeComp().equals("PAYEECOMP")) ? "checked='checked'" : "" %> name="showPayeeComp" id="showPayeeComp" value="PAYEECOMP" />Payee Company
									</div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowPayeeName()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowPayeeName()!=null && portletState.getShowPayeeName().equals("DATEPAID")) ? "checked='checked'" : "" %> name="showPayeeName" id="showPayeeName" value="PAYEENAME" />Payee Name
									</div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowClientPaidFor()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowClientPaidFor()!=null && portletState.getShowClientPaidFor().equals("CLIENTPAIDFOR")) ? "checked='checked'" : "" %> name="showClientPaidFor" id="showClientPaidFor" value="CLIENTPAIDFOR" />Client TPIN Paid For
									</div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowDatePaid()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowDatePaid()!=null && portletState.getShowDatePaid().equals("DATEPAID")) ? "checked='checked'" : "" %> name="showDatePaid" id="showDatePaid" value="DATEPAID" />Date Paid
									</div>
								</div>
							</div>
						</div>
			        </div>
					
					<div style="padding-top:10px; display:none">
			          	<div style="font-weight:bold">Generate Report and Send Report to this email Address:</div>
						<div style="padding-bottom:10px"><input  type="text"  class="form-control" name="reportEmailSend" id="reportEmailSend" value="<%=portletState.getReportEmailSend()==null ? "" : portletState.getReportEmailSend()%>" placeholder="" /></div>
			        </div>
			      </div>
			      </div>
				  <div style="padding-top:10px; padding-bottom:10px;">
			        <button name="filterreport" id="filterreport" class="btn btn-danger" style="float:left; display:none">Back</button>
					<button name="filterreport" id="filterreport" class="btn btn-success" style="float:right">Generate Report</button>
			      </div>
			    </fieldset>
		    </form>
		</div>
	</div>
</div>

<script type="text/javascript" charset="utf-8" src="<%=jqueryJsUrl%>"></script>
<script type="text/javascript" charset="utf-8" src="<%=jqueryUIJsUrl%>"></script>
<script type="text/javascript">

<%
int year1 = Calendar.getInstance().get(Calendar.YEAR);
boolean proceed;
int startYear = 1990;
int earliestYearDiff = year1 - startYear;
%>

$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#endDate').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "-<%=earliestYearDiff%>:+0",
		maxDate: -0,
		showButtonPanel : true,
		dateFormat: 'yy/mm/dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false,
		onSelect: function(date){
			var date1 = $('#agmDate__NSMC').datepicker('getDate');
			//alert(date1);
			var maxdate = new Date(Date.parse(date1));
			//alert(maxdate.toDateString());
			var mindate = new Date(Date.parse(date1));
		}
	});
	
	
	
});


$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#startDate').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "-<%=earliestYearDiff%>:+0",
		maxDate: -0,
		showButtonPanel : true,
		dateFormat: 'yy/mm/dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false,
		onSelect: function(date){
			var date1 = $('#agmDate__NSMC').datepicker('getDate');
			//alert(date1);
			var maxdate = new Date(Date.parse(date1));
			//alert(maxdate.toDateString());
			var mindate = new Date(Date.parse(date1));
		}
	});
	
	
	
});



function downloadReport()
{
	document.location.href='<%=response.encodeURL(request.getContextPath()+"/ActiveServlet?action=downloadReceipt&reportId=" + portletState.getFilName())%>';
}


function displayCustomColumns(id)
{
	document.getElementById('defaultColumnShow').checked = false;
	document.getElementById('customizeColumn').style.display = 'block';
}


function handleDefaultColumnShow()
{
	if(document.getElementById('defaultColumnShow').checked)
	{
		document.getElementById('customizeColumn').style.display = 'none';
	}else
	{
		document.getElementById('customizeColumn').style.display = 'block';
	}
}



function onlyDoubleKey(e, elementId)
{
	var src = (e.srcElement || e.target);
	var unicode=e.charCode? e.charCode : e.keyCode;
	
	var check = false;
	var lenAfter = document.getElementById(elementId).value.length - document.getElementById(elementId).value.indexOf(".");
	if(document.getElementById(elementId).value.length>0 && 
			document.getElementById(elementId).value.indexOf(".")==-1 && 
			lenAfter<3)
	{
		check =true;
	}
	
	if(((unicode>47) && (unicode<58)) || (check==true && unicode==46))
		{}
	else
		{return false}
	 
	
}


function onlyNumKey(e)
{
	var src = (e.srcElement || e.target);
	var unicode=e.charCode? e.charCode : e.keyCode
	
	
	
	if ((unicode>47) && (unicode<58)) 
		{}
	else
		{return false}
	 
	
}

</script>