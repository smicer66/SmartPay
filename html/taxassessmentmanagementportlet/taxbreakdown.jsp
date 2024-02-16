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
<%@page import="smartpay.entity.enumerations.SmartPayConstants"%>
<%@page import="java.util.ArrayList"%>
<%@page import="smartpay.entity.TpinInfo"%>
<%@page import="java.text.DateFormat"%>
<%@page import="com.probase.smartpay.commins.Util"%>
<%@page import="com.probase.smartpay.commins.TaxBreakDownResponse"%>
<%@page import="smartpay.entity.AuthorizePanelCombination"%>
<%@page import="com.probase.smartpay.commins.TaxDetails"%>
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
Double transactionFee = portletState.getTaxAssessmentManagementPortletUtil().getTransactionFee(portletState.getPortalUser().getCompany().getId());
Boolean proceed2pay = false;
Double totalAmountInit = 0.00;
if(portletState.getPortalUser().getCompany().getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY))
{
	proceed2pay = portletState.getPortalUser().getCompany().getMandatePanelsOn()!=null && portletState.getPortalUser().getCompany().getMandatePanelsOn().equals(Boolean.TRUE) ? true : false;
}
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
Double totalAmount = 0.00;
Double totalSum = 0.00;
if(portletState.getAllAssessmentListing()!=null)
{
 	if(portletState.getAllAssessmentListing().size()>0)
 	{
%>
		<form  id="taxassessmentForm" action="<%=handleAssessmentListing%>" method="post" enctype="application/x-www-form-urlencoded">
			<%
			if(portletState.getTaxBreakDownList()!=null && portletState.getTaxBreakDownList().size()>0)
			{
				
			%>
			<%
				if(portletState.getBalanceInquiry()==null)
				{
			%>
                    <div style="clear:both; padding-bottom:40px; padding-right:10px; padding-left:10px;">
                        
                        <div class="btn-group" style="float:left">
                            <button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('goBackToAssessments', '')">Go Back</button>
                        </div>
                        <div style="float:right">
                          <button type="button" class="btn btn-success" onclick="javascript:handleButtonAction('getBalance', '')">View Company Bank Balance</button>
                          <button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
                            <span class="caret"></span>
                            <span class="sr-only">Toggle Dropdown</span>
                          </button>
                          <ul class="dropdown-menu" role="menu">
                            <%
                            if(portletState.getPortalUser().getCompany().getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY))
                            {
                            %>
                            <li><a href="javascript: handleButtonAction('getBalance', '')">View Company Bank Account Balance</a></li>
                            <%
                            }else
                            {
                            %>
                            <li><a href="javascript: handleButtonAction('getBalance', '')">View Company Bank Account Balance</a></li>
                            <%
                            }
                            %>
                          </ul>
                        </div>
                    </div>
			<%
				}
			%>
				<div class="panel panel-info">
					<!-- Default panel contents -->
					<div class="panel-heading"><strong>Tax BreakDown for Assessment(s)</strong></div>
					<div style="padding:10px;">
						<table width="100%" id="" style="border-bottom: #000 1px solid; clear:both">	
							<thead>
								<tr>
									<th width="14%" style="background-color: #00448D; color:#fff; font-weight:bold; padding: 5px; padding-top:5px; padding-bottom:5px; ">Tax Item Name</th>
								  	<th width="17%" style="background-color: #00448D; color:#fff; font-weight:bold; padding: 5px; padding-top:5px; padding-bottom:5px; ">Registration Number </th>
								  	<th width="35%" style="background-color: #00448D; color:#fff; font-weight:bold; padding: 5px; padding-top:5px; padding-bottom:5px; ">Product</th>
								  	<th width="14%" style="background-color: #00448D; color:#fff; font-weight:bold; padding: 5px; padding-top:5px; padding-bottom:5px; ">Product Code </th>
								  	<th width="20%" style="background-color: #00448D; color:#fff; font-weight:bold; padding: 5px; padding-top:5px; padding-bottom:5px; text-align:right ">Tax Item Amount</th>
								</tr>
							</thead>		
							<tbody>
<%
				
				for(Iterator<HashMap> iterHM = portletState.getTaxBreakDownList().iterator(); iterHM.hasNext();)
				{
					HashMap<String, ArrayList<TaxBreakDownResponse>> hashMap = iterHM.next();
					for (String key : hashMap.keySet()) 
					{
						ArrayList<TaxBreakDownResponse> tbdRList = hashMap.get(key);
						if(tbdRList!=null)
						{
							for(Iterator<TaxBreakDownResponse> itertbdr = tbdRList.iterator(); itertbdr.hasNext();)
							{
								TaxBreakDownResponse taxBreakDownResponse = itertbdr.next();
								Collection<TaxDetails> tdetailsList = taxBreakDownResponse.getTaxDetailListing();
			
								for(Iterator<TaxDetails> itertd = tdetailsList.iterator(); itertd.hasNext();)
								{
									TaxDetails tdetail = itertd.next();
									totalSum = totalSum + tdetail.getAmountToBePaid();
									%>
									<tr style="border-bottom: #fff 1px solid;">
										<td style="background-color: #BFDEFF; padding: 5px; padding-top:5px; padding-bottom:5px; "><%=tdetail.getTaxCode() %></td>
										<td style="background-color: #BFDEFF; padding: 5px; padding-top:5px; padding-bottom:5px; "><%=key %></td>
										<td style="background-color: #BFDEFF; padding: 5px; padding-top:5px; padding-bottom:5px; "><%=taxBreakDownResponse.getProductName()==null ? "N/A" : taxBreakDownResponse.getProductName() %></td>
										<td style="background-color: #BFDEFF; padding: 5px; padding-top:5px; padding-bottom:5px; "><%=taxBreakDownResponse.getProductCode()==null ? "N/A" : taxBreakDownResponse.getProductCode() %></td>
										<td style="background-color: #BFDEFF; padding: 5px; padding-top:5px; padding-bottom:5px; text-align: right "><%=new Util().roundUpAmount(tdetail.getAmountToBePaid()) %></td>
									</tr>
									<%
								}
							}
						}
					}
				}
			%>
									<tr style="border-bottom: #fff 1px solid;">
										<td style="background-color: #BFDEFF; padding: 5px; padding-top:5px; padding-bottom:5px; "><strong>Total</strong></td>
										<td style="background-color: #BFDEFF; padding: 5px; padding-top:5px; padding-bottom:5px; ">&nbsp;</td>
										<td style="background-color: #BFDEFF; padding: 5px; padding-top:5px; padding-bottom:5px; ">&nbsp;</td>
										<td style="background-color: #BFDEFF; padding: 5px; padding-top:5px; padding-bottom:5px; ">&nbsp;</td>
										<td style="background-color: #BFDEFF; padding: 5px; padding-top:5px; padding-bottom:5px; text-align: right "><strong><%=new Util().roundUpAmount(totalSum) %></strong></td>
									</tr>
								</tbody>
							</table>
					</div>
				</div>
				<%
				if(portletState.getBalanceInquiry()==null)
				{
				%>
                    <div style="clear:both; padding-bottom:40px; padding-right:10px; padding-left:10px;">
                        
                        <div class="btn-group" style="float:left">
                            <button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('goBackToAssessments', '')">Go Back</button>
                        </div>
                        <div style="float:right">
                          <button type="button" class="btn btn-success" onclick="javascript:handleButtonAction('getBalance', '')">View Company Bank Account Balance</button>
                        </div>  
                    </div>
				<%
				}else
                {
                %>
                	<div class="panel panel-info">
                        <!-- Default panel contents -->
                        <div class="panel-heading"><strong>Current Bank Balance</strong></div>
                        <table width="100%" id="btable3">		
                          <tbody>
                                <tr>
                                    <td width="20%">Account Number:</td>
                                    <td><%=portletState.getBalanceInquiry().getAccountNumber()%></td>
                                </tr>
                                <tr>
                                    <td width="20%">Type of Account:</td>
                                    <td><%=portletState.getBalanceInquiry().getType()%></td>
                                </tr>
                                <tr>
                                    <td width="20%">Account Balance:</td>
                                    <td><strong>ZMW <%=new Util().roundUpAmount(portletState.getBalanceInquiry().getAvailableBalance())%></strong></td>
                                </tr>
                          </tbody>
                        </table>
                    
                          <input type="hidden" name="selectedAssessmentsClicked" id="SelectedAssessmentsClicked" value="<%=portletState.getSelectedAssessmentsClicked() %>" />
                    </div>
                    <div style="clear:both; padding-bottom:40px; padding-right:10px;">
                        <div class="btn-group" style="float:left">
                            <button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('goBackToAssessments', '')">Go Back</button>
                        </div>
                        <%
                        if(totalSum!=null && totalSum>0.0 && portletState.getBalanceInquiry().getAvailableBalance()>= totalSum)
                        {
                        %>
                        	 
                              <%
                              if(portletState.getPortalUser().getCompany().getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY) && proceed2pay==true)
                              {
                              %>
                                <button style="float:right" type="button" class="btn btn-success" onclick="javascript:handleButtonAction('initiatePayment', '')">Proceed to Initiate Payment</button>
                              <%
                              }else if(portletState.getPortalUser().getCompany().getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY) && proceed2pay==false)
                              {
                              %>
                                <button style="float:right" type="button" class="btn btn-success" onclick="javascript:handleButtonAction('pay', '<%=portletState.getBalanceInquiry().getAvailableBalance()%>')">Proceed to Pay</button>
                              <%
                              }
                              else if (portletState.getPortalUser().getCompany().getCompanyType().equals(CompanyTypeConstants.COMPANY_TYPE_RETAIL_COMPANY))
                              {
                              %>
                                <button style="float:right" type="button" class="btn btn-success" onclick="javascript:handleButtonAction('pay', '<%=portletState.getBalanceInquiry().getAvailableBalance()%>')">Proceed To Pay</button>
                              <%
                              }
                              %>
                              
                        
                        <%
                        }
                        %>
                <%
                }
				%>
			<%
			}
			%>
			<input type="hidden" name="selectedAssessmentsClicked" id="SelectedAssessmentsClicked" value="<%=portletState.getSelectedAssessmentsClicked() %>" />
            <input type="hidden" name="selectedAssessment" id="selectedAssessment" value="" />
			<input type="hidden" name="selectedAssessmentAction" id="selectedAssessmentAction" value="" />
		</form>
<%
 	}
    else
    {
%>
        <div class="panel panel-info">
            <!-- Default panel contents -->
            <div class="panel-heading"><strong>Tax BreakDown for Assessment(s)</strong></div>
            <div class="panel-body">
                <p>Click on an assessment to carry out an action on the assessment</p>
            </div>	
            <table width="100%" id="btable4">				  
                <thead>
              <th>Tax</th>
                    <th>Registration Number</th>
                    <th>Amount</th>
                </thead>
            </table>
              <div>There are currently no unpaid assessments</div>
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
	if(action=='initiatePayment')
	{
		proceed = true;
	}
	if(action=='pay')
	{
		proceed = true;
	}
	if(action=='goBackToAssessments')
	{
		document.getElementById('selectedAssessmentAction').value = action;
		document.getElementById('taxassessmentForm').submit();
	}else
	{
		if(proceed==true)
		{
			if(action=='pay')
			{
				
				if(new String(id).length > 0)
				{
					//alert(2);
					var total = parseFloat(<%=totalSum%>);
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
				document.getElementById('selectedAssessmentAction').value = action;
				document.getElementById('taxassessmentForm').submit();
			}else if(action=='getBalance')
			{
				document.getElementById('selectedAssessmentAction').value = action;
				document.getElementById('taxassessmentForm').submit();
			}
		}else
		{
			alert('You must select at least one tax item before carrying out this action. Select tax items by clicking on their respective checkboxes');
		}
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

</script>