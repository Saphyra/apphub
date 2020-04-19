package com.github.saphyra.apphub.lib.config.access_token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import com.github.saphyra.apphub.lib.common_util.CustomObjectMapperWrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class AccessTokenConfiguration {
    @Bean
    @ConditionalOnMissingBean(CustomObjectMapperWrapper.class)
    public CustomObjectMapperWrapper customObjectMapperWrapper(ObjectMapper objectMapper) {
        return new CustomObjectMapperWrapper(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(Base64Encoder.class)
    public Base64Encoder base64Encoder() {
        return new Base64Encoder();
    }
}
