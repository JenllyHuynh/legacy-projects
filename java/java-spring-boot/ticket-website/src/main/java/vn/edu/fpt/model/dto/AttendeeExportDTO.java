package vn.edu.fpt.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AttendeeExportDTO {

    private String fullName;
    private String email;
    private String phone;
    private String ticketCode;
    private String ticketType;
    private String ticketStatus;
    private String orderStatus;
    private BigDecimal unitPrice;
    private Integer quantity;
    private LocalDateTime orderDate;
    private LocalDateTime checkinTime;

    public AttendeeExportDTO(String fullName, String email, String phone,
                             String ticketCode, String ticketType, String ticketStatus,
                             String orderStatus, BigDecimal unitPrice, Integer quantity,
                             LocalDateTime orderDate, LocalDateTime checkinTime) {
        this.fullName    = fullName;
        this.email       = email;
        this.phone       = phone;
        this.ticketCode  = ticketCode;
        this.ticketType  = ticketType;
        this.ticketStatus = ticketStatus;
        this.orderStatus = orderStatus;
        this.unitPrice   = unitPrice;
        this.quantity    = quantity;
        this.orderDate   = orderDate;
        this.checkinTime = checkinTime;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDateTime getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(LocalDateTime checkinTime) {
        this.checkinTime = checkinTime;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}