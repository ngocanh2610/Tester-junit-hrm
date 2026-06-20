package com.nhom1;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class AccountPanel extends JPanel {

    private JTable tblAcc;
    private JTextField txtUser, txtPass;
    private JComboBox<String> cboRole, cboNhanVien;

    // Màu sắc
    private final Color COL_PRIMARY = new Color(0, 150, 136);
    private final Color COL_HEADER_TEXT = Color.WHITE;
    private final Color COL_BG = new Color(245, 247, 250);
    private final Color COL_ADD = new Color(46, 204, 113);
    private final Color COL_DEL = new Color(231, 76, 60);
    private final Color COL_RESET = new Color(52, 152, 219);

    public AccountPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(COL_BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- 1. TOP CARD (FORM) ---
        JPanel pnlTopCard = createCardPanel();
        pnlTopCard.setLayout(new BorderLayout());
        
        addHeader(pnlTopCard, "QUẢN LÝ TÀI KHOẢN", "key.png"); 

        JPanel pnlForm = new JPanel(new GridLayout(2, 4, 15, 15));
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(new EmptyBorder(10, 10, 10, 10));

        txtUser = createTextField();
        txtPass = createTextField(); 
        cboRole = new JComboBox<>(new String[]{"GiangVien", "Admin"});
        cboNhanVien = new JComboBox<>();
        
        // Load lần đầu
        loadCboNhanVien();

        pnlForm.add(createInputGroup("Tên Đăng Nhập:", txtUser));
        pnlForm.add(createInputGroup("Mật Khẩu:", txtPass));
        pnlForm.add(createInputGroup("Phân Quyền:", cboRole));
        pnlForm.add(createInputGroup("Gán Nhân Viên:", cboNhanVien));

        pnlTopCard.add(pnlForm, BorderLayout.CENTER);
        
        // Buttons
        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBtn.setBackground(Color.WHITE);
        JButton btnAdd = createBtn("Thêm TK", COL_ADD, "add.png");
        JButton btnDel = createBtn("Xóa TK", COL_DEL, "delete.png");
        JButton btnReset = createBtn("Reset Pass (123)", COL_RESET, "refresh.png");
        
        pnlBtn.add(btnAdd); pnlBtn.add(btnDel); pnlBtn.add(btnReset);
        pnlTopCard.add(pnlBtn, BorderLayout.SOUTH);

        add(pnlTopCard, BorderLayout.NORTH);

        // --- 2. CENTER CARD (TABLE) ---
        JPanel pnlTableCard = createCardPanel();
        tblAcc = createModernTable();
        pnlTableCard.add(new JScrollPane(tblAcc), BorderLayout.CENTER);
        add(pnlTableCard, BorderLayout.CENTER);

        refreshData();

        // EVENTS
        tblAcc.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = tblAcc.getSelectedRow();
                if(r != -1) {
                    txtUser.setText(tblAcc.getValueAt(r, 0).toString());
                    txtPass.setText(tblAcc.getValueAt(r, 1).toString());
                    cboRole.setSelectedItem(tblAcc.getValueAt(r, 2).toString());
                    String maNV = tblAcc.getValueAt(r, 3) != null ? tblAcc.getValueAt(r, 3).toString() : "";
                    setSelectedNhanVien(maNV);
                }
            }
        });

        btnAdd.addActionListener(e -> {
            String u = txtUser.getText(); String p = txtPass.getText();
            String r = cboRole.getSelectedItem().toString();
            String nvString = cboNhanVien.getSelectedItem() != null ? cboNhanVien.getSelectedItem().toString() : "";
            
            if(nvString.isEmpty()) { JOptionPane.showMessageDialog(this, "Chưa có nhân viên nào để gán!"); return; }
            String maNV = nvString.split(" - ")[0];
            
            if(u.isEmpty() || p.isEmpty()) { JOptionPane.showMessageDialog(this, "Nhập đủ User/Pass!"); return; }
            if(TaiKhoanDAO.addTaiKhoan(u, p, r, maNV)) {
                JOptionPane.showMessageDialog(this, "Thêm thành công!"); refreshData();
            } else JOptionPane.showMessageDialog(this, "Lỗi! Tên đăng nhập đã tồn tại.");
        });

        btnDel.addActionListener(e -> {
            String u = txtUser.getText();
            if(!u.isEmpty() && JOptionPane.showConfirmDialog(this, "Xóa tài khoản " + u + "?", "Confirm", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                if(TaiKhoanDAO.deleteTaiKhoan(u)) refreshData();
            }
        });
        
        btnReset.addActionListener(e -> {
             String u = txtUser.getText();
             if(!u.isEmpty() && TaiKhoanDAO.updatePassword(u, "123")) {
                 JOptionPane.showMessageDialog(this, "Đã reset mật khẩu về 123"); refreshData();
             }
        });
    }

    // --- SỬA Ở ĐÂY: Thêm loadCboNhanVien() vào refreshData() ---
    public void refreshData() { 
        tblAcc.setModel(TaiKhoanDAO.getDSTaiKhoan()); 
        loadCboNhanVien(); // <--- Cập nhật danh sách nhân viên mới nhất vào ComboBox
    }

    private void loadCboNhanVien() {
        cboNhanVien.removeAllItems();
        DefaultTableModel model = NhanSuDAO.getNhanVienModel();
        for(int i=0; i<model.getRowCount(); i++) {
            cboNhanVien.addItem(model.getValueAt(i, 0) + " - " + model.getValueAt(i, 1));
        }
    }

    private void setSelectedNhanVien(String maNV) {
        for(int i=0; i<cboNhanVien.getItemCount(); i++) {
            if(cboNhanVien.getItemAt(i).startsWith(maNV + " - ")) { cboNhanVien.setSelectedIndex(i); break; }
        }
    }

    // --- UI HELPERS ---
    private JPanel createInputGroup(String title, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(Color.WHITE);
        p.add(new JLabel(title), BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }
    
    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 2, 0, COL_PRIMARY), new EmptyBorder(5, 5, 5, 5)));
        return txt;
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
        btn.setFocusPainted(false); btn.setPreferredSize(new Dimension(140, 40));
        return btn;
    }
}