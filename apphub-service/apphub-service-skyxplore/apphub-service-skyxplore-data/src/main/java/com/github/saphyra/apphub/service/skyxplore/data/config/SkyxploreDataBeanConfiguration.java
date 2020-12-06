package com.github.saphyra.apphub.service.skyxplore.data.config;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.config.liquibase.EnableLiquibase;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorHandler;
import com.github.saphyra.apphub.lib.event.processor.EnableEventProcessor;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocaleMandatoryRequestValidation;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenFilterConfiguration;
import com.github.saphyra.apphub.lib.security.role.RoleFilterConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableHealthCheck
@Import({
    AccessTokenFilterConfiguration.class,
    RoleFilterConfiguration.class
})
@EnableLocaleMandatoryRequestValidation
@EnableErrorHandler
@EnableLiquibase
@EnableEventProcessor
public class SkyxploreDataBeanConfiguration {
    @Bean
    IdGenerator idGenerator() {
        return new IdGenerator();
    }

    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }

    @Bean
    DateTimeUtil dateTimeUtil() {
        return new DateTimeUtil();
    }
}
