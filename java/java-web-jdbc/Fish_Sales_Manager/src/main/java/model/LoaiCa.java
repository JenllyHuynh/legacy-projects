package model;

import java.util.Date;

public class LoaiCa {

    private int maLoaiCa;
    private DanhMuc danhMuc;
    private String tenLoaiCa;
    private String moTa;
    private String donViTinh;
    private double giaBan;
    private String hinhAnh;
    private Date ngayTao;
    private boolean trangThai;

    public LoaiCa() {
    }

    public LoaiCa(int maLoaiCa, DanhMuc danhMuc, String tenLoaiCa, String moTa, String donViTinh, double giaBan, String hinhAnh, Date ngayTao, boolean trangThai) {
        this.maLoaiCa = maLoaiCa;
        this.danhMuc = danhMuc;
        this.tenLoaiCa = tenLoaiCa;
        this.moTa = moTa;
        this.donViTinh = donViTinh;
        this.giaBan = giaBan;
        this.hinhAnh = hinhAnh;
        this.ngayTao = ngayTao;
        this.trangThai = trangThai;
    }

    public int getMaLoaiCa() {
        return maLoaiCa;
    }

    public DanhMuc getDanhMuc() {
        return danhMuc;
    }

    public String getTenLoaiCa() {
        return tenLoaiCa;
    }

    public String getMoTa() {
        return moTa;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public double getGiaBan() {
        return giaBan;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setMaLoaiCa(int maLoaiCa) {
        this.maLoaiCa = maLoaiCa;
    }

    public void setDanhMuc(DanhMuc danhMuc) {
        this.danhMuc = danhMuc;
    }

    public void setTenLoaiCa(String tenLoaiCa) {
        this.tenLoaiCa = tenLoaiCa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public void setGiaBan(double giaBan) {
        this.giaBan = giaBan;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }
    
    
}
