package vn.edu.fpt.scheduler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.repository.OtpVerificationRepository;
import vn.edu.fpt.service.OtpService;

@Component
@RequiredArgsConstructor
public class OtpCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(OtpCleanupScheduler.class);

    @Autowired
    private OtpVerificationRepository otpRepository;
    @Autowired
    private OtpService otpService;

    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void cleanUpExpiredOtps() {
        try {
            otpService.deleteExpiredAndUsed();
            log.info("[OTP Cleanup] Expired and used OTPs deleted successfully");
        } catch (Exception e) {
            log.error("[OTP Cleanup] Failed to delete expired OTPs", e);
        }
    }
}