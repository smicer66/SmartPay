package com.probase.smartpay.tester;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;

import smartpay.entity.PortalUser;
import smartpay.entity.Role;
import smartpay.service.SwpService;

import com.sf.primepay.smartpay13.ServiceLocator;


public class TesterPortletUtil {

	private static TesterPortletUtil merchantManagementPortletUtil=null;
	SwpService swpService = null;
	com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	Logger log = Logger.getLogger(TesterPortletUtil.class);
	
	public TesterPortletUtil() {
		swpService = serviceLocator.getSwpService();
	}
	
	public static TesterPortletUtil getInstance()
	{
		if(merchantManagementPortletUtil==null)
		{
			TesterPortletUtil.merchantManagementPortletUtil = new TesterPortletUtil();
		}
		return TesterPortletUtil.merchantManagementPortletUtil;
	}
	
	
	public Role getRoleByName(String name)
	{
		Role role = null;
		try {
			String hql = "select m from Role m where " +
					"m.role = '" + name + "'";
			log.info("Get Role = " + hql);
			role = (Role) swpService.getUniqueRecordByHQL(hql);
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return role;
	}
	
//	
//	
//	public Double getTotalSumByServiceCode(ServiceCode sc)
//	{
//		Double totalSum = 0.0;
//		try {
//			String hql = "select sum(amount) as total from Transaction m where " +
//					"m.serviceCode = '" + sc.getCode() + "' AND m.status = " +
//							" '" + BytePayAppConstant.PAYMENT_STATUS_APPROVED + "'" ;
//			
//			List<Object[]> list = PrbCustomService.getInstance().executeQuery(hql);
//			if(list.size()>0)
//			{				
//				for (Object[] row: list) {
//					totalSum = totalSum + (Double)row[1];
//				}
//			}
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return totalSum;
//	}
//	
//	public List getUniqueTransactionByMerchantAndServiceCode(Merchant m)
//	{
//		Double totalSum = 0.0;
//		List<Object[]> list= null;
//		try {
//			String hql = "SELECT s.code FROM Transaction t WHERE " ;
//			//SELECT s.code as acode, (SELECT sum(amount) from com.bw.entity.Transaction m, com.bw.entity.ServiceCode s1 
//			//WHERE s1.code = acode and m.serviceCode.id = s1.id) as amount from com.bw.entity.Transaction m, 
//			//com.bw.entity.ServiceCode s WHERE m.merchant.id = 102 GROUP BY s.code]
//			
//			list = PrbCustomService.getInstance().executeQuery(hql);
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return list;
//	}
//	
//	
//	public Collection<Reconciliation>  getAllReconciliationByStatus(String value)
//	{
//		Collection<Reconciliation> role = null;
//		try {
//			String hql = "select m from Reconciliation m where " +
//					"m.status = '" + value + "'";
//			
//			role = (Collection<Reconciliation> ) swpService.getUniqueRecordByHQL(hql);
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return role;
//	}
//	
//	
//	public Collection<Merchant> getMerchantsByPortalUser(PortalUser portalUser)
//	{
//		Collection<Merchant> merchant = null;
//		try {
//			String hql = "select m from Merchant m where " +
//					"m.portalUser.id = '" + portalUser.getId() + "' and " +
//							"m.portalUser.approvedYes = 'true'";
//			
//			merchant = (Collection<Merchant>) swpService.getAllRecordsByHQL(hql);
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return merchant;
//	}
//
//	public Collection<Merchant> getMerchantsByCriteria(String hql) {
//		// TODO Auto-generated method stub
//		Collection<Merchant> merchant = null;
//		try {
//			merchant = (Collection<Merchant>) swpService.getAllRecordsByHQL(hql);
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return merchant;
//	}
//
//	
//	
//	public Collection<Merchant> getAllMerchantList()
//	{
//		Collection<Merchant> merchant = null;
//		try {
//			String hql = "select m from Merchant m";
//			
//			merchant = (Collection<Merchant>) swpService.getAllRecordsByHQL(hql);
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return merchant;
//	}
//
//	public PortalUserType getPortalUserTypeByName(String string) {
//		// TODO Auto-generated method stub
//		PortalUserType portalUserType = null;
//		try {
//			String hql = "select pt from PortalUserType pt where " +
//					"pt.name = '" + string + "'";
//			
//			portalUserType = (PortalUserType) swpService.getUniqueRecordByHQL(hql);
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return portalUserType;
//	}
//
//	
//	
//	public Collection<Merchant> getAllMerchantListByStatus(String status)
//	{
//		ArrayList<Merchant> merchantList = new ArrayList<Merchant>();
//		boolean returnValue = false;
//		try {
//			String hql = "select m from Merchant m where " +
//					"m.portalUser.status = '" + status + "'";
//			Collection<Merchant> merchantCollection = (Collection<Merchant>) swpService.getAllRecordsByHQL(hql);
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return merchantList;
//	}
//	
//	
//	public boolean deactivateMerchant(long id, BytePayAppConstant bpac) {
//		// TODO Auto-generated method stub
//		Merchant merchant = null;
//		boolean returnValue = false;
//		try {
//			String hql = "select m from Merchant m where " +
//					"m.id = '" + id + "'";
//			
//			merchant = (Merchant) swpService.getUniqueRecordByHQL(hql);
//			
//			if(merchant!=null)
//			{
//				merchant.setStatus(bpac.getValue());
//				swpService.updateRecord(merchant);
//				returnValue = true;
//			}
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return returnValue;
//	}

	public ArrayList<Role> getRolesByPortalUser(PortalUser portalUser) {
		// TODO Auto-generated method stub
		ArrayList<Role> roleList = new ArrayList<Role>();
//		boolean returnValue = false;
//		try {
//			String hql = "select m from PortalUserRole m where " +
//					"m.portalUser.id = " + portalUser.getId();
//			log.info("GET THE PORTALUSER ROLE ==" + hql);
//			Collection<PortalUserRole> roleCollection = (Collection<PortalUserRole>) swpService.getAllRecordsByHQL(hql);
//			if(roleCollection==null || roleCollection.size()==0)
//			{
//				roleList = null;
//			}else
//			{
//				for(Iterator<PortalUserRole> iter = roleCollection.iterator(); iter.hasNext();)
//				{
//					PortalUserRole purole = iter.next();
//					roleList.add(purole.getRole());
//				}
//			}
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
		return roleList;
	}

	public Collection<Role> getAllRoles() {
		// TODO Auto-generated method stub
		Collection<Role> roleList = null;
		try {
			String hql = "select m from Role m";
			roleList = (Collection<Role>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return roleList;
	}

//	public Collection<Bank> getAllBanks() {
//		// TODO Auto-generated method stub
//		Collection<Bank> bankList = null;
//		try {
//			String hql = "select m from Bank m";
//			bankList = (Collection<Bank>) swpService.getAllRecordsByHQL(hql);
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return bankList;
//	}
//
//	public Collection<PortalUserType> getAllPortalUserType() {
//		// TODO Auto-generated method stub
//		Collection<PortalUserType> portalUserTypeList = null;
//		try {
//			String hql = "select m from PortalUserType m";
//			portalUserTypeList = (Collection<PortalUserType>) swpService.getAllRecordsByHQL(hql);
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return portalUserTypeList;
//	}
//	
//	
//	
//	/***finances section starts here***/
//	
//	
//	
//	
//	public Collection<MerchantFinancialAccount> getMerchantFAccountsLinkedToSCodes()
//	{
//		Collection<MerchantFinancialAccount> merchantList = null;
////		
////		try {
////			String hql = "select unique from Transaction t WHERE " +
////					"t.dateOfPayment = '" + timestamp + "' AND t.status = '" + 
////					BytePayAppConstant.PAYMENT_STATUS_APPROVED + "'";
////			
////			merchantList = (Collection<Merchant>)swpService.getAllRecordsByHQL(hql);
////			
////		} catch (HibernateException e) {
////			log.error("",e);
////		} catch (Exception e) {
////			log.error("",e);
////		} finally {
////			
////		}
//		return merchantList;
//	}
//	
//	
//	public Collection<ServiceCode> getAllServiceCodes()
//	{
//		Collection<ServiceCode> serviceCodes = null;
//		try {
//			String hql = "select s from ServiceCode s";
//			
//			serviceCodes = (Collection<ServiceCode>) swpService.getAllRecordsByHQL(hql);
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return serviceCodes;
//	}
//	
//	
//
//
//	public Collection<MerchantFinancialAccount> getMerchantFinancialAccountByPortalUser(
//			PortalUser portalUser) {
//		// TODO Auto-generated method stub
//		Collection<MerchantFinancialAccount> merchantFinancialAccount = null;
//		
//		
//		try {
//			String hql = "select ma from MerchantFinancialAccount ma where " +
//					"ma.merchant.portalUser.id = '" + portalUser.getId() + "' and " +
//							"ma.merchant.portalUser.approvedYes = 'true'";
//			
//			merchantFinancialAccount = (Collection<MerchantFinancialAccount>) swpService.getAllRecordsByHQL(hql);
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return merchantFinancialAccount;
//	}
//	
//	public Collection<MerchantFinancialAccount> getAllMerchantFinancialAccount() {
//		// TODO Auto-generated method stub
//		Collection<MerchantFinancialAccount> merchantFinancialAccount = null;
//		
//		
//		try {
//			String hql = "select m from MerchantFinancialAccount m";
//			log.info("HQL ===>" + hql);
//			
//			merchantFinancialAccount = (Collection<MerchantFinancialAccount>) swpService.getAllRecordsByHQL(hql);
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return merchantFinancialAccount;
//	}
//	
	
//	public Collection<MerchantFinancialAccount> getMerchantFinancialAccountByPortalUser(PortalUser portalUser)
//	{
//		Collection<MerchantFinancialAccount> merchantFinancialAccount = null;
//		try {
//			String hql = "select m from MerchantFinancialAccount m where " +
//					"m.merchant.portalUser.id = '" + portalUser.getId() + "' and " +
//							"m.merchant.portalUser.approvedYes = 'true'";
//			
//			merchantFinancialAccount = (Collection<MerchantFinancialAccount>) swpService.getAllRecordsByHQL(hql);
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return merchantFinancialAccount;
//	}
	
//
//	public Collection<MerchantFinancialAccount> getMerchantFinancialAccountsByCriteria(String hql) {
//		// TODO Auto-generated method stub
//		Collection<MerchantFinancialAccount> merchantFinancialAccount = null;
//		try {
//			merchantFinancialAccount = (Collection<MerchantFinancialAccount>) swpService.getAllRecordsByHQL(hql);
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return merchantFinancialAccount;
//	}
//	
//	
//	public Collection<MerchantFinancialAccount> getMerchantFinancialAccountsMappedByServiceCode(ServiceCode serviceCode) {
//		// TODO Auto-generated method stub
//		Collection<MerchantFinancialAccount> merchantFinancialAccount = null;
//		try {
//			String hql = "Select distinct rm.merchantFinancialAccount from ReconciliationMerchantFinancialAccount rm " +
//					"where rm.serviceCode.id = " + serviceCode.getId();
//			
////			"SELECT rm  from ReconciliationMerchantFinancialAccount rm " +
////			"WHERE rm.merchantFinancialAccount.accountNumber IN ( select "
//			merchantFinancialAccount = (Collection<MerchantFinancialAccount>) swpService.getAllRecordsByHQL(hql);
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return merchantFinancialAccount;
//	}
//	
//	
//	
//	public Double getTotalAmountCollected(MerchantFinancialAccount merchantFinancialAccount)
//	{
//		Double totalAmount = 0.0;
//		
//		try {
//			String hql = "select m from Reconciliation m where " +
//					"m.reconciliationMerchantFinancialAccount.merchantFinancialAccount.accountNumber" +
//					" = '" + merchantFinancialAccount.getAccountNumber() + "'";
//			log.info("hql == " + hql);
//			//merchantFinancialAccount = (Collection<MerchantFinancialAccount>) swpService.getAllRecordsByHQL(hql);
//			Collection<Reconciliation> rMerchantFinancialAccountList = swpService.getAllRecordsByHQL(hql);
//			for(Iterator<Reconciliation> rMerchantFinIter = 
//					rMerchantFinancialAccountList.iterator(); 
//					rMerchantFinIter.hasNext();)
//			{
//				Reconciliation reconciliation = 
//						(Reconciliation)rMerchantFinIter.next(); 
//				totalAmount = totalAmount + 
//						reconciliation.getAmountReconciled();
//				
//			}
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return totalAmount;
//	}
//
//	public Collection<Transaction> getTransactionByCriteria(String hql) {
//		// TODO Auto-generated method stub
//		Collection<Transaction> transactionList = null;
//		try {
//			log.info("HQL+++++" + hql);
//			transactionList = (Collection<Transaction>) swpService.getAllRecordsByHQL(hql);
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return transactionList;
//	}
//
//	public Transaction getTransactionById(String transactionId) {
//		// TODO Auto-generated method stub
//		Transaction transaction = null;
//		
//		try {
//			String hql = "select t from Transaction t where " +
//					"t.id = " + transactionId;
//			
//			transaction = (Transaction)swpService.getUniqueRecordByHQL(hql);
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return transaction;
//	}
//	
//	public OfflineReversalPayment getOfflineReversalByTransactionId(long id)
//	{
//		OfflineReversalPayment offlineReversalPaymentList = null;
//		
//		try {
//			String hql = "select ofrp from OfflineReversalPayment ofrp WHERE " +
//					"ofrp.transaction.id = " + 
//					id + "";
//			
//			offlineReversalPaymentList = (OfflineReversalPayment)swpService.getUniqueRecordByHQL(hql);
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return offlineReversalPaymentList;
//	}
//	
//	public int getTransactionCountByDateAndMerchant(Timestamp stamp, Merchant merchant)
//	{
//		Collection<Merchant> merchantList = null;
//		
//		try {
//			String hql = "select t from Transaction t WHERE " +
//					"t.dateOfPayment = '" + stamp + "' AND t.status = '" + 
//					BytePayAppConstant.PAYMENT_STATUS_APPROVED + "'";
//			
//			merchantList = (Collection<Merchant>)swpService.getAllRecordsByHQL(hql);
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return merchantList.size();
//	}
//	
//	
//	public Collection<Merchant> getUniqueTransactionMerchantList(Timestamp timestamp)
//	{
//		Collection<Merchant> merchantList = null;
//		
//		try {
//			String hql = "select distinct t.merchant from Transaction t WHERE " +
//					"t.dateOfPayment = '" + timestamp + "' AND t.status = '" + 
//					BytePayAppConstant.PAYMENT_STATUS_APPROVED + "'";
//			
//			merchantList = (Collection<Merchant>)swpService.getAllRecordsByHQL(hql);
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return merchantList;
//	}
//
//	public Merchant getMerchantByPortalUser(PortalUser portalUser) {
//		// TODO Auto-generated method stub
//		Merchant merchant = null;
//		
//		try {
//			String hql = "select m from Merchant m WHERE " +
//					"m.portalUser.id = '" + portalUser.getId() + "' AND m.status = '" + 
//					BytePayAppConstant.MERCHANT_STATUS_ACTIVE.getValue() + "'";
//			log.info("HQL ===>" + hql);
//			merchant = (Merchant)swpService.getUniqueRecordByHQL(hql);
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return merchant;
//	}
//
//	public Bank getBankById(long accountbank) {
//		// TODO Auto-generated method stub
//		Bank bank = null;
//		
//		try {
//			String hql = "select b from Bank b WHERE " +
//					"b.id = " + accountbank + "";
//			
//			bank = (Bank)swpService.getUniqueRecordByHQL(hql);
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return bank;
//	}
//
//	public ServiceCode getServiceCodeById(int serviceId) {
//		// TODO Auto-generated method stub
//		ServiceCode serviceCode = null;
//		
//		try {
//			String hql = "select b from ServiceCode b WHERE " +
//					"b.id = '" + serviceId + "'";
//			
//			serviceCode = (ServiceCode)swpService.getUniqueRecordByHQL(hql);
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return serviceCode;
//	}
//
//	public Collection<MerchantFinancialAccount> getFinancesAccountByNumberAndBank(String accountNumber,
//			long accountBank, Merchant merchant) {
//		// TODO Auto-generated method stub
//		Collection<MerchantFinancialAccount> merchantFinancialAccount = null;
//		
//		try {
//			String hql = "select b from MerchantFinancialAccount b WHERE " +
//					"b.accountNumber = '" + accountNumber + "' AND " +
//							"b.bank.id = " + accountBank + " AND b.merchant.id = " + merchant.getId();
//			
//			merchantFinancialAccount = (Collection<MerchantFinancialAccount>)swpService.getAllRecordsByHQL(hql);
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return merchantFinancialAccount;
//	}
//
//	public MerchantFinancialAccount getMerchantFinancialAccountById(
//			int accountId) {
//		// TODO Auto-generated method stub
//		MerchantFinancialAccount merchantFinancialAccount = null;
//		
//		try {
//			String hql = "select b from MerchantFinancialAccount b WHERE " +
//					"b.id = " + accountId;
//			log.info("hql==" + hql);
//			merchantFinancialAccount = (MerchantFinancialAccount)swpService.getUniqueRecordByHQL(hql);
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return merchantFinancialAccount;
//	}
//
//	public boolean isServiceCodeMapped(ServiceCode serviceCode) {
//		// TODO Auto-generated method stub
//		ReconciliationMerchantFinancialAccount rmfa = null;
//		
//		try {
//			String hql = "select b from ReconciliationMerchantFinancialAccount b WHERE " +
//					"b.serviceCode.id = " + serviceCode.getId();
//			log.info("hql==" + hql);
//			rmfa = (ReconciliationMerchantFinancialAccount)swpService.getUniqueRecordByHQL(hql);
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return (rmfa!=null) ? true : false;
//	}
//
//	public Collection<ReconciliationMerchantFinancialAccount> selectRMFAByServiceCodeAndNotDateMapped(ServiceCode sc, Timestamp timestamp) {
//		// TODO Auto-generated method stub
//		Collection<ReconciliationMerchantFinancialAccount> rmfa = null;
//		try {
//			String hql = "Select b FROM ReconciliationMerchantFinancialAccount b WHERE " +
//					"b.serviceCode.id = " + sc.getId() + " AND b.dateMapped != '" + timestamp + "'";
//			log.info("hql==" + hql);
//			rmfa = (Collection<ReconciliationMerchantFinancialAccount>)swpService.getUniqueRecordByHQL(hql);
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return rmfa;
//	}
//
//	public Collection<MerchantFinancialAccount> getMerchantFinancialAccountUsingHQL(String hql) {
//		// TODO Auto-generated method stub
//		Collection<MerchantFinancialAccount> rmfa = null;
//		try {
//			
//			log.info("hql==" + hql);
//			rmfa = (Collection<MerchantFinancialAccount>)swpService.getAllRecordsByHQL(hql);
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return rmfa;
//	}
//	
//	public Collection<Transaction> getTransactionByStatusAndMerchantList(String[] status, Long merchantId) {
//		// TODO Auto-generated method stub
//		Collection<Transaction> txnList = null;
//		try {
//			String status_ = "";
//			for(int c=0; c<status.length; c++)
//			{
//				status_ = status_ + " t.status = '" + status[c] + "'";
//				status_ = (c==(status.length-1)) ? "" : (status_  + " OR ");
//			}
//			if(merchantId !=null)
//			{
//				status_ = (status_.length()>0) ? status_ + " AND (t.merchant.id = " + merchantId + ")" : (status_  + " (t.merchant.id = " + merchantId.longValue() + ")");
//			}
//			status_ = (status_.length()>0) ? " WHERE " + status_ : "";
//			String hql = "Select t from Transaction" + status_;
//			log.info("hql==" + hql);
//			txnList = (Collection<Transaction>)swpService.getAllRecordsByHQL(hql);
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return txnList;
//	}
//
//	public Merchant getMerchantById(String merchantId, PortalUser portalUser) {
//		// TODO Auto-generated method stub
//		log.info("Just got in==");
//		Merchant merchant = null;
//		long mId = 0;
//		try {
//			try
//			{
//				log.info("Just got in==");
//				mId = Long.valueOf(merchantId);
//				log.info("Just got in==");
//			}catch(NumberFormatException e)
//			{
//				log.info("Just got in==");
//			}
//			log.info("Just got in==");
//			if(mId>0)
//			{
//				log.info("Just got in==");
//				if(portalUser.getPortalUserType().getName().equals(BytePayAppConstant.PORTAL_USER_TYPE_ADMIN.getValue()))
//				{
//					String hql = "Select m from Merchant m where m.id = " + mId;
//					log.info("hql==" + hql);
//					merchant = (Merchant)swpService.getUniqueRecordByHQL(hql);
//				}
//				else if(portalUser.getPortalUserType().getName().equals(BytePayAppConstant.PORTAL_USER_TYPE_MERCHANT.getValue()))
//				{
//					String hql = "Select m from Merchant m where m.id = " + mId + " AND m.portalUser.id = " + portalUser.getId();
//					log.info("hql==" + hql);
//					merchant = (Merchant)swpService.getUniqueRecordByHQL(hql);
//				}
//			}
//			else
//			{
//				merchant = null;
//			}
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return merchant;
//	}
//
//	public Transaction getTransactionByTransactionRefId(String orderId) {
//		// TODO Auto-generated method stub
//		log.info("Just got in==");
//		Transaction transaction = null;
//		long mId = 0;
//		try {
//			String hql = "Select t from Transaction t where t.transactionReferenceId = '" + orderId + "'";
//			log.info("hql==" + hql);
//			transaction = (Transaction)swpService.getUniqueRecordByHQL(hql);
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return transaction;
//	}

//	public ApprovalPersonnel getApprovalPersonnelListByPortalUser(String entityname, Long portalUserId) {
//		// TODO Auto-generated method stub
//		log.info("Just got in==");
//		ApprovalPersonnel approvalPersonnel = null;
//		long mId = 0;
//		try {
//			String hql = "Select t from Approval t where t.entityName = '" + entityname + "' AND t.status IS true AND t.portalUser.id = " + portalUserId;
//			log.info("hql==" + hql);
//			approvalPersonnel = (ApprovalPersonnel)swpService.getUniqueRecordByHQL(hql);
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return approvalPersonnel;
//	}

	
	
	public Collection<PortalUser> getPortaluserByPortalUserType(String userType)
	{
		log.info("Just got in==");
		Collection<PortalUser> puList = null;
		long mId = 0;
		try {
			String hql = "Select t from PortalUser t where t.portalUserType.name = '" + userType + "'";
			log.info("hql==" + hql);
			puList = (Collection<PortalUser>)swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return puList;
	}
	
	
	
//	public Collection<MerchantFinancialAccount> getAllMerchantFinancialAccountByStatus(
//			String value) {
//		// TODO Auto-generated method stub
//		log.info("Just got in==");
//		Collection<MerchantFinancialAccount> mfaList = null;
//		long mId = 0;
//		try {
//			String hql = "Select t from MerchantFinancialAccount t where t.status = '" + value + "'";
//			log.info("hql==" + hql);
//			mfaList = (Collection<MerchantFinancialAccount>)swpService.getUniqueRecordByHQL(hql);
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return mfaList;
//	}
//
//	
//	
//	public JSONObject getTotalAmountCollectableByMerchant(Merchant merchant)
//	{
//		JSONObject mfaList = null;
//		
//		long mId = 0;
//		try {
//			String hql = "Select sum(t.amount) as generalAmount, t.servicecode.code from Transaction t group by t.servicecode";
//			log.info("hql==" + hql);
//			List<Object[]> list = PrbCustomService.getInstance().executeQuery(hql);
//			if(list.size()>0)
//			{				
//				mfaList = new JSONObject();
//				for (Object[] row: list) {
//					mfaList.put((String)row[1], (Double)row[1]);
//				    System.out.println(" ------------------- ");
//				    System.out.println("generalAmount: " + row[0]);
//				    System.out.println("code: " + row[1]);
//				}
//			}
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return mfaList;
//	}
//	
//	
//	
//	public boolean isUserAlreadyMapped(String entity, Long pId, Integer positionId) {
//		// TODO Auto-generated method stub
//		log.info("Just got in==");
//		Collection<ApprovalPersonnel> mfaList = null;
//		long mId = 0;
//		try {
//			String hql = "Select t from ApprovalPersonnel t where t.approvingEntity = '" + entity + "' " +
//					"AND  t.position = " + positionId.intValue() 
//					+ " AND " +
//							" t.portalUser.id = '" + pId + "'";
//			log.info("hql==" + hql);
//			mfaList = (Collection<ApprovalPersonnel>)swpService.getUniqueRecordByHQL(hql);
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return mfaList==null ? false : true;
//	}

	public PortalUser getPortalUserById(Long pId) {
		// TODO Auto-generated method stub
		log.info("Just got in==");
		PortalUser mfaList = null;
		long mId = 0;
		try {
			String hql = "Select t from PortalUser pu where pu.id = " + pId + "";
			log.info("hql==" + hql);
			mfaList = (PortalUser)swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return mfaList;
	}
	
//	public Collection<ApprovalPersonnel> getAllAssignedUsers(String entity) {
//		// TODO Auto-generated method stub
//		log.info("Just got in==");
//		Collection<ApprovalPersonnel> mfaList = null;
//		long mId = 0;
//		try {
//			String hql = "Select t from ApprovalPersonnel t where t.approvingEntity = '" + entity + "'";
//			log.info("hql==" + hql);
//			mfaList = (Collection<ApprovalPersonnel>)swpService.getUniqueRecordByHQL(hql);
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return mfaList;
//	}
//	
//	
//	public ReconciliationMerchantFinancialAccount getRMFAByServiceCode(ServiceCode serviceCode)
//	{
//		log.info("Just got in==");
//		ReconciliationMerchantFinancialAccount rmfaList = null;
//		long mId = 0;
//		try {
//			String hql = "Select t from ReconciliationMerchantFinancialAccount t where t.serviceCode.id = '" + serviceCode.getId() + "'";
//			log.info("hql==" + hql);
//			rmfaList = (ReconciliationMerchantFinancialAccount)swpService.getUniqueRecordByHQL(hql);
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return rmfaList;
//	}
//	
//	
//	public Collection<ServiceCode> getUniqueServiceCodeByMerchantAndDate(Merchant merchant, Timestamp timestamp)
//	{
//		log.info("Just got in==");
//		Collection<ServiceCode> list= null;
//		long mId = 0;
//		try {
////			String hql = "Select t.serviceCode from Transaction t where " +
////					"t.merchant.id = " + merchant.getId() + " AND t.dateOfPayment = '" + timestamp+ "' AND " +
////							"t.status = '" + BytePayAppConstant.PAYMENT_STATUS_APPROVED.getValue() + "' GROUP BY t.serviceCode";
//			
//			String hql = "Select distinct t.serviceCode from Transaction t where " +
//					"t.merchant.id = " + merchant.getId() + " AND date(t.dateOfPayment) = date('" + timestamp+ "') AND " +
//							"t.status = '" + BytePayAppConstant.PAYMENT_STATUS_APPROVED.getValue() + "'";
//			log.info("hql==" + hql);
//			list = (Collection<ServiceCode>)swpService.getAllRecordsByHQL(hql);
//			
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return list;
//	}
//
//	
//	
//	public JSONObject getTotalSumOfTransactionsByDateAndMerchant(Merchant merchant, Timestamp timestamp, ServiceCode sc) {
//		// TODO Auto-generated method stub
//		log.info("Just got in==");
//		Collection<Transaction> list = null;
//		Double sum = 0.00;
//		long mId = 0;
//		JSONObject js = null;
//		try {
//			String hql = "Select t from Transaction t where " +
//					"t.merchant.id = " + merchant.getId() + " AND date(t.dateOfPayment) = date('" + timestamp+ "') AND " +
//							"t.status = '" + BytePayAppConstant.PAYMENT_STATUS_APPROVED.getValue() + "' AND " +
//									"t.serviceCode.id = " + sc.getId();
//			log.info("hql==" + hql);
//			list = (Collection<Transaction>)swpService.getAllRecordsByHQL(hql);
//			
//			
//			for(Iterator<Transaction> it = list.iterator(); it.hasNext();)
//			{
//				Transaction tct = it.next();
//				sum = sum + tct.getAmount();
//			}
//			
//			js = new JSONObject();
//			js.put("totalCount", list.size());
//			js.put("totalAmount", sum);
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return js;
//	}
//	
//	
//	
//	public boolean isReconciled(ServiceCode serviceCode, Timestamp timestamp)
//	{
//		Collection<Transaction> txnList = null;
//		long mId = 0;
//		Double totalAmount = 0.00;
//		try {
//			String hql = "Select t from Reconciliation t where " +
//					"t.reconciliationMerchantFinancialAccount.serviceCode.code = '" + serviceCode.getCode()+ "' " +
//							"AND date(t.dateReconciled) = date('" + timestamp + "')";
//			log.info("hql==" + hql);
//			txnList = swpService.getAllRecordsByHQL(hql);
//			
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return (txnList!=null && txnList.size()> 0) ? true : false; 
//	}
//	public JSONObject getTotalSumOfTransactionsByDate(Timestamp timestamp) {
//		// TODO Auto-generated method stub
//		log.info("Just got in==");
//		JSONObject jsObj = null;
//		long mId = 0;
//		Double totalAmount = 0.00;
//		try {
//			String hql = "Select t from Transaction t where " +
//					"t.dateOfPayment = '" + timestamp+ "' AND t.status = '" + BytePayAppConstant.PAYMENT_STATUS_APPROVED.getValue() + "'";
//			log.info("hql==" + hql);
//			List<Object[]> list = PrbCustomService.getInstance().executeQuery(hql);
//			Collection<Transaction> txnList = swpService.getAllRecordsByHQL(hql);
//			for(Iterator<Transaction> t = txnList.iterator(); t.hasNext();)
//			{
//				Transaction txn = t.next();
//				totalAmount = totalAmount + txn.getAmount();
//			}
//			if(txnList.size()>0)
//			{				
//				jsObj = new JSONObject();
//				jsObj.put("Amount", totalAmount);
//				jsObj.put("Count", txnList.size());
//				
//			}
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return jsObj;
//	}
//
//	public Map getAllServiceCodeByDate(Timestamp timestamp) {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	
	
//	public Collection<Transaction> getServiceCodeByCode(String uniqueServiceCode) {
//		// TODO Auto-generated method stub
//		Collection<Transaction> txnList = null;
//		long mId = 0;
//		Double totalAmount = 0.00;
//		try {
//			String hql = "Select t from ServiceCode t where " +
//					"t.code = '" + uniqueServiceCode + "'";
//			log.info("hql==" + hql);
//			txnList = swpService.getAllRecordsByHQL(hql);
//			
//			
//		} catch (HibernateException e) {
//			log.error("",e);
//		} catch (Exception e) {
//			log.error("",e);
//		} finally {
//			
//		}
//		return txnList; 
//	}
}
