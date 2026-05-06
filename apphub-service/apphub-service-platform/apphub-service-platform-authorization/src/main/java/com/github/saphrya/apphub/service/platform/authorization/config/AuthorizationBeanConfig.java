package com.github.saphrya.apphub.service.platform.authorization.config;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AuthorizationBeanConfig {
    @Bean
    IdGenerator idGenerator() {
        return new IdGenerator();
    }

    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }
}
