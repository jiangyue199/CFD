package com.cfd.risk.persistence;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 风控规则实体类。
 *
 * <p>对应数据库 risk_rule 表，存储风控规则编码及对应的阈值。</p>
 *
 * @author CFD Platform Team
 */
@TableName("risk_rule")
public class RiskRuleEntity {

    /** 规则编码，作为主键 */
    @TableId(value = "rule_code", type = IdType.INPUT)
    private String ruleCode;

    /** 规则阈值 */
    @TableField("rule_value")
    private BigDecimal ruleValue;

    /**
     * 获取规则编码。
     *
     * @return 规则编码
     */
    public String getRuleCode() {
        return ruleCode;
    }

    /**
     * 设置规则编码。
     *
     * @param ruleCode 规则编码
     */
    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    /**
     * 获取规则阈值。
     *
     * @return 规则阈值
     */
    public BigDecimal getRuleValue() {
        return ruleValue;
    }

    /**
     * 设置规则阈值。
     *
     * @param ruleValue 规则阈值
     */
    public void setRuleValue(BigDecimal ruleValue) {
        this.ruleValue = ruleValue;
    }
}
