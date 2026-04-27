package com.cfd.marketdata.persistence;

import java.math.BigDecimal;
import java.time.Instant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("market_quote")
public class MarketQuoteEntity {

    @TableId(value = "symbol", type = IdType.INPUT)
    private String symbol;

    @TableField("bid")
    private BigDecimal bid;

    @TableField("ask")
    private BigDecimal ask;

    @TableField("quote_time")
    private Instant quoteTime;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public void setBid(BigDecimal bid) {
        this.bid = bid;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

    public Instant getQuoteTime() {
        return quoteTime;
    }

    public void setQuoteTime(Instant quoteTime) {
        this.quoteTime = quoteTime;
    }
}
