<%@page import="com.probase.smartpay.admin.taxtype.TaxTypePortletState"%>
<%@page import="com.probase.smartpay.admin.taxtype.TaxTypePortletState.*"%>
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

TaxTypePortletState portletState = TaxTypePortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(TaxTypePortletState.class);

/*<jsp:include page="/html/annualreturnsportlet/tabs.jsp" flush="" />*/
%>

<jsp:include page="/html/taxtypeportlet/tabs.jsp" flush="" />


<portlet:actionURL var="portcreator" name="processAction">
	<portlet:param name="action"
		value="<%=TAXTYPE_ACTION.UPDATE_NEW_TAXTYPE_ACTION.name()%>" />
</portlet:actionURL>


<div style="padding:10px; width:900px"> 	
    <div class="panel panel-primary">
		<div class="panel-heading"><span style="color:white; font-weight: bold">Edit A Port</span></div>
		<div class="panel-body">
		    <form  id="portcreatorform" action="<%=portcreator%>" method="post" enctype="application/x-www-form-urlencoded">
		    <fieldset>
		    	<legend>Provide the details required</legend>
		      		  <div> <strong>Tax Type Name:</strong>
				        <div>
				          <input class="form-control" type="text" value="<%=portletState.getTaxTypeName()==null ? "" : portletState.getTaxTypeName() %>" name="taxTypeName" id="taxTypeName" placeholder="Provide The Tax Type Name" />
				        </div>
				        
				      </div>
					  <div style="padding-bottom:10px"> <strong>Tax Type Code:</strong>
				        <div>
				          <input class="form-control" type="text" value="<%=portletState.getTaxTypeCode()==null ? "" : portletState.getTaxTypeCode() %>" name="taxTypeCode" id="taxTypeCode" placeholder="Provide A Tax Type Code" />
				        </div>
				        
				      </div>
				      <div> <strong>Tax Type Account Number:</strong>
				        <div>
				          <input class="form-control" type="text" value="<%=portletState.getTaxTypeAccount()==null ? "" : portletState.getTaxTypeAccount() %>" name="taxTypeAccount" id="taxTypeAccount" placeholder="Provide The Tax Type Account Number" />
				        </div>
				        
				      </div>
					  <div style="padding-bottom:10px"> <strong>Tax Type Account Sort Code:</strong>
				        <div>
				          <input class="form-control" type="text" value="<%=portletState.getTaxTypeCode()==null ? "" : portletState.getTaxTypeCode() %>" name="taxTypeSortCode" id="taxTypeSortCode" placeholder="Provide A Tax Type Account Sort Code" />
				        </div>
				        
				      </div>
		      <div style="clear:both; padding-top:10px; font-size:11px; color:red; font-weight:bold">
		    All fields with red asterisk (*) imply they must be provided</div>
		      <div>
		        <button name="createport" id="createport" class="btn btn-success">Save Tax Type</button>
		      </div>
		    </fieldset>
		    </form>
		</div>
	</div>
</div>
