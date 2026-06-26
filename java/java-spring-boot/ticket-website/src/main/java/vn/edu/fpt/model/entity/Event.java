package vn.edu.fpt.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

@Entity
@Table(name = "Events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EventId", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "Title", nullable = false)
    private String title;

    @Nationalized
    @Lob
    @Column(name = "Description")
    private String description;

    @Nationalized
    @Column(name = "ShortDescription", length = 500)
    private String shortDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CategoryId")
    private Category category;

    @Nationalized
    @Column(name = "EventType", length = 20)
    private String eventType;

    @Nationalized
    @ColumnDefault("'Draft'")
    @Column(name = "EventStatus", length = 30)
    private String eventStatus;

    @Column(name = "StartDateTime", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "EndDateTime", nullable = false)
    private LocalDateTime endDateTime;

    @Column(name = "RegistrationDeadline")
    private LocalDateTime registrationDeadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VenueId")
    private Venue venue;

    @Nationalized
    @Column(name = "OnlineMeetingLink", length = 500)
    private String onlineMeetingLink;

    @Nationalized
    @Column(name = "BannerImageUrl", length = 500)
    private String bannerImageUrl;

    @Nationalized
    @Column(name = "ThumbnailUrl", length = 500)
    private String thumbnailUrl;

    @Column(name = "MaxAttendees")
    private Integer maxAttendees;

    @ColumnDefault("0")
    @Column(name = "CurrentAttendees")
    private Integer currentAttendees;

    @ColumnDefault("0")
    @Column(name = "ViewCount")
    private Integer viewCount;

    @ColumnDefault("0")
    @Column(name = "IsFeatured")
    private Boolean isFeatured;

    @ColumnDefault("'USD'")
    @Column(name = "Currency", length = 3)
    private String currency;

    @ColumnDefault("0")
    @Column(name = "IsFree")
    private Boolean isFree;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OrganizerId", nullable = false)
    private Organizer organizer;

    @ColumnDefault("getdate()")
    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @ColumnDefault("getdate()")
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "PublishedAt")
    private LocalDateTime publishedAt;

    @OneToMany(mappedBy = "event")
    private Set<Comment> comments = new LinkedHashSet<>();

    @OneToMany(mappedBy = "event")
    private Set<DiscountCode> discountCodes = new LinkedHashSet<>();

    @OneToMany(mappedBy = "event")
    private Set<EventStaffAssignment> eventStaffAssignments = new LinkedHashSet<>();
//
//    @OneToMany(mappedBy = "event")
//    private Set<Order> orders = new LinkedHashSet<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TicketType> ticketTypes = new LinkedHashSet<>();

    @Transient
    private double ticketSoldPercent;

    @Transient
    private BigDecimal revenue;

    @Transient
    private String startDateString;

    @Transient
    private String startTimeString;

    @Transient
    private Double viewCountPercent;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public LocalDateTime getRegistrationDeadline() {
        return registrationDeadline;
    }

    public void setRegistrationDeadline(LocalDateTime registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public String getOnlineMeetingLink() {
        return onlineMeetingLink;
    }

    public void setOnlineMeetingLink(String onlineMeetingLink) {
        this.onlineMeetingLink = onlineMeetingLink;
    }

    public String getBannerImageUrl() {
        return bannerImageUrl;
    }

    public void setBannerImageUrl(String bannerImageUrl) {
        this.bannerImageUrl = bannerImageUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Integer getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(Integer maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public Integer getCurrentAttendees() {
        return currentAttendees;
    }

    public void setCurrentAttendees(Integer currentAttendees) {
        this.currentAttendees = currentAttendees;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean getIsFree() {
        return isFree;
    }

    public void setIsFree(Boolean isFree) {
        this.isFree = isFree;
    }

    public Organizer getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Set<DiscountCode> getDiscountCodes() {
        return discountCodes;
    }

    public void setDiscountCodes(Set<DiscountCode> discountCodes) {
        this.discountCodes = discountCodes;
    }

    public Set<EventStaffAssignment> getEventStaffAssignments() {
        return eventStaffAssignments;
    }

    public void setEventStaffAssignments(Set<EventStaffAssignment> eventStaffAssignments) {
        this.eventStaffAssignments = eventStaffAssignments;
    }

//    public Set<Order> getOrders() {
//        return orders;
//    }

//    public void setOrders(Set<Order> orders) {
//        this.orders = orders;
//    }

    public Set<TicketType> getTicketTypes() {
        return ticketTypes;
    }

    public void setTicketTypes(Set<TicketType> ticketTypes) {
        this.ticketTypes = ticketTypes;
    }

    public Boolean getFeatured() {
        return isFeatured;
    }

    public void setFeatured(Boolean featured) {
        isFeatured = featured;
    }

    public Boolean getFree() {
        return isFree;
    }

    public void setFree(Boolean free) {
        isFree = free;
    }

    public double getTicketSoldPercent() {
        return ticketSoldPercent;
    }

    public void setTicketSoldPercent(double ticketSoldPercent) {
        this.ticketSoldPercent = ticketSoldPercent;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public String getStartDateTimeString() {
        return startDateString;
    }

    public String getStartDateString() {
        return startDateString;
    }

    public void setStartDateString(String startDateString) {
        this.startDateString = startDateString;
    }

    public String getStartTimeString() {
        return startTimeString;
    }

    public void setStartTimeString(String startTimeString) {
        this.startTimeString = startTimeString;
    }

    public Double getViewCountPercent() {
        return viewCountPercent;
    }

    public void setViewCountPercent(Double viewCountPercent) {
        this.viewCountPercent = viewCountPercent;
    }
}