package dao;

import connect.Database;
import entity.SuatChieu;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class SuatChieuDAO {
    
    // Đọc danh sách suất chiếu
    public ArrayList<SuatChieu> docTuBang() {
        ArrayList<SuatChieu> dsSuat = new ArrayList<>();
        try {
            Connection con = Database.getInstance().getConnection();
            String sql = "SELECT * FROM SuatChieu";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                String maS = rs.getString("MaSuat");
                String maP = rs.getString("MaPhim");
                String phong = rs.getString("PhongChieu");
                String ngay = rs.getString("NgayChieu");
                String gio = rs.getString("GioChieu");
                dsSuat.add(new SuatChieu(maS, maP, phong, ngay, gio));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsSuat;
    }

    // Thêm suất chiếu
    public boolean themSuatChieu(SuatChieu s) {
        Connection con = Database.getInstance().getConnection();
        PreparedStatement stmt = null;
        int n = 0;
        try {
            String sql = "INSERT INTO SuatChieu VALUES(?, ?, ?, ?, ?)";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, s.getMaSuat());
            stmt.setString(2, s.getMaPhim());
            stmt.setString(3, s.getPhongChieu());
            stmt.setString(4, s.getNgayChieu());
            stmt.setString(5, s.getGioChieu());
            n = stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return n > 0;
    }
}