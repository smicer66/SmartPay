<%@page import="com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortletState"%>
<%@page import="com.probase.smartpay.admin.usermanagementsystemadmin.UserManagementSystemAdminPortletState.*"%>
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
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.RoleType"%>
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
Collection<RoleType> userRoleTypeList = portletState.getRoleTypeListing();


%>
<div style="padding-left:10px; padding-right:10px">
	<jsp:include page="/html/usermanagementsystemadminportlet/tabs.jsp" flush="" />
</div>

<portlet:actionURL var="proceedToStepTwo" name="processAction">
	<portlet:param name="action"
		value="<%=USER_MANAGEMENT_SYSTEM_ADMIN_ACTIONS.CREATE_A_PORTAL_USER_STEP_ONE.name()%>" />
</portlet:actionURL>


<div style="padding-left:10px; padding-right:10px; width:900px">
	<h2>Create A Bank Staff Profile</h2>
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold">Step 1 of 2: Provide Users Information</span></div>
	  <div class="panel-body">
	    	
	    <form  id="startRegFormId" action="<%=proceedToStepTwo%>" method="post" enctype="application/x-www-form-urlencoded">
	     <%
	     if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_SYSTEM_SUPER_ADMINISTRATOR))
	     {
	     %>
	      <div style="padding-bottom:5px"> <strong>Select User's Role for System Super admin:<span style="color:red">*</span></strong>
	        <div>
	          <select name="roletype" class="form-control">
	            <option value="-1">-Select A User Role-</option>
	            <%if(userRoleTypeList!=null && userRoleTypeList.size()>0)
	            {
	            	for(Iterator<RoleType> iter = userRoleTypeList.iterator(); iter.hasNext();)
	            	{
	            		RoleType roleType = iter.next();
	            		String selected = "";
	            		if((roleType.getRoleTypeName().getValue().equalsIgnoreCase(RoleTypeConstants.ROLE_TYPE_BANK_SUPER_ADMINISTRATOR.getValue())))
	            		{
		            		if(portletState.getSelectedUserRoleId()!=null && portletState.getSelectedUserRoleId().equals(Long.toString(roleType.getId())))
		            		{
		            			selected = "selected='selected'";
		            		}
	            	%>
	            <option <%=selected %> value="<%=roleType.getId()%>"><%=roleType.getRoleTypeName().getValue().replace("_", "")%></option>
	            <%
	            		}
	            	}
	            } %>
	          </select>
	        </div>
	      </div>
		 <%
	     }else if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_SUPER_ADMINISTRATOR))
	     {
	     %>
	      <div style="padding-bottom:5px"> <strong>Select User's or Super Bank Admin Role:<span style="color:red">*</span></strong>
	        <div>
	          <select name="roletype" class="form-control">
	            <option value="-1">-Select A User Role-</option>
	            <%if(userRoleTypeList!=null && userRoleTypeList.size()>0)
	            {
	            	for(Iterator<RoleType> iter = userRoleTypeList.iterator(); iter.hasNext();)
	            	{
	            		RoleType roleType = iter.next();
	            		String selected = "";
	            		if(roleType.getRoleTypeName().getValue().equalsIgnoreCase(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR.getValue()))
	            		{
		            		if(portletState.getSelectedUserRoleId()!=null && portletState.getSelectedUserRoleId().equals(Long.toString(roleType.getId())))
		            		{
		            			selected = "selected='selected'";
		            		}
	            	%>
	            <option <%=selected %> value="<%=roleType.getId()%>"><%=roleType.getRoleTypeName().getValue().replace("_", "")%></option>
	            <%
	            		}
	            	}
	            } %>
	          </select>
	        </div>
	      </div>
		 <%
	     }else if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
	     {
	     %>
	      <div style="padding-bottom:5px"> <strong>Select User's Bank Admin Role:<span style="color:red">*</span></strong>
	        <div>
	          <select name="roletype" class="form-control">
	            <option value="-1">-Select A User Role-</option>
	            <%if(userRoleTypeList!=null && userRoleTypeList.size()>0)
	            {
	            	for(Iterator<RoleType> iter = userRoleTypeList.iterator(); iter.hasNext();)
	            	{
	            		RoleType roleType = iter.next();
	            		String selected = "";
	            		if((!roleType.getRoleTypeName().getValue().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR.getValue()) 
	            						&& !roleType.getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_SUPER_ADMINISTRATOR) 
	            								&& !roleType.getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_SYSTEM_SUPER_ADMINISTRATOR)
	            										&& !roleType.getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR)
	            												&& !roleType.getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_NON_ADMINISTRATOR_PERSONNEL)
	            													&& !roleType.getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_RETAIL_STAFF)))
	            		{
		            		if(portletState.getSelectedUserRoleId()!=null && portletState.getSelectedUserRoleId().equals(Long.toString(roleType.getId())))
		            		{
		            			selected = "selected='selected'";
		            		}
	            	%>
	            <option <%=selected %> value="<%=roleType.getId()%>"><%=roleType.getRoleTypeName().getValue().replace("_", "")%></option>
	            <%
	            		}
	            	}
	            } %>
	          </select>
	        </div>
	      </div>
		 <%
	     }
		 %>
		  <div style="padding-bottom:5px"> <strong>Individuals First Name:<span style="color:red">*</span></strong>
	        <div>
	          <input class="clear form-control" type="text" value="<%=portletState.getportaluserfirstname()==null ? "" : portletState.getportaluserfirstname() %>" name="firstname" id="firstname" placeholder="Provide The Company's Personnel First Name" />
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>Individuals Last Name:<span style="color:red">*</span></strong>
	        <div>
	          <input class="clear form-control" type="text" value="<%=portletState.getportaluserlastname()==null ? "" : portletState.getportaluserlastname() %>" name="lastname" id="lastname" placeholder="Provide The Company's Personnel Last Name" />
	        </div>
	      </div>
		  <div style="padding-bottom:5px"><strong>First Line of Address:</strong>
	        <div>
	          <input class="clear form-control" type="text" value="<%=portletState.getportaluserAddressLine1()==null ? "" : portletState.getportaluserAddressLine1() %>" name="contactAddressLine1" id="contactAddressLine1" placeholder="Provide First Line of Address" />
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>Second Line of Address:</strong>
	        <div>
	          <input class="clear form-control" type="text" value="<%=portletState.getportaluserAddressLine2()==null ? "" : portletState.getportaluserAddressLine2() %>" name="contactAddressLine2" id="contactAddressLine2" placeholder="Provide Second Line of Address" />
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>Primary Contact Email Address:<span style="color:red">*</span></strong>
	        <div>
	          <input class="clear form-control" type="text" value="<%=portletState.getportaluserfirstemail()==null ? "" : portletState.getportaluserfirstemail() %>" name="contactEmailAddress" id="contactEmailAddress" placeholder="Provide Contact Email Address" />
	        </div>
	      </div>
		  <div style="padding-bottom:5px; clear:both"><div><strong>Primary Contact Mobile Number:<span style="color:red">*</span></strong></div>
	        <div>
				<div style="float:left; padding-right:10px">
					<select class="form-control" name="countryCode1">
						<%
						String[] countryCode = new Util().getCountrycode();
						String[] countryName = new Util().getCountryname();
						
						for(int c=0; c<countryCode.length; c++)
						{
							String selected = "";
							if(portletState.getCountryCodeAlt1()!=null && portletState.getCountryCodeAlt1().equals(countryCode[c]))
							{
								selected = "selected='selected'";
							}
						%>
						<option <%=selected%> value="<%=countryCode[c]%>">+<%=countryCode[c]%></option>
						<%
						}
						%>
					</select>
				</div>
				<div style="float:left">
	          <input onkeypress="return onlyNumKey(event)" class="form-control" type="text" value="<%=portletState.getportaluserfirstmobile()==null ? "" : portletState.getportaluserfirstmobile().substring(0, 3) %>" name="contactMobileNumber" id="contactMobileNumber" placeholder="e.g. 9XXXXXXXXX" />
			  	</div>
	        </div>
	      </div>
	      <div style="padding-bottom:5px; clear:both"><div><strong>1st Alternative Contact Mobile Number:</strong></div>
	        <div>
				<div style="float:left; padding-right:10px">
					<select class="form-control" name="countryCode2">
						<%
						
						
						for(int c=0; c<countryCode.length; c++)
						{
							String selected = "";
							if(portletState.getCountryCodeAlt2()!=null && portletState.getCountryCodeAlt2().equals(countryCode[c]))
							{
								selected = "selected='selected'";
							}
						%>
						<option <%=selected%> value="<%=countryCode[c]%>">+<%=countryCode[c]%></option>
						<%
						}
						%>
					</select>
				</div>
				<div style="float:left">
	          <input onkeypress="return onlyNumKey(event)" class="clear form-control" type="text" value="<%=portletState.getportalusersecondmobile()==null ? "" : portletState.getportalusersecondmobile().substring(0, 3) %>" name="contactMobileNumberFirstAlternative" id="contactMobileNumberFirstAlternative" placeholder="e.g. 9XXXXXXXXX" />
			  	</div>
	        </div>
	      </div>
	      <div style="padding-bottom:5px; clear:both"><div><strong>2nd Alternative Contact Mobile Number:</strong></div>
	        <div>
				<div style="float:left; padding-right:10px">
					<select class="form-control" name="countryCode3">
						<%
						
						for(int c=0; c<countryCode.length; c++)
						{
							String selected = "";
							if(portletState.getCountryCodeAlt3()!=null && portletState.getCountryCodeAlt3().equals(countryCode[c]))
							{
								selected = "selected='selected'";
							}
						%>
						<option <%=selected%> value="<%=countryCode[c]%>">+<%=countryCode[c]%></option>
						<%
						}
						%>
					</select>
				</div>
				<div style="float:left">
	          <input onkeypress="return onlyNumKey(event)" class="clear form-control" type="text" value="<%=portletState.getportaluserthirdmobile()==null ? "" : portletState.getportaluserthirdmobile().substring(0, 3) %>" name="contactMobileNumberSecondAlternative" id="contactMobileNumberSecondAlternative" placeholder="e.g. 9XXXXXXXXX" />
			  	</div>
	        </div>
	      </div>
	      <div style="padding-bottom:5px; clear:both"><strong>1st Alternative Contact Email Address:</strong>
	        <div>
	          <input class="clear form-control" type="text" value="<%=portletState.getportalusersecondemail()==null ? "" : portletState.getportalusersecondemail() %>" name="contactEmailAddressFirstAlternative" id="contactEmailAddressFirstAlternative" placeholder="Provide Contact Email Address" />
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>2nd Alternative Contact Email Address:</strong>
	        <div>
	          <input class="clear form-control" type="text" value="<%=portletState.getportaluserthirdemail()==null ? "" : portletState.getportaluserthirdemail() %>" name="contactEmailAddressSecondAlternative" id="contactEmailAddressSecondAlternative" placeholder="Provide Contact Email Address" />
	        </div>
	      </div>
		  <%
	     if(portletState.getPortalUser()!=null && (portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_SUPER_ADMINISTRATOR) || portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_SYSTEM_SUPER_ADMINISTRATOR)))
	     {
		 		String checkedUser0 = "";
				String checkedUser1 = "";
				String checkedUser2 = "";
				String checkedUser3 = "";
			 	String checkedCompany0 = "";
				String checkedCompany1 = "";
				String checkedCompany2 = "";
				String checkedCompany3 = "";
				if(portletState.getUserCRUD()==null)
				{
					checkedUser0 = "checked='checked'";
				}else
				{
					if(portletState.getUserCRUD().equals("0"))
						checkedUser0 = "checked='checked'";
					if(portletState.getUserCRUD().equals("1"))
						checkedUser1 = "checked='checked'";
				}
				if(portletState.getCompanyCRUD()==null)
				{
					checkedUser2 = "checked='checked'";
				}else
				{
					if(portletState.getCompanyCRUD().equals("0"))
						checkedUser2 = "checked='checked'";
					if(portletState.getCompanyCRUD().equals("1"))
						checkedUser3 = "checked='checked'";
				}
	     %>
		 
	      <div style="padding-bottom:5px"><strong>User-Creation Priviledges:</strong>
	        <div>
	          <label>
	          <input name="userCRUD" type="radio" value="0" <%=checkedUser0%> />
	          Give This Staff Only User-CRUD-Initiator Rights</label><br />
			  <div style="clear:both; padding-bottom:10px; font-size:11px; color:red; font-weight:bold"><em>
	    Implies this staff can View other users on the system but can only initiate the Creation, Update and Delete actions of other users with the approval of a fellow User-CRUD-Approval administrative staff. </em></div>
	          <label>
	          <input name="userCRUD" type="radio" value="1" <%=checkedUser1%> />
	          Give This Staff Only User-CRUD-Approval Rights</label>
			  <div style="clear:both; padding-bottom:10px; font-size:11px; color:red; font-weight:bold"><em>
		Implies this staff can View other users on the system but can only approve the Creation, Update and Delete actions of other users. This user requires the initial action of a User-CRUD-Initiating Staff to carry out this task</em></div>
	      </div>
		  
		  <div style="padding-bottom:5px"><strong>Company-Creation Priviledges:</strong>
	        <div>
	          <label>
	          <input name="companyCRUD" type="radio" value="0" <%=checkedUser2%> />
	          Give This Staff Only Company-CRUD-Initiator Rights</label><br />
			  <div style="clear:both; padding-bottom:10px; font-size:11px; color:red; font-weight:bold"><em>
	    Implies this staff can View companies on the system but can only initiate the Creation, Update and Delete actions of other companies with the approval of a fellow Company-CRUD-approving administrative staff. </em></div>
	          <label>
	          <input name="companyCRUD" type="radio" value="1" <%=checkedUser3%> />
	          Give This Staff Only Company-CRUD-Approval Rights</label>
			  <div style="clear:both; padding-bottom:10px; font-size:11px; color:red; font-weight:bold"><em>
		Implies this staff can View companies on the system but can only approve the Creation, Update and Delete actions of other companies. This user requires the initial action of a Company-CRUD-Initiating Staff to carry out this task</em></div>
	      </div>
		  
		 <%
		 }
		 %>
	      <div>
	      	<button type="submit" class="btn btn-success">Proceed to Next</button>
	      	<div style="clear:both; padding-top:10px; font-size:11px; color:red; font-weight:bold">
	    All fields with red asterisk (*) imply they must be provided</div>
	        <!-- <input type="submit" name="createportaluserStepOne" value="Proceed to Next" id="createportaluserStepOne" class="floatLeft" style="background-color:#00CC00" />-->
	      </div>
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