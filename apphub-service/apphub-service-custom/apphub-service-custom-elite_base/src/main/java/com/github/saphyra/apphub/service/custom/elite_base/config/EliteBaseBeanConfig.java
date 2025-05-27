package com.github.saphyra.apphub.service.custom.elite_base.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.concurrency.DefaultExecutorServiceBeanConfig;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import com.github.saphyra.apphub.lib.concurrency.FixedExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ScheduledExecutorServiceBean;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.config.liquibase.EnableLiquibase;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorHandler;
import com.github.saphyra.apphub.lib.event.processor.EnableEventProcessor;
import com.github.saphyra.apphub.lib.geometry.n_dimension.NDimensionDistanceCalculator;
import com.github.saphyra.apphub.lib.monitoring.EnableMemoryMonitoring;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReportingConfiguration;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocaleMandatoryRequestValidation;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenFilterConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableLiquibase
@Import({
    AccessTokenFilterConfiguration.class,
    CommonConfigProperties.class,
    DefaultExecutorServiceBeanConfig.class,
    PerformanceReportingConfiguration.class
})
@EnableEventProcessor
@EnableErrorHandler
@EnableLocaleMandatoryRequestValidation
@EnableHealthCheck
@EnableMemoryMonitoring
class EliteBaseBeanConfig {
    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }

    @Bean
    IdGenerator idGenerator() {
        return new IdGenerator();
    }

    @Bean
    ObjectMapperWrapper objectMapperWrapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        objectMapper.findAndRegisterModules();
        return new ObjectMapperWrapper(objectMapper);
    }

    @Bean
    DateTimeConverter dateTimeConverter() {
        return new DateTimeConverter();
    }

    @Bean
    ScheduledExecutorServiceBean scheduledExecutorServiceBean(
        ExecutorServiceBeanFactory factory,
        @Value("${elite-base.executor.scheduled.threadCount}") int threadCount
    ) {
        return factory.createScheduled(threadCount);
    }

    @Bean
    ApplicationContextProxy applicationContextProxy(ConfigurableApplicationContext applicationContext) {
        return new ApplicationContextProxy(applicationContext);
    }

    @Bean
    NDimensionDistanceCalculator nDimensionDistanceCalculator() {
        return new NDimensionDistanceCalculator();
    }
}
