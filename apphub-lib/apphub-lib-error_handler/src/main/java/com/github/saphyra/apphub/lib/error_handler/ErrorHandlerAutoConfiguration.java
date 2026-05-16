package com.github.saphyra.apphub.lib.error_handler;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackageClasses = {
    ErrorHandlerAdvice.class,
    ErrorReporterService.class
})
class ErrorHandlerAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(DateTimeUtil.class)
    DateTimeUtil dateTimeUtil() {
        return new DateTimeUtil();
    }
}
