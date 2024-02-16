package com.probase.smartpay.commins;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import smartpay.entity.Settings;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.service.SwpService;

import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Servlet implementation class PaymentSplitterServlet
 */
public class PaymentSplitterServlet extends HttpServlet implements ServletContextListener {
	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(PaymentSplitterServlet.class);
	ServiceLocator serviceLocator = ServiceLocator.getInstance();
	SwpService swpService = serviceLocator.getSwpService();
	PrbCustomService swpCustomService = PrbCustomService.getInstance();
	CronScheduler cronScheduler = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PaymentSplitterServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		log.info("Some fool Sending a get request to this servlet from ip => " + request.getRemoteAddr());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		log.info("Some fool Sending a post request to this servlet from ip => " + request.getRemoteAddr());
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		log.info("Some test");
		super.destroy();
		if(this.cronScheduler!=null)
		{
			//this.cronScheduler.shutdownScheduler();
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		log.info("Some89");
		try
		{
			//this.cronScheduler = new CronScheduler();
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
