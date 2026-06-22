package com.cfd.risk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cfd.risk.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 强平服务。
 *
 * <p>负责执行强制平仓操作，包括：</p>
 * <ul>
 *   <li>止损离场（Stop Out）</li>
 *   <li>负余额保护强平</li>
 *   <li>风险控制强平</li>
 * </ul>
 *
 * @author CFD Platform Team
 */
@Service
public class ForceCloseService {

    private final MarginCalculationService marginCalculationService;
    private final AccountRiskService accountRiskService;
    private final PositionMarginDbMapper positionMarginMapper;
    private final AccountMarginDbMapper accountMarginMapper;
    private final ForceCloseRecordDbMapper forceCloseRecordMapper;

    public ForceCloseService(MarginCalculationService marginCalculationService,
                             AccountRiskService accountRiskService,
                             PositionMarginDbMapper positionMarginMapper,
                             AccountMarginDbMapper accountMarginMapper,
                             ForceCloseRecordDbMapper forceCloseRecordMapper) {
        this.marginCalculationService = marginCalculationService;
        this.accountRiskService = accountRiskService;
        this.positionMarginMapper = positionMarginMapper;
        this.accountMarginMapper = accountMarginMapper;
        this.forceCloseRecordMapper = forceCloseRecordMapper;
    }

    /**
     * 执行强平操作。
     *
     * <p>按占用保证金从大到小逐笔强平，直到保证金水平恢复到安全线以上。</p>
     *
     * @param userId 用户ID
     * @param userLevel 用户等级
     * @param targetMarginLevel 目标保证金水平（强平后应达到的水平）
     * @return 强平的持仓数量
     */
    @Transactional
    public int executeForceClose(String userId, String userLevel, BigDecimal targetMarginLevel) {
        List<PositionMarginEntity> positions = accountRiskService.checkStopOut(userId, userLevel);
        if (positions.isEmpty()) {
            return 0;
        }

        int closedCount = 0;
        AccountMarginEntity margin = marginCalculationService.getAccountMargin(userId).orElse(null);
        if (margin == null) {
            return 0;
        }

        MarginConfigEntity config = marginCalculationService.getMarginConfig(userLevel);
        BigDecimal marginBefore = margin.getUsedMargin();

        for (PositionMarginEntity position : positions) {
            // 检查是否已恢复到安全水平
            if (margin.getMarginLevel().compareTo(targetMarginLevel) > 0) {
                break;
            }

            // 执行强平
            forceClosePosition(position, "STOP_OUT", margin);

            // 更新账户保证金
            margin = marginCalculationService.getAccountMargin(userId).orElse(margin);
            closedCount++;
        }

        return closedCount;
    }

    /**
     * 强平单个持仓。
     *
     * @param position 持仓
     * @param reason 强平原因
     * @param margin 账户保证金信息
     */
    @Transactional
    public void forceClosePosition(PositionMarginEntity position, String reason, AccountMarginEntity margin) {
        BigDecimal marginBefore = margin.getUsedMargin();

        // 更新持仓状态
        position.setStatus("FORCE_CLOSED");
        positionMarginMapper.updateById(position);

        // 重新计算账户保证金
        BigDecimal newUsedMargin = margin.getUsedMargin().subtract(position.getUsedMargin());
        BigDecimal newFloatingPnl = margin.getFloatingPnl().subtract(position.getFloatingPnl());
        BigDecimal newEquity = margin.getBalance().add(newFloatingPnl);
        BigDecimal newFreeMargin = newEquity.subtract(newUsedMargin);
        BigDecimal newMarginLevel = marginCalculationService.calculateMarginLevel(newEquity, newUsedMargin);

        margin.setUsedMargin(newUsedMargin);
        margin.setFloatingPnl(newFloatingPnl);
        margin.setEquity(newEquity);
        margin.setFreeMargin(newFreeMargin);
        margin.setMarginLevel(newMarginLevel);
        accountMarginMapper.updateById(margin);

        // 记录强平
        ForceCloseRecordEntity record = new ForceCloseRecordEntity();
        record.setUserId(position.getUserId());
        record.setOrderId(position.getOrderId());
        record.setSymbol(position.getSymbol());
        record.setCloseReason(reason);
        record.setClosePrice(position.getCurrentPrice());
        record.setCloseQuantity(position.getQuantity());
        record.setRealizedPnl(position.getFloatingPnl());
        record.setMarginBefore(marginBefore);
        record.setMarginAfter(newUsedMargin);
        forceCloseRecordMapper.insert(record);
    }

    /**
     * 执行负余额保护强平。
     *
     * <p>当账户权益为负时，平掉所有持仓。</p>
     *
     * @param userId 用户ID
     * @return 强平的持仓数量
     */
    @Transactional
    public int executeNegativeBalanceProtection(String userId) {
        List<PositionMarginEntity> positions = positionMarginMapper.selectList(
                new LambdaQueryWrapper<PositionMarginEntity>()
                        .eq(PositionMarginEntity::getUserId, userId)
                        .eq(PositionMarginEntity::getStatus, "OPEN"));

        AccountMarginEntity margin = marginCalculationService.getAccountMargin(userId).orElse(null);
        if (margin == null) {
            return 0;
        }

        int closedCount = 0;
        for (PositionMarginEntity position : positions) {
            forceClosePosition(position, "NEGATIVE_BALANCE", margin);
            margin = marginCalculationService.getAccountMargin(userId).orElse(margin);
            closedCount++;
        }

        return closedCount;
    }

    /**
     * 获取用户强平记录。
     *
     * @param userId 用户ID
     * @return 强平记录列表
     */
    public List<ForceCloseRecordEntity> getForceCloseRecords(String userId) {
        return forceCloseRecordMapper.selectList(
                new LambdaQueryWrapper<ForceCloseRecordEntity>()
                        .eq(ForceCloseRecordEntity::getUserId, userId)
                        .orderByDesc(ForceCloseRecordEntity::getCreatedAt));
    }

    /**
     * 检查并执行强平（定时任务调用）。
     *
     * @param userId 用户ID
     * @param userLevel 用户等级
     */
    @Transactional
    public void checkAndExecuteForceClose(String userId, String userLevel) {
        MarginConfigEntity config = marginCalculationService.getMarginConfig(userLevel);

        // 检查是否需要强平
        List<PositionMarginEntity> stopOutPositions = accountRiskService.checkStopOut(userId, userLevel);
        if (!stopOutPositions.isEmpty()) {
            // 强平到保证金水平恢复到预警线以上
            executeForceClose(userId, userLevel, config.getMarginCallLevel());
        }

        // 检查负余额保护
        accountRiskService.checkNegativeBalanceProtection(userId, userLevel);
    }
}
