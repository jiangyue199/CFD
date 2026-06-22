package com.cfd.risk.engine;

import java.math.BigDecimal;

/**
 * 风控校验事实 - 扩展版本，包含所有事前风控所需字段。
 */
public class RiskCheckFact {

    private String userId;
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal leverage;
    private String ip;
    private String deviceId;
    private String cardNo;
    private String userLevel;
    private String region;
    private String direction;
    private Boolean hedgeAllowed;
    private BigDecimal equity;
    private BigDecimal initMargin;
    private BigDecimal currentPrice;
    private BigDecimal orderPrice;
    private BigDecimal stopLevel;
    private String orderType;

    public RiskCheckFact() {
    }

    public RiskCheckFact(String userId, String symbol, BigDecimal quantity, BigDecimal leverage) {
        this.userId = userId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.leverage = leverage;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getLeverage() { return leverage; }
    public void setLeverage(BigDecimal leverage) { this.leverage = leverage; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getCardNo() { return cardNo; }
    public void setCardNo(String cardNo) { this.cardNo = cardNo; }
    public String getUserLevel() { return userLevel; }
    public void setUserLevel(String userLevel) { this.userLevel = userLevel; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    public Boolean getHedgeAllowed() { return hedgeAllowed; }
    public void setHedgeAllowed(Boolean hedgeAllowed) { this.hedgeAllowed = hedgeAllowed; }
    public BigDecimal getEquity() { return equity; }
    public void setEquity(BigDecimal equity) { this.equity = equity; }
    public BigDecimal getInitMargin() { return initMargin; }
    public void setInitMargin(BigDecimal initMargin) { this.initMargin = initMargin; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    public BigDecimal getOrderPrice() { return orderPrice; }
    public void setOrderPrice(BigDecimal orderPrice) { this.orderPrice = orderPrice; }
    public BigDecimal getStopLevel() { return stopLevel; }
    public void setStopLevel(BigDecimal stopLevel) { this.stopLevel = stopLevel; }
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
}
