package com.github.saphyra.apphub.service.modules;

import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ModulesPageController {
    @GetMapping(Endpoints.MODULES_PAGE)
    public String index() {
        return "modules";
    }
}
