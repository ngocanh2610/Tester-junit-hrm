package com.nhom1;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
//import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
//import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.time.LocalDate;

public class ResearchPanel extends JPanel {

    private JTable tblStaff;
    private JTable tblResearch;
    private String selectedMaNV = null;
    private JLabel lblCurrentStaff;

    // --- MÀU SẮC ---
    private final Color COL_PRIMARY = new Color(0, 150, 136);
    private final Color COL_HEADER_TEXT = Color.WHITE;
    private final Color COL_BG = new Color(245, 247, 250);
    private final Color COL_ADD = new Color(46, 204, 113);
    private final Color COL_DEL = new Color(231, 76, 60);

    public ResearchPanel() {
        setLayout(new GridLayout(1, 2, 20, 0)); // Chia đôi màn hình
        setBackground(COL_BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- LEFT CARD: DANH SÁCH GIẢNG VIÊN ---
        JPanel pnlLeftCard = createCardPanel();
        addHeader(pnlLeftCard, "1. CHỌN GIẢNG VIÊN", "staff.png");
        
        tblStaff = createModernTable();
        pnlLeftCard.add(new JScrollPane(tblStaff), BorderLayout.CENTER);
        
        // --- RIGHT CARD: QUẢN LÝ NCKH ---
        JPanel pnlRightCard = createCardPanel();
        
        // Header bên phải chứa cả nút bấm
        JPanel pnlRightHeader = new JPanel(new BorderLayout());
        pnlRightHeader.setBackground(Color.WHITE);
        pnlRightHeader.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Title & Info
        JPanel pnlTitleInfo = new JPanel(new GridLayout(2, 1));
        pnlTitleInfo.setBackground(Color.WHITE);
        
        JLabel lblTitleRight = new JLabel("2. CÔNG TRÌNH / BÀI BÁO");
        lblTitleRight.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitleRight.setForeground(new Color(50, 50, 50));
        
        lblCurrentStaff = new JLabel("--- Chọn GV để xem ---");
        lblCurrentStaff.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblCurrentStaff.setForeground(COL_PRIMARY);
        
        pnlTitleInfo.add(lblTitleRight);
        pnlTitleInfo.add(lblCurrentStaff);
        
        // Buttons
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBtns.setBackground(Color.WHITE);
        JButton btnAdd = createBtn("Thêm", COL_ADD, "add.png");
        JButton btnDel = createBtn("Xóa", COL_DEL, "delete.png");
        pnlBtns.add(btnAdd); pnlBtns.add(btnDel);

        pnlRightHeader.add(pnlTitleInfo, BorderLayout.WEST);
        pnlRightHeader.add(pnlBtns, BorderLayout.EAST);
        
        pnlRightCard.add(pnlRightHeader, BorderLayout.NORTH);
        
        tblResearch = createModernTable();
        pnlRightCard.add(new JScrollPane(tblResearch), BorderLayout.CENTER);

        add(pnlLeftCard);
        add(pnlRightCard);

        // --- EVENTS ---
        tblStaff.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = tblStaff.getSelectedRow();
                if (r != -1) {
                    selectedMaNV = tblStaff.getValueAt(r, 0).toString();
                    String ten = tblStaff.getValueAt(r, 1).toString();
                    lblCurrentStaff.setText("Đang xem: " + ten.toUpperCase());
                    loadResearchData();
                }
            }
        });

        btnAdd.addActionListener(e -> showAddDialog());
        btnDel.addActionListener(e -> deleteResearch());

        refreshData(); 
    }

    public void refreshData() {
        if (Auth.isGiangVien() && Auth.maNV != null) {
            tblStaff.setModel(NhanSuDAO.getNhanVienByMa(Auth.maNV));
        } else {
            tblStaff.setModel(NhanSuDAO.getNhanVienModel());
        }
        hideUselessColumns(tblStaff);
        
        // Auto select dòng đầu nếu là GV
        if (Auth.isGiangVien() && tblStaff.getRowCount() > 0) {
            tblStaff.setRowSelectionInterval(0, 0);
            selectedMaNV = tblStaff.getValueAt(0, 0).toString();
            lblCurrentStaff.setText("Đang xem: " + tblStaff.getValueAt(0, 1).toString().toUpperCase());
            loadResearchData();
        }
    }
    
    private void hideUselessColumns(JTable t) {
        if (t.getColumnCount() > 2) {
            for(int i=2; i<t.getColumnCount(); i++) {
                t.getColumnModel().getColumn(i).setMinWidth(0);
                t.getColumnModel().getColumn(i).setMaxWidth(0);
            }
        }
    }

    private void loadResearchData() {
        if (selectedMaNV != null) {
            tblResearch.setModel(NghienCuuDAO.getListNCKH(selectedMaNV));
            if(tblResearch.getColumnCount() > 0) {
                tblResearch.getColumnModel().getColumn(0).setMinWidth(0);
                tblResearch.getColumnModel().getColumn(0).setMaxWidth(0);
            }
        }
    }

    private void deleteResearch() {
        int r = tblResearch.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Chọn đề tài cần xóa!"); return; }
        int id = Integer.parseInt(tblResearch.getValueAt(r, 0).toString());
        if (JOptionPane.showConfirmDialog(this, "Xóa đề tài này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (NghienCuuDAO.deleteNCKH(id)) {
                loadResearchData();
                JOptionPane.showMessageDialog(this, "Đã xóa!");
            }
        }
    }

    private void showAddDialog() {
        if (selectedMaNV == null) { JOptionPane.showMessageDialog(this, "Chưa chọn giảng viên!"); return; }
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm NCKH", true);
        d.setSize(450, 400);
        d.setLocationRelativeTo(this);
        
        JPanel pForm = new JPanel(new GridLayout(6, 1, 10, 10));
        pForm.setBorder(new EmptyBorder(20,20,20,20));
        
        JTextField txtTen = new JTextField();
        String[] loais = {"Bài báo Quốc tế (ISI/Scopus)", "Bài báo Trong nước", "Sách chuyên khảo", "Đề tài cấp Trường", "Hướng dẫn NCS"};
        JComboBox<String> cboLoai = new JComboBox<>(loais);
        JTextField txtNgay = new JTextField(LocalDate.now().toString());
        JTextField txtDiem = new JTextField("1.0");

        pForm.add(createInputGroup("Tên Đề tài/Bài báo:", txtTen));
        pForm.add(createInputGroup("Loại hình:", cboLoai));
        pForm.add(createInputGroup("Ngày công bố (yyyy-mm-dd):", txtNgay));
        pForm.add(createInputGroup("Điểm thưởng / Quy đổi:", txtDiem));
        
        JButton btnSave = new JButton("Lưu Dữ Liệu");
        btnSave.setBackground(COL_ADD); btnSave.setForeground(Color.WHITE);
        btnSave.setPreferredSize(new Dimension(0, 40));
        
        btnSave.addActionListener(e -> {
            try {
                double diem = Double.parseDouble(txtDiem.getText());
                if (NghienCuuDAO.addNCKH(selectedMaNV, txtTen.getText(), cboLoai.getSelectedItem().toString(), txtNgay.getText(), diem)) {
                    JOptionPane.showMessageDialog(d, "Thêm thành công!");
                    loadResearchData(); d.dispose();
                } else JOptionPane.showMessageDialog(d, "Lỗi lưu dữ liệu!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(d, "Lỗi nhập liệu: " + ex.getMessage()); }
        });
        
        d.add(pForm, BorderLayout.CENTER);
        d.add(btnSave, BorderLayout.SOUTH);
        d.setVisible(true);
    }
    
    // --- UI HELPERS ---
    private JPanel createInputGroup(String title, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.add(new JLabel(title), BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JPanel createCardPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1), new EmptyBorder(10, 10, 10, 10)));
        return p;
    }
    
    private void addHeader(JPanel container, String title, String iconName) {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(new Color(50, 50, 50));
        ImageIcon icon = loadResizedIcon(iconName, 30, 30);
        if (icon != null) lbl.setIcon(icon);
        header.add(lbl); container.add(header, BorderLayout.NORTH);
    }

    private ImageIcon loadResizedIcon(String path, int w, int h) {
        URL url = getClass().getResource("/icons/" + path);
        if (url == null) return null;
        Image scaled = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private JTable createModernTable() {
        JTable t = new JTable(); t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setRowHeight(35); t.setShowVerticalLines(false);
        t.setSelectionBackground(new Color(220, 240, 255)); t.setSelectionForeground(Color.BLACK);
        t.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(COL_PRIMARY); lbl.setForeground(COL_HEADER_TEXT);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 14)); lbl.setHorizontalAlignment(JLabel.CENTER);
                return lbl;
            }
        });
        t.getTableHeader().setPreferredSize(new Dimension(0, 40));
        return t;
    }
    
    private JButton createBtn(String text, Color bg, String iconName) {
        JButton btn = new JButton(text);
        ImageIcon icon = loadResizedIcon(iconName, 20, 20);
        if (icon != null) btn.setIcon(icon);
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }
}