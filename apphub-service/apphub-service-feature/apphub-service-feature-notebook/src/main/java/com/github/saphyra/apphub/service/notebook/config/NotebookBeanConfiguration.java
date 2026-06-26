package com.github.saphyra.apphub.service.notebook.config;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.github.saphyra.util")
class NotebookBeanConfiguration {
    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }

    @Bean
    IdGenerator idGenerator() {
        return new IdGenerator();
    }
}
