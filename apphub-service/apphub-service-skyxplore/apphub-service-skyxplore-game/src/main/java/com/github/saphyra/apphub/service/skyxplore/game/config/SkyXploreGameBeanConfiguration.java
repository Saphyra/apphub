package com.github.saphyra.apphub.service.skyxplore.game.config;

import com.github.saphyra.apphub.lib.common_util.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorHandler;
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
public class SkyXploreGameBeanConfiguration {
    @Bean
    ExecutorServiceBean executorServiceBean() {
        return new ExecutorServiceBean();
    }
}
