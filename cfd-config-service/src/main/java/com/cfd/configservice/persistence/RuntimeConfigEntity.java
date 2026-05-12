package com.cfd.configservice.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 运行时配置数据库实体。
 *
 * <p>映射数据库表 {@code runtime_config}，存储运行时配置的键值对。</p>
 *
 * @author CFD Platform Team
 */
@TableName("runtime_config")
public class RuntimeConfigEntity {

    /** 配置键（主键） */
    @TableId(value = "config_key", type = IdType.INPUT)
    private String configKey;

    /** 配置值 */
    @TableField("config_value")
    private String configValue;

    /** @return 配置键 */
    public String getConfigKey() {
        return configKey;
    }

    /** @param configKey 配置键 */
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    /** @return 配置值 */
    public String getConfigValue() {
        return configValue;
    }

    /** @param configValue 配置值 */
    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }
}
