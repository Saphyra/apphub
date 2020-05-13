package com.github.saphyra.apphub.lib.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@Configuration
@Getter
@EnableConfigurationProperties
@ConfigurationProperties
@Data
@Validated
public class CommonConfigProperties {
    @NotNull
    private String defaultLocale;

    @NotNull
    private List<String> supportedLocales;
}
