package dao;

import connect.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ThongKeDAO {

    // 1. Lấy dữ liệu Tổng quan (Tổng số vé đã bán và Tổng doanh thu toàn hệ thống)
    public double[] layThongKeTongQuan() {
        double[] ketQua = new double[2]; // Index 0: Số vé, Index 1: Tổng tiền
        try {
            Connection con = Database.getInstance().getConnection();
            String sql = "SELECT COUNT(MaVe) as TongVe, SUM(GiaVe) as TongTien FROM VePhim";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                ketQua[0] = rs.getDouble("TongVe");
                ketQua[1] = rs.getDouble("TongTien");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ketQua;
    }

    // 2. Thống kê doanh thu theo từng bộ phim (Xếp hạng từ cao xuống thấp)
    // Trả về danh sách mảng Object chứa: [Tên Phim, Số Vé Bán Được, Tổng Tiền]
    public ArrayList<Object[]> thongKeDoanhThuTheoPhim() {
        ArrayList<Object[]> list = new ArrayList<>();
        try {
            Connection con = Database.getInstance().getConnection();
            // Kết nối 3 bảng: Phim -> SuatChieu -> VePhim để gom nhóm theo Tên Phim
            String sql = "SELECT p.TenPhim, COUNT(v.MaVe) as SoVe, SUM(v.GiaVe) as DoanhThu " +
                         "FROM Phim p " +
                         "JOIN SuatChieu s ON p.MaPhim = s.MaPhim " +
                         "JOIN VePhim v ON s.MaSuat = v.MaSuat " +
                         "GROUP BY p.TenPhim " +
                         "ORDER BY DoanhThu DESC";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                String ten = rs.getString("TenPhim");
                int soVe = rs.getInt("SoVe");
                double doanhThu = rs.getDouble("DoanhThu");
                list.add(new Object[]{ten, soVe, doanhThu});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}