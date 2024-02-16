<%@page import="com.probase.smartpay.admin.companymanagement.CompanyManagementPortletState"%>
<%@page import="com.probase.smartpay.admin.companymanagement.CompanyManagementPortletState.COMPANY_CREATION"%>
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
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.enumerations.CompanyTypeConstants"%>
<%@page import="smartpay.entity.AuthorizePanelCombination"%>
<%@page import="smartpay.entity.AuthorizePanel"%>
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

CompanyManagementPortletState portletState = CompanyManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(CompanyManagementPortletState.class);
Collection<BankBranches> bankbranches = portletState.getAllBankBranchListing();

/*<jsp:include page="/html/annualreturnsportlet/tabs.jsp" flush="" />*/


%>

<jsp:include page="/html/companymanagementportlet/company/tabs.jsp" flush="" />


<portlet:actionURL var="proceedToStepTwo" name="processAction">
	<portlet:param name="action"
		value="<%=COMPANY_CREATION.CREATE_A_COMPANY_STEP_ONE.name()%>" />
</portlet:actionURL>

<div style="padding:10px; width:900px">
	<h2>Create A Corporate Firm</h2>
	<form  id="startRegFormId" action="<%=proceedToStepTwo%>" method="post" enctype="application/x-www-form-urlencoded">
		<div style="padding:10px;">
			<div class="panel panel-info">
				<!-- Default panel contents -->
				<div class="panel-heading"><strong>Step 1 of 2: Create A Corporate Firm Profile</strong></div>
				<div class="panel-body">
					<div style="padding:10px;">
						<fieldset style="padding:10px;">
						  <div style="padding-bottom:5px"> <strong>Corporate Company Name:<span style="color:red">*</span></strong>
							<div>
							  <input class="form-control" type="text" value="<%=portletState.getCompanyname()==null ? "" : portletState.getCompanyname() %>" name="companyName" id="companyName" placeholder="Provide The Company Name" />
							</div>
						  </div>
						  <div style="padding-bottom:5px"> <strong>Company Registration Number:<span style="color:red">*</span></strong>
							<div>
							  <input onkeypress="return onlyNumKey(event)" class="form-control" type="text" value="<%=portletState.getCompanyrcnumber()==null ? "" : portletState.getCompanyrcnumber() %>" name="companyRCNumber" id="companyRCNumber" placeholder="Provide The Company Registration Number" />
							</div>
						  </div>
						  <div style="padding-bottom:5px"> <strong>Contact Address Line 1:</strong>
							<div>
							  <input class="form-control" type="text" value="<%=portletState.getLine1addressofcompany()==null ? "" : portletState.getLine1addressofcompany() %>" name="contactAddressLine1" id="contactAddressLine1" placeholder="1st Line of Address" />
							</div>
						  </div>
						  <div style="padding-bottom:5px"> <strong>Contact Address Line 2:</strong>
							<div>
							  <input class="form-control" type="text" value="<%=portletState.getLine2addressofcompany()==null ? "" : portletState.getLine2addressofcompany() %>" name="contactAddressLine2" id="contactAddressLine2" placeholder="2nd Line of Address" />
							</div>
						  </div>
						  <div style="padding-bottom:5px"> <strong>Contact Mobile Number:<span style="color:red">*</span></strong>
							<div>
							  <input onkeypress="return onlyNumKey(event)" class="form-control" type="text" value="<%=portletState.getCompanycontactphonenumber()==null ? "" : portletState.getCompanycontactphonenumber() %>" name="contactMobileNumber" id="contactMobileNumber" placeholder="Provide Contact Mobile Number" />
							</div>
						  </div>
						  <div style="padding-bottom:5px"> <strong>Contact Email Address:<span style="color:red">*</span></strong>
							<div>
							  <input class="form-control" type="text" value="<%=portletState.getCompanyemailaddress()==null ? "" : portletState.getCompanyemailaddress() %>" name="contactEmailAddress" id="contactEmailAddress" placeholder="Provide Contact Mobile Number" />
							</div>
						  </div>
						  <div style="padding-bottom:5px"> <strong>Tax Payer Identification Number:<span style="color:red">*</span></strong>
							<div>
							  <input onkeypress="return onlyNumKey(event)" class="form-control" type="text" value="<%=portletState.getTpin()==null ? "" : portletState.getTpin() %>" name="tpin" id="tpin" placeholder="Provide Company's TPIN" />
							</div>
						  </div>
					    <div style="padding-bottom:5px">
							<div style="padding-bottom:5px; width:50%; float:left">
							  <div><strong>Bank Branch:<span style="color:red">*</span></strong></div>
							  <select name="bankBranch" id="bankBranch" class="form-control">
								<option value="-1">-Select Bank Branch-</option>
								<%if(bankbranches!=null)
								{
									for(Iterator<BankBranches> iterB = bankbranches.iterator(); iterB.hasNext();)
									{
										BankBranches bb = iterB.next();
										String selected="";
										try
										{
											if(Long.valueOf(portletState.getSelectedBankBranchId()).equals(bb.getId()))
											{
												selected = "selected='selected'";
											}
										}catch(NumberFormatException e)
										{
											
										}
									%>
								<option <%=selected %> value="<%=bb.getId()%>"><%=bb.getName()%> <%=bb.getBankCode()!=null ? (" - Bank Sort Code: " + bb.getBankCode()) : "" %></option>
								<%
									}
								} %>
							  </select>
							</div>
							<div style="padding-bottom:5px; width:50%; float:left">
							<div><strong>Bank Account Number:<span style="color:red">*</span></strong></div>
							  <input onkeypress="return onlyNumKey(event)" class="form-control" type="text" value="<%=portletState.getBankNumber()==null ? "" : portletState.getBankNumber() %>" name="accountNumber" id="accountNumber" placeholder="Type Bank Account Number" />
							</div>
						  </div>
						<div style="padding-bottom:5px; clear:both"> <strong>Specify Company Type:<span style="color:red">*</span></strong>
                            <div>
                              <select name="companyType" id="companyType" class="form-control" onchange="javascript:handleCompanyTypeOnChange('companyType')">
                                <option value="-1">-Select Company Type-</option>
                                <%
									String selected = ""; 
									if(portletState.getSelectedCompanyType()!=null && portletState.getSelectedCompanyType().equalsIgnoreCase(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY.getValue()))
									{
										selected = "selected='selected'";
									}
								%>
                                <option <%=selected %> value="<%=CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY.getValue()%>"><%=CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY.getValue().replace("_", " ") %></option>
                                <%
									selected = "";
									if(portletState.getSelectedCompanyType()!=null && portletState.getSelectedCompanyType().equalsIgnoreCase(CompanyTypeConstants.COMPANY_TYPE_RETAIL_COMPANY.getValue()))
									{
										selected = "selected='selected'";
									}
								%>
                                <option <%=selected %> value="<%=CompanyTypeConstants.COMPANY_TYPE_RETAIL_COMPANY.getValue()%>"><%=CompanyTypeConstants.COMPANY_TYPE_RETAIL_COMPANY.getValue().replace("_", " ") %></option>
                              </select>
                            </div>
					    </div>
						
						<%
					    String display = "none";
						String display1 = "none";
					    if(portletState.getSelectedCompanyType()!=null && portletState.getSelectedCompanyType().equalsIgnoreCase(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY.getValue()))
						{
					    	display = "block";
					    	display1 = "block";
						}
					    %>
							<div style="padding-bottom:5px; display:<%=display %>" id="companyClass1"> <strong>Company Classification:<span style="color:red">*</span></strong>
                            <div>
                              <select name="classification" id="classification" class="form-control">
                                <option value="-1">-Select Company Classification-</option>
                                <%
									selected = ""; 
									if(portletState.getSelectedCompanyClass()!=null && portletState.getSelectedCompanyClass().equals("1"))
									{
										selected = "selected='selected'";
									}
								%>
                                <option <%=selected %> value="1">This Company operates as an Agent</option>
                                <%
									selected = "";
									if(portletState.getSelectedCompanyClass()!=null && portletState.getSelectedCompanyClass().equals("0"))
									{
										selected = "selected='selected'";
									}
								%>
                                <option <%=selected %> value="0">This Company operates as a Sole Trader</option>
                              </select>
                            </div>
					    </div>
					    
					    
					    <div style="padding-bottom:5px; display:<%=display1 %>" id="companyClass2"> <strong>Turn On Authorisation Mandate Process<span style="color:red">*</span></strong>
                            <div>
                              <select name="mandatePanelsOn" id="mandatePanelsOn" class="form-control">
                                <option value="-1">-Select An Answer-</option>
                                <%
									selected = ""; 
									if(portletState.getMandatePanelsOn()!=null && portletState.getMandatePanelsOn().equals("1"))
									{
										selected = "selected='selected'";
									}
								%>
                                <option <%=selected %> value="1">Yes</option>
                                <%
									selected = "";
									if(portletState.getMandatePanelsOn()!=null && portletState.getMandatePanelsOn().equals("0"))
									{
										selected = "selected='selected'";
									}
								%>
                                <option <%=selected %> value="0">No</option>
                              </select>
                            </div>
					    </div>
					    
					    
						<div style="padding-top:20px; clear:both">
							<button class="btn btn-success" id="createcompany">Create Company</button>
							<div style="clear:both; padding-top:10px; font-size:11px; color:red; font-weight:bold">
							All fields with red asterisk (*) imply they must be provided</div>
					  	</div>
						</fieldset>
					</div>
				</div>
			</div>
		</div>
	</form>
</div>


<script type="text/javascript">

function handleCompanyTypeOnChange(d)
{
	var e = document.getElementById(d);
	var strUser = e.options[e.selectedIndex].value;
	if(e.selectedIndex==1)
	{
		document.getElementById('companyClass1').style.display = 'block';
		document.getElementById('companyClass2').style.display = 'block';
	}else
	{
		document.getElementById('companyClass1').style.display = 'none';
		document.getElementById('companyClass2').style.display = 'none';
	}	
}



function handleAuthPanelSelect(divId, count)
{
	var selectedAuth = [];
	var notifyChangeDivStr = '';
	for(c = 0; c<count; c++)
	{
		var b = 'maxcount' + c;
		var d = 'authpanel' + c;
		var e = document.getElementById(d);
		var strUser = e.options[e.selectedIndex].value;
		var strUserText = e.options[e.selectedIndex].text;
		var maxCount = document.getElementById(b).value;
		if(strUser!="-1")
		{
			if(selectedAuth.indexOf(strUser)==-1)
			{
				selectedAuth.push(strUser);
				//document.getElementById(d).disabled = true;
			}else
			{
				alert("You have already selected this Authorisation Panel previously. You can only select one Authorisation panel per drop down menu");
				document.getElementById(divId).selectedIndex = 0;
			}
			try
			{
				if(isNaN(parseInt(maxCount)))
				{
					
				}else
				{
					notifyChangeDivStr = notifyChangeDivStr + maxCount + '-' + strUserText + ' ';
				}
			}catch(Errror){
				alert("Only numeric values are allowed in the Maximum Number of Individuals field")
			}
		}
		
	}
	
	if(notifyChangeDivStr.trim().length>0)
	{
		document.getElementById('notifyChangeDiv').innerHTML = notifyChangeDivStr.trim();
	}
	
}


function restrictNumber(eId, selectDivId, count)
{
	document.getElementById(eId).onchange = function() {
	    this.value = this.value.match(/\d*\.?\d+/);
	};	
	if(document.getElementById(eId).value.length==0)
	{
		//document.getElementById(selectDivId).disabled=false;
	}else
	{
		handleAuthPanelSelect(selectDivId, count)
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
