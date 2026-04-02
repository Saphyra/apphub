package com.github.saphyra.apphub.lib.monitoring;

import com.github.saphyra.apphub.api.platform.monitoring.client.MonitoringClient;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.monitoring.core.CollectedMetricClient;
import com.github.saphyra.apphub.lib.monitoring.core.DefaultCollectedMetricClient;
import com.github.saphyra.apphub.lib.monitoring.instrument.DefaultMetricMapper;
import com.github.saphyra.apphub.lib.monitoring.instrument.MetricMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackageClasses = MonitoringAutoConfiguration.class)
public class MonitoringAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(DateTimeUtil.class)
    DateTimeUtil dateTimeUtil() {
        return new DateTimeUtil();
    }

    @Bean
    @ConditionalOnMissingBean(CollectedMetricClient.class)
    CollectedMetricClient defaultCollectedMetricClient(@Value("${spring.application.name}") String serviceName, MonitoringClient monitoringClient) {
        return new DefaultCollectedMetricClient(monitoringClient, serviceName);
    }

    @Bean
    @ConditionalOnMissingBean(MetricMapper.class)
    MetricMapper defaultMetricMapper() {
        return new DefaultMetricMapper();
    }
}
