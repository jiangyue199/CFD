package com.cfd.configservice.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cfd.configservice.persistence.RuntimeConfigDbMapper;
import com.cfd.configservice.persistence.RuntimeConfigEntity;

@Service
public class RuntimeConfigService {

    private final RuntimeConfigDbMapper runtimeConfigDbMapper;

    public RuntimeConfigService(RuntimeConfigDbMapper runtimeConfigDbMapper) {
        this.runtimeConfigDbMapper = runtimeConfigDbMapper;
        ensureDefaults();
    }

    public String get(String key) {
        RuntimeConfigEntity config = runtimeConfigDbMapper.selectById(key);
        return config == null ? null : config.getConfigValue();
    }

    public Map<String, String> all() {
        Map<String, String> result = new HashMap<>();
        runtimeConfigDbMapper.selectList(new LambdaQueryWrapper<RuntimeConfigEntity>())
                .forEach(config -> result.put(config.getConfigKey(), config.getConfigValue()));
        return Map.copyOf(result);
    }

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

    private void ensureDefaults() {
        ensurePresent("leverage.max", "50");
        ensurePresent("spread.default", "0.0002");
        ensurePresent("license.region", "GLOBAL");
    }

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
