package com.disasterresponse.database;

import com.disasterresponse.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages JDBC connections to MySQL database (with graceful fallback for local development).
 * Provides clean java.sql.Connection instances for all DAO classes.
 */
public class DatabaseConnection {

    private static boolean useH2Fallback = false;
    private static boolean initialized = false;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[DatabaseConnection] MySQL Driver not found on classpath: " + e.getMessage());
        }
    }

    public static synchronized Connection getConnection() throws SQLException {
        if (useH2Fallback) {
            return getH2Connection();
        }

        String host = DatabaseConfig.getHost();
        int port = DatabaseConfig.getPort();
        String dbName = DatabaseConfig.getDatabaseName();
        String user = DatabaseConfig.getUsername();
        String password = DatabaseConfig.getPassword();

        String mysqlUrl = String.format(
                "jdbc:mysql://%s:%d/%s?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8",
                host, port, dbName
        );

        try {
            Connection conn = DriverManager.getConnection(mysqlUrl, user, password);
            if (!initialized) {
                System.out.println("[DatabaseConnection] Successfully connected to MySQL database: " + dbName + " at " + host + ":" + port);
                initialized = true;
            }
            return conn;
        } catch (SQLException ex) {
            if (!initialized) {
                System.err.println("===============================================================================");
                System.err.println("[DatabaseConnection] NOTICE: Could not connect to MySQL at " + host + ":" + port);
                System.err.println("Reason: " + ex.getMessage());
                System.err.println("Active credentials: User=" + user + ", DB=" + dbName);
                System.err.println("-> Falling back to Embedded in-memory DB for immediate testing and viva demo.");
                System.err.println("-> To connect to your MySQL Server, set DB_PASSWORD in .env or environment variable.");
                System.err.println("===============================================================================");
                useH2Fallback = true;
                initialized = true;
            }
            return getH2Connection();
        }
    }

    private static Connection getH2Connection() throws SQLException {
        String h2Url = "jdbc:h2:mem:disaster_preparedness;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_LOWER=TRUE";
        return DriverManager.getConnection(h2Url, "sa", "");
    }

    public static boolean isUsingH2Fallback() {
        return useH2Fallback;
    }
}
