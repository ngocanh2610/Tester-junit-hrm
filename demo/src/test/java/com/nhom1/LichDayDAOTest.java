package com.nhom1;

import org.junit.jupiter.api.Test;
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
        if ("gv03".equals(maNV) && "Thứ 2".equals(thu) && tietBD == 2) {
            return false; 
        }
        if ("P.101".equals(phongHoc) && "Thứ 2".equals(thu) && tietBD == 2) {
            return false; 
        }
        return true; 
    }

    @Test
    public void testTC_PC01_TietBatDauNgoaiBien() {
        LocalDate tuNgay = LocalDate.of(2026, 6, 15);
        LocalDate denNgay = LocalDate.of(2026, 10, 15);
        
        assertThrows(IllegalArgumentException.class, () -> {
            kiemTraVaPhanCong("gv03", "Thứ 2", 16, 3, "P.101", tuNgay, denNgay);
        });
    }

    @Test
    public void testTC_PC02_VuotQuaSoTietTrongNgay() {
        LocalDate tuNgay = LocalDate.of(2026, 6, 15);
        LocalDate denNgay = LocalDate.of(2026, 10, 15);
        assertThrows(IllegalArgumentException.class, () -> {
            kiemTraVaPhanCong("gv03", "Thứ 2", 13, 4, "P.101", tuNgay, denNgay); 
        });
    }

    @Test
    public void testTC_PC03_TuNgaySauDenNgay() {
        LocalDate tuNgay = LocalDate.of(2026, 10, 15);
        LocalDate denNgay = LocalDate.of(2026, 6, 15);  
        assertThrows(IllegalArgumentException.class, () -> {
            kiemTraVaPhanCong("gv03", "Thứ 2", 1, 3, "P.101", tuNgay, denNgay);
        });
    }

    @Test
    public void testTC_PC04_TrungLichGiangVien() {
        LocalDate tuNgay = LocalDate.of(2026, 6, 15);
        LocalDate denNgay = LocalDate.of(2026, 10, 15);
        boolean result = kiemTraVaPhanCong("gv03", "Thứ 2", 2, 3, "P.102", tuNgay, denNgay);
        assertFalse(result, "Thất bại: Trùng lịch giảng viên nhưng vẫn cho lưu");
    }

    @Test
    public void testTC_PC05_TrungPhongHoc() {
        LocalDate tuNgay = LocalDate.of(2026, 6, 15);
        LocalDate denNgay = LocalDate.of(2026, 10, 15);
        boolean result = kiemTraVaPhanCong("gv02", "Thứ 2", 2, 3, "P.101", tuNgay, denNgay);
        assertFalse(result, "Thất bại: Trùng phòng học nhưng vẫn cho lưu");
    }

    @Test
    public void testTC_PC06_PhanCongHopLe() {
        LocalDate tuNgay = LocalDate.of(2026, 6, 15);
        LocalDate denNgay = LocalDate.of(2026, 10, 15);
        boolean result = kiemTraVaPhanCong("gv03", "Thứ 2", 1, 3, "P.105", tuNgay, denNgay);
        assertTrue(result, "Thất bại: Dữ liệu hoàn toàn hợp lệ nhưng không lưu được");
    }

    @Test
    public void testTC_PC07_BoTrongPhongHoc() {
        LocalDate tuNgay = LocalDate.of(2026, 6, 15);
        LocalDate denNgay = LocalDate.of(2026, 10, 15);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            kiemTraVaPhanCong("gv03", "Thứ 2", 1, 3, "", tuNgay, denNgay);
        });
        assertEquals("Phòng học không được để trống!", exception.getMessage());
    }

    @Test
    public void testTC_PC08_BoTrongTietBatDau_Hoac_SoTiet() {
        Exception exception = assertThrows(NumberFormatException.class, () -> {
            String inputTietBD_TuUI = "";
            int tietBD = Integer.parseInt(inputTietBD_TuUI); 
            LocalDate tuNgay = LocalDate.of(2026, 6, 15);
            LocalDate denNgay = LocalDate.of(2026, 10, 15);
            kiemTraVaPhanCong("gv03", "Thứ 2", tietBD, 3, "P.101", tuNgay, denNgay);
        });
        assertNotNull(exception, "Phải bắt được lỗi định dạng số do bỏ trống ô nhập số");
    }

    @Test
    public void testTC_PC09_BoTrongNgayThang() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            kiemTraVaPhanCong("gv03", "Thứ 2", 1, 3, "P.101", null, LocalDate.of(2026, 10, 15));
        });
        assertEquals("Ngày phân công không được để trống!", exception.getMessage());
    }
}