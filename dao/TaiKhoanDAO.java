package dao;

import connect.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TaiKhoanDAO {
    public boolean kiemTraDangNhap(String username, String password) {
        boolean isValid = false;
        try {
            Connection con = Database.getInstance().getConnection();
            String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ? AND MatKhau = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                isValid = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isValid;
    }
}