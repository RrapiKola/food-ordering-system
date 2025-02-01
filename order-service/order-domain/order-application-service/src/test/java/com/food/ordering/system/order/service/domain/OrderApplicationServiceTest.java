package com.food.ordering.system.order.service.domain;


import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderApplicationService.class)
public class OrderApplicationServiceTest {


    @Autowired
    private OrderApplicationService orderApplicationService;

    @Autowired
    private OrderDataMapper orderDataMapper;

}
