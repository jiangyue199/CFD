package com.cfd.risk.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 风控校验结果 - Drools 规则执行后累积的结果对象。
 *
 * <p>插入工作内存后，各条规则将拒绝原因添加到 reasons 列表中。
 * 规则全部执行完毕后，由服务层根据 reasons 是否为空决定最终 allowed 状态。</p>
 *
 * @author CFD Platform Team
 */
public class RiskCheckResult {

    private boolean allowed = true;
    private List<String> reasons = new ArrayList<>();

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public List<String> getReasons() {
        return Collections.unmodifiableList(reasons);
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }

    /**
     * 添加一条拒绝原因。
     *
     * @param reason 拒绝原因描述
     */
    public void addReason(String reason) {
        this.reasons.add(reason);
    }
}
