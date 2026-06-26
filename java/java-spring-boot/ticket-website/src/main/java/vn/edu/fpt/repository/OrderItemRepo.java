package vn.edu.fpt.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.entity.Order;
import vn.edu.fpt.model.entity.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Integer> {

    List<OrderItem> findByOrder_Customer_Id(Integer customerId);

    @Query("""
                SELECT e.title, SUM(oi.quantity * oi.unitPrice)
                FROM OrderItem oi
                JOIN oi.order o
                JOIN oi.ticketType tt
                JOIN tt.event e
                WHERE o.orderDate BETWEEN :from AND :to
                  AND o.orderStatus = 'PAID'
                GROUP BY e.title
                ORDER BY SUM(oi.quantity * oi.unitPrice) DESC
            """)
    List<Object[]> getTopEventsBetween(
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

    @Query("""
            SELECT COALESCE(SUM(oi.quantity), 0)
            FROM OrderItem oi
            WHERE oi.order.customer.id = :customerId
            AND oi.ticketType.id = :ticketTypeId
            AND oi.order.orderStatus = 'PAID'
            """)
    Integer countPurchasedTicket(
            @Param("customerId") Integer customerId,
            @Param("ticketTypeId") Integer ticketTypeId
    );

    @Query("""
        select sum(oi.quantity * oi.unitPrice)
            from OrderItem oi
                where oi.order.id = :orderId
    """)
    BigDecimal getSubtotalByOrderId(
            @Param("orderId") int orderId
    );
}
