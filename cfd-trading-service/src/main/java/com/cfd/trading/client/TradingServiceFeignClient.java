package com.cfd.trading.client;

import com.cfd.domain.model.OpenPositionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 交易服务Feign客户端接口。
 *
 * <p>供其他微服务通过Feign远程调用交易服务的持仓查询接口。
 * 默认连接地址为 http://localhost:8083。</p>
 *
 * @author CFD Platform Team
 */
@FeignClient(name = "cfd-trading-service",
        path = "/positions",
        url = "${services.trading.url:http://localhost:8083}")
public interface TradingServiceFeignClient {

    /**
     * 根据订单ID获取持仓信息。
     *
     * @param orderId 订单ID
     * @return 持仓响应对象
     */
    @GetMapping("/{orderId}")
    OpenPositionResponse getByOrderId(@PathVariable("orderId") String orderId);

    /**
     * 根据用户ID获取该用户所有持仓列表。
     *
     * @param userId 用户ID
     * @return 持仓响应列表
     */
    @GetMapping("/user/{userId}")
    List<OpenPositionResponse> listByUser(@PathVariable("userId") String userId);
}
