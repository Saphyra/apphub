package com.github.saphyra.apphub.service.skyxplore.lobby.config;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.config.FilterOrder;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorHandler;
import com.github.saphyra.apphub.lib.event.processor.EnableEventProcessor;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocalMandatoryRequestValidation;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenFilterConfiguration;
import com.github.saphyra.apphub.lib.security.role.RoleFilterConfiguration;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.cleanup.LobbyInterceptorFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@Configuration
@EnableHealthCheck
@Import({
    AccessTokenFilterConfiguration.class,
    RoleFilterConfiguration.class
})
@EnableLocalMandatoryRequestValidation
@EnableErrorHandler
@EnableAspectJAutoProxy
@EnableEventProcessor
public class SkyxploreDataLobbyBeanConfiguration {
    @Bean
    FilterRegistrationBean<LobbyInterceptorFilter> metricsFilterFilterRegistrationBean(LobbyInterceptorFilter filter) {
        FilterRegistrationBean<LobbyInterceptorFilter> filterRegistrationBean = new FilterRegistrationBean<>();
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
    DateTimeUtil dateTimeUtil() {
        return new DateTimeUtil();
    }
}
