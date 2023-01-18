package com.github.saphyra.apphub.lib.common_util;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Configuration
@Getter
@EnableConfigurationProperties
@ConfigurationProperties
@Data
@Validated
public class CommonConfigProperties {
    @Value("${spring.application.name}")
    @NotNull
    private String applicationName;

    @NotNull
    private String defaultLocale;

    @NotNull
    private List<String> supportedLocales;

    @Value("${fileUpload.maxFileSize}")
    private Long maxUploadedFileSize;
}
