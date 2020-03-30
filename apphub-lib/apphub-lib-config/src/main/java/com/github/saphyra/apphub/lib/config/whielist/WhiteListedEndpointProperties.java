package com.github.saphyra.apphub.lib.config.whielist;

import com.github.saphyra.apphub.lib.common_domain.WhiteListedEndpoint;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;

@Component
@ConfigurationProperties(prefix = "endpoint.whitelisting")
@Data
@Slf4j
public class WhiteListedEndpointProperties {
    private Map<String, WhiteListedEndpoint> whiteListedEndpoints;

    @PostConstruct
    public void validate() {
        log.info("{}", this);
        Objects.requireNonNull(whiteListedEndpoints, "whiteListedEndpoints is null");

        whiteListedEndpoints.values()
            .forEach(WhiteListedEndpoint::validate);
    }
}
