package vn.edu.fpt.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.model.entity.Cart;
import vn.edu.fpt.model.entity.Customer;
import vn.edu.fpt.repository.CustomerRepo;
import vn.edu.fpt.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    private CustomerRepo customerRepo;
    private CartService cartService;

    public CartApiController(CustomerRepo customerRepo, CartService cartService) {
        this.customerRepo = customerRepo;
        this.cartService = cartService;
    }

    @GetMapping("/count")
    public Long getCartCount(Authentication authentication) {
        String email = authentication.getName();
        Customer customer = customerRepo.findByEmail(email).get();

        Cart cart = cartService.getAtiveCart(customer.getId());
        return cartService.getCartItemCount(cart.getId());
    }
}
