package com.cfd.trading.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.cfd.domain.model.OpenPositionResponse;
import com.cfd.trading.service.OpenPositionQueryService;

@RestController
@RequestMapping("/positions")
public class TradingQueryController {

    private final OpenPositionQueryService openPositionQueryService;

    public TradingQueryController(OpenPositionQueryService openPositionQueryService) {
        this.openPositionQueryService = openPositionQueryService;
    }

    @GetMapping("/{orderId}")
    public OpenPositionResponse queryByOrderId(@PathVariable String orderId) {
        return openPositionQueryService.queryByOrderId(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "position not found"));
    }

    @GetMapping("/user/{userId}")
    public List<OpenPositionResponse> queryByUserId(@PathVariable String userId) {
        return openPositionQueryService.queryByUserId(userId);
    }
}
