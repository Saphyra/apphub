package com.github.saphyra.apphub.service.skyxplore.lobby.config;

import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.config.common.FilterOrder;
import com.github.saphyra.apphub.service.skyxplore.lobby.controller.filter.LobbyLastAccessInterceptorFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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

    @Bean
    ApplicationContextProxy applicationContextProxy(ConfigurableApplicationContext applicationContext) {
        return new ApplicationContextProxy(applicationContext);
    }
}
