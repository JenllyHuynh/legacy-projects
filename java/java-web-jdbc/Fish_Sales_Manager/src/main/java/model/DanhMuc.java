package model;

public class DanhMuc {

    private int maDanhMuc;
    private String tenDanhMuc;
    private String moTa;
    private boolean trangThai;

    public DanhMuc() {
    }

    public DanhMuc(int maDanhMuc, String tenDanhMuc, String moTa, boolean trangThai) {
        this.maDanhMuc = maDanhMuc;
        this.tenDanhMuc = tenDanhMuc;
        this.moTa = moTa;
        this.trangThai = trangThai;
    }

    public int getMaDanhMuc() {
        return maDanhMuc;
    }

    public String getTenDanhMuc() {
        return tenDanhMuc;
    }

    public String getMoTa() {
        return moTa;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setMaDanhMuc(int maDanhMuc) {
        this.maDanhMuc = maDanhMuc;
    }

    public void setTenDanhMuc(String tenDanhMuc) {
        this.tenDanhMuc = tenDanhMuc;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

}
