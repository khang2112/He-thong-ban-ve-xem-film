package entity;

public class ChiTietHoaDon {
    private String maHD;
    private String maPhim;
    private int soLuong;
    private double donGia;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(String maHD, String maPhim, int soLuong, double donGia) {
        this.maHD = maHD;
        this.maPhim = maPhim;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public String getMaPhim() {
        return maPhim;
    }

    public void setMaPhim(String maPhim) {
        this.maPhim = maPhim;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    // Tính thành tiền (rất hữu ích)
    public double getThanhTien() {
        return soLuong * donGia;
    }
}