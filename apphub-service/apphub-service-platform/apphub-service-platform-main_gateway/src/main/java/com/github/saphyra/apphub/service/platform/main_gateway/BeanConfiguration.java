package com.github.saphyra.apphub.service.platform.main_gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import com.github.saphyra.apphub.lib.common_util.CookieUtil;
import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.RequestContextProvider;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.config.CommonConfigProperties;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.config.whitelist.EnableWhiteListedEndpointProperties;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorTranslation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.AntPathMatcher;

@Configuration
@ComponentScan(basePackages = "com.github.saphyra.util")
@EnableZuulProxy
@EnableWhiteListedEndpointProperties
@EnableErrorTranslation
@Import(CommonConfigProperties.class)
@EnableHealthCheck
class BeanConfiguration {
    @Bean
    AntPathMatcher antPathMatcher() {
        return new AntPathMatcher();
    }

    @Bean
    Base64Encoder base64Encoder() {
        return new Base64Encoder();
    }

    @Bean
    @ConditionalOnMissingBean(LocaleProvider.class)
    LocaleProvider localeProvider(RequestContextProvider requestContextProvider) {
        return new LocaleProvider(requestContextProvider);
    }

    @Bean
    @ConditionalOnMissingBean(RequestContextProvider.class)
    RequestContextProvider requestContextProvider() {
        return new RequestContextProvider();
    }

    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }

    @Bean
    CookieUtil cookieUtil() {
        return new CookieUtil();
    }

    @Bean
    ObjectMapperWrapper objectMapperWrapper(ObjectMapper objectMapper) {
        return new ObjectMapperWrapper(objectMapper);
    }
}
