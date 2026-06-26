package vn.edu.fpt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.model.dto.CheckInResultDTO;
import vn.edu.fpt.model.entity.Ticket;
import vn.edu.fpt.repository.TicketRepo;
import vn.edu.fpt.service.QrService;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class CheckInController {

    @Autowired
    private TicketRepo ticketRepo;

    // -------------------------------------------------------
    // 1. Trang giao diện scan QR — chỉ Staff mới truy cập được
    // -------------------------------------------------------
    @PreAuthorize("hasRole('STAFF')")
    @GetMapping("/staff/checkin")
    public String checkinPage(
            @RequestParam(required = false) Integer eventId,
            Model model
    ) {
        model.addAttribute("eventId", eventId);
        return "staff/checkin";
    }

    // -------------------------------------------------------
    // 2. API check-in — nhận ticketId + ticketCode từ QR
    //    QR encode URL: /api/checkin?t={ticketId}&c={ticketCode}
    //    Frontend truyền thêm &e={eventId} để validate đúng event
    // -------------------------------------------------------
    @PreAuthorize("hasRole('STAFF')")
    @GetMapping("/api/checkin")
    @ResponseBody
    public ResponseEntity<CheckInResultDTO> checkin(
            @RequestParam("t") String ticketIdStr,
            @RequestParam("c") String ticketCode,
            @RequestParam(value = "e", required = false) Integer eventId  // eventId của trang đang check-in
    ) {
        // --- Bước 1: Validate format ticketId ---
        int ticketId;
        try {
            ticketId = Integer.parseInt(ticketIdStr);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(new CheckInResultDTO(false, "QR_INVALID", "Invalid QR code."));
        }

        // --- Bước 2: Kiểm tra ticket tồn tại (BR-60) ---
        Optional<Ticket> ticketOpt = ticketRepo.findById(ticketId);
        if (ticketOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new CheckInResultDTO(false, "TICKET_NOT_FOUND", "Ticket not found in the system."));
        }

        Ticket ticket = ticketOpt.get();

        // --- Bước 2b: Kiểm tra vé có thuộc đúng event đang check-in không ---
        if (eventId != null) {
            try {
                Integer ticketEventId = ticket.getTicketType().getEvent().getId();
                if (!eventId.equals(ticketEventId)) {
                    return ResponseEntity.badRequest()
                            .body(new CheckInResultDTO(false, "WRONG_EVENT",
                                    "This ticket does not belong to the current event."));
                }
            } catch (Exception e) {
                return ResponseEntity.internalServerError()
                        .body(new CheckInResultDTO(false, "SERVER_ERROR", "System error. Please try again."));
            }
        }

        // --- Bước 3: Hash ticketCode rồi so khớp với DB (BR-63, BR-64) ---
        try {
            String scannedHash = QrService.sha256(ticketCode);
            if (ticket.getTicketCodeHash() == null || !scannedHash.equals(ticket.getTicketCodeHash())) {
                return ResponseEntity.badRequest()
                        .body(new CheckInResultDTO(false, "SIG_INVALID", "Invalid or tampered QR code."));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new CheckInResultDTO(false, "SERVER_ERROR", "System error. Please try again."));
        }

        // --- Bước 4: Kiểm tra đã check-in chưa (BR-31, BR-61) ---
        if ("CheckedIn".equals(ticket.getTicketStatus())) {
            String checkedInTime = ticket.getCheckedInAt() != null
                    ? ticket.getCheckedInAt().toString()
                    : "unknown";
            return ResponseEntity.badRequest()
                    .body(new CheckInResultDTO(false, "ALREADY_CHECKED_IN",
                            "This ticket was already checked in at " + checkedInTime + "."));
        }

        // --- Bước 5: Cập nhật trạng thái (BR-65) ---
        ticket.setTicketStatus("CheckedIn");
        ticket.setCheckedInAt(LocalDateTime.now());
        ticketRepo.save(ticket);

        // --- Bước 6: Trả về thông tin để hiện popup ---
        String ticketTypeName = "";
        String eventName = "";
        try {
            ticketTypeName = ticket.getTicketType().getName();
            eventName = ticket.getTicketType().getEvent().getTitle();
        } catch (Exception ignored) {}

        CheckInResultDTO result = new CheckInResultDTO(true, "SUCCESS", "Check-in successful!");
        result.setTicketId(ticketId);
        result.setTicketNumber(ticket.getTicketNumber());
        result.setTicketType(ticketTypeName);
        result.setEventName(eventName);
        result.setCheckedInAt(ticket.getCheckedInAt().toString());

        return ResponseEntity.ok(result);
    }
}