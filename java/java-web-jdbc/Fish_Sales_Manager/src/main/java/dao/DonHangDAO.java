package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.DonHang;
import model.KhachHang;
import model.GioHang;
import util.DBContext;

public class DonHangDAO extends DBContext {

    public boolean createOrder(DonHang donHang, List<GioHang> cartItems) {
        try {
            // Thêm đơn hàng
            String sqlDonHang = "INSERT INTO DON_HANG (ma_khach_hang, ma_don_hang_hien_thi, ten_nguoi_nhan, "
                    + "so_dien_thoai_nhan, dia_chi_giao_hang, tong_tien_hang, phi_van_chuyen, tong_thanh_toan, "
                    + "phuong_thuc_thanh_toan, trang_thai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = connection.prepareStatement(sqlDonHang, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, donHang.getKhachHang().getMaKhachHang());
            stmt.setString(2, donHang.getMaDonHangHienThi());
            stmt.setString(3, donHang.getTenNguoiNhan());
            stmt.setString(4, donHang.getSoDienThoaiNhan());
            stmt.setString(5, donHang.getDiaChiGiaoHang());
            stmt.setDouble(6, donHang.getTongTienHang());
            stmt.setDouble(7, donHang.getPhiVanChuyen());
            stmt.setDouble(8, donHang.getTongThanhToan());
            stmt.setString(9, donHang.getPhuongThucThanhToan());
            stmt.setString(10, donHang.getTrangThai());

            int rows = stmt.executeUpdate();
            if (rows == 0) return false;

            // Lấy mã đơn hàng vừa thêm
            ResultSet rs = stmt.getGeneratedKeys();
            int maDonHang = 0;
            if (rs.next()) {
                maDonHang = rs.getInt(1);
            }
            rs.close();
            stmt.close();

            // Thêm chi tiết đơn hàng
            String sqlChiTiet = "INSERT INTO CHI_TIET_DON_HANG (ma_don_hang, ma_loai_ca, so_luong, don_gia, thanh_tien) "
                    + "VALUES (?, ?, ?, ?, ?)";
            stmt = connection.prepareStatement(sqlChiTiet);

            for (GioHang item : cartItems) {
                stmt.setInt(1, maDonHang);
                stmt.setInt(2, item.getLoaiCa().getMaLoaiCa());
                stmt.setDouble(3, item.getSoLuong());
                stmt.setDouble(4, item.getLoaiCa().getGiaBan());
                stmt.setDouble(5, item.getGiaTamTinh());
                stmt.executeUpdate();
            }
            stmt.close();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy danh sách đơn hàng theo khách hàng
    public List<DonHang> getByKhachHang(int maKhachHang) {
        List<DonHang> list = new ArrayList<>();
        String sql = "SELECT * FROM DON_HANG WHERE ma_khach_hang = ? ORDER BY ngay_dat_hang DESC";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, maKhachHang);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                DonHang dh = new DonHang();
                dh.setMaDonHang(rs.getInt("ma_don_hang"));
                dh.setMaDonHangHienThi(rs.getString("ma_don_hang_hien_thi"));
                dh.setTenNguoiNhan(rs.getString("ten_nguoi_nhan"));
                dh.setSoDienThoaiNhan(rs.getString("so_dien_thoai_nhan"));
                dh.setDiaChiGiaoHang(rs.getString("dia_chi_giao_hang"));
                dh.setTongTienHang(rs.getDouble("tong_tien_hang"));
                dh.setPhiVanChuyen(rs.getDouble("phi_van_chuyen"));
                dh.setTongThanhToan(rs.getDouble("tong_thanh_toan"));
                dh.setPhuongThucThanhToan(rs.getString("phuong_thuc_thanh_toan"));
                dh.setTrangThai(rs.getString("trang_thai"));
                dh.setNgayDatHang(rs.getTimestamp("ngay_dat_hang"));
                list.add(dh);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy tất cả đơn hàng (admin)
    public List<DonHang> getAll() {
        List<DonHang> list = new ArrayList<>();
        String sql = "SELECT dh.*, kh.ho_ten FROM DON_HANG dh "
                + "INNER JOIN KHACH_HANG kh ON dh.ma_khach_hang = kh.ma_khach_hang "
                + "ORDER BY dh.ngay_dat_hang DESC";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                DonHang dh = new DonHang();
                dh.setMaDonHang(rs.getInt("ma_don_hang"));
                dh.setMaDonHangHienThi(rs.getString("ma_don_hang_hien_thi"));
                dh.setTenNguoiNhan(rs.getString("ten_nguoi_nhan"));
                dh.setTrangThai(rs.getString("trang_thai"));
                dh.setTongThanhToan(rs.getDouble("tong_thanh_toan"));
                dh.setNgayDatHang(rs.getTimestamp("ngay_dat_hang"));

                KhachHang kh = new KhachHang();
                kh.setHoTen(rs.getString("ho_ten"));
                dh.setKhachHang(kh);

                list.add(dh);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Cập nhật trạng thái đơn hàng
    public boolean updateStatus(int maDonHang, String trangThai) {
        try {
            String sql = "UPDATE DON_HANG SET trang_thai = ? WHERE ma_don_hang = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, trangThai);
            stmt.setInt(2, maDonHang);
            int rows = stmt.executeUpdate();
            stmt.close();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
