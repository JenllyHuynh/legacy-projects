package vn.edu.fpt.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Entity
@Table(name = "StaffNotifications")
public class StaffNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StaffNotificationId", nullable = false)
    private Integer id;

    /** Người NHẬN thông báo */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "StaffId", nullable = false)
    private Staff staff;

    /** Người GỬI — null nếu là thông báo hệ thống (approved/rejected) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SenderStaffId")
    private Staff senderStaff;

    /** Event liên quan */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EventId")
    private Event event;

    @Nationalized
    @Column(name = "Title", nullable = false, length = 200)
    private String title;

    @Nationalized
    @Lob
    @Column(name = "Message", nullable = false)
    private String message;

    /** "InApp" | "Email" | "System" */
    @Nationalized
    @Column(name = "NotificationType", length = 50)
    private String notificationType;

    @ColumnDefault("0")
    @Column(name = "IsRead")
    private Boolean isRead;

    @ColumnDefault("getdate()")
    @Column(name = "SentAt")
    private Instant sentAt;

    @Column(name = "ReadAt")
    private Instant readAt;

    // ── Getters / Setters ────────────────────────────────────────────

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Staff getStaff() { return staff; }
    public void setStaff(Staff staff) { this.staff = staff; }

    public Staff getSenderStaff() { return senderStaff; }
    public void setSenderStaff(Staff senderStaff) { this.senderStaff = senderStaff; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getNotificationType() { return notificationType; }
    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }

    public Instant getReadAt() { return readAt; }
    public void setReadAt(Instant readAt) { this.readAt = readAt; }
}