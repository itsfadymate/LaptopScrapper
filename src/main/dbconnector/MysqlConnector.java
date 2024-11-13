package main.dbconnector;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

import main.Currency;
import main.LaptopData;


public class MysqlConnector {
	private final String url =  "jdbc:mysql://localhost:3306/laptop_registry";
	private final String username =  "root";
	private final String password =  "admin";
	private Connection connection;
    public static void main(String[] args) {
    	try {
			MysqlConnector db = new MysqlConnector();
			List<LaptopData> d =db.getAllLaptops(false);
			int n=6;
			//System.out.println("\n\n\ngetting top " + n + " laptops\n\n");
			//System.out.println(db.getTopNLaptops(n) +"\n");
			System.out.println("curr laptop\n " + d.get(0));
			d.get(0).setInSale(true);
			d.get(0).setHdd(0);
			//System.out.println("after mod " + d.get(0));
			db.addLaptop(d.get(0));
			db.getAllLaptops(true);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
     
    }
    public MysqlConnector() throws SQLException {
        this.connection = DriverManager.getConnection(url,username,password);
        System.out.println("connected to laptop registry");
    }
    public List<LaptopData> getTopNLaptops(int n) throws SQLException{
    	if (n<1) return new ArrayList<>();
    	String sql = "CALL laptop_registry.select_top_n_entries("+n+");";
    	List<LaptopData> ans  = selectLaptops(sql);
    	return ans;
    }
    public List<LaptopData> getAllLaptops(boolean printRes) throws SQLException {
    	String sql = "SELECT * FROM laptop_registry.laptop;";
    	List<LaptopData> ans = selectLaptops(sql);
    	if (printRes)
    		ans.forEach(e->System.out.println(e));
    	return ans;
    	
    }
    private List<LaptopData> selectLaptops(String sql) throws SQLException{
    	ArrayList<LaptopData> ans = new ArrayList<>();
    	Statement statement = connection.createStatement();
    	ResultSet res = statement.executeQuery(sql);
    	while (res.next()) {
    		ans.add(extractLaptop(res));		
    	}
    	return ans;
    }

    private LaptopData extractLaptop(ResultSet res) throws SQLException {
		// TODO Auto-generated method stub
		LaptopData l =  new LaptopData(res.getString("currency").equals("usd")?Currency.USD:res.getString("currency").equals("euro")?Currency.EURO : Currency.EGP);
				l.setPurchaseLink(res.getString("purchase_link"));
				l.setName(res.getString("name"));
				l.setGpu(res.getString("gpu"));
				l.setCpu(res.getString("cpu"));
				l.setRam(res.getInt("ram"));
				l.setHdd(res.getInt("hdd"));
			    l.setSsd(res.getInt("ssd"));
				l.setPrice(res.getDouble("price"));
				l.setInStock(res.getBoolean("in_stock"));
				l.setInSale(res.getBoolean("on_sale"));
		return l;	
	}
	public void addLaptop(LaptopData l) throws SQLException {
		String sql = "INSERT INTO laptop_registry.laptop (purchase_link, name, gpu, cpu, ram, hdd, ssd, price, currency,in_stock,on_sale) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?,?) "
                + "ON DUPLICATE KEY UPDATE "
                + "name = VALUES(name), gpu = VALUES(gpu), cpu = VALUES(cpu), ram = VALUES(ram), "
                + "hdd = VALUES(hdd), ssd = VALUES(ssd), price = VALUES(price), currency = VALUES(currency),in_stock = VALUES(in_stock),on_sale = VALUES(on_sale)";
		
		PreparedStatement pstmt = connection.prepareStatement(sql);

            try {
				pstmt.setString(1, l.getPurchaseLink());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            try {
				pstmt.setString(2, l.getName());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            try {
				pstmt.setString(3, l.getGpu());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
            try {
				pstmt.setString(4, l.getCpu());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            try {
				pstmt.setInt(5, l.getRam());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            try {
				pstmt.setInt(6, l.getHdd());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            try {
				pstmt.setInt(7, l.getSsd());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            try {
				pstmt.setFloat(8, (float) l.getPrice());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            try {
				pstmt.setString(9, l.getC().toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            try {
				pstmt.setBoolean(10, l.isInStock());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            try {
				pstmt.setBoolean(11, l.isInSale());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected + " row(s) affected.");

    
}
}

