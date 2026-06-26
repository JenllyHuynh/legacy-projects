package vn.edu.fpt.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import vn.edu.fpt.security.CustomOAuth2UserService;
import vn.edu.fpt.security.CustomStaffUserDetailsService;
import vn.edu.fpt.security.CustomUserDetailsService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * SecurityConfig — Cấu hình toàn bộ bảo mật cho ứng dụng
 * Xử lý 3 loại login:
 * 1. Form login (Customer)   -> CustomUserDetailsService
 * 2. Form login (Staff/Admin) -> CustomStaffUserDetailsService
 * 3. Google OAuth2           -> CustomOAuth2UserService + oidcUserService (OIDC)
 * Phân quyền route:
 * - Public          : /, /home/**, /event/**, /auth/**, static
 * - USER role       : /user/**
 * - STAFF/ADMIN     : /staff/**
 * - ADMIN only      : /admin/**
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customerDetailsService;
    @Autowired
    private CustomStaffUserDetailsService staffDetailsService;
    @Autowired
    private CustomOAuth2UserService oAuth2UserService;

    // PasswordEncoder KHÔNG khai báo ở đây nữa - nằm trong PasswordEncoderConfig.java
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Provider cho Customer
    @Bean
    public DaoAuthenticationProvider customerAuthProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(customerDetailsService);
        p.setPasswordEncoder(passwordEncoder);
        return p;
    }

    // Provider cho Staff/Admin
    @Bean
    public DaoAuthenticationProvider staffAuthProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(staffDetailsService);
        p.setPasswordEncoder(passwordEncoder);
        return p;
    }

    // AuthenticationManager: thử Customer trước, rồi Staff
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(customerAuthProvider(), staffAuthProvider()));
    }

    // Redirect sau khi login thành công theo role
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new SavedRequestAwareAuthenticationSuccessHandler() {

            @Override
            protected String determineTargetUrl(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Authentication authentication) {

                var authorities = authentication.getAuthorities();

                boolean isAdmin = authorities.stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                boolean isStaff = authorities.stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"));
                boolean isUser = authorities.stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));

                // ✅ LẤY URL TỪ SPRING (QUAN TRỌNG)
                String targetUrl = super.determineTargetUrl(request, response, authentication);
                System.out.println("Spring targetUrl: " + targetUrl);
                // 👉 Nếu URL hợp lệ với role → dùng luôn
                if (isValidUrlForRole(targetUrl, isAdmin, isStaff, isUser)) {
                    return targetUrl;
                }

                // 👉 fallback theo role
                if (isAdmin) return "/admin/system-revenue";
                if (isStaff) return "/staff";
                if (isUser) return "/home";

                return "/home";
            }
        };
    }

    private boolean isValidUrlForRole(String url, boolean isAdmin, boolean isStaff, boolean isUser) {
        if (url == null) return false;

        if (isAdmin) return true;
        if (isStaff) return url.contains("/staff");
        if (isUser) return !url.contains("/admin") && !url.contains("/staff");

        return false;
    }

    /**
     * FIX: Google dùng OIDC nên Spring Security gọi OidcUserService thay vì DefaultOAuth2UserService.
     * Bean này wrap OidcUserService để inject ROLE_USER vào principal sau khi login Google thành công.
     */
    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        OidcUserService delegate = new OidcUserService();
        return (userRequest) -> {
            // Gọi OidcUserService mặc định để load user từ Google
            OidcUser oidcUser = delegate.loadUser(userRequest);

            // Đồng bộ user vào DB (Customer + UserSocialLogin)
            // NOTE: Google thực tế chạy OIDC nên nếu không sync ở đây thì user mới sẽ không có trong DB.
            Map<String, Object> attrs = oidcUser.getAttributes();
            String email = (String) attrs.get("email");
            String fullName = (String) attrs.get("name");
            String avatar = (String) attrs.get("picture");
            String provider = userRequest.getClientRegistration().getRegistrationId();
            String providerKey = (String) attrs.get("sub");
            oAuth2UserService.syncUser(provider, providerKey, email, fullName, avatar);

            // Giữ lại tất cả authorities hiện có, thêm ROLE_USER vào
            Set<GrantedAuthority> authorities = new HashSet<>(oidcUser.getAuthorities());
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            System.out.println("=== OIDC User loaded ===");
            System.out.println("Email: " + oidcUser.getEmail());
            System.out.println("Authorities after fix: " + authorities);

            // Trả về DefaultOidcUser với authorities đã được bổ sung
            return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
        };
    }

    // Filter Chain
    @Bean
    @Profile("prod")
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.ignoringRequestMatchers(
                        // VNPAY
                        "/payment-ipn", "/auth/**",        // Bỏ qua CSRF cho auth
                        "/api/**",         // Bỏ qua cho API
                        "/ai-chat/**",     // Bỏ qua cho AI Chat
                        "/search/**",      // Bỏ qua cho search
                        "/images/**",      // Bỏ qua cho img
                        "/upload/**", "/user/profile/**" // Cho phép POST/PUT từ profile
                ))

                .authorizeHttpRequests(auth -> auth
                        //  Public: ai cũng vào được
                        .requestMatchers(
                                // VNPAY
                                "/payment-ipn", "/payment-return",
                                // Trang chủ
                                "/", "/home", "/home/**",

                                // Trang auth (login, reg, forgot password )
                                "/auth/**", "/auth/register", "/auth/register/**", "/auth/save", "/auth/save/**",

                                // Trang public
                                "/public/**",

                                // Search
                                "/search", "/search/**",

                                // Static resources
                                "/css/**", "/js/**", "/images/**", "/fonts/**", "/bootstrap/**", "/favicon.ico", "/actuator/**",
                                // Update thư mục ảnh
                                "/upload/**",

                                // OAuth2 của gg mail
                                "/oauth2/**", "/login/**",

                                // Của anh bạn A.i
                                "/ai-chat/**", "/ai-chat/send", "/ai-chat/history", "/ai-chat/auth-status", "/fragment/**").permitAll()

                        // PUBLIC API - Guest có thể dùng
                        .requestMatchers(
                                // UC-01: Browse Events - Xem danh sách sự kiện
                                "/event", "/event/**", "/api/events", "/api/events/**",

                                // UC-02: Search Events - Tìm kiếm
                                "/api/events/search", "/api/events/search/**",

                                // UC-03: Filter Events - Lọc
                                "/api/events/filter", "/api/events/filter/**",

                                // UC-04: View Event Details - Xem chi tiết
                                "/api/events/*/details", "/api/events/*/view",

                                // UC-06: View AI Recommendations
                                "/api/events/recommendations", "/api/events/recommendations/**",

                                // Categories và Venues (dùng cho filter)
                                "/api/categories", "/api/categories/**", "/api/venues", "/api/venues/**",

                                // Comments - chỉ xem
                                "/api/events/*/comments", "/api/comments/event/*",

                                // Images
                                "/images/**", "/api/images/**", "/upload/**").permitAll()

                        // USER ONLY - CHỈ CẦN 1 DÒNG "/user/**" LÀ ĐỦ
                        .requestMatchers("/user/**"  // TẤT CẢ CÁC URL BẮT ĐẦU BẰNG /user/ ĐỀU CẦN ROLE USER
                        ).hasRole("USER")

                        // STAFF hoặc ADMIN
                        .requestMatchers("/staff/events/**", "/api/address/openStreet/**", "/api/staff/events/**", "/staff/ticket-types/**", "/api/staff/ticket-types/**", "/staff/discounts/**", "/api/staff/discounts/**", "/staff/venues/**", "/api/staff/venues/**", "/staff/staffs/**", "/api/staff/staffs/**", "/staff/events/*/attendees/**", "/api/staff/events/*/attendees/**", "/api/revenue/**", "/staff/events/*/revenue/**", "/api/staff/events/*/revenue/**", "/staff/notifications/**", "/api/staff/notifications/**", "/staff/comments/**", "/api/staff/comments/**", "/api/checkin", "/staff/**").hasAnyRole("STAFF", "ADMIN")

                        // Chỉ ADMIN
                        .requestMatchers("/admin/users/**", "/api/admin/users/**", "/admin/events/**", "/api/admin/events/**", "/admin/categories/**", "/api/admin/categories/**", "/admin/revenue/**", "/api/admin/revenue/**", "/admin/**"

                        ).hasRole("ADMIN")

                        // Còn lại: phải đăng nhập
                        .anyRequest().authenticated()
                ).
                exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) -> {

                            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

                            if (auth != null) {
                                boolean isStaff = auth.getAuthorities().stream()
                                        .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"));

                                boolean isCustomer = auth.getAuthorities().stream()
                                        .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));

                                if (isStaff) {
                                    response.sendRedirect("/staff"); // 👈 staff về staff
                                    return;
                                }

                                if (isCustomer) {
                                    response.sendRedirect("/home"); // 👈 customer về home
                                    return;
                                }
                            }

                            // fallback nếu ko xác định được
                            response.sendRedirect("/home");
                        })
                )

                // Form Login (email + password)
                .formLogin(form -> form.loginPage("/auth/login")           // GET: hiện trang login
                        .loginProcessingUrl("/auth/login")    // POST: Security tự xử lý
                        .usernameParameter("email").passwordParameter("password")
                        .successHandler(successHandler())
                        .failureHandler((request, response, exception) -> {
                            String emailParam = request.getParameter("email");
                            String redirectUrl = "/auth/login?error=true";
                            if (emailParam != null && !emailParam.isBlank()) {
                                try {
                                    redirectUrl += "&email=" + java.net.URLEncoder.encode(emailParam.trim(), "UTF-8");
                                } catch (Exception ex) { /* ignore */ }
                            }
                            response.sendRedirect(redirectUrl);
                        }).permitAll())

                // Google OAuth2
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/auth/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)
                                .oidcUserService(oidcUserService()) // FIX: Thêm OIDC service để inject ROLE_USER
                        )
                        .successHandler((request, response, authentication) -> {
                            System.out.println("=== OAuth2 Login Success ===");
                            System.out.println("Authentication: " + authentication);
                            System.out.println("Principal: " + authentication.getPrincipal());
                            System.out.println("Authorities: " + authentication.getAuthorities());
                            System.out.println("Session ID: " + request.getSession().getId());

                            // Kiểm tra role
                            boolean hasUserRole = authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
                            System.out.println("Has ROLE_USER: " + hasUserRole);

                            response.sendRedirect("/home");
                        })
                        .failureHandler((request, response, exception) -> {
                            System.out.println("OAuth2 login failed: " + exception.getMessage());
                            exception.printStackTrace();
                            response.sendRedirect("/auth/login?error=true");
                        })
                )
                // Update: xóa tận góc remember-me để thực hiện thanh toán an toàn.
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)

                        // XÓA COOKIE PHÍA CLIENT (chắc chắn)
                        .addLogoutHandler((request, response, auth) -> {
                            jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("remember-me", null);
                            cookie.setPath("/");
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);
                        })

                        // XÓA COOKIE SPRING
                        .deleteCookies("JSESSIONID", "remember-me")

                        .permitAll()
                )
                // REMEMBER ME
                .rememberMe(remember -> remember
                        .key("uniqueAndSecret")
                        .tokenValiditySeconds(2592000)
                        // KHÔNG auto bật remember-me để đúng logic thực tế
                        .alwaysRemember(false)
                        // chỉ bật khi user tick
                        .rememberMeParameter("remember-me")
                        .userDetailsService(username -> {
                            try {
                                return customerDetailsService.loadUserByUsername(username);
                            } catch (UsernameNotFoundException e) {
                                try {
                                    return staffDetailsService.loadUserByUsername(username);
                                } catch (UsernameNotFoundException ex) {
                                    throw new UsernameNotFoundException("User not found: " + username);
                                }
                            }
                        })
                )

                // Session management
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession()
                        .invalidSessionUrl("/auth/login?invalid=true")
                        .maximumSessions(1)
                        .expiredUrl("/auth/login?expired=true")
                )

//                .authenticationManager(authenticationManager())
        ;

        return http.build();
    }


    // Bản vá nhanh chóng nhất để test mà không cần login
    // Kích hoạt khi spring.profiles.active=dev
    @Bean
    @Profile("dev")
    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // Cho phép tất cả, KHÔNG CẦN LOGIN
                )
                .csrf(csrf -> csrf.disable());  // Tắt CSRF để test

        return http.build();
    }

}
