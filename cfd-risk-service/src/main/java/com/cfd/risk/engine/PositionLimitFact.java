package com.cfd.risk.engine;

import java.math.BigDecimal;

/**
 * 持仓限额事实 - 包含持仓限额配置。
 *
 * <p>用于规则1.7（单一品种持仓限额校验）和规则1.8（全局风险敞口校验）。</p>
 *
 * @author CFD Platform Team
 */
public class PositionLimitFact {

    private String symbol;
    private BigDecimal perSymbolLimit;
    private BigDecimal globalNetExposure;
    private BigDecimal currentNetPosition;

    public PositionLimitFact() {
    }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public BigDecimal getPerSymbolLimit() { return perSymbolLimit; }
    public void setPerSymbolLimit(BigDecimal perSymbolLimit) { this.perSymbolLimit = perSymbolLimit; }
    public BigDecimal getGlobalNetExposure() { return globalNetExposure; }
    public void setGlobalNetExposure(BigDecimal globalNetExposure) { this.globalNetExposure = globalNetExposure; }
    public BigDecimal getCurrentNetPosition() { return currentNetPosition; }
    public void setCurrentNetPosition(BigDecimal currentNetPosition) { this.currentNetPosition = currentNetPosition; }
}
