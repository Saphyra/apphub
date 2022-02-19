package com.github.saphyra.apphub.service.platform.localization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.data.CommonDataConfiguration;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.monitoring.EnableMemoryMonitoring;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocaleMandatoryRequestValidation;
import com.github.saphyra.apphub.lib.web_utils.CustomLocaleProvider;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CommonDataConfiguration.class)
@EnableHealthCheck
@EnableLocaleMandatoryRequestValidation
@ComponentScan(basePackageClasses = ErrorReporterService.class)
@EnableMemoryMonitoring
public class LocalizationBeanConfiguration {
    @Bean
    @ConditionalOnMissingBean(DateTimeUtil.class)
    DateTimeUtil dateTimeUtil() {
        return new DateTimeUtil();
    }

    @Bean
    CustomLocaleProvider customLocaleProvider(CommonConfigProperties commonConfigProperties, LocaleProvider localeProvider) {
        return new CustomLocaleProvider(commonConfigProperties, localeProvider);
    }

    @Bean
    ObjectMapperWrapper customObjectMapperWrapper(ObjectMapper objectMapper) {
        return new ObjectMapperWrapper(objectMapper);
    }
}
