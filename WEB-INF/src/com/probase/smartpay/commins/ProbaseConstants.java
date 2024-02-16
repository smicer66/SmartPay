package com.probase.smartpay.commins;

public class ProbaseConstants {

	//user types
	public static final String PORTAL_ADMIN = "Portal Admin";
	public static final String PORTAL_USER = "Portal User";
	
	//orbita things
	public static final Long COMPANY_ID = 10154L;
	public static final Long SUPERADMIN_ID = 10196L;
	public static final Long DEFAULT_USER_COMMUNITY_ID = 15182L;
	public static final Long ORBITA_ADMIN_ROLE_ID = 10161L;//
	public static final Long ORBITA_USER_ROLE_ID = 10165L;//
	public static final Long ORBITA_USER_GROUP_ID = 14204L;
	public static final String PORTAL_USER_COMMUNITY_ID = "PORTAL_USER_COMMUNITY_ID";
	public static final String DEFAULT_SITE = "DEFAULT_SITE";
	public static final Long DEFAULT_SITE_ID = 100L;
	public static final Long SUPERVISOR_SITE =65801L;
	
	
	
	
//	public static final Long BANK_SUPER_ADMIN_COMMUNITY_ID = 11009L;
	public static final Long BANK_SUPER_ADMIN_COMMUNITY_ID = 11138L;
	public static final Long BANK_ADMIN_COMMUNITY_ID = 11629L;
	public static final Long BANK_TELLER_COMMUNITY_ID = 11525L;
	public static final Long BANK_HEAD_OF_OPERATIONS_COMMUNITY_ID = 11027L;
	public static final Long BANK_HQ_OPERATIONS_ASSISTANT_COMMUNITY_ID = 11499L;
	public static final Long CORPORATE_FIRM_ADMINISTRATOR_COMMUNITY_ID = 11551L;
	public static final Long CORPORATE_STAFF_COMMUNITY_ID = 11577L;
	public static final Long RETAIL_STAFF_COMMUNITY_ID = 11603L;
	public static final Long SYSTEM_SUPER_ADMIN_COMMUNITY_ID = 11422L;
			
	//audit trail
	
	//email things
	public static final String DEFAULT_PASSWORD = "password";
	public static final String DOMAIN_EMAIL_SUFFIX = "@nigerianportal.com";
	public static final String DEFAULT_SENDER_EMAIL = "noreply@seamfix.com";
	public static final String DEFAULT_SENDER_PASSWORD = "n0reply";
	
	//
	public static final String SENDER_EMAIL = "SENDER_EMAIL";
	public static final String SENDER_PASSWORD = "SENDER_PASSWORD";
	
	public static final String SEND_SMS = "SEND_SMS";
	
	//
	public static final String PORTAL_URL = "PORTAL_URL";
	public static final int EXCHANGE_RATE = 1;
	public static final String CURRENCY = "ZMW";
	
	
	public static enum CORE_VIEW
	{
		BANK_STAFF_MGMT, BANK_BRANCH_MGMT, TAX_FEES_MGMT, PORT_MGMT, PORTAL_SETTINGS, 
		FIN_AMT_REST_MGMT, AUTH_MANDATE_MGMT, CORPORATE_FIRMS_MGMT, CORPORATE_STAFF_MGMT, 
		INDIVIDUAL_MANAGEMENT
	}
}
