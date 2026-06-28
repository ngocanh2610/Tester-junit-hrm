package com.nhom1;

import java.sql.*;
import java.util.Vector;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class NhanSuDAO {

    public static DefaultTableModel getNhanVienModel() {
        Vector<String> columns = getColumns();
        Vector<Vector<Object>> rows = new Vector<>();

        String sql = "SELECT nv.MaNV, nv.HoTen, nv.NgaySinh, k.TenKhoa, nv.ChucVu, nv.TrinhDo, " +
                "nv.LoaiHinh, nv.TrangThai, nv.HeSoLuong, nv.LuongCoBan, nv.PhuCap, nv.HinhAnh " +
                "FROM NhanVien nv LEFT JOIN Khoa k ON nv.MaKhoa = k.MaKhoa";

        try (Connection conn = ConnectDatabase.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            rows = getDataRows(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new DefaultTableModel(rows, columns);
    }

    // --- MỚI: LẤY THÔNG TIN CỦA 1 NGƯỜI (Dùng cho Giảng viên) ---
    public static DefaultTableModel getNhanVienByMa(String maNV) {
        Vector<String> columns = getColumns();
        Vector<Vector<Object>> rows = new Vector<>();

        String sql = "SELECT nv.MaNV, nv.HoTen, nv.NgaySinh, k.TenKhoa, nv.ChucVu, nv.TrinhDo, " +
                "nv.LoaiHinh, nv.TrangThai, nv.HeSoLuong, nv.LuongCoBan, nv.PhuCap, nv.HinhAnh " +
                "FROM NhanVien nv LEFT JOIN Khoa k ON nv.MaKhoa = k.MaKhoa WHERE nv.MaNV = ?";

        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement p = conn.prepareStatement(sql)) {

            p.setString(1, maNV);
            ResultSet rs = p.executeQuery();
            rows = getDataRows(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new DefaultTableModel(rows, columns);
    }

    // Helper: Định nghĩa cột (để dùng chung, tránh sai lệch)
    private static Vector<String> getColumns() {
        Vector<String> columns = new Vector<>();
        columns.add("Mã NV");
        columns.add("Họ Tên");
        columns.add("Ngày Sinh");
        columns.add("Khoa");
        columns.add("Chức Vụ");
        columns.add("Trình Độ");
        columns.add("Loại Hình");
        columns.add("Trạng Thái");
        columns.add("Hệ Số");
        columns.add("Lương CB");
        columns.add("Phụ Cấp");
        columns.add("Hình Ảnh");
        return columns;
    }

    // Helper: Lấy dữ liệu từ ResultSet
    private static Vector<Vector<Object>> getDataRows(ResultSet rs) throws SQLException {
        Vector<Vector<Object>> rows = new Vector<>();
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            row.add(rs.getString("MaNV"));
            row.add(rs.getString("HoTen"));
            row.add(rs.getString("NgaySinh"));
            row.add(rs.getString("TenKhoa"));
            row.add(rs.getString("ChucVu"));
            row.add(rs.getString("TrinhDo"));
            row.add(rs.getString("LoaiHinh"));
            row.add(rs.getString("TrangThai"));
            row.add(rs.getDouble("HeSoLuong"));
            row.add(nf.format(rs.getDouble("LuongCoBan")));
            row.add(nf.format(rs.getDouble("PhuCap")));
            row.add(rs.getString("HinhAnh"));
            rows.add(row);
        }
        return rows;
    }

    // 2. LẤY DANH SÁCH KHOA
    public static List<String> getKhoaList() {
        List<String> list = new ArrayList<>();
        try (Connection conn = ConnectDatabase.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT MaKhoa, TenKhoa FROM Khoa")) {
            while (rs.next()) {
                list.add(rs.getString("MaKhoa") + " - " + rs.getString("TenKhoa"));
            }
        } catch (SQLException e) {
        }
        return list;
    }

    // 3. THÊM NHÂN VIÊN
    public static boolean addNhanVien(String ma, String ten, String ns, String gt, String makhoa,
            String cv, String td, String lh, String tt,
            double heSo, double luongCB, double phuCap, String hinh) {

        // --- 1. RÀO CHẮN KIỂM TRA DỮ LIỆU ĐẦU VÀO (VALIDATION) ---
        if (ma == null || ma.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nhân viên không được để trống!");
        }
        if (ten == null || ten.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống!");
        }
        if (heSo < 0 || luongCB < 0 || phuCap < 0) {
            throw new IllegalArgumentException("Các trường lương không được âm!");
        }

        // Kiểm tra ngày sinh (Nếu có nhập thì parse ra LocalDate để so sánh với ngày
        // hôm nay)
        if (ns != null && !ns.trim().isEmpty()) {
            try {
                java.time.LocalDate ngaySinh = java.time.LocalDate.parse(ns.trim());
                if (ngaySinh.isAfter(java.time.LocalDate.now())) {
                    throw new IllegalArgumentException("Ngày sinh không được lớn hơn ngày hiện tại!");
                }
            } catch (java.time.format.DateTimeParseException e) {
                // Đề phòng trường hợp chuỗi ns truyền vào không đúng chuẩn năm-tháng-ngày
                throw new IllegalArgumentException("Định dạng ngày sinh không hợp lệ!");
            }
        }
        String sql = "INSERT INTO NhanVien (MaNV, HoTen, NgaySinh, GioiTinh, MaKhoa, ChucVu, TrinhDo, LoaiHinh, TrangThai, HeSoLuong, LuongCoBan, PhuCap, HinhAnh) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectDatabase.getConnection(); PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, ma);
            p.setString(2, ten);
            p.setString(3, ns);
            p.setString(4, gt);
            p.setString(5, makhoa);
            p.setString(6, cv);
            p.setString(7, td);
            p.setString(8, lh);
            p.setString(9, tt);
            p.setDouble(10, heSo);
            p.setDouble(11, luongCB);
            p.setDouble(12, phuCap);
            p.setString(13, hinh);

            return p.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. SỬA NHÂN VIÊN
    public static boolean updateNhanVien(String ma, String ten, String ns, String gt, String makhoa,
            String cv, String td, String lh, String tt,
            double heSo, double luongCB, double phuCap, String hinh) {

        String sql = "UPDATE NhanVien SET HoTen=?, NgaySinh=?, GioiTinh=?, MaKhoa=?, ChucVu=?, " +
                "TrinhDo=?, LoaiHinh=?, TrangThai=?, HeSoLuong=?, LuongCoBan=?, PhuCap=?, HinhAnh=? WHERE MaNV=?";
        try (Connection conn = ConnectDatabase.getConnection(); PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, ten);
            p.setString(2, ns);
            p.setString(3, gt);
            p.setString(4, makhoa);
            p.setString(5, cv);
            p.setString(6, td);
            p.setString(7, lh);
            p.setString(8, tt);
            p.setDouble(9, heSo);
            p.setDouble(10, luongCB);
            p.setDouble(11, phuCap);
            p.setString(12, hinh);
            p.setString(13, ma);
            return p.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    // 5. XÓA NHÂN VIÊN
    public static boolean deleteNhanVien(String ma) {
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement p = conn.prepareStatement("DELETE FROM NhanVien WHERE MaNV=?")) {
            p.setString(1, ma);
            return p.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }
}