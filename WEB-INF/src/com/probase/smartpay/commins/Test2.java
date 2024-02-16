package com.probase.smartpay.commins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Test2 {

	private static String prn;
	private static String tpin;
	private static String tp_name;
	private static String amnt;
	private static String prn_dt;
	private static String status;
	private static String acc_no;
	private static String mid;
	private static String prn_exp_dt;
	
	private static Map<String, Integer> holder = new HashMap<String, Integer>();
	private static Map<String, String> holder1 = new HashMap<String, String>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

//		Mailer emailer = new Mailer();
//		List<String> assessmentlistString = new ArrayList<String>();
//		assessmentlistString.add("26");
//		assessmentlistString.add("25");
//		SendMail sm = emailer.emailWorkFlow("smicer66@gmail.com", 
//				"Ten Inc",
//				assessmentlistString,
//				"3283jsdf", 
//				"http://www.stanbicibtc.com.zm",
//				"James", 
//				"Asuzu", 
//				"Work Flow Item - Request for Approval of Payment for Assessment - ");
		
		
		//new Util().checkmate();
		String encdata = "prn=113002191940|tpin=1002094680|tp_name=ORMA CONSTRUCTION " +
				"LIMITED|amnt=1111.00|prn_dt=20140825|status=A|prn_exp_dt=20140904|" +
				"acc_no=0|mid=ZRA_TXO";
		
		
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("reasoncode", 0);
			jsonObject.put("reasonDescription", "SUCCESS");
			jsonObject.put("source", "SCB");
			jsonObject.put("sourceID", "SCB-9210231391");
			jsonObject.put("timestamp", "2014-08-31 12:22:21:312");
			jsonObject.put("tpin_declarantCode", "1209301911");
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonObject1 = new JSONObject();
			jsonObject1.put("amountToBePaid", 2312.09);
			jsonObject1.put("assessmentNumber", "341");
			jsonObject1.put("assessmentYear", "2014");
			jsonObject1.put("portOfEntry", "NKO");
			jsonObject1.put("registrationDate", "2014-02-20");
			jsonObject1.put("registrationNumber", "341");
			jsonObject1.put("registrationSerial", "C");
			jsonObject1.put("clientTaxPayerIdentification", "8932188721");
			jsonObject1.put("declarantCode", "1209301911");
			

			JSONObject jsonObject2 = new JSONObject();
			jsonObject2.put("amountToBePaid", 2532.09);
			jsonObject2.put("assessmentNumber", "343");
			jsonObject2.put("assessmentYear", "2014");
			jsonObject2.put("portOfEntry", "NKO");
			jsonObject2.put("registrationDate", "2014-02-20");
			jsonObject2.put("registrationNumber", "343");
			jsonObject2.put("registrationSerial", "C");
			jsonObject2.put("clientTaxPayerIdentification", "8932188721");
			jsonObject2.put("declarantCode", "1209301911");
			
			jsonArray.put(jsonObject1);
			jsonArray.put(jsonObject2);
			jsonObject.put("assessmentDetailsList", jsonArray);
			
			System.out.println(jsonObject.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if(encdata!=null && encdata.length()>0)
		{
			String[] breakData = encdata.split("\\|");
			System.out.println(breakData.length);
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
		
		
		int[] prizes = {580, 170, 80, 90, 200, 1120, 50, 100,200,400,1000};
		String[] winners = {"Joshua","Mahesh","Lilian"};
		distribute(prizes, winners);
	}
	
	
	
	
	public static void distribute(int[] prizes, String[] winners)
	{
		
		System.out.println("----------------------------");
		String[] winnersRandom = shuffleArray(winners);
		
		
		String[] winnings = new String[winnersRandom.length];
		int[] winningsTotal = new int[winnersRandom.length];
		
		
		System.out.println(Arrays.toString(winnersRandom));
		
		Arrays.sort(prizes);
		System.out.println(Arrays.toString(prizes));
		int j = 0;
		int i=(prizes.length - 1);
		int k = winningsTotal.length;
		boolean proceed = true;
		winningsTotal = stepOne(winningsTotal, prizes, winnersRandom);
		
		Arrays.sort(winningsTotal);
		Collections.sort(new ArrayList(Test2.holder.values()));
		Iterator<String> it = Test2.holder.keySet().iterator();
		while(it.hasNext())
		{
			String key = it.next();
			System.out.println("key = " + key + " && value = " + Test2.holder.get(key));
			
		}
		
		System.out.println("--" + Arrays.toString(winningsTotal));
		//while(!proceed)
		//{
			
			
//			if(i%(winnings.length)==0)
//			{
//				j=0;
//			}
//			if()
//			winnings[j] += Integer.toString(prizes[i]) + ", ";
//			winningsTotal[j] += prizes[i];
//			j++;
		for(int m=prizes.length-winningsTotal.length-1; m>-1; m--)
		{
			Arrays.sort(winningsTotal);
			Collections.sort(new ArrayList(Test2.holder.values()));
			int max = winningsTotal[winningsTotal.length-1];
			int max1 = Collections.max(Test2.holder.values());
			
			
			
			proceed= true;
			System.out.println("m=" + m + " ==> " + prizes[m]);
			System.out.println("==============");
//			System.out.println("Before>>>" + Arrays.toString(winningsTotal));
			System.out.println("Before>>>");
			Iterator<String> it1 = Test2.holder.keySet().iterator();
			while(it1.hasNext())
			{
				String key = it1.next();
				System.out.println("key = " + key + " && value = " + Test2.holder.get(key));
				
			}
			
			it1 = Test2.holder.keySet().iterator();
			while(it1.hasNext())
			{
				String key = it1.next();
				System.out.println("key = " + key + " && value = " + Test2.holder.get(key));
				if(Test2.holder.get(key) + prizes[m]>max && Test2.holder.get(key)!=max)
				{
					System.out.println("Skip");
				}else
				{
					Integer val = Test2.holder.get(key);
					val += prizes[m];
					Test2.holder.put(key, val);
					proceed = false;
					System.out.println("Add");
				}
				
				if(proceed==false)
					break;
			}
			
			
			System.out.println("Mid>>>");
			it1 = Test2.holder.keySet().iterator();
			String key = "";
			while(it1.hasNext())
			{
				key = it1.next();
				System.out.println("key = " + key + " && value = " + Test2.holder.get(key));
				
			}

			if(proceed)
			{
				winningsTotal[winningsTotal.length-1] += prizes[m];
				Integer val = Test2.holder.get(key);
				val += prizes[m];
				Test2.holder.put(key, val);
				proceed = false;
			}
			
			System.out.println("After>>>");
			it1 = Test2.holder.keySet().iterator();
			key = "";
			while(it1.hasNext())
			{
				key = it1.next();
				System.out.println("key = " + key + " && value = " + Test2.holder.get(key));
				
			}
			
			
			for(int n=winningsTotal.length-2; ((n>-1)); n--)
			{
				System.out.println("n=" + n);
				if(winningsTotal[n] + prizes[m]>max)
				{
					System.out.println("Skip");
				}else
				{
					winningsTotal[n] += prizes[m];
					proceed = false;
					System.out.println("Add");
				}
				
				if(proceed==false)
					break;
				
			}System.out.println("Mid>>>" + Arrays.toString(winningsTotal));
			
			if(proceed)
			{
				winningsTotal[winningsTotal.length-1] += prizes[m];
				proceed = false;
			}System.out.println("After>>>" + Arrays.toString(winningsTotal));
		}
			
		System.out.println(Arrays.toString(winningsTotal));
		
		
		System.out.println("Final>>>");
		Iterator<String> it1 = Test2.holder.keySet().iterator();
		String key = "";
		while(it1.hasNext())
		{
			key = it1.next();
			System.out.println("key = " + key + " && value = " + Test2.holder.get(key));
			
		}
	}
	
	
	private static int[] stepOne(int[] winningsTotal, int[] prizes, String[] winnersRandom) {
		// TODO Auto-generated method stub
		int pz = prizes.length-1;
		for(int i=0; i<winningsTotal.length; i++)
		{
			Test2.holder.put(winnersRandom[i], prizes[pz]);
			Test2.holder1.put(winnersRandom[i], Integer.toString(prizes[pz]));
			winningsTotal[i] = prizes[pz--];
			
			
		}
		return winningsTotal;
	}




	static String[] shuffleArray(String[] winners)
	{
		String[] winnersRandom = new String[winners.length];
	    Random rnd = new Random();
	    int c=0;
    	boolean proceed = true;
    	while(proceed)
	    {
	    	
    		int index = rnd.nextInt(winners.length);
    		if(Arrays.asList(winnersRandom).contains(winners[index]))
    		{
    			
    		}else
    		{
	    		String a = winners[index];
	    		winnersRandom[c++] = a;
	    		if(c==winners.length)
	    			proceed = false;
    		}
	    	
	    }
	    return winnersRandom;
	}

}
