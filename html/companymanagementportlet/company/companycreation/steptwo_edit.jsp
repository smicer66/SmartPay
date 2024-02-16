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
<%@page import="smartpay.entity.enumerations.CompanyTypeConstants"%>
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
BankBranches branchName = (BankBranches)portletState.getCompanyManagementPortletUtil().getEntityObjectById(BankBranches.class, Long.valueOf(portletState.getSelectedBankBranchId()));

/*<jsp:include page="/html/annualreturnsportlet/tabs.jsp" flush="" />*/
%>

<jsp:include page="/html/companymanagementportlet/company/tabs.jsp" flush="" />


<portlet:actionURL var="proceedToStepTwo" name="processAction">
	<portlet:param name="action"
		value="<%=COMPANY_CREATION.EDIT_A_COMPANY_STEP_TWO.name()%>" />
</portlet:actionURL>

<div style="padding:10px; width:900px">
  	
    <h2>
		<strong>Update Company Profile</strong>
	</h2>
    <form  id="startRegFormId" action="<%=proceedToStepTwo%>" method="post" enctype="application/x-www-form-urlencoded">
		<div class="panel panel-info">
			<!-- Default panel contents -->
			<div class="panel-heading"><strong>Step 2 of 2: Confirm Changes to Company Profile Before Saving</strong></div>
			<div class="panel-body">
				<div style="padding:10px">
					<fieldset>
						<legend>Preview to ensure the data provided is correct</legend>
					  <div> <strong>Corporate Company Name:</strong>
						<div>
						  <%=portletState.getCompanyname()==null ? "" : portletState.getCompanyname() %>
						</div>
						</label>
					  </div>
					  <div> <strong>Company Registration Number:</strong>
						<div>
						  <%=portletState.getCompanyrcnumber()==null ? "" : portletState.getCompanyrcnumber() %>
						</div>
						</label>
					  </div>
					  <div><strong>Contact Address Line 1:</strong>
						<div>
						  <%=portletState.getLine1addressofcompany()==null ? "" : portletState.getLine1addressofcompany() %>
						</div>
						</label>
					  </div>
					  <div><strong>Contact Address Line 2:</strong>
						<div>
						  <%=portletState.getLine2addressofcompany()==null ? "" : portletState.getLine2addressofcompany() %>
						</div>
						</label>
					  </div>
					  <div><strong>Contact Mobile Number:</strong>
						<div>
						  <%=portletState.getCompanycontactphonenumber()==null ? "" : portletState.getCompanycontactphonenumber() %>
						</div>
						</label>
					  </div>
					  <div><strong>Contact Email Address:</strong>
						<div>
						  <%=portletState.getCompanyemailaddress()==null ? "" : portletState.getCompanyemailaddress() %>
						</div>
						</label>
					  </div>
					  <div style="padding-bottom:5px; width:30%; float:left"><strong>Bank Branch:</strong> <%=branchName==null ? "" : branchName.getName()%></div>
					  <div style="padding-bottom:5px; width:50%; float:left"><strong>Bank Account Number:</strong> <%=portletState.getBankNumber()==null ? "" : portletState.getBankNumber()%></div>
					  <div style="padding-bottom:5px; clear:both"> <strong>Specify Company Type:<span style="color:red">*</span></strong>
                            <div><% if(portletState.getSelectedCompanyType()!=null && portletState.getSelectedCompanyType().equalsIgnoreCase(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY.getValue()))
							{%>Corporate Company<%}else{%>Retail Company<%}%>
                            </div>
					    </div>
						<%
					    if(portletState.getSelectedCompanyType().equalsIgnoreCase(CompanyTypeConstants.COMPANY_TYPE_CORPORATE_COMPANY.getValue()))
					    {
					    %>
						<div style="padding-bottom:5px"> <strong>Company Classification:<span style="color:red">*</span></strong>
                            <div><%if(portletState.getSelectedCompanyClass()!=null && portletState.getSelectedCompanyClass().equals("1")){%>Agent<%}else{%>Sole Trader<%}%>
                            </div>
					    </div>
					    <%
					    }
					    %>
					  <div style="padding-top:20px; clear:both">
							<div>
								<button class="btn btn-danger" id="cancelcreatecompany" style="float:left" onclick="javascript:handleButtonAction('goBack'); return false;">Go Back</button>
								<button class="btn btn-success" id="createcompany" style="float:right" onclick="javascript:handleButtonAction('yessave'); return false;">Yes, Save Company Details</button>
							</div>
					  </div>
					</fieldset>
				</div>
			</div>
		</div>
		<input type="hidden" name="companyeditaction" id="companyeditaction" value="" />
	</form>
</div>


<script language="javascript" type="text/javascript">
function handleButtonAction(value)
{
	document.getElementById('companyeditaction').value=value;
	document.getElementById('startRegFormId').submit();
}
</script>