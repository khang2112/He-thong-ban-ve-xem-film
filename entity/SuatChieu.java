package entity;

public class SuatChieu {
    private String maSuat;
    private String maPhim;
    private String phongChieu;
    private String ngayChieu; // Dùng String cho dễ xử lý giao diện
    private String gioChieu;

    public SuatChieu(String maSuat, String maPhim, String phongChieu, String ngayChieu, String gioChieu) {
        this.maSuat = maSuat;
        this.maPhim = maPhim;
        this.phongChieu = phongChieu;
        this.ngayChieu = ngayChieu;
        this.gioChieu = gioChieu;
    }

    public String getMaSuat() { return maSuat; }
    public String getMaPhim() { return maPhim; }
    public String getPhongChieu() { return phongChieu; }
    public String getNgayChieu() { return ngayChieu; }
    public String getGioChieu() { return gioChieu; }
}