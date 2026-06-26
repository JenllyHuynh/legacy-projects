package vn.edu.fpt.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.entity.Staff;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepo extends JpaRepository<Staff, Integer> {

    Staff findStaffById(int id);

    Optional<Staff> findByEmail(String email);
    Staff getStaffByEmail(String email);
    boolean existsByEmail(String email);

    // Lấy tất cả staff đang active được assign vào 1 event
    @Query(value = """
        SELECT s.*
        FROM   dbo.Staffs s
        INNER JOIN dbo.EventStaffAssignments esa ON s.StaffId = esa.StaffId
        WHERE  esa.EventId  = :eventId
          AND  esa.IsActive = 1
        """, nativeQuery = true)
    List<Staff> findActiveStaffByEventId(@Param("eventId") Integer eventId);

    @Query("""
        SELECT s
        FROM Staff s
        WHERE (:isActive IS NULL OR s.isActive = :isActive)
    """)
    Page<Staff> getPageStaffByStatus(
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );
}
