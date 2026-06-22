package com.cfd.configservice.api;

import com.cfd.configservice.service.RuntimeConfigService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 配置 REST 控制器。
 *
 * <p>提供运行时配置的查询和更新 HTTP 接口，
 * 所有请求路径以 {@code /configs} 为前缀。</p>
 *
 * @author CFD Platform Team
 */
@RestController
@RequestMapping("/configs")
public class ConfigController {

    private final RuntimeConfigService runtimeConfigService;

    /**
     * 构造配置控制器。
     *
     * @param runtimeConfigService 运行时配置服务
     */
    public ConfigController(RuntimeConfigService runtimeConfigService) {
        this.runtimeConfigService = runtimeConfigService;
    }

    /**
     * 获取所有运行时配置。
     *
     * @return 所有配置的键值对映射
     */
    @GetMapping
    public Map<String, String> all() {
        return runtimeConfigService.all();
    }

    /**
     * 根据键获取单个配置值。
     *
     * @param key 配置键
     * @return 配置值，不存在时返回 null
     */
    @GetMapping("/{key}")
    public String get(@PathVariable String key) {
        return runtimeConfigService.get(key);
    }

    /**
     * 更新或新增配置项。
     *
     * @param request 包含配置键和值的更新请求
     * @return 更新后的所有配置的键值对映射
     */
    @PostMapping
    public Map<String, String> update(@Validated @RequestBody ConfigUpdateRequest request) {
        runtimeConfigService.put(request.key(), request.value());
        return runtimeConfigService.all();
    }
}
