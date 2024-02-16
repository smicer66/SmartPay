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
<%@page import="smartpay.entity.RoleType"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.enumerations.CompanyTypeConstants"%>
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


%>

<jsp:include page="/html/corporateindividualmanagementportlet/corporateindividual/tabs.jsp" flush="" />


<portlet:actionURL var="proceedToStepTwo" name="processAction">
	<portlet:param name="action"
		value="<%=COMPANY_CREATE_INDIVIDUAL_ACTIONS.UPDATE_AN_INDIVIDUAL_STEP_ONE.name()%>" />
</portlet:actionURL>

<div style="padding:10px; width:900px"> 	
    <div class="panel  panel-primary">
		<div class="panel-heading"><span style="color:white; font-weight: bold">Step 1 of 2: Update A Company Staff Profile</span></div>
		<div class="panel-body">
    <form  id="startRegFormId" action="<%=proceedToStepTwo%>" method="post" enctype="application/x-www-form-urlencoded">
		<div style="padding:10px;">
      
	  <div> <strong>Individuals First Name:<span style="color:red">*</span></strong>
        <div>
          <input class="form-control" type="text" value="<%=portletState.getCorporateindividualfirstname()==null ? "" : portletState.getCorporateindividualfirstname() %>" name="firstname" id="firstname" placeholder="Provide The Company's Personnel First Name" />
        </div>
      </div>
      <div><strong>Individuals Last Name:<span style="color:red">*</span></strong>
        <div>
          <input class="form-control" type="text" value="<%=portletState.getCorporateindividuallastname()==null ? "" : portletState.getCorporateindividuallastname() %>" name="lastname" id="lastname" placeholder="Provide The Company's Personnel Last Name" />
        </div>
      </div>
	  <div><strong>First Line of Address:</strong>
        <div>
          <input class="form-control" type="text" value="<%=portletState.getCorporateindividualAddressLine1()==null ? "" : portletState.getCorporateindividualAddressLine1() %>" name="contactAddressLine1" id="contactAddressLine1" placeholder="Provide First Line of Address" />
        </div>
      </div>
      <div><strong>Second Line of Address:</strong>
        <div>
          <input class="form-control" type="text" value="<%=portletState.getCorporateindividualAddressLine2()==null ? "" : portletState.getCorporateindividualAddressLine2() %>" name="contactAddressLine2" id="contactAddressLine2" placeholder="Provide Second Line of Address" />
        </div>
      </div>
      
      
      <br>
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
	          <input onkeypress="return onlyNumKey(event)" class="form-control" type="text" value="<%=portletState.getCorporateindividualfirstmobile()==null ? "" : portletState.getCorporateindividualfirstmobile().substring(0, (portletState.getCorporateindividualfirstmobile().length())) %>" name="contactMobileNumber" id="contactMobileNumber" placeholder="e.g. 9XXXXXXXXX" />
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
	          <input onkeypress="return onlyNumKey(event)" class="clear form-control" type="text" value="<%=portletState.getCorporateindividualsecondmobile()==null ? "" : (portletState.getCorporateindividualsecondmobile().length()>3 ? portletState.getCorporateindividualsecondmobile().substring(3, portletState.getCorporateindividualsecondmobile().length()) : "") %>" name="contactMobileNumberFirstAlternative" id="contactMobileNumberFirstAlternative" placeholder="e.g. 9XXXXXXXXX" />
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
	          <input onkeypress="return onlyNumKey(event)" class="clear form-control" type="text" value="<%=portletState.getCorporateindividualthirdmobile()==null ? "" : (portletState.getCorporateindividualthirdmobile().length()>3 ? portletState.getCorporateindividualthirdmobile().substring(3, portletState.getCorporateindividualthirdmobile().length()) : "") %>" name="contactMobileNumberSecondAlternative" id="contactMobileNumberSecondAlternative" placeholder="e.g. 9XXXXXXXXX" />
			  	</div>
	        </div>
	      </div>
	      
	      
      <div><strong>Primary Contact Email Address:<span style="color:red">*</span></strong>
        <div>
          <input class="form-control" type="text" value="<%=portletState.getCorporateindividualfirstemail()==null ? "" : portletState.getCorporateindividualfirstemail() %>" name="contactEmailAddress" id="contactEmailAddress" placeholder="Provide Contact Email Address" />
        </div>
      </div>
      <div><strong>1st Alternative Contact Email Address:</strong>
        <div>
          <input class="form-control" type="text" value="<%=portletState.getCorporateindividualsecondemail()==null ? "" : portletState.getCorporateindividualsecondemail() %>" name="contactEmailAddressFirstAlternative" id="contactEmailAddressFirstAlternative" placeholder="Provide Contact Email Address" />
        </div>
      </div>
      <div><strong>2nd Alternative Contact Email Address:</strong>
        <div>
          <input class="form-control" type="text" value="<%=portletState.getCorporateindividualthirdemail()==null ? "" : portletState.getCorporateindividualthirdemail() %>" name="contactEmailAddressSecondAlternative" id="contactEmailAddressSecondAlternative" placeholder="Provide Contact Email Address" />
        </div>
      </div>
      <div style="clear:both; padding-top:10px; font-size:11px; color:red; font-weight:bold">
    All fields with red asterisk (*) imply they must be provided</div>
      <div style="padding-top:20px;">
		<button name="createcorporateindividualStepOne" onclick="javascript:handleButton('back')" id="createcorporateindividualStepOne" class="btn btn-danger" style="float:left">Cancel</button>
		<button name="createcorporateindividualStepOne" onclick="javascript:handleButton('next')" id="createcorporateindividualStepOne" class="btn btn-success" style="float:right">Proceed to Next</button>
      </div>
	  	</div>
		<input type="hidden" name="selectedPortalUserAction" id="selectedPortalUserAction" value="" />
		
    </form>
		</div>
  </div>
</div>


<script type="text/javascript">
function handleButton(action)
{
	document.getElementById('selectedPortalUserAction').value=action;
	document.getElementById('startRegFormId').submit();
}



function onlyDoubleKey(e, elementId)
{
	var src = (e.srcElement || e.target);
	var unicode=e.charCode? e.charCode : e.keyCode;
	
	var check = false;
	var lenAfter = document.getElementById(elementId).value.length - document.getElementById(elementId).value.indexOf(".");
	if(document.getElementById(elementId).value.length>0 && 
			document.getElementById(elementId).value.indexOf(".")==-1 && 
			lenAfter<3)
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