package com.github.saphyra.apphub.lib.config.health;

import com.github.saphyra.apphub.lib.config.common.endpoints.GenericEndpoints;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HealthCheckController {
    @GetMapping(GenericEndpoints.HEALTH)
    public void healthCheck() {
        log.debug("HealthCheck request arrived.");
    }
}
