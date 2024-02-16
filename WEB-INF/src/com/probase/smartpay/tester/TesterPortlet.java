package com.probase.smartpay.tester;

import com.liferay.util.bridges.mvc.MVCPortlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.portlet.PortletFileUpload;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;

import smartpay.service.SwpService;


import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.DuplicateUserEmailAddressException;
import com.liferay.portal.DuplicateUserScreenNameException;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.servlet.SessionErrors;
//import com.bw.service.SwpService;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.ClassName;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Contact;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserGroup;
import com.liferay.portal.service.ClassNameLocalServiceUtil;
import com.liferay.portal.service.ContactLocalServiceUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserGroupLocalServiceUtil;
import com.liferay.portal.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.sf.primepay.smartpay13.ServiceLocator;
import com.sf.util.randomcodegenerator.RandomCodeGenerator;

/**
 * Portlet implementation class TestPortlet
 */
public class TesterPortlet extends MVCPortlet {	
	Logger log = Logger.getLogger(TesterPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.probase.smartpay.commins.PrbCustomService swpCustomService = com.probase.smartpay.commins.PrbCustomService.getInstance();
	TesterPortletUtil util = TesterPortletUtil.getInstance();
	static String subject = "BytePay Merchant Sign Up - Welcome!";
	
	@Override
	public void init(PortletConfig config) throws PortletException {
		// TODO Auto-generated method stub
		log.info("MerchantManagement portlet init called...");		
		pContext = config.getPortletContext();
		super.init(config);
		pConfig = config;
	    this.swpService = this.serviceLocator.getSwpService();
	}
	
	@Override
	public void render(RenderRequest renderRequest,
			RenderResponse renderResponse) throws PortletException, IOException {
		log.info("ChangeOfName render called...");	
		PortletSession ps = renderRequest.getPortletSession();
		TesterPortletState portletState = 
				TesterPortletState.getInstance(renderRequest, renderResponse);

		log.info(">>>next page = " + renderRequest.getParameter("jspPage"));
		super.render(renderRequest, renderResponse);
	}

	@Override
	public void serveResource(ResourceRequest resourceRequest,
			ResourceResponse resourceResponse) throws IOException,
			PortletException {

		String resourceID = resourceRequest.getResourceID();
		if (resourceID == null || resourceID.equals(""))
			return;
	}
	
	
	
	public void deactivateMerchant(long id, TesterPortletState portletState, ActionRequest aReq)
	{
		
	}
	
	@Override
	public void processAction(ActionRequest aReq,
			ActionResponse aRes) throws IOException, PortletException {
		SessionMessages.add(aReq, pConfig.getPortletName()
				+ SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
		log.info("insideAnnual retuns step one");
		
		TesterPortletState portletState = TesterPortletState.getInstance(aReq, aRes);
		
		
		String action = aReq.getParameter("action");
		log.info("Company Selected b");
		
		
	}

	
	private void approveEntity(String entityname, Long valueOf,
			TesterPortletState portletState, ActionRequest aReq, SwpService swpService, String successMessage, String failureMessage) {
		// TODO Auto-generated method stub
//		ApprovalPersonnel ap = portletState.getTestPortletUtil().
//				getApprovalPersonnelListByPortalUser(entityname, portletState.getPortalUser().getId());
//		if(ap!=null)
//		{
//			Approval approval = new Approval();
//			approval.setEntityName(entityname);
//			approval.setEntityId(valueOf);
//			approval.setStatus(Boolean.TRUE);
//			approval.setApprovalPersonnel(ap);
//			approval.setDateApproved(new Timestamp((new Date()).getTime()));
//			approval = (Approval)swpService.createNewRecord(approval);
//			if (approval==null){
//				portletState.addSuccess(aReq, successMessage, portletState);
//			}
//			else{
//				portletState.addError(aReq, failureMessage, portletState);
//			}//SEND EMAIL TO NEXT PERSON ON LIST AFTER APPROVING AN ENTITY
//			
//			if(ap.getOutOf().equals(ap.getPosition()))
//			{
			
//			}
//		}
//		else
//		{
//			portletState.addError(aReq, "You are not allowed to carry out this operation", portletState);
//		}
	}

	public static boolean addUserToCommmunity(long userId, long communityId) {
        boolean status = false;
        try {
                long[] group = new long[1];
                group[0] = userId; // possible null if user is not

                Logger.getLogger(TesterPortlet.class).info("userId is " + userId + " community id is " + communityId);

                try { UserLocalServiceUtil.getUserById(userId);
                }
                catch (NoSuchUserException e){
                        System.out.println("NoSuchUserException");
                        return status;
                }

                if (!UserLocalServiceUtil.hasGroupUser(communityId, userId)) {
                        UserLocalServiceUtil.addGroupUsers(communityId, group);
                }
                status = UserLocalServiceUtil.hasGroupUser(communityId, userId);

        } catch (Exception e) { throw new HibernateException(e); }
        return status;
	}
	
	

	
	
	
	public User handleCreateUserOrbitaAccountOld(User user, String firstname,
			String middlename, String surname, String username, String email,
			String phoneNumber, long[] communities, ServiceContext
			serviceContext, SwpService sService, 
			boolean passwordReset, boolean active, boolean sendEmail, String
			password) throws PortalException, SystemException {
		User newlyCreated = null;

        User createdUser = user;
        long creatorUserId = 0;
        //System.out.println("communities "+communities.length);
        boolean alreadyInOrbita = Boolean.FALSE;
        User newlyCreatedUser = null;

        long companyId = 10157;

        //Group guestGroup = GroupLocalServiceUtil.getGroup(companyId,
                //      GroupConstants.GUEST);

        //Organization ngalabaOrganization = OrganizationLocalServiceUtil
                        //.getOrganization(companyId, "ngalaba mobile");

        String jobTitle = "";
        long organizationId = 0;
        long locationId = 0;
        long[] orgAndLocation = new long[2];
        orgAndLocation[0] = organizationId;
        orgAndLocation[1] = locationId;

        int prefixId = 0;
        int suffixId = 0;

        boolean male = true;
        boolean emailSend = false;

        int birthdayMonth = Calendar.JANUARY;
        int birthdayDay = 2;
        int birthdayYear = 1980;

        long facebookId = 0;
        String openId = StringPool.BLANK;;

        User aUser = null;


        boolean autoPassword = false;
        String password1 = password;
        String password2 = password;
        String emailSuffix = "@bytepay.com";

        boolean autoScreenName = false;
        String formattedUsername = "BYTEPAY_MERCHANT";

        String screenName = formattedUsername;
        String emailAddress = email;
        String firstName = firstname;
        String lastName = surname;
        String middleName = StringPool.BLANK;
        String username1 = username;

        System.out.println("Successfully set all parameters 1");

        long[] organizationIds = null;
        long[] roleIds = new long[2];
        long[] userGroupIds = new long[2];
        long[] groupIds = new long[2];

        //organizationIds[0] = 10134L;



        roleIds[0] = 10167;//User role
        roleIds[1] = 10168;

        userGroupIds[0] = 10602L;
        groupIds[0] = 10602L;


        boolean addGroupStatus = Boolean.FALSE;
        System.out.println("Successfully set all parameters 2");
        serviceContext = null;
        //UserLocalServiceUtil usc = new UserLocalServiceUtil();

        try {
                newlyCreatedUser = UserLocalServiceUtil.addUser(
                                creatorUserId, companyId, autoPassword,
                                password1, password2, autoScreenName,
                                username1, emailAddress, facebookId, openId,
                                Locale.US, firstName, middleName, lastName,
                                prefixId, suffixId, male, birthdayMonth,
                                birthdayDay, birthdayYear, jobTitle, groupIds,
                                organizationIds, roleIds, userGroupIds,
                                emailSend, serviceContext);

                //newlyCreatedUser = UserLocalServiceUtil.getUser(10196L);
                System.out.println("Creation succcessful..now adding to community!!!" + newlyCreatedUser);
                System.out.println("Creation succcessful..now adding to community!!! " + newlyCreatedUser.getUserId());
               // setNewlyCreatedUserId(newlyCreatedUser.getUserId());

                Date date = new Date();
                newlyCreatedUser.setLastLoginDate(date);
                newlyCreatedUser.setModifiedDate(date);
                newlyCreatedUser.setNew(false);
                newlyCreatedUser.setPasswordModified(true);
                newlyCreatedUser.setModifiedDate(date);
                newlyCreatedUser.setPasswordReset(false);
                newlyCreatedUser.setAgreedToTermsOfUse(true);
                newlyCreatedUser.setReminderQueryQuestion("what is your fathers middle name?");
                newlyCreatedUser.setReminderQueryAnswer(username);
                UserLocalServiceUtil.updateUser(newlyCreatedUser);
        }


//			              for(int i = 0; i < communities.length; i++){
//			                      addGroupStatus = addUserToCommmunity(newlyCreatedUser.getUserId(), communities[i] );
//			                      System.out.println("adding to communities: " + newlyCreatedUser.getUserId()
//			                                      + communities[i]);
//			                      }

//				}
//			              catch (DuplicateUserScreenNameException t) {
//			                      System.out.println("DuplicateScreenNameException");
//			                      return null ;
//			              }
        catch (Exception e) {
                System.out.println("exception: " + e);
                //throw new HibernateException(e);
        }

        System.out.println("Added succcessfully");

        return newlyCreated;
	}

	
	
	
	public boolean saveMerchantDetails(ActionRequest aReq, TesterPortletState portletState)
	{
		boolean proceed = false;
		String yourcompanyname = aReq.getParameter("yourcompanyname");
	    String companyrcnumber = aReq.getParameter("companyrcnumber");
	    String line1addressofcompany = aReq.getParameter("line1addressofcompany");
	    String line2addressofcompany = aReq.getParameter("line2addressofcompany");
	    String yourwebsitename = aReq.getParameter("yourwebsitename");
	    String yourwebsiteurl = aReq.getParameter("yourwebsiteurl");
	    String yourwebsiteipaddress = aReq.getParameter("yourwebsiteipaddress");
	    String yourfirstname = aReq.getParameter("yourfirstname");
	    String yourlastname = aReq.getParameter("yourlastname");
	    String youremailaddress = aReq.getParameter("youremailaddress");
	    String yourcontactphonenumber = aReq.getParameter("yourcontactphonenumber");
	    if(yourcompanyname!=null && yourcompanyname.length()>0)
	    {
	    	if(companyrcnumber!=null && companyrcnumber.length()>0)
	    	{
	    		if(line1addressofcompany!=null && line1addressofcompany.length()>0)
	    		{
	    			if(yourwebsitename!=null && yourwebsitename.length()>0)
	    			{
	    				if(yourwebsiteurl!=null && yourwebsiteurl.length()>0)
		    			{
	    					if(yourwebsiteipaddress!=null && yourwebsiteipaddress.length()>0)
			    			{
	    						if(yourfirstname!=null && yourfirstname.length()>0)
	    						{
	    							if(yourlastname!=null && yourlastname.length()>0)
		    						{
	    								if(youremailaddress!=null && youremailaddress.length()>0)
			    						{
	    									if(yourcontactphonenumber!=null && yourcontactphonenumber.length()>0)
				    						{
				    							proceed = true;
				    							
				    						}else
				    						{
				    							//yourcontactphonenumber
				    							portletState.addToErrorList("Provide your contact phone number");
				    						}
			    						}else
			    						{
			    							//youremailaddress
			    							portletState.addToErrorList("Provide your email address");
			    						}
		    						}else
		    						{
		    							//last name
		    							portletState.addToErrorList("Provide your last name");
		    						}
	    						}else
	    						{
	    							//first name
	    							portletState.addToErrorList("Provide your first name");
	    						}
			    			}else
			    			{
			    				//website ip address
			    				portletState.addToErrorList("Provide the website IP Addresss you want to implement BytePay on");
			    			}
		    			}else
		    			{
		    				//website url
		    				portletState.addToErrorList("Provide the website URL you want to implement BytePay on");
		    			}
	    			}else
	    			{
	    				//website name
	    				portletState.addToErrorList("Provide the website name you want to implement BytePay on");
	    			}
	    		}
	    		else
	    		{
	    			//line1 address not provided
	    			portletState.addToErrorList("Provide the 1st line of your company's address");
	    		}
	    	}
	    	else
	    	{
	    		//company rc number
	    		portletState.addToErrorList("Provide a valid company RC number");
	    	}
	    }
	    else
	    {
	    	//company name invalid
	    	portletState.addToErrorList("Provide a valid company name");
	    }
		return proceed;
	}
	
	
	
	public void uploadCase(ActionRequest actionRequest,
			 ActionResponse actionRresponse) throws PortletException,
			 IOException {
			 
		String folder = getInitParameter("uploadFolder");
		String realPath = getPortletContext().getRealPath("/");
		
		log.info("RealPath" + realPath + " UploadFolder :" + folder);
		try {
			log.info("Siamo nel try");
			UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(actionRequest);
			System.out.println("Size: "+uploadRequest.getSize("fileName"));
			
			if (uploadRequest.getSize("fileName")==0) {
				SessionErrors.add(actionRequest, "error");
			}
			
			String sourceFileName = uploadRequest.getFileName("fileName");
			File file = uploadRequest.getFile("fileName");
			
			log.info("Nome file:" + uploadRequest.getFileName("fileName"));
			File newFile = null;
			newFile = new File(folder + sourceFileName);
			log.info("New file name: " + newFile.getName());
			log.info("New file path: " + newFile.getPath());
			
			InputStream in = new BufferedInputStream(uploadRequest.getFileAsStream("fileName"));
			FileInputStream fis = new FileInputStream(file);
			FileOutputStream fos = new FileOutputStream(newFile);
			
			byte[] bytes_ = FileUtil.getBytes(in);
			int i = fis.read(bytes_);
			
			while (i != -1) {
			fos.write(bytes_, 0, i);
			i = fis.read(bytes_);
			}
			fis.close();
			fos.close();
			Float size = (float) newFile.length();
			System.out.println("file size bytes:" + size);
			System.out.println("file size Mb:" + size / 1048576);
			
			log.info("File created: " + newFile.getName());
			SessionMessages.add(actionRequest, "success");
			
			} catch (FileNotFoundException e) {
			System.out.println("File Not Found.");
			e.printStackTrace();
			SessionMessages.add(actionRequest, "error");
			} catch (NullPointerException e) {
			System.out.println("File Not Found");
			e.printStackTrace();
			SessionMessages.add(actionRequest, "error");
			}
			
			catch (IOException e1) {
			System.out.println("Error Reading The File.");
			SessionMessages.add(actionRequest, "error");
			e1.printStackTrace();
			}
			
		}
	
	
	
	public boolean saveFinancialAccountDetails(TesterPortletState portletState)
	{
		boolean proceed = false;
		
		if(portletState.getAccountname()!=null && portletState.getAccountname().length()>0)
	    {
	    	if(portletState.getAccountnumber()!=null && portletState.getAccountnumber().length()>0)
	    	{
    			proceed = true;
    			
	    	}
	    	else
	    	{
	    		//company rc number
	    		portletState.addToErrorList("Provide a valid account number");
	    	}
	    }
	    else
	    {
	    	//company name invalid
	    	portletState.addToErrorList("Provide a valid account name");
	    }
		return proceed;
	}
	
}
