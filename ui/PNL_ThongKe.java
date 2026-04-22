package ui;

import dao.ThongKeDAO;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class PNL_ThongKe extends JPanel {
    private JLabel lblTongDoanhThu, lblTongSoVe, lblPhimTop1;
    private DefaultTableModel modelXepHang;
    private JTable tblXepHang;
    private ThongKeDAO thongKeDAO;

    public PNL_ThongKe() {
        thongKeDAO = new ThongKeDAO();
        
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(30, 30, 30));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // ==========================================
        // 1. PHẦN TRÊN: CÁC THẺ TỔNG QUAN (SUMMARY CARDS)
        // ==========================================
        JPanel pnlCards = new JPanel(new GridLayout(1, 3, 20, 0));
        pnlCards.setOpaque(false);
        pnlCards.setPreferredSize(new Dimension(0, 120));

        // Card 1: Tổng Doanh Thu
        lblTongDoanhThu = new JLabel("0 VNĐ", JLabel.CENTER);
        pnlCards.add(createSummaryCard("TỔNG DOANH THU", lblTongDoanhThu, new Color(46, 204, 113)));

        // Card 2: Tổng Số Vé
        lblTongSoVe = new JLabel("0 Vé", JLabel.CENTER);
        pnlCards.add(createSummaryCard("TỔNG SỐ VÉ ĐÃ BÁN", lblTongSoVe, new Color(52, 152, 219)));

        // Card 3: Phim Bán Chạy Nhất
        lblPhimTop1 = new JLabel("Chưa có dữ liệu", JLabel.CENTER);
        pnlCards.add(createSummaryCard("PHIM HOT NHẤT", lblPhimTop1, new Color(241, 196, 15)));

        add(pnlCards, BorderLayout.NORTH);

        // ==========================================
        // 2. PHẦN DƯỚI: BẢNG XẾP HẠNG DOANH THU PHIM
        // ==========================================
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setOpaque(false);
        TitledBorder borderTbl = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "BẢNG XẾP HẠNG DOANH THU THEO PHIM");
        borderTbl.setTitleColor(Color.ORANGE);
        borderTbl.setTitleFont(new Font("Arial", Font.BOLD, 14));
        pnlTable.setBorder(borderTbl);

        String[] cols = {"Hạng", "Tên Phim", "Số Lượng Vé Bán", "Tổng Doanh Thu"};
        modelXepHang = new DefaultTableModel(cols, 0);
        tblXepHang = new JTable(modelXepHang);
        
        // Custom giao diện bảng
        tblXepHang.setRowHeight(35);
        tblXepHang.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tblXepHang.getTableHeader().setBackground(new Color(60, 60, 60));
        tblXepHang.getTableHeader().setForeground(Color.WHITE);
        tblXepHang.setFont(new Font("Arial", Font.PLAIN, 15));

        pnlTable.add(new JScrollPane(tblXepHang), BorderLayout.CENTER);
        add(pnlTable, BorderLayout.CENTER);

        // Nạp dữ liệu lúc mới mở form
        loadData();
    }

    // Hàm tạo 1 thẻ (Card) hiển thị số liệu
    private JPanel createSummaryCard(String title, JLabel lblValue, Color titleColor) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(new Color(45, 45, 45));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70), 2),
            new EmptyBorder(15, 10, 15, 10)
        ));

        JLabel lblTitle = new JLabel(title, JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(titleColor);

        lblValue.setFont(new Font("Arial", Font.BOLD, 24));
        lblValue.setForeground(Color.WHITE);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    // Hàm lấy dữ liệu từ DAO và đổ lên giao diện
    public void loadData() {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

        // 1. Load Tổng quan
        double[] tongQuan = thongKeDAO.layThongKeTongQuan();
        lblTongSoVe.setText((int) tongQuan[0] + " Vé");
        lblTongDoanhThu.setText(nf.format(tongQuan[1]) + " đ");

        // 2. Load Bảng xếp hạng phim
        modelXepHang.setRowCount(0);
        ArrayList<Object[]> listPhim = thongKeDAO.thongKeDoanhThuTheoPhim();
        
        int hang = 1;
        for (Object[] row : listPhim) {
            String tenPhim = (String) row[0];
            int soVe = (int) row[1];
            double tien = (double) row[2];
            
            // Lấy tên phim top 1 gắn lên Thẻ Summary thứ 3
            if (hang == 1) {
                lblPhimTop1.setText(tenPhim);
            }

            modelXepHang.addRow(new Object[]{
                "TOP " + hang, 
                tenPhim, 
                soVe, 
                nf.format(tien) + " đ"
            });
            hang++;
        }
        
        if(listPhim.isEmpty()) {
            lblPhimTop1.setText("Chưa có dữ liệu");
        }
    }
}