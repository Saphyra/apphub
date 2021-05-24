package com.github.saphyra.apphub.service.user.config;

import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenConfiguration;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.config.liquibase.EnableLiquibase;
import com.github.saphyra.apphub.lib.config.thymeleaf.EnableThymeLeaf;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorHandler;
import com.github.saphyra.apphub.lib.event.processor.EnableEventProcessor;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocaleMandatoryRequestValidation;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenFilterConfiguration;
import com.github.saphyra.apphub.lib.security.role.RoleFilterConfiguration;
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
@ComponentScan(basePackages = "com.github.saphyra.util")
@EnableLiquibase
@EnableErrorHandler
@Import({
    CommonConfigProperties.class,
    AccessTokenConfiguration.class,
    AccessTokenFilterConfiguration.class,
    RoleFilterConfiguration.class
})
@EnableThymeLeaf
@EnableEventProcessor
@EnableHealthCheck
@EnableLocaleMandatoryRequestValidation
class UserBeanConfiguration {
    @Bean
    @ConditionalOnMissingBean(LocaleProvider.class)
    LocaleProvider localeProvider(RequestContextProvider requestContextProvider, CommonConfigProperties commonConfigProperties) {
        return new LocaleProvider(requestContextProvider, commonConfigProperties);
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

    @Bean
    DateTimeUtil dateTimeUtil() {
        return new DateTimeUtil();
    }

    @Bean
    IdGenerator idGenerator() {
        return new IdGenerator();
    }
}


