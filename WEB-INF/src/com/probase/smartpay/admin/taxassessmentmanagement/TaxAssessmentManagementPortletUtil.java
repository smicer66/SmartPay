package com.probase.smartpay.admin.taxassessmentmanagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import antlr.collections.List;

import com.google.zxing.common.Collections;
import com.probase.smartpay.commins.ProbaseConstants;
import com.sf.primepay.smartpay13.ServiceLocator;

import smartpay.entity.Assessment;
import smartpay.entity.AuthorizePanel;
import smartpay.entity.AuthorizePanelCombination;
import smartpay.entity.BankBranches;
import smartpay.entity.Company;
import smartpay.entity.CompanyFeeDescription;
import smartpay.entity.FeeDescription;
import smartpay.entity.FinancialAmountRestriction;
import smartpay.entity.PaymentHistory;
import smartpay.entity.PortalUser;
import smartpay.entity.Ports;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.TaxType;
import smartpay.entity.TpinInfo;
import smartpay.entity.WorkFlow;
import smartpay.entity.WorkFlowAssessment;
import smartpay.entity.enumerations.CompanyStatusConstants;
import smartpay.entity.enumerations.PanelTypeConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.entity.enumerations.WorkFlowConstants;
import smartpay.service.SwpService;

public class TaxAssessmentManagementPortletUtil {

	
	private static TaxAssessmentManagementPortletUtil bankbranchUtil=null;
	SwpService swpService = null;
	com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	Logger log = Logger.getLogger(TaxAssessmentManagementPortletUtil.class);
	
	public TaxAssessmentManagementPortletUtil() {
		swpService = serviceLocator.getSwpService();
	}
	
	public static TaxAssessmentManagementPortletUtil getInstance()
	{
		if(bankbranchUtil==null)
		{
			TaxAssessmentManagementPortletUtil.bankbranchUtil = new TaxAssessmentManagementPortletUtil();
		}
		return TaxAssessmentManagementPortletUtil.bankbranchUtil;
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
								"rt.authorizePanel.financialAmountRestriction.lowerLimitValue <= " + amount + " AND " +
										"rt.authorizePanel.financialAmountRestriction.upperLimitValue >= " + amount;
				
				log.info("Get hql = " + hql);
				rt = (AuthorizePanelCombination) swpService.getUniqueRecordByHQL(hql);
				log.info(rt.getId());
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<AuthorizePanelCombination> getAuthorizedPanelCombinationForFirstAuthoriser(
			SmartPayConstants status,
			Double amount, Company company) {
		// TODO Auto-generated method stub
		AuthorizePanelCombination rt = null;
		Collection<AuthorizePanelCombination> rt1 = null;
		
		try {
			
				String hql = "select rt from AuthorizePanelCombination rt where " +
						"lower(rt.status) = lower('" + status.getValue() + "') AND " +
								"rt.authorizePanel.company.id = " + company.getId() + " AND  (" +
								"rt.authorizePanel.financialAmountRestriction.lowerLimitValue <= " + amount + " AND " +
										"rt.authorizePanel.financialAmountRestriction.upperLimitValue >= " + amount + ") AND " +
												"lower(rt.authorizePanel.authorizeType) = lower('" + 
													PanelTypeConstants.AUTHORIZE_PANEL_TYPE_AUTHORISER.getValue() + "') ORDER BY rt.position ASC";
				
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
						"lower(rt.status) = lower('" + status.getValue() + "') AND " +
								"rt.authorizePanel.company.id = " + company.getId() + " AND  (" +
								"rt.authorizePanel.financialAmountRestriction.lowerLimitValue <= " + amount + " AND " +
										"rt.authorizePanel.financialAmountRestriction.upperLimitValue >= " + amount + ") AND " +
												"lower(rt.authorizePanel.authorizeType) = lower('" + 
													PanelTypeConstants.AUTHORIZE_PANEL_TYPE_AUTHORISER.getValue() + "') AND " +
															"rt.position = " + rt.getPosition();
					
					log.info("Get hql = " + hql);
					rt1 = (Collection<AuthorizePanelCombination>) swpService.getAllRecordsByHQL(hql);
				}else
				{
					rt1= null;
				}
				
				
				
				
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt1;
	}
	
	
	public AuthorizePanelCombination getAuthorizedPanelCombinationForFirstInitiator(
			SmartPayConstants status,
			Double amount) {
		// TODO Auto-generated method stub
		AuthorizePanelCombination rt = null;
		Collection<AuthorizePanelCombination> rt1 = null;
		
		try {
			
				String hql = "select rt from AuthorizePanelCombination rt where " +
						"lower(rt.status) = lower('" + status.getValue() + "') AND (" +
								"rt.authorizePanel.financialAmountRestriction.lowerLimitValue < " + amount + " AND " +
										"rt.authorizePanel.financialAmountRestriction.upperLimitValue > " + amount + ") AND " +
												"lower(rt.authorizePanel.authorizeType) = lower('" + 
													PanelTypeConstants.AUTHORIZE_PANEL_TYPE_INITIATOR.getValue() + "') ORDER BY rt.position ASC";
				
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

	public TpinInfo getTpinInfoByTpId(String trip) {
		// TODO Auto-generated method stub
		TpinInfo rt = null;
		
		try {
			
				String hql = "select rt from TpinInfo rt where " +
						"lower(rt.tpin) = lower('" + trip + "')";
				
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

	public Collection<Assessment> getWorkFlowsByCompany(Company company) {
		// TODO Auto-generated method stub
		Collection<Assessment> rt = null;
		
		try {
			
			String hql = "select rt.assessment from WorkFlowAssessment rt where " +
					"rt.assessment.tpinInfo.company.id = " + company.getId();
//				String hql = "select rt.assessment.registrationNumber from WorkFlow rt where " +
//						"rt.assessment.tpinInfo.company.id = " + company.getId();
				
				log.info("Get hql = " + hql);
				rt = (Collection<Assessment>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		log.info("rt.size===" + rt.size());
		return rt;
	}

	public Collection<Assessment> getAssessmentsByCompany(Company company) {
		// TODO Auto-generated method stub
		Collection<Assessment> rt = null;
		
		try {
			
				String hql = "select rt from Assessment rt where " +
						"rt.tpinInfo.company.id = " + company.getId();
				
				log.info("Get hql = " + hql);
				rt = (Collection<Assessment>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Assessment getAssessmentByRegNo(String registrationNumber, Long id) {
		// TODO Auto-generated method stub
		Assessment rt = null;
		
		try {
			
				String hql = "select rt from Assessment rt where " +
						"rt.registrationNumber = '" + registrationNumber + "' AND rt.tpinInfo.company = " + id;
				
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
	
	
	public Double getTransactionFee(Long companyId)
	{
		
		
		try {
			
				String hql = "select rt from CompanyFeeDescription rt where " +
						"rt.company.id = " + companyId + " AND rt.status = 'true'";
				
				log.info("Get hql = " + hql);
				CompanyFeeDescription rt = (CompanyFeeDescription) swpService.getUniqueRecordByHQL(hql);
				
				if(rt!=null)
				{
					return rt.getFeeDescription().getAmountApplicable();
				}else
				{
					hql = "select rt from Settings rt where " +
							"lower(rt.name) = lower('" + SmartPayConstants.SETTINGS_PRIMARY_FEE_DESCRIPTION.getValue() + "')" +
									" AND rt.status = 'true'";
					
					log.info("Get hql = " + hql);
					Settings rt1 = (Settings) swpService.getUniqueRecordByHQL(hql);
					if(rt1!=null)
					{
						hql = "select rt from FeeDescription rt where " +
								"rt.id = " + rt1.getValue() + " AND rt.status = '" + SmartPayConstants.FEE_DESCRIPTION_STATUS_ACTIVE.getValue() + "'";
						log.info("Get hql = " + hql);
						FeeDescription rt2 = (FeeDescription) swpService.getUniqueRecordByHQL(hql);
						if(rt2!=null)
						{
							return rt2.getAmountApplicable();
						}else
						{
							return 0.00;
						}
						
					}else
					{
						return 0.00;
					}
				}
			
		} catch (HibernateException e) {
			log.error("",e);
			return 0.00;
		} catch (Exception e) {
			log.error("",e);
			return 0.00;
		} finally {
			
		}
	}

	public Collection<AuthorizePanelCombination> getNextInWorkFlow(TaxAssessmentManagementPortletState portletState, Double amount) {
		// TODO Auto-generated method stub
		Collection<AuthorizePanelCombination> apcCollection= null;
		apcCollection = portletState.getTaxAssessmentManagementPortletUtil().getAuthorizedPanelCombinationForFirstAuthoriser(
				SmartPayConstants.AUTHORIZE_PANEL_COMBINATION_STATUS_ACTIVE, amount, portletState.getPortalUser().getCompany());
		return apcCollection;
	}

	public Collection<AuthorizePanel> isAuthorizedPanelByCompanyExist(Long id,
			boolean b) {
		// TODO Auto-generated method stub
		Collection<AuthorizePanel> rt = null;
		
		try {
			
				String hql = "select rt from AuthorizePanel rt where " +
						"rt.company.id = " + id + " AND rt.status = 'true'";
				
				log.info("Get hql = " + hql);
				rt = (Collection<AuthorizePanel>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public TaxType getTaxTypeByTaxCode(String taxCode) {
		// TODO Auto-generated method stub
		TaxType rt = null;
		
		try {
			
				String hql = "select rt from TaxType rt where " +
						"rt.taxCode = '" + taxCode + "'";
				
				log.info("Get hql = " + hql);
				rt = (TaxType) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<Assessment> getAssessmentsPaidByCompany(Company company) {
		// TODO Auto-generated method stub
		Collection<Assessment> rt = null;
		
		try {
			
				String hql = "select rt from Assessment rt where " +
						"rt.tpinInfo.company.id = " + company.getId() + " AND (rt.paidFor = 'true' " +
								"OR rt.movedToWorkFlow = 'true')";
				
				log.info("Get hql = " + hql);
				rt = (Collection<Assessment>) swpService.getAllRecordsByHQL(hql);
				log.info("Size==" + rt.size());
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	
	public Collection<WorkFlowAssessment> getWorkFlowAssessmentByAssessment(Assessment assessment) {
		// TODO Auto-generated method stub
		Collection<WorkFlowAssessment> rt = null;
		
		try {
			
				String hql = "select rt from WorkFlowAssessment rt where " +
						"rt.assessment.id = " + assessment.getId();
				
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

	public Assessment getAssessmentByRegNoAssessmentYearAndPort(String regNo,
			Integer yr, Long portId) {
		// TODO Auto-generated method stub
		Assessment rt = null;
		
		try {
			
				String hql = "select rt from Assessment rt where " +
						"rt.registrationNumber = '" + regNo + "' AND " +
						"rt.assessmentYear = " + yr + " AND rt.ports.id = " +
						portId;
				 
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
	
	public Assessment getAssessmentByRegNoAssessmentYearAndPortAndTpin(String regNo,
			Integer yr, Long portId, Long tpin) {
		// TODO Auto-generated method stub
		Assessment rt = null;
		
		try {
			
				String hql = "select rt from Assessment rt where " +
						"rt.registrationNumber = '" + regNo + "' AND " +
						"rt.assessmentYear = " + yr + " AND rt.ports.id = " +
						portId + " AND rt.tpinInfo.id = " + tpin;
				 
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

	public Assessment getPaymentHistoryByTranxId(String id) {
		// TODO Auto-generated method stub
		Assessment rt = null;
		
		try {
			
				String hql = "select rt.assessment from PaymentHistory rt where " +
						"rt.transactionReferenceId = '" + id + "'";
				
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
	
	

}
