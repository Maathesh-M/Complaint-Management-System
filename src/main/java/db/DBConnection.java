package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL      = "jdbc:mysql://localhost:3306/complaint_system?useSSL=false&serverTimezone=UTC";
    private static final String USER     = "root";
    private static final String PASSWORD = "dbms"; // ← Change to your MySQL password

    private static Connection connection = null;
    private DBConnection() {}
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load the MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Connection established successfully.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[DB] MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DB] Connection failed: " + e.getMessage());
        }
        return connection;
    }
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing connection: " + e.getMessage());
        }
    }
}