package com.cfd.risk.api;

import com.cfd.domain.model.RiskCheckRequest;
import com.cfd.domain.model.RiskCheckResponse;
import com.cfd.risk.engine.RiskEngineService;
import com.cfd.risk.persistence.RiskRuleEntity;
import com.cfd.risk.service.RiskRuleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 风控检查控制器。
 *
 * <p>提供开仓风控校验接口和风控规则管理接口。
 * 风控校验通过 Drools 规则引擎执行，规则可动态增删改查。</p>
 *
 * @author CFD Platform Team
 */
@RestController
@RequestMapping("/risk")
public class RiskController {

    private final RiskEngineService riskEngineService;
    private final RiskRuleService riskRuleService;

    public RiskController(RiskEngineService riskEngineService, RiskRuleService riskRuleService) {
        this.riskEngineService = riskEngineService;
        this.riskRuleService = riskRuleService;
    }

    /**
     * 开仓风控校验接口。
     *
     * <p>通过 Drools 规则引擎执行风控校验，支持杠杆、数量、风险敞口、黑名单等多维规则。</p>
     *
     * @param request 风控校验请求
     * @return 风控校验结果，包含是否通过及原因
     */
    @PostMapping("/open/check")
    public RiskCheckResponse checkOpen(@RequestBody RiskCheckRequest request) {
        return riskEngineService.evaluate(request);
    }

    /**
     * 获取所有风控规则。
     *
     * @return 规则列表
     */
    @GetMapping("/rules")
    public List<RiskRuleEntity> listRules() {
        return riskRuleService.getAllRules();
    }

    /**
     * 获取指定规则详情。
     *
     * @param id 规则ID
     * @return 规则实体
     */
    @GetMapping("/rules/{id}")
    public RiskRuleEntity getRule(@PathVariable Long id) {
        return riskRuleService.getRule(id);
    }

    /**
     * 新增风控规则。
     *
     * @param entity 规则实体
     * @return 新增后的规则实体
     */
    @PostMapping("/rules")
    public RiskRuleEntity addRule(@RequestBody RiskRuleEntity entity) {
        riskRuleService.addRule(entity);
        return entity;
    }

    /**
     * 更新风控规则。
     *
     * @param id     规则ID
     * @param entity 规则实体
     * @return 更新后的规则实体
     */
    @PutMapping("/rules/{id}")
    public RiskRuleEntity updateRule(@PathVariable Long id, @RequestBody RiskRuleEntity entity) {
        entity.setId(id);
        riskRuleService.updateRule(entity);
        return entity;
    }

    /**
     * 删除风控规则。
     *
     * @param id 规则ID
     */
    @DeleteMapping("/rules/{id}")
    public void deleteRule(@PathVariable Long id) {
        riskRuleService.deleteRule(id);
    }
}
