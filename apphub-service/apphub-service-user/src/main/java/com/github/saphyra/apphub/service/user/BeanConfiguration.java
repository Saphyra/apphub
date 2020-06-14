package com.github.saphyra.apphub.service.user;

import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.lib.common_util.RequestContextProvider;
import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.apphub.lib.config.CommonConfigProperties;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenConfiguration;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.config.liquibase.EnableLiquibase;
import com.github.saphyra.apphub.lib.config.thymeleaf.EnableThymeLeaf;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorHandler;
import com.github.saphyra.apphub.lib.event.processor.EnableEventProcessor;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocalMandatoryRequestValidation;
import com.github.saphyra.encryption.impl.PasswordService;
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
@EnableJpaRepositories
@EntityScan
@ComponentScan(basePackages = "com.github.saphyra.util")
@EnableLiquibase
@EnableErrorHandler
@Import({
    CommonConfigProperties.class,
    AccessTokenConfiguration.class
})
@EnableThymeLeaf
@EnableEventProcessor
@EnableHealthCheck
@EnableLocalMandatoryRequestValidation
class BeanConfiguration {
    @Bean
    @ConditionalOnMissingBean(LocaleProvider.class)
    LocaleProvider localeProvider(RequestContextProvider requestContextProvider) {
        return new LocaleProvider(requestContextProvider);
    }

    @Bean
    PasswordService passwordService() {
        return new PasswordService();
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
}


