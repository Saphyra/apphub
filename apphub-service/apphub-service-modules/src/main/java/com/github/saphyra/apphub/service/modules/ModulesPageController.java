package com.github.saphyra.apphub.service.modules;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ModulesPageController {
    @GetMapping("/web/modules")
    public String index() {
        return "modules";
    }
}
