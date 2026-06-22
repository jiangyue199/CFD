package com.cfd.risk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cfd.risk.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * 保证金计算服务。
 *
 * <p>负责计算账户保证金、持仓保证金、保证金水平等核心指标。</p>
 *
 * @author CFD Platform Team
 */
@Service
public class MarginCalculationService {

    private final AccountMarginDbMapper accountMarginMapper;
    private final PositionMarginDbMapper positionMarginMapper;
    private final MarginConfigDbMapper marginConfigMapper;

    public MarginCalculationService(AccountMarginDbMapper accountMarginMapper,
                                    PositionMarginDbMapper positionMarginMapper,
                                    MarginConfigDbMapper marginConfigMapper) {
        this.accountMarginMapper = accountMarginMapper;
        this.positionMarginMapper = positionMarginMapper;
        this.marginConfigMapper = marginConfigMapper;
    }

    /**
     * 计算并更新账户保证金信息。
     *
     * @param userId 用户ID
     * @param balance 账户余额
     * @return 更新后的账户保证金实体
     */
    @Transactional
    public AccountMarginEntity calculateAndUpdateMargin(String userId, BigDecimal balance) {
        List<PositionMarginEntity> positions = positionMarginMapper.selectList(
                new LambdaQueryWrapper<PositionMarginEntity>()
                        .eq(PositionMarginEntity::getUserId, userId)
                        .eq(PositionMarginEntity::getStatus, "OPEN"));

        BigDecimal totalUsedMargin = BigDecimal.ZERO;
        BigDecimal totalFloatingPnl = BigDecimal.ZERO;

        for (PositionMarginEntity position : positions) {
            totalUsedMargin = totalUsedMargin.add(position.getUsedMargin() != null ? position.getUsedMargin() : BigDecimal.ZERO);
            totalFloatingPnl = totalFloatingPnl.add(position.getFloatingPnl() != null ? position.getFloatingPnl() : BigDecimal.ZERO);
        }

        BigDecimal equity = balance.add(totalFloatingPnl);
        BigDecimal freeMargin = equity.subtract(totalUsedMargin);
        BigDecimal marginLevel = calculateMarginLevel(equity, totalUsedMargin);

        AccountMarginEntity entity = accountMarginMapper.selectOne(
                new LambdaQueryWrapper<AccountMarginEntity>().eq(AccountMarginEntity::getUserId, userId));

        if (entity == null) {
            entity = new AccountMarginEntity();
            entity.setUserId(userId);
        }

        entity.setBalance(balance);
        entity.setEquity(equity);
        entity.setUsedMargin(totalUsedMargin);
        entity.setFreeMargin(freeMargin);
        entity.setMarginLevel(marginLevel);
        entity.setFloatingPnl(totalFloatingPnl);

        if (entity.getId() == null) {
            accountMarginMapper.insert(entity);
        } else {
            accountMarginMapper.updateById(entity);
        }

        return entity;
    }

    /**
     * 计算保证金水平。
     *
     * @param equity 账户权益
     * @param usedMargin 已用保证金
     * @return 保证金水平（百分比）
     */
    public BigDecimal calculateMarginLevel(BigDecimal equity, BigDecimal usedMargin) {
        if (usedMargin == null || usedMargin.compareTo(BigDecimal.ZERO) == 0) {
            return new BigDecimal("9999");
        }
        return equity.multiply(new BigDecimal("100"))
                .divide(usedMargin, 4, RoundingMode.HALF_UP);
    }

    /**
     * 计算持仓所需保证金。
     *
     * @param quantity 持仓数量
     * @param price 开仓价格
     * @param leverage 杠杆倍数
     * @return 所需保证金
     */
    public BigDecimal calculateRequiredMargin(BigDecimal quantity, BigDecimal price, BigDecimal leverage) {
        return quantity.multiply(price).divide(leverage, 8, RoundingMode.HALF_UP);
    }

    /**
     * 计算持仓浮动盈亏。
     *
     * @param quantity 持仓数量
     * @param openPrice 开仓价格
     * @param currentPrice 当前价格
     * @param direction 方向（BUY/SELL）
     * @return 浮动盈亏
     */
    public BigDecimal calculateFloatingPnl(BigDecimal quantity, BigDecimal openPrice, 
                                           BigDecimal currentPrice, String direction) {
        BigDecimal priceDiff = currentPrice.subtract(openPrice);
        if ("SELL".equalsIgnoreCase(direction)) {
            priceDiff = priceDiff.negate();
        }
        return quantity.multiply(priceDiff);
    }

    /**
     * 获取用户保证金配置。
     *
     * @param userLevel 用户等级
     * @return 保证金配置
     */
    public MarginConfigEntity getMarginConfig(String userLevel) {
        MarginConfigEntity config = marginConfigMapper.selectOne(
                new LambdaQueryWrapper<MarginConfigEntity>()
                        .eq(MarginConfigEntity::getUserLevel, userLevel));
        if (config == null) {
            config = marginConfigMapper.selectOne(
                    new LambdaQueryWrapper<MarginConfigEntity>()
                            .eq(MarginConfigEntity::getUserLevel, "RETAIL"));
        }
        return config;
    }

    /**
     * 获取账户保证金信息。
     *
     * @param userId 用户ID
     * @return 账户保证金实体
     */
    public Optional<AccountMarginEntity> getAccountMargin(String userId) {
        return Optional.ofNullable(accountMarginMapper.selectOne(
                new LambdaQueryWrapper<AccountMarginEntity>()
                        .eq(AccountMarginEntity::getUserId, userId)));
    }

    /**
     * 获取用户所有持仓。
     *
     * @param userId 用户ID
     * @return 持仓列表
     */
    public List<PositionMarginEntity> getUserPositions(String userId) {
        return positionMarginMapper.selectList(
                new LambdaQueryWrapper<PositionMarginEntity>()
                        .eq(PositionMarginEntity::getUserId, userId)
                        .eq(PositionMarginEntity::getStatus, "OPEN")
                        .orderByDesc(PositionMarginEntity::getUsedMargin));
    }

    /**
     * 获取用户指定品种的持仓保证金占比。
     *
     * @param userId 用户ID
     * @param symbol 品种
     * @return 占用保证金
     */
    public BigDecimal getSymbolUsedMargin(String userId, String symbol) {
        List<PositionMarginEntity> positions = positionMarginMapper.selectList(
                new LambdaQueryWrapper<PositionMarginEntity>()
                        .eq(PositionMarginEntity::getUserId, userId)
                        .eq(PositionMarginEntity::getSymbol, symbol)
                        .eq(PositionMarginEntity::getStatus, "OPEN"));

        return positions.stream()
                .map(p -> p.getUsedMargin() != null ? p.getUsedMargin() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 检查是否有足够的可用保证金。
     *
     * @param userId 用户ID
     * @param requiredMargin 所需保证金
     * @return 是否足够
     */
    public boolean hasSufficientMargin(String userId, BigDecimal requiredMargin) {
        Optional<AccountMarginEntity> marginOpt = getAccountMargin(userId);
        if (marginOpt.isEmpty()) {
            return false;
        }
        AccountMarginEntity margin = marginOpt.get();
        return margin.getFreeMargin().compareTo(requiredMargin) >= 0;
    }
}
