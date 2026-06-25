package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.GioHang;
import model.KhachHang;
import model.LoaiCa;

public class GioHangDAO extends util.DBContext {

    // Map ResultSet cho đối tượng giỏ hàng
    private GioHang mapResultSetToGioHang(ResultSet rs) throws SQLException {
        GioHang gh = new GioHang();
        gh.setMaGioHang(rs.getInt("ma_gio_hang"));

        KhachHang kh = new KhachHang();
        kh.setMaKhachHang(rs.getInt("ma_khach_hang"));
        gh.setKhachHang(kh);

        LoaiCa lc = new LoaiCa();
        lc.setMaLoaiCa(rs.getInt("ma_loai_ca"));
        lc.setTenLoaiCa(rs.getString("ten_loai_ca"));
        lc.setGiaBan(rs.getDouble("gia_ban"));
        lc.setHinhAnh(rs.getString("hinh_anh"));
        lc.setDonViTinh(rs.getString("don_vi_tinh"));
        gh.setLoaiCa(lc);

        gh.setSoLuong(rs.getDouble("so_luong"));
        gh.setGiaTamTinh(rs.getDouble("gia_tam_tinh"));
        gh.setNgayTao(rs.getTimestamp("ngay_tao"));

        return gh;
    }

    // Lấy giỏ hàng của khách hàng
    public List<GioHang> getByKhachHang(int maKhachHang) {
        List<GioHang> list = new ArrayList<>();
        String sql = "SELECT gh.*, lc.ten_loai_ca, lc.gia_ban, lc.hinh_anh, lc.don_vi_tinh "
                + "FROM GIO_HANG gh "
                + "INNER JOIN LOAI_CA lc ON gh.ma_loai_ca = lc.ma_loai_ca "
                + "WHERE gh.ma_khach_hang = ? "
                + "ORDER BY gh.ngay_tao DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, maKhachHang);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToGioHang(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm sản phẩm vào giỏ hàng
    public boolean addToCart(GioHang gh) {
        // Kiểm tra xem sản phẩm đã có trong giỏ chưa
        String checkSql = "SELECT * FROM GIO_HANG WHERE ma_khach_hang = ? AND ma_loai_ca = ?";
        String insertSql = "INSERT INTO GIO_HANG (ma_khach_hang, ma_loai_ca, so_luong, gia_tam_tinh) VALUES (?, ?, ?, ?)";
        String updateSql = "UPDATE GIO_HANG SET so_luong = so_luong + ?, gia_tam_tinh = gia_tam_tinh + ? WHERE ma_khach_hang = ? AND ma_loai_ca = ?";

        try {
            // Kiểm tra sản phẩm đã tồn tại chưa
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setInt(1, gh.getKhachHang().getMaKhachHang());
            checkStmt.setInt(2, gh.getLoaiCa().getMaLoaiCa());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Đã có -> cập nhật số lượng
                PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                updateStmt.setDouble(1, gh.getSoLuong());
                updateStmt.setDouble(2, gh.getGiaTamTinh());
                updateStmt.setInt(3, gh.getKhachHang().getMaKhachHang());
                updateStmt.setInt(4, gh.getLoaiCa().getMaLoaiCa());
                return updateStmt.executeUpdate() > 0;
            } else {
                // Chưa có -> thêm mới
                PreparedStatement insertStmt = connection.prepareStatement(insertSql);
                insertStmt.setInt(1, gh.getKhachHang().getMaKhachHang());
                insertStmt.setInt(2, gh.getLoaiCa().getMaLoaiCa());
                insertStmt.setDouble(3, gh.getSoLuong());
                insertStmt.setDouble(4, gh.getGiaTamTinh());
                return insertStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    public boolean updateQuantity(int maGioHang, double soLuong) {
        String sql = "UPDATE GIO_HANG SET so_luong = ?, gia_tam_tinh = ? WHERE ma_gio_hang = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Lấy giá sản phẩm để tính lại giá tạm tính
            String priceSql = "SELECT lc.gia_ban FROM GIO_HANG gh "
                    + "INNER JOIN LOAI_CA lc ON gh.ma_loai_ca = lc.ma_loai_ca "
                    + "WHERE gh.ma_gio_hang = ?";
            PreparedStatement priceStmt = connection.prepareStatement(priceSql);
            priceStmt.setInt(1, maGioHang);
            ResultSet rs = priceStmt.executeQuery();

            if (rs.next()) {
                double giaBan = rs.getDouble("gia_ban");
                double giaTamTinh = giaBan * soLuong;

                stmt.setDouble(1, soLuong);
                stmt.setDouble(2, giaTamTinh);
                stmt.setInt(3, maGioHang);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa sản phẩm khỏi giỏ hàng
    public boolean removeFromCart(int maGioHang) {
        String sql = "DELETE FROM GIO_HANG WHERE ma_gio_hang = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, maGioHang);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa toàn bộ giỏ hàng của khách hàng
    public boolean clearCart(int maKhachHang) {
        String sql = "DELETE FROM GIO_HANG WHERE ma_khach_hang = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, maKhachHang);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
