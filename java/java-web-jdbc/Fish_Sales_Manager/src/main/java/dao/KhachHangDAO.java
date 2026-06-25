package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import model.KhachHang;

public class KhachHangDAO extends util.DBContext {

    // Map ResulSet cho đối tượng khách hàng
    private KhachHang mapResultSetToKhachHang(ResultSet rs) throws SQLException {
        KhachHang kh = new KhachHang();
        kh.setMaKhachHang(rs.getInt("ma_khach_hang"));
        kh.setTenDangNhap(rs.getString("ten_dang_nhap"));
        kh.setMatKhau(rs.getString("mat_khau"));
        kh.setHoTen(rs.getString("ho_ten"));
        kh.setEmail(rs.getString("email"));
        kh.setSoDienThoai(rs.getString("so_dien_thoai"));
        kh.setNgaySinh(rs.getDate("ngay_sinh"));
        kh.setGioiTinh(rs.getString("gioi_tinh"));
        kh.setVaiTro(rs.getString("vai_tro"));
        kh.setNgayDangKy(rs.getTimestamp("ngay_dang_ky"));
        kh.setTrangThai(rs.getBoolean("trang_thai"));
        return kh;
    }

    // Đăng nhập
    public KhachHang login(String tenDangNhap, String matKhau) {
       String sql = "select * from KHACH_HANG where ten_dang_nhap = ? AND mat_khau = ? AND trang_thai = 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tenDangNhap);
            stmt.setString(2, matKhau);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToKhachHang(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Kiểm tra username có tồn tại không
    public boolean isUsernameExists(String tenDangNhap) {
        String sql = "select *from KHACH_HANG where ten_dang_nhap = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tenDangNhap);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Kiểm tra email có tồn tại không
    public boolean isEmailExists(String email) {
        String sql = "select * from KHACH_HANG where email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Đăng ký khách hàng mới
    public boolean register(KhachHang kh) {
        String sql = "insert into KHACH_HANG (ten_dang_nhap, mat_khau, ho_ten, email, so_dien_thoai, ngay_sinh, gioi_tinh)"
                + "values(?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, kh.getTenDangNhap());
            stmt.setString(2, kh.getMatKhau());
            stmt.setString(3, kh.getHoTen());
            stmt.setString(4, kh.getEmail());
            stmt.setString(5, kh.getSoDienThoai());
            if (kh.getNgaySinh() != null) {
                stmt.setDate(6, new java.sql.Date(kh.getNgaySinh().getTime()));
            } else {
                stmt.setNull(6, Types.DATE);
            }
            stmt.setString(7, kh.getGioiTinh());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy khách hàng theo ID
    public KhachHang getById(int maKhacHang) {
        String sql = "select * from KHACH_HANG where ma_khach_hang = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, maKhacHang);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToKhachHang(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Cập nhật thông tin khách hàng
    public boolean update(KhachHang kh) {
        String sql = "select KHACH_HANG set ho_ten = ?, email = ?, so_dien_thoai = ?, ngay_sinh = ?, gioi_tinh = ? "
                + "where ma_khach_hang = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, kh.getHoTen());
            stmt.setString(2, kh.getEmail());
            stmt.setString(3, kh.getSoDienThoai());
            if (kh.getNgaySinh() != null) {
                stmt.setDate(4, new java.sql.Date(kh.getNgaySinh().getTime()));
            } else {
                stmt.setNull(4, Types.DATE);
            }
            stmt.setString(5, kh.getGioiTinh());
            stmt.setInt(6, kh.getMaKhachHang());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
