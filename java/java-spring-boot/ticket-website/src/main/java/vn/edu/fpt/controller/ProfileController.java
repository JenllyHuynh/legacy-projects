package vn.edu.fpt.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.model.dto.ProfileDTO;
import vn.edu.fpt.model.dto.ProfileUpdateDTO;
import vn.edu.fpt.model.entity.Customer;
import vn.edu.fpt.repository.CustomerRepo;
import vn.edu.fpt.service.CustomerService;
import vn.edu.fpt.service.ProfileService;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Lấy email từ principal (hỗ trợ cả UserDetails và OAuth2User)
     */
    private String getEmailFromPrincipal(Object principal) {
        if (principal == null) {
            System.out.println("Principal is null!");
            return null;
        }

        System.out.println("Principal class: " + principal.getClass().getName());

        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            System.out.println("Email from UserDetails: " + email);
            return email;
        } else if (principal instanceof OAuth2User) {
            String email = ((OAuth2User) principal).getAttribute("email");
            System.out.println("Email from OAuth2User: " + email);
            return email;
        }

        System.out.println("Unknown principal type: " + principal.getClass());
        return null;
    }

    /**
     * Lấy customerId từ email của user đang đăng nhập.
     * Email đã được xác minh OTP khi đăng ký và không thể thay đổi
     * → dùng làm key tra cứu là an toàn.
     */
    private Integer getCurrentCustomerId(Object principal) {
        String email = getEmailFromPrincipal(principal);
        if (email == null) {
            throw new RuntimeException("Không thể xác định email từ principal");
        }

        Customer customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản: " + email));

        System.out.println("Found customer with ID: " + customer.getId());
        return customer.getId();
    }

    // ── MVC ───────────────────────────────────────────────────

    @GetMapping("/user/profile")
    public String viewProfile(@AuthenticationPrincipal Object principal,
                              Model model) {
        System.out.println("=== Accessing /user/profile ===");

        if (principal == null) {
            System.out.println("ERROR: Principal is null - redirecting to login");
            return "redirect:/auth/login";
        }

        try {
            Integer customerId = getCurrentCustomerId(principal);
            ProfileDTO profile = profileService.getProfile(customerId);

            model.addAttribute("profile", profile);
            model.addAttribute("updateDTO", new ProfileUpdateDTO());

            // Thêm flag để biết user có phải OAuth2 không
            boolean isOAuth2User = principal instanceof OAuth2User;
            model.addAttribute("isOAuth2User", isOAuth2User);

            System.out.println("Profile loaded successfully for customer ID: " + customerId);

            return "user/profile";

        } catch (Exception e) {
            System.out.println("Error loading profile: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/auth/login?error=profile_error";
        }
    }

    @PostMapping("/user/profile")
    public String updateProfileForm(@AuthenticationPrincipal Object principal,
                                    @Valid @ModelAttribute("updateDTO") ProfileUpdateDTO dto,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/auth/login";
        }

        Integer customerId = getCurrentCustomerId(principal);

        if (bindingResult.hasErrors()) {
            model.addAttribute("profile", profileService.getProfile(customerId));
            return "user/profile";
        }

        try {
            profileService.updateProfile(customerId, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/user/profile";
    }

    @GetMapping("/user/profile/security")
    public String openSecurity(@AuthenticationPrincipal Object principal,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/auth/login";
        }

        // Kiểm tra nếu là OAuth2 user thì không cho vào trang security
        if (principal instanceof OAuth2User) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Tài khoản Google không thể đổi mật khẩu. Vui lòng sử dụng tài khoản Google để đăng nhập.");
            return "redirect:/user/profile";
        }

        try {
            Integer customerId = getCurrentCustomerId(principal);
            ProfileDTO profile = profileService.getProfile(customerId);
            model.addAttribute("profile", profile);
            model.addAttribute("updateDTO", new ProfileUpdateDTO());
            return "user/changePassword";
        } catch (Exception e) {
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/user/profile/changePassword")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 @AuthenticationPrincipal Object principal,
                                 RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/auth/login";
        }

        // Kiểm tra nếu là OAuth2 user
        if (principal instanceof OAuth2User) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Tài khoản Google không thể đổi mật khẩu.");
            return "redirect:/user/profile/security";
        }

        try {
            String email = getEmailFromPrincipal(principal);
            Customer customer = customerRepo.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản: " + email));

            if (passwordEncoder.matches(oldPassword, customer.getPasswordHash())) {
                if (newPassword.equals(confirmPassword)) {
                    customerService.updatePassword(email, newPassword);
                    redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu mới không khớp!");
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu cũ không đúng!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }

        return "redirect:/user/profile/security";
    }

    // ── REST API (AJAX) ───────────────────────────────────────

    @GetMapping("/api/profile/me")
    @ResponseBody
    public ResponseEntity<ProfileDTO> getProfileApi(
            @AuthenticationPrincipal Object principal) {
        try {
            return ResponseEntity.ok(profileService.getProfile(getCurrentCustomerId(principal)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/api/profile/update")
    @ResponseBody
    public ResponseEntity<?> updateProfileApi(@AuthenticationPrincipal Object principal,
                                              @Valid @RequestBody ProfileUpdateDTO dto,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors()
                    .forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            ProfileDTO updated = profileService.updateProfile(getCurrentCustomerId(principal), dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Lỗi server"));
        }
    }

    @PostMapping("/api/profile/avatar")
    @ResponseBody
    public ResponseEntity<?> uploadAvatar(@AuthenticationPrincipal Object principal,
                                          @RequestParam("file") MultipartFile file) {
        try {
            String newUrl = profileService.updateAvatar(getCurrentCustomerId(principal), file);
            return ResponseEntity.ok(Map.of("avatarUrl", newUrl));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Lỗi server khi upload ảnh"));
        }
    }
}