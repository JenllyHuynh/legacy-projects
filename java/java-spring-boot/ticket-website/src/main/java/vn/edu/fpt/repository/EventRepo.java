package vn.edu.fpt.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.dto.CommentDTO;
import vn.edu.fpt.model.dto.EventDTO;
import vn.edu.fpt.model.dto.StaffEventAttendeeDTO;
import vn.edu.fpt.model.entity.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepo extends JpaRepository<Event, Integer> {

    Event getEventById(Integer eventId);

    @Query("""
            select new vn.edu.fpt.model.dto.EventDTO(
            e.startDateTime, e.id, e.title, e.venue, e.category, min (t.price), e.thumbnailUrl, e.currency
            )
            from Event e
            join e.ticketTypes t
            where e.startDateTime > :now and e.eventStatus = :status and e.isFeatured = true
            group by e.title, e.startDateTime, e.venue, e.category, e.id, e.thumbnailUrl, e.currency
            order by e.startDateTime asc
            """)
    List<EventDTO> getListFeatureEvents(@Param("now") LocalDateTime now, @Param("status") String status, Pageable pageable);

    @Query("""
            select new vn.edu.fpt.model.dto.EventDTO(
            e.startDateTime, e.id, e.title, e.venue, e.category, min (t.price), e.thumbnailUrl, e.currency
            )
            from Event e
            join e.ticketTypes t
            where e.eventStatus = :status
            group by e.title, e.startDateTime, e.endDateTime, e.venue, e.category, e.id, e.thumbnailUrl, e.currency
            order by e.endDateTime asc
            """)
    List<EventDTO> getListCompletedEvents(@Param("status") String status, Pageable pageable);

    @Query("""
            select new vn.edu.fpt.model.dto.EventDTO(
            e.startDateTime, e.id, e.title, e.venue, e.category, min (t.price), e.thumbnailUrl, e.currency
            )
            from Event e
            join e.ticketTypes t
            where e.startDateTime > :now and e.eventStatus = :status
            group by e.title, e.startDateTime, e.venue, e.category, e.id, e.thumbnailUrl, e.currency
            order by e.startDateTime asc
            """)
    List<EventDTO> getListUpcomingEvents(@Param("now") LocalDateTime now, @Param("status") String status, Pageable pageable);

    @Query("""
            select new vn.edu.fpt.model.dto.EventDTO(
            e.startDateTime, e.id, e.title, e.venue, e.category, min (t.price), e.thumbnailUrl, e.currency
            )
            from Event e
            join e.ticketTypes t
            where e.startDateTime > :now and e.eventStatus = :status
            group by e.title, e.startDateTime, e.venue, e.category, e.id, e.thumbnailUrl, e.viewCount, e.currency
            order by e.viewCount desc
            """)
    List<EventDTO> getListTrendingEvents(@Param("now") LocalDateTime now, @Param("status") String status, Pageable pageable);

    @Query("""
            select new vn.edu.fpt.model.dto.EventDTO(
            e.startDateTime, e.id, e.title, e.venue, e.category, min (t.price), e.thumbnailUrl, e.currency
            )
            from Event e
            join e.ticketTypes t
            where e.startDateTime > :now and e.eventStatus = :status and e.category.name = :category
            group by e.title, e.startDateTime, e.venue, e.category, e.id, e.thumbnailUrl, e.viewCount, e.currency
            order by e.viewCount desc
            """)
    List<EventDTO> getListEventByCategory(@Param("now") LocalDateTime now, @Param("status") String status, @Param("category") String category, Pageable pageable);

    @Query("""
                select sum (t.quantity)
                from TicketType t
                where t.event.id = :id
            
            """)
    Long getTotalTicketType(@Param("id") int id);

    @Query("""
                select sum(oi.quantity) from Event e
                join TicketType t on t.event.id = e.id
                join OrderItem oi on oi.ticketType.id = t.id
                join Order o on o.id = oi.order.id
                where e.id = :id and o.orderStatus = 'Paid'
            """)
    Long getTotalTicketSold(@Param("id") int id);

    @Query("""
                select
                	sum(oi.quantity * oi.unitPrice)
                from Event e
                join e.ticketTypes t on e.id = t.event.id
                join OrderItem oi on oi.ticketType.id = t.id
                join Order o on o.id = oi.order.id
                where e.id = :id and o.orderStatus = :status
            """)
    Long getTotalRevenue(
            @Param("id") int id,
            @Param("status") String status
    );

//    List<EventDTO> getListEventHomeDto(@Param("now") LocalDateTime now,
//                                       @Param("status") String status,
//                                       Pageable pageable);


    Optional<Event> findByIdAndEventStatus(Integer id, String status);

    @Query("""
                select
                    1.0 * e.viewCount / m.maxView * 100
                from Event e,
                     (select max(e1.viewCount) as maxView from Event e1) m
                where e.id = :id
            """)
    Double getViewCountPercentByEventId(@Param("id") int id);

//    @Query("""
//        select distinct new vn.edu.fpt.model.dto.CommentDTO(
//            cm.rating,
//            cm.content,
//            cm.updatedAt,
//            c.fullName,
//            c.avatarUrl,
//            c.email
//        )
//        from Customer c
//        join c.comments cm
//        join c.orders od
//        join od.orderItems oi
//        where cm.event.id = :eventId and od.event.id = cm.event.id
//    """)
//    List<CommentDTO> getListCommentsByEventId(@Param("eventId") int eventId);

    @Query("""
                select distinct new vn.edu.fpt.model.dto.CommentDTO(
                    cm.id,
                    cm.rating,
                    cm.content,
                    cm.updatedAt,
                    c.fullName,
                    c.avatarUrl,
                    c.email
                )
                from Customer c
                join c.comments cm
                join c.orders od
                join od.orderItems oi
                join oi.ticketType tt
                join tt.event e
                where cm.event.id = :eventId
                and e.id = cm.event.id
            """)
    List<CommentDTO> getListCommentsByEventId(@Param("eventId") int eventId);

    @Query("""
                select (
                    count(distinct od.id)
                )
                from Customer c
                join c.orders od
                join od.orderItems oi
                join oi.ticketType tt
                join tt.event e
                where c.id = :customerId and tt.event.id = :eventId
            """)
    int countOrder(
            @Param("eventId") Integer eventId,
            @Param("customerId") Integer customerId
    );

    @Query(
            value = """
                    select new vn.edu.fpt.model.dto.EventDTO(
                        e.startDateTime, e.id, e.title, e.venue, e.category,
                        min(t.price), e.thumbnailUrl, e.currency, e.eventStatus
                    )
                    from Event e
                    join e.ticketTypes t
                    left join e.venue v
                    where e.eventStatus in ('Published','Completed','Live')
                      and (:keyword is null or e.title like %:keyword%)
                      and (:categoryIds is null or e.category.id in :categoryIds)
                      and (:city is null or v.city = :city)
                      and (:startDateTime is null or e.startDateTime >= :startDateTime)
                      and (:endDateTime is null or e.startDateTime <= :endDateTime)
                    group by
                        e.startDateTime, e.id, e.title,
                        e.venue, e.category,
                        e.thumbnailUrl, e.currency, e.isFree, e.eventStatus
                        having (
                            (:minPrice is null or min(t.price) >= :minPrice)
                        and (:maxPrice is null or min(t.price) <= :maxPrice)
                        )
                        or (:priceMode = 'free' and e.isFree = true)
                    order by e.startDateTime desc                     
                    """,
            countQuery = """
                    select count(distinct e.id)
                    from Event e
                    join e.ticketTypes t
                    left join e.venue v
                    where e.eventStatus in ('Published','Completed','Live')
                      and (:keyword is null or e.title like %:keyword%)
                      and (:categoryIds is null or e.category.id in :categoryIds)
                      and (:city is null or v.city = :city)
                      and (:startDateTime is null or e.startDateTime >= :startDateTime)
                      and (:endDateTime is null or e.startDateTime <= :endDateTime)
                    group by e.id, e.isFree
                        having (
                                (:minPrice is null or min(t.price) >= :minPrice)
                            and (:maxPrice is null or min(t.price) <= :maxPrice)
                            )
                            or (:priceMode = 'free' and e.isFree = true)
                    """
    )
    Page<EventDTO> searchAllEvents(
            @Param("keyword") String keyword,
            @Param("categoryIds") List<Integer> categoryIds,
            @Param("city") String city,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("priceMode") String priceMode,
            Pageable pageable
    );


    @Query(
            value = """
                    select new vn.edu.fpt.model.dto.EventDTO(
                        e.startDateTime, e.id, e.title, e.venue, e.category,
                        min(t.price), e.thumbnailUrl, e.currency
                    )
                    from Event e
                    join e.ticketTypes t
                    left join e.venue v
                    where e.eventStatus = 'Published'
                      and e.isFeatured = true
                      and e.startDateTime > :now
                      and (:keyword is null or e.title like %:keyword%)
                      and (:categoryIds is null or e.category.id in :categoryIds)
                      and (:city is null or v.city = :city)
                      and (:startDateTime is null or e.startDateTime >= :startDateTime)
                      and (:endDateTime is null or e.startDateTime <= :endDateTime)
                    group by
                        e.id, e.startDateTime, e.title,
                        e.venue, e.category,
                        e.thumbnailUrl, e.currency, e.isFree
                    having (
                            (:minPrice is null or min(t.price) >= :minPrice)
                        and (:maxPrice is null or min(t.price) <= :maxPrice)
                        )
                        or (:priceMode = 'free' and e.isFree = true)
                    order by e.startDateTime desc
                    """,
            countQuery = """
                    select count(distinct e.id)
                    from Event e
                    join e.ticketTypes t
                    left join e.venue v
                    where e.eventStatus = 'Published'
                      and e.isFeatured = true
                      and e.startDateTime > :now
                      and (:keyword is null or e.title like %:keyword%)
                      and (:categoryIds is null or e.category.id in :categoryIds)
                      and (:city is null or v.city = :city)
                      and (:startDateTime is null or e.startDateTime >= :startDateTime)
                      and (:endDateTime is null or e.startDateTime <= :endDateTime)
                    group by e.id, e.isFree
                    having (
                            (:minPrice is null or min(t.price) >= :minPrice)
                        and (:maxPrice is null or min(t.price) <= :maxPrice)
                        )
                        or (:priceMode = 'free' and e.isFree = true)
                    """
    )
    Page<EventDTO> getFeaturedEventsWithFilter(
            @Param("keyword") String keyword,
            @Param("categoryIds") List<Integer> categoryIds,
            @Param("city") String city,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("priceMode") String priceMode,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    @Query(
            value = """
                    select new vn.edu.fpt.model.dto.EventDTO(
                        e.startDateTime, e.id, e.title, e.venue, e.category,
                        min(t.price), e.thumbnailUrl, e.currency
                    )
                    from Event e
                    join e.ticketTypes t
                    left join e.venue v
                    where e.eventStatus = 'Published'
                      and e.startDateTime > :now
                      and (:keyword is null or e.title like %:keyword%)
                      and (:categoryIds is null or e.category.id in :categoryIds)
                      and (:city is null or v.city = :city)
                      and (:startDateTime is null or e.startDateTime >= :startDateTime)
                      and (:endDateTime is null or e.startDateTime <= :endDateTime)
                    group by
                        e.id, e.startDateTime, e.title,
                        e.venue, e.category,
                        e.thumbnailUrl, e.currency, e.isFree
                    having (
                        (:minPrice is null or min(t.price) >= :minPrice)
                    and (:maxPrice is null or min(t.price) <= :maxPrice)
                    )
                    or (:priceMode = 'free' and e.isFree = true)
                    order by e.startDateTime asc
                    """,
            countQuery = """
                    select count(distinct e.id)
                    from Event e
                    join e.ticketTypes t
                    left join e.venue v
                    where e.eventStatus = 'Published'
                      and e.startDateTime > :now
                      and (:keyword is null or e.title like %:keyword%)
                      and (:categoryIds is null or e.category.id in :categoryIds)
                      and (:city is null or v.city = :city)
                      and (:startDateTime is null or e.startDateTime >= :startDateTime)
                      and (:endDateTime is null or e.startDateTime <= :endDateTime)
                    group by e.id, e.isFree
                    having (
                        (:minPrice is null or min(t.price) >= :minPrice)
                    and (:maxPrice is null or min(t.price) <= :maxPrice)
                    )
                    or (:priceMode = 'free' and e.isFree = true)
                    """
    )
    Page<EventDTO> getUpcomingEventsWithFilter(
            @Param("keyword") String keyword,
            @Param("categoryIds") List<Integer> categoryIds,
            @Param("city") String city,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("priceMode") String priceMode,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );


    @Query(
            value = """
                    select new vn.edu.fpt.model.dto.EventDTO(
                        e.startDateTime, e.id, e.title, e.venue, e.category,
                        min(t.price), e.thumbnailUrl, e.currency
                    )
                    from Event e
                    join e.ticketTypes t
                    left join e.venue v
                    where e.eventStatus = 'Published'
                      and e.startDateTime > :now
                      and (:keyword is null or e.title like %:keyword%)
                      and (:categoryIds is null or e.category.id in :categoryIds)
                      and (:city is null or v.city = :city)
                      and (:startDateTime is null or e.startDateTime >= :startDateTime)
                      and (:endDateTime is null or e.startDateTime <= :endDateTime)
                    group by
                        e.id, e.startDateTime, e.title,
                        e.venue, e.category,
                        e.thumbnailUrl, e.currency, e.viewCount, e.isFree
                    having (
                        (:minPrice is null or min(t.price) >= :minPrice)
                    and (:maxPrice is null or min(t.price) <= :maxPrice)
                    )
                    or (:priceMode = 'free' and e.isFree = true)
                    order by e.viewCount desc
                    """,
            countQuery = """
                    select count(distinct e.id)
                    from Event e
                    join e.ticketTypes t
                    left join e.venue v
                    where e.eventStatus = 'Published'
                      and e.startDateTime > :now
                      and (:keyword is null or e.title like %:keyword%)
                      and (:categoryIds is null or e.category.id in :categoryIds)
                      and (:city is null or v.city = :city)
                      and (:startDateTime is null or e.startDateTime >= :startDateTime)
                      and (:endDateTime is null or e.startDateTime <= :endDateTime)
                    group by e.id, e.isFree
                    having (
                        (:minPrice is null or min(t.price) >= :minPrice)
                    and (:maxPrice is null or min(t.price) <= :maxPrice)
                    )
                    or (:priceMode = 'free' and e.isFree = true)
                    """
    )
    Page<EventDTO> getTrendingEventsWithFilter(
            @Param("keyword") String keyword,
            @Param("categoryIds") List<Integer> categoryIds,
            @Param("city") String city,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("priceMode") String priceMode,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    @Query(
            value = """
                    select new vn.edu.fpt.model.dto.EventDTO(
                        e.startDateTime, e.id, e.title, e.venue, e.category,
                        min(t.price), e.thumbnailUrl, e.currency, e.eventStatus
                    )
                    from Event e
                    join e.ticketTypes t
                    left join e.venue v
                    where e.eventStatus = 'Completed'
                      and (:keyword is null or e.title like %:keyword%)
                      and (:categoryIds is null or e.category.id in :categoryIds)
                      and (:city is null or v.city = :city)
                      and (:startDateTime is null or e.startDateTime >= :startDateTime)
                      and (:endDateTime is null or e.startDateTime <= :endDateTime)
                    group by
                        e.id, e.startDateTime, e.title,
                        e.venue, e.category,
                        e.thumbnailUrl, e.currency, e.isFree, e.endDateTime, e.eventStatus
                    having (
                            (:minPrice is null or min(t.price) >= :minPrice)
                        and (:maxPrice is null or min(t.price) <= :maxPrice)
                        )
                        or (:priceMode = 'free' and e.isFree = true)
                    order by e.startDateTime desc
                    """,
            countQuery = """
                    select count(distinct e.id)
                    from Event e
                    join e.ticketTypes t
                    left join e.venue v
                    where e.eventStatus = 'Completed'
                      and (:keyword is null or e.title like %:keyword%)
                      and (:categoryIds is null or e.category.id in :categoryIds)
                      and (:city is null or v.city = :city)
                      and (:startDateTime is null or e.startDateTime >= :startDateTime)
                      and (:endDateTime is null or e.startDateTime <= :endDateTime)
                    group by e.id, e.isFree
                    having (
                            (:minPrice is null or min(t.price) >= :minPrice)
                        and (:maxPrice is null or min(t.price) <= :maxPrice)
                        )
                        or (:priceMode = 'free' and e.isFree = true)
                    """
    )
    Page<EventDTO> getCompletedEventsWithFilter(
            @Param("keyword") String keyword,
            @Param("categoryIds") List<Integer> categoryIds,
            @Param("city") String city,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("priceMode") String priceMode,
            Pageable pageable
    );

    @Query(
            value = """
                select new vn.edu.fpt.model.dto.EventDTO(
                    e.startDateTime, e.id, e.title, e.venue, e.category,
                    min(t.price), e.thumbnailUrl, e.currency, e.eventStatus
                )
                from Event e
                join e.ticketTypes t
                left join e.venue v
                where e.eventStatus = 'Live'
                  and (:keyword is null or e.title like %:keyword%)
                  and (:categoryIds is null or e.category.id in :categoryIds)
                  and (:city is null or v.city = :city)
                  and (:startDateTime is null or e.startDateTime >= :startDateTime)
                  and (:endDateTime is null or e.startDateTime <= :endDateTime)
                group by
                    e.startDateTime, e.id, e.title,
                    e.venue, e.category,
                    e.thumbnailUrl, e.currency, e.isFree, e.eventStatus
                having (
                    (:minPrice is null or min(t.price) >= :minPrice)
                and (:maxPrice is null or min(t.price) <= :maxPrice)
                )
                or (:priceMode = 'free' and e.isFree = true)
                order by e.startDateTime asc
                """,
            countQuery = """
                select count(distinct e.id)
                from Event e
                join e.ticketTypes t
                left join e.venue v
                where e.eventStatus = 'Live'
                  and (:keyword is null or e.title like %:keyword%)
                  and (:categoryIds is null or e.category.id in :categoryIds)
                  and (:city is null or v.city = :city)
                  and (:startDateTime is null or e.startDateTime >= :startDateTime)
                  and (:endDateTime is null or e.startDateTime <= :endDateTime)
                group by e.id, e.isFree
                having (
                    (:minPrice is null or min(t.price) >= :minPrice)
                and (:maxPrice is null or min(t.price) <= :maxPrice)
                )
                or (:priceMode = 'free' and e.isFree = true)
                """
    )
    Page<EventDTO> getLiveEventsWithFilter(
            @Param("keyword") String keyword,
            @Param("categoryIds") List<Integer> categoryIds,
            @Param("city") String city,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("priceMode") String priceMode,
            Pageable pageable
    );

    // ── getEventsByStaff — chỉ lấy thông tin event, KHÔNG COUNT/SUM ───────
    // Bug cũ: LEFT JOIN tt.tickets → COUNT(t) đếm cả ticket Available
    // Fix: bỏ JOIN tickets, service tự tính registered/checkedIn sau
    @Query("""
                SELECT new vn.edu.fpt.model.dto.StaffEventAttendeeDTO(
                    e.id,
                    e.title,
                    v.name,
                    v.city,
                    e.startDateTime,
                    e.endDateTime,
                    e.thumbnailUrl,
                    e.eventStatus
                )
                FROM EventStaffAssignment esa
                JOIN esa.event e
                JOIN esa.staff s
                LEFT JOIN e.venue v
                WHERE s.id = :staffId
                  AND esa.isActive = true
                  AND (:keyword is null or e.title like %:keyword%)
                  AND (:status is null or e.eventStatus like %:status%)
                GROUP BY
                    e.id,
                    e.title,
                    v.name,
                    v.city,
                    e.startDateTime,
                    e.endDateTime,
                    e.thumbnailUrl,
                    e.eventStatus
            """)
    Page<StaffEventAttendeeDTO> getEventsByStaff(
            @Param("staffId") Integer staffId,
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable);

    @Query("""
    SELECT COUNT(e) > 0
    FROM EventStaffAssignment esa
    JOIN esa.event e
    JOIN esa.staff s
    WHERE s.id = :staffId
      AND e.id = :eventId
      AND esa.isActive = true
""")
    boolean existsByStaffAndEvent(@Param("staffId") Integer staffId,
                                  @Param("eventId") Integer eventId);

    // ── getEventSummary — chỉ lấy thông tin cơ bản cho header ────────────
    // Bỏ JOIN tickets và GROUP BY → query đơn giản, không lỗi
    @Query("""
            SELECT new vn.edu.fpt.model.dto.StaffEventAttendeeDTO(
                e.id,
                e.title,
                v.name,
                v.city,
                e.startDateTime,
                e.endDateTime,
                e.thumbnailUrl
            )
            FROM Event e
            LEFT JOIN e.venue v
            WHERE e.id = :eventId
            """)
    StaffEventAttendeeDTO getEventSummary(@Param("eventId") Integer eventId);

    @Modifying
    @Transactional
    @Query("""
                UPDATE Event e
                    SET e.eventStatus = 'Live'
                    WHERE e.startDateTime <= CURRENT_TIMESTAMP
                      AND e.endDateTime   >= CURRENT_TIMESTAMP
                      AND e.eventStatus <> 'Live'
            """)
    int updateLiveEvents();

    @Modifying
    @Transactional
    @Query("""
               UPDATE Event e
               SET e.eventStatus = 'Completed'
               WHERE e.endDateTime < CURRENT_TIMESTAMP
                 AND e.eventStatus <> 'Completed'
            """)
    int updateEndEvent();

    @Modifying
    @Query("UPDATE Event e SET e.viewCount = e.viewCount + 1 WHERE e.id = :id")
    void incrementView(@Param("id") Integer id);

    @Query("""
                SELECT COUNT(e)
                FROM Event e
                WHERE e.eventStatus = 'Published'
            """)
    int countEvents();

    @Query("""
                SELECT c.name, COUNT(e)
                FROM Event e
                JOIN e.category c
                WHERE e.createdAt BETWEEN :from AND :to
                GROUP BY c.name
            """)
    List<Object[]> countEventsByCategoryBetween(LocalDateTime from, LocalDateTime to);

    @Query("""
                SELECT COUNT(e)
                FROM Event e
                WHERE e.createdAt BETWEEN :from AND :to
            """)
    long countEventsBetween(LocalDateTime from, LocalDateTime to);

    @Query("""
                SELECT COUNT(e)
                FROM Event e
                WHERE MONTH(e.createdAt) = MONTH(CURRENT_DATE) - 1
                  AND YEAR(e.createdAt) = YEAR(CURRENT_DATE)
            """)
    long countLastMonth();

    @Query("""
                SELECT e
                FROM Event e
                JOIN EventStaffAssignment es ON es.event.id = e.id
                WHERE es.staff.id = :staffId
                ORDER BY es.assignedAt desc
            """)
    List<Event> getAllEventByEventStaff(@Param("staffId") int id);

    @Query("""
                SELECT e
                FROM Event e
                JOIN EventStaffAssignment es ON es.event.id = e.id
                WHERE es.staff.id = :staffId
                  AND es.isActive = true
                ORDER BY es.assignedAt desc
            """)
    Page<Event> getEventsByStaff(@Param("staffId") int staffId, Pageable pageable);

    @Query("""
                SELECT e
                FROM Event e
                JOIN EventStaffAssignment es ON es.event.id = e.id
                WHERE es.staff.id = :staffId
                  AND es.isActive = true
                  AND (:status IS NULL OR e.eventStatus = :status)
                ORDER BY es.assignedAt desc
            """)
    Page<Event> getEventsByStaffAndStatus(
            @Param("staffId") int staffId,
            @Param("status") String status,
            Pageable pageable
    );
}
