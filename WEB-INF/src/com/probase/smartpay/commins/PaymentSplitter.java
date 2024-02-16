package com.probase.smartpay.commins;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import smartpay.entity.PaymentBreakDownHistory;
import smartpay.entity.PaymentHistory;
import smartpay.entity.PaymentTempHolder;
import smartpay.entity.PortalUser;
import smartpay.entity.Settings;
import smartpay.entity.TaxType;
import smartpay.entity.TaxTypeAccount;
import smartpay.entity.enumerations.PaymentBreakDownHistoryConstants;
import smartpay.entity.enumerations.PaymentHistoryConstants;
import smartpay.entity.enumerations.PaymentTypeConstants;
import smartpay.entity.enumerations.SmartPayConstants;
import smartpay.service.SwpService;

import com.sf.primepay.smartpay13.ServiceLocator;

public class PaymentSplitter implements Job {

	
	Logger log = Logger.getLogger(PaymentSplitter.class);
	private Boolean mode = Boolean.TRUE;
	ServiceLocator serviceLocator = ServiceLocator.getInstance();
	SwpService swpService = serviceLocator.getSwpService();
	PrbCustomService swpCustomService = PrbCustomService.getInstance();
	
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub

//		PrbCustomService pcs = PrbCustomService.getInstance();
//		List<Settings> allSettings = pcs.getValidSettings();
		Settings zraAccount = null;
		Settings zraSortCode = null;
		Settings currency = null;
		Settings proxyHost = null;
		Settings proxyUsername = null;
		Settings proxyPassword = null;
		Settings proxyPort = null;
		Settings bankPaymentUrl = null;
		Settings sysApplication = null;
		
		try{
			String hql = "Select st FROM Settings st where st.status = 'true'";
			Collection<Settings> allSettings = (Collection<Settings>)swpService.getAllRecordsByHQL(hql);
			for(Iterator<Settings> iter = allSettings.iterator(); iter.hasNext();)
			{
				Settings temp = iter.next();
				if(temp.getName().equals(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_NUMBER))
				{
					log.info("SETTINGS_ZRA_BANK_ACCOUNT_NUMBER = " + temp.getId());
					zraAccount = temp;
				}
				if(temp.getName().equals(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_SORT_CODE))
				{
					log.info("SETTINGS_ZRA_BANK_ACCOUNT_SORT_CODE = " + temp.getId());
					zraSortCode = temp;
				}
				if(temp.getName().equals(SmartPayConstants.SETTINGS_PLATFORM_COUNTRY))
				{
					log.info("Platform country = " + temp.getId());
					currency = temp;
				}
				if(temp.getName().equals(SmartPayConstants.SETTINGS_APPLICATION_NAME))
				{
					log.info("Platform sysApplication = " + temp.getId());
					sysApplication = temp;
				}
				
				
				if(temp.getName().equals(SmartPayConstants.SETTINGS_PROXY_HOST))
				{
					log.info("SETTINGS_ZRA_BANK_ACCOUNT_NUMBER = " + temp.getId());
					proxyHost = temp;
				}
				if(temp.getName().equals(SmartPayConstants.SETTINGS_PROXY_USERNAME))
				{
					log.info("SETTINGS_ZRA_BANK_ACCOUNT_SORT_CODE = " + temp.getId());
					proxyUsername = temp;
				}
				if(temp.getName().equals(SmartPayConstants.SETTINGS_PROXY_PASSWORD))
				{
					log.info("Platform country = " + temp.getId());
					proxyPassword = temp;
				}
				if(temp.getName().equals(SmartPayConstants.SETTINGS_PROXY_PORT))
				{
					log.info("SETTINGS_ZRA_BANK_ACCOUNT_NUMBER = " + temp.getId());
					proxyPort = temp;
				}
				if(temp.getName().equals(SmartPayConstants.SETTINGS_BANK_PAYMENT_WS_URL))
				{
					log.info("Platform country = " + temp.getId());
					bankPaymentUrl = temp;
				}
			}
			
			hql = "Select st FROM TaxType st";
			Collection<TaxType> allTaxType = (Collection<TaxType>)swpService.getAllRecordsByHQL(hql);
			log.info("allTaxType Size = " + allTaxType.size());
			
			if(currency!=null && zraSortCode!=null && zraAccount!=null)
			{
//				List<PaymentBreakDownHistory> paymentBreakDownHistory = null;
				
//				paymentBreakDownHistory = pcs.getPaymentBreakDownHistory(
//						PaymentBreakDownHistoryConstants.PAYMENT_BREAKDOWN_STATUS_PENDING.getValue(), 
//						PaymentHistoryConstants.PAYMENTHISTORY_STATUS_ZRA_CONFIRMED.getValue());
				hql = "Select pbdh FROM PaymentBreakDownHistory pbdh " +
						" WHERE lower(pbdh.status) = lower('" + 
						PaymentBreakDownHistoryConstants.PAYMENT_BREAKDOWN_STATUS_PENDING.getValue() + "') AND " +
								"lower(pbdh.paymentHistory.status) = " +
						" lower('" + PaymentHistoryConstants.PAYMENTHISTORY_STATUS_ZRA_CONFIRMED.getValue() + "')  " +
								"ORDER by pbdh.paymentHistory.entryDate ASC";
				log.info("hql = " + hql);
				
				Collection<PaymentBreakDownHistory> paymentBreakDownHistory = (Collection<PaymentBreakDownHistory>) swpService.getAllRecordsByHQL(hql);
				
				if(paymentBreakDownHistory != null){
					log.info("paymentBreakDownHistory Id = "+paymentBreakDownHistory.size());
					for(Iterator<PaymentBreakDownHistory> pbdhIter = paymentBreakDownHistory.iterator(); pbdhIter.hasNext();)
					{
						PaymentBreakDownHistory pbdh = pbdhIter.next();
						log.info("pbdh id = " + pbdh.getId());
						pbdh.setStatus(PaymentBreakDownHistoryConstants.PAYMENT_BREAKDOWN_STATUS_CANCELED);
						swpService.updateRecord(pbdh);
						TaxType tt = null;
						TaxTypeAccount tta = null;
						for(Iterator<TaxType> ttIter = allTaxType.iterator(); ttIter.hasNext();)
						{
							TaxType taxType = ttIter.next();
							log.info("---> " + taxType.getId() + " &&& " + (pbdh.getTaxType().getId()));
							if(taxType.getId().equals(pbdh.getTaxType().getId()))
							{
								tt = pbdh.getTaxType();
								if(tt!=null)
									log.info("tt = " + tt.getId() + " && tt = " + tt.getTaxCode());
								
								hql = "Select tta From TaxTypeAccount tta where lower(tta.taxType.taxCode) = " +
										"lower('" + tt.getTaxCode() + "')";
								tta = (TaxTypeAccount)swpService.getUniqueRecordByHQL(hql);
								if(tta!=null)
									log.info("tta = " + tta.getId() + " && tt = " + tta.getAccountNumber());
								
								if(tt!=null && tta!=null)
								{
									log.info("tta id = " + tta.getId());
									log.info("tt id = " + tt.getId());
									String query = "Select st FROM TaxTypeAccount st where st.taxType.id = " + tt.getId();
									TaxTypeAccount taxTypeAccount = (TaxTypeAccount)swpService.getUniqueRecordByHQL(query);
									
									
									if(taxTypeAccount!=null)
									{
										DateFormat df = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss:S");
								        DateFormat df1 = new SimpleDateFormat( "SssmmHHddMMyy");
								        DateFormat df2 = new SimpleDateFormat( "yyyyMMdd");
										DateFormat df3 = new SimpleDateFormat( "Sssmm");
								        String currentTimeStamp = df.format(new Date());
								        String currentTimeStamp1 = df1.format(new Date());
								        String currentTimeStamp2 = df2.format(new Date());
								        String currentTimeStamp3 = df3.format(new Date());
								        String uniqId = "SPI" +  currentTimeStamp1;
								        
								        
								        log.info("currentTimeStamp1 = " + currentTimeStamp1);
								        log.info("currentTimeStamp = " + currentTimeStamp);
								        log.info("uniqId = " + uniqId);
								        log.info("zraAccount = " + zraAccount.getValue());
								        log.info("zraSortCode = " + zraSortCode.getValue());
								        log.info("pbdh = " + Double.toString(pbdh.getAmount()));
								        log.info("currentTimeStamp2 = " + currentTimeStamp2);
								        log.info("currentTimeStamp3 = " + currentTimeStamp3);
								        log.info("tta.getAccountNumber = " + tta.getAccountNumber());
								        log.info("tta.getAccountSortCode = " + tta.getAccountSortCode());
										
										
								        FundsTransferResponse ftr= null;
										try {
											if(this.mode.equals(Boolean.TRUE))
											{
												log.info("We are in 0");
												DateFormat dfg = new SimpleDateFormat("yyyyddMMHHmmssS");
												PaymentHistory pbdh12 = pbdh.getPaymentHistory();
//												String ref = pbdh12.getAssessment()!=null ? (pbdh12.getAssessment().getPorts().getPortCode() + "/" + 
//														pbdh12.getAssessment().getAssessmentNumber() + "/" + 
//														pbdh12.getAssessment().getAssessmentYear() + "/" + 
//														pbdh.getTaxType().getTaxCode()+"/"+ dfg.format(new Date())) : 
//															(pbdh12.getDomTax()!=null ? pbdh12.getRequestMessageId() : "");
												String ref = pbdh12.getAssessment()!=null ? ("SP/" + pbdh.getTransactionNumber()) : 
															(pbdh12.getDomTax()!=null ? pbdh12.getRequestMessageId() : "");
 

												if(ComminsApplicationState.STB==1)
												{
													ftr = Util.doFundsTransfer(sysApplication.getValue(), "SMARTPAY:ZM:REVENUE:ENTRY:REQUEST", 
														  currentTimeStamp1, currentTimeStamp, uniqId, 
														"ZMW", "1095", zraAccount.getValue(), 
														zraSortCode.getValue(), Double.toString(pbdh.getAmount()), currentTimeStamp2, 
														currentTimeStamp3, "00-00-00", tta.getAccountNumber(), 
														tta.getAccountSortCode(),
									    				proxyUsername.getValue(), 
									    				proxyPassword.getValue(), 
									    				proxyHost.getValue(), 
									    				proxyPort.getValue(), 
														bankPaymentUrl.getValue(), ref);
												}
												
												if(ComminsApplicationState.BANC_ABC==1)
												{
													ftr = Util.doFundsTransfer(sysApplication.getValue(), "SMARTPAY:ZM:REVENUE:ENTRY:REQUEST", 
														  currentTimeStamp1, currentTimeStamp, uniqId, 
														"ZMW", "1095", zraAccount.getValue(), 
														zraSortCode.getValue(), Double.toString(pbdh.getAmount()), currentTimeStamp2, 
														currentTimeStamp3 + "" + pbdh.getTaxType().getTaxCode(), "00-00-00", tta.getAccountNumber(), 
														tta.getAccountNumber(),
									    				proxyUsername.getValue(), 
									    				proxyPassword.getValue(), 
									    				proxyHost.getValue(), 
									    				proxyPort.getValue(), 
														bankPaymentUrl.getValue(), ref);
												}
												
												
											}
											else
											{
												log.info("We are in 01");
												ftr = new FundsTransferResponse();
												ftr.setAccountNumber(zraAccount.getValue());
												ftr.setResMessageId(pbdh.getTransactionNumber());
												ftr.setResTimeStamp(Boolean.TRUE);
												ftr.setStatus(Boolean.TRUE);
											}
											
											if(ftr!=null)
											{
												log.info("We are in 3");
												if(ftr.getStatus().equals(Boolean.TRUE))
												{
													log.info("We are in 1");
													pbdh.setStatus(PaymentBreakDownHistoryConstants.PAYMENT_BREAKDOWN_STATUS_APPROVED);
													pbdh.setDateOfTransaction(new Timestamp((new Date()).getTime()));
													pbdh.setReceipientAccountNumber(tta.getAccountNumber());
													pbdh.setSourceAccountNumber(zraAccount.getValue());
													pbdh.setTransactionNumber(ftr.getResMessageId());
													//pcs.deleteRecord(pth);
													swpService.updateRecord(pbdh);
													log.info("We are in 11");
													
												}else
												{
													log.info("We are in 2");
													//pcs.deleteRecord(pth);
													pbdh.setStatus(PaymentBreakDownHistoryConstants.PAYMENT_BREAKDOWN_STATUS_PENDING);
													swpService.updateRecord(pbdh);
													log.info("We are in 21");
												}
											}else
											{
												log.info("We are in 30");
											}
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
						}
					}
				}
				
				
			}
					
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		
		
		
		
//		List<TaxType> allTaxType = pcs.getTaxTypes();
		
		
				
					
//					PaymentTempHolder pth = new PaymentTempHolder();
//					pth.setPaymentBreakDownHistory(pbdh);
//					pth.setDateAdded(new Timestamp((new Date()).getTime()));
//					pth = (PaymentTempHolder)pcs.createNewRecord(pth);
//					log.info("pth id = " + pth.getId());
					
				
//		String hql = "Select st FROM Settings where st.status = 'true'";
//		Collection<Settings> allSettings = (Collection<Settings>)swpService.getAllRecordsByHQL(hql);
//		Settings zraAccount = null;
//		Settings zraSortCode = null;
//		Settings currency = null;
//		
//		for(Iterator<Settings> iter = allSettings.iterator(); iter.hasNext();)
//		{
//			Settings temp = iter.next();
//			if(temp.getName().equals(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_NUMBER))
//				zraAccount = temp;
//			if(temp.getName().equals(SmartPayConstants.SETTINGS_ZRA_BANK_ACCOUNT_SORT_CODE))
//				zraSortCode = temp;
//			if(temp.getName().equals(SmartPayConstants.SETTINGS_PLATFORM_COUNTRY))
//				currency = temp;
//		}
//		
//		
//		hql = "Select st FROM TaxType";
//		Collection<TaxType> allTaxType = (Collection<TaxType>)swpService.getAllRecordsByHQL(hql);
//		
//		
//		if(currency!=null && zraSortCode!=null && zraAccount!=null)
//		{
//			Collection<PaymentBreakDownHistory> paymentBreakDownHistory = null;
//			hql = "Select top 100 pbdh FROM PaymentBreakDownHistory pbdh " +
//					" WHERE pbdh.id not in (Select pth.paymentBreakDownHistory.id FROM " +
//					"PaymentTempHolder pth) AND lower(pbdh.status) = lower('" + 
//					PaymentBreakDownHistoryConstants.PAYMENT_BREAKDOWN_STATUS_PENDING.getValue() + "') " +
//					" AND lower(pbdh.paymentHistory.status) = " +
//					" lower('" + PaymentHistoryConstants.PAYMENTHISTORY_STATUS_ZRA_CONFIRMED.getValue() + "') " +
//					" AND pbdh.id !=  ORDER by pbdh.entryDate ASC";
//			log.info("hql = " + hql);
//			
//			paymentBreakDownHistory = (Collection<PaymentBreakDownHistory>) swpService.getAllRecordsByHQL(hql);
//			if(paymentBreakDownHistory != null){
//				log.info("paymentBreakDownHistory Id = "+paymentBreakDownHistory.size());
//				for(Iterator<PaymentBreakDownHistory> pbdhIter = paymentBreakDownHistory.iterator(); pbdhIter.hasNext();)
//				{
//					PaymentBreakDownHistory pbdh = pbdhIter.next();
//					log.info("pbdh id = " + pbdh.getId());
//					PaymentTempHolder pth = new PaymentTempHolder();
//					pth.setPaymentBreakDownHistory(pbdh);
//					pth.setDateAdded(new Timestamp((new Date()).getTime()));
//					pth = (PaymentTempHolder)swpService.createNewRecord(pth);
//					TaxType tt = null;
//					TaxTypeAccount tta = null;
//					for(Iterator<TaxType> ttIter = allTaxType.iterator(); ttIter.hasNext();)
//					{
//						TaxType taxType = ttIter.next();
//						if(taxType.getId().equals(pbdh.getTaxType().getId()))
//						{
//							tt = pbdh.getTaxType();
//							hql = "Select tta From TaxTypeAccount tta where lower(tta.taxType.taxCode) = " +
//									"lower('" + tt.getTaxCode() + "')";
//							log.info("hql = " + hql);
//							tta = (TaxTypeAccount)swpService.getUniqueRecordByHQL(hql);
//							break;
//						}
//					}
//					if(tt!=null && tta!=null)
//					{
//						log.info("tta id = " + tta.getId());
//						log.info("tt id = " + tt.getId());
//						hql = "Select st FROM TaxTypeAccount where st.taxType.id = " + tt.getId();
//						log.info("hql = " + hql);
//						TaxTypeAccount taxTypeAccount = (TaxTypeAccount)swpService.getUniqueRecordByHQL(hql);
//						if(taxTypeAccount!=null)
//						{
//							DateFormat df = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss:S");
//					        DateFormat df1 = new SimpleDateFormat( "SssmmHHddMMyy");
//					        DateFormat df2 = new SimpleDateFormat( "yyyyMMdd");
//							DateFormat df3 = new SimpleDateFormat( "Sssmm");
//					        String currentTimeStamp = df.format(new Date());
//					        String currentTimeStamp1 = df1.format(new Date());
//					        String currentTimeStamp2 = df2.format(new Date());
//					        String currentTimeStamp3 = df3.format(new Date());
//					        String uniqId = "SPI" +  currentTimeStamp1;
//					        
//					        
//					        log.info("currentTimeStamp1 = " + currentTimeStamp1);
//					        log.info("currentTimeStamp = " + currentTimeStamp);
//					        log.info("uniqId = " + uniqId);
//					        log.info("zraAccount = " + zraAccount.getValue());
//					        log.info("zraSortCode = " + zraSortCode.getValue());
//					        log.info("pbdh = " + Double.toString(pbdh.getAmount()));
//					        log.info("currentTimeStamp2 = " + currentTimeStamp2);
//					        log.info("currentTimeStamp3 = " + currentTimeStamp3);
//					        log.info("tta.getAccountNumber = " + tta.getAccountNumber());
//					        log.info("tta.getAccountSortCode = " + tta.getAccountSortCode());
//							
//							
//					        FundsTransferResponse ftr= null;
//							try {
//								if(this.mode .equals(Boolean.FALSE))
//								{
//									
//									ftr = Util.doFundsTransfer("SmartPay", "SMARTPAY:ZM:REVENUE:ENTRY:REQUEST", 
//										  currentTimeStamp1, currentTimeStamp, uniqId, 
//										"ZMW", "1095", zraAccount.getValue(), 
//										zraSortCode.getValue(), Double.toString(pbdh.getAmount()), currentTimeStamp2, 
//										currentTimeStamp3, "00-00-00", tta.getAccountNumber(), 
//										tta.getAccountSortCode());
//									
//								}
//								else
//								{
//									ftr = new FundsTransferResponse();
//									ftr.setAccountNumber(zraAccount.getValue());
//									ftr.setResMessageId(RandomStringUtils.random(8).toUpperCase());
//									ftr.setResTimeStamp(Boolean.TRUE);
//									ftr.setStatus(Boolean.TRUE);
//								}
//								
//								if(ftr!=null)
//								{
//									if(ftr.getStatus().equals(Boolean.TRUE))
//									{
//										pbdh.setStatus(PaymentBreakDownHistoryConstants.PAYMENT_BREAKDOWN_STATUS_APPROVED);
//										pbdh.setDateOfTransaction(new Timestamp((new Date()).getTime()));
//										pbdh.setReceipientAccountNumber(tta.getAccountNumber());
//										pbdh.setSourceAccountNumber(zraAccount.getValue());
//										pbdh.setTransactionNumber(ftr.getResMessageId());
//										swpService.deleteRecord(pth);
//										swpService.updateRecord(pbdh);
//										
//									}
//								}
//							} catch (MalformedURLException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//					}
//				}
//					
//			}
//		}
		
	}

}
