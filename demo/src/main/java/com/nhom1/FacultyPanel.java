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

public class FacultyPanel extends JPanel {

    private JTable tblKhoa, tblMonHoc;
    private JTextField txtSearchKhoa, txtSearchMon;
    private DefaultTableModel modelKhoa, modelMonHoc;
    private String selectedMaKhoa = null;
    private JLabel lblMonHocTitle;
    
    // Biến toàn cục để quản lý Sorter (quan trọng để tránh lỗi)
    private TableRowSorter<DefaultTableModel> sorterKhoa;
    private TableRowSorter<DefaultTableModel> sorterMon;

    // --- MÀU SẮC GIAO DIỆN ---
    private final Color COL_PRIMARY = new Color(0, 150, 136);
    private final Color COL_HEADER_TEXT = Color.WHITE;
    private final Color COL_DANGER = new Color(231, 76, 60);
    private final Color COL_EDIT = new Color(52, 152, 219);
    private final Color COL_EXCEL = new Color(33, 115, 70);
    private final Color COL_BG = new Color(245, 247, 250);

    public FacultyPanel() {
        setLayout(new GridLayout(1, 2, 20, 0));
        setBackground(COL_BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel cardKhoa = createCardPanel();
        JPanel cardMon = createCardPanel();

        // 1. CARD KHOA
        JPanel pnlTopKhoa = new JPanel();
        pnlTopKhoa.setLayout(new BoxLayout(pnlTopKhoa, BoxLayout.Y_AXIS));
        pnlTopKhoa.setBackground(Color.WHITE);
        addHeader(pnlTopKhoa, "DANH SÁCH KHOA", "folder.png");
        txtSearchKhoa = createSearchField();
        pnlTopKhoa.add(createSearchPanel(txtSearchKhoa));
        cardKhoa.add(pnlTopKhoa, BorderLayout.NORTH);

        tblKhoa = createModernTable();
        cardKhoa.add(new JScrollPane(tblKhoa), BorderLayout.CENTER);

        JPanel pnlKhoaTools = new JPanel(new GridLayout(1, 4, 10, 0));
        pnlKhoaTools.setBackground(Color.WHITE);
        pnlKhoaTools.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnAddK = createBtn("Thêm", COL_PRIMARY, "add.png");
        JButton btnEditK = createBtn("Sửa", COL_EDIT, "edit.png");
        JButton btnDelK = createBtn("Xóa", COL_DANGER, "delete.png");
        JButton btnExcelK = createBtn("Excel", COL_EXCEL, "excel.png");

        pnlKhoaTools.add(btnAddK); pnlKhoaTools.add(btnEditK); pnlKhoaTools.add(btnDelK); pnlKhoaTools.add(btnExcelK);
        JPanel pnlSouthKhoa = new JPanel(new BorderLayout());
        pnlSouthKhoa.setBackground(Color.WHITE);
        pnlSouthKhoa.add(pnlKhoaTools, BorderLayout.CENTER);
        cardKhoa.add(pnlSouthKhoa, BorderLayout.SOUTH);

        // 2. CARD MÔN HỌC
        JPanel pnlTopMon = new JPanel();
        pnlTopMon.setLayout(new BoxLayout(pnlTopMon, BoxLayout.Y_AXIS));
        pnlTopMon.setBackground(Color.WHITE);
        lblMonHocTitle = addHeader(pnlTopMon, "DANH SÁCH MÔN HỌC", "book.png");
        txtSearchMon = createSearchField();
        pnlTopMon.add(createSearchPanel(txtSearchMon));
        cardMon.add(pnlTopMon, BorderLayout.NORTH);

        tblMonHoc = createModernTable();
        cardMon.add(new JScrollPane(tblMonHoc), BorderLayout.CENTER);

        JPanel pnlMonTools = new JPanel(new GridLayout(1, 4, 10, 0));
        pnlMonTools.setBackground(Color.WHITE);
        pnlMonTools.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnAddM = createBtn("Thêm", COL_PRIMARY, "add.png");
        JButton btnEditM = createBtn("Sửa", COL_EDIT, "edit.png");
        JButton btnDelM = createBtn("Xóa", COL_DANGER, "delete.png");
        JButton btnExcelM = createBtn("Excel", COL_EXCEL, "excel.png");

        pnlMonTools.add(btnAddM); pnlMonTools.add(btnEditM); pnlMonTools.add(btnDelM); pnlMonTools.add(btnExcelM);
        JPanel pnlSouthMon = new JPanel(new BorderLayout());
        pnlSouthMon.setBackground(Color.WHITE);
        pnlSouthMon.add(pnlMonTools, BorderLayout.CENTER);
        cardMon.add(pnlSouthMon, BorderLayout.SOUTH);

        add(cardKhoa);
        add(cardMon);

        // --- SETUP LISTENERS TÌM KIẾM MỘT LẦN DUY NHẤT ---
        setupSearchListener(txtSearchKhoa, true);
        setupSearchListener(txtSearchMon, false);

        // Load dữ liệu
        loadKhoa();

        // Sự kiện Click bảng Khoa
        tblKhoa.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int viewRow = tblKhoa.getSelectedRow();
                if (viewRow != -1) {
                    int modelRow = tblKhoa.convertRowIndexToModel(viewRow);
                    selectedMaKhoa = modelKhoa.getValueAt(modelRow, 0).toString();
                    String tenKhoa = modelKhoa.getValueAt(modelRow, 1).toString();
                    lblMonHocTitle.setText("MÔN HỌC: " + tenKhoa.toUpperCase());
                    lblMonHocTitle.setForeground(COL_PRIMARY);
                    loadMonHoc();
                }
            }
        });

        // --- BUTTON EVENTS ---
        btnAddK.addActionListener(e -> {
            JTextField txtMa = new JTextField();
            JTextField txtTen = new JTextField();
            Object[] message = {"Mã Khoa:", txtMa, "Tên Khoa:", txtTen};

            if (JOptionPane.showConfirmDialog(this, message, "Thêm Khoa", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                String ma = txtMa.getText().trim();
                String ten = txtTen.getText().trim();
                if (ma.isEmpty() || ten.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (KhoaDAO.addKhoa(ma, ten)) {
                    loadKhoa();
                    txtSearchKhoa.setText(""); // Clear search
                    JOptionPane.showMessageDialog(this, "Thêm thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm thất bại!\nCó thể Mã Khoa đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnEditK.addActionListener(e -> {
            int viewRow = tblKhoa.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn Khoa cần sửa!");
                return;
            }
            int modelRow = tblKhoa.convertRowIndexToModel(viewRow);
            String oldMa = modelKhoa.getValueAt(modelRow, 0).toString();
            String oldTen = modelKhoa.getValueAt(modelRow, 1).toString();
            String newTen = JOptionPane.showInputDialog(this, "Tên mới:", oldTen);
            if (newTen != null && !newTen.trim().isEmpty() && KhoaDAO.updateKhoa(oldMa, newTen)) {
                loadKhoa();
            }
        });

        btnDelK.addActionListener(e -> {
            int viewRow = tblKhoa.getSelectedRow();
            if (viewRow != -1 && JOptionPane.showConfirmDialog(this, "Xóa Khoa này (Xóa hết môn học)?") == JOptionPane.YES_OPTION) {
                int modelRow = tblKhoa.convertRowIndexToModel(viewRow);
                String maKhoa = modelKhoa.getValueAt(modelRow, 0).toString();
                if(KhoaDAO.deleteKhoa(maKhoa)){
                    loadKhoa();
                    // Reset bảng môn học an toàn
                    modelMonHoc = new DefaultTableModel();
                    tblMonHoc.setModel(modelMonHoc);
                    sorterMon = null; 
                    lblMonHocTitle.setText("DANH SÁCH MÔN HỌC");
                    lblMonHocTitle.setForeground(new Color(50, 50, 50));
                    selectedMaKhoa = null;
                }
            }
        });

        btnExcelK.addActionListener(e -> exportFile(tblKhoa, "DanhSachKhoa"));

        btnAddM.addActionListener(e -> {
            if (selectedMaKhoa == null) {
                JOptionPane.showMessageDialog(this, "Chọn Khoa trước!");
                return;
            }
            JTextField txtMa = new JTextField();
            JTextField txtTen = new JTextField();
            JTextField txtTC = new JTextField();
            Object[] message = {"Mã Môn:", txtMa, "Tên Môn:", txtTen, "Số TC:", txtTC};

            if (JOptionPane.showConfirmDialog(this, message, "Thêm Môn", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    String ma = txtMa.getText().trim();
                    String ten = txtTen.getText().trim();
                    int tc = Integer.parseInt(txtTC.getText().trim());
                    if (ma.isEmpty() || ten.isEmpty()) return;
                    
                    if (KhoaDAO.addMonHoc(ma, ten, tc, selectedMaKhoa)) {
                        loadMonHoc();
                        txtSearchMon.setText("");
                        JOptionPane.showMessageDialog(this, "Thêm thành công!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Lỗi thêm môn (Mã trùng?)");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Số Tín Chỉ phải là số nguyên!");
                }
            }
        });

        btnEditM.addActionListener(e -> {
            int viewRow = tblMonHoc.getSelectedRow();
            if (viewRow == -1) return;
            int modelRow = tblMonHoc.convertRowIndexToModel(viewRow);
            String ma = modelMonHoc.getValueAt(modelRow, 0).toString();
            String ten = modelMonHoc.getValueAt(modelRow, 1).toString();
            String tc = modelMonHoc.getValueAt(modelRow, 2).toString();
            
            JTextField txtTen = new JTextField(ten);
            JTextField txtTC = new JTextField(tc);
            Object[] message = {"Tên Môn:", txtTen, "Số TC:", txtTC};
            
            if (JOptionPane.showConfirmDialog(this, message, "Sửa Môn", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    if (KhoaDAO.updateMonHoc(ma, txtTen.getText(), Integer.parseInt(txtTC.getText()))) {
                        loadMonHoc();
                    }
                } catch (Exception ex) {}
            }
        });

        btnDelM.addActionListener(e -> {
            int viewRow = tblMonHoc.getSelectedRow();
            if (viewRow != -1 && JOptionPane.showConfirmDialog(this, "Xóa Môn này?") == JOptionPane.YES_OPTION) {
                int modelRow = tblMonHoc.convertRowIndexToModel(viewRow);
                if (KhoaDAO.deleteMonHoc(modelMonHoc.getValueAt(modelRow, 0).toString())) {
                    loadMonHoc();
                }
            }
        });

        btnExcelM.addActionListener(e -> {
            if (tblMonHoc != null && tblMonHoc.getRowCount() > 0) {
                exportFile(tblMonHoc, "MonHoc_" + selectedMaKhoa);
            } else {
                JOptionPane.showMessageDialog(this, "Danh sách trống!");
            }
        });
    }

    // --- SETUP TÌM KIẾM AN TOÀN ---
    private void setupSearchListener(JTextField txt, boolean isKhoa) {
        txt.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilter(); }
            public void removeUpdate(DocumentEvent e) { applyFilter(); }
            public void changedUpdate(DocumentEvent e) { applyFilter(); }
            
            private void applyFilter() {
                String text = txt.getText().trim();
                TableRowSorter<DefaultTableModel> sorter = isKhoa ? sorterKhoa : sorterMon;
                if (sorter != null) {
                    sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + text));
                }
            }
        });
    }

    private void loadKhoa() {
        modelKhoa = KhoaDAO.getDSKhoa();
        tblKhoa.setModel(modelKhoa);
        
        // Cập nhật Sorter mới cho Model mới
        sorterKhoa = new TableRowSorter<>(modelKhoa);
        tblKhoa.setRowSorter(sorterKhoa);
        
        // Áp dụng lại filter nếu đang có chữ trong ô tìm kiếm
        String text = txtSearchKhoa.getText().trim();
        if(!text.isEmpty()) sorterKhoa.setRowFilter(RowFilter.regexFilter("(?i)" + text));
    }

    private void loadMonHoc() {
        if (selectedMaKhoa != null) {
            modelMonHoc = KhoaDAO.getDSMonHoc(selectedMaKhoa);
            tblMonHoc.setModel(modelMonHoc);
            
            sorterMon = new TableRowSorter<>(modelMonHoc);
            tblMonHoc.setRowSorter(sorterMon);
            
            String text = txtSearchMon.getText().trim();
            if(!text.isEmpty()) sorterMon.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    // --- CÁC HÀM HELPER KHÁC GIỮ NGUYÊN ---
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

    private JLabel addHeader(JPanel container, String title, String iconName) {
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
        return lbl;
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
        JTable table = new JTable();
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(35);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(220, 240, 255));
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
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
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);
        return table;
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