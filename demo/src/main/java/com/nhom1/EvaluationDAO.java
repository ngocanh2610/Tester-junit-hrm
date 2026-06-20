package com.nhom1;
import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class EvaluationDAO {

    // Hàm quan trọng: Tính toán KPI tự động cho toàn bộ nhân viên trong năm
    public static DefaultTableModel calculateKPI(int nam) {
        Vector<String> cols = new Vector<>();
        cols.add("Mã NV");
        cols.add("Họ Tên");
        cols.add("Tổng Tiết Dạy");
        cols.add("Điểm NCKH");
        cols.add("Đề Xuất Xếp Loại"); // Máy tự tính
        cols.add("Trạng Thái"); // Đã lưu hay chưa

        Vector<Vector<Object>> rows = new Vector<>();

        // Query phức tạp: Join bảng NhanVien với tổng Lịch Dạy và tổng NCKH
        String sql = "SELECT nv.MaNV, nv.HoTen, " +
                     // Tính tổng tiết dạy trong năm (ISNULL để nếu không dạy thì bằng 0)
                     "(SELECT ISNULL(SUM(SoTiet), 0) FROM LichDay ld WHERE ld.MaNV = nv.MaNV AND YEAR(TuNgay) = ?) AS TongTiet, " +
                     // Tính tổng điểm NCKH trong năm
                     "(SELECT ISNULL(SUM(DiemThuong), 0) FROM NCKH nc WHERE nc.MaNV = nv.MaNV AND YEAR(NgayCongBo) = ?) AS TongDiem, " +
                     // Kiểm tra xem đã chốt sổ chưa
                     "(SELECT XepLoai FROM XepLoai xl WHERE xl.MaNV = nv.MaNV AND xl.NamHoc = ?) AS DaLuu " +
                     "FROM NhanVien nv";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            
            p.setInt(1, nam);
            p.setInt(2, nam);
            p.setInt(3, nam);
            
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                Vector<Object> r = new Vector<>();
                String maNV = rs.getString("MaNV");
                int tongTiet = rs.getInt("TongTiet");
                double tongDiem = rs.getDouble("TongDiem");
                String daLuu = rs.getString("DaLuu");

                r.add(maNV);
                r.add(rs.getString("HoTen"));
                r.add(tongTiet);
                r.add(tongDiem);

                // --- LOGIC XẾP LOẠI (Bạn có thể sửa tiêu chí ở đây) ---
                String xepLoai;
                if (daLuu != null) {
                    xepLoai = daLuu; // Nếu đã lưu trong DB thì lấy giá trị cũ
                } else {
                    // Máy tự động đề xuất dựa trên số liệu
                    if (tongTiet >= 270 && tongDiem >= 1.0) {
                        xepLoai = "A - Xuất Sắc"; // Đủ tiết + Có NCKH
                    } else if (tongTiet >= 270) {
                        xepLoai = "B - Hoàn Thành Tốt"; // Chỉ đủ tiết
                    } else if (tongTiet >= 100) {
                        xepLoai = "C - Hoàn Thành";
                    } else {
                        xepLoai = "D - Không Hoàn Thành";
                    }
                }
                r.add(xepLoai);
                r.add(daLuu != null ? "Đã chốt" : "Chưa chốt");
                
                rows.add(r);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return new DefaultTableModel(rows, cols) {
            @Override // Chỉ cho phép sửa cột Xếp Loại (Cột 4)
            public boolean isCellEditable(int row, int column) {
                return column == 4; 
            }
        };
    }

    // Lưu kết quả xếp loại vào DB
    public static boolean saveEvaluation(String maNV, int nam, int tongTiet, double tongDiem, String xepLoai) {
        try (Connection conn = ConnectDatabase.getConnection()) {
            // Xóa cũ nếu có (để lưu mới)
            PreparedStatement del = conn.prepareStatement("DELETE FROM XepLoai WHERE MaNV=? AND NamHoc=?");
            del.setString(1, maNV); del.setInt(2, nam);
            del.executeUpdate();

            // Thêm mới
            String sql = "INSERT INTO XepLoai (MaNV, NamHoc, TongTietDay, TongDiemNCKH, XepLoai) VALUES (?,?,?,?,?)";
            PreparedStatement p = conn.prepareStatement(sql);
            p.setString(1, maNV);
            p.setInt(2, nam);
            p.setInt(3, tongTiet);
            p.setDouble(4, tongDiem);
            p.setString(5, xepLoai);
            return p.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}