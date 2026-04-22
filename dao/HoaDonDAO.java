package dao;

import connect.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class HoaDonDAO {

    // Hàm thực hiện lưu Hóa Đơn và các Vé Phim cùng lúc
    public boolean thanhToanHoaDon(String maHD, String maSuat, ArrayList<String> danhSachGhe, double giaVe) {
        Connection con = Database.getInstance().getConnection();
        
        try {
            // 1. Tắt tự động lưu để bắt đầu Transaction
            con.setAutoCommit(false); 

            // 2. Thêm dữ liệu vào bảng HoaDon
            // (GETDATE() là hàm SQL Server tự lấy ngày giờ hiện tại hệ thống)
            String sqlHD = "INSERT INTO HoaDon (MaHD, MaNV, NgayLap, TongTien) VALUES (?, ?, GETDATE(), ?)";
            PreparedStatement pstHD = con.prepareStatement(sqlHD);
            pstHD.setString(1, maHD);
            pstHD.setString(2, "admin"); // Tạm fix cứng người lập là admin (hoặc truyền biến vào)
            
            double tongTien = danhSachGhe.size() * giaVe;
            pstHD.setDouble(3, tongTien);
            pstHD.executeUpdate();

            // 3. Thêm dữ liệu vào bảng VePhim (Dùng vòng lặp cho từng ghế)
            String sqlVe = "INSERT INTO VePhim (MaHD, MaSuat, MaGhe, GiaVe) VALUES (?, ?, ?, ?)";
            PreparedStatement pstVe = con.prepareStatement(sqlVe);
            
            for (String ghe : danhSachGhe) {
                pstVe.setString(1, maHD);
                pstVe.setString(2, maSuat);
                pstVe.setString(3, ghe);
                pstVe.setDouble(4, giaVe);
                pstVe.executeUpdate();
            }

            // 4. Nếu code chạy trót lọt đến đây -> Xác nhận lưu toàn bộ xuống SQL
            con.commit(); 
            return true;

        } catch (Exception e) {
            // Nếu có bất kỳ lỗi nào xảy ra -> Hoàn tác toàn bộ, không lưu gì cả
            try {
                con.rollback(); 
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            // Bật lại tự động lưu cho các tính năng khác của phần mềm
            try {
                con.setAutoCommit(true); 
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
 // Hàm lấy danh sách các ghế đã bán theo Mã Suất Chiếu
    public ArrayList<String> layDanhSachGheDaBan(String maSuat) {
        ArrayList<String> dsGhe = new ArrayList<>();
        try {
            Connection con = Database.getInstance().getConnection();
            String sql = "SELECT MaGhe FROM VePhim WHERE MaSuat = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, maSuat);
            
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                dsGhe.add(rs.getString("MaGhe")); // Lấy tên ghế (VD: A1, B2) cho vào danh sách
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsGhe;
    }
 // Lấy toàn bộ danh sách Hóa Đơn
    public ArrayList<entity.HoaDon> layDanhSachHoaDon() {
        ArrayList<entity.HoaDon> dsHD = new ArrayList<>();
        try {
            Connection con = Database.getInstance().getConnection();
            String sql = "SELECT * FROM HoaDon ORDER BY NgayLap DESC"; // Sắp xếp hóa đơn mới nhất lên đầu
            PreparedStatement pst = con.prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                String maHD = rs.getString("MaHD");
                String maNV = rs.getString("MaNV");
                String ngay = rs.getString("NgayLap");
                double tong = rs.getDouble("TongTien");
                dsHD.add(new entity.HoaDon(maHD, maNV, ngay, tong));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsHD;
    }

    // Lấy chi tiết các vé phim thuộc về 1 mã Hóa Đơn
    public ArrayList<entity.VePhim> layChiTietVe(String maHD) {
        ArrayList<entity.VePhim> dsVe = new ArrayList<>();
        try {
            Connection con = Database.getInstance().getConnection();
            String sql = "SELECT * FROM VePhim WHERE MaHD = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, maHD);
            java.sql.ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                int maVe = rs.getInt("MaVe");
                String mHD = rs.getString("MaHD");
                String maSuat = rs.getString("MaSuat");
                String maGhe = rs.getString("MaGhe");
                double gia = rs.getDouble("GiaVe");
                dsVe.add(new entity.VePhim(maVe, mHD, maSuat, maGhe, gia));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsVe;
    }
}