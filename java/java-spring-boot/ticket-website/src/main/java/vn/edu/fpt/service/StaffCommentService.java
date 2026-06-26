package vn.edu.fpt.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.dto.StaffCommentDTO;
import vn.edu.fpt.model.dto.StaffEventSimpleDTO;
import vn.edu.fpt.model.entity.Staff;
import vn.edu.fpt.repository.StaffCommentRepository;
import vn.edu.fpt.repository.StaffRepo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

// ── Phía Staff (UC-30) ────────────────────────────────────────────────────────
@Service
public class StaffCommentService {

    private final StaffCommentRepository staffCommentRepository;
    private final StaffRepo              staffRepo;

    public StaffCommentService(StaffCommentRepository staffCommentRepository,
                               StaffRepo staffRepo) {
        this.staffCommentRepository = staffCommentRepository;
        this.staffRepo              = staffRepo;
    }

    /**
     * Lấy staffId của người đang đăng nhập từ SecurityContext.
     * Trả về null nếu chưa đăng nhập hoặc email không khớp với Staff nào.
     *
     * Controller gọi method này thay vì tự inject StaffRepo hay StaffService —
     * giữ cho Controller chỉ phụ thuộc vào StaffCommentService duy nhất.
     */
    public Integer getAuthenticatedStaffId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        Optional<Staff> staffOpt = staffRepo.findByEmail(auth.getName());
        return staffOpt.map(Staff::getId).orElse(null);
    }

    /**
     * Lấy danh sách comments (phân trang + filter).
     * BR-41 enforce trong Repository qua JOIN EventStaffAssignments.
     */
    public Page<StaffCommentDTO> getCommentsForStaff(Integer staffId,
                                                     String keyword,
                                                     Integer eventId,
                                                     Integer rating,
                                                     Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int limit  = pageable.getPageSize();

        List<Object[]> rows = staffCommentRepository
                .findCommentsForStaffRaw(staffId, keyword, eventId, rating, offset, limit);

        long total = staffCommentRepository
                .countCommentsForStaff(staffId, keyword, eventId, rating);

        List<StaffCommentDTO> dtos = rows.stream()
                .map(this::mapRowToDto)
                .toList();

        return new PageImpl<>(dtos, pageable, total);
    }

    /** Tổng số comments (stats card). */
    public long countByStaffId(Integer staffId) {
        return staffCommentRepository.countByStaffId(staffId);
    }

    /** Số comments mới hôm nay (stats card). */
    public long countTodayByStaffId(Integer staffId) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        return staffCommentRepository.countTodayByStaffId(staffId, startOfDay);
    }

    /** Rating trung bình (stats card). */
    public double avgRatingByStaffId(Integer staffId) {
        return staffCommentRepository.avgRatingByStaffId(staffId);
    }

    /**
     * BR-41 guard: kiểm tra comment có thuộc event được assign cho Staff không.
     * Bắt buộc gọi trước deleteById().
     */
    public boolean isCommentOwnedByStaff(Integer commentId, Integer staffId) {
        return staffCommentRepository.existsByIdAndStaffIdRaw(commentId, staffId) > 0;
    }

    /** Xóa comment theo ID (UC-30 Normal Flow step 7). */
    @Transactional
    public void deleteById(Integer commentId) {
        staffCommentRepository.deleteById(commentId);
    }

    /**
     * Danh sách Events mà Staff được assign (dùng cho dropdown filter trên UI).
     */
    public List<StaffEventSimpleDTO> getAssignedEvents(Integer staffId) {
        return staffCommentRepository.findAssignedEventsRaw(staffId)
                .stream()
                .map(row -> new StaffEventSimpleDTO(
                        ((Number) row[0]).intValue(),
                        (String)  row[1]
                ))
                .toList();
    }

    // ── Mapping helper ────────────────────────────────────────────────────────

    private StaffCommentDTO mapRowToDto(Object[] row) {
        Integer       commentId         = ((Number) row[0]).intValue();
        Integer       eventId           = ((Number) row[1]).intValue();
        String        eventTitle        = (String)  row[2];
        Integer       rating            = row[3] != null ? ((Number) row[3]).intValue() : null;
        String        content           = (String)  row[4];
        LocalDateTime createdAt         = toLocalDateTime(row[5]);
        String        customerFullName  = (String)  row[6];
        String        customerAvatarUrl = (String)  row[7];
        String        customerEmail     = (String)  row[8];

        return new StaffCommentDTO(
                commentId, eventId, eventTitle,
                rating, content, createdAt,
                customerFullName, customerAvatarUrl, customerEmail
        );
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value == null) return null;
        if (value instanceof java.sql.Timestamp ts) return ts.toLocalDateTime();
        if (value instanceof Instant instant)
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return null;
    }
}