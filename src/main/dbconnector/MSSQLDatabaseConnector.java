package main.dbconnector;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MSSQLDatabaseConnector {
    public static void main(String[] args) {
        
        String connectionUrl = "jdbc:sqlserver://(localdb)\\MSSQLLocalDB;databaseName=laptop_registry;integratedSecurity=true";

        
        try (Connection conn = DriverManager.getConnection(connectionUrl);
             Statement stmt = conn.createStatement()) {

            
            String sql = "SELECT  * FROM laptop";
            ResultSet rs = stmt.executeQuery(sql);

            
            while (rs.next()) {
                System.out.println("Data: " + rs.getString(1)); // Adjust based on your columns
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

