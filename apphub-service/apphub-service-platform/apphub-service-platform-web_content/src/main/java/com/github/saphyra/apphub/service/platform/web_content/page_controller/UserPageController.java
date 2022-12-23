package com.github.saphyra.apphub.service.platform.web_content.page_controller;

import com.github.saphyra.apphub.lib.config.common.Endpoints;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
class UserPageController {
    @GetMapping(Endpoints.INDEX_PAGE)
    String index() {
        log.info("Index page called.");
        return "user/index";
    }

    @GetMapping(Endpoints.ACCOUNT_PAGE)
    String account() {
        log.info("Account page called.");
        return "user/account";
    }
}
