package vn.edu.fpt.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.entity.CartItem;
import vn.edu.fpt.repository.CartItemRepo;

import java.util.List;

@Service
public class CartItemService {
    @Autowired
    CartItemRepo cartItemRepo;

    public List<CartItem> getAllItem(Integer cartId) {
        return cartItemRepo.findByCart_Id(cartId);
    }

    public int deleteItem(Integer itemId) {
        CartItem cartItem = cartItemRepo.getReferenceById(itemId);
        cartItemRepo.delete(cartItem);
        return itemId;
    }

    public long getTotalCartItemByCartId(int cartId) {
        return cartItemRepo.getTotalCartItemByCartId(cartId);
    }

    @Transactional
    public void deleteCartItemByCartId(int cartId) {
        cartItemRepo.deleteCartItemByCart_Id(cartId);
    }

    @Transactional
    public void save(CartItem cartItem) {
        cartItemRepo.save(cartItem);
    }

}
