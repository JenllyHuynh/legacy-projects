package vn.edu.fpt.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.model.dto.TicketOrderDTO;
import vn.edu.fpt.model.entity.*;
import vn.edu.fpt.service.*;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class InvoiceController {
    private final CustomerService customerService;
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final DiscountCodeService discountCodeService;

    public InvoiceController(CustomerService customerService, PaymentService paymentService, OrderService orderService, OrderItemService orderItemService, DiscountCodeService discountCodeService) {
        this.customerService = customerService;
        this.paymentService = paymentService;
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        this.discountCodeService = discountCodeService;
    }

    private String getEmailFromAuth(Authentication auth) {
        if (auth == null) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails) return ((UserDetails) principal).getUsername();
        if (principal instanceof OAuth2User) return ((OAuth2User) principal).getAttribute("email");
        // fallback (thường là email với form login, nhưng với OAuth2 có thể là "sub" nên không tin 100%)
        String name = auth.getName();
        if (name == null || "anonymousUser".equals(name)) return null;
        return name;
    }

    @GetMapping("/invoice/{txnRef}")
    public String viewInvoice(
            @PathVariable(value = "txnRef", required = false) String txnRef,
            Model model
    ) {
        // Get customer

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = getEmailFromAuth(auth);
        Optional<Customer> customerOptional = customerService.findByEmail(email);
        if (customerOptional.isEmpty()) {
            return "error/404";
        }

        Customer customer = customerOptional.get();

        // Get payment

        Optional<Payment> paymentOptional =  paymentService.getPaymentByTransactionIdPayment(txnRef);
        if (!paymentOptional.isPresent()) {
            return "error/404";
        }

        Payment payment = paymentOptional.get();
        // Order of payment
        Order orderOfPayment = payment.getOrder();
        // Check cus has order or not?
        Boolean customerHasOrderOfPayment = customer.getOrders().contains(orderOfPayment);
        if (!customerHasOrderOfPayment) {
            return "error/404";
        }

        // add model
        model.addAttribute("customer", customer);
        model.addAttribute("address", String.format("%s, %s, %s, %s", orderOfPayment
                .getStreetAddress(), orderOfPayment.getWard(), orderOfPayment.getCity(), orderOfPayment.getCountry()));
        model.addAttribute("payment", payment);
        model.addAttribute("order", orderOfPayment);

        // Tickets
        Map<Event, List<TicketOrderDTO>> eventTickets = new LinkedHashMap<>();

        List<Event> events = orderService.getEventByOrderId(orderOfPayment.getId());

        for (Event event : events) {
            List<TicketOrderDTO> tickets =
                    orderService.getTicketsByOrderIdAndEventId(orderOfPayment.getId(), event.getId());

            eventTickets.put(event, tickets);
        }


        BigDecimal subTotal = orderItemService.getSubtotalByOrderId(orderOfPayment.getId());
        DiscountCode discountOfOrder = orderOfPayment.getDiscountCode();
        DiscountCode discountCode = null;
        if (discountOfOrder != null) {
            Optional<DiscountCode> discountCodeOptional = discountCodeService.getDiscountOptCodeById(discountOfOrder.getId());
            if (discountCodeOptional.isPresent()) {
                discountCode = discountCodeOptional.get();
            }
        }
        List<OrderItem> orderItems = orderOfPayment.getOrderItems().stream().toList();
        BigDecimal discountTotal = BigDecimal.valueOf(paymentService.calculateDiscountOrderItems(orderItems, discountCode));
        System.out.println(discountTotal);
        model.addAttribute("discountTotal", discountTotal);
        model.addAttribute("subTotal", subTotal);
        model.addAttribute("discountCode", discountCode);

        model.addAttribute("eventTickets", eventTickets);


        return "payment/invoice";
     }
}
