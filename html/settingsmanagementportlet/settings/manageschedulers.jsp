<%@page import="com.probase.smartpay.admin.settingsmanagement.SettingsManagementPortletState"%>
<%@page import="com.probase.smartpay.admin.settingsmanagement.SettingsManagementPortletState.*"%>
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
<%@page import="smartpay.entity.Company"%>
<%@page import="smartpay.entity.FeeDescription"%>
<%@page import="smartpay.entity.enumerations.SmartPayConstants"%>
<%@page import="smartpay.entity.Settings"%>
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
<link href="<%=jqueryUICssUrl%>" rel="stylesheet" type="text/css" /><style type="text/css">
<!--
.style1 {color: red}
-->
</style><%

SettingsManagementPortletState portletState = SettingsManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(SettingsManagementPortletState.class);
Collection<FeeDescription> allFeeDescription= portletState.getAllFeeDescription();
Settings eodsettings = portletState.getEODSetting();
Settings paysplitter = portletState.getPaySplitterSetting();



/*
<jsp:include page="/html/annualreturnsportlet/tabs.jsp" flush="" />*/
%>


<jsp:include page="/html/settingsmanagementportlet/settings/tabs.jsp" flush="" />

<portlet:actionURL var="payurl" name="processAction">
	<portlet:param name="action"
		value="<%=SETTINGS.MANAGE_JOBS_PAY_SPLIT.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="eodurl" name="processAction">
	<portlet:param name="action"
		value="<%=SETTINGS.MANAGE_JOBS_EOD.name()%>" />
</portlet:actionURL>


<div style="padding:10px;">
<div class="panel panel-info">
  	<!-- Default panel contents -->
  	<div class="panel-heading"><strong>Manage Schedules &amp; Jobs </strong></div>
  	<div class="panel-body">
    	<p>Use these to start and shutdown running Schedules &amp; Jobs </p>
  	</div>
  	<div style="padding:10px;">
    <form  id="splitform" action="<%=payurl%>" method="post" enctype="application/x-www-form-urlencoded">
      <div style="padding-bottom:5px"> <strong>Payment Splitter:</strong><span class="taglib-icon-help"><img alt="" aria-labelledby="dplx" onblur="Liferay.Portal.ToolTip.hide();" onfocus="Liferay.Portal.ToolTip.show(this);" onMouseOver="Liferay.Portal.ToolTip.show(this);" src="/html/themes/control_panel/images/portlet/help.png" tabindex="0" aria-controls="aui_3_4_0_1_18202" id="aui_3_4_0_1_18192"><span class="aui-helper-hidden-accessible tooltip-text" id="dplx">Split payments into various collecting accounts</span></span>
          <div>
          	<button name="settingsupdate" id="settingsupdate" onclick="javascript:setAct('splitform', 'actSplit', '2')" class="btn btn-success" style="background-color:#00CC00; color:#FFFFFF" >Start Running Manually</button>
		  	<%
			if(paysplitter!=null && paysplitter.getValue().equals("0"))
			{
			%>
            <button name="settingsupdate" id="settingsupdate" onclick="javascript:setAct('splitform', 'actSplit', '1')" class="btn btn-success" style="background-color:#00CC00; color:#FFFFFF" >Start Running Automatically</button>
			<%
			}else if(paysplitter!=null && paysplitter.getValue().equals("1"))
			{
			%>
			<button name="settingsupdate" id="settingsupdate" onclick="javascript:setAct('splitform', 'actSplit', '0')" class="btn btn-success" style="background-color:#FF0000; color:#FFFFFF" >Stop Running Automatically</button>
			<%
			}
			%>
          </div>
          </label>
      </div>
      	<input type="hidden" id="actSplit" name="actSplit" value="-1">
	</form>
	<form  id="eodform" action="<%=eodurl%>" method="post" enctype="application/x-www-form-urlencoded">
	  <div style="padding-bottom:5px"> <strong>End-of-Day Domestic Tax <span style="color:red"></span></strong><span class="taglib-icon-help"><img alt="" aria-labelledby="dplx2" onblur="Liferay.Portal.ToolTip.hide();" onfocus="Liferay.Portal.ToolTip.show(this);" onMouseOver="Liferay.Portal.ToolTip.show(this);" src="/html/themes/control_panel/images/portlet/help.png" tabindex="0" aria-controls="aui_3_4_0_1_18202" id="aui_3_4_0_1_18192"><span class="aui-helper-hidden-accessible tooltip-text" id="dplx2">Send end of day domestic taxes to ZRA</span></span>
        <div>
		<%
		if(eodsettings!=null && eodsettings.getValue().equals("0"))
		{
		%>
		<button name="settingsupdate" id="settingsupdate" onclick="javascript:setAct('eodform', 'actEod', '1')" class="btn btn-success" style="background-color:#00CC00; color:#FFFFFF" >Start Running</button>
		<%
		}else if(eodsettings!=null && eodsettings.getValue().equals("1"))
		{
		%>
		<button name="settingsupdate" id="settingsupdate" onclick="javascript:setAct('eodform', 'actEod', '0')" class="btn btn-success" style="background-color:#FF0000; color:#FFFFFF" >Stop Running</button>
		<%
		}
		%>
        </div>
        </label>
      </div>
      <div style="padding-bottom:20px;"></div>
      <input type="hidden" name="actEod" id="actEod" value="-1">
    </form>
    </div>
</div>
</div>

<script language="javascript">
function setAct(formId, fieldId, value)
{
	if(document.getElementById(fieldId))
	{
		document.getElementById(fieldId).value = value;
		document.getElementById(formId).submit();
	}else
	{
	
	}
}
</script>