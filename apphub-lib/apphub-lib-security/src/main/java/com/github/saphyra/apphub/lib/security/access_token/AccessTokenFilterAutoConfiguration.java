package com.github.saphyra.apphub.lib.security.access_token;

import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.lib.config.common.FilterOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import tools.jackson.databind.ObjectMapper;

@AutoConfiguration
@Slf4j
@ComponentScan
public class AccessTokenFilterAutoConfiguration {
    @Bean
    public FilterRegistrationBean<AccessTokenFilter> accessTokenFilterFilterRegistrationBean(AccessTokenFilter contextFilter) {
        log.debug("AccessTokenFilterOrder order: {}", FilterOrder.ACCESS_TOKEN_FILTER);
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

    @Bean
    @ConditionalOnMissingBean(Base64Encoder.class)
    Base64Encoder base64Encoder() {
        return new Base64Encoder();
    }

    @Bean
    @ConditionalOnMissingBean(AccessTokenHeaderConverter.class)
    AccessTokenHeaderConverter accessTokenHeaderConverter(Base64Encoder base64Encoder, ObjectMapper objectMapper) {
        return new AccessTokenHeaderConverter(base64Encoder, objectMapper);
    }
}
