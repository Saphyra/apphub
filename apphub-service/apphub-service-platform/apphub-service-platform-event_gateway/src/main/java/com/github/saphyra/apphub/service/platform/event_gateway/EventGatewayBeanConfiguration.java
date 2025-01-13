package com.github.saphyra.apphub.service.platform.event_gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.apphub.api.admin_panel.client.MonitoringClient;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.config.liquibase.EnableLiquibase;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorHandler;
import com.github.saphyra.apphub.lib.monitoring.MemoryMonitoringEventController;
import com.github.saphyra.apphub.lib.monitoring.MemoryStatusModelFactory;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocaleMandatoryRequestValidation;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import com.github.saphyra.apphub.lib.web_utils.RequestContextProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableJpaRepositories
@EntityScan
@ComponentScan(basePackages = "com.github.saphyra.util", basePackageClasses = ExecutorServiceBeanFactory.class)
@EnableLiquibase
@EnableErrorHandler
@Import(CommonConfigProperties.class)
@EnableHealthCheck
@EnableLocaleMandatoryRequestValidation
class EventGatewayBeanConfiguration {
    @Bean
    @ConditionalOnMissingBean(ExecutorServiceBean.class)
    ExecutorServiceBean executorServiceBean(ExecutorServiceBeanFactory executorServiceBeanFactory) {
        return executorServiceBeanFactory.create();
    }

    @Bean
    @ConditionalOnMissingBean(SleepService.class)
    SleepService sleepService() {
        return new SleepService();
    }

    @Bean
    @ConditionalOnMissingBean(LocaleProvider.class)
    LocaleProvider localeProvider(RequestContextProvider requestContextProvider, CommonConfigProperties commonConfigProperties) {
        return new LocaleProvider(requestContextProvider, commonConfigProperties);
    }

    @Bean
    @ConditionalOnMissingBean(RequestContextProvider.class)
    RequestContextProvider requestContextProvider() {
        return new RequestContextProvider();
    }

    @Bean
    //@LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }

    @Bean
    IdGenerator idGenerator() {
        return new IdGenerator();
    }

    @Bean
    MemoryStatusModelFactory memoryStatusModelFactory(DateTimeUtil dateTimeUtil) {
        return new MemoryStatusModelFactory(dateTimeUtil);
    }

    @Bean
    MemoryMonitoringEventController memoryMonitoringEventController(
        CommonConfigProperties commonConfigProperties,
        MonitoringClient monitoringClient,
        MemoryStatusModelFactory memoryStatusModelFactory,
        @Value("${spring.application.name}") String serviceName
    ) {
        return MemoryMonitoringEventController.builder()
            .commonConfigProperties(commonConfigProperties)
            .monitoringClient(monitoringClient)
            .memoryStatusModelFactory(memoryStatusModelFactory)
            .serviceName(serviceName)
            .build();
    }

    @Bean
    ObjectMapperWrapper objectMapperWrapper() {
        return new ObjectMapperWrapper(new ObjectMapper());
    }
}
