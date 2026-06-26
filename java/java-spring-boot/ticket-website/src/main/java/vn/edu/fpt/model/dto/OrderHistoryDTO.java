package vn.edu.fpt.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderHistoryDTO {

    private Integer orderId;

    private String eventName;
    private String eventThumbnailURL;
    private String venueName;

    private String ticketTypeName;
    private Integer quantity;

    private BigDecimal totalAmount;
    private String status;

    private LocalDateTime orderDate;

    private String currency;

    public OrderHistoryDTO() {
    }

    public OrderHistoryDTO(Integer orderId, String eventName, String eventThumbnailURL, String venueName, String ticketTypeName, Integer quantity, BigDecimal totalAmount, String status, LocalDateTime orderDate, String currency) {
        this.orderId = orderId;
        this.eventName = eventName;
        this.eventThumbnailURL = eventThumbnailURL;
        this.venueName = venueName;
        this.ticketTypeName = ticketTypeName;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderDate = orderDate;
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventThumbnailURL() {
        return eventThumbnailURL;
    }

    public void setEventThumbnailURL(String eventThumbnailURL) {
        this.eventThumbnailURL = eventThumbnailURL;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getTicketTypeName() {
        return ticketTypeName;
    }

    public void setTicketTypeName(String ticketTypeName) {
        this.ticketTypeName = ticketTypeName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
}
