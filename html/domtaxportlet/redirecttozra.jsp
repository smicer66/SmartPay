<%@page import="smartpay.entity.enumerations.CompanyTypeConstants"%>
<%@page import="com.probase.smartpay.domtax.DomTaxPortletState"%>
<%@page import="com.probase.smartpay.domtax.DomTaxPortletState.*"%>
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
<%@page import="smartpay.entity.Ports"%>
<%@page import="smartpay.entity.Company"%>
<%@page import="smartpay.entity.DomTax"%>
<%@page import="smartpay.entity.WorkFlowAssessment"%>
<%@page import="java.util.ArrayList"%>
<%@page import="smartpay.entity.TpinInfo"%>
<%@page import="java.text.DateFormat"%>
<%@page import="com.probase.smartpay.commins.Util"%>
<%@page import="com.probase.smartpay.commins.TaxBreakDownResponse"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>





<portlet:defineObjects />
<%
	String resourceBaseURL = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ "/resources";
	String faceboxCssUrl = resourceBaseURL + "/css/facebox.css";
	String pagingUrl = resourceBaseURL + "/css/paging.css";

	String jqueryDataTableCssUrl = resourceBaseURL + "/css/jquery.dataTables.css";
	String jqueryDataTableThemeCssUrl = resourceBaseURL + "/css/jquery.dataTables_themeroller.css";
	String jqueryDataTableUrl = resourceBaseURL + "/js/jquery.dataTables.js";

		
	String jqueryUICssUrl = resourceBaseURL + "/css/jquery-ui.min.css";
	
	String jqueryJsUrl = resourceBaseURL + "/js/jquery-1.10.2.min.js";
	String jqueryUIJsUrl = resourceBaseURL + "/js/jquery-ui.min.js";
%>


<link href="<%=jqueryDataTableCssUrl %>" rel="stylesheet" type="text/css" />


<body>
ecdata == <%=renderRequest.getPortletSession().getAttribute("encdata") %><br>
ecdata1 == <%=renderRequest.getAttribute("encdata") %><br>
prn == <%=renderRequest.getAttribute("prn") %><br>
tpin == <%=renderRequest.getAttribute("tpin") %><br>
tp_name == <%=renderRequest.getAttribute("tp_name") %><br>
b_amnt == <%=renderRequest.getAttribute("b_amnt") %><br>
b_pmnt_dt == <%=renderRequest.getAttribute("b_pmnt_dt") %><br>
b_ref_no == <%=renderRequest.getAttribute("b_ref_no") %><br>
b_status == <%=renderRequest.getAttribute("b_status") %><br>
http://10.16.76.69:9999/ZRAPortal/pages/eServices/ePayment/responseBank.jsp
<form name="redirectForm" action="http://10.16.76.69:9999/ZRAPortal/pages/eServices/ePayment/responseBank.jsp" method="post">
    <input name="Encdata" type="hidden" value="<%=renderRequest.getAttribute("encdata") %>" />
    <input name="prn" type="hidden" value="<%=renderRequest.getAttribute("prn") %>" />
    <input name="tpin" type="hidden" value="<%=renderRequest.getAttribute("tpin") %>" />
    <input name="tp_name" type="hidden" value="<%=renderRequest.getAttribute("tp_name") %>" />
    <input name="b_amnt" type="hidden" value="<%=renderRequest.getAttribute("b_amnt") %>" />
    <input name="b_pmnt_dt" type="hidden" value="<%=renderRequest.getAttribute("b_pmnt_dt") %>" />
    <input name="b_ref_no" type="hidden" value="<%=renderRequest.getAttribute("b_ref_no") %>" />
    <input name="b_status" type="hidden" value="<%=renderRequest.getAttribute("b_status") %>" />
    <noscript>
        <input type="submit" value="Click here to Complete Transaction" />
    </noscript>
</form>



<script type="text/javascript" charset="utf-8" src="<%=jqueryJsUrl%>"></script>
<script type="text/javascript" charset="utf-8" src="<%=jqueryDataTableUrl%>"></script>


<script type="text/javascript">

    $(document).ready(function() {
        document.redirectForm.submit();
    });

</script>
</body>
</html>