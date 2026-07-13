package com.nhom1;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import static org.junit.jupiter.api.Assertions.*;

public class LuongDAOTest {

    @ParameterizedTest(name = "{0}: {1}")
    @CsvFileSource(resources = "/luong_test_data.csv", numLinesToSkip = 1)
    public void testTinhLuong_DataDriven(String testCaseID, String description, 
                                         double luongCB, double heSo, double phuCap, 
                                         int soTiet, double donGiaTiet, 
                                         double expectedResult, boolean isValid,
                                         String expectedMsg) {
        
        if (!isValid) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                LuongDAO.tinhLuong(luongCB, heSo, phuCap, soTiet, donGiaTiet);
            }, testCaseID + " That bai: Khong nem ra ngoai le khi co tham so am");
            assertEquals(expectedMsg, exception.getMessage(), testCaseID + " sai thong diep loi!");
            System.out.println("Thanh cong: " + testCaseID + " [" + description + "] -> pass");
        } else {
            double actual = LuongDAO.tinhLuong(luongCB, heSo, phuCap, soTiet, donGiaTiet);
            assertEquals(expectedResult, actual, 0.001, testCaseID + " That bai: Sai so thuc linh");
            System.out.println("Thanh cong: " + testCaseID + " [" + description + "] -> pass");
        }
    }
}