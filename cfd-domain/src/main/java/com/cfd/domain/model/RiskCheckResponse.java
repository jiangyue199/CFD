package com.cfd.domain.model;

public record RiskCheckResponse(
        boolean allowed,
        String reason
) {
}
