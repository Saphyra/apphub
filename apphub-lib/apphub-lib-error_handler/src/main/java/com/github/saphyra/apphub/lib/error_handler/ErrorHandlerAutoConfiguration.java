package com.github.saphyra.apphub.lib.error_handler;

import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.web_utils.CustomLocaleProvider;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ComponentScan(basePackageClasses = {
    ErrorHandlerAdvice.class,
    ErrorReporterService.class
})
@Import({
    ErrorTranslationConfiguration.class
})
class ErrorHandlerAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(DateTimeUtil.class)
    DateTimeUtil dateTimeUtil() {
        return new DateTimeUtil();
    }

    @Bean
    @ConditionalOnMissingBean(CustomLocaleProvider.class)
    CustomLocaleProvider customLocaleProvider(
        CommonConfigProperties commonConfigProperties,
        LocaleProvider localeProvider
    ) {
        return new CustomLocaleProvider(commonConfigProperties, localeProvider);
    }
}
