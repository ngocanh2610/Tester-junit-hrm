package com.nhom1;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import java.sql.*;
//import java.util.Vector;
//import javax.swing.table.DefaultTableModel;

public class ConnectDatabase {

    public static Connection getConnection() {
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setUser("sa");
        ds.setPassword("123456");
        ds.setDatabaseName("hrm2"); 
        ds.setServerName("localhost"); 
        ds.setPortNumber(1433);
        ds.setTrustServerCertificate(true);

        try {
            return ds.getConnection();
        } catch (SQLException ex) {
            System.out.println("Lỗi kết nối: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }
    
    public static void main(String[] args) {
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("KẾT NỐI THÀNH CÔNG ĐẾN DATABASE: HRM");
            try {
                conn.close();
            } catch (SQLException ex) {}
        } else {
            System.out.println("KẾT NỐI THẤT BẠI");
        }
    }
}