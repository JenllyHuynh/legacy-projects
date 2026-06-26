package vn.edu.fpt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.dto.NotificationDTO;
import vn.edu.fpt.model.dto.NotificationGroupDTO;
import vn.edu.fpt.model.dto.NotificationHistoryDTO;
import vn.edu.fpt.model.entity.Customer;
import vn.edu.fpt.model.entity.Event;
import vn.edu.fpt.model.entity.Notification;
import vn.edu.fpt.model.entity.Staff;
import vn.edu.fpt.model.entity.StaffNotification;
import vn.edu.fpt.repository.CustomerRepo;
import vn.edu.fpt.repository.EventRepo;
import vn.edu.fpt.repository.NotificationRepo;
import vn.edu.fpt.repository.StaffNotificationRepo;
import vn.edu.fpt.repository.StaffRepo;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired private NotificationRepo      notificationRepository;
    @Autowired private StaffNotificationRepo staffNotifRepo;
    @Autowired private CustomerRepo          customerRepo;
    @Autowired private EmailService          emailService;
    @Autowired private EventRepo             eventRepo;
    @Autowired private StaffRepo             staffRepo;


    // ── Customer notification methods (giữ nguyên) ───────────────────

    public List<NotificationDTO> getAll(Integer customerId) {
        return notificationRepository
                .findByCustomer_IdOrderBySentAtDesc(customerId)
                .stream().map(NotificationDTO::new).collect(Collectors.toList());
    }

    public List<NotificationDTO> getRecent(Integer customerId) {
        return notificationRepository
                .findTop5ByCustomer_IdOrderBySentAtDesc(customerId)
                .stream().map(NotificationDTO::new).collect(Collectors.toList());
    }

    public long countUnread(Integer customerId) {
        return notificationRepository.countByCustomer_IdAndIsReadFalse(customerId);
    }

    @Transactional
    public void markAsRead(Integer notificationId, Integer customerId) {
        notificationRepository.markAsRead(notificationId, customerId);
    }

    @Transactional
    public void markAllAsRead(Integer customerId) {
        notificationRepository.markAllAsRead(customerId);
    }

    @Transactional
    public void send(Integer customerId, String title, String message, String type) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));
        Notification n = new Notification();
        n.setCustomer(customer);
        n.setSenderStaff(null);
        n.setEvent(null);
        n.setTitle(title);
        n.setMessage(message);
        n.setNotificationType(type);
        n.setIsRead(false);
        n.setSentAt(Instant.now());
        notificationRepository.save(n);
    }

    @Transactional
    public int sendToEventAttendees(
            Integer eventId,
            String title,
            String message,
            String type,
            boolean sendInApp,
            boolean sendEmail,
            Integer senderStaffId) {

        List<Customer> attendees = customerRepo.findDistinctCustomersByEventId(eventId);
        if (attendees == null || attendees.isEmpty()) return 0;

        Event event = eventRepo.findById(eventId).orElse(null);
        Staff senderStaff = senderStaffId != null
                ? staffRepo.findById(senderStaffId).orElse(null) : null;
        String eventTitle = event != null ? event.getTitle() : "Event Go";

        Instant now = Instant.now();
        int successCount = 0;
        List<String> emailErrors = new ArrayList<>();

        for (Customer customer : attendees) {
            if (sendInApp) {
                try {
                    Notification n = new Notification();
                    n.setCustomer(customer);
                    n.setSenderStaff(senderStaff);
                    n.setEvent(event);
                    n.setTitle(title);
                    n.setMessage(message);
                    n.setNotificationType("InApp");
                    n.setIsRead(false);
                    n.setSentAt(now);
                    notificationRepository.save(n);
                } catch (Exception e) {
                    System.err.println("[NotificationService] InApp failed for customer "
                            + customer.getId() + ": " + e.getMessage());
                }
            }
            if (sendEmail && customer.getEmail() != null && !customer.getEmail().isBlank()) {
                try {
                    emailService.sendEventNotificationEmail(
                            customer.getEmail(), customer.getFullName(),
                            eventTitle, title, message);
                } catch (Exception e) {
                    emailErrors.add(customer.getEmail());
                }
            }
            successCount++;
        }
        return successCount;
    }

    public int countAttendeesByEvent(Integer eventId) {
        List<Customer> attendees = customerRepo.findDistinctCustomersByEventId(eventId);
        return attendees == null ? 0 : attendees.size();
    }


    // ── Notification History — merge cả 2 bảng ───────────────────────

    public List<NotificationGroupDTO> getNotificationHistory() {
        List<Event> events = eventRepo.findAll();
        List<NotificationGroupDTO> groups = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("MMM dd, yyyy · HH:mm", Locale.ENGLISH)
                .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));

        for (Event event : events) {

            // ── Lấy từ bảng Notifications (gửi cho Customer) ─────────
            List<Notification> customerNotifs =
                    notificationRepository.findByEventId(event.getId());

            // ── Lấy từ bảng StaffNotifications (gửi cho Staff) ───────
            List<StaffNotification> staffNotifs =
                    staffNotifRepo.findByEvent_IdOrderBySentAtDesc(event.getId());

            // Nếu cả 2 đều rỗng thì bỏ qua event này
            // Chỗ này quyết định có hiện trong list hay không!
            if (customerNotifs.isEmpty() && staffNotifs.isEmpty()) continue;

            Map<String, NotificationHistoryDTO> batchMap = new LinkedHashMap<>();

            // ── Group customer notifications ──────────────────────────
            for (Notification n : customerNotifs) {
                String minuteKey = formatter.format(n.getSentAt()).substring(0, 16);
                // Thêm "C|" để phân biệt với batch staff cùng title
                String batchKey  = "C|" + n.getTitle() + "|" + n.getNotificationType() + "|" + minuteKey;

                if (!batchMap.containsKey(batchKey)) {
                    NotificationHistoryDTO dto = new NotificationHistoryDTO();
                    dto.setTitle(n.getTitle());
                    dto.setMessage(n.getMessage());
                    dto.setNotificationType(n.getNotificationType());
                    dto.setRecipientScope("Customers");
                    dto.setSentAtString(formatter.format(n.getSentAt()));
                    dto.setRecipientCount(0);
                    dto.setReadRate(0);
                    batchMap.put(batchKey, dto);
                }

                NotificationHistoryDTO dto = batchMap.get(batchKey);
                dto.setRecipientCount(dto.getRecipientCount() + 1);

                // Tính read rate
                if ("InApp".equals(n.getNotificationType())) {
                    long read = customerNotifs.stream()
                            .filter(x -> x.getTitle().equals(n.getTitle())
                                    && Boolean.TRUE.equals(x.getIsRead()))
                            .count();
                    int rate = dto.getRecipientCount() > 0
                            ? (int) Math.round((double) read / dto.getRecipientCount() * 100)
                            : 0;
                    dto.setReadRate(Math.min(rate, 100));
                }
            }

            // ── Group staff notifications ─────────────────────────────
            for (StaffNotification n : staffNotifs) {
                String minuteKey = formatter.format(n.getSentAt()).substring(0, 16);
                // Thêm "S|" để phân biệt với batch customer cùng title
                String batchKey  = "S|" + n.getTitle() + "|" + n.getNotificationType() + "|" + minuteKey;

                if (!batchMap.containsKey(batchKey)) {
                    NotificationHistoryDTO dto = new NotificationHistoryDTO();
                    dto.setTitle(n.getTitle());
                    dto.setMessage(n.getMessage());
                    dto.setNotificationType(n.getNotificationType());
                    dto.setRecipientScope("Staff");
                    dto.setSentAtString(formatter.format(n.getSentAt()));
                    dto.setRecipientCount(0);
                    dto.setReadRate(0);
                    batchMap.put(batchKey, dto);
                }

                NotificationHistoryDTO dto = batchMap.get(batchKey);
                dto.setRecipientCount(dto.getRecipientCount() + 1);

                // Read rate cho staff notifications
                if ("InApp".equals(n.getNotificationType())) {
                    long read = staffNotifs.stream()
                            .filter(x -> x.getTitle().equals(n.getTitle())
                                    && Boolean.TRUE.equals(x.getIsRead()))
                            .count();
                    int rate = dto.getRecipientCount() > 0
                            ? (int) Math.round((double) read / dto.getRecipientCount() * 100)
                            : 0;
                    dto.setReadRate(Math.min(rate, 100));
                }
            }

            // ── Build group ───────────────────────────────────────────
            // Tìm thời gian gửi gần nhất trong cả 2 nguồn
            Instant lastCustomer = customerNotifs.isEmpty() ? null
                    : customerNotifs.get(0).getSentAt();
            Instant lastStaff = staffNotifs.isEmpty() ? null
                    : staffNotifs.get(0).getSentAt();
            Instant lastSent = lastCustomer == null ? lastStaff
                    : lastStaff == null ? lastCustomer
                    : lastCustomer.isAfter(lastStaff) ? lastCustomer : lastStaff;

            NotificationGroupDTO group = new NotificationGroupDTO();
            group.setEventId(event.getId());
            group.setEventTitle(event.getTitle());
            group.setThumbnailUrl(event.getThumbnailUrl());
            group.setNotifications(new ArrayList<>(batchMap.values()));
            group.setNotificationCount(batchMap.size());
            group.setLastSentString(lastSent != null ? formatter.format(lastSent) : "-");
            groups.add(group);
        }

        return groups;
    }
}