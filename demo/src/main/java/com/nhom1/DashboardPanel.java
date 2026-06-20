package com.nhom1;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DashboardPanel extends JPanel {
    
    // Màu sắc
    Color colPrimary = new Color(52, 152, 219);
    Color colDanger = new Color(231, 76, 60);
    Color colSuccess = new Color(46, 204, 113);
    Color colWarning = new Color(241, 196, 15);

    // Bảng màu biểu đồ
    final Color[] CHART_COLORS = {
        new Color(52, 152, 219), new Color(231, 76, 60), new Color(46, 204, 113),
        new Color(155, 89, 182), new Color(241, 196, 15), new Color(52, 73, 94)
    };

    private JLabel lblTongNV, lblTongTS, lblSapHetHD, lblQuyLuong;
    private ChartPanel chartTrinhDo, chartGioiTinh;

    public DashboardPanel() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 245));

        // 1. HEADER
        JLabel lblTitle = new JLabel("TỔNG QUAN QUẢN TRỊ NHÂN SỰ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(44, 62, 80));
        add(lblTitle, BorderLayout.NORTH);

        // 2. CARDS PANEL
        JPanel pnlCards = new JPanel(new GridLayout(1, 4, 20, 0));
        pnlCards.setOpaque(false);
        pnlCards.setPreferredSize(new Dimension(0, 120));

        lblTongNV = createValueLabel(colPrimary);
        lblTongTS = createValueLabel(colSuccess);
        lblSapHetHD = createValueLabel(colDanger);
        lblQuyLuong = createValueLabel(colWarning);

        pnlCards.add(createCard("Tổng Nhân Sự", lblTongNV, colPrimary));
        pnlCards.add(createCard("Tiến Sĩ/PGS", lblTongTS, colSuccess));
        pnlCards.add(createCard("Sắp Hết HĐ", lblSapHetHD, colDanger)); 
        pnlCards.add(createCard("Quỹ Lương T12", lblQuyLuong, colWarning)); 

        // 3. CHARTS
        JPanel pnlCharts = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlCharts.setOpaque(false);
        
        chartTrinhDo = new ChartPanel("Cơ cấu Trình độ", "SELECT TrinhDo, COUNT(*) FROM NhanVien GROUP BY TrinhDo");
        chartGioiTinh = new ChartPanel("Tỷ lệ Nam/Nữ", "SELECT GioiTinh, COUNT(*) FROM NhanVien GROUP BY GioiTinh");
        
        pnlCharts.add(chartTrinhDo);
        pnlCharts.add(chartGioiTinh);

        JPanel pnlCenter = new JPanel(new BorderLayout(0, 20));
        pnlCenter.setOpaque(false);
        pnlCenter.add(pnlCards, BorderLayout.NORTH);
        pnlCenter.add(pnlCharts, BorderLayout.CENTER);
        
        add(pnlCenter, BorderLayout.CENTER);
        
        refreshData();
    }

    // Hàm gọi để làm mới data
    public void refreshData() {
        //số liệu trên thẻ Cards
        new Thread(() -> { // Chạy luồng riêng để không đơ giao diện
            int valTongNV = getCount("SELECT COUNT(*) FROM NhanVien");
            int valTongTS = getCount("SELECT COUNT(*) FROM NhanVien WHERE TrinhDo = N'Tiến sĩ'");
            int valSapHetHD = getCount("SELECT COUNT(*) FROM NhanVien WHERE DATEDIFF(day, GETDATE(), NgayHetHanHD) < 30");
            
            int finalTongNV = (valTongNV == -1) ? 0 : valTongNV;
            int finalTongTS = (valTongTS == -1) ? 0 : valTongTS;
            int finalSapHetHD = (valSapHetHD == -1) ? 0 : valSapHetHD;

            SwingUtilities.invokeLater(() -> {
                lblTongNV.setText(String.valueOf(finalTongNV));
                lblTongTS.setText(String.valueOf(finalTongTS));
                lblSapHetHD.setText(String.valueOf(finalSapHetHD));
                lblQuyLuong.setText("2.4 Tỷ");
            });
        }).start();

        //Biểu đồ
        chartTrinhDo.refresh();
        chartGioiTinh.refresh();
    }

    private JLabel createValueLabel(Color c) {
        JLabel l = new JLabel("...");
        l.setFont(new Font("Arial", Font.BOLD, 32));
        l.setForeground(c);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        return l;
    }

    private JPanel createCard(String title, JLabel lblValue, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, color)); 
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(Color.GRAY);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBorder(new EmptyBorder(0,0,10,0));

        p.add(lblValue, BorderLayout.CENTER);
        p.add(lblTitle, BorderLayout.SOUTH);
        
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230,230,230), 1),
            p.getBorder()
        ));
        return p;
    }

    private int getCount(String sql) {
        try (Connection conn = ConnectDatabase.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { 
            // e.printStackTrace(); // Bỏ comment nếu muốn debug
            return -1; 
        }
        return 0;
    }

    // CHART PANEL
    class ChartPanel extends JPanel {
        String title, sqlQuery;
        
        class Slice {
            String label; double value; Color color;
            public Slice(String l, double v, Color c) { label=l; value=v; color=c; }
        }
        
        List<Slice> slices = new ArrayList<>();
        double totalValue = 0;

        public ChartPanel(String title, String sqlQuery) {
            this.title = title;
            this.sqlQuery = sqlQuery;
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(230,230,230)));
        }

        public void refresh() {
            slices.clear();
            totalValue = 0;
            try (Connection conn = ConnectDatabase.getConnection();
                 Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sqlQuery)) {
                
                int colorIndex = 0;
                while (rs.next()) {
                    String label = rs.getString(1); 
                    if(label == null) label = "Chưa rõ";
                    double val = rs.getDouble(2);   
                    Color c = CHART_COLORS[colorIndex % CHART_COLORS.length];
                    slices.add(new Slice(label, val, c));
                    totalValue += val;
                    colorIndex++;
                }
            } catch (Exception e) {
                slices.add(new Slice("No Data", 1, Color.GRAY));
                totalValue = 1;
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(); int h = getHeight();
            g2.setColor(Color.DARK_GRAY);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.drawString(title, 20, 30);

            if (totalValue == 0) return;

            int size = Math.min(w, h) - 180; 
            if (size < 50) size = 50;

            int chartX = 30; 
            int chartY = (h - size) / 2 + 10;
            
            double startAngle = 90;
            for (Slice s : slices) {
                double angle = (s.value / totalValue) * 360;
                g2.setColor(s.color);
                g2.fill(new Arc2D.Double(chartX, chartY, size, size, startAngle, -angle, Arc2D.PIE));
                startAngle -= angle;
            }
            
            g2.setColor(Color.WHITE);
            int innerSize = size / 2; 
            g2.fill(new Arc2D.Double(chartX + size/4.0, chartY + size/4.0, innerSize, innerSize, 0, 360, Arc2D.PIE));

            drawLegend(g2, chartX + size + 40, chartY); 
        }
        
        private void drawLegend(Graphics2D g2, int x, int y) {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            DecimalFormat df = new DecimalFormat("##.0");
            for (Slice s : slices) {
                g2.setColor(s.color);
                g2.fillRoundRect(x, y, 15, 15, 3, 3);
                g2.setColor(Color.DARK_GRAY);
                double percent = (s.value / totalValue) * 100;
                String text = s.label + " (" + df.format(percent) + "%)";
                g2.drawString(text, x + 25, y + 12);
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.drawString("SL: " + (int)s.value, x + 25, y + 26);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13)); 
                y += 40; 
            }
        }
    }
}