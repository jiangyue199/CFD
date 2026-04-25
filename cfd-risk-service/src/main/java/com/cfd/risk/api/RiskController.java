package com.cfd.risk.api;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cfd.domain.model.RiskCheckRequest;
import com.cfd.domain.model.RiskCheckResponse;

@RestController
@RequestMapping("/risk")
public class RiskController {

    private static final BigDecimal MAX_LEVERAGE = new BigDecimal("50");
    private static final BigDecimal MAX_QUANTITY = new BigDecimal("1000");

    @PostMapping("/open/check")
    public RiskCheckResponse checkOpen(@RequestBody RiskCheckRequest request) {
        if (request.leverage().compareTo(MAX_LEVERAGE) > 0) {
            return new RiskCheckResponse(false, "Leverage exceeds threshold");
        }
        if (request.quantity().compareTo(MAX_QUANTITY) > 0) {
            return new RiskCheckResponse(false, "Quantity exceeds threshold");
        }
        return new RiskCheckResponse(true, "PASS");
    }
}
