package com.github.saphyra.integration.server.api;

import com.github.saphyra.apphub.lib.config.common.endpoints.GenericEndpoints;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class PlatformController {
    @GetMapping(GenericEndpoints.HEALTH)
    void health() {
        log.info("Health check ping arrived");
    }
}
