package vn.edu.fpt.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.entity.Cart;
import vn.edu.fpt.model.entity.CartItem;
import vn.edu.fpt.model.entity.TicketType;
import vn.edu.fpt.repository.CartItemRepo;
import vn.edu.fpt.repository.CartRepo;
import vn.edu.fpt.repository.TicketRepo;
import vn.edu.fpt.repository.TicketTypeRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepo cartRepo;
    private final CartItemRepo cartItemRepo;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketService ticketService;

    public CartService(CartRepo cartRepo, CartItemRepo cartItemRepo, TicketTypeRepository ticketTypeRepository, TicketRepo ticketRepo,
                       TicketService ticketService) {
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
        this.ticketTypeRepository = ticketTypeRepository;
        this.ticketService = ticketService;
    }

    /**
     * Lấy cart Active; nếu chưa có thì tự tạo.
     * Case thường gặp: user mới (Google login / đăng ký mới) chưa có row trong bảng Carts.
     */
    @Transactional
    public Cart getAtiveCart(Integer customerId) {
        Optional<Cart> existing = cartRepo.findByCustomer_IdAndStatus(customerId, "Active");
        if (existing.isPresent()) return existing.get();

        Cart cart = new Cart();
        cart.setStatus("Active");
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());

        // set Customer bằng reference để khỏi cần query thêm
        vn.edu.fpt.model.entity.Customer cRef = new vn.edu.fpt.model.entity.Customer();
        cRef.setId(customerId);
        cart.setCustomer(cRef);

        return cartRepo.save(cart);
    }

    public int addToCart(Integer customerId, Integer ticketTypeId, int quantity) {
        Cart cart = getAtiveCart(customerId);
        TicketType ticketType = ticketTypeRepository.getReferenceById(ticketTypeId);

        Integer maxPerUser = ticketType.getMaxTicketsPerUser();
        Optional<CartItem> existingItem =cartItemRepo.findByCart_IdAndTicketType_Id(
                cart.getId(),
                ticketTypeId
        );

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            if (cartItem.getQuantity() + quantity > maxPerUser) {
                throw new IllegalArgumentException("Exceed Maximum Allowed!. You can only get " + (maxPerUser - cartItem.getQuantity()) + " more tickets. ");
            }
            if (ticketService.countNumberOfTicketAvailable(ticketTypeId) >= quantity && cartItem.getQuantity() + quantity <= ticketService.countNumberOfTicketAvailable(ticketTypeId)) {
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
            } else {
                throw new IllegalArgumentException("There aren't enough tickets left.");
            }

        } else {
            if (quantity > maxPerUser) {
                throw new IllegalArgumentException("Exceed Maximum Allowed!. You can only get " + (maxPerUser) + " more tickets. ");
            } else {
                if (ticketService.countNumberOfTicketAvailable(ticketTypeId) >= quantity) {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setTicketType(ticketType);
                    newItem.setQuantity(quantity);
                    newItem.setUnitPrice(ticketType.getPrice());
                    cartItemRepo.save(newItem);
                } else {
                    throw new IllegalArgumentException("There aren't enough tickets left.");
                }

            }

        }
        return ticketTypeId;

    }

    public int updateItem(Integer cartItemId, int quantity) {
        CartItem cartItem = cartItemRepo.getReferenceById(cartItemId);

        if (quantity <= 0) {
            cartItemRepo.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
        }

        return cartItemId;
    }

    public void removeItem(Integer cartItemId) {
        cartItemRepo.deleteById(cartItemId);

    }

    public BigDecimal calculateSubtotal(Integer cartId) {
        return cartItemRepo.findByCart_Id(cartId)
                .stream()
                .map(CartItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int increaseQuantity(Integer cartId, Integer itemId) {
        Cart cart = cartRepo.findById(cartId).
                orElseThrow(() -> new RuntimeException("Cart Not Found"));

        CartItem item = cart.getItems()
                .stream().filter(i ->  i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item Not Found"));

        int maxAllowed = item.getTicketType().getMaxTicketsPerUser();
        int conlai = ticketService.countNumberOfTicketAvailable(item.getTicketType().getId());
        if (item.getQuantity() >= conlai) {
            throw new IllegalArgumentException("There aren't enough tickets left.");
        }
        if (item.getQuantity() > maxAllowed) {
            throw new IllegalArgumentException("You have reached maximum quantity allowed!");
        }

        item.setQuantity(item.getQuantity() + 1);

        cartRepo.save(cart);
        return item.getQuantity();
    }

    public int decreaseItem(Integer cartId, Integer itemId) {

        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem item = cart.getItems()
                .stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
        } else {
            cart.getItems().remove(item);
        }

        cartRepo.save(cart);

        return item.getQuantity();
    }


    /**
     * Lưu cart mới — tạo cart cho user vừa đăng ký
     */
    @Transactional
    public Cart save(Cart cart) {
        return cartRepo.save(cart);
    }

    public Long getCartItemCount(Integer cartId) {
        return cartItemRepo.countTotalItemsByCartId(cartId);
    }








}
