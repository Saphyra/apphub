package com.github.saphyra.apphub.service.platform.event_gateway;

import com.github.saphyra.apphub.lib.common_util.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.RequestContextProvider;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.config.CommonConfigProperties;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.config.liquibase.EnableLiquibase;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorHandler;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocaleMandatoryRequestValidation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableJpaRepositories
@EntityScan
@ComponentScan(basePackages = "com.github.saphyra.util")
@EnableLiquibase
@EnableErrorHandler
@Import(CommonConfigProperties.class)
@EnableHealthCheck
@EnableLocaleMandatoryRequestValidation
class BeanConfiguration {
    @Bean
    @ConditionalOnMissingBean(ExecutorServiceBean.class)
    ExecutorServiceBean executorServiceBean() {
        return new ExecutorServiceBean();
    }

    @Bean
    @ConditionalOnMissingBean(LocaleProvider.class)
    LocaleProvider localeProvider(RequestContextProvider requestContextProvider) {
        return new LocaleProvider(requestContextProvider);
    }

    @Bean
    @ConditionalOnMissingBean(RequestContextProvider.class)
    RequestContextProvider requestContextProvider() {
        return new RequestContextProvider();
    }

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }

    @Bean
    DateTimeUtil dateTimeUtil() {
        return new DateTimeUtil();
    }

    @Bean
    IdGenerator idGenerator() {
        return new IdGenerator();
    }
}
