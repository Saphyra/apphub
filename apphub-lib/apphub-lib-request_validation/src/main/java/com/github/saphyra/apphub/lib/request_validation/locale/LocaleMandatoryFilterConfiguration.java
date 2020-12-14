package com.github.saphyra.apphub.lib.request_validation.locale;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.apphub.lib.common_domain.WhiteListedEndpoint;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.config.common.CommonConfigProperties;
import com.github.saphyra.apphub.lib.config.FilterOrder;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorTranslation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
@ComponentScan
@Import(CommonConfigProperties.class)
@EnableErrorTranslation
@ConfigurationProperties(prefix = "locale-mandatory-filter")
@Data
class LocaleMandatoryFilterConfiguration {
    private List<WhiteListedEndpoint> whiteListedEndpoints = new ArrayList<>();

    @Bean
    @ConditionalOnMissingBean(AntPathMatcher.class)
    AntPathMatcher antPathMatcher() {
        return new AntPathMatcher();
    }

    @Bean
    @ConditionalOnMissingBean(ObjectMapperWrapper.class)
    ObjectMapperWrapper objectMapperWrapper(ObjectMapper objectMapper) {
        return new ObjectMapperWrapper(objectMapper);
    }

    @Bean
    FilterRegistrationBean<LocaleMandatoryFilter> localeMandatoryFilterFilterRegistrationBean(LocaleMandatoryFilter contextFilter) {
        log.info("AccessTokenFilterOrder order: {}", FilterOrder.LOCALE_MANDATORY_FILTER);
        FilterRegistrationBean<LocaleMandatoryFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(contextFilter);
        filterRegistrationBean.setOrder(FilterOrder.LOCALE_MANDATORY_FILTER.getFilterOrder());
        filterRegistrationBean.addUrlPatterns(
            "/api/*",
            "/internal/*",
            "/web/*"
        );
        return filterRegistrationBean;
    }
}
