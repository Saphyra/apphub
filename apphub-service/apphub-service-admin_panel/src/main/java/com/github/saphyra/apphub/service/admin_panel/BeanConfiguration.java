package com.github.saphyra.apphub.service.admin_panel;

import com.github.saphyra.apphub.lib.config.CommonConfigProperties;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenConfiguration;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.config.liquibase.EnableLiquibase;
import com.github.saphyra.apphub.lib.config.thymeleaf.EnableThymeLeaf;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorHandler;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocalMandatoryRequestValidation;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenFilterConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableThymeLeaf
@EnableLiquibase
@Import({
    AccessTokenConfiguration.class,
    AccessTokenFilterConfiguration.class,
    CommonConfigProperties.class
})
@EnableErrorHandler
@EnableLocalMandatoryRequestValidation
@EnableHealthCheck
public class BeanConfiguration {
}
