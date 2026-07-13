package com.nhom1;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mockito;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class LichDayDAOTest {

    private LichDayDAO lichDayDAO;

    @ParameterizedTest(name = "{0}: {1}")
    @CsvFileSource(resources = "/lich_day_test_data.csv", numLinesToSkip = 1)
    public void testLichDay_DataDriven(String testCaseID, String description, 
                                       String maNV, String thu, String tietBDStr, String soTietStr, 
                                       String phongHoc, String tuNgayStr, String denNgayStr, 
                                       boolean expectException, String expectedMsg) {
        lichDayDAO = Mockito.spy(new LichDayDAO());
    
        boolean mockTrungLich = "TC_PC05".equals(testCaseID);
        boolean mockTrungPhong = "TC_PC06".equals(testCaseID);

        Mockito.doReturn(mockTrungLich).when(lichDayDAO)
            .checkTrungLichGiangVien(anyString(), anyInt(), anyInt(), anyInt(), any(), any());
        Mockito.doReturn(mockTrungPhong).when(lichDayDAO)
            .checkTrungPhongHoc(anyString(), anyInt(), anyInt(), anyInt(), any(), any());
        Mockito.doReturn(true).when(lichDayDAO)
            .addLichDay(anyString(), anyString(), anyString(), anyInt(), anyInt(), anyInt(), any(), any());
        
        try {
            if (tietBDStr == null || tietBDStr.trim().isEmpty() || soTietStr == null || soTietStr.trim().isEmpty()) {
                throw new NumberFormatException("Phai bat duoc loi dinh dang so do bo trong o nhap so");
            }
            int tietBD = Integer.parseInt(tietBDStr.trim());
            int soTiet = Integer.parseInt(soTietStr.trim());
            
            LocalDate tuNgay = (tuNgayStr == null || tuNgayStr.trim().isEmpty()) ? null : LocalDate.parse(tuNgayStr.trim());
            LocalDate denNgay = (denNgayStr == null || denNgayStr.trim().isEmpty()) ? null : LocalDate.parse(denNgayStr.trim());
            String phongHocParam = (phongHoc == null) ? "" : phongHoc;
            String tenMon = "Mon Test"; 

            if ("RETURN_TRUE".equals(expectedMsg) || "RETURN_FALSE".equals(expectedMsg)) {
                boolean result = lichDayDAO.kiemTraVaPhanCong(maNV, tenMon, thu, tietBD, soTiet, phongHocParam, tuNgay, denNgay);
                if ("RETURN_TRUE".equals(expectedMsg)) {
                    assertTrue(result, testCaseID + " That bai: Du lieu hop le dang le phai luu duoc!");
                } else {
                    assertFalse(result, testCaseID + " That bai: Trung lich/Phong dang le ko cho luu!");
                }
            } 
            else {
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    lichDayDAO.kiemTraVaPhanCong(maNV, tenMon, thu, tietBD, soTiet, phongHocParam, tuNgay, denNgay);
                });
                assertEquals(expectedMsg, exception.getMessage(), testCaseID + " sai thong diep loi!");
            }
            System.out.println("Thanh cong: " + testCaseID + " [" + description + "] -> pass");
        } catch (NumberFormatException e) {
            if ("NUMBER_FORMAT_EXCEPTION".equals(expectedMsg)) {
                assertNotNull(e.getMessage());
                System.out.println("Thanh cong: " + testCaseID + " [" + description + "] -> pass");
            } else {
                fail(testCaseID + " Bi loi dinh dang so ngoai y muon: " + e.getMessage());
            }
        }
    }
}