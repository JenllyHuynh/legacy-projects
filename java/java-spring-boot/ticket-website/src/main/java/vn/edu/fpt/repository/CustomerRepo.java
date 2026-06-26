package vn.edu.fpt.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.entity.Customer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer,Integer> {
    boolean existsByEmail(String email);
    Optional<Customer> findByEmail(String email);

//    @Query("SELECT DISTINCT o.customer FROM Order o " +
//            "WHERE o.event.id = :eventId " +
//            "AND o.orderStatus != 'Cancelled'")
//    List<Customer> findDistinctCustomersByEventId(@Param("eventId") Integer eventId);

    @Query("""
        SELECT DISTINCT o.customer
        FROM Order o
        JOIN o.orderItems oi
        JOIN oi.ticketType tt
        JOIN tt.event e
        WHERE e.id = :eventId
        AND o.orderStatus <> 'Cancelled'
    """)
    List<Customer> findDistinctCustomersByEventId(@Param("eventId") Integer eventId);

    @Query("SELECT DISTINCT o.customer FROM Order o " +
            "JOIN o.orderItems oi " +
            "JOIN oi.ticketType tt " +
            "JOIN Payment p ON p.order = o " +
            "WHERE tt.event.id = :eventId " +
            "AND p.paymentStatus = 'Completed'")
    List<Customer> findCustomersByEvent(@Param("eventId") Integer eventId);

    @Query("""
    SELECT COUNT(c)
    FROM Customer c
    WHERE c.createdAt BETWEEN :from AND :to
""")
    long countUsersBetween(LocalDateTime from, LocalDateTime to);

    @Query("""
        SELECT COUNT(c)
        FROM Customer c
        WHERE MONTH(c.createdAt) = MONTH(CURRENT_DATE)
          AND YEAR(c.createdAt) = YEAR(CURRENT_DATE)
          AND c.isActive = true
    """)
    long countNewThisMonth();

    @Query("""
    SELECT COUNT(c)
    FROM Customer c
    WHERE c.isActive = true
""")
    long totalUser();

    @Query("""
        SELECT c
        FROM Customer c
        WHERE (:isActive IS NULL OR c.isActive = :isActive)
    """)
    Page<Customer> getPageCustomerByStatus(
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );
}
