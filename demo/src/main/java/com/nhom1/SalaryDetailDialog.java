package com.nhom1;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;

public class SalaryDetailDialog extends JDialog {

    public SalaryDetailDialog(JFrame parent, String maNV, int thang, int nam, double donGiaTiet) {
        super(parent, "Phiếu Lương Chi Tiết: " + maNV + " - Tháng " + thang + "/" + nam, true);
        setSize(500, 600); 
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        Object[] data = LuongDAO.getChiTietLuong(maNV, thang, nam);
        
        if (data == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu lương cho nhân viên này!");
            dispose();
            return;
        }

        String hoTen = (String) data[1];
        String loaiHinh = (String) data[2];
        double heSo = (double) data[3];
        double luongCB = (double) data[4];
        double phuCap = (double) data[5];
        int tongTiet = (int) data[6];
        double thucLinhDaLuu = (double) data[7]; 

        double luongHeSo = heSo * luongCB;
        double thuLao = tongTiet * donGiaTiet;
        double tongLuongHienTai = luongHeSo + phuCap + thuLao;

        NumberFormat nf = NumberFormat.getInstance();

        JPanel pnlContent = new JPanel(new GridLayout(0, 1, 10, 10));
        pnlContent.setBorder(new EmptyBorder(20, 30, 20, 30));
        pnlContent.setBackground(Color.WHITE);

        addLabel(pnlContent, "HỌ TÊN:", hoTen.toUpperCase(), true);
        addLabel(pnlContent, "LOẠI HÌNH:", loaiHinh, false);
        pnlContent.add(new JSeparator());

        addRow(pnlContent, "1. Lương Cơ Bản:", nf.format(luongCB) + " VNĐ");
        addRow(pnlContent, "    x Hệ Số:", String.valueOf(heSo));
        addRow(pnlContent, "    = Thành Tiền:", nf.format(luongHeSo) + " VNĐ");
        
        pnlContent.add(new JSeparator());
        
        addRow(pnlContent, "2. Phụ Cấp / Thưởng:", nf.format(phuCap) + " VNĐ");
        
        pnlContent.add(new JSeparator());

        addRow(pnlContent, "3. Thù Lao Giảng Dạy:", "");
        addRow(pnlContent, "    Tổng Tiết:", String.valueOf(tongTiet) + " tiết");
        addRow(pnlContent, "    x Đơn Giá:", nf.format(donGiaTiet) + " VNĐ/tiết");
        addRow(pnlContent, "    = Thành Tiền:", nf.format(thuLao) + " VNĐ");

        pnlContent.add(new JSeparator());
        
        JLabel lblTotal = new JLabel("TỔNG THỰC LĨNH: " + nf.format(tongLuongHienTai) + " VNĐ");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setForeground(new Color(231, 76, 60)); 
        lblTotal.setHorizontalAlignment(SwingConstants.CENTER);
        lblTotal.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        pnlContent.add(lblTotal);

        if (thucLinhDaLuu > 0) {
             JLabel lblSaved = new JLabel("(Đã chốt sổ: " + nf.format(thucLinhDaLuu) + " VNĐ)");
             lblSaved.setHorizontalAlignment(SwingConstants.CENTER);
             lblSaved.setForeground(new Color(39, 174, 96));
             pnlContent.add(lblSaved);
        }

        add(pnlContent, BorderLayout.CENTER);
        
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        JPanel pnlBot = new JPanel(); pnlBot.add(btnClose);
        add(pnlBot, BorderLayout.SOUTH);
    }

    private void addLabel(JPanel p, String title, String val, boolean bold) {
        JLabel l = new JLabel(title + " " + val);
        if(bold) l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        p.add(l);
    }

    private void addRow(JPanel p, String title, String val) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.add(new JLabel(title), BorderLayout.WEST);
        row.add(new JLabel(val), BorderLayout.EAST);
        p.add(row);
    }
}