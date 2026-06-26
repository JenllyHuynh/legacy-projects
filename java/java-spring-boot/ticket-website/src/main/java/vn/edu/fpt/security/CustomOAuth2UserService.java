package vn.edu.fpt.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.user.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.entity.Customer;
import vn.edu.fpt.model.entity.UserSocialLogin;
import vn.edu.fpt.repository.CustomerRepo;
import vn.edu.fpt.repository.UserSocialLoginRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private UserSocialLoginRepo userSocialLoginRepo;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attrs = oAuth2User.getAttributes();

        String email    = (String) attrs.get("email");
        String fullName = (String) attrs.get("name");
        String avatar   = (String) attrs.get("picture");
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerKey = (String) attrs.get("sub");

        syncUser(provider, providerKey, email, fullName, avatar);

        // QUAN TRỌNG: Tạo authorities với ROLE_USER
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER")); // THÊM ROLE_USER
        authorities.addAll(oAuth2User.getAuthorities()); // Giữ lại các scope

        DefaultOAuth2User defaultUser = new DefaultOAuth2User(
                authorities,
                attrs,
                "email"
        );

        System.out.println("Created OAuth2User with authorities: " + defaultUser.getAuthorities());

        return defaultUser;
    }

    /**
     * Đồng bộ user (Customer + UserSocialLogin) cho cả OAuth2 và OIDC.
     * Không tạo bảng mới, chỉ đảm bảo Google user mới được lưu đúng để /user/profile tra được.
     */
    @Transactional
    public void syncUser(String provider, String providerKey, String email, String fullName, String avatar) {
        System.out.println("=== Social Login Sync ===");
        System.out.println("Email: " + email);
        System.out.println("FullName: " + fullName);
        System.out.println("Provider: " + provider);
        System.out.println("ProviderKey: " + providerKey);

        if (email == null || email.isBlank()) {
            throw new RuntimeException("Google account không trả về email (email=null)");
        }

        // Fallback providerKey nếu thiếu (tránh lưu null gây lỗi findByProviderAndProviderKey)
        String safeProviderKey = (providerKey == null || providerKey.isBlank()) ? email : providerKey;

        // Kiểm tra xem đã có social login này chưa
        Optional<UserSocialLogin> existingSocialLogin =
                userSocialLoginRepo.findByProviderAndProviderKey(provider, safeProviderKey);

        Customer customer;

        if (existingSocialLogin.isPresent()) {
            customer = existingSocialLogin.get().getCustomer();
            System.out.println("Found existing social login for customer: " + customer.getEmail());

            // Cập nhật avatar nếu cần
            if (customer.getAvatarUrl() == null && avatar != null) {
                customer.setAvatarUrl(avatar);
                customerRepo.save(customer);
                System.out.println("Updated avatar for customer");
            }
            return;
        }

        System.out.println("No existing social login found, checking by email...");

        // Chưa có social login -> tìm hoặc tạo customer mới
        Optional<Customer> existingCustomer = customerRepo.findByEmail(email);

        if (existingCustomer.isPresent()) {
            customer = existingCustomer.get();
            System.out.println("Found existing customer with email: " + email);
        } else {
            customer = new Customer();
            customer.setEmail(email);
            customer.setFullName(fullName);
            customer.setAvatarUrl(avatar);
            customer.setPasswordHash("GOOGLE_OAUTH2");
            customer.setIsActive(true);
            customer = customerRepo.save(customer);
            System.out.println("Created new customer: " + email);
        }

        // Lưu thông tin social login
        UserSocialLogin socialLogin = new UserSocialLogin();
        socialLogin.setCustomer(customer);
        socialLogin.setProvider(provider);
        socialLogin.setProviderKey(safeProviderKey);
        userSocialLoginRepo.save(socialLogin);
        System.out.println("Saved social login info");
    }
}