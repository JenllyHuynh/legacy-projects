package vn.edu.fpt.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Venues")
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VenueId", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "Name", nullable = false)
    private String name;

    @Nationalized
    @Column(name = "Street")
    private String street;

    @Nationalized
    @Column(name = "Ward", length = 100)
    private String ward;

    @Nationalized
    @Column(name = "City", length = 100)
    private String city;

    @Nationalized
    @Column(name = "Country", length = 100)
    private String country;

    @Column(name = "Capacity")
    private Integer capacity;

    @Column(name = "latitude")
    private Double lat;

    @Column(name = "longitude")
    private Double lon;

    @ManyToOne
    @JoinColumn(name = "createdBy", updatable = false)
    private Staff staff;

    @ColumnDefault("getdate()")
    @Column(name = "CreatedAt")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime createdAt;

    @ColumnDefault("1")
    @Column(name = "IsActive")
    private Boolean isActive;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events = new ArrayList<>();


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getAddress() {
        return String.format("%s, %s", getStreet(), getWard());
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public void update(Venue venue) {
        this.setCountry(venue.getCountry());
        this.setWard(venue.getWard());
        this.setCity(venue.getCity());
        this.setStreet(venue.getStreet());
        this.setIsActive(venue.getIsActive());
        this.setCapacity(venue.getCapacity());
        this.setName(venue.getName());
        this.setLat(venue.getLat());
        this.setLon(venue.getLon());
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return "Venue{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", street='" + street + '\'' +
                ", ward='" + ward + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", capacity=" + capacity +
                ", createdAt=" + createdAt +
                ", isActive=" + isActive +
                '}';
    }
}