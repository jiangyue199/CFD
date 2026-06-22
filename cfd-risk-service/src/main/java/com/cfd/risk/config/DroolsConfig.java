package com.cfd.risk.config;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Drools 规则引擎 Spring 配置。
 *
 * <p>从 classpath 加载 DRL 规则文件（通过 META-INF/kmodule.xml 配置），
 * 构建 KieContainer 供风控引擎使用。</p>
 *
 * @author CFD Platform Team
 */
@Configuration
public class DroolsConfig {

    @Bean
    public KieContainer kieContainer() {
        return KieServices.Factory.get().getKieClasspathContainer();
    }
}
