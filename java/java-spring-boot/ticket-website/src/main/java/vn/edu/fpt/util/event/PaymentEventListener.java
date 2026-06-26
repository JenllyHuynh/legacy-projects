package vn.edu.fpt.util.event;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.dto.TicketOrderDTO;
import vn.edu.fpt.model.entity.Customer;
import vn.edu.fpt.model.entity.Event;
import vn.edu.fpt.model.entity.Order;
import vn.edu.fpt.model.entity.Payment;
import vn.edu.fpt.service.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final CustomerService customerService;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Async
    @Transactional
    @EventListener
    public void handlePaymentEvent(PaymentCompletedEvent event) throws MessagingException {

        Order order = orderService.getOrderById(event.getOrderId()).get();
        Payment payment = paymentService.getPaymentById(event.getPaymentId()).get();
        Customer customer = customerService.getCustomerOptById(event.getCustomerId()).get();

        if (event.isSuccess()) {
            handleSuccess(customer, order, payment);
        } else {
            handleFail(customer, order, payment);
        }
    }

    private void handleSuccess(Customer customer, Order order, Payment payment) throws MessagingException {

        Map<Event, List<TicketOrderDTO>> eventTickets = new LinkedHashMap<>();

        List<Event> events =
                orderService.getEventByOrderId(order.getId());

        for (Event event : events) {
            List<TicketOrderDTO> tickets =
                    orderService.getTicketsByOrderIdAndEventId(
                            order.getId(),
                            event.getId()
                    );

            eventTickets.put(event, tickets);
        }
        String html =
                emailService.buildPurchaseSuccessEmail(
                        customer, order, payment, eventTickets
                );

        emailService.sendEmailPurchaseSuccess(customer.getEmail(), html);

        notificationService.send(
                customer.getId(),
                String.format("Payment Successful"),
                String.format("Your payment for order #%s has been successfully completed. " +
                                "Thank you for your purchase! We look forward to seeing you at the event.\n",
                        payment.getTransactionId()),
                "InApp"
        );
    }

    private void handleFail(Customer customer, Order order, Payment payment) throws MessagingException {

        Map<Event, List<TicketOrderDTO>> eventTickets = new LinkedHashMap<>();

        List<Event> events =
                orderService.getEventByOrderId(order.getId());

        for (Event event : events) {
            List<TicketOrderDTO> tickets =
                    orderService.getTicketsByOrderIdAndEventId(
                            order.getId(),
                            event.getId()
                    );

            eventTickets.put(event, tickets);
        }
        String html = emailService.buildPaymentFailedEmail(
                customer,
                order,
                payment,
                eventTickets
        );
        emailService.sendEmailPaymentFailed(customer.getEmail(), html);

        notificationService.send(
                customer.getId(),
                String.format("Payment Failed – Transaction #%s", payment.getTransactionId()),
                String.format("We regret to inform you that your payment for transaction #%s was unsuccessful. " +
                                "This may be due to insufficient balance, network issues, or an interruption during the payment process. " +
                                "Please review your payment details and try again. If the issue persists, feel free to contact our support team for assistance. " +
                                "We apologize for the inconvenience and hope to complete your transaction soon.",
                        payment.getTransactionId()),
                "InApp"
        );
    }
}
