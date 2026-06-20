package com.nhom1;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.time.LocalDate;

public class CVPanel extends JPanel {

    private JTable tblStaff;
    private JTable tblHistory;
    private String selectedMaNV = null;
    private String selectedTenNV = "";
    private String currentTrinhDo = "";
    private String currentChucVu = "";

    // Màu sắc giao diện
    private final Color COL_PRIMARY = new Color(0, 150, 136);
    private final Color COL_BG = new Color(245, 247, 250);
    private final Color COL_ADD = new Color(46, 204, 113);
    private final Color COL_DEL = new Color(231, 76, 60);

    public CVPanel() {
        setLayout(new GridLayout(1, 3, 15, 0)); // 3 cột
        setBackground(COL_BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- CARD 1: CHỌN GIẢNG VIÊN ---
        JPanel pnlCol1 = createCardPanel();
        addHeader(pnlCol1, "1. CHỌN GIẢNG VIÊN", "staff.png");
        tblStaff = createModernTable();
        pnlCol1.add(new JScrollPane(tblStaff), BorderLayout.CENTER);

        // --- CARD 2: QUÁ TRÌNH CÔNG TÁC ---
        JPanel pnlCol2 = createCardPanel();
        addHeader(pnlCol2, "2. QUÁ TRÌNH CÔNG TÁC", "history.png");
        
        tblHistory = createModernTable();
        pnlCol2.add(new JScrollPane(tblHistory), BorderLayout.CENTER);
        
        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlAction.setBackground(Color.WHITE);
        JButton btnAdd = createBtn("Thêm", COL_ADD, "add.png");
        JButton btnDel = createBtn("Xóa", COL_DEL, "delete.png");
        pnlAction.add(btnAdd); pnlAction.add(btnDel);
        pnlCol2.add(pnlAction, BorderLayout.SOUTH);

        // --- CARD 3: XUẤT FILE ---
        JPanel pnlCol3 = createCardPanel();
        addHeader(pnlCol3, "3. XUẤT LÝ LỊCH", "export.png");
        
        JPanel pnlExportInfo = new JPanel(new GridLayout(0, 1, 10, 10));
        pnlExportInfo.setBackground(Color.WHITE);
        pnlExportInfo.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel lblIcon = new JLabel("📄");
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 80));
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel lblDesc = new JLabel("<html><center style='color:#7f8c8d'>Hệ thống sẽ tự động ghép nối:<br>" +
                "<b>I. Thông tin cá nhân</b><br>" +
                "<b>II. Quá trình công tác</b><br>" +
                "<b>III. Công trình NCKH</b></center></html>");
        lblDesc.setHorizontalAlignment(SwingConstants.CENTER);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JButton btnPreview = new JButton("XEM & XUẤT FILE");
        btnPreview.setBackground(new Color(52, 152, 219)); btnPreview.setForeground(Color.WHITE);
        btnPreview.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPreview.setPreferredSize(new Dimension(0, 50));
        btnPreview.setFocusPainted(false);
        
        pnlExportInfo.add(lblIcon); pnlExportInfo.add(lblDesc); pnlExportInfo.add(btnPreview);
        pnlCol3.add(pnlExportInfo, BorderLayout.CENTER);

        add(pnlCol1); add(pnlCol2); add(pnlCol3);
        
        // --- EVENTS ---
        tblStaff.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = tblStaff.getSelectedRow();
                if(r != -1) {
                    selectedMaNV = tblStaff.getValueAt(r, 0).toString();
                    selectedTenNV = tblStaff.getValueAt(r, 1).toString();
                    try {
                        if (tblStaff.getColumnCount() > 5) {
                            currentChucVu = tblStaff.getModel().getValueAt(r, 4).toString(); 
                            currentTrinhDo = tblStaff.getModel().getValueAt(r, 5).toString();
                        }
                    } catch (Exception ex) { }
                    loadHistory();
                }
            }
        });
        
        btnAdd.addActionListener(e -> showAddDialog());
        btnDel.addActionListener(e -> deleteHistory());
        btnPreview.addActionListener(e -> generateCV());
        
        refreshData(); 
    }

    public void refreshData() {
        if (Auth.isGiangVien() && Auth.maNV != null) {
            tblStaff.setModel(NhanSuDAO.getNhanVienByMa(Auth.maNV));
        } else {
            tblStaff.setModel(NhanSuDAO.getNhanVienModel());
        }
        
        if (tblStaff.getColumnCount() > 2) {
            for(int i=2; i<tblStaff.getColumnCount(); i++) {
                tblStaff.getColumnModel().getColumn(i).setMinWidth(0);
                tblStaff.getColumnModel().getColumn(i).setMaxWidth(0);
            }
        }
        
        if (Auth.isGiangVien() && tblStaff.getRowCount() > 0) {
            tblStaff.setRowSelectionInterval(0, 0);
            selectedMaNV = tblStaff.getValueAt(0, 0).toString();
            selectedTenNV = tblStaff.getValueAt(0, 1).toString();
            try {
                if (tblStaff.getColumnCount() > 5) {
                    currentChucVu = tblStaff.getModel().getValueAt(0, 4).toString(); 
                    currentTrinhDo = tblStaff.getModel().getValueAt(0, 5).toString();
                }
            } catch (Exception ex) { }
            loadHistory();
        }
    }

    private void loadHistory() {
        if(selectedMaNV != null) {
            tblHistory.setModel(QuaTrinhDAO.getQuaTrinh(selectedMaNV));
            if(tblHistory.getColumnCount() > 0) tblHistory.getColumnModel().getColumn(0).setMaxWidth(0);
        }
    }

    private void deleteHistory() {
        int r = tblHistory.getSelectedRow();
        if (r == -1) return;
        int id = Integer.parseInt(tblHistory.getValueAt(r, 0).toString());
        if(QuaTrinhDAO.deleteQuaTrinh(id)) loadHistory();
    }

    private void showAddDialog() {
        if(selectedMaNV == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên trước!"); return; }
        
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm Quá Trình", true);
        d.setSize(400, 300); d.setLocationRelativeTo(this);
        
        JPanel pContent = new JPanel(new GridLayout(0, 1, 10, 10));
        pContent.setBorder(new EmptyBorder(15, 15, 15, 15));

        JTextField txtTime = new JTextField();
        JTextField txtPlace = new JTextField();
        JTextField txtRole = new JTextField();
        
        pContent.add(new JLabel("Thời gian (VD: 2010-2014):")); pContent.add(txtTime);
        pContent.add(new JLabel("Đơn vị / Trường học:")); pContent.add(txtPlace);
        pContent.add(new JLabel("Chức vụ / Văn bằng:")); pContent.add(txtRole);

        JButton btnSave = new JButton("LƯU DỮ LIỆU");
        btnSave.setBackground(COL_ADD); btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> {
            if(QuaTrinhDAO.addQuaTrinh(selectedMaNV, txtTime.getText(), txtPlace.getText(), txtRole.getText(), "")) {
                loadHistory(); d.dispose();
            }
        });
        
        d.add(pContent, BorderLayout.CENTER); d.add(btnSave, BorderLayout.SOUTH);
        d.setVisible(true);
    }
    
    // --- HÀM TẠO HTML ---
    private void generateCV() {
        if(selectedMaNV == null) return;
        DefaultTableModel modelQT = QuaTrinhDAO.getQuaTrinh(selectedMaNV);
        DefaultTableModel modelNCKH = NghienCuuDAO.getListNCKH(selectedMaNV);

        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial; padding: 40px;'>");
        html.append("<h1 style='color: #2c3e50; text-align: center; border-bottom: 2px solid #3498db; padding-bottom: 10px;'>LÝ LỊCH KHOA HỌC</h1>");
        
        html.append("<h3 style='color: #2980b9'>I. THÔNG TIN CHUNG</h3>");
        html.append("<p><b>Họ và tên:</b> ").append(selectedTenNV.toUpperCase()).append("</p>");
        html.append("<p><b>Mã cán bộ:</b> ").append(selectedMaNV).append("</p>");
        html.append("<p><b>Học vị:</b> ").append(currentTrinhDo).append(" &nbsp;&nbsp;|&nbsp;&nbsp; <b>Chức vụ:</b> ").append(currentChucVu).append("</p>");

        html.append("<h3 style='color: #2980b9'>II. QUÁ TRÌNH CÔNG TÁC</h3>");
        if(modelQT.getRowCount() > 0) {
            html.append("<table border='1' cellspacing='0' cellpadding='8' width='100%' style='border-collapse: collapse; border: 1px solid #bdc3c7;'>");
            html.append("<tr style='background-color: #ecf0f1;'><th>Thời gian</th><th>Đơn vị / Trường học</th><th>Chức vụ / Văn bằng</th></tr>");
            for(int i=0; i<modelQT.getRowCount(); i++) {
                html.append("<tr><td>").append(modelQT.getValueAt(i, 1)).append("</td><td>").append(modelQT.getValueAt(i, 2)).append("</td><td>").append(modelQT.getValueAt(i, 3)).append("</td></tr>");
            }
            html.append("</table>");
        } else {
            html.append("<p><i>(Chưa cập nhật)</i></p>");
        }

        html.append("<h3 style='color: #2980b9'>III. CÔNG TRÌNH KHOA HỌC</h3>");
        if(modelNCKH.getRowCount() > 0) {
            html.append("<ul>");
            for(int i=0; i<modelNCKH.getRowCount(); i++) {
                html.append("<li style='margin-bottom: 5px;'>").append("<b>[").append(modelNCKH.getValueAt(i, 3)).append("]</b> ").append(modelNCKH.getValueAt(i, 1)).append("</li>");
            }
            html.append("</ul>");
        } else {
            html.append("<p><i>(Chưa có công trình)</i></p>");
        }
        
        // [ĐÃ KHÔI PHỤC PHẦN FOOTER NGÀY THÁNG & CHỮ KÝ]
        LocalDate now = LocalDate.now();
        html.append("<br><br><div style='text-align: right; margin-right: 50px;'>");
        html.append("<i>Hà Nội, ngày ").append(now.getDayOfMonth()).append(" tháng ").append(now.getMonthValue()).append(" năm ").append(now.getYear()).append("</i><br><br><br><b>Người khai</b><br>").append(selectedTenNV);
        html.append("</div>");
        
        html.append("</body></html>");

        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Xem trước CV", true);
        d.setSize(800, 900); d.setLocationRelativeTo(this);
        JEditorPane ed = new JEditorPane("text/html", html.toString());
        ed.setEditable(false);
        d.add(new JScrollPane(ed));
        
        JButton btnExport = new JButton("LƯU FILE HTML");
        btnExport.setBackground(new Color(52, 152, 219)); btnExport.setForeground(Color.WHITE);
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnExport.setPreferredSize(new Dimension(0, 50));
        
        btnExport.addActionListener(ev -> {
            try {
                JFileChooser fc = new JFileChooser();
                fc.setSelectedFile(new File("CV_" + selectedMaNV + ".html"));
                if(fc.showSaveDialog(d) == JFileChooser.APPROVE_OPTION) {
                    FileWriter fw = new FileWriter(fc.getSelectedFile());
                    fw.write(html.toString()); fw.close();
                    JOptionPane.showMessageDialog(d, "Lưu thành công!");
                }
            } catch(Exception ex) {}
        });
        d.add(btnExport, BorderLayout.SOUTH);
        d.setVisible(true);
    }

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
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 14)); lbl.setHorizontalAlignment(JLabel.CENTER);
                return lbl;
            }
        });
        return t;
    }
    private JButton createBtn(String t, Color bg, String i) {
        JButton b = new JButton(t); b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setPreferredSize(new Dimension(100, 35));
        return b;
    }
    private void addHeader(JPanel c, String t, String i) {
        JPanel h = new JPanel(new FlowLayout(FlowLayout.LEFT)); h.setBackground(Color.WHITE);
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.BOLD, 16)); l.setForeground(new Color(50,50,50));
        ImageIcon icon = loadResizedIcon(i, 30, 30);
        if (icon != null) l.setIcon(icon);
        h.add(l); c.add(h, BorderLayout.NORTH);
    }
    private ImageIcon loadResizedIcon(String path, int w, int h) {
        URL url = getClass().getResource("/icons/" + path);
        if (url == null) return null;
        Image scaled = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}