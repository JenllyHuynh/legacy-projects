package vn.edu.fpt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import vn.edu.fpt.model.entity.Customer;
import vn.edu.fpt.repository.CustomerRepo;

@ControllerAdvice
public class GlobalUserAdvice {

    @Autowired
    private CustomerRepo customerRepo;

    // Tránh đụng tên với GlobalControllerAdvice (nó đang inject ProfileDTO cho header)
    @ModelAttribute("sessionCustomer")
    public Customer addUserToModel(@AuthenticationPrincipal Object principal) {
        // Nếu chưa đăng nhập, trả về null
        if (principal == null) {
            return null;
        }

        String email = null;

        // Xử lý cả UserDetails (form login) và OAuth2User (Google login)
        if (principal instanceof UserDetails) {
            // Form login: username là email
            email = ((UserDetails) principal).getUsername();
            System.out.println("GlobalUserAdvice - UserDetails email: " + email);
        } else if (principal instanceof OAuth2User) {
            // Google login: lấy email từ attributes
            email = ((OAuth2User) principal).getAttribute("email");
            System.out.println("GlobalUserAdvice - OAuth2User email: " + email);
        }

        // Nếu có email, tìm Customer trong database
        if (email != null) {
            return customerRepo.findByEmail(email).orElse(null);
        }

        return null;
    }
}