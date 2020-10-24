package com.github.saphyra.apphub.lib.request_validation.locale;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.apphub.lib.config.CommonConfigProperties;
import com.github.saphyra.apphub.lib.config.FilterOrder;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorTranslation;
import com.github.saphyra.util.ObjectMapperWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Slf4j
@ComponentScan
@Import(CommonConfigProperties.class)
@EnableErrorTranslation
class LocaleMandatoryFilterConfiguration {

    @Bean
    @ConditionalOnMissingBean(ObjectMapperWrapper.class)
    public ObjectMapperWrapper objectMapperWrapper(ObjectMapper objectMapper) {
        return new ObjectMapperWrapper(objectMapper);
    }

    @Bean
    public FilterRegistrationBean<LocaleMandatoryFilter> localeMandatoryFilterFilterRegistrationBean(LocaleMandatoryFilter contextFilter) {
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
