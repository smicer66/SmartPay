<%@page import="com.probase.smartpay.admin.bankbranchmanagement.BankBranchManagementPortletState"%>
<%@page import="com.probase.smartpay.admin.bankbranchmanagement.BankBranchManagementPortletState.*"%>
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

BankBranchManagementPortletState portletState = BankBranchManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(BankBranchManagementPortletState.class);
Collection<BankBranches> bankbranches = portletState.getAllBankBranchListing();

%>

<jsp:include page="/html/bankbranchmanagementportlet/bankbranch/tabs.jsp" flush="" />


<portlet:actionURL var="bankbranchcreator" name="processAction">
	<portlet:param name="action"
		value="<%=BANK_BRANCH_ACTION.CREATE_A_BANK_BRANCH.name()%>" />
</portlet:actionURL>


<div style="padding:10px; width:900px;">
	<h2>Create New Bank Branch</h2>
	
	<div class="panel panel-info">
	  	<!-- Default panel contents -->
	  	<div class="panel-heading">New Bank Branch</div>
	    <form  id="bankbranchcreatorform" action="<%=bankbranchcreator%>" method="post" enctype="application/x-www-form-urlencoded">
	    <fieldset>
	    	<div style="padding:10px;">
			    	<legend>Provide the details required</legend>
			      <div style="padding-bottom:10px"> <strong>Bank Branch Name:<span style="color:red">*</span></strong>
			        <div>
			          <input class="form-control" type="text" value="<%=portletState.getBankBranchName()==null ? "" : portletState.getBankBranchName() %>" name="bankbranchname" id="bankbranchname" placeholder="Provide The Bank Branch Name" />
			        </div>
			        </label>
			      </div>
				  <div> <strong>Bank Branch Sort Code:<span style="color:red">*</span></strong>
			        <div>
			          <input onkeypress="return onlyNumKey(event)" class="form-control" type="text" value="<%=portletState.getBankBranchCode()==null ? "" : portletState.getBankBranchCode() %>" name="bankbranchcode" id="bankbranchcode" placeholder="Provide The Bank Branch Code" />
			        </div>
			        </label>
			      </div>
			      <div style="padding-top:20px;">
						<button type="submit" class="btn btn-success">Create A Bank Branch</button>
					  	<div style="clear:both; padding-top:10px; font-size:11px; color:red; font-weight:bold">
						All fields with red asterisk (*) imply they must be provided</div>
			      </div>
			</div>
	    </fieldset>
	    </form>
	</div>
</div>



<script type="text/javascript">
$(document).ready(function() {
    $('#btable').dataTable();
} );


function handleButtonAction(id){
	
	document.getElementById(id).submit();
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