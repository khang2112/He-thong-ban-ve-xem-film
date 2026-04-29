package ui;

import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class PNL_BanVe extends JPanel implements ActionListener {
    private JComboBox<String> cboSuatChieu;
    private JPanel pnlSeatMap;
    
    // Giỏ hàng chuyên nghiệp dùng JTable thay vì JTextArea
    private DefaultTableModel cartModel;
    private JTable tblCart;
    
    private JButton btnThanhToan, btnHuy;
    private JLabel lblTongTien, lblSoLuong;
    private JTextField txtMaKH;
    
    private ArrayList<JToggleButton> listGhế = new ArrayList<>();
    private ArrayList<String> gheDangChon = new ArrayList<>();
    private double giaVeHienTai = 80000; 
    
    private dao.HoaDonDAO hoaDonDAO = new dao.HoaDonDAO();
    private dao.KhachHangDAO khachHangDAO = new dao.KhachHangDAO(); // <-- THÊM DÒNG NÀY
    
    public PNL_BanVe() {
        setLayout(new BorderLayout(20, 20)); // Tăng khoảng cách các khối
        setBackground(new Color(25, 25, 25)); // Nền đen sâu hơn
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // =========================================
        // 1. TOP BAR: CHỌN SUẤT CHIẾU & KHÁCH HÀNG
        // =========================================
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel pnlTopLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlTopLeft.setOpaque(false);
        
        JLabel lblChonSuat = new JLabel("SUẤT CHIẾU:");
        lblChonSuat.setForeground(new Color(150, 150, 150));
        lblChonSuat.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        cboSuatChieu = new JComboBox<>();
        cboSuatChieu.setFont(new Font("Segoe UI", Font.BOLD, 15));
        cboSuatChieu.setPreferredSize(new Dimension(350, 40));
        cboSuatChieu.setBackground(new Color(40, 40, 40));
        cboSuatChieu.setForeground(Color.WHITE);
        cboSuatChieu.addItem("S001 - Mai (2D) - 19:00 Hôm nay");
        cboSuatChieu.addItem("S002 - Avatar 2 (3D) - 20:30 Hôm nay");
        
        pnlTopLeft.add(lblChonSuat);
        pnlTopLeft.add(cboSuatChieu);

        JPanel pnlTopRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlTopRight.setOpaque(false);
        
        JLabel lblKhachHang = new JLabel("MÃ THẺ KHÁCH HÀNG:");
        lblKhachHang.setForeground(new Color(150, 150, 150));
        lblKhachHang.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        txtMaKH = new JTextField();
        txtMaKH.setPreferredSize(new Dimension(150, 40));
        txtMaKH.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtMaKH.setBackground(new Color(40, 40, 40));
        txtMaKH.setForeground(new Color(241, 196, 15)); // Màu vàng cho mã thẻ
        txtMaKH.setCaretColor(Color.WHITE);
        txtMaKH.setHorizontalAlignment(JTextField.CENTER);
        txtMaKH.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(70, 70, 70), 1),
            new EmptyBorder(5, 5, 5, 5)
        ));

        pnlTopRight.add(lblKhachHang);
        pnlTopRight.add(txtMaKH);

        pnlTop.add(pnlTopLeft, BorderLayout.WEST);
        pnlTop.add(pnlTopRight, BorderLayout.EAST);
        add(pnlTop, BorderLayout.NORTH);

        // =========================================
        // 2. CENTER: SƠ ĐỒ GHẾ CÓ HÀNH LANG LỐI ĐI
        // =========================================
        JPanel pnlCenter = new JPanel(new BorderLayout(0, 20));
        pnlCenter.setOpaque(false);
        
        // Màn hình rạp phim uốn cong giả lập
        JLabel lblScreen = new JLabel("M À N   H Ì N H", JLabel.CENTER);
        lblScreen.setOpaque(true);
        lblScreen.setBackground(new Color(52, 152, 219)); // Sáng lên trông như màn hình đang chiếu
        lblScreen.setForeground(Color.WHITE);
        lblScreen.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblScreen.setPreferredSize(new Dimension(0, 50));
        pnlCenter.add(lblScreen, BorderLayout.NORTH);

        // Lưới ghế: 6 hàng x 10 cột (8 ghế + 2 lối đi trống)
        pnlSeatMap = new JPanel(new GridLayout(6, 10, 12, 12));
        pnlSeatMap.setOpaque(false);
        pnlSeatMap.setBorder(new EmptyBorder(10, 40, 20, 40));
        
        taoSoDoGheChuyenNghiep(); 
        
        pnlCenter.add(pnlSeatMap, BorderLayout.CENTER);
        
        // Chú thích
        JPanel pnlLegend = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        pnlLegend.setOpaque(false);
        pnlLegend.add(createLegend(new Color(60, 60, 60), "Ghế Trống"));
        pnlLegend.add(createLegend(new Color(46, 204, 113), "Đang Chọn"));
        pnlLegend.add(createLegend(new Color(231, 76, 60), "Đã Bán"));
        pnlCenter.add(pnlLegend, BorderLayout.SOUTH);

        add(pnlCenter, BorderLayout.CENTER);

        // =========================================
        // 3. RIGHT: GIỎ HÀNG & THANH TOÁN
        // =========================================
        JPanel pnlRight = new JPanel(new BorderLayout(0, 10));
        pnlRight.setOpaque(false);
        pnlRight.setPreferredSize(new Dimension(380, 0)); // Rộng hơn một chút
        pnlRight.setBorder(new EmptyBorder(0, 15, 0, 0)); // Cách lề trái

        // Bảng Giỏ hàng (Cart)
        String[] cartCols = {"Loại", "Ghế", "Thành tiền"};
        cartModel = new DefaultTableModel(cartCols, 0);
        tblCart = new JTable(cartModel);
        tblCart.setRowHeight(35);
        tblCart.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblCart.setBackground(new Color(40, 40, 40));
        tblCart.setForeground(Color.WHITE);
        tblCart.setShowGrid(false); // Bỏ lưới cho hiện đại
        
        // Tiêu đề giỏ hàng
        tblCart.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblCart.getTableHeader().setBackground(new Color(30, 30, 30));
        tblCart.getTableHeader().setForeground(new Color(150, 150, 150));
        tblCart.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        // Căn phải cho cột tiền
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblCart.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);

        JScrollPane scrollCart = new JScrollPane(tblCart);
        scrollCart.getViewport().setBackground(new Color(40, 40, 40));
        scrollCart.setBorder(new LineBorder(new Color(60, 60, 60), 1));
        
        JPanel pnlCartWrapper = new JPanel(new BorderLayout());
        pnlCartWrapper.setOpaque(false);
        JLabel lblCartTitle = new JLabel("CHI TIẾT VÉ CHỌN");
        lblCartTitle.setForeground(Color.WHITE);
        lblCartTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCartTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        pnlCartWrapper.add(lblCartTitle, BorderLayout.NORTH);
        pnlCartWrapper.add(scrollCart, BorderLayout.CENTER);
        
        pnlRight.add(pnlCartWrapper, BorderLayout.CENTER);

        // Khung chốt tiền (Checkout Box)
        JPanel pnlCheckout = new JPanel(new BorderLayout(0, 15));
        pnlCheckout.setBackground(new Color(40, 40, 40));
        pnlCheckout.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(60, 60, 60), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JPanel pnlTotalInfo = new JPanel(new GridLayout(2, 1, 0, 5));
        pnlTotalInfo.setOpaque(false);
        
        lblSoLuong = new JLabel("Số lượng vé: 0", JLabel.LEFT);
        lblSoLuong.setForeground(new Color(200, 200, 200));
        lblSoLuong.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        
        lblTongTien = new JLabel("0 đ", JLabel.RIGHT);
        lblTongTien.setForeground(new Color(46, 204, 113)); // Xanh lá nổi bật tổng tiền
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 32));
        
        pnlTotalInfo.add(lblSoLuong);
        pnlTotalInfo.add(lblTongTien);
        
        pnlCheckout.add(pnlTotalInfo, BorderLayout.NORTH);

        JPanel pnlActionBtns = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlActionBtns.setOpaque(false);
        
        btnHuy = new JButton("HỦY BỎ");
        btnHuy.setBackground(new Color(80, 80, 80));
        btnHuy.setForeground(Color.WHITE);
        btnHuy.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnHuy.setFocusPainted(false);
        btnHuy.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        btnThanhToan = new JButton("THANH TOÁN");
        btnThanhToan.setBackground(new Color(52, 152, 219));
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnThanhToan.setFocusPainted(false);
        btnThanhToan.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        pnlActionBtns.add(btnHuy);
        pnlActionBtns.add(btnThanhToan);
        pnlCheckout.add(pnlActionBtns, BorderLayout.SOUTH);

        pnlRight.add(pnlCheckout, BorderLayout.SOUTH);
        add(pnlRight, BorderLayout.EAST);

        // Events
        btnThanhToan.addActionListener(this);
        btnHuy.addActionListener(this);
        cboSuatChieu.addActionListener(e -> loadGheTheoSuat()); 
        
        loadGheTheoSuat();
    }

    // --- Hàm sinh sơ đồ ghế mô phỏng rạp thật ---
    private void taoSoDoGheChuyenNghiep() {
        String[] hang = {"A", "B", "C", "D", "E", "F"};
        
        for (int i = 0; i < hang.length; i++) {
            int gheSo = 1;
            // 10 cột để chứa cả lối đi (Cột index 2 và 7 là lối đi)
            for (int col = 0; col < 10; col++) {
                if (col == 2 || col == 7) {
                    // Tạo một JLabel rỗng làm hành lang lối đi
                    pnlSeatMap.add(new JLabel("")); 
                } else {
                    String tenGhe = hang[i] + gheSo;
                    JToggleButton btnGhe = new JToggleButton(tenGhe);
                    btnGhe.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    btnGhe.setBackground(new Color(70, 70, 70)); 
                    btnGhe.setForeground(Color.WHITE);
                    btnGhe.setFocusPainted(false);
                    btnGhe.setBorder(BorderFactory.createLineBorder(new Color(40, 40, 40), 2));
                    
                    // Style khi hover hoặc chọn
                    btnGhe.addActionListener(e -> {
                        if (btnGhe.isSelected()) {
                            btnGhe.setBackground(new Color(46, 204, 113)); 
                            gheDangChon.add(tenGhe);
                        } else {
                            btnGhe.setBackground(new Color(70, 70, 70)); 
                            gheDangChon.remove(tenGhe);
                        }
                        capNhatGioHang();
                    });
                    
                    listGhế.add(btnGhe);
                    pnlSeatMap.add(btnGhe);
                    gheSo++;
                }
            }
        }
    }

    // --- Cập nhật Bảng Giỏ Hàng (Cart) ---
    private void capNhatGioHang() {
        cartModel.setRowCount(0); // Xóa bảng cũ
        double tongTien = 0;
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        
        for (String ghe : gheDangChon) {
            // Giả lập logic: Ghế hàng F là ghế VIP đắt hơn
            String loaiGhe = ghe.startsWith("F") ? "VIP" : "Thường";
            double giaGheNay = ghe.startsWith("F") ? 120000 : giaVeHienTai;
            
            cartModel.addRow(new Object[]{loaiGhe, ghe, nf.format(giaGheNay)});
            tongTien += giaGheNay;
        }
        
        lblSoLuong.setText("Số lượng vé: " + gheDangChon.size());
        lblTongTien.setText(nf.format(tongTien) + " đ");
    }

    private void loadGheTheoSuat() {
        for (JToggleButton btn : listGhế) {
            btn.setSelected(false);
            btn.setEnabled(true);
            btn.setBackground(new Color(70, 70, 70));
        }
        gheDangChon.clear();
        capNhatGioHang();

        if (cboSuatChieu.getSelectedItem() == null) return;
        String suatDangChon = cboSuatChieu.getSelectedItem().toString();
        String maSuat = suatDangChon.split(" - ")[0]; 

        ArrayList<String> gheDaBan = hoaDonDAO.layDanhSachGheDaBan(maSuat);

        for (JToggleButton btn : listGhế) {
            if (gheDaBan.contains(btn.getText())) {
                btn.setBackground(new Color(231, 76, 60)); // Đỏ
                btn.setEnabled(false);
            }
        }
    }

    private JPanel createLegend(Color c, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setOpaque(false);
        JLabel lblColor = new JLabel();
        lblColor.setPreferredSize(new Dimension(20, 20));
        lblColor.setOpaque(true);
        lblColor.setBackground(c);
        lblColor.setBorder(new LineBorder(Color.DARK_GRAY, 1));
        
        JLabel lblText = new JLabel(text);
        lblText.setForeground(Color.LIGHT_GRAY);
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        p.add(lblColor);
        p.add(lblText);
        return p;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnHuy) {
            loadGheTheoSuat(); 
            txtMaKH.setText(""); 
        } else if (e.getSource() == btnThanhToan) {
            if (gheDangChon.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất 1 ghế!");
                return;
            }
            
            String maHD = "HD" + new SimpleDateFormat("HHmmss").format(new Date());
            String maSuat = cboSuatChieu.getSelectedItem().toString().split(" - ")[0]; 
            String maKH = txtMaKH.getText().trim();
            
            // ========================================================
            // CHỐT CHẶN: KIỂM TRA RÀNG BUỘC KHÁCH HÀNG
            // ========================================================
            if (!maKH.isEmpty()) {
                // Nếu có nhập mã KH -> Bắt buộc phải kiểm tra xem có tồn tại không
                if (!khachHangDAO.kiemTraTonTai(maKH)) {
                    JOptionPane.showMessageDialog(this, 
                        "Mã khách hàng '" + maKH + "' không tồn tại trong hệ thống!\n" +
                        "Vui lòng sang mục Khách Hàng để tạo thẻ mới, hoặc xóa trống ô này nếu là khách vãng lai.", 
                        "Lỗi Khách Hàng", JOptionPane.ERROR_MESSAGE);
                    
                    txtMaKH.requestFocus();
                    txtMaKH.selectAll(); // Bôi đen chữ bị sai cho thu ngân dễ xóa
                    return; // Dừng lại ngay lập tức, không cho hiện hộp thoại thanh toán
                }
            }
            // ========================================================
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Xác nhận thanh toán cho " + gheDangChon.size() + " vé?\nTổng tiền: " + lblTongTien.getText(), 
                "XÁC NHẬN THANH TOÁN", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                boolean isSuccess = hoaDonDAO.thanhToanHoaDon(maHD, maSuat, gheDangChon, giaVeHienTai, maKH);
                
                if (isSuccess) {
                    String loiChuc = "Thanh toán thành công!\nMã Hóa Đơn: " + maHD;
                    if (!maKH.isEmpty()) {
                        int diemThuong = (int) ((gheDangChon.size() * giaVeHienTai) / 10000);
                        loiChuc += "\n🎉 Đã tích lũy " + diemThuong + " điểm cho khách hàng " + maKH;
                    }
                    JOptionPane.showMessageDialog(this, loiChuc, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    
                    txtMaKH.setText("");
                    loadGheTheoSuat();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL, thanh toán thất bại!", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}