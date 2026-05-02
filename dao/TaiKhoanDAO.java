package dao;

import connect.Database;
import entity.TaiKhoan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TaiKhoanDAO {
    public TaiKhoan kiemTraDangNhap(String username, String password) {
        TaiKhoan tk = null;
        try {
            Connection con = Database.getInstance().getConnection();
            String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ? AND MatKhau = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
            	tk = new TaiKhoan(
                        rs.getString("TenDangNhap"),
                        rs.getString("MatKhau"),
                        rs.getString("HoTen"),
                        rs.getString("VaiTro")
                    );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tk;
    }
}