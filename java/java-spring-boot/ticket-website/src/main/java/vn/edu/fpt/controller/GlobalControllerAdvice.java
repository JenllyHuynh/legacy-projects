package vn.edu.fpt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import vn.edu.fpt.model.dto.ProfileDTO;
import vn.edu.fpt.service.ProfileService;
import vn.edu.fpt.model.entity.Customer;
import vn.edu.fpt.repository.CustomerRepo;

/**
 * Inject ${sessionUser} vào tất cả các view — dùng cho header fragment.
 *
 * Thymeleaf fragment header dùng:
 *   ${sessionUser.fullName}
 *   ${sessionUser.avatarUrl}
 *   ${sessionUser.email}
 *
 * Nếu user chưa đăng nhập → userDetails = null → sessionUser = null
 * Header fragment dùng sec:authorize để ẩn/hiện đúng phần.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private CustomerRepo customerRepo;

    @ModelAttribute("sessionUser")
    public ProfileDTO addSessionUser(@AuthenticationPrincipal Object principal) {
        // Chưa đăng nhập → trả về null (header hiện nút Login/Register)
        if (principal == null) {
            return null;
        }

        try {
            String email = null;
            if (principal instanceof UserDetails) {
                email = ((UserDetails) principal).getUsername();
            } else if (principal instanceof OAuth2User) {
                email = ((OAuth2User) principal).getAttribute("email");
            }
            if (email == null || email.isBlank()) return null;

            Customer customer = customerRepo.findByEmail(email).orElse(null);
            if (customer == null) return null;

            // Tái dùng ProfileService để lấy đầy đủ thông tin (avatarUrl, fullName, ...)
            return profileService.getProfile(customer.getId());
        } catch (Exception e) {
            // Không để lỗi ở đây crash toàn bộ request
            return null;
        }
    }
}