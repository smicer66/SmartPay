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
<%@page import="smartpay.entity.FinancialAmountRestriction"%>
<%@page import="smartpay.entity.AuthorizePanel"%>
<%@page import="smartpay.entity.AuthorizePanelCombination"%>
<%@page import="smartpay.entity.PortalUser"%>
<%@page import="smartpay.entity.enumerations.PanelTypeConstants"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Arrays"%>
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
Collection<Integer> personnelPositionList = portletState.getPersonnelPositionList();

/*<jsp:include page="/html/annualreturnsportlet/tabs.jsp" flush="" />*/
%>

<jsp:include page="/html/mandatepanelportlet/mandatepanel/tabs.jsp" flush="" />


<portlet:actionURL var="panelusermappingurl" name="processAction">
	<portlet:param name="action"
		value="<%=AUTHORISATION_PANEL.MAP_PORTAL_USER_STEP_TWO.name()%>" />
</portlet:actionURL>


<div style="padding:10px;"> 	
    <div class="panel  panel-primary">
		<div class="panel-heading"><span style="color:white; font-weight: bold">Create An Authorisation Panel</span></div>
		<div class="panel-body">
		    <form  id="panelusermapping" action="<%=panelusermappingurl%>" method="post" enctype="application/x-www-form-urlencoded">
			    <div style="padding:10px;">
			    <fieldset>
			    	
				  
			      <div> <strong>Selected Mandate Panel:</strong>
			        <div>
			          <%=portletState.getMandatePanelPortletUtil().getAuthorizedPanelById(Long.valueOf(portletState.getSelectedMapPanel())).getPanelName() %>
			        </div>
			        </label>
			      </div>
				  
			      <div> <strong>Company Personnel:</strong>
			      <span class="taglib-icon-help"><img alt="" aria-labelledby="dplx" onblur="Liferay.Portal.ToolTip.hide();" onfocus="Liferay.Portal.ToolTip.show(this);" onmouseover="Liferay.Portal.ToolTip.show(this);" src="/html/themes/control_panel/images/portlet/help.png" tabindex="0" aria-controls="aui_3_4_0_1_18202" id="aui_3_4_0_1_18192"><span class="aui-helper-hidden-accessible tooltip-text" id="dplx">This company personnel will only act on a transaction when the transaction process gets to the company personnels turn. This turn is totally dependent on the position selected</span></span>
			        <div>
			          <select name="personnel" id="personnel" class="form-control">
					  	<option value="-1">-Select A Company/Firm Personnel-</option>
						<%
						if(portletState.getAllCompanyPersonnel()!=null && portletState.getAllCompanyPersonnel().size()>0)
						{
							String select = "";
							for(Iterator<PortalUser> puListIter = portletState.getAllCompanyPersonnel().iterator(); puListIter.hasNext();)
							{
								PortalUser pu = puListIter.next();
							%>
						  		<option value="<%=pu.getId()%>"><%=pu.getFirstName() + " " + pu.getLastName()%></option>
							<%
							}
						}
						%>		
					  </select>
			        </div>
			        </label>
			      </div>
				  
				  <div> <strong>Flow Process Position of Personnel in Panel:</strong>
			        <div>
			          <select name="processposition" id="processposition" class="form-control">
					  	<option value="-1">-Select A Position-</option>
						<%
						String select = "";
						for(int c=1; c<11; c++)
						{
							if(personnelPositionList!=null && !personnelPositionList.contains(c))
							{
						%>
					  		<option value="<%=c%>"><%=c%></option>
						<%
							}else if(personnelPositionList==null)
							{
						%>
				  		<option value="<%=c%>"><%=c%></option>
						<%
							}
						}
						%>		
					  </select>
			        </div>
			        </label>
			      </div>
			      <div style="clear:both; padding-top:10px; font-size:11px; color:red; font-weight:bold">
			    All fields must be provided</div>
			      <div>
			        <button name="addUserToPanel" id="addUserToPanel" class="btn btn-success">Add Staff To This Authorisation Panel</button>
			      </div>
			    </fieldset>
			    </div>
		    </form>
		</div>
  	</div>
</div>
