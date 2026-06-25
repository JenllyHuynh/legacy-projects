package model;

public class ChiTietDonHang {

    private int maChiTietDH;
    private DonHang donHang;
    private LoaiCa loaiCa;
    private double soLuong;
    private double donGia;
    private double thanhTien;

    public ChiTietDonHang() {
    }

    public ChiTietDonHang(int maChiTietDH, DonHang donHang, LoaiCa loaiCa, double soLuong, double donGia, double thanhTien) {
        this.maChiTietDH = maChiTietDH;
        this.donHang = donHang;
        this.loaiCa = loaiCa;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
    }

    public int getMaChiTietDH() {
        return maChiTietDH;
    }

    public DonHang getDonHang() {
        return donHang;
    }

    public LoaiCa getLoaiCa() {
        return loaiCa;
    }

    public double getSoLuong() {
        return soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public double getThanhTien() {
        return thanhTien;
    }

    public void setMaChiTietDH(int maChiTietDH) {
        this.maChiTietDH = maChiTietDH;
    }

    public void setDonHang(DonHang donHang) {
        this.donHang = donHang;
    }

    public void setLoaiCa(LoaiCa loaiCa) {
        this.loaiCa = loaiCa;
    }

    public void setSoLuong(double soLuong) {
        this.soLuong = soLuong;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public void setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
    }

}
