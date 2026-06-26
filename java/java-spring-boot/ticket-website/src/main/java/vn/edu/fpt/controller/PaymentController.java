package vn.edu.fpt.controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.client.AddressClient;
import vn.edu.fpt.config.payment.VNPAY.VNPayConfig;
import vn.edu.fpt.model.dto.CheckoutFormDTO;
import vn.edu.fpt.model.dto.CheckoutItem;
import vn.edu.fpt.model.dto.TicketOrderDTO;
import vn.edu.fpt.model.dto.ValidationResult;
import vn.edu.fpt.model.entity.*;
import vn.edu.fpt.service.*;
import vn.edu.fpt.service.VNPAY.VNPayService;
import vn.edu.fpt.service.session.SessionService;
import vn.edu.fpt.util.VNPAY.VNPayUtil;
import vn.edu.fpt.util.event.PaymentCompletedEvent;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.microsoft.sqlserver.jdbc.StringUtils.isEmpty;
import static com.microsoft.sqlserver.jdbc.StringUtils.isNumeric;

@Controller
@RequestMapping()
public class PaymentController {
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    private final VNPayConfig vnPayConfig;
    private final CartService cartService;
    private final CartItemService cartItemService;
    private final CustomerService customerService;
    private final DiscountCodeService discountCodeService;
    private final VNPayService vNPayService;
    private final OrderService orderService;
    private final EventService eventService;
    private final PaymentService paymentService;
    private final TicketService ticketService;
    private final EmailService emailService;
    private final AddressClient addressClient;
    private final NotificationService notificationService;
    private final PdfService pdfService;
    private final OrderItemService orderItemService;
    private ValidationResult result = new ValidationResult();
    @Autowired
    private SessionService sessionService;
    @Autowired
    private TicketTypeService ticketTypeService;


    public PaymentController(CartService cartService, CartItemService cartItemService,
                             CustomerService customerService, DiscountCodeService discountCodeService,
                             VNPayService vNPayService, VNPayConfig vnPayConfig, OrderService orderService,
                             EventService eventService, PaymentService paymentService, TicketService ticketService,
                             EmailService emailService, AddressClient addressClient,
                             NotificationService notificationService, PdfService pdfService, OrderItemService orderItemService) {
        this.cartService = cartService;
        this.cartItemService = cartItemService;
        this.customerService = customerService;
        this.discountCodeService = discountCodeService;
        this.vNPayService = vNPayService;
        this.vnPayConfig = vnPayConfig;
        this.orderService = orderService;
        this.eventService = eventService;
        this.paymentService = paymentService;
        this.ticketService = ticketService;
        this.emailService = emailService;
        this.addressClient = addressClient;
        this.notificationService = notificationService;
        this.pdfService = pdfService;
        this.orderItemService = orderItemService;
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

    @Transactional
    @GetMapping("/checkout")
    public String checkout(
            @RequestParam(value = "isCheckOutWithoutCart", required = false, defaultValue = "false") String check,
            @RequestParam(value = "eventId", required = false) String eventId,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        // Get user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = getEmailFromAuth(auth);
        if (email == null) return "redirect:/auth/login";
        Optional<Customer> customerOptional = customerService.findByEmail(email);
        Customer customer = customerOptional.get();

        // CHECK IN WITHOUT CART
        boolean isCheckOutWithoutCart = false;

        Integer eventIdInt = null;

        List<CheckoutItem> checkoutItems = new ArrayList<>();

        try {
            isCheckOutWithoutCart = Boolean.parseBoolean(check);
        } catch (Exception e) {
            return "error/404";
        }

        if (isCheckOutWithoutCart) {
            try {
                eventIdInt = Integer.parseInt(eventId);
                if (eventService.getEventById(eventIdInt) == null) {
                    System.out.println("Event Id ko có!");
                    return "error/404";
                }
                checkoutItems = sessionService.getCheckoutItemsByEvent(session, eventIdInt);
            } catch (Exception e) {
                return "error/404";
            }
            if (checkoutItems.isEmpty()) {
                return "error/404";
            }
        }

        // CHECK IN WITH CART
        // Get cart of user
        Cart cart = cartService.getAtiveCart(customer.getId());
        // Get cart items
        List<CartItem> cartItems = cartItemService.getAllItem(cart.getId());

        // Tu dong them vao gio hang:
        for (CheckoutItem checkoutItem : checkoutItems) {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            Optional<TicketType> ticketTypeOptional = ticketTypeService.getTicketTypeOptById(checkoutItem.getTicketTypeId());
            if (ticketTypeOptional.isEmpty()) {
                return "error/404";
            } else if (!ticketTypeOptional.get().getIsActive()) {
                return "error/404";
            }
            cartItem.setTicketType(ticketTypeOptional.get());
            cartItem.setQuantity(checkoutItem.getQuantity());
            cartItem.setUnitPrice(BigDecimal.valueOf(checkoutItem.getPrice()));
            cartItem.setAddedAt(LocalDateTime.now());
            if (!cartItems.contains(cartItem)) {
                cartItems.add(cartItem);
            } else {
                System.out.println("Them trung san pham!");
            }
            cartItemService.save(cartItem);
        }

        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        // === App DiscountCode ===
        Integer discountId = (Integer) session.getAttribute("appliedDiscountId");
        Optional<DiscountCode> discountCodeOptional;
        DiscountCode discountCode = new DiscountCode();

        // Total
        double discountAmount = 0;

        // Already applied
        if (discountId != null) {
            discountCodeOptional = discountCodeService.getDiscountOptCodeById(discountId);
            if (discountCodeOptional.isEmpty()) {
                result.setErrorTitle("Invalid discount code");
                result.setMessage("We dont have this discount!");
                session.removeAttribute("appliedDiscountId");
                return handleCannotCheckOut(result, redirectAttributes);
            }
            discountCode = discountCodeOptional.get();
            discountAmount = paymentService.calculateDiscount(cartItems, discountCode);

        }


        List<Integer> eventIds = cartItems.stream()
                .map(item -> item.getTicketType().getEvent().getId())
                .collect(Collectors.toList());

        // Lấy discount theo event
        List<DiscountCode> listDiscountCodes =
                discountCodeService.getAvailableDiscountByEventIds(eventIds);

        model.addAttribute("listDiscountCodes", listDiscountCodes);

        Long total = cartItemService.getTotalCartItemByCartId(cart.getId());


        cartItems = cartItemService.getAllItem(cart.getId());

        for (CartItem item : cartItems) {
            if (LocalDateTime.now().isAfter(item.getTicketType().getSalesEndDate())) {
                result.setErrorTitle("Purchase Time Expired");
                result.setMessage("The time allowed to purchase tickets has expired.");
                return handleCannotCart(result, redirectAttributes);
            }
        }

        // Create CheckoutFormDTO
        CheckoutFormDTO checkoutFormDTO = new CheckoutFormDTO();
        checkoutFormDTO.setPaymentMethod("vnpay");
        checkoutFormDTO.setEmail(customer.getEmail());
        checkoutFormDTO.setCountry("Việt Nam");
        model.addAttribute("checkoutFormDTO", checkoutFormDTO);
        model.addAttribute("total", total);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("customer", customer);
        // Discount code
        model.addAttribute("discountAmount", discountAmount);
        model.addAttribute("finalTotal", total - discountAmount <= 0 ? 0 : total - discountAmount);
        model.addAttribute("discountCode", discountCode);
        session.removeAttribute("checkout");
        return "user/checkout";
    }

    /*
     * ===================
     * ======Discount=====
     * ===================
     * */
    @Transactional
    @PostMapping("/apply-discount-code")
    public String appDiscountCode(
            @RequestParam("discountId") String discountId,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        // User
        // Get user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = getEmailFromAuth(auth);
        if (email == null) return "redirect:/auth/login";
        Optional<Customer> customerOptional = customerService.findByEmail(email);
        Customer customer = customerOptional.get();
        // Get cart of user
        Cart cart = cartService.getAtiveCart(customer.getId());
        // Get cart items
        List<CartItem> cartItems = cartItemService.getAllItem(cart.getId());
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        // Validate
        int idIntPost;
        try {
            idIntPost = Integer.parseInt(discountId);
        } catch (Exception e) {
            result.setErrorTitle("Error while apply discount code");
            result.setMessage("Invalid Discount code form!");
            return handleCannotCheckOut(result, redirectAttributes);
        }

        // If user already apply discount code
        Integer idInt = (Integer) session.getAttribute("appliedDiscountId");
        DiscountCode discountCode;
        if (idInt == null) {
            discountCode = new DiscountCode();
        } else {
            // Discount code already applied is the same with Post discount code
            if (idInt == idIntPost) {
                result.setErrorTitle("Discount code đã sử dụng");
                result.setMessage("Bạn đã sử dụng discount code này! Vui lòng sử dụng cái khác. [DEV check xem db có tăng lượt sử dụng khi hiện tbao này hay không?]");
                return handleCannotCheckOut(result, redirectAttributes);
            } else {
                Integer id = (Integer) session.getAttribute("appliedDiscountId");
                Optional<DiscountCode> discountCodeOptional;

                if (id != null) {
                    discountCodeOptional = discountCodeService.getDiscountOptCodeById(id);
                    if (discountCodeOptional.isEmpty()) {
                        result.setErrorTitle("Có lỗi trong quá trình xóa discount code!");
                        result.setMessage("Phát hiện lỗi trong quá trình xóa discount! Vui lòng chọn lại!");
                        return handleCannotCheckOut(result, redirectAttributes);
                    } else {
                        discountCodeService.deleteDiscountInCheckOut(id);
                        session.removeAttribute("appliedDiscountId");
                    }
                }

            }
        }

        // === Apply discount form post ===
        Optional<DiscountCode> discountCodePost = discountCodeService.getDiscountOptCodeById(idIntPost);

        // Discount code is invalid
        if (discountCodePost.isEmpty()) {
            result.setErrorTitle("Invalid discount code");
            result.setMessage("We dont have this discount!");
            return handleCannotCheckOut(result, redirectAttributes);
        }

        // Valid discount code
        discountCode = discountCodePost.get();

        // Invalid ticket - event - discount
        List<Integer> eventIds = cartItems.stream()
                .map(item -> item.getTicketType().getEvent().getId())
                .collect(Collectors.toList());

        // Lấy discount theo event
        List<DiscountCode> listDiscountCodes =
                discountCodeService.getAvailableDiscountByEventIds(eventIds);

        // Check xem đúng discount trong các sự kiện cần mua ko
        if (!listDiscountCodes.contains(discountCode)) {
            result.setErrorTitle("Invalid discount code");
            result.setMessage("We dont have this discount for these event!");
            return handleCannotCheckOut(result, redirectAttributes);
        }

        // Giu cho cho discount
        boolean isAvailable = discountCodeService.reserveDiscount(discountCode.getId());

        // Bước này đã update trong db rồi
        if (!isAvailable) {
            result.setErrorTitle("Hết lượt dùng");
            result.setMessage("We dont have this discount for these event!");
            return handleCannotCheckOut(result, redirectAttributes);
        }

        if (!discountCode.getIsActive()) {
            result.setErrorTitle("Discount Code không có sẵn");
            result.setMessage("We dont have this discount for these event!");
            return handleCannotCheckOut(result, redirectAttributes);
        }

        session.setAttribute("appliedDiscountId", discountCode.getId());
        redirectAttributes.addFlashAttribute("success", "Applied successfully");
        return "redirect:/checkout";
    }

    @Transactional
    @PostMapping("/delete/discount")
    public String deleteDiscount(
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        // Lay discount code
        Integer discountId = (Integer) session.getAttribute("appliedDiscountId");
        Optional<DiscountCode> discountCodeOptional;

        if (discountId != null) {
            discountCodeOptional = discountCodeService.getDiscountOptCodeById(discountId);
            if (discountCodeOptional.isEmpty()) {
                result.setErrorTitle("Có lỗi trong quá trình xóa discount code!");
                result.setMessage("Phát hiện lỗi trong quá trình xóa discount! Vui lòng chọn lại!");
                return handleCannotCheckOut(result, redirectAttributes);
            } else {
                discountCodeService.deleteDiscountInCheckOut(discountId);
                session.removeAttribute("appliedDiscountId");
            }
        }

        result.setErrorTitle("Đã xóa discount");
        result.setMessage("Đã xóa discount! Vui lòng chọn discount khác!");
        return handleCannotCheckOut(result, redirectAttributes);
    }

    //    public double calculateTotal(List<CartItem> items, DiscountCode discountCode) {
//
//        double total = 0;
//        boolean applicable = false;
//        double discountAmount = 0;
//        // 1️⃣ tính tổng + check discount áp dụng được không
//        for (CartItem item : items) {
//
//            double itemTotal =
//                    item.getTicketType().getPrice().doubleValue()
//                            * item.getQuantity();
//
//            total += itemTotal;
//
//            // discount áp dụng nếu cùng event
//            if (discountCode != null &&
//                    item.getTicketType()
//                            .getEvent()
//                            .getId()
//                            .equals(discountCode.getEvent().getId())) {
//
//                applicable = true;
//            }
//        }
//
//        // 2️⃣ apply discount (chỉ 1 lần)
//        if (applicable && discountCode != null) {
//
//            if ("Percentage".equals(discountCode.getDiscountType())) {
//                discountAmount = Math.max(total * discountCode.getDiscountValue()
//                        .doubleValue() / 100, 0);
//            } else {
//                discountAmount = Math.max(discountCode.getDiscountValue().doubleValue(), 0);
//
//            }
//        }
//        return discountAmount;
//    }
//    public double calculateDiscount(List<CartItem> items,
//                                    DiscountCode discountCode) {
//
//        if (discountCode == null) {
//            return 0;
//        }
//
//        double eventSubtotal = 0;
//
//        // 1️⃣ chỉ cộng tiền item thuộc event của discount
//        for (CartItem item : items) {
//
//            if (item.getTicketType()
//                    .getEvent()
//                    .getId()
//                    .equals(discountCode.getEvent().getId())) {
//
//                eventSubtotal +=
//                        item.getTicketType().getPrice().doubleValue()
//                                * item.getQuantity();
//            }
//        }
//
//        // không có item hợp lệ
//        if (eventSubtotal == 0) {
//            return 0;
//        }
//
//        // 2️⃣ apply discount
//        if ("Percentage".equals(discountCode.getDiscountType())) {
//
//            return Math.max(
//                    eventSubtotal *
//                            discountCode.getDiscountValue().doubleValue() / 100,
//                    0
//            );
//
//        } else { // Fixed amount
//
//            return Math.min(
//                    discountCode.getDiscountValue().doubleValue(),
//                    eventSubtotal // tránh âm tiền
//            );
//        }
//    }

    @Transactional
    @PostMapping("/checkout")
    public String checkoutPost(
            RedirectAttributes redirectAttributes,
            @ModelAttribute("checkoutFormDTO") CheckoutFormDTO checkoutFormDTO,
            HttpSession session
    ) {
        // If user do not agree with policy
        if (!checkoutFormDTO.isAgreePolicy()) {
            result.setErrorTitle("You must agree poliry before make a payment!");
            result.setMessage("You must agree poliry before make a payment!");
            return handleCannotCheckOut(result, redirectAttributes);
        }
        // Get user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = getEmailFromAuth(auth);
        if (email == null) return "redirect:/auth/login";
        Optional<Customer> customerOptional = customerService.findByEmail(email);
        Customer customer = customerOptional.get();

        // Get cart of user
        Cart cart = cartService.getAtiveCart(customer.getId());
        // Get cart items
        List<CartItem> cartItems = cartItemService.getAllItem(cart.getId());
        // cart empty check (EARLY EXIT)
        if (cartItems == null || cartItems.isEmpty()) {
            return "redirect:/cart";
        }
        // Check max ticket per user (including purchased tickets)
        for (CartItem item : cartItems) {

            TicketType ticketType = item.getTicketType();
            Integer maxPerUser = ticketType.getMaxTicketsPerUser();
            if (LocalDateTime.now().isAfter(item.getTicketType().getSalesEndDate())) {
                result.setErrorTitle("Purchase Time Expired");
                result.setMessage("The time allowed to purchase tickets has expired.");
                return handleCannotCart(result, redirectAttributes);
            }
            // nếu ticket có giới hạn
            if (maxPerUser != null && maxPerUser > 0) {

                Integer purchasedQuantity =
                        orderItemService.countPurchasedTicket(
                                customer.getId(),
                                ticketType.getId()
                        );

                if (purchasedQuantity == null) {
                    purchasedQuantity = 0;
                }

                int totalAfterCheckout =
                        purchasedQuantity + item.getQuantity();

                if (totalAfterCheckout > maxPerUser) {

                    result.setErrorTitle("Ticket quantity exceeded!");
                    result.setMessage(
                            "You have already purchased "
                                    + purchasedQuantity
                                    + " ticket(s). Maximum allowed for \""
                                    + ticketType.getName()
                                    + "\" is "
                                    + maxPerUser
                                    + "."
                    );

                    return handleCannotCheckOut(result, redirectAttributes);
                }
            }
        }

        // Calculate Total
        BigDecimal total = BigDecimal.ZERO; // dùng luôn hằng số sẵn có
        for (CartItem item : cartItems) {

            BigDecimal itemTotal =
                    item.getTicketType().getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(itemTotal); // phải gán lại
        }

        // ===== Check inventory =====
        boolean canBuy = true;

        for (CartItem ci : cartItems) {

            int availableLeft = ticketService
                    .countNumberOfTicketAvailable(ci.getTicketType().getId());

            ci.getTicketType().setAvailableLeft(availableLeft);

            // FIX: phải là < (không phải <=)
            if (availableLeft < ci.getQuantity()) {
                canBuy = false;
                break;
            }
        }

        if (!canBuy) {
            result.setErrorTitle("Tickets are no longer available!");
            result.setMessage("Some tickets in your cart are no longer available in requested quantity.");
            return handleCannotCheckOut(result, redirectAttributes);
        }

        // Create order
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderStatus("Pending");
        order.setOrderDate(LocalDateTime.now());
        // ===== Apply Discount Code =====
        Integer discountCodeId = (Integer) session.getAttribute("appliedDiscountId");
        // Test Get discount index = 0
        if (discountCodeId == null) {
            order.setDiscountCode(null);
            order.setTotalAmount(total);
        } else {
            Optional<DiscountCode> discountCodeOptional = discountCodeService.getDiscountOptCodeById(discountCodeId);
            DiscountCode discountCode = new DiscountCode();
            if (discountCodeOptional.isPresent()) {
                discountCode = discountCodeOptional.get();
                if (!discountCode.getIsActive()) {
                    result.setErrorTitle("Discount code is inactive!");
                    result.setMessage("Discount code is inactive! Please try another one!");
                    return handleCannotCheckOut(result, redirectAttributes);
                }
                if (discountCode.getValidFrom().isAfter(LocalDateTime.now())) {
                    result.setErrorTitle("Discount code is access to use!");
                    result.setMessage("Discount code is in the future! Please try another one!");
                    return handleCannotCheckOut(result, redirectAttributes);
                }
                if (discountCode.getValidTo().isBefore(LocalDateTime.now())) {
                    result.setErrorTitle("Discount code is expire!");
                    result.setMessage("Discount code is expire! Please try another one!");
                    return handleCannotCheckOut(result, redirectAttributes);
                }
                if (discountCode.getUsedCount() + 1 > discountCode.getMaxUses()) {
                    result.setErrorTitle("Discount code is sold out!");
                    result.setMessage("Discount code is sold out! Please try another one!");
                    return handleCannotCheckOut(result, redirectAttributes);
                }
                // Set discount
                order.setDiscountCode(discountCode);
                total = total.subtract(BigDecimal.valueOf(paymentService.calculateDiscount(cartItems, discountCode)));
                order.setTotalAmount(total);
            } else {
                order.setTotalAmount(BigDecimal.valueOf(cartItemService.getTotalCartItemByCartId(cart.getId())));
            }
        }

//        order.setCountry(checkoutFormDTO.getCountry());
//        order.setStreetAddress(checkoutFormDTO.getFullAddress());
        // Convert id of location
//        Venue venue = new Venue();
//        venue.setCity(checkoutFormDTO.getCity());
//        venue.setWard(checkoutFormDTO.getWard());
//        convertLocationCodeToName(venue);
//        order.setCity(venue.getCity());
//        order.setWard(venue.getWard());
        // Luu order vao db
        orderService.save(order);
        Set<OrderItem> orderItems = cartItems.stream()
                .map(item -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setTicketType(item.getTicketType());
                    orderItem.setUnitPrice(item.getUnitPrice());
                    return orderItem;
                })
                .collect(Collectors.toSet());
        order.setOrderItems(orderItems);
        order.setOrderInfo(String.format("Payment for Order #%s %s %s EvenGo VNPAY",
                cart.getId(), customer.getFullName(), LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("MMddyyyyHHmm"))));
        orderService.save(order);
        // Dat truoc ve
        ticketService.reserveTickets(order);
        redirectAttributes.addAttribute("orderId", order.getId());
        session.removeAttribute("appliedDiscountId");
        return "redirect:/create-payment";
    }

    /*
     * ===================
     * =======VNPAY=======
     * ===================
     * */
    @GetMapping("/create-payment")
    public String createPayment(
            HttpServletRequest request,
            @RequestParam("orderId") Integer orderId
    ) throws UnsupportedEncodingException {
        Optional<Order> orderOptional = orderService.getOrderById(orderId);
        if (!orderOptional.isPresent()) {
            return "redirec:/cart";
        }
        Order order = orderOptional.get();
        String paymentUrl = vNPayService.createPaymentUrl(request, order);
        return "redirect:" + paymentUrl;
    }

    @GetMapping("/payment-detail/{txnRef}")
    public String paymentDetail(
            @PathVariable(value = "txnRef", required = true) String txnRef,
            HttpServletRequest request,
            Model model
    ) {
        // Get customer
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        System.out.println("AUTH = " + auth);
        System.out.println("NAME = " + (auth != null ? auth.getName() : "null"));
        String email = getEmailFromAuth(auth);

        Optional<Customer> customerOptional = customerService.findByEmail(email);
        Customer customer = customerOptional.get();

        // ========= Start Validate ==========
        System.out.println("============================");
        System.out.println(txnRef);
        System.out.println("============================");
        Optional<Payment> paymentOptional = paymentService.getPaymentByTransactionIdPayment(txnRef);

        if (!paymentOptional.isPresent()) {
            return "error/404";
        }
        Payment payment = paymentOptional.get();

        if (payment.getPaymentStatus() == null) {
            return "error/404";
        }

        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                fields.put(fieldName, fieldValue);
            }
        }

        String secureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        String signValue = VNPayUtil.hashAllFields(fields, vnPayConfig.getSecretKey());
        String responseCode = request.getParameter("vnp_ResponseCode");

        if (payment.getPaymentStatus().equalsIgnoreCase("Failed")) {
            model.addAttribute("payment", payment);
            return "payment/history/payment-failed";
        } else if (payment.getPaymentStatus().equalsIgnoreCase("Pending")) {
            return "error/404";
        }
        if (!payment.getPaymentStatus().equalsIgnoreCase("Completed")) {
            return "error/404";
        }

        if (payment.getGatewayResponse().equalsIgnoreCase("00")) {
            Order orderPayment = payment.getOrder();
            Boolean customerHasOrderOfPayment = customer.getOrders().contains(orderPayment);
            if (!customerHasOrderOfPayment) {
                return "error/404";
            }
            // ========= End Validate ==========
            // Payment success:

            Map<Event, List<TicketOrderDTO>> eventTickets = new LinkedHashMap<>();

            List<Event> events = orderService.getEventByOrderId(orderPayment.getId());

            for (Event event : events) {
                List<TicketOrderDTO> tickets =
                        orderService.getTicketsByOrderIdAndEventId(
                                orderPayment.getId(), event.getId()
                        );
                eventTickets.put(event, tickets);
            }

            BigDecimal subTotal = orderItemService.getSubtotalByOrderId(orderPayment.getId());
            DiscountCode discountOfOrder = orderPayment.getDiscountCode();
            DiscountCode discountCode = null;
            if (discountOfOrder != null) {
                Optional<DiscountCode> discountCodeOptional = discountCodeService.getDiscountOptCodeById(discountOfOrder.getId());
                if (discountCodeOptional.isPresent()) {
                    discountCode = discountCodeOptional.get();
                }
            }
            List<OrderItem> orderItems = orderPayment.getOrderItems().stream().toList();
            BigDecimal discountTotal = BigDecimal.valueOf(paymentService.calculateDiscountOrderItems(orderItems, discountCode));

            model.addAttribute("discountTotal", discountTotal);
            model.addAttribute("subTotal", subTotal);
            model.addAttribute("discountCode", discountCode);
            model.addAttribute("order", orderPayment);
            model.addAttribute("payment", payment);
            model.addAttribute("customer", customer);
            model.addAttribute("eventTickets", eventTickets);

        }
        return "payment/payment-success";
    }

    @GetMapping("/api/payment-status")
    @ResponseBody
    public Map<String, Object> checkPaymentStatus(@RequestParam String txnRef) {

        Optional<Payment> payment = paymentService.getPaymentByTransactionIdPayment(txnRef);

        Map<String, Object> res = new HashMap<>();

        if (payment.isEmpty()) {
            res.put("status", "NOT_FOUND");
        } else {
            res.put("status", payment.get().getPaymentStatus()); // PENDING / SUCCESS / FAILED
        }

        return res;
    }

    @Transactional
    @GetMapping("/payment-return")
    public String paymentReturn(
            HttpServletRequest request,
            Model model
    ) throws MessagingException {
        // Get customer
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // chưa login → bắt login
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }
        System.out.println("AUTH = " + auth);
        System.out.println("NAME = " + (auth != null ? auth.getName() : "null"));
        String email = getEmailFromAuth(auth);
        if (email == null) return "redirect:/auth/login";
        Optional<Customer> customerOptional = customerService.findByEmail(email);
        Customer customer = customerOptional.get();
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                fields.put(fieldName, fieldValue);
            }
        }
        String secureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        String signValue = VNPayUtil.hashAllFields(fields, vnPayConfig.getSecretKey());
        if (secureHash != null && secureHash.equals(signValue)) {
            String responseCode = request.getParameter("vnp_ResponseCode");
            String txnRef = request.getParameter("vnp_TxnRef");
            System.out.println("VNPAY HASH = " + secureHash);
            System.out.println("LOCAL HASH = " + signValue);

            // TESTING

            Payment payment = null;
            if ("00".equals(responseCode)) {
                Optional<Payment> paymentOptional = paymentService.getPaymentByTransactionIdPayment(txnRef);
                if (!paymentOptional.isPresent()) {
                    return "payment/payment-error";
                }
                payment = paymentOptional.get();
                Order orderPayment = payment.getOrder();
                Boolean customerHasOrderOfPayment = customer.getOrders().contains(orderPayment);
                if (!customerHasOrderOfPayment) {
                    return "error/404";
                }
                // TODO: DO NOT UPDATE DB IN THIS TRANSACTION
                // Payment success:
                String payDate = request.getParameter("vnp_PayDate");
                DateTimeFormatter inputFormatter =
                        DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                DateTimeFormatter outputFormatter =
                        DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(payDate, inputFormatter);
                String formattedDate = dateTime.format(outputFormatter);
                model.addAttribute("customer", customer);
                model.addAttribute("vnp_PayDate", formattedDate);
                model.addAttribute("vnp_Amount", Long.parseLong(request.getParameter("vnp_Amount")));
                model.addAttribute("txnRef", txnRef);
                return "payment/success";
            } else {
                Optional<Payment> paymentOptional = paymentService.getPaymentByTransactionIdPayment(txnRef);
                if (!paymentOptional.isPresent()) {
                    return "payment/payment-error";
                }
                payment = paymentOptional.get();
                // TESTING
                model.addAttribute("message", "Payment Failed");
                model.addAttribute("orderId", txnRef);
                model.addAttribute("payment", payment);
                return "payment/payment-fail";
            }
        } else {
            model.addAttribute("message", "Invalid Signature");
            return "payment/payment-error";
        }
    }

    @Transactional
    @GetMapping("/payment-ipn")
    public ResponseEntity<Map<String, String>> paymentIPN(HttpServletRequest request, HttpSession httpSession) {
        Map<String, String> response = new HashMap<>();

        System.out.println(" ==== BẮT ĐẦU GỌI IPN ====");
        System.out.println("==== VNPAY ====");

        try {
            Map<String, String> fields = new HashMap<>();
            System.out.println("Đang lấy tham số từ ngân hàng...");
            for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {

                String fieldName = params.nextElement();
                String fieldValue = request.getParameter(fieldName);

                if (fieldValue != null && !fieldValue.isEmpty()) {
                    fields.put(fieldName, fieldValue);
                    System.out.println(String.format("%s - %s", fieldName, fieldValue));
                }
            }
            System.out.println("=================================");
            String secureHash = request.getParameter("vnp_SecureHash");

            fields.remove("vnp_SecureHash");
            fields.remove("vnp_SecureHashType");

            String signValue = VNPayUtil.hashAllFields(fields, vnPayConfig.getSecretKey());
            System.out.println("Sign Value: " + signValue);
            if (secureHash != null && secureHash.equals(signValue)) {
                System.out.println("MẬT KHẨU ĐÃ ĐÚNG");
                String responseCode = request.getParameter("vnp_ResponseCode");
                String txnRef = request.getParameter("vnp_TxnRef");
                String transactionNo = request.getParameter("vnp_TransactionNo");

                // Server side


                boolean checkOrderId = true;
                boolean checkAmount = true;
                boolean checkOrderStatus = true;
                boolean checkTmnCode = true;
                DiscountCode discountCode = null;
                boolean checkDiscount = false;


                System.out.println("Check order id: " + checkOrderId);
                System.out.println("Check amount: " + checkAmount);
                System.out.println("Check order status: " + checkOrderStatus);
                System.out.println("Check tmn code: " + checkTmnCode);
                System.out.println("Check discount: " + checkDiscount);

                BigDecimal totalAmount = BigDecimal.valueOf(Long.parseLong(request.getParameter("vnp_Amount")))
                        .divide(BigDecimal.valueOf(100));


                Optional<Payment> paymentOptional = paymentService.getPaymentByTransactionIdPayment(txnRef);
                if (!paymentOptional.isPresent()) {
                    System.out.println("===================================");
                    System.out.println("Hệ thống đã tạo thành công payment");
                    System.out.println("===================================");
                    response.put("RspCode", "01");
                    response.put("Message", "Order not Found");
                    return ResponseEntity.ok(response);
                }
                Payment payment = paymentOptional.get();
                Order order = payment.getOrder();
                BigDecimal amount = order.getTotalAmount();

                // Check discount
                if (order.getDiscountCode() != null) {

                    Optional<DiscountCode> discountCodeOptional =
                            discountCodeService.getDiscountOptCodeById(
                                    order.getDiscountCode().getId()
                            );

                    if (discountCodeOptional.isPresent()) {
                        discountCode = discountCodeOptional.get();
                        checkDiscount = true;
                        System.out.println("Có discount code: " + discountCode.getId());
                    }
                } else {
                    System.out.println("Order không có discount");
                }
                System.out.printf("Thông tin order:");
                System.out.println(order.toString());
                System.out.println("Số tiền thanh toán: " + amount);

                if (totalAmount.compareTo(amount) != 0) {
                    System.out.println("Số tiền không khớp!");
                    if (discountCode != null) {
                        discountCode.setReservedCount(Math.max(discountCode.getReservedCount() - 1, 0));
                    }
                    response.put("RspCode", "04");
                    response.put("Message", "Invalid Amount");
                    return ResponseEntity.ok(response);
                }

                if (
                        (
                                payment.getVnpTransactionno() != null
                                        || payment.getPaymentStatus().equalsIgnoreCase("Completed")
                                        || order.getOrderStatus().equalsIgnoreCase("Paid")
                        )
                                && checkDiscount
                ) {
                    System.out.println("Có discount code, đơn hàng đã already completed!");
                    if (discountCode != null) {
                        discountCode.setReservedCount(Math.max(discountCode.getReservedCount() - 1, 0));
                    }
                    response.put("RspCode", "02");
                    response.put("Message", "Order already confirmed");
                    return ResponseEntity.ok(response);

                }

                if (
                        (
                                payment.getPaymentStatus().equalsIgnoreCase("Completed")
                                        || order.getOrderStatus().equalsIgnoreCase("Paid")
                        )
                ) {
                    System.out.println("Không có discount code, đơn hàng đã already completed!");

                    response.put("RspCode", "02");
                    response.put("Message", "Order already confirmed");
                    return ResponseEntity.ok(response);
                }

                if (!vnPayConfig.getTmnCode().equals(request.getParameter("vnp_TmnCode"))) {
                    System.out.println("Chữ ký khách hàng bị sai!");
                    checkTmnCode = false;
                }

                if (checkTmnCode) {
                    System.out.println("PASS: CHECK TMN CODE");
                    if (checkOrderId) {
                        System.out.println("PASS: CHECK Order Id");

                        if (checkAmount) {
                            System.out.println("PASS: CHECK Check amount");

                            if (checkOrderStatus) {
                                System.out.println("PASS: CHECK status");

                                if ("00".equals(responseCode)) {
                                    System.out.println("Ngân hàng trả tham số 00: Thanh toán thành công. Chuẩn bị cập nhật bên trong db!");
                                    // TODO: update order status = PAID
                                    System.out.println("================ PAYMENT FOR ORDER ================");
                                    order.setOrderStatus("Paid");
                                    payment.setPaymentStatus("Completed");
                                    payment.setPaidAt(LocalDateTime.now());
                                    payment.setUpdatedAt(LocalDateTime.now());
                                    payment.setGatewayResponse(responseCode);
                                    payment.setVnpTransactionno(transactionNo);
                                    payment.setVnp_BankCode(request.getParameter("vnp_BankCode"));
                                    payment.setVnp_BankTranNo(request.getParameter("vnp_BankTranNo"));
                                    payment.setVnp_CardType(request.getParameter("vnp_CardType"));
                                    payment.setVnp_OrderInfo(request.getParameter("vnp_OrderInfo"));
                                    payment.setVnp_TransactionStatus(request.getParameter("vnp_TransactionStatus"));
                                    // Set ticket cho orderItem
                                    // Get all OrderItems
                                    Set<OrderItem> orderItems = order.getOrderItems();
                                    for (OrderItem orderItem : orderItems) {
                                        for (int i = 0; i < orderItem.getQuantity(); i++) {
                                            for (Ticket ticket : orderItem.getTickets()) {
                                                ticket.setTicketStatus("Sold");
                                            }
                                        }
                                    }
                                    Cart cart = cartService.getAtiveCart(order.getCustomer().getId());
                                    cartItemService.deleteCartItemByCartId(cart.getId());
                                    // SEND EMAIL
                                    Map<Event, List<TicketOrderDTO>> eventTickets = new LinkedHashMap<>();

                                    List<Event> events = orderService.getEventByOrderId(order.getId());

                                    for (Event event : events) {
                                        List<TicketOrderDTO> tickets =
                                                orderService.getTicketsByOrderIdAndEventId(order.getId(), event.getId());

                                        eventTickets.put(event, tickets);
                                    }
                                    Customer customer = order.getCustomer();

                                    // TESTING MAIL
                                    eventPublisher.publishEvent(
                                            new PaymentCompletedEvent(customer.getId(), order.getId(), payment.getId(), true)
                                    );
                                    if (checkDiscount) {
                                        System.out.println("Cập nhật discount");
                                        discountCode.setUsedCount(discountCode.getUsedCount() + 1);
                                        discountCode.setReservedCount(Math.max(Math.max(discountCode.getReservedCount() - 1, 0), 0));
                                    }
                                    httpSession.removeAttribute("appliedDiscountId");
                                } else {
                                    Set<OrderItem> orderItems = order.getOrderItems();
                                    for (OrderItem orderItem : orderItems) {
                                        for (int i = 0; i < orderItem.getQuantity(); i++) {
                                            for (Ticket ticket : orderItem.getTickets()) {
                                                ticket.setTicketStatus("Available");
                                            }
                                        }
                                    }
                                    System.out.println("THANH TOÁN THẤT BẠI. TIẾN HÀNH GỬI MAIL");
                                    order.setDiscountCode(null);
                                    if (checkDiscount) {
                                        discountCode.setReservedCount(Math.max(discountCode.getReservedCount() - 1, 0));
                                    }
                                    // TODO update PaymentStatus = 2 (Failed)
                                    order.setOrderStatus("PaymentFailed");
                                    payment.setPaymentStatus("Failed");
                                    // KO SET PAIDAT
                                    payment.setUpdatedAt(LocalDateTime.now());
                                    payment.setGatewayResponse(responseCode);
                                    payment.setVnpTransactionno(transactionNo);
                                    Customer customer = order.getCustomer();
                                    // GỬI MAIL
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
                                    eventPublisher.publishEvent(
                                            new PaymentCompletedEvent(customer.getId(), order.getId(), payment.getId(), false)
                                    );
                                    if (checkDiscount) {
                                        discountCode.setReservedCount(Math.max(discountCode.getReservedCount() - 1, 0));
                                    }

                                }
                                orderService.save(order);
                                paymentService.save(payment);
                                response.put("RspCode", "00");
                                response.put("Message", "Confirm Success");
                            } else {
                                System.out.println("Đơn hàng đã được confirm!");
                                order.setDiscountCode(null);
                                response.put("RspCode", "02");
                                response.put("Message", "Order already confirmed");
                            }
                        } else {
                            System.out.println("Số tiền không hợp lệ!");
                            order.setDiscountCode(null);
                            response.put("RspCode", "04");
                            response.put("Message", "Invalid Amount");
                        }
                    } else {
                        System.out.println("Không có order trong db!");
                        order.setDiscountCode(null);
                        response.put("RspCode", "01");
                        response.put("Message", "Order not Found");
                    }
                }
            } else {
                System.out.println("Invalid checksum");
                response.put("RspCode", "97");
                response.put("Message", "Invalid Checksum");
            }
        } catch (Exception e) {
            System.out.println("===== IPN ERROR =====");
            e.printStackTrace();
            System.out.println("Lỗi bất định");
            response.put("RspCode", "99");
            response.put("Message", "Unknown error");
        }
        return ResponseEntity.ok(response);
    }

    private void convertLocationCodeToName(Venue venue) {

        if (isNumeric(venue.getCity())) {
            venue.setCity(
                    addressClient
                            .getProvinceByProvinceCode(venue.getCity(), 1)
                            .getName()
            );
        } else {
            venue.setCity(venue.getCity().replace("_", " "));
        }

        if (isNumeric(venue.getWard())) {
            venue.setWard(
                    addressClient
                            .getWardByWardCode(venue.getWard())
                            .getName()
            );
        } else {
            venue.setWard(venue.getWard().replace("_", " "));
        }
    }

    // Download invoice
    @GetMapping("/invoice/{txnRef}/download")
    public ResponseEntity<byte[]> download(@PathVariable String txnRef) {

        // ========= Start Get Information =============
        // Get customer

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = getEmailFromAuth(auth);
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("Invoice not found");
        }

        Optional<Customer> customerOptional = customerService.findByEmail(email);
        if (customerOptional.isEmpty()) {
            throw new RuntimeException("Invoice not found");
        }

        Customer customer = customerOptional.get();

        // Get payment

        Optional<Payment> paymentOptional = paymentService.getPaymentByTransactionIdPayment(txnRef);
        if (!paymentOptional.isPresent()) {
            throw new RuntimeException("Invoice not found");
        }

        Payment payment = paymentOptional.get();
        // Order of payment
        Order orderOfPayment = payment.getOrder();
        // Check cus has order or not?
        Boolean customerHasOrderOfPayment = customer.getOrders().contains(orderOfPayment);
        if (!customerHasOrderOfPayment) {
            throw new RuntimeException("Invoice not found");
        }

        // Tickets
        Map<Event, List<TicketOrderDTO>> eventTickets = new LinkedHashMap<>();

        List<Event> events = orderService.getEventByOrderId(orderOfPayment.getId());

        for (Event event : events) {
            List<TicketOrderDTO> tickets =
                    orderService.getTicketsByOrderIdAndEventId(orderOfPayment.getId(), event.getId());

            eventTickets.put(event, tickets);
        }
        // ========= End Get Information =============
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

        byte[] pdf = pdfService.generateInvoicePdf(
                txnRef,
                customer,
                String.format("%s, %s, %s, %s",
                        orderOfPayment
                                .getStreetAddress(),
                        orderOfPayment.getWard(),
                        orderOfPayment.getCity(),
                        orderOfPayment.getCountry()),
                payment,
                orderOfPayment,
                eventTickets,
                subTotal,
                discountCode,
                discountTotal
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=invoice-" + txnRef + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    private String handleCannotCart(ValidationResult result,
                                    RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("hasMessage", true);
        redirectAttributes.addFlashAttribute("errorTitle", result.getErrorTitle());
        redirectAttributes.addFlashAttribute("message", result.getMessage());
        System.out.println("Error: " + result.getErrorTitle());
        return "redirect:/cart";
    }

    private String handleCannotCheckOut(ValidationResult result,
                                        RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("hasMessage", true);
        redirectAttributes.addFlashAttribute("errorTitle", result.getErrorTitle());
        redirectAttributes.addFlashAttribute("message", result.getMessage());
        System.out.println("Error: " + result.getErrorTitle());
        return "redirect:/checkout";
    }
}
