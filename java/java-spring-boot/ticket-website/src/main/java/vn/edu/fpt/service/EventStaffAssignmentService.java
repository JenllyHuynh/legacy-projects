package vn.edu.fpt.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.dto.EventStaffAssignmentDTO;
import vn.edu.fpt.model.entity.*;
import vn.edu.fpt.repository.EventRepo;
import vn.edu.fpt.repository.EventStaffAssignmentRepo;
import vn.edu.fpt.repository.RoleRepo;
import vn.edu.fpt.repository.StaffRepo;
import vn.edu.fpt.util.PageSetting;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventStaffAssignmentService {

    private final EventStaffAssignmentRepo eventStaffAssignmentRepo;
    private final StaffRepo staffRepo;
    private final EventRepo eventRepo;
    private final RoleRepo roleRepo;


    public EventStaffAssignmentService(EventStaffAssignmentRepo eventStaffAssignmentRepo,
                                       StaffRepo staffRepo,
                                       EventRepo eventRepo,
                                       RoleRepo roleRepo) {
        this.eventStaffAssignmentRepo = eventStaffAssignmentRepo;
        this.staffRepo = staffRepo;
        this.eventRepo = eventRepo;
        this.roleRepo = roleRepo;
    }


    //truy vấn dữ liệu
    public List<EventStaffAssignment> getAll() {
        return eventStaffAssignmentRepo.findAll();
    }

    public void save(EventStaffAssignment eventStaffAssignment) {
        eventStaffAssignmentRepo.save(eventStaffAssignment);
    }

    public EventStaffAssignment getEventStaffById(EventStaffAssignmentDTO EventStaff) {
        EventStaffAssignmentId id = new EventStaffAssignmentId();
        id.setStaffId(EventStaff.getStaffId());
        id.setRoleId(EventStaff.getRoleId());
        id.setEventId(EventStaff.getEventId());
        return eventStaffAssignmentRepo.getEventStaffById(id);
    }

    public EventStaffAssignment getEventStaffById(EventStaffAssignmentId EventStaffId) {
        return eventStaffAssignmentRepo.getEventStaffById(EventStaffId);
    }

    //cập nhật và tạo mới dữ liệu
    @Transactional
    public void save(EventStaffAssignmentDTO newEventStaff, int adminId) {
            System.err.println("không tìm thấy staff!");
            EventStaffAssignment eventStaffAssignment = new EventStaffAssignment();
            eventStaffAssignment.setId(newEventStaff.getEventStaffId());
            eventStaffAssignment.setEvent(eventRepo.getEventById(newEventStaff.getEventId()));
            eventStaffAssignment.setStaff(staffRepo.findStaffById(newEventStaff.getEventId()));
            eventStaffAssignment.setRole(roleRepo.findRoleById(newEventStaff.getEventId()));
            eventStaffAssignment.getId().setAssignedBy(adminId);
            eventStaffAssignment.setIsActive(newEventStaff.getIsActive());
            LocalDateTime now = LocalDateTime.now();
            eventStaffAssignment.setAssignedAt(now);
            eventStaffAssignmentRepo.saveAndFlush(eventStaffAssignment);

    }

    @Transactional
    public void deleteEventStaff(String id) {
        EventStaffAssignmentId eventStaffId = new EventStaffAssignmentId();
        String[] ids = id.split("_");
        eventStaffId.setEventId(Integer.parseInt(ids[0]));
        eventStaffId.setStaffId(Integer.parseInt(ids[1]));
        eventStaffId.setRoleId(Integer.parseInt(ids[2]));
        eventStaffAssignmentRepo.delete(eventStaffAssignmentRepo.getEventStaffById(eventStaffId));
    }

    @Transactional
    public void deactiveEventStaff(String id) {
        try {
            System.err.println(id);
            EventStaffAssignmentId eventStaffId = new EventStaffAssignmentId();
            String[] ids = id.split("_");
            eventStaffId.setEventId(Integer.parseInt(ids[0]));
            eventStaffId.setStaffId(Integer.parseInt(ids[1]));
            eventStaffId.setRoleId(Integer.parseInt(ids[2]));
            EventStaffAssignment eventStaffAssignment = eventStaffAssignmentRepo.getEventStaffById(eventStaffId);
            eventStaffAssignment.setIsActive(!eventStaffAssignment.getIsActive());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Page<EventStaffAssignment> findEventStaffsWithCriteria(String filter, String name, int page) {
        Pageable pageable = PageRequest.of(page,
                PageSetting.SIZE_OF_EACH_DISCOUNT,
                Sort.by("staff.fullName").ascending());
        switch (filter) {
            case "all":
                if (name == null) {
                    return eventStaffAssignmentRepo.findAll(pageable);
                } else {
                    return eventStaffAssignmentRepo.findEventStaffAssignmentByName(name, pageable);
                }
            case "isActive":
                if (name == null) {
                    return eventStaffAssignmentRepo.findEventStaffAssignmentByInActive(pageable);
                } else {
                    return eventStaffAssignmentRepo.findEventStaffAssignmentByIsActiveAndName(name, pageable);
                }
            case "inActive":
                if (name == null) {
                    return eventStaffAssignmentRepo.findEventStaffAssignmentByInActive(pageable);
                } else {
                    return eventStaffAssignmentRepo.findEventStaffAssignmentByInActiveAndName(name, pageable);
                }
            default:
                return null;
        }
    }

    public void update(EventStaffAssignmentDTO newEventStaff) {
    }
}
