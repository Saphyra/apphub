package com.github.saphyra.apphub.service.platform.event_gateway;

import com.github.saphyra.apphub.lib.common_util.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.lib.common_util.RequestContextProvider;
import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.apphub.lib.config.CommonConfigProperties;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.config.liquibase.EnableLiquibase;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorHandler;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocalMandatoryRequestValidation;
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
@EnableLocalMandatoryRequestValidation
public class BeanConfiguration {
    @Bean
    @ConditionalOnMissingBean(ExecutorServiceBean.class)
    public ExecutorServiceBean executorServiceBean() {
        return new ExecutorServiceBean();
    }

    @Bean
    @ConditionalOnMissingBean(LocaleProvider.class)
    public LocaleProvider localeProvider(RequestContextProvider requestContextProvider) {
        return new LocaleProvider(requestContextProvider);
    }

    @Bean
    @ConditionalOnMissingBean(RequestContextProvider.class)
    public RequestContextProvider requestContextProvider() {
        return new RequestContextProvider();
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public UuidConverter uuidConverter() {
        return new UuidConverter();
    }
}
