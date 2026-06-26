package vn.edu.fpt.service;

import org.springframework.stereotype.Service;
import vn.edu.fpt.repository.OrderItemRepo;
import vn.edu.fpt.repository.OrderRepo;

import java.math.BigDecimal;

@Service
public class OrderItemService {

    private OrderItemRepo orderItemRepo;

    private OrderRepo orderRepo;

    private TicketTypeService ticketTypeService;

    public OrderItemService(OrderItemRepo orderItemRepo, OrderRepo orderRepo, TicketTypeService ticketTypeService) {
        this.orderItemRepo = orderItemRepo;
        this.orderRepo = orderRepo;
        this.ticketTypeService = ticketTypeService;
    }

    public Integer countPurchasedTicket(Integer customerId, Integer ticketTypeId) {
        return orderItemRepo.countPurchasedTicket(customerId,  ticketTypeId);
    }


    public BigDecimal getSubtotalByOrderId(int orderId) {
        return orderItemRepo.getSubtotalByOrderId(orderId);
    }


}
