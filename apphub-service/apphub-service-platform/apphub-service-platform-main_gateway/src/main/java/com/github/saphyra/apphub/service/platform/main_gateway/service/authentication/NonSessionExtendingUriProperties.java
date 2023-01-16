package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.apphub.lib.common_domain.WhiteListedEndpoint;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
@Data
@Slf4j
public class NonSessionExtendingUriProperties {
    private List<WhiteListedEndpoint> nonSessionExtendingUris;

    @PostConstruct
    public void validate() {
        requireNonNull(nonSessionExtendingUris, "nonSessionExtendingUris must not be null.");

        nonSessionExtendingUris.forEach(nonSessionExtendingUri -> {
            requireNonNull(nonSessionExtendingUri.getPattern(), "uri must not be null.");
            requireNonNull(nonSessionExtendingUri.getMethod(), "method must not be null.");
        });
    }
}
