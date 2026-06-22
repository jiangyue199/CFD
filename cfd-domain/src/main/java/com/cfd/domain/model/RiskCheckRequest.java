package com.cfd.domain.model;

import java.math.BigDecimal;

/**
 * 事前交易风控校验请求 - 订单服务发送给风控服务的完整校验请求。
 * <p>
 * 包含进行事前风控校验所需的全部字段，支持14条事前交易校验规则。
 * <p>
 * Pre-trade risk validation request sent from Order Service to Risk Service.
 * Contains all fields required for comprehensive pre-trade risk validation.
 *
 * @author CFD Platform Team
 * @param userId        用户标识 / User identifier
 * @param symbol        交易品种代码 / Trading instrument symbol
 * @param quantity      交易数量（手数） / Trade quantity (lots)
 * @param leverage      杠杆倍数 / Leverage multiplier
 * @param ip            用户IP地址 / User IP address
 * @param deviceId      设备标识 / Device identifier
 * @param cardNo        银行卡号（脱敏） / Card number (masked)
 * @param userLevel     用户等级 / User level (e.g., RETAIL, PRO, INSTITUTIONAL)
 * @param region        用户所在区域 / User region (e.g., CN, EU, US)
 * @param direction     开仓方向 / Order direction (BUY or SELL)
 * @param hedgeAllowed  是否允许锁仓 / Whether hedge is allowed
 * @param equity        账户权益 / Account equity
 * @param initMargin    初始保证金 / Initial margin required
 * @param currentPrice  当前市价 / Current market price
 * @param orderPrice    订单价格 / Order price
 * @param stopLevel     止损价位 / Stop loss level (if applicable)
 * @param orderType     订单类型 / Order type (MARKET, LIMIT, STOP)
 */
public record RiskCheckRequest(
        String userId,
        String symbol,
        BigDecimal quantity,
        BigDecimal leverage,
        String ip,
        String deviceId,
        String cardNo,
        String userLevel,
        String region,
        String direction,
        Boolean hedgeAllowed,
        BigDecimal equity,
        BigDecimal initMargin,
        BigDecimal currentPrice,
        BigDecimal orderPrice,
        BigDecimal stopLevel,
        String orderType
) {

    /**
     * 创建最小化请求（兼容原有接口）。
     */
    public static RiskCheckRequest minimal(String userId, String symbol, BigDecimal quantity, BigDecimal leverage) {
        return new RiskCheckRequest(
                userId, symbol, quantity, leverage,
                null, null, null, null, null,
                null, null, null, null, null, null, null, null
        );
    }
}
