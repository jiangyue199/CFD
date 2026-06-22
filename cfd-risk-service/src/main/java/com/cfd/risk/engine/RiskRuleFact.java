package com.cfd.risk.engine;

import java.math.BigDecimal;

/**
 * 风控规则事实 - 从数据库加载后插入 Drools 工作内存的规则事实。
 *
 * <p>每条数据库中的风控规则转换为 RiskRuleFact 对象，供 DRL 规则匹配使用。
 * symbol 为 "*" 表示全局规则，否则为品种特定规则。</p>
 *
 * @author CFD Platform Team
 */
public class RiskRuleFact {

    private String ruleCode;
    private String ruleName;
    private String symbol;
    private BigDecimal ruleValue;
    private int priority;

    public RiskRuleFact() {
    }

    public RiskRuleFact(String ruleCode, String ruleName, String symbol, BigDecimal ruleValue, int priority) {
        this.ruleCode = ruleCode;
        this.ruleName = ruleName;
        this.symbol = symbol;
        this.ruleValue = ruleValue;
        this.priority = priority;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getRuleValue() {
        return ruleValue;
    }

    public void setRuleValue(BigDecimal ruleValue) {
        this.ruleValue = ruleValue;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
