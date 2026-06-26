package vn.edu.fpt.model.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StaffEventAttendeeDTO {

    private Integer       eventId;
    private String        title;
    private String        venueName;
    private String        city;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String        eventThumbnailURL;
    private String        eventStatus;
    private Long          totalRegistered;
    private Long          totalCheckedIn;

    // ── Constructor 8 tham số — dùng cho getEventsByStaff ────────────────
    // Không COUNT/SUM trong query → tránh đếm nhầm ticket Available.
    // totalRegistered và totalCheckedIn được service set sau.
    public StaffEventAttendeeDTO(Integer eventId,
                                 String title,
                                 String venueName,
                                 String city,
                                 LocalDateTime startDateTime,
                                 LocalDateTime endDateTime,
                                 String eventThumbnailURL,
                                 String eventStatus) {
        this.eventId           = eventId;
        this.title             = title;
        this.venueName         = venueName;
        this.city              = city;
        this.startDateTime     = startDateTime;
        this.endDateTime       = endDateTime;
        this.eventThumbnailURL = eventThumbnailURL;
        this.eventStatus       = eventStatus;
        this.totalRegistered   = 0L;
        this.totalCheckedIn    = 0L;
    }

    // ── Constructor 7 tham số — dùng cho getEventSummary ─────────────────
    // Chỉ cần thông tin cơ bản để hiển thị header trên attendeeList,
    // checkInStatus, export — không cần count hay eventStatus.
    public StaffEventAttendeeDTO(Integer eventId,
                                 String title,
                                 String venueName,
                                 String city,
                                 LocalDateTime startDateTime,
                                 LocalDateTime endDateTime,
                                 String eventThumbnailURL) {
        this.eventId           = eventId;
        this.title             = title;
        this.venueName         = venueName;
        this.city              = city;
        this.startDateTime     = startDateTime;
        this.endDateTime       = endDateTime;
        this.eventThumbnailURL = eventThumbnailURL;
        this.eventStatus       = null;
        this.totalRegistered   = 0L;
        this.totalCheckedIn    = 0L;
    }

    public double getCheckinRate() {
        if (totalRegistered == 0) return 0;
        return (totalCheckedIn * 100.0) / totalRegistered;
    }

    public String getFormattedEventDate() {

        if (startDateTime == null || endDateTime == null) {
            return "";
        }

        DateTimeFormatter monthDay = DateTimeFormatter.ofPattern("MMM d");
        DateTimeFormatter full = DateTimeFormatter.ofPattern("MMM d, yyyy");

        if (startDateTime.getYear() != endDateTime.getYear()) {
            return startDateTime.format(full) + " – " + endDateTime.format(full);
        }

        if (startDateTime.getMonth() != endDateTime.getMonth()) {
            return startDateTime.format(monthDay) + " – " + endDateTime.format(full);
        }

        if (startDateTime.getDayOfMonth() != endDateTime.getDayOfMonth()) {
            return startDateTime.format(monthDay) + "–" +
                    endDateTime.format(DateTimeFormatter.ofPattern("d, yyyy"));
        }

        return startDateTime.format(full);
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Long getTotalCheckedIn() {
        return totalCheckedIn;
    }

    public void setTotalCheckedIn(Long totalCheckedIn) {
        this.totalCheckedIn = totalCheckedIn;
    }

    public Long getTotalRegistered() {
        return totalRegistered;
    }

    public void setTotalRegistered(Long totalRegistered) {
        this.totalRegistered = totalRegistered;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }

    public String getEventThumbnailURL() {
        return eventThumbnailURL;
    }

    public void setEventThumbnailURL(String eventThumbnailURL) {
        this.eventThumbnailURL = eventThumbnailURL;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
