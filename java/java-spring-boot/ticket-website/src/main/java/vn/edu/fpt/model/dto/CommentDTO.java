package vn.edu.fpt.model.dto;

import java.time.LocalDateTime;

public record CommentDTO(
        int id,
        Integer rating,
        String comment,
        LocalDateTime updatedAt,
        String customerFullName,
        String avatarUrl,
        String username
) {
}
