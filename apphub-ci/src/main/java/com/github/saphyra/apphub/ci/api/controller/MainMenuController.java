package com.github.saphyra.apphub.ci.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class MainMenuController {
    @Deprecated
    @GetMapping("/")
    String mainMenu() {
        return "index";
    }

    @GetMapping("/v2")
    String v2MainMenu() {
        return "v2_index";
    }
}
