package com.github.saphyra.apphub.service.platform.web_content.page_controller;

import com.github.saphyra.apphub.lib.config.common.Endpoints;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class UtilsPageController {
    @GetMapping(Endpoints.UTILS_JSON_FORMATTER_PAGE)
    public String jsonFormatterPage() {
        return "utils/json_formatter";
    }

    @GetMapping(Endpoints.UTILS_LOG_FORMATTER_PAGE)
    public String logFormatterPage() {
        return "utils/log_formatter";
    }

    @GetMapping(Endpoints.UTILS_BASE64_ENCODER_PAGE)
    public String base64() {
        return "utils/base64";
    }
}
