package com.github.saphyra.apphub.service.modules;

import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.lib.common_util.RequestContextProvider;
import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.apphub.lib.config.CommonConfigProperties;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenConfiguration;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.config.liquibase.EnableLiquibase;
import com.github.saphyra.apphub.lib.config.thymeleaf.EnableThymeLeaf;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorHandler;
import com.github.saphyra.apphub.lib.event.processor.EnableEventProcessor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenFilterConfiguration;
import com.github.saphyra.encryption.EnableEncryption;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableThymeLeaf
@EnableHealthCheck
@EnableLiquibase
@Import({
    AccessTokenConfiguration.class,
    AccessTokenFilterConfiguration.class,
    CommonConfigProperties.class
})
@EnableEncryption
@EnableEventProcessor
@EnableErrorHandler
@EnableFeignClients(basePackages = {
    "com.github.saphyra.apphub.api"
})
class BeanConfiguration {
    @Bean
    LocaleProvider localeProvider(RequestContextProvider requestContextProvider){
        return new LocaleProvider(requestContextProvider);
    }

    @Bean
    RequestContextProvider requestContextProvider(){
        return new RequestContextProvider();
    }

    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }
}
