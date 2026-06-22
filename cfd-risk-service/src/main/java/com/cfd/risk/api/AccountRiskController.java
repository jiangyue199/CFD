package com.cfd.risk.api;

import com.cfd.risk.persistence.*;
import com.cfd.risk.service.AccountRiskService;
import com.cfd.risk.service.ForceCloseService;
import com.cfd.risk.service.MarginCalculationService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 账户风控 REST 控制器。
 *
 * <p>提供账户保证金查询、风险检查、强平等接口。</p>
 *
 * @author CFD Platform Team
 */
@RestController
@RequestMapping("/risk/account")
public class AccountRiskController {

    private final MarginCalculationService marginCalculationService;
    private final AccountRiskService accountRiskService;
    private final ForceCloseService forceCloseService;

    public AccountRiskController(MarginCalculationService marginCalculationService,
                                 AccountRiskService accountRiskService,
                                 ForceCloseService forceCloseService) {
        this.marginCalculationService = marginCalculationService;
        this.accountRiskService = accountRiskService;
        this.forceCloseService = forceCloseService;
    }

    /**
     * 获取账户保证金信息。
     *
     * @param userId 用户ID
     * @return 账户保证金信息
     */
    @GetMapping("/{userId}/margin")
    public AccountMarginEntity getAccountMargin(@PathVariable String userId) {
        return marginCalculationService.getAccountMargin(userId).orElse(null);
    }

    /**
     * 更新账户保证金信息。
     *
     * @param userId 用户ID
     * @param request 更新请求
     * @return 更新后的账户保证金信息
     */
    @PostMapping("/{userId}/margin")
    public AccountMarginEntity updateAccountMargin(@PathVariable String userId,
                                                   @RequestBody MarginUpdateRequest request) {
        return marginCalculationService.calculateAndUpdateMargin(userId, request.balance());
    }

    /**
     * 检查保证金是否充足。
     *
     * @param userId 用户ID
     * @param requiredMargin 所需保证金
     * @return 检查结果
     */
    @GetMapping("/{userId}/margin/check")
    public MarginCheckResponse checkMargin(@PathVariable String userId,
                                           @RequestParam BigDecimal requiredMargin) {
        boolean sufficient = accountRiskService.checkSufficientMargin(userId, requiredMargin);
        return new MarginCheckResponse(sufficient, sufficient ? "保证金充足" : "保证金不足");
    }

    /**
     * 获取用户持仓列表。
     *
     * @param userId 用户ID
     * @return 持仓列表
     */
    @GetMapping("/{userId}/positions")
    public List<PositionMarginEntity> getUserPositions(@PathVariable String userId) {
        return marginCalculationService.getUserPositions(userId);
    }

    /**
     * 获取保证金配置。
     *
     * @param userLevel 用户等级
     * @return 保证金配置
     */
    @GetMapping("/config/{userLevel}")
    public MarginConfigEntity getMarginConfig(@PathVariable String userLevel) {
        return marginCalculationService.getMarginConfig(userLevel);
    }

    /**
     * 获取用户未处理的告警。
     *
     * @param userId 用户ID
     * @return 告警列表
     */
    @GetMapping("/{userId}/alerts")
    public List<RiskAlertEntity> getUnhandledAlerts(@PathVariable String userId) {
        return accountRiskService.getUnhandledAlerts(userId);
    }

    /**
     * 处理告警。
     *
     * @param alertId 告警ID
     */
    @PostMapping("/alerts/{alertId}/handle")
    public void handleAlert(@PathVariable Long alertId) {
        accountRiskService.handleAlert(alertId);
    }

    /**
     * 手动触发账户风险扫描。
     *
     * @param userId 用户ID
     * @param userLevel 用户等级
     */
    @PostMapping("/{userId}/scan")
    public void scanAccountRisk(@PathVariable String userId,
                                @RequestParam(defaultValue = "RETAIL") String userLevel) {
        accountRiskService.scanAccountRisk(userId, userLevel);
    }

    /**
     * 手动触发强平检查。
     *
     * @param userId 用户ID
     * @param userLevel 用户等级
     */
    @PostMapping("/{userId}/force-close/check")
    public void checkForceClose(@PathVariable String userId,
                                @RequestParam(defaultValue = "RETAIL") String userLevel) {
        forceCloseService.checkAndExecuteForceClose(userId, userLevel);
    }

    /**
     * 获取强平记录。
     *
     * @param userId 用户ID
     * @return 强平记录列表
     */
    @GetMapping("/{userId}/force-close/records")
    public List<ForceCloseRecordEntity> getForceCloseRecords(@PathVariable String userId) {
        return forceCloseService.getForceCloseRecords(userId);
    }

    /**
     * 检查品种集中度风险。
     *
     * @param userId 用户ID
     * @param symbol 品种
     * @param userLevel 用户等级
     * @return 检查结果
     */
    @GetMapping("/{userId}/concentration/{symbol}")
    public ConcentrationCheckResponse checkConcentration(@PathVariable String userId,
                                                         @PathVariable String symbol,
                                                         @RequestParam(defaultValue = "RETAIL") String userLevel) {
        boolean triggered = accountRiskService.checkConcentrationRisk(userId, symbol, userLevel);
        return new ConcentrationCheckResponse(triggered, triggered ? "品种集中度过高" : "正常");
    }

    // ===== 请求/响应 DTO =====

    public record MarginUpdateRequest(BigDecimal balance) {}

    public record MarginCheckResponse(boolean sufficient, String message) {}

    public record ConcentrationCheckResponse(boolean triggered, String message) {}
}
