package com.probase.smartpay.commins;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.probase.smartpay.admin.corporateindividualmanagement.CorporateIndividualManagementPortletState;
import com.probase.smartpay.admin.mandatepanelmanagement.MandatePanelPortletState;
import com.sf.primepay.smartpay13.ServiceLocator;

import smartpay.entity.PortalUser;
import smartpay.entity.Settings;
import smartpay.service.SwpService;


public class Emailer
{
	static String userDir = System.getProperty("user.dir");
	//userDir + File.separator + "emailtemplates" + File.separator + 
	public static String NEW_BANK_STAFF_PROFILE_EMAIL_TEMPLATE = "/emailtemplates/newbankstaffprofile.stl";
	public static String PAYMENT_EMAIL_TEMPLATE = "/emailtemplates/emailtemplatespayment.stl";
	public static String ADD_WORKDONE_EMAIL_TEMPLATE = "/emailtemplates/workdonetemplatespayment.stl";
	public static String NEW_CORPORATE_INDIVIDUAL_EMAIL_TEMPLATE = "/emailtemplates/newcorporateindividual.stl";
	public static String NEW_CORPORATE_FIRM_EMAIL_TEMPLATE = "/emailtemplates/newcorporatefirm.stl";
	public static String NEW_CORPORATE_STAFF_EMAIL_TEMPLATE = "/emailtemplates/newcorporatestaff.stl";
	public static String USER_ACCOUNT_STATUS_UPDATE = "/emailtemplates/useraccountstatusupdate.stl";
	public static final String DEFAULT_SENDER_NAME = "Stanbic IBTC eTax Unit";
	public static String UPDATE_USER_PROFILE_EMAIL_TEMPLATE = "/emailtemplates/updatebankstaffprofile.stl";
	public static String EMAIL_SIGNATURE = "<br><br><br><span style=\"font-size:9px\">This e-mail and any file attachments transmitted with it are intended solely for the addressee(s) and may be legally privileged and/or confidential. If you have received this e-mail in error please destroy it. If you are not the addressee you may not disclose, copy, distribute or take any action based on the contents hereof. Any unauthorized use or disclosure is  prohibited and may be unlawful.</span>";
	
	String FROM_EMAIL = "admin@stanbicibtc.com.zm"; 
	String FROM_PASSWORD = "*admin@123#"; 

	int PORT = 25; //465;
	String SENDER_USERNAME = "admin@stanbicibtc.com.zm";
	SwpService sservice = ServiceLocator.getInstance().getSwpService();

	Logger log = Logger.getLogger(Emailer.class);

	public Emailer()
	{
//		Settings sender = getSettingByName("SENDER_EMAIL");
//		if (sender != null)
//		{
//			this.FROM_EMAIL = sender.getValue();
//		}
//
//		Settings password = getSettingByName("SENDER_PASSWORD");
//		if (password != null)
//		{
//			this.FROM_PASSWORD = password.getValue();
//		}
//
//		Settings portSetting = getSettingByName("PORT");
//		if (portSetting != null)
//		{
//			this.PORT = Integer.valueOf(portSetting.getValue().trim());
//		}
//
//		Settings senderuserName = getSettingByName("SENDER_USERNAME");
//		if (senderuserName != null)
//		{
//			this.SENDER_USERNAME = senderuserName.getValue();
//		}
	}

//	public SendMail emailPayment1(String toEmail, String companyName, String assessmentRegNo, 
//			HashMap<String, Double> paymentdetails, String accountNumber, 
//			String firstname, String lastname, String subject, String bankName)
//	{
//		
//		System.out.println("in send payment email");
//		SendMail sendEmail = null;
//		String body = "";
//		try
//		{
//			List<String> lines = IOUtils.readLines(Emailer.class.getResourceAsStream(PAYMENT_EMAIL_TEMPLATE));
//			StringBuffer buffer = new StringBuffer();
//			for (String line : lines)
//			{
//				buffer.append(line).append("\n");
//			}
//			
//			Set<String> keys = paymentdetails.keySet();
//			String key  = keys.iterator().next();
//			String returnStr = "";
//			
//			returnStr += "<div>Assessment Registration Number:" + key + "</div>";
//			returnStr += "<table style='width: 100%; border: 1px #ffffff solid;'>";
//			returnStr += "<tr><td style='background-color: #003775; font-weight:bold'>Payment Item</td>" +
//					"<td style='background-color: #003775; font-weight:bold'>Amount(ZMW)</td></tr>";
//			Set<String> keys1 = paymentdetails.keySet();
//			for(Iterator<String> iter1 = keys1.iterator(); iter1.hasNext();)
//			{
//				String key1 = iter1.next();
//				returnStr += "<tr><td>";
//				returnStr += key1;
//				returnStr += "</td><td>";
//				returnStr += "ZMW" + paymentdetails.get(key1);
//				returnStr += "</td><tr>";
//			}
//			returnStr += "</table>";
//				
//			
//			Map<String, String> params = new HashMap<String, String>();
//			params.put("companyName", companyName);
//			params.put("firstname", firstname);
//			params.put("lastname", lastname);
//			params.put("paymentdetails", returnStr);
//			params.put("accountNumber", accountNumber);
//			params.put("successful", "successful");
//			params.put("bank", bankName);
//			params.put("key",key);
//			StringTemplate template = new StringTemplate(buffer.toString());
//
//			template.setAttributes(params);
//			body = template.toString();
//
//			sendEmail = new SendMail(FROM_EMAIL, toEmail, FROM_PASSWORD,
//					subject, body, PORT, SENDER_USERNAME, "Stanbic IBTC e-Tax Unit");
//		} catch (Exception ex)
//		{
//			ex.printStackTrace();
//			log.error("", ex);
//		}
//		return sendEmail;
//	}
//	
//	public Settings getSettingByName(String name)
//	{
//		String query = "select s from Settings s where lower(s.name) = lower('"
//				+ name + "')";
//		return (Settings) sservice.getUniqueRecordByHQL(query);
//	}
//
//	public SendMail emailWorkFlow1(String toEmail, String companyName,
//			List<String> registrationNumber, String token, String url,
//			String firstName, String lastName, String subject) {
//		// TODO Auto-generated method stub
//		System.out.println("in send payment email");
//		SendMail sendEmail = null;
//		String body = "";
//		try
//		{
//			List<String> lines = IOUtils.readLines(Emailer.class
//					.getResourceAsStream(ADD_WORKDONE_EMAIL_TEMPLATE));
//			StringBuffer buffer = new StringBuffer();
//			for (String line : lines)
//			{
//				buffer.append(line).append("\n");
//			}
//			
//			String returnStr = "";
//			String str="";
//			for(Iterator<String> st = registrationNumber.iterator(); st.hasNext();)
//			{
//				str = str + st.next() + ", ";
//			}
//			returnStr += "<div><strong>Access Token:</strong>" + token + "</div>";
//			returnStr += "<div><strong>Assessment(s):</strong>" + str.substring(0, str.length()-2) + "</div>";
//			
//			
//			Map<String, String> params = new HashMap<String, String>();
//			params.put("companyName", companyName);
//			params.put("firstname", firstName);
//			params.put("lastname", lastName);
//			params.put("paymentdetails", returnStr);
//			params.put("token", token);
//			params.put("systemUrl", url);
//			StringTemplate template = new StringTemplate(buffer.toString());
//
//			template.setAttributes(params);
//			body = template.toString();
//
//			sendEmail = new SendMail(FROM_EMAIL, toEmail, FROM_PASSWORD,
//					subject, body, PORT, SENDER_USERNAME, "Stanbic IBTC e-Tax Unit");
//		} catch (Exception ex)
//		{
//			ex.printStackTrace();
//			log.error("", ex);
//		}
//		return sendEmail;
//	}
//
//	public SendMail emailNewCorporateIndividualAccount1(String emailAddress,
//			String companyName, String password1, String systemUrl,
//			String firstName, String lastName, String userclass, String subject) {
//		// TODO Auto-generated method stub
//		System.out.println("in send new corporate individual email");
//		SendMail sendEmail = null;
//		String body = "";
//		try
//		{
//			List<String> lines = IOUtils.readLines(Emailer.class
//					.getResourceAsStream(NEW_CORPORATE_INDIVIDUAL_EMAIL_TEMPLATE));
//			StringBuffer buffer = new StringBuffer();
//			for (String line : lines)
//			{
//				buffer.append(line).append("\n");
//			}
//			
//			Map<String, String> params = new HashMap<String, String>();
//			params.put("companyName", companyName);
//			params.put("firstname", firstName);
//			params.put("lastname", lastName);
//			params.put("userclass", userclass);
//			params.put("username", emailAddress);
//			params.put("password", password1);
//			params.put("systemUrl", systemUrl);
//			StringTemplate template = new StringTemplate(buffer.toString());
//
//			template.setAttributes(params);
//			body = template.toString();
//
//			sendEmail = new SendMail(FROM_EMAIL, emailAddress, FROM_PASSWORD,
//					subject, body, PORT, SENDER_USERNAME, "Stanbic IBTC e-Tax Unit");
//		} catch (Exception ex)
//		{
//			ex.printStackTrace();
//			log.error("", ex);
//		}
//		return sendEmail;
//	}
//	
//	
//	public SendMail emailNewBankStaffAccount(String emailAddress,
//			String companyName, String password1, String systemUrl,
//			String firstName, String lastName, String userclass, String subject) {
//		// TODO Auto-generated method stub
//		System.out.println("in send new bank staff individual email");
//		SendMail sendEmail = null;
//		String body = "";
//		try
//		{
//			List<String> lines = IOUtils.readLines(Emailer.class
//					.getResourceAsStream(NEW_BANK_STAFF_PROFILE_EMAIL_TEMPLATE));
//			StringBuffer buffer = new StringBuffer();
//			for (String line : lines)
//			{
//				buffer.append(line).append("\n");
//			}
//			
//			Map<String, String> params = new HashMap<String, String>();
//			params.put("firstname", firstName);
//			params.put("lastname", lastName);
//			params.put("userclass", userclass);
//			params.put("username", emailAddress);
//			params.put("password", password1);
//			params.put("systemUrl", systemUrl);
//			StringTemplate template = new StringTemplate(buffer.toString());
//
//			template.setAttributes(params);
//			body = template.toString();
//
//			sendEmail = new SendMail(FROM_EMAIL, emailAddress, FROM_PASSWORD,
//					subject, body, PORT, SENDER_USERNAME, "Stanbic IBTC e-Tax Unit");
//		} catch (Exception ex)
//		{
//			ex.printStackTrace();
//			log.error("", ex);
//		}
//		return sendEmail;
//	}
//
//	public SendMail emailNewCorporateCompany1(String companyEmail, 
//			String companyName,
//			String systemUrl, 
//			String accountType,
//			String tpin,
//			String accountNumber,
//			String bankBranches,
//			String mobileNumber,
//			String subject, String selectedCompanyClass) {
//		// TODO Auto-generated method stub
//		System.out.println("in send new corporate individual email");
//		SendMail sendEmail = null;
//		String body = "";
//		try
//		{
//			List<String> lines = IOUtils.readLines(Emailer.class
//					.getResourceAsStream(NEW_CORPORATE_FIRM_EMAIL_TEMPLATE));
//			StringBuffer buffer = new StringBuffer();
//			for (String line : lines)
//			{
//				buffer.append(line).append("\n");
//			}
//			
//			Map<String, String> params = new HashMap<String, String>();
//			params.put("companyName", companyName);
//			params.put("accountType", accountType);
//			params.put("accountClass", selectedCompanyClass);
//			params.put("tpin", tpin);
//			params.put("bankAccount", accountNumber);
//			params.put("systemUrl", systemUrl);
//			params.put("bankBranch", accountType);
//			params.put("mobileNumber", mobileNumber);
//			StringTemplate template = new StringTemplate(buffer.toString());
//
//			template.setAttributes(params);
//			body = template.toString();
//
//			sendEmail = new SendMail(FROM_EMAIL, companyEmail, FROM_PASSWORD,
//					subject, body, PORT, SENDER_USERNAME, "Stanbic IBTC e-Tax Unit");
//		} catch (Exception ex)
//		{
//			ex.printStackTrace();
//			log.error("", ex);
//		}
//		return sendEmail;
//	}
//	
//	
//	public SendMail emailNewCorporateStaffAccount1(String emailAddress,
//			String companyName, String password1, String systemUrl,
//			String firstName, String lastName, String subject) {
//		// TODO Auto-generated method stub
//		System.out.println("in send new corporate staff email");
//		SendMail sendEmail = null;
//		String body = "";
//		try
//		{
//			List<String> lines = IOUtils.readLines(Emailer.class
//					.getResourceAsStream(NEW_CORPORATE_STAFF_EMAIL_TEMPLATE));
//			StringBuffer buffer = new StringBuffer();
//			for (String line : lines)
//			{
//				buffer.append(line).append("\n");
//			}
//			
//			Map<String, String> params = new HashMap<String, String>();
//			params.put("companyName", companyName);
//			params.put("firstname", firstName);
//			params.put("lastname", lastName);
//			params.put("username", emailAddress);
//			params.put("password", password1);
//			params.put("systemUrl", systemUrl);
//			StringTemplate template = new StringTemplate(buffer.toString());
//
//			template.setAttributes(params);
//			body = template.toString();
//
//			sendEmail = new SendMail(FROM_EMAIL, emailAddress, FROM_PASSWORD,
//					subject, body, PORT, SENDER_USERNAME, "Stanbic IBTC e-Tax Unit");
//		} catch (Exception ex)
//		{
//			ex.printStackTrace();
//			log.error("", ex);
//		}
//		return sendEmail;
//	}
//
//	public SendMail emailChangeOfAccountStatus1(String emailAddress,
//			String systemUrl, String firstName, String lastName, String subject, String msg) {
//		// TODO Auto-generated method stub
//		System.out.println("in send new corporate individual email");
//		SendMail sendEmail = null;
//		String body = "";
//		try
//		{
//			List<String> lines = IOUtils.readLines(Emailer.class
//					.getResourceAsStream(USER_ACCOUNT_STATUS_UPDATE));
//			StringBuffer buffer = new StringBuffer();
//			for (String line : lines)
//			{
//				buffer.append(line).append("\n");
//			}
//			
//			Map<String, String> params = new HashMap<String, String>();
//			params.put("firstname", firstName);
//			params.put("lastname", lastName);
//			params.put("systemUrl", systemUrl);
//			params.put("message", msg);
//			StringTemplate template = new StringTemplate(buffer.toString());
//
//			template.setAttributes(params);
//			body = template.toString();
//
//			sendEmail = new SendMail(FROM_EMAIL, emailAddress, FROM_PASSWORD,
//					subject, body, PORT, SENDER_USERNAME, "Stanbic IBTC e-Tax Unit");
//		} catch (Exception ex)
//		{
//			ex.printStackTrace();
//			log.error("", ex);
//		}
//		return sendEmail;
//	}
//
//	public void emailNewBankStaffProfileRequireApproval1(String emailAddress,
//			String string, String firstName, String lastName, String string2) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void emailBankStaffUpdateProfileRequireApproval1(String emailAddress,
//			String string, String firstName, String lastName, String string2) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void emailDisapprovalSettingsUpdate1(String emailAddress,
//			String firstName, String lastName, String firstName2,
//			String lastName2, String reason) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void emailDisapprovalFeeDescriptionUpdate1(String emailAddress,
//			String firstName, String lastName, String firstName2,
//			String lastName2, String reason) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void emailDisapprovalAuthorizePanelUpdate1(String emailAddress,
//			String firstName, String lastName, String firstName2,
//			String lastName2, String reason) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void emailDisapprovalCompanyUpdate1(String emailAddress,
//			String firstName, String lastName, String firstName2,
//			String lastName2, String reason) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void emailDisapprovalPortalUserUpdate1(String emailAddress,
//			String firstName, String lastName, String firstName2,
//			String lastName2, String reason) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void emailDeletePortalUser1(String firstName2, String lastName2, String puActedOnEmail, String systemUrl) {
//		// TODO Auto-generated method stub
//		emailChangeOfAccountStatus(puActedOnEmail,
//				systemUrl, firstName2, lastName2, "Approval of deletion of your eTax profile account", 
//				"Your eTax Profile account has been deleted. You will no longer be able to access your account on " + 
//				systemUrl);
//	}
//
//	public void emailBlockPortalUser1(String firstName2, String lastName2, String puActedOnEmail, String systemUrl) {
//		// TODO Auto-generated method stub
//		emailChangeOfAccountStatus(puActedOnEmail,
//				systemUrl, firstName2, lastName2, "Approval of the Blocking of your eTax profile account", 
//				"Your eTax Profile account has been blocked. You cnanot access your eTax profile account until " +
//				"it is unblocked");
//	}
//
//	public void emailNewCompanyRequestForApproval1(String companyname,
//			String string) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void emailDeletePortalUserRequest1(PortalUser pu,
//			CorporateIndividualManagementPortletState portletState) {
//		// TODO Auto-generated method stub
//		emailChangeOfAccountStatus(puActedOnEmail,
//				systemUrl, firstName2, lastName2, "Approval of the Blocking of your eTax profile account", 
//				"Your eTax Profile account has been blocked. You cnanot access your eTax profile account until " +
//				"it is unblocked");
//	}
//
//	public void emailRemoveUserFromPanel1(MandatePanelPortletState portletState) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public SendMail emailUpdateUserProfileAccount(String emailAddress, String value,
//			String firstname, String surname, String subject, String systemUrl) {
//		// TODO Auto-generated method stub
//		System.out.println("in send update corporate staff email");
//		SendMail sendEmail = null;
//		String body = "";
//		try
//		{
//			List<String> lines = IOUtils.readLines(Emailer.class
//					.getResourceAsStream(UPDATE_USER_PROFILE_EMAIL_TEMPLATE));
//			StringBuffer buffer = new StringBuffer();
//			for (String line : lines)
//			{
//				buffer.append(line).append("\n");
//			}
//			
//			Map<String, String> params = new HashMap<String, String>();
//			params.put("firstname", firstname);
//			params.put("lastname", surname);
//			params.put("username", emailAddress);
//			params.put("systemUrl", systemUrl);
//			StringTemplate template = new StringTemplate(buffer.toString());
//
//			template.setAttributes(params);
//			body = template.toString();
//
//			sendEmail = new SendMail(FROM_EMAIL, emailAddress, FROM_PASSWORD,
//					subject, body, PORT, SENDER_USERNAME, "Stanbic IBTC e-Tax Unit");
//		} catch (Exception ex)
//		{
//			ex.printStackTrace();
//			log.error("", ex);
//		}
//		return sendEmail;
//	}
	
}

