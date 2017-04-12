package com.itsys.vncLog.jdbc;

import com.itsys.vncLog.entities.VncRelation;
import org.apache.log4j.Logger;
import java.util.Date;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;

public class JdbcConnection {

    private static final String INSERT_SQL_STRING = "INSERT INTO t_vnc_relation(clientIp,serverIp,protocol,startTime,endTime,relationId) values(?,?,?,?,?,?)";
    private static final String UPDATE_SQL_STRING = "UPDATE t_vnc_relation SET endTime = ? WHERE clientIp = ? AND serverIp = ? AND endTime = ? AND relationId = ?";
    private C3P0ConnentionProvider c3p0ConnentionProvider;
//    private ConnectionPool pool;
    private static final Logger LOG = Logger.getLogger(JdbcConnection.class);

    public JdbcConnection() {
        this.c3p0ConnentionProvider = C3P0ConnentionProvider.getInstance();
    }

    public Connection getConnection() throws SQLException {
        return c3p0ConnentionProvider.getConnection();
    }

    public Date TimeConvert(String s) throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return simpleDateFormat.parse(s);
    }

//    public ConnectionPool getPool(){
//        return pool;
//    }
    public String insert(VncRelation relation) throws SQLException {
        Connection c = getConnection();
        PreparedStatement pre = null;
        String sqlString = "";

        try {
            pre = c.prepareStatement(INSERT_SQL_STRING);
            pre.setString(1, relation.getClientIp().split(":")[0]);
            pre.setString(2, relation.getServerIp().split(":")[0]);
            pre.setString(3, "vnc");
            pre.setTimestamp(4, new Timestamp(TimeConvert(relation.getStartTime()).getTime()));
            pre.setTimestamp(5, null);
            pre.setString(6, relation.getId());
            pre.execute();

            sqlString = pre.unwrap(PreparedStatement.class).toString().split(":", 2)[1].trim();
            System.out.println(sqlString);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(JdbcConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            close(pre);
            cl(c);

        }

        return sqlString;
    }
    
    public String update(VncRelation relation) throws SQLException {
        Connection c = getConnection();
        PreparedStatement pre = null;
        String sqlString = "";

        try {
            pre = c.prepareStatement(UPDATE_SQL_STRING);
            pre.setTimestamp(1, new Timestamp(TimeConvert(relation.getEndTime()).getTime()));
            pre.setString(2, relation.getClientIp());
            pre.setString(3, relation.getServerIp());
            pre.setTimestamp(4, new Timestamp(TimeConvert(relation.getEndTime()).getTime()));
            pre.setString(5, relation.getId());
            pre.executeUpdate();

            sqlString = pre.unwrap(PreparedStatement.class).toString().split(":", 2)[1].trim();
            System.out.println(sqlString);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(JdbcConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(pre);
            cl(c);
        }

        return sqlString;
    }
    

    public void close(PreparedStatement pre) {
        try {
            if (pre != null) {
                pre.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cl(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
