package com.probase.smartpay.commins;

import java.util.Date;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;

public class SendMail extends Thread
{
	private static final Logger logger = Logger.getLogger(SendMail.class);
	private String fromEmail;
	private String toEmail;
	private String password;
	private int port;
	private String subject;
	private String body;
	private String username;
	private String sender;

	public SendMail(String fromEmail, String toEmail, String password,
			String subject, String body, int port, String userName, String sender)
	{
		System.out.println("in send mail");
		this.fromEmail = fromEmail;
		this.toEmail = toEmail;
		this.password = password;
		this.subject = subject;
		this.body = body;
		this.port = port;
		this.username = userName;
		this.sender = sender;
		Thread thread = new Thread(this);
		thread.start();
	}

	public void run()
	{
		System.out.println(this.username + "/" + this.password);
		//172.31.67.3
		sendMailAdvance(this.username, this.password, "smtp.gmail.com", Integer.toString(this.port),  this.toEmail, this.subject, this.body);
		//sendMailAdvance("etax", this.password, "10.236.6.65", Integer.toString(this.port),  "smicer66@gmail.com", this.subject, this.body);
//		System.out.println("in run" + port);
//		HtmlEmail email = new HtmlEmail();
//		email.setHostName("10.236.6.65");
//		email.setSmtpPort(port);
//		email.setAuthenticator(new DefaultAuthenticator(username, password));
//		//email.setSSL(true);
//		email.setDebug(true);
//		//email.setTLS(true);
//		try
//		{
//			email.setFrom(fromEmail, this.sender);
//			email.setSubject(subject);
//			email.setHtmlMsg(body);
//			email.addTo("smicer66@gmail.com");
//			email.send();
//		} catch (Exception ex)
//		{
//			System.err.println(ex.toString());
//			logger.error("error sending email ", ex);
//		}
//		
//		Email email = new SimpleEmail();
//		email.setHostName("10.236.6.65");
//		email.setSmtpPort(25);
//		email.setAuthenticator(new DefaultAuthenticator(username, password));
//		try {
//			email.setFrom(fromEmail);
//			email.setSubject("TestMail");
//			email.setMsg("This is a test mail ... :-)");
//			email.addTo("smicer66@gmail.com");
//			email.send();
//		} catch (EmailException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		

	}
	
	
	
	
	
	public synchronized static boolean sendMailAdvance(final String username, final String password, 
			String host, String port, String emailTo, String subject, String body)
	{
		                      

        try
        {
            java.util.Properties props = null;
            props = System.getProperties();
//            System.setProperty("http.proxyHost", "10.236.6.99");
//			System.setProperty("http.proxyPort", "80");
    		
    		
            props.put("mail.smtp.user", username);
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.debug", "true");

            
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.socketFactory.port", port);

            props.put("mail.smtp.starttls.enable",true);

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    System.out.println("------------------------------------");
                    System.out.println("username = " + username + " &&& password = " + password);
                    return new PasswordAuthentication(username, password);
                    
                }
            });
            session.setDebug(true);

            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(username));
            msg.setSubject(subject);                
            msg.setText(body, "ISO-8859-1");
            msg.setSentDate(new Date());
            msg.setHeader("content-Type", "text/html;charset=\"ISO-8859-1\"");
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));
            msg.saveChanges();

            Transport transport = session.getTransport("smtp");
            transport.connect(host, username, password);
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();

            return true;
        }
        catch (Exception mex)
        {
            mex.printStackTrace();
            return false;
        }
	}
}
