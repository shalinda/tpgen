package com.tpgen.common;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.util.Properties;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

/**
 * Conducts global configuration for the framework.
 *
 * @author Shalinda Ranasinghe
 * @version 1.0
 */
public class Global {

    private static final String CONFIG_FILE = "config.properties";
    private static Properties config = new Properties();
    private static Logger log = Logger.getLogger(Global.class);

    private static final String JDBC_DRIVER = "jdbc.driver";
    private static final String DB_CONNECTION_STRING = "db.connection.string";
    private static final String DB_USER = "db.user";
    private static final String DB_PASSWORD = "db.password";

    private static Connection con;

    /**
     * Configiration
     * @return Configaration
     */
    public static Properties getConfig() {
        return config;
    }

    {
         try {
             config.load(new FileInputStream(CONFIG_FILE));
             log.debug("Loaded " + CONFIG_FILE);
        } catch (Exception e) {
            log.fatal("Configuration issue " + CONFIG_FILE, e);
        }
    }

    public static Connection connect() throws FrameworkException {
        String driver = config.getProperty(JDBC_DRIVER);
        String constr = config.getProperty(DB_CONNECTION_STRING);
        String user = config.getProperty(DB_USER);
        String password = config.getProperty(DB_PASSWORD);

        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            log.error("Error Closing connection", e);
            // However can continue
        }

        try {
            Class.forName(driver);
            con = DriverManager.getConnection(constr, user, password);
            con.setAutoCommit(true);
        } catch (Exception e) {
            log.fatal("Error getting connection", e);
            throw new FrameworkException(e);
        }
        return con;
    }

    public static void close() throws FrameworkException {
        try {
            if (con != null && !con.isClosed()) con.close();
        } catch (SQLException e) {
            throw new FrameworkException(e);
        }
    }
}
