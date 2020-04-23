package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Component
@ConfigurationProperties()
@Data
@Slf4j
public class NonSessionExtendingUriProperties {
    private final List<NonSessionExtendingUri> nonSessionExtendingUris;

    @PostConstruct
    public void validate() {
        requireNonNull(nonSessionExtendingUris, "nonSessionExtendingUris must not be null.");

        nonSessionExtendingUris.forEach(nonSessionExtendingUri -> {
            requireNonNull(nonSessionExtendingUri.getUri(), "uri must not be null.");
            requireNonNull(nonSessionExtendingUri.getMethod(), "method must not be null.");
        });
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class NonSessionExtendingUri {
        private String uri;
        private String method;
    }
}
