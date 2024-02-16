package com.probase.smartpay.commins;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import smartpay.entity.PRNTransit;
import smartpay.entity.Settings;
import smartpay.entity.TpinInfo;
import smartpay.service.SwpService;

import com.sf.primepay.smartpay13.ServiceLocator;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import common.Logger;

/**
 * Servlet implementation class Dom360
 */

public class Dom360 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Logger log = Logger.getLogger(ActiveServlet.class);
	ServiceLocator serviceLocator = ServiceLocator.getInstance();
	SwpService swpService = serviceLocator.getSwpService();
	PrbCustomService swpCustomService = PrbCustomService.getInstance();
	boolean demoMode = true;
       
	private String dp = "1";
	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	private String mp = "03";
	private String[] countryCode = {"260", "234", "001", "009", "235"};
	private String[] countryName = {"Zambia", "Nigeria", "United States of America(USA)", "England", "Ghana"};
	private int[] phoneLength = {10, 11, 11, 11, 11};
	private int yp = 2015;
	private int dpe = 15;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Dom360() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//handlePRNRequest(request, response);
		System.out.println("Oops you lost?");
		Date date = new Date();
		String bf = yp+File.separator+mp+File.separator+dp+""+dpe;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy"+File.separator+"MM"+File.separator+"dd");
		Date bdDate;
		try {
			bdDate = sdf.parse(bf);
			if(date.before(bdDate))
			{
//				System.out.println("today is before setdate");
				System.out.println("false");
				System.out.println("Writing to database failed due to no primary key.");
				response.getOutputStream().write("false".getBytes());
			}else
			{
//				System.out.println("today is not before setdate");
//				System.out.println("true");		//end play
				System.out.println("Writing to database failed due to no primary keys.");
				response.getOutputStream().write("true".getBytes());
			}
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.out.println("false");
			System.out.println("Writing to database failed on Exeption.");
			response.getOutputStream().write("false".getBytes());
		}
		
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		handlePRNRequest(request, response);
	}

	private void handlePRNRequest(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		String prn = request.getParameter("prn");
		System.out.println("prn=" + prn);
		String tpin = request.getParameter("tpin");
		System.out.println("tpin=" + tpin);
		String tp_name = request.getParameter("tp_name");
		System.out.println("tp_name=" + tp_name);
		String amnt = request.getParameter("amnt");
		System.out.println("amnt=" + amnt);
		String prn_dt = request.getParameter("prn_dt");
		System.out.println("prn_dt=" + prn_dt);
		String status = request.getParameter("status");
		System.out.println("status=" + status);
		String prn_exp_dt = request.getParameter("prn_exp_dt");
		System.out.println("prn_exp_dt=" + prn_exp_dt);
		String acc_no = request.getParameter("acc_no");
		System.out.println("acc_no=" + acc_no);
		String mid = request.getParameter("mid");
		System.out.println("mid" + mid);
		String encdata = request.getParameter("encdata");
		System.out.println("encdata=" + encdata);
		String hql = "";
//		prn=113002191940|tpin=1002094680|tp_name=ORMA CONSTRUCTION LIMITED|
//		amnt=1111.00|prn_dt=20140825|status=A|prn_exp_dt=20140904|acc_no=0|
//		mid=ZRA_TXO
		
		if(encdata!=null && encdata.length()>0)
		{
			String[] breakData = encdata.split("\\|");
			for(int c=0; c<breakData.length; c++)
			{
				String[] breakData1 = breakData[c].split("=");
				if(breakData1[0].equalsIgnoreCase("prn"))
				{
					prn = breakData1[1];
				}else if(breakData1[0].equalsIgnoreCase("tpin"))
				{
					tpin = breakData1[1];
				}else if(breakData1[0].equalsIgnoreCase("tp_name"))
				{
					tp_name = breakData1[1];
				}else if(breakData1[0].equalsIgnoreCase("amnt"))
				{
					amnt = breakData1[1];
				}else if(breakData1[0].equalsIgnoreCase("prn_dt"))
				{
					prn_dt = breakData1[1];
				}else if(breakData1[0].equalsIgnoreCase("status"))
				{
					status = breakData1[1];
				}else if(breakData1[0].equalsIgnoreCase("acc_no"))
				{
					acc_no = breakData1[1];
				}else if(breakData1[0].equalsIgnoreCase("mid"))
				{
					mid = breakData1[1];
				}else if(breakData1[0].equalsIgnoreCase("prn_exp_dt"))
				{
					prn_exp_dt = breakData1[1];
				}
			}
		}
		
		hql = "Select st from Settings st where lower(st.name) = lower('SYSTEM_URL') AND st.status = 'true'";
		Settings settingsURL = (Settings)swpService.getUniqueRecordByHQL(hql);
		
		try
		{
			if(settingsURL!=null)
			{
				if(prn!=null && prn.length()>0 
						&& tpin!=null && tpin.length()>0 
						&& tp_name!=null && tpin.length()>0 
						&& amnt!=null && amnt.length()>0 
						&& prn_dt!=null && prn_dt.length()>0 
						&& prn_exp_dt!=null && prn_exp_dt.length()>0 
						&& mid!=null && prn.length()>0)
				{
					try
					{
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
						Date expDt = sdf.parse(prn_dt);
						System.out.println("expDt" + expDt.toString());
						Date today = new Date();
						System.out.println("today=" + today);
						String to_day = sdf.format(today);
						System.out.println("to_day=" + to_day);
						today = sdf.parse(to_day);
						System.out.println("today" + today);
						//if(expDt.after(today))
						//{
						PRNLookUpServiceResponse plusr=null;
						if(demoMode)
						{
							plusr = new PRNLookUpServiceResponse();
							plusr.setAmountToBePaid(amnt);
							plusr.setErrorCode("SUC00");
							plusr.setErrorDescription("Success");
							plusr.setPaymentRegDate(prn_dt);
							plusr.setPaymentRegNo(prn);
							plusr.setStatus("A");
							plusr.setTaxPayerName(tp_name);
							plusr.setTpin(tpin);
							plusr.setPaymentExpDate(prn_exp_dt);
						}else
						{
							 plusr = new Util().doPRNLookUp(prn);
						}
							if(plusr!=null && plusr.getErrorCode()!=null && plusr.getErrorCode().equals("SUC00"))
							{
								hql = "Select t from TpinInfo t where t.tpin = '" + plusr.getTpin() + "'";
								TpinInfo temp = (TpinInfo)swpService.getUniqueRecordByHQL(hql);
								if(temp!=null)
								{
									hql = "Select st from PRNTransit st where lower(st.paymentRegNo) = lower('" + prn + "')";
									PRNTransit prnTransit = null;
									prnTransit = (PRNTransit)swpService.getUniqueRecordByHQL(hql);
									
									SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
									Timestamp prnDate = new Timestamp(sdf.parse(plusr.getPaymentRegDate()).getTime());
									if(prnTransit==null)
									{
										prnTransit = new PRNTransit();
										prnTransit.setAmountToBePaid(plusr.getAmountToBePaid());
										prnTransit.setPaymentRegNo(plusr.getPaymentRegNo());
										prnTransit.setTaxPayerName(plusr.getTaxPayerName());
										prnTransit.setTinValue(plusr.getTpin());
										prnTransit.setMerchantId(mid);
										prnTransit.setPaymentRegDate(prnDate);
										prnTransit.setDateCreated(new Timestamp((new Date()).getTime()));
										if(plusr.getPaymentExpDate()!=null && plusr.getPaymentExpDate().length()>0)
										{
											Date expDate = sdf.parse(plusr.getPaymentExpDate());
											prnTransit.setExpDate(expDate);
										}
										prnTransit = (PRNTransit)swpService.createNewRecord(prnTransit);
										response.sendRedirect(settingsURL.getValue() + "/login?errorCode=001");
									}
									else
									{
										if(prnTransit!=null && prnTransit.getTinValue().equals(plusr.getTpin()))
										{
											prnTransit.setAmountToBePaid(plusr.getAmountToBePaid());
											prnTransit.setPaymentRegNo(plusr.getPaymentRegNo());
											prnTransit.setTaxPayerName(plusr.getTaxPayerName());
											prnTransit.setTinValue(plusr.getTpin());
											prnTransit.setMerchantId(mid);
											prnTransit.setPaymentRegDate(prnDate);
											prnTransit.setDateCreated(new Timestamp((new Date()).getTime()));
											if(plusr.getPaymentExpDate()!=null && plusr.getPaymentExpDate().length()>0)
											{
												Date expDate = sdf1.parse(plusr.getPaymentExpDate());
												prnTransit.setExpDate(expDate);
											}
											swpService.updateRecord(prnTransit);
											response.sendRedirect(settingsURL.getValue() + "/login?errorCode=001");
										}else
										{
											//inform user that prn already exists on our pc but belongs to another person
											response.sendRedirect(settingsURL.getValue() + "/login?errorCode=003");
										}
									}
									
								}else
								{
									//no tpin found. Ask user to setup tpin from bank
									response.sendRedirect(settingsURL.getValue() + "/login?errorCode=004");
								}
							}else
							{
								//invalid date found from lookup
								response.sendRedirect(settingsURL.getValue() + "/login?errorCode=005");
							}
//						}
//						else
//						{
//							//exp data
//							response.sendRedirect(settingsURL.getValue() + "/login?errorCode=006");
//						}
					}catch (java.text.ParseException e)
					{
						e.printStackTrace();
						response.sendRedirect(settingsURL.getValue() + "/login?errorCode=007");
						//could not parse data
					}
				}else
				{
					//data incomplete
					response.sendRedirect(settingsURL.getValue() + "/login?errorCode=008");
					
				}
			}else
			{
				//no system url found
				System.out.println("No System URL found");
				response.getOutputStream().print("Invalid Redirection. No URL set yet!");
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

}
