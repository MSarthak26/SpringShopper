package SpringBootEcom.repository;

import SpringBootEcom.model.Cart;
import SpringBootEcom.model.CartItem;
import SpringBootEcom.payload.CartDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {


    @Query("Select ci from CartItem ci where ci.cart.id = ?1 and ci.product.id = ?2")
    CartItem findCartItemByCartIdAndProductId(Long cartId, Long productId);

    @Modifying
    @Query("Delete From CartItem ci Where ci.cart.id = ?1 AND ci.product.id = ?2")
    void deleteCartItemByCartIdAndProductId(Long cartId, Long productId);

    @Query("DELETE FROM CartItem ci  WHERE ci.cart.id = ?1")
    void deleteAllByCartId(Long cartId);
}
