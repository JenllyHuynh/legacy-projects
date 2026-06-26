package vn.edu.fpt.repository;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.entity.Event;
import vn.edu.fpt.model.entity.EventStaffAssignment;
import vn.edu.fpt.model.entity.EventStaffAssignmentId;
import vn.edu.fpt.model.entity.Venue;

@Repository
public interface EventStaffAssignmentRepo extends JpaRepository<EventStaffAssignment, Integer> {

    @Override
    Page<EventStaffAssignment> findAll(@NonNull Pageable pageable);

    @Query(value = """
        SELECT es
        FROM EventStaffAssignment es
        WHERE es.staff.fullName LIKE CONCAT('%', :name, '%')
    """)
    Page<EventStaffAssignment> findEventStaffAssignmentByName(@Param("name") String name, Pageable pageable);

    @Query(value = """
        SELECT es
        FROM EventStaffAssignment es
        WHERE es.isActive = true
    """)
    Page<EventStaffAssignment> findEventStaffAssignmentByIsActive(@NonNull Pageable pageable);

    @Query(value = """
        SELECT es
        FROM EventStaffAssignment es
        WHERE es.isActive = true
        AND es.staff.fullName LIKE CONCAT('%', :name, '%')
    """)
    Page<EventStaffAssignment> findEventStaffAssignmentByIsActiveAndName(@Param("name") String name, @NonNull Pageable pageable);

    @Query(value = """
        SELECT es
        FROM EventStaffAssignment es
        WHERE es.isActive = false
    """)
    Page<EventStaffAssignment> findEventStaffAssignmentByInActive(@NonNull Pageable pageable);

    @Query(value = """
        SELECT es
        FROM EventStaffAssignment es
        WHERE es.isActive = false
        AND es.staff.fullName LIKE CONCAT('%', :name, '%')
    """)
    Page<EventStaffAssignment> findEventStaffAssignmentByInActiveAndName(@Param("name") String name, @NonNull Pageable pageable);

    EventStaffAssignment getEventStaffById(EventStaffAssignmentId id);

    EventStaffAssignment event(Event event);
}
