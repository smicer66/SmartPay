<%@page import="com.probase.smartpay.admin.mandatepanelmanagement.MandatePanelPortletState"%>
<%@page import="com.probase.smartpay.admin.mandatepanelmanagement.MandatePanelPortletState.*"%>
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
<%@page import="com.probase.smartpay.commins.ComminsApplicationState"%>
<%@page import="com.probase.smartpay.commins.Util"%>
<%@page import="com.probase.smartpay.commins.Util.DETERMINE_ACCESS"%>
<%@page import="smartpay.entity.Company"%>
<%@page import="smartpay.entity.FinancialAmountRestriction"%>
<%@page import="smartpay.entity.AuthorizePanelCombination"%>
<%@page import="smartpay.entity.enumerations.PanelTypeConstants"%>
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

MandatePanelPortletState portletState = MandatePanelPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(MandatePanelPortletState.class);
ComminsApplicationState cappState = portletState.getCas();
Boolean twoStep = portletState.getTwoStepLogin()!=null && portletState.getTwoStepLogin().getStatus().equals(Boolean.TRUE) && portletState.getTwoStepLogin().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
Boolean approvalProcess = portletState.getApprovalProcess()!=null && portletState.getApprovalProcess().getStatus().equals(Boolean.TRUE) && portletState.getApprovalProcess().getValue().equals("1") ? Boolean.TRUE : Boolean.FALSE;
DETERMINE_ACCESS determinAccessForUser = new Util().determineAccessForUserFxns(twoStep, approvalProcess, cappState, portletState.getPortalUserCRUDRights());
/*<jsp:include page="/html/annualreturnsportlet/tabs.jsp" flush="" />*/
%>

<jsp:include page="/html/mandatepanelportlet/mandatepanel/tabs.jsp" flush="" />


<portlet:actionURL var="bankbranchcreator" name="processAction">
	<portlet:param name="action"
		value="<%=AUTHORISATION_PANEL.PRE_MAP_PANEL_TO_PORTAL_USER.name()%>" />
</portlet:actionURL>



<%
if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_COMPANY_SUPER_ADMINISTRATOR))
{
	%>
	<div style="padding:10px;"> 	
	    <div class="panel  panel-primary">
			<div class="panel-heading"><span style="color:white; font-weight: bold">Specify The Company</span></div>
			<div class="panel-body">
			    <form  id="panelcreatorform" action="<%=bankbranchcreator%>" method="post" enctype="application/x-www-form-urlencoded">
				    <fieldset>
				    	
				      <%
				      if(portletState.getPortalUser()!=null && (
				    		  portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR)))
				      {
				      %>
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
						  	<div style="clear:both; padding-top:10px; font-size:11px; color:red; font-weight:bold">
				    		This is the company you are mapping your corporate staff members to</div>
				        </div>
				        </label>
				      </div>
				      <%
				      }
				      %>
				      	<div style="padding-top:10px">
					        <button name="createPanelName" id="createPanelName" class="btn btn-success">Next</button>
				        </div>
				      </div>
				    </fieldset>
			    </form>
			</div>
		</div>
	</div>
	<%
}else
{
	if(determinAccessForUser.equals(DETERMINE_ACCESS.DISPLAY_SECOND_LEVEL_LOGIN) && determinAccessForUser.equals(DETERMINE_ACCESS.DISPLAY_SECOND_LEVEL_LOGIN))
	{
	%>
	<jsp:include page="/html/mandatepanelportlet/mandatepanel/login_step2.jsp" flush="" />
	<%	
	}else
	{
		if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR))
		{
			%>
			<div style="padding:10px;"> 	
			    <div class="panel  panel-primary">
					<div class="panel-heading"><span style="color:white; font-weight: bold">Specify The Company</span></div>
					<div class="panel-body">
					    <form  id="panelcreatorform" action="<%=bankbranchcreator%>" method="post" enctype="application/x-www-form-urlencoded">
						    <fieldset>
						    	
						      <%
						      if(portletState.getPortalUser()!=null && (
						    		  portletState.getPortalUser().getRoleType().getRoleTypeName().equals(RoleTypeConstants.ROLE_TYPE_BANK_ADMINISTRATOR)))
						      {
						      %>
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
								  	<div style="clear:both; padding-top:10px; font-size:11px; color:red; font-weight:bold">
						    		This is the company you are mapping your corporate staff members to</div>
						        </div>
						        </label>
						      </div>
						      <%
						      }
						      %>
						      	<div style="padding-top:10px">
							        <button name="createPanelName" id="createPanelName" class="btn btn-success">Next</button>
						        </div>
						      </div>
						    </fieldset>
					    </form>
					</div>
				</div>
			</div>
			<%
		}else
		{
	%>
	<div class="panel panel-danger">You do not have access to carry out any actions as you do not have valid access. Contact Appropriate Administrators for rights</div>
	<%
		}
	}
}
%>