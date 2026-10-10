package com.disasterresponse;

import com.disasterresponse.config.DatabaseConfig;
import com.disasterresponse.database.DatabaseInitializer;
import com.disasterresponse.handler.*;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Main application entry point for the Core Java HTTP Server.
 * Clean, lightweight, and easy to explain during B.Tech viva and software engineering lab.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("   DISASTER PREPAREDNESS AND RESPONSE EDUCATION SYSTEM");
        System.out.println("   Core Java + Java HttpServer + JDBC + MySQL Architecture");
        System.out.println("===============================================================================");

        // 1. Initialize Database & Seed Records
        DatabaseInitializer.initialize();

        // 2. Start Lightweight Core Java HTTP Server
        int port = DatabaseConfig.getServerPort();
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            // Register Handlers
            server.createContext("/api/auth", new AuthHandler());
            server.createContext("/api/users", new UserHandler());
            server.createContext("/api/disasters", new DisasterHandler());
            server.createContext("/api/modules", new ModuleHandler());
            server.createContext("/api/quizzes", new QuizHandler());
            server.createContext("/api/simulations", new SimulationHandler());
            server.createContext("/api/preparedness", new PreparednessHandler());
            server.createContext("/api/reports", new ReportHandler());
            server.createContext("/api/map", new MapHandler());
            server.createContext("/api/officer", new OfficerHandler());
            server.createContext("/api/admin", new AdminHandler());
            server.createContext("/api/dashboard", new DashboardHandler());
            server.createContext("/api/contacts", new EmergencyContactHandler());
            server.createContext("/api/notifications", new NotificationHandler());
            server.createContext("/api/evidence", new StaticFileHandler());
            server.createContext("/uploads", new StaticFileHandler());

            // Multi-threaded request execution
            server.setExecutor(Executors.newFixedThreadPool(25));
            server.start();

            System.out.println("[Main] Server successfully started on http://localhost:" + port);
            System.out.println("[Main] React Frontend Proxy will forward requests to port " + port);
            System.out.println("-------------------------------------------------------------------------------");
            System.out.println("Default Demo Credentials:");
            System.out.println("  * Administrator: admin@disasterprep.gov / AdminPassword@123");
            System.out.println("  * Officer:       officer.sharma@disasterprep.gov / OfficerPass@123");
            System.out.println("  * Officer:       officer.chen@disasterprep.gov / OfficerPass@123");
            System.out.println("  * Student:       student.alex@university.edu / StudentPass@123");
            System.out.println("  * Student:       student.priya@university.edu / StudentPass@123");
            System.out.println("===============================================================================");
        } catch (Exception e) {
            System.err.println("[Main] Fatal: Unable to start HTTP Server on port " + port);
            e.printStackTrace();
            System.exit(1);
        }
    }
}
