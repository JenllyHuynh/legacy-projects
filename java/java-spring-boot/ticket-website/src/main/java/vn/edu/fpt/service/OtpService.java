package vn.edu.fpt.service;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.entity.OtpVerification;
import vn.edu.fpt.repository.OtpVerificationRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpService {

    @Autowired
    private OtpVerificationRepository otpRepository;

    @Autowired
    private EmailService emailService;

    // Đọc từ application.properties: otp.expiry-minutes=2
    @Value("${otp.expiry-minutes:2}")
    private int expiryMinutes;

    // Dùng SecureRandom thay Random thông thường — an toàn hơn cho mã xác thực
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Tạo OTP mới, lưu DB và gửi email
     * Gọi khi user vừa đăng ký
     */
    @Transactional
    public void generateAndSend(String email) throws MessagingException {
        // 1. Xóa tất cả OTP cũ của email này (tránh tồn đọng)
        otpRepository.deleteAllByEmail(email);

        // 2. Tạo mã 6 số ngẫu nhiên (có thể bắt đầu bằng 0, vd: 034521)
        String otpCode = String.format("%06d", secureRandom.nextInt(1_000_000));

        // 3. Lưu vào database
        OtpVerification otp = new OtpVerification();
        otp.setEmail(email);
        otp.setOtpCode(otpCode);
        otp.setExpiresAt(LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh")).plusMinutes(expiryMinutes));
        otp.setIsUsed(false);
        otpRepository.save(otp);

        // 4. Gửi email
        emailService.sendOtpEmail(email, otpCode);
    }

    /**
     * Xác minh OTP user nhập vào
     * Trả về: "VALID" | "INVALID" | "EXPIRED"
     */
    @Transactional
    public String validate(String email, String inputOtp) {
        Optional<OtpVerification> otpOpt = otpRepository.findLatestValidOtp(email);

        // Không tìm thấy OTP hợp lệ nào
        if (otpOpt.isEmpty()) {
            return "EXPIRED"; // hết hạn hoặc chưa từng gửi
        }

        OtpVerification otp = otpOpt.get();

        // Sai mã
        if (!otp.getOtpCode().equals(inputOtp)) {
            return "INVALID";
        }

        // Đúng → đánh dấu đã dùng
        otp.setIsUsed(true);
        otpRepository.save(otp);
        return "VALID";
    }

    /**
     * Gửi lại OTP mới (resend)
     * Logic giống generateAndSend — tái sử dụng luôn
     */
    @Transactional
    public void resend(String email) throws MessagingException {
        generateAndSend(email);
    }

    /**
     * Dọn dẹp OTP sau khi xác minh thành công
     */
    @Transactional
    public void cleanUp(String email) {
        otpRepository.deleteAllByEmail(email);
    }

    @Transactional
    public void deleteExpiredAndUsed() {
        otpRepository.deleteExpiredAndUsed();
    }

    /**
     * Trả về số giây còn lại của OTP hiện tại.
     * Trả về 0 nếu không có OTP hoặc đã hết hạn.
     */
    public long getRemainingSeconds(String email) {
        Optional<OtpVerification> otpOpt = otpRepository.findLatestOtpByEmail(email);
        if (otpOpt.isEmpty()) return 0;

        LocalDateTime expiresAt = otpOpt.get().getExpiresAt();
        LocalDateTime now = LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));

        long remaining = java.time.Duration.between(now, expiresAt).getSeconds();
        return Math.max(remaining, 0);
    }
}