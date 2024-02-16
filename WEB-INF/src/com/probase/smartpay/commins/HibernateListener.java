package com.probase.smartpay.commins;


import java.io.PrintStream;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;

import com.sf.primepay.smartpay13.HibernateUtils;


public class HibernateListener implements ServletContextListener
{
  public void contextDestroyed(ServletContextEvent evt)
  {
    try
    {
      SessionFactory sf = (SessionFactory)evt.getServletContext().getAttribute("sf");
      if (sf != null) {
        sf.close();
        System.out.println("Hibernate Session Closed successfully");
      }
    }
    catch (HibernateException e) {
      System.out.println("Error closing Hibernate Session");

      e.printStackTrace();
    } catch (Exception e) {
      System.out.println("Error closing Hibernate Session");

      e.printStackTrace();
    }
  }

  public void contextInitialized(ServletContextEvent evt)
  {
    try
    {
      SessionFactory sf = HibernateUtils.getSessionFactory();
      evt.getServletContext().setAttribute("sf", sf);
      System.out.println("Hibernate Session started successfully");
    }
    catch (Exception e) {
      System.out.println("Error occured while trying to start Hibernate Session.");

      e.printStackTrace();
    }
  }
}
