package ui;

import dao.TaiKhoanDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class UI_DangNhap extends JFrame implements ActionListener {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;
    private JButton btnThoat; // Thêm khai báo nút Thoát
    private TaiKhoanDAO taiKhoanDAO;

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String fileName) {
            backgroundImage = new ImageIcon(fileName).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public UI_DangNhap() {
        taiKhoanDAO = new TaiKhoanDAO();
        setTitle("Hệ Thống Đặt Vé - Đăng Nhập");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        BackgroundPanel bgPanel = new BackgroundPanel("images/bg_login.jpg");
        bgPanel.setLayout(null);
        setContentPane(bgPanel);

        // --- TIÊU ĐỀ ---
        JLabel lblTitle = new JLabel("RẠP CHIẾU PHIM XIN CHÀO");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(80, 20, 300, 30);
        bgPanel.add(lblTitle);

        // --- TÀI KHOẢN ---
        JLabel lblUser = new JLabel("Tài khoản:");
        lblUser.setFont(new Font("Arial", Font.BOLD, 14));
        lblUser.setForeground(Color.WHITE);
        lblUser.setBounds(60, 80, 80, 25);
        bgPanel.add(lblUser);

        txtUser = new JTextField();
        txtUser.setBounds(150, 80, 200, 30);
        bgPanel.add(txtUser);

        // --- MẬT KHẨU ---
        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setFont(new Font("Arial", Font.BOLD, 14));
        lblPass.setForeground(Color.WHITE);
        lblPass.setBounds(60, 130, 80, 25);
        bgPanel.add(lblPass);

        txtPass = new JPasswordField();
        txtPass.setBounds(150, 130, 200, 30);
        bgPanel.add(txtPass);

        // --- NÚT ĐĂNG NHẬP ---
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setBackground(new Color(52, 152, 219)); // Màu xanh dương nhạt
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        // Đẩy nút đăng nhập sang trái (X = 90)
        btnLogin.setBounds(90, 180, 120, 35);
        bgPanel.add(btnLogin);

        // --- NÚT THOÁT ---
        btnThoat = new JButton("Thoát");
        btnThoat.setFont(new Font("Arial", Font.BOLD, 14));
        btnThoat.setBackground(new Color(231, 76, 60)); // Màu đỏ
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setFocusPainted(false);
        // Đặt nút thoát nằm bên phải (X = 230)
        btnThoat.setBounds(230, 180, 120, 35);
        bgPanel.add(btnThoat);

        // Thêm sự kiện click cho cả 2 nút
        btnLogin.addActionListener(this);
        btnThoat.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource(); // Lấy đối tượng vừa được click
        
        if (o == btnThoat) {
            // Xử lý khi bấm nút Thoát
            int confirm = JOptionPane.showConfirmDialog(this, 
                    "Bạn có chắc chắn muốn thoát ứng dụng?", 
                    "Xác nhận thoát", 
                    JOptionPane.YES_NO_OPTION);
                    
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0); // Tắt chương trình
            }
            
        } else if (o == btnLogin) {
            // Xử lý khi bấm nút Đăng nhập
            String user = txtUser.getText();
            String pass = new String(txtPass.getPassword());

            if (taiKhoanDAO.kiemTraDangNhap(user, pass)) {
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
                new UI_TrangChu().setVisible(true); 
                this.dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new UI_DangNhap().setVisible(true);
        });
    }
}