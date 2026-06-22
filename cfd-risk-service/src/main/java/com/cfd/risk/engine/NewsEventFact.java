package com.cfd.risk.engine;

import java.time.LocalDateTime;

/**
 * 新闻事件事实 - 包含活跃的新闻事件信息。
 *
 * <p>用于规则1.13（重要新闻事件期间禁止开仓），在重大新闻发布前后一段时间内禁止交易。</p>
 *
 * @author CFD Platform Team
 */
public class NewsEventFact {

    private String symbol;
    private LocalDateTime eventTime;
    private String impactLevel;
    private Integer preEventLockSeconds;
    private Integer postEventLockSeconds;
    private String description;
    private boolean isLocked;

    public NewsEventFact() {
    }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }
    public String getImpactLevel() { return impactLevel; }
    public void setImpactLevel(String impactLevel) { this.impactLevel = impactLevel; }
    public Integer getPreEventLockSeconds() { return preEventLockSeconds; }
    public void setPreEventLockSeconds(Integer preEventLockSeconds) { this.preEventLockSeconds = preEventLockSeconds; }
    public Integer getPostEventLockSeconds() { return postEventLockSeconds; }
    public void setPostEventLockSeconds(Integer postEventLockSeconds) { this.postEventLockSeconds = postEventLockSeconds; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isLocked() { return isLocked; }
    public void setLocked(boolean locked) { isLocked = locked; }
}
