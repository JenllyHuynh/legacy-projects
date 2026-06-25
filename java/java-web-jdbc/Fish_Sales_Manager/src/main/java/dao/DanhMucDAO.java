package dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.sql.ResultSet;
import model.DanhMuc;

public class DanhMucDAO extends util.DBContext {

    // Map ResultSet cho đối tượng Danh Mục
    private DanhMuc mapResultSetToDanhMuc(ResultSet rs) throws SQLException {
        DanhMuc dm = new DanhMuc();
        dm.setMaDanhMuc(rs.getInt("ma_danh_muc"));
        dm.setTenDanhMuc(rs.getString("ten_danh_muc"));
        dm.setMoTa(rs.getString("mo_ta"));
        dm.setTrangThai(rs.getBoolean("trang_thai"));
        return dm;
    }

    // Lấy tất cả danh mục sản phẩm
    public List<DanhMuc> getAll() {
        List<DanhMuc> list = new ArrayList<>();
        String sql = "select * from DANH_MUC where trang_thai = 1 order by ten_danh_muc";
        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToDanhMuc(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy danh mục theo Id
    public DanhMuc getById(int maDanhMuc) {
        String sql = "select * from DANH_MUC where ma_danh_muc = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, maDanhMuc);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToDanhMuc(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

//    // Thêm danh mục mới
//    public boolean add(DanhMuc dm) {
//        String sql = "insert into DANH_MUC(ten_danh_muc, mo_ta) VALUES (?, ?)";
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setString(1, dm.getTenDanhMuc());
//            stmt.setString(2, dm.getMoTa());
//
//            return stmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    // Cập nhật danh mục
//    public boolean update(DanhMuc dm) {
//        String sql = "update DANH_MUC set ten_danh_muc = ?, mo_ta = ? WHERE ma_danh_muc = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setString(1, dm.getTenDanhMuc());
//            stmt.setString(2, dm.getMoTa());
//            stmt.setInt(3, dm.getMaDanhMuc());
//
//            return stmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

//    // Xóa danh mục
//    public boolean delete(int maDanhMuc) {
//        String sql = "update DANH_MUC SET trang_thai = 0 where ma_danh_muc = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setInt(1, maDanhMuc);
//            return stmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
}
