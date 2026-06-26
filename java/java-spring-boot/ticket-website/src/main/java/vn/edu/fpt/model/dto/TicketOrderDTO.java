package vn.edu.fpt.model.dto;

import java.math.BigDecimal;

public class TicketOrderDTO {
    private String name;
    private Long qty;
    private BigDecimal totalPrice;
    private BigDecimal unitPrice;
    private String ticketId;

    public TicketOrderDTO() {
    }


    public TicketOrderDTO(String name, Long qty, BigDecimal totalPrice, BigDecimal unitPrice, String ticketId) {
        this.name = name;
        this.qty = qty;
        this.totalPrice = totalPrice;
        this.unitPrice = unitPrice;
        this.ticketId = ticketId;
    }

    public TicketOrderDTO(String name, Long qty, BigDecimal totalPrice, BigDecimal unitPrice) {
        this.name = name;
        this.qty = qty;
        this.totalPrice = totalPrice;
        this.unitPrice = unitPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }
}
