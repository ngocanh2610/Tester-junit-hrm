package com.nhom1;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class EvaluationPanel extends JPanel {

    private JTable tblEval;
    private JTextField txtNam;

    private final Color COL_PRIMARY = new Color(0, 150, 136);
    private final Color COL_BG = new Color(245, 247, 250);

    public EvaluationPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(COL_BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- MAIN CARD ---
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));

        // --- TOP: CONTROL ---
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTop.setBackground(Color.WHITE);
        pnlTop.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel lblTitle = new JLabel("ĐÁNH GIÁ THI ĐUA & KPI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(50,50,50));
        
        txtNam = new JTextField(String.valueOf(LocalDate.now().getYear()), 5);
        txtNam.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtNam.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(0,0,2,0, COL_PRIMARY), new EmptyBorder(5,5,5,5)));

        JButton btnLoad = new JButton("Tổng Hợp Số Liệu");
        btnLoad.setBackground(new Color(52, 152, 219)); btnLoad.setForeground(Color.WHITE);
        
        JButton btnSave = new JButton("Lưu Kết Quả");
        btnSave.setBackground(new Color(230, 126, 34)); btnSave.setForeground(Color.WHITE);
        
        pnlTop.add(lblTitle);
        pnlTop.add(Box.createHorizontalStrut(30));
        pnlTop.add(new JLabel("Năm: ")); pnlTop.add(txtNam);
        pnlTop.add(Box.createHorizontalStrut(10));
        pnlTop.add(btnLoad); pnlTop.add(btnSave);
        
        card.add(pnlTop, BorderLayout.NORTH);

        // --- CENTER: TABLE ---
        tblEval = new JTable();
        tblEval.setRowHeight(35);
        tblEval.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblEval.getTableHeader().setPreferredSize(new Dimension(0, 40));
        tblEval.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(COL_PRIMARY); lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 14)); lbl.setHorizontalAlignment(JLabel.CENTER);
                return lbl;
            }
        });
        
        card.add(new JScrollPane(tblEval), BorderLayout.CENTER);
        
        // --- BOTTOM: NOTE ---
        JLabel lblNote = new JLabel("<html><i>* Tiêu chuẩn A: >270 tiết dạy & >1 điểm NCKH | Double click cột 'Xếp Loại' để chọn đánh giá.</i></html>");
        lblNote.setForeground(Color.GRAY);
        lblNote.setBorder(new EmptyBorder(10, 0, 0, 0));
        card.add(lblNote, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);

        // --- EVENT ---
        btnLoad.addActionListener(e -> refreshData());
        btnSave.addActionListener(e -> {
            if(JOptionPane.showConfirmDialog(this, "Lưu kết quả xếp loại?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                saveData();
            }
        });
        refreshData();
    }

    public void refreshData() {
        try {
            int nam = Integer.parseInt(txtNam.getText());
            tblEval.setModel(EvaluationDAO.calculateKPI(nam));
            if (tblEval.getColumnCount() > 4) {
                tblEval.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(
                    new JComboBox<>(new String[]{"A - Xuất Sắc", "B - Hoàn Thành Tốt", "C - Hoàn Thành", "D - Không Hoàn Thành"})
                ));
            }
        } catch(Exception ex) {}
    }

    private void saveData() {
        try {
            int nam = Integer.parseInt(txtNam.getText());
            DefaultTableModel model = (DefaultTableModel) tblEval.getModel();
            int count = 0;
            for(int i=0; i<model.getRowCount(); i++) {
                String maNV = model.getValueAt(i, 0).toString();
                int tongTiet = Integer.parseInt(model.getValueAt(i, 2).toString());
                double tongDiem = Double.parseDouble(model.getValueAt(i, 3).toString());
                String xepLoai = model.getValueAt(i, 4).toString();
                if(EvaluationDAO.saveEvaluation(maNV, nam, tongTiet, tongDiem, xepLoai)) count++;
            }
            JOptionPane.showMessageDialog(this, "Đã lưu " + count + " hồ sơ!");
            refreshData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }
}