package com.food.ordering.system.order.service.domain;


import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddressDto;
import com.food.ordering.system.order.service.domain.dto.create.OrderItemDto;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import com.food.ordering.system.service.domain.entity.*;
import com.food.ordering.system.service.domain.exception.OrderDomainException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderApplicationServiceTest {


    @Autowired
    private OrderApplicationService orderApplicationService;

    @Autowired
    private OrderDataMapper orderDataMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;


    private CreateOrderCommand createOrderCommand;
    private CreateOrderCommand createOrderCommandWrongPrice;
    private CreateOrderCommand createOrderCommandWrongProductPrice;
    private final UUID CUSTOMER_ID = UUID.fromString("d8f9b2a3-6f9d-4b12-a8ec-cfdf1b9c1234");
    private final UUID ORDER_ID = UUID.fromString("3c9b6f2e-1f34-47a9-bc1e-6ac6b2f91234");
    private final UUID RESTAURANT_ID = UUID.fromString("9d1e3ab7-4a5b-463f-8c3a-7b4dfcc61234");
    private final UUID PRODUCT_ID_1 = UUID.fromString("fd4a3b9e-5a6b-4e67-a0b1-812c3d4e1234");
    private final UUID PRODUCT_ID_2 = UUID.fromString("e1f3a0b2-6b5c-4d7a-9b8c-23d45678abcd");
    private final BigDecimal PRICE = new BigDecimal("200.00");


    @BeforeAll
    public  void init(){
        createOrderCommand = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddressDto.builder()
                        .street("street_1")
                        .postalCode("1000AB")
                        .city("Paris")
                        .build())
                .price(PRICE)
                .items(List.of(OrderItemDto.builder()
                        .productId(PRODUCT_ID_1)
                        .quantity(1)
                        .price(new BigDecimal("50.00"))
                        .subtotal(new BigDecimal("50.00"))
                        .build(),
                        OrderItemDto.builder()
                                .productId(PRODUCT_ID_1)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subtotal(new BigDecimal("150.00"))
                                .build()
                )).build();

        createOrderCommandWrongPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddressDto.builder()
                        .street("street_1")
                        .postalCode("1000AB")
                        .city("Paris")
                        .build())
                .price(new BigDecimal("250.00"))
                .items(List.of(OrderItemDto.builder()
                                .productId(PRODUCT_ID_1)
                                .quantity(1)
                                .price(new BigDecimal("50.00"))
                                .subtotal(new BigDecimal("50.00"))
                                .build(),
                        OrderItemDto.builder()
                                .productId(PRODUCT_ID_1)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subtotal(new BigDecimal("150.00"))
                                .build()
                )).build();



        createOrderCommandWrongProductPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddressDto.builder()
                        .street("street_1")
                        .postalCode("1000AB")
                        .city("Paris")
                        .build())
                .price(new BigDecimal("210"))
                .items(List.of(OrderItemDto.builder()
                                .productId(PRODUCT_ID_1)
                                .quantity(1)
                                .price(new BigDecimal("60.00"))
                                .subtotal(new BigDecimal("60.00"))
                                .build(),
                        OrderItemDto.builder()
                                .productId(PRODUCT_ID_1)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subtotal(new BigDecimal("150.00"))
                                .build()
                )).build();


        Customer customer = new Customer();
        customer.setId(new CustomerId(CUSTOMER_ID));

        Restaurant restaurant = Restaurant.builder()
                .restaurantId(new RestaurantId(RESTAURANT_ID))
                .products(List.of(new Product(new ProductId(PRODUCT_ID_1),"product-1",
                        new Money(new BigDecimal("50.00"))),
                        new Product(new ProductId(PRODUCT_ID_2),"product-2",
                              new Money(new BigDecimal("50.00")))))
                .active(true)
                .build();


        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        order.setId(new OrderId(ORDER_ID));

        when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findRestaurantInformation(restaurant)).thenReturn(Optional.of(restaurant));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

    }


    @Test
    public void testCreateOrder() {
     CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
     assertEquals(OrderStatus.PENDING, createOrderResponse.getOrderStatus());
     assertEquals("Order created successfully", createOrderResponse.getMessage());
     assertNotNull(createOrderResponse.getOrderTrackingId());

    }


    @Test
    public void testCreateOrderWrongPrice() {
      OrderDomainException orderDomainException =  assertThrows(OrderDomainException.class, ()-> orderApplicationService.createOrder(createOrderCommandWrongPrice));
        assertEquals("Total price: 250.00 is not equal to Order items total: 200.00!", orderDomainException.getMessage());
    }


    @Test
    public void testCreateOrderWrongProductPrice() {
      OrderDomainException orderDomainException = assertThrows(OrderDomainException.class, ()-> orderApplicationService.createOrder(createOrderCommandWrongProductPrice));
      assertEquals("Order item price : 60.00 is not valid for product: " + PRODUCT_ID_1, orderDomainException.getMessage() );

    }


    @Test
    public void testCreateOrderWithPassiveRestaurant() {
        Restaurant restaurantNotActive = Restaurant.builder()
                .restaurantId(new RestaurantId(RESTAURANT_ID))
                .products(List.of(new Product(new ProductId(PRODUCT_ID_1),"product-1",
                                new Money(new BigDecimal("50.00"))),
                        new Product(new ProductId(PRODUCT_ID_2),"product-2",
                                new Money(new BigDecimal("50.00")))))
                .active(false)
                .build();

        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand))).thenReturn(Optional.of(restaurantNotActive));
        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class, ()-> orderApplicationService.createOrder(createOrderCommand));
        assertEquals("Restaurant with id " + RESTAURANT_ID + " is not active", orderDomainException.getMessage());



    }

}
