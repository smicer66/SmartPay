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
<%@page import="smartpay.entity.Company"%>
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
		value="<%=FINANCIAL_AMOUNT_RESTRICTION.CREATE_A_FINANCIAL_AMOUNT_RESTRICTION.name()%>" />
</portlet:actionURL>


<div style="padding:10px; width: 900px"> 	
    <div class="panel panel-primary">
		<div class="panel-heading"><span style="color:white; font-weight: bold">Create A Financial Restriction</span></div>
		<div class="panel-body">
		    <form  id="financialAmountRestrictionForm" action="<%=financialAmountRestrictionUrl%>" method="post" enctype="application/x-www-form-urlencoded">
		    <fieldset>
		    	<div style="padding:10px">		      
				      <%
				      if(portletState.getPortalUser()!=null && (portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR)))
				      {
				      %>
				      <div> <strong>Company:</strong><span class="taglib-icon-help"><img alt="" aria-labelledby="dplx" onblur="Liferay.Portal.ToolTip.hide();" onfocus="Liferay.Portal.ToolTip.show(this);" onmouseover="Liferay.Portal.ToolTip.show(this);" src="/html/themes/control_panel/images/portlet/help.png" tabindex="0" aria-controls="aui_3_4_0_1_18202" id="aui_3_4_0_1_18192"><span class="aui-helper-hidden-accessible tooltip-text" id="dplx">This is the company you are creating a financial restriction for.</span></span>
				        <div>
				          <select name="companySelected" id="companySelected" class="form-control">
						  	<option value="-1">-Select A Company-</option>
							<%
							if(portletState.getAllCompanyListing()!=null && portletState.getAllCompanyListing().size()>0)
							{
								for(Iterator<Company> iterCompany = portletState.getAllCompanyListing().iterator(); iterCompany.hasNext();)
								{
									Company company = iterCompany.next();
								%>
									<option value="<%=company.getId()%>"><%=company.getCompanyName()%> <%=company.getCompanyRCNumber()==null ? "" : " - RC Number(" + company.getCompanyRCNumber() + ")"%></option>
								<%
								}
							}
							%>
						  </select>
				        </div>
				        </label>
				      </div>
				      <%
				      }
				      %>
				      
				      <div> <strong>Minimum Payment Threshold:</strong><span class="taglib-icon-help"><img alt="" aria-labelledby="dplx" onblur="Liferay.Portal.ToolTip.hide();" onfocus="Liferay.Portal.ToolTip.show(this);" onmouseover="Liferay.Portal.ToolTip.show(this);" src="/html/themes/control_panel/images/portlet/help.png" tabindex="0" aria-controls="aui_3_4_0_1_18202" id="aui_3_4_0_1_18192"><span class="aui-helper-hidden-accessible tooltip-text" id="dplx">This is the lower boundary amount for this restriction.</span></span>
				        <div>
				          <input onkeypress="return onlyDoubleKey(event, 'minpaymentthreshold')" class="form-control" type="text" value="<%=portletState.getMinimumPaymentThreshold()==null ? "" : portletState.getMinimumPaymentThreshold() %>" name="minpaymentthreshold" id="minpaymentthreshold" placeholder="Provide The Minimum Payment Threshold" />
				        </div>
				        </label>
				      </div>
					  <div> <strong>Maximum Payment Threshold:</strong><span class="taglib-icon-help"><img alt="" aria-labelledby="dplx" onblur="Liferay.Portal.ToolTip.hide();" onfocus="Liferay.Portal.ToolTip.show(this);" onmouseover="Liferay.Portal.ToolTip.show(this);" src="/html/themes/control_panel/images/portlet/help.png" tabindex="0" aria-controls="aui_3_4_0_1_18202" id="aui_3_4_0_1_18192"><span class="aui-helper-hidden-accessible tooltip-text" id="dplx">This is the upper boundary amount for this restriction.</span></span>
				        <div>
				          <input onkeypress="return onlyDoubleKey(event, 'maxpaymentthreshold')" class="form-control" type="text" value="<%=portletState.getMaximumPaymentThreshold()==null ? "" : portletState.getMaximumPaymentThreshold() %>" name="maxpaymentthreshold" id="maxpaymentthreshold" placeholder="Provide The Maximum Payment Threshold" />
				        </div>
				        </label>
				      </div>
				      <div style="clear:both; padding-top:10px; font-size:11px; color:red; font-weight:bold">
				    All fields must be provided</div>
				      <div>
				        <button name="createFinancialAmountRestriction" id="createFinancialAmountRestriction" class="btn btn-success">Create A Financial Restriction</button>
				      </div>
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
