package vn.edu.fpt.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.model.dto.ValidationResult;
import vn.edu.fpt.model.entity.*;
import vn.edu.fpt.repository.CustomerRepo;
import vn.edu.fpt.service.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
@Transactional
public class CartController {

    private final CartService cartService;

    private final CartItemService cartItemService;

    private final CustomerRepo customerRepo;

    private final TicketService ticketService;
    private final OrderItemService orderItemService;
    private final TicketTypeService ticketTypeService;

    private ValidationResult result = new ValidationResult();

    public CartController(CartService cartService, CartItemService cartItemService, CustomerRepo customerRepo,
                          TicketService ticketService, OrderItemService orderItemService, TicketTypeService ticketTypeService) {
        this.cartService = cartService;
        this.cartItemService = cartItemService;
        this.customerRepo = customerRepo;
        this.ticketService = ticketService;
        this.orderItemService = orderItemService;
        this.ticketTypeService = ticketTypeService;
    }

    private String getEmailFromPrincipal(Object principal) {
        if (principal == null) return null;
        if (principal instanceof UserDetails) return ((UserDetails) principal).getUsername();
        if (principal instanceof OAuth2User) return ((OAuth2User) principal).getAttribute("email");
        return null;
    }

    @GetMapping
    public String viewCart(Model model,
                           @AuthenticationPrincipal Object principal) {
        String email = getEmailFromPrincipal(principal);
        if (email == null) return "redirect:/auth/login";
        Customer customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Cart cart = cartService.getAtiveCart(customer.getId());

        List<CartItem> items = cartItemService.getAllItem(cart.getId());
        boolean canBuy = true;
        for (CartItem ci : items) {
            ci.getTicketType().
                    setAvailableLeft(ticketService.
                            countNumberOfTicketAvailable(ci.getTicketType().getId()));

            int availableLeft = ci.getTicketType().getAvailableLeft();
            // Check = là chớt á, trường hợp còn 1 vé là cook
//            if (availableLeft <= ci.getQuantity()) {
            if (availableLeft < ci.getQuantity()) {
                canBuy = false;
                break;
            }

        }
        System.out.println(items.isEmpty());

        BigDecimal totalCart = cartService.calculateSubtotal(cart.getId());
        model.addAttribute("canBuy", canBuy);
        model.addAttribute("totalCart", totalCart.longValue());
        model.addAttribute("cart", cart);
        model.addAttribute("items", items);
        model.addAttribute("subtotal", cartService.calculateSubtotal(cart.getId()));
        return "cart/cart";
    }

    @PostMapping("/delete/{id}")
    public String deleteItem(@PathVariable("id") Integer cartItemId, Model model,
                             @AuthenticationPrincipal Object principal) {
        String email = getEmailFromPrincipal(principal);
        if (email == null) return "redirect:/auth/login";
        Customer customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        cartService.getAtiveCart(customer.getId());

        int rs = cartItemService.deleteItem(cartItemId);
        System.out.print(rs);
        return "redirect:/cart";

    }

//    @PutMapping("/{cartId}/items/{itemId}/increase")
//    @ResponseBody
//    public Map<String, Object> increase(
//            @PathVariable(name = "cartId") Integer cartId,
//            @PathVariable(name = "itemId") Integer itemId
//    ) {
//
//        int newQuantity = cartService.increaseQuantity(cartId, itemId);
//        return Map.of("quantity", newQuantity);
//    }
//
//    @PutMapping("/{cartId}/items/{itemId}/decrease")
//    @ResponseBody
//    public Map<String, Object> decrease(
//            @PathVariable Integer cartId,
//            @PathVariable Integer itemId) {
//
//        int newQuantity = cartService.decreaseItem(cartId, itemId);
//
//        return Map.of("quantity", newQuantity);
//    }

    @PostMapping("/increase/{cartId}/{itemId}")
    public String increaseItem(@PathVariable Integer cartId,
                               @PathVariable Integer itemId,
                               RedirectAttributes redirectAttributes) {

        try {
            cartService.increaseQuantity(cartId, itemId);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/decrease/{cartId}/{itemId}")
    public String decreaseItem(@PathVariable Integer cartId,
                               @PathVariable Integer itemId) {

        cartService.decreaseItem(cartId, itemId);

        return "redirect:/cart";
    }

//    @PostMapping("/add/{ticketTypeId}/{quantity}")
//    public String addToCart(@PathVariable Integer ticketTypeId, @PathVariable Integer quantity,
//                            HttpServletRequest request,
//                            RedirectAttributes redirectAttributes,
//                            @AuthenticationPrincipal UserDetails userDetails) {
//        String email = userDetails.getUsername();
//        Customer customer = customerRepo.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("Customer not found"));
//        try {
//            cartService.addToCart(customer.getId(), ticketTypeId, quantity);
//        } catch (IllegalArgumentException e) {
//            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
//        }
//
//        String referer = request.getHeader("Referer");
//        return "redirect:" + referer;
//    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Integer ticketTypeId,
                            @RequestParam Integer quantity,
                            HttpServletRequest request,
                            RedirectAttributes redirectAttributes,
                            @AuthenticationPrincipal Object principal) {

        String email = getEmailFromPrincipal(principal);
        if (email == null) return "redirect:/auth/login";

        Customer customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Cart cart = cartService.getAtiveCart(customer.getId());

        TicketType ticketType = new TicketType();
        Optional<TicketType> ticketTypeOptional = ticketTypeService.getTicketTypeOptById(ticketTypeId);
        if (ticketTypeOptional.isPresent()) {
            ticketType = ticketTypeOptional.get();
        }

        Integer maxPerUser = ticketType.getMaxTicketsPerUser();

        if (maxPerUser != null && maxPerUser > 0) {

            // ===== số lượng đã mua =====
            Integer purchasedQuantity =
                    orderItemService.countPurchasedTicket(
                            customer.getId(),
                            ticketType.getId()
                    );

            // ===== tổng hiện tại =====
            int totalQuantity =
                    purchasedQuantity + quantity;

            // ===== vượt giới hạn =====
            if (totalQuantity > maxPerUser) {

                result.setErrorTitle("Ticket quantity exceeded!");
                result.setMessage(
                        "You have already purchased "
                                + purchasedQuantity
                                + " ticket(s). Maximum allowed for \""
                                + ticketType.getName()
                                + "\" is "
                                + maxPerUser + "."
                );


                // rollback đúng item vừa thêm
//                cartService.decreaseItem(cart.getId(), addedItem.getId());
                Event event = ticketType.getEvent();

                return handleCannotCheckOut(result, redirectAttributes, event);
            }
        }

        try {
            // ===== ADD TO CART =====
            cartService.addToCart(customer.getId(), ticketTypeId, quantity);

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:" + request.getHeader("Referer");
        }

        // RELOAD cart items
        List<CartItem> cartItems = cartItemService.getAllItem(cart.getId());

        // tìm đúng item vừa add
        CartItem addedItem = cartItems.stream()
                .filter(i -> i.getTicketType().getId().equals(ticketTypeId))
                .findFirst()
                .orElse(null);

        redirectAttributes.addFlashAttribute("successMessage", "Add to cart successfully!");
        return "redirect:" + request.getHeader("Referer");
    }


//    @PostMapping("/api/cart/add")
//    @ResponseBody
//    public Map<String, Object> addToCart(
//            @RequestBody AddCartRequest request,
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        Map<String, Object> response = new HashMap<>();
//
//        String email = userDetails.getUsername();
//
//        Customer customer = customerRepo.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("Customer not found"));
//
//        try {
//
//            cartService.addToCart(customer.getId(),
//                    request.getTicketTypeId(),
//                    request.getQuantity());
//
//            response.put("success", true);
//
//        } catch (IllegalArgumentException e) {
//
//            response.put("success", false);
//            response.put("message", e.getMessage());
//
//        }
//
//        return response;
//    }


    private String handleCannotCheckOut(ValidationResult result,
                                        RedirectAttributes redirectAttributes,
                                        Event event) {
        redirectAttributes.addFlashAttribute("hasMessage", true);
        redirectAttributes.addFlashAttribute("errorTitle", result.getErrorTitle());
        redirectAttributes.addFlashAttribute("message", result.getMessage());
        System.out.println("Error: " + result.getErrorTitle());
        return "redirect:/event/" + event.getId();
    }


}
