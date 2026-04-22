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
import javax.swing.border.TitledBorder;

public class PNL_BanVe extends JPanel implements ActionListener {
    private JComboBox<String> cboSuatChieu;
    private JPanel pnlSeatMap;
    private JTextArea txtHoaDon;
    private JButton btnThanhToan, btnHuy;
    private JLabel lblTongTien;
    
    // Lưu trữ các nút ghế và ghế đang chọn
    private ArrayList<JToggleButton> listGhế = new ArrayList<>();
    private ArrayList<String> gheDangChon = new ArrayList<>();
    private double giaVeHienTai = 80000; // Giả sử giá vé mặc định là 80k
    
    // Gọi DAO để xử lý lưu Database
    private dao.HoaDonDAO hoaDonDAO = new dao.HoaDonDAO();
    
    public PNL_BanVe() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(30, 30, 30));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // =========================================
        // 1. PHẦN TRÊN: CHỌN SUẤT CHIẾU
        // =========================================
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTop.setOpaque(false);
        JLabel lblChonSuat = new JLabel("Chọn Suất Chiếu: ");
        lblChonSuat.setForeground(Color.WHITE);
        lblChonSuat.setFont(new Font("Arial", Font.BOLD, 16));
        
        cboSuatChieu = new JComboBox<>();
        cboSuatChieu.setFont(new Font("Arial", Font.PLAIN, 14));
        cboSuatChieu.setPreferredSize(new Dimension(300, 30));
        
        // Tạm thời nạp dữ liệu cứng để test
        cboSuatChieu.addItem("S001 - Mai - 19:00 Hôm nay");
        cboSuatChieu.addItem("S002 - Avatar 2 - 20:30 Hôm nay");
        
        pnlTop.add(lblChonSuat);
        pnlTop.add(cboSuatChieu);
        add(pnlTop, BorderLayout.NORTH);

        // =========================================
        // 2. PHẦN TRUNG TÂM: SƠ ĐỒ GHẾ NGỒI
        // =========================================
        JPanel pnlCenter = new JPanel(new BorderLayout(0, 15));
        pnlCenter.setOpaque(false);
        
        // Màn hình rạp phim
        JLabel lblScreen = new JLabel("MÀN HÌNH CHÍNH", JLabel.CENTER);
        lblScreen.setOpaque(true);
        lblScreen.setBackground(new Color(200, 200, 200));
        lblScreen.setForeground(Color.BLACK);
        lblScreen.setFont(new Font("Arial", Font.BOLD, 16));
        lblScreen.setPreferredSize(new Dimension(0, 40));
        pnlCenter.add(lblScreen, BorderLayout.NORTH);

        // Lưới chứa ghế (6 hàng x 8 cột)
        pnlSeatMap = new JPanel(new GridLayout(6, 8, 10, 10));
        pnlSeatMap.setOpaque(false);
        pnlSeatMap.setBorder(new EmptyBorder(20, 50, 20, 50));
        
        taoSoDoGhe(); // Gọi hàm sinh ghế
        
        pnlCenter.add(pnlSeatMap, BorderLayout.CENTER);
        
        // Chú thích màu sắc ghế
        JPanel pnlLegend = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        pnlLegend.setOpaque(false);
        pnlLegend.add(createLegendColor(new Color(60, 60, 60), "Ghế Trống"));
        pnlLegend.add(createLegendColor(new Color(46, 204, 113), "Đang Chọn"));
        pnlLegend.add(createLegendColor(new Color(231, 76, 60), "Đã Bán"));
        pnlCenter.add(pnlLegend, BorderLayout.SOUTH);

        add(pnlCenter, BorderLayout.CENTER);

        // =========================================
        // 3. PHẦN BÊN PHẢI: KHUNG HÓA ĐƠN
        // =========================================
        JPanel pnlRight = new JPanel(new BorderLayout(10, 10));
        pnlRight.setOpaque(false);
        pnlRight.setPreferredSize(new Dimension(300, 0));
        
        TitledBorder billBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "THÔNG TIN HÓA ĐƠN");
        billBorder.setTitleColor(Color.ORANGE);
        billBorder.setTitleFont(new Font("Arial", Font.BOLD, 14));
        pnlRight.setBorder(billBorder);

        txtHoaDon = new JTextArea();
        txtHoaDon.setEditable(false);
        txtHoaDon.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtHoaDon.setBackground(new Color(40, 40, 40));
        txtHoaDon.setForeground(Color.WHITE);
        pnlRight.add(new JScrollPane(txtHoaDon), BorderLayout.CENTER);

        // Khung chốt tiền và nút thanh toán
        JPanel pnlCheckout = new JPanel(new GridLayout(2, 1, 5, 10));
        pnlCheckout.setOpaque(false);
        
        lblTongTien = new JLabel("TỔNG TIỀN: 0 VNĐ", JLabel.RIGHT);
        lblTongTien.setForeground(Color.RED);
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 18));
        pnlCheckout.add(lblTongTien);

        JPanel pnlActionBtns = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlActionBtns.setOpaque(false);
        btnHuy = new JButton("Hủy Bỏ");
        btnHuy.setBackground(Color.GRAY);
        btnHuy.setForeground(Color.WHITE);
        
        btnThanhToan = new JButton("THANH TOÁN");
        btnThanhToan.setBackground(new Color(52, 152, 219));
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.setFont(new Font("Arial", Font.BOLD, 14));
        
        pnlActionBtns.add(btnHuy);
        pnlActionBtns.add(btnThanhToan);
        pnlCheckout.add(pnlActionBtns);

        pnlRight.add(pnlCheckout, BorderLayout.SOUTH);
        add(pnlRight, BorderLayout.EAST);

        // Đăng ký sự kiện
        btnThanhToan.addActionListener(this);
        btnHuy.addActionListener(this);
        
        // [ĐÃ SỬA]: Gọi loadGheTheoSuat() khi đổi combobox
        cboSuatChieu.addActionListener(e -> loadGheTheoSuat()); 
        
        // [ĐÃ THÊM]: Load dữ liệu ghế cho suất đầu tiên ngay khi mở Form
        loadGheTheoSuat();
    }

    // --- Hàm sinh tự động 48 ghế (A1 đến F8) ---
    private void taoSoDoGhe() {
        String[] hang = {"A", "B", "C", "D", "E", "F"};
        for (int i = 0; i < hang.length; i++) {
            for (int j = 1; j <= 8; j++) {
                String tenGhe = hang[i] + j;
                JToggleButton btnGhe = new JToggleButton(tenGhe);
                btnGhe.setFont(new Font("Arial", Font.BOLD, 12));
                btnGhe.setBackground(new Color(60, 60, 60)); 
                btnGhe.setForeground(Color.WHITE);
                btnGhe.setFocusPainted(false);
                
                // Sự kiện khi bấm vào 1 ghế
                btnGhe.addActionListener(e -> {
                    if (btnGhe.isSelected()) {
                        btnGhe.setBackground(new Color(46, 204, 113)); 
                        gheDangChon.add(tenGhe);
                    } else {
                        btnGhe.setBackground(new Color(60, 60, 60)); 
                        gheDangChon.remove(tenGhe);
                    }
                    capNhatHoaDon();
                });
                
                listGhế.add(btnGhe);
                pnlSeatMap.add(btnGhe);
            }
        }
    }

    // --- Hàm cập nhật nội dung Hóa Đơn bên phải ---
    private void capNhatHoaDon() {
        if (gheDangChon.isEmpty()) {
            txtHoaDon.setText("");
            lblTongTien.setText("TỔNG TIỀN: 0 VNĐ");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(" SUẤT CHIẾU:\n ").append(cboSuatChieu.getSelectedItem()).append("\n");
        sb.append("----------------------------\n");
        sb.append(" GHẾ CHỌN:\n");
        
        double tongTien = 0;
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        
        for (String ghe : gheDangChon) {
            sb.append(" - Ghế ").append(ghe).append("\t: ").append(nf.format(giaVeHienTai)).append(" đ\n");
            tongTien += giaVeHienTai;
        }
        
        sb.append("----------------------------\n");
        sb.append(" SỐ LƯỢNG VÉ: ").append(gheDangChon.size()).append("\n");
        
        txtHoaDon.setText(sb.toString());
        lblTongTien.setText("TỔNG TIỀN: " + nf.format(tongTien) + " VNĐ");
    }

    // --- Hàm tải trạng thái ghế thực tế từ Database ---
    private void loadGheTheoSuat() {
        // 1. Reset toàn bộ ghế về trạng thái trống
        for (JToggleButton btn : listGhế) {
            btn.setSelected(false);
            btn.setEnabled(true);
            btn.setBackground(new Color(60, 60, 60));
        }
        gheDangChon.clear();
        capNhatHoaDon();

        // 2. Lấy mã suất chiếu đang chọn ở ComboBox
        if (cboSuatChieu.getSelectedItem() == null) return;
        String suatDangChon = cboSuatChieu.getSelectedItem().toString();
        String maSuat = suatDangChon.split(" - ")[0]; 

        // 3. Gọi DAO để lấy danh sách ghế ĐÃ BÁN của suất này
        ArrayList<String> gheDaBan = hoaDonDAO.layDanhSachGheDaBan(maSuat);

        // 4. Duyệt qua sơ đồ ghế, ghế nào Đã Bán thì tô Đỏ & Khóa lại
        for (JToggleButton btn : listGhế) {
            if (gheDaBan.contains(btn.getText())) {
                btn.setBackground(new Color(231, 76, 60)); // Màu đỏ
                btn.setEnabled(false); // Khóa nút
            }
        }
    }

    // Hàm hỗ trợ tạo chú thích màu
    private JPanel createLegendColor(Color c, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setOpaque(false);
        JLabel lblColor = new JLabel("   ");
        lblColor.setOpaque(true);
        lblColor.setBackground(c);
        JLabel lblText = new JLabel(text);
        lblText.setForeground(Color.WHITE);
        p.add(lblColor);
        p.add(lblText);
        return p;
    }

    // --- XỬ LÝ SỰ KIỆN NÚT BẤM ---
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnHuy) {
            loadGheTheoSuat(); // [ĐÃ SỬA]: Xóa dư dấu ;
        } else if (e.getSource() == btnThanhToan) {
            if (gheDangChon.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất 1 ghế!");
                return;
            }
            
            // 1. Tạo mã Hóa Đơn tự động
            String maHD = "HD" + new SimpleDateFormat("HHmmss").format(new Date());
            
            // 2. Lấy mã suất chiếu đang được chọn
            String suatDangChon = cboSuatChieu.getSelectedItem().toString();
            String maSuat = suatDangChon.split(" - ")[0]; 
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Xác nhận thanh toán cho " + gheDangChon.size() + " vé?\nTổng tiền: " + lblTongTien.getText(), 
                "Xác nhận Hóa Đơn", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                
                // 3. Gọi DAO lưu xuống Database
                boolean isSuccess = hoaDonDAO.thanhToanHoaDon(maHD, maSuat, gheDangChon, giaVeHienTai);
                
                if (isSuccess) {
                    JOptionPane.showMessageDialog(this, "Thanh toán thành công!\nMã Hóa Đơn: " + maHD);
                    
                    // Sau khi thanh toán thành công, load lại màu ghế thực tế từ DB
                    loadGheTheoSuat();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL, thanh toán thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}