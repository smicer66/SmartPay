<%@page import="com.probase.smartpay.admin.corporateindividualmanagement.CorporateIndividualManagementPortletState"%>
<%@page import="com.probase.smartpay.admin.corporateindividualmanagement.CorporateIndividualManagementPortletState.*"%>
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

CorporateIndividualManagementPortletState portletState = CorporateIndividualManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(CorporateIndividualManagementPortletState.class);


%>

<jsp:include page="/html/corporateindividualmanagementportlet/corporateindividual/tabs.jsp" flush="" />


<portlet:actionURL var="proceedToStepOne" name="processAction">
	<portlet:param name="action"
		value="<%=COMPANY_CREATE_INDIVIDUAL_ACTIONS.CREATE_AN_INDIVIDUAL__PRE_STEP_ONE.name()%>" />
</portlet:actionURL>


<div style="padding:10px; width:900px"> 	
    <div class="panel  panel-primary">
		<div class="panel-heading"><span style="color:white; font-weight: bold">Select Company To Add Staff To:</span></div>
		<div class="panel-body">
		    <form  id="panelcreatorform" action="<%=proceedToStepOne%>" method="post" enctype="application/x-www-form-urlencoded">
			    <fieldset>
			      <div> <strong>Company:</strong>
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
			      </div>
				  <div style="padding-top: 10px;">
			        <button name="createPanelName" id="createPanelName" class="btn btn-success">Next</button>
			      </div>
			    </fieldset>
		    </form>
		</div>
	</div>
</div>