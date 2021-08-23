package com.github.saphyra.apphub.service.notebook;

import com.github.saphyra.apphub.lib.config.Endpoints;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
class PageController {

    @GetMapping(Endpoints.NOTEBOOK_PAGE)
    String notebookPage() {
        log.info("Notebook page called.");
        return "notebook";
    }
}
