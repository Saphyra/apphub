package com.github.saphyra.apphub.service.utils.config;

import com.github.saphyra.apphub.lib.config.CommonConfigProperties;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenConfiguration;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.config.thymeleaf.EnableThymeLeaf;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorHandler;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocaleMandatoryRequestValidation;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenFilterConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableThymeLeaf
@Import({
    AccessTokenConfiguration.class,
    AccessTokenFilterConfiguration.class,
    CommonConfigProperties.class
})
@EnableErrorHandler
@EnableLocaleMandatoryRequestValidation
@EnableHealthCheck
public class UtilsBeanConfiguration {
}
