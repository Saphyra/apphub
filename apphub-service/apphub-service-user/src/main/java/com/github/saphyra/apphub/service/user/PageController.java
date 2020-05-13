package com.github.saphyra.apphub.service.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/web")
    public String index() {
        return "index";
    }

    @GetMapping("/web/user/account")
    public String account() {
        return "account";
    }
}
