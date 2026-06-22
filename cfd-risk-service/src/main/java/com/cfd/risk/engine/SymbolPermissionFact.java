package com.cfd.risk.engine;

import java.math.BigDecimal;

/**
 * 品种权限事实 - 包含品种交易权限配置。
 */
public class SymbolPermissionFact {

    private String symbol;
    private String userLevel;
    private String region;
    private Boolean allowed;
    private String direction;
    private Boolean hedgeAllowed;
    private BigDecimal minLot;
    private BigDecimal maxLot;
    private BigDecimal stepLot;
    private BigDecimal maxLeverage;
    private BigDecimal minStopLevel;

    public SymbolPermissionFact() {
    }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public String getUserLevel() { return userLevel; }
    public void setUserLevel(String userLevel) { this.userLevel = userLevel; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public Boolean getAllowed() { return allowed; }
    public void setAllowed(Boolean allowed) { this.allowed = allowed; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    public Boolean getHedgeAllowed() { return hedgeAllowed; }
    public void setHedgeAllowed(Boolean hedgeAllowed) { this.hedgeAllowed = hedgeAllowed; }
    public BigDecimal getMinLot() { return minLot; }
    public void setMinLot(BigDecimal minLot) { this.minLot = minLot; }
    public BigDecimal getMaxLot() { return maxLot; }
    public void setMaxLot(BigDecimal maxLot) { this.maxLot = maxLot; }
    public BigDecimal getStepLot() { return stepLot; }
    public void setStepLot(BigDecimal stepLot) { this.stepLot = stepLot; }
    public BigDecimal getMaxLeverage() { return maxLeverage; }
    public void setMaxLeverage(BigDecimal maxLeverage) { this.maxLeverage = maxLeverage; }
    public BigDecimal getMinStopLevel() { return minStopLevel; }
    public void setMinStopLevel(BigDecimal minStopLevel) { this.minStopLevel = minStopLevel; }
}
