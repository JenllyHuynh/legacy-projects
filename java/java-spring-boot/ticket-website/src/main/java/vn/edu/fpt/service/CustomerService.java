package vn.edu.fpt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.dto.CustomerDTO;
import vn.edu.fpt.model.entity.Cart;
import vn.edu.fpt.model.entity.Customer;
import vn.edu.fpt.repository.CustomerRepo;
import vn.edu.fpt.repository.OtpVerificationRepository;
import vn.edu.fpt.repository.PaymentRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Service layer to handle business logic for Customers
@Service
public class CustomerService {

    private final PaymentRepo paymentRepo;

    public CustomerService(PaymentRepo paymentRepo) {
        this.paymentRepo = paymentRepo;
    }

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OtpVerificationRepository otpVerificationRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private NotificationService notificationService;

    public boolean existsByEmail(String email) {
        return customerRepo.existsByEmail(email);
    }

    public Optional<Customer> findByEmail(String email) {
        return customerRepo.findByEmail(email);
    }

    public List<Customer> getRegistedCustomer(int eventId) {

        return  customerRepo.findCustomersByEvent(eventId);
    }

    // Method to register a new customer
    // Transactional ensures data integrity (rollback if any step fails)
    @Transactional
    public Customer addNewCustomer(CustomerDTO customerDTO) {

        // --- Phone Number Normalization Logic ---
        String phone = customerDTO.getPhone();
        if (phone != null) {
            // 1. Remove all non-numeric characters (spaces, dots, plus signs)
            phone = phone.replaceAll("[^0-9]", "");

            // 2. Handle country code '84' (Vietnam)
            if (phone.startsWith("84")) {
                phone = phone.substring(2); // Remove '84'
                // Ensure the remaining number starts with '0'
                if (!phone.startsWith("0")) phone = "0" + phone;
            }

            // 3. Ensure the number starts with '0' if it doesn't already
            if (!phone.startsWith("0") && !phone.isEmpty()) {
                phone = "0" + phone;
            }

            // Update the normalized phone number back to the DTO
            customerDTO.setPhone(phone);
        }

        // --- Validation Logic ---

        // Check if Full Name is provided
        if (customerDTO.getFullName() == null || customerDTO.getFullName().isBlank()) {
            throw new IllegalArgumentException("Full name is required");
        }

        // Check Email format using a stricter Regex (requires domain extension like .com)
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (customerDTO.getEmail() == null || !customerDTO.getEmail().matches(emailRegex)) {
            throw new IllegalArgumentException("Invalid email format (e.g., abc@domain.com)");
        }

        // Check if Email already exists in the database
        if (customerRepo.existsByEmail(customerDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Check Phone Number format (must start with 0 and have 10 digits total)
        if (customerDTO.getPhone() == null || !customerDTO.getPhone().matches("^0\\d{9}$")) {
            throw new IllegalArgumentException("Invalid phone number (must be 10 digits)");
        }

        // Check Password length (minimum 8 characters)
        if (customerDTO.getPasswordHash() == null || customerDTO.getPasswordHash().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        // Check if Password matches Confirm Password
        if (!customerDTO.getPasswordHash().equals(customerDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Encrypt the password before saving
        String encryptedPassword = passwordEncoder.encode(customerDTO.getPasswordHash());

        // Create and populate the Customer Entity
        Customer customer = new Customer();
        customer.setFullName(customerDTO.getFullName().trim());
        customer.setEmail(customerDTO.getEmail().trim());
        customer.setPhone(customerDTO.getPhone());
        customer.setPasswordHash(encryptedPassword);

        // Save to database
        return customerRepo.save(customer);
    }

    /**
     * Chỉ validate, KHÔNG lưu DB
     * Gọi trước khi gửi OTP
     */
    public void validateOnly(CustomerDTO customerDTO) {
        // Normalize phone
        String phone = customerDTO.getPhone();
        if (phone != null) {
            phone = phone.replaceAll("[^0-9]", "");
            if (phone.startsWith("84")) {
                phone = phone.substring(2);
                if (!phone.startsWith("0")) phone = "0" + phone;
            }
            if (!phone.startsWith("0") && !phone.isEmpty()) phone = "0" + phone;
            customerDTO.setPhone(phone);
        }

        // Validation
        if (customerDTO.getFullName() == null || customerDTO.getFullName().isBlank())
            throw new IllegalArgumentException("Full name is required");

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (customerDTO.getEmail() == null || !customerDTO.getEmail().matches(emailRegex))
            throw new IllegalArgumentException("Invalid email format (e.g., abc@domain.com)");

        if (customerRepo.existsByEmail(customerDTO.getEmail()))
            throw new IllegalArgumentException("Email already exists");

        if (customerDTO.getPhone() == null || !customerDTO.getPhone().matches("^0\\d{9}$"))
            throw new IllegalArgumentException("Invalid phone number (must be 10 digits)");

        if (customerDTO.getPasswordHash() == null || customerDTO.getPasswordHash().length() < 8)
            throw new IllegalArgumentException("Password must be at least 8 characters");

        if (!customerDTO.getPasswordHash().equals(customerDTO.getConfirmPassword()))
            throw new IllegalArgumentException("Passwords do not match");
    }

    /**
     * Lưu DB sau khi OTP xác minh thành công
     * isActive = TRUE ngay từ đầu, không cần update sau
     */
    @Transactional
    public Customer saveVerifiedCustomer(CustomerDTO customerDTO) {
        String encryptedPassword = passwordEncoder.encode(customerDTO.getPasswordHash());

        Customer customer = new Customer();
        customer.setFullName(customerDTO.getFullName().trim());
        customer.setEmail(customerDTO.getEmail().trim());
        customer.setPhone(customerDTO.getPhone());
        customer.setPasswordHash(encryptedPassword);
        customer.setIsActive(true);

        Customer saved = customerRepo.save(customer);

        // Tạo giỏ hàng ngay sau khi lưu user thành công
        Cart cart = new Cart();
        cart.setCustomer(saved);
        cart.setStatus("Active");
        cart.setCreatedAt(LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh")));
        cart.setUpdatedAt(LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh")));
        cartService.save(cart); // ← gọi thẳng cartRepo qua CartService

        // Gửi welcome notification
        notificationService.send(
                saved.getId(),
                "Welcome to Event Go! 🎉",
                "Your account has been verified successfully. Start exploring amazing events around you!",
                "InApp"
        );

        return saved;
    }

    // Cập nhật mật khẩu mới cho customer( dùng cho quên mật khẩu )
    @Transactional
    public void updatePassword(String email, String newPassword) {
        Customer customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email not found"));

        String encryptedPassword = passwordEncoder.encode(newPassword);
        customer.setPasswordHash(encryptedPassword);
        customerRepo.save(customer);
    }

    // Check email
    public boolean isEmailBelongsToCurrentUser(String email) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return auth.getName().equals(email);
        }
        return false; // Chưa login
    }

    public Page<Customer> getPageCustomerByStatus(
            int pageNumber,
            int pageSize,
            Boolean isActive
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return customerRepo.getPageCustomerByStatus(isActive, pageable);
    }

    @Transactional
    public void changeStatus(Integer id, boolean active) {

        Customer customer = customerRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));

        customer.setIsActive(active);
    }

    public Optional<Customer> getCustomerOptById(int id) {
        return customerRepo.findById(id);
    }
}