<%@page import="smartpay.entity.enumerations.CompanyTypeConstants"%>
<%@page import="com.probase.smartpay.domtax.DomTaxPortletState"%>
<%@page import="com.probase.smartpay.domtax.DomTaxPortletState.*"%>
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
<%@page import="smartpay.entity.DomTax"%>
<%@page import="smartpay.entity.WorkFlowAssessment"%>
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
DomTaxPortletState portletState = DomTaxPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(DomTaxPortletState.class);
TpinInfo ctpinInfo = portletState.getDomTaxPortletUtil().getTpinInfoByPortalUser(portletState);
%>


<portlet:actionURL var="handleDomTaxListing" name="processAction">
	<portlet:param name="action"
		value="<%=DOM_TAX_ACTION.HANDLE_DOM_TAX_LISTING.name()%>" />
</portlet:actionURL>
<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>


<div style="padding:10px;">


	<div style="padding:5px">
		<div style=" clear:both; float:left; font-weight:bold; padding:5px; width:150px;">Company Name:</div>
	  	<div style="float:left; padding:5px; width:150px;"><%=portletState.getPortalUser().getCompany().getCompanyName()%></div>
	  
	  	<!-- <div style=" clear:both; float:left; font-weight:bold;padding:5px; width:150px;">Company Reg No:</div>
	  	<div style="float:left; padding:5px; width:150px;"><%=portletState.getPortalUser().getCompany().getCompanyRCNumber()%></div>-->
	  
	  	<div style=" clear:both; float:left; font-weight:bold;padding:5px; width:150px;">Tax Payer Id No:</div>
	  	<div style="float:left; padding:5px; width:150px;"><%=ctpinInfo.getTpin()%></div>
	</div>
	
	<div style="clear:both">&nbsp;</div>





	<div class="panel panel-info" style="padding-bottom:15px;">
		<!-- Default panel contents -->
		<div class="panel-heading"><strong>Domestic Tax Listing</strong></div>
		<div class="panel-body">
			<p>Click on a taxes' PAY button to pay for the tax</p>
		</div>
			<form  id="taxassessmentForm" action="<%=handleDomTaxListing%>" method="post" enctype="application/x-www-form-urlencoded">
				
			<div style="font-size:13px; padding:5px;">
			  <%
			  Double totalAmount = 0.0;
			  if(portletState.getAllDomTaxListing()!=null && portletState.getAllDomTaxListing().size()>0)
			  {
			  %>
				  
				  <table width="100%" id="btable">
					<thead>
						<th valign="middle" align="left">Payment Reg No</th>
						<th valign="middle" align="left">Payment Reg Date</th>
						<th valign="middle" align="left">Tax Identification Number</th>
						<th valign="middle" align="left">Amount (ZMW)</th>
						<th valign="middle" align="left">Status</th>
						<th valign="middle" align="left">Action</th>
					</thead>
					<tbody>
				  <%
				  int c=0;
				  
				  for(Iterator<DomTax> iter = portletState.getAllDomTaxListing().iterator(); iter.hasNext();)
				  {
					  DomTax domTax = iter.next();
					  String bgColor = "#ffffff";
					  if(c%2==0)
					  {
						bgColor = "#cccccc";
					  }
					  boolean show = true;
					  String paidForString = "Active";
					  
					  if(portletState.getAllWorkFlowDomTax()!=null && portletState.getAllWorkFlowDomTax().size()>0)
					  {
						  for(Iterator<DomTax> iterAss = portletState.getAllWorkFlowDomTax().iterator(); iterAss.hasNext();)
						  {
							  DomTax temp = iterAss.next();
							  Collection<WorkFlowAssessment> wfaList1= portletState.getDomTaxPortletUtil().getWorkFlowAssessmentByDomTax(temp);
							  if(temp.getPaymentRegNo().equals(domTax.getPaymentRegNo()))
							  {
								  if(temp.getPaidFor().equals(Boolean.TRUE))
								  {
									  show = false;
									  paidForString = "<span style='background-color:#ff6600; padding:2px;'>Paid(Awaiting Confirmation From ZRA)</span>";
								  }else
								  {
									  show = false;
									  paidForString = "<span style='background-color:#5cb85c; color: #ffffff; padding:2px;'>Moved to Approval Workflow</span>";
								  }
							  }
						  }
					  }
					  
					  if(show==false)
					  {
					  %>
					 <tr>
						<td><%=domTax.getPaymentRegNo()==null ? "N/A" : domTax.getPaymentRegNo() %></td>
						<td><%=domTax.getPaymentRegDate()==null ? "N/A" : domTax.getPaymentRegDate().toString().split(" ")[0] %></td>
						<td><%=(domTax.getTpinInfo()!=null && domTax.getTpinInfo().getTpin()!=null)  ? domTax.getTpinInfo().getTpin() : "N/A" %></td>
						<td><%=domTax.getAmountPayable()==null ? "N/A" : new Util().roundUpAmount(domTax.getAmountPayable()) %></td>
						<td><%=paidForString%></td>
						<td>&nbsp;
						</td>
					  </tr>
					 
					 <%
					  }else
					  {
					 %>
					  <tr>
						<td><%=domTax.getPaymentRegNo()==null ? "N/A" : domTax.getPaymentRegNo() %></td>
						<td><%=domTax.getPaymentRegDate()==null ? "N/A" : domTax.getPaymentRegDate().toString().split(" ")[0] %></td>
						<td><%=(domTax.getTpinInfo()!=null && domTax.getTpinInfo().getTpin()!=null)  ? domTax.getTpinInfo().getTpin() : "N/A" %></td>
						<td><%=domTax.getAmountPayable()==null ? "N/A" : new Util().roundUpAmount(domTax.getAmountPayable()) %></td>
						<td>Active
						<input type="hidden" id="amtChange<%=domTax.getPaymentRegNo()%>" value="<%=domTax.getAmountPayable()==null ? 0.00 : domTax.getAmountPayable()%>">
						</td>
						<td>
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
										<%
										if(portletState.getBalanceInquiry()!=null && portletState.getBalanceInquiry().getAvailableBalance()>domTax.getAmountPayable())
										{
											if(portletState.getPortalUser().getCompany().getMandatePanelsOn().equals(Boolean.TRUE))
											{
												if(portletState.getDomTaxPortletUtil().getAllAuthorizePanelUsersByAmountAndInitiator(domTax.getAmountPayable()).contains(portletState.getPortalUser().getId()))
												{
										%>
												<li><a href="javascript: handleButtonAction('initiatePayment', '<%=domTax.getPaymentRegNo()%>', '<%=portletState.getBalanceInquiry().getAvailableBalance()%>' )">Add Domestic Tax to Workflow Tray</a></li>
										<%
												}
											}else
											{
										%>
												<li><a href="javascript: handleButtonAction('pay', '<%=domTax.getPaymentRegNo()%>', '<%=portletState.getBalanceInquiry()!=null ? portletState.getBalanceInquiry().getAvailableBalance() : ""%>')">Pay</a></li>
										<%	
											}
										%>
										<%	
										}
										%>
									</ul>
								</div>
							<%
							}else
							{
								if(portletState.getBalanceInquiry()==null)
								{
							%>
								<button type="button" class="btn btn-success" onclick="javascript:handleButtonAction('getBalance', '')">First View Your Company Bank Account Balance</button>
							<%
								}
							}
							%>
							</div>
						</td>
					  </tr>
				  <%  
					  }
				  }
				  %>
				  </tbody>
				</table>
				  <div id="totalAmountDiv" style="clear:both; text-align: right; padding-bottom:10px; padding-right:10px;">&nbsp;
				  </div>
				  
					
					
					
				
				  <%
			  }else
			  {
				  %>
				  <table width="100%" id="btable1">				  
					<thead>
						<th valign="middle" align="left">Payment Reg No</th>
						<th valign="middle" align="left">Payment Reg Date</th>
						<th valign="middle" align="left">Tax Identification Number</th>
						<th valign="middle" align="left">Amount (ZMW)</th>
					</thead>
				  </table>
				  <div>There are no outstanding tax assessments</div>
				  
				  <%  
			  }
			  %>
				
			
			<input type="hidden" name="selectedDomTax" id="selectedDomTax" value="" />
			<input type="hidden" name="selectedDomTaxAction" id="selectedDomTaxAction" value="" />	
			</form>
			
			<div style="float:left">
				<div class="panel panel-info" style="clear:both">
					<div class="panel-heading"><strong>Current Bank Balance</strong></div>
				</div>
			</div>
			
			
			
	</div>
	
	<div style="padding:15px;">
		<div style="clear:both; float:left; text-align: left; ">
			<%
			if(portletState.getBalanceInquiry()!=null)
			{
			%>
				   <div style="float:left; width:150px; padding:5px">Account Number:</div>
				   <div style="float:left;  padding:5px"><%=portletState.getBalanceInquiry().getAccountNumber()%></div>
				   <div style="clear:both">&nbsp;</div>
				   <div style="float:left; width:150px; padding:5px">Type of Account:</div>
				   <div style="float:left; padding:5px"><%=portletState.getBalanceInquiry().getType()%></div>
				   <div style="clear:both">&nbsp;</div>
				   <div style="clear:both; float:left; width:150px; padding:5px">Account Balance:</div>
				   <div style="float:left; padding:5px"><strong>ZMW <%=new Util().roundUpAmount(portletState.getBalanceInquiry().getAvailableBalance())%></strong></div>
			 <%
			  }
			  %>
		</div>
	</div>
		
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

function handleButtonAction(action, id, balance){
	
	var proceed = true;
	if(action=='getBalance')
	{
		proceed = true;
	}
	if(action=='getBalance1')
	{
		proceed = true;
	}
	if(action=='initiatePayment')
	{
		proceed = true;
	}
	
	if(proceed==true)
	{
		if(action=='pay')
		{
			////alert(1);
			if(new String(id).length > 0)
			{
				////alert(2);
				var total = changeTotalAmount(id);
				////alert(total);
				var amt = parseFloat(balance);
				////alert(amt);
				if(amt>total)
				{
					if(confirm("Confirm you wish to pay for this domestic tax!"))
					{
						document.getElementById('selectedDomTaxAction').value = action;
						document.getElementById('selectedDomTax').value = id;
						document.getElementById('taxassessmentForm').submit();
					}
				}else
				{
					//alert("You do not have enough funds in your account to pay for the selected domestic tax(es)");
				}
			}else
			{
				//alert("We experienced problems retrieving your bank balance. Please click on VIEW MY BANK BALANCE button first before trying again");
			}
		}
		else if(action=='initiatePayment')
		{
			if(new String(id).length > 0)
			{
				//alert(11);
				var total = changeTotalAmount(id);
				//alert(total);

				var amt = parseFloat(id);
				//alert(22);
				if(amt>total)
				{
					//alert(3);
					document.getElementById('selectedDomTaxAction').value = action;
					document.getElementById('selectedDomTax').value = id;
					document.getElementById('taxassessmentForm').submit();
				}else
				{
					//alert("You can not add the selected domestic tax to your company's workflow tray as you do not have enough funds in your account." +
							//"Try choosing domestic taxes having lesser amounts");
				}
			}else
			{
				//alert("First click on VIEW MY COMPANYS ACCOUNT BALANCE button to view your account balance before trying again");
			}
			
		}else if(action=='getBalance')
		{
			document.getElementById('selectedDomTaxAction').value = action;
			document.getElementById('taxassessmentForm').submit();
		}else if(action=='getBalance1')
		{
			document.getElementById('selectedDomTaxAction').value = action;
			document.getElementById('taxassessmentForm').submit();
		}
	}else
	{
		//alert('You must select at least one tax item before carrying out this action. Select tax items by clicking on their respective checkboxes');
	}
}




function changeTotalAmount(id)
{
	var total = 0.0;
	////alert(2);
	  ////alert(3);
 	var id1 = "amtChange" + id;
	var amtChange = document.getElementById(id1).value;
	total = total + parseFloat(amtChange);
	var total1 = total;
	////alert(14);
	total1 = total.formatMoney(2);
	////alert(15);
	document.getElementById('totalAmountDiv').innerHTML = "<strong>Total Amount Payable: ZMW " + total1 + "</strong>";
	////alert(16);
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

