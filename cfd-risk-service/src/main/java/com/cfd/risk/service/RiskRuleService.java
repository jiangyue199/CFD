package com.cfd.risk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cfd.risk.engine.RiskRuleFact;
import com.cfd.risk.persistence.RiskRuleDbMapper;
import com.cfd.risk.persistence.RiskRuleEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 风控规则管理服务。
 *
 * <p>封装风控规则的数据库 CRUD 操作，以及从 DB 加载规则并转换为
 * Drools 事实对象的逻辑。支持按品种解析有效规则（品种特定规则优先于全局规则）。</p>
 *
 * @author CFD Platform Team
 */
@Service
public class RiskRuleService {

    private final RiskRuleDbMapper riskRuleDbMapper;

    public RiskRuleService(RiskRuleDbMapper riskRuleDbMapper) {
        this.riskRuleDbMapper = riskRuleDbMapper;
        ensureDefaults();
    }

    /**
     * 加载指定品种的有效规则事实列表。
     *
     * <p>对于每种 ruleCode，优先选择品种特定规则（symbol == 请求品种），
     * 若不存在则回退到全局规则（symbol == "*"）。</p>
     *
     * @param symbol 交易品种代码
     * @return 该品种适用的规则事实列表
     */
    public List<RiskRuleFact> loadEffectiveRules(String symbol) {
        List<RiskRuleEntity> allRules = riskRuleDbMapper.selectList(
                new LambdaQueryWrapper<RiskRuleEntity>()
                        .eq(RiskRuleEntity::getEnabled, true)
                        .eq(RiskRuleEntity::getSymbol, symbol)
                        .or()
                        .eq(RiskRuleEntity::getEnabled, true)
                        .eq(RiskRuleEntity::getSymbol, "*")
        );

        // 按 ruleCode 分组，品种特定规则优先
        Map<String, List<RiskRuleEntity>> grouped = allRules.stream()
                .collect(Collectors.groupingBy(RiskRuleEntity::getRuleCode));

        return grouped.values().stream()
                .map(rules -> {
                    Optional<RiskRuleEntity> specific = rules.stream()
                            .filter(r -> symbol.equals(r.getSymbol()))
                            .findFirst();
                    Optional<RiskRuleEntity> global = rules.stream()
                            .filter(r -> "*".equals(r.getSymbol()))
                            .findFirst();
                    return specific.or(() -> global).orElse(null);
                })
                .filter(r -> r != null)
                .map(this::toFact)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有风控规则。
     *
     * @return 所有规则实体列表
     */
    public List<RiskRuleEntity> getAllRules() {
        return riskRuleDbMapper.selectList(new LambdaQueryWrapper<RiskRuleEntity>());
    }

    /**
     * 根据ID获取规则。
     *
     * @param id 规则ID
     * @return 规则实体
     */
    public RiskRuleEntity getRule(Long id) {
        return riskRuleDbMapper.selectById(id);
    }

    /**
     * 新增风控规则。
     *
     * @param entity 规则实体
     */
    public void addRule(RiskRuleEntity entity) {
        riskRuleDbMapper.insert(entity);
    }

    /**
     * 更新风控规则。
     *
     * @param entity 规则实体
     */
    public void updateRule(RiskRuleEntity entity) {
        riskRuleDbMapper.updateById(entity);
    }

    /**
     * 删除风控规则。
     *
     * @param id 规则ID
     */
    public void deleteRule(Long id) {
        riskRuleDbMapper.deleteById(id);
    }

    /**
     * 将数据库实体转换为 Drools 规则事实。
     */
    private RiskRuleFact toFact(RiskRuleEntity entity) {
        return new RiskRuleFact(
                entity.getRuleCode(),
                entity.getRuleName(),
                entity.getSymbol(),
                entity.getRuleValue(),
                entity.getPriority() != null ? entity.getPriority() : 0
        );
    }

    /**
     * 确保数据库中存在默认风控规则。
     */
    private void ensureDefaults() {
        ensurePresent("MAX_LEVERAGE", "全局最大杠杆限制", "*", new BigDecimal("50"), 100, "全局最大杠杆倍数");
        ensurePresent("MAX_QUANTITY", "全局最大数量限制", "*", new BigDecimal("1000"), 90, "全局最大交易数量");
        ensurePresent("MAX_EXPOSURE", "全局最大风险敞口", "*", new BigDecimal("50000"), 80, "全局最大风险敞口(数量×杠杆)");
    }

    private void ensurePresent(String ruleCode, String ruleName, String symbol,
                               BigDecimal ruleValue, int priority, String description) {
        Long count = riskRuleDbMapper.selectCount(
                new LambdaQueryWrapper<RiskRuleEntity>()
                        .eq(RiskRuleEntity::getRuleCode, ruleCode)
                        .eq(RiskRuleEntity::getSymbol, symbol));
        if (count != null && count > 0) {
            return;
        }
        RiskRuleEntity entity = new RiskRuleEntity();
        entity.setRuleCode(ruleCode);
        entity.setRuleName(ruleName);
        entity.setSymbol(symbol);
        entity.setRuleValue(ruleValue);
        entity.setPriority(priority);
        entity.setEnabled(true);
        entity.setDescription(description);
        riskRuleDbMapper.insert(entity);
    }
}
