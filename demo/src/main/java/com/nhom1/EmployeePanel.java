package com.nhom1;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;

public class EmployeePanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private TableRowSorter<DefaultTableModel> sorter;

    // --- MÀU SẮC GIAO DIỆN (ĐỒNG BỘ) ---
    private final Color COL_PRIMARY = new Color(0, 150, 136);
    private final Color COL_HEADER_TEXT = Color.WHITE;
    private final Color COL_DANGER = new Color(231, 76, 60);
    private final Color COL_EDIT = new Color(52, 152, 219);
    private final Color COL_EXCEL = new Color(33, 115, 70);
    private final Color COL_BG = new Color(245, 247, 250);

    public EmployeePanel(JFrame parentFrame) {
        setLayout(new BorderLayout(20, 20));
        setBackground(COL_BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel mainCard = createCardPanel();
        add(mainCard, BorderLayout.CENTER);

        // --- 1. HEADER & SEARCH ---
        JPanel pnlTop = new JPanel();
        pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.Y_AXIS));
        pnlTop.setBackground(Color.WHITE);
        
        addHeader(pnlTop, "QUẢN LÝ NHÂN SỰ", "staff.png");
        
        txtSearch = createSearchField();
        pnlTop.add(createSearchPanel(txtSearch));
        mainCard.add(pnlTop, BorderLayout.NORTH);

        // --- 2. TABLE ---
        table = createModernTable();
        mainCard.add(new JScrollPane(table), BorderLayout.CENTER);

        // --- 3. BUTTONS ---
        JPanel pnlTools = new JPanel(new GridLayout(1, 4, 10, 0));
        pnlTools.setBackground(Color.WHITE);
        pnlTools.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnAdd = createBtn("Thêm NV", COL_PRIMARY, "add.png");
        JButton btnEdit = createBtn("Sửa NV", COL_EDIT, "edit.png");
        JButton btnDel = createBtn("Xóa NV", COL_DANGER, "delete.png");
        JButton btnExcel = createBtn("Excel", COL_EXCEL, "excel.png");

        pnlTools.add(btnAdd); pnlTools.add(btnEdit); pnlTools.add(btnDel); pnlTools.add(btnExcel);
        
        JPanel pnlBot = new JPanel(new BorderLayout());
        pnlBot.setBackground(Color.WHITE);
        pnlBot.add(pnlTools, BorderLayout.CENTER);
        mainCard.add(pnlBot, BorderLayout.SOUTH);

        // --- SETUP SEARCH ---
        setupSearchListener(txtSearch);

        // --- EVENTS ---
        btnAdd.addActionListener(e -> {
            EmployeeDialog d = new EmployeeDialog(parentFrame, null);
            d.setVisible(true);
            if(d.isSaved()) loadData();
        });

        btnEdit.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để sửa!");
                return;
            }
            int modelRow = table.convertRowIndexToModel(r);
            int cols = model.getColumnCount();
            String[] data = new String[cols];
            for(int i=0; i<cols; i++) {
                Object val = model.getValueAt(modelRow, i);
                data[i] = val == null ? "" : val.toString();
            }
            
            EmployeeDialog d = new EmployeeDialog(parentFrame, data);
            d.setVisible(true);
            if(d.isSaved()) loadData();
        });

        btnDel.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r != -1 && JOptionPane.showConfirmDialog(this, "Xóa nhân viên này?") == JOptionPane.YES_OPTION) {
                int modelRow = table.convertRowIndexToModel(r);
                if(NhanSuDAO.deleteNhanVien(model.getValueAt(modelRow, 0).toString())) loadData();
            } else if (r == -1) {
                JOptionPane.showMessageDialog(this, "Chọn nhân viên cần xóa!");
            }
        });

        btnExcel.addActionListener(e -> exportFile(table, "DanhSachNhanVien"));

        loadData();
    }

    private void loadData() {
        model = NhanSuDAO.getNhanVienModel();
        table.setModel(model);
        
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        String text = txtSearch.getText().trim();
        if(!text.isEmpty()) sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));

        // Ẩn cột hình ảnh (cột cuối)
        if (table.getColumnCount() > 0) {
            int lastCol = table.getColumnCount() - 1;
            table.getColumnModel().getColumn(lastCol).setMinWidth(0);
            table.getColumnModel().getColumn(lastCol).setMaxWidth(0);
        }
    }
    
    // --- UI HELPER METHODS (GIỐNG FACULTY PANEL) ---
    private void setupSearchListener(JTextField txt) {
        txt.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { apply(); }
            public void removeUpdate(DocumentEvent e) { apply(); }
            public void changedUpdate(DocumentEvent e) { apply(); }
            private void apply() {
                String text = txt.getText().trim();
                if (sorter != null) sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + text));
            }
        });
    }

    private ImageIcon loadResizedIcon(String path, int w, int h) {
        URL url = getClass().getResource("/icons/" + path);
        if (url == null) return null;
        ImageIcon original = new ImageIcon(url);
        Image scaled = original.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private JPanel createCardPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        return p;
    }

    private void addHeader(JPanel container, String title, String iconName) {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(0, 5, 5, 0));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(new Color(50, 50, 50));
        ImageIcon icon = loadResizedIcon(iconName, 30, 30);
        if (icon != null) lbl.setIcon(icon);
        header.add(lbl);
        container.add(header);
    }

    private JTextField createSearchField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 2, 0, COL_PRIMARY),
            new EmptyBorder(5, 5, 5, 5)
        ));
        return txt;
    }

    private JPanel createSearchPanel(JTextField txt) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(0, 10, 10, 10));
        JLabel icon = new JLabel();
        ImageIcon img = loadResizedIcon("search.png", 20, 20);
        if (img != null) icon.setIcon(img); else icon.setText("🔍");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        p.add(icon, BorderLayout.WEST);
        p.add(txt, BorderLayout.CENTER);
        return p;
    }

    private JTable createModernTable() {
        JTable tbl = new JTable();
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tbl.setRowHeight(35);
        tbl.setGridColor(new Color(230, 230, 230));
        tbl.setShowVerticalLines(false);
        tbl.setSelectionBackground(new Color(220, 240, 255));
        tbl.setSelectionForeground(Color.BLACK);
        
        tbl.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(COL_PRIMARY);
                lbl.setForeground(COL_HEADER_TEXT);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                lbl.setHorizontalAlignment(JLabel.CENTER);
                lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.WHITE));
                return lbl;
            }
        });
        tbl.getTableHeader().setPreferredSize(new Dimension(0, 40));
        return tbl;
    }

    private JButton createBtn(String text, Color bg, String iconName) {
        JButton btn = new JButton(text);
        ImageIcon icon = loadResizedIcon(iconName, 20, 20);
        if (icon != null) btn.setIcon(icon);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 40));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    private void exportFile(JTable table, String defaultName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
        fileChooser.setSelectedFile(new File(defaultName + ".xlsx"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            ExcelExporter.exportToExcel(table, fileChooser.getSelectedFile(), "Sheet1");
        }
    }
}