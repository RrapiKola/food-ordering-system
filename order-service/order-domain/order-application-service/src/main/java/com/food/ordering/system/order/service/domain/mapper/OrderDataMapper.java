package com.food.ordering.system.order.service.domain.mapper;


import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.ProductId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddressDto;
import com.food.ordering.system.order.service.domain.dto.create.OrderItemDto;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.service.domain.entity.Order;
import com.food.ordering.system.service.domain.entity.OrderItem;
import com.food.ordering.system.service.domain.entity.Product;
import com.food.ordering.system.service.domain.entity.Restaurant;

import com.food.ordering.system.service.domain.valueobject.StreetAddress;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Component
public class OrderDataMapper {

   public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {

        return Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(createOrderCommand.getItems().stream().map(orderItemDto -> new Product(new ProductId(orderItemDto.getProductId()))).toList())
                .build();
    }


    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {

       return Order.builder()
               .customerId(new CustomerId(createOrderCommand.getCustomerId()))
               .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
               .deliveryAddress(orderAddressToStreetAddress(createOrderCommand.getAddress()))
               .price(new Money(createOrderCommand.getPrice()))
               .items(orderItemsToOrderItemsEntity(createOrderCommand.getItems()))
               .build();
    }

    public CreateOrderResponse orderToCreateOrderResponse(Order order,String message) {
       return CreateOrderResponse.builder()
               .orderTrackingId(order.getTrackingId().getValue())
               .orderStatus(order.getOrderStatus())
               .message(message)
               .build();

    }

    public TrackOrderResponse orderToTrackOrderResponse(Order order) {
       return TrackOrderResponse.builder()
               .orderTrackingId(order.getTrackingId().getValue())
               .orderStatus(order.getOrderStatus())
               .failureMessages(order.getFailureMessages())
               .build();
    }



    private List<OrderItem> orderItemsToOrderItemsEntity(@NotNull List<OrderItemDto> orderItemDtos) {
       return orderItemDtos.stream().map(orderItemDto -> OrderItem.builder()
               .product(new Product(new ProductId(orderItemDto.getProductId())))
               .price(new Money(orderItemDto.getPrice()))
               .quantity(orderItemDto.getQuantity())
               .subTotal(new Money(orderItemDto.getSubtotal()))
               .build()).toList();
    }

    private StreetAddress orderAddressToStreetAddress(@NotNull OrderAddressDto orderAddressDto) {

       return new StreetAddress(
               UUID.randomUUID(),
               orderAddressDto.getStreet(),
               orderAddressDto.getPostalCode(),
               orderAddressDto.getCity()
       );
    }


}
