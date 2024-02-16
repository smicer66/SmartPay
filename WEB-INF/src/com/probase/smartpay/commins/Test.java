package com.probase.smartpay.commins;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;

import com.microsoft.sqlserver.jdbc.SQLServerException;


public class Test {
	
	
	private static Map<String, Integer> holder = new HashMap<String, Integer>();
	private static Map<String, String> holder_list = new HashMap<String, String>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		
		int[] prizes = {580, 170, 80, 90, 89, 238, 23721, 101323, 23812, 1104, 34834, 29302, 23923, 200, 1120, 50, 100,200,400,1000};
		//int[] prizes = {100,800,200,500,400,1000}; 
		//int[] prizes = {580, 170, 80, 90, 200, 1120, 50, 100,200,400,1000};
		String[] winners = {"Joshua","Mahesh","Lilian"};
		//distribute(prizes, winners);r=
		
		System.out.println(new Util().roundUpAmount(60.2));
		
		runthis();
	}
	
	
	
	private static void runthis() {
		// TODO Auto-generated method stub
		try {
			System.out.println("test 1212");
	         Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	         Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1434;databaseName=portalasp;",
	                  "ecims_user", "k0l0zaq1ZAQ!");
	         
	         System.out.println("connected");
	         Statement statement = conn.createStatement();
	         String queryString = "select *, (Select c1.itemCategoryName from itemCategory c1 WHERE c1.itemCategoryID = c.ParentID) as e45 from ItemCategory c WHERE itemCategoryID > 8";		//FROM portalaspdb
	         ResultSet rs = statement.executeQuery(queryString);
	         

		        ArrayList a1 = new ArrayList();
	            ArrayList a2 = new ArrayList();
	            ArrayList a3 = new ArrayList();
	            
	         while (rs.next()) {
	            System.out.println(rs.getString(1));
//	            Statement st = conn1.createStatement();
	            String a = rs.getString(2);
	            String b = rs.getString(3);
	            String c = rs.getString("e45");
//	            String queryString1 = "select *, (Select c1.itemCategoryName from itemCategory c1 WHERE c1.id = c.ParentID) from itemcategory c WHERE c.itemCategoryID = " + rs.getString(4);
//	            System.out.println("queryString1 = " + queryString1);
	            
	            
//	            ResultSet rs1 = statement.executeQuery(queryString1);
//		        rs1.next();
//		        String rsName = rs1.getString(2);
	            a1.add(a);
	            a2.add(b);
	            a3.add(c);
	            //a3.add(rsName);
//		        System.out.println("INSERT INTO Item_Category_Sub " + "VALUES ('"+a+"', '"+b+"'" +
//	            		", (Select id from ItemCategory itc WHERE lower(itc.itemCategoryName) = lower('"+rsName.trim()+"')))");
	            
	            
//	            st.executeUpdate("INSERT INTO ItemCategorySub " + "VALUES ('"+rs.getString(2)+"', '"+rs.getString(3)+"'" +
//	            		", (Select id from ItemCategory itc WHERE itc.itemCategoryName = '"+rsName+"'))");
	         }
	         conn.close();
	         
	         Connection conn1 = DriverManager.getConnection("jdbc:sqlserver://localhost:1434;databaseName=ecims;",
	                  "ecims_user", "k0l0zaq1ZAQ!");
	         
	         Statement st = conn1.createStatement();
	         for(int c = 0; c<a1.size(); c++)
	         {
	        	 try
	        	 {
	        	 //st.executeUpdate
	        	 System.out.println("INSERT INTO Item_Category_Sub " + "VALUES ('"+a1.get(c)+"', '"+a2.get(c)+"'" +
		            		", (Select id from Item_Category itc WHERE itc.item_Category_Name = '"+a3.get(c)+"'))");
	        	 st.executeUpdate("INSERT INTO Item_Category_Sub " + "VALUES ('"+a1.get(c)+"', '"+a2.get(c)+"'" +
		            		", (Select id from Item_Category itc WHERE itc.item_Category_Name = '"+a3.get(c)+"'))");
	        	 }catch(SQLServerException e)
	        	 {
	        		 e.printStackTrace();
	         }
	         }
	         System.out.println("a.zie = " + a1.size() + " && b.zie=" + a2.size() + " && c.zie=" + a3.size());
	         conn1.close();
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
		
		
		
	}



	public void doNow()
	{
		String serverCertFile = "D:\\Development\\ws-config\\client_trust.der";
	    String clientKeyStore = "D:\\Development\\ws-config\\client_store.jks";
	    String clientKeyStorePass = "Leia";
	    String clientKeyAlias = "trust";
	    String clientKeyPass = "Leia";

	    // Create list of credential providers
//	    List<CredentialProvider> credProviders = new ArrayList<CredentialProvider>();
//
//	    X509Certificate serverCertInit = null;
//	    CredentialProvider cp = null;
//	    try {
//	      // Create a credential provider with the client indentity and the server certificate
//	      serverCertInit = (X509Certificate) CertUtils.getCertificate(serverCertFile);
//	      serverCertInit.checkValidity();
//
//	      cp = new ClientBSTCredentialProvider(clientKeyStore, clientKeyStorePass, clientKeyAlias, clientKeyPass, "JKS", serverCertInit);
//	    } catch (Exception e) {
//	      e.printStackTrace();
//	      System.exit(1);
//	    }
//
//	    credProviders.add(cp);
//
//	    // Finally add the credential providers to the request context
//	    Map<String, Object> requestContext = ((BindingProvider) port).getRequestContext();
//	    requestContext.put(WSSecurityContext.CREDENTIAL_PROVIDER_LIST, credProviders);
//
//	    List certificate = CertUtils.getCertificate(clientKeyStore, clientKeyStorePass, clientKeyAlias, "JKS");
//
//	    final X509Certificate clientCert = (X509Certificate) certificate.get(0);
//	    final X509Certificate serverCert = serverCertInit;
//
//	    // Setup the TrustManager to verify the signature on the returned message
//	    requestContext.put(WSSecurityContext.TRUST_MANAGER, new TrustManager() {
//	      public boolean certificateCallback(X509Certificate[] chain, int validateErr) {
//	        // Check the server and client cert
//	        boolean validServer = chain[0].equals(serverCert);
//	        System.out.println("Server cert valid: " + validServer);
//	        boolean validClient = chain[0].equals(clientCert);
//	        System.out.println("Client cert valid: " + validClient);
//
//	        return validClient ^ validServer;
//	      }
//	    });
	}
	
	
	
	
	/**
	 * This method assigns prize monies to winners of a lottery
	 * in the fairest manner possible.
	 * This method takes the following process to assign the amounts:
	 * Start
	 * 1) 	Assign each of the winner a prize amount from the prize monies
	 * 2) 	The amount assigned for each of the winners is the highest set of amounts
	 * 		from the pool of prize monies with each winner only getting a single prize money.
	 * 		This implies that if there are n-number of winners, the top-n prize money will be 
	 * 		assigned to the winners with each winner getting a single prize money.
	 * 3)	Loop through the next set of instructions (4-8) until the monies in the prize 
	 * 		pool have all been allotted
	 * 4)	Get the next highest amount in the pool of prizes (X)
	 * 5)	Check to see if by adding (X) to a winner-N (other than the winner with the current 
	 * 		highest amount - winner-M), the winner-N's new allocated amount is more than the 
	 * 		winner-M's allocated amount.
	 * 6)	If step 5 yields a positive answer, skip and go to the next winner (winner-P).
	 * 7)	For winner-P in step-6 if step 5 yields a negative answer then add current prize
	 * 		amount to winner-P's current winning prize
	 * 8)   However if step 5 yields a positive answer for all the other winners asides winner-M, 
	 * 		then add current prize amount to winner-M's current winning prize and continue with the loop.
	 * 9) 	Print out the prize amounts assigned to each winner and their total amounts assigned
	 * End  
	 * 
	 * @param prizes: Array of prize amounts
	 * @param winners: Array of prize winners
	 */
	public static void distribute(int[] prizes, String[] winners)
	{
		
		System.out.println("Start----------------------------");
		String[] winnersRandom = shuffleArray(winners);
		
		System.out.println("List of winners in random order            \t\t= " + Arrays.toString(winnersRandom));
		
		Arrays.sort(prizes);
		System.out.println("Prizes to be allocated sorted order         \t\t= " + Arrays.toString(prizes));
		
		boolean proceed = true;
		stepOne(prizes, winnersRandom);
		for(int m=prizes.length-winnersRandom.length-1; m>-1; m--)
		{
			
			int max1 = Collections.max(Test.holder.values());
			
			proceed= true;
			System.out.println("");
			System.out.println("Current prize to allocate              	\t\t= " + prizes[m]);
			System.out.println("Current Maximum in shared winnings pool	\t\t= " + max1);
			System.out.println("Current winnings pool					\t\t");
			System.out.println("----------------------------------------\t\t");
			Iterator<String> it1 = Test.holder.keySet().iterator();
			while(it1.hasNext())
			{
				String key = it1.next();
				System.out.println("Winner = " + key + "	|\t Current Amount Allocated = " + Test.holder.get(key) + "	|\t Winnings = " + Test.holder_list.get(key));
				
			}
			System.out.println("----------------------------------------\t\t");
			
			it1 = Test.holder.keySet().iterator();
			while(it1.hasNext())
			{
				String key = it1.next();
				
				if(Test.holder.get(key)==max1)
				{
					
				}else
				{
					if(Test.holder.get(key) + prizes[m]>max1)
					{
						
					}else
					{
						//OK, Add (current prize to allocate) to a winner
						Integer val = Test.holder.get(key);
						String val_list = Test.holder_list.get(key);
						val += prizes[m];
						val_list += ", " + prizes[m];
						Test.holder.put(key, val);
						Test.holder_list.put(key, val_list);
						proceed = false;
					}
				}
				
				if(proceed==false)
					break;
			}
			
			
			
			it1 = Test.holder.keySet().iterator();
			String key = "";
			String keyMax = null;
			while(it1.hasNext())
			{
				key = it1.next();
				max1 = Collections.max(Test.holder.values());
				if(Test.holder.get(key).equals(max1))
				{
					keyMax = key;
				}
			}

			if(proceed)
			{
				Integer val = Test.holder.get(keyMax);
				String val_list = Test.holder_list.get(keyMax);
				val += prizes[m];
				val_list += ", " + prizes[m];
				Test.holder.put(keyMax, val);
				Test.holder_list.put(keyMax, val_list);
				proceed = false;
			}
			
			
		}
			
		
		
		System.out.println("Final Prize Allocation>>>");
		Iterator<String> it1 = Test.holder.keySet().iterator();
		String key = "";
		while(it1.hasNext())
		{
			key = it1.next();
			System.out.println(key + ": " + Test.holder_list.get(key));
			
		}
	}
	
	
	
	
	/**
	 * This method assigns each of the winner a prize amount.
	 * Prize amounts assigned are chosen from the maximum set of 
	 * prizes in the prize array with each winner getting only 
	 * ONE prize amount
	 * @param prizes: Array of prizes
	 * @param winnersRandom: Array of winners
	 */
	private static void stepOne(int[] prizes, String[] winnersRandom) {
		// TODO Auto-generated method stub
		int pz = prizes.length-1;
		for(int i=0; i<winnersRandom.length; i++)
		{
			Test.holder.put(winnersRandom[i], prizes[pz]);
			Test.holder_list.put(winnersRandom[i], Integer.toString(prizes[pz--]));
		}
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


