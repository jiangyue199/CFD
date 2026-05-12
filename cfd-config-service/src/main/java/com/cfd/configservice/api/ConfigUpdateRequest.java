package com.cfd.configservice.api;

import jakarta.validation.constraints.NotBlank;

/**
 * 配置更新请求 DTO。
 *
 * <p>用于接收运行时配置的新增或更新请求参数。</p>
 *
 * @param key   配置键，不能为空
 * @param value 配置值，不能为空
 * @author CFD Platform Team
 */
public record ConfigUpdateRequest(
        @NotBlank String key,
        @NotBlank String value
) {
}
