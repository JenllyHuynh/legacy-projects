package vn.edu.fpt.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MyTicketDTO {
    private Integer ticketId;

    private String ticketNumber;

    private String ticketStatus;

    private String qrCodeUrl;

    private String eventTitle;

    private LocalDateTime startDateTime;

    private String ticketTypeName;

    private BigDecimal price;

    private String thumbnailUrl;

    private String displayStatus;

    public MyTicketDTO(Integer ticketId, String ticketNumber, String ticketStatus, String qrCodeUrl, String eventTitle, LocalDateTime startDateTime, String ticketTypeName, BigDecimal price, String thumbnailUrl) {
        this.ticketId = ticketId;
        this.ticketNumber = ticketNumber;
        this.ticketStatus = ticketStatus;
        this.qrCodeUrl = qrCodeUrl;
        this.eventTitle = eventTitle;
        this.startDateTime = startDateTime;
        this.ticketTypeName = ticketTypeName;
        this.price = price;
        this.thumbnailUrl = thumbnailUrl;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getTicketTypeName() {
        return ticketTypeName;
    }

    public void setTicketTypeName(String ticketTypeName) {
        this.ticketTypeName = ticketTypeName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }
}
