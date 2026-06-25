package model;

import java.util.Date;
import java.util.List;

public class DonHang {

    private int maDonHang;
    private KhachHang khachHang;
    private String maDonHangHienThi;
    private String tenNguoiNhan;
    private String soDienThoaiNhan;
    private String diaChiGiaoHang;
    private double tongTienHang;
    private double phiVanChuyen;
    private double tongThanhToan;
    private String phuongThucThanhToan;
    private String trangThai;
    private Date ngayDatHang;
    private List<ChiTietDonHang> chiTietDonHang;

    public DonHang() {
    }

    public DonHang(int maDonHang, KhachHang khachHang, String maDonHangHienThi, String tenNguoiNhan, String soDienThoaiNhan, String diaChiGiaoHang, double tongTienHang, double phiVanChuyen, double tongThanhToan, String phuongThucThanhToan, String trangThai, Date ngayDatHang, List<ChiTietDonHang> chiTietDonHang) {
        this.maDonHang = maDonHang;
        this.khachHang = khachHang;
        this.maDonHangHienThi = maDonHangHienThi;
        this.tenNguoiNhan = tenNguoiNhan;
        this.soDienThoaiNhan = soDienThoaiNhan;
        this.diaChiGiaoHang = diaChiGiaoHang;
        this.tongTienHang = tongTienHang;
        this.phiVanChuyen = phiVanChuyen;
        this.tongThanhToan = tongThanhToan;
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.trangThai = trangThai;
        this.ngayDatHang = ngayDatHang;
        this.chiTietDonHang = chiTietDonHang;
    }

    public int getMaDonHang() {
        return maDonHang;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public String getMaDonHangHienThi() {
        return maDonHangHienThi;
    }

    public String getTenNguoiNhan() {
        return tenNguoiNhan;
    }

    public String getSoDienThoaiNhan() {
        return soDienThoaiNhan;
    }

    public String getDiaChiGiaoHang() {
        return diaChiGiaoHang;
    }

    public double getTongTienHang() {
        return tongTienHang;
    }

    public double getPhiVanChuyen() {
        return phiVanChuyen;
    }

    public double getTongThanhToan() {
        return tongThanhToan;
    }

    public String getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public Date getNgayDatHang() {
        return ngayDatHang;
    }

    public List<ChiTietDonHang> getChiTietDonHang() {
        return chiTietDonHang;
    }

    public void setMaDonHang(int maDonHang) {
        this.maDonHang = maDonHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public void setMaDonHangHienThi(String maDonHangHienThi) {
        this.maDonHangHienThi = maDonHangHienThi;
    }

    public void setTenNguoiNhan(String tenNguoiNhan) {
        this.tenNguoiNhan = tenNguoiNhan;
    }

    public void setSoDienThoaiNhan(String soDienThoaiNhan) {
        this.soDienThoaiNhan = soDienThoaiNhan;
    }

    public void setDiaChiGiaoHang(String diaChiGiaoHang) {
        this.diaChiGiaoHang = diaChiGiaoHang;
    }

    public void setTongTienHang(double tongTienHang) {
        this.tongTienHang = tongTienHang;
    }

    public void setPhiVanChuyen(double phiVanChuyen) {
        this.phiVanChuyen = phiVanChuyen;
    }

    public void setTongThanhToan(double tongThanhToan) {
        this.tongThanhToan = tongThanhToan;
    }

    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public void setNgayDatHang(Date ngayDatHang) {
        this.ngayDatHang = ngayDatHang;
    }

    public void setChiTietDonHang(List<ChiTietDonHang> chiTietDonHang) {
        this.chiTietDonHang = chiTietDonHang;
    }

}
