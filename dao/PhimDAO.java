package dao;

import connect.Database;
import entity.Phim;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class PhimDAO {
    public ArrayList<Phim> docTuBang() {
        ArrayList<Phim> dsPhim = new ArrayList<>();
        try {
            Connection con = Database.getInstance().getConnection();
            String sql = "SELECT * FROM Phim";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            
            while (rs.next()) {
                String ma = rs.getString("MaPhim");
                String ten = rs.getString("TenPhim");
                String theLoai = rs.getString("TheLoai");
                double gia = rs.getDouble("GiaVe");
                Phim p = new Phim(ma, ten, theLoai, gia);
                dsPhim.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsPhim;
    }
    public boolean themPhim(Phim p) {
        Connection con = Database.getInstance().getConnection();
        PreparedStatement stmt = null;
        int n = 0;
        try {
            String sql = "INSERT INTO Phim (MaPhim, TenPhim, TheLoai, GiaVe) VALUES(?, ?, ?, ?)";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, p.getMaPhim());
            stmt.setString(2, p.getTenPhim());
            stmt.setString(3, p.getTheLoai());
            stmt.setDouble(4, p.getGiaVe());
            n = stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return n > 0;
    }

    // 2. Hàm XÓA phim
    public boolean xoaPhim(String maPhim) {
        Connection con = Database.getInstance().getConnection();
        PreparedStatement stmt = null;
        int n = 0;
        try {
            String sql = "DELETE FROM Phim WHERE MaPhim = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maPhim);
            n = stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return n > 0;
    }

    // 3. Hàm SỬA phim
    public boolean suaPhim(Phim p) {
        Connection con = Database.getInstance().getConnection();
        PreparedStatement stmt = null;
        int n = 0;
        try {
            String sql = "UPDATE Phim SET TenPhim = ?, TheLoai = ?, GiaVe = ? WHERE MaPhim = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, p.getTenPhim());
            stmt.setString(2, p.getTheLoai());
            stmt.setDouble(3, p.getGiaVe());
            stmt.setString(4, p.getMaPhim());
            n = stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return n > 0;
    }
}