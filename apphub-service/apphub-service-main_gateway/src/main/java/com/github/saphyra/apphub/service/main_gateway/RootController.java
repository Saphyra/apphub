package com.github.saphyra.apphub.service.main_gateway;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {
    @GetMapping("/")
    public String rootMapping() {
        return String.format("redirect:%s", "/web");
    }
}
