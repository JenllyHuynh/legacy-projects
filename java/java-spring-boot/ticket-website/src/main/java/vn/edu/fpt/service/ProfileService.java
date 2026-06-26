package vn.edu.fpt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.fpt.model.dto.ProfileDTO;
import vn.edu.fpt.model.dto.ProfileUpdateDTO;
import vn.edu.fpt.model.entity.Customer;
import vn.edu.fpt.repository.CustomerRepo;

import java.io.IOException;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * ProfileService — Xử lý logic nghiệp vụ cho tính năng Manage Profile.
 *
 * Các chức năng:
 *   1. getProfile(customerId)        — Lấy thông tin profile (UC-09 View)
 *   2. updateProfile(id, dto)        — Cập nhật fullName, phone (UC-09 Update)
 *   3. updateAvatar(id, file)        — Upload và cập nhật ảnh đại diện
 *
 * Theo SDS: chỉ cho phép sửa FullName, Phone, AvatarUrl.
 * Email KHÔNG được cập nhật (đã xác minh).
 */
@Service
public class ProfileService {

    // Thư mục lưu avatar — cấu hình trong application.properties
    @Value("${app.upload.avatar-dir:uploads/avatars}")
    private String avatarDir;

    // URL prefix để trả về cho client
    @Value("${app.upload.avatar-url-prefix:/uploads/avatars}")
    private String avatarUrlPrefix;

    @Autowired
    private CustomerRepo customerRepository;

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ─────────────────────────────────────────────────────────────
    // 1. VIEW PROFILE
    // ─────────────────────────────────────────────────────────────

    /**
     * Lấy thông tin profile của customer theo ID.
     * Query tương ứng (SDS):
     *   SELECT CustomerId, FullName, Email, Phone, AvatarUrl
     *   FROM Customers WHERE CustomerId = :customerId;
     */
    public ProfileDTO getProfile(Integer customerId) {
        Customer customer = findCustomerOrThrow(customerId);
        return convertToProfileDTO(customer);
    }

    // ─────────────────────────────────────────────────────────────
    // 2. UPDATE PROFILE
    // ─────────────────────────────────────────────────────────────

    /**
     * Cập nhật FullName và Phone của customer.
     * Query tương ứng (SDS):
     *   UPDATE Customers SET FullName = ?, Phone = ?, AvatarUrl = ?
     *   WHERE CustomerId = ?;
     *
     * @throws IllegalArgumentException nếu fullName rỗng
     */
    public ProfileDTO updateProfile(Integer customerId, ProfileUpdateDTO dto) {
        Customer customer = findCustomerOrThrow(customerId);

        // Validate fullName không được rỗng
        if (dto.getFullName() == null || dto.getFullName().isBlank()) {
            throw new IllegalArgumentException("Họ tên không được để trống");
        }

        // Chỉ cập nhật các field được phép — email KHÔNG thay đổi
        customer.setFullName(dto.getFullName().trim());
        customer.setPhone(dto.getPhone() != null ? dto.getPhone().trim() : null);

        customerRepository.save(customer);
        return convertToProfileDTO(customer);
    }

    // ─────────────────────────────────────────────────────────────
    // 3. UPLOAD AVATAR
    // ─────────────────────────────────────────────────────────────

    /**
     * Upload ảnh đại diện mới, lưu vào thư mục server,
     * cập nhật AvatarUrl vào DB rồi trả về URL mới.
     *
     * @throws IllegalArgumentException nếu file rỗng hoặc không phải ảnh
     * @throws RuntimeException         nếu lỗi I/O khi lưu file
     */
    public String updateAvatar(Integer customerId, MultipartFile file) {
        // Validate file
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File ảnh không được rỗng");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Chỉ chấp nhận file ảnh (jpg, png, webp...)");
        }

        // Giới hạn dung lượng 5MB
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Ảnh tối đa 5MB");
        }

        Customer customer = findCustomerOrThrow(customerId);

        // Tạo tên file duy nhất để tránh trùng
        String extension  = getExtension(file.getOriginalFilename());
        String fileName   = "avatar_" + customerId + "_" + UUID.randomUUID() + extension;

        // Lưu file vào thư mục
        try {
            Path uploadPath = Paths.get(avatarDir);
            Files.createDirectories(uploadPath);
            Files.copy(file.getInputStream(),
                    uploadPath.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu ảnh đại diện: " + e.getMessage(), e);
        }

        // Xóa avatar cũ nếu là file local (không xóa URL từ OAuth2)
        deleteOldAvatarIfLocal(customer.getAvatarUrl());

        // Cập nhật URL vào DB
        String newAvatarUrl = avatarUrlPrefix + "/" + fileName;
        customer.setAvatarUrl(newAvatarUrl);
        customerRepository.save(customer);

        return newAvatarUrl;
    }

    // ─────────────────────────────────────────────────────────────
    // HELPER METHODS
    // ─────────────────────────────────────────────────────────────

    /** Tìm Customer theo ID, ném exception nếu không tồn tại */
    private Customer findCustomerOrThrow(Integer customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy customer với ID: " + customerId));
    }

    /** Convert Customer entity → ProfileDTO (không expose field nhạy cảm) */
    private ProfileDTO convertToProfileDTO(Customer customer) {
        ProfileDTO dto = new ProfileDTO();
        dto.setCustomerId(customer.getId());
        dto.setFullName(customer.getFullName());
        dto.setEmail(customer.getEmail());       // chỉ đọc, không sửa
        dto.setPhone(customer.getPhone());
        dto.setAvatarUrl(customer.getAvatarUrl());
        dto.setCreatedAt(customer.getCreatedAt() != null
                ? customer.getCreatedAt().format(DATE_FMT)
                : null);
        return dto;
    }

    /** Lấy extension file (.jpg, .png...) */
    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf("."));
    }

    /** Xóa avatar cũ nếu là file được upload lên server (không phải URL bên ngoài) */
    private void deleteOldAvatarIfLocal(String oldAvatarUrl) {
        if (oldAvatarUrl == null || !oldAvatarUrl.startsWith(avatarUrlPrefix)) return;
        try {
            String oldFileName = oldAvatarUrl.replace(avatarUrlPrefix + "/", "");
            Path oldFile = Paths.get(avatarDir, oldFileName);
            Files.deleteIfExists(oldFile);
        } catch (IOException ignored) {
            // Không ảnh hưởng flow chính nếu xóa thất bại
        }
    }

    //check length of password
    public boolean isStrongPassword(String password) {

        boolean hasLength = password.length() >= 8;
        boolean hasLetter = Pattern.compile("[A-Za-z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("\\d").matcher(password).find();

        if (hasLength && hasLetter && hasDigit) {
            System.out.println("Is strong password");
            return true;
        } else {
            System.out.println("Is not strong password");
            return false;
        }
    }
}