package vn.edu.fpt.model.dto;

public class NotificationHistoryDTO {

    private String title;
    private String message;
    private String notificationType;   // "InApp" | "Email"
    private String recipientScope;     // "Customers" | "Staff"
    private int    recipientCount;
    private String sentAtString;
    private int    readRate;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getNotificationType() { return notificationType; }
    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }

    public String getRecipientScope() { return recipientScope; }
    public void setRecipientScope(String recipientScope) { this.recipientScope = recipientScope; }

    public int getRecipientCount() { return recipientCount; }
    public void setRecipientCount(int recipientCount) { this.recipientCount = recipientCount; }

    public String getSentAtString() { return sentAtString; }
    public void setSentAtString(String sentAtString) { this.sentAtString = sentAtString; }

    public int getReadRate() { return readRate; }
    public void setReadRate(int readRate) { this.readRate = readRate; }
}