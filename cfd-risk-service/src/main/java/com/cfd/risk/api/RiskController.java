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

/**
 * 风控检查控制器。
 *
 * <p>提供开仓风控校验接口，根据数据库中配置的风控规则（最大杠杆、最大数量）
 * 对订单请求进行合规性验证。</p>
 *
 * @author CFD Platform Team
 */
@RestController
@RequestMapping("/risk")
public class RiskController {

    private final RiskRuleDbMapper riskRuleDbMapper;

    /**
     * 构造风控控制器并初始化默认规则。
     *
     * @param riskRuleDbMapper 风控规则数据库 Mapper
     */
    public RiskController(RiskRuleDbMapper riskRuleDbMapper) {
        this.riskRuleDbMapper = riskRuleDbMapper;
        ensureDefaults();
    }

    /**
     * 开仓风控校验接口。
     *
     * <p>校验请求中的杠杆倍数和数量是否超过风控阈值。</p>
     *
     * @param request 风控校验请求
     * @return 风控校验结果，包含是否通过及原因
     */
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

    /**
     * 从数据库获取指定规则的数值，若不存在则返回默认值。
     *
     * @param ruleCode     规则编码
     * @param defaultValue 默认值
     * @return 规则数值
     */
    private BigDecimal decimalRule(String ruleCode, String defaultValue) {
        RiskRuleEntity entity = riskRuleDbMapper.selectOne(new LambdaQueryWrapper<RiskRuleEntity>()
                .eq(RiskRuleEntity::getRuleCode, ruleCode)
                .last("limit 1"));
        if (entity == null) {
            return new BigDecimal(defaultValue);
        }
        return entity.getRuleValue();
    }

    /**
     * 确保数据库中存在默认风控规则。
     */
    private void ensureDefaults() {
        ensurePresent("MAX_LEVERAGE", "50");
        ensurePresent("MAX_QUANTITY", "1000");
    }

    /**
     * 若指定规则不存在，则插入默认值。
     *
     * @param ruleCode  规则编码
     * @param ruleValue 规则值
     */
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
