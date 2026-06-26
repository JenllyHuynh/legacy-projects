package vn.edu.fpt.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.entity.Customer;
import vn.edu.fpt.repository.CustomerRepo;


/**
 * Load Customer từ DB cho form login.
 *
 * Xử lý 2 trường hợp password trong DB:
 *  - BCrypt  : bắt đầu bằng "$2a$" hoặc "$2b$" -> giữ nguyên, Spring Security tự verify
 *  - Plain text: "123456", "abc"... -> tự động hash BCrypt rồi update lại DB luôn
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired private CustomerRepo    customerRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Customer customer = customerRepo.findByEmail(email.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Email không tồn tại."));

        if (!Boolean.TRUE.equals(customer.getIsActive())) {
            throw new DisabledException("Tài khoản đã bị khóa.");
        }

        // Nếu password CHƯA phải BCrypt -> hash lại và update DB luôn
        String storedPassword = customer.getPasswordHash();
        if (!isBCrypt(storedPassword)) {
            String hashed = passwordEncoder.encode(storedPassword);
            customer.setPasswordHash(hashed);
            customerRepo.save(customer);
            storedPassword = hashed;
        }

        return User.builder()
                .username(customer.getEmail())
                .password(storedPassword)
                .roles("USER")
                .build();
    }

    /** Kiểm tra chuỗi có phải BCrypt hash không */
    private boolean isBCrypt(String password) {
        return password != null
                && (password.startsWith("$2a$")
                ||  password.startsWith("$2b$")
                ||  password.startsWith("$2y$"));
    }
}
