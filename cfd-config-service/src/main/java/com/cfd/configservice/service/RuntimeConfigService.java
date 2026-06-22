package com.cfd.configservice.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cfd.configservice.persistence.RuntimeConfigDbMapper;
import com.cfd.configservice.persistence.RuntimeConfigEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 运行时配置服务。
 *
 * <p>封装运行时配置的业务逻辑，包括配置的查询、更新和默认值初始化。
 * 服务启动时自动确保默认配置项存在。</p>
 *
 * @author CFD Platform Team
 */
@Service
public class RuntimeConfigService {

    private final RuntimeConfigDbMapper runtimeConfigDbMapper;

    /**
     * 构造运行时配置服务，并初始化默认配置。
     *
     * @param runtimeConfigDbMapper 运行时配置数据库 Mapper
     */
    public RuntimeConfigService(RuntimeConfigDbMapper runtimeConfigDbMapper) {
        this.runtimeConfigDbMapper = runtimeConfigDbMapper;
        ensureDefaults();
    }

    /**
     * 根据键获取配置值。
     *
     * @param key 配置键
     * @return 配置值，不存在时返回 null
     */
    public String get(String key) {
        RuntimeConfigEntity config = runtimeConfigDbMapper.selectById(key);
        return config == null ? null : config.getConfigValue();
    }

    /**
     * 获取所有运行时配置。
     *
     * @return 所有配置的不可变键值对映射
     */
    public Map<String, String> all() {
        Map<String, String> result = new HashMap<>();
        runtimeConfigDbMapper.selectList(new LambdaQueryWrapper<RuntimeConfigEntity>())
                .forEach(config -> result.put(config.getConfigKey(), config.getConfigValue()));
        return Map.copyOf(result);
    }

    /**
     * 新增或更新配置项。
     *
     * @param key   配置键
     * @param value 配置值
     */
    @Transactional
    public void put(String key, String value) {
        RuntimeConfigEntity existing = runtimeConfigDbMapper.selectById(key);
        if (existing == null) {
            RuntimeConfigEntity created = new RuntimeConfigEntity();
            created.setConfigKey(key);
            created.setConfigValue(value);
            runtimeConfigDbMapper.insert(created);
            return;
        }
        existing.setConfigValue(value);
        runtimeConfigDbMapper.updateById(existing);
    }

    /**
     * 确保默认配置项存在。
     */
    private void ensureDefaults() {
        ensurePresent("leverage.max", "50");
        ensurePresent("spread.default", "0.0002");
        ensurePresent("license.region", "GLOBAL");
    }

    /**
     * 若指定键的配置不存在，则插入默认值。
     *
     * @param key   配置键
     * @param value 默认配置值
     */
    private void ensurePresent(String key, String value) {
        if (runtimeConfigDbMapper.selectById(key) != null) {
            return;
        }
        RuntimeConfigEntity entity = new RuntimeConfigEntity();
        entity.setConfigKey(key);
        entity.setConfigValue(value);
        runtimeConfigDbMapper.insert(entity);
    }
}
