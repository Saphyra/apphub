package com.github.saphyra.apphub.ci.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import static com.github.saphyra.apphub.ci.api.ApiConstants.REDIRECT;

@Controller
class MainMenuController {
    @GetMapping("/")
    String mainMenu() {
        return REDIRECT + "v2";
    }

    @GetMapping("/v2")
    String v2MainMenu() {
        return "v2_index";
    }
}
