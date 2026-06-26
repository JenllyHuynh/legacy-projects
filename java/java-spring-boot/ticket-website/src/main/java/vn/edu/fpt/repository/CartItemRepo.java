package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.entity.CartItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem, Integer> {

    Optional<CartItem> findByCart_IdAndTicketType_Id(Integer cartId, Integer ticketTypeId);

    List<CartItem> findByCart_Id(Integer cartId);

    @Query("""
            select sum(cartItem.unitPrice * cartItem.quantity)
            from CartItem cartItem
                        where cartItem.cart.id = :cartId
            """)
    long getTotalCartItemByCartId(@Param("cartId") int cartId);

    void deleteCartItemByCart_Id(int cartId);

    @Query("""
        SELECT COALESCE(SUM(ci.quantity), 0)
        FROM CartItem ci
        WHERE ci.cart.id = :cartId
    """)
    Long countTotalItemsByCartId(@Param("cartId") Integer cartId);
}
