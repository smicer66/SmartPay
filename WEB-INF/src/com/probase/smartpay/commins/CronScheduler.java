package com.probase.smartpay.commins;

import javax.naming.NamingException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.sf.primepay.smartpay13.HibernateUtils;

import common.Logger;

import smartpay.entity.Settings;

public class CronScheduler {

	//CronScheduler cronScheduler = null;
	//private Scheduler sche = null;
	Logger log = Logger.getLogger(CronScheduler.class);
	SchedulerFactory sf = new StdSchedulerFactory();
	Scheduler sche = sf.getScheduler();
	
	SchedulerFactory sf_eod = new StdSchedulerFactory();
	Scheduler sche_eod = sf_eod.getScheduler();
	
	
	public static enum ACTION{
		PAY_SPLIT, EOD
	}
	
	
	public CronScheduler(ACTION action) throws Exception
	{
		
		
		if(action.equals(ACTION.PAY_SPLIT))
		{
			sche.start();
			
			JobDetail jDetail = null;
			jDetail = sche.getJobDetail("SplitPayments", "NJob");
			if(jDetail==null)
			{
				jDetail = new JobDetail("SplitPayments", "NJob", PaymentSplitter.class);
				CronTrigger crTrigger = new CronTrigger("cronTrigger", "NJob", "0/60 * * * * ?");
				sche.scheduleJob(jDetail, crTrigger);
			}
		}
		//sche.shutdown();
		
		else if(action.equals(ACTION.EOD))
		{
			sche_eod.start();
			
			JobDetail jDetailEndOfDayDomT = null;
			jDetailEndOfDayDomT = sche.getJobDetail("DomTEndOfDay", "NJob1");
			if(jDetailEndOfDayDomT==null)
			{
				jDetailEndOfDayDomT = new JobDetail("DomTEndOfDay", "NJob1", EndOfDayDomT.class);
				CronTrigger crTrigger = new CronTrigger("cronTrigger1", "NJob1", "0/10800 * * * * ?");
				sche_eod.scheduleJob(jDetailEndOfDayDomT, crTrigger);
			}
		}
		//sche.shutdown();
	}
	
	public void shutdownScheduler(ACTION action)
	{
		log.info("Shutdown scheduler");
		if(action!=null)
		{
			try {
				if(action.equals(ACTION.PAY_SPLIT))
				{
					log.info("Shutdown scheduler1");
					sche.shutdown();
					try {
						AbstractPrbCustomService.setSessionFactory(HibernateUtils.getSessionFactory());
					} catch (NamingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					log.info("Shutdown scheduler2");
				}else if(action.equals(ACTION.EOD))
				{
					log.info("Shutdown scheduler1");
					sche_eod.shutdown();
					try {
						AbstractPrbCustomService.setSessionFactory(HibernateUtils.getSessionFactory());
					} catch (NamingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					log.info("Shutdown scheduler2");
				}
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void turnOff(Boolean boolVal, Scheduler sche)
	{
		if(boolVal.equals(Boolean.TRUE) && sche!=null)
		{
			try {
				sche.shutdown();
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else
		{
			log.info("Could not shut down fee splitter scheduler");
		}
	}
}
