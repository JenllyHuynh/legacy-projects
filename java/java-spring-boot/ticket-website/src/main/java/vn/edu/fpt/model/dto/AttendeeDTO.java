package vn.edu.fpt.model.dto;

import java.time.LocalDateTime;

public class AttendeeDTO {

    private Integer ticketId;
    private String fullName;
    private String email;
    private String avatarUrl;
    private String ticketCode;
    private String ticketType;
    private String status;
    private LocalDateTime checkinTime;

    public AttendeeDTO(Integer ticketId, LocalDateTime checkinTime, String status, String ticketType, String ticketCode, String avatarUrl, String email, String fullName) {
        this.ticketId = ticketId;
        this.checkinTime = checkinTime;
        this.status = status;
        this.ticketType = ticketType;
        this.ticketCode = ticketCode;
        this.avatarUrl = avatarUrl;
        this.email = email;
        this.fullName = fullName;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public LocalDateTime getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(LocalDateTime checkinTime) {
        this.checkinTime = checkinTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
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
}
