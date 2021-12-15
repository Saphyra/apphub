package com.github.saphyra.apphub.service.user.common;

import com.github.saphyra.apphub.lib.config.Endpoints;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
class PageController {
    @GetMapping(Endpoints.INDEX_PAGE)
    String index() {
        log.info("Index page called.");
        return "index";
    }

    @GetMapping(Endpoints.ACCOUNT_PAGE)
    String account() {
        log.info("Account page called.");
        return "account";
    }
}
