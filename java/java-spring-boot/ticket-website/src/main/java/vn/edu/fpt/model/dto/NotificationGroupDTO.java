package vn.edu.fpt.model.dto;

import java.util.List;

/**
 * Nhóm thông báo theo từng Event — dùng cho trang Notification History.
 */
public class NotificationGroupDTO {

    private Integer eventId;
    private String  eventTitle;
    private String  thumbnailUrl;
    private int     notificationCount;
    private String  lastSentString;     // "Mar 04, 2026 · 14:30"
    private List<NotificationHistoryDTO> notifications;

    // ── Getters / Setters ────────────────────────────────────────────

    public Integer getEventId() { return eventId; }
    public void setEventId(Integer eventId) { this.eventId = eventId; }

    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public int getNotificationCount() { return notificationCount; }
    public void setNotificationCount(int notificationCount) { this.notificationCount = notificationCount; }

    public String getLastSentString() { return lastSentString; }
    public void setLastSentString(String lastSentString) { this.lastSentString = lastSentString; }

    public List<NotificationHistoryDTO> getNotifications() { return notifications; }
    public void setNotifications(List<NotificationHistoryDTO> notifications) { this.notifications = notifications; }
}

