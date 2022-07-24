package com.github.saphyra.apphub.service.diary;

import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class PageController {
    @GetMapping(Endpoints.DIARY_PAGE)
    String diaryPage() {
        return "diary";
    }
}
