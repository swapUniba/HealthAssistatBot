package com.triage.rest;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class TriageServletContextListener implements ServletContextListener{

	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("Stopping server");
	}

	public void contextInitialized(ServletContextEvent arg0) {
		/*PoolProperties p = new PoolProperties();
        p.setUrl("jdbc:mysql://localhost/triagebot");
        p.setDriverClassName("com.mysql.jdbc.Driver");
        p.setUsername("triagebot");
        p.setPassword("triageb0t");
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(100);
        p.setInitialSize(10);
        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(60);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMinIdle(10);
        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);
        DataSource datasource = new DataSource();
        datasource.setPoolProperties(p);*/
		System.out.println("Starting server");
	}

}
