package com.github.saphyra.apphub.ci.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
class ApphubCiBeanConfig {
    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
