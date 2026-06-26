package vn.edu.fpt.model.dto;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class NotificationDTO {

    private Integer id;
    private String title;
    private String message;
    private String notificationType;
    private Boolean isRead;
    private Instant sentAt;
    private Instant readAt;
    private String sentAtFormatted;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("HH:mm - dd/MM/yyyy", Locale.ENGLISH)
            .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));

    public NotificationDTO(vn.edu.fpt.model.entity.Notification n) {
        this.id               = n.getId();
        this.title            = n.getTitle();
        this.message          = n.getMessage();
        this.notificationType = n.getNotificationType();
        this.isRead           = Boolean.TRUE.equals(n.getIsRead());
        this.sentAt           = n.getSentAt();
        this.readAt           = n.getReadAt();
        this.sentAtFormatted  = n.getSentAt() != null
                ? FORMATTER.format(n.getSentAt())
                : "";
    }

    public Integer getId()               { return id; }
    public String getTitle()             { return title; }
    public String getMessage()           { return message; }
    public String getNotificationType()  { return notificationType; }
    public Boolean getIsRead()           { return isRead; }
    public Instant getSentAt()           { return sentAt; }
    public Instant getReadAt()           { return readAt; }
    public String getSentAtFormatted()   { return sentAtFormatted; }
}