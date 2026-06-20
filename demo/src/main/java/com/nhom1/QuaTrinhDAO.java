package com.nhom1;
import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class QuaTrinhDAO {

    // Lấy danh sách quá trình công tác
    public static DefaultTableModel getQuaTrinh(String maNV) {
        Vector<String> cols = new Vector<>();
        cols.add("ID");
        cols.add("Thời Gian");
        cols.add("Đơn Vị Công Tác / Học Tập");
        cols.add("Chức Vụ / Học Vị");
        cols.add("Ghi Chú");

        Vector<Vector<Object>> rows = new Vector<>();
        String sql = "SELECT * FROM QuaTrinhCongTac WHERE MaNV = ? ORDER BY ID ASC";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, maNV);
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                Vector<Object> r = new Vector<>();
                r.add(rs.getInt("ID"));
                r.add(rs.getString("ThoiGian"));
                r.add(rs.getString("DonVi"));
                r.add(rs.getString("ChucVu"));
                r.add(rs.getString("GhiChu"));
                rows.add(r);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return new DefaultTableModel(rows, cols);
    }

    // Thêm quá trình mới
    public static boolean addQuaTrinh(String maNV, String tg, String dv, String cv, String ghiChu) {
        String sql = "INSERT INTO QuaTrinhCongTac (MaNV, ThoiGian, DonVi, ChucVu, GhiChu) VALUES (?,?,?,?,?)";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, maNV);
            p.setString(2, tg);
            p.setString(3, dv);
            p.setString(4, cv);
            p.setString(5, ghiChu);
            return p.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    // Xóa quá trình
    public static boolean deleteQuaTrinh(int id) {
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement p = conn.prepareStatement("DELETE FROM QuaTrinhCongTac WHERE ID=?")) {
            p.setInt(1, id);
            return p.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }
}