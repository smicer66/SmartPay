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
<%@page import="smartpay.entity.Ports"%>
<%@page import="smartpay.entity.Company"%>
<%@page import="smartpay.entity.Assessment"%>
<%@page import="java.util.ArrayList"%>
<%@page import="smartpay.entity.TpinInfo"%>
<%@page import="smartpay.entity.FeeDescription"%>
<%@page import="smartpay.entity.CompanyFeeDescription"%>
<%@page import="java.text.DateFormat"%>
<%@page import="com.probase.smartpay.commins.TaxBreakDownResponse"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />
<%
	String resourceBaseURL = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ "/resources";
	String faceboxCssUrl = resourceBaseURL + "/css/facebox.css";
	String pagingUrl = resourceBaseURL + "/css/paging.css";

	String jqueryDataTableCssUrl = resourceBaseURL + "/css/jquery.dataTables.css";
	String jqueryDataTableThemeCssUrl = resourceBaseURL + "/css/jquery.dataTables_themeroller.css";
	String jqueryDataTableUrl = resourceBaseURL + "/js/jquery.dataTables.min.js";

		
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
<link href="<%=jqueryUICssUrl%>" rel="stylesheet" type="text/css" /><%

FeeDescriptionPortletState portletState = FeeDescriptionPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(FeeDescriptionPortletState.class);



%>


<portlet:actionURL var="mapfeetocompany" name="processAction">
	<portlet:param name="action"
		value="<%=FEE_DESCRIPTION.MAP_FEE_TO_COMPANY.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="updatefeecompanymapping" name="processAction">
	<portlet:param name="action"
		value="<%=FEE_DESCRIPTION.UPDATE_FEE_COMPANY_MAPPING.name()%>" />
</portlet:actionURL>

<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>



<div style="padding-left:10px; padding-right:10px">
<jsp:include page="/html/feedescriptionportlet/feedescription/tabs.jsp" flush="" />
</div>



<div style="padding-left:10px; padding-right:10px">
	<div class="panel  panel-primary">
		<div class="panel-heading"><span style="color:white; font-weight: bold">Assign a Transaction Fee To A Corporate Firm</span></div>
	  	<div class="panel-body">
			<form  id="mapFeeToCompany" action="<%=mapfeetocompany%>" method="post" enctype="application/x-www-form-urlencoded">
			<fieldset>
				<legend>Assign Special Transaction Fees to Corporate Firms</legend>
				<div style="width:100%">
					
					<div>
						<Label>Fee Description</Label>
						<br>
						<select name="feedescription" class="form-control">
							<option value="-1">-Select A Transaction Fee-</option>
							<%
						if(portletState.getAllFeeDescription()!=null && portletState.getAllFeeDescription().size()>0)
						{
							for(Iterator<FeeDescription> iterFeeDesc = portletState.getAllFeeDescription().iterator(); iterFeeDesc.hasNext();)
							{
								FeeDescription feedescription = iterFeeDesc.next();
							%>
								<option value="<%=feedescription.getId()%>"><%=feedescription.getFeeName()%></option>
							<%
							}
						}
							%>
						</select>
						<div style="clear:both; padding-top:10px; font-size:11px; color:red; font-weight:bold">
						Transaction Fee selected here will be applicable to transactions carried out by the selected company rather than the Primary transaction fee
						</div>
					</div>
					
					<div>
						<Label>Apply this transaction Fee to this company</Label>
						<br>
						<select name="company" class="form-control">
							<option value="-1">-Select A Company-</option>
							<%
						if(portletState.getAllCompanyListing()!=null && portletState.getAllCompanyListing().size()>0)
						{
							for(Iterator<Company> iterCompany = portletState.getAllCompanyListing().iterator(); iterCompany.hasNext();)
							{
								Company company = iterCompany.next();
							%>
								<option value="<%=company.getId()%>"><%=company.getCompanyName()%></option>
							<%
							}
						}
							%>
						</select>
					</div>
					
					<div style="padding-top:20px">
						<button name="mapfeetocompany" class="btn btn-success">Set Transaction Fee for this company</button>
					</div>
				</div>
			</fieldset>
			</form>
		</div>
	</div>
</div>

			

<script type="text/javascript" charset="utf-8" src="<%=jqueryDataTableUrl%>"></script>

<script type="text/javascript">
$(document).ready(function() {
    $('#btable').dataTable();
} );

function handleButtonAction(id){
	
	document.getElementById(id).submit();
}
</script>
