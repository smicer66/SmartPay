<%@page import="com.probase.smartpay.admin.financialamountrestrictionmanagement.FinancialAmountRestrictionManagementPortletState"%>
<%@page import="com.probase.smartpay.admin.financialamountrestrictionmanagement.FinancialAmountRestrictionManagementPortletState.*"%>
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
<%@page import="smartpay.entity.AuthorizePanelCombination"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
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

FinancialAmountRestrictionManagementPortletState portletState = FinancialAmountRestrictionManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(FinancialAmountRestrictionManagementPortletState.class);

%>

<jsp:include page="/html/financialamountrestrictionmanagementportlet/tabs.jsp" flush="" />


<portlet:actionURL var="financialAmountRestrictionUrl" name="processAction">
	<portlet:param name="action"
		value="<%=FINANCIAL_AMOUNT_RESTRICTION.EDIT_A_FINANCIAL_AMOUNT_RESTRICTION.name()%>" />
</portlet:actionURL>

<div style="padding:10px; width:900px"> 	
    <div class="panel panel-primary">
		<div class="panel-heading"><span style="color:white; font-weight: bold">Update Financial Restriction</span></div>
		<div class="panel-body">
		    <form  id="financialAmountRestrictionForm" action="<%=financialAmountRestrictionUrl%>" method="post" enctype="application/x-www-form-urlencoded">
		    <fieldset>
		    	<div style="padding:10px;">
				      <div> <strong>Minimum Payment Threshold:</strong>
				        <div>
				          <input onkeypress="return onlyDoubleKey(event, 'minpaymentthreshold')" class="form-control" type="text" value="<%=portletState.getMinimumPaymentThreshold()==null ? "" : portletState.getMinimumPaymentThreshold() %>" name="minpaymentthreshold" id="minpaymentthreshold" placeholder="Provide The Minimum Payment Threshold" />
				        </div>
				        </label>
				      </div>
					  <div> <strong>Maximum Payment Threshold:</strong>
				        <div>
				          <input onkeypress="return onlyDoubleKey(event, 'maxpaymentthreshold')" class="form-control" type="text" value="<%=portletState.getMaximumPaymentThreshold()==null ? "" : portletState.getMaximumPaymentThreshold() %>" name="maxpaymentthreshold" id="maxpaymentthreshold" placeholder="Provide The Maximum Payment Threshold" />
				        </div>
				        </label>
				      </div>
				      <div style="clear:both; padding-top:10px; font-size:11px; color:red; font-weight:bold">
				    All fields with red asterisk (*) imply they must be provided</div>
				      <div>
				        <button name="editFinancialAmountRestriction" id="editFinancialAmountRestriction" class="btn btn-success">Save Financial Restriction</button>
				      </div>
				      <%
				      	if(portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
						{
				      %>
				      <input type="hidden" name="companySelected" id="companySelected" value="<%=portletState.getSelectedCompanyId()%>">
				      <%
						}
				      %>
		      	</div>
		    </fieldset>
		    </form>
		</div>
  	</div>
</div>




<script type="text/javascript">

function onlyDoubleKey(e, elementId)
{
	var src = (e.srcElement || e.target);
	var unicode=e.charCode? e.charCode : e.keyCode;
	
	var check = false;
	var lenAfter = document.getElementById(elementId).value.length - document.getElementById(elementId).value.indexOf(".");
	if(document.getElementById(elementId).value.length>0 && 
			document.getElementById(elementId).value.indexOf(".")==-1)
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
