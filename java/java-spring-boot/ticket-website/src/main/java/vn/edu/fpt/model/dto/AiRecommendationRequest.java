package vn.edu.fpt.model.dto;

import java.math.BigDecimal;
import java.util.List;

public class AiRecommendationRequest {
    private String message;
    private Integer customerId;
    private List<Long> eventHistory;
    private List<String> preferredCategories;
    private List<String> preferredVenues;
    private boolean isNewCustomer;

    // Thêm context lịch sử mua vé để AI tư vấn thật hơn
    private List<PurchaseContext> purchaseHistory;
    private BigDecimal avgSpentPerEvent;  // Mức chi tiêu trung bình
    private BigDecimal maxSpentPerEvent;  // Mức chi tiêu cao nhất

    public AiRecommendationRequest() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public List<Long> getEventHistory() {
        return eventHistory;
    }

    public void setEventHistory(List<Long> eventHistory) {
        this.eventHistory = eventHistory;
    }

    public List<String> getPreferredCategories() {
        return preferredCategories;
    }

    public void setPreferredCategories(List<String> preferredCategories) {
        this.preferredCategories = preferredCategories;
    }

    public List<String> getPreferredVenues() {
        return preferredVenues;
    }

    public void setPreferredVenues(List<String> preferredVenues) {
        this.preferredVenues = preferredVenues;
    }

    public boolean isNewCustomer() {
        return isNewCustomer;
    }

    public void setNewCustomer(boolean newCustomer) {
        isNewCustomer = newCustomer;
    }

    public List<PurchaseContext> getPurchaseHistory() {
        return purchaseHistory;
    }

    public void setPurchaseHistory(List<PurchaseContext> purchaseHistory) {
        this.purchaseHistory = purchaseHistory;
    }

    public BigDecimal getAvgSpentPerEvent() {
        return avgSpentPerEvent;
    }

    public void setAvgSpentPerEvent(BigDecimal avgSpentPerEvent) {
        this.avgSpentPerEvent = avgSpentPerEvent;
    }

    public BigDecimal getMaxSpentPerEvent() {
        return maxSpentPerEvent;
    }

    public void setMaxSpentPerEvent(BigDecimal maxSpentPerEvent) {
        this.maxSpentPerEvent = maxSpentPerEvent;
    }

    /**
     * Thông tin 1 lần mua vé — dùng để build context cho AI
     */
    public static class PurchaseContext {
        private String eventTitle;
        private String category;
        private String venue;
        private String ticketTypeName;
        private BigDecimal pricePaid;
        private String orderDate;

        public PurchaseContext() {
        }

        public PurchaseContext(String eventTitle, String category, String venue,
                               String ticketTypeName, BigDecimal pricePaid, String orderDate) {
            this.eventTitle = eventTitle;
            this.category = category;
            this.venue = venue;
            this.ticketTypeName = ticketTypeName;
            this.pricePaid = pricePaid;
            this.orderDate = orderDate;
        }

        public String getEventTitle() {
            return eventTitle;
        }

        public void setEventTitle(String eventTitle) {
            this.eventTitle = eventTitle;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getVenue() {
            return venue;
        }

        public void setVenue(String venue) {
            this.venue = venue;
        }

        public String getTicketTypeName() {
            return ticketTypeName;
        }

        public void setTicketTypeName(String ticketTypeName) {
            this.ticketTypeName = ticketTypeName;
        }

        public BigDecimal getPricePaid() {
            return pricePaid;
        }

        public void setPricePaid(BigDecimal pricePaid) {
            this.pricePaid = pricePaid;
        }

        public String getOrderDate() {
            return orderDate;
        }

        public void setOrderDate(String orderDate) {
            this.orderDate = orderDate;
        }

        @Override
        public String toString() {
            return eventTitle + " (" + category + " · " + venue + " · " + ticketTypeName + " · " + pricePaid + ")";
        }
    }
}