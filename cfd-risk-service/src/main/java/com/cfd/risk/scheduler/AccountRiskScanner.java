package com.cfd.risk.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cfd.risk.persistence.AccountMarginDbMapper;
import com.cfd.risk.persistence.AccountMarginEntity;
import com.cfd.risk.service.AccountRiskService;
import com.cfd.risk.service.ForceCloseService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 账户风险扫描定时任务。
 *
 * <p>定时扫描所有账户的保证金水平，执行风险检查和强平操作。</p>
 *
 * @author CFD Platform Team
 */
@Component
public class AccountRiskScanner {

    private final AccountMarginDbMapper accountMarginMapper;
    private final AccountRiskService accountRiskService;
    private final ForceCloseService forceCloseService;

    public AccountRiskScanner(AccountMarginDbMapper accountMarginMapper,
                              AccountRiskService accountRiskService,
                              ForceCloseService forceCloseService) {
        this.accountMarginMapper = accountMarginMapper;
        this.accountRiskService = accountRiskService;
        this.forceCloseService = forceCloseService;
    }

    /**
     * 每5秒扫描一次账户风险。
     *
     * <p>扫描所有有持仓的账户，检查保证金水平并执行相应操作。</p>
     */
    @Scheduled(fixedRate = 5000)
    public void scanAllAccounts() {
        List<AccountMarginEntity> accounts = accountMarginMapper.selectList(
                new LambdaQueryWrapper<AccountMarginEntity>()
                        .gt(AccountMarginEntity::getUsedMargin, 0));

        for (AccountMarginEntity account : accounts) {
            try {
                String userLevel = "RETAIL"; // 默认用户等级，实际应从用户服务获取
                accountRiskService.scanAccountRisk(account.getUserId(), userLevel);
                forceCloseService.checkAndExecuteForceClose(account.getUserId(), userLevel);
            } catch (Exception e) {
                // 记录错误但继续处理其他账户
                System.err.println("Error scanning account " + account.getUserId() + ": " + e.getMessage());
            }
        }
    }

    /**
     * 每分钟检查隔夜利息预警。
     */
    @Scheduled(fixedRate = 60000)
    public void checkSwapWarnings() {
        List<AccountMarginEntity> accounts = accountMarginMapper.selectList(
                new LambdaQueryWrapper<AccountMarginEntity>()
                        .gt(AccountMarginEntity::getUsedMargin, 0));

        for (AccountMarginEntity account : accounts) {
            try {
                accountRiskService.checkSwapWarning(account.getUserId(), new java.math.BigDecimal("100"));
            } catch (Exception e) {
                System.err.println("Error checking swap for account " + account.getUserId() + ": " + e.getMessage());
            }
        }
    }
}
