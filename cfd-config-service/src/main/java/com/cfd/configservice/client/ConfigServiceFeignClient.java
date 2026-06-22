package com.cfd.configservice.client;

import com.cfd.configservice.api.ConfigUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 配置服务 Feign 客户端接口。
 *
 * <p>供其他微服务通过 Feign 远程调用配置服务的查询和更新接口。</p>
 *
 * @author CFD Platform Team
 */
@FeignClient(
        name = "cfd-config-service",
        path = "/configs",
        url = "${services.config.url:http://localhost:8090}"
)
public interface ConfigServiceFeignClient {

    /**
     * 远程获取所有运行时配置。
     *
     * @return 所有配置的键值对映射
     */
    @GetMapping
    Map<String, String> all();

    /**
     * 远程根据键获取单个配置值。
     *
     * @param key 配置键
     * @return 配置值
     */
    @GetMapping("/{key}")
    String get(@PathVariable("key") String key);

    /**
     * 远程更新或新增配置项。
     *
     * @param request 配置更新请求
     * @return 更新后的所有配置的键值对映射
     */
    @PostMapping
    Map<String, String> update(@RequestBody ConfigUpdateRequest request);
}
