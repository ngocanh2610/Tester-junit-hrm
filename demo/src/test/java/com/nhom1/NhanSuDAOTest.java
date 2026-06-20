
package com.nhom1;

import org.junit.jupiter.api.*;
import javax.swing.table.DefaultTableModel;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NhanSuDAOTest {

    private final String TEST_EMP_ID = "NV_TEST_01";

    @Test
    @Order(1)
    @DisplayName("TC-NS01: Lấy toàn bộ danh sách nhân viên")
    public void testGetNhanVienModel_Success() {
        DefaultTableModel model = NhanSuDAO.getNhanVienModel();
        assertNotNull(model, "Model không được phép null");
        assertTrue(model.getColumnCount() > 0, "Model phải có các cột dữ liệu");
    }

    @Test
    @Order(2)
    @DisplayName("TC-NS02: Tìm nhân viên theo mã TỒN TẠI")
    public void testGetNhanVienByMa_Found() {
        String validId = "NV001"; 
        DefaultTableModel model = NhanSuDAO.getNhanVienByMa(validId);
        assertNotNull(model);
        assertEquals(1, model.getRowCount(), "Phải tìm thấy chính xác 1 nhân viên");
        assertEquals(validId, model.getValueAt(0, 0), "Mã nhân viên trả về phải khớp với mã tìm kiếm");
    }

    @Test
    @Order(3)
    @DisplayName("TC-NS03: Tìm nhân viên theo mã KHÔNG TỒN TẠI")
    public void testGetNhanVienByMa_NotFound() {
        DefaultTableModel model = NhanSuDAO.getNhanVienByMa("ID_KHONG_CO_THUC");
        
        assertNotNull(model);
        assertEquals(0, model.getRowCount(), "Không tìm thấy thì số dòng phải bằng 0");
    }

    @Test
    @Order(4)
    @DisplayName("TC-NS04: Thêm nhân viên MỚI hợp lệ")
    public void testAddNhanVien_Success() {
        NhanSuDAO.deleteNhanVien(TEST_EMP_ID);
        boolean isAdded = NhanSuDAO.addNhanVien(
                TEST_EMP_ID, "Nguyen Van Test", "1999-01-01", "Nam", "a",
                "Giảng Viên", "Cử nhân", "Cơ hữu (Biên chế)", "Đang làm việc",
                1.0, 0.0, 0.0, "avatar.png"
        );
        assertTrue(isAdded, "Thêm nhân viên mới đầy đủ dữ liệu phải trả về true");
    }

    @Test
    @Order(5)
    @DisplayName("TC-NS05: Lỗi thêm nhân viên TRÙNG MÃ")
    public void testAddNhanVien_DuplicateID() {
        boolean isAdded = NhanSuDAO.addNhanVien(
                TEST_EMP_ID, "Ten Khac", "1999-01-01", "Nam", "a",
                "Giảng Viên", "Cử nhân", "Cơ hữu (Biên chế)", "Đang làm việc",
                1.0, 0.0, 0.0, "avatar.png"
        );
        assertFalse(isAdded, "Thêm trùng mã (Khóa chính) phải bị catch Exception và trả về false");
    }

    @Test
    @Order(6)
    @DisplayName("TC-NS06: Cập nhật nhân viên TỒN TẠI")
    public void testUpdateNhanVien_Success() {
        boolean isUpdated = NhanSuDAO.updateNhanVien(
                TEST_EMP_ID, "Nguyen Van Test Updated", "1999-01-01", "Nam", "a",
                "Giảng Viên", "Cử nhân", "Cơ hữu (Biên chế)", "Đang làm việc",
                2.0, 5000000.0, 1000000.0, "avatar_new.png"
        );
        assertTrue(isUpdated, "Cập nhật nhân viên đang tồn tại phải trả về true");
    }

    @Test
    @Order(7)
    @DisplayName("TC-NS07: Cập nhật nhân viên KHÔNG TỒN TẠI")
    public void testUpdateNhanVien_NotExists() {
        boolean isUpdated = NhanSuDAO.updateNhanVien(
                "ID_ẢO", "Nguoi Vo Danh", "1999-01-01", "Nam", "a",
                "Giảng Viên", "Cử nhân", "Cơ hữu (Biên chế)", "Đang làm việc",
                1.0, 0.0, 0.0, "avatar.png"
        );
        assertFalse(isUpdated, "Cập nhật nhân viên không có trong DB phải trả về false");
    }

    @Test
    @Order(8)
    @DisplayName("TC-NS08: Xóa nhân viên TỒN TẠI")
    public void testDeleteNhanVien_Success() {
        boolean isDeleted = NhanSuDAO.deleteNhanVien(TEST_EMP_ID);
        assertTrue(isDeleted, "Xóa nhân viên có thực phải trả về true");
        DefaultTableModel model = NhanSuDAO.getNhanVienByMa(TEST_EMP_ID);
        assertEquals(0, model.getRowCount(), "Nhân viên sau khi xóa không thể tra cứu được nữa");
    }

    @Test
    @Order(9)
    @DisplayName("TC-NS09: Xóa nhân viên KHÔNG TỒN TẠI")
    public void testDeleteNhanVien_NotExists() {
        boolean isDeleted = NhanSuDAO.deleteNhanVien("ID_DELETED_ALREADY");
        assertFalse(isDeleted, "Xóa ID không có trong DB phải trả về false");
    }
}
