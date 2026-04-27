package com.cfd.order.api;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cfd.domain.model.OrderOpenRequest;
import com.cfd.domain.model.OrderResponse;
import com.cfd.order.service.OrderApplicationService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @PostMapping("/open")
    public OrderResponse open(@Validated @RequestBody OpenOrderHttpRequest request) {
        return orderApplicationService.submitOpenOrder(new OrderOpenRequest(
                request.orderId(),
                request.userId(),
                request.symbol(),
                request.openPrice(),
                request.quantity(),
                request.leverage()
        ));
    }

    @GetMapping("/{orderId}")
    public OrderResponse getById(@PathVariable String orderId) {
        return orderApplicationService.getOrder(orderId);
    }
}
