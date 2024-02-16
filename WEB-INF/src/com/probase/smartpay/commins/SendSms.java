package com.probase.smartpay.commins;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;

import com.liferay.portal.kernel.servlet.URLEncoder;

public class SendSms extends Thread
{
	private static final Logger logger = Logger.getLogger(SendMail.class);
	private String toMobileNumber;
	private String message;
	private String from;
	private static final String URL_ = "http://sms2.probasegroup.com/groupsms.php";
	private static final String USER_AGENT = "Mozilla/5.0";
	private String proxyHost=null;
	private String proxyPort=null;

	public SendSms(String toMobileNumber, String message, String from, String proxyHost, String proxyPort)
	{
		System.out.println("in send sms");
		
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.toMobileNumber = toMobileNumber;
		try {
			this.message = java.net.URLEncoder.encode(message, "UTF-8");
			this.from = from;
			Thread thread = new Thread(this);
			thread.start();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run()
	{
		if(this.proxyHost!=null && this.proxyPort!=null)
		{
			if(ComminsApplicationState.STB==1)
			{
				System.setProperty("http.proxyHost", "10.236.6.99");
				System.setProperty("http.proxyPort", "80");
			}
			
		}
		
		if(ComminsApplicationState.SMS_SEND==1)
		{
			try {
				URL url = new URL(URL_ + "?username=Kaziva&password=pr0base&type=TEXT&sender=" + this.from + "&mobile=" + this.toMobileNumber + "&message=" + this.message);
				System.out.println("URL ==" + URL_ + "?username=Kaziva&password=pr0base&type=TEXT&sender=" + this.from + "&mobile=" + this.toMobileNumber + "&message=" + this.message);
				URLConnection con = url.openConnection();
				BufferedReader in = new BufferedReader(
	                    new InputStreamReader(
	                    		con.getInputStream()));
				String inputLine;
				
				while ((inputLine = in.readLine()) != null) 
				System.out.println(inputLine);
				in.close();
				System.out.println("inputLine ==" + inputLine);
				in.close();
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
}
