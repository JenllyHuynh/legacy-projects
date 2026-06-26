package vn.edu.fpt.controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.model.dto.CustomerDTO;
import vn.edu.fpt.service.CustomerService;
import vn.edu.fpt.service.OtpService;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private HttpSession session;

    // Số lần tối đa khi nhập sai sẽ đưa ra gợi ý
    private static final int MAX_ATTEMPTS_BEFORE_SUGGEST = 5;

    @Autowired
    public CustomerService customerService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private AuthenticationManager authenticationManager;

    // ─────────────────────────────────────────
    // Login
    // ─────────────────────────────────────────
    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String email,
            Model model,
            HttpSession session) {

        // Luôn thêm attribute với giá trị mặc định
        model.addAttribute("loginError", false);
        model.addAttribute("showForgotSuggestion", false);
        model.addAttribute("failedAttempts", 0);
        model.addAttribute("remainingAttempts", MAX_ATTEMPTS_BEFORE_SUGGEST);

        // Nếu đăng nhập sai
        if (error != null) {
            if (email != null && !email.isBlank()) {
                // Có email đếm số lần sai theo email
                Integer failedAttempts = (Integer) session.getAttribute("failedAttempts_" + email);
                if (failedAttempts == null) {
                    failedAttempts = 1;
                } else {
                    failedAttempts++;
                }
                session.setAttribute("failedAttempts_" + email, failedAttempts);

                if (failedAttempts >= MAX_ATTEMPTS_BEFORE_SUGGEST) {
                    model.addAttribute("showForgotSuggestion", true);
                    model.addAttribute("suggestedEmail", email);
                    model.addAttribute("failedAttempts", failedAttempts);
                } else {
                    model.addAttribute("loginError", true);
                    model.addAttribute("email", email);
                    model.addAttribute("remainingAttempts", MAX_ATTEMPTS_BEFORE_SUGGEST - failedAttempts);
                    model.addAttribute("failedAttempts", failedAttempts);
                }
            } else {
                // Không có email trong URL (không nên xảy ra nữa sau khi fix SecurityConfig)
                model.addAttribute("loginError", true);
                model.addAttribute("remainingAttempts", MAX_ATTEMPTS_BEFORE_SUGGEST);
            }
        }
        return "auth/login";
    }

    // Reset counter khi login thành công
    @GetMapping("/reset-attempts")
    @ResponseBody
    public String resetAttempts(@RequestParam String email, HttpSession session) {
        session.removeAttribute("failedAttempts_" + email);
        return "okeBro";
    }

    // AJAX Login không reload trang tăng cải thiện ux
    @PostMapping("/login-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> loginAjax(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            jakarta.servlet.http.HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();

        try {
            // 🔐 Authenticate
            var authToken = new UsernamePasswordAuthenticationToken(email.trim(), password);
            var authentication = authenticationManager.authenticate(authToken);

            // ✅ Lưu SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );

            // Reset failed attempts
            session.removeAttribute("failedAttempts_" + email.trim());

            // 🎯 Lấy role
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isStaff = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"));
            boolean isUser = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));

            // 🔥 Lấy saved request
            var requestCache = new HttpSessionRequestCache();
            var savedRequest = requestCache.getRequest(request, null);

            String redirectUrl = null;

            if (savedRequest != null) {
                try {
                    String rawUrl = savedRequest.getRedirectUrl();

                    URI uri = new URI(rawUrl);
                    String path = uri.getPath();

                    // giữ nguyên query
                    String fullUrl = path +
                            (uri.getQuery() != null ? "?" + uri.getQuery() : "");

                    System.out.println("FULL URL: " + fullUrl);

                    if (isSafeRedirect(path) &&
                            isValidUrlForRole(path, isAdmin, isStaff, isUser)) {

                        redirectUrl = fullUrl; // ✅ dùng full URL
                    }

                } catch (Exception e) {
                    System.out.println("Invalid URL");
                }
            }

            // 👉 fallback theo role
            if (redirectUrl == null) {
                redirectUrl = resolveDefaultByRole(isAdmin, isStaff, isUser);
            }

            // ❗ Xóa savedRequest sau khi dùng
            requestCache.removeRequest(request, null);

            System.out.println("FINAL REDIRECT: " + redirectUrl);

            result.put("success", true);
            result.put("redirectUrl", redirectUrl);
            return ResponseEntity.ok(result);

        } catch (DisabledException e) {
            result.put("success", false);
            result.put("errorType", "DISABLED");
            result.put("message", "Tài khoản đã bị khóa.");
            return ResponseEntity.ok(result);

        } catch (BadCredentialsException e) {
            // Đếm số lần sai
            String key = "failedAttempts_" + email.trim();
            Integer failedAttempts = (Integer) session.getAttribute(key);
            failedAttempts = (failedAttempts == null) ? 1 : failedAttempts + 1;
            session.setAttribute(key, failedAttempts);

            result.put("success", false);
            result.put("failedAttempts", failedAttempts);

            if (failedAttempts >= MAX_ATTEMPTS_BEFORE_SUGGEST) {
                result.put("errorType", "SUGGEST_FORGOT");
                result.put("message", "You\'ve tried " + failedAttempts + " times. Did you forget your password?");
                result.put("suggestedEmail", email.trim());
            } else {
                result.put("errorType", "BAD_CREDENTIALS");
                result.put("message", "Invalid email or password.");
                result.put("remainingAttempts", MAX_ATTEMPTS_BEFORE_SUGGEST - failedAttempts);
            }
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.put("success", false);
            result.put("errorType", "ERROR");
            result.put("message", "An error occurred. Please try again.");
            return ResponseEntity.ok(result);
        }
    }

    private boolean isValidUrlForRole(String url, boolean isAdmin, boolean isStaff, boolean isUser) {
        if (url == null) return false;

        if (isAdmin) return true;
        if (isStaff) return url.contains("/staff");
        if (isUser) return !url.contains("/admin/system-revenue") && !url.contains("/staff");

        return false;
    }

    private boolean isSafeRedirect(String path) {
        if (path == null) return false;

        // ❌ chặn hệ thống / rác
        if (path.startsWith("/.well-known")) return false;
        if (path.startsWith("/error")) return false;
        if (path.startsWith("/auth/login")) return false;

        // ✅ chỉ cho phép internal URL
        return path.startsWith("/");
    }

    private String resolveDefaultByRole(boolean isAdmin, boolean isStaff, boolean isUser) {
        if (isAdmin) return "/admin";
        if (isStaff) return "/staff";
        return "/home";
    }

    @GetMapping("/register")
    public String register(
            @RequestParam(required = false) String step,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String error,
            HttpSession session,
            Model model) {

        model.addAttribute("showOtpModal", false);
        model.addAttribute("otpError", null);
        model.addAttribute("otpSuccess", null);
        model.addAttribute("otpEmail", null);
        model.addAttribute("otpVerified", null);
        model.addAttribute("otpRedirectUrl", null);
        model.addAttribute("otpRemainingSeconds", 0L);

        if (!model.containsAttribute("customer")) {
            // ← Nếu session còn pendingCustomer thì dùng lại, không tạo mới
            CustomerDTO pending = (CustomerDTO) session.getAttribute("pendingCustomer");
            if (pending != null) {
                // Không trả về passwordHash ra form vì lý do bảo mật
                CustomerDTO prefilled = new CustomerDTO();
                prefilled.setFullName(pending.getFullName());
                prefilled.setEmail(pending.getEmail());
                prefilled.setPhone(pending.getPhone());
                model.addAttribute("customer", prefilled);
            } else {
                model.addAttribute("customer", new CustomerDTO());
            }
        }

        if ("verify".equals(step) && email != null) {
            model.addAttribute("showOtpModal", true);
            model.addAttribute("otpEmail", email);

            long remaining = otpService.getRemainingSeconds(email);
            model.addAttribute("otpRemainingSeconds", remaining);

            if ("invalid".equals(error)) {
                model.addAttribute("otpError", "Invalid code. Please try again.");
            } else if ("expired".equals(error)) {
                model.addAttribute("otpError", "Code has expired. Please request a new one.");
            }
        }

        return "auth/register";
    }

    // ─────────────────────────────────────────
    // Save — xử lý form đăng ký
    // ─────────────────────────────────────────
    @PostMapping("/save")
    public String addNewCustomer(
            @ModelAttribute("customer") CustomerDTO customerDTO,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        System.out.println("=== POST /auth/save ===");
        System.out.println("Email: " + customerDTO.getEmail());
        System.out.println("FullName: " + customerDTO.getFullName());

        try {
            // 1. Chỉ validate, KHÔNG lưu DB
            customerService.validateOnly(customerDTO);

            // 2. Lưu CustomerDTO vào Session để dùng sau khi OTP xác minh
            session.setAttribute("pendingCustomer", customerDTO);

            // 3. Tạo OTP và gửi email
            otpService.generateAndSend(customerDTO.getEmail());

            // 4. Redirect sang modal OTP
            redirectAttributes.addAttribute("step", "verify");
            redirectAttributes.addAttribute("email", customerDTO.getEmail());
            return "redirect:/auth/register";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("customer", customerDTO);
            return "auth/register";

        } catch (MessagingException | MailException e) {
            model.addAttribute("error", "Failed to send verification email. Please try again.");
            model.addAttribute("customer", customerDTO);
            return "auth/register";
        }
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam String email,
            @RequestParam String otp1, @RequestParam String otp2,
            @RequestParam String otp3, @RequestParam String otp4,
            @RequestParam String otp5, @RequestParam String otp6,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String fullOtp = otp1 + otp2 + otp3 + otp4 + otp5 + otp6;
        String result = otpService.validate(email, fullOtp);

        switch (result) {
            case "VALID" -> {
                // Lấy CustomerDTO từ Session
                CustomerDTO pendingCustomer =
                        (CustomerDTO) session.getAttribute("pendingCustomer");

                if (pendingCustomer == null) {
                    // Session hết hạn → quay về đăng ký lại
                    redirectAttributes.addFlashAttribute("error",
                            "Session expired. Please register again.");
                    return "redirect:/auth/register";
                }

                // Lúc này mới lưu vào DB, isActive = TRUE luôn
                customerService.saveVerifiedCustomer(pendingCustomer);


                // Dọn dẹp session và OTP
                session.removeAttribute("pendingCustomer");
                otpService.cleanUp(email);

                return "redirect:/auth/verified";
            }
            case "INVALID" -> {
                redirectAttributes.addAttribute("step", "verify");
                redirectAttributes.addAttribute("email", email);
                redirectAttributes.addAttribute("error", "invalid");
                return "redirect:/auth/register";
            }
            case "EXPIRED" -> {
                redirectAttributes.addAttribute("step", "verify");
                redirectAttributes.addAttribute("email", email);
                redirectAttributes.addAttribute("error", "expired");
                return "redirect:/auth/register";
            }
            default -> {
                return "redirect:/auth/register";
            }
        }
    }

    // ─────────────────────────────────────────
    // Resend OTP — gửi lại mã mới
    // ─────────────────────────────────────────
    @GetMapping("/resend-otp")
    public String resendOtp(
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {
        try {
            otpService.resend(email);
        } catch (MessagingException e) {
            // Gửi lại thất bại → vẫn quay về modal, không crash
        }
        redirectAttributes.addAttribute("step", "verify");
        redirectAttributes.addAttribute("email", email);
        return "redirect:/auth/register";
    }

    // ─────────────────────────────────────────
    // Verified — trang chúc mừng
    // ─────────────────────────────────────────
    @GetMapping("/verified")
    public String verified() {
        return "auth/verified";
    }

    // PHẦN MỚI THÊM - FORGOT PASSWORD

    // Forgot Password
    @GetMapping("/forgot-password")
    public String forgotPassword(HttpSession session, Model model) {
        // Lấy email từ session đã được lưu
        String currentEmail = null;

        // Thử cách lấy qua SecutityContext(nếu đã login)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            currentEmail = auth.getName();
            System.out.println("Email: " + currentEmail);
        }

        // Thử cách lấy từ GlobalUserAdvice được lấy qua model
        model.addAttribute("currentEmail", currentEmail);
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam String email,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        System.out.println("Email: " + email);
        //Lấy email từ session đã lưu được hiện tại(nếu đang login)
        String currentEmail = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            currentEmail = auth.getName();
            System.out.println("Email: " + currentEmail);
        }
        // Nếu đang login, kiểm tra email nập vào có khớp không
        if (currentEmail != null && !currentEmail.equals(email)) {
            System.out.println("Email mismatch! Logged in as: " + currentEmail + ", but requested: " + email);
            redirectAttributes.addFlashAttribute("error",
                    "You are currently logged in as " + currentEmail);
            return "redirect:/auth/forgot-password";
        }

        // Kiểm tra email có tồn tại không
        if (!customerService.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("error", "Email not found in our system.");
            return "redirect:/auth/forgot-password";
        }

        try {
            otpService.generateAndSend(email);
            session.setAttribute("resetEmail", email);

            // Lưu thêm thông tin cho chắc chắn để kiểm tra sau đó
            session.setAttribute("resetRequestTime", System.currentTimeMillis());

            redirectAttributes.addAttribute("email", email);
            return "redirect:/auth/forgot-verify-otp";

        } catch (MessagingException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send OTP. Please try again.");
            return "redirect:/auth/forgot-password";
        }
    }

    // FORGOT VERIFY OTP
    @GetMapping("/forgot-verify-otp")
    public String forgotVerifyOtpPage(
            @RequestParam String email,
            @RequestParam(required = false) String error,
            Model model) {

        model.addAttribute("email", email);

        if ("invalid".equals(error)) {
            model.addAttribute("error", "Invalid OTP code. Please try again.");
        } else if ("expired".equals(error)) {
            model.addAttribute("error", "OTP has expired. Please request a new one.");
        }

        return "auth/forgot-verify-otp";
    }

    @PostMapping("/forgot-verify-otp")
    public String forgotVerifyOtp(
            @RequestParam String email,
            @RequestParam String otp1, @RequestParam String otp2,
            @RequestParam String otp3, @RequestParam String otp4,
            @RequestParam String otp5, @RequestParam String otp6,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Kiểm tra email có khớp với session không
        String sessionEmail = (String) session.getAttribute("resetEmail");
        if (sessionEmail == null || !sessionEmail.equals(email)) {
            redirectAttributes.addFlashAttribute("error", "Session expired or email mismatch. Please try again.");
            return "redirect:/auth/forgot-password";
        }

        String fullOtp = otp1 + otp2 + otp3 + otp4 + otp5 + otp6;
        String result = otpService.validate(email, fullOtp);

        switch (result) {
            case "VALID" -> {
                session.setAttribute("verifiedEmail", email);
                return "redirect:/auth/reset-password";
            }
            case "INVALID" -> {
                redirectAttributes.addAttribute("email", email);
                redirectAttributes.addAttribute("error", "invalid");
                return "redirect:/auth/forgot-verify-otp";
            }
            case "EXPIRED" -> {
                redirectAttributes.addAttribute("email", email);
                redirectAttributes.addAttribute("error", "expired");
                return "redirect:/auth/forgot-verify-otp";
            }
            default -> {
                return "redirect:/auth/forgot-password";
            }
        }
    }

    // FORGOT RESEND OTP
    @GetMapping("/forgot-resend-otp")
    public String forgotResendOtp(
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {
        try {
            otpService.resend(email);
            redirectAttributes.addFlashAttribute("success", "New OTP has been sent to your email.");
        } catch (MessagingException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to resend OTP. Please try again.");
        }
        redirectAttributes.addAttribute("email", email);
        return "redirect:/auth/forgot-verify-otp";
    }

    // RESET PASSWORD
    @GetMapping("/reset-password")
    public String resetPasswordPage(HttpSession session) {
        String email = (String) session.getAttribute("verifiedEmail");
        if (email == null) {
            return "redirect:/auth/forgot-password";
        }
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String email = (String) session.getAttribute("verifiedEmail");
        if (email == null) {
            return "redirect:/auth/forgot-password";
        }

        // Validate passwords
        if (newPassword == null || newPassword.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Password cannot be empty.");
            return "redirect:/auth/reset-password";
        }

        // Check password strength
        List<String> passwordErrors = new ArrayList<>();

        if (newPassword.length() < 8) {
            passwordErrors.add("at least 8 characters");
        }

        if (!newPassword.matches(".*[A-Z].*")) {
            passwordErrors.add("at least one uppercase letter");
        }

        if (!newPassword.matches(".*[0-9].*")) {
            passwordErrors.add("at least one number");
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/auth/reset-password";
        }

        if (!passwordErrors.isEmpty()) {
            String errorMessage = "Password must contain: " + String.join(", ", passwordErrors);
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/auth/reset-password";
        }

        try {
            customerService.updatePassword(email, newPassword);

            // Clean up
            session.removeAttribute("verifiedEmail");
            session.removeAttribute("resetEmail");
            otpService.cleanUp(email);

            redirectAttributes.addFlashAttribute("success",
                    "Password has been reset successfully. Please login with your new password.");
            return "redirect:/auth/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to reset password. Please try again.");
            return "redirect:/auth/reset-password";
        }
    }
}