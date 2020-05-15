package com.github.saphyra.apphub.lib.security.access_token;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@ComponentScan
public class AccessTokenFilterConfiguration {
    private static final int FILTER_ORDER = Integer.MIN_VALUE;

    @Bean
    public FilterRegistrationBean<AccessTokenFilter> accessTokenFilterFilterRegistrationBean(AccessTokenFilter contextFilter) {
        log.info("AccessTokenFilterOrder order: {}", FILTER_ORDER);
        FilterRegistrationBean<AccessTokenFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(contextFilter);
        filterRegistrationBean.setOrder(FILTER_ORDER);
        filterRegistrationBean.addUrlPatterns(
            "/api/*",
            "/internal/*",
            "/web/*"
        );
        return filterRegistrationBean;
    }
}
