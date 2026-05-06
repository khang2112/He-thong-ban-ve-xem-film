package ui;

import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import entity.SuatChieu;

public class PNL_BanVe extends JPanel implements ActionListener {
    private JComboBox<String> cboSuatChieu;
    private JPanel pnlSeatMap;
    
    private DefaultTableModel cartModel;
    private JTable tblCart;
    
    private JButton btnThanhToan, btnHuy;
    private JLabel lblTongTien, lblSoLuong;
    private JTextField txtMaKH;
    
    private ArrayList<SeatButton> listGhế = new ArrayList<>();
    private ArrayList<String> gheDangChon = new ArrayList<>();
    private double giaVeHienTai = 80000; 
    
    private dao.HoaDonDAO hoaDonDAO = new dao.HoaDonDAO();
    private dao.KhachHangDAO khachHangDAO = new dao.KhachHangDAO(); 
    private dao.SuatChieuDAO suatChieuDAO = new dao.SuatChieuDAO();

    // --- LỚP TỰ VẼ GHẾ NGỒI ĐỂ TRÁNH BỊ WINDOWS LÀM TRẮNG ---
    class SeatButton extends JToggleButton {
        public SeatButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setContentAreaFilled(false); 
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (!isEnabled()) {
                g2.setColor(new Color(229, 9, 20)); // Ghế đã bán (Đỏ Netflix)
            } else if (isSelected()) {
                g2.setColor(new Color(46, 204, 113)); // Ghế đang chọn (Xanh lá)
            } else {
                g2.setColor(new Color(60, 60, 60)); // Ghế trống (Xám tối)
            }

            g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 15, 15);
            g2.dispose();
            super.paintComponent(g); 
        }
    }

    // --- LỚP NÚT BẤM POS CAO CẤP ---
    class PosButton extends JButton {
        private Color bgColor;
        public PosButton(String text, Color bg, Color fg) {
            super(text);
            this.bgColor = bg;
            setFont(new Font("Segoe UI", Font.BOLD, 15));
            setForeground(fg);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { setBackground(bgColor.brighter()); repaint(); }
                public void mouseExited(MouseEvent e) { setBackground(bgColor); repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground() == null ? bgColor : getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public PNL_BanVe() {
        setLayout(new BorderLayout(20, 20)); 
        setBackground(new Color(18, 18, 18)); 
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
        cboSuatChieu.setPreferredSize(new Dimension(420, 40));
        cboSuatChieu.setUI(new BasicComboBoxUI());
        cboSuatChieu.setBackground(new Color(40, 40, 40));
        cboSuatChieu.setForeground(Color.WHITE);
        
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) { loadSCMoiNhat(); }
        });
        
        pnlTopLeft.add(lblChonSuat);
        pnlTopLeft.add(cboSuatChieu);

        JPanel pnlTopRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlTopRight.setOpaque(false);
        
        JLabel lblKhachHang = new JLabel("MÃ THẺ KHÁCH HÀNG:");
        lblKhachHang.setForeground(new Color(150, 150, 150));
        lblKhachHang.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        txtMaKH = new JTextField();
        txtMaKH.setPreferredSize(new Dimension(150, 40));
        txtMaKH.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtMaKH.setBackground(new Color(40, 40, 40));
        txtMaKH.setForeground(new Color(212, 175, 55)); 
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
        // 2. CENTER: SƠ ĐỒ GHẾ NGỒI CÓ LỐI ĐI
        // =========================================
        JPanel pnlCenter = new JPanel(new BorderLayout(0, 20));
        pnlCenter.setOpaque(false);
        
        JLabel lblScreen = new JLabel("M À N   H Ì N H", JLabel.CENTER);
        lblScreen.setOpaque(true);
        lblScreen.setBackground(new Color(52, 152, 219)); 
        lblScreen.setForeground(Color.WHITE);
        lblScreen.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblScreen.setPreferredSize(new Dimension(0, 40));
        lblScreen.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(41, 128, 185))); 
        pnlCenter.add(lblScreen, BorderLayout.NORTH);

        pnlSeatMap = new JPanel(new GridLayout(6, 10, 12, 12));
        pnlSeatMap.setOpaque(false);
        pnlSeatMap.setBorder(new EmptyBorder(10, 40, 20, 40));
        
        taoSoDoGheChuyenNghiep(); 
        
        pnlCenter.add(pnlSeatMap, BorderLayout.CENTER);
        
        JPanel pnlLegend = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        pnlLegend.setOpaque(false);
        pnlLegend.add(createLegend(new Color(60, 60, 60), "Ghế Trống"));
        pnlLegend.add(createLegend(new Color(46, 204, 113), "Đang Chọn"));
        pnlLegend.add(createLegend(new Color(229, 9, 20), "Đã Bán"));
        pnlCenter.add(pnlLegend, BorderLayout.SOUTH);

        add(pnlCenter, BorderLayout.CENTER);

        // =========================================
        // 3. RIGHT: GIỎ HÀNG & THANH TOÁN
        // =========================================
        JPanel pnlRight = new JPanel(new BorderLayout(0, 15));
        pnlRight.setOpaque(false);
        pnlRight.setPreferredSize(new Dimension(400, 0)); 
        pnlRight.setBorder(new EmptyBorder(0, 20, 0, 0)); 

        String[] cartCols = {"Loại", "Ghế", "Thành tiền"};
        cartModel = new DefaultTableModel(cartCols, 0);
        tblCart = new JTable(cartModel);
        tblCart.setRowHeight(35);
        tblCart.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblCart.setBackground(new Color(30, 30, 30));
        tblCart.setForeground(Color.WHITE);
        tblCart.setShowGrid(false); 
        
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(40, 40, 40));
        headerRenderer.setForeground(new Color(212, 175, 55)); 
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 13));
        for (int i = 0; i < tblCart.getModel().getColumnCount(); i++) {
            tblCart.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblCart.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);

        JScrollPane scrollCart = new JScrollPane(tblCart);
        scrollCart.getViewport().setBackground(new Color(30, 30, 30));
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

        JPanel pnlCheckout = new JPanel(new BorderLayout(0, 15));
        pnlCheckout.setBackground(new Color(35, 35, 35));
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
        lblTongTien.setForeground(new Color(46, 204, 113)); 
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 36));
        
        pnlTotalInfo.add(lblSoLuong);
        pnlTotalInfo.add(lblTongTien);
        
        pnlCheckout.add(pnlTotalInfo, BorderLayout.NORTH);

        JPanel pnlActionBtns = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlActionBtns.setOpaque(false);
        
        btnHuy = new PosButton("HỦY BỎ", new Color(100, 100, 100), Color.WHITE);
        btnThanhToan = new PosButton("THANH TOÁN", new Color(229, 9, 20), Color.WHITE); // Đổi nút thanh toán sang Đỏ Netflix
        
        pnlActionBtns.add(btnHuy);
        pnlActionBtns.add(btnThanhToan);
        pnlCheckout.add(pnlActionBtns, BorderLayout.SOUTH);

        pnlRight.add(pnlCheckout, BorderLayout.SOUTH);
        add(pnlRight, BorderLayout.EAST);

        // Events
        btnThanhToan.addActionListener(this);
        btnHuy.addActionListener(this);
        cboSuatChieu.addActionListener(e -> loadGheTheoSuat()); 
    }

    private void taoSoDoGheChuyenNghiep() {
        String[] hang = {"A", "B", "C", "D", "E", "F"};
        for (int i = 0; i < hang.length; i++) {
            int gheSo = 1;
            for (int col = 0; col < 10; col++) {
                if (col == 2 || col == 7) {
                    pnlSeatMap.add(new JLabel("")); 
                } else {
                    String tenGhe = hang[i] + gheSo;
                    SeatButton btnGhe = new SeatButton(tenGhe); 
                    
                    btnGhe.addActionListener(e -> {
                        if (btnGhe.isSelected()) gheDangChon.add(tenGhe);
                        else gheDangChon.remove(tenGhe);
                        capNhatGioHang();
                    });
                    
                    listGhế.add(btnGhe);
                    pnlSeatMap.add(btnGhe);
                    gheSo++;
                }
            }
        }
    }

    private void capNhatGioHang() {
        cartModel.setRowCount(0); 
        double tongTien = 0;
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        
        for (String ghe : gheDangChon) {
            String loaiGhe = ghe.startsWith("F") ? "VIP" : "Thường";
            double giaGheNay = ghe.startsWith("F") ? 120000 : giaVeHienTai;
            
            cartModel.addRow(new Object[]{loaiGhe, ghe, nf.format(giaGheNay)});
            tongTien += giaGheNay;
        }
        
        lblSoLuong.setText("Số lượng vé: " + gheDangChon.size());
        lblTongTien.setText(nf.format(tongTien) + " đ");
    }

    private void loadGheTheoSuat() {
        for (SeatButton btn : listGhế) {
            btn.setSelected(false);
            btn.setEnabled(true);
        }
        gheDangChon.clear();
        capNhatGioHang();

        if (cboSuatChieu.getSelectedItem() == null) return;
        String suatDangChon = cboSuatChieu.getSelectedItem().toString();
        String maSuat = suatDangChon.split(" - ")[0]; 

        ArrayList<String> gheDaBan = hoaDonDAO.layDanhSachGheDaBan(maSuat);

        for (SeatButton btn : listGhế) {
            if (gheDaBan.contains(btn.getText())) {
                btn.setEnabled(false); 
            }
        }
    }
    
    private void loadSCMoiNhat() {
        ActionListener[] listeners = cboSuatChieu.getActionListeners();
        for (ActionListener l : listeners) cboSuatChieu.removeActionListener(l);
        
        cboSuatChieu.removeAllItems();
        ArrayList<entity.SuatChieu> dsSuat = suatChieuDAO.docTuBang();
        
        DateTimeFormatter dtfNgay = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dtfGio = DateTimeFormatter.ofPattern("HH:mm");
        
        for (entity.SuatChieu s : dsSuat) {
            String hienThi = s.getMaSuat() + " - " + s.getMaPhim() + " - " + s.getPhongChieu() + " - " 
                           + s.getGioChieu().format(dtfGio) + " ngày " + s.getNgayChieu().format(dtfNgay);
            cboSuatChieu.addItem(hienThi);
        }
        
        for (ActionListener l : listeners) cboSuatChieu.addActionListener(l);
        
        if(cboSuatChieu.getItemCount() > 0) {
            cboSuatChieu.setSelectedIndex(0);
            loadGheTheoSuat();
        }
    }

    private JPanel createLegend(Color c, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setOpaque(false);
        JLabel lblColor = new JLabel();
        lblColor.setPreferredSize(new Dimension(18, 18));
        lblColor.setOpaque(true);
        lblColor.setBackground(c);
        lblColor.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
        
        JLabel lblText = new JLabel(text);
        lblText.setForeground(Color.LIGHT_GRAY);
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        p.add(lblColor);
        p.add(lblText);
        return p;
    }

    // =========================================================================
    // HÀM TẠO VÀ IN HÓA ĐƠN (RECEIPT) CHUẨN MÁY IN NHIỆT POS
    // =========================================================================
    private void inHoaDon(String maHD, String suatInfo, String maKH, String tongTienStr) {
        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("                CINEMA POS                \n");
        sb.append("            HÓA ĐƠN THANH TOÁN            \n");
        sb.append("==========================================\n");
        sb.append("Mã HĐ: ").append(maHD).append("\n");
        sb.append("Ngày in: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())).append("\n");
        if (!maKH.isEmpty()) {
            sb.append("Thẻ Khách hàng: ").append(maKH).append("\n");
        }
        sb.append("------------------------------------------\n");
        sb.append("Suất chiếu: \n").append(suatInfo).append("\n");
        sb.append("Vị trí ghế: ").append(String.join(", ", gheDangChon)).append("\n");
        sb.append("Số lượng:   ").append(gheDangChon.size()).append(" vé\n");
        sb.append("------------------------------------------\n");
        sb.append("TỔNG TIỀN:                  ").append(tongTienStr).append("\n");
        sb.append("==========================================\n");
        sb.append("     CẢM ƠN QUÝ KHÁCH VÀ HẸN GẶP LẠI!     \n");
        sb.append("        Wifi: Cinema_Free (Pass: 8888)    \n");

        JTextArea txtBill = new JTextArea(sb.toString());
        txtBill.setFont(new Font("Monospaced", Font.BOLD, 13)); // Dùng font Monospaced để căn lề chuẩn
        txtBill.setEditable(false);
        txtBill.setBackground(Color.WHITE);
        txtBill.setForeground(Color.BLACK);
        txtBill.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(txtBill);
        scroll.setPreferredSize(new Dimension(380, 450));

        // Hiện Dialog giả lập hóa đơn và hỏi in
        Object[] options = {"In Hóa Đơn (Print)", "Đóng lại"};
        int choice = JOptionPane.showOptionDialog(this, scroll, "Chi tiết Hóa Đơn: " + maHD,
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                // Kích hoạt hộp thoại in của Windows
                boolean complete = txtBill.print();
                if (complete) {
                    JOptionPane.showMessageDialog(this, "Đã gửi lệnh in thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Đã hủy lệnh in.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi kết nối máy in: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
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
            String suatInfo = cboSuatChieu.getSelectedItem().toString();
            String maSuat = suatInfo.split(" - ")[0]; 
            String maKH = txtMaKH.getText().trim();
            
            if (!maKH.isEmpty()) {
                if (!khachHangDAO.kiemTraTonTai(maKH)) {
                    JOptionPane.showMessageDialog(this, 
                        "Mã khách hàng '" + maKH + "' không tồn tại trong hệ thống!\n" +
                        "Vui lòng sang mục Khách Hàng để tạo thẻ mới.", 
                        "Lỗi Khách Hàng", JOptionPane.ERROR_MESSAGE);
                    txtMaKH.requestFocus();
                    txtMaKH.selectAll(); 
                    return; 
                }
            }
            
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
                    
                    // GỌI HÀM IN HÓA ĐƠN VỪA TẠO
                    String tongTienStr = lblTongTien.getText();
                    inHoaDon(maHD, suatInfo, maKH, tongTienStr);
                    
                    txtMaKH.setText("");
                    loadGheTheoSuat();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL, thanh toán thất bại!", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}