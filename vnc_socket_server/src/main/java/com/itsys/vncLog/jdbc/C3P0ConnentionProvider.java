package com.itsys.vncLog.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import com.mchange.v2.c3p0.DataSources;
import java.util.Iterator;
import org.apache.log4j.Logger;

public class C3P0ConnentionProvider
{
  private static final String JDBC_DRIVER = "driverClass";
  private static final String JDBC_URL = "jdbcUrl";
  private static final Logger log = Logger.getLogger(C3P0ConnentionProvider.class);
  private static DataSource ds;
  private static C3P0ConnentionProvider instance = null;

  private static final void initDBSource()
  {
    Properties prop = new Properties();
    try
    {
      prop.load(C3P0ConnentionProvider.class.getResourceAsStream("/META-INF/c3p0.properties"));
    } catch (Exception e) {
      e.printStackTrace();
    }

    String drverClass = prop.getProperty("driverClass");
    if (drverClass != null) {
      try
      {
        Class.forName(drverClass);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }

    }

    Properties jdbcpropes = new Properties();
    Properties c3propes = new Properties();
    for (Iterator i$ = prop.keySet().iterator(); i$.hasNext(); ) { Object key = i$.next();
      String skey = (String)key;
      if (skey.startsWith("c3p0."))
        c3propes.put(skey, prop.getProperty(skey));
      else {
        jdbcpropes.put(skey, prop.getProperty(skey));
      }
    }

    try
    {
      DataSource unPooled = DataSources.unpooledDataSource(prop.getProperty("jdbcUrl"), jdbcpropes);

      ds = DataSources.pooledDataSource(unPooled, c3propes);
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public synchronized Connection getConnection()
    throws SQLException
  {
    Connection conn = ds.getConnection();
    conn.setTransactionIsolation(2);
//    System.out.println("get c3p0 connection :Thread name:" + Thread.currentThread().getName());
    return conn;
  }

  public static C3P0ConnentionProvider getInstance() {
    if (instance == null) {
//      System.out.println("C3P0ConnentionProvider  is  null");
      instance = new C3P0ConnentionProvider();
    }
//    System.out.println("C3P0ConnentionProvider  is not null");
    return instance;
  }

  static
  {
    initDBSource();
  }
}