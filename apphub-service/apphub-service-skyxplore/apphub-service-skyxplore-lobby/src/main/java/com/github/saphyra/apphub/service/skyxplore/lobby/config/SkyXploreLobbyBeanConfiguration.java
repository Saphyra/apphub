package com.github.saphyra.apphub.service.skyxplore.lobby.config;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.config.common.FilterOrder;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorHandler;
import com.github.saphyra.apphub.lib.event.processor.EnableEventProcessor;
import com.github.saphyra.apphub.lib.monitoring.EnableMemoryMonitoring;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocaleMandatoryRequestValidation;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenFilterConfiguration;
import com.github.saphyra.apphub.lib.security.role.RoleFilterConfiguration;
import com.github.saphyra.apphub.service.skyxplore.lobby.controller.filter.LobbyLastAccessInterceptorFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
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
@EnableEventProcessor
@EnableMemoryMonitoring
public class SkyXploreLobbyBeanConfiguration {
    @Bean
    FilterRegistrationBean<LobbyLastAccessInterceptorFilter> metricsFilterFilterRegistrationBean(LobbyLastAccessInterceptorFilter filter) {
        FilterRegistrationBean<LobbyLastAccessInterceptorFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setOrder(FilterOrder.ACCESS_TOKEN_FILTER.getFilterOrder() + 1);
        filterRegistrationBean.addUrlPatterns("/api/**");
        return filterRegistrationBean;
    }

    @Bean
    IdGenerator idGenerator() {
        return new IdGenerator();
    }

    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }
}
