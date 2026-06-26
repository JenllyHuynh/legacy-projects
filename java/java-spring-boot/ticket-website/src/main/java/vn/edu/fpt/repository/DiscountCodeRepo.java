package vn.edu.fpt.repository;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.entity.DiscountCode;
import vn.edu.fpt.model.entity.Event;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiscountCodeRepo extends JpaRepository<DiscountCode, Integer> {

    DiscountCode findByCode(String code);

    @Query("""
            SELECT d
            FROM DiscountCode d
            JOIN d.event e
            JOIN EventStaffAssignment es ON es.event.id = e.id
            WHERE es.staff.id = :id
            """)
    Page<DiscountCode> findAllByEvent_EventStaffAssignments_staffId(@Param("id") int id, @NonNull Pageable pageable);

    @Query(value = """
                SELECT d
                FROM DiscountCode d
                JOIN EventStaffAssignment es ON es.event.id = d.event.id
                WHERE es.staff.id = :id
                AND d.code LIKE CONCAT('%', :code, '%')
            """)
    Page<DiscountCode> findDiscountByCode(@Param("id") int id, @Param("code") String code, @NonNull Pageable pageable);

    @Query(value = """
                SELECT d
                FROM DiscountCode d
                JOIN EventStaffAssignment es ON es.event.id = d.event.id
                WHERE es.staff.id = :id
                AND d.isActive = true
            """)
    Page<DiscountCode> findDiscountByIsActive(@Param("id") int id, @NonNull Pageable pageable);

    @Query(value = """
                SELECT d
                FROM DiscountCode d
                JOIN EventStaffAssignment es ON es.event.id = d.event.id
                WHERE es.staff.id = :id
                AND d.isActive = false
            """)
    Page<DiscountCode> findDiscountByInactive(@Param("id") int id, @NonNull Pageable pageable);

    @Query(value = """
                SELECT d
                FROM DiscountCode d
                JOIN EventStaffAssignment es ON es.event.id = d.event.id
                WHERE es.staff.id = :id
                AND d.isActive = true
                AND d.code LIKE CONCAT('%', :code, '%')
            """)
    Page<DiscountCode> findDiscountByIsActiveAndCode(@Param("id") int id, @Param("code") String code, @NonNull Pageable pageable);

    @Query(value = """
                SELECT d
                FROM DiscountCode d
                JOIN EventStaffAssignment es ON es.event.id = d.event.id
                WHERE es.staff.id = :id
                AND d.isActive = false
                AND d.code LIKE CONCAT('%', :code, '%')
            """)
    Page<DiscountCode> findDiscountByInactiveAndCode(@Param("id") int id, @Param("code") String code, @NonNull Pageable pageable);


    @Query(value = """
            SELECT d
            FROM DiscountCode d
            JOIN EventStaffAssignment es ON es.event.id = d.event.id
            WHERE es.staff.id = :id
            AND  d.validTo < :date
            """)
    Page<DiscountCode> findExpiredDiscounts(@Param("id") int id, @Param("date") LocalDateTime now, Pageable pageable);

    @Query(value = """
            SELECT d
            FROM DiscountCode d
            JOIN EventStaffAssignment es ON es.event.id = d.event.id
            WHERE es.staff.id = :id
            AND  d.validTo < :date
            AND d.code LIKE CONCAT('%', :code, '%')
            """)
    Page<DiscountCode> findExpiredDiscountsAndCode(@Param("id") int id, @Param("code") String code, @Param("date") LocalDateTime now, Pageable pageable);

    // phân loại discount type
    @Query(value = """
            SELECT d
                FROM DiscountCode d
                JOIN EventStaffAssignment es ON es.event.id = d.event.id
                WHERE es.staff.id = :staffId
                AND d.discountType = :type
            """)
    Page<DiscountCode> findByDiscountType(@Param("staffId") int id, @NonNull Pageable pageable, @Param("type") String discountType);

    // phân loại discount type là percentage
    @Query(value = """
                SELECT d
                FROM DiscountCode d
                JOIN EventStaffAssignment es ON es.event.id = d.event.id
                WHERE es.staff.id = :id
                AND d.code LIKE CONCAT('%', :code, '%')
                AND d.discountType = 'percentage'
            """)
    Page<DiscountCode> findPercentageDiscountByCode(@Param("id") int id, @Param("code") String code, @NonNull Pageable pageable);

    @Query(value = """
                SELECT d
                FROM DiscountCode d
                JOIN EventStaffAssignment es ON es.event.id = d.event.id
                WHERE es.staff.id = :id
                AND d.isActive = true
                AND d.discountType = 'percentage'
            """)
    Page<DiscountCode> findPercentageDiscountByIsActive(@Param("id") int id, @NonNull Pageable pageable);

    @Query(value = """
                SELECT d
                FROM DiscountCode d
                JOIN EventStaffAssignment es ON es.event.id = d.event.id
                WHERE es.staff.id = :id
                AND d.isActive = false
                AND d.discountType = 'percentage'
            """)
    Page<DiscountCode> findPercentageDiscountByInactive(@Param("id") int id, @NonNull Pageable pageable);

    @Query(value = """
                SELECT d
                FROM DiscountCode d
                JOIN EventStaffAssignment es ON es.event.id = d.event.id
                WHERE es.staff.id = :id
                AND d.isActive = true
                AND d.code LIKE CONCAT('%', :code, '%')
                AND d.discountType = 'percentage'
            """)
    Page<DiscountCode> findPercentageDiscountByIsActiveAndCode(@Param("id") int id, @Param("code") String code, @NonNull Pageable pageable);

    @Query(value = """
                SELECT d
                FROM DiscountCode d
                JOIN EventStaffAssignment es ON es.event.id = d.event.id
                WHERE es.staff.id = :id
                AND d.isActive = false
                AND d.code LIKE CONCAT('%', :code, '%')
                AND d.discountType = 'percentage'
            """)
    Page<DiscountCode> findPercentageDiscountByInactiveAndCode(@Param("id") int id, @Param("code") String code, @NonNull Pageable pageable);


    @Query(value = """
            SELECT d
            FROM DiscountCode d
            JOIN EventStaffAssignment es ON es.event.id = d.event.id
            WHERE es.staff.id = :id
            AND d.validTo < :date
            AND d.discountType = 'percentage'
            """)
    Page<DiscountCode> findPercentageExpiredDiscounts(@Param("id") int id, @Param("date") LocalDateTime now, Pageable pageable);

    @Query(value = """
            SELECT d
            FROM DiscountCode d
            JOIN EventStaffAssignment es ON es.event.id = d.event.id
            WHERE es.staff.id = :id
            AND d.validTo < :date
            AND d.code LIKE CONCAT('%', :code, '%')
            AND d.discountType = 'percentage'
            """)
    Page<DiscountCode> findPercentageExpiredDiscountsAndCode(@Param("id") int id, @Param("code") String code, @Param("date") LocalDateTime now, Pageable pageable);

    // Phân loại discount type là fixedAmount
    @Query(value = """
                SELECT d
                FROM DiscountCode d
                JOIN EventStaffAssignment es ON es.event.id = d.event.id
                WHERE es.staff.id = :id
                AND d.code LIKE CONCAT('%', :code, '%')
                AND d.discountType = 'fixedAmount'
            """)
    Page<DiscountCode> findfixedAmountDiscountByCode(@Param("id") int id, @Param("code") String code, @NonNull Pageable pageable);

    @Query(value = """
                SELECT d
                FROM DiscountCode d
                JOIN EventStaffAssignment es ON es.event.id = d.event.id
                WHERE es.staff.id = :id
                AND d.isActive = true
                AND d.discountType = 'fixedAmount'
            """)
    Page<DiscountCode> findfixedAmountDiscountByIsActive(@Param("id") int id, @NonNull Pageable pageable);

    @Query(value = """
                SELECT d
                FROM DiscountCode d
                JOIN EventStaffAssignment es ON es.event.id = d.event.id
                WHERE es.staff.id = :id
                AND d.isActive = false
                AND d.discountType = 'fixedAmount'
            """)
    Page<DiscountCode> findfixedAmountDiscountByInactive(@Param("id") int id, @NonNull Pageable pageable);

    @Query(value = """
                SELECT d
                FROM DiscountCode d
                JOIN EventStaffAssignment es ON es.event.id = d.event.id
                WHERE es.staff.id = :id
                AND d.isActive = true
                AND d.code LIKE CONCAT('%', :code, '%')
                AND d.discountType = 'fixedAmount'
            """)
    Page<DiscountCode> findfixedAmountDiscountByIsActiveAndCode(@Param("id") int id, @Param("code") String code, @NonNull Pageable pageable);

    @Query(value = """
                SELECT d
                FROM DiscountCode d
                JOIN EventStaffAssignment es ON es.event.id = d.event.id
                WHERE es.staff.id = :id
                AND d.isActive = false
                AND d.code LIKE CONCAT('%', :code, '%')
                AND d.discountType = 'fixedAmount'
            """)
    Page<DiscountCode> findfixedAmountDiscountByInactiveAndCode(@Param("id") int id, @Param("code") String code, @NonNull Pageable pageable);


    @Query(value = """
            SELECT d
            FROM DiscountCode d
            JOIN EventStaffAssignment es ON es.event.id = d.event.id
            WHERE es.staff.id = :id
            AND d.validTo < :date
            AND d.discountType = 'fixedAmount'
            """)
    Page<DiscountCode> findfixedAmountExpiredDiscounts(@Param("id") int id, @Param("date") LocalDateTime now, Pageable pageable);

    @Query(value = """
            SELECT d
            FROM DiscountCode d
            JOIN EventStaffAssignment es ON es.event.id = d.event.id
            WHERE es.staff.id = :id
            AND d.validTo < :date
            AND d.code LIKE CONCAT('%', :code, '%')
            AND d.discountType = 'fixedAmount'
            """)
    Page<DiscountCode> findfixedAmountExpiredDiscountsAndCode(@Param("id") int id, @Param("code") String code, @Param("date") LocalDateTime now, Pageable pageable);


    DiscountCode findDiscountById(Integer DiscountCodeId);

    List<DiscountCode> findByEvent_Organizer_Id(Integer OrganizerId);

    List<DiscountCode> getAllByEvent(Event event);

    @Query(value = """
                SELECT *
                FROM DiscountCodes d
                WHERE d.EventId IN (:eventIds)
                  AND d.IsActive = 1
                  AND (d.MaxUses IS NULL OR d.UsedCount < d.MaxUses)
                  AND (d.ValidFrom IS NULL OR d.ValidFrom <= GETDATE())
                  AND (d.ValidTo IS NULL OR d.ValidTo >= GETDATE())
            """, nativeQuery = true)
    List<DiscountCode> getAvailableDiscountByEventIds(@Param("eventIds") List<Integer> eventIds);

//    @Modifying
//    @Query("""
//                UPDATE DiscountCode d
//                SET d.usedCount = d.usedCount + 1
//                WHERE d.id = :id
//                  AND d.usedCount < d.maxUses
//            """)
//    int increaseUsedCountIfAvailable(@Param("id") Integer id);


    // Giu cho discount
    @Modifying
    @Query("""
                UPDATE DiscountCode d
                SET d.reservedCount = d.reservedCount + 1
                WHERE d.id = :id
                AND d.isActive = true
                AND d.validFrom <= CURRENT_TIMESTAMP
                AND d.validTo >= CURRENT_TIMESTAMP
                AND (d.usedCount + d.reservedCount) < d.maxUses
            """)
    int reserveDiscount(Integer id);

    // Dung khi thanh toan thanh cong that
    @Modifying
    @Query("""
            UPDATE DiscountCode d
            SET d.usedCount = d.usedCount + 1,
                d.reservedCount = d.reservedCount - 1
            WHERE d.id = :id
            AND d.reservedCount > 0
            """)
    void confirmUsage(Integer id);

    // Thanh toan that bai
    @Modifying
    @Query("""
            UPDATE DiscountCode d
            SET d.reservedCount = d.reservedCount - 1
            WHERE d.id = :id
            AND d.reservedCount > 0
            """)
    void releaseReservation(Integer id);

    // Dung khi xoa discount code
    @Modifying
    @Query("""
            UPDATE DiscountCode d
            SET d.reservedCount = d.reservedCount - 1
            WHERE d.id = :id
            AND d.reservedCount > 0
            """)
    void deleteDiscountInCheckOut(Integer id);

    @Modifying
    @Transactional
    @Query("""
            Update DiscountCode d
            SET d.isActive = false
            WHERE d.isActive = true
            AND (d.validTo < CURRENT_TIMESTAMP OR d.usedCount >= d.maxUses)
            """)
    int updateExpiredCode();
}
