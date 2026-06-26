package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.entity.StaffNotification;

import java.util.List;

@Repository
public interface StaffNotificationRepo extends JpaRepository<StaffNotification, Integer> {

    // Lấy tất cả thông báo của 1 staff (cho trang history + popup chuông)
    List<StaffNotification> findByStaff_IdOrderBySentAtDesc(Integer staffId);

    // Lấy 5 thông báo gần nhất cho popup chuông
    List<StaffNotification> findTop5ByStaff_IdOrderBySentAtDesc(Integer staffId);

    // Đếm chưa đọc cho badge
    long countByStaff_IdAndIsReadFalse(Integer staffId);

    // Đánh dấu 1 thông báo đã đọc
    @Modifying
    @Query("UPDATE StaffNotification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP " +
            "WHERE n.id = :notifId AND n.staff.id = :staffId")
    void markAsRead(@Param("notifId") Integer notifId, @Param("staffId") Integer staffId);

    // Đánh dấu tất cả đã đọc
    @Modifying
    @Query("UPDATE StaffNotification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP " +
            "WHERE n.staff.id = :staffId AND n.isRead = false")
    void markAllAsRead(@Param("staffId") Integer staffId);


    // Lấy tất cả staff notifications của 1 event — dùng cho trang History
    List<StaffNotification> findByEvent_IdOrderBySentAtDesc(Integer eventId);
}