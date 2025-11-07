package com.github.saphyra.apphub.service.training.config;

import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.config.thymeleaf.EnableThymeLeaf;
import com.github.saphyra.apphub.lib.error_handler.EnableErrorHandler;
import com.github.saphyra.apphub.lib.monitoring.EnableMemoryMonitoring;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocaleMandatoryRequestValidation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import tools.jackson.databind.ObjectMapper;

@Configuration

@ComponentScan(basePackages = "com.github.saphyra.util")
@EnableHealthCheck
@EnableThymeLeaf
@EnableErrorHandler
@EnableLocaleMandatoryRequestValidation
@Import({
    CommonConfigProperties.class
})
@EnableMemoryMonitoring
class TrainingBeanConfiguration {
    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }

    @Bean
    IdGenerator idGenerator() {
        return new IdGenerator();
    }

    @Bean
    ObjectMapperWrapper objectMapperWrapper() {
        return new ObjectMapperWrapper(new ObjectMapper());
    }
}
