package com.github.saphyra.apphub.service.etc.modules;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ModulesBeanConfiguration {
    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }
}
