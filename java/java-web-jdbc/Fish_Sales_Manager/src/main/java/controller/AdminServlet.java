package controller;

import java.io.IOException;

import dao.DanhMucDAO;
import dao.LoaiCaDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.DanhMuc;
import model.LoaiCa;

public class AdminServlet extends HttpServlet {

    private LoaiCaDAO loaiCaDAO;
    private DanhMucDAO danhMucDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        loaiCaDAO = new LoaiCaDAO();
        danhMucDAO = new DanhMucDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        try {
            switch (action) {
                case "list":
                    listProducts(request, response);
                    break;
                case "add":
                    showAddForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteProduct(request, response);
                    break;
                default:
                    listProducts(request, response);
                    break;
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        String action = request.getParameter("action");
//        if (action == null) {
//            action = "list"; // Hoặc có thể đổi thành "dashboard" nếu muốn
//        }
//        try {
//            switch (action) {
//                case "dashboard":
//                    // Chuyển hướng đến trang dashboard
//                    RequestDispatcher dispatcher = request.getRequestDispatcher("/views/admin/dashboard.jsp");
//                    dispatcher.forward(request, response);
//                    break;
//                case "list":
//                    listProducts(request, response);
//                    break;
//                case "add":
//                    showAddForm(request, response);
//                    break;
//                case "edit":
//                    showEditForm(request, response);
//                    break;
//                case "delete":
//                    deleteProduct(request, response);
//                    break;
//                default:
//                    listProducts(request, response);
//                    break;
//            }
//        } catch (Exception e) {
//            throw new ServletException(e);
//        }
//    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            switch (action) {
                case "add":
                    addProduct(request, response);
                    break;
                case "update":
                    updateProduct(request, response);
                    break;
                default:
                    listProducts(request, response);
                    break;
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void listProducts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<LoaiCa> products = loaiCaDAO.getAll();
        request.setAttribute("products", products);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/admin/manage-products.jsp");
        dispatcher.forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<DanhMuc> categories = danhMucDAO.getAll();
            request.setAttribute("categories", categories);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/admin/add-product.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            System.err.println("❌ Lỗi showAddForm: " + e.getMessage());
            request.setAttribute("error", "Không thể tải form thêm sản phẩm: " + e.getMessage());
            listProducts(request, response);
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int productId = Integer.parseInt(request.getParameter("id"));
            LoaiCa product = loaiCaDAO.getById(productId);

            if (product == null) {
                request.setAttribute("error", "Không tìm thấy sản phẩm với ID: " + productId);
                listProducts(request, response);
                return;
            }

            List<DanhMuc> categories = danhMucDAO.getAll();
            request.setAttribute("product", product);
            request.setAttribute("categories", categories);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/admin/edit-product.jsp");
            dispatcher.forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID sản phẩm không hợp lệ");
            listProducts(request, response);
        } catch (Exception e) {
            System.err.println("❌ Lỗi showEditForm: " + e.getMessage());
            request.setAttribute("error", "Không thể tải form sửa sản phẩm: " + e.getMessage());
            listProducts(request, response);
        }
    }

    private void addProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Validation cơ bản
            String tenLoaiCa = request.getParameter("tenLoaiCa");
            String maDanhMucStr = request.getParameter("maDanhMuc");
            String giaBanStr = request.getParameter("giaBan");

            if (tenLoaiCa == null || tenLoaiCa.trim().isEmpty()
                    || maDanhMucStr == null || maDanhMucStr.trim().isEmpty()
                    || giaBanStr == null || giaBanStr.trim().isEmpty()) {

                request.setAttribute("error", "Vui lòng điền đầy đủ thông tin bắt buộc");
                showAddForm(request, response);
                return;
            }

            LoaiCa product = new LoaiCa();

            DanhMuc danhMuc = new DanhMuc();
            danhMuc.setMaDanhMuc(Integer.parseInt(maDanhMucStr));
            product.setDanhMuc(danhMuc);

            product.setTenLoaiCa(tenLoaiCa.trim());
            product.setMoTa(request.getParameter("moTa"));
            product.setDonViTinh(request.getParameter("donViTinh"));
            product.setGiaBan(Double.parseDouble(giaBanStr));
            product.setHinhAnh(request.getParameter("hinhAnh"));

            boolean success = loaiCaDAO.add(product);

            if (success) {
                request.setAttribute("message", "✅ Thêm sản phẩm thành công!");
            } else {
                request.setAttribute("error", "❌ Thêm sản phẩm thất bại!");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "❌ Giá bán hoặc danh mục không hợp lệ!");
            showAddForm(request, response);
            return;
        } catch (Exception e) {
            System.err.println("❌ Lỗi addProduct: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "❌ Lỗi hệ thống: " + e.getMessage());
            showAddForm(request, response);
            return;
        }

        // Quay lại trang danh sách
        listProducts(request, response);
    }

    private void updateProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Validation
            String maLoaiCaStr = request.getParameter("maLoaiCa");
            String tenLoaiCa = request.getParameter("tenLoaiCa");
            String maDanhMucStr = request.getParameter("maDanhMuc");
            String giaBanStr = request.getParameter("giaBan");

            if (maLoaiCaStr == null || maLoaiCaStr.trim().isEmpty()
                    || tenLoaiCa == null || tenLoaiCa.trim().isEmpty()
                    || maDanhMucStr == null || maDanhMucStr.trim().isEmpty()
                    || giaBanStr == null || giaBanStr.trim().isEmpty()) {

                request.setAttribute("error", "Vui lòng điền đầy đủ thông tin bắt buộc");
                listProducts(request, response);
                return;
            }

            LoaiCa product = new LoaiCa();
            product.setMaLoaiCa(Integer.parseInt(maLoaiCaStr));

            DanhMuc danhMuc = new DanhMuc();
            danhMuc.setMaDanhMuc(Integer.parseInt(maDanhMucStr));
            product.setDanhMuc(danhMuc);

            product.setTenLoaiCa(tenLoaiCa.trim());
            product.setMoTa(request.getParameter("moTa"));
            product.setDonViTinh(request.getParameter("donViTinh"));
            product.setGiaBan(Double.parseDouble(giaBanStr));
            product.setHinhAnh(request.getParameter("hinhAnh"));

            boolean success = loaiCaDAO.update(product);

            if (success) {
                request.setAttribute("message", "✅ Cập nhật sản phẩm thành công!");
            } else {
                request.setAttribute("error", "❌ Cập nhật sản phẩm thất bại!");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "❌ Dữ liệu số không hợp lệ!");
        } catch (Exception e) {
            System.err.println("❌ Lỗi updateProduct: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "❌ Lỗi hệ thống: " + e.getMessage());
        }

        listProducts(request, response);
    }

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int productId = Integer.parseInt(request.getParameter("id"));
            boolean success = loaiCaDAO.delete(productId);

            if (success) {
                request.setAttribute("message", "Xóa sản phẩm thành công!");
            } else {
                request.setAttribute("error", "Xóa sản phẩm thất bại!");
            }

        } catch (Exception e) {
            request.setAttribute("error", "Lỗi: " + e.getMessage());
        }

        listProducts(request, response);
    }
}
