package main.dbconnector;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;


public class MysqlConnector {
    public static void main(String[] args) {
    	String username = "root";
    	String password = "admin";
    	String url = "jdbc:mysql://localhost:3306/world";



        try {
        	Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("driver is here");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
        	Connection con = DriverManager.getConnection(url,username,password);
        	System.out.println("connecteeeeed");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
}

