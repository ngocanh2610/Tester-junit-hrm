package com.nhom1;

import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class LuongDAO {

    public static double tinhLuong(double luongCB, double heSo, double phuCap, 
                                   int soTiet, double donGiaTiet) {
        if (luongCB < 0 || heSo < 0 || phuCap < 0 || soTiet < 0 || donGiaTiet < 0) {
            throw new IllegalArgumentException("Du lieu dau vao khong duoc am!");
        }
        return (luongCB * heSo) + phuCap + (soTiet * donGiaTiet);
    }

    public static DefaultTableModel getBangLuong(int thang, int nam) {
        Vector<String> cols = new Vector<>();
        cols.add("Mã NV");
        cols.add("Họ Tên");
        cols.add("Chức Vụ");
        cols.add("Hệ Số");
        cols.add("Lương CB");
        cols.add("Phụ Cấp");
        cols.add("THỰC LĨNH");

        Vector<Vector<Object>> rows = new Vector<>();
        String sql = "SELECT nv.MaNV, nv.HoTen, nv.ChucVu, " +
                     "ISNULL(bl.HeSo, nv.HeSoLuong) AS HienThiHeSo, " +
                     "ISNULL(bl.LuongCB, nv.LuongCoBan) AS HienThiLuongCB, " +
                     "ISNULL(bl.PhuCap, nv.PhuCap) AS HienThiPhuCap, " +
                     "ISNULL(bl.ThucLinh, 0) AS DaLinh " +
                     "FROM NhanVien nv " +
                     "LEFT JOIN BangLuong bl ON nv.MaNV = bl.MaNV AND bl.Thang = ? AND bl.Nam = ?";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            
            p.setInt(1, thang);
            p.setInt(2, nam);
            
            ResultSet rs = p.executeQuery();
            java.text.NumberFormat nf = java.text.NumberFormat.getInstance();

            while (rs.next()) {
                Vector<Object> r = new Vector<>();
                r.add(rs.getString("MaNV"));
                r.add(rs.getString("HoTen"));
                r.add(rs.getString("ChucVu"));
                r.add(rs.getDouble("HienThiHeSo"));
                r.add(nf.format(rs.getDouble("HienThiLuongCB")));
                r.add(nf.format(rs.getDouble("HienThiPhuCap")));
                double thucLinh = rs.getDouble("DaLinh");
                r.add(nf.format(thucLinh)); 
                rows.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DefaultTableModel(rows, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static Object[] getChiTietLuong(String maNV, int thang, int nam) {
        String sql = "SELECT " +
                     "nv.MaNV, nv.HoTen, nv.LoaiHinh, " +
                     "ISNULL(bl.HeSo, nv.HeSoLuong) AS HeSo, " +
                     "ISNULL(bl.LuongCB, nv.LuongCoBan) AS LuongCB, " +
                     "ISNULL(bl.PhuCap, nv.PhuCap) AS PhuCap, " +
                     "bl.TongTiet, bl.ThuLao, bl.ThucLinh " +
                     "FROM NhanVien nv " +
                     "LEFT JOIN BangLuong bl ON nv.MaNV = bl.MaNV AND bl.Thang = ? AND bl.Nam = ? " +
                     "WHERE nv.MaNV = ?";
                     
        try (Connection conn = ConnectDatabase.getConnection(); 
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setInt(1, thang);
            p.setInt(2, nam);
            p.setString(3, maNV);
            
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return new Object[] {
                    rs.getString("MaNV"),
                    rs.getString("HoTen"),
                    rs.getString("LoaiHinh"),
                    rs.getDouble("HeSo"),
                    rs.getDouble("LuongCB"),
                    rs.getDouble("PhuCap"),
                    rs.getObject("TongTiet") != null ? rs.getInt("TongTiet") : 0,
                    rs.getObject("ThucLinh") != null ? rs.getDouble("ThucLinh") : 0
                };
            }
        } catch(Exception e) {
            e.printStackTrace();
        } 
        return null;
    }

    public static boolean saveSingleSalary(String maNV, int thang, int nam, 
                                           String luongCBStr, String heSoStr, String phuCapStr,
                                           String luongCungStr, String tongTietStr, String thuLaoStr, String thucLinhStr) {
        double luongCB, heSo, phuCap, luongCung, thuLao, thucLinh;
        int tongTiet;

        try {
            luongCB = Double.parseDouble(luongCBStr.trim());
            heSo = Double.parseDouble(heSoStr.trim());
            phuCap = Double.parseDouble(phuCapStr.trim());
            luongCung = Double.parseDouble(luongCungStr.trim());
            tongTiet = Integer.parseInt(tongTietStr.trim());
            thuLao = Double.parseDouble(thuLaoStr.trim());
            thucLinh = Double.parseDouble(thucLinhStr.trim());
            
            if (luongCB <= 0 || heSo <= 0) {
                System.out.println("Lỗi: Lương cơ bản và Hệ số phải lớn hơn 0");
                return false;
            }
            if (phuCap < 0 || tongTiet < 0 || thuLao < 0 || luongCung < 0 || thucLinh < 0) {
                System.out.println("Lỗi: Tiền phụ cấp và số tiết không được là số âm");
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("Lỗi: Dữ liệu nhập vào phải là số, không được chứa chữ cái!");
            return false;
        }

        Connection conn = null;
        try {
            conn = ConnectDatabase.getConnection();
            String checkSql = "SELECT COUNT(*) FROM BangLuong WHERE MaNV=? AND Thang=? AND Nam=?";
            PreparedStatement check = conn.prepareStatement(checkSql);
            check.setString(1, maNV); check.setInt(2, thang); check.setInt(3, nam);
            ResultSet rs = check.executeQuery();
            rs.next();
            boolean exists = rs.getInt(1) > 0;

            String sql;
            if (exists) {
                sql = "UPDATE BangLuong SET LuongCB=?, HeSo=?, PhuCap=?, LuongCung=?, TongTiet=?, ThuLao=?, ThucLinh=?, NgayChot=GETDATE() WHERE MaNV=? AND Thang=? AND Nam=?";
            } else {
                sql = "INSERT INTO BangLuong (LuongCB, HeSo, PhuCap, LuongCung, TongTiet, ThuLao, ThucLinh, MaNV, Thang, Nam) VALUES (?,?,?,?,?,?,?,?,?,?)";
            }

            PreparedStatement p = conn.prepareStatement(sql);
            p.setDouble(1, luongCB);
            p.setDouble(2, heSo);
            p.setDouble(3, phuCap);
            p.setDouble(4, luongCung);
            p.setInt(5, tongTiet);
            p.setDouble(6, thuLao);
            p.setDouble(7, thucLinh);
            p.setString(8, maNV);
            p.setInt(9, thang);
            p.setInt(10, nam);

            return p.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if(conn != null) conn.close(); } catch(Exception e) {}
        }
    }

    public static double getPhuCapCoBan(String maNV) {
        String sql = "SELECT PhuCap FROM NhanVien WHERE MaNV = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, maNV);
            ResultSet rs = p.executeQuery();
            if(rs.next()) return rs.getDouble(1);
        } catch(Exception e) { e.printStackTrace(); }
        return 0;
    }
}