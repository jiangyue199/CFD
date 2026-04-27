package com.cfd.configservice.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class RuntimeConfigService {

    private final Map<String, String> configs = new ConcurrentHashMap<>();

    public RuntimeConfigService() {
        configs.put("leverage.max", "50");
        configs.put("spread.default", "0.0002");
        configs.put("license.region", "GLOBAL");
    }

    public String get(String key) {
        return configs.get(key);
    }

    public Map<String, String> all() {
        return Map.copyOf(configs);
    }

    public void put(String key, String value) {
        configs.put(key, value);
    }
}
