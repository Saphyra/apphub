package com.github.saphyra.apphub.service.platform.localization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.apphub.lib.common_util.CustomObjectMapperWrapper;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.data.CommonDataConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableHealthCheck
@Import(CommonDataConfiguration.class)
public class BeanConfiguration {
    @Bean
    public CustomObjectMapperWrapper customObjectMapperWrapper(ObjectMapper objectMapper) {
        return new CustomObjectMapperWrapper(objectMapper);
    }
}
