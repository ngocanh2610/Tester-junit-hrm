package com.nhom1;
import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class KhoaDAO {

    // --- PHẦN 1: XỬ LÝ KHOA ---
    public static DefaultTableModel getDSKhoa() {
        Vector<String> cols = new Vector<>();
        cols.add("Mã Khoa");
        cols.add("Tên Khoa");
        Vector<Vector<Object>> rows = new Vector<>();

        try (Connection conn = ConnectDatabase.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM Khoa")) {
            while (rs.next()) {
                Vector<Object> r = new Vector<>();
                r.add(rs.getString("MaKhoa"));
                r.add(rs.getString("TenKhoa"));
                rows.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DefaultTableModel(rows, cols);
    }

    public static boolean addKhoa(String ma, String ten) {
        try (Connection conn = ConnectDatabase.getConnection(); PreparedStatement p = conn.prepareStatement("INSERT INTO Khoa VALUES(?,?)")) {
            p.setString(1, ma);
            p.setString(2, ten);
            return p.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean updateKhoa(String ma, String ten) {
        try (Connection conn = ConnectDatabase.getConnection(); PreparedStatement p = conn.prepareStatement("UPDATE Khoa SET TenKhoa=? WHERE MaKhoa=?")) {
            p.setString(1, ten);
            p.setString(2, ma);
            return p.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean deleteKhoa(String ma) {
        try (Connection conn = ConnectDatabase.getConnection(); PreparedStatement p = conn.prepareStatement("DELETE FROM Khoa WHERE MaKhoa=?")) {
            p.setString(1, ma);
            return p.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isKhoaExist(String maKhoa) {
        try (Connection conn = ConnectDatabase.getConnection(); PreparedStatement p = conn.prepareStatement("SELECT COUNT(*) FROM Khoa WHERE MaKhoa = ?")) {
            p.setString(1, maKhoa);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Trả về true nếu count > 0
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // --- PHẦN 2: XỬ LÝ MÔN HỌC ---
    public static DefaultTableModel getDSMonHoc(String maKhoa) {
        Vector<String> cols = new Vector<>();
        cols.add("Mã Môn");
        cols.add("Tên Môn");
        cols.add("Số TC");
        Vector<Vector<Object>> rows = new Vector<>();

        String sql = "SELECT * FROM MonHoc WHERE MaKhoa=?";
        try (Connection conn = ConnectDatabase.getConnection(); PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, maKhoa);
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                Vector<Object> r = new Vector<>();
                r.add(rs.getString("MaMon"));
                r.add(rs.getString("TenMon"));
                r.add(rs.getInt("SoTinChi"));
                rows.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DefaultTableModel(rows, cols);
    }

    public static boolean addMonHoc(String ma, String ten, int tc, String maKhoa) {
        try (Connection conn = ConnectDatabase.getConnection(); PreparedStatement p = conn.prepareStatement("INSERT INTO MonHoc VALUES(?,?,?,?)")) {
            p.setString(1, ma);
            p.setString(2, ten);
            p.setInt(3, tc);
            p.setString(4, maKhoa);
            return p.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean updateMonHoc(String ma, String ten, int tc) {
        try (Connection conn = ConnectDatabase.getConnection(); PreparedStatement p = conn.prepareStatement("UPDATE MonHoc SET TenMon=?, SoTinChi=? WHERE MaMon=?")) {
            p.setString(1, ten);
            p.setInt(2, tc);
            p.setString(3, ma);
            return p.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean deleteMonHoc(String ma) {
        try (Connection conn = ConnectDatabase.getConnection(); PreparedStatement p = conn.prepareStatement("DELETE FROM MonHoc WHERE MaMon=?")) {
            p.setString(1, ma);
            return p.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isMonHocExist(String maMon) {
        try (Connection conn = ConnectDatabase.getConnection(); PreparedStatement p = conn.prepareStatement("SELECT COUNT(*) FROM MonHoc WHERE MaMon = ?")) {
            p.setString(1, maMon);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
