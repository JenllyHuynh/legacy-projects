package vn.edu.fpt.service.session;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.dto.CheckoutItem;
import vn.edu.fpt.model.dto.TicketTypeDTO;
import vn.edu.fpt.model.entity.Customer;
import vn.edu.fpt.service.CustomerService;
import vn.edu.fpt.service.TicketTypeService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SessionService {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private TicketTypeService ticketTypeService;

    public String saveSession(HttpSession session,
                              Integer ticketTypeId,
                              Integer quantity) {

        TicketTypeDTO ticketType = ticketTypeService.getTicketTypeById(ticketTypeId);
        Integer eventId = ticketType.getEventId();
        Integer maxTicket = ticketType.getMaxTicketsPerUser();
        Double price = ticketType.getPrice().doubleValue();
        String ticketName = ticketType.getName();
        String email;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof OAuth2User oAuth2User) {
            email = oAuth2User.getAttribute("email");
        } else {
            email = auth.getName();
        }
        Optional<Customer> customerOptional = customerService.findByEmail(email);

        if (customerOptional.isPresent()) {
            List<CheckoutItem> checkout =
                    (List<CheckoutItem>) session.getAttribute("checkout");

            if (checkout == null) {
                checkout = new ArrayList<>();
            }

            // kiểm tra nếu ticket đã tồn tại thì cộng số lượng
            Optional<CheckoutItem> existing = checkout.stream()
                    .filter(i -> i.getTicketTypeId().equals(ticketTypeId))
                    .findFirst();

            if (existing.isPresent()) {
                CheckoutItem item = existing.get();
                if (item.getQuantity() + quantity > maxTicket) {
                    return String.valueOf(maxTicket - item.getQuantity());
                }
                item.setQuantity(item.getQuantity() + quantity);
            } else {
                if (quantity > maxTicket) {
                    return String.valueOf(maxTicket);
                }

                CheckoutItem item = new CheckoutItem();
                item.setTicketTypeId(ticketTypeId);
                item.setTicketName(ticketName);
                item.setPrice(price);
                item.setQuantity(quantity);
                item.setEventId(eventId);
                checkout.add(item);
            }

            session.setAttribute("checkout", checkout);
            return "ok";

        } else {
            return "Not Login";
        }

    }

    public List<CheckoutItem> getCheckoutItemsByEvent(HttpSession session, Integer eventId) {
        String email;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof OAuth2User oAuth2User) {
            email = oAuth2User.getAttribute("email");
        } else {
            email = auth.getName();
        }
        Optional<Customer> customerOptional = customerService.findByEmail(email);

        if (customerOptional.isPresent()) {
            List<CheckoutItem> checkout =
                    (List<CheckoutItem>) session.getAttribute("checkout");

            if (checkout == null) {
                checkout = new ArrayList<>();
            }

            List<CheckoutItem> eventCheckout = checkout.stream()
                    .filter(i -> i.getEventId().equals(eventId))
                    .toList();
            return eventCheckout;
        } else {
            return new ArrayList<>();
        }
    }

    public List<CheckoutItem> getAllInSession(HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Optional<Customer> customerOptional = customerService.findByEmail(email);

        if (customerOptional.isPresent()) {
            List<CheckoutItem> checkout =
                    (List<CheckoutItem>) session.getAttribute("checkout");

            if (checkout == null) {
                checkout = new ArrayList<>();
            }

            return checkout;
        } else {
            return null;
        }
    }

    public Double totalByEvent(List<CheckoutItem> checkoutItems) {
        if (checkoutItems != null) {
            Double total = checkoutItems.stream().
                    mapToDouble(CheckoutItem::getSubtotal).sum();
            return total;
        } else return 0.0;

    }

}
