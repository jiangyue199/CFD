package com.cfd.trading.domain;

import com.cfd.domain.model.OpenPositionResponse;
import com.cfd.domain.model.PositionStatus;

/**
 * 持仓对象映射器。
 *
 * <p>负责将{@link OpenPosition}领域对象转换为{@link OpenPositionResponse} DTO。</p>
 *
 * @author CFD Platform Team
 */
public final class OpenPositionMapper {

    private OpenPositionMapper() {
    }

    /**
     * 将持仓领域对象转换为响应DTO。
     *
     * @param position 持仓领域对象
     * @return 持仓响应DTO
     */
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
