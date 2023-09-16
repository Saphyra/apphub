package com.github.saphyra.apphub.service.admin_panel.config;

import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.config.liquibase.EnableLiquibase;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorHandler;
import com.github.saphyra.apphub.lib.monitoring.MemoryStatusModelFactory;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocaleMandatoryRequestValidation;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenFilterConfiguration;
import com.github.saphyra.apphub.lib.web_socket.WebSocketConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    AccessTokenFilterConfiguration.class,
    CommonConfigProperties.class,
    WebSocketConfiguration.class
})
@EnableErrorHandler
@EnableLocaleMandatoryRequestValidation
@EnableHealthCheck
@EnableLiquibase
public class AdminPanelBeanConfiguration {
    @Bean
    IdGenerator idGenerator() {
        return new IdGenerator();
    }

    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }

    @Bean
    MemoryStatusModelFactory memoryStatusModelFactory(DateTimeUtil dateTimeUtil) {
        return new MemoryStatusModelFactory(dateTimeUtil);
    }
}
