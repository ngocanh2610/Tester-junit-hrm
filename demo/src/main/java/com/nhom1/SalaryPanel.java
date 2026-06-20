package com.nhom1;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.time.LocalDate;
import javax.swing.JFileChooser;
import java.io.File;
import java.net.URL;

public class SalaryPanel extends JPanel {

    private JTable tblNhanVien;
    private DefaultTableModel modelNV;
    
    // Inputs
    private JTextField txtMa, txtTen, txtLoaiHinh;
    private JTextField txtLuongCB, txtHeSo, txtPhuCap;
    private JTextField txtSoTiet, txtDonGia;
    private JLabel lblTongLuong;
    
    private JComboBox<Integer> cboThang;
    private JTextField txtNam;

    // Biến lưu giá trị tính toán
    private double curLuongCung = 0, curThuLao = 0, curThucLinh = 0;

    // COLORS
    private final Color COL_PRIMARY = new Color(0, 150, 136);
    private final Color COL_BG = new Color(245, 247, 250);

    public SalaryPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(COL_BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- 1. TOP CARD: TOOLBAR ---
        JPanel pnlToolbar = createCardPanel();
        pnlToolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        
        cboThang = new JComboBox<>();
        for(int i=1; i<=12; i++) cboThang.addItem(i);
        cboThang.setSelectedItem(LocalDate.now().getMonthValue());
        
        txtNam = new JTextField(String.valueOf(LocalDate.now().getYear()), 4);
        txtNam.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtNam.setHorizontalAlignment(JTextField.CENTER);
        
        JButton btnReload = createBtn("Tải DS", new Color(52, 152, 219), "refresh.png");
        JButton btnExcel = createBtn("Xuất Excel", new Color(39, 174, 96), "excel.png");

        pnlToolbar.add(new JLabel("Tháng:")); pnlToolbar.add(cboThang);
        pnlToolbar.add(new JLabel("Năm:")); pnlToolbar.add(txtNam);
        pnlToolbar.add(Box.createHorizontalStrut(20));
        pnlToolbar.add(btnReload); pnlToolbar.add(btnExcel);
        
        add(pnlToolbar, BorderLayout.NORTH);

        // --- 2. MAIN CONTENT (Split: Left List - Right Form) ---
        JPanel pnlCenter = new JPanel(new BorderLayout(15, 0));
        pnlCenter.setOpaque(false);

        // A. LEFT: LIST
        JPanel pnlLeft = createCardPanel();
        pnlLeft.setPreferredSize(new Dimension(500, 0));
        addHeader(pnlLeft, "1. DANH SÁCH NHÂN SỰ", "staff.png");
        
        tblNhanVien = createModernTable();
        pnlLeft.add(new JScrollPane(tblNhanVien), BorderLayout.CENTER);
        pnlCenter.add(pnlLeft, BorderLayout.CENTER);

        // B. RIGHT: DETAIL FORM
        JPanel pnlRight = createCardPanel();
        pnlRight.setPreferredSize(new Dimension(400, 0));
        addHeader(pnlRight, "2. TÍNH LƯƠNG CHI TIẾT", "calculator.png");
        
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(new EmptyBorder(10, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5); gbc.fill = GridBagConstraints.HORIZONTAL;

        // Init Fields
        txtMa = createReadOnlyField(); txtTen = createReadOnlyField(); txtLoaiHinh = createReadOnlyField();
        txtLuongCB = createField(); txtHeSo = createField(); txtPhuCap = createField();
        txtSoTiet = createField(); txtDonGia = createField(); txtDonGia.setText("50000");

        addSection(pnlForm, "Thông Tin Chung", 0, gbc);
        addLabel(pnlForm, "Mã NV:", 1, gbc); addComp(pnlForm, txtMa, 1, gbc);
        addLabel(pnlForm, "Họ Tên:", 2, gbc); addComp(pnlForm, txtTen, 2, gbc);
        addLabel(pnlForm, "Loại Hình:", 3, gbc); addComp(pnlForm, txtLoaiHinh, 3, gbc);

        addSection(pnlForm, "Lương Cố Định", 4, gbc);
        addLabel(pnlForm, "Lương CB:", 5, gbc); addComp(pnlForm, txtLuongCB, 5, gbc);
        addLabel(pnlForm, "Hệ Số:", 6, gbc); addComp(pnlForm, txtHeSo, 6, gbc);
        addLabel(pnlForm, "Phụ Cấp (+NCKH):", 7, gbc); addComp(pnlForm, txtPhuCap, 7, gbc);

        addSection(pnlForm, "Thù Lao Giảng Dạy", 8, gbc);
        addLabel(pnlForm, "Số Tiết:", 9, gbc); addComp(pnlForm, txtSoTiet, 9, gbc);
        addLabel(pnlForm, "Đơn Giá:", 10, gbc); addComp(pnlForm, txtDonGia, 10, gbc);

        pnlRight.add(new JScrollPane(pnlForm), BorderLayout.CENTER);

        // Footer Actions
        JPanel pnlAction = new JPanel(new GridLayout(2, 1, 10, 10));
        pnlAction.setBackground(new Color(245, 245, 245));
        pnlAction.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        lblTongLuong = new JLabel("0 VNĐ", SwingConstants.CENTER);
        lblTongLuong.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTongLuong.setForeground(new Color(231, 76, 60));
        
        JPanel pBtns = new JPanel(new GridLayout(1, 2, 10, 0));
        pBtns.setOpaque(false);
        JButton btnTinh = createBtn("TÍNH TOÁN", new Color(46, 204, 113), "calculator.png");
        JButton btnLuu = createBtn("LƯU KẾT QUẢ", new Color(52, 152, 219), "save.png");
        btnLuu.setEnabled(false);
        
        pBtns.add(btnTinh); pBtns.add(btnLuu);
        pnlAction.add(lblTongLuong); pnlAction.add(pBtns);
        
        pnlRight.add(pnlAction, BorderLayout.SOUTH);
        pnlCenter.add(pnlRight, BorderLayout.EAST);
        
        add(pnlCenter, BorderLayout.CENTER);

        // --- LOGIC & EVENTS ---
        loadTable();
        ActionListener timeChangeListener = e -> loadTable();
        cboThang.addActionListener(timeChangeListener);
        txtNam.addActionListener(timeChangeListener);

        tblNhanVien.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tblNhanVien.getSelectedRow();
                if(row != -1) { loadEmployeeData(row); btnLuu.setEnabled(false); }
            }
        });
        
        btnTinh.addActionListener(e -> { calculateSalary(); btnLuu.setEnabled(true); });
        
        btnLuu.addActionListener(e -> {
            String maNV = txtMa.getText();
            if(maNV.isEmpty()) return;
            try { 
                int tongTiet = Integer.parseInt(txtSoTiet.getText()); 
                // Sử dụng hàm parse tương ứng cho từng loại dữ liệu
                double valLuongCB = parseMoney(txtLuongCB);
                double valHeSo = parseCoefficient(txtHeSo);
                double valPhuCap = parseMoney(txtPhuCap);
                
                boolean result = LuongDAO.saveSingleSalary(maNV, (int)cboThang.getSelectedItem(), getNam(), valLuongCB, valHeSo, valPhuCap, curLuongCung, tongTiet, curThuLao, curThucLinh);
                if(result) {
                    JOptionPane.showMessageDialog(this, "Đã lưu thành công!");
                    loadTable(); selectRowByMaNV(maNV);
                }
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi nhập liệu: " + ex.getMessage()); }
        });

        btnReload.addActionListener(e -> loadTable());
        btnExcel.addActionListener(e -> exportExcel());
    }

    // --- HELPERS: XỬ LÝ SỐ LIỆU ---
    
    // 1. Hàm cho Hệ Số (Giữ lại dấu thập phân)
    // Ví dụ: "1.5" -> 1.5 | "1,5" -> 1.5
    private double parseCoefficient(JTextField t) { 
        String text = t.getText().trim();
        if(text.isEmpty()) return 0;
        try {
            return Double.parseDouble(text.replace(",", ".")); 
        } catch (NumberFormatException e) { return 0; }
    }

    // 2. Hàm cho Tiền Tệ (Xóa hết dấu phân cách)
    // Ví dụ: "10.000.000" -> 10000000 | "500,000" -> 500000
    private double parseMoney(JTextField t) { 
        String text = t.getText().trim();
        if(text.isEmpty()) return 0;
        try {
            return Double.parseDouble(text.replace(",", "").replace(".", "")); 
        } catch (NumberFormatException e) { return 0; }
    }

    private int getNam() { try { return Integer.parseInt(txtNam.getText()); } catch(Exception e) { return LocalDate.now().getYear(); } }

    private void loadTable() {
        modelNV = LuongDAO.getBangLuong((int)cboThang.getSelectedItem(), getNam());
        tblNhanVien.setModel(modelNV);
        if(tblNhanVien.getColumnCount()>0) {
            tblNhanVien.getColumnModel().getColumn(1).setPreferredWidth(150);
            tblNhanVien.getColumnModel().getColumn(6).setPreferredWidth(100);
        }
    }

    private void loadEmployeeData(int row) {
        try {
            String maNV = tblNhanVien.getValueAt(row, 0).toString();
            Object[] data = LuongDAO.getChiTietLuong(maNV, (int)cboThang.getSelectedItem(), getNam());
            if(data != null) {
                txtMa.setText(data[0].toString()); txtTen.setText(data[1].toString()); txtLoaiHinh.setText(data[2].toString());
                txtHeSo.setText(String.valueOf(data[3]));
                
                DecimalFormat df = new DecimalFormat("###");
                txtLuongCB.setText(df.format(data[4])); 
                txtPhuCap.setText(df.format(data[5])); 
                
                txtSoTiet.setText(String.valueOf(data[6]));
                double saved = (double) data[7];
                lblTongLuong.setText(saved > 0 ? "Đã lưu: " + new DecimalFormat("#,###").format(saved) + " VNĐ" : "0 VNĐ");
            }
            
            String lh = txtLoaiHinh.getText().toLowerCase();
            boolean isCoHuu = lh.contains("cơ hữu") || lh.contains("biên chế");
            txtSoTiet.setEnabled(!isCoHuu); txtDonGia.setEnabled(!isCoHuu);
            
            if(isCoHuu) { 
                txtSoTiet.setText("0"); txtSoTiet.setBackground(new Color(240,240,240)); 
            } else {
                txtSoTiet.setBackground(Color.WHITE);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    // --- [LOGIC TÍNH TOÁN CHÍNH] ---
    private void calculateSalary() {
        try {
            String maNV = txtMa.getText();
            if (maNV.isEmpty()) return;

            // 1. Lấy dữ liệu từ giao diện (Dùng đúng hàm parse cho từng loại)
            double heSo = parseCoefficient(txtHeSo);
            double luongCB = parseMoney(txtLuongCB);
            
            // 2. Lấy Phụ Cấp GỐC từ Database để reset (tránh cộng dồn)
            double phuCapGoc = getRawPhuCapFromDB(maNV); 
            
            // 3. Lấy điểm thưởng NCKH mới nhất
            int thang = (int) cboThang.getSelectedItem();
            int nam = getNam();
            double diemNCKH = NghienCuuDAO.getTongDiemThuong(maNV, thang, nam);
            double thuongNCKH = diemNCKH * 1000000; 
            
            // 4. Tính lại tổng phụ cấp & GHI ĐÈ vào ô nhập liệu
            double tongPhuCapMoi = phuCapGoc + thuongNCKH;
            txtPhuCap.setText(new DecimalFormat("###").format(tongPhuCapMoi));

            // 5. Tính lương cứng
            curLuongCung = (heSo * luongCB) + tongPhuCapMoi;

            // 6. Tính thù lao giảng dạy
            curThuLao = 0;
            if (txtSoTiet.isEnabled()) {
                double donGia = parseMoney(txtDonGia);
                int soTiet = txtSoTiet.getText().isEmpty() ? 0 : Integer.parseInt(txtSoTiet.getText());
                curThuLao = soTiet * donGia;
            }

            // 7. Tổng thực lĩnh
            curThucLinh = curLuongCung + curThuLao;
            
            lblTongLuong.setText(new DecimalFormat("#,###").format(curThucLinh) + " VNĐ");
            
            String msg = "Đã tính toán lại cho nhân viên " + txtTen.getText() + ":\n" +
                         "- Hệ số: " + heSo + "\n" +
                         "- Phụ cấp gốc: " + new DecimalFormat("#,###").format(phuCapGoc) + "\n" +
                         "- Thưởng NCKH (" + diemNCKH + " điểm): " + new DecimalFormat("#,###").format(thuongNCKH);
            JOptionPane.showMessageDialog(this, msg);
            
        } catch (Exception e) { 
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tính toán: " + e.getMessage()); 
        }
    }

    private double getRawPhuCapFromDB(String maNV) {
        double pc = 0;
        String sql = "SELECT PhuCap FROM NhanVien WHERE MaNV = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, maNV);
            ResultSet rs = p.executeQuery();
            if (rs.next()) pc = rs.getDouble("PhuCap");
        } catch (Exception e) { e.printStackTrace(); }
        return pc;
    }

    private void selectRowByMaNV(String maNV) {
        for(int i=0; i<tblNhanVien.getRowCount(); i++) if(tblNhanVien.getValueAt(i, 0).equals(maNV)) { tblNhanVien.setRowSelectionInterval(i, i); break; }
    }
    
    private void exportExcel() {
        if(tblNhanVien.getRowCount()==0) return;
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("Bang_Luong_Thang_" + cboThang.getSelectedItem() + ".xlsx"));
        if(fc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION) ExcelExporter.exportToExcel(tblNhanVien, fc.getSelectedFile(), "Luong");
    }

    // --- UI COMPONENTS ---
    private JPanel createCardPanel() {
        JPanel p = new JPanel(new BorderLayout()); p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1), new EmptyBorder(10, 10, 10, 10)));
        return p;
    }
    private JTable createModernTable() {
        JTable t = new JTable(); t.setFont(new Font("Segoe UI", Font.PLAIN, 14)); t.setRowHeight(35);
        t.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(COL_PRIMARY); lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); lbl.setHorizontalAlignment(JLabel.CENTER);
                return lbl;
            }
        });
        return t;
    }
    private JButton createBtn(String t, Color bg, String i) {
        JButton b = new JButton(t); b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setPreferredSize(new Dimension(110, 35));
        ImageIcon icon = loadResizedIcon(i, 18, 18); if(icon!=null) b.setIcon(icon);
        return b;
    }
    private void addHeader(JPanel c, String t, String i) {
        JPanel h = new JPanel(new FlowLayout(FlowLayout.LEFT)); h.setBackground(Color.WHITE);
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.BOLD, 16)); l.setForeground(new Color(50,50,50));
        ImageIcon icon = loadResizedIcon(i, 24, 24); if(icon!=null) l.setIcon(icon);
        h.add(l); c.add(h, BorderLayout.NORTH);
    }
    private JTextField createField() { JTextField t = new JTextField(); t.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(0,0,1,0, COL_PRIMARY), new EmptyBorder(5,5,5,5))); return t; }
    private JTextField createReadOnlyField() { JTextField t = createField(); t.setEditable(false); t.setBackground(new Color(250,250,250)); return t; }
    private void addSection(JPanel p, String t, int r, GridBagConstraints g) { g.gridx=0; g.gridy=r; g.gridwidth=2; JLabel l=new JLabel(t); l.setForeground(COL_PRIMARY); l.setFont(new Font("Segoe UI", Font.BOLD, 14)); l.setBorder(new EmptyBorder(10,0,5,0)); p.add(l,g); }
    private void addLabel(JPanel p, String t, int r, GridBagConstraints g) { g.gridx=0; g.gridy=r; g.gridwidth=1; g.weightx=0; p.add(new JLabel(t),g); }
    private void addComp(JPanel p, Component c, int r, GridBagConstraints g) { g.gridx=1; g.gridy=r; g.weightx=1; p.add(c,g); }
    private ImageIcon loadResizedIcon(String path, int w, int h) {
        URL url = getClass().getResource("/icons/" + path); if (url == null) return null;
        return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }
}