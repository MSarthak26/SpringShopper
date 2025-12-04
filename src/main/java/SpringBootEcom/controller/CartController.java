package SpringBootEcom.controller;

import SpringBootEcom.Util.AuthUtil;
import SpringBootEcom.model.Cart;
import SpringBootEcom.payload.CartDTO;
import SpringBootEcom.payload.ProductDTO;
import SpringBootEcom.repository.CartRepository;
import SpringBootEcom.service.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private ModelMapper modelMapper;


    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId,@PathVariable Integer quantity){
        CartDTO cartDTO = cartService.addProductToCart(productId,quantity);
        return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getAllCarts(){
        List<CartDTO> cartList = cartService.getAllCarts();
        return new ResponseEntity<>(cartList,HttpStatus.FOUND);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCart(){
        String emailId = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(emailId);
        Long cartId = cart.getCartId();

        cart.getCartItems().forEach(c-> c.getProduct().setQuantity(c.getQuantity()));
        CartDTO cartDTO = cartService.getCart(emailId,cartId);
        List<ProductDTO> productDTOS = cart.getCartItems().stream().map(p->modelMapper.map(p.getProduct(),ProductDTO.class)).toList();
        cartDTO.setProducts(productDTOS);


        return new ResponseEntity<>(cartDTO,HttpStatus.OK);
    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long productId,@PathVariable String operation){
        CartDTO cartDTO = cartService.updateProductQuantityInCart(productId,operation.equalsIgnoreCase("delete") ? -1 : 1);
        return new ResponseEntity<>(cartDTO,HttpStatus.OK);
    }

    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId , @PathVariable Long productId){
        String status = cartService.deleteProductFromCart(cartId,productId);
        return new ResponseEntity<>(status,HttpStatus.OK);
    }
}
