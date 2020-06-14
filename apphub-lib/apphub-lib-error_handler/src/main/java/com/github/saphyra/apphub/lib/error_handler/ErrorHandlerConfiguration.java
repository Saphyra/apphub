package com.github.saphyra.apphub.lib.error_handler;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackageClasses = ErrorHandlerAdvice.class)
@Import(ErrorTranslationConfiguration.class)
class ErrorHandlerConfiguration {

}
