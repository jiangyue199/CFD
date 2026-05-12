package com.cfd.trading.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cfd.domain.model.OpenPositionResponse;
import com.cfd.trading.domain.OpenPositionMapper;
import com.cfd.trading.domain.OpenPositionRepository;

/**
 * 持仓查询服务。
 *
 * <p>提供持仓信息的查询能力，支持按订单ID和用户ID查询，
 * 将领域对象转换为响应DTO返回。</p>
 *
 * @author CFD Platform Team
 */
@Service
public class OpenPositionQueryService {

    private final OpenPositionRepository openPositionRepository;

    public OpenPositionQueryService(OpenPositionRepository openPositionRepository) {
        this.openPositionRepository = openPositionRepository;
    }

    /**
     * 根据订单ID查询持仓信息。
     *
     * @param orderId 订单ID
     * @return 持仓响应DTO（如果存在）
     */
    public Optional<OpenPositionResponse> queryByOrderId(String orderId) {
        return openPositionRepository.findByOrderId(orderId).map(OpenPositionMapper::toResponse);
    }

    /**
     * 根据用户ID查询该用户所有持仓信息。
     *
     * @param userId 用户ID
     * @return 持仓响应DTO列表
     */
    public List<OpenPositionResponse> queryByUserId(String userId) {
        return openPositionRepository.findByUserId(userId).stream()
                .map(OpenPositionMapper::toResponse)
                .toList();
    }
}
