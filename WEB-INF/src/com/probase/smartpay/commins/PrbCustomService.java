package com.probase.smartpay.commins;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

import smartpay.entity.PaymentBreakDownHistory;
import smartpay.entity.PortalUser;
import smartpay.entity.Settings;
import smartpay.entity.TaxType;
import smartpay.entity.TaxTypeAccount;
import smartpay.entity.enumerations.PaymentBreakDownHistoryConstants;
import smartpay.entity.enumerations.PaymentHistoryConstants;

import com.liferay.portal.model.Role;




public class PrbCustomService extends AbstractPrbCustomService{

  private static Logger log = Logger.getLogger(PrbCustomService.class);

  public static PrbCustomService getInstance()
  {
    return new PrbCustomService();
  }

  public PortalUser getPortalUserByOrbitaId(String orbitaId) {
    //String query = "select pu from PortalUser pu where pu.userId=?";
	String query = "select pu from PortalUser pu where pu.userId= '" + orbitaId + "'"; 
	log.info(query);
    List paramsList = new ArrayList();
    return (PortalUser)executeQueryUniqueResult(query, paramsList);
  }

  public List<Role> getPortalUserRoleType(long userOrbitaId) {
    String id = String.valueOf(userOrbitaId);
    log.debug("User Orbita ID: " + id);
    String query = "select pur.role from PortalUserRole pur where pur.portalUser.userId=?";
    List params = new ArrayList();
    params.add(id);

    return executeQuery(query, id);
  }
  
  
  public List<Settings> getValidSettings() {
	    String query = "Select st FROM Settings st where st.status = 'true'";
	    return executeQuery(query);
  }

	public List<TaxType> getTaxTypes() {
		// TODO Auto-generated method stub
		String query = "Select st FROM TaxType st";
	    return executeQuery(query);
	}

	public List<PaymentBreakDownHistory> getPaymentBreakDownHistory(
			String value, String value2) {
		// TODO Auto-generated method stub
//		String query = "Select pbdh FROM PaymentBreakDownHistory pbdh " +
//				" WHERE pbdh.id not in (Select pth.paymentBreakDownHistory.id FROM " +
//				"PaymentTempHolder pth) AND lower(pbdh.status) = lower('" + 
//				value + "') AND lower(pbdh.paymentHistory.status) = " +
//				" lower('" + value2 + "')  ORDER by pbdh.paymentHistory.entryDate ASC";
		String query = "Select pbdh FROM PaymentBreakDownHistory pbdh " +
				" WHERE lower(pbdh.status) = lower('" + 
				value + "') AND lower(pbdh.paymentHistory.status) = " +
				" lower('" + value2 + "')  ORDER by pbdh.paymentHistory.entryDate ASC";
		log.info("Query = " + query);
		return executeQuery(query);
	}

	public TaxTypeAccount getTaxTypeAccountByTaxCode(String taxCode) {
		// TODO Auto-generated method stub
		String query = "Select tta From TaxTypeAccount tta where lower(tta.taxType.taxCode) = " +
				"lower('" + taxCode + "')";
		log.info("Query = " + query);
		List paramsList = new ArrayList();
		return (TaxTypeAccount)executeQueryUniqueResult(query, paramsList);
	}

	public TaxTypeAccount getTaxTypeAccountByTaxTypeId(Long id) {
		// TODO Auto-generated method stub
		String query = "Select st FROM TaxTypeAccount st where st.taxType.id = " + id;
		List paramsList = new ArrayList();
		return (TaxTypeAccount)executeQueryUniqueResult(query, paramsList);
		
	}
  
  
  
  

  /*public Set<RoleType> getRoles(Long userOrbitaId) {
    log.debug("User Orbita Id is: " + userOrbitaId);
    List<Role> roles = getPortalUserRoleType(userOrbitaId.longValue());
    Set roleTypes = new HashSet();

    if ((roles != null) && (!roles.isEmpty())) {
      for (Role role : roles) {
        roleTypes.add(role.get);
      }
    }
    return roleTypes;
  }*/

  
}