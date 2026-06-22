package com.cfd.marketdata.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 市场报价数据库实体。
 *
 * <p>映射数据库表 {@code market_quote}，存储交易品种的买入价、卖出价和报价时间。</p>
 *
 * @author CFD Platform Team
 */
@TableName("market_quote")
public class MarketQuoteEntity {

    /** 交易品种代码（主键） */
    @TableId(value = "symbol", type = IdType.INPUT)
    private String symbol;

    /** 买入价 */
    @TableField("bid")
    private BigDecimal bid;

    /** 卖出价 */
    @TableField("ask")
    private BigDecimal ask;

    /** 报价时间 */
    @TableField("quote_time")
    private Instant quoteTime;

    /** @return 交易品种代码 */
    public String getSymbol() {
        return symbol;
    }

    /** @param symbol 交易品种代码 */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /** @return 买入价 */
    public BigDecimal getBid() {
        return bid;
    }

    /** @param bid 买入价 */
    public void setBid(BigDecimal bid) {
        this.bid = bid;
    }

    /** @return 卖出价 */
    public BigDecimal getAsk() {
        return ask;
    }

    /** @param ask 卖出价 */
    public void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

    /** @return 报价时间 */
    public Instant getQuoteTime() {
        return quoteTime;
    }

    /** @param quoteTime 报价时间 */
    public void setQuoteTime(Instant quoteTime) {
        this.quoteTime = quoteTime;
    }
}
