package entity;

public class HoaDon {
    private String maHD;
    private String maNV;
    private String ngayLap;
    private double tongTien;

    public HoaDon(String maHD, String maNV, String ngayLap, double tongTien) {
        this.maHD = maHD;
        this.maNV = maNV;
        this.ngayLap = ngayLap;
        this.tongTien = tongTien;
    }

    public String getMaHD() { return maHD; }
    public String getMaNV() { return maNV; }
    public String getNgayLap() { return ngayLap; }
    public double getTongTien() { return tongTien; }
}