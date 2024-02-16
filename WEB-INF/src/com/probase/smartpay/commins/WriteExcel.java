package com.probase.smartpay.commins;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;



public class WriteExcel {
	private WritableCellFormat timesBoldUnderline;
	private WritableCellFormat times;
	private String inputFile;
	private ArrayList<String> labels;
	private String reportTitle;
	private ArrayList<ArrayList<String>> content;
	
	public static void main(String[] args) throws WriteException, IOException {
		
		ArrayList<String> label = new ArrayList<String>();
		label.add("T1");
		label.add("T2");
		label.add("T3");
		label.add("T4");
		
		
		ArrayList<ArrayList<String>> content  = new ArrayList<ArrayList<String>>();
		ArrayList<String> content1 = new ArrayList<String>();
		content1.add("A");
		content1.add("B");
		content1.add("C");
		content1.add("D");
		
		ArrayList<String> content2 = new ArrayList<String>();
		content2.add("E");
		content2.add("F");
		content2.add("G");
		content2.add("H");
		
		ArrayList<String> content3 = new ArrayList<String>();
		content3.add("I");
		content3.add("J");
		content3.add("K");
		content3.add("L");
		
		content.add(content1);
		content.add(content2);
		content.add(content3);
		
		
	    WriteExcel test = new WriteExcel("c:/jcodes/lars.xls", "Test10", label, content);
	    test.write();
	    System.out
	        .println("Please check the result file under c:/temp/lars.xls ");
	  }
	
	
	
	public WriteExcel(String inputFile, String reportTitle, ArrayList<String> labels, ArrayList<ArrayList<String>> contnet)
	{
		this.labels = labels;
		this.inputFile = inputFile;
		this.reportTitle = reportTitle;
		this.setOutputFile(inputFile);
		this.content = contnet;
	}
	
	public void setOutputFile(String inputFile) {
		this.inputFile = inputFile;
	}
	
	
	public void write() throws IOException, WriteException {
	    File file = new File(inputFile);
	    WorkbookSettings wbSettings = new WorkbookSettings();

	    wbSettings.setLocale(new Locale("en", "EN"));

	    WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
	    workbook.createSheet("Report", 0);
	    WritableSheet excelSheet = workbook.getSheet(0);
	    createLabel(excelSheet);
	    createContent(excelSheet);

	    workbook.write();
	    workbook.close();
	}
	
	
	private void createLabel(WritableSheet sheet)throws WriteException {
	    // Lets create a times font
	    WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
	    // Define the cell format
	    times = new WritableCellFormat(times10pt);
	    // Lets automatically wrap the cells
	    times.setWrap(true);
	
	    // create create a bold font with unterlines
	    
	    WritableFont times10ptBoldUnderline = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false,
	        UnderlineStyle.SINGLE, Colour.BLUE);
	    timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
	    // Lets automatically wrap the cells
	    timesBoldUnderline.setWrap(true);
	    
	
	    CellView cv = new CellView();
	   // cv.setFormat(times);
	    cv.setFormat(timesBoldUnderline);
	    
	    
	    
	    
	
	    // Write a few headers
	    int c = 0;
	    for(Iterator<String> iter = this.labels.iterator(); iter.hasNext();)
	    {
	    	String lbl = iter.next();
	    	addCaption(sheet, c++, 0, lbl);
	    }
		    

	}
	
	private void addCaption(WritableSheet sheet, int column, int row, String s) throws RowsExceededException, WriteException {
	    Label label;
	    label = new Label(column, row, s, timesBoldUnderline);
	    sheet.addCell(label);
	}
	
	private void addNumber(WritableSheet sheet, int column, int row, Integer integer) throws WriteException, RowsExceededException {
	    Number number;
	    number = new Number(column, row, integer, times);
	    sheet.addCell(number);
	}
	
	
	private void createContent(WritableSheet sheet) throws WriteException,
	    RowsExceededException {
		// Write a few number
//		for (int i = 1; i < 10; i++) {
//			// First column
//			addNumber(sheet, 0, i, i + 10);
//			// Second column
//		    addNumber(sheet, 1, i, i * i);
//		}
//		// Lets calculate the sum of it
//		StringBuffer buf = new StringBuffer();
//		buf.append("SUM(A2:A10)");
//		Formula f = new Formula(0, 10, buf.toString());
//		sheet.addCell(f);
//		buf = new StringBuffer();
//		buf.append("SUM(B2:B10)");
//		f = new Formula(1, 10, buf.toString());
//		sheet.addCell(f);
//		
//		  // now a bit of text
//		for (int i = 12; i < 20; i++) {
//		    // First column
//			addLabel(sheet, 0, i, "Boring text " + i);
//			// Second column
//			addLabel(sheet, 1, i, "Another text");
//		}
		
		
		int j =1;
		
		for(Iterator<ArrayList<String>> iter = this.content.iterator(); iter.hasNext();)
		{
			ArrayList<String> arrList = iter.next();

			int i=0;
			for(Iterator<String> iterA = arrList.iterator(); iterA.hasNext();)
			{
				String str = iterA.next();
				System.out.println("i=" + i + " & j =" + j + " & str = " + str );
				addLabel(sheet, i++, j, str);
			}
			j++;
		}
	}
	
	private void addLabel(WritableSheet sheet, int column, int row, String s) throws WriteException, RowsExceededException {
	    Label label;
	    label = new Label(column, row, s, times);
	    sheet.addCell(label);
	}
}
