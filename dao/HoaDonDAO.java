package dao;

import connect.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class HoaDonDAO {

    // Hàm thực hiện lưu Hóa Đơn và các Vé Phim cùng lúc
	public boolean thanhToanHoaDon(String maHD, String maSuat, ArrayList<String> danhSachGhe, double giaVe, String maKH) {
        Connection con = Database.getInstance().getConnection();
        
        try {
            con.setAutoCommit(false); // Bắt đầu Transaction

            // 1. LƯU HÓA ĐƠN
            String sqlHD = "INSERT INTO HoaDon (MaHD, MaNV, NgayLap, TongTien, MaKH) VALUES (?, ?, GETDATE(), ?, ?)";
            PreparedStatement pstHD = con.prepareStatement(sqlHD);
            pstHD.setString(1, maHD);
            pstHD.setString(2, "admin"); 
            
            double tongTien = danhSachGhe.size() * giaVe;
            pstHD.setDouble(3, tongTien);
            
            // Nếu không có mã KH thì set là NULL trong SQL
            if (maKH == null || maKH.isEmpty()) {
                pstHD.setNull(4, java.sql.Types.VARCHAR);
            } else {
                pstHD.setString(4, maKH);
            }
            pstHD.executeUpdate();

            // 2. LƯU VÉ PHIM
            String sqlVe = "INSERT INTO VePhim (MaHD, MaSuat, MaGhe, GiaVe) VALUES (?, ?, ?, ?)";
            PreparedStatement pstVe = con.prepareStatement(sqlVe);
            for (String ghe : danhSachGhe) {
                pstVe.setString(1, maHD);
                pstVe.setString(2, maSuat);
                pstVe.setString(3, ghe);
                pstVe.setDouble(4, giaVe);
                pstVe.executeUpdate();
            }

            // 3. TÍCH ĐIỂM TỰ ĐỘNG (Nếu có Mã Khách Hàng)
            if (maKH != null && !maKH.isEmpty()) {
                // Công thức: 10.000đ = 1 điểm
                int diemCong = (int) (tongTien / 10000); 
                
                String sqlDiem = "UPDATE KhachHang SET DiemTichLuy = DiemTichLuy + ? WHERE MaKH = ?";
                PreparedStatement pstDiem = con.prepareStatement(sqlDiem);
                pstDiem.setInt(1, diemCong);
                pstDiem.setString(2, maKH);
                pstDiem.executeUpdate();
            }

            con.commit(); // Thành công tất cả thì chốt lưu
            return true;

        } catch (Exception e) {
            try { con.rollback(); } catch (Exception ex) {} // Lỗi thì hoàn tác
            e.printStackTrace();
            return false;
        } finally {
            try { con.setAutoCommit(true); } catch (Exception ex) {}
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