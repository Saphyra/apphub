package com.github.saphyra.apphub.service.platform.localization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.data.CommonDataConfiguration;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocaleMandatoryRequestValidation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CommonDataConfiguration.class)
@EnableHealthCheck
@EnableLocaleMandatoryRequestValidation
public class LocalizationBeanConfiguration {
    @Bean
    public ObjectMapperWrapper customObjectMapperWrapper(ObjectMapper objectMapper) {
        return new ObjectMapperWrapper(objectMapper);
    }
}
