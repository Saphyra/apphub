package com.github.saphyra.apphub.lib.monitoring;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.config.rabbitmq.message.monitoring.MonitoringRabbitMqPublisherConfig;
import com.github.saphyra.apphub.lib.monitoring.instrument.DefaultMetricMapper;
import com.github.saphyra.apphub.lib.monitoring.instrument.MetricMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ComponentScan(basePackageClasses = MonitoringAutoConfiguration.class)
@Import(MonitoringRabbitMqPublisherConfig.class)
public class MonitoringAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(DateTimeUtil.class)
    DateTimeUtil dateTimeUtil() {
        return new DateTimeUtil();
    }

    @Bean
    @ConditionalOnMissingBean(MetricMapper.class)
    MetricMapper defaultMetricMapper() {
        return new DefaultMetricMapper();
    }
}
