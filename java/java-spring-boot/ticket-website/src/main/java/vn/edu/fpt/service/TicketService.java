package vn.edu.fpt.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.dto.MyTicketDTO;
import vn.edu.fpt.model.entity.Order;
import vn.edu.fpt.model.entity.OrderItem;
import vn.edu.fpt.model.entity.Ticket;
import vn.edu.fpt.repository.TicketRepo;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService {
    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);
    @Autowired
    private TicketRepo ticketRepository;
    @Autowired
    private TicketRepo ticketRepo;

    public List<MyTicketDTO> getMyTickets(Integer customerId) {

        List<MyTicketDTO> tickets =
                ticketRepository.findTicketsByCustomer(customerId);

        for (MyTicketDTO t : tickets) {

            String status = calculateTicketStatus(
                    t.getTicketStatus(),
                    t.getStartDateTime()
            );

            t.setDisplayStatus(status);
        }

        return tickets;
    }


    public String calculateTicketStatus(
            String ticketStatus,
            LocalDateTime eventStart
    ) {
        LocalDateTime now = LocalDateTime.now();
        if ("Cancelled".equals(ticketStatus)) {
            return "Cancelled";
        }

        if ("CheckedIn".equals(ticketStatus)) {
            return "Attended";
        }

        if (now.isBefore(eventStart)) {
            return "Upcoming";
        }

        return "Ended";
    }

    public Ticket getTopOneAvailableTicket(int ticketTypeId){

        List<Ticket> tickets = ticketRepo.findAvailableTickets(
                ticketTypeId,
                PageRequest.of(0,1)
        );

        return tickets.get(0);
    }

    @Transactional
    public void reserveTickets(Order order){

        for (OrderItem orderItem : order.getOrderItems()) {

            for(int i = 0; i < orderItem.getQuantity(); i++){

                List<Ticket> tickets = ticketRepository.findAvailableTickets(
                        orderItem.getTicketType().getId(),
                        PageRequest.of(0,1)
                );

                if(tickets.isEmpty()){
                    throw new RuntimeException("Ticket sold out");
                }

                Ticket ticket = tickets.get(0);

                ticket.setTicketStatus("Reserved");
                ticket.setOrderItem(orderItem);
                ticket.setReservedAt(LocalDateTime.now());

            }
        }
    }

    @Transactional
    public List<Ticket> findExpiredReservedTickets(LocalDateTime expiredTime) {
        return ticketRepository.findExpiredReservedTickets(expiredTime);
    }

    public int countNumberOfTicketAvailable(Integer ticketTypeId) {
        return ticketRepo.countTicketByTicketType_IdAndTicketStatus(ticketTypeId, "Available");
    }

    public int releaseExpiredTickets(LocalDateTime expiredTime) {
        return ticketRepository.releaseExpiredTickets(expiredTime);
    }
}
