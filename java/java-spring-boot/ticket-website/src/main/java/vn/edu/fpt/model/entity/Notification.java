package vn.edu.fpt.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Entity
@Table(name = "Notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NotificationId", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CustomerId", nullable = false)
    private Customer customer;

    // NULL = hệ thống tự tạo | NOT NULL = staff chủ động gửi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SenderStaffId", nullable = true)
    private Staff senderStaff;

    // NULL = thông báo hệ thống | NOT NULL = gắn với event cụ thể
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EventId", nullable = true)
    private Event event;

    @Nationalized
    @Column(name = "Title", nullable = false)
    private String title;

    @Nationalized
    @Lob
    @Column(name = "Message", nullable = false)
    private String message;

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

    // ── Getters / Setters ─────────────────────────────────────────────

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

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

    public Boolean getRead() { return isRead; }
    public void setRead(Boolean read) { isRead = read; }

    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }

    public Instant getReadAt() { return readAt; }
    public void setReadAt(Instant readAt) { this.readAt = readAt; }
}