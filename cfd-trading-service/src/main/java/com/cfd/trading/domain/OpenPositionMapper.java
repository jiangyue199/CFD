package com.cfd.trading.domain;

import com.cfd.domain.model.OpenPositionResponse;
import com.cfd.domain.model.PositionStatus;

public final class OpenPositionMapper {

    private OpenPositionMapper() {
    }

    public static OpenPositionResponse toResponse(OpenPosition position) {
        return new OpenPositionResponse(
                position.getOrderId(),
                position.getUserId(),
                position.getSymbol(),
                position.getOpenPrice(),
                position.getQuantity(),
                position.getLeverage(),
                position.getMargin(),
                position.getFloatingPnl(),
                PositionStatus.OPENED,
                position.getCreatedAt());
    }
}
