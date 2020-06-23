package com.github.saphyra.apphub.lib.security.role;

import com.github.saphyra.apphub.lib.config.FilterOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

@Configuration
@ComponentScan
@Slf4j
public class RoleFilterConfiguration {
    @Bean
    public FilterRegistrationBean<RoleFilter> roleFilterFilterRegistrationBean(RoleFilter filter) {
        log.info("RoleFilter order: {}", FilterOrder.ROLE_FILTER_ORDER);
        FilterRegistrationBean<RoleFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setOrder(FilterOrder.ROLE_FILTER_ORDER.getFilterOrder());
        filterRegistrationBean.addUrlPatterns(
            "/api/*",
            "/internal/*",
            "/web/*"
        );
        return filterRegistrationBean;
    }

    @Bean
    @ConditionalOnMissingBean(AntPathMatcher.class)
    AntPathMatcher antPathMatcher() {
        return new AntPathMatcher();
    }
}
