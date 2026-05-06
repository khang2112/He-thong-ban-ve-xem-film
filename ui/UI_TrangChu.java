package ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import dao.PhimDAO;
import entity.TaiKhoan;
import entity.TopPhim;

public class UI_TrangChu extends JFrame implements ActionListener {
    // Lưu thông tin người đăng nhập
    private TaiKhoan userLogin;
    private boolean isAdmin; // Biến kiểm tra quyền Quản lý
    
    // --- BẢNG MÀU CHUYÊN NGHIỆP ---
    private Color bgDark = new Color(18, 18, 18);         
    private Color bgSidebar = new Color(25, 25, 25);      
    private Color colorHover = new Color(45, 45, 45);     
    private Color colorGold = new Color(212, 175, 55);    
    private Color colorRed = new Color(220, 20, 60);      
    private Color textWhite = new Color(240, 240, 240);   
    private Color textGray = new Color(150, 150, 150);    

    private MenuButton btnTrangChu, btnPhim, btnSuatChieu, btnNhanVien, btnHoaDon, btnBanVe, btnThongKe, btnDangXuat;
    private MenuButton btnKhachHang;
    
    private JPanel pnlCards; 
    private CardLayout cardLayout;
    private PNL_SuatChieu pnlSuatChieu;
    private PNL_BanVe pnlBanVe;

    // --- LỚP NÚT BẤM MENU ---
    class MenuButton extends JButton {
        public MenuButton(String text) {
            super("  " + text); 
            setFont(new Font("Segoe UI", Font.BOLD, 15));
            setForeground(textWhite);
            setBackground(bgSidebar);
            setFocusPainted(false);
            setContentAreaFilled(false); 
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setHorizontalAlignment(SwingConstants.LEFT);
            
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { setBackground(colorHover); repaint(); }
                public void mouseExited(MouseEvent e) { setBackground(bgSidebar); repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public UI_TrangChu(TaiKhoan tk) {
        this.userLogin = tk;
        
        // Phân quyền cứng dựa vào tên đăng nhập do DB không có cột VaiTro
        this.isAdmin = (userLogin != null && userLogin.getTenDangNhap().equalsIgnoreCase("admin"));
        
        setTitle("Hệ Thống Quản Lý Rạp Chiếu Phim - POS");
        setSize(1200, 750); 
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ==========================================
        // 1. SIDEBAR (THANH ĐIỀU HƯỚNG TRÁI)
        // ==========================================
        JPanel pnlSidebar = new JPanel();
        pnlSidebar.setLayout(new BorderLayout()); 
        pnlSidebar.setBackground(bgSidebar);
        pnlSidebar.setPreferredSize(new Dimension(220, 0)); 
        pnlSidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(50, 50, 50))); 

        JPanel pnlLogo = new JPanel(new GridLayout(2, 1));
        pnlLogo.setBackground(bgSidebar);
        pnlLogo.setBorder(new EmptyBorder(25, 20, 25, 20));
        
        JLabel lblLogoTitle = new JLabel("CINEMA POS", JLabel.LEFT);
        lblLogoTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogoTitle.setForeground(colorRed);
        
        JLabel lblLogoSub = new JLabel("Management System", JLabel.LEFT);
        lblLogoSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLogoSub.setForeground(textGray);
        
        pnlLogo.add(lblLogoTitle);
        pnlLogo.add(lblLogoSub);
        pnlSidebar.add(pnlLogo, BorderLayout.NORTH);

        JPanel pnlMenu = new JPanel(new GridLayout(10, 1, 0, 5));
        pnlMenu.setBackground(bgSidebar);
        pnlMenu.setBorder(new EmptyBorder(10, 10, 10, 10));

        btnTrangChu = new MenuButton("Trang chủ");
        btnPhim = new MenuButton("Quản lý Phim");
        btnSuatChieu = new MenuButton("Suất chiếu");
        btnBanVe = new MenuButton("Bán vé (POS)");
        btnHoaDon = new MenuButton("Hóa đơn");
        btnKhachHang = new MenuButton("Khách hàng");
        btnNhanVien = new MenuButton("Nhân viên");
        btnThongKe = new MenuButton("Thống kê");
        
        btnDangXuat = new MenuButton("Đăng xuất");
        btnDangXuat.setForeground(new Color(231, 76, 60)); 
        btnBanVe.setForeground(colorGold);
        
        pnlMenu.add(btnTrangChu);
        pnlMenu.add(btnBanVe); 
        pnlMenu.add(btnPhim);
        pnlMenu.add(btnSuatChieu);
        pnlMenu.add(btnKhachHang);
        pnlMenu.add(btnHoaDon);
        pnlMenu.add(btnNhanVien);
        pnlMenu.add(btnThongKe);
        
        pnlSidebar.add(pnlMenu, BorderLayout.CENTER);

        JPanel pnlLogout = new JPanel(new BorderLayout());
        pnlLogout.setBackground(bgSidebar);
        pnlLogout.setBorder(new EmptyBorder(10, 10, 20, 10));
        pnlLogout.add(btnDangXuat, BorderLayout.CENTER);
        pnlSidebar.add(pnlLogout, BorderLayout.SOUTH);

        add(pnlSidebar, BorderLayout.WEST);

        // Đăng ký sự kiện
        btnTrangChu.addActionListener(this);
        btnPhim.addActionListener(this);
        btnSuatChieu.addActionListener(this);
        btnNhanVien.addActionListener(this);
        btnKhachHang.addActionListener(this);
        btnHoaDon.addActionListener(this);
        btnBanVe.addActionListener(this);
        btnThongKe.addActionListener(this);
        btnDangXuat.addActionListener(this);

        // ==========================================
        // 2. VÙNG TRUNG TÂM (NỘI DUNG CHÍNH)
        // ==========================================
        JPanel pnlMainArea = new JPanel(new BorderLayout());
        pnlMainArea.setBackground(bgDark);

        JPanel pnlTopBar = new JPanel(new BorderLayout());
        pnlTopBar.setBackground(bgDark);
        pnlTopBar.setBorder(new EmptyBorder(15, 25, 15, 25));
        
        String chucVu = isAdmin ? "Quản lý" : "Nhân viên";
        String tenNV = userLogin != null ? userLogin.getHoTen() : "Khách";
        
        JLabel lblGreeting = new JLabel("Xin chào, " + tenNV + " | Vai trò: " + chucVu);
        lblGreeting.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblGreeting.setForeground(textWhite);
        pnlTopBar.add(lblGreeting, BorderLayout.EAST);
        
        pnlMainArea.add(pnlTopBar, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        pnlCards = new JPanel(cardLayout);
        pnlCards.setBackground(bgDark);

        JPanel pnlHome = createTrangChuPanel();
        PNL_Phim pnlPhim = new PNL_Phim();
        pnlSuatChieu = new PNL_SuatChieu(this);
        pnlBanVe = new PNL_BanVe(this);
        
        pnlCards.add(pnlHome, "TrangChu");
        pnlCards.add(pnlPhim, "Phim");
        pnlCards.add(pnlSuatChieu, "SuatChieu");
        pnlCards.add(new PNL_NhanVien(), "NhanVien");
        pnlCards.add(new PNL_KhachHang(), "KhachHang");
        pnlCards.add(pnlBanVe, "BanVe");
        pnlCards.add(new PNL_ThongKe(), "ThongKe");
        
        pnlMainArea.add(pnlCards, BorderLayout.CENTER);
        add(pnlMainArea, BorderLayout.CENTER);

        phanQuyen();
    }

    // ==========================================================
    // HÀM TẠO NỘI DUNG TRANG CHỦ (DASHBOARD)
    // ==========================================================
    private JPanel createTrangChuPanel() {
        JPanel pnlContent = new JPanel(new BorderLayout(20, 20));
        pnlContent.setBackground(bgDark);
        pnlContent.setBorder(new EmptyBorder(10, 25, 25, 25));

        JPanel pnlStats = new JPanel(new GridLayout(1, 3, 20, 0));
        pnlStats.setBackground(bgDark);
        pnlStats.setPreferredSize(new Dimension(0, 100));

        pnlStats.add(createStatCard("Doanh thu hôm nay", "12.500.000 đ", new Color(46, 204, 113)));
        pnlStats.add(createStatCard("Vé đã bán", "156 Vé", new Color(52, 152, 219)));
        pnlStats.add(createStatCard("Khách hàng mới", "+24 Người", colorGold));

        pnlContent.add(pnlStats, BorderLayout.NORTH);

        JPanel pnlBottom = new JPanel(new BorderLayout(5, 15));
        pnlBottom.setBackground(bgDark);

        JLabel lblTitlePhim = new JLabel("Phim đang chiếu (Now Showing)");
        lblTitlePhim.setForeground(textWhite);
        lblTitlePhim.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlBottom.add(lblTitlePhim, BorderLayout.NORTH);

        JPanel pnlMovieList = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        pnlMovieList.setBackground(bgDark);

        pnlMovieList.add(createMovieCard("images/phiphong.jpg", "Phí phông: Quỷ máu rừng thiên", "Kinh dị, Tâm linh"));
        pnlMovieList.add(createMovieCard("images/mai.jpg", "Mai", "Tâm lý, Tình cảm"));
        pnlMovieList.add(createMovieCard("images/datrungphuongnam.jpg", "Đất Rừng Phương Nam", "Lịch sử, Hành động"));
        pnlMovieList.add(createMovieCard("images/springjourney.jpg", "Bố Già", "Tâm lý, Gia đình"));
        pnlMovieList.add(createMovieCard("images/thejunglebook.jpg", "Avatar 2: Dòng Chảy", "Khoa học viễn tưởng"));
        pnlMovieList.add(createMovieCard("images/trumso.jpg", "Trùm sò", "Hài hước, Hành động"));

        JScrollPane scrollPane = new JScrollPane(pnlMovieList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(bgDark);
        scrollPane.getViewport().setBackground(bgDark);
        
        pnlBottom.add(scrollPane, BorderLayout.CENTER);
        
        JPanel centerWrap = new JPanel(new BorderLayout(20, 0));
        centerWrap.setBackground(bgDark);
        centerWrap.add(pnlBottom, BorderLayout.CENTER);
        centerWrap.add(createTopPhimPanel(), BorderLayout.EAST);

        pnlContent.add(centerWrap, BorderLayout.CENTER);

        return pnlContent;
    }
    
    private JPanel createTopPhimPanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(bgDark);
        pnl.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Top phim hôm nay");
        title.setForeground(colorGold);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pnl.add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(bgDark);

        PhimDAO dao = new PhimDAO();
        java.util.List<TopPhim> list = dao.getTop3PhimHomNay();

        if (list == null || list.isEmpty()) {
            JLabel empty = new JLabel("Chưa có dữ liệu hôm nay");
            empty.setForeground(textGray);
            listPanel.add(empty);
        } else {
            int rank = 1;
            for (TopPhim tp : list) {
                JPanel item = new JPanel(new BorderLayout());
                item.setPreferredSize(new Dimension(220, 55));
                item.setMaximumSize(new Dimension(220, 55));
                item.setBorder(new EmptyBorder(8, 10, 8, 10));

                Color bgItem;
                Color rankColor;

                if (rank == 1) {
                    bgItem = new Color(60, 50, 20);
                    rankColor = new Color(255, 215, 0); 
                } else if (rank == 2) {
                    bgItem = new Color(45, 45, 45);
                    rankColor = new Color(180, 180, 180);
                } else {
                    bgItem = new Color(35, 35, 35);
                    rankColor = new Color(150, 150, 150);
                }

                item.setBackground(bgItem);

                JLabel lblRank = new JLabel("#" + rank + " ");
                lblRank.setForeground(rankColor);
                lblRank.setFont(new Font("Segoe UI", Font.BOLD, 16));

                JLabel lblName = new JLabel(tp.getTenPhim());
                lblName.setForeground(textWhite);
                lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));

                JLabel lblSold = new JLabel(tp.getSoVeBan() + " vé");
                lblSold.setForeground(colorGold);
                lblSold.setFont(new Font("Segoe UI", Font.BOLD, 12));

                item.add(lblRank, BorderLayout.WEST);
                item.add(lblName, BorderLayout.CENTER);
                item.add(lblSold, BorderLayout.EAST);

                item.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) { item.setBackground(new Color(70, 70, 70)); }
                    public void mouseExited(java.awt.event.MouseEvent e) { item.setBackground(bgItem); }
                });

                listPanel.add(Box.createVerticalStrut(8));
                listPanel.add(item);
                rank++;
            }
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(bgDark);
        pnl.add(scroll, BorderLayout.CENTER);

        return pnl;
    }
    
    private JPanel createStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(30, 30, 30));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor), 
            new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(textGray);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel lblValue = new JLabel(value);
        lblValue.setForeground(textWhite);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createMovieCard(String imagePath, String title, String desc) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(35, 35, 35));
        card.setPreferredSize(new Dimension(220, 330)); 
        card.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblImg = new JLabel();
        lblImg.setHorizontalAlignment(JLabel.CENTER);
        ImageIcon icon = scaleImage(imagePath, 200, 250);
        if(icon != null) lblImg.setIcon(icon);
        else lblImg.setText("No Image");
        lblImg.setForeground(Color.WHITE);

        JLabel lblTitle = new JLabel(title, JLabel.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setBorder(new EmptyBorder(10, 0, 2, 0));

        JLabel lblDesc = new JLabel(desc, JLabel.CENTER);
        lblDesc.setForeground(colorGold);
        lblDesc.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        JPanel pnlText = new JPanel(new BorderLayout());
        pnlText.setBackground(new Color(35, 35, 35));
        pnlText.add(lblTitle, BorderLayout.NORTH);
        pnlText.add(lblDesc, BorderLayout.SOUTH);

        card.add(lblImg, BorderLayout.CENTER);
        card.add(pnlText, BorderLayout.SOUTH);
        
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                chuyenTrangSuatChieuTheoPhim(title);
            }
            public void mouseEntered(MouseEvent e) { card.setBackground(new Color(50, 50, 50)); }
            public void mouseExited(MouseEvent e) { card.setBackground(new Color(35, 35, 35)); }
        });
        
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
    
    // --- PHÂN QUYỀN ĐÃ ĐƯỢC CHỈNH SỬA ---
    private void phanQuyen() {
         if (!isAdmin) {
             btnNhanVien.setEnabled(false);
             btnNhanVien.setToolTipText("Chỉ Quản lý mới có quyền truy cập");
             btnPhim.setEnabled(false);
             btnPhim.setToolTipText("Chỉ Quản lý mới có quyền truy cập");
             btnSuatChieu.setEnabled(false);
             btnSuatChieu.setToolTipText("Chỉ Quản lý mới có quyền truy cập");
         }
    }
    
    // --- XỬ LÝ SỰ KIỆN CHUYỂN TRANG ---
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == btnTrangChu) {
            cardLayout.show(pnlCards, "TrangChu");
        } else if (source == btnPhim) {
            if (!isAdmin) {
                JOptionPane.showMessageDialog(this, "Nhân viên không có quyền quản lý Phim!");
                return;
            }
            cardLayout.show(pnlCards, "Phim");
        } else if (source == btnSuatChieu) {
            if (!isAdmin) {
                JOptionPane.showMessageDialog(this, "Nhân viên không có quyền quản lý Suất chiếu!");
                return;
            }
            cardLayout.show(pnlCards, "SuatChieu");
        } else if (source == btnNhanVien) {
            if (!isAdmin) {
                JOptionPane.showMessageDialog(this, "Chỉ Quản lý mới có quyền truy cập Nhân viên!");
                return;
            }
            cardLayout.show(pnlCards, "NhanVien");
        } else if (source == btnKhachHang) {
            Component[] comps = pnlCards.getComponents();
            for (Component c : comps) {
                if (c instanceof PNL_KhachHang) {
                    ((PNL_KhachHang) c).loadDataToTable(); 
                }
            }
            cardLayout.show(pnlCards, "KhachHang");
        } else if (source == btnHoaDon) {
            Component[] comps = pnlCards.getComponents();
            for (Component c : comps) {
                if (c instanceof PNL_HoaDon) {
                    ((PNL_HoaDon) c).loadDataHoaDon(); 
                }
            }
            cardLayout.show(pnlCards, "HoaDon");
        } else if (source == btnBanVe) {
            cardLayout.show(pnlCards, "BanVe");
        } else if (source == btnThongKe) {
            Component[] comps = pnlCards.getComponents();
            for (Component c : comps) {
                if (c instanceof PNL_ThongKe) {
                    ((PNL_ThongKe) c).loadData(); 
                }
            }
            cardLayout.show(pnlCards, "ThongKe");
        } else if (source == btnDangXuat) {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new UI_DangNhap().setVisible(true);
                this.dispose();
            }
        }
    }
    
    public void chuyenTrangSuatChieuTheoPhim(String tenPhim) {
        pnlSuatChieu.locTheoPhim(tenPhim); 
        cardLayout.show(pnlCards, "SuatChieu");
    }

    public void chuyenTrangBanVeTheoSuat(String tenPhim, String ngayChieu, String maSuat) {
        pnlBanVe.tuDongChonSuat(tenPhim, ngayChieu, maSuat); 
        cardLayout.show(pnlCards, "BanVe");
    }

    public void capNhatSoLuongSuatChieu() {
        pnlSuatChieu.loadDataToTable(); 
    }
}