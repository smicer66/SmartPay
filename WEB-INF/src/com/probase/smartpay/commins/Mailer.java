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


public class Mailer
{
	static String userDir = System.getProperty("user.dir");
	
	public static String APPROVAL_REQUEST = "/mailtemplates/approvalrequest.stl";
	public static String PAYMENT_EMAIL_TEMPLATE = "/mailtemplates/emailtemplatespayment.stl";
	public static String NEW_CORPORATE_INDIVIDUAL_EMAIL_TEMPLATE = "/mailtemplates/newcorporateindividual.stl";
	public static String NEW_CORPORATE_FIRM_EMAIL_TEMPLATE = "/mailtemplates/newcorporatefirm.stl";
	public static String NEW_BANK_STAFF_PROFILE_EMAIL_TEMPLATE = "/mailtemplates/newbankstaffprofile.stl";
	public static String USER_ACCOUNT_STATUS_UPDATE = "/mailtemplates/useraccountstatusupdate.stl";
	public static String UPDATE_USER_PROFILE_EMAIL_TEMPLATE = "/mailtemplates/updatebankstaffprofile.stl";
	public static String ADD_WORKDONE_EMAIL_TEMPLATE = "/mailtemplates/workdonetemplatespayment.stl";
	public static String DISAPPROVE_REQUEST_FOR_APPROVAL = "/mailtemplates/disapprovalrequest.stl";
	
	
//	String FROM_EMAIL = "etax@probasegroup.com"; 
//	String FROM_PASSWORD = "liverppolmlp-MLP_"; 
	private String FROM_EMAIL = "etax@stanbic.com"; 
	private String FROM_PASSWORD = "St@nbic1";

	private int PORT = 465; //465;25
	private String SENDER_USERNAME = "etax@probasegroup.com";
	SwpService sservice = ServiceLocator.getInstance().getSwpService();

	Logger log = Logger.getLogger(Mailer.class);

	public Mailer(String email, String password, int port, String username)
	{
		this.FROM_EMAIL = email;
		this.FROM_PASSWORD = password;
		this.PORT = port;
		this.SENDER_USERNAME = username;
		
	}
	
	
	
	public SendMail emailDisapproval(String toEmail,
			String firstName, String lastName, String reason, String url, String subject, String applicationName) {
		// TODO Auto-generated method stub
			System.out.println("in send payment email");
			SendMail sendEmail = null;
			String body = "";
			try
			{
				log.info("URL = " + DISAPPROVE_REQUEST_FOR_APPROVAL);
				List<String> lines = IOUtils.readLines(Mailer.class
						.getResourceAsStream(DISAPPROVE_REQUEST_FOR_APPROVAL));
				StringBuffer buffer = new StringBuffer();
				for (String line : lines)
				{
					buffer.append(line).append("\n");
				}
				
				String returnStr = "";
				String str="";
				
				
				Map<String, String> params = new HashMap<String, String>();
				params.put("firstname", firstName);
				params.put("lastname", lastName);
				params.put("details", reason);
				params.put("systemUrl", url);
				StringTemplate template = new StringTemplate(buffer.toString());

				template.setAttributes(params);
				body = template.toString();

				sendEmail = new SendMail(FROM_EMAIL, toEmail, FROM_PASSWORD,
						subject, body, PORT, SENDER_USERNAME, applicationName);
			} catch (Exception ex)
			{
				ex.printStackTrace();
				log.error("", ex);
			}
			return sendEmail;
	}
	
	
	public SendMail emailWorkFlow(String toEmail, String companyName,
			List<String> registrationNumber, String token, String url,
			String firstName, String lastName, String subject, String applicationName) {
		// TODO Auto-generated method stub
		System.out.println("in send payment email");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + ADD_WORKDONE_EMAIL_TEMPLATE);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(ADD_WORKDONE_EMAIL_TEMPLATE));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			String returnStr = "";
			String str="";
			for(Iterator<String> st = registrationNumber.iterator(); st.hasNext();)
			{
				str = str + st.next() + ", ";
			}
			returnStr += "<div><strong>Access Token:</strong>" + token + "</div>";
			returnStr += "<div><strong>Assessment(s):</strong>" + str.substring(0, str.length()-2) + "</div>";
			
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("companyName", companyName);
			params.put("firstname", firstName);
			params.put("lastname", lastName);
			params.put("paymentdetails", returnStr);
			params.put("token", token);
			params.put("systemUrl", url);
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, toEmail, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, applicationName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}
	
	
	
	public SendMail emailApprovalRequest(String firstName, String lastName, String emailAddress,
			String systemUrl, String subject, String applicationName) {
		// TODO Auto-generated method stub
		System.out.println("in send new bank staff individual email");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + APPROVAL_REQUEST);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(APPROVAL_REQUEST));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("firstname", firstName);
			params.put("lastname", lastName);
			params.put("username", emailAddress);
			params.put("systemUrl", systemUrl);
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, emailAddress, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, applicationName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}
	
	
	public SendMail emailNewBankStaffAccount(String emailAddress,
			String companyName, String password1, String systemUrl,
			String firstName, String lastName, String userclass, String subject, String applicationName) {
		// TODO Auto-generated method stub
		System.out.println("in send new bank staff individual email");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + NEW_BANK_STAFF_PROFILE_EMAIL_TEMPLATE);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(NEW_BANK_STAFF_PROFILE_EMAIL_TEMPLATE));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("firstname", firstName);
			params.put("lastname", lastName);
			params.put("userclass", userclass);
			params.put("username", emailAddress);
			params.put("password", password1);
			params.put("systemUrl", systemUrl);
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, emailAddress, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, applicationName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}
	
	
	public SendMail emailChangeOfAccountStatus(String emailAddress,
			String systemUrl, String firstName, String lastName, String subject, String msg, String applicationName) {
		// TODO Auto-generated method stub
		System.out.println("in send new corporate individual email");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + USER_ACCOUNT_STATUS_UPDATE);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(USER_ACCOUNT_STATUS_UPDATE));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("firstname", firstName);
			params.put("lastname", lastName);
			params.put("systemUrl", systemUrl);
			params.put("message", msg);
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, emailAddress, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, applicationName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}
	
	
	public SendMail emailUpdateUserProfileAccount(String emailAddress, 
			String firstname, String surname, String subject, String systemUrl, String applicationName) {
		// TODO Auto-generated method stub
		System.out.println("in send update corporate staff email");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + UPDATE_USER_PROFILE_EMAIL_TEMPLATE);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(UPDATE_USER_PROFILE_EMAIL_TEMPLATE));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("firstname", firstname);
			params.put("lastname", surname);
			params.put("username", emailAddress);
			params.put("systemUrl", systemUrl);
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, emailAddress, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, applicationName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}
	
	
	public SendMail emailNewCorporateIndividualAccount(String emailAddress,
			String companyName, String password1, String systemUrl,
			String firstName, String lastName, String userclass, String subject, String applicationName) {
		// TODO Auto-generated method stub
		System.out.println("in send new corporate individual email");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + NEW_CORPORATE_INDIVIDUAL_EMAIL_TEMPLATE);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(NEW_CORPORATE_INDIVIDUAL_EMAIL_TEMPLATE));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("companyName", companyName);
			params.put("firstname", firstName);
			params.put("lastname", lastName);
			params.put("userclass", userclass);
			params.put("username", emailAddress);
			params.put("password", password1);
			params.put("systemUrl", systemUrl);
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, emailAddress, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, applicationName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}
	
	
	public SendMail emailPayment(String toEmail, String companyName, String assessmentRegNo, 
			HashMap<String, Double> paymentdetails, String accountNumber, 
			String firstname, String lastname, String subject, String bankName, String applicationName)
	{
		
		System.out.println("in send payment email");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + PAYMENT_EMAIL_TEMPLATE);
			List<String> lines = IOUtils.readLines(Mailer.class.getResourceAsStream(PAYMENT_EMAIL_TEMPLATE));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Set<String> keys = paymentdetails.keySet();
			String key  = keys.iterator().next();
			String returnStr = "";
			
			returnStr += "<div>Assessment Registration Number:" + key + "</div>";
			returnStr += "<table style='width: 100%; border: 1px #ffffff solid;'>";
			returnStr += "<tr><td style='background-color: #003775; font-weight:bold'>Payment Item</td>" +
					"<td style='background-color: #003775; font-weight:bold'>Amount(ZMW)</td></tr>";
			Set<String> keys1 = paymentdetails.keySet();
			for(Iterator<String> iter1 = keys1.iterator(); iter1.hasNext();)
			{
				String key1 = iter1.next();
				returnStr += "<tr><td>";
				returnStr += key1;
				returnStr += "</td><td>";
				returnStr += "ZMW" + paymentdetails.get(key1);
				returnStr += "</td><tr>";
			}
			returnStr += "</table>";
				
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("companyName", companyName);
			params.put("firstname", firstname);
			params.put("lastname", lastname);
			params.put("paymentdetails", returnStr);
			params.put("accountNumber", accountNumber);
			params.put("successful", "successful");
			params.put("bank", bankName);
			params.put("key",key);
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			
			sendEmail = new SendMail(FROM_EMAIL, toEmail, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, applicationName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}
	
	
	
	
	public SendMail emailNewCorporateCompany(String companyEmail, 
			String companyName,
			String systemUrl, 
			String accountType,
			String tpin,
			String accountNumber,
			String bankBranches,
			String mobileNumber,
			String subject, String selectedCompanyClass, String applicationName) {
		// TODO Auto-generated method stub
		System.out.println("in send new corporate individual email");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + NEW_CORPORATE_FIRM_EMAIL_TEMPLATE);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(NEW_CORPORATE_FIRM_EMAIL_TEMPLATE));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("companyName", companyName);
			params.put("accountType", accountType);
			params.put("accountClass", selectedCompanyClass);
			params.put("tpin", tpin);
			params.put("bankAccount", accountNumber);
			params.put("systemUrl", systemUrl);
			params.put("bankBranch", accountType);
			params.put("mobileNumber", mobileNumber);
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, companyEmail, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, applicationName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}

	
	
}

