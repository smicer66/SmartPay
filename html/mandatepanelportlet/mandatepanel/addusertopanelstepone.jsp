<%@page import="com.probase.smartpay.admin.mandatepanelmanagement.MandatePanelPortletState"%>
<%@page import="com.probase.smartpay.admin.mandatepanelmanagement.MandatePanelPortletState.*"%>
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
<%@page import="smartpay.entity.FinancialAmountRestriction"%>
<%@page import="smartpay.entity.AuthorizePanel"%>
<%@page import="smartpay.entity.AuthorizePanelCombination"%>
<%@page import="smartpay.entity.PortalUser"%>
<%@page import="smartpay.entity.enumerations.PanelTypeConstants"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Arrays"%>
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

MandatePanelPortletState portletState = MandatePanelPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(MandatePanelPortletState.class);

/*<jsp:include page="/html/annualreturnsportlet/tabs.jsp" flush="" />*/
%>

<jsp:include page="/html/mandatepanelportlet/mandatepanel/tabs.jsp" flush="" />


<portlet:actionURL var="panelusermappingurl" name="processAction">
	<portlet:param name="action"
		value="<%=AUTHORISATION_PANEL.MAP_PORTAL_USER_STEP_ONE.name()%>" />
</portlet:actionURL>


<div style="padding:10px;"> 	
    <div class="panel  panel-primary">
		<div class="panel-heading"><span style="color:white; font-weight: bold">Add Company Staff to Mandate Panels</span></div>
		<div class="panel-body">
		    <form  id="panelusermapping" action="<%=panelusermappingurl%>" method="post" enctype="application/x-www-form-urlencoded">
		    <fieldset>
		    	<legend>Provide the details required</legend>
			  
		      <div> <strong>Mandate Panels:</strong>
		        <div>
		          <select name="mandatepanel" id="mandatepanel" class="form-control">
				  	<option value="-1">-Select A Mandate Panel-</option>
					<%
					if(portletState.getAllAuthorizePanel()!=null && portletState.getAllAuthorizePanel().size()>0)
					{
						for(Iterator<AuthorizePanel> iterAP = portletState.getAllAuthorizePanel().iterator(); iterAP.hasNext();)
						{
							AuthorizePanel ap = iterAP.next();
						%>
							<option value="<%=ap.getId()%>"><%=ap.getPanelName() + " - " + ap.getAuthorizeType().getValue() %></option>
						<%
						}
					}
					%>
				  </select>
		        </div>
		        </label>
		      </div>
		      <div style="padding-top:10px">
		        <button name="addUserToPanelStepOne" id="addUserToPanelStepOne" class="btn btn-success">Next</button>
		      </div>
		    </fieldset>
		    </form>
		</div>
  	</div>
</div>
