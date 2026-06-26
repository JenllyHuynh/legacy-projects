package vn.edu.fpt.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "Tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TicketId", nullable = false)
    private Integer id;

    // Thêm qua hệ với ticketType để code crud vì bên đây chưa có
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "TicketTypeId", nullable = false)
    private TicketType ticketType;

    // Thêm mối quan hệ với order item theo db của giỏ hàng vì cái vé dính tới giỏ hàng
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "OrderItemId")
    private OrderItem orderItem;

    @Column(name = "TicketNumber", nullable = false, length = 50)
    private String ticketNumber;

    @Nationalized
    @Column(name = "QRCodeUrl", length = 500)
    private String qRCodeUrl;

    @Nationalized
    @ColumnDefault("'Available'")
    @Column(name = "TicketStatus", length = 20)
    private String ticketStatus;

    @ColumnDefault("getdate()")
    @Column(name = "IssuedAt")
    private LocalDateTime issuedAt;

    @Column(name = "CheckedInAt")
    private LocalDateTime checkedInAt;

    @Nationalized
    @Column(name = "TicketCodeHash", length = 256)
    private String ticketCodeHash;

    @Column(name = "ReservedAt")
    private LocalDateTime reservedAt;

    public LocalDateTime getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(LocalDateTime reservedAt) {
        this.reservedAt = reservedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getQRCodeUrl() {
        return qRCodeUrl;
    }

    public void setQRCodeUrl(String qRCodeUrl) {
        this.qRCodeUrl = qRCodeUrl;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public String getqRCodeUrl() {
        return qRCodeUrl;
    }

    public void setqRCodeUrl(String qRCodeUrl) {
        this.qRCodeUrl = qRCodeUrl;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    public LocalDateTime getCheckedInAt() {
        return checkedInAt;
    }

    public void setCheckedInAt(LocalDateTime checkedInAt) {
        this.checkedInAt = checkedInAt;
    }

    public String getTicketCodeHash() {
        return ticketCodeHash;
    }

    public void setTicketCodeHash(String ticketCodeHash) {
        this.ticketCodeHash = ticketCodeHash;
    }
}