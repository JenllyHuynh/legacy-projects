package vn.edu.fpt.model.dto;

import vn.edu.fpt.model.entity.Category;
import vn.edu.fpt.model.entity.Venue;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class EventDTO {
    private Integer id;
    private String title;
    private LocalDateTime startDateTime;
    private Venue venue;
    private Category category;
    private String startDateTimeString;
    private BigDecimal minPrice;
    private String eventThumbnailURL;
    private String currency;

    private Double ratinAvg;

    private String eventStatus;

    public EventDTO() {
    }

    public EventDTO(Integer id, String title, LocalDateTime startDateTime, Venue venue, Category category,
                    String startDateTimeString, BigDecimal minPrice, String eventThumbnailURL,
                    String currency, Double ratinAvg) {
        this.id = id;
        this.title = title;
        this.startDateTime = startDateTime;
        this.venue = venue;
        this.category = category;
        this.startDateTimeString = startDateTimeString;
        this.minPrice = minPrice;
        this.eventThumbnailURL = eventThumbnailURL;
        this.currency = currency;
        this.ratinAvg = ratinAvg;
    }

    public EventDTO(LocalDateTime startDateTime, Integer id, String title, Venue venue,
                    Category category, BigDecimal minPrice, String eventThumbnailURL, String currency) {
        this.startDateTime = startDateTime;
        this.id = id;
        this.title = title;
        this.venue = venue;
        this.category = category;
        this.minPrice = minPrice;
        this.eventThumbnailURL = eventThumbnailURL;
        this.currency = currency;
        this.setStartDateTimeString();
    }

    public EventDTO(LocalDateTime startDateTime, Integer id, String title, Venue venue,
                    Category category, BigDecimal minPrice, String eventThumbnailURL, String currency, String eventStatus) {
        this.startDateTime = startDateTime;
        this.id = id;
        this.title = title;
        this.venue = venue;
        this.category = category;
        this.minPrice = minPrice;
        this.eventThumbnailURL = eventThumbnailURL;
        this.currency = currency;
        this.eventStatus = eventStatus;
        this.setStartDateTimeString();
    }

    public Double getRatinAvg() {
        return ratinAvg;
    }

    public void setRatinAvg(Double ratinAvg) {
        this.ratinAvg = ratinAvg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getStartDateTimeString() {
        return startDateTimeString;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setStartDateTimeString(String startDateTimeString) {
        this.startDateTimeString = startDateTimeString;
    }

    public String getEventThumbnailURL() {
        return eventThumbnailURL;
    }

    public void setEventThumbnailURL(String eventThumbnailURL) {
        this.eventThumbnailURL = eventThumbnailURL;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }

    public void setStartDateTimeString() {
        this.startDateTimeString =
                DateTimeFormatter
                        .ofPattern("EEE, MMM dd • HH:mm", Locale.ENGLISH)
                        .format(this.getStartDateTime());
    }

}
