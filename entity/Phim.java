package entity;

public class Phim {
    private String maPhim;
    private String tenPhim;
    private String theLoai;
    private double giaVe;

    public Phim(String maPhim, String tenPhim, String theLoai, double giaVe) {
        this.maPhim = maPhim;
        this.tenPhim = tenPhim;
        this.theLoai = theLoai;
        this.giaVe = giaVe;
    }

    public String getMaPhim() { return maPhim; }
    public String getTenPhim() { return tenPhim; }
    public String getTheLoai() { return theLoai; }
    public double getGiaVe() { return giaVe; }
}