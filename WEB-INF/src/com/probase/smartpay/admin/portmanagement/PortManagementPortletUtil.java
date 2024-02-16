package com.probase.smartpay.admin.portmanagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import antlr.collections.List;

import com.google.zxing.common.Collections;
import com.sf.primepay.smartpay13.ServiceLocator;

import smartpay.entity.AuthorizePanel;
import smartpay.entity.AuthorizePanelCombination;
import smartpay.entity.BankBranches;
import smartpay.entity.Company;
import smartpay.entity.FeeDescription;
import smartpay.entity.FinancialAmountRestriction;
import smartpay.entity.PortalUser;
import smartpay.entity.Ports;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.enumerations.CompanyStatusConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.service.SwpService;

public class PortManagementPortletUtil {

	
	private static PortManagementPortletUtil adminPortletUtil=null;
	SwpService swpService = null;
	com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	Logger log = Logger.getLogger(PortManagementPortletUtil.class);
	
	public PortManagementPortletUtil() {
		swpService = serviceLocator.getSwpService();
	}
	
	public static PortManagementPortletUtil getInstance()
	{
		if(adminPortletUtil==null)
		{
			PortManagementPortletUtil.adminPortletUtil = new PortManagementPortletUtil();
		}
		return PortManagementPortletUtil.adminPortletUtil;
	}

	public ArrayList<RoleType> getRoleTypeByPortalUser(PortalUser portalUser) {
		// TODO Auto-generated method stub
		Collection<RoleType> roles = null;
		try {
			String hql = "select pu.roleType from PortalUser pu where " +
					"pu.id = " + portalUser.getId();
			log.info("Get hqlType = " + hql);
			roles = (Collection<RoleType>) swpService.getAllRecordsByHQL(hql);
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		if(roles!=null && roles.size()>0)
		{
			ArrayList<RoleType> roleList = new ArrayList<RoleType>();
			for(Iterator<RoleType> roleIter = roles.iterator(); roleIter.hasNext();)
			{
				roleList.add(roleIter.next());
			}
			return roleList;
		}else
		{
			return null;
		}
	}

	public Collection<BankBranches> getAllBankBranchListing() {
		// TODO Auto-generated method stub
		Collection<BankBranches> bbList = null;
		try {
			String hql = "select bb from BankBranches bb where " +
					"bb.status = 'true'";
			log.info("Get hql = " + hql);
			bbList = (Collection<BankBranches>) swpService.getAllRecordsByHQL(hql);
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return bbList;
	}
	
	
	
	public Integer getMaximumAuthorizationsAllowed() {
		// TODO Auto-generated method stub
		Integer maxAuth = null;
		try {
			String hql = "select bb.value from Settings bb where " +
					"bb.status = 'true' AND bb.name = '" + SmartPayConstants.MAXIMUM_AUTHORIZATIONS_ALLOWED.getValue() + "'";
			log.info("Get hql = " + hql);
			maxAuth = (Integer) swpService.getUniqueRecordByHQL(hql);
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return maxAuth;
	}

	
	public BankBranches getBankBranchesById(Long id) {
		// TODO Auto-generated method stub
		BankBranches bankBranches = null;
		try {
			String hql = "select bb from BankBranches bb where " +
					"bb.id = " + id;
			log.info("Get hql = " + hql);
			bankBranches = (BankBranches) swpService.getUniqueRecordByHQL(hql);
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return bankBranches;
	}
	
	
	
	
	public Collection<String> getAuthorizePanelCombinationListingGroupByCode()
	{
		Collection<String> authorizePanelCombinationListing = null;
		try {
			String hql = "select apc.combinationCode from AuthorizePanelCombination apc where " +
					"apc.status = 'true' GROUP by apc.combinationCode";
			log.info("Get hql = " + hql);
			authorizePanelCombinationListing = (Collection<String>) swpService.getAllRecordsByHQL(hql);
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return authorizePanelCombinationListing;
	}
	
	
	
	public Collection<AuthorizePanelCombination> getAuthorizePanelCombinationListingByCode(String code)
	{
		Collection<AuthorizePanelCombination> authorizePanelCombinationListing = null;
		try {
			String hql = "select apc from AuthorizePanelCombination apc where " +
					"apc.combinationCode = '" + code +"'";
			log.info("Get hql = " + hql);
			authorizePanelCombinationListing = (Collection<AuthorizePanelCombination>) swpService.getAllRecordsByHQL(hql);
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return authorizePanelCombinationListing;
	}

	public Collection<Company> getCompanyListing(CompanyStatusConstants companyStatusActive) {
		// TODO Auto-generated method stub
		
		Collection<Company> companyListing = null;
		try {
			
			String hql = "select apc from Company apc where " +
				"apc.status = '" + companyStatusActive.getValue() +"'";
			log.info("Get hql = " + hql);
			companyListing = (Collection<Company>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return companyListing;
	}
	
	public Company getCompanyById(Long id)
	{
		Company company = null;
		try {
			String hql = "select apc from Company apc where " +
					"apc.id = " + id;
			log.info("Get hql = " + hql);
			company = (Company) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return company;
	}
	
	
	public Object getEntityObjectById(Class claz, Long id)
	{
		Object object = null;
		try {
			String hql = "select apc from " + claz.getSimpleName() + " apc where " +
					"apc.id = " + id;
			log.info("Get hql = " + hql);
			object = swpService.getRecordById(claz, id);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return object;
	}
	
	
	
	public FinancialAmountRestriction getFinancialAmountRestrictionById(Long id)
	{
		FinancialAmountRestriction financialAmountRestriction = null;
		try {
			String hql = "select apc from FinancialAmountRestriction apc where " +
					"apc.id = " + id;
			log.info("Get hql = " + hql);
			financialAmountRestriction = (FinancialAmountRestriction) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return financialAmountRestriction;
	}
	
	public AuthorizePanel getAuthorizedPanelById(Long id)
	{
		AuthorizePanel authorizedPanel = null;
		try {
			String hql = "select apc from AuthorizePanel apc where " +
					"apc.id = " + id;
			log.info("Get hql = " + hql);
			authorizedPanel = (AuthorizePanel) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return authorizedPanel;
	}

	public Collection<Company> getCompanyByNameOrRCNumber(String companyname,
			String companyrcnumber) {
		// TODO Auto-generated method stub
		Collection<Company> companyList = null;
		try {
			String hql = "select apc from Company apc where " +
					"lower(apc.companyName) = '" + companyname.toLowerCase() + "' " +
							"OR lower(apc.companyRCNumber) = '" + companyrcnumber.toLowerCase() + "'";
			log.info("Get hql = " + hql);
			companyList = (Collection<Company>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return companyList;
	}
	
	
	public Collection<Company> getCompanyByNameOrRCNumberForEdit(String companyname,
			String companyrcnumber, Long companyId) {
		// TODO Auto-generated method stub
		Collection<Company> companyList = null;
		try {
			String hql = "select apc from Company apc where " +
					"(lower(apc.companyName) = '" + companyname.toLowerCase() + "' " +
							"OR lower(apc.companyRCNumber) = '" + companyrcnumber.toLowerCase() + "') AND apc.id != " + companyId;
			log.info("Get hql = " + hql);
			companyList = (Collection<Company>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return companyList;
	}

	public Collection<BankBranches> getBankBranchByNameOrCode(String bankBranchName,
			String bankBranchCode) {
		// TODO Auto-generated method stub
		Collection<BankBranches> bankBranchList = null;
		try {
			String hql = "select bb from BankBranches bb where " +
					"lower(bb.name) = '" + bankBranchName.toLowerCase() + "' " +
							"OR lower(bb.bankCode) = '" + bankBranchCode.toLowerCase() + "'";
			log.info("Get hql = " + hql);
			bankBranchList = (Collection<BankBranches>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return bankBranchList;
	}
	
	
	
	
	public Collection<BankBranches> getBankBranchByNameOrCodeForEdit(String bankBranchName,
			String bankBranchCode, Long id) {
		// TODO Auto-generated method stub
		Collection<BankBranches> bankBranchList = null;
		try {
			String hql = "select bb from BankBranches bb where (" +
					"lower(bb.name) = '" + bankBranchName.toLowerCase() + "' " +
							"OR lower(bb.bankCode) = '" + bankBranchCode.toLowerCase() + "') AND bb.id != " + id;
			log.info("Get hql = " + hql);
			bankBranchList = (Collection<BankBranches>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return bankBranchList;
	}
	
	public AuthorizePanel getAuthorizePanelByNameForEdit(String panelName, Long id) {
		// TODO Auto-generated method stub
		AuthorizePanel ap = null;
		try {
			String hql = "select ap from AuthorizePanel ap where (" +
					"lower(ap.name) = '" + panelName.toLowerCase() + "') AND ap.id != " + id;
			log.info("Get hql = " + hql);
			ap = (AuthorizePanel) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return ap;
	}
	
	public AuthorizePanel getAuthorizePanelByName(String panelName) {
		// TODO Auto-generated method stub
		AuthorizePanel ap = null;
		try {
			String hql = "select ap from AuthorizePanel ap where (" +
					"lower(ap.name) = '" + panelName.toLowerCase() + "')";
			log.info("Get hql = " + hql);
			ap = (AuthorizePanel) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return ap;
	}

	public Settings getSettingByName(
			SmartPayConstants setting) {
		// TODO Auto-generated method stub
		Settings ap = null;
		try {
			String hql = "select ap from Settings ap where (" +
					"lower(ap.name) = '" + setting.getValue().toLowerCase() + "')";
			log.info("Get hql = " + hql);
			ap = (Settings) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return ap;
	}

	public Collection<AuthorizePanelCombination> getAuthorizedPanelCombinationByCompanyId(
			Long id) {
		// TODO Auto-generated method stub
		Collection<AuthorizePanelCombination> apcList = null;
		try {
			String hql = "select apc from AuthorizePanelCombination apc where (" +
					"ap.company.id = " + id;
			log.info("Get hql = " + hql);
			apcList = (Collection<AuthorizePanelCombination>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return apcList;
	}

	public Collection<FeeDescription> getFeeDescriptionListing(Boolean true1) {
		// TODO Auto-generated method stub
		Collection<FeeDescription> apcList = null;
		try {
			if(true1)
			{
				String hql = "select fd from FeeDescription fd where (" +
						"lower(fd.status) = lower('" + SmartPayConstants.FEE_DESCRIPTION_STATUS_ACTIVE.getValue() + "'))";
				log.info("Get hql = " + hql);
				apcList = (Collection<FeeDescription>) swpService.getAllRecordsByHQL(hql);
			}else
			{
				String hql = "select fd from FeeDescription fd where (" +
						"lower(fd.status) = lower('" + SmartPayConstants.FEE_DESCRIPTION_STATUS_INACTIVE.getValue() + "'))";
				log.info("Get hql = " + hql);
				apcList = (Collection<FeeDescription>) swpService.getAllRecordsByHQL(hql);
			}
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return apcList;
	}

	public Ports getPortByNameOrPortCode(
			String portName, String portCode) {
		// TODO Auto-generated method stub
		Ports fd = null;
		try {
			
				String hql = "select fd from Ports fd where (" +
						"lower(fd.fullName) = lower('" + portName + "') OR lower(fd.portCode) = lower('" + portCode + "')";
				log.info("Get hql = " + hql);
				fd = (Ports) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return fd;
	}
	
	
	public Collection<Ports> getPortByNameOrCodeAndNotId(
			String fullName, String portCode, Long id) {
		// TODO Auto-generated method stub
		Collection<Ports> fd = null;
		try {
			
				String hql = "select fd from Ports fd where (" +
						"(lower(fd.fullName) = lower('" + fullName + "') OR lower(fd.portCode)=lower('" + portCode + "'))  AND fd.id != " + id +")";
				log.info("Get hql = " + hql);
				fd = (Collection<Ports>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return fd;
	}

	public Collection<AuthorizePanel> getAllAuthorizePanels(boolean b) {
		// TODO Auto-generated method stub
		Collection<AuthorizePanel> fd = null;
		try {
			
				String hql = "select fd from AuthorizePanel fd where (" +
						"fd.status = 'true')";
				log.info("Get hql = " + hql);
				fd = (Collection<AuthorizePanel>) swpService.getAllRecordsByHQL(hql);
				log.info("fd===" + (fd!=null ? fd.size() : "NA"));
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return fd;
	}

	public Collection<FinancialAmountRestriction> getFinancialAmountRestrictionBasedOnBoundaries(
			Double upperLimit, Double lowerLimit,
			Long companyId) {
		// TODO Auto-generated method stub
		Collection<FinancialAmountRestriction> fd = null;
		
		try {
			
				String hql = "select fd from FinancialAmountRestriction fd where (" +
						"fd.upperLimitValue > " + lowerLimit+ ")";
				log.info("Get hql = " + hql);
				fd = (Collection<FinancialAmountRestriction>) swpService.getAllRecordsByHQL(hql);
				log.info("fd===" + (fd!=null ? fd.size() : "NA"));
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return fd;
	}

	public Collection<AuthorizePanelCombination> getPanelMappingToPortalUser(
			Long selectedMapPanelPortalUser, Long selectedMapPanel,
			Long selectedMappingId, boolean b) {
		// TODO Auto-generated method stub
		Collection<AuthorizePanelCombination> apcList = null;
		try
		{
			
			String hql = null;
			if(b==true)
			{
				hql = "select apc from AuthorizeCombination apc where (apc.portalUser.id = " + selectedMapPanelPortalUser + 
						" AND apc.authorizePanel.id = " + selectedMapPanel + ")";
			}else{
				hql = "select apc from AuthorizeCombination apc where (apc.portalUser.id = " + selectedMapPanelPortalUser + 
						" AND apc.authorizePanel.id = " + selectedMapPanel + ") and apc.id != " + selectedMappingId;
			}
			log.info("Get hql = " + hql);
			apcList = (Collection<AuthorizePanelCombination>) swpService.getAllRecordsByHQL(hql);
			log.info("fd===" + (apcList!=null ? apcList.size() : "NA"));
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return apcList;
		
	}

	public PortalUser getPortalUserByEmailAddress(
			String emailAddress) {
		// TODO Auto-generated method stub
		PortalUser fd = null;
		
		try {
			
				String hql = "select pu from PortalUser pu where (" +
						"fd.emailAddress = '" + emailAddress + "')";
				log.info("Get hql = " + hql);
				fd = (PortalUser) swpService.getUniqueRecordByHQL(hql);
				log.info("fd===" + (fd!=null ? fd.getEmailAddress() : "NA"));
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return fd;
	}

	public RoleType getRoleTypeByRoleTypeName(
			RoleTypeConstants roleType) {
		// TODO Auto-generated method stub
		RoleType rt = null;
		
		try {
			
				String hql = "select rt from RoleType rt where (" +
						"lower(rt.roleTypeName) = lower('" + roleType.getValue() + "'))";
				log.info("Get hql = " + hql);
				rt = (RoleType) swpService.getUniqueRecordByHQL(hql);
				log.info("rt===" + (rt!=null ? rt.getId() : "NA"));
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<PortalUser> getAllPortalUserByCompany(Company company) {
		// TODO Auto-generated method stub
		Collection<PortalUser> rt = null;
		
		try {
			
				String hql = "select rt from PortalUser rt where (" +
						"rt.company.id = " + company.getId() + ")";
				log.info("Get hql = " + hql);
				rt = (Collection<PortalUser>) swpService.getAllRecordsByHQL(hql);
				log.info("rt===" + (rt!=null ? rt.size() : "NA"));
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<Integer> getAuthorizedPanelCombinationByCompanyAndMapPanel(Long id,
			Long mapPanelId) {
		// TODO Auto-generated method stub
		Collection<Integer> rt = null;
		
		try {
			
				String hql = "select rt.position from AuthorizePanelCombination rt where (" +
						"rt.company.id = " + id + " AND rt.authorizePanel.id = " + mapPanelId +")";
				log.info("Get hql = " + hql);
				rt = (Collection<Integer>) swpService.getAllRecordsByHQL(hql);
				log.info("rt===" + (rt!=null ? rt.size() : "NA"));
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	
	
	public Collection<Ports> getAllPortsListing(boolean b) {
		// TODO Auto-generated method stub
		Collection<Ports> rt = null;
		
		try {
			
			if(b==true)
			{
				String hql = "select rt from Ports rt where (" +
						"rt.status = 'true')";
				log.info("Get hql = " + hql);
				rt = (Collection<Ports>) swpService.getAllRecordsByHQL(hql);
				log.info("rt===" + (rt!=null ? rt.size() : "NA"));
			}else
			{
				String hql = "select rt from Ports rt where (" +
						"rt.status = 'false')";
				log.info("Get hql = " + hql);
				rt = (Collection<Ports>) swpService.getAllRecordsByHQL(hql);
				log.info("rt===" + (rt!=null ? rt.size() : "NA"));
			}
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	

}
