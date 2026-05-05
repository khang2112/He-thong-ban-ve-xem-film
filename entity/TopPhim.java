package entity;

public class TopPhim {
    private String tenPhim;
    private int soVeBan;

    // Constructor
    public TopPhim(String tenPhim, int soVeBan) {
        this.tenPhim = tenPhim;
        this.soVeBan = soVeBan;
    }

    // Getter & Setter
    public String getTenPhim() {
        return tenPhim;
    }

    public void setTenPhim(String tenPhim) {
        this.tenPhim = tenPhim;
    }

    public int getSoVeBan() {
        return soVeBan;
    }

    public void setSoVeBan(int soVeBan) {
        this.soVeBan = soVeBan;
    }
}