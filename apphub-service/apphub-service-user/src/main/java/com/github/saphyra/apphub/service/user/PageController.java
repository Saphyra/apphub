package com.github.saphyra.apphub.service.user;

import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping(Endpoints.INDEX_PAGE)
    public String index() {
        return "index";
    }

    @GetMapping(Endpoints.ACCOUNT_PAGE)
    public String account() {
        return "account";
    }
}
