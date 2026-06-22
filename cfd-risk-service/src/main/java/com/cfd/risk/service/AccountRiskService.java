package com.cfd.risk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cfd.risk.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 账户风控服务。
 *
 * <p>实现账户与保证金风控规则：</p>
 * <ul>
 *   <li>2.1 可用保证金不足（Order Reject）</li>
 *   <li>2.2 保证金水平预警（Margin Call）</li>
 *   <li>2.3 止损离场/强平线（Stop Out）</li>
 *   <li>2.4 负余额保护（Negative Balance Protection）</li>
 *   <li>2.5 单一品种集中度预警</li>
 *   <li>2.6 浮动亏损过快告警</li>
 *   <li>2.7 隔夜利息/展期费用预警</li>
 * </ul>
 *
 * @author CFD Platform Team
 */
@Service
public class AccountRiskService {

    private final MarginCalculationService marginCalculationService;
    private final AccountMarginDbMapper accountMarginMapper;
    private final MarginConfigDbMapper marginConfigMapper;
    private final PositionMarginDbMapper positionMarginMapper;
    private final RiskAlertDbMapper riskAlertMapper;
    private final UserFloatingLossDbMapper userFloatingLossMapper;
    private final ForceCloseRecordDbMapper forceCloseRecordMapper;

    public AccountRiskService(MarginCalculationService marginCalculationService,
                              AccountMarginDbMapper accountMarginMapper,
                              MarginConfigDbMapper marginConfigMapper,
                              PositionMarginDbMapper positionMarginMapper,
                              RiskAlertDbMapper riskAlertMapper,
                              UserFloatingLossDbMapper userFloatingLossMapper,
                              ForceCloseRecordDbMapper forceCloseRecordMapper) {
        this.marginCalculationService = marginCalculationService;
        this.accountMarginMapper = accountMarginMapper;
        this.marginConfigMapper = marginConfigMapper;
        this.positionMarginMapper = positionMarginMapper;
        this.riskAlertMapper = riskAlertMapper;
        this.userFloatingLossMapper = userFloatingLossMapper;
        this.forceCloseRecordMapper = forceCloseRecordMapper;
    }

    /**
     * 规则2.1: 检查可用保证金是否充足。
     *
     * @param userId 用户ID
     * @param requiredMargin 所需保证金
     * @return 是否充足
     */
    public boolean checkSufficientMargin(String userId, BigDecimal requiredMargin) {
        Optional<AccountMarginEntity> marginOpt = marginCalculationService.getAccountMargin(userId);
        if (marginOpt.isEmpty()) {
            return requiredMargin.compareTo(BigDecimal.ZERO) <= 0;
        }
        AccountMarginEntity margin = marginOpt.get();
        return margin.getFreeMargin().compareTo(requiredMargin) >= 0;
    }

    /**
     * 规则2.2: 保证金水平预警（Margin Call）。
     *
     * <p>当 MarginLevel ≤ MarginCallLevel 时触发预警。</p>
     *
     * @param userId 用户ID
     * @param userLevel 用户等级
     * @return 是否触发预警
     */
    @Transactional
    public boolean checkMarginCall(String userId, String userLevel) {
        Optional<AccountMarginEntity> marginOpt = marginCalculationService.getAccountMargin(userId);
        if (marginOpt.isEmpty()) {
            return false;
        }

        AccountMarginEntity margin = marginOpt.get();
        MarginConfigEntity config = marginCalculationService.getMarginConfig(userLevel);

        if (margin.getMarginLevel().compareTo(config.getMarginCallLevel()) <= 0) {
            createAlert(userId, "MARGIN_CALL", "WARNING",
                    "保证金水平" + margin.getMarginLevel() + "%低于预警线" + config.getMarginCallLevel() + "%，请追加保证金",
                    margin.getMarginLevel(), margin.getEquity());
            return true;
        }
        return false;
    }

    /**
     * 规则2.3: 止损离场/强平线（Stop Out）。
     *
     * <p>当 MarginLevel ≤ StopOutLevel 时触发强平。</p>
     *
     * @param userId 用户ID
     * @param userLevel 用户等级
     * @return 需要强平的持仓列表
     */
    @Transactional
    public List<PositionMarginEntity> checkStopOut(String userId, String userLevel) {
        Optional<AccountMarginEntity> marginOpt = marginCalculationService.getAccountMargin(userId);
        if (marginOpt.isEmpty()) {
            return List.of();
        }

        AccountMarginEntity margin = marginOpt.get();
        MarginConfigEntity config = marginCalculationService.getMarginConfig(userLevel);

        if (margin.getMarginLevel().compareTo(config.getStopOutLevel()) <= 0) {
            createAlert(userId, "STOP_OUT", "CRITICAL",
                    "保证金水平" + margin.getMarginLevel() + "%低于强平线" + config.getStopOutLevel() + "%，将执行强平",
                    margin.getMarginLevel(), margin.getEquity());

            // 返回按占用保证金从大到小排序的持仓
            return marginCalculationService.getUserPositions(userId);
        }
        return List.of();
    }

    /**
     * 规则2.4: 负余额保护（Negative Balance Protection）。
     *
     * <p>当 Equity < 0 时，调整余额至0。</p>
     *
     * @param userId 用户ID
     * @param userLevel 用户等级
     * @return 是否触发了负余额保护
     */
    @Transactional
    public boolean checkNegativeBalanceProtection(String userId, String userLevel) {
        MarginConfigEntity config = marginCalculationService.getMarginConfig(userLevel);
        if (!Boolean.TRUE.equals(config.getNegativeBalanceProtection())) {
            return false;
        }

        Optional<AccountMarginEntity> marginOpt = marginCalculationService.getAccountMargin(userId);
        if (marginOpt.isEmpty()) {
            return false;
        }

        AccountMarginEntity margin = marginOpt.get();
        if (margin.getEquity().compareTo(BigDecimal.ZERO) < 0) {
            createAlert(userId, "NEGATIVE_BALANCE", "CRITICAL",
                    "账户权益为负" + margin.getEquity() + "，触发负余额保护，已调整至0",
                    margin.getMarginLevel(), margin.getEquity());

            // 调整余额
            BigDecimal adjustment = margin.getEquity().abs();
            margin.setBalance(margin.getBalance().add(adjustment));
            margin.setEquity(BigDecimal.ZERO);
            margin.setFreeMargin(BigDecimal.ZERO.subtract(margin.getUsedMargin()));
            accountMarginMapper.updateById(margin);

            return true;
        }
        return false;
    }

    /**
     * 规则2.5: 单一品种集中度预警。
     *
     * <p>当某品种占用保证金 / 账户权益 > concentrationRatio 时触发预警。</p>
     *
     * @param userId 用户ID
     * @param symbol 品种
     * @param userLevel 用户等级
     * @return 是否触发预警
     */
    @Transactional
    public boolean checkConcentrationRisk(String userId, String symbol, String userLevel) {
        Optional<AccountMarginEntity> marginOpt = marginCalculationService.getAccountMargin(userId);
        if (marginOpt.isEmpty()) {
            return false;
        }

        AccountMarginEntity margin = marginOpt.get();
        if (margin.getEquity().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        BigDecimal symbolMargin = marginCalculationService.getSymbolUsedMargin(userId, symbol);
        BigDecimal concentration = symbolMargin.multiply(new BigDecimal("100"))
                .divide(margin.getEquity(), 4, RoundingMode.HALF_UP);

        MarginConfigEntity config = marginCalculationService.getMarginConfig(userLevel);

        if (concentration.compareTo(config.getConcentrationRatio()) > 0) {
            createAlert(userId, "CONCENTRATION", "WARNING",
                    "品种" + symbol + "占用保证金占比" + concentration + "%超过预警阈值" + config.getConcentrationRatio() + "%",
                    margin.getMarginLevel(), margin.getEquity());
            return true;
        }
        return false;
    }

    /**
     * 规则2.6: 浮动亏损过快告警。
     *
     * <p>当日浮动亏损 / 账户权益 > 阈值 时触发预警。</p>
     *
     * @param userId 用户ID
     * @param userLevel 用户等级
     * @return 是否触发预警
     */
    @Transactional
    public boolean checkFloatingLossRate(String userId, String userLevel) {
        LocalDate today = LocalDate.now();
        UserFloatingLossEntity lossEntity = userFloatingLossMapper.selectOne(
                new LambdaQueryWrapper<UserFloatingLossEntity>()
                        .eq(UserFloatingLossEntity::getUserId, userId)
                        .eq(UserFloatingLossEntity::getTradeDate, today));

        if (lossEntity == null || Boolean.TRUE.equals(lossEntity.getAlertTriggered())) {
            return false;
        }

        MarginConfigEntity config = marginCalculationService.getMarginConfig(userLevel);

        if (lossEntity.getLossRatio().compareTo(config.getFloatingLossRatio()) > 0) {
            createAlert(userId, "FLOATING_LOSS", "WARNING",
                    "当日浮动亏损比例" + lossEntity.getLossRatio() + "%超过预警阈值" + config.getFloatingLossRatio() + "%",
                    null, lossEntity.getStartEquity().subtract(lossEntity.getCurrentFloatingLoss()));

            lossEntity.setAlertTriggered(true);
            userFloatingLossMapper.updateById(lossEntity);
            return true;
        }
        return false;
    }

    /**
     * 规则2.7: 隔夜利息/展期费用预警。
     *
     * <p>持仓过夜收取swap为负且金额较大时提示用户。</p>
     *
     * @param userId 用户ID
     * @param swapThreshold 隔夜利息预警阈值
     * @return 是否触发预警
     */
    @Transactional
    public boolean checkSwapWarning(String userId, BigDecimal swapThreshold) {
        List<PositionMarginEntity> positions = positionMarginMapper.selectList(
                new LambdaQueryWrapper<PositionMarginEntity>()
                        .eq(PositionMarginEntity::getUserId, userId)
                        .eq(PositionMarginEntity::getStatus, "OPEN")
                        .lt(PositionMarginEntity::getSwap, BigDecimal.ZERO));

        BigDecimal totalNegativeSwap = positions.stream()
                .map(p -> p.getSwap() != null ? p.getSwap().abs() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalNegativeSwap.compareTo(swapThreshold) > 0) {
            createAlert(userId, "SWAP_WARNING", "INFO",
                    "持仓隔夜利息累计" + totalNegativeSwap.negate() + "，金额较大请注意",
                    null, null);
            return true;
        }
        return false;
    }

    /**
     * 执行账户风险扫描（定时任务调用）。
     *
     * @param userId 用户ID
     * @param userLevel 用户等级
     */
    @Transactional
    public void scanAccountRisk(String userId, String userLevel) {
        checkMarginCall(userId, userLevel);
        checkStopOut(userId, userLevel);
        checkNegativeBalanceProtection(userId, userLevel);
        checkFloatingLossRate(userId, userLevel);
        checkSwapWarning(userId, new BigDecimal("100"));
    }

    /**
     * 创建风险告警记录。
     */
    private void createAlert(String userId, String alertType, String alertLevel,
                            String message, BigDecimal marginLevel, BigDecimal equity) {
        RiskAlertEntity alert = new RiskAlertEntity();
        alert.setUserId(userId);
        alert.setAlertType(alertType);
        alert.setAlertLevel(alertLevel);
        alert.setMessage(message);
        alert.setMarginLevel(marginLevel);
        alert.setEquity(equity);
        alert.setHandled(false);
        riskAlertMapper.insert(alert);
    }

    /**
     * 获取用户未处理的告警。
     */
    public List<RiskAlertEntity> getUnhandledAlerts(String userId) {
        return riskAlertMapper.selectList(
                new LambdaQueryWrapper<RiskAlertEntity>()
                        .eq(RiskAlertEntity::getUserId, userId)
                        .eq(RiskAlertEntity::getHandled, false)
                        .orderByDesc(RiskAlertEntity::getCreatedAt));
    }

    /**
     * 处理告警。
     */
    @Transactional
    public void handleAlert(Long alertId) {
        RiskAlertEntity alert = riskAlertMapper.selectById(alertId);
        if (alert != null) {
            alert.setHandled(true);
            alert.setHandledAt(LocalDateTime.now());
            riskAlertMapper.updateById(alert);
        }
    }
}
