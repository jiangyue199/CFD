package com.cfd.risk.persistence;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("risk_rule")
public class RiskRuleEntity {

    @TableId(value = "rule_code", type = IdType.INPUT)
    private String ruleCode;

    @TableField("rule_value")
    private BigDecimal ruleValue;

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public BigDecimal getRuleValue() {
        return ruleValue;
    }

    public void setRuleValue(BigDecimal ruleValue) {
        this.ruleValue = ruleValue;
    }
}
