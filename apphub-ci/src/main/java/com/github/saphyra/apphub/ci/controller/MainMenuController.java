package com.github.saphyra.apphub.ci.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class MainMenuController {
    @GetMapping("/")
    String mainMenu() {
        return "index";
    }
}
