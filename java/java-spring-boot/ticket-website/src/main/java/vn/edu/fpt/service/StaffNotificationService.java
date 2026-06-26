package vn.edu.fpt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.dto.StaffNotificationDTO;
import vn.edu.fpt.model.entity.Event;
import vn.edu.fpt.model.entity.Staff;
import vn.edu.fpt.model.entity.StaffNotification;
import vn.edu.fpt.repository.EventRepo;
import vn.edu.fpt.repository.StaffNotificationRepo;
import vn.edu.fpt.repository.StaffRepo;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StaffNotificationService {

    @Autowired
    private StaffNotificationRepo staffNotifRepo;

    @Autowired
    private StaffRepo staffRepo;

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private EmailService emailService;


    // ── Lấy 5 thông báo gần nhất cho popup chuông ────────────────────
    public List<StaffNotificationDTO> getRecent(Integer staffId) {
        return staffNotifRepo.findTop5ByStaff_IdOrderBySentAtDesc(staffId)
                .stream()
                .map(StaffNotificationDTO::new)
                .collect(Collectors.toList());
    }

    // ── Lấy TẤT CẢ thông báo cho trang inbox  ─────────────────────────
    public List<StaffNotificationDTO> getAll(Integer staffId) {
        return staffNotifRepo.findByStaff_IdOrderBySentAtDesc(staffId)
                .stream()
                .map(StaffNotificationDTO::new)
                .collect(Collectors.toList());
    }


    // ── Đếm badge chưa đọc ───────────────────────────────────────────
    public long countUnread(Integer staffId) {
        return staffNotifRepo.countByStaff_IdAndIsReadFalse(staffId);
    }

    // ── Đánh dấu 1 thông báo đã đọc ──────────────────────────────────
    @Transactional
    public void markAsRead(Integer notifId, Integer staffId) {
        staffNotifRepo.markAsRead(notifId, staffId);
    }

    // ── Đánh dấu tất cả đã đọc ───────────────────────────────────────
    @Transactional
    public void markAllAsRead(Integer staffId) {
        staffNotifRepo.markAllAsRead(staffId);
    }

    // ── [UC-29 mở rộng] Gửi thông báo đến tất cả staff của 1 event ──
    /**
     * @param eventId       Event cần gửi
     * @param senderStaffId Staff người gửi (null = hệ thống)
     * @param title         Tiêu đề
     * @param message       Nội dung
     * @param sendInApp     Có gửi in-app không
     * @param sendEmail     Có gửi email không
     * @return              Số staff được gửi
     */
    @Transactional
    public int sendToEventStaff(
            Integer eventId,
            Integer senderStaffId,
            String title,
            String message,
            boolean sendInApp,
            boolean sendEmail) {

        // 1. Lấy danh sách staff active của event
        List<Staff> staffList = staffRepo.findActiveStaffByEventId(eventId);
        if (staffList == null || staffList.isEmpty()) return 0;

        // 2. Lấy event + sender
        Event event = eventRepo.findById(eventId).orElse(null);
        Staff sender = senderStaffId != null
                ? staffRepo.findById(senderStaffId).orElse(null)
                : null;

        String eventTitle = event != null ? event.getTitle() : "Event Go";
        String senderName = sender != null ? sender.getFullName() : "Ban tổ chức";
        Instant now = Instant.now();
        int count = 0;

        for (Staff recipient : staffList) {
            // Không gửi cho chính người gửi
            if (senderStaffId != null && recipient.getId().equals(senderStaffId)) continue;

            // ── In-App ───────────────────────────────────────────────
            if (sendInApp) {
                try {
                    StaffNotification n = new StaffNotification();
                    n.setStaff(recipient);
                    n.setSenderStaff(sender);
                    n.setEvent(event);
                    n.setTitle(title);
                    n.setMessage(message);
                    n.setNotificationType("InApp");
                    n.setIsRead(false);
                    n.setSentAt(now);
                    staffNotifRepo.save(n);
                } catch (Exception e) {
                    System.err.println("[StaffNotifService] InApp failed for staff "
                            + recipient.getId() + ": " + e.getMessage());
                }
            }

            // ── Email ────────────────────────────────────────────────
            if (sendEmail && recipient.getEmail() != null) {
                try {
                    emailService.sendEventNotificationEmail(
                            recipient.getEmail(),
                            recipient.getFullName(),
                            eventTitle,
                            "[Staff] " + title,
                            message
                    );
                } catch (Exception e) {
                    System.err.println("[StaffNotifService] Email failed for "
                            + recipient.getEmail() + ": " + e.getMessage());
                }
            }

            count++;
        }

        return count;
    }

    // ── Hệ thống tự gửi (BR-54: notify sau approved/rejected) ────────
    @Transactional
    public void sendSystemNotification(Integer staffId, Integer eventId,
                                       String title, String message) {
        Staff recipient = staffRepo.findById(staffId).orElse(null);
        if (recipient == null) return;

        Event event = eventId != null ? eventRepo.findById(eventId).orElse(null) : null;

        StaffNotification n = new StaffNotification();
        n.setStaff(recipient);
        n.setSenderStaff(null);        // null = hệ thống gửi
        n.setEvent(event);
        n.setTitle(title);
        n.setMessage(message);
        n.setNotificationType("System");
        n.setIsRead(false);
        n.setSentAt(Instant.now());
        staffNotifRepo.save(n);
    }

    // ── Đếm số staff của event (hiển thị trên form) ──────────────────
    public int countStaffByEvent(Integer eventId) {
        List<Staff> staffList = staffRepo.findActiveStaffByEventId(eventId);
        return staffList == null ? 0 : staffList.size();
    }
}