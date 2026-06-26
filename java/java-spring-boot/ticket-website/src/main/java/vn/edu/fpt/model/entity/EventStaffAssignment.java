package vn.edu.fpt.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import vn.edu.fpt.model.dto.EventStaffAssignmentDTO;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "EventStaffAssignments")
public class EventStaffAssignment {

    @EmbeddedId
    private EventStaffAssignmentId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EventId", insertable = false, updatable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "StaffId", insertable = false, updatable = false)
    private Staff staff;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RoleId", insertable = false, updatable = false)
    private Role role;

    @ColumnDefault("getdate()")
    @Column(name = "AssignedAt")
    private LocalDateTime assignedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AssignedBy", insertable = false, updatable = false)
    private Staff assignedBy;

    @ColumnDefault("1")
    @Column(name = "IsActive")
    private Boolean isActive;

    public EventStaffAssignmentId getId() {
        return id;
    }

    public void setId(EventStaffAssignmentId id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Staff getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(Staff assignedBy) {
        this.assignedBy = assignedBy;
    }

    @Override
    public String toString() {
        return "EventStaffAssignment{" +
                ", event=" + event.getTitle() +
                ", staff=" + staff.getFullName() +
                ", role=" + role.getRoleName() +
                ", assignedAt=" + assignedAt +
                ", isActive=" + isActive +
                '}';
    }
}