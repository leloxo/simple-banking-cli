package com.github.leloxo.bankserver.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);

    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        try {
            loadProperties();
        } catch (Exception e) {
            logger.error("Failed to load database properties: {}", e.getMessage());
            throw new RuntimeException("Unable to initialize DatabaseConnection", e);
        }
    }

    public static void loadProperties() throws Exception {
        Properties properties = new Properties();

        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new FileNotFoundException("config.properties not found in classpath");
            }
            properties.load(input);
        }

        URL = properties.getProperty("db.url");
        USER = properties.getProperty("db.user");
        PASSWORD = properties.getProperty("db.password");

        logger.info("Database properties loaded successfully.");
    }

    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            logger.info("Database connection established successfully.");
            return connection;
        } catch (SQLException e) {
            logger.error("Failed to establish database connection: {}", e.getMessage());
            throw e;
        }
    }

}