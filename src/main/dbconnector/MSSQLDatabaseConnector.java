package main.dbconnector;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MSSQLDatabaseConnector {
    public static void main(String[] args) {
        // Connection details
        String connectionUrl = "jdbc:sqlserver://<hostname>:<port>;databaseName=<database>;user=<username>;password=<password>";

        // Connect to the database
        try (Connection conn = DriverManager.getConnection(connectionUrl);
             Statement stmt = conn.createStatement()) {

            // Test Query
            String sql = "SELECT TOP 1 * FROM your_table";
            ResultSet rs = stmt.executeQuery(sql);

            // Process the results
            while (rs.next()) {
                System.out.println("Data: " + rs.getString(1)); // Adjust based on your columns
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

