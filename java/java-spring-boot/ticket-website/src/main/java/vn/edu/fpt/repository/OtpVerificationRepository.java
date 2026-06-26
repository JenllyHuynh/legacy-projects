package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.entity.OtpVerification;

import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Integer> {

    @Query(value = """
    SELECT TOP 1 * FROM OtpVerification
    WHERE Email = :email
      AND IsUsed = 0
      AND ExpiresAt > DATEADD(HOUR, 7, GETUTCDATE())
    ORDER BY CreatedAt DESC
    """, nativeQuery = true)
    Optional<OtpVerification> findLatestValidOtp(@Param("email") String email);

    // Xóa tất cả OTP cũ của email (dùng khi resend)
    @Modifying
    @Query("DELETE FROM OtpVerification o WHERE o.email = :email")
    void deleteAllByEmail(@Param("email") String email);

    @Modifying
    @Query(value = """
    DELETE FROM OtpVerification
    WHERE ExpiresAt < DATEADD(HOUR, 7, GETUTCDATE())
       OR IsUsed = 1
    """, nativeQuery = true)
    void deleteExpiredAndUsed();

    @Query(value = """
    SELECT TOP 1 * FROM OtpVerification
    WHERE Email = :email
      AND IsUsed = 0
    ORDER BY CreatedAt DESC
    """, nativeQuery = true)
    Optional<OtpVerification> findLatestOtpByEmail(@Param("email") String email);
}