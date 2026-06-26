package vn.edu.fpt.model.dto;

/**
 * DTO đơn giản cho dropdown filter Events trên trang Comment Management (UC-30).
 * Không cần toàn bộ thông tin Event — chỉ cần id và title để render <option>.
 */
public record StaffEventSimpleDTO(
        Integer id,
        String  title
) {}