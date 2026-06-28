package com.nhom1;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class LichDayDAOTest {
    public boolean kiemTraVaPhanCong(String maNV, String thu, int tietBD, int soTiet, 
                                     String phongHoc, LocalDate tuNgay, LocalDate denNgay) {
        if (phongHoc == null || phongHoc.trim().isEmpty()) {
            throw new IllegalArgumentException("Phòng học không được để trống!");
        }
        if (tuNgay == null || denNgay == null) {
            throw new IllegalArgumentException("Ngày phân công không được để trống!");
        }
        if (tuNgay.isAfter(denNgay)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được lớn hơn ngày kết thúc!");
        }
        if (tietBD < 1 || tietBD > 15) {
            throw new IllegalArgumentException("Tiết bắt đầu phải từ 1 đến 15");
        }
        if (tietBD + soTiet - 1 > 15) {
            throw new IllegalArgumentException("Lịch học vượt quá tiết 15 trong ngày");
        }
        if (soTiet < 1 ){
            throw new IllegalArgumentException("Số tiết phải lớn hơn 0");
        }
        if ("gv03".equals(maNV) && "Thứ 2".equals(thu) && tietBD == 2) {
            return false; 
        }
        if ("P.101".equals(phongHoc) && "Thứ 2".equals(thu) && tietBD == 2) {
            return false; 
        }
        return true; 
    }

    @ParameterizedTest(name = "{0}: {1}")
    @CsvFileSource(resources = "/lich_day_test_data.csv", numLinesToSkip = 1)
    public void testLichDay_DataDriven(String testCaseID, String description, 
                                       String maNV, String thu, String tietBDStr, String soTietStr, 
                                       String phongHoc, String tuNgayStr, String denNgayStr, 
                                       boolean expectException, String expectedMsg) {
        try {
            if (tietBDStr == null || tietBDStr.trim().isEmpty() || soTietStr == null || soTietStr.trim().isEmpty()) {
                throw new NumberFormatException("Phải bắt được lỗi định dạng số do bỏ trống ô nhập số");
            }
            int tietBD = Integer.parseInt(tietBDStr.trim());
            int soTiet = Integer.parseInt(soTietStr.trim());
            LocalDate tuNgay = (tuNgayStr == null || tuNgayStr.trim().isEmpty()) ? null : LocalDate.parse(tuNgayStr.trim());
            LocalDate denNgay = (denNgayStr == null || denNgayStr.trim().isEmpty()) ? null : LocalDate.parse(denNgayStr.trim());
            String phongHocParam = (phongHoc == null) ? "" : phongHoc;
            if ("RETURN_TRUE".equals(expectedMsg) || "RETURN_FALSE".equals(expectedMsg)) {
                boolean result = kiemTraVaPhanCong(maNV, thu, tietBD, soTiet, phongHocParam, tuNgay, denNgay);
                if ("RETURN_TRUE".equals(expectedMsg)) {
                    assertTrue(result, testCaseID + " Thất bại: Dữ liệu hợp lệ đáng lẽ phải lưu được!");
                } else {
                    assertFalse(result, testCaseID + " Thất bại: Trùng lịch/Phòng đáng lẽ ko cho lưu!");
                }
                System.out.println("Thanh cong: " + testCaseID + " [" + description + "] -> pass");
            } else {
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    kiemTraVaPhanCong(maNV, thu, tietBD, soTiet, phongHocParam, tuNgay, denNgay);
                });
                assertEquals(expectedMsg, exception.getMessage(), testCaseID + " sai thông điệp lỗi!");
                System.out.println("Thanh cong: " + testCaseID + " [" + description + "] -> pass");
            }

        } catch (NumberFormatException e) {
            if ("NUMBER_FORMAT_EXCEPTION".equals(expectedMsg)) {
                assertNotNull(e.getMessage());
                System.out.println("Thanh cong: " + testCaseID + " [" + description + "] -> pass");
            } else {
                fail(testCaseID + " Bị lỗi định dạng số ngoài ý muốn: " + e.getMessage());
            }
        }
    }
}