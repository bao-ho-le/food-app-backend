package com.example.foodie.services.impl;

import com.example.foodie.dtos.OrderDishResponseDTO;
import com.example.foodie.enums.Status;
import com.example.foodie.models.*;
import com.example.foodie.repos.*;
import com.example.foodie.services.interfaces.OrderService;
import jakarta.transaction.Transactional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl extends BaseServiceImpl<Order> implements OrderService {
    private final OrderRepository orderRepository;
    private final UserDishRepository userDishRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            UserDishRepository userDishRepository,
                            UserRepository userRepository,
                            AddressRepository addressRepository) {
        super(orderRepository, Order.class);
        this.orderRepository = orderRepository;
        this.userDishRepository = userDishRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public List<Order> getAllOrdersByUserId(Authentication authentication){
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        List<Order> allOrders = orderRepository.findAllByUser_Id(user.getId());
        return allOrders;
    }

    @Transactional
    @Override
    public Order createOrder(Authentication authentication, Integer addressId){
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        List<UserDish> allUserDishByUserId = userDishRepository.findAllByUser_Id(user.getId());

        if(allUserDishByUserId.isEmpty()){
            throw new RuntimeException("Không có user dish nào");
        }

        Optional<Address> address = addressRepository.findById(addressId);

        if (address.isEmpty()){
            throw new RuntimeException("Không tồn tại địa chỉ này");
        }

        Order newOrder = Order.builder()
                .user(user)
                .deliveryAddress(address.get().getAddress())
                .totalPrice(0f)
                .status(Status.DELIVERED)
                .build();

        List<OrderDish> orderDishes = new ArrayList<>();

        for (UserDish userDish: allUserDishByUserId){
            if (userDish.getQuantity() <= 0 || userDish.getDish().isAvailable() == false){
                continue; 
            }
            
            OrderDish newOrderDish = OrderDish.builder()
                    .dish(userDish.getDish())
                    .quantity(userDish.getQuantity())
                    .price(userDish.getDish().getPrice())
                    .order(newOrder)
                    .build();

            orderDishes.add(newOrderDish);
        }

        newOrder.setOrderDishes(orderDishes);
        newOrder.setTotalPrice(totalPrice(orderDishes));

        // Xoá các user dish đã đặt (userdish ở đây đóng vai trò như Cart)
        userDishRepository.deleteAll(allUserDishByUserId);

        return orderRepository.save(newOrder);
    }

    private Float totalPrice(List<OrderDish> orderDishes){
        float totalPrice = 0.0f;
        for (OrderDish orderDish: orderDishes){
            totalPrice += orderDish.getPrice() * orderDish.getQuantity();
        }

        return totalPrice;
    }

    @Override
    public List<OrderDishResponseDTO> getAllOrderItems(Integer orderId) {
        Optional<Order> order = orderRepository.findById(orderId);

        if (order.isEmpty()){
            throw new RuntimeException("Order không tồn tại");
        }

        List<OrderDishResponseDTO> orderDishResponseDTOS = new ArrayList<>();
        for (OrderDish orderDish: order.get().getOrderDishes()){
            Dish dish = orderDish.getDish();
            OrderDishResponseDTO dto = OrderDishResponseDTO.builder()
                    .id(orderDish.getId())
                    .dishName(dish.getName())
                    .quantity(orderDish.getQuantity())
                    .price(orderDish.getPrice())
                    .dishId(dish.getId())
                    .imageUrl(dish.getImages().isEmpty() ? "https://statics.vinpearl.com/com-tam-da-nang-4_1710137440.jpg" : dish.getImages().get(0).getUrl())
                    .restaurantName(dish.getRestaurant().getName())
                    .reviewed(orderDish.getReview() != null)
                    .build();
            orderDishResponseDTOS.add(dto);
        }

        return orderDishResponseDTOS;

    }
}
