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
<%@page import="smartpay.entity.TaxType"%>
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
<link rel="stylesheet" href='<%=faceboxCssUrl%>' type="text/css" />
<link rel="stylesheet" href='<%=pagingUrl%>' type="text/css" />
<link href="<%=jqueryUICssUrl%>" rel="stylesheet" type="text/css" />
<%

ReportPortletState portletState = ReportPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ReportPortletState.class);
List<String> paymentTypes = PaymentTypeConstants.values();
List<String> paymentStatus = PaymentHistoryConstants.values();
Collection<Ports> portList = portletState.getPortsList();
Collection<TaxType> taxTypes = portletState.getTaxTypeListing();

%>


<portlet:actionURL var="proceedToStepOne" name="processAction">
	<portlet:param name="action"
		value="<%=REPORTING_ACTIONS.CREATE_A_REPORT_STEP_TWO.name()%>" />
</portlet:actionURL>


<div style="padding:10px; width:900px"> 	
    <div class="panel  panel-primary">
		<div class="panel-heading">
			<div style="color:white; font-weight: bold; float:left">Report on Payments!</div>
			<div style="color:white; font-weight: bold; float:right">Report Format: 
							<select name="reportFormat" id="reportFormat" class="form-control">
								<option value="-1">-Select A Report Format-</option>
								<option value="PDF">PDF</option>
								<option value="Spreadsheet">Spreadsheet</option>
							</select>
			</div>
		</div>
		<div class="panel-body">
		    <form  id="panelcreatorform" action="<%=proceedToStepOne%>" method="post" enctype="application/x-www-form-urlencoded">
			    <fieldset>
			      <div>
				  	<div style="padding-bottom:10px"><strong>Generate reports on Breakdown of Assessment Payments based on this filter properties </strong></div>
			      	<div>
			          	<div>Date Period is between:</div>
						<div style="padding-bottom:10px"><input type="text" name="startDate" id="startDate" value="<%=portletState.getStartDate()%>" placeholder="yyyy/mm/dd" readonly="readonly" /> and <input readonly="readonly" type="text" name="endDate" id="endDate" value="<%=portletState.getEndDate()%>" placeholder="yyyy/mm/dd" /></div>
			        </div>
					<div>
			          	<div>Source Account:</div>
						<div style="padding-bottom:10px"><input type="text" name="sourceAccount" id="sourceAccount" value="<%=portletState.getSourceAccount()%>" /></div>
			        </div>
					<div>
			          	<div>Source Sort Code:</div>
						<div style="padding-bottom:10px"><input type="text" name="sourceSortCode" id="sourceSortCode" value="<%=portletState.getSourceSortCode()%>" /></div>
			        </div>
					<div>
			          	<div>Payee TPIN:</div>
						<div style="padding-bottom:10px"><input type="text" name="tpin" id="tpin" value="<%=portletState.getSourceSortCode()%>" /></div>
			        </div>
					<div>
			          	<div>Tax Type:</div>
							<div style="padding-bottom:10px">
								<select name="paymentType" id="paymentType" class="form-control">
									<option value="-1">-Select A Tax Type-</option>
									<%
									for(Iterator<TaxType> it = taxTypes.iterator(); it.hasNext();)
									{
										TaxType itName = it.next();
									%>
										<option value="<%=itName.getId()%>"><%=itName.getTaxName()%></option>
									<%
									}
									%>
								</select>
							<div>
						</div>
			        </div>
					<div>
			          	<div>Status of Payment:</div>
							<div style="padding-bottom:10px">
								<select name="paymentStatus" id="paymentStatus" class="form-control">
									<option value="-1">-Select A Payment Status-</option>
									<%
									for(Iterator<String> it = paymentStatus.iterator(); it.hasNext();)
									{
										String itName = it.next();
									%>
										<option value="<%=itName%>">itName</option>
									<%
									}
									%>
								</select>
							<div>
						</div>
			        </div>
					<div>
			          	<div>A single Tax Breakdown Amount Paid is between:</div>
						<div style="padding-bottom:10px"><input type="text" name="amountLowerLimit" id="amountLowerLimit" value="<%=portletState.getAmountLowerLimit()%>" placeholder="" /> and <input type="text" name="amountUpperLimit" id="amountUpperLimit" value="<%=portletState.getAmountUpperLimit()%>" placeholder="" /></div>
			        </div>
					<div>
			          	<div>Payment is for Assessment Registration Number:</div>
						<div style="padding-bottom:10px"><input type="text" name="assessmentRegNo" id="assessmentRegNo" value="<%=portletState.getAssessmentRegNo()%>" placeholder="" /></div>
			        </div>
					<div>
			          	<div>Payment is made by company registration number:</div>
						<div style="padding-bottom:10px"><input type="text" name="companyRegNo" id="companyRegNo" value="<%=portletState.getAssessmentRegNo()%>" placeholder="" /></div>
			        </div>
					<div>
			          	<div><input onclick="javascript:handleDefaultColumnShow()" type="checkbox" checked="checked" name="defaultColumnShow" id="defaultColumnShow" value="1" />Display default columns in generated report! <a href="javascript:displayCustomColumns('defaultColumnShow')">Customize Columns to display</a></div>
			        </div>
					<div style="display:none" id="customizeColumn">
			          	<div>Select Columns to display:</div>
						<div style="padding-bottom:10px">
							<input type="checkbox" name="columnToShow" id="columnToShow" value="TXNREFNO" />Transaction Reference Number
						</div>
						<div style="padding-bottom:10px">
							<input type="checkbox" name="columnToShow" id="columnToShow" value="RECTNO" />Receipt Number
						</div>
						<div style="padding-bottom:10px">
							<input type="checkbox" name="columnToShow" id="columnToShow" value="SRCACCTNO" />Source Account Number
						</div>
						<div style="padding-bottom:10px">
							<input type="checkbox" name="columnToShow" id="columnToShow" value="SRCSORTCODE" />Source Sort Code
						</div>
						<div style="padding-bottom:10px">
							<input type="checkbox" name="columnToShow" id="columnToShow" value="PAYSTATUS" />Payment Status
						</div>
						<div style="padding-bottom:10px">
							<input type="checkbox" name="columnToShow" id="columnToShow" value="TAXTYPE" />Type of Tax
						</div>
						<div style="padding-bottom:10px">
							<input type="checkbox" name="columnToShow" id="columnToShow" value="TXNAMT" />Assessment BreakDown Item Amount
						</div>
						<div style="padding-bottom:10px">
							<input type="checkbox" name="columnToShow" id="columnToShow" value="ASSPAID" />Assessment Paid for
						</div>
						<div style="padding-bottom:10px">
							<input type="checkbox" name="columnToShow" id="columnToShow" value="PAYEETPIN" />Payee TPIN
						</div>
						<div style="padding-bottom:10px">
							<input type="checkbox" name="columnToShow" id="columnToShow" value="PAYEECOMP" />Payee Company
						</div>
						<div style="padding-bottom:10px">
							<input type="checkbox" name="columnToShow" id="columnToShow" value="PAYEENAME" />Payee Name
						</div>
						<div style="padding-bottom:10px">
							<input type="checkbox" name="columnToShow" id="columnToShow" value="DATEPAID" />Date Paid
						</div>
			        </div>
					
					<div style="padding-top:10px">
			          	<div>Generate Report and Send Report to this email Address:</div>
						<div style="padding-bottom:10px"><input type="text" name="reportEmailSend" id="reportEmailSend" value="<%=portletState.getAssessmentRegNo()%>" placeholder="" /></div>
			        </div>
			      </div>
				  <div style="padding-top:10px; padding-bottom:10px;">
			        <button name="filterreport" id="filterreport" class="btn btn-danger" style="float:left">Back</button>
					<button name="filterreport" id="filterreport" class="btn btn-success" style="float:right">Generate Report</button>
			      </div>
			    </fieldset>
		    </form>
		</div>
	</div>
</div>


<script type="text/javascript">
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
</script>