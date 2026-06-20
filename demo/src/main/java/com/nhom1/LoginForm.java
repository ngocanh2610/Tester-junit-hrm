package com.nhom1;
import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {

    public LoginForm() {
        setTitle("LOGIN SYSTEM");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JLabel lblTitle = new JLabel("HRM LOGIN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(41, 128, 185));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        // Form nhập liệu
        JPanel pnlCenter = new JPanel(new GridLayout(2, 2, 10, 10));
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        
        JTextField txtUser = new JTextField("admin"); // Điền sẵn để test cho nhanh
        JPasswordField txtPass = new JPasswordField("123");
        
        pnlCenter.add(new JLabel("Tài khoản:")); pnlCenter.add(txtUser);
        pnlCenter.add(new JLabel("Mật khẩu:")); pnlCenter.add(txtPass);
        add(pnlCenter, BorderLayout.CENTER);

        // Nút Login
        JButton btnLogin = new JButton("ĐĂNG NHẬP");
        btnLogin.setBackground(new Color(41, 128, 185));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(0, 50));
        
        // Sự kiện Login
        btnLogin.addActionListener(e -> {
            String u = txtUser.getText();
            String p = new String(txtPass.getPassword());
            
            if (TaiKhoanDAO.checkLogin(u, p)) {
                new MainDashboard().setVisible(true); // Mở Dashboard
                this.dispose(); // Đóng form login
            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        add(btnLogin, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnLogin); // Cho phép bấm Enter
    }

    public static void main(String[] args) {
        // Chạy ứng dụng từ đây
        new LoginForm().setVisible(true);
    }
}