package com.itsys.portLink.JDBC;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.mchange.v2.c3p0.DataSources;
import org.apache.log4j.Logger;

/**
 * Created by Administrator on 2016/5/31.
 */
public class C3P0ConnentionProvider {
    private static final String JDBC_DRIVER = "driverClass";
    private static final String JDBC_URL = "jdbcUrl";
    private static final Logger LOG = Logger.getLogger(C3P0ConnentionProvider.class);
    private static DataSource ds;
    private static C3P0ConnentionProvider instance = null;


    static {

        initDBSource();
    }

    private static final void initDBSource() {
        Properties prop = new Properties();
        try {

         prop.load(C3P0ConnentionProvider.class.getResourceAsStream("/META-INF/jdbc.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String drverClass = prop.getProperty(JDBC_DRIVER);
        if (drverClass != null) {
            try {
                Class.forName(drverClass);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        Properties jdbcpropes = new Properties();
        Properties c3propes = new Properties();
        for (Object key : prop.keySet()) {
            String skey = (String) key;
            if (skey.startsWith("c3p0.")) {
                c3propes.put(skey, prop.getProperty(skey));
            } else {
                jdbcpropes.put(skey, prop.getProperty(skey));
            }
        }

        try {
            DataSource unPooled = DataSources.unpooledDataSource(prop.getProperty(JDBC_URL), jdbcpropes);

            ds = DataSources.pooledDataSource(unPooled, c3propes);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public  synchronized Connection getConnection() throws SQLException {
        final Connection conn = ds.getConnection();
        conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        return conn;
    }

    public  static  C3P0ConnentionProvider getInstance(){
        if (instance == null) {
            instance = new C3P0ConnentionProvider();
        }
        return instance;
    }
}
