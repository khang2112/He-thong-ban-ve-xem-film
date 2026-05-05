package dao;

import connect.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ThongKeDAO {

    // 1. Lấy dữ liệu Tổng quan (Tổng số vé đã bán và Tổng doanh thu toàn hệ thống)
    public double[] layThongKeTongQuan(int thang, int nam, String theLoai) {
        double[] kq = new double[]{0, 0}; // Mảng 2 phần tử: [0] là Số vé, [1] là Tổng tiền
        try {
            Connection con = Database.getInstance().getConnection();
            
            StringBuilder sql = new StringBuilder(
                "SELECT COUNT(v.MaVe) as TongSoVe, SUM(v.GiaVe) as TongDoanhThu " +
                "FROM Phim p " +
                "JOIN SuatChieu s ON p.MaPhim = s.MaPhim " +
                "JOIN VePhim v ON s.MaSuat = v.MaSuat " +
                "WHERE 1=1 "
            );

            if (thang > 0) {
                sql.append("AND MONTH(s.NgayChieu) = ? ");
            }
            if (nam > 0) {
                sql.append("AND YEAR(s.NgayChieu) = ? ");
            }
            if (theLoai != null && !theLoai.isEmpty()) {
                sql.append("AND p.TheLoai LIKE ? ");
            }

            PreparedStatement pst = con.prepareStatement(sql.toString());

            int index = 1;
            if (thang > 0) {
                pst.setInt(index++, thang);
            }
            if (nam > 0) {
                pst.setInt(index++, nam);
            }
            if (theLoai != null && !theLoai.isEmpty()) {
                pst.setString(index++, "%" + theLoai + "%");
            }

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                kq[0] = rs.getInt("TongSoVe");
                kq[1] = rs.getDouble("TongDoanhThu");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kq;
    }

    // 2. Thống kê doanh thu theo từng bộ phim (Xếp hạng từ cao xuống thấp)
    // Trả về danh sách mảng Object chứa: [Tên Phim, Số Vé Bán Được, Tổng Tiền]
    public ArrayList<Object[]> thongKeDoanhThuTheoPhim(int thang, int nam, String theLoai) {
        ArrayList<Object[]> list = new ArrayList<>();
        try {
            Connection con = Database.getInstance().getConnection();
            
            // Dùng StringBuilder để cộng chuỗi SQL động tùy theo bộ lọc
            StringBuilder sql = new StringBuilder(
                "SELECT p.TenPhim, COUNT(v.MaVe) as SoVe, SUM(v.GiaVe) as DoanhThu " +
                "FROM Phim p " +
                "JOIN SuatChieu s ON p.MaPhim = s.MaPhim " +
                "JOIN VePhim v ON s.MaSuat = v.MaSuat " +
                "WHERE 1=1 " // 1=1 là mẹo để nối các điều kiện AND phía sau dễ dàng
            );

            // Nối thêm điều kiện nếu có chọn bộ lọc
            if (thang > 0) {
                sql.append("AND MONTH(s.NgayChieu) = ? ");
            }
            if (nam > 0) {
                sql.append("AND YEAR(s.NgayChieu) = ? ");
            }
            if (theLoai != null && !theLoai.isEmpty()) {
                sql.append("AND p.TheLoai LIKE ? ");
            }

            // Cuối cùng mới gom nhóm và sắp xếp
            sql.append("GROUP BY p.TenPhim ORDER BY DoanhThu DESC");

            PreparedStatement pst = con.prepareStatement(sql.toString());

            // Gắn giá trị vào các dấu '?' tương ứng
            int index = 1;
            if (thang > 0) {
                pst.setInt(index++, thang);
            }
            if (nam > 0) {
                pst.setInt(index++, nam);
            }
            if (theLoai != null && !theLoai.isEmpty()) {
                pst.setString(index++, "%" + theLoai + "%"); // Dùng % để tìm kiếm gần đúng thể loại
            }

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