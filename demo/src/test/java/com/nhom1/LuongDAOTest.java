package com.nhom1;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import static org.junit.jupiter.api.Assertions.*;

public class LuongDAOTest {
    public double tinhLuong(double luongCB, double heSo, double phuCap, int soTiet, double donGiaTiet) {
        if (luongCB < 0 || heSo < 0 || phuCap < 0 || soTiet < 0 || donGiaTiet < 0) {
            throw new IllegalArgumentException("Dữ liệu đầu vào không được âm!");
        }
        return (luongCB * heSo) + phuCap + (soTiet * donGiaTiet);
    }

    @ParameterizedTest(name = "{0}: {1}")
    @CsvFileSource(resources = "/luong_test_data.csv", numLinesToSkip = 1)
    public void testTinhLuong_DataDriven(String testCaseID, String description, 
                                         double luongCB, double heSo, double phuCap, 
                                         int soTiet, double donGiaTiet, 
                                         double expectedResult, boolean isValid,
                                         String expectedMsg) {
        
        if (!isValid) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                tinhLuong(luongCB, heSo, phuCap, soTiet, donGiaTiet);
            }, testCaseID + " Thất bại: Không ném ra ngoại lệ khi có tham số âm");
            assertEquals(expectedMsg, exception.getMessage(), testCaseID + " sai thong diep loi!");
            System.out.println("Thanh cong: " + testCaseID + " [" + description + "] -> pass");
        } else {
            double actual = tinhLuong(luongCB, heSo, phuCap, soTiet, donGiaTiet);
            assertEquals(expectedResult, actual, 0.001, testCaseID + " Thất bại: Sai số thực lĩnh");
            System.out.println("Thanh cong: " + testCaseID + " [" + description + "] -> pass");
        }
    }
}