package controller;

import dao.LoaiCaDAO;
import dao.DanhMucDAO;
import model.LoaiCa;
import model.DanhMuc;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class ProductServlet extends HttpServlet {

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

        String categoryParam = request.getParameter("category");
        String keyword = request.getParameter("keyword");

        List<LoaiCa> products = null;
        DanhMuc currentCategory = null;

        try {
            // Tìm kiếm theo keyword
            if (keyword != null && !keyword.trim().isEmpty()) {
                products = loaiCaDAO.search(keyword.trim());
                request.setAttribute("keyword", keyword);
                request.setAttribute("resultCount", products != null ? products.size() : 0);
            } 
            // Lọc theo category
            else if (categoryParam != null && !categoryParam.isEmpty()) {
                try {
                    int categoryId = Integer.parseInt(categoryParam);
                    products = loaiCaDAO.getByDanhMuc(categoryId);
                    currentCategory = danhMucDAO.getById(categoryId);
                    request.setAttribute("currentCategory", currentCategory);
                } catch (NumberFormatException e) {
                    products = loaiCaDAO.getAll();
                }
            } 
            // Hiển thị tất cả
            else {
                products = loaiCaDAO.getAll();
            }

            // Set attributes
            request.setAttribute("products", products);
            request.setAttribute("categories", danhMucDAO.getAll());

            // Page title
            String pageTitle = "Sản phẩm - FishStore";
            if (currentCategory != null) {
                pageTitle = currentCategory.getTenDanhMuc() + " - FishStore";
            } else if (keyword != null && !keyword.trim().isEmpty()) {
                pageTitle = "Tìm kiếm: " + keyword + " - FishStore";
            }
            request.setAttribute("pageTitle", pageTitle);

            // Forward to JSP
            RequestDispatcher rd = request.getRequestDispatcher("/views/outside/product.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            RequestDispatcher rd = request.getRequestDispatcher("/views/error.jsp");
            rd.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}