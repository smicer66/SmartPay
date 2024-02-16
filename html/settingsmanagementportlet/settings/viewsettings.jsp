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
Settings primaryFeeSetting = portletState.getSettingsManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_PRIMARY_FEE_DESCRIPTION);
Settings notifyCorporateFirmEmail = portletState.getSettingsManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_EMAIL);
Settings notifyCorporateFirmSms = portletState.getSettingsManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_FIRM_SMS);
Settings notifyCorporateIndividualEmail = portletState.getSettingsManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_EMAIL);
Settings notifyCorporateIndividualSMS = portletState.getSettingsManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_NOTIFY_CORPORATE_INDIVIDUAL_SMS);
Settings etaxPaymentNotifyEmail = portletState.getSettingsManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_EMAIL);
Settings etaxPaymentNotifySMS = portletState.getSettingsManagementPortletUtil().getSettingByName(SmartPayConstants.SETTINGS_ETAX_PAYMENT_NOTIFY_SMS);
FeeDescription fd = null;
if(primaryFeeSetting!=null && primaryFeeSetting.getValue()!=null)
	try{
	fd = (FeeDescription)portletState.getSettingsManagementPortletUtil().getEntityObjectById(FeeDescription.class, 
			Long.valueOf(primaryFeeSetting.getValue()));
	}catch(NumberFormatException e){}

/*
<jsp:include page="/html/annualreturnsportlet/tabs.jsp" flush="" />*/
%>


<jsp:include page="/html/settingsmanagementportlet/settings/tabs.jsp" flush="" />

<portlet:actionURL var="settingsupdate" name="processAction">
	<portlet:param name="action"
		value="<%=SETTINGS.GO_TO_UPDATE_SETTINGS_INTERFACE.name()%>" />
</portlet:actionURL>

<div style="padding:10px">
	<div class="panel panel-info">
	  	<!-- Default panel contents -->
	  	<div class="panel-heading">View System Settings</div>
	  	<div class="panel-body">
	    	<p>To Update of make changes click on the Update Settings Tab</p>
	  	</div>
		<div style="padding:10px">
		      <div> <strong>Primary Fee Charge For E-Tax Payments:</strong>
		        <div>
		          <%=fd!=null ? fd.getFeeName() : "N/A"%> - <%=fd!=null ? fd.getDescription() : "" %>
		        </div>
		        </label>
		      </div>
			  <div> <strong>Send Notification To Corporate Firms on Registration?</strong>
		        <div>
					<%
					String select0 = "";
					String select1 = "";
					if(notifyCorporateFirmEmail.getValue()!=null && notifyCorporateFirmEmail.getValue().equalsIgnoreCase("0"))
					{
					%>
						No, Do not Send Email Notifications
					<%
					}else if(notifyCorporateFirmEmail.getValue()!=null && notifyCorporateFirmEmail.getValue().equalsIgnoreCase("1"))
					{
					%>
						Yes, Send Email Notifications
					<%
					}
					%>
				  <br>
				  <%
					select0 = "";
					select1 = "";
					if(notifyCorporateFirmSms.getValue()!=null && notifyCorporateFirmSms.getValue().equalsIgnoreCase("0"))
					{
						%>
						No, Do not Send SMS Notifications
						<%
					}else if(notifyCorporateFirmSms.getValue()!=null && notifyCorporateFirmSms.getValue().equalsIgnoreCase("1"))
					{
						%>
						Yes, Send SMS Notifications
						<%
					}
					%>
		        </div>
		        </label>
		      </div>
		      <div><strong>Send Notificication To Corporate Individuals on Corporate Individual Account Creation?</strong>
		        <div>
		          
					<%
					select0 = "";
					select1 = "";
					if(notifyCorporateIndividualEmail.getValue()!=null && notifyCorporateIndividualEmail.getValue().equalsIgnoreCase("0"))
					{
						%>
						No, Do not Send Email Notifications
						<%
					}else if(notifyCorporateIndividualEmail.getValue()!=null && notifyCorporateIndividualEmail.getValue().equalsIgnoreCase("1"))
					{
						%>
						Yes, Send Email Notifications
						<%
					}
					%>
					<br>
					<%
					select0 = "";
					select1 = "";
					if(notifyCorporateIndividualSMS.getValue()!=null && notifyCorporateIndividualSMS.getValue().equalsIgnoreCase("0"))
					{
						%>
						No, Do not Send SMS Notifications
						<%
					}else if(notifyCorporateIndividualSMS.getValue()!=null && notifyCorporateIndividualSMS.getValue().equalsIgnoreCase("1"))
					{
						%>
						Yes, Send SMS Notifications
						<%
					}
					%>
		        </div>
		        </label>
		      </div>
		      <div><strong>On Payment of E-Tax, Forward Notification to this email address and mobile number</strong>
		        <div>
		          <%=etaxPaymentNotifyEmail==null ? "" : etaxPaymentNotifyEmail.getValue() %>
		          <%=etaxPaymentNotifySMS==null ? "" : etaxPaymentNotifySMS.getValue() %>
		        </div>
		        </label>
		      </div>
		      </div>
	    </div>
	</div>
</div>
