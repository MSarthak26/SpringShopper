package SpringBootEcom.service;

import SpringBootEcom.Util.AuthUtil;
import SpringBootEcom.exceptions.APIException;
import SpringBootEcom.exceptions.ResourceNotFoundException;
import SpringBootEcom.model.Cart;
import SpringBootEcom.model.CartItem;
import SpringBootEcom.model.Product;
import SpringBootEcom.payload.CartDTO;
import SpringBootEcom.payload.ProductDTO;
import SpringBootEcom.repository.CartItemRepository;
import SpringBootEcom.repository.CartRepository;
import SpringBootEcom.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.ToString;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService{

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;


    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart userCart = createCart();

        Product product = productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));

        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(userCart.getCartId(),productId);

        if(cartItem != null){
            throw new APIException("Product " + product.getProductName() +" already exists in the cart.");
        }

        if(product.getQuantity() == 0){
            throw new APIException(product.getProductName() +" is not available currently.");
        }

        if(product.getQuantity() < quantity){
            throw new APIException("Please make an order of " + product.getProductName() + " of less than or equal to " + product.getQuantity() + ".");
        }

        CartItem newCartItem = new CartItem();
        newCartItem.setCart(userCart);
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        cartItemRepository.save(newCartItem);

        userCart.getCartItems().add(newCartItem);

        product.setQuantity(product.getQuantity());

        userCart.setTotalPrice(userCart.getTotalPrice() + (product.getSpecialPrice() * quantity));

        cartRepository.save(userCart);

        CartDTO cartDTO = modelMapper.map(userCart,CartDTO.class);

        List<CartItem> cartItems = userCart.getCartItems();

        Stream<ProductDTO> productDTOStream = cartItems.stream().map(item->{
            ProductDTO map = modelMapper.map(item.getProduct(),ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        });


        cartDTO.setProducts(productDTOStream.toList());

        return cartDTO;

    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();

        if(carts.isEmpty()){
            throw new APIException("No carts exist.");
        }
        List<CartDTO> cartDTOList = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
            List<ProductDTO>productDTOS = cart.getCartItems().stream().map(cartItem -> modelMapper.map(cartItem.getProduct(),ProductDTO.class)).toList();
            cartDTO.setProducts(productDTOS);
            return cartDTO;
        }).toList();
        return cartDTOList;
    }


    public Cart createCart(){
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart != null){
            return userCart;
        }
        Cart cart = new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser(authUtil.loggedInUser());
        Cart newCart = cartRepository.save(cart);
        return newCart;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId,cartId);
        if(cart == null){
            throw new ResourceNotFoundException("Cart","cartId",cartId);
        }

        return modelMapper.map(cart,CartDTO.class);
    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        String email = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(email);

        Long cartId = userCart.getCartId();
        Cart cart = cartRepository.findById(cartId).orElseThrow(()->new ResourceNotFoundException("Cart","cartId",cartId));

        Product product = productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));

        if(product.getQuantity() == 0){
            throw new APIException(product.getProductName() +" is not available currently.");
        }

        if(product.getQuantity() < quantity){
            throw new APIException("Please make an order of " + product.getProductName() + " of less than or equal to " + product.getQuantity() + ".");
        }

        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(cartId,productId);

        if(cartItem == null){
            throw new APIException("Product " + product.getProductName() + " not available in the cart!!!");
        }

        int newQuantity = cartItem.getQuantity() + quantity;

        if(newQuantity < 0){
            throw new APIException("Product not available in the cart.");
        }

        if(newQuantity == 0){
            deleteProductFromCart(cartId,productId);
        }else {
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));

            cartRepository.save(cart);
        }
        CartItem updatedCartItem = cartItemRepository.save(cartItem);

        if(updatedCartItem.getQuantity() == 0){
            cartItemRepository.deleteById(updatedCartItem.getCartItemId());
        }

        CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();
        Stream<ProductDTO> productDTOStream = cartItems.stream().map(item->{
            ProductDTO prd = modelMapper.map(item.getProduct(),ProductDTO.class);
            prd.setQuantity(item.getQuantity());
            return prd;
        });

        cartDTO.setProducts(productDTOStream.toList());
        return cartDTO;
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(()-> new ResourceNotFoundException("Cart","cartId",cartId));
        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(cartId,productId);

        if(cartItem == null){
            throw new ResourceNotFoundException("Product","productId",productId);
        }

        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByCartIdAndProductId(cartId,productId);

        return "Product " + cartItem.getProduct().getProductName() + " removed from the cart!!!.";
    }
}
