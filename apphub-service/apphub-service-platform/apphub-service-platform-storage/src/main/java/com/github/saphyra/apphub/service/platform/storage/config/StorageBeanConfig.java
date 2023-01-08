package com.github.saphyra.apphub.service.platform.storage.config;

import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.config.liquibase.EnableLiquibase;
import com.github.saphyra.apphub.lib.encryption.EnableEncryption;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorHandler;
import com.github.saphyra.apphub.lib.event.processor.EnableEventProcessor;
import com.github.saphyra.apphub.lib.monitoring.EnableMemoryMonitoring;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocaleMandatoryRequestValidation;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenFilterConfiguration;
import com.github.saphyra.apphub.service.platform.storage.StorageApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
@EnableJpaRepositories(basePackageClasses = StorageApplication.class)
@EntityScan(basePackageClasses = StorageApplication.class)
@EnableLiquibase
@EnableErrorHandler
@Import({
    CommonConfigProperties.class,
    AccessTokenFilterConfiguration.class
})
@EnableHealthCheck
@EnableLocaleMandatoryRequestValidation
@EnableMemoryMonitoring
@EnableEventProcessor
@EnableEncryption
public class StorageBeanConfig {
    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }

    @Bean
    IdGenerator idGenerator() {
        return new IdGenerator();
    }

    @Bean
    MultipartResolver customMultipartResolver() {
        final CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setResolveLazily(true);
        return multipartResolver;
    }
}
