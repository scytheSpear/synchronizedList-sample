package com.itsys.portLink.JDBC;

import com.itsys.portLink.entities.PortLink;
import org.apache.log4j.Logger;
import java.sql.*;

public class JdbcConnection {

    private static final String INSERT_SQL_STRING = "INSERT INTO t_port_relation(sourceIp,targetIp, sourcePort, targetPort, protocol,startDate,endDate, remark, relationId) values(?,?,?,?,?,?,?,?,?)";
    private C3P0ConnentionProvider c3p0ConnentionProvider;
    private static final Logger LOG = Logger.getLogger(JdbcConnection.class);

    public JdbcConnection() {
        this.c3p0ConnentionProvider = C3P0ConnentionProvider.getInstance();
    }

    public Connection getConnection() throws SQLException {
        return c3p0ConnentionProvider.getConnection();
    }

    public void insert(PortLink pl) throws SQLException {
        Connection c = getConnection();
        PreparedStatement pre = null;
        try {
            pre = c.prepareStatement(INSERT_SQL_STRING);
            pre.setString(1, pl.getSourceIp());
            pre.setString(2, pl.getTargetIp());
            pre.setInt(3, pl.getSourcePort());
            pre.setInt(4, pl.getTargetPort());
            pre.setString(5, pl.getProtocol());
            pre.setTimestamp(6, new Timestamp(pl.getStartTime().getTime()));
            pre.setTimestamp(7, new Timestamp(pl.getEndTime().getTime()));
            pre.setString(8, pl.getRemark());
            pre.setString(9, pl.getId());
            pre.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(pre);
            cl(c);

        }

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
