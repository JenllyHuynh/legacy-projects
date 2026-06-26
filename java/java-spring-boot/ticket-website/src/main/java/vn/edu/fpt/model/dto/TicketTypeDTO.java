package vn.edu.fpt.model.dto;

import jakarta.validation.constraints.*;
//import lombok.Data;
//import lombok.Getter;
//import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

//@Data
//@Getter
//@Setter
public class TicketTypeDTO {

   private Integer id;

   @NotNull(message = "Event ID is required")
   private Integer eventId;

   @NotBlank(message ="Ticket name is required")
   @Size(min = 2, max = 100, message ="Ticket name must be between 2 and 100 character")
   @Pattern(
           regexp = "^(?=.*[a-zA-Z])[a-zA-Z0-9\\s\\-_()]+$",
           message = "Ticket type name must contain at least one letter and can only include letters, numbers, spaces, hyphens, underscores, and parentheses"
   )
   private String name;

   @Size(max = 500, message ="Description must be less than 500 characters")
   private String description;

   @NotNull(message ="Price is required")
   @DecimalMin(value ="0.0", inclusive = false, message ="Price must be greater than 0" )
   @Digits(integer = 10, fraction = 2, message = "Price must be a valid number with up to 2 decimal places")
   private BigDecimal price;

   @NotNull(message ="Quantity is required")
   @Min(value = 1, message = "Quantity must be at least 1")
   @Max(value = 1000000, message = "Quantity cannot exceed 1.000.000")
   private Integer quantity;

   @NotNull(message = "Max tickets per user is required")
   @Min(value = 1,message ="Max tickets per user must be at least 1")
   @Max(value = 100, message = "Max tickets per user cannot exceed 100")
   private Integer maxTicketsPerUser = 5;

   @NotNull(message ="Sales Star Date is required")
   private LocalDateTime salesStartDate;

   @NotNull(message ="Sales End Date is required")
   private LocalDateTime salesEndDate;

   private Boolean isActive = true;

   // Dành cho việc hiển thị
   private String eventTitle;
   private Integer soldTickets = 0;

    public TicketTypeDTO() {
    }

    public Integer getId() {
        return id;
    }

    public Integer getEventId() {
        return eventId;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getMaxTicketsPerUser() {
        return maxTicketsPerUser;
    }

    public LocalDateTime getSalesStartDate() {
        return salesStartDate;
    }

    public LocalDateTime getSalesEndDate() {
        return salesEndDate;
    }

    public Boolean getActive() {
        return isActive;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public Integer getSoldTickets() {
        return soldTickets;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public void setName(String name) {
        this.name = (name != null) ? name.trim() : null;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = (description != null) ? description.trim() : null;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setMaxTicketsPerUser(Integer maxTicketsPerUser) {
        this.maxTicketsPerUser = maxTicketsPerUser;
    }

    public void setSalesStartDate(LocalDateTime salesStartDate) {
        this.salesStartDate = salesStartDate;
    }

    public void setSalesEndDate(LocalDateTime salesEndDate) {
        this.salesEndDate = salesEndDate;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public void setSoldTickets(Integer soldTickets) {
        this.soldTickets = soldTickets;
    }

    // Convert cái LocalDate sang Instant
    public Instant getSalesStartDateAsInstant(){
        return salesStartDate != null ?
                salesStartDate.atZone(ZoneId.systemDefault()).toInstant() : null;
    }

    public Instant getSalesEndDateAsInstant() {
        return salesEndDate != null ?
                salesEndDate.atZone(ZoneId.systemDefault()).toInstant() : null;
    }

    // Convert ngược nó lại sang LocalDateTime
    public static LocalDateTime instantToLocalDateTime(Instant instant){
        return instant != null ?
                LocalDateTime.ofInstant(instant,ZoneId.systemDefault()) : null;
    }
}
