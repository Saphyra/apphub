package com.github.saphyra.apphub.service.platform.main_gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
public class RootController {
    @GetMapping("/api")
    @ResponseBody
    public void availabilityCheck() {
        log.info("Availability check was executed by a mobile device.");
    }


    @GetMapping("/")
    public String rootMapping() {
        log.info("Root was called. Redirecting to index page.");
        return String.format("redirect:%s", "/web");
    }
}
