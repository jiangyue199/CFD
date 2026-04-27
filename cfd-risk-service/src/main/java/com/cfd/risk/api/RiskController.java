package com.cfd.risk.api;

import java.math.BigDecimal;
import java.util.Optional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cfd.domain.model.RiskCheckRequest;
import com.cfd.domain.model.RiskCheckResponse;
import com.cfd.risk.persistence.RiskRuleDbMapper;
import com.cfd.risk.persistence.RiskRuleEntity;

@RestController
@RequestMapping("/risk")
public class RiskController {

    private final RiskRuleDbMapper riskRuleDbMapper;

    public RiskController(RiskRuleDbMapper riskRuleDbMapper) {
        this.riskRuleDbMapper = riskRuleDbMapper;
        ensureDefaults();
    }

    @PostMapping("/open/check")
    public RiskCheckResponse checkOpen(@RequestBody RiskCheckRequest request) {
        BigDecimal maxLeverage = decimalRule("MAX_LEVERAGE", "50");
        BigDecimal maxQuantity = decimalRule("MAX_QUANTITY", "1000");
        if (request.leverage().compareTo(maxLeverage) > 0) {
            return new RiskCheckResponse(false, "Leverage exceeds threshold");
        }
        if (request.quantity().compareTo(maxQuantity) > 0) {
            return new RiskCheckResponse(false, "Quantity exceeds threshold");
        }
        return new RiskCheckResponse(true, "PASS");
    }

    private BigDecimal decimalRule(String ruleCode, String defaultValue) {
        RiskRuleEntity entity = riskRuleDbMapper.selectOne(new LambdaQueryWrapper<RiskRuleEntity>()
                .eq(RiskRuleEntity::getRuleCode, ruleCode)
                .last("limit 1"));
        if (entity == null) {
            return new BigDecimal(defaultValue);
        }
        return entity.getRuleValue();
    }

    private void ensureDefaults() {
        ensurePresent("MAX_LEVERAGE", "50");
        ensurePresent("MAX_QUANTITY", "1000");
    }

    private void ensurePresent(String ruleCode, String ruleValue) {
        Optional<RiskRuleEntity> existing = Optional.ofNullable(riskRuleDbMapper.selectOne(
                new LambdaQueryWrapper<RiskRuleEntity>()
                        .eq(RiskRuleEntity::getRuleCode, ruleCode)
                        .last("limit 1")));
        if (existing.isPresent()) {
            return;
        }
        RiskRuleEntity entity = new RiskRuleEntity();
        entity.setRuleCode(ruleCode);
        entity.setRuleValue(new BigDecimal(ruleValue));
        riskRuleDbMapper.insert(entity);
    }
}
