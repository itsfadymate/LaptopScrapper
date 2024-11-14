package main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import main.dbconnector.MysqlConnector;
import main.scrappers.AmazonProductReader;
import main.scrappers.NeweggProductReader;

public class MainScrapper {

	public static void main(String[] args) throws SQLException {
		List<LaptopData> failedToWriteProducts = new ArrayList<>();
		MysqlConnector db = new MysqlConnector();
		addAllProducts(AmazonProductReader.readProduct(),failedToWriteProducts,db);
		try {
			addAllProducts(NeweggProductReader.readProducts(false),failedToWriteProducts,db);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("failed write operations: " + failedToWriteProducts.size());	
		System.out.println(db.getTopNLaptops(10));
	}
	

	
	public static void addAllProducts(List<LaptopData> products,List<LaptopData> failedToWriteProducts,MysqlConnector db ) {
		for (LaptopData product : products) {
			try {
				db.addLaptop(product);
			}catch (SQLException e) {
				failedToWriteProducts.add(product);
				e.printStackTrace();
			}
		}
		System.out.println("failed in " + failedToWriteProducts.size() +"/"+products.size());
	}
}
