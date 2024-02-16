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
<%@page import="smartpay.entity.AuthorizePanelCombination"%>
<%@page import="smartpay.entity.FinancialAmountRestriction"%>
<%@page import="smartpay.entity.enumerations.PanelTypeConstants"%>
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

/*<jsp:include page="/html/annualreturnsportlet/tabs.jsp" flush="" />*/
%>


<jsp:include page="/html/mandatepanelportlet/mandatepanel/tabs.jsp" flush="" />

<portlet:actionURL var="bankbranchcreator" name="processAction">
	<portlet:param name="action"
		value="<%=AUTHORISATION_PANEL.EDIT_AUTH_PANEL.name()%>" />
</portlet:actionURL>


  	<%
  	if(portletState.getAllFinancialRestrictionsListing()==null || (portletState.getAllFinancialRestrictionsListing()!=null && portletState.getAllFinancialRestrictionsListing().size()==0))
	{
  	%>
  	<div style="padding:20px;" class="panel-info">
  		You do not have a financial amount restriction currently created on the platform. Please create one!
  	</div>
  	<%
	}
  	%>
<div style="padding:10px;"> 	
    <div class="panel  panel-primary">
		<div class="panel-heading"><span style="color:white; font-weight: bold">Update An Authorisation Panel</span></div>
		<div class="panel-body">
		    <form  id="panelupdateform" action="<%=bankbranchcreator%>" method="post" enctype="application/x-www-form-urlencoded">
		    <fieldset>
		    	<legend>Provide the details required</legend>
		      <div> <strong>Authorisation Panel Name:</strong>
		        <div>
		          <input class="clear" type="text" value="<%=portletState.getPanelName()==null ? "" : portletState.getPanelName() %>" name="panelname" id="panelname" placeholder="Provide The Panel Name" />
		        </div>
		        </label>
		      </div>
		      
		      <div> <strong>Financial Amount Restriction:</strong><span class="taglib-icon-help"><img alt="" aria-labelledby="dplx" onblur="Liferay.Portal.ToolTip.hide();" onfocus="Liferay.Portal.ToolTip.show(this);" onmouseover="Liferay.Portal.ToolTip.show(this);" src="/html/themes/control_panel/images/portlet/help.png" tabindex="0" aria-controls="aui_3_4_0_1_18202" id="aui_3_4_0_1_18192"><span class="aui-helper-hidden-accessible tooltip-text" id="dplx">Boundary amounts within which this authorisation panel is based on</span></span>
		        <div>
		          <select name="financialAmountRestriction" id="financialAmountRestriction">
				  	<option value="-1">-Select A Financial Amount Threshold</option>
					<%
					if(portletState.getAllFinancialRestrictionsListing()!=null && portletState.getAllFinancialRestrictionsListing().size()>0)
					{
						for(Iterator<FinancialAmountRestriction> iterFAR = portletState.getAllFinancialRestrictionsListing().iterator(); iterFAR.hasNext();)
						{
							FinancialAmountRestriction far = iterFAR.next();
							String select1="";
							if(far.getId().equals(Long.valueOf(portletState.getSelectedFinancialAmountRestriction())))
							{
								select1 = "selected='selected'";
							}
						%>
							<option <%=select1 %> value="<%=far.getId()%>"><%=far.getName()%></option>
						<%
						}
					}
					%>
				  </select>
		        </div>
		        </label>
		      </div>
			  
		      <div> <strong>Type of Panel:</strong><span class="taglib-icon-help"><img alt="" aria-labelledby="dplx" onblur="Liferay.Portal.ToolTip.hide();" onfocus="Liferay.Portal.ToolTip.show(this);" onmouseover="Liferay.Portal.ToolTip.show(this);" src="/html/themes/control_panel/images/portlet/help.png" tabindex="0" aria-controls="aui_3_4_0_1_18202" id="aui_3_4_0_1_18192"><span class="aui-helper-hidden-accessible tooltip-text" id="dplx">Transaction Initiators initiate a transaction. Transaction Authorisers authorise a transaction</span></span>
		        <div>
		          <select name="panelType" id="panelType">
				  	<option value="-1">-Select A Financial Amount Threshold</option>
					<%
					String select2 = "";
					String select1= "";
					if(portletState.getPanelType().equals(PanelTypeConstants.AUTHORIZE_PANEL_TYPE_INITIATOR.getValue()))
					{
						select1 = "selected='selected'";
					}
					%>
				  	<option <%=select1%> value="<%=PanelTypeConstants.AUTHORIZE_PANEL_TYPE_INITIATOR.getValue()%>">TRANSACTION INITIATOR</option>
					<%
					if(portletState.getPanelType().equals(PanelTypeConstants.AUTHORIZE_PANEL_TYPE_AUTHORISER.getValue()))
					{
						select2 = "selected='selected'";
					}
					%>
				  	<option <%=select2%> value="<%=PanelTypeConstants.AUTHORIZE_PANEL_TYPE_AUTHORISER.getValue()%>">TRANSACTION AUTHORISER</option>		
				  </select>
		        </div>
		        </label>
		      </div>
		      
		      <div style="clear:both; padding-top:10px; font-size:11px; color:red; font-weight:bold">
		    All fields must be provided</div>
		      <div>
		        <button name="updateAuthPanel" id="updateAuthPanel" class="btn btn-success">Update Authorisation Panel</button>
		      </div>
		    </fieldset>
		    </form>
		</div>
	 </div>
</div>
