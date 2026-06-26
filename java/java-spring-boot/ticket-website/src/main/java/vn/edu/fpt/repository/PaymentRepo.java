package vn.edu.fpt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.entity.Order;
import vn.edu.fpt.model.entity.Payment;
import vn.edu.fpt.model.entity.TicketType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> getPaymentByPaymentStatus(String paymentStatus);

    @Query("""
            SELECT Sum(o.totalAmount) FROM Order o
            JOIN o.orderItems oi 
            JOIN oi.ticketType tt 
            JOIN Payment p ON p.order = o 
            WHERE tt.event.id = :eventId 
            AND p.paymentStatus = 'Completed'
    """)
    BigDecimal getTotalAmount(@Param("eventId") int eventId);

    @Query("""
            SELECT Sum(o.totalAmount) FROM Order o
            JOIN o.orderItems oi 
            JOIN oi.ticketType tt 
            JOIN Payment p ON p.order = o 
            WHERE tt.event.id = :eventId 
            AND p.paymentStatus = 'Completed'
            AND month(p.paidAt) = :mon
    """)
    BigDecimal getTotalAmountByMonth(@Param("eventId") int eventId,
                                     @Param("mon") int month);
    @Query("""
            SELECT Sum(o.totalAmount) FROM Order o
            JOIN o.orderItems oi 
            JOIN oi.ticketType tt 
            JOIN Payment p ON p.order = o 
            WHERE tt.event.id = :eventId 
            AND p.paymentStatus = 'Completed'
            AND month(p.paidAt) = :mon
            AND day(p.paidAt) = :date
    """)
    BigDecimal getTotalAmountByDate(@Param("eventId") int eventId,
                                     @Param("date") int date,
                                    @Param("mon") int month);
    @Query("""
            SELECT oi.ticketType, (o.totalAmount) FROM Order o
            JOIN o.orderItems oi 
            JOIN oi.ticketType tt 
            JOIN Payment p ON p.order = o 
            WHERE tt.event.id = :eventId 
            AND p.paymentStatus = 'Completed'
            GROUP BY oi.ticketType
    """)
    List<Object[]> getTotalAmountByTicketType(@Param("eventId") int eventId);

    @Query("""
        select p from Payment p
            where p.order.customer.id = :customerId and (:status is null or p.paymentStatus = :status)
    """)
    Page<Payment> getAllPaymentByCustomerId(
            @Param("customerId") int customerId,
            @Param("status") String status,
            Pageable pageable
    );
}
