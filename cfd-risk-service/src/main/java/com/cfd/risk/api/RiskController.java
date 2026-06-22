package com.cfd.risk.api;

import com.cfd.domain.model.RiskCheckRequest;
import com.cfd.domain.model.RiskCheckResponse;
import com.cfd.risk.engine.RiskEngineService;
import com.cfd.risk.persistence.RiskRuleEntity;
import com.cfd.risk.service.RiskRuleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 风控检查控制器 - 服务端实现。
 *
 * <p>实现{@link RiskService}接口，提供开仓风控校验和规则管理功能。</p>
 *
 * @author CFD Platform Team
 */
@RestController
@RequestMapping("/risk")
public class RiskController implements RiskService {

    private final RiskEngineService riskEngineService;
    private final RiskRuleService riskRuleService;

    public RiskController(RiskEngineService riskEngineService, RiskRuleService riskRuleService) {
        this.riskEngineService = riskEngineService;
        this.riskRuleService = riskRuleService;
    }

    @Override
    @PostMapping("/open/check")
    public RiskCheckResponse checkOpenRisk(@RequestBody RiskCheckRequest request) {
        return riskEngineService.evaluate(request);
    }

    @Override
    @GetMapping("/rules")
    public List<RiskRuleEntity> listRules() {
        return riskRuleService.getAllRules();
    }

    @Override
    @GetMapping("/rules/{id}")
    public RiskRuleEntity getRule(@PathVariable Long id) {
        return riskRuleService.getRule(id);
    }

    @Override
    @PostMapping("/rules")
    public RiskRuleEntity addRule(@RequestBody RiskRuleEntity entity) {
        riskRuleService.addRule(entity);
        return entity;
    }

    @Override
    @PutMapping("/rules/{id}")
    public RiskRuleEntity updateRule(@PathVariable Long id, @RequestBody RiskRuleEntity entity) {
        entity.setId(id);
        riskRuleService.updateRule(entity);
        return entity;
    }

    @Override
    @DeleteMapping("/rules/{id}")
    public void deleteRule(@PathVariable Long id) {
        riskRuleService.deleteRule(id);
    }
}
