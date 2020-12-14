package com.github.saphyra.apphub.lib.error_handler;

import com.github.saphyra.apphub.lib.config.common.CommonConfigProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackageClasses = ErrorHandlerAdvice.class)
@Import({
    ErrorTranslationConfiguration.class,
    CommonConfigProperties.class
})
class ErrorHandlerConfiguration {

}
