package model;

import java.util.Date;

public class GioHang {

    private int maGioHang;
    private KhachHang khachHang;
    private LoaiCa loaiCa;
    private double soLuong;
    private double giaTamTinh;
    private Date ngayTao;

    public GioHang() {
    }

    public GioHang(int maGioHang, KhachHang khachHang, LoaiCa loaiCa, double soLuong, double giaTamTinh, Date ngayTao) {
        this.maGioHang = maGioHang;
        this.khachHang = khachHang;
        this.loaiCa = loaiCa;
        this.soLuong = soLuong;
        this.giaTamTinh = giaTamTinh;
        this.ngayTao = ngayTao;
    }

    public int getMaGioHang() {
        return maGioHang;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public LoaiCa getLoaiCa() {
        return loaiCa;
    }

    public double getSoLuong() {
        return soLuong;
    }

    public double getGiaTamTinh() {
        return giaTamTinh;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setMaGioHang(int maGioHang) {
        this.maGioHang = maGioHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public void setLoaiCa(LoaiCa loaiCa) {
        this.loaiCa = loaiCa;
    }

    public void setSoLuong(double soLuong) {
        this.soLuong = soLuong;
    }

    public void setGiaTamTinh(double giaTamTinh) {
        this.giaTamTinh = giaTamTinh;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

}
