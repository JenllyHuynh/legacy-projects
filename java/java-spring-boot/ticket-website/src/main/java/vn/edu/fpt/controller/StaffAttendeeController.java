package vn.edu.fpt.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.model.dto.AttendeeDTO;
import vn.edu.fpt.model.dto.AttendeeDetailDTO;
import vn.edu.fpt.model.dto.DistributionDTO;
import vn.edu.fpt.model.dto.StaffEventAttendeeDTO;
import vn.edu.fpt.service.AttendeeService;
import vn.edu.fpt.service.EventService;
import vn.edu.fpt.service.StaffCommentService;
import vn.edu.fpt.util.PageSetting;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/staff/attendee")
public class StaffAttendeeController {

    private final AttendeeService     attendeeService;
    private final EventService        eventService;
    private final StaffCommentService staffService;

    public StaffAttendeeController(AttendeeService attendeeService,
                                   EventService eventService,
                                   StaffCommentService staffService) {
        this.attendeeService = attendeeService;
        this.eventService    = eventService;
        this.staffService    = staffService;
    }

    // ── Attendee Management (danh sách event của staff) ───────────────────
    @GetMapping(value = "")
    public String attendeeManagement(
            Model model,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") String page) {

        Integer staffId = staffService.getAuthenticatedStaffId();
        if (staffId == null) return "redirect:/login";

        // Chuyển tab name → eventStatus
        String eventStatus = null;
        if (status != null && !status.isBlank()) {
            if (status.equalsIgnoreCase("live")) {
                eventStatus = "Live";
            } else if (status.equalsIgnoreCase("published")) {
                eventStatus = "Published";
            } else if (status.equalsIgnoreCase("completed")) {
                eventStatus = "Completed";
            }
        }

        Map<String, String> statusClassMap = Map.of(
                "Published", "bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400",
                "Completed", "bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400",
                "Cancelled", "bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400",
                "Rejected", "bg-slate-200 text-slate-700 dark:bg-slate-700 dark:text-slate-300",
                "Approved", "bg-teal-100 text-teal-700 dark:bg-teal-900/30 dark:text-teal-400",
                "PendingApproval", "bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400",
                "Draft", "bg-slate-100 text-slate-500 dark:bg-slate-800 dark:text-slate-400 border border-slate-200 dark:border-slate-700",
                "Live", "bg-red-600 text-white animate-pulse shadow-lg shadow-red-500/40"
        );

        Page<StaffEventAttendeeDTO> events = attendeeService.getEventsByStaffFiltered(staffId, keyword, eventStatus, page);

        model.addAttribute("statusClassMap", statusClassMap);
        model.addAttribute("events",         events);
        model.addAttribute("keyword",        keyword);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("totalEvents", events.getTotalElements());
        model.addAttribute("totalPages", events.getTotalPages());
        model.addAttribute("currentPage", events.getNumber());
        return "staff/attendee/attendeeManagement";
    }


    // ── Attendee List ─────────────────────────────────────────────────────
    @GetMapping("/list")
    public String attendeeList(Model model,
                               @RequestParam("eventId") String eventIdTemp,
                               @RequestParam(defaultValue = "0") String page,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String ticketType,
                               @RequestParam(required = false) String status) {

        Integer staffId = staffService.getAuthenticatedStaffId();
        if (staffId == null) return "redirect:/login";

        Integer eventId;
        try {
            eventId = Integer.parseInt(eventIdTemp);
        } catch (Exception e) {
            return "redirect:/staff/attendee?error=invalid_event";
        }
        //  Kiểm tra quyền
        if (!attendeeService.hasAccessToEvent(staffId, eventId)) {
            return "redirect:/staff/attendee?error=access_denied";
        }

        Page<AttendeeDTO> attendees = attendeeService
                .getAttendeesByEventFiltered(eventId, keyword, ticketType, status, page);

        model.addAttribute("ticketTypes",    attendeeService.getDistinctTicketTypes(eventId));
        model.addAttribute("keyword",        keyword);
        model.addAttribute("selectedType",   ticketType);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("totalPages",     attendees.getTotalPages());
        model.addAttribute("currentPage",    attendees.getNumber());
        model.addAttribute("totalAttendees", attendees.getTotalElements());
        model.addAttribute("attendees",      attendees);
        model.addAttribute("event",          eventService.getEventSummary(eventId));
        model.addAttribute("eventId",        eventId);
        return "staff/attendee/attendeeList";
    }

    // ── Attendee detail ────────────────────────────────────────────
    @GetMapping("/detail")
    @ResponseBody
    public AttendeeDetailDTO getAttendeeDetail(@RequestParam Integer ticketId) {
        return attendeeService.getAttendeeDetail(ticketId);
    }

    @PostMapping("/checkin")
    public String performCheckin(@RequestParam Integer ticketId,
                                 @RequestParam("eventId") String eventIdTemp) {

        Integer staffId = staffService.getAuthenticatedStaffId();
        if (staffId == null) return "redirect:/login";

        Integer eventId;
        try {
            eventId = Integer.parseInt(eventIdTemp);
        } catch (Exception e) {
            return "redirect:/staff/attendee?error=invalid_event";
        }
        // check quyền
        if (!attendeeService.hasAccessToEvent(staffId, eventId)) {
            return "redirect:/staff/attendee/list?eventId=" + eventId + "&error=access_denied";
        }

        // CHECK TIME Ở ĐÂY
        StaffEventAttendeeDTO event = eventService.getEventSummary(eventId);

        LocalDateTime now = LocalDateTime.now();

        // cho vào sớm 4 tiếng (bạn chỉnh số này nếu muốn)
        LocalDateTime allowCheckinTime = event.getStartDateTime().minusHours(4);

        //  chưa tới giờ check-in
        if (now.isBefore(allowCheckinTime)) {
            return "redirect:/staff/attendee/list?eventId=" + eventId + "&error=checkin_not_started";
        }

        //  event đã kết thúc
        if (now.isAfter(event.getEndDateTime())) {
            return "redirect:/staff/attendee/list?eventId=" + eventId + "&error=event_ended";
        }

        // OK thì check-in
        attendeeService.checkIn(ticketId);

        return "redirect:/staff/attendee/list?eventId=" + eventId + "&success=checked_in";
    }

    // ── Check-in Status ───────────────────────────────────────────────────
    @GetMapping(value = "/checkInStatus")
    public String attendeeCheckInStatus(Model model,
                                        @RequestParam("eventId") String eventIdTemp,
                                        @RequestParam(required = false) List<String> types) {

        Integer staffId = staffService.getAuthenticatedStaffId();
        if (staffId == null) return "redirect:/login";

        Integer eventId;
        try {
            eventId = Integer.parseInt(eventIdTemp);
        } catch (Exception e) {
            return "redirect:/staff/attendee?error=invalid_event";
        }
        //  Kiểm tra quyền
        if (!attendeeService.hasAccessToEvent(staffId, eventId)) {
            return "redirect:/staff/attendee?error=access_denied";
        }

        Pageable pageable = PageRequest.of(0, PageSetting.SIZE_OF_EACH_PAGE);

        Page<AttendeeDTO>     recentCheckins = attendeeService.getRecentCheckins(eventId, types, pageable);
        List<DistributionDTO> distributions  = attendeeService.getDistributionFiltered(eventId, types);
        List<String>          ticketTypes    = attendeeService.getDistinctTicketTypes(eventId);

        long   totalRegistered = attendeeService.countRegisteredFiltered(eventId, types);
        long   totalCheckedIn  = attendeeService.countCheckedInFiltered(eventId, types);
        double checkinRate     = totalRegistered > 0 ? (totalCheckedIn * 100.0 / totalRegistered) : 0;

        List<String> selectedTypes;
        if (types != null && !types.isEmpty()) {
            selectedTypes = types;
        } else {
            selectedTypes = List.of();
        }

        model.addAttribute("ticketTypes",         ticketTypes);
        model.addAttribute("selectedTypes",        selectedTypes);
        model.addAttribute("distributionTotal",    attendeeService.distributionTotal(distributions));
        model.addAttribute("distributions",        distributions);
        model.addAttribute("recentCheckins",       recentCheckins);
        model.addAttribute("totalRecentCheckins",  recentCheckins.getTotalElements());
        model.addAttribute("totalRegistered",      totalRegistered);
        model.addAttribute("totalCheckedIn",       totalCheckedIn);
        model.addAttribute("totalPending",         totalRegistered - totalCheckedIn);
        model.addAttribute("checkinRate",          checkinRate);
        model.addAttribute("event",                eventService.getEventSummary(eventId));
        model.addAttribute("eventId",              eventId);
        return "staff/attendee/attendeeCheckInStatus";
    }

    // ── Export page ───────────────────────────────────────────────────────
    @GetMapping(value = "/export")
    public String attendeeExport(Model model, @RequestParam("eventId") String eventIdTemp) {

        Integer staffId = staffService.getAuthenticatedStaffId();
        if (staffId == null) return "redirect:/login";

        Integer eventId;
        try {
            eventId = Integer.parseInt(eventIdTemp);
        } catch (Exception e) {
            return "redirect:/staff/attendee?error=invalid_event";
        }
        //  Kiểm tra quyền
        if (!attendeeService.hasAccessToEvent(staffId, eventId)) {
            return "redirect:/staff/attendee?error=access_denied";
        }

        model.addAttribute("event",         eventService.getEventSummary(eventId));
        model.addAttribute("eventId",       eventId);
        model.addAttribute("distributions", attendeeService.getDistribution(eventId));
        model.addAttribute("totalAttendees", attendeeService.countRegisteredFiltered(eventId, null));
        return "staff/attendee/attendeeExport";
    }

    // ── Export download ───────────────────────────────────────────────────
    @GetMapping(value = "/export/download")
    public ResponseEntity<byte[]> downloadExcel(
            @RequestParam Integer eventId,
            @RequestParam(required = false) List<String> columns) {
        try {
            if (columns == null || columns.isEmpty()) {
                columns = List.of("fullName", "email", "phone", "ticketCode",
                        "ticketType", "ticketStatus", "orderDate", "checkinTime");
            }

            byte[] excelBytes = attendeeService.exportAttendeesToExcel(eventId, columns);

            String filename = "attendees_event_" + eventId + "_"
                    + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelBytes);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}