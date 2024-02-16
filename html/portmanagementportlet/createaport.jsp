<%@page import="com.probase.smartpay.admin.portmanagement.PortManagementPortletState"%>
<%@page import="com.probase.smartpay.admin.portmanagement.PortManagementPortletState.*"%>
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

PortManagementPortletState portletState = PortManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(PortManagementPortletState.class);

/*<jsp:include page="/html/annualreturnsportlet/tabs.jsp" flush="" />*/
%>

<jsp:include page="/html/portmanagementportlet/tabs.jsp" flush="" />


<portlet:actionURL var="portcreator" name="processAction">
	<portlet:param name="action"
		value="<%=PORTS_ACTION.CREATE_A_NEW_PORT_ACTION.name()%>" />
</portlet:actionURL>



<div style="padding:10px; width:900px"> 	
    <div class="panel panel-primary">
		<div class="panel-heading"><span style="color:white; font-weight: bold">Create A Port</span></div>
		<div class="panel-body">
		    <form  id="portcreatorform" action="<%=portcreator%>" method="post" enctype="application/x-www-form-urlencoded">
		    	<div style="padding:10px;">
				      <div> <strong>Name of Port:</strong>
				        <div>
				          <input class="form-control" type="text" value="<%=portletState.getFullName()==null ? "" : portletState.getFullName() %>" name="fullName" id="fullName" placeholder="Provide The Port Name" />
				        </div>
				        
				      </div>
					  <div style="padding-bottom:10px"> <strong>Port Code:</strong>
				        <div>
				          <input class="form-control" type="text" value="<%=portletState.getPortCode()==null ? "" : portletState.getPortCode() %>" name="portCode" id="portCode" placeholder="Provide A Port Code" />
				        </div>
				        
				      </div>
				      <div style="clear:both; padding-top:10px; font-size:11px; color:red; font-weight:bold">
				    All fields with red asterisk (*) imply they must be provided</div>
				      <div>
				        <button name="createport" id="createport" class="btn btn-success">Create A New Port</button>
				      </div>
		    	</div>
		    </form>
		</div>	  
  	</div>
</div>
