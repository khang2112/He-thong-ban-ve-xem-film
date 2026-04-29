package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class UI_TrangChu extends JFrame implements ActionListener {
    
    // Khai báo các màu sắc chủ đạo
    private Color darkBg = new Color(30, 30, 30);      
    private Color sidebarBg = new Color(20, 20, 20);   
    private Color textWhite = new Color(220, 220, 220);

    private JButton btnTrangChu, btnPhim, btnSuatChieu, btnNhanVien, btnHoaDon, btnBanVe, btnThongKe, btnDangXuat;
    private JButton btnKhachHang;
    
    // --- KHAI BÁO CARDLAYOUT ---
    private JPanel pnlCards; 
    private CardLayout cardLayout;

    public UI_TrangChu() {
        setTitle("Hệ Thống Quản Lý Rạp Chiếu Phim");
        setSize(1000, 700); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- 1. HEADER ---
        JLabel lblHeader = new JLabel("HỆ THỐNG QUẢN LÝ RẠP CHIẾU PHIM", JLabel.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 24));
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setOpaque(true);
        lblHeader.setBackground(Color.BLACK);
        lblHeader.setBorder(new EmptyBorder(10, 0, 10, 0));
        add(lblHeader, BorderLayout.NORTH);

        // --- 2. SIDEBAR ---
        JPanel pnlSidebar = new JPanel();
        pnlSidebar.setLayout(new GridLayout(10, 1, 0, 5)); 
        pnlSidebar.setBackground(sidebarBg);
        pnlSidebar.setPreferredSize(new Dimension(180, 0)); 
        pnlSidebar.setBorder(new EmptyBorder(20, 0, 0, 0));

        btnTrangChu = createMenuButton("Trang chủ");
        btnPhim = createMenuButton("Phim");
        btnSuatChieu = createMenuButton("Suất chiếu");
        btnNhanVien = createMenuButton("Nhân viên");
        btnKhachHang = createMenuButton("Khách hàng");
        btnHoaDon = createMenuButton("Hoá đơn");
        btnBanVe = createMenuButton("Bán vé");
        btnThongKe = createMenuButton("Thống kê");
        btnDangXuat = createMenuButton("Đăng xuất");
        btnDangXuat.setForeground(new Color(231, 76, 60)); // Đỏ nổi bật cho nút đăng xuất
        
        pnlSidebar.add(btnTrangChu);
        pnlSidebar.add(btnPhim);
        pnlSidebar.add(btnSuatChieu);
        pnlSidebar.add(btnNhanVien);
        pnlSidebar.add(btnKhachHang); // <-- Đã đưa Khách hàng lên nhóm trên
        pnlSidebar.add(btnHoaDon);
        pnlSidebar.add(btnBanVe);
        pnlSidebar.add(btnThongKe);
        pnlSidebar.add(Box.createVerticalGlue()); 
        pnlSidebar.add(btnDangXuat); // <-- Đăng xuất nằm gọn gàng ở cuối

        add(pnlSidebar, BorderLayout.WEST);

        // --- 3. VÙNG TRUNG TÂM (CARD LAYOUT) ---
        cardLayout = new CardLayout();
        pnlCards = new JPanel(cardLayout);

        JPanel pnlHome = createTrangChuPanel();
        PNL_Phim pnlPhim = new PNL_Phim();
        
        pnlCards.add(pnlHome, "TrangChu");
        pnlCards.add(pnlPhim, "Phim");
        pnlCards.add(new PNL_SuatChieu(), "SuatChieu");
        pnlCards.add(new PNL_NhanVien(), "NhanVien");
        pnlCards.add(new PNL_KhachHang(), "KhachHang");
        pnlCards.add(new PNL_HoaDon(), "HoaDon");
        pnlCards.add(new PNL_BanVe(), "BanVe");
        pnlCards.add(new PNL_ThongKe(), "ThongKe");
        
        add(pnlCards, BorderLayout.CENTER);

        // --- 4. FOOTER ---
        JLabel lblFooter = new JLabel("Nhóm Quản lý Rạp Chiếu Phim", JLabel.CENTER);
        lblFooter.setForeground(Color.GRAY);
        lblFooter.setBorder(new EmptyBorder(5, 0, 5, 0));
        add(lblFooter, BorderLayout.SOUTH);
    }

    // ==========================================================
    // HÀM TẠO NỘI DUNG TRANG CHỦ
    // ==========================================================
    private JPanel createTrangChuPanel() {
        JPanel pnlContent = new JPanel(new BorderLayout(10, 10));
        pnlContent.setBackground(darkBg);
        pnlContent.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Banner Slider ---
        JLabel lblBanner = new JLabel();
        lblBanner.setHorizontalAlignment(JLabel.CENTER);
        lblBanner.setForeground(Color.WHITE);
        pnlContent.add(lblBanner, BorderLayout.NORTH);

        String[] danhSachBanner = {
            "images/banner1.jpg",
            "images/banner2.jpg",
            "images/banner3.jpg"
        };
        
        ImageIcon[] bannerIcons = new ImageIcon[danhSachBanner.length];
        for (int i = 0; i < danhSachBanner.length; i++) {
            bannerIcons[i] = scaleImage(danhSachBanner[i], 800, 280); 
        }

        if (bannerIcons[0] != null) {
            lblBanner.setIcon(bannerIcons[0]);
        } else {
            lblBanner.setText("[Lỗi tải ảnh Banner]");
        }

        Timer bannerTimer = new Timer(3000, new ActionListener() {
            int currentBannerIndex = 0; 
            @Override
            public void actionPerformed(ActionEvent e) {
                if (bannerIcons.length > 0 && bannerIcons[0] != null) {
                    currentBannerIndex++; 
                    if (currentBannerIndex >= bannerIcons.length) {
                        currentBannerIndex = 0;
                    }
                    lblBanner.setIcon(bannerIcons[currentBannerIndex]);
                }
            }
        });
        bannerTimer.start(); 

        // --- Khung chứa Tiêu đề và Danh sách phim ---
        JPanel pnlBottom = new JPanel(new BorderLayout(5, 5));
        pnlBottom.setBackground(darkBg);

        JLabel lblTitlePhim = new JLabel("🎥 Danh sách phim");
        lblTitlePhim.setForeground(textWhite);
        lblTitlePhim.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitlePhim.setBorder(new EmptyBorder(5, 5, 5, 0));
        pnlBottom.add(lblTitlePhim, BorderLayout.NORTH);

        JPanel pnlMovieList = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlMovieList.setBackground(darkBg);

        pnlMovieList.add(createMovieCard("images/datrungphuongnam.jpg", "Đất rừng phương nam", "Phim lịch sử - hành động"));
        pnlMovieList.add(createMovieCard("images/springjourney.jpg", "Bố Già", "Phim tâm lý - gia đình"));
        pnlMovieList.add(createMovieCard("images/thejunglebook.jpg", "Avatar 2", "Phim khoa học viễn tưởng"));

        JScrollPane scrollPane = new JScrollPane(pnlMovieList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(darkBg);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); 
        
        pnlBottom.add(scrollPane, BorderLayout.CENTER);
        pnlContent.add(pnlBottom, BorderLayout.CENTER);

        return pnlContent;
    }

    // --- HÀM HỖ TRỢ ---
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(textWhite);
        btn.setBackground(sidebarBg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setHorizontalAlignment(SwingConstants.LEFT); 
        btn.addActionListener(this);
        return btn;
    }

    private JPanel createMovieCard(String imagePath, String title, String desc) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(40, 40, 40));
        card.setPreferredSize(new Dimension(200, 280)); 
        card.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));

        JLabel lblImg = new JLabel();
        lblImg.setHorizontalAlignment(JLabel.CENTER);
        ImageIcon icon = scaleImage(imagePath, 180, 220);
        if(icon != null) lblImg.setIcon(icon);
        else lblImg.setText("No Image");
        lblImg.setForeground(Color.WHITE);

        JLabel lblTitle = new JLabel(title, JLabel.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 13));

        JLabel lblDesc = new JLabel("<html><center>" + desc + "</center></html>", JLabel.CENTER);
        lblDesc.setForeground(Color.GRAY);
        lblDesc.setFont(new Font("Arial", Font.PLAIN, 11));

        JPanel pnlText = new JPanel(new GridLayout(2, 1));
        pnlText.setBackground(new Color(40, 40, 40));
        pnlText.add(lblTitle);
        pnlText.add(lblDesc);

        card.add(lblImg, BorderLayout.CENTER);
        card.add(pnlText, BorderLayout.SOUTH);

        return card;
    }

    private ImageIcon scaleImage(String path, int w, int h) {
        try {
            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        } catch (Exception e) {
            return null; 
        }
    }

    // --- XỬ LÝ SỰ KIỆN CHUYỂN TRANG ---
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == btnTrangChu) {
            cardLayout.show(pnlCards, "TrangChu");
        } else if (source == btnPhim) {
            cardLayout.show(pnlCards, "Phim");
        } else if (source == btnSuatChieu) {
            cardLayout.show(pnlCards, "SuatChieu");
        } else if (source == btnNhanVien) {
            cardLayout.show(pnlCards, "NhanVien");
        } else if (source == btnKhachHang) {
            Component[] comps = pnlCards.getComponents();
            for (Component c : comps) {
                if (c instanceof PNL_KhachHang) {
                    ((PNL_KhachHang) c).loadDataToTable(); // Cập nhật bảng
                }
            }
            cardLayout.show(pnlCards, "KhachHang");
        } else if (source == btnHoaDon) {
            Component[] comps = pnlCards.getComponents();
            for (Component c : comps) {
                if (c instanceof PNL_HoaDon) {
                    ((PNL_HoaDon) c).loadDataHoaDon(); // Cập nhật hóa đơn
                }
            }
            cardLayout.show(pnlCards, "HoaDon");
        } else if (source == btnBanVe) {
            cardLayout.show(pnlCards, "BanVe");
        } else if (source == btnThongKe) {
            Component[] comps = pnlCards.getComponents();
            for (Component c : comps) {
                if (c instanceof PNL_ThongKe) {
                    ((PNL_ThongKe) c).loadData(); // Cập nhật thống kê
                }
            }
            cardLayout.show(pnlCards, "ThongKe");
        } else if (source == btnDangXuat) {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new UI_DangNhap().setVisible(true);
                this.dispose();
            }
        } else {
            JButton clickedBtn = (JButton) source;
            JOptionPane.showMessageDialog(this, "Chức năng '" + clickedBtn.getText() + "' đang phát triển!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new UI_TrangChu().setVisible(true);
        });
    }
}