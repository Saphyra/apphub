package com.github.saphyra.integration.server.api;

import com.github.saphyra.integration.server.util.Endpoints;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class PlatformController {
    @GetMapping(Endpoints.HEALTH)
    void health() {
        log.info("Health check ping arrived");
    }
}
