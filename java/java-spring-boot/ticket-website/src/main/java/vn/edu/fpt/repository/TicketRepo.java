package vn.edu.fpt.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.dto.*;
import vn.edu.fpt.model.entity.Ticket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepo extends JpaRepository<Ticket, Integer> {

    @Query("""
                SELECT new vn.edu.fpt.model.dto.MyTicketDTO (
                    t.id,
                    t.ticketNumber,
                    t.ticketStatus,
                    t.qRCodeUrl,
                    e.title,
                    e.startDateTime,
                    tt.name,
                    tt.price,
                    e.thumbnailUrl
                )
                FROM Ticket t
                JOIN t.orderItem oi
                JOIN oi.order o
                JOIN t.ticketType tt
                JOIN tt.event e
                WHERE o.customer.id = :customerId AND (t.ticketStatus = 'Sold' or t.ticketStatus = 'CheckedIn' or t.ticketStatus = 'Cancelled')
            """)
    List<MyTicketDTO> findTicketsByCustomer(@Param("customerId") Integer customerId);

    /**
     * Lấy tối đa :limit tickets Available của một TicketType
     * Dùng khi giảm quantity → cần xóa bớt tickets chưa bán
     */
    @Query("""
                SELECT t FROM Ticket t
                WHERE t.ticketType.id = :ticketTypeId
                  AND t.ticketStatus = 'Available'
                  AND t.orderItem IS NULL
                ORDER BY t.id DESC
            """)
    List<Ticket> findByTicketTypeIdAndStatusAvailable(
            @Param("ticketTypeId") Integer ticketTypeId,
            org.springframework.data.domain.Pageable pageable);

    /**
     * Overload tiện dùng: truyền số lượng tối đa cần lấy
     */
    default List<Ticket> findByTicketTypeIdAndStatusAvailable(Integer ticketTypeId, int limit) {
        return findByTicketTypeIdAndStatusAvailable(
                ticketTypeId,
                org.springframework.data.domain.PageRequest.of(0, limit));
    }

    /**
     * Đếm số ticket đã CheckedIn của một event (dùng cho progress bar trong Event Detail)
     */
    @Query("""
                SELECT COUNT(t) FROM Ticket t
                JOIN t.ticketType tt
                WHERE tt.event.id = :eventId
                  AND t.ticketStatus = 'CheckedIn'
            """)
    long countCheckedInByEvent(@Param("eventId") Integer eventId);

    /**
     * Đếm tổng số ticket đã được mua (Sold + CheckedIn) của một event
     */
    @Query("""
                SELECT COUNT(t) FROM Ticket t
                JOIN t.ticketType tt
                WHERE tt.event.id = :eventId
                  AND t.ticketStatus IN ('Sold', 'CheckedIn')
            """)
    long countRegisteredByEvent(@Param("eventId") Integer eventId);

    // Lock row ngay khi SELECT → người khác phải đợi transaction kết thúc.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select t
            from Ticket t
            where t.ticketStatus = 'Available'
            and t.ticketType.id = :ticketTypeId
            """)
    List<Ticket> findAvailableTickets(
            @Param("ticketTypeId") int ticketTypeId,
            Pageable pageable
    );

    @Query("""
            select t
            from Ticket t
            where t.ticketStatus = 'reserved'
            and t.reservedAt < :time
            """)
    List<Ticket> findExpiredReservedTickets(
            @Param("time") LocalDateTime time
    );

    @Query("""
            SELECT new vn.edu.fpt.model.dto.AttendeeDTO(
                t.id,
                t.checkedInAt,
                t.ticketStatus,
                tt.name,
                t.ticketNumber,
                c.avatarUrl,
                c.email,
                c.fullName
            )
            FROM Ticket t
            JOIN t.ticketType tt
            JOIN t.orderItem oi
            JOIN oi.order o
            JOIN o.customer c
            WHERE tt.event.id = :eventId
              AND (t.ticketStatus = 'Sold' OR t.ticketStatus = 'CheckedIn')
              AND (:keyword IS NULL
                   OR c.fullName     LIKE %:keyword%
                   OR c.email        LIKE %:keyword%
                   OR t.ticketNumber LIKE %:keyword%)
              AND (:ticketType IS NULL OR tt.name = :ticketType)
              AND (:status IS NULL
                   OR (:status = 'CheckedIn' AND t.ticketStatus = 'CheckedIn')
                   OR (:status = 'Pending'   AND t.ticketStatus <> 'CheckedIn'))
            """)
    Page<AttendeeDTO> getAttendeesByEventFiltered(
            @Param("eventId") Integer eventId,
            @Param("keyword") String keyword,
            @Param("ticketType") String ticketType,
            @Param("status") String status,
            Pageable pageable
    );

    // ── Distinct ticket type names của 1 event ────────────────────────────
    @Query("SELECT DISTINCT tt.name FROM TicketType tt WHERE tt.event.id = :eventId ORDER BY tt.name")
    List<String> findDistinctTicketTypesByEvent(@Param("eventId") Integer eventId);

    // ── Attendee detail ───────────────────────────────────────────────────
    @Query("""
            SELECT new vn.edu.fpt.model.dto.AttendeeDetailDTO(
                c.fullName,
                c.email,
                c.phone,
                c.avatarUrl,
                t.ticketNumber,
                tt.name,
                t.ticketStatus,
                t.issuedAt,
                t.checkedInAt,
                e.title,
                v.name,
                o.orderStatus
            )
            FROM Ticket t
            JOIN t.ticketType tt
            JOIN tt.event e
            LEFT JOIN e.venue v
            LEFT JOIN t.orderItem oi
            LEFT JOIN oi.order o
            LEFT JOIN o.customer c
            WHERE t.id = :ticketId
            """)
    AttendeeDetailDTO getAttendeeDetail(@Param("ticketId") Integer ticketId);

    // ── Recent checkins — không filter (dùng khi types null) ─────────────
    @Query("""
            SELECT new vn.edu.fpt.model.dto.AttendeeDTO(
                t.id,
                t.checkedInAt,
                t.ticketStatus,
                tt.name,
                t.ticketNumber,
                c.avatarUrl,
                c.email,
                c.fullName
            )
            FROM Ticket t
            JOIN t.ticketType tt
            JOIN t.orderItem oi
            JOIN oi.order o
            JOIN o.customer c
            WHERE tt.event.id = :eventId
            AND t.checkedInAt IS NOT NULL
            ORDER BY t.checkedInAt DESC
            """)
    Page<AttendeeDTO> getRecentCheckins(
            @Param("eventId") Integer eventId,
            Pageable pageable
    );

    // ── Recent checkins — có filter theo nhiều loại vé ────────────────────
    @Query("""
            SELECT new vn.edu.fpt.model.dto.AttendeeDTO(
                t.id,
                t.checkedInAt,
                t.ticketStatus,
                tt.name,
                t.ticketNumber,
                c.avatarUrl,
                c.email,
                c.fullName
            )
            FROM Ticket t
            JOIN t.ticketType tt
            JOIN t.orderItem oi
            JOIN oi.order o
            JOIN o.customer c
            WHERE tt.event.id = :eventId
            AND t.checkedInAt IS NOT NULL
            AND tt.name IN :types
            ORDER BY t.checkedInAt DESC
            """)
    Page<AttendeeDTO> getRecentCheckinsByTypes(
            @Param("eventId") Integer eventId,
            @Param("types") List<String> types,
            Pageable pageable
    );

    @Query("""
                SELECT COUNT(t)
                FROM Ticket t
                WHERE t.ticketStatus = 'Sold'
            """)
    int countTickets();

    // ── Count registered — có filter theo nhiều loại vé ──────────────────
    @Query("""
            SELECT COUNT(t) FROM Ticket t
            JOIN t.ticketType tt
            JOIN t.orderItem oi
            JOIN oi.order o
            WHERE tt.event.id = :eventId
            AND t.ticketStatus IN ('Sold', 'CheckedIn')
            AND tt.name IN :types
            """)
    long countRegisteredByTypes(
            @Param("eventId") Integer eventId,
            @Param("types") List<String> types
    );

    // ── Count checked-in — có filter theo nhiều loại vé ──────────────────
    @Query("""
            SELECT COUNT(t) FROM Ticket t
            JOIN t.ticketType tt
            JOIN t.orderItem oi
            JOIN oi.order o
            WHERE tt.event.id = :eventId
            AND t.ticketStatus = 'CheckedIn'
            AND tt.name IN :types
            """)
    long countCheckedInByTypes(
            @Param("eventId") Integer eventId,
            @Param("types") List<String> types
    );

    // ── Distribution — không filter ───────────────────────────────────────
    @Query("""
            SELECT new vn.edu.fpt.model.dto.DistributionDTO(tt.name, COUNT(t))
            FROM Ticket t
            JOIN t.ticketType tt
            WHERE tt.event.id = :eventId
            AND t.checkedInAt IS NOT NULL
            GROUP BY tt.name
            """)
    List<DistributionDTO> getDistribution(@Param("eventId") Integer eventId);

    // ── Distribution — có filter theo nhiều loại vé ───────────────────────
    @Query("""
            SELECT new vn.edu.fpt.model.dto.DistributionDTO(tt.name, COUNT(t))
            FROM Ticket t
            JOIN t.ticketType tt
            WHERE tt.event.id = :eventId
            AND t.checkedInAt IS NOT NULL
            AND tt.name IN :types
            GROUP BY tt.name
            """)
    List<DistributionDTO> getDistributionByTypes(
            @Param("eventId") Integer eventId,
            @Param("types") List<String> types
    );

    // ── Export ────────────────────────────────────────────────────────────
    @Query("""
                SELECT new vn.edu.fpt.model.dto.AttendeeExportDTO(
                    c.fullName,
                    c.email,
                    c.phone,
                    t.ticketNumber,
                    tt.name,
                    t.ticketStatus,
                    o.orderStatus,
                    oi.unitPrice,
                    oi.quantity,
                    o.orderDate,
                    t.checkedInAt
                )
                FROM Ticket t
                JOIN t.ticketType tt
                JOIN t.orderItem oi
                JOIN oi.order o
                JOIN o.customer c
                WHERE tt.event.id = :eventId
                ORDER BY o.orderDate DESC
            """)
    List<AttendeeExportDTO> findAttendeesForExport(@Param("eventId") Integer eventId);


    int countTicketByTicketType_IdAndTicketStatus(Integer ticketTypeId, String ticketStatus);

    @Modifying
    @Query("""
            UPDATE Ticket t
            SET t.ticketStatus = 'Available',
                t.orderItem = null,
                t.reservedAt = null
            WHERE t.ticketStatus = 'RESERVED'
            AND t.reservedAt < :expiredTime
            """)
    int releaseExpiredTickets(@Param("expiredTime") LocalDateTime expiredTime);
}