package com.nhom1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.MockedStatic;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class NhanSuDAOTest {

    private Connection mockConn;
    private PreparedStatement mockPstmt;
    private MockedStatic<ConnectDatabase> mockedDb;

    @BeforeEach
    void setUp() throws SQLException {
        mockConn = mock(Connection.class);
        mockPstmt = mock(PreparedStatement.class);
        mockedDb = mockStatic(ConnectDatabase.class);
        mockedDb.when(ConnectDatabase::getConnection).thenReturn(mockConn);
    }

    @AfterEach
    void tearDown() {
        mockedDb.close();
    }

    @ParameterizedTest(name = "{0}: {9}")
    @CsvFileSource(resources = "/nhan_su_test_data.csv", numLinesToSkip = 1)
    void testAddNhanVien_Validation(String testCaseID, String maNV, String hoTen, String ngaySinh, 
                                    double heSo, double luongCB, double phuCap, 
                                    boolean expectException, String expectedMsg, String description) throws SQLException {
        
        // Gia lap DB luon luu thanh cong cho case input hop le (TC_NS01)
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPstmt);
        when(mockPstmt.executeUpdate()).thenReturn(1);

        // Xu ly chuoi null tu CSV
        String inputMaNV = (maNV == null) ? "" : maNV;
        String inputHoTen = (hoTen == null) ? "" : hoTen;

        if (expectException) {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                // Cac truong ko quan trong ta fix cung du lieu gia (Nam, CNTT, Giang Vien...)
                NhanSuDAO.addNhanVien(inputMaNV, inputHoTen, ngaySinh, "Nam", "CNTT", 
                                      "Giảng Viên", "Cử nhân", "Biên chế", "Đang làm việc", 
                                      heSo, luongCB, phuCap, "avatar.png");
            });
            assertEquals(expectedMsg, exception.getMessage(), testCaseID + " sai thong diep loi!");
            System.out.println("Thanh cong: " + testCaseID + " [" + description + "] -> pass");
        } else {
            boolean result = NhanSuDAO.addNhanVien(inputMaNV, inputHoTen, ngaySinh, "Nam", "CNTT", 
                                                   "Giảng Viên", "Cử nhân", "Biên chế", "Đang làm việc", 
                                                   heSo, luongCB, phuCap, "avatar.png");
            assertTrue(result, testCaseID + " That bai: Du lieu chuan dang le phai return true!");
            System.out.println("Thanh cong: " + testCaseID + " [" + description + "] -> pass");
        }
    }
}