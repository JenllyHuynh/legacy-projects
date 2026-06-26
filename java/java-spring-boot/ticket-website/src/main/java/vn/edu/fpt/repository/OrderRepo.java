package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.dto.OrderHistoryDTO;
import vn.edu.fpt.model.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.model.dto.TicketOrderDTO;
import vn.edu.fpt.model.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, Integer> {

    // Dùng method naming (Spring Data JPA tự động hiểu ko biết có hiểu không nên Query cho chắc @_@ )
    // Order có quan hệ với Customer, nên phải là "customer.id"
    List<Order> findByCustomerId(Integer customerId);
    @Query("""
            SELECT new vn.edu.fpt.model.dto.OrderHistoryDTO(
                    o.id,
                    e.title,
                    e.thumbnailUrl,
                    v.name,
                    tt.name,
                    oi.quantity,
                    o.totalAmount,
                    o.orderStatus,
                    o.orderDate,
                    e.currency
            )
            FROM OrderItem oi
            JOIN oi.order o
            JOIN oi.ticketType tt
            JOIN tt.event e
            LEFT JOIN e.venue v
            WHERE o.customer.id = :customerId
            AND (:status IS NULL OR o.orderStatus = :status)
            ORDER BY o.orderDate DESC
            """)
    Page<OrderHistoryDTO> getOrderHistory(
            @Param("customerId") Integer customerId,
            @Param("status") String status,
            Pageable pageable
    );

    // Hoặc dùng @Query cho rõ ràng
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId")
    List<Order> findOrdersByCustomerId(@Param("customerId") Integer customerId);

    // Tìm theo customerId và orderStatus
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId AND o.orderStatus = :status")
    List<Order> findByCustomerIdAndOrderStatus(@Param("customerId") Integer customerId,
                                               @Param("status") String status);

    @Query("""
        SELECT DISTINCT e.id
        FROM Order o
        JOIN o.orderItems d
        JOIN d.ticketType t
        JOIN t.event e
        WHERE o.customer.id = :customerId
    """)
    List<Long> findEventHistoryByCustomerId(Integer customerId);

    @Query("""
        select distinct oi.ticketType.event
        from OrderItem oi
        where oi.order.id = :orderId
       """)
    List<Event> getEventByOrderId(
            @Param("orderId") int orderId
    );

    @Query("""
            select t.ticketType.name
                        from Ticket t
            """)
    List<Ticket> getTicketOrderByEvent(
            @Param("orderId") int orderId,
            @Param("eventId") int eventId
    );

    @Query("""
        select new vn.edu.fpt.model.dto.TicketOrderDTO(
            tt.name,
            sum(oi.quantity),
            cast(sum(oi.quantity * oi.unitPrice) as bigdecimal),
            cast(max(oi.unitPrice) as bigdecimal)
        )
        from OrderItem oi
            join oi.ticketType tt
            join tt.event e
        where oi.order.id = :orderId
          and e.id = :eventId
        group by tt.name
    """)
    List<TicketOrderDTO> getTicketsByOrderIdAndEventId(
            @Param("orderId") int orderId,
            @Param("eventId") int eventId
    );

    // Tổng doanh thu
    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.orderStatus = 'Paid'
    """)
    Double getTotalRevenue();

    @Query("""
    SELECT COALESCE(SUM(o.totalAmount), 0)
    FROM Order o
    WHERE o.orderStatus = 'Paid'
    AND MONTH(o.orderDate) = :month
    AND YEAR(o.orderDate) = :year
""")
    Double getRevenueByMonth(int month, int year);


    @Query(value = """
    SELECT
        DATEPART(WEEK, o.OrderDate) AS week_number,
    
        COUNT(DISTINCT CASE\s
            WHEN o.OrderDate = first_order.first_date THEN o.CustomerId
        END) AS new_users,
    
        COUNT(DISTINCT CASE\s
            WHEN o.OrderDate > first_order.first_date THEN o.CustomerId
        END) AS returning_users
    
    FROM Orders o
    
    CROSS APPLY (
        SELECT MIN(o2.OrderDate) AS first_date
        FROM Orders o2
        WHERE o2.CustomerId = o.CustomerId
    ) first_order
    
    WHERE o.OrderStatus = 'Paid' AND o.orderDate BETWEEN :from AND :to
    
    GROUP BY DATEPART(WEEK, o.OrderDate)
    ORDER BY week_number;
""", nativeQuery = true)
    List<Object[]> getCustomerGrowth(@Param("from") LocalDateTime from,
                                     @Param("to") LocalDateTime to);

    @Query(value = """
    SELECT
            CAST(o.OrderDate AS date) AS order_date,
            SUM(o.TotalAmount) AS revenue
        FROM Orders o
        JOIN Payments p on p.OrderId = o.OrderId
        WHERE o.OrderStatus = 'Paid' AND p.PaymentStatus = 'Completed'
          AND o.OrderDate BETWEEN :from AND :to
        GROUP BY CAST(o.OrderDate AS date)
        ORDER BY order_date
""", nativeQuery = true)

    List<Object[]> getRevenueByDate(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
        SELECT o
        FROM Order o
        JOIN FETCH o.customer
        ORDER BY o.orderDate DESC
    """)
    List<Order> findRecentTransactions(Pageable pageable);

    @Query(value = """
    SELECT 
        o.OrderId,
        c.CustomerId,
        c.FullName,
        o.TotalAmount,
        o.OrderStatus,
        p.PaidAt,
        p.PaymentStatus
    FROM Orders o
    JOIN Customers c ON o.CustomerId = c.CustomerId
    JOIN Payments p ON p.OrderId = o.OrderId
    ORDER BY p.PaidAt DESC
""", nativeQuery = true)
    List<Object[]> getRevenueRawData();

    @Query(value = """
    SELECT DISTINCT 
        oi.OrderId,
        e.EventId,
        e.Title
    FROM OrderItems oi
    JOIN TicketTypes tt ON tt.TicketTypeId = oi.TicketTypeId
    JOIN Events e ON e.EventId = tt.EventId
    WHERE oi.OrderId IN (:orderIds)
""", nativeQuery = true)
    List<Object[]> getEventsByOrderIds(List<Integer> orderIds);

    // ===== TOTAL REVENUE =====
    @Query("""
    SELECT COALESCE(SUM(o.totalAmount), 0)
    FROM Order o
    JOIN o.payments p
    WHERE o.orderDate BETWEEN :from AND :to
      AND o.orderStatus = 'Paid' AND p.paymentStatus = 'Completed'
""")
    double sumRevenueBetween(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    // ===== PREVIOUS REVENUE =====
    @Query("""
    SELECT COALESCE(SUM(o.totalAmount), 0)
    FROM Order o
    WHERE o.orderDate BETWEEN :prevFrom AND :prevTo
      AND o.orderStatus = 'Paid'
""")
    double sumRevenuePrevious(
            @Param("prevFrom") LocalDateTime prevFrom,
            @Param("prevTo") LocalDateTime prevTo
    );

    // ===== TICKETS THIS MONTH =====
    @Query("""
    SELECT COALESCE(SUM(oi.quantity), 0)
    FROM OrderItem oi
    JOIN oi.order o
    WHERE o.orderDate BETWEEN :from AND :to
      AND o.orderStatus = 'PAID'
""")
    long countTicketsBetween(LocalDateTime from, LocalDateTime to);

    // ===== TICKETS LAST MONTH =====
    @Query("""
        SELECT COALESCE(SUM(oi.quantity), 0)
        FROM OrderItem oi
        JOIN oi.order o
        WHERE MONTH(o.orderDate) = MONTH(CURRENT_DATE) - 1
          AND YEAR(o.orderDate) = YEAR(CURRENT_DATE)
    """)
    long countTicketsLastMonth();

    @Query("""
            select distinct o
            from Order o
            join fetch o.discountCode d
            where o.orderDate < :expiredTime
              and o.orderStatus in ('Pending')
            """)
    List<Order> findExpiredDiscountCodeByOrderId(
            @Param("expiredTime") LocalDateTime expiredTime
    );

    @Query(value = """
            SELECT DISTINCT o.DiscountCodeId
            FROM Orders o
            WHERE o.DiscountCodeId = :discountCode
            """, nativeQuery = true)
    List<Integer> getDiscountCodeId(@Param("discountCode") Integer discountId);
}
