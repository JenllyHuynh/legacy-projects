package vn.edu.fpt.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.model.dto.StaffCommentDTO;
import vn.edu.fpt.model.dto.StaffEventSimpleDTO;
import vn.edu.fpt.service.StaffCommentService;

import java.util.List;

@Controller
@RequestMapping("/staff/comments")
public class StaffCommentController {

    private static final int PAGE_SIZE = 10;

    // Controller chỉ phụ thuộc vào 1 service duy nhất — mọi thứ đều đi qua đây.
    private final StaffCommentService staffCommentService;

    public StaffCommentController(StaffCommentService staffCommentService) {
        this.staffCommentService = staffCommentService;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-30 Normal Flow Step 1–3:
    //   Staff mở Comment Management → System hiển thị danh sách comments
    //   của các Events Staff được assign vào (BR-41).
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping
    public String listComments(
            @RequestParam(value = "keyword",  required = false, defaultValue = "") String  keyword,
            @RequestParam(value = "eventId",  required = false) Integer eventId,
            @RequestParam(value = "rating",   required = false) Integer rating,
            @RequestParam(value = "page",     required = false, defaultValue = "1") int page,
            Model model) {

        Integer staffId = staffCommentService.getAuthenticatedStaffId();
        if (staffId == null) return "redirect:/login";

        int pageIndex = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(pageIndex, PAGE_SIZE);

        Page<StaffCommentDTO> commentPage = staffCommentService
                .getCommentsForStaff(staffId, keyword, eventId, rating, pageable);

        List<StaffEventSimpleDTO> eventList = staffCommentService.getAssignedEvents(staffId);

        model.addAttribute("commentList",     commentPage.getContent());
        model.addAttribute("totalPages",      commentPage.getTotalPages());
        model.addAttribute("currentPage",     page);
        model.addAttribute("pageSize",        PAGE_SIZE);
        model.addAttribute("totalComments",   staffCommentService.countByStaffId(staffId));
        model.addAttribute("todayComments",   staffCommentService.countTodayByStaffId(staffId));
        model.addAttribute("avgRating",       staffCommentService.avgRatingByStaffId(staffId));
        model.addAttribute("eventList",       eventList);
        model.addAttribute("keyword",         keyword);
        model.addAttribute("selectedEventId", eventId);
        model.addAttribute("selectedRating",  rating);

        return "staff/comment/list";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-30 Normal Flow Step 4–8: Confirm delete → xóa → success message.
    // UC-30 AF-2: Cancel → đóng modal ở client, không gọi endpoint này.
    // UC-30 Exception: Lỗi hệ thống → error message.
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/delete")
    public String deleteComment(
            @RequestParam("id") Integer commentId,
            RedirectAttributes redirectAttributes) {

        Integer staffId = staffCommentService.getAuthenticatedStaffId();
        if (staffId == null) return "redirect:/login";

        try {
            // BR-41 guard
            if (!staffCommentService.isCommentOwnedByStaff(commentId, staffId)) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "You do not have permission to delete this comment.");
                return "redirect:/staff/comments";
            }

            staffCommentService.deleteById(commentId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Comment has been deleted successfully.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An error occurred while deleting the comment. Please try again.");
        }

        return "redirect:/staff/comments";
    }
}