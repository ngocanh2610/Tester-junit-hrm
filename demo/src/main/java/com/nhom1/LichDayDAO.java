package com.nhom1;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LichDayDAO {
    public boolean kiemTraVaPhanCong(
            String maNV,
            String tenMon,
            String thu,
            int tietBD,
            int soTiet,
            String phongHoc,
            LocalDate tuNgay,
            LocalDate denNgay) {
        if (phongHoc == null || phongHoc.trim().isEmpty()) {
            throw new IllegalArgumentException("Phòng học không được để trống!");
        }
        if (tuNgay == null || denNgay == null) {
            throw new IllegalArgumentException("Ngày phân công không được để trống!");
        }
        if (tuNgay.isAfter(denNgay)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được lớn hơn ngày kết thúc!");
        }
        if (soTiet < 1 ){
            throw new IllegalArgumentException("Số tiết phải lớn hơn 0");
        }
        if (tietBD < 1 || tietBD > 15) {
            throw new IllegalArgumentException("Tiết bắt đầu phải từ 1 đến 15");
        }
        if (tietBD + soTiet - 1 > 15) {
            throw new IllegalArgumentException("Lịch học vượt quá tiết 15 trong ngày");
        }
        int thuInt = 0;
        if (thu != null && !thu.isEmpty()) {
            try {
                thuInt = Integer.parseInt(thu.replaceAll("[^0-9]", ""));
            } catch (NumberFormatException e) {
                thuInt = 0;
            }
        }
        if (checkTrungLichGiangVien(maNV, thuInt, tietBD, soTiet, tuNgay, denNgay)) {
            return false;
        }
        if (checkTrungPhongHoc(phongHoc, thuInt, tietBD, soTiet, tuNgay, denNgay)) {
            return false;
        }
        return addLichDay(
                maNV,
                tenMon,
                phongHoc,
                thuInt,
                tietBD,
                soTiet,
                tuNgay,
                denNgay);
    }

    protected boolean checkTrungLichGiangVien(String maNV, int thu, int inputTietBD, int inputSoTiet,
            LocalDate inputTuNgay, LocalDate inputDenNgay) {
        String sql = "SELECT COUNT(*) FROM LichDay WHERE MaNV = ? AND Thu = ? " +
                "AND (TuNgay <= ? AND DenNgay >= ?) " +
                "AND (TietBatDau <= ? AND (TietBatDau + SoTiet - 1) >= ?)";

        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, maNV);
            p.setInt(2, thu);
            p.setDate(3, java.sql.Date.valueOf(inputDenNgay));
            p.setDate(4, java.sql.Date.valueOf(inputTuNgay));
            p.setInt(5, inputTietBD + inputSoTiet - 1);
            p.setInt(6, inputTietBD);

            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    protected boolean checkTrungPhongHoc(String phongHoc, int thu, int inputTietBD, int inputSoTiet,
            LocalDate inputTuNgay, LocalDate inputDenNgay) {
        String sql = "SELECT COUNT(*) FROM LichDay WHERE PhongHoc = ? AND Thu = ? " +
                "AND (TuNgay <= ? AND DenNgay >= ?) " +
                "AND (TietBatDau <= ? AND (TietBatDau + SoTiet - 1) >= ?)";

        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, phongHoc);
            p.setInt(2, thu);
            p.setDate(3, java.sql.Date.valueOf(inputDenNgay));
            p.setDate(4, java.sql.Date.valueOf(inputTuNgay));
            p.setInt(5, inputTietBD + inputSoTiet - 1);
            p.setInt(6, inputTietBD);

            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    protected boolean addLichDay(String maNV, String mon, String phong, int thu, int tiet, int soTiet, LocalDate tuNgay,
            LocalDate denNgay) {
        String sql = "INSERT INTO LichDay (MaNV, TenMonHoc, PhongHoc, Thu, TietBatDau, SoTiet, TuNgay, DenNgay) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = ConnectDatabase.getConnection(); PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, maNV);
            p.setString(2, mon);
            p.setString(3, phong);
            p.setInt(4, thu);
            p.setInt(5, tiet);
            p.setInt(6, soTiet);
            p.setDate(7, java.sql.Date.valueOf(tuNgay));
            p.setDate(8, java.sql.Date.valueOf(denNgay));
            return p.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Object[][] getScheduleMatrix(String maNV, String monday, String sunday) {
        Object[][] data = new Object[15][8];
        for (int i = 0; i < 15; i++)
            data[i][0] = "Tiết " + (i + 1);

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static List<String> getDSMonHoc() {
        List<String> list = new ArrayList<>();
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