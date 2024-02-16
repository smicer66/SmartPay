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


<portlet:actionURL var="proceedToStepTwo" name="processAction">
	<portlet:param name="action"
		value="<%=COMPANY_CREATE_INDIVIDUAL_ACTIONS.CREATE_AN_INDIVIDUAL_STEP_THREE.name()%>" />
</portlet:actionURL>

<div style="width:100%">
  <div id="" class="floatLeft pad100">
  	
    <h2 class="form-header bodytext">

		<strong>
        	<span style="color:red;">Step 2 of 2: Preview Corporate Individual Details</span>
		</strong>
	</h2>
    <form  id="startRegFormId" action="<%=proceedToStepTwo%>" method="post" enctype="application/x-www-form-urlencoded">
    	<fieldset>
    		<legend>Individuals Profile</legend>
		      <div><strong>Company Selected:</strong>
		        <div>
		            <%=portletState.getSelectedCompanyId()!=null ? portletState.getCorporateIndividualManagementPortletUtil().getCompanyById(portletState.getSelectedCompanyId()) : "N/A" %>
		        </div>
		      </div>
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
		          <%=portletState.getCorporateindividualsecondmobile()==null ? "N/A" : portletState.getCorporateindividualsecondmobile()%>
		        </div>
		      </div>
		      <div><strong>2nd Alternative Contact Mobile Number:</strong>
		        <div>
		          <%=portletState.getCorporateindividualthirdmobile()==null ? "N/A" : portletState.getCorporateindividualthirdmobile()%>
		        </div>
		      </div>
		      <div><strong>Primary Contact Email Address:</strong>
		        <div>
		          <%=portletState.getCorporateindividualfirstemail()==null ? "N/A" : portletState.getCorporateindividualfirstemail()%>
		        </div>
		      </div>
		      <div><strong>1st Alternative Contact Email Address:</strong>
		        <div>
		          <%=portletState.getCorporateindividualsecondemail()==null ? "N/A" : portletState.getCorporateindividualsecondemail()%>
		        </div>
		      </div>
		      <div><strong>2nd Alternative Contact Email Address:</strong>
		        <div>
		          <%=portletState.getCorporateindividualthirdmobile()==null ? "N/A" : portletState.getCorporateindividualthirdmobile()%>
		        </div>
		      </div>
		      <div style="clear:both; padding-top:10px; font-size:11px; color:red; font-weight:bold">
		    All fields with red asterisk (*) imply they must be provided</div>
		      <div>
		        <input type="button" name="createcorporateindividualStepThree" value="Proceed to Create A Corporate Individual Account" id="createcorporateindividualStepThree" class="floatLeft" style="background-color:#00CC00" />
		      </div>
		</fieldset>
		
		
    </form>
  </div>
</div>
