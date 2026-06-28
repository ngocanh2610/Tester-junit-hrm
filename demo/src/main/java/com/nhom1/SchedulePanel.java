package com.nhom1;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public class SchedulePanel extends JPanel {

    private JTable tblSchedule;
    private JTable tblStaff;
    private DefaultTableModel modelSchedule;

    private LocalDate currentMonday;
    private JLabel lblDateRange;
    private String selectedMaNV = null;
    private String selectedTenNV = "";
    private JButton btnAddLich;

    // COLORS
    private final Color COL_PRIMARY = new Color(0, 150, 136);
    private final Color COL_BG = new Color(245, 247, 250);
    // private final Color COL_HEADER = Color.WHITE;

    public SchedulePanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(COL_BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        currentMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // --- LEFT CARD: DANH SÁCH GIẢNG VIÊN ---
        JPanel pnlLeft = createCardPanel();
        pnlLeft.setPreferredSize(new Dimension(350, 0));
        addHeader(pnlLeft, "1. CHỌN GIẢNG VIÊN", "staff.png");

        tblStaff = createModernTable();
        pnlLeft.add(new JScrollPane(tblStaff), BorderLayout.CENTER);

        // Toolbar bên trái
        JPanel pnlLeftTools = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlLeftTools.setBackground(Color.WHITE);
        pnlLeftTools.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnAddLich = createBtn("Phân Công", new Color(46, 204, 113), "add.png");
        JButton btnExport = createBtn("Xuất Excel", new Color(39, 174, 96), "excel.png");

        pnlLeftTools.add(btnAddLich);
        pnlLeftTools.add(btnExport);
        pnlLeft.add(pnlLeftTools, BorderLayout.SOUTH);

        // --- RIGHT CARD: THỜI KHÓA BIỂU ---
        JPanel pnlRight = createCardPanel();

        // Nav Bar
        JPanel pnlNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pnlNav.setBackground(Color.WHITE);
        pnlNav.setBorder(new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        JButton btnPrev = createNavBtn("◀");
        JButton btnNext = createNavBtn("▶");
        JButton btnToday = createNavBtn("Hiện tại");

        lblDateRange = new JLabel();
        lblDateRange.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDateRange.setForeground(COL_PRIMARY);

        pnlNav.add(btnPrev);
        pnlNav.add(lblDateRange);
        pnlNav.add(btnNext);
        pnlNav.add(btnToday);
        pnlRight.add(pnlNav, BorderLayout.NORTH);

        // Table Schedule
        String[] columns = { "Tiết", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ Nhật" };
        modelSchedule = new DefaultTableModel(new Object[15][8], columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblSchedule = new JTable(modelSchedule);
        tblSchedule.setRowHeight(60); // Cao hơn để chứa nhiều dòng
        tblSchedule.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblSchedule.setGridColor(new Color(220, 220, 220));
        tblSchedule.setShowVerticalLines(true);
        tblSchedule.setShowHorizontalLines(true);

        // Header style
        tblSchedule.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                lbl.setBackground(COL_PRIMARY);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                lbl.setHorizontalAlignment(JLabel.CENTER);
                return lbl;
            }
        });
        tblSchedule.getTableHeader().setPreferredSize(new Dimension(0, 40));

        // Custom Cell Renderer cho TKB (Wrap text)
        tblSchedule.setDefaultRenderer(Object.class, new MultiLineCellRenderer());
        tblSchedule.getColumnModel().getColumn(0).setMaxWidth(60); // Cột Tiết nhỏ lại

        pnlRight.add(new JScrollPane(tblSchedule), BorderLayout.CENTER);

        add(pnlLeft, BorderLayout.WEST);
        add(pnlRight, BorderLayout.CENTER);

        // --- EVENTS ---
        updateDateLabel();

        btnPrev.addActionListener(e -> changeWeek(-1));
        btnNext.addActionListener(e -> changeWeek(1));
        btnToday.addActionListener(e -> {
            currentMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            updateDateLabel();
            if (selectedMaNV != null)
                loadSchedule(selectedMaNV);
        });

        tblStaff.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tblStaff.getSelectedRow();
                if (row != -1) {
                    selectedMaNV = tblStaff.getValueAt(row, 0).toString();
                    selectedTenNV = tblStaff.getValueAt(row, 1).toString();
                    loadSchedule(selectedMaNV);
                }
            }
        });

        btnAddLich.addActionListener(e -> {
            if (selectedMaNV == null)
                JOptionPane.showMessageDialog(this, "Vui lòng chọn Giảng viên trước!");
            else
                showAddScheduleDialog(selectedMaNV, selectedTenNV);
        });

        btnExport.addActionListener(e -> {
            if (selectedMaNV == null) {
                JOptionPane.showMessageDialog(this, "Chọn Giảng viên để xuất lịch!");
                return;
            }
            exportExcel();
        });

        refreshStaffTable();
    }

    // --- LOGIC ---
    private void changeWeek(int weeksToAdd) {
        currentMonday = currentMonday.plusWeeks(weeksToAdd);
        updateDateLabel();
        if (selectedMaNV != null)
            loadSchedule(selectedMaNV);
    }

    private void updateDateLabel() {
        LocalDate sunday = currentMonday.plusDays(6);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
        lblDateRange.setText(" " + currentMonday.format(fmt) + " - " + sunday.format(fmt) + " ");
    }

    private void loadSchedule(String maNV) {
        String monStr = currentMonday.toString();
        String sunStr = currentMonday.plusDays(6).toString();
        Object[][] data = LichDayDAO.getScheduleMatrix(maNV, monStr, sunStr);
        String[] columns = { "Tiết", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ Nhật" };
        modelSchedule.setDataVector(data, columns);
        tblSchedule.getColumnModel().getColumn(0).setMaxWidth(60);
    }

    public void refreshStaffTable() {
        if (Auth.isGiangVien() && Auth.maNV != null) {
            tblStaff.setModel(NhanSuDAO.getNhanVienByMa(Auth.maNV));
            if (btnAddLich != null)
                btnAddLich.setVisible(false);
        } else {
            tblStaff.setModel(NhanSuDAO.getNhanVienModel());
            if (btnAddLich != null)
                btnAddLich.setVisible(true);
        }
        if (tblStaff.getColumnCount() > 4) {
            for (int i = 4; i < tblStaff.getColumnCount(); i++) {
                tblStaff.getColumnModel().getColumn(i).setMinWidth(0);
                tblStaff.getColumnModel().getColumn(i).setMaxWidth(0);
            }
        }
        if (Auth.isGiangVien() && tblStaff.getRowCount() > 0) {
            tblStaff.setRowSelectionInterval(0, 0);
            selectedMaNV = tblStaff.getValueAt(0, 0).toString();
            selectedTenNV = tblStaff.getValueAt(0, 1).toString();
            loadSchedule(selectedMaNV);
        }
    }

    private void exportExcel() {
        try {
            LocalDate sunday = currentMonday.plusDays(6);
            DateTimeFormatter fmtFilename = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String fileName = selectedTenNV + " TKB " + currentMonday.format(fmtFilename) + " den "
                    + sunday.format(fmtFilename) + ".xlsx";
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File(fileName));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                ExcelExporter.exportToExcel(tblSchedule, fc.getSelectedFile(), "ThoiKhoaBieu");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi xuất file: " + ex.getMessage());
        }
    }

    private void showAddScheduleDialog(String maNV, String tenNV) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Phân công: " + tenNV, true);
        d.setSize(450, 500);
        d.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridLayout(9, 2, 10, 15));
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        JComboBox<String> cboMonHoc = new JComboBox<>();
        for (String mh : LichDayDAO.getDSMonHoc())
            cboMonHoc.addItem(mh);

        JTextField txtPhong = new JTextField();
        JComboBox<String> cboThu = new JComboBox<>(
                new String[] { "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ Nhật" });
        JTextField txtTiet = new JTextField();
        JTextField txtSoTiet = new JTextField();
        JTextField txtTuNgay = new JTextField(currentMonday.toString());
        JTextField txtDenNgay = new JTextField(currentMonday.plusMonths(4).toString());

        JButton btnLuu = new JButton("Lưu & Phân công");
        btnLuu.setBackground(new Color(46, 204, 113));
        btnLuu.setForeground(Color.WHITE);

        p.add(new JLabel("Môn học:"));
        p.add(cboMonHoc);
        p.add(new JLabel("Phòng học:"));
        p.add(txtPhong);
        p.add(new JLabel("Thứ:"));
        p.add(cboThu);
        p.add(new JLabel("Tiết bắt đầu (1-15):"));
        p.add(txtTiet);
        p.add(new JLabel("Số tiết dạy:"));
        p.add(txtSoTiet);
        p.add(new JLabel("Từ ngày (yyyy-MM-dd):"));
        p.add(txtTuNgay);
        p.add(new JLabel("Đến ngày (yyyy-MM-dd):"));
        p.add(txtDenNgay);
        p.add(new JLabel(""));
        p.add(btnLuu);

        btnLuu.addActionListener(ev -> {
            try {
                if (txtTiet.getText().isEmpty() || txtSoTiet.getText().isEmpty() || txtPhong.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(d, "Vui lòng điền đầy đủ thông tin!");
                    return;
                }
                int thu = cboThu.getSelectedIndex() + 2;
                int tiet = Integer.parseInt(txtTiet.getText());
                int soTiet = Integer.parseInt(txtSoTiet.getText());
                String phong = txtPhong.getText();
                String tuNgay = txtTuNgay.getText();
                String denNgay = txtDenNgay.getText();
                String tenMon = cboMonHoc.getSelectedItem() != null ? cboMonHoc.getSelectedItem().toString() : "";
                LichDayDAO dao = new LichDayDAO();
                boolean isSuccess = dao.kiemTraVaPhanCong(
                        maNV,
                        tenMon,
                        "Thứ " + thu,
                        tiet,
                        soTiet,
                        phong,
                        LocalDate.parse(tuNgay),
                        LocalDate.parse(denNgay));

                if (isSuccess) {
                    JOptionPane.showMessageDialog(d, "Thành công!");
                    loadSchedule(maNV);
                    d.dispose();
                } else {
                    JOptionPane.showMessageDialog(d, "Phân công bị trùng lịch hoặc trùng phòng!");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(d, "Lỗi: Tiết và số tiết phải là số nguyên!");
            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(d, "Lỗi: Ngày tháng phải đúng định dạng yyyy-MM-dd!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(d, "Lỗi hệ thống: " + ex.getMessage());
            }
        });
        d.add(p);
        d.setVisible(true);
    }

    private JPanel createCardPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(10, 10, 10, 10)));
        return p;
    }

    private JTable createModernTable() {
        JTable t = new JTable();
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setRowHeight(35);
        t.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                lbl.setBackground(COL_PRIMARY);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                lbl.setHorizontalAlignment(JLabel.CENTER);
                return lbl;
            }
        });
        return t;
    }

    private JButton createBtn(String t, Color bg, String i) {
        JButton b = new JButton(t);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(0, 40));
        ImageIcon icon = loadResizedIcon(i, 20, 20);
        if (icon != null)
            b.setIcon(icon);
        return b;
    }

    private JButton createNavBtn(String t) {
        JButton b = new JButton(t);
        b.setBackground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }

    private void addHeader(JPanel c, String t, String i) {
        JPanel h = new JPanel(new FlowLayout(FlowLayout.LEFT));
        h.setBackground(Color.WHITE);
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setForeground(new Color(50, 50, 50));
        ImageIcon icon = loadResizedIcon(i, 30, 30);
        if (icon != null)
            l.setIcon(icon);
        h.add(l);
        c.add(h, BorderLayout.NORTH);
    }

    private ImageIcon loadResizedIcon(String path, int w, int h) {
        URL url = getClass().getResource("/icons/" + path);
        if (url == null)
            return null;
        Image scaled = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {
        public MultiLineCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            setText(value == null ? "" : value.toString());
            setBackground(
                    isSelected ? new Color(220, 240, 255) : (column == 0 ? new Color(240, 240, 240) : Color.WHITE));
            setForeground(Color.BLACK);
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(230, 230, 230)));
            return this;
        }
    }
}