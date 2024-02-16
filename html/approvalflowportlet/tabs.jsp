<%@page import="com.probase.smartpay.approvalflow.ApprovalFlowPortletState"%>
<%@page	import="com.probase.smartpay.approvalflow.ApprovalFlowPortletState.*"%>
<%@page	import="com.probase.smartpay.approvalflow.ApprovalFlowPortletState.APPROVAL_TAB_ACTION"%>
<%@page import="java.util.Collection"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@ page import="com.liferay.portal.kernel.util.Validator"%>
<%@ page import="javax.portlet.PortletPreferences"%>
<%@ page import="javax.portlet.PortletURL"%>
<%@ page import="javax.portlet.ActionRequest"%>
<%@ page import="javax.portlet.RenderResponse"%>
<%@ page import="com.liferay.portal.util.PortalUtil"%>
<%@ page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.PortalUser"%>
<%@page import="smartpay.entity.Company"%>
<%@page import="smartpay.entity.AuthorizePanelCombination"%>
<%@page import="smartpay.entity.FeeDescription"%>
<%@page import="smartpay.entity.Settings"%>
<%@page import="smartpay.entity.TaxType"%>
<%@page import="com.probase.smartpay.admin.companymanagement.CompanyManagementPortletState"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />

<%

ApprovalFlowPortletState portletState = ApprovalFlowPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ApprovalFlowPortletState.class);

%>
<portlet:actionURL var="createUser" name="processAction">
		<portlet:param name="action" value="<%=APPROVAL_TAB_ACTION.HANDLE_CREATE_NEW_USER.name()%>" />
	</portlet:actionURL>
	<portlet:actionURL var="updateUser" name="processAction">
		<portlet:param name="action" value="<%=APPROVAL_TAB_ACTION.HANDLE_UPDATE_NEW_USER.name()%>" />
	</portlet:actionURL>
	<portlet:actionURL var="blockUser" name="processAction">
		<portlet:param name="action" value="<%=APPROVAL_TAB_ACTION.HANDLE_BLOCK_NEW_USER.name()%>" />
	</portlet:actionURL>
	<portlet:actionURL var="unblockUser" name="processAction">
		<portlet:param name="action" value="<%=APPROVAL_TAB_ACTION.HANDLE_UNBLOCK_NEW_USER.name()%>" />
	</portlet:actionURL>
	<portlet:actionURL var="deleteUser" name="processAction">
		<portlet:param name="action" value="<%=APPROVAL_TAB_ACTION.HANDLE_DELETE_NEW_USER.name()%>" />
	</portlet:actionURL>
	<portlet:actionURL var="createCompany" name="processAction">
		<portlet:param name="action" value="<%=APPROVAL_TAB_ACTION.HANDLE_CREATE_NEW_COMPANY.name()%>" />
	</portlet:actionURL>
	<portlet:actionURL var="updateCompany" name="processAction">
		<portlet:param name="action" value="<%=APPROVAL_TAB_ACTION.HANDLE_UPDATE_COMPANY.name()%>" />
	</portlet:actionURL>
	<portlet:actionURL var="blockCompany" name="processAction">
		<portlet:param name="action" value="<%=APPROVAL_TAB_ACTION.HANDLE_BLOCK_COMPANY.name()%>" />
	</portlet:actionURL>
	<portlet:actionURL var="unblockCompany" name="processAction">
		<portlet:param name="action" value="<%=APPROVAL_TAB_ACTION.HANDLE_UNBLOCK_COMPANY.name()%>" />
	</portlet:actionURL>
	<portlet:actionURL var="deleteCompany" name="processAction">
		<portlet:param name="action" value="<%=APPROVAL_TAB_ACTION.HANDLE_DELETE_COMPANY.name()%>" />
	</portlet:actionURL>
	<portlet:actionURL var="mapMandatePanel" name="processAction">
		<portlet:param name="action" value="<%=APPROVAL_TAB_ACTION.HANDLE_MAP_PANEL.name()%>" />
	</portlet:actionURL>
	<portlet:actionURL var="unmapMandatePanel" name="processAction">
		<portlet:param name="action" value="<%=APPROVAL_TAB_ACTION.HANDLE_UNMAP_PANEL.name()%>" />
	</portlet:actionURL>
	<portlet:actionURL var="createTransactionFee" name="processAction">
		<portlet:param name="action" value="<%=APPROVAL_TAB_ACTION.HANDLE_CREATE_FEE.name()%>" />
	</portlet:actionURL>
	<portlet:actionURL var="updateTransactionFee" name="processAction">
		<portlet:param name="action" value="<%=APPROVAL_TAB_ACTION.HANDLE_UPDATE_FEE.name()%>" />
	</portlet:actionURL>
	<portlet:actionURL var="deleteTransactionFee" name="processAction">
		<portlet:param name="action" value="<%=APPROVAL_TAB_ACTION.HANDLE_DELETE_FEE.name()%>" />
	</portlet:actionURL>
	<portlet:actionURL var="mapTransactionFee" name="processAction">
		<portlet:param name="action" value="<%=APPROVAL_TAB_ACTION.HANDLE_MAP_FEE.name()%>" />
	</portlet:actionURL>
	<portlet:actionURL var="unmapTransactionFee" name="processAction">
		<portlet:param name="action" value="<%=APPROVAL_TAB_ACTION.HANDLE_UNMAP_FEE.name()%>" />
	</portlet:actionURL>
	<portlet:actionURL var="updateSettings" name="processAction">
		<portlet:param name="action" value="<%=APPROVAL_TAB_ACTION.HANDLE_UPDATE_SETTINGS.name()%>" />
	</portlet:actionURL>
	<portlet:actionURL var="newTaxType" name="processAction">
		<portlet:param name="action" value="<%=APPROVAL_TAB_ACTION.HANDLE_NEW_TAX_TYPE.name()%>" />
	</portlet:actionURL>
	<portlet:actionURL var="taxTypeListSuspend" name="processAction">
		<portlet:param name="action" value="<%=APPROVAL_TAB_ACTION.HANDLE_TAX_TYPE_LIST_SUSPEND.name()%>" />
	</portlet:actionURL>
	<portlet:actionURL var="taxTypeListReactivate" name="processAction">
		<portlet:param name="action" value="<%=APPROVAL_TAB_ACTION.HANDLE_TAX_TYPE_LIST_REACTIVATE.name()%>" />
	</portlet:actionURL>
	<portlet:actionURL var="taxTypeUpdate" name="processAction">
		<portlet:param name="action" value="<%=APPROVAL_TAB_ACTION.HANDLE_TAX_TYPE_LIST_UPDATE.name()%>" />
	</portlet:actionURL>
<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>



<%
String createUserColor="#000000";
String createUserBgColor="#CCCCCC";
String updateUserColor="#000000";
String updateUserBgColor="#CCCCCC";
String blockUserColor="#000000";
String blockUserBgColor="#CCCCCC";
String unblockUserColor="#000000";
String unblockUserBgColor="#CCCCCC";
String deleteUserColor="#000000";
String deleteUserBgColor="#CCCCCC";
String createCompanyColor="#000000";
String createCompanyBgColor="#CCCCCC";
String updateCompanyColor="#000000";
String updateCompanyBgColor="#CCCCCC";
String blockCompanyColor="#000000";
String blockCompanyBgColor="#CCCCCC";
String unblockCompanyColor="#000000";
String unblockCompanyBgColor="#CCCCCC";
String deleteCompanyColor="#000000";
String deleteCompanyBgColor="#CCCCCC";
String mapMandatePanelColor="#000000";
String mapMandatePanelBgColor="#CCCCCC";
String unmapMandatePanelColor="#000000";
String unmapMandatePanelBgColor="#CCCCCC";
String mapTransactionFeeColor="#000000";
String mapTransactionFeeBgColor="#CCCCCC";
String unmapTransactionFeeColor="#000000";
String unmapTransactionFeeBgColor="#CCCCCC";
String newTransactionFeeColor="#000000";
String newTransactionFeeBgColor="#CCCCCC";
String deleteTransactionFeeColor="#000000";
String deleteTransactionFeeBgColor="#CCCCCC";
String updateTransactionFeeColor="#000000";
String updateTransactionFeeBgColor="#CCCCCC";
String updateSettingsColor="#000000";
String updateSettingsBgColor="#CCCCCC";	
String newTaxTypeColor="#000000";
String newTaxTypeBgColor="#CCCCCC";	
String taxTypeListReactivateColor="#000000";
String taxTypeListReactivateBgColor="#CCCCCC";	
String taxTypeListSuspendColor="#000000";
String taxTypeListSuspendBgColor="#CCCCCC";	
String taxTypeUpdateColor="#000000";
String taxTypeUpdateBgColor="#CCCCCC";


if(portletState.getSelectedApprovalItemType()!=null && portletState.getSelectedApprovalItemType().equalsIgnoreCase((PortalUser.class.getSimpleName())))
{
	if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VNEW_USER))
	{
		createUserColor="#ffffff"; createUserBgColor="#000000";
	}
	else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VUPDATE_USER))
	{
		updateUserColor="#ffffff"; updateUserBgColor="#000000";
	}
	else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VBLOCK_USER))
	{
		blockUserColor="#ffffff"; blockUserBgColor="#000000";
	}
	else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VUNBLOCK_USER))
	{
		unblockUserColor="#ffffff"; unblockUserBgColor="#000000";
	}
	else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VDELETE_USER))
	{
		deleteUserColor="#ffffff"; deleteUserBgColor="#000000";
	}
	else
	{
		createUserColor="#ffffff";
		createUserBgColor="#000000";
	}
}
else if(portletState.getSelectedApprovalItemType()!=null && portletState.getSelectedApprovalItemType().equalsIgnoreCase((Company.class.getSimpleName())))
{
	if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VNEW_COMPANY))
	{
		createCompanyColor="#ffffff"; createCompanyBgColor="#000000";
	}
	else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VUPDATE_COMPANY))
	{
		updateCompanyColor="#ffffff"; updateCompanyBgColor="#000000";
	}
	else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VBLOCK_COMPANY))
	{
		blockCompanyColor="#ffffff"; blockCompanyBgColor="#000000";
	}
	else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VUNBLOCK_COMPANY))
	{
		unblockCompanyColor="#ffffff"; unblockCompanyBgColor="#000000";
	}
	else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VDELETE_COMPANY))
	{
		deleteCompanyColor="#ffffff"; deleteCompanyBgColor="#000000";
	}
	else
	{
		createCompanyColor="#ffffff";
		createCompanyBgColor="#000000";
	}
}

else if(portletState.getSelectedApprovalItemType()!=null && portletState.getSelectedApprovalItemType().equalsIgnoreCase((AuthorizePanelCombination.class.getSimpleName())))
{
	if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VMAP_PANEL))
	{
		mapMandatePanelColor="#ffffff"; mapMandatePanelBgColor="#000000";
	}	
	else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VUNMAP_PANEL))
	{
		unmapMandatePanelColor="#ffffff"; unmapMandatePanelBgColor="#000000";
	}
	else
	{
		mapMandatePanelColor="#ffffff";
		mapMandatePanelBgColor="#000000";
	}
}
else if(portletState.getSelectedApprovalItemType()!=null && portletState.getSelectedApprovalItemType().equalsIgnoreCase((FeeDescription.class.getSimpleName())))
{
	if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VMAP_FEE))
	{
		mapTransactionFeeColor="#ffffff"; mapTransactionFeeBgColor="#000000";
	}
	else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VUNMAP_FEE))
	{
		unmapTransactionFeeColor="#ffffff"; unmapTransactionFeeBgColor="#000000";
	}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VNEW_FEE))
	{
		newTransactionFeeColor="#ffffff"; newTransactionFeeBgColor="#000000";
	}
	else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VDELETE_FEE))
	{
		deleteTransactionFeeColor="#ffffff"; deleteTransactionFeeBgColor="#000000";
	}
	else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VUPDATE_FEE))
	{
		updateTransactionFeeColor="#ffffff"; updateTransactionFeeBgColor="#000000";
	}
	else
	{
		newTransactionFeeColor="#ffffff";
		newTransactionFeeBgColor="#000000";
	}
}
else if(portletState.getSelectedApprovalItemType()!=null && portletState.getSelectedApprovalItemType().equalsIgnoreCase((Settings.class.getSimpleName())))
{
	if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VUPDATE_SETTINGS))
	{
		updateSettingsColor="#ffffff"; updateSettingsBgColor="#000000";
	}else
	{
		updateSettingsColor="#ffffff";
		updateSettingsBgColor="#000000";
	}
}else if(portletState.getSelectedApprovalItemType()!=null && portletState.getSelectedApprovalItemType().equalsIgnoreCase((TaxType.class.getSimpleName())))
{
	if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VNEW_TAXTYPE))
	{
		newTaxTypeColor="#ffffff"; newTaxTypeBgColor="#000000";
	}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VUPDATE_TAXTYPE))
	{
		taxTypeUpdateColor="#ffffff";
		taxTypeUpdateBgColor="#000000";
	}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VACT_TAXTYPE_REACTIVATE))
	{
		taxTypeListReactivateColor="#ffffff"; taxTypeListReactivateBgColor="#000000";
	}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VACT_TAXTYPE_SUSPEND))
	{
		taxTypeListSuspendColor="#ffffff";
		taxTypeListSuspendBgColor="#000000";
	}else
	{
		newTaxTypeColor="#ffffff"; newTaxTypeBgColor="#000000";
	}
}
%>

<div style="padding-top: 20px; padding-left: 10px;">
	<%
	
	
	if(portletState.getSelectedApprovalItemType()!=null && portletState.getSelectedApprovalItemType().equalsIgnoreCase((PortalUser.class.getSimpleName())))
	{
	%>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=createUser%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px; 
		background-color:<%=createUserBgColor %>; color:<%=createUserColor %>">
		New User Profiles</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=updateUser%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px;  
		background-color:<%=updateUserBgColor %>; color:<%=updateUserColor %>">
		User Profiles Updates</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=blockUser%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px;  
		background-color:<%=blockUserBgColor %>; color:<%=blockUserColor %>">
		Block User Profiles</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=unblockUser%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px;  
		background-color:<%=unblockUserBgColor %>; color:<%=unblockUserColor %>">
		Unblock User Profiles</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=deleteUser%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px;  
		background-color:<%=deleteUserBgColor %>; color:<%=deleteUserColor %>">
		Delete User Profiles</div></a>
	</div>
	<%
	}else if(portletState.getSelectedApprovalItemType()!=null && portletState.getSelectedApprovalItemType().equalsIgnoreCase((Company.class.getSimpleName())))
	{
	%>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=createCompany%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px; 
		background-color:<%=createCompanyBgColor %>; color:<%=createCompanyColor %>">
		New Company Profiles</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=updateCompany%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px;  
		background-color:<%=updateCompanyBgColor %>; color:<%=updateCompanyColor %>">
		Update Company Profiles</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=blockCompany%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px;  
		background-color:<%=blockCompanyBgColor %>; color:<%=blockCompanyColor %>">
		Block Company Profiles</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=unblockCompany%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px;  
		background-color:<%=unblockCompanyBgColor %>; color:<%=unblockCompanyColor %>">
		Unblock Company Profiles</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=deleteCompany%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px;  
		background-color:<%=deleteCompanyBgColor %>; color:<%=deleteCompanyColor %>">
		Delete Company Profiles</div></a>
	</div>
	<%
	}
	else if(portletState.getSelectedApprovalItemType()!=null && portletState.getSelectedApprovalItemType().equalsIgnoreCase((TaxType.class.getSimpleName())))
	{
	%>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=newTaxType%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px; 
		background-color:<%=newTaxTypeBgColor %>; color:<%=newTaxTypeColor %>">
		Create New tax Type</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=taxTypeUpdate%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px;  
		background-color:<%=taxTypeUpdateBgColor %>; color:<%=taxTypeUpdateColor %>">
		Update Tax Type</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=taxTypeListSuspend%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px;  
		background-color:<%=taxTypeListSuspendBgColor %>; color:<%=taxTypeListSuspendColor %>">
		Suspend Tax Type</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=taxTypeListReactivate%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px;  
		background-color:<%=taxTypeListReactivateBgColor %>; color:<%=taxTypeListReactivateColor %>">
		Reactivate Tax Type</div></a>
	</div>
	<%
	}
	else if(portletState.getSelectedApprovalItemType()!=null && portletState.getSelectedApprovalItemType().equalsIgnoreCase((AuthorizePanelCombination.class.getSimpleName())))
	{
	%>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=mapMandatePanel%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px; 
		background-color:<%=mapMandatePanelBgColor %>; color:<%=mapMandatePanelColor %>">
		Map Users To Mandate Panels</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=unmapMandatePanel%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px;  
		background-color:<%=unmapMandatePanelBgColor %>; color:<%=unmapMandatePanelColor %>">
		Remove Users From Mandate Panels</div></a>
	</div>
	<%
	}else if(portletState.getSelectedApprovalItemType()!=null && portletState.getSelectedApprovalItemType().equalsIgnoreCase((FeeDescription.class.getSimpleName())))
	{
	%>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=createTransactionFee%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px; 
		background-color:<%=newTransactionFeeBgColor %>; color:<%=newTransactionFeeColor %>">
		New Transaction Fees</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=updateTransactionFee%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px;  
		background-color:<%=updateTransactionFeeBgColor %>; color:<%=updateTransactionFeeColor %>">
		Update Transaction Fees</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=deleteTransactionFee%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px;  
		background-color:<%=deleteTransactionFeeBgColor %>; color:<%=deleteTransactionFeeColor %>">
		Delete Transaction Fees</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=mapTransactionFee%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px; 
		background-color:<%=mapTransactionFeeBgColor %>; color:<%=mapTransactionFeeColor %>">
		Assign Transaction Fee to Companies</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=unmapTransactionFee%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px;  
		background-color:<%=unmapTransactionFeeBgColor %>; color:<%=unmapTransactionFeeColor %>">
		Reset Transaction Fees of Companies</div></a>
	</div>
	<%
	}else if(portletState.getSelectedApprovalItemType()!=null && portletState.getSelectedApprovalItemType().equalsIgnoreCase((Settings.class.getSimpleName())))
	{
	%>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=updateSettings%>">
		<div style="padding:5px; padding-left:8px; padding-right:10px; 
		background-color:<%=updateSettingsBgColor %>; color:<%=updateSettingsColor %>">
		Update System Settings </div></a>
	</div>
	<%
	}
	%>
</div>

<div style="clear:both; font-size:20px; height:20px">&nbsp;</div>