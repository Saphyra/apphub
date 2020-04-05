package com.github.saphyra.apphub.service.main_gateway;

import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

@Configuration
@ComponentScan(basePackages = "com.github.saphyra.util")
public class BeanConfig {
    @Bean
    public AntPathMatcher antPathMatcher() {
        return new AntPathMatcher();
    }

    @Bean
    public Base64Encoder base64Encoder() {
        return new Base64Encoder();
    }

    @Bean
    public UuidConverter uuidConverter() {
        return new UuidConverter();
    }
}
