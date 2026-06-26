package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.entity.TicketType;

import java.util.List;

@Repository
public interface TicketTypeRepository extends JpaRepository<TicketType, Integer> {
    // Tìm tất cả ticket types theo event ID
    List<TicketType> findByEventId(Integer eventId);

    // Tìm ticket types đang active theo event ID
    List<TicketType> findByEventIdAndIsActiveTrue(Integer eventId);

    // Tìm ticket types đang active theo event ID (cách khác của Triết )
    List<TicketType> findByEvent_IdAndIsActiveTrue(Integer eventId);

    /**
     * ĐẾM SỐ VÉ ĐÃ BÁN
     * Đếm từ bảng Tickets với status = 'Sold' hoặc 'Used'
     *
     * Giải thích:
     * - Chỉ đếm vé đã thực sự được phát hành
     * - Filter theo status để loại bỏ vé Available (chưa bán)
     * - COALESCE đảm bảo trả về 0 nếu không có vé nào
     */
    @Query(value = """
        SELECT COALESCE(COUNT(*), 0) 
        FROM Tickets 
        WHERE TicketTypeId = :ticketTypeId 
          AND TicketStatus IN ('Sold', 'Used', 'Checked-in')
        """, nativeQuery = true)
    Integer countSoldTickets(@Param("ticketTypeId") Integer ticketTypeId);

    /**
     * ĐẾM SỐ VÉ ĐÃ BÁN (TỪ ORDER ITEMS)
     * Tính tổng số lượng từ OrderItems
     *
     * LƯU Ý: Cách này có thể không chính xác nếu:
     * - Order bị cancel nhưng chưa trả vé
     * - Chưa tạo Ticket từ OrderItem
     * Nhưng sinh ra là để test :]
     */
    @Query(value = """
        SELECT COALESCE(SUM(oi.Quantity), 0) 
        FROM OrderItems oi
        INNER JOIN Orders o ON oi.OrderId = o.OrderId
        WHERE oi.TicketTypeId = :ticketTypeId
          AND o.OrderStatus IN ('Completed', 'Confirmed', 'Paid')
        """, nativeQuery = true)
    Integer countSoldTicketsFromOrders(@Param("ticketTypeId") Integer ticketTypeId);

    /**
     * ĐẾM SỐ VÉ CÒN LẠI (Available)
     * Đếm vé chưa bán trong bảng Tickets
     */
    @Query(value = """
        SELECT COALESCE(COUNT(*), 0) 
        FROM Tickets 
        WHERE TicketTypeId = :ticketTypeId 
          AND TicketStatus = 'Available'
        """, nativeQuery = true)
    Integer countAvailableTickets(@Param("ticketTypeId") Integer ticketTypeId);

    /**
     * LẤY THỐNG KÊ CHI TIẾT
     * Trả về: Total, Sold, Available, Used
     */
    @Query(value = """
        SELECT 
            COUNT(*) as total,
            SUM(CASE WHEN TicketStatus IN ('Sold', 'Used', 'Checked-in') THEN 1 ELSE 0 END) as sold,
            SUM(CASE WHEN TicketStatus = 'Available' THEN 1 ELSE 0 END) as available,
            SUM(CASE WHEN TicketStatus = 'Used' THEN 1 ELSE 0 END) as used
        FROM Tickets 
        WHERE TicketTypeId = :ticketTypeId
        """, nativeQuery = true)
    Object[] getTicketStatistics(@Param("ticketTypeId") Integer ticketTypeId);
}
