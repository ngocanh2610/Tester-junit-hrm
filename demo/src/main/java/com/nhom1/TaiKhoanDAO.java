package com.nhom1;
import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class TaiKhoanDAO {
    public static boolean checkLogin(String username, String password) {
        String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ? AND MatKhau = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Auth.user = rs.getString("TenDangNhap");
                Auth.role = rs.getString("Quyen");
                Auth.maNV = rs.getString("MaNV");
                return true;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public static DefaultTableModel getDSTaiKhoan() {
        Vector<String> cols = new Vector<>();
        cols.add("Tài Khoản");
        cols.add("Mật Khẩu");
        cols.add("Quyền");
        cols.add("Mã NV");
        cols.add("Chủ Sở Hữu"); 

        Vector<Vector<Object>> rows = new Vector<>();
        String sql = "SELECT t.TenDangNhap, t.MatKhau, t.Quyen, t.MaNV, nv.HoTen " +
                     "FROM TaiKhoan t LEFT JOIN NhanVien nv ON t.MaNV = nv.MaNV";

        try (Connection conn = ConnectDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while(rs.next()) {
                Vector<Object> r = new Vector<>();
                r.add(rs.getString("TenDangNhap"));
                r.add(rs.getString("MatKhau"));
                r.add(rs.getString("Quyen"));
                r.add(rs.getString("MaNV"));
                r.add(rs.getString("HoTen"));
                rows.add(r);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return new DefaultTableModel(rows, cols);
    }

    public static boolean addTaiKhoan(String user, String pass, String role, String maNV) {
        String sql = "INSERT INTO TaiKhoan(TenDangNhap, MatKhau, Quyen, MaNV) VALUES(?,?,?,?)";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, user);
            p.setString(2, pass);
            p.setString(3, role);
            p.setString(4, maNV);
            return p.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    public static boolean deleteTaiKhoan(String user) {
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement p = conn.prepareStatement("DELETE FROM TaiKhoan WHERE TenDangNhap=?")) {
            p.setString(1, user);
            return p.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    public static boolean updatePassword(String user, String newPass) {
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement p = conn.prepareStatement("UPDATE TaiKhoan SET MatKhau=? WHERE TenDangNhap=?")) {
            p.setString(1, newPass);
            p.setString(2, user);
            return p.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }
}