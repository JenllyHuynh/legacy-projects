package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.DanhMuc;
import model.LoaiCa;

public class LoaiCaDAO extends util.DBContext {

    // Map ResultSet cho đối tượng Loại cá
    private LoaiCa mapResultSetToLoaiCa(ResultSet rs) throws SQLException {
        LoaiCa lc = new LoaiCa();
        lc.setMaLoaiCa(rs.getInt("ma_loai_ca"));

        DanhMuc dm = new DanhMuc();
        dm.setMaDanhMuc(rs.getInt("ma_danh_muc"));
        dm.setTenDanhMuc(rs.getString("ten_danh_muc"));
        lc.setDanhMuc(dm);

        lc.setTenLoaiCa(rs.getString("ten_loai_ca"));
        lc.setMoTa(rs.getString("mo_ta"));
        lc.setDonViTinh(rs.getString("don_vi_tinh"));
        lc.setGiaBan(rs.getDouble("gia_ban"));
        lc.setHinhAnh(rs.getString("hinh_anh"));
        lc.setNgayTao(rs.getTimestamp("ngay_tao"));
        lc.setTrangThai(rs.getBoolean("trang_thai"));
        return lc;
    }

    // Lấy tất cả loại cá
    public List<LoaiCa> getAll() {
        List<LoaiCa> list = new ArrayList<>();
        String sql = "SELECT lc.*, dm.ten_danh_muc "
                + "FROM LOAI_CA lc "
                + "INNER JOIN DANH_MUC dm ON lc.ma_danh_muc = dm.ma_danh_muc "
                + "WHERE lc.trang_thai = 1 ORDER BY lc.ngay_tao DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToLoaiCa(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy loại cá theo Id
    public LoaiCa getById(int maLoaiCa) {
        String sql = "SELECT lc.*, dm.ten_danh_muc "
                + "FROM LOAI_CA lc "
                + "INNER JOIN DANH_MUC dm ON lc.ma_danh_muc = dm.ma_danh_muc "
                + "WHERE lc.ma_loai_ca = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, maLoaiCa);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToLoaiCa(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy loại cá theo danh mục
    public List<LoaiCa> getByDanhMuc(int maDanhMuc) {
        List<LoaiCa> list = new ArrayList<>();
        String sql = "SELECT lc.*, dm.ten_danh_muc "
                + "FROM LOAI_CA lc "
                + "INNER JOIN DANH_MUC dm ON lc.ma_danh_muc = dm.ma_danh_muc "
                + "WHERE lc.ma_danh_muc = ? AND lc.trang_thai = 1 "
                + "ORDER BY lc.ten_loai_ca";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, maDanhMuc);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToLoaiCa(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Tìm kiếm loại cá
    public List<LoaiCa> search(String keyword) {
        List<LoaiCa> list = new ArrayList<>();
        String sql = "SELECT lc.*, dm.ten_danh_muc "
                + "FROM LOAI_CA lc "
                + "INNER JOIN DANH_MUC dm ON lc.ma_danh_muc = dm.ma_danh_muc "
                + "WHERE (lc.ten_loai_ca LIKE ? OR lc.mo_ta LIKE ? OR dm.ten_danh_muc LIKE ?) "
                + "AND lc.trang_thai = 1 "
                + "ORDER BY lc.ten_loai_ca";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String searchTerm = "%" + keyword + "%";
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            stmt.setString(3, searchTerm);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToLoaiCa(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm loại cá mới
    public boolean add(LoaiCa lc) {
        String sql = "INSERT INTO LOAI_CA (ma_danh_muc, ten_loai_ca, mo_ta, don_vi_tinh, gia_ban, hinh_anh) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, lc.getDanhMuc().getMaDanhMuc());
            stmt.setString(2, lc.getTenLoaiCa());
            stmt.setString(3, lc.getMoTa());
            stmt.setString(4, lc.getDonViTinh());
            stmt.setDouble(5, lc.getGiaBan());
            stmt.setString(6, lc.getHinhAnh());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật loại cá
    public boolean update(LoaiCa lc) {
        String sql = "UPDATE LOAI_CA SET ma_danh_muc = ?, ten_loai_ca = ?, mo_ta = ?, "
                + "don_vi_tinh = ?, gia_ban = ?, hinh_anh = ? WHERE ma_loai_ca = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, lc.getDanhMuc().getMaDanhMuc());
            stmt.setString(2, lc.getTenLoaiCa());
            stmt.setString(3, lc.getMoTa());
            stmt.setString(4, lc.getDonViTinh());
            stmt.setDouble(5, lc.getGiaBan());
            stmt.setString(6, lc.getHinhAnh());
            stmt.setInt(7, lc.getMaLoaiCa());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa loại cá
    public boolean delete(int maLoaiCa) {
        String sql = "delete LOAI_CA WHERE ma_loai_ca = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, maLoaiCa);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
