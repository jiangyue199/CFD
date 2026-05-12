package com.cfd.domain.model;

/**
 * 风控校验响应 - 风控服务返回的校验结果。
 * <p>
 * 包含是否允许开仓及拒绝原因（如被拒绝时）。
 * <p>
 * Risk validation response returned by the Risk Service.
 * Contains whether the trade is allowed and the rejection reason (if rejected).
 *
 * @author CFD Platform Team
 * @param allowed 是否通过风控校验 / Whether the trade passed risk validation
 * @param reason  拒绝原因（通过时为 null） / Rejection reason (null if allowed)
 */
public record RiskCheckResponse(
        boolean allowed,
        String reason
) {
}
