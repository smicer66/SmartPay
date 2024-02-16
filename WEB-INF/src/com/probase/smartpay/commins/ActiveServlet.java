package com.probase.smartpay.commins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;

import smartpay.entity.Company;
import smartpay.entity.PaymentHistory;
import smartpay.service.SwpService;

import com.probase.smartpay.admin.payments.PaymentsPortletState;
import com.sf.primepay.smartpay13.ServiceLocator;

import common.Logger;

/**
 * Servlet implementation class ActiveServlet
 */
public class ActiveServlet extends HttpServlet {
	Logger log = Logger.getLogger(ActiveServlet.class);
	ServiceLocator serviceLocator = ServiceLocator.getInstance();
	SwpService swpService = serviceLocator.getSwpService();
	PrbCustomService swpCustomService = PrbCustomService.getInstance();
	//CacProcessPortletUtil sub = new CacProcessPortletUtil();
	String sep = File.separator;

	public ActiveServlet() {

	}
	
	

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		Long companyId = Long.valueOf(request.getParameter("companyId") == null ? "0" : request.getParameter("companyId"));
		String paymentTxnId = (request.getParameter("paymentTxnId") == null ? "0" : request.getParameter("paymentTxnId"));
		Double amount =  Double.valueOf(request.getParameter("amount") == null ? "0.0" : request.getParameter("amount"));
		String assessmentId = (request.getParameter("assessmentId") == null ? "0" : request.getParameter("assessmentId"));
		String workFlowRefId = (request.getParameter("workFlowRefId") == null ? "0" : request.getParameter("workFlowRefId"));
		String domTaxId = (request.getParameter("domTaxId") == null ? "0" : request.getParameter("domTaxId"));
		String reportId = (request.getParameter("reportId") == null ? null : request.getParameter("reportId"));
		Long assessmentIdL = null;
		if(assessmentId!=null && assessmentId.length()>0)
		{
			assessmentIdL = Long.valueOf(assessmentId);
		}
		Company company = null;
		PaymentHistory paymentHistory =null;
		
		if (action.equalsIgnoreCase("downloadPaymentSlipForAssessment")) {
			try{
				//paymentHistory = (PaymentHistory) swpService.getRecordById(PaymentHistory.class,paymentTxnId);
				//if(paymentHistory != null){
					log.info("paymentTxnId = "+paymentTxnId);
					
					String fileName = "Payment_Receipt_For_Txn_Ref_No_" + paymentTxnId.replaceAll("/", "_") + ".pdf";
					handleDownloadPaymentSlipForm(paymentTxnId, amount, request, response, "PaymentReceiptForTaxAssessment.rptdesign", "PaymentHistory", fileName);
				//}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		} 
		else if (action.equalsIgnoreCase("downloadLumpSlip")) {
			try{
				//paymentHistory = (PaymentHistory) swpService.getRecordById(PaymentHistory.class,paymentTxnId);
				//if(paymentHistory != null){
					log.info("assessmentId = "+assessmentIdL);
					
					String fileName = "Payment_Receipt_For_Ass_Reg_No" + assessmentIdL + ".pdf";
					handleDownloadLumpPaymentSlipForm(assessmentIdL, amount, request, response, "PaymentReceiptForTaxAssessmentLumpedNew.rptdesign", "PaymentHistory", fileName);
				//}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		else if (action.equalsIgnoreCase("downloadWFLumpSlip")) {
			try{
				//paymentHistory = (PaymentHistory) swpService.getRecordById(PaymentHistory.class,paymentTxnId);
				//if(paymentHistory != null){
					log.info("workFlowRefId = "+workFlowRefId);
					
					String fileName = "Payment_Receipt_For_WF_Det_" + workFlowRefId + ".pdf";
					handleDownloadWFPaymentSlipForm(workFlowRefId, amount, request, response, "PaymentReceiptForTaxAssessmentWF.rptdesign", "PaymentHistory", fileName);
				//}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		else if (action.equalsIgnoreCase("downloadDomTaxSlip")) {
			try{
				//paymentHistory = (PaymentHistory) swpService.getRecordById(PaymentHistory.class,paymentTxnId);
				//if(paymentHistory != null){
					log.info("domTaxId = "+domTaxId);
					
					String fileName = "Payment_Receipt_For_DomTax_" + domTaxId + ".pdf";
					handleDownloadDomTaxPaymentSlipForm(domTaxId, amount, request, response, "PaymentReceiptForTaxDomTaxLumped.rptdesign", "PaymentHistory", fileName);
				//}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}else  if (action.equalsIgnoreCase("downloadReceipt"))
		{
			try{
				//paymentHistory = (PaymentHistory) swpService.getRecordById(PaymentHistory.class,paymentTxnId);
				//if(paymentHistory != null){
					log.info("domTaxId = "+domTaxId);
					
					String fileName = "Payment_Receipt_For_DomTax_" + domTaxId + ".pdf";
					if(reportId!=null)
						handleDownloadReceipt(reportId, request, response);
				//}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	//
	
	
	
	public void handleDownloadDomTaxPaymentSlipForm(String domTaxId, Double amount, HttpServletRequest request, HttpServletResponse response, 
			String reportName, String outputFilePath, String fileName){
		IReportEngine reportEngine = null;
		 
        EngineConfig config = new EngineConfig();
        log.info("-----------------user dir is " + System.getProperty("user.dir"));
        String userDir = System.getProperty("user.dir");
        config.setLogConfig(userDir +sep+ "birtlogs", Level.SEVERE);
        config.setEngineHome(userDir +sep+ "ReportEngine");

        try {
                Platform.startup(config);
        } catch (BirtException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
        }

        IReportEngineFactory iReportEngineFactory = (IReportEngineFactory) Platform
                        .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
        reportEngine = iReportEngineFactory.createReportEngine(config);

        String reportsUrl = userDir +sep+"ReportEngine"+sep+"Reports";
        
        log.info("reportsUrl = "+reportsUrl);
        
        IReportRunnable runnableReport = null; 
        log.info("reportsUrl1 = ");
        try {
                runnableReport = reportEngine.openReportDesign(reportsUrl + sep+""+reportName);
                log.info("reportsUrl2 = ");
        } catch (EngineException e1) {
                // TODO Auto-generated catch block
        	log.info("reportsUrl3 = ");
                e1.printStackTrace();
                
        }
        
        log.info("reportsUrl4 = ");
        IRunAndRenderTask runAndRenderTask = reportEngine.createRunAndRenderTask(runnableReport);
        log.info("reportsUrl5 = ");
        PDFRenderOption renderOption = new PDFRenderOption();
        log.info("reportsUrl6 = ");
        renderOption.setOutputFileName(reportsUrl + sep+""+ outputFilePath+sep+""+fileName);
        log.info("reportsUrl7 = ");
        renderOption.setOutputFormat(IRenderOption.OUTPUT_FORMAT_PDF);
        log.info("reportsUrl8 = ");
       
        runAndRenderTask.setRenderOption(renderOption);
        log.info("reportsUrl9 = ");
        //runAndRenderTask.setRenderOption(htmlRenderOption);
        runAndRenderTask.setParameterValue("domTaxId", domTaxId);
        runAndRenderTask.setParameterValue("total_amt", amount);
        log.info("reportsUrl10 = ");
        try {
			runAndRenderTask.run();
			log.info("reportsUrl11 = ");
		} catch (EngineException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			log.info("reportsUrl12 = ");
		}
        File file = null;
        try{
        	log.info("reportsUrl13 = ");
        	file = new File(reportsUrl + sep+""+ outputFilePath+sep+""+fileName);
        	log.info("File Name ==" + reportsUrl + sep+""+ outputFilePath+sep+""+fileName);
        	log.info("reportsUrl13 = ");
        	FileInputStream baos = new FileInputStream(file);
        	log.info("reportsUrl14 = ");
			response.setContentType("application/pdf");
			log.info("reportsUrl15 = ");
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			log.info("reportsUrl16 = ");
			
 
			int read=0;
			byte[] bytes = new byte[1024];
			OutputStream os = response.getOutputStream();
 
			log.info("reportsUrl17 = ");
			while((read = baos.read(bytes))!= -1){
				os.write(bytes, 0, read);
			}
			log.info("reportsUrl18 = ");
			os.flush();
			os.close();
        }catch(Exception ex){
        	ex.printStackTrace();
        	log.info("reportsUrl19 = ");
        }
        
	}
	
	
	public void handleDownloadReceipt(String reportIdFileName, HttpServletRequest request, HttpServletResponse response){
		IReportEngine reportEngine = null;
		 
        log.info("-----------------user dir is " + System.getProperty("user.dir"));
        String userDir = System.getProperty("user.dir");

        String reportsUrl = userDir +sep+"ReportEngine"+sep+"Reports";
        
        log.info("reportsUrl = "+reportsUrl);
        
        File file = null;
        try{
        	log.info("reportsUrl13 = ");
        	file = new File(reportsUrl + sep+""+ "Payments" + sep + reportIdFileName);
        	log.info("File Name ==" + reportsUrl + sep+""+reportIdFileName);
        	log.info("reportsUrl13 = ");
        	if(!file.exists())
        	{
        		file.createNewFile();
        	}
        	FileInputStream baos = new FileInputStream(file);
        	
        	log.info("reportsUrl14 = ");
			response.setContentType("application/vnd.ms-excel");
			log.info("reportsUrl15 = ");
			response.setHeader("Content-Disposition", "attachment;filename=" + reportIdFileName);
			log.info("reportsUrl16 = ");
			
 
			int read=0;
			byte[] bytes = new byte[1024];
			OutputStream os = response.getOutputStream();
 
			log.info("reportsUrl17 = ");
			while((read = baos.read(bytes))!= -1){
				os.write(bytes, 0, read);
			}
			log.info("reportsUrl18 = ");
			os.flush();
			os.close();
        }catch(Exception ex){
        	ex.printStackTrace();
        	log.info("reportsUrl19 = ");
        }
        
	}

	public void handleDownloadPaymentSlipForm(String paymentTxnId, Double amount, HttpServletRequest request, HttpServletResponse response, 
			String reportName, String outputFilePath, String fileName){
		IReportEngine reportEngine = null;
		 
        EngineConfig config = new EngineConfig();
        log.info("-----------------user dir is " + System.getProperty("user.dir"));
        String userDir = System.getProperty("user.dir");
        config.setLogConfig(userDir +sep+ "birtlogs", Level.SEVERE);
        config.setEngineHome(userDir +sep+ "ReportEngine");

        try {
                Platform.startup(config);
        } catch (BirtException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
        }

        IReportEngineFactory iReportEngineFactory = (IReportEngineFactory) Platform
                        .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
        reportEngine = iReportEngineFactory.createReportEngine(config);

        String reportsUrl = userDir +sep+"ReportEngine"+sep+"Reports";
        
        log.info("reportsUrl = "+reportsUrl);
        
        IReportRunnable runnableReport = null; 
        log.info("reportsUrl1 = ");
        try {
                runnableReport = reportEngine.openReportDesign(reportsUrl + sep+""+reportName);
                log.info("reportsUrl2 = ");
        } catch (EngineException e1) {
                // TODO Auto-generated catch block
        	log.info("reportsUrl3 = ");
                e1.printStackTrace();
                
        }
        
        log.info("reportsUrl4 = ");
        IRunAndRenderTask runAndRenderTask = reportEngine.createRunAndRenderTask(runnableReport);
        log.info("reportsUrl5 = ");
        PDFRenderOption renderOption = new PDFRenderOption();
        log.info("reportsUrl6 = ");
        renderOption.setOutputFileName(reportsUrl + sep+""+ outputFilePath+sep+""+fileName);
        log.info("reportsUrl7 = ");
        renderOption.setOutputFormat(IRenderOption.OUTPUT_FORMAT_PDF);
        log.info("reportsUrl8 = ");
       
        runAndRenderTask.setRenderOption(renderOption);
        log.info("reportsUrl9 = ");
        //runAndRenderTask.setRenderOption(htmlRenderOption);
        runAndRenderTask.setParameterValue("paymentTxnId", paymentTxnId);
        runAndRenderTask.setParameterValue("total_amt", amount);
        log.info("reportsUrl10 = ");
        try {
			runAndRenderTask.run();
			log.info("reportsUrl11 = ");
		} catch (EngineException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			log.info("reportsUrl12 = ");
		}
        File file = null;
        try{
        	log.info("reportsUrl13 = ");
        	file = new File(reportsUrl + sep+""+ outputFilePath+sep+""+fileName);
        	log.info("File Name ==" + reportsUrl + sep+""+ outputFilePath+sep+""+fileName);
        	log.info("reportsUrl13 = ");
        	FileInputStream baos = new FileInputStream(file);
        	log.info("reportsUrl14 = ");
			response.setContentType("application/pdf");
			log.info("reportsUrl15 = ");
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			log.info("reportsUrl16 = ");
			
 
			int read=0;
			byte[] bytes = new byte[1024];
			OutputStream os = response.getOutputStream();
 
			log.info("reportsUrl17 = ");
			while((read = baos.read(bytes))!= -1){
				os.write(bytes, 0, read);
			}
			log.info("reportsUrl18 = ");
			os.flush();
			os.close();
        }catch(Exception ex){
        	ex.printStackTrace();
        	log.info("reportsUrl19 = ");
        }
        
	}

	
	public void handleDownloadLumpPaymentSlipForm(Long assessmentIdL, Double amount, HttpServletRequest request, HttpServletResponse response, 
			String reportName, String outputFilePath, String fileName){
		IReportEngine reportEngine = null;
		 
        EngineConfig config = new EngineConfig();
        log.info("-----------------user dir is " + System.getProperty("user.dir"));
        String userDir = System.getProperty("user.dir");
        config.setLogConfig(userDir +sep+ "birtlogs", Level.SEVERE);
        config.setEngineHome(userDir +sep+ "ReportEngine");

        try {
                Platform.startup(config);
        } catch (BirtException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
        }

        IReportEngineFactory iReportEngineFactory = (IReportEngineFactory) Platform
                        .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
        reportEngine = iReportEngineFactory.createReportEngine(config);

        String reportsUrl = userDir +sep+"ReportEngine"+sep+"Reports";
        
        log.info("reportsUrl = "+reportsUrl);
        
        IReportRunnable runnableReport = null; 
        log.info("reportsUrl1 = ");
        try {
                runnableReport = reportEngine.openReportDesign(reportsUrl + sep+""+reportName);
                log.info("reportsUrl2 = ");
        } catch (EngineException e1) {
                // TODO Auto-generated catch block
        	log.info("reportsUrl3 = ");
                e1.printStackTrace();
                
        }
        
        log.info("reportsUrl4 = ");
        IRunAndRenderTask runAndRenderTask = reportEngine.createRunAndRenderTask(runnableReport);
        log.info("reportsUrl5 = ");
        PDFRenderOption renderOption = new PDFRenderOption();
        log.info("reportsUrl6 = ");
        renderOption.setOutputFileName(reportsUrl + sep+""+ outputFilePath+sep+""+fileName);
        log.info("reportsUrl7 = ");
        renderOption.setOutputFormat(IRenderOption.OUTPUT_FORMAT_PDF);
        log.info("reportsUrl8 = ");
       
        runAndRenderTask.setRenderOption(renderOption);
        log.info("reportsUrl9 = ");
        //runAndRenderTask.setRenderOption(htmlRenderOption);
        runAndRenderTask.setParameterValue("assessmentId", assessmentIdL);
        runAndRenderTask.setParameterValue("total_amt", amount);
        log.info("reportsUrl10 = ");
        try {
			runAndRenderTask.run();
			log.info("reportsUrl11 = ");
		} catch (EngineException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			log.info("reportsUrl12 = ");
		}
        File file = null;
        try{
        	log.info("reportsUrl13 = ");
        	file = new File(reportsUrl + sep+""+ outputFilePath+sep+""+fileName);
        	log.info("File Name ==" + reportsUrl + sep+""+ outputFilePath+sep+""+fileName);
        	log.info("reportsUrl13 = ");
        	FileInputStream baos = new FileInputStream(file);
        	log.info("reportsUrl14 = ");
			response.setContentType("application/pdf");
			log.info("reportsUrl15 = ");
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			log.info("reportsUrl16 = ");
			
 
			int read=0;
			byte[] bytes = new byte[1024];
			OutputStream os = response.getOutputStream();
 
			log.info("reportsUrl17 = ");
			while((read = baos.read(bytes))!= -1){
				os.write(bytes, 0, read);
			}
			log.info("reportsUrl18 = ");
			os.flush();
			os.close();
        }catch(Exception ex){
        	ex.printStackTrace();
        	log.info("reportsUrl19 = ");
        }
        
	}
	
	
	public void handleDownloadWFPaymentSlipForm(String workFlowRefId, Double amount, HttpServletRequest request, HttpServletResponse response, 
			String reportName, String outputFilePath, String fileName){
		IReportEngine reportEngine = null;
		 
        EngineConfig config = new EngineConfig();
        log.info("-----------------user dir is " + System.getProperty("user.dir"));
        String userDir = System.getProperty("user.dir");
        config.setLogConfig(userDir +sep+ "birtlogs", Level.SEVERE);
        config.setEngineHome(userDir +sep+ "ReportEngine");

        try {
                Platform.startup(config);
        } catch (BirtException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
        }

        IReportEngineFactory iReportEngineFactory = (IReportEngineFactory) Platform
                        .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
        reportEngine = iReportEngineFactory.createReportEngine(config);

        String reportsUrl = userDir +sep+"ReportEngine"+sep+"Reports";
        
        log.info("reportsUrl = "+reportsUrl);
        
        IReportRunnable runnableReport = null; 
        log.info("reportsUrl1 = ");
        try {
                runnableReport = reportEngine.openReportDesign(reportsUrl + sep+""+reportName);
                log.info("reportsUrl2 = ");
        } catch (EngineException e1) {
                // TODO Auto-generated catch block
        	log.info("reportsUrl3 = ");
                e1.printStackTrace();
                
        }
        
        log.info("reportsUrl4 = ");
        IRunAndRenderTask runAndRenderTask = reportEngine.createRunAndRenderTask(runnableReport);
        log.info("reportsUrl5 = ");
        PDFRenderOption renderOption = new PDFRenderOption();
        log.info("reportsUrl6 = ");
        renderOption.setOutputFileName(reportsUrl + sep+""+ outputFilePath+sep+""+fileName);
        log.info("reportsUrl7 = ");
        renderOption.setOutputFormat(IRenderOption.OUTPUT_FORMAT_PDF);
        log.info("reportsUrl8 = ");
       
        runAndRenderTask.setRenderOption(renderOption);
        log.info("reportsUrl9 = ");
        //runAndRenderTask.setRenderOption(htmlRenderOption);
        runAndRenderTask.setParameterValue("workFlowRefId", workFlowRefId);
        runAndRenderTask.setParameterValue("total_amt", amount);
        log.info("reportsUrl10 = ");
        try {
			runAndRenderTask.run();
			log.info("reportsUrl11 = ");
		} catch (EngineException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			log.info("reportsUrl12 = ");
		}
        File file = null;
        try{
        	log.info("reportsUrl13 = ");
        	file = new File(reportsUrl + sep+""+ outputFilePath+sep+""+fileName);
        	log.info("File Name ==" + reportsUrl + sep+""+ outputFilePath+sep+""+fileName);
        	log.info("reportsUrl13 = ");
        	FileInputStream baos = new FileInputStream(file);
        	log.info("reportsUrl14 = ");
			response.setContentType("application/pdf");
			log.info("reportsUrl15 = ");
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			log.info("reportsUrl16 = ");
			
 
			int read=0;
			byte[] bytes = new byte[1024];
			OutputStream os = response.getOutputStream();
 
			log.info("reportsUrl17 = ");
			while((read = baos.read(bytes))!= -1){
				os.write(bytes, 0, read);
			}
			log.info("reportsUrl18 = ");
			os.flush();
			os.close();
        }catch(Exception ex){
        	ex.printStackTrace();
        	log.info("reportsUrl19 = ");
        }
        
	}
	
	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		log.info("in doPost of passport servlet");
	}

}
