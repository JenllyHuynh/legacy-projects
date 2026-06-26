package vn.edu.fpt.model.dto;


import vn.edu.fpt.model.entity.Event;
import vn.edu.fpt.model.entity.EventStaffAssignmentId;
import vn.edu.fpt.model.entity.Role;
import vn.edu.fpt.model.entity.Staff;


public class EventStaffAssignmentDTO {
    private int eventId;
    private int staffId;
    private int roleId;
    private boolean isActive;


    public EventStaffAssignmentDTO() {
    }


    public EventStaffAssignmentDTO(int eventId, int staffId, int roleId, boolean isActive) {
        this.eventId = eventId;
        this.staffId = staffId;
        this.roleId = roleId;
        this.isActive = isActive;
    }


    public int getEventId() {
        return eventId;
    }


    public void setEventId(int eventId) {
        this.eventId = eventId;
    }


    public int getStaffId() {
        return staffId;
    }


    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }


    public int getRoleId() {
        return roleId;
    }


    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }


    public boolean getIsActive() {
        return isActive;
    }


    public void setIsActive(boolean active) {
        isActive = active;
    }


    public EventStaffAssignmentId getEventStaffId() {
        return new EventStaffAssignmentId(this.eventId, this.staffId, this.roleId);
    }


    @Override
    public String toString() {
        return "EventStaffAssignmentDTO{" +
                "event=" + eventId +
                ", staff=" + staffId +
                ", role=" + roleId +
                ", isActive=" + isActive +
                '}';
    }
}


