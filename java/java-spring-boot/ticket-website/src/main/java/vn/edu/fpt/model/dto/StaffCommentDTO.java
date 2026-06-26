package vn.edu.fpt.model.dto;

import java.time.LocalDateTime;

/**
 * DTO dùng cho Staff để quản lý/moderate comments (UC-30).
 *
 * Khác với CommentDTO (dùng phía Customer/guest), DTO này bao gồm:
 * - commentId         : để Staff xác định comment cần xóa
 * - eventId/eventTitle: hiển thị comment thuộc event nào
 * - createdAt         : thời điểm comment được đăng
 */
public record StaffCommentDTO(
        Integer commentId,
        Integer eventId,
        String  eventTitle,
        Integer rating,
        String  content,
        LocalDateTime createdAt,
        String  customerFullName,
        String  customerAvatarUrl,
        String  customerEmail
) {}