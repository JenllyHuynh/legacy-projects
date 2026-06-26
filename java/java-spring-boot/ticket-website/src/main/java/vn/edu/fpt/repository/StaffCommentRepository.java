package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.model.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;

// ── Phía Staff (UC-30) ────────────────────────────────────────────────────────
//
// Dùng native SQL vì EventStaffAssignmentId chưa có field staffId.
//
// FIX BR-41 BUG: StaffId và IsActive phải nằm trong ON clause của JOIN,
// KHÔNG để ở WHERE clause.
//
// Lý do: nếu EventId 12 có cả StaffId 2 (IsActive=0) và StaffId 3 (IsActive=1),
// khi Staff 2 login mà điều kiện chỉ ở WHERE:
//   WHERE esa.StaffId = 2 AND esa.IsActive = 1
// → query đúng, nhưng nếu vô tình dùng OR hoặc logic sai thì match nhầm Staff 3.
//
// Để ON clause xử lý:
//   JOIN ... ON esa.EventId = c.EventId AND esa.StaffId = :staffId AND esa.IsActive = 1
// → chỉ JOIN đúng assignment của Staff đang login, loại hoàn toàn Staff khác.

public interface StaffCommentRepository extends JpaRepository<Comment, Integer> {

    // ── Danh sách comments có phân trang + filter (BR-41) ────────────────────

    @Query(value = """
        SELECT c.CommentId,
               c.EventId,
               e.Title            AS eventTitle,
               c.Rating,
               c.Comment          AS content,
               c.CreatedAt,
               COALESCE(cust.FullName, cust.Email) AS customerFullName,
               cust.AvatarUrl     AS customerAvatarUrl,
               cust.Email         AS customerEmail
        FROM   dbo.Comments c
        INNER JOIN dbo.Events e ON e.EventId = c.EventId
        INNER JOIN dbo.EventStaffAssignments esa
               ON  esa.EventId  = c.EventId
               AND esa.StaffId  = :staffId
               AND esa.IsActive = 1
        LEFT  JOIN dbo.Customers cust ON cust.CustomerId = c.CustomerId
        WHERE  (:eventId IS NULL OR c.EventId = :eventId)
          AND  (:rating  IS NULL OR c.Rating  = :rating)
          AND  (
               :keyword IS NULL OR :keyword = ''
            OR LOWER(CAST(c.Comment AS NVARCHAR(MAX))) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(cust.FullName)                    LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(cust.Email)                       LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        ORDER BY c.CreatedAt DESC
        OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY
        """, nativeQuery = true)
    List<Object[]> findCommentsForStaffRaw(
            @Param("staffId")  Integer staffId,
            @Param("keyword")  String  keyword,
            @Param("eventId")  Integer eventId,
            @Param("rating")   Integer rating,
            @Param("offset")   int     offset,
            @Param("limit")    int     limit
    );

    // ── Count cho pagination ──────────────────────────────────────────────────

    @Query(value = """
        SELECT COUNT(*)
        FROM   dbo.Comments c
        INNER JOIN dbo.EventStaffAssignments esa
               ON  esa.EventId  = c.EventId
               AND esa.StaffId  = :staffId
               AND esa.IsActive = 1
        LEFT  JOIN dbo.Customers cust ON cust.CustomerId = c.CustomerId
        WHERE  (:eventId IS NULL OR c.EventId = :eventId)
          AND  (:rating  IS NULL OR c.Rating  = :rating)
          AND  (
               :keyword IS NULL OR :keyword = ''
            OR LOWER(CAST(c.Comment AS NVARCHAR(MAX))) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(cust.FullName)                    LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(cust.Email)                       LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        """, nativeQuery = true)
    long countCommentsForStaff(
            @Param("staffId")  Integer staffId,
            @Param("keyword")  String  keyword,
            @Param("eventId")  Integer eventId,
            @Param("rating")   Integer rating
    );

    // ── Stats cards ──────────────────────────────────────────────────────────

    @Query(value = """
        SELECT COUNT(*)
        FROM   dbo.Comments c
        INNER JOIN dbo.EventStaffAssignments esa
               ON  esa.EventId  = c.EventId
               AND esa.StaffId  = :staffId
               AND esa.IsActive = 1
        """, nativeQuery = true)
    long countByStaffId(@Param("staffId") Integer staffId);

    @Query(value = """
        SELECT COUNT(*)
        FROM   dbo.Comments c
        INNER JOIN dbo.EventStaffAssignments esa
               ON  esa.EventId  = c.EventId
               AND esa.StaffId  = :staffId
               AND esa.IsActive = 1
        WHERE  c.CreatedAt >= :startOfDay
        """, nativeQuery = true)
    long countTodayByStaffId(
            @Param("staffId")    Integer staffId,
            @Param("startOfDay") LocalDateTime startOfDay
    );

    @Query(value = """
        SELECT COALESCE(AVG(CAST(c.Rating AS FLOAT)), 0.0)
        FROM   dbo.Comments c
        INNER JOIN dbo.EventStaffAssignments esa
               ON  esa.EventId  = c.EventId
               AND esa.StaffId  = :staffId
               AND esa.IsActive = 1
        WHERE  c.Rating IS NOT NULL
        """, nativeQuery = true)
    double avgRatingByStaffId(@Param("staffId") Integer staffId);

    // ── BR-41 guard trước khi xóa ────────────────────────────────────────────

    @Query(value = """
        SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END
        FROM   dbo.Comments c
        INNER JOIN dbo.EventStaffAssignments esa
               ON  esa.EventId  = c.EventId
               AND esa.StaffId  = :staffId
               AND esa.IsActive = 1
        WHERE  c.CommentId = :commentId
        """, nativeQuery = true)
    int existsByIdAndStaffIdRaw(
            @Param("commentId") Integer commentId,
            @Param("staffId")   Integer staffId
    );

    // ── Danh sách Events của Staff (cho dropdown filter) ─────────────────────

    @Query(value = """
        SELECT DISTINCT e.EventId, e.Title
        FROM   dbo.Events e
        INNER JOIN dbo.EventStaffAssignments esa
               ON  esa.EventId  = e.EventId
               AND esa.StaffId  = :staffId
               AND esa.IsActive = 1
        ORDER BY e.Title
        """, nativeQuery = true)
    List<Object[]> findAssignedEventsRaw(@Param("staffId") Integer staffId);
}