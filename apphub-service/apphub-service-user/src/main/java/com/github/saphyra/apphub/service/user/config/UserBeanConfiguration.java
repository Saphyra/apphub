package com.github.saphyra.apphub.service.user.config;

import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.config.liquibase.EnableLiquibase;
import com.github.saphyra.apphub.lib.encryption.EnableEncryption;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorHandler;
import com.github.saphyra.apphub.lib.event.processor.EnableEventProcessor;
import com.github.saphyra.apphub.lib.monitoring.EnableMemoryMonitoring;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocaleMandatoryRequestValidation;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenFilterConfiguration;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import com.github.saphyra.apphub.lib.web_utils.RequestContextProvider;
import com.github.saphyra.apphub.service.user.UserApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@Slf4j
@EnableJpaRepositories(basePackageClasses = UserApplication.class)
@EntityScan(basePackageClasses = UserApplication.class)
@ComponentScan(basePackages = "com.github.saphyra.util", basePackageClasses = {
    ExecutorServiceBeanFactory.class
})
@EnableLiquibase
@EnableErrorHandler
@Import({
    CommonConfigProperties.class,
    AccessTokenFilterConfiguration.class
})
@EnableEventProcessor
@EnableHealthCheck
@EnableLocaleMandatoryRequestValidation
@EnableMemoryMonitoring
@EnableEncryption
class UserBeanConfiguration {
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
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }

    @Bean
    IdGenerator idGenerator() {
        return new IdGenerator();
    }

    @Bean
    @ConditionalOnMissingBean(SleepService.class)
    SleepService sleepService() {
        return new SleepService();
    }
}


