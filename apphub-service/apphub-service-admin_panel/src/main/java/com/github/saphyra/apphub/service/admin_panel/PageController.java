package com.github.saphyra.apphub.service.admin_panel;

import com.github.saphyra.apphub.lib.config.Endpoints;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class PageController {
    @GetMapping(Endpoints.ADMIN_PANEL_INDEX_PAGE)
    public String adminPanelIndexPage(){
        log.info("Admin panel index page called.");
        return "index";
    }
}
