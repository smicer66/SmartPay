package com.probase.smartpay.commins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.sf.primepay.smartpay13.HibernateUtils;

public class AbstractPrbCustomService {

  private static SessionFactory sessionFactory = null;

  private static Logger log = Logger.getLogger(AbstractPrbCustomService.class);

  static {
    try { sessionFactory = HibernateUtils.getSessionFactory();
    } catch (Exception localException)
    {
    }
  }
  
  public static void setSessionFactory(SessionFactory sessionFactory)
  {
	  AbstractPrbCustomService.sessionFactory = sessionFactory;  
  }

  public <T> T executeQueryUniqueResult(String query, Object param) {
    List paramList = new ArrayList();
    paramList.add(param);
    return executeQueryUniqueResult(query, paramList);
  }

  public <T> T executeQueryUniqueResult(String query, List paramsList) {
    Query q = getSession().createQuery(query);
    for (int i = 0; i < paramsList.size(); i++) {
      q.setParameter(i, paramsList.get(i));
    }
    return (T)q.uniqueResult();
  }

  public <T> List<T> executeQuery(String query, Object param) {
    List paramList = new ArrayList();
    paramList.add(param);
    return executeQuery(query, paramList);
  }

  public <T> List<T> executeQuery(String query, Object param, int start, int size)
  {
    List paramList = new ArrayList();
    paramList.add(param);
    return executeQuery(query, paramList, start, size);
  }

  public <T> List<T> executeQuery(String query, List paramsList) {
    List entityList = new ArrayList();
    try {
      Query q = getSession().createQuery(query);
      for (int i = 0; i < paramsList.size(); i++) {
        q.setParameter(i, paramsList.get(i));
      }
      entityList = q.list();
    } catch (Exception ex) {
      log.error("", ex);
    }
    return entityList;
  }

  public <T> List<T> executeQuery(String query, List paramsList, int start, int size)
  {
    List entityList = new ArrayList();
    try {
      Query q = getSession().createQuery(query);
      for (int i = 0; i < paramsList.size(); i++) {
        q.setParameter(i, paramsList.get(i));
      }
      q.setFirstResult(start);
      q.setMaxResults(size);
      entityList = q.list();
    } catch (Exception ex) {
      log.error("", ex);
    }
    return entityList;
  }

  public <T> List<T> executeQuery(String query, Map<String, Object> paramsMap) {
    List entityList = new ArrayList();
    try {
      Query q = getSession().createQuery(query);
      for (String key : paramsMap.keySet()) {
        q.setParameter(key, paramsMap.get(key));
      }
      entityList = q.list();
    } catch (Exception ex) {
      log.error("", ex);
    }
    return entityList;
  }

  public <T> List<T> executeQuery(String query) {
    List entityList = new ArrayList();
    try {
      Query q = getSession().createQuery(query);
      entityList = q.list();
    } catch (Exception ex) {
      log.error("", ex);
    }
    return entityList;
  }

  public <T> List<T> executeQuery(String query, Map<String, Object> paramsMap, int start, int size)
  {
    List entityList = new ArrayList();
    try {
      Query q = getSession().createQuery(query);
      for (String key : paramsMap.keySet()) {
        q.setParameter(key, paramsMap.get(key));
      }
      q.setFirstResult(start);
      q.setMaxResults(size);
      entityList = q.list();
    } catch (Exception ex) {
      log.error("", ex);
    }
    return entityList;
  }
  
  
  public java.lang.Object createNewRecord(java.lang.Object object)
  {
	  Session session = null;
	  Transaction tx = null;
      try{			
			session = getSession();			
			tx = session.beginTransaction();
			java.lang.Object newRecord = session.save(object);
			tx.commit();
			return newRecord;
	  }catch(Exception e)
	  {
		  e.printStackTrace();
		  return null;
	  }finally
      {
        	if (session != null)
			{
	      		try
					{
	      			session.close();
	      		}catch (HibernateException he)
	      		{
	      			he.printStackTrace();
	            }
			}
      }
  }
  
  
  public void updateRecord(Object object)
  {
	  Session session = null;
	  Transaction tx = null;
      try{			
			session = getSession();			
			tx = session.beginTransaction();
			session.saveOrUpdate(object);
			tx.commit();
	  }catch(Exception e)
	  {
		  e.printStackTrace();
	  }finally
      {
          	if (session != null)
			{
	      		try
					{
	      			session.close();
	      		}catch (HibernateException he)
	      		{
	      			he.printStackTrace();
	            }
			}
      }
  }
  
  public void deleteRecord(Object object)
  {
	  Session session = null;
	  Transaction tx = null;
      try{			
			session = getSession();			
			tx = session.beginTransaction();
			session.delete(object);
			tx.commit();
	  }catch(Exception e)
	  {
		  e.printStackTrace();
	  }finally
      {
        	if (session != null)
			{
	      		try
					{
	      			session.close();
	      		}catch (HibernateException he)
	      		{
	      			he.printStackTrace();
	            }
			}
      }
  }
  
  

  public Session getSession() {
    Session session = null;

    return sessionFactory.openSession();
  }
}
