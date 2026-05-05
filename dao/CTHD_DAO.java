package dao;

import connect.Database;
import entity.ChiTietHoaDon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CTHD_DAO {

    // 1. THÊM CHI TIẾT HÓA ĐƠN
    public boolean themChiTiet(ChiTietHoaDon ct) {
        Connection con = Database.getInstance().getConnection();
        try {
            String sql = "INSERT INTO ChiTietHoaDon (MaHD, MaPhim, SoLuong, DonGia) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, ct.getMaHD());
            ps.setString(2, ct.getMaPhim());
            ps.setInt(3, ct.getSoLuong());
            ps.setDouble(4, ct.getDonGia());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 2. LẤY DANH SÁCH CHI TIẾT THEO MÃ HĐ
    public List<ChiTietHoaDon> getChiTietByMaHD(String maHD) {
        List<ChiTietHoaDon> list = new ArrayList<>();
        Connection con = Database.getInstance().getConnection();

        try {
            String sql = "SELECT * FROM ChiTietHoaDon WHERE MaHD = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maHD);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ChiTietHoaDon ct = new ChiTietHoaDon(
                        rs.getString("MaHD"),
                        rs.getString("MaPhim"),
                        rs.getInt("SoLuong"),
                        rs.getDouble("DonGia")
                );
                list.add(ct);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 3. XÓA CHI TIẾT THEO HĐ
    public boolean xoaTheoMaHD(String maHD) {
        Connection con = Database.getInstance().getConnection();

        try {
            String sql = "DELETE FROM ChiTietHoaDon WHERE MaHD = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maHD);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // 4. TÍNH TỔNG TIỀN HÓA ĐƠN
    public double tinhTongTien(String maHD) {
        double tong = 0;
        Connection con = Database.getInstance().getConnection();

        try {
            String sql = "SELECT SUM(SoLuong * DonGia) AS Tong FROM ChiTietHoaDon WHERE MaHD = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maHD);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                tong = rs.getDouble("Tong");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tong;
    }
}