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
Settings primaryFeeSetting = portletState.getPrimaryFeeSetting();
Settings notifyCorporateFirmEmail = portletState.getNotifyCorporateFirmEmail();
Settings notifyCorporateFirmSms = portletState.getNotifyCorporateFirmSms();
Settings notifyCorporateIndividualEmail = portletState.getNotifyCorporateIndividualEmail();
Settings notifyCorporateIndividualSMS = portletState.getNotifyCorporateIndividualSMS();
Settings etaxPaymentNotifyEmail = portletState.getEtaxPaymentNotifyEmail();
Settings etaxPaymentNotifySMS = portletState.getEtaxPaymentNotifySMS();
Settings systemUrl = portletState.getSystemUrl();
Settings platformCountry = portletState.getPlatformCountry();
Settings platformBank = portletState.getPlatformBank();
Settings taxCompanyAccount = portletState.getSettingsZRAAccount();
Settings taxCompanySortCode = portletState.getSettingsZRASortCode();
Settings sendingEmail = portletState.getSendingEmail();
Settings sendingEmailPassword = portletState.getSendingEmailPassword();
Settings sendingEmailPort = portletState.getSendingEmailPort();
Settings sendingEmailUsername = portletState.getSendingEmailUsername();
Settings approvalProcess = portletState.getApprovalProcess();
Settings twoStepLogin = portletState.getTwoStepLogin();
Settings applicationName = portletState.getApplicationName();
Settings mobileApplicationName = portletState.getMobileApplicationName();
Settings proxyHost = portletState.getProxyHost();
Settings proxyPort = portletState.getProxyPort();
Settings proxyUsername = portletState.getProxyUsername();
Settings proxyPassword = portletState.getProxyPassword();
Settings bankName = portletState.getBankName();
Settings currency = portletState.getCurrency();
Settings bankPaymentWebServiceUrl = portletState.getBankPaymentWebServiceUrl();
Settings zraWebServiceUrl = portletState.getZraWebServiceUrl();



/*
<jsp:include page="/html/annualreturnsportlet/tabs.jsp" flush="" />*/
%>


<jsp:include page="/html/settingsmanagementportlet/settings/tabs.jsp" flush="" />

<portlet:actionURL var="settingsupdate" name="processAction">
	<portlet:param name="action"
		value="<%=SETTINGS.UPDATE_SETTINGS.name()%>" />
</portlet:actionURL>


<div style="padding:10px;">
<div class="panel panel-info">
  	<!-- Default panel contents -->
  	<div class="panel-heading"><strong>Manage System Settings</strong></div>
  	<div class="panel-body">
    	<p>Be Careful of the choices you make here. Activities carried out here are logged in the audit trail. Make your changes and click on the PROCEED TO SAVE SETTINGS button to Save your changes</p>
  	</div>
  	<div style="padding:10px;">
    <form  id="startRegFormId" action="<%=settingsupdate%>" method="post" enctype="application/x-www-form-urlencoded">
      <div style="padding-bottom:5px"> <strong>Select Primary Fee Charge For E-Tax Payments:<span style="color:red">*</span></strong>
        <div>
          <select name="feedescription" class="form-control">
            <option value="-1">-Select A Fee Description-</option>
            <%if(allFeeDescription!=null && allFeeDescription.size()>0)
            {
            	for(Iterator<FeeDescription> iter = allFeeDescription.iterator(); iter.hasNext();)
            	{
            		FeeDescription fd = iter.next();
            		String selected = "";
            		if(primaryFeeSetting.getValue()!=null && primaryFeeSetting.getValue().equals(Long.toString(fd.getId())))
            		{
            			selected = "selected='selected'";
            		}
            	%>
            <option <%=selected %> value="<%=fd.getId()%>"><%=fd.getFeeName()%> - <%=fd.getDescription() %></option>
            <%
            	}
            } %>
          </select>
        </div>
        </label>
      </div>
	  <div style="padding-bottom:5px"> <strong>Send Notification To Corporate Firms on Registration:<span style="color:red">*</span></strong>
        <div>
          <select name="emailnotificationforcorporatefirm" class="form-control">
            <option value="-1">-Select Option for Email Notification-</option>
			<%
			String select0 = "";
			String select1 = "";
			if(notifyCorporateFirmEmail.getValue()!=null && notifyCorporateFirmEmail.getValue().equalsIgnoreCase("0"))
			{
				select0="selected='selected'";
			}else if(notifyCorporateFirmEmail.getValue()!=null && notifyCorporateFirmEmail.getValue().equalsIgnoreCase("1"))
			{
				select1="selected='selected'";
			}
			%>
            <option value="0" <%=select0%>>No, Do not Send Email Notifications</option>
			<option value="1" <%=select1%>>Yes, Send Email Notifications</option>
          </select>
		  <br />
		  <select name="mobilenotificationforcorporatefirm" class="form-control">
            <option value="-1">-Select Option for SMS Notification-</option>
			<%
			select0 = "";
			select1 = "";
			if(notifyCorporateFirmSms.getValue()!=null && notifyCorporateFirmSms.getValue().equalsIgnoreCase("0"))
			{
				select0="selected='selected'";
			}else if(notifyCorporateFirmSms.getValue()!=null && notifyCorporateFirmSms.getValue().equalsIgnoreCase("1"))
			{
				select1="selected='selected'";
			}
			%>
            <option value="0" <%=select1%>>No, Do not Send SMS Notifications</option>
			<option value="1" <%=select1%>>Yes, Send SMS Notifications</option>
          </select>
        </div>
        </label>
      </div>
      <div style="padding-bottom:5px"><strong>Send Notificication To Corporate Individuals on Corporate Individual Account Creation:</strong>
        <div>
          <select name="emailnotificationforcorporateindivididuals" class="form-control">
            <option value="-1">-Select Option for Email Notification-</option>
			<%
			select0 = "";
			select1 = "";
			if(notifyCorporateIndividualEmail.getValue()!=null && notifyCorporateIndividualEmail.getValue().equalsIgnoreCase("0"))
			{
				select0="selected='selected'";
			}else if(notifyCorporateIndividualEmail.getValue()!=null && notifyCorporateIndividualEmail.getValue().equalsIgnoreCase("1"))
			{
				select1="selected='selected'";
			}
			%>
            <option value="0" <%=select1%>>No, Do not Send Email Notifications</option>
            <option value="1" <%=select1%>>Yes, Send Email Notifications</option>
          </select><br>
          <select name="mobilenotificationforcorporateindivididuals" class="form-control">
            <option value="-1">-Select Option for SMS Notification-</option>
			<%
			select0 = "";
			select1 = "";
			if(notifyCorporateIndividualSMS.getValue()!=null && notifyCorporateIndividualSMS.getValue().equalsIgnoreCase("0"))
			{
				select0="selected='selected'";
			}else if(notifyCorporateIndividualSMS.getValue()!=null && notifyCorporateIndividualSMS.getValue().equalsIgnoreCase("1"))
			{
				select1="selected='selected'";
			}
			%>
            <option value="0" <%=select1%>>No, Do not Send SMS Notifications</option>
            <option value="1" <%=select1%>>Yes, Send SMS Notifications</option>
          </select>
        </div>
        </label>
      </div>
      <div style="padding-bottom:5px"><strong>On Payment of E-Tax, Forward Notification to this email address and mobile number:</strong>
        <div>
          <input class="form-control" type="text" value="<%=etaxPaymentNotifyEmail==null ? "" : etaxPaymentNotifyEmail.getValue() %>" name="etaxpaymentemailnotify" id="etaxpaymentemailnotify" placeholder="Provide An Email Address for Email Notifications" />
          <input class="form-control" type="text" value="<%=etaxPaymentNotifySMS==null ? "" : etaxPaymentNotifySMS.getValue() %>" name="etaxpaymentsmsnotify" id="etaxpaymentsmsnotify" placeholder="Provide A Mobile Number for SMS Notifications" />
        </div>
        </label>
      </div>
	  <div style="padding-bottom:5px"><strong>System URL:</strong>
        <div>
          <input class="form-control" type="text" value="<%=systemUrl==null ? "" : systemUrl.getValue() %>" name="systemUrl" id="systemUrl" placeholder="Provide the URL to access this application" />
        </div>
        </label>
      </div>
	  <div style="padding-bottom:5px"><strong>Platform Country Code:</strong>
        <div>
          <input class="form-control" type="text" value="<%=platformCountry==null ? "" : platformCountry.getValue() %>" name="platformCountry" id="platformCountry" placeholder="Provide the Platform Country Code e.g. ZM for Zambia" />
        </div>
        </label>
      </div>
	  <div style="padding-bottom:5px"><strong>Platform Bank Code:</strong>
        <div>
          <input class="form-control" type="text" value="<%=platformBank==null ? "" : platformBank.getValue() %>" name="platformBank" id="platformBank" placeholder="Provide the Platform Bank Code e.g. SCB for Standard Chartered" />
        </div>
        </label>
      </div>
	  <div style="padding-bottom:5px"><strong>Tax Company Bank Account Number:</strong>
        <div>
          <input class="form-control" type="text" value="<%=taxCompanyAccount==null ? "" : taxCompanyAccount.getValue() %>" name="taxCompanyAccount" id="taxCompanyAccount" placeholder="Provide the Bank Account Number for the Tax Company e.g. ZRA Bank Account" />
        </div>
        </label>
      </div>
	  <div style="padding-bottom:5px"><strong>Tax Company Bank Sort Code:</strong>
        <div>
          <input class="form-control" type="text" value="<%=taxCompanySortCode==null ? "" : taxCompanySortCode.getValue() %>" name="taxCompanySortCode" id="taxCompanySortCode" placeholder="Provide the Bank Account Sort Code for the Tax Company" />
        </div>
        </label>
      </div>
	  <div style="padding-bottom:5px"><strong>Email Sending Account:</strong>
        <div>
          <input class="form-control" type="text" value="<%=sendingEmail==null ? "" : sendingEmail.getValue() %>" name="sendingEmail" id="sendingEmail" placeholder="Provide the email address to be used for sending emails" />
        </div>
        </label>
      </div>
	  <div style="padding-bottom:5px"><strong>Email Sending Account Password:</strong>
        <div>
          <input class="form-control" type="text" value="<%=sendingEmailPassword==null ? "" : sendingEmailPassword.getValue() %>" name="sendingEmailPassword" id="sendingEmailPassword" placeholder="Provide the password for the email address to be used for sending emails" />
        </div>
        </label>
      </div>
	  <div style="padding-bottom:5px"><strong>Email Sender Port:</strong>
        <div>
          <input class="form-control" type="text" value="<%=sendingEmailPort==null ? "" : sendingEmailPort.getValue() %>" name="sendingEmailPort" id="sendingEmailPort" placeholder="Provide the port for sending emails" />
        </div>
        </label>
      </div>
	  <div style="padding-bottom:5px"><strong>Email Sender Username:</strong>
        <div>
          <input class="form-control" type="text" value="<%=sendingEmailUsername==null ? "" : sendingEmailUsername.getValue() %>" name="sendingEmailUsername" id="sendingEmailUsername" placeholder="Provide the username for sending emails" />
        </div>
        </label>
      </div>
	  
	  
	  
	  
	  
	  <div style="padding-bottom:5px"><strong>Application Name:</strong>
        <div>
          <input class="form-control" type="text" value="<%=applicationName==null ? "" : applicationName.getValue() %>" name="applicationName" id="applicationName" placeholder="Provide the name of the application" />
        </div>
        </label>
      </div>
	  <div style="padding-bottom:5px"><strong>Short Application Name:</strong>
	  	<span class="taglib-icon-help"><img alt="" aria-labelledby="dplx" onblur="Liferay.Portal.ToolTip.hide();" onfocus="Liferay.Portal.ToolTip.show(this);" onmouseover="Liferay.Portal.ToolTip.show(this);" src="/html/themes/control_panel/images/portlet/help.png" tabindex="0" aria-controls="aui_3_4_0_1_18202" id="aui_3_4_0_1_18192"><span class="aui-helper-hidden-accessible tooltip-text" id="dplx">Short Application name that appears in SMS messages. Not more than 11 characters</span></span>
        <div>
          <input class="form-control" type="text" value="<%=mobileApplicationName==null ? "" : mobileApplicationName.getValue() %>" name="mobileApplicationName" id="mobileApplicationName" placeholder="Provide the Short Application Name for SMS Messages" />
        </div>
        </label>
      </div>
	  <div style="padding-bottom:5px"><strong>Proxy Host:</strong><span class="taglib-icon-help"><img alt="" aria-labelledby="dplx" onblur="Liferay.Portal.ToolTip.hide();" onfocus="Liferay.Portal.ToolTip.show(this);" onmouseover="Liferay.Portal.ToolTip.show(this);" src="/html/themes/control_panel/images/portlet/help.png" tabindex="0" aria-controls="aui_3_4_0_1_18202" id="aui_3_4_0_1_18192"><span class="aui-helper-hidden-accessible tooltip-text" id="dplx">If this application runs on a network implementing a proxy, provide the proxy host here else leave empty</span></span>
        <div>
          <input class="form-control" type="text" value="<%=proxyHost==null ? "" : proxyHost.getValue() %>" name="proxyHost" id="proxyHost" placeholder="Provide a proxy host" />
        </div>
        </label>
      </div>
	  <div style="padding-bottom:5px"><strong>Proxy Port:</strong><span class="taglib-icon-help"><img alt="" aria-labelledby="dplx" onblur="Liferay.Portal.ToolTip.hide();" onfocus="Liferay.Portal.ToolTip.show(this);" onmouseover="Liferay.Portal.ToolTip.show(this);" src="/html/themes/control_panel/images/portlet/help.png" tabindex="0" aria-controls="aui_3_4_0_1_18202" id="aui_3_4_0_1_18192"><span class="aui-helper-hidden-accessible tooltip-text" id="dplx">If this application runs on a network implementing a proxy, provide the proxy port here else leave empty</span></span>
        <div>
          <input class="form-control" type="text" value="<%=proxyPort==null ? "" : proxyPort.getValue() %>" name="proxyPort" id="proxyPort" placeholder="Provide the proxy port" />
        </div>
        </label>
      </div>
	  <div style="padding-bottom:5px"><strong>Proxy Authentication Username:</strong><span class="taglib-icon-help"><img alt="" aria-labelledby="dplx" onblur="Liferay.Portal.ToolTip.hide();" onfocus="Liferay.Portal.ToolTip.show(this);" onmouseover="Liferay.Portal.ToolTip.show(this);" src="/html/themes/control_panel/images/portlet/help.png" tabindex="0" aria-controls="aui_3_4_0_1_18202" id="aui_3_4_0_1_18192"><span class="aui-helper-hidden-accessible tooltip-text" id="dplx">If this application runs on a network implementing a proxy and the proxy has a username and password, provide the proxy username here else leave empty</span></span>
        <div>
          <input class="form-control" type="text" value="<%=proxyUsername==null ? "" : proxyUsername.getValue() %>" name="proxyUsername" id="proxyUsername" placeholder="Provide the proxy authentication username" />
        </div>
        </label>
      </div>
	  <div style="padding-bottom:5px"><strong>Proxy Authentication Password:</strong><span class="taglib-icon-help"><img alt="" aria-labelledby="dplx" onblur="Liferay.Portal.ToolTip.hide();" onfocus="Liferay.Portal.ToolTip.show(this);" onmouseover="Liferay.Portal.ToolTip.show(this);" src="/html/themes/control_panel/images/portlet/help.png" tabindex="0" aria-controls="aui_3_4_0_1_18202" id="aui_3_4_0_1_18192"><span class="aui-helper-hidden-accessible tooltip-text" id="dplx">If this application runs on a network implementing a proxy and the proxy has a username and password, provide the proxy password here else leave empty</span></span>
        <div>
          <input class="form-control" type="text" value="<%=proxyPassword==null ? "" : proxyPassword.getValue() %>" name="proxyPassword" id="proxyPassword" placeholder="Provide the proxy authentication password" />
        </div>
        </label>
      </div>
	  <div style="padding-bottom:5px"><strong>Bank Name:</strong>
        <div>
          <input class="form-control" type="text" value="<%=bankName==null ? "" : bankName.getValue() %>" name="bankName" id="bankName" placeholder="Provide the name of the bank hosting this application" />
        </div>
        </label>
      </div>
	  <div style="padding-bottom:5px"><strong>Currency:</strong><span class="taglib-icon-help"><img alt="" aria-labelledby="dplx" onblur="Liferay.Portal.ToolTip.hide();" onfocus="Liferay.Portal.ToolTip.show(this);" onmouseover="Liferay.Portal.ToolTip.show(this);" src="/html/themes/control_panel/images/portlet/help.png" tabindex="0" aria-controls="aui_3_4_0_1_18202" id="aui_3_4_0_1_18192"><span class="aui-helper-hidden-accessible tooltip-text" id="dplx">The currency  used for financial transactions on this system</span></span>
        <div>
          <input class="form-control" type="text" value="<%=currency==null ? "" : currency.getValue() %>" name="currency" id="currency" placeholder="Provide the currency to be used on this system" />
        </div>
        </label>
      </div>
	  <div style="padding-bottom:5px"><strong>Banks Payment Gateway URL:</strong><span class="taglib-icon-help"><img alt="" aria-labelledby="dplx" onblur="Liferay.Portal.ToolTip.hide();" onfocus="Liferay.Portal.ToolTip.show(this);" onmouseover="Liferay.Portal.ToolTip.show(this);" src="/html/themes/control_panel/images/portlet/help.png" tabindex="0" aria-controls="aui_3_4_0_1_18202" id="aui_3_4_0_1_18192"><span class="aui-helper-hidden-accessible tooltip-text" id="dplx">The URL this application connects to during bank balance and funds transfer transactions</span></span>
        <div>
          <input class="form-control" type="text" value="<%=bankPaymentWebServiceUrl==null ? "" : bankPaymentWebServiceUrl.getValue() %>" name="bankPaymentWebServiceUrl" id="bankPaymentWebServiceUrl" placeholder="Provide the URL to the banks payment web service" />
        </div>
        </label>
      </div>
	  <div style="padding-bottom:5px"><strong>Tax Body Web Service URL:</strong>
        <div>
          <input class="form-control" type="text" value="<%=zraWebServiceUrl==null ? "" : zraWebServiceUrl.getValue() %>" name="zraWebServiceUrl" id="zraWebServiceUrl" placeholder="Provide the URL to Tax Body's web service" />
        </div>
        </label>
      </div>
	  
	  
	  
	  <div style="padding-bottom:5px"> <strong>Turn Approval Processs On:<span style="color:red">*</span></strong>
        <div>
          <select name="approvalProcess" class="form-control">
            <option value="-1">-Select Option for Approval Process-</option>
			<%
			select0 = "";
			select1 = "";
			if(approvalProcess.getValue()!=null && approvalProcess.getValue().equalsIgnoreCase("0"))
			{
				select0="selected='selected'";
			}else if(approvalProcess.getValue()!=null && approvalProcess.getValue().equalsIgnoreCase("1"))
			{
				select1="selected='selected'";
			}
			%>
            <option value="0" <%=select0%>>No, Do Turn Off</option>
			<option value="1" <%=select1%>>Yes, Turn On</option>
          </select>
        </div>
        </label>
      </div>
	  
	  <div style="padding-bottom:5px"> <strong>Turn Two-Step Login On:<span style="color:red">*</span></strong>
        <div>
          <select name="twoStepLogin" class="form-control">
            <option value="-1">-Select Option for Two-Step Login-</option>
			<%
			select0 = "";
			select1 = "";
			if(twoStepLogin.getValue()!=null && twoStepLogin.getValue().equalsIgnoreCase("0"))
			{
				select0="selected='selected'";
			}else if(twoStepLogin.getValue()!=null && twoStepLogin.getValue().equalsIgnoreCase("1"))
			{
				select1="selected='selected'";
			}
			%>
            <option value="0" <%=select0%>>No, Do Turn Off</option>
			<option value="1" <%=select1%>>Yes, Turn On</option>
          </select>
        </div>
        </label>
      </div>
      <div style="clear:both; padding-top:10px; font-size:11px; color:red; font-weight:bold">
    All fields with red asterisk (*) imply they must be provided</div>
      <div style="padding-bottom:20px;">
        <button name="settingsupdate" id="settingsupdate" class="btn btn-success" style="background-color:#00CC00" >Proceed to Save Settings</button>
      </div>
    </form>
    </div>
</div>
</div>