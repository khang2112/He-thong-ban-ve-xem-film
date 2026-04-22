package ui;

import dao.HoaDonDAO;
import entity.HoaDon;
import entity.VePhim;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class PNL_HoaDon extends JPanel implements MouseListener {
    private DefaultTableModel modelHoaDon;
    private JTable tblHoaDon;
    
    private DefaultTableModel modelChiTiet;
    private JTable tblChiTiet;
    
    private HoaDonDAO hoaDonDAO;

    public PNL_HoaDon() {
        hoaDonDAO = new HoaDonDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(30, 30, 30));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // ==========================================
        // 1. BẢNG TRÊN: DANH SÁCH HÓA ĐƠN
        // ==========================================
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        
        TitledBorder borderHD = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "DANH SÁCH HÓA ĐƠN");
        borderHD.setTitleColor(Color.ORANGE);
        borderHD.setTitleFont(new Font("Arial", Font.BOLD, 14));
        pnlTop.setBorder(borderHD);

        String[] colsHD = {"Mã Hóa Đơn", "Nhân Viên Lập", "Ngày Lập", "Tổng Tiền"};
        modelHoaDon = new DefaultTableModel(colsHD, 0);
        tblHoaDon = new JTable(modelHoaDon);
        setupTableStyle(tblHoaDon);
        
        JScrollPane scrollHD = new JScrollPane(tblHoaDon);
        scrollHD.setPreferredSize(new Dimension(0, 300)); // Chiều cao cố định cho nửa trên
        pnlTop.add(scrollHD, BorderLayout.CENTER);
        add(pnlTop, BorderLayout.NORTH);

        // ==========================================
        // 2. BẢNG DƯỚI: CHI TIẾT VÉ PHIM
        // ==========================================
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setOpaque(false);
        
        TitledBorder borderCT = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "CHI TIẾT VÉ PHIM (Chọn Hóa Đơn ở trên để xem)");
        borderCT.setTitleColor(new Color(46, 204, 113)); // Màu xanh lá
        borderCT.setTitleFont(new Font("Arial", Font.BOLD, 14));
        pnlBottom.setBorder(borderCT);

        String[] colsCT = {"Mã Vé", "Mã Suất Chiếu", "Ghế", "Giá Vé"};
        modelChiTiet = new DefaultTableModel(colsCT, 0);
        tblChiTiet = new JTable(modelChiTiet);
        setupTableStyle(tblChiTiet);

        pnlBottom.add(new JScrollPane(tblChiTiet), BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.CENTER);

        // Đăng ký sự kiện click chuột
        tblHoaDon.addMouseListener(this);

        // Load dữ liệu khi mở form
        loadDataHoaDon();
    }

    // Hàm tùy chỉnh giao diện JTable cho đẹp
    private void setupTableStyle(JTable table) {
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(60, 60, 60));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    // Tải dữ liệu Hóa Đơn từ SQL lên bảng trên
    public void loadDataHoaDon() {
        modelHoaDon.setRowCount(0);
        ArrayList<HoaDon> dsHD = hoaDonDAO.layDanhSachHoaDon();
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        
        for (HoaDon hd : dsHD) {
            modelHoaDon.addRow(new Object[]{
                hd.getMaHD(), 
                hd.getMaNV(), 
                hd.getNgayLap().substring(0, 19), // Cắt lấy chuỗi ngày giờ cho đẹp
                nf.format(hd.getTongTien()) + " đ"
            });
        }
    }

    // Sự kiện khi click vào 1 dòng trên bảng Hóa Đơn
    @Override
    public void mouseClicked(MouseEvent e) {
        int row = tblHoaDon.getSelectedRow();
        if (row != -1) {
            String maHD = modelHoaDon.getValueAt(row, 0).toString();
            
            // Xóa dữ liệu cũ bảng dưới
            modelChiTiet.setRowCount(0);
            
            // Gọi DAO lấy danh sách vé của Hóa Đơn này
            ArrayList<VePhim> dsVe = hoaDonDAO.layChiTietVe(maHD);
            NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
            
            for (VePhim ve : dsVe) {
                modelChiTiet.addRow(new Object[]{
                    ve.getMaVe(), 
                    ve.getMaSuat(), 
                    ve.getMaGhe(), 
                    nf.format(ve.getGiaVe()) + " đ"
                });
            }
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}