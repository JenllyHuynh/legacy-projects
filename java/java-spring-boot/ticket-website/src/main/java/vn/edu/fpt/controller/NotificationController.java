package vn.edu.fpt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.model.dto.NotificationDTO;
import vn.edu.fpt.model.entity.Customer;
import vn.edu.fpt.repository.CustomerRepo;
import vn.edu.fpt.service.NotificationService;

import java.util.List;
import java.util.Map;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CustomerRepo customerRepo;

    // ── Helper: lấy email từ principal (hỗ trợ cả UserDetails và OAuth2User) ──
    private String getEmailFromPrincipal(Object principal) {
        if (principal == null) return null;

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            return ((OAuth2User) principal).getAttribute("email");
        }
        return null;
    }

    // ── Helper: lấy customerId từ principal ────────────
    private Integer getCurrentCustomerId(Object principal) {
        String email = getEmailFromPrincipal(principal);
        if (email == null) {
            throw new RuntimeException("Cannot determine email from principal");
        }
        Customer customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return customer.getId();
    }

    // ── MVC: Trang xem tất cả thông báo ──────────────────────
    @GetMapping("/user/notifications")
    public String notificationsPage(@AuthenticationPrincipal Object principal,
                                    Model model) {
        try {
            Integer customerId = getCurrentCustomerId(principal);
            List<NotificationDTO> notifications = notificationService.getAll(customerId);
            model.addAttribute("notifications", notifications);
            return "user/notifications";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/auth/login";
        }
    }

    // ── API: 5 thông báo gần nhất cho popup chuông ───────────
    @GetMapping("/api/notifications/recent")
    @ResponseBody
    public ResponseEntity<List<NotificationDTO>> getRecent(
            @AuthenticationPrincipal Object principal) {
        try {
            return ResponseEntity.ok(
                    notificationService.getRecent(getCurrentCustomerId(principal)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ── API: Số thông báo chưa đọc cho badge ─────────────────
    @GetMapping("/api/notifications/unread-count")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @AuthenticationPrincipal Object principal) {
        try {
            long count = notificationService.countUnread(getCurrentCustomerId(principal));
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("count", 0L));
        }
    }

    // ── API: Đánh dấu 1 thông báo đã đọc ────────────────────
    @PutMapping("/api/notifications/{id}/read")
    @ResponseBody
    public ResponseEntity<?> markAsRead(@PathVariable Integer id,
                                        @AuthenticationPrincipal Object principal) {
        try {
            notificationService.markAsRead(id, getCurrentCustomerId(principal));
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── API: Đánh dấu tất cả đã đọc ─────────────────────────
    @PutMapping("/api/notifications/read-all")
    @ResponseBody
    public ResponseEntity<?> markAllAsRead(
            @AuthenticationPrincipal Object principal) {
        try {
            notificationService.markAllAsRead(getCurrentCustomerId(principal));
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}