package com.probase.smartpay.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import antlr.collections.List;

import com.google.zxing.common.Collections;
import com.sf.primepay.smartpay13.ServiceLocator;

import smartpay.entity.Assessment;
import smartpay.entity.AuthorizePanel;
import smartpay.entity.AuthorizePanelCombination;
import smartpay.entity.BankBranches;
import smartpay.entity.Company;
import smartpay.entity.DomTax;
import smartpay.entity.FeeDescription;
import smartpay.entity.FinancialAmountRestriction;
import smartpay.entity.PortalUser;
import smartpay.entity.Ports;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.Tokens;
import smartpay.entity.TpinInfo;
import smartpay.entity.WorkFlow;
import smartpay.entity.WorkFlowAssessment;
import smartpay.entity.enumerations.CompanyStatusConstants;
import smartpay.entity.enumerations.PanelTypeConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.entity.enumerations.WorkFlowConstants;
import smartpay.service.SwpService;

public class WorkFlowPortletUtil {

	
	private static WorkFlowPortletUtil bankbranchUtil=null;
	SwpService swpService = null;
	com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	Logger log = Logger.getLogger(WorkFlowPortletUtil.class);
	
	public WorkFlowPortletUtil() {
		swpService = serviceLocator.getSwpService();
	}
	
	public static WorkFlowPortletUtil getInstance()
	{
		if(bankbranchUtil==null)
		{
			WorkFlowPortletUtil.bankbranchUtil = new WorkFlowPortletUtil();
		}
		return WorkFlowPortletUtil.bankbranchUtil;
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

	public FeeDescription getFeeDescriptionByName(
			String feeDescriptionName) {
		// TODO Auto-generated method stub
		FeeDescription fd = null;
		try {
			
				String hql = "select fd from FeeDescription fd where (" +
						"lower(fd.feeName) = lower('" + feeDescriptionName + "'))";
				log.info("Get hql = " + hql);
				fd = (FeeDescription) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return fd;
	}
	
	
	public FeeDescription getFeeDescriptionByNameAndNotId(
			String feeDescriptionName, Long id) {
		// TODO Auto-generated method stub
		FeeDescription fd = null;
		try {
			
				String hql = "select fd from FeeDescription fd where (" +
						"lower(fd.feeName) = lower('" + feeDescriptionName + "') AND fd.id != " + id +")";
				log.info("Get hql = " + hql);
				fd = (FeeDescription) swpService.getUniqueRecordByHQL(hql);
			
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

	public TpinInfo getTPINInfoByCompany(Long id) {
		// TODO Auto-generated method stub
		TpinInfo rt = null;
		
		try {
			
				String hql = "select rt from TpinInfo rt where (" +
						"rt.company.id = " + id + ")";
				log.info("Get hql = " + hql);
				rt = (TpinInfo) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<Ports> getAllPortListing() {
		// TODO Auto-generated method stub
		Collection<Ports> rt = null;
		
		try {
			
				String hql = "select rt from Ports rt";
				log.info("Get hql = " + hql);
				rt = (Collection<Ports>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Ports getPortByPortCode(String portCode) {
		// TODO Auto-generated method stub
		Ports rt = null;
		
		try {
			
				String hql = "select rt from Ports rt where lower(rt.portCode) = lower('" + portCode + "')";
				log.info("Get hql = " + hql);
				rt = (Ports) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<AuthorizePanelCombination> getAuthorizedPanelCombinationByPortalUser(PortalUser portalUser, SmartPayConstants status) {
		// TODO Auto-generated method stub
		Collection<AuthorizePanelCombination> rt = null;
		
		try {
			
				String hql = "select rt from AuthorizePanelCombination rt where rt.portalUser.id = " + portalUser.getId() + " AND " +
						"lower(rt.status) = lower('" + status.getValue() + "')";
				log.info("Get hql = " + hql);
				rt = (Collection<AuthorizePanelCombination>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	
	public AuthorizePanelCombination getAuthorizedPanelCombinationByPortalUserAndPanelThreshold(PortalUser portalUser, SmartPayConstants status, Double amount) {
		// TODO Auto-generated method stub
		AuthorizePanelCombination rt = null;
		
		try {
			
				String hql = "select rt from AuthorizePanelCombination rt where rt.portalUser.id = " + portalUser.getId() + " AND " +
						"lower(rt.status) = lower('" + status.getValue() + "') AND " +
								"rt.authorizePanel.financialAmountRestriction.lowerLimitValue < " + amount + " AND " +
										"rt.authorizePanel.financialAmountRestriction.upperLimitValue > " + amount;
				
				log.info("Get hql = " + hql);
				rt = (AuthorizePanelCombination) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public AuthorizePanelCombination getAuthorizedPanelCombinationForFirstAuthoriser(
			SmartPayConstants status,
			Double amount) {
		// TODO Auto-generated method stub
		AuthorizePanelCombination rt = null;
		
		try {
			
				String hql = "select rt from AuthorizePanelCombination rt where " +
						"lower(rt.status) = lower('" + status.getValue() + "') AND (" +
								"rt.authorizePanel.financialAmountRestriction.lowerLimitValue < " + amount + " AND " +
										"rt.authorizePanel.financialAmountRestriction.upperLimitValue > " + amount + ") AND " +
												"lower(rt.authorizePanel.authorizeType) = lower('" + 
													PanelTypeConstants.AUTHORIZE_PANEL_TYPE_AUTHORISER.getValue() + "')";
				
				log.info("Get hql = " + hql);
				rt = (AuthorizePanelCombination) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<WorkFlowAssessment> getWorkFlowsByReceipientId(Long id) {
		// TODO Auto-generated method stub
		Collection<WorkFlowAssessment> rt = null;
		//Select all workflow assessment that have workflows that have mandate panels that a user belongs to with the position
		try {
			
			String hql = "Select rt from WorkFlowAssessment rt where rt.workFlow.workFlowReceipientPanelId in " +
					"(Select apc.authorizePanel.id from AuthorizePanelCombination apc where apc.portalUser.id = " + id + " " +
					"AND apc.authorizePanel.status = 'true') " +
					"AND rt.workFlow.workFlowReceipientPositionId in " +
					"(Select apc.position from AuthorizePanelCombination apc where apc.portalUser.id = " + id + " " +
					"AND apc.authorizePanel.status = 'true') " +
					"AND (lower(rt.workFlow.status) = lower('" + WorkFlowConstants.WORKFLOW_STATUS_CREATED.getValue() + "') OR " +
					"lower(rt.workFlow.status) = lower('" + WorkFlowConstants.WORKFLOW_STATUS_FORWARDED.getValue() + "'))";
//				String hql = "select rt from WorkFlowAssessment rt where " +
//						"(lower(rt.workFlow.status) = lower('" + WorkFlowConstants.WORKFLOW_STATUS_CREATED.getValue() + "') OR " +
//								"lower(rt.workFlow.status) = lower('" + WorkFlowConstants.WORKFLOW_STATUS_FORWARDED.getValue() + "')) AND (" +
//								"rt.workFlow. in (select apc.portalUser.id FROM AuthorizePanelCombination apc  ) " + id + ")";
				
				
				
				log.info("Get hql = " + hql);
				rt = (Collection<WorkFlowAssessment>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	
	
	public Collection<WorkFlow> getWorkFlowListByReceipientId(Long id) {
		// TODO Auto-generated method stub
		Collection<WorkFlow> rt = null;
		
		try {
			
//				String hql = "select rt from WorkFlow rt where " +
//						"(lower(rt.status) = lower('" + WorkFlowConstants.WORKFLOW_STATUS_CREATED.getValue() + "') OR " +
//								"lower(rt.status) = lower('" + WorkFlowConstants.WORKFLOW_STATUS_FORWARDED.getValue() + "')) AND (" +
//								"rt.workFlowReceipientId = " + id + ")";
				String hql = "Select rt from WorkFlow rt where rt.workFlowReceipientPanelId in " +
						"(Select apc.authorizePanel.id from AuthorizePanelCombination apc where apc.portalUser.id = " + id + " AND " +
						"apc.authorizePanel.status = 'true') " +
						"AND rt.workFlowReceipientPositionId in " +
						"(Select apc.position from AuthorizePanelCombination apc where apc.portalUser.id = " + id + " AND " +
						"apc.authorizePanel.status = 'true') AND " +
						"(lower(rt.status) = lower('" + WorkFlowConstants.WORKFLOW_STATUS_CREATED.getValue() + "') OR " +
						"lower(rt.status) = lower('" + WorkFlowConstants.WORKFLOW_STATUS_FORWARDED.getValue() + "'))";
				
				log.info("Get hql = " + hql);
				rt = (Collection<WorkFlow>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	public Collection<WorkFlowAssessment> getWorkFlowsByCompany(Company company) {
		// TODO Auto-generated method stub
		Collection<WorkFlowAssessment> rt = null;
		
		try {
			
				String hql = "select rt from WorkFlowAssessment rt where " +
						"(lower(rt.workFlow.status) = lower('" + WorkFlowConstants.WORKFLOW_STATUS_CREATED.getValue() + "') OR " +
								"lower(rt.workFlow.status) = lower('" + WorkFlowConstants.WORKFLOW_STATUS_FORWARDED.getValue() + "')) AND " +
										"rt.assessment.tpinInfo.company.id = " + company.getId();
				
				log.info("Get hql = " + hql);
				rt = (Collection<WorkFlowAssessment>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public WorkFlow getWorkFlowByTokenAndReceipientId(String token, long id) {
		// TODO Auto-generated method stub
		WorkFlow rt = null;
		
		try {
			
				String hql = "select t.workFlow from Tokens t, WorkFlow wf, AuthorizePanelCombination apc where " +
						"lower(t.tokenValue) = lower('" + token + "') AND t.isValid = 'true' AND " +
						"t.workFlow.id = wf.id and wf.workFlowReceipientPanelId = " +
						"apc.authorizePanel.id and apc.portalUser.id =  " + id + 
						"AND lower(apc.status) = lower('" + SmartPayConstants.AUTHORIZE_PANEL_COMBINATION_STATUS_ACTIVE.getValue() + 
						"') AND apc.authorizePanel.status = 'true'";
				
				log.info("Get hql = " + hql);
				rt = (WorkFlow) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	
	public Collection<AuthorizePanelCombination> getAuthorizedPanelCombinationForSubsequentAuthorizers(
			SmartPayConstants status,
			Double amount,
			Integer currentPosition) {
		// TODO Auto-generated method stub
		AuthorizePanelCombination rt = null;
		Collection<AuthorizePanelCombination> rt1 = null;
		
		try {
			
				String hql = "select rt from AuthorizePanelCombination rt where " +
						"rt.authorizePanel.status = 'true' AND " +
						"lower(rt.status) = lower('" + status.getValue() + "') AND (" +
								"rt.authorizePanel.financialAmountRestriction.lowerLimitValue < " + amount + " AND " +
										"rt.authorizePanel.financialAmountRestriction.upperLimitValue > " + amount + ") AND " +
												"lower(rt.authorizePanel.authorizeType) = lower('" + 
													PanelTypeConstants.AUTHORIZE_PANEL_TYPE_AUTHORISER.getValue() + "') AND " +
															"rt.position > " + currentPosition + " ORDER by rt.position ASC";
				
				log.info("Get hql = " + hql);
				rt1 = (Collection<AuthorizePanelCombination>) swpService.getAllRecordsByHQL(hql);
				if(rt1!=null && rt1.size()>0)
				{
					Iterator<AuthorizePanelCombination> it = rt1.iterator();
					rt = it.next();
				}
				
				if(rt!=null)
				{
					hql = "select rt from AuthorizePanelCombination rt where " +
						"rt.authorizePanel.status = 'true' AND " +
						"lower(rt.status) = lower('" + status.getValue() + "') AND (" +
								"rt.authorizePanel.financialAmountRestriction.lowerLimitValue < " + amount + " AND " +
										"rt.authorizePanel.financialAmountRestriction.upperLimitValue > " + amount + ") AND " +
												"lower(rt.authorizePanel.authorizeType) = lower('" + 
													PanelTypeConstants.AUTHORIZE_PANEL_TYPE_AUTHORISER.getValue() + "') AND " +
															"rt.position = " + rt.getPosition();
					
					log.info("Get hql = " + hql);
					rt1 = (Collection<AuthorizePanelCombination>) swpService.getAllRecordsByHQL(hql);
				}
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt1;
	}

	public AuthorizePanelCombination getAuthorizedPanelCombinationForAPortalUser(
			PortalUser portalUser, Double amount,
			PanelTypeConstants panelType) {
		// TODO Auto-generated method stub
		AuthorizePanelCombination rt = null;
		Collection<AuthorizePanelCombination> rt1 = null;
		
		try {
			
				String hql = "select rt from AuthorizePanelCombination rt where " +
						"rt.authorizePanel.status = 'true' AND " +
						"lower(rt.status) = lower('" + SmartPayConstants.AUTHORIZE_PANEL_COMBINATION_STATUS_ACTIVE.getValue() + "') AND (" +
						"rt.authorizePanel.financialAmountRestriction.lowerLimitValue < " + amount + " AND " +
						"rt.authorizePanel.financialAmountRestriction.upperLimitValue > " + amount + ") " +
						"AND lower(rt.authorizePanel.authorizeType) = lower('" + panelType.getValue() +"') AND " +
						"rt.portalUser.id = " + portalUser.getId() + " ORDER by rt.position ASC";
				
				log.info("Get hql = " + hql);
				rt1 = (Collection<AuthorizePanelCombination>) swpService.getAllRecordsByHQL(hql);
				
				if(rt1!=null && rt1.size()>0)
				{
					Iterator<AuthorizePanelCombination> it = rt1.iterator();
					rt = it.next();
				}
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Assessment getAssessmentByRegNo(String regNo) {
		// TODO Auto-generated method stub
		Assessment rt = null;
		
		try {
			
				String hql = "select rt from Assessment rt where " +
						"lower(rt.registrationNumber) = lower('" + regNo +"')";
				
				log.info("Get hql = " + hql);
				rt = (Assessment) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<WorkFlowAssessment> getAllWorkFlowAssessmentbyWorkFlowId(
			Long id) {
		// TODO Auto-generated method stub
		Collection<WorkFlowAssessment> rt1 = null;
		
		try {
			
				String hql = "select rt from WorkFlowAssessment rt where " +
						"rt.workFlow.id = " + id + " AND " +
						"(lower(rt.status) = lower('" + WorkFlowConstants.WORKFLOW_STATUS_CREATED + "') OR " +
						"lower(rt.status) = lower('" + WorkFlowConstants.WORKFLOW_STATUS_FORWARDED + "')) ";
				
				log.info("Get hql = " + hql);
				rt1 = (Collection<WorkFlowAssessment>) swpService.getAllRecordsByHQL(hql);
				
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt1;
	}

	public Collection<Assessment> getAssessmentListingsOfWorkFlowAssessment(Long workFlowId) {
		// TODO Auto-generated method stub
		Collection<Assessment> rt1 = null;
		
		try {
			
				String hql = "select rt.assessment from WorkFlowAssessment rt where " +
						"rt.workFlow.id = " + workFlowId;
				
				log.info("Get hql = " + hql);
				rt1 = (Collection<Assessment>) swpService.getAllRecordsByHQL(hql);
				
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt1;
	}

	public Collection<WorkFlowAssessment> getWorkFlowAssessmentsByWorkFlow(
			WorkFlow workFlow) {
		// TODO Auto-generated method stub
		Collection<WorkFlowAssessment> rt1 = null;
		
		try {
			
				String hql = "select rt from WorkFlowAssessment rt where " +
						"rt.workFlow.id = " + workFlow.getId();
				
				log.info("Get hql = " + hql);
				rt1 = (Collection<WorkFlowAssessment>) swpService.getAllRecordsByHQL(hql);
				
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt1;
	}

	public Tokens getTokenByWorkFlow(WorkFlow workflow) {
		// TODO Auto-generated method stub
		Tokens rt1 = null;
		
		try {
			String hql = "select rt from Tokens rt where " +
						"rt.workFlow.id = " + workflow.getId();
			
			log.info("Get hql = " + hql);
			rt1 = (Tokens) swpService.getUniqueRecordByHQL(hql);
				
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt1;
	}

	public WorkFlowAssessment getWorkFlowAssessmentsByAssessment(
			Assessment tempAssessment, WorkFlow workflow) {
		// TODO Auto-generated method stub
		WorkFlowAssessment rt1 = null;
		
		try {
			String hql = "select rt from WorkFlowAssessment rt where " +
						"rt.assessment.id = " + tempAssessment.getId() + 
						" AND rt.workFlow.id = " + workflow.getId();
						
			log.info("Get hql = " + hql);
			rt1 = (WorkFlowAssessment) swpService.getUniqueRecordByHQL(hql);
				
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt1;
	}

	public Assessment getAssessmentByRegNoAssessmentYearAndPort(String regNo,
			Integer year, Long port) {
		// TODO Auto-generated method stub
		Assessment rt1 = null;
		
		try {
			String hql = "select rt from Assessment rt where " +
						"rt.registrationNumber = '" + regNo + "' AND rt.assessmentYear = " + year + " AND " +
								"rt.ports.id = " + port;
			
			log.info("Get hql = " + hql);
			rt1 = (Assessment) swpService.getUniqueRecordByHQL(hql);
				
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt1;
	}

	public AuthorizePanelCombination getCurrentPortalUserAPC(
			PortalUser portalUser, WorkFlow workflow) {
		// TODO Auto-generated method stub
		AuthorizePanelCombination rt1 = null;
		
		try {
			String hql = "select rt from AuthorizePanelCombination rt where " +
						"rt.portalUser.id = " + portalUser.getId() + " AND rt.authorizePanel.id in " +
						"(Select wf.workFlowReceipientPanelId from WorkFlow wf where wf.id = " + workflow.getId() + 
						" AND (lower(wf.status) = '" + WorkFlowConstants.WORKFLOW_STATUS_CREATED +"') OR " +
						"lower(wf.status) = '" + WorkFlowConstants.WORKFLOW_STATUS_FORWARDED +"')) AND " +
						"rt.position = " + workflow.getWorkFlowReceipientPositionId() + " AND lower(rt.status) = " +
						"lower('" + SmartPayConstants.AUTHORIZE_PANEL_COMBINATION_STATUS_ACTIVE.getValue() + "')";
			
			log.info("Get hql = " + hql);
			rt1 = (AuthorizePanelCombination) swpService.getUniqueRecordByHQL(hql);
				
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt1;
	}

	public Collection<AuthorizePanelCombination> getAPCForSubsequentPortalUsers(
			SmartPayConstants authorizePanelCombinationStatusActive,
			WorkFlow workflow) {
		// TODO Auto-generated method stub
		AuthorizePanelCombination rt = null;
		Collection<AuthorizePanelCombination> rt1 = null;
		
		try {
			
			String hql = "select rt from AuthorizePanelCombination rt where rt.authorizePanel.id in " +
					"(Select wf.workFlowReceipientPanelId from WorkFlow wf where wf.id = " + workflow.getId() + 
					" AND (lower(wf.status) = lower('" + WorkFlowConstants.WORKFLOW_STATUS_CREATED +"') OR " +
					"lower(wf.status) = lower('" + WorkFlowConstants.WORKFLOW_STATUS_FORWARDED +"'))) " +
							"" +
							"" +
							"AND " +
					"lower(rt.status) =  lower('" + authorizePanelCombinationStatusActive.getValue() + "') AND " +
					"rt.position > " + workflow.getWorkFlowReceipientPositionId() + " ORDER by rt.position ASC";
			
				log.info("Get hql = " + hql);
				rt1 = (Collection<AuthorizePanelCombination>) swpService.getAllRecordsByHQL(hql);
				if(rt1!=null && rt1.size()>0)
				{
					Iterator<AuthorizePanelCombination> it = rt1.iterator();
					rt = it.next();
				}
				
				if(rt!=null)
				{
					hql = "select rt from AuthorizePanelCombination rt where " +
						"lower(rt.status) = lower('" + authorizePanelCombinationStatusActive.getValue() + "') AND (" +
								"rt.authorizePanel.id = " + rt.getId() + ") AND " +
												"lower(rt.authorizePanel.authorizeType) = lower('" + 
													PanelTypeConstants.AUTHORIZE_PANEL_TYPE_AUTHORISER.getValue() + "') AND " +
															"rt.position = " + rt.getPosition();
					
					log.info("Get hql = " + hql);
					rt1 = (Collection<AuthorizePanelCombination>) swpService.getAllRecordsByHQL(hql);
				}
		
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt1;
	}

	public DomTax getDomTaxByWorkFlow(WorkFlow workflow) {
		// TODO Auto-generated method stub
		DomTax rt1 = null;
		
		try {
			String hql = "select rt.domTax from WorkFlowAssessment rt where " +
						"rt.workFlow.id = " + workflow.getId();
						
			log.info("Get hql = " + hql);
			rt1 = (DomTax) swpService.getUniqueRecordByHQL(hql);
				
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt1;
	}

	public WorkFlowAssessment getWorkFlowAssessmentsDomTax(
			DomTax selectedDomTax, WorkFlow workflow) {
		// TODO Auto-generated method stub
		WorkFlowAssessment rt1 = null;
		
		try {
			String hql = "select rt from WorkFlowAssessment rt where " +
						"rt.workFlow.id = " + workflow.getId() + " AND rt.domTax.id = " + selectedDomTax.getId();
						
			log.info("Get hql = " + hql);
			rt1 = (WorkFlowAssessment) swpService.getUniqueRecordByHQL(hql);
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt1;
	}
	

}
