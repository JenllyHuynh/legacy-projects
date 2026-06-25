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

public class AuthFilter implements Filter {
    
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

        // === CÁC TRANG ĐƯỢC TRUY CẬP KHÔNG CẦN ĐĂNG NHẬP ===
        if (isPublicResource(path) || isPublicPage(path) || isAIService(path)) {
            chain.doFilter(request, response);
            return;
        }

        // === KIỂM TRA ĐĂNG NHẬP CHO CÁC TRANG CẦN XÁC THỰC ===
        HttpSession session = httpRequest.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            // Đã đăng nhập -> cho phép truy cập
            chain.doFilter(request, response);
        } else {
            // Chưa đăng nhập -> chuyển hướng đến trang đăng nhập
            httpResponse.sendRedirect(contextPath + "/views/authen/login.jsp");
        }
    }

    /**
     * Kiểm tra xem có phải là tài nguyên tĩnh không
     */
    private boolean isPublicResource(String path) {
        return path.startsWith("/assets/") ||
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/images/") ||
               path.endsWith(".css") ||
               path.endsWith(".js") ||
               path.endsWith(".png") ||
               path.endsWith(".jpg") ||
               path.endsWith(".jpeg") ||
               path.endsWith(".gif") ||
               path.endsWith(".ico") ||
               path.endsWith(".woff") ||
               path.endsWith(".woff2") ||
               path.endsWith(".ttf");
    }

    /**
     * Kiểm tra xem có phải là trang công khai không
     */
    private boolean isPublicPage(String path) {
        return path.equals("/") ||
               path.equals("/home") ||
               path.startsWith("/home") ||
               path.startsWith("/views/outside/") ||
               path.startsWith("/views/authen/") ||
               path.startsWith("/authen") ||
               path.contains("login") ||
               path.contains("register") ||
               path.contains("logout");
    }

    /**
     * Kiểm tra xem có phải là service AI
     */
    private boolean isAIService(String path) {
        return path.startsWith("/ai-chat") ||
               path.contains("ai-chat") ||
               path.contains("gemini") ||
               path.contains("ai") ||
               path.contains("chatbot");
    }

    @Override
    public void destroy() {
        // Dọn dẹp filter
    }
}