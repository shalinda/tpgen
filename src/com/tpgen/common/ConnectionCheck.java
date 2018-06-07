package com.tpgen.common;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: shalinda
 * Date: 4/19/11
 * Time: 10:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionCheck {

    private static Logger log = Logger.getLogger(ConnectionCheck.class);

    public static void main(String[] args) {
        Connection cn;
        try {
            Class.forName("com.tpgen.common.Global").newInstance();
        } catch (Exception e) {
        }
        Properties config = Global.getConfig();
        int s = 0;
        try {
            cn = Global.connect();
            s = Integer.parseInt(Global.getConfig().getProperty("s"));
        } catch (FrameworkException e) {
            log.error("Initial connection failed" ,e);
            return;
        }
        while (true) {
            try {
                PreparedStatement ps = cn.prepareStatement("select 1 from dual");
                ResultSet rs = ps.executeQuery();
                if (rs.next())
                    log.debug("response from db " + rs.getInt(1));
                else
                    log.error("fatal no record");
                rs.close();
                ps.close();
                try {
                    log.debug("sleep 1 min");
                    Thread.sleep(1000 * s);
                } catch (InterruptedException e) {
                }
            } catch (SQLException e) {
                log.error("encountered SQL Error", e);
                if (cn != null) { // close connection if possible
                    try {
                        Global.close();
                    } catch (FrameworkException e1) {
                        log.error(e1, e1);
                    }
                }
                try {
                    cn = Global.connect();
                } catch (FrameworkException e2) {
                    log.error("Recovering connection failed sleep 1 min" ,e2);
                    try {
                        Thread.sleep(1000 * s);
                    } catch (InterruptedException e3) {
                    }
                }
            }
        }
    }
}
