package com.github.saphyra.apphub.service.platform.encryption.config;

import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.platform.encryption.EncryptionApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableJpaRepositories(basePackageClasses = EncryptionApplication.class)
@EntityScan(basePackageClasses = EncryptionApplication.class)
class EncryptionBeanConfiguration {
    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }

    @Bean
    IdGenerator idGenerator() {
        return new IdGenerator();
    }

    @Bean
    AccessTokenHeaderConverter accessTokenHeaderConverter(Base64Encoder base64Encoder, ObjectMapper objectMapper) {
        return new AccessTokenHeaderConverter(base64Encoder, objectMapper);
    }

    @Bean
    Base64Encoder base64Encoder() {
        return new Base64Encoder();
    }
}
