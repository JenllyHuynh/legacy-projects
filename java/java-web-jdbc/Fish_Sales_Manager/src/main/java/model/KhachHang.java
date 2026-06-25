package model;

import java.util.Date;

public class KhachHang {

    private int maKhachHang;
    private String tenDangNhap;
    private String matKhau;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private Date ngaySinh;
    private String gioiTinh;
    private String vaiTro;
    private Date ngayDangKy;
    private boolean trangThai;

    public KhachHang() {
    }

    public KhachHang(int maKhachHang, String tenDangNhap, String matKhau, String hoTen, String email, String soDienThoai, Date ngaySinh, String gioiTinh, String vaiTro, Date ngayDangKy, boolean trangThai) {
        this.maKhachHang = maKhachHang;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.hoTen = hoTen;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.vaiTro = vaiTro;
        this.ngayDangKy = ngayDangKy;
        this.trangThai = trangThai;
    }

    public int getMaKhachHang() {
        return maKhachHang;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public String getHoTen() {
        return hoTen;
    }

    public String getEmail() {
        return email;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public Date getNgayDangKy() {
        return ngayDangKy;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setMaKhachHang(int maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public void setNgayDangKy(Date ngayDangKy) {
        this.ngayDangKy = ngayDangKy;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

}
