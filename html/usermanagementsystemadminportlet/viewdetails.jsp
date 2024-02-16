<%@page import="com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortletUtil"%>
<%@page import="com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortletState.*"%>
<%@page import="com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortletState"%>
<%@page import="com.probase.smartpay.commins.ComminsApplicationState"%>
<%@page import="com.probase.smartpay.commins.Util"%>
<%@page import="com.probase.smartpay.commins.Util.DETERMINE_ACCESS"%>
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
<%@page import="smartpay.entity.RoleType"%>
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

UserManagementSystemAdminPortletState portletState = UserManagementSystemAdminPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(UserManagementSystemAdminPortletState.class);
Collection<Company> companyListing = portletState.getCompanyListing();
ComminsApplicationState cappState = portletState.getCas();
Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
DETERMINE_ACCESS determinAccess = new Util().determineAccessForUserFxns(twoStep, approvalProcess, cappState, portletState.getPortalUserCRUDRights());

%>

<div style="padding-left:10px; padding-right:10px">
	<jsp:include page="/html/usermanagementsystemadminportlet/tabs.jsp" flush="" />
</div>


<portlet:actionURL var="proceedToStepTwo" name="processAction">
	<portlet:param name="action"
		value="<%=USER_MANAGEMENT_SYSTEM_ADMIN_ACTIONS.VIEW_A_PORTAL_USER_ACTION.name()%>" />
</portlet:actionURL>

<div style="padding:10px;">
	<h2>View Portal User Details</h2>
	<div style="padding-left:10px; padding-right:10px">
		<div class="panel  panel-primary">
		  <div class="panel-heading"><span style="color:white; font-weight: bold;">View Users Details</span></div>
		  <div class="panel-body">
		  	<div style="padding:10px;">
	    <form  id="formAction" action="<%=proceedToStepTwo%>" method="post" enctype="application/x-www-form-urlencoded">
	    	<fieldset>
			      <div style="padding-bottom:5px"><strong>User's Role:</strong>
			        <div>
			            <%=portletState.getSelectedUserRoleId()!=null ? ((RoleType)portletState.getUserManagementSystemAdminPortletUtil().getEntityObjectById(RoleType.class, Long.valueOf(portletState.getSelectedUserRoleId()))).getRoleTypeName().getValue().replace("_", "") : "N/A" %>
			        </div>
			      </div>
			      <%
			      if(portletState.getSelectedCompany()!=null)
			      {
			      %>
			      <div style="padding-bottom:5px"><strong>User's Company:</strong>
			        <div>
			            <%=portletState.getSelectedCompany().getCompanyName() %>
			        </div>
			      </div>
			      <%
			      }
			      %>
				  <div style="padding-bottom:5px"><strong>Individuals First Name:</strong>
			        <div>
			          <%=portletState.getportaluserfirstname()==null ? "N/A" : portletState.getportaluserfirstname()%>
			        </div>
			      </div>
			      <div style="padding-bottom:5px"><strong>Individuals Last Name:</strong>
			        <div>
			          <%=portletState.getportaluserlastname()==null ? "N/A" : portletState.getportaluserlastname()%>
			        </div>
			      </div>
				  <div style="padding-bottom:5px"><strong>First Line of Address:</strong>
					<div>
					  <%=portletState.getportaluserAddressLine1()==null ? "N/A" : portletState.getportaluserAddressLine1() %>
					</div>
				  </div>
				  <div style="padding-bottom:5px"><strong>Second Line of Address:</strong>
					<div>
					  <%=portletState.getportaluserAddressLine2()==null ? "N/A" : portletState.getportaluserAddressLine2() %>
					</div>
				  </div>
			      <div style="padding-bottom:5px"><strong>Primary Contact Mobile Number:</strong>
			        <div>
			          <%=portletState.getportaluserfirstmobile()==null ? "N/A" : portletState.getportaluserfirstmobile()%>
			        </div>
			      </div>
			      <div style="padding-bottom:5px"><strong>1st Alternative Contact Mobile Number:</strong>
			        <div>
			          <%=portletState.getportalusersecondmobile()==null ? "N/A" : (portletState.getportalusersecondmobile().length()==0 ? "N/A" : portletState.getportalusersecondmobile())%>
			        </div>
			      </div>
			      <div style="padding-bottom:5px"><strong>2nd Alternative Contact Mobile Number:</strong>
			        <div>
			          <%=portletState.getportaluserthirdmobile()==null ? "N/A" : (portletState.getportaluserthirdmobile().length()==0 ? "N/A" : portletState.getportaluserthirdmobile())%>
			        </div>
			      </div>
			      <div style="padding-bottom:5px"><strong>Primary Contact Email Address:</strong>
			        <div>
			          <%=portletState.getportaluserfirstemail()==null ? "N/A" : portletState.getportaluserfirstemail()%>
			        </div>
			      </div>
			      <div style="padding-bottom:5px"><strong>1st Alternative Contact Email Address:</strong>
			        <div>
			          <%=portletState.getportalusersecondemail()==null ? "N/A" : (portletState.getportalusersecondemail().length()==0 ? "N/A ": portletState.getportalusersecondemail())%>
			        </div>
			      </div>
			      <div style="padding-bottom:5px"><strong>2nd Alternative Contact Email Address:</strong>
			        <div>
			          <%=portletState.getportaluserthirdmobile()==null ? "N/A" : (portletState.getportaluserthirdemail().length()==0 ? "N/A ": portletState.getportaluserthirdemail())%>
			        </div>
			      </div>
			      <div>
			      	<button type="submit" class="btn btn-danger" style="float:left" onclick="javascript:handleButtonAction('proceedtogoback')">Go Back</button>
		      	   <%
			      if(determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_ACCESS) || determinAccess.equals(DETERMINE_ACCESS.GRANT_INITIATOR_AND_APPROVER_ACCESS))
			      {
			      %>
		      		<button type="submit" class="btn btn-success" style="float:right" onclick="javascript:handleButtonAction('proceedtoedit')">Edit User Account</button>
		      	  <%
			      }
		      	  %>
			        <!-- <input type="submit" name="createportaluserStepThree" value="Proceed to Create User Account" id="createportaluserStepThree" class="floatLeft" style="background-color:#00CC00" />-->
			      </div>
			</fieldset>
			
			<input type="hidden" name="selectedPortalUserAction" id="selectedPortalUserAction" value="">
			<input type="hidden" name="selectedPortalUserActionId" id="selectedPortalUserActionId" value="<%=portletState.getSelectedPortalUserId()%>">
	    </form>
	  		</div>
	  	</div>
	</div>
</div>



<script type="text/javascript">
function handleButtonAction(id)
{
	document.getElementById('selectedPortalUserAction').value=id;
	document.getElementById('formAction').submit();
}
</script>