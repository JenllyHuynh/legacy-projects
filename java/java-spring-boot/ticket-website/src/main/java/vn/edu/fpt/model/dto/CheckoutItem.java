package vn.edu.fpt.model.dto;

public class CheckoutItem {

    private Integer ticketTypeId;
    private Integer eventId;
    private String ticketName;
    private Double price;
    private Integer quantity;

    public CheckoutItem(Integer ticketTypeId, String ticketName, Double price, Integer quantity) {
        this.ticketTypeId = ticketTypeId;
        this.ticketName = ticketName;
        this.price = price;
        this.quantity = quantity;
    }

    public CheckoutItem() {
    }

    public Integer getTicketTypeId() {
        return ticketTypeId;
    }

    public void setTicketTypeId(Integer ticketTypeId) {
        this.ticketTypeId = ticketTypeId;
    }

    public String getTicketName() {
        return ticketName;
    }

    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Double getSubtotal(){
        return price * quantity;
    }
}
