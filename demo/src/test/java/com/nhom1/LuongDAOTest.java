package com.nhom1;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LuongDAOTest {
    public double tinhLuong(double luongCB, double heSo, double phuCap, int soTiet, double donGiaTiet) {
        if (luongCB < 0 || heSo < 0 || phuCap < 0 || soTiet < 0 || donGiaTiet < 0) {
            throw new IllegalArgumentException("Dữ liệu đầu vào không được âm!");
        }
        return (luongCB * heSo) + phuCap + (soTiet * donGiaTiet);
    }

    @Test
    public void testTC_L01_LuongCBAm() {
        assertThrows(IllegalArgumentException.class, () -> {
            tinhLuong(-100000000, 1.0, 0, 10, 150000);
        }, "TC-L01 Thất bại: Không ném ra ngoại lệ khi Lương CB âm");
    }

    @Test
    public void testTC_L02_HeSoAm() {
        assertThrows(IllegalArgumentException.class, () -> {
            tinhLuong(200000000, -0.5, 0, 10, 150000);
        }, "TC-L02 Thất bại: Không ném ra ngoại lệ khi Hệ số âm");
    }

    @Test
    public void testTC_L03_SoTietAm() {
        assertThrows(IllegalArgumentException.class, () -> {
            tinhLuong(200000000, 1.0, 0, -5, 150000);
        }, "TC-L03 Thất bại: Không ném ra ngoại lệ khi Số tiết âm");
    }

    @Test
    public void testTC_L04_DonGiaAm() {
        assertThrows(IllegalArgumentException.class, () -> {
            tinhLuong(200000000, 1.0, 0, 10, -50000);
        }, "TC-L04 Thất bại: Không ném ra ngoại lệ khi Đơn giá âm");
    }

    @Test
    public void testTC_L05_NhanVienBienChe() {
        double expected = 21000000.0;
        double actual = tinhLuong(10000000, 2.0, 1000000, 0, 150000);
        
        assertEquals(expected, actual, 0.001, "TC-L05 Sai số thực lĩnh của nhân viên biên chế");
    }

    @Test
    public void testTC_L06_GiangVienHopDong() {
        double expected = 8000000.0;
        double actual = tinhLuong(0, 0.0, 0, 40, 200000);
        
        assertEquals(expected, actual, 0.001, "TC-L06 Sai số thực lĩnh của giảng viên hợp đồng");
    }

    @Test
    public void testTC_L07_NhanVienHonHop() {
        double expected = 200000000.0;
        double actual = tinhLuong(200000000, 1.0, 0, 0, 0);
        
        assertEquals(expected, actual, 0.001, "TC-L07 Sai số thực lĩnh thực tế của gv02");
    }

    @Test
    public void testTC_L08_GiaTriBienBangKhong() {
        double expected = 0.0;
        double actual = tinhLuong(0, 0, 0, 0, 0);
        
        assertEquals(expected, actual, 0.001, "TC-L08 Giá trị biên bằng 0 phải trả về kết quả bằng 0");
    }
}