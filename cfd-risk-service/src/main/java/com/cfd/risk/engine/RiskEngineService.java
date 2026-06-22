package com.cfd.risk.engine;

import com.cfd.domain.model.RiskCheckRequest;
import com.cfd.domain.model.RiskCheckResponse;
import com.cfd.risk.persistence.DailyTradeLimitEntity;
import com.cfd.risk.persistence.PositionLimitEntity;
import com.cfd.risk.persistence.SymbolPermissionEntity;
import com.cfd.risk.persistence.UserDailyTradeEntity;
import com.cfd.risk.service.RiskRuleService;
import com.cfd.risk.service.RuleDataService;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 风控规则引擎服务。
 *
 * <p>编排 Drools 规则执行流程：加载有效规则 → 插入事实 → 触发规则 → 汇总结果。
 * 所有风控校验逻辑由 DRL 规则文件定义，本服务仅负责引擎调度。</p>
 *
 * @author CFD Platform Team
 */
@Service
public class RiskEngineService {

    private final KieContainer kieContainer;
    private final RiskRuleService riskRuleService;
    private final RuleDataService ruleDataService;

    public RiskEngineService(KieContainer kieContainer, RiskRuleService riskRuleService, RuleDataService ruleDataService) {
        this.kieContainer = kieContainer;
        this.riskRuleService = riskRuleService;
        this.ruleDataService = ruleDataService;
    }

    /**
     * 执行风控校验。
     *
     * <p>将请求转换为事实对象，加载品种适用的规则，创建 Drools 会话并触发所有规则，
     * 最终根据结果中的拒绝原因列表决定是否通过。</p>
     *
     * @param request 风控校验请求
     * @return 风控校验响应
     */
    public RiskCheckResponse evaluate(RiskCheckRequest request) {
        KieSession kieSession = kieContainer.newKieSession();
        try {
            RiskCheckFact checkFact = buildCheckFact(request);
            RiskCheckResult checkResult = new RiskCheckResult();

            kieSession.insert(checkFact);
            kieSession.insert(checkResult);

            insertUserStatus(kieSession, request.userId());
            insertSymbolPermission(kieSession, request);
            insertDailyTradeStats(kieSession, request);
            insertPositionLimit(kieSession, request.symbol());
            insertNewsEvent(kieSession, request.symbol());
            insertRiskRules(kieSession, request.symbol());

            kieSession.fireAllRules();

            if (checkResult.getReasons().isEmpty()) {
                return new RiskCheckResponse(true, "PASS");
            }
            checkResult.setAllowed(false);
            String reason = String.join("; ", checkResult.getReasons());
            return new RiskCheckResponse(false, reason);
        } finally {
            kieSession.dispose();
        }
    }

    private RiskCheckFact buildCheckFact(RiskCheckRequest request) {
        RiskCheckFact fact = new RiskCheckFact();
        fact.setUserId(request.userId());
        fact.setSymbol(request.symbol());
        fact.setQuantity(request.quantity());
        fact.setLeverage(request.leverage());
        fact.setIp(request.ip());
        fact.setDeviceId(request.deviceId());
        fact.setCardNo(request.cardNo());
        fact.setUserLevel(request.userLevel());
        fact.setRegion(request.region());
        fact.setDirection(request.direction());
        fact.setHedgeAllowed(request.hedgeAllowed());
        fact.setEquity(request.equity());
        fact.setInitMargin(request.initMargin());
        fact.setCurrentPrice(request.currentPrice());
        fact.setOrderPrice(request.orderPrice());
        fact.setStopLevel(request.stopLevel());
        fact.setOrderType(request.orderType());
        return fact;
    }

    private void insertUserStatus(KieSession kieSession, String userId) {
        String status = ruleDataService.getUserStatus(userId);
        boolean isWhitelist = ruleDataService.isInWhitelist(userId);
        
        UserStatusFact fact = new UserStatusFact();
        fact.setUserId(userId);
        if (isWhitelist) {
            fact.setStatus("WHITELIST");
            fact.setFrozen(false);
        } else if ("FROZEN".equals(status)) {
            fact.setStatus("FROZEN");
            fact.setFrozen(true);
        } else {
            fact.setStatus(status);
            fact.setFrozen(false);
        }
        kieSession.insert(fact);
    }

    private void insertSymbolPermission(KieSession kieSession, RiskCheckRequest request) {
        Optional<SymbolPermissionEntity> permOpt = ruleDataService.getSymbolPermission(
                request.symbol(), 
                request.userLevel() != null ? request.userLevel() : "RETAIL",
                request.region() != null ? request.region() : "CN"
        );
        
        if (permOpt.isPresent()) {
            SymbolPermissionEntity perm = permOpt.get();
            SymbolPermissionFact fact = new SymbolPermissionFact();
            fact.setSymbol(perm.getSymbol());
            fact.setUserLevel(perm.getUserLevel());
            fact.setRegion(perm.getRegion());
            fact.setAllowed(perm.getAllowed());
            fact.setDirection(perm.getDirection());
            fact.setHedgeAllowed(perm.getHedgeAllowed());
            fact.setMinLot(perm.getMinLot() != null ? perm.getMinLot() : new BigDecimal("0.001"));
            fact.setMaxLot(perm.getMaxLot() != null ? perm.getMaxLot() : new BigDecimal("1000"));
            fact.setStepLot(perm.getStepLot() != null ? perm.getStepLot() : new BigDecimal("0.001"));
            fact.setMaxLeverage(perm.getMaxLeverage() != null ? perm.getMaxLeverage() : new BigDecimal("50"));
            fact.setMinStopLevel(perm.getMinStopLevel() != null ? perm.getMinStopLevel() : BigDecimal.ZERO);
            kieSession.insert(fact);
        }
    }

    private void insertDailyTradeStats(KieSession kieSession, RiskCheckRequest request) {
        UserDailyTradeEntity todayTrade = ruleDataService.getTodayTrade(request.userId(), request.symbol());
        Optional<DailyTradeLimitEntity> limitOpt = ruleDataService.getDailyTradeLimit(request.userId(), request.symbol());

        DailyTradeStatsFact fact = new DailyTradeStatsFact();
        fact.setUserId(request.userId());
        fact.setSymbol(request.symbol());
        fact.setTodayTotalLots(todayTrade.getTotalLots() != null ? todayTrade.getTotalLots() : BigDecimal.ZERO);
        fact.setTodayOrderCount(todayTrade.getOrderCount() != null ? todayTrade.getOrderCount() : 0);
        
        if (limitOpt.isPresent()) {
            DailyTradeLimitEntity limit = limitOpt.get();
            fact.setDailyMaxLots(limit.getDailyMaxLots() != null ? limit.getDailyMaxLots() : new BigDecimal("1000"));
            fact.setDailyMaxOrders(limit.getDailyMaxOrders() != null ? limit.getDailyMaxOrders() : 100);
        } else {
            fact.setDailyMaxLots(new BigDecimal("1000"));
            fact.setDailyMaxOrders(100);
        }
        kieSession.insert(fact);
    }

    private void insertPositionLimit(KieSession kieSession, String symbol) {
        Optional<PositionLimitEntity> limitOpt = ruleDataService.getPositionLimit(symbol);
        
        PositionLimitFact fact = new PositionLimitFact();
        fact.setSymbol(symbol);
        
        if (limitOpt.isPresent()) {
            PositionLimitEntity limit = limitOpt.get();
            fact.setPerSymbolLimit(limit.getPerSymbolLimit() != null ? limit.getPerSymbolLimit() : new BigDecimal("100"));
            fact.setGlobalNetExposure(limit.getGlobalNetExposure() != null ? limit.getGlobalNetExposure() : new BigDecimal("1000000"));
        } else {
            fact.setPerSymbolLimit(new BigDecimal("100"));
            fact.setGlobalNetExposure(new BigDecimal("1000000"));
        }
        fact.setCurrentNetPosition(BigDecimal.ZERO);
        kieSession.insert(fact);
    }

    private void insertNewsEvent(KieSession kieSession, String symbol) {
        Optional<com.cfd.risk.persistence.NewsEventEntity> eventOpt = ruleDataService.getActiveNewsEvent(symbol);
        
        if (eventOpt.isPresent()) {
            com.cfd.risk.persistence.NewsEventEntity event = eventOpt.get();
            NewsEventFact fact = new NewsEventFact();
            fact.setSymbol(event.getSymbol());
            fact.setEventTime(event.getEventTime());
            fact.setImpactLevel(event.getImpactLevel());
            fact.setPreEventLockSeconds(event.getPreEventLockSeconds());
            fact.setPostEventLockSeconds(event.getPostEventLockSeconds());
            fact.setDescription(event.getDescription());
            fact.setLocked(true);
            kieSession.insert(fact);
        }
    }

    private void insertRiskRules(KieSession kieSession, String symbol) {
        List<RiskRuleFact> effectiveRules = riskRuleService.loadEffectiveRules(symbol);
        for (RiskRuleFact ruleFact : effectiveRules) {
            kieSession.insert(ruleFact);
        }
    }
}
