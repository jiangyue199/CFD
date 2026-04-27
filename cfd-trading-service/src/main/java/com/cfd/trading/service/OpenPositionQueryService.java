package com.cfd.trading.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cfd.domain.model.OpenPositionResponse;
import com.cfd.trading.domain.OpenPositionMapper;
import com.cfd.trading.domain.OpenPositionRepository;

@Service
public class OpenPositionQueryService {

    private final OpenPositionRepository openPositionRepository;

    public OpenPositionQueryService(OpenPositionRepository openPositionRepository) {
        this.openPositionRepository = openPositionRepository;
    }

    public Optional<OpenPositionResponse> queryByOrderId(String orderId) {
        return openPositionRepository.findByOrderId(orderId).map(OpenPositionMapper::toResponse);
    }

    public List<OpenPositionResponse> queryByUserId(String userId) {
        return openPositionRepository.findByUserId(userId).stream()
                .map(OpenPositionMapper::toResponse)
                .toList();
    }
}
