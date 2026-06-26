package vn.edu.fpt.service;

import jakarta.transaction.Transactional;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.dto.RevenueDTO;
import vn.edu.fpt.model.dto.TicketOrderDTO;
import vn.edu.fpt.model.entity.DiscountCode;
import vn.edu.fpt.model.entity.Event;
import vn.edu.fpt.model.entity.Order;
import vn.edu.fpt.model.entity.OrderItem;
import vn.edu.fpt.repository.CustomerRepo;
import vn.edu.fpt.repository.DiscountCodeRepo;
import vn.edu.fpt.repository.EventRepo;
import vn.edu.fpt.model.dto.OrderHistoryDTO;
import vn.edu.fpt.repository.OrderRepo;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepo orderRepo;

    private CustomerRepo customerRepo;

    private DiscountCodeRepo discountCodeRepo;
    @Autowired
    private DiscountCodeService discountCodeService;

    public OrderService(OrderRepo orderRepo, CustomerRepo customerRepo, DiscountCodeRepo discountCodeRepo) {
        this.orderRepo = orderRepo;
        this.customerRepo = customerRepo;
        this.discountCodeRepo = discountCodeRepo;
    }

    public Page<OrderHistoryDTO> getMyOrderHistory(
            Integer customerId,
            String status,
            Pageable pageable) {
        return orderRepo.getOrderHistory(customerId, status, pageable);
    }

    public void save(Order order) {
        orderRepo.save(order);
    }

    public Optional<Order> getOrderById(int id) {
        return orderRepo.findById(id);
    }

    public List<Event> getEventByOrderId(int id) {
        return orderRepo.getEventByOrderId(id);
    }

    public List<TicketOrderDTO> getTicketsByOrderIdAndEventId(int orderId, int eventId) {
        return orderRepo.getTicketsByOrderIdAndEventId(orderId, eventId);
    }

    public List<Order> getRecentTransactions() {
        return orderRepo.findRecentTransactions(PageRequest.of(0, 7));
    }

    public List<RevenueDTO> getRevenueData() {
        System.out.println("VIP");
        List<Object[]> rows = orderRepo.getRevenueRawData();

        System.out.println("and do");
        List<RevenueDTO> result = new ArrayList<>();

        for (Object[] row : rows) {
            Timestamp ts = (Timestamp) row[5];
            LocalDateTime paidAt = ts != null ? ts.toLocalDateTime() : null;

            RevenueDTO dto = new RevenueDTO(
                    ((Number) row[0]).intValue(),   // OrderId
                    ((Number) row[1]).intValue(),   // CustomerId
                    (String) row[2],                // CustomerName
                    ((Number) row[3]).doubleValue(), // TotalAmount (đúng)
                    (String) row[4],                // Status
                    paidAt,
                    (String) row[6]                 // PaymentStatus
            );

            result.add(dto);
        }

        // lấy list orderId
        List<Integer> orderIds = result.stream()
                .map(RevenueDTO::getOrderId)
                .toList();

        // query event
        List<Object[]> eventRows = orderRepo.getEventsByOrderIds(orderIds);

        // map orderId -> event
        Map<Integer, List<Object[]>> eventMap = eventRows.stream()
                .collect(Collectors.groupingBy(r -> ((Number) r[0]).intValue()));

        // gán event vào DTO
        for (RevenueDTO dto : result) {
            List<Object[]> events = eventMap.get(dto.getOrderId());

            if (events != null && !events.isEmpty()) {
                // lấy event đầu tiên (hoặc join string nếu muốn)
                Object[] e = events.get(0);

                dto.setEventId(((Number) e[1]).longValue());
                dto.setEventName((String) e[2]);
            }
        }
        System.out.println("Het cuy");

        return result;
    }

    public boolean isDiscountCodeUsed(Integer discountCodeId) {
        List<Integer> discountCodeIds = orderRepo.getDiscountCodeId(discountCodeId);
        return discountCodeIds.isEmpty();
    }

    public List<Order> findExpiredDiscountCodeByOrderId(LocalDateTime expiredTime) {
        return orderRepo.findExpiredDiscountCodeByOrderId(expiredTime);
    }

    @Transactional
    public void releaseDiscount(LocalDateTime expiredTime) {
        List<Order> listOrders = this.findExpiredDiscountCodeByOrderId(expiredTime);
        for (Order order : listOrders) {
            DiscountCode discountCode = order.getDiscountCode();
            if (discountCode != null) {
                // Giam used count - 1
                discountCode.setUsedCount(discountCode.getUsedCount() - 1);
                // Giam giu cho - 1
                discountCodeService.deleteDiscountInCheckOut(discountCode.getId());
                order.setOrderStatus("PaymentFailed");
                order.setDiscountCode(null);
            }
        }
    }
}
