package com.ctrip.framework.apollo.configservice;

import com.ctrip.framework.apollo.biz.eureka.ApolloEurekaClientConfig;
import com.netflix.appinfo.InstanceInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author : kl
 * After startup, set FetchRegistry to true, refresh eureka client
 **/
@Configuration
@ConditionalOnProperty(value = {"eureka.client.enabled", "apollo.eureka.server.enabled"}, havingValue = "true", matchIfMissing = true)
public class ConfigServerEurekaClientConfigure implements ApplicationListener<SpringApplicationEvent> {

    private static final String EUREKA_CLIENT_BEAN_NAME = "eurekaClient";
    private final ApolloEurekaClientConfig eurekaClientConfig;
    private final AtomicBoolean isRefreshed = new AtomicBoolean(false);
    private final RefreshScope refreshScope;

    public ConfigServerEurekaClientConfigure(ApolloEurekaClientConfig eurekaClientConfig, RefreshScope refreshScope) {
        this.eurekaClientConfig = eurekaClientConfig;
        this.refreshScope = refreshScope;
    }

    @Override
    public void onApplicationEvent(SpringApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent) {
            this.refreshEurekaClient();
        }
    }

    private void refreshEurekaClient() {
        if (isRefreshed.compareAndSet(false, true)) {
            eurekaClientConfig.setFetchRegistry(true);
            eurekaClientConfig.setRegisterWithEureka(true);
            refreshScope.refresh(EUREKA_CLIENT_BEAN_NAME);
        }
    }
}
