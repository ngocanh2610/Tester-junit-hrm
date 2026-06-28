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

public class TaiKhoanDAOTest {

    private Connection mockConn;
    private PreparedStatement mockPstmt;
    private ResultSet mockRs;
    private MockedStatic<ConnectDatabase> mockedDb;

    @BeforeEach
    void setUp() throws SQLException {
        mockConn = mock(Connection.class);
        mockPstmt = mock(PreparedStatement.class);
        mockRs = mock(ResultSet.class);
        
        mockedDb = mockStatic(ConnectDatabase.class);
        mockedDb.when(ConnectDatabase::getConnection).thenReturn(mockConn);
    }

    @AfterEach
    void tearDown() {
        mockedDb.close(); 
    }

    @ParameterizedTest(name = "{0}: {5}")
    @CsvFileSource(resources = "/tai_khoan_test_data.csv", numLinesToSkip = 1)
    void testLogin_DataDriven(String testCaseID, String username, String password, 
                              boolean dbFound, boolean expectedResult, String description) throws SQLException {
        
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPstmt);
        when(mockPstmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(dbFound);

        String user = (username != null && username.isEmpty()) ? null : username;
        String pass = (password != null && password.isEmpty()) ? null : password;

        boolean actualResult = TaiKhoanDAO.checkLogin(user, pass);

        assertEquals(expectedResult, actualResult, testCaseID + " chay sai logic!");
        
        System.out.println("Thanh cong: " + testCaseID + " [" + description + "]");
    }
}