package com.cfd.risk.engine;

import java.math.BigDecimal;

/**
 * 日交易统计事实 - 包含用户当日交易统计数据。
 */
public class DailyTradeStatsFact {

    private String userId;
    private String symbol;
    private BigDecimal todayTotalLots;
    private Integer todayOrderCount;
    private BigDecimal dailyMaxLots;
    private Integer dailyMaxOrders;

    public DailyTradeStatsFact() {
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public BigDecimal getTodayTotalLots() { return todayTotalLots; }
    public void setTodayTotalLots(BigDecimal todayTotalLots) { this.todayTotalLots = todayTotalLots; }
    public Integer getTodayOrderCount() { return todayOrderCount; }
    public void setTodayOrderCount(Integer todayOrderCount) { this.todayOrderCount = todayOrderCount; }
    public BigDecimal getDailyMaxLots() { return dailyMaxLots; }
    public void setDailyMaxLots(BigDecimal dailyMaxLots) { this.dailyMaxLots = dailyMaxLots; }
    public Integer getDailyMaxOrders() { return dailyMaxOrders; }
    public void setDailyMaxOrders(Integer dailyMaxOrders) { this.dailyMaxOrders = dailyMaxOrders; }
}
