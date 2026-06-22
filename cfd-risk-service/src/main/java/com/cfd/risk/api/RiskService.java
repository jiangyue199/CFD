package com.cfd.risk.api;

import com.cfd.domain.model.RiskCheckRequest;
import com.cfd.domain.model.RiskCheckResponse;
import com.cfd.risk.persistence.RiskRuleEntity;

import java.util.List;

public interface RiskService {
    RiskCheckResponse checkOpenRisk(RiskCheckRequest request);
    List<RiskRuleEntity> listRules();
    RiskRuleEntity getRule(Long id);
    RiskRuleEntity addRule(RiskRuleEntity entity);
    RiskRuleEntity updateRule(Long id, RiskRuleEntity entity);
    void deleteRule(Long id);
}