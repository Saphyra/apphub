package com.github.saphyra.apphub.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ApplicationProxyController {
    @GetMapping("/platform/health")
    void health(){
        log.info("Health check arrived");
    }
}
