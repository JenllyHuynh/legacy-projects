package controller;

import dao.GioHangDAO;
import dao.LoaiCaDAO;
import dao.DonHangDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.GioHang;
import model.KhachHang;
import model.LoaiCa;
import model.DonHang;

public class CartServlet extends HttpServlet {

    private GioHangDAO gioHangDAO;
    private LoaiCaDAO loaiCaDAO;
    private DonHangDAO donHangDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        gioHangDAO = new GioHangDAO();
        loaiCaDAO = new LoaiCaDAO();
        donHangDAO = new DonHangDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        KhachHang user = (KhachHang) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/views/authen/login.jsp");
            return;
        }

        try {
            if (action == null) {
                action = "view";
            }

            switch (action) {
                case "view":
                    viewCart(request, response, user);
                    break;
                case "checkout":
                    showCheckoutPage(request, response, user);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action không hợp lệ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi server: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        KhachHang user = (KhachHang) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/views/authen/login.jsp");
            return;
        }

        try {
            switch (action) {
                case "add":
                    addToCart(request, response, user, session);
                    break;
                case "update":
                    updateCart(request, response, user);
                    break;
                case "remove":
                    removeFromCart(request, response, user, session);
                    break;
                case "checkout":
                    processCheckout(request, response, user, session);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action không hợp lệ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Lỗi server: " + e.getMessage() + "\"}");
        }
    }

    private void viewCart(HttpServletRequest request, HttpServletResponse response, KhachHang user)
            throws ServletException, IOException {
        
        System.out.println("===== VIEW CART =====");
        System.out.println("User ID: " + user.getMaKhachHang());
        
        List<GioHang> cartItems = gioHangDAO.getByKhachHang(user.getMaKhachHang());
        System.out.println("Cart items: " + (cartItems != null ? cartItems.size() : "NULL"));
        
        double tongTien = calculateTotal(cartItems);
        
        request.setAttribute("cartItems", cartItems);
        request.setAttribute("tongTien", tongTien);
        request.getRequestDispatcher("/views/outside/cart.jsp").forward(request, response);
    }

    private void showCheckoutPage(HttpServletRequest request, HttpServletResponse response, KhachHang user)
            throws ServletException, IOException {
        
        List<GioHang> cartItems = gioHangDAO.getByKhachHang(user.getMaKhachHang());
        
        if (cartItems == null || cartItems.isEmpty()) {
            request.setAttribute("error", "Giỏ hàng trống!");
            response.sendRedirect(request.getContextPath() + "/cart?action=view");
            return;
        }
        
        double tongTienHang = calculateTotal(cartItems);
        double phiVanChuyen = calculateShipping(tongTienHang);
        double tongThanhToan = tongTienHang + phiVanChuyen;
        
        request.setAttribute("cartItems", cartItems);
        request.setAttribute("tongTienHang", tongTienHang);
        request.setAttribute("phiVanChuyen", phiVanChuyen);
        request.setAttribute("tongThanhToan", tongThanhToan);
        
        request.getRequestDispatcher("/views/outside/checkout.jsp").forward(request, response);
    }

    private void addToCart(HttpServletRequest request, HttpServletResponse response, KhachHang user, HttpSession session)
            throws IOException {

        String productIdParam = request.getParameter("productId");
        String quantityParam = request.getParameter("quantity");

        try {
            int productId = Integer.parseInt(productIdParam);
            double quantity = Double.parseDouble(quantityParam);

            LoaiCa product = loaiCaDAO.getById(productId);
            if (product == null) {
                sendJsonResponse(response, false, "Sản phẩm không tồn tại");
                return;
            }

            GioHang cartItem = new GioHang();
            cartItem.setKhachHang(user);
            cartItem.setLoaiCa(product);
            cartItem.setSoLuong(quantity);
            cartItem.setGiaTamTinh(product.getGiaBan() * quantity);

            boolean success = gioHangDAO.addToCart(cartItem);

            if (success) {
                List<GioHang> cartItems = gioHangDAO.getByKhachHang(user.getMaKhachHang());
                session.setAttribute("cartCount", cartItems.size());
                sendJsonResponse(response, true, "Đã thêm vào giỏ hàng", cartItems.size());
            } else {
                sendJsonResponse(response, false, "Không thể thêm vào giỏ hàng");
            }

        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "Dữ liệu không hợp lệ");
        }
    }

    private void processCheckout(HttpServletRequest request, HttpServletResponse response,
                                 KhachHang user, HttpSession session)
            throws IOException, ServletException {

        List<GioHang> cartItems = gioHangDAO.getByKhachHang(user.getMaKhachHang());
        if (cartItems == null || cartItems.isEmpty()) {
            request.setAttribute("error", "Giỏ hàng trống!");
            request.getRequestDispatcher("/views/outside/cart.jsp").forward(request, response);
            return;
        }

        String tenNguoiNhan = request.getParameter("tenNguoiNhan");
        String soDienThoai = request.getParameter("soDienThoaiNhan");
        String diaChi = request.getParameter("diaChiGiaoHang");
        String phuongThucThanhToan = request.getParameter("phuongThucThanhToan");

        if (tenNguoiNhan == null || tenNguoiNhan.trim().isEmpty() ||
            soDienThoai == null || soDienThoai.trim().isEmpty() ||
            diaChi == null || diaChi.trim().isEmpty()) {
            
            request.setAttribute("error", "Vui lòng điền đầy đủ thông tin!");
            showCheckoutPage(request, response, user);
            return;
        }

        double tongTienHang = calculateTotal(cartItems);
        double phiVanChuyen = calculateShipping(tongTienHang);
        double tongThanhToan = tongTienHang + phiVanChuyen;

        DonHang donHang = new DonHang();
        donHang.setKhachHang(user);
        donHang.setTenNguoiNhan(tenNguoiNhan.trim());
        donHang.setSoDienThoaiNhan(soDienThoai.trim());
        donHang.setDiaChiGiaoHang(diaChi.trim());
        donHang.setPhuongThucThanhToan(phuongThucThanhToan);
        donHang.setTongTienHang(tongTienHang);
        donHang.setPhiVanChuyen(phiVanChuyen);
        donHang.setTongThanhToan(tongThanhToan);
        donHang.setTrangThai("Chờ xác nhận");
        donHang.setMaDonHangHienThi("DH" + System.currentTimeMillis());

        boolean success = donHangDAO.createOrder(donHang, cartItems);

        if (success) {
            gioHangDAO.clearCart(user.getMaKhachHang());
            session.setAttribute("cartCount", 0);
            request.setAttribute("message", "Đặt hàng thành công!");
            request.setAttribute("maDonHang", donHang.getMaDonHangHienThi());
            request.getRequestDispatcher("/views/outside/home.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Không thể đặt hàng!");
            showCheckoutPage(request, response, user);
        }
    }

    private void updateCart(HttpServletRequest request, HttpServletResponse response, KhachHang user)
            throws IOException {
        
        String cartIdParam = request.getParameter("cartId");
        String quantityParam = request.getParameter("quantity");

        try {
            int cartId = Integer.parseInt(cartIdParam);
            double quantity = Double.parseDouble(quantityParam);

            if (quantity <= 0) {
                sendJsonResponse(response, false, "Số lượng phải lớn hơn 0");
                return;
            }

            boolean success = gioHangDAO.updateQuantity(cartId, quantity);

            if (success) {
                sendJsonResponse(response, true, "Đã cập nhật số lượng");
            } else {
                sendJsonResponse(response, false, "Không thể cập nhật số lượng");
            }

        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "Dữ liệu không hợp lệ");
        }
    }

    private void removeFromCart(HttpServletRequest request, HttpServletResponse response, 
                               KhachHang user, HttpSession session)
            throws IOException {
        
        String cartIdParam = request.getParameter("cartId");

        try {
            int cartId = Integer.parseInt(cartIdParam);
            boolean success = gioHangDAO.removeFromCart(cartId);

            if (success) {
                List<GioHang> cartItems = gioHangDAO.getByKhachHang(user.getMaKhachHang());
                session.setAttribute("cartCount", cartItems.size());
                sendJsonResponse(response, true, "Đã xóa sản phẩm", cartItems.size());
            } else {
                sendJsonResponse(response, false, "Không thể xóa sản phẩm");
            }

        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "Dữ liệu không hợp lệ");
        }
    }

    private void sendJsonResponse(HttpServletResponse response, boolean success, String message)
            throws IOException {
        sendJsonResponse(response, success, message, 0);
    }

    private void sendJsonResponse(HttpServletResponse response, boolean success, String message, int cartCount)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String json = String.format("{\"success\": %b, \"message\": \"%s\", \"cartCount\": %d}",
                success, message, cartCount);
        out.print(json);
        out.flush();
    }

    private double calculateTotal(List<GioHang> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return 0;
        }
        return cartItems.stream().mapToDouble(GioHang::getGiaTamTinh).sum();
    }

    private double calculateShipping(double total) {
        return total >= 500000 ? 0 : 30000;
    }
}