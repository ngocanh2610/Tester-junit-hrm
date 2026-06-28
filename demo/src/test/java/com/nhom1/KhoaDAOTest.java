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

public class KhoaDAOTest {

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

    @ParameterizedTest(name = "{0}: {10}")
    @CsvFileSource(resources = "/khoa_mon_test_data.csv", numLinesToSkip = 1)
    public void testKhoaVaMonHoc_DataDriven(String testCaseID, String type, String action,
                                           String param1, String param2, String param3, String param4,
                                           Integer dbResult, String dbError, boolean expectedResult, 
                                           String description) throws SQLException {
        
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPstmt);
        
        if (dbError != null && !dbError.trim().isEmpty()) {
            when(mockPstmt.executeUpdate()).thenThrow(new SQLException(dbError));
        } else {
            int resultCount = 0;
            if (dbResult != null) {
                resultCount = dbResult;
            }
            when(mockPstmt.executeUpdate()).thenReturn(resultCount);
        }

        String p1 = (param1 == null) ? "" : param1;
        String p2 = (param2 == null) ? "" : param2;
        boolean actualResult = false;

        if ("KHOA".equals(type)) {
            if ("ADD".equals(action)) {
                actualResult = KhoaDAO.addKhoa(p1, p2);
            } else if ("UPDATE".equals(action)) {
                actualResult = KhoaDAO.updateKhoa(p1, p2);
            } else if ("DELETE".equals(action)) {
                actualResult = KhoaDAO.deleteKhoa(p1);
            }
        } else if ("MON".equals(type)) {
            int tinChi = 0;
            if (param3 != null && !param3.trim().isEmpty()) {
                try {
                    tinChi = (int) Double.parseDouble(param3.trim());
                } catch (NumberFormatException e) {
                    tinChi = 0;
                }
            }
            String maKhoa = (param4 == null) ? "" : param4;
            if ("ADD".equals(action)) {
                actualResult = KhoaDAO.addMonHoc(p1, p2, tinChi, maKhoa);
            } else if ("UPDATE".equals(action)) {
                actualResult = KhoaDAO.updateMonHoc(p1, p2, tinChi);
            } else if ("DELETE".equals(action)) {
                actualResult = KhoaDAO.deleteMonHoc(p1);
            }
        }
        assertEquals(expectedResult, actualResult, testCaseID + " that bai");
        System.out.println("Thanh cong: " + testCaseID + " [" + description + "] -> pass");
    }
}