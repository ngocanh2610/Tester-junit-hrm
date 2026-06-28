package com.nhom1;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class EmployeeDialog extends JDialog {

    private JTextField txtMaNV, txtHoTen, txtNgaySinh;
    private JTextField txtHeSo, txtLuongCB, txtPhuCap;
    private JComboBox<String> cboChucVu, cboPhongBan, cboTrangThai;
    private JComboBox<String> cboTrinhDo, cboLoaiHinh;
    
    private JRadioButton rdoNam, rdoNu;
    private JLabel lblHinhAnh; 
    private String tenFileHinh = ""; 
    
    private boolean isSaved = false;
    private boolean isEditMode = false;

    private final Color PRIMARY_COLOR = new Color(46, 204, 113); 
    private final Color EDIT_COLOR = new Color(52, 152, 219);    

    public EmployeeDialog(JFrame parent) { this(parent, null); }

    public EmployeeDialog(JFrame parent, String[] data) {
        super(parent, data == null ? "Thêm Mới Nhân Sự" : "Cập Nhật Hồ Sơ", true);
        setSize(950, 680);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        if (data != null) isEditMode = true;

        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(isEditMode ? EDIT_COLOR : PRIMARY_COLOR);
        pnlHeader.setPreferredSize(new Dimension(getWidth(), 50));
        
        JLabel lblTitle = new JLabel(isEditMode ? "CẬP NHẬT HỒ SƠ NHÂN VIÊN" : "THÊM MỚI NHÂN VIÊN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle);
        this.add(pnlHeader, BorderLayout.NORTH);

        JPanel pnlContent = new JPanel(new BorderLayout(15, 15));
        pnlContent.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel pnlForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaNV = new JTextField();
        txtHoTen = new JTextField();
        txtNgaySinh = new JTextField();
        
        rdoNam = new JRadioButton("Nam"); rdoNu = new JRadioButton("Nữ");
        ButtonGroup bg = new ButtonGroup(); bg.add(rdoNam); bg.add(rdoNu);
        JPanel pnlGender = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); 
        pnlGender.add(rdoNam); pnlGender.add(Box.createHorizontalStrut(20)); pnlGender.add(rdoNu);
        rdoNam.setSelected(true);

        cboPhongBan = new JComboBox<>();
        loadPhongBanData();
        
        cboChucVu = new JComboBox<>(new String[]{"Giảng Viên", "Trợ Giảng", "Hiệu Trưởng", "Phó Hiệu Trưởng", "Chuyên Viên", "Thực Tập Sinh", "Trưởng Khoa"});
        cboTrinhDo = new JComboBox<>(new String[]{"Cử nhân", "Kỹ sư", "Thạc sĩ", "Tiến sĩ", "Phó Giáo sư", "Giáo sư"});
        cboLoaiHinh = new JComboBox<>(new String[]{"Cơ hữu (Biên chế)", "Hợp đồng dài hạn", "Thỉnh giảng"});
        cboTrangThai = new JComboBox<>(new String[]{"Đang làm việc", "Đã nghỉ việc", "Tạm hoãn HĐ"});

        txtHeSo = new JTextField("1.0");
        txtLuongCB = new JTextField("0");
        txtPhuCap = new JTextField("0");

        addSectionHeader(pnlForm, "1. THÔNG TIN CÁ NHÂN", 0, gbc);
        addFormRow(pnlForm, "Mã Nhân Viên (*):", txtMaNV, 1, gbc);
        addFormRow(pnlForm, "Họ và Tên (*):", txtHoTen, 2, gbc);
        addFormRow(pnlForm, "Ngày Sinh (yyyy-mm-dd):", txtNgaySinh, 3, gbc);
        addFormRow(pnlForm, "Giới Tính:", pnlGender, 4, gbc);

        addSectionHeader(pnlForm, "2. THÔNG TIN CÔNG VIỆC", 5, gbc);
        addFormRow(pnlForm, "Phòng / Khoa:", cboPhongBan, 6, gbc);
        addFormRow(pnlForm, "Chức Vụ:", cboChucVu, 7, gbc);
        addFormRow(pnlForm, "Trình Độ:", cboTrinhDo, 8, gbc);
        addFormRow(pnlForm, "Loại Hình:", cboLoaiHinh, 9, gbc);
        addFormRow(pnlForm, "Trạng Thái:", cboTrangThai, 10, gbc);

        addSectionHeader(pnlForm, "3. THÔNG TIN LƯƠNG & THƯỞNG", 11, gbc);
        addFormRow(pnlForm, "Hệ Số Lương:", txtHeSo, 12, gbc);
        addFormRow(pnlForm, "Lương Cơ Bản (VNĐ):", txtLuongCB, 13, gbc);
        addFormRow(pnlForm, "Phụ Cấp (VNĐ):", txtPhuCap, 14, gbc);

        gbc.weighty = 1.0; 
        gbc.gridy = 15;
        pnlForm.add(new JLabel(), gbc);

        JPanel pnlRight = new JPanel(new BorderLayout());
        pnlRight.setPreferredSize(new Dimension(220, 0));
        pnlRight.setBorder(BorderFactory.createTitledBorder("Ảnh Đại Diện"));

        lblHinhAnh = new JLabel("Click để chọn ảnh");
        lblHinhAnh.setHorizontalAlignment(SwingConstants.CENTER);
        lblHinhAnh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblHinhAnh.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        lblHinhAnh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { chonAnh(); }
        });

        pnlRight.add(lblHinhAnh, BorderLayout.CENTER);
        
        pnlContent.add(new JScrollPane(pnlForm), BorderLayout.CENTER);
        pnlContent.add(pnlRight, BorderLayout.EAST);
        this.add(pnlContent, BorderLayout.CENTER);

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlBtn.setBackground(new Color(245, 245, 245));
        
        JButton btnSave = new JButton(isEditMode ? "LƯU CẬP NHẬT" : "LƯU HỒ SƠ");
        JButton btnCancel = new JButton("ĐÓNG");
        
        btnSave.setPreferredSize(new Dimension(150, 40));
        btnSave.setBackground(isEditMode ? EDIT_COLOR : PRIMARY_COLOR);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Arial", Font.BOLD, 12));
        btnSave.setFocusPainted(false);

        btnCancel.setPreferredSize(new Dimension(100, 40));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setFocusPainted(false);

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());

        pnlBtn.add(btnCancel); pnlBtn.add(btnSave);
        this.add(pnlBtn, BorderLayout.SOUTH);

        if (isEditMode) loadOldData(data);
    }
    
    private void addSectionHeader(JPanel panel, String title, int row, GridBagConstraints gbc) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(44, 62, 80));
        lbl.setBorder(new EmptyBorder(15, 0, 5, 0));
        
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(lbl, gbc);
        
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.CENTER;
    }

    private void addFormRow(JPanel panel, String labelText, Component field, int row, GridBagConstraints gbc) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST; 
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.gridy = row;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(field, gbc);
    }

    private void chonAnh() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Hình ảnh (jpg, png)", "jpg", "png", "jpeg"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            XImage.save(file); 
            setHinhAnh(file.getName()); 
        }
    }

    private void setHinhAnh(String fileName) {
        ImageIcon icon = XImage.read(fileName); 
        if (icon != null) {
            lblHinhAnh.setIcon(XImage.resize(icon, 180, 220)); 
            lblHinhAnh.setText(""); 
            this.tenFileHinh = fileName;
        }
    }

    private void loadPhongBanData() {
        for (String pb : NhanSuDAO.getKhoaList()) cboPhongBan.addItem(pb);
    }

    private void loadOldData(String[] data) {
        try {
            txtMaNV.setText(data[0]); 
            txtMaNV.setEditable(false);
            txtMaNV.setBackground(new Color(240,240,240));
            
            txtHoTen.setText(data[1]);
            txtNgaySinh.setText(data[2]);
            
            for(int i=0; i<cboPhongBan.getItemCount(); i++) 
                if(cboPhongBan.getItemAt(i).contains(data[3])) { cboPhongBan.setSelectedIndex(i); break; }
            
            cboChucVu.setSelectedItem(data[4]);
            cboTrinhDo.setSelectedItem(data[5]);
            cboLoaiHinh.setSelectedItem(data[6]);
            cboTrangThai.setSelectedItem(data[7]);
            if(data.length > 8) txtHeSo.setText(data[8]);
            if(data.length > 9) txtLuongCB.setText(data[9].replace(",", "").replace(" VNĐ", ""));
            if(data.length > 10) txtPhuCap.setText(data[10].replace(",", "").replace(" VNĐ", ""));
            if (data.length > 0) {
                String hinh = data[data.length - 1]; 
                if (hinh != null && hinh.length() > 0) setHinhAnh(hinh);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void save() {
        String ma = txtMaNV.getText().trim();
        String ten = txtHoTen.getText().trim();
        String ngaySinh = txtNgaySinh.getText().trim(); 
        if(ngaySinh.isEmpty()) ngaySinh = null;
        String gioiTinh = rdoNam.isSelected() ? "Nam" : "Nữ";
        String chucVu = cboChucVu.getSelectedItem().toString();
        String trinhDo = cboTrinhDo.getSelectedItem().toString();
        String loaiHinh = cboLoaiHinh.getSelectedItem().toString();
        String trangThai = cboTrangThai.getSelectedItem().toString();
        String pbStr = cboPhongBan.getSelectedItem() != null ? cboPhongBan.getSelectedItem().toString() : null;
        String maPB = pbStr != null ? pbStr.split("-")[0].trim() : null;
        if (ma.isEmpty() || ten.isEmpty()) { 
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã và Tên nhân viên!", "Cảnh báo", JOptionPane.WARNING_MESSAGE); 
            return; 
        }
        if (ngaySinh != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate inputDate = LocalDate.parse(ngaySinh, formatter);
                LocalDate currentDate = LocalDate.now();

                if (inputDate.isAfter(currentDate)) {
                    JOptionPane.showMessageDialog(this, "Ngày sinh không được lớn hơn ngày hiện tại!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Định dạng ngày sinh không đúng! Vui lòng nhập theo định dạng yyyy-mm-dd (Ví dụ: 1990-12-30).", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        double heSo = 0, luongCB = 0, phuCap = 0;
        try {
            heSo = Double.parseDouble(txtHeSo.getText().trim());
            luongCB = Double.parseDouble(txtLuongCB.getText().replace(",", "").trim());
            phuCap = Double.parseDouble(txtPhuCap.getText().replace(",", "").trim());
            
            if (heSo < 0 || luongCB < 0 || phuCap < 0) {
                JOptionPane.showMessageDialog(this, "Hệ số, Lương cơ bản và Phụ cấp không được là số âm!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) { 
            JOptionPane.showMessageDialog(this, "Hệ số, Lương cơ bản và Phụ cấp bắt buộc phải là số!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE); 
            return; 
        }
        boolean result;
        if (isEditMode) {
            result = NhanSuDAO.updateNhanVien(ma, ten, ngaySinh, gioiTinh, maPB, chucVu, trinhDo, loaiHinh, trangThai, heSo, luongCB, phuCap, tenFileHinh);
        } else {
            result = NhanSuDAO.addNhanVien(ma, ten, ngaySinh, gioiTinh, maPB, chucVu, trinhDo, loaiHinh, trangThai, heSo, luongCB, phuCap, tenFileHinh);
        }

        if (result) { 
            JOptionPane.showMessageDialog(this, "Lưu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE); 
            isSaved = true; 
            dispose(); 
        } else { 
            JOptionPane.showMessageDialog(this, "Lưu thất bại! (Mã nhân viên có thể đã tồn tại hoặc lỗi CSDL)", "Lỗi", JOptionPane.ERROR_MESSAGE); 
        }
    }
    
    public boolean isSaved() { return isSaved; }
}