package com.nhom1;

import org.junit.jupiter.api.*;
import javax.swing.table.DefaultTableModel;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KhoaDAOTest {
    private final String T_KHOA = "f_test"; 
    private final String T_MON = "m1_test"; 
    private final String KHOA_CO_SAN = "a"; 
    private final String MON_CO_SAN = "g";  

    @Test
    @Order(1)
    @DisplayName("TC_K01: Lấy danh sách Khoa")
    public void testTC_K01() {
        DefaultTableModel model = KhoaDAO.getDSKhoa();
        assertNotNull(model, "Bảng danh sách khoa không được rỗng");
        assertTrue(model.getRowCount() > 0, "Phải lấy ra được dữ liệu (ví dụ: a, c, e)");
    }

    @Test
    @Order(2)
    @DisplayName("TC_K02: Thêm Khoa MỚI hợp lệ")
    public void testTC_K02() {
        KhoaDAO.deleteKhoa(T_KHOA); 
        boolean isAdded = KhoaDAO.addKhoa(T_KHOA, "Khoa Mới");
        assertTrue(isAdded, "Thêm mã khoa mới phải trả về true");
    }

    @Test
    @Order(3)
    @DisplayName("TC_K03: Thêm Khoa TRÙNG MÃ")
    public void testTC_K03() {
        boolean isAdded = KhoaDAO.addKhoa(KHOA_CO_SAN, "Khoa Test");
        assertFalse(isAdded, "Thêm mã khoa 'a' đã tồn tại phải bị từ chối và trả về false");
    }

    @Test
    @Order(4)
    @DisplayName("TC_K04: Thêm Khoa bỏ trống")
    public void testTC_K04() {
        boolean isAdded = KhoaDAO.addKhoa("", "");
        assertFalse(isAdded, "Bỏ trống mã khoa phải trả về false (Yêu cầu DB chặn rỗng)");
    }

    @Test
    @Order(5)
    @DisplayName("TC_K05: Cập nhật Khoa")
    public void testTC_K05() {
        boolean isUpdated = KhoaDAO.updateKhoa(T_KHOA, "Khoa Đổi Tên");
        assertTrue(isUpdated, "Cập nhật Khoa đang tồn tại phải thành công");
    }

    @Test
    @Order(6)
    @DisplayName("TC_K06: Xóa Khoa")
    public void testTC_K06() {
        boolean isDeleted = KhoaDAO.deleteKhoa(T_KHOA);
        assertTrue(isDeleted, "Xóa Khoa tồn tại phải thành công");
    }

    @Test
    @Order(7)
    @DisplayName("TC_M01: Xem danh sách Môn theo Khoa")
    public void testTC_M01() {
        DefaultTableModel model = KhoaDAO.getDSMonHoc(KHOA_CO_SAN);
        assertNotNull(model);
        assertTrue(model.getRowCount() > 0, "Khoa 'a' đang có môn học thì phải trả về > 0 dòng");
    }

    @Test
    @Order(8)
    @DisplayName("TC_M02: Thêm Môn MỚI hợp lệ")
    public void testTC_M02() {
        KhoaDAO.deleteMonHoc(T_MON);
        boolean isAdded = KhoaDAO.addMonHoc(T_MON, "Test", 3, KHOA_CO_SAN);
        assertTrue(isAdded, "Thêm môn học mới phải thành công");
    }

    @Test
    @Order(9)
    @DisplayName("TC_M03: Thêm Môn TRÙNG MÃ")
    public void testTC_M03() {
        boolean isAdded = KhoaDAO.addMonHoc(MON_CO_SAN, "Khác", 2, KHOA_CO_SAN);
        assertFalse(isAdded, "Thêm trùng mã môn 'g' phải trả về false");
    }

    @Test
    @Order(10)
    @DisplayName("TC_M04: Thêm Môn sai kiểu Tín Chỉ (nhập chữ cái)")
    public void testTC_M04() {
        Exception exception = assertThrows(NumberFormatException.class, () -> {
            String inputTuGiaoDien = "chữ cái";
            int tcSaiKieu = Integer.parseInt(inputTuGiaoDien); 
            
            KhoaDAO.addMonHoc("m2", "Toán", tcSaiKieu, KHOA_CO_SAN);
        });
        assertNotNull(exception, "Phải bắt được lỗi định dạng số");
    }

    @Test
    @Order(11)
    @DisplayName("TC_M05: Cập nhật Môn học")
    public void testTC_M05() {
        boolean isUpdated = KhoaDAO.updateMonHoc(T_MON, "R+", 4);
        assertTrue(isUpdated, "Cập nhật môn học tồn tại phải thành công");
    }

    @Test
    @Order(12)
    @DisplayName("TC_M06: Xóa Môn học")
    public void testTC_M06() {
        boolean isDeleted = KhoaDAO.deleteMonHoc(T_MON);
        assertTrue(isDeleted, "Xóa môn học tồn tại phải thành công");
    }
}