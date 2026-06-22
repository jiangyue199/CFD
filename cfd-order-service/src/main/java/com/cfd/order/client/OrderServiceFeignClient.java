package com.cfd.order.client;

import com.cfd.domain.model.OrderOpenRequest;
import com.cfd.domain.model.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 订单服务 Feign 客户端接口。
 *
 * <p>供其他微服务通过 Feign 远程调用订单服务的开仓和查询接口。</p>
 *
 * @author CFD Platform Team
 */
@FeignClient(name = "cfd-order-service", path = "/orders", url = "${services.order.url:http://localhost:8082}")
public interface OrderServiceFeignClient {

    /**
     * 远程调用：提交开仓订单。
     *
     * @param request 开仓订单请求
     * @return 订单响应
     */
    @PostMapping("/open")
    OrderResponse open(@RequestBody OrderOpenRequest request);

    /**
     * 远程调用：根据订单ID查询订单。
     *
     * @param orderId 订单唯一标识
     * @return 订单响应
     */
    @GetMapping("/{orderId}")
    OrderResponse getById(@PathVariable("orderId") String orderId);
}
