package vn.edu.fpt.model.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
public class EventStaffAssignmentId implements Serializable {
    private static final long serialVersionUID = -5527973860114095843L;

    private Integer eventId;
    private Integer staffId;
    private Integer roleId;
    private Integer assignedBy; // 🔥 BẮT BUỘC (thiếu cái này là lỗi)

    public EventStaffAssignmentId() {
    }

    public EventStaffAssignmentId(Integer eventId, Integer staffId, Integer roleId) {
        this.eventId = eventId;
        this.staffId = staffId;
        this.roleId = roleId;
    }

    public EventStaffAssignmentId(Integer eventId, Integer staffId, Integer roleId, Integer assignedBy) {
        this.eventId = eventId;
        this.staffId = staffId;
        this.roleId = roleId;
        this.assignedBy = assignedBy;
    }

    public Integer getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(Integer assignedBy) {
        this.assignedBy = assignedBy;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}