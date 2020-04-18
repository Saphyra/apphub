package com.github.saphyra.apphub.lib.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class CommonConfigProperties {
    private final String defaultLocale;

    public CommonConfigProperties(
        @Value("${defaultLocale}") String defaultLocale
    ) {
        this.defaultLocale = defaultLocale;
    }
}
