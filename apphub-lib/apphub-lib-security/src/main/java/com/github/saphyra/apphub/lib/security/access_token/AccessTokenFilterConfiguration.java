package com.github.saphyra.apphub.lib.security.access_token;

import com.github.saphyra.apphub.lib.config.FilterOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@ComponentScan
public class AccessTokenFilterConfiguration {
    @Bean
    public FilterRegistrationBean<AccessTokenFilter> accessTokenFilterFilterRegistrationBean(AccessTokenFilter contextFilter) {
        log.info("AccessTokenFilterOrder order: {}", FilterOrder.ACCESS_TOKEN_FILTER);
        FilterRegistrationBean<AccessTokenFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(contextFilter);
        filterRegistrationBean.setOrder(FilterOrder.ACCESS_TOKEN_FILTER.getFilterOrder());
        filterRegistrationBean.addUrlPatterns(
            "/api/*",
            "/internal/*",
            "/web/*"
        );
        return filterRegistrationBean;
    }
}
