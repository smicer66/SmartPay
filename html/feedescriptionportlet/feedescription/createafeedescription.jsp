<%@page import="com.probase.smartpay.admin.feedescriptionmanagement.FeeDescriptionPortletState"%>
<%@page import="com.probase.smartpay.admin.feedescriptionmanagement.FeeDescriptionPortletState.*"%>
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

FeeDescriptionPortletState portletState = FeeDescriptionPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(FeeDescriptionPortletState.class);

/*<jsp:include page="/html/annualreturnsportlet/tabs.jsp" flush="" />*/
%>

<div style="padding-left:10px; padding-right:10px">
<jsp:include page="/html/feedescriptionportlet/feedescription/tabs.jsp" flush="" />
</div>

<portlet:actionURL var="feedescriptioncreator" name="processAction">
	<portlet:param name="action"
		value="<%=FEE_DESCRIPTION.CREATE_A_FEE_DESCRIPTION.name()%>" />
</portlet:actionURL>

<h2>Create A Transaction Fee</h2>


<div style="padding-left:10px; padding-right:10px">
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold">Create A New Transaction Fee</span></div>
	  <div class="panel-body">
	    <form  id=newfeetransactionform" action="<%=feedescriptioncreator%>" method="post" enctype="application/x-www-form-urlencoded">
	    <fieldset>
	    	<legend>Provide the details required</legend>
	      <div style="padding-bottom:5px"> <strong>Name of Fee:<span style="color:red">*</span></strong>
	        <div>
	          <input class="form-control" type="text" value="<%=portletState.getFeeDescriptionName()==null ? "" : portletState.getFeeDescriptionName() %>" name="feeDescriptionName" id="feeDescriptionName" placeholder="Provide The Fee Name" />
	        </div>
	        
	      </div>
		  <div style="padding-bottom:5px"> <strong>Short Description:</strong>
	        <div>
	          <input class="form-control" type="text" value="<%=portletState.getFeeDescriptionDetail()==null ? "" : portletState.getFeeDescriptionDetail() %>" name="feeDescriptionDetail" id="feeDescriptionDetail" placeholder="Provide A Short Description of the fee" />
	        </div>
	        
	      </div>
		  <div style="padding-bottom:5px"> <strong>Amount Applicable:<span style="color:red">*</span></strong>
	        <div>
	          <input onkeypress="return onlyDoubleKey(event, 'feeDescriptionAmount')" class="form-control" type="text" value="<%=portletState.getFeeDescriptionAmount()==null ? "" : portletState.getFeeDescriptionAmount() %>" name="feeDescriptionAmount" id="feeDescriptionAmount" placeholder="Provide Amount Applicable" />
	        </div>
	        
	      </div>
		  <div style="padding-bottom:5px"> <input value="1" type="checkbox" <%=portletState.isPrimaryFeeChecked()!=null && portletState.isPrimaryFeeChecked()==true  ? "checked='checked'" : ""  %> name="primaryFee" id="primaryFee"  /> <strong>This is a Primary Fee!</strong>
	          <div style="clear:both; padding-bottom:10px; font-size:11px; color:red; font-weight:bold">
		    A Primary Fee can be set as the default applicable fee during transactions</div>
	        
	      </div>
	      <div style="padding-bottom:5px">
	        <button name="createFeeDescription" id="createFeeDescription" class="btn btn-success">Create A Fee Description</button>
		      <div style="clear:both; padding-top:10px; font-size:11px; color:red; font-weight:bold">
		    All fields with red asterisk (*) imply they must be provided</div>
	      </div>
	    </fieldset>
	    </form>
	  </div>
	</div>
</div>




<script type="text/javascript">


function handleButtonAction(){
	
	document.getElementById('newfeetransactionform').submit();
}


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