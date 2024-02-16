<%@page import="com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortletUtil"%>
<%@page import="com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortletState.*"%>
<%@page import="com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortletState"%>
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
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.AuthorizePanelCombination"%>
<%@page import="java.text.DateFormat"%>
<%@page import="com.probase.smartpay.commins.Util"%>
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


%>

<div style="padding-left:10px; padding-right:10px">
	<jsp:include page="/html/usermanagementsystemadminportlet/tabs.jsp" flush="" />
</div>


<portlet:actionURL var="proceedToStepTwo" name="processAction">
	<portlet:param name="action"
		value="<%=USER_MANAGEMENT_SYSTEM_ADMIN_ACTIONS.UPDATE_A_PORTAL_USER_STEP_THREE.name()%>" />
</portlet:actionURL>

<div style="padding:10px; width:900px">
	<h2>Update A Portal User</h2>
	<div style="padding-left:10px; padding-right:10px">
		<div class="panel  panel-primary">
		  <div class="panel-heading"><span style="color:white; font-weight: bold;">Step 2 of 2: Preview Users Details</span></div>
		  <div class="panel-body">
		  	<div style="padding:10px;">
	    <form  id="startRegFormId" action="<%=proceedToStepTwo%>" method="post" enctype="application/x-www-form-urlencoded">
	    	<fieldset>
	    		<legend>Confirm Individuals Profile Provided</legend>
			      <div style="padding-bottom:5px"><strong>Selected User's Role:</strong>
			        <div>
			            <%=portletState.getSelectedUserRoleId()!=null ? ((RoleType)portletState.getUserManagementSystemAdminPortletUtil().getEntityObjectById(RoleType.class, Long.valueOf(portletState.getSelectedUserRoleId()))).getRoleTypeName().getValue().replace("_", "") : "N/A" %>
			        </div>
			      </div>
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
			      <div style="padding-bottom:5px"><strong>Primary Contact Email Address:</strong>
			        <div>
			          <%=portletState.getportaluserfirstemail()==null ? "N/A" : portletState.getportaluserfirstemail()%>
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
				  <%
				 if(portletState.getPortalUser()!=null && (portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_SUPER_ADMINISTRATOR) || portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_SYSTEM_SUPER_ADMINISTRATOR)))
				 {
				 %>
				 
				  <div style="padding-bottom:5px"><strong>User-Creation Priviledges:</strong>
					<div>
						<%
						if(portletState.getUserCRUD().equals("0"))
						{
						%>
					  <label>
					  This Staff has User-CRUD-Initiator Rights</label><br />
					  <div style="clear:both; padding-bottom:10px; font-size:11px; color:red; font-weight:bold"><em>
				Implies this staff can View other users on the system but can only initiate the Creation, Update and Delete actions of other users with the approval of a fellow User-CRUD-Approval administrative staff. </em></div>
						<%
						}else if(portletState.getUserCRUD().equals("1"))
						{
						%>
					  <label>
					  This Staff has User-CRUD-Approval Rights</label>
					  <div style="clear:both; padding-bottom:10px; font-size:11px; color:red; font-weight:bold"><em>
				Implies this staff can View other users on the system but can only approve the Creation, Update and Delete actions of other users. This user requires the initial action of a User-CRUD-Initiating Staff to carry out this task</em></div>
						<%
						}
						%>
				  </div>
				  
				  <div style="padding-bottom:5px"><strong>Company-Creation Priviledges:</strong>
					<div>
						<%
						if(portletState.getCompanyCRUD().equals("0"))
						{
						%>
					  <span style="font-weight:100">
					  This Staff has Company-CRUD-Initiator Rights</span><br />
					  <div style="clear:both; padding-bottom:10px; font-size:11px; color:red; font-weight:bold"><em>
				Implies this staff can View companies on the system but can only initiate the Creation, Update and Delete actions of other companies with the approval of a fellow Company-CRUD-approving administrative staff. </em></div>
						<%
						}else if(portletState.getCompanyCRUD().equals("1"))
						{
						%>
					  <span style="font-weight:100">
					  This Staff has Company-CRUD-Approval Rights</span>
					  <div style="clear:both; padding-bottom:10px; font-size:11px; color:red; font-weight:bold"><em>
				Implies this staff can View companies on the system but can only approve the Creation, Update and Delete actions of other companies. This user requires the initial action of a Company-CRUD-Initiating Staff to carry out this task</em></div>
						<%
						}	
						%>
				  </div>
				  
				 <%
				 }
				 %>
			      <div>
		      		<button type="submit" class="btn btn-danger" onclick="javascript:handleButtonAction('goBackToEditUPPg1')">Go Back</button>
			      	<button type="submit" class="btn btn-success" onclick="javascript:handleButtonAction('createUserAccount')">Proceed to Save</button>
			      <div style="clear:both; padding-top:10px; font-size:11px; color:red; font-weight:bold">
			    	Clicking on the PROCEED TO CREATE USER ACCOUNT implies you agree and accept to our terms and conditions</div>
			        <!-- <input type="submit" name="createportaluserStepThree" value="Proceed to Create User Account" id="createportaluserStepThree" class="floatLeft" style="background-color:#00CC00" />-->
			      </div>
			</fieldset>
			
			<input type="hidden" name="selectedPortalUser" id="selectedPortalUser" value="<%=portletState.getSelectedPortalUserId() %>" />
			<input type="hidden" name="selectedPortalUserAction" id="selectedPortalUserAction" value="" />	
			
	    </form>
	  		</div>
	  	</div>
	</div>
</div>


<script type="text/javascript">

function handleButtonAction(action, usId){
	
	document.getElementById('selectedPortalUser').value = usId;
	document.getElementById('selectedPortalUserAction').value = action;
	document.getElementById('startRegFormId').submit();
}
</script>