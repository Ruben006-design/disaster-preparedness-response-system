package com.disasterresponse.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration reader for MySQL Database connection parameters.
 * Reads environment variables first, falls back to .env file, then to student defaults.
 */
public class DatabaseConfig {

    private static final Map<String, String> envFileProps = new HashMap<>();

    static {
        loadEnvFile();
    }

    private static void loadEnvFile() {
        File[] candidateLocations = new File[]{
                new File(".env"),
                new File("../.env"),
                new File(".env.example"),
                new File("../.env.example")
        };

        for (File file : candidateLocations) {
            if (file.exists() && file.isFile()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) continue;
                        int idx = line.indexOf('=');
                        if (idx > 0) {
                            String key = line.substring(0, idx).trim();
                            String val = line.substring(idx + 1).trim();
                            envFileProps.putIfAbsent(key, val);
                        }
                    }
                } catch (Exception ignored) {
                }
                break;
            }
        }
    }

    public static String get(String key, String defaultValue) {
        String sysEnv = System.getenv(key);
        if (sysEnv != null && !sysEnv.trim().isEmpty()) {
            return sysEnv.trim();
        }
        String fileVal = envFileProps.get(key);
        if (fileVal != null && !fileVal.trim().isEmpty()) {
            return fileVal.trim();
        }
        return defaultValue;
    }

    public static String getHost() {
        return get("DB_HOST", "localhost");
    }

    public static int getPort() {
        return Integer.parseInt(get("DB_PORT", "3306"));
    }

    public static String getDatabaseName() {
        return get("DB_NAME", "disaster_response_db");
    }

    public static String getUsername() {
        return get("DB_USERNAME", "root");
    }

    public static String getPassword() {
        return get("DB_PASSWORD", "root");
    }

    public static int getServerPort() {
        return Integer.parseInt(get("SERVER_PORT", "8080"));
    }

    public static String getUploadDir() {
        return get("FILE_UPLOAD_DIR", "./uploads");
    }
}
