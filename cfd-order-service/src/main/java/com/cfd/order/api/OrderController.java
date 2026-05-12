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

/**
 * 订单 REST 控制器。
 *
 * <p>提供订单提交（开仓）和订单查询接口。</p>
 *
 * @author CFD Platform Team
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    /**
     * 构造订单控制器。
     *
     * @param orderApplicationService 订单应用服务
     */
    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    /**
     * 提交开仓订单。
     *
     * <p>接收开仓请求，经风控校验后保存订单并发送至交易系统。</p>
     *
     * @param request 开仓订单请求
     * @return 订单响应
     */
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

    /**
     * 根据订单ID查询订单详情。
     *
     * @param orderId 订单唯一标识
     * @return 订单响应
     */
    @GetMapping("/{orderId}")
    public OrderResponse getById(@PathVariable String orderId) {
        return orderApplicationService.getOrder(orderId);
    }
}
