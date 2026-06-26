package vn.edu.fpt.scheduler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.edu.fpt.model.entity.DiscountCode;
import vn.edu.fpt.model.entity.Ticket;
import vn.edu.fpt.service.OrderService;
import vn.edu.fpt.service.TicketService;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentScheduler {
    private static final Logger logger = LoggerFactory.getLogger(PaymentScheduler.class);
    private final TicketService ticketService;
    private final OrderService orderService;

//    @Scheduled(cron = "*/30 * * * * *")
//    @Transactional
//    public void releaseExpiredTickets() {
//        logger.info("Scheduler start: checking expired reserved tickets...");
//        // Production cho 15p
//        // DEV cho 2p
//        LocalDateTime expiredTime = LocalDateTime.now().minusMinutes(2);
//
//        List<Ticket> tickets = ticketService.findExpiredReservedTickets(expiredTime);
//        logger.info("Found {} expired reserved tickets", tickets.size());
//        for (Ticket ticket : tickets) {
//            logger.info("Releasing ticket ID: {} (was RESERVED at {})",
//                    ticket.getId(),
//                    ticket.getReservedAt()
//            );
//
//            ticket.setTicketStatus("Available");
//            ticket.setOrderItem(null);
//            ticket.setReservedAt(null);
//        }
//        logger.info("Scheduler finished releasing tickets.");
//    }

    @Scheduled(cron = "*/30 * * * * *")
    @Transactional
    public void releaseExpiredTickets() {

        LocalDateTime expiredTime =
                LocalDateTime.now().minusMinutes(2);

        int updated =
                ticketService.releaseExpiredTickets(expiredTime);

        logger.info("Released {} expired tickets", updated);
    }

    @Scheduled(cron = "*/30 * * * * *")
    @Transactional
    public void releaseExpiredDiscount() {
        LocalDateTime expiredTime = LocalDateTime.now().minusMinutes(17);
        orderService.releaseDiscount(expiredTime);
        logger.warn("Release Order and Discount Code is Expired!");
    }


}
