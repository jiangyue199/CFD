package com.cfd.risk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cfd.risk.persistence.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * 规则数据服务 - 从数据库加载各类风控规则配置。
 * 
 * @author CFD Platform Team
 */
@Service
public class RuleDataService {

    private final RiskBlacklistDbMapper blacklistMapper;
    private final UserStatusDbMapper userStatusMapper;
    private final SymbolPermissionDbMapper symbolPermissionMapper;
    private final DailyTradeLimitDbMapper dailyTradeLimitMapper;
    private final PositionLimitDbMapper positionLimitMapper;
    private final TradingSessionDbMapper tradingSessionMapper;
    private final NewsEventDbMapper newsEventMapper;
    private final UserDailyTradeDbMapper userDailyTradeMapper;

    public RuleDataService(RiskBlacklistDbMapper blacklistMapper,
                          UserStatusDbMapper userStatusMapper,
                          SymbolPermissionDbMapper symbolPermissionMapper,
                          DailyTradeLimitDbMapper dailyTradeLimitMapper,
                          PositionLimitDbMapper positionLimitMapper,
                          TradingSessionDbMapper tradingSessionMapper,
                          NewsEventDbMapper newsEventMapper,
                          UserDailyTradeDbMapper userDailyTradeMapper) {
        this.blacklistMapper = blacklistMapper;
        this.userStatusMapper = userStatusMapper;
        this.symbolPermissionMapper = symbolPermissionMapper;
        this.dailyTradeLimitMapper = dailyTradeLimitMapper;
        this.positionLimitMapper = positionLimitMapper;
        this.tradingSessionMapper = tradingSessionMapper;
        this.newsEventMapper = newsEventMapper;
        this.userDailyTradeMapper = userDailyTradeMapper;
    }

    // === 1.1 黑名单检查 ===
    public boolean isInBlacklist(String targetType, String targetValue) {
        Long count = blacklistMapper.selectCount(new LambdaQueryWrapper<RiskBlacklistEntity>()
                .eq(RiskBlacklistEntity::getListType, "BLACK")
                .eq(RiskBlacklistEntity::getTargetType, targetType)
                .eq(RiskBlacklistEntity::getTargetValue, targetValue)
                .eq(RiskBlacklistEntity::getEnabled, true)
                .and(w -> w.isNull(RiskBlacklistEntity::getExpiresAt)
                        .or().gt(RiskBlacklistEntity::getExpiresAt, LocalDateTime.now())));
        return count != null && count > 0;
    }

    // === 1.2 白名单检查 ===
    public boolean isInWhitelist(String userId) {
        Long count = blacklistMapper.selectCount(new LambdaQueryWrapper<RiskBlacklistEntity>()
                .eq(RiskBlacklistEntity::getListType, "WHITE")
                .eq(RiskBlacklistEntity::getTargetType, "USER_ID")
                .eq(RiskBlacklistEntity::getTargetValue, userId)
                .eq(RiskBlacklistEntity::getEnabled, true)
                .and(w -> w.isNull(RiskBlacklistEntity::getExpiresAt)
                        .or().gt(RiskBlacklistEntity::getExpiresAt, LocalDateTime.now())));
        return count != null && count > 0;
    }

    // === 用户状态检查 ===
    public String getUserStatus(String userId) {
        UserStatusEntity status = userStatusMapper.selectById(userId);
        return status != null ? status.getStatus() : "ACTIVE";
    }

    // === 1.3 品种权限检查 ===
    public Optional<SymbolPermissionEntity> getSymbolPermission(String symbol, String userLevel, String region) {
        SymbolPermissionEntity specific = symbolPermissionMapper.selectOne(new LambdaQueryWrapper<SymbolPermissionEntity>()
                .eq(SymbolPermissionEntity::getSymbol, symbol)
                .eq(SymbolPermissionEntity::getUserLevel, userLevel)
                .eq(SymbolPermissionEntity::getRegion, region));
        if (specific != null) {
            return Optional.of(specific);
        }
        SymbolPermissionEntity global = symbolPermissionMapper.selectOne(new LambdaQueryWrapper<SymbolPermissionEntity>()
                .eq(SymbolPermissionEntity::getSymbol, symbol)
                .eq(SymbolPermissionEntity::getUserLevel, userLevel)
                .eq(SymbolPermissionEntity::getRegion, "GLOBAL"));
        return Optional.ofNullable(global);
    }

    // === 1.6 日交易限额检查 ===
    public Optional<DailyTradeLimitEntity> getDailyTradeLimit(String userId, String symbol) {
        DailyTradeLimitEntity specific = dailyTradeLimitMapper.selectOne(new LambdaQueryWrapper<DailyTradeLimitEntity>()
                .eq(DailyTradeLimitEntity::getUserId, userId)
                .eq(DailyTradeLimitEntity::getSymbol, symbol));
        if (specific != null) {
            return Optional.of(specific);
        }
        DailyTradeLimitEntity global = dailyTradeLimitMapper.selectOne(new LambdaQueryWrapper<DailyTradeLimitEntity>()
                .eq(DailyTradeLimitEntity::getUserId, userId)
                .eq(DailyTradeLimitEntity::getSymbol, "*"));
        return Optional.ofNullable(global);
    }

    // === 1.6 获取当日交易统计 ===
    public UserDailyTradeEntity getTodayTrade(String userId, String symbol) {
        LocalDate today = LocalDate.now();
        UserDailyTradeEntity entity = userDailyTradeMapper.selectOne(new LambdaQueryWrapper<UserDailyTradeEntity>()
                .eq(UserDailyTradeEntity::getUserId, userId)
                .eq(UserDailyTradeEntity::getSymbol, symbol)
                .eq(UserDailyTradeEntity::getTradeDate, today));
        if (entity == null) {
            entity = new UserDailyTradeEntity();
            entity.setUserId(userId);
            entity.setSymbol(symbol);
            entity.setTradeDate(today);
        }
        return entity;
    }

    // === 1.7/1.8 持仓限额 ===
    public Optional<PositionLimitEntity> getPositionLimit(String symbol) {
        return Optional.ofNullable(positionLimitMapper.selectOne(new LambdaQueryWrapper<PositionLimitEntity>()
                .eq(PositionLimitEntity::getSymbol, symbol)));
    }

    // === 1.12 交易时段检查 ===
    public boolean isInTradingSession(String symbol) {
        int dayOfWeek = LocalDate.now().getDayOfWeek().getValue();
        LocalTime now = LocalTime.now();
        
        TradingSessionEntity session = tradingSessionMapper.selectOne(new LambdaQueryWrapper<TradingSessionEntity>()
                .eq(TradingSessionEntity::getSymbol, symbol)
                .eq(TradingSessionEntity::getDayOfWeek, dayOfWeek)
                .eq(TradingSessionEntity::getEnabled, true));
        
        if (session == null) {
            return true;
        }
        
        LocalTime startTime = session.getStartTime();
        LocalTime endTime = session.getEndTime();
        
        return !now.isBefore(startTime) && !now.isAfter(endTime);
    }

    // === 1.13 新闻事件检查 ===
    public Optional<NewsEventEntity> getActiveNewsEvent(String symbol) {
        LocalDateTime now = LocalDateTime.now();
        List<NewsEventEntity> events = newsEventMapper.selectList(new LambdaQueryWrapper<NewsEventEntity>()
                .eq(NewsEventEntity::getSymbol, symbol)
                .or()
                .eq(NewsEventEntity::getSymbol, "*"));
        
        for (NewsEventEntity event : events) {
            LocalDateTime preTime = event.getEventTime().minusSeconds(event.getPreEventLockSeconds());
            LocalDateTime postTime = event.getEventTime().plusSeconds(event.getPostEventLockSeconds());
            if (now.isAfter(preTime) && now.isBefore(postTime)) {
                return Optional.of(event);
            }
        }
        return Optional.empty();
    }
}
