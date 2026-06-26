package vn.edu.fpt.model.dto;

import java.util.List;

/**
 * DTO nhận dữ liệu từ form gửi thông báo (UC-29).
 */
public class SendNotificationRequest {

    /** Tiêu đề thông báo */
    private String title;

    /** Nội dung thông báo */
    private String message;

    /**
     * Phạm vi gửi: "ALL" = tất cả attendees của event.
     * Extensible: sau có thể thêm "TICKET_TYPE" v.v.
     */
    private String recipientScope = "ALL";

    /**
     * Kênh gửi: ["INAPP"], ["EMAIL"], hoặc ["INAPP","EMAIL"].
     * Map sang checkbox name="channels" trong form.
     */
    private List<String> channels;

    // ── Getters / Setters ───────────────────────────────────────────

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getRecipientScope() { return recipientScope; }
    public void setRecipientScope(String recipientScope) { this.recipientScope = recipientScope; }

    public List<String> getChannels() { return channels; }
    public void setChannels(List<String> channels) { this.channels = channels; }
}