package com.nhom1;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class TaiKhoanDAOTest {

    @Test
    @DisplayName("Đăng nhập thành công với tài khoản hợp lệ")
    void testLogin_Success() {
        assertTrue(TaiKhoanDAO.checkLogin("admin", "123"), "Admin phải đăng nhập thành công");
    }

    @Test
    @DisplayName("Sai mật khẩu")
    void testLogin_WrongPass() {
        assertFalse(TaiKhoanDAO.checkLogin("admin", "111"), "Sai mật khẩu phải trả về false");
    }

    @Test
    @DisplayName("Sai tên đăng nhập")
    void testLogin_WrongUser() {
        assertFalse(TaiKhoanDAO.checkLogin("adddd", "123"), "Sai tên đăng nhập phải trả về false");
    }

    @Test
    @DisplayName("Để trống tài khoản hoặc mật khẩu")
    void testLogin_EmptyFields() {
        assertFalse(TaiKhoanDAO.checkLogin("", "123"), "Để trống phải trả về false");
        assertFalse(TaiKhoanDAO.checkLogin("admin", ""), "Trống mật khẩu phải trả về false");
    }

    @Test
    @DisplayName("Tham số null")
    void testLogin_NullFields() {
        assertFalse(TaiKhoanDAO.checkLogin(null, null), "Truyền null phải trả về false hoặc không crash");
    }
}