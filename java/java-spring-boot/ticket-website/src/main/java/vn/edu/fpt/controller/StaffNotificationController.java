package vn.edu.fpt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.model.dto.StaffNotificationDTO;
import vn.edu.fpt.model.entity.Staff;
import vn.edu.fpt.repository.StaffRepo;
import vn.edu.fpt.service.StaffNotificationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staff/notifications")
public class StaffNotificationController {

    @Autowired
    private StaffNotificationService staffNotifService;

    @Autowired
    private StaffRepo staffRepo;

    // ── Helper: lấy staff đang login từ SecurityContext ───────────────
    private Staff getCurrentStaff() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) return null;
            String email = auth.getName();
            return staffRepo.findByEmail(email).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    // ── GET /recent ───────────────────────────────────────────────────
    @GetMapping("/recent")
    public ResponseEntity<?> getRecent() {
        Staff staff = getCurrentStaff();
        if (staff == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        return ResponseEntity.ok(staffNotifService.getRecent(staff.getId()));
    }

    // ── GET /unread-count ─────────────────────────────────────────────
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount() {
        Staff staff = getCurrentStaff();
        if (staff == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        return ResponseEntity.ok(Map.of("count", staffNotifService.countUnread(staff.getId())));
    }

    // ── PUT /{id}/read ────────────────────────────────────────────────
    @PutMapping("/{id}/read")
    @Transactional
    public ResponseEntity<?> markAsRead(@PathVariable Integer id) {
        Staff staff = getCurrentStaff();
        if (staff == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        staffNotifService.markAsRead(id, staff.getId());
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ── PUT /read-all ─────────────────────────────────────────────────
    @PutMapping("/read-all")
    @Transactional
    public ResponseEntity<?> markAllAsRead() {
        Staff staff = getCurrentStaff();
        if (staff == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        staffNotifService.markAllAsRead(staff.getId());
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ── GET /all — dùng cho trang inbox ──────────────────────────────
    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        Staff staff = getCurrentStaff();
        if (staff == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        return ResponseEntity.ok(staffNotifService.getAll(staff.getId()));
    }
}