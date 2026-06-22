package com.cfd.trading.api;

import com.cfd.domain.model.OpenPositionResponse;
import com.cfd.trading.service.OpenPositionQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * 交易持仓查询控制器。
 *
 * <p>提供REST接口用于查询持仓信息，支持按订单ID和用户ID进行查询。</p>
 *
 * @author CFD Platform Team
 */
@RestController
@RequestMapping("/positions")
public class TradingQueryController {

    private final OpenPositionQueryService openPositionQueryService;

    public TradingQueryController(OpenPositionQueryService openPositionQueryService) {
        this.openPositionQueryService = openPositionQueryService;
    }

    /**
     * 根据订单ID查询持仓信息。
     *
     * @param orderId 订单ID
     * @return 持仓响应对象
     * @throws ResponseStatusException 当持仓不存在时抛出404异常
     */
    @GetMapping("/{orderId}")
    public OpenPositionResponse queryByOrderId(@PathVariable String orderId) {
        return openPositionQueryService.queryByOrderId(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "position not found"));
    }

    /**
     * 根据用户ID查询该用户所有持仓列表。
     *
     * @param userId 用户ID
     * @return 持仓响应列表
     */
    @GetMapping("/user/{userId}")
    public List<OpenPositionResponse> queryByUserId(@PathVariable String userId) {
        return openPositionQueryService.queryByUserId(userId);
    }
}
