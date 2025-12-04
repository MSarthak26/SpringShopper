package SpringBootEcom.repository;

import SpringBootEcom.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {
    @Query("SELECT c from Cart c where c.user.email = ?1")
    Cart findCartByEmail(String email);


    @Query("SELECT c from Cart c where c.user.email = ?1 AND c.id = ?2")
    Cart findCartByEmailAndCartId(String emailId, Long cartId);


}
