package controller;

import java.io.IOException;
import java.util.List;

import dao.DanhMucDAO;
import dao.LoaiCaDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DanhMuc;
import model.LoaiCa;

public class HomePageServlet extends HttpServlet {

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
            action = "home";
        }
        try {
            switch (action) {
                case "home":
                    showHomePage(request, response);
                    break;
                case "search":
                    searchProducts(request, response);
                    break;
                case "category":
                    showProductsByCategory(request, response);
                    break;
                default:
                    showHomePage(request, response);
                    break;
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Hiển thị trang chủ với sản phẩm nổi bật và danh mục
     */
    private void showHomePage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Lấy tất cả sản phẩm nổi bật
            List<LoaiCa> featuredProducts = loaiCaDAO.getAll();

            // Lấy tất cả danh mục
            List<DanhMuc> categories = danhMucDAO.getAll();

            // Set attributes để truyền sang JSP
            request.setAttribute("featuredProducts", featuredProducts);
            request.setAttribute("categories", categories);
            request.setAttribute("pageTitle", "Trang chủ - FishStore");

            // Forward đến trang home.jsp
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/outside/home.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            // Xử lý lỗi và chuyển hướng đến trang lỗi
            request.setAttribute("errorMessage", "Có lỗi xảy ra khi tải trang chủ: " + e.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/error.jsp");
            dispatcher.forward(request, response);
        }
    }

    /**
     * Tìm kiếm sản phẩm theo từ khóa
     */
    private void searchProducts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("keyword");

        try {
            List<LoaiCa> searchResults;
            List<DanhMuc> categories = danhMucDAO.getAll();

            if (keyword != null && !keyword.trim().isEmpty()) {
                searchResults = loaiCaDAO.search(keyword.trim());
            } else {
                // Nếu không có từ khóa, hiển thị tất cả sản phẩm
                searchResults = loaiCaDAO.getAll();
            }

            request.setAttribute("searchResults", searchResults);
            request.setAttribute("categories", categories);
            request.setAttribute("keyword", keyword);
            request.setAttribute("pageTitle", "Tìm kiếm: " + keyword);
            request.setAttribute("resultCount", searchResults.size());

            // Forward đến trang product.jsp để hiển thị kết quả
            RequestDispatcher dispatcher = request.getRequestDispatcher("/product");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Có lỗi xảy ra khi tìm kiếm: " + e.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/error.jsp");
            dispatcher.forward(request, response);
        }
    }

    /**
     * Hiển thị sản phẩm theo danh mục
     */
    private void showProductsByCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String categoryIdParam = request.getParameter("categoryId");

        try {
            List<LoaiCa> products;
            List<DanhMuc> categories = danhMucDAO.getAll();
            DanhMuc currentCategory = null;

            if (categoryIdParam != null && !categoryIdParam.isEmpty()) {
                int categoryId = Integer.parseInt(categoryIdParam);
                products = loaiCaDAO.getByDanhMuc(categoryId);
                currentCategory = danhMucDAO.getById(categoryId);
            } else {
                // Nếu không có categoryId, hiển thị tất cả sản phẩm
                products = loaiCaDAO.getAll();
            }

            request.setAttribute("products", products);
            request.setAttribute("categories", categories);
            request.setAttribute("currentCategory", currentCategory);
            request.setAttribute("pageTitle",
                    currentCategory != null ? currentCategory.getTenDanhMuc() : "Tất cả sản phẩm");

            // Forward đến trang product.jsp
            RequestDispatcher dispatcher = request.getRequestDispatcher("/product");
            dispatcher.forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Danh mục không hợp lệ");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/error.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Có lỗi xảy ra khi tải sản phẩm: " + e.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/error.jsp");
            dispatcher.forward(request, response);
        }
    }
}
