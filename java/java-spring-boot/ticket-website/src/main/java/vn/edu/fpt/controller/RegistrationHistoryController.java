package vn.edu.fpt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.model.dto.OrderHistoryDTO;
import vn.edu.fpt.model.entity.Customer;
import vn.edu.fpt.model.entity.Payment;
import vn.edu.fpt.service.CustomerService;
import vn.edu.fpt.service.OrderService;
import vn.edu.fpt.service.PaymentService;
import vn.edu.fpt.util.PageSetting;

import java.util.Optional;

@Controller
public class RegistrationHistoryController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private PaymentService paymentService;

    @GetMapping("/history")
    public String viewHistory(Model model,
                              @RequestParam(required = false) String status,
                              @RequestParam(defaultValue = "0") int page
    ) {

        // Get user
        String email;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof OAuth2User oAuth2User) {
            email = oAuth2User.getAttribute("email");
        } else {
            email = auth.getName();
        }
        Optional<Customer> customerOptional = customerService.findByEmail(email);
        Customer customer = customerOptional.get();

        if (status != null && status.isBlank()) {
            status = null;
        }

        Page<Payment> paymentPage = paymentService.getAllPaymentHistory(customer.getId(), status, page);

        model.addAttribute("paymentPage", paymentPage);
        model.addAttribute("status", status);

        model.addAttribute("totalPages", paymentPage.getTotalPages());
        model.addAttribute("currentPage", paymentPage.getNumber());
        model.addAttribute("totalItems", paymentPage.getTotalElements());
        model.addAttribute("size", paymentPage.getNumberOfElements());
        return "user/orderHistory";
    }

}
