package SpringBootEcom.service;

import SpringBootEcom.exceptions.APIException;
import SpringBootEcom.exceptions.ResourceNotFoundException;
import SpringBootEcom.model.*;
import SpringBootEcom.payload.OrderDTO;
import SpringBootEcom.payload.OrderItemDTO;
import SpringBootEcom.repository.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {

        Cart cart = cartRepository.findCartByEmail(emailId);
        if(cart == null){
            throw new ResourceNotFoundException("Cart","emailId",emailId);
        }

        Address address = addressRepository.findById(addressId).orElseThrow(()->new ResourceNotFoundException("Address","addressId",addressId));

        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setAddress(address);
        order.setEmail(emailId);
        order.setStatus("Order accepted!!.");
        order.setTotalAmount(cart.getTotalPrice());

        Payment payment = new Payment(paymentMethod,pgPaymentId,pgStatus,pgResponseMessage,pgName);
        payment.setOrder(order);

        payment = paymentRepository.save(payment);

        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);

        List<CartItem> cartItems = cart.getCartItems();
        if(cartItems.isEmpty()){
            throw new APIException("Cart is empty!!.");
        }

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems){
           OrderItem orderItem = new OrderItem();

           orderItem.setProduct(cartItem.getProduct());
           orderItem.setDiscount(cartItem.getDiscount());
           orderItem.setQuantity(cartItem.getQuantity());
           orderItem.setOrderedProductPrice(cartItem.getProductPrice());
           orderItem.setOrder(savedOrder);

           orderItems.add(orderItem);
        }

        orderItems = orderItemRepository.saveAll(orderItems);

//        savedOrder.setOrderItems(orderItems);
        orderRepository.save(savedOrder);

        cartItems.forEach(item->{
            int quantity = item.getQuantity();
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);

            cartService.deleteProductFromCart(cart.getCartId(), item.getProduct().getProductId());
        });

        OrderDTO orderDTO = modelMapper.map(order,OrderDTO.class);

        List<OrderItemDTO> orderItemDTOS =  orderItems.stream().map(item->modelMapper.map(item, OrderItemDTO.class)).toList();

        orderDTO.setOrderItems(orderItemDTOS);
        orderDTO.setAddressId(addressId);
        return orderDTO;
    }
}
