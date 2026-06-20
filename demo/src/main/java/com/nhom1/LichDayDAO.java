package com.nhom1;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LichDayDAO {

    // 1. Hàm lấy TKB có lọc theo tuần
    public static Object[][] getScheduleMatrix(String maNV, String monday, String sunday) {
        Object[][] data = new Object[15][8];
        for (int i = 0; i < 15; i++) data[i][0] = "Tiết " + (i + 1);

        String sql = "SELECT TenMonHoc, PhongHoc, Thu, TietBatDau, SoTiet FROM LichDay " +
                     "WHERE MaNV = ? AND (TuNgay <= ? AND DenNgay >= ?)";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, maNV);
            pstmt.setString(2, sunday); 
            pstmt.setString(3, monday); 
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String mon = rs.getString("TenMonHoc");
                String phong = rs.getString("PhongHoc");
                int thu = rs.getInt("Thu");      
                int tietBD = rs.getInt("TietBatDau"); 
                int soTiet = rs.getInt("SoTiet");

                int colIndex = thu - 1; 
                for (int k = 0; k < soTiet; k++) {
                    int rowIndex = (tietBD - 1) + k; 
                    if (rowIndex < 15) {
                        data[rowIndex][colIndex] = mon + "\n(" + phong + ")";
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return data;
    }
    
    // 2. Hàm thêm lịch dạy
    public static boolean addLichDay(String maNV, String mon, String phong, int thu, int tiet, int soTiet, String tuNgay, String denNgay) {
        String sql = "INSERT INTO LichDay (MaNV, TenMonHoc, PhongHoc, Thu, TietBatDau, SoTiet, TuNgay, DenNgay) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = ConnectDatabase.getConnection(); PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, maNV); 
            p.setString(2, mon); 
            p.setString(3, phong);
            p.setInt(4, thu); 
            p.setInt(5, tiet); 
            p.setInt(6, soTiet);
            p.setString(7, tuNgay); 
            p.setString(8, denNgay);
            return p.executeUpdate() > 0;
        } catch (Exception e) { 
            e.printStackTrace();
            return false; 
        }
    }

    // --- 3. HÀM MỚI: LẤY DANH SÁCH MÔN HỌC ---
    public static List<String> getDSMonHoc() {
        List<String> list = new ArrayList<>();
        // Lấy danh sách tên môn từ bảng MonHoc (Bảng này đã tạo ở bước trước)
        String sql = "SELECT TenMon FROM MonHoc ORDER BY TenMon ASC";
        try (Connection conn = ConnectDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString("TenMon"));
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return list;
    }
}