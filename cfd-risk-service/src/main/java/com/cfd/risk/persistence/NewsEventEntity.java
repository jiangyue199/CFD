package com.cfd.risk.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 新闻事件实体类。
 *
 * <p>对应数据库 news_event 表，记录重大新闻事件（规则1.13）。
 * 在事件前后一段时间内禁止新开仓，防止高波动期间的异常交易。</p>
 *
 * @author CFD Platform Team
 */
@TableName("news_event")
public class NewsEventEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("symbol")
    private String symbol;

    @TableField("event_time")
    private LocalDateTime eventTime;

    @TableField("impact_level")
    private String impactLevel;

    @TableField("pre_event_lock_seconds")
    private Integer preEventLockSeconds;

    @TableField("post_event_lock_seconds")
    private Integer postEventLockSeconds;

    @TableField("description")
    private String description;

    @TableField("created_at")
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
