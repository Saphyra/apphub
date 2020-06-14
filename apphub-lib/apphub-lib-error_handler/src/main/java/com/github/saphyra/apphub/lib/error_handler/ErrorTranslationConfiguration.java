package com.github.saphyra.apphub.lib.error_handler;

import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.lib.common_util.RequestContextProvider;
import com.github.saphyra.apphub.lib.config.FeignClientConfiguration;
import com.github.saphyra.apphub.lib.error_handler.service.ErrorResponseFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackageClasses = ErrorResponseFactory.class)
@Import(FeignClientConfiguration.class)
public class ErrorTranslationConfiguration {
    @Bean
    @ConditionalOnMissingBean(LocaleProvider.class)
    public LocaleProvider localeProvider(RequestContextProvider requestContextProvider) {
        return new LocaleProvider(requestContextProvider);
    }

    @Bean
    @ConditionalOnMissingBean(RequestContextProvider.class)
    public RequestContextProvider requestContextProvider() {
        return new RequestContextProvider();
    }
}
