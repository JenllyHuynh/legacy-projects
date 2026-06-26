package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.entity.Notification;

import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Integer> {

    // ── Customer queries (giữ nguyên) ─────────────────────────────────
    List<Notification> findByCustomer_IdOrderBySentAtDesc(Integer customerId);
    List<Notification> findTop5ByCustomer_IdOrderBySentAtDesc(Integer customerId);
    long countByCustomer_IdAndIsReadFalse(Integer customerId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP " +
            "WHERE n.id = :id AND n.customer.id = :customerId")
    int markAsRead(@Param("id") Integer id, @Param("customerId") Integer customerId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP " +
            "WHERE n.customer.id = :customerId AND n.isRead = false")
    int markAllAsRead(@Param("customerId") Integer customerId);

    // ── History queries — query thẳng theo EventId ────────────────────
    // Đơn giản, chính xác 100%, không còn join vòng vèo qua customer

    // Lấy tất cả notifications staff gửi cho 1 event cụ thể
    @Query("SELECT n FROM Notification n " +
            "WHERE n.event.id = :eventId " +
            "AND n.senderStaff IS NOT NULL " +
            "ORDER BY n.sentAt DESC")
    List<Notification> findByEventId(@Param("eventId") Integer eventId);

    // Lấy notifications của nhiều events (dùng cho trang tổng)
    @Query("SELECT n FROM Notification n " +
            "WHERE n.event.id IN :eventIds " +
            "AND n.senderStaff IS NOT NULL " +
            "ORDER BY n.sentAt DESC")
    List<Notification> findByEventIds(@Param("eventIds") List<Integer> eventIds);

    // Đếm notifications của 1 event
    @Query("SELECT COUNT(n) FROM Notification n " +
            "WHERE n.event.id = :eventId " +
            "AND n.senderStaff IS NOT NULL")
    long countByEventId(@Param("eventId") Integer eventId);
}