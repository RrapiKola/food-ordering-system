package com.food.ordering.system.order.service.domain.dto.create;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
public class OrderAddressDto {


    @NotNull
    private final String street;

    @NotNull
    private final String postalCode;

    @NotNull
    private final String city;

}
