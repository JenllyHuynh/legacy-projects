package controller;

import dao.KhachHangDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.KhachHang;

public class AuthenServlet extends HttpServlet {

    private KhachHangDAO khachHangDAO;

    @Override
    public void init() throws ServletException {
        khachHangDAO = new KhachHangDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("logout".equals(action)) {
            // Xử lý đăng xuất
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        
        if ("register".equals(action)) {
            // Hiển thị trang đăng ký
            request.getRequestDispatcher("/views/authen/register.jsp").forward(request, response);
            return;
        }
        
        // Mặc định hiển thị trang đăng nhập
        request.getRequestDispatcher("/views/authen/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("register".equals(action)) {
            register(request, response);
        } else {
            login(request, response);
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String remember = request.getParameter("remember");
        
        // Kiểm tra dữ liệu đầu vào
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ thông tin!");
            request.getRequestDispatcher("/views/authen/login.jsp").forward(request, response);
            return;
        }
        
        // Thực hiện đăng nhập
        KhachHang kh = khachHangDAO.login(username, password);
        
        if (kh == null) {
            request.setAttribute("error", "Sai tên đăng nhập hoặc mật khẩu!");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/views/authen/login.jsp").forward(request, response);
        } else {
            // Tạo session
            HttpSession session = request.getSession();
            session.setAttribute("user", kh);
            session.setAttribute("username", kh.getTenDangNhap());
            session.setAttribute("role", kh.getVaiTro());
            session.setAttribute("userId", kh.getMaKhachHang());
            
            // Xử lý remember me
            if ("on".equals(remember)) {
                Cookie userCookie = new Cookie("rememberUser", username);
                userCookie.setMaxAge(30 * 24 * 60 * 60); // 30 ngày
                userCookie.setPath("/");
                response.addCookie(userCookie);
            }
            
            // Chuyển hướng theo vai trò
            String vaiTro = kh.getVaiTro();
            if ("Admin".equalsIgnoreCase(vaiTro) || "Nhân viên".equalsIgnoreCase(vaiTro)) {
                response.sendRedirect(request.getContextPath() + "/admin");
            } else {
                response.sendRedirect(request.getContextPath() + "/home");
            }
        }
    }

    private void register(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String hoTen = request.getParameter("hoTen");
        String email = request.getParameter("email");
        String soDienThoai = request.getParameter("soDienThoai");
        
        // Kiểm tra dữ liệu bắt buộc
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            hoTen == null || hoTen.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {
            
            request.setAttribute("error", "Vui lòng nhập đầy đủ thông tin bắt buộc!");
            setRegistrationAttributes(request, username, hoTen, email, soDienThoai);
            request.getRequestDispatcher("/views/authen/register.jsp").forward(request, response);
            return;
        }
        
        // Kiểm tra mật khẩu xác nhận
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp!");
            setRegistrationAttributes(request, username, hoTen, email, soDienThoai);
            request.getRequestDispatcher("/views/authen/register.jsp").forward(request, response);
            return;
        }
        
        // Kiểm tra username đã tồn tại
        if (khachHangDAO.isUsernameExists(username)) {
            request.setAttribute("error", "Tên đăng nhập đã tồn tại!");
            setRegistrationAttributes(request, username, hoTen, email, soDienThoai);
            request.getRequestDispatcher("/views/authen/register.jsp").forward(request, response);
            return;
        }
        
        // Kiểm tra email đã tồn tại
        if (khachHangDAO.isEmailExists(email)) {
            request.setAttribute("error", "Email đã được sử dụng!");
            setRegistrationAttributes(request, username, hoTen, email, soDienThoai);
            request.getRequestDispatcher("/views/authen/register.jsp").forward(request, response);
            return;
        }
        
        // Tạo tài khoản mới
        KhachHang newUser = new KhachHang();
        newUser.setTenDangNhap(username);
        newUser.setMatKhau(password);
        newUser.setHoTen(hoTen);
        newUser.setEmail(email);
        newUser.setSoDienThoai(soDienThoai);
        newUser.setVaiTro("Khách hàng");
        
        boolean success = khachHangDAO.register(newUser);
        
        if (success) {
            request.setAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            request.getRequestDispatcher("/views/authen/login.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Đăng ký thất bại! Vui lòng thử lại.");
            setRegistrationAttributes(request, username, hoTen, email, soDienThoai);
            request.getRequestDispatcher("/views/authen/register.jsp").forward(request, response);
        }
    }
    
    private void setRegistrationAttributes(HttpServletRequest request, String username, 
                                         String hoTen, String email, String soDienThoai) {
        request.setAttribute("username", username);
        request.setAttribute("hoTen", hoTen);
        request.setAttribute("email", email);
        request.setAttribute("soDienThoai", soDienThoai);
    }
}