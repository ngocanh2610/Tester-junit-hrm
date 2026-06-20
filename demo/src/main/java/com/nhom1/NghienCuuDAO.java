package com.nhom1;
import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class NghienCuuDAO {

    // Lấy danh sách NCKH của 1 nhân viên đổ vào bảng
    public static DefaultTableModel getListNCKH(String maNV) {
        Vector<String> cols = new Vector<>();
        cols.add("ID");
        cols.add("Tên Đề Tài / Bài Báo");
        cols.add("Loại Hình");
        cols.add("Ngày Công Bố");
        cols.add("Điểm Thưởng");

        Vector<Vector<Object>> rows = new Vector<>();
        String sql = "SELECT * FROM NCKH WHERE MaNV = ?";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, maNV);
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                Vector<Object> r = new Vector<>();
                r.add(rs.getInt("ID"));
                r.add(rs.getString("TenDeTai"));
                r.add(rs.getString("LoaiHinh"));
                r.add(rs.getDate("NgayCongBo"));
                r.add(rs.getFloat("DiemThuong"));
                rows.add(r);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return new DefaultTableModel(rows, cols);
    }

    // Thêm mới NCKH
    public static boolean addNCKH(String maNV, String ten, String loai, String ngay, double diem) {
        String sql = "INSERT INTO NCKH (MaNV, TenDeTai, LoaiHinh, NgayCongBo, DiemThuong) VALUES (?,?,?,?,?)";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, maNV);
            p.setString(2, ten);
            p.setString(3, loai);
            p.setString(4, ngay);
            p.setDouble(5, diem);
            return p.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Xóa NCKH
    public static boolean deleteNCKH(int id) {
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement p = conn.prepareStatement("DELETE FROM NCKH WHERE ID=?")) {
            p.setInt(1, id);
            return p.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    // --- MỚI: Hàm lấy tổng điểm thưởng trong tháng ---
    public static double getTongDiemThuong(String maNV, int thang, int nam) {
        double tongDiem = 0;
        // Lấy tổng điểm của các bài báo công bố trong Tháng & Năm đang tính lương
        String sql = "SELECT SUM(DiemThuong) FROM NCKH WHERE MaNV = ? AND MONTH(NgayCongBo) = ? AND YEAR(NgayCongBo) = ?";
        
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, maNV);
            p.setInt(2, thang);
            p.setInt(3, nam);
            
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                tongDiem = rs.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tongDiem;
    }
}