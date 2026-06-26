package vn.edu.fpt.model.dto;

import java.time.LocalDateTime;

public class AttendeeDetailDTO {

    private String fullName;
    private String email;
    private String phone;
    private String avatarUrl;

    private String ticketCode;
    private String ticketType;
    private String ticketStatus;

    private LocalDateTime issuedDate;
    private LocalDateTime checkInTime; //hiện tại đã bỏ ra vì thấy không cân thiết trong detail nên giữ tạm để xem còn phát triển thêm được gì không, nếu không sẽ xóa sau

    private String eventName;
    private String venue;
    private String paymentStatus;

    public AttendeeDetailDTO(
            String fullName,
            String email,
            String phone,
            String avatarUrl,
            String ticketCode,
            String ticketType,
            String ticketStatus,
            LocalDateTime issuedDate,
            LocalDateTime checkInTime,
            String eventName,
            String venue,
            String paymentStatus
    ) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
        this.ticketCode = ticketCode;
        this.ticketType = ticketType;
        this.ticketStatus = ticketStatus;
        this.issuedDate = issuedDate;
        this.checkInTime = checkInTime;
        this.eventName = eventName;
        this.venue = venue;
        this.paymentStatus = paymentStatus;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public LocalDateTime getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDateTime issuedDate) {
        this.issuedDate = issuedDate;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
