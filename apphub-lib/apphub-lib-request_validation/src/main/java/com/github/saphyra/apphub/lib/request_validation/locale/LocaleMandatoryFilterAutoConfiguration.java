package com.github.saphyra.apphub.lib.request_validation.locale;

import com.github.saphyra.apphub.lib.common_domain.WhiteListedEndpoint;
import com.github.saphyra.apphub.lib.config.common.FilterOrder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.List;

@Slf4j
/*@ComponentScan
@EnableErrorTranslation
@ConfigurationProperties(prefix = "locale-mandatory-filter")
 */
@Data
@Deprecated(forRemoval = true) //TODO remove
class LocaleMandatoryFilterAutoConfiguration {
    private List<WhiteListedEndpoint> whiteListedEndpoints = new ArrayList<>();

    @Bean
    @ConditionalOnMissingBean(AntPathMatcher.class)
    AntPathMatcher antPathMatcher() {
        return new AntPathMatcher();
    }

    FilterRegistrationBean<LocaleMandatoryFilter> localeMandatoryFilterFilterRegistrationBean(LocaleMandatoryFilter contextFilter) {
        log.debug("LocaleMandatoryFilter order: {}", FilterOrder.LOCALE_MANDATORY_FILTER);
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
