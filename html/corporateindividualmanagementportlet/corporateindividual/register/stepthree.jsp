<%@page import="com.probase.smartpay.admin.corporateindividualmanagement.CorporateIndividualManagementPortletState"%>
<%@page import="com.probase.smartpay.admin.corporateindividualmanagement.CorporateIndividualManagementPortletState.*"%>
<%@page import="java.util.Collection"%>
<%@page import="com.probase.smartpay.commins.Util"%>
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
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.enumerations.CompanyTypeConstants"%>
<%@page import="smartpay.entity.RoleType"%>

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
Collection<Company> companyListing = portletState.getCompanyListing();
Collection<RoleType> userRoleTypeList = portletState.getRoleTypeListing();
RoleType roleType = null;
if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
{
	roleType= (RoleType)portletState.getCorporateIndividualManagementPortletUtil().getEntityObjectById(RoleType.class, Long.valueOf(portletState.getSelectedUserRoleId()));
}
%>

<jsp:include page="/html/usermanagementsystemadminportlet/tabs.jsp" flush="" />


<portlet:actionURL var="proceedToStepTwo" name="processAction">
	<portlet:param name="action"
		value="<%=COMPANY_CREATE_INDIVIDUAL_ACTIONS.CREATE_AN_INDIVIDUAL_STEP_THREE.name()%>" />
</portlet:actionURL>

<div style="padding:10px; width:900px"> 	
    <div class="panel  panel-primary">
		<div class="panel-heading"><span style="color:white; font-weight: bold">Step 1 of 2: Preview Company Staff Profile Details</span></div>
		<div class="panel-body">
    <form  id="startRegFormId" action="<%=proceedToStepTwo%>" method="post" enctype="application/x-www-form-urlencoded">
    	<div style="padding:10px">
			
			  <%
			 if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
			 {
			 %>
			  <div style="padding-bottom:5px"> <strong>Select Company Staff Role:<span style="color:red">*</span></strong>
				<div>
				  <%=roleType!=null ? roleType.getRoleTypeName().getValue().replace("_", "") : ""%>
				  </select>
				</div>
			  </div>
			 <%
			 }
			 %>
			  <div><strong>Individuals First Name:</strong>
		        <div>
		          <%=portletState.getCorporateindividualfirstname()==null ? "N/A" : portletState.getCorporateindividualfirstname()%>
		        </div>
		      </div>
		      <div><strong>Individuals Last Name:</strong>
		        <div>
		          <%=portletState.getCorporateindividuallastname()==null ? "N/A" : portletState.getCorporateindividuallastname()%>
		        </div>
		      </div>
			  <div><strong>First Line of Address:</strong>
				<div>
				  <%=portletState.getCorporateindividualAddressLine1()==null ? "N/A" : portletState.getCorporateindividualAddressLine1() %>
				</div>
			  </div>
			  <div><strong>Second Line of Address:</strong>
				<div>
				  <%=portletState.getCorporateindividualAddressLine2()==null ? "N/A" : portletState.getCorporateindividualAddressLine2() %>
				</div>
			  </div>
		      <div><strong>Primary Contact Mobile Number:</strong>
		        <div>
		          <%=portletState.getCorporateindividualfirstmobile()==null ? "N/A" : portletState.getCorporateindividualfirstmobile()%>
		        </div>
		      </div>
		      <div><strong>1st Alternative Contact Mobile Number:</strong>
		        <div>
		          <%=(portletState.getCorporateindividualsecondmobile()==null || (portletState.getCorporateindividualsecondmobile()!=null && portletState.getCorporateindividualsecondmobile().length()==0)) ? "N/A" : portletState.getCorporateindividualsecondmobile()%>
		        </div>
		      </div>
		      <div><strong>2nd Alternative Contact Mobile Number:</strong>
		        <div>
		          <%=(portletState.getCorporateindividualthirdmobile()==null || (portletState.getCorporateindividualthirdmobile()!=null && portletState.getCorporateindividualthirdmobile().length()==0)) ? "N/A" : portletState.getCorporateindividualthirdmobile()%>
		        </div>
		      </div>
		      <div><strong>Primary Contact Email Address:</strong>
		        <div>
		          <%=portletState.getCorporateindividualfirstemail()==null ? "N/A" : portletState.getCorporateindividualfirstemail()%>
		        </div>
		      </div>
		      <div><strong>1st Alternative Contact Email Address:</strong>
		        <div>
		          <%=(portletState.getCorporateindividualsecondemail()==null || (portletState.getCorporateindividualsecondemail()!=null && portletState.getCorporateindividualsecondemail().length()==0)) ? "N/A" : portletState.getCorporateindividualsecondemail()%>
		        </div>
		      </div>
		      <div><strong>2nd Alternative Contact Email Address:</strong>
		        <div>
		          <%=(portletState.getCorporateindividualthirdmobile()==null || (portletState.getCorporateindividualthirdmobile()!=null && portletState.getCorporateindividualthirdmobile().length()==0)) ? "N/A" : portletState.getCorporateindividualthirdmobile()%>
		        </div>
		      </div>
		      <div style="clear:both; padding-top:10px; font-size:11px; color:red; font-weight:bold">
		    All fields with red asterisk (*) imply they must be provided</div>
		      <div>
		        <button name="createcorporateindividualStepThree" id="createcorporateindividualStepThree" class="btn btn-success">Proceed to Create A Corporate Individual Account</button>
		      </div>
		</div>
		
		
    </form>
		</div>
  </div>
</div>
