package vn.edu.fpt.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.entity.Staff;
import vn.edu.fpt.repository.StaffRepo;


/**
 * Load Staff từ DB cho form login.
 *
 * Xử lý 2 trường hợp password trong DB:
 *  - BCrypt    : bắt đầu bằng "$2a$" hoặc "$2b$" -> giữ nguyên
 *  - Plain text: -> tự động hash BCrypt rồi update DB luôn
 *
 * createdBy == null -> ADMIN, ngược lại → STAFF
 */
@Service
public class CustomStaffUserDetailsService implements UserDetailsService {

    @Autowired private StaffRepo       staffRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Staff staff = staffRepo.findByEmail(email.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Staff không tồn tại."));

        if (!Boolean.TRUE.equals(staff.getIsActive())) {
            throw new DisabledException("Tài khoản nhân viên đã bị khóa.");
        }

        // Nếu password CHƯA phải BCrypt -> hash lại và update DB luôn
        String storedPassword = staff.getPasswordHash();
        if (!isBCrypt(storedPassword)) {
            String hashed = passwordEncoder.encode(storedPassword);
            staff.setPasswordHash(hashed);
            staffRepo.save(staff);
            storedPassword = hashed;
        }

        String role = (staff.getCreatedBy() == null) ? "ADMIN" : "STAFF";

        System.out.println("=== DEBUG ===");
        System.out.println("Email: " + staff.getEmail());
        System.out.println("Password in DB: " + staff.getPasswordHash());
        System.out.println("Is BCrypt? " + isBCrypt(staff.getPasswordHash()));
        if (!isBCrypt(staff.getPasswordHash())) {
            String hashed = passwordEncoder.encode(staff.getPasswordHash());
            System.out.println("Hashed: " + hashed);
        }
        return User.builder()
                .username(staff.getEmail())
                .password(storedPassword)
                .roles(role)
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
