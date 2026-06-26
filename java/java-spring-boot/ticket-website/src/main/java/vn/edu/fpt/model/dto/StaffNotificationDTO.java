package vn.edu.fpt.model.dto;

import vn.edu.fpt.model.entity.StaffNotification;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class StaffNotificationDTO {

    private Integer id;
    private String  title;
    private String  message;
    private String  notificationType;
    private Boolean isRead;
    private String  sentAt;           // ISO string cho JS
    private String  senderName;       // tên người gửi hoặc "Hệ thống"
    private String  eventTitle;

    private static final DateTimeFormatter FMT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
            .withZone(ZoneId.of("UTC"));

    public StaffNotificationDTO(StaffNotification n) {
        this.id              = n.getId();
        this.title           = n.getTitle();
        this.message         = n.getMessage();
        this.notificationType = n.getNotificationType();
        this.isRead          = n.getIsRead();
        this.sentAt          = n.getSentAt() != null ? FMT.format(n.getSentAt()) : null;
        this.senderName      = n.getSenderStaff() != null
                ? n.getSenderStaff().getFullName()
                : "Hệ thống";
        this.eventTitle      = n.getEvent() != null ? n.getEvent().getTitle() : null;
    }

    // ── Getters ──────────────────────────────────────────────────────

    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getNotificationType() { return notificationType; }
    public Boolean getIsRead() { return isRead; }
    public String getSentAt() { return sentAt; }
    public String getSenderName() { return senderName; }
    public String getEventTitle() { return eventTitle; }
}