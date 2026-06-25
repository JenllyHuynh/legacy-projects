package filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AdminFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Khởi tạo filter
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String contextPath = httpRequest.getContextPath();
        String uri = httpRequest.getRequestURI();
        String path = uri.substring(contextPath.length());

        // === KIỂM TRA QUYỀN TRUY CẬP ADMIN ===
        HttpSession session = httpRequest.getSession(false);
        
        if (session != null && 
            session.getAttribute("username") != null && 
            isAdmin(session.getAttribute("role"))) {
            // Có quyền admin -> cho phép truy cập
            chain.doFilter(request, response);
        } else {
            // Không có quyền -> chuyển hướng đến trang lỗi hoặc trang chủ
            httpResponse.sendRedirect(contextPath + "/views/error/access-denied.jsp");
        }
    }

    /**
     * Kiểm tra xem user có phải là Admin hoặc Nhân viên không
     */
    private boolean isAdmin(Object role) {
        if (role == null) return false;
        
        String roleStr = role.toString();
        return "Admin".equals(roleStr) || "Nhân viên".equals(roleStr);
    }

    @Override
    public void destroy() {
        // Dọn dẹp filter
    }
}