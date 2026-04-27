package com.cfd.configservice.api;

import java.util.Map;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cfd.configservice.service.RuntimeConfigService;

@RestController
@RequestMapping("/configs")
public class ConfigController {

    private final RuntimeConfigService runtimeConfigService;

    public ConfigController(RuntimeConfigService runtimeConfigService) {
        this.runtimeConfigService = runtimeConfigService;
    }

    @GetMapping
    public Map<String, String> all() {
        return runtimeConfigService.all();
    }

    @GetMapping("/{key}")
    public String get(@PathVariable String key) {
        return runtimeConfigService.get(key);
    }

    @PostMapping
    public Map<String, String> update(@Validated @RequestBody ConfigUpdateRequest request) {
        runtimeConfigService.put(request.key(), request.value());
        return runtimeConfigService.all();
    }
}
